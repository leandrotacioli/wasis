package br.unicamp.fnjv.wasis.features;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTWindowFunction;
import br.unicamp.fnjv.wasis.libs.RoundNumbers;
import br.unicamp.fnjv.wasis.libs.Statistics;

/**
 * Feature extraction class used to extract Perceptual Linear Prediction (PLP) from audio signals.
 *
 * @author Leandro Tacioli
 * @version 4.0 - 27/Out/2017
 */
public class PLP extends Features {
	/** Number of PLP filters. */
    private final int PLP_FILTERS = 21;
	
    /** LPC Order. Equivalent to the number of LPC coefficients. */
    private final int LPC_ORDER = 24;
    
    /** LPCC Order. Equivalent to the number of LPCC coefficients.<br>
     * Also, the final number of PLP coefficients. */
    private final int LPCC_ORDER = 24;
    
	/** Number of samples per frame. */
    private final int FRAME_LENGTH = 1024;
    
    /** Number of overlapping samples (Usually 50% of the <i>FRAME_LENGTH</i>). */
    private final int OVERLAP_SAMPLES = FRAME_LENGTH / 2;
    
    /** Window Function */
    private final String WINDOW_FUNCTION = FFTWindowFunction.HAMMING;
    
    /** Lower limit of the filter */
    private final double LOWER_FILTER_FREQUENCY = 45.0;
    
    /** Sample rate */
    private double dblSampleRate;
    
    /** Filter coefficients. */
    private BarkFilterbank[] barkFilterbanks;
    
    /** Equal Loudness. */
    private double[] equalLoudness;
    
    /** Fast Fourier Transform */
    private FFT objFFT;
    
    private double dblMinimumBarkFrequency;
    private double dblMaximumBarkFrequency;
    private double dblDeltaBarkFrequency;
    
    private double[][] cosine;
    
    /** Final PLP Coefficients */
    private double[][] plp;
    
    private double[] mean;
    private double[] standardDeviation;
    
	/**
     * Feature extraction class used to extract Perceptual Linear Prediction (PLP) from audio signal.
     */
    public PLP(double dblSampleRate) {
    	this.dblSampleRate = dblSampleRate;
    	
    	computeCosine();
    }
    
    /**
     * Takes an audio signal and computes the Perceptual Linear Prediction (PLP).<br>
     * <br>
     * It starts processing the samples by performing framing.
     * 
     * @param audioSignal
     */
    @Override
    public void process(double[] audioSignal) {
    	// Step 1 - Frame Blocking
        double[][] frames = Preprocessing.framing(audioSignal, FRAME_LENGTH, OVERLAP_SAMPLES);
        
        processFrames(frames);
    }
    
    /**
     * Computes the Perceptual Linear Prediction (PLP) from audio frames.<br>
     * <br>
     * It assumes that framing have already been performed.
     * 
     * @param frames
     */
    @Override
    public void processFrames(double[][] frames) {
        // Step 2 - Windowing - Apply Hamming Window to all frames
        objFFT = new FFT(FRAME_LENGTH, WINDOW_FUNCTION);
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	frames[indexFrame] = objFFT.applyWindow(frames[indexFrame]);
        }
        
        dblMinimumBarkFrequency = frequencyToBark(LOWER_FILTER_FREQUENCY);
        dblMaximumBarkFrequency = frequencyToBark(dblSampleRate / 2);
        dblDeltaBarkFrequency = (dblMaximumBarkFrequency - dblMinimumBarkFrequency) / (PLP_FILTERS + 1);
        
        plp = new double[frames.length][LPCC_ORDER];
        
        double[] magnitudeSpectrum;
        double[] plpSpectral;
        double[] intensityLoudness;
        double[] autoCorrelation;
        
        LPC objLPC;
        
        // Below computations are all based on individual frames
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	// Step 3 - Magnitude Spectrum (FFT)
            magnitudeSpectrum = magnitudeSpectrum(frames[indexFrame]);
            
            // Step 4 - Bark Filter Bank
            barkFilterBank(magnitudeSpectrum);
            
            // Step 5 - Equal Loudness / Preemphasis
            equalLoudness();
            
            // PLP Spectral array
            plpSpectral = new double[PLP_FILTERS];
            
            for (int indexFilter = 0; indexFilter < PLP_FILTERS; indexFilter++) {
            	plpSpectral[indexFilter] = barkFilterbanks[indexFilter].filterOutput(magnitudeSpectrum);
            	plpSpectral[indexFilter] *= equalLoudness[indexFilter];  // Scale for equal loudness preemphasis
            }
            
            // Step 6 - Intensity Loudness
            intensityLoudness = intensityLoudness(plpSpectral);
            
            autoCorrelation = applyCosine(intensityLoudness);
            
            // Step 7 - Linear Predictive Coding (LPC)
            objLPC = new LPC(LPC_ORDER, LPCC_ORDER);
            objLPC.processAutocorrelatedFrame(autoCorrelation);
            
            // Step 8 - Linear Prediction Cepstral Coefficient (LPCC)
            plp[indexFrame] = objLPC.getFeatureLpcc()[0];
        }
        
        // Calculates mean and standard deviation for each coefficient
        mean = new double[LPCC_ORDER];
        standardDeviation = new double[LPCC_ORDER];
        
        double[] coefficientValues;
        
    	for (int indexCoefficient = 0; indexCoefficient < LPCC_ORDER; indexCoefficient++) {
    		coefficientValues = new double[frames.length];
    		
    		for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
    			coefficientValues[indexFrame] = plp[indexFrame][indexCoefficient];
    		}
    		
    		mean[indexCoefficient] = RoundNumbers.round(Statistics.calculateMean(coefficientValues), 4);
    		standardDeviation[indexCoefficient] = RoundNumbers.round(Statistics.calculateStandardDeviation(coefficientValues), 4);
    	}
    }
    
    /**
     * Returns the final PLP Coefficients.
     * 
     * @return plp
     */
    @Override
    public double[][] getFeature() {
    	return plp;
    }
    
    /**
     * Returns the mean of the MFCC Coefficients.
     * 
     * @return mean
     */
    @Override
    public double[] getMean() {
    	return mean;
    }
    
    /**
     * Returns the standard deviation of the MFCC Coefficients.
     * 
     * @return standardDeviation
     */
    @Override
    public double[] getStandardDeviation() {
    	return standardDeviation;
    }
    
    /**
     * Computes the magnitude spectrum of the input frame (FFT).
     * 
     * @param frame - Input frame signal
     * 
     * @return magnitudeSpectrum - Magnitude Spectrum
     */
    private double[] magnitudeSpectrum(double[] frame) {
        double[] magnitudeSpectrum = new double[frame.length];
        
        objFFT.executeFFT(frame);
        
        for (int k = 0; k < frame.length; k++){
        	magnitudeSpectrum[k] = Math.pow(objFFT.getReal()[k] * objFFT.getReal()[k] + objFFT.getImag()[k] * objFFT.getImag()[k], 0.5);
        }
        
        return magnitudeSpectrum;
    }
    
    /**
     * Computes BarkFilterBank.
     * 
     * @param magnitudeSpectrum
     */
    private void barkFilterBank(double[] magnitudeSpectrum) {
    	double[] frequencyBins = new double[FRAME_LENGTH];
        
        for (int indexFrameLength = 0; indexFrameLength < FRAME_LENGTH; indexFrameLength++) {
        	frequencyBins[indexFrameLength] = (indexFrameLength * (dblSampleRate / 2)) / (FRAME_LENGTH - 1);
        }
        
        barkFilterbanks = new BarkFilterbank[PLP_FILTERS];
        
        for (int indexFilter = 0; indexFilter < PLP_FILTERS; indexFilter++) {
        	double dblCenterFrequency = barkToFrequency(dblMinimumBarkFrequency + indexFilter * dblDeltaBarkFrequency);
            
        	barkFilterbanks[indexFilter] = new BarkFilterbank(frequencyBins, dblCenterFrequency);
        }
    }
    
    /**
     * Create an array of equal loudness preemphasis scaling terms for all the filters.
     */
    private void equalLoudness() {
        equalLoudness = new double[PLP_FILTERS];
        
        for (int indexFilter = 0; indexFilter < PLP_FILTERS; indexFilter++) {
            double dblCenterFrequency = barkFilterbanks[indexFilter].getCenterFrequency();
            
            equalLoudness[indexFilter] = loudnessScalingFunction(dblCenterFrequency);
        }
    }
    
    /**
     * This function return the equal loudness preemphasis factor at any frequency.
     * The preemphasis function is given by:
     * 
     * <p>E(w) = f^4 / (f^2 + 1.6e5) ^ 2 * (f^2 + 1.44e6) / (f^2 + 9.61e6)<p>
     * 
     * This is more modern one from HTK, for some reason it's preferred over old variant, and 
     * it doesn't require conversion to radians
     * 
     * <p>E(w) = (w^2+56.8e6)*w^4/((w^2+6.3e6)^2(w^2+0.38e9)(w^6+9.58e26))<p>
     * 
     * where w is frequency in radians/second
     * 
     * @param freq
     */
    private double loudnessScalingFunction(double dblFrequency) {
        double fsq = dblFrequency * dblFrequency;
        double fsub = fsq / (fsq + 1.6e5);
        
        return fsub * fsub * ((fsq + 1.44e6) / (fsq + 9.61e6));
    }
    
    /**
     * Applies the intensity loudness power law. This operation is an approximation to the power law of hearing and
     * simulates the non-linear relationship between sound intensity and percieved loudness. Computationally, this
     * operation is used to reduce the spectral amplitude of the critical band to enable all-pole modeling with
     * relatively low order AR filters.
     * 
     * @param plpSpectrum
     */
    private double[] intensityLoudness(double[] plpSpectrum) {
        double[] intensityLoudness = new double[plpSpectrum.length];
        
        for (int i = 0; i < plpSpectrum.length; i++) {
        	intensityLoudness[i] = Math.pow(plpSpectrum[i], 1.0 / 3.0);
        }
        
        return intensityLoudness;
    }
    
    /**
     * Compute the Cosine values for IDCT.
     */
    private void computeCosine() {
        cosine = new double[LPC_ORDER + 1][PLP_FILTERS];
        
        double dblPeriod = (double) 2 * PLP_FILTERS;
        
        for (int i = 0; i <= LPC_ORDER; i++) {
            double dblFrequency = 2 * Math.PI * i / dblPeriod;
            
            for (int j = 0; j < PLP_FILTERS; j++) {
                cosine[i][j] = Math.cos(dblFrequency * (j + 0.5));
            }
        }
    }
    
    /**
     * Compute the Discrete Cosine transform for the given power spectrum.
     *
     * @param plpSpectrum - PLP Spectrum
     * 
     * @return autoCorrelation
     */
    private double[] applyCosine(double[] plpSpectrum) {
        double[] autoCorrelation = new double[LPC_ORDER + 1];
        double dblPeriod = PLP_FILTERS;
        double dblBeta = 0.5f;
        
        // Apply the IDCT
        for (int i = 0; i <= LPC_ORDER; i++) {
            if (PLP_FILTERS > 0) {
                int j = 0;
                
                autoCorrelation[i] += (dblBeta * plpSpectrum[j] * cosine[i][j]);
                
                for (j = 1; j < PLP_FILTERS; j++) {
                	autoCorrelation[i] += (plpSpectrum[j] * cosine[i][j]);
                }
                
                autoCorrelation[i] /= dblPeriod;
            }
        }

        return autoCorrelation;
    }
    
    /**
     * Convert Frequency (Hz) to Bark-Frequency.
     * 
     * @param dblFrequency
     * 
     * @return Bark-Frequency
     */
    private double frequencyToBark(double dblFrequency) {
        double x = dblFrequency / 600;
        
        return (6.0 * Math.log(x + Math.sqrt(x * x + 1)));
    }
    
    /**
     * Convert Bark-Frequency to Frequency (Hz).
     * 
     * @param dblBarkFrequency
     * 
     * @return dblFrequency
     */
    private double barkToFrequency(double dblBarkFrequency) {
        double x = dblBarkFrequency / 6.0;
        
        return (300.0 * (Math.exp(x) - Math.exp(-x)));
    }
    
    /**
     * Bark Filterbank.
     */
    class BarkFilterbank {
    	private double[] filterCoefficients;
    	private double dblCenterFrequency;
    	
    	/**
    	 * Return center frequency.
    	 * 
    	 * @return dblCenterFrequency
    	 */
    	protected double getCenterFrequency() {
    		return dblCenterFrequency;
    	}
    	
		/**
		 * Bark Filterbank.
		 * 
		 * @param frequencyBins
		 * @param dblCenterFrequency
		 */
    	private BarkFilterbank(double[] frequencyBins, double dblCenterFrequency) {
    		this.dblCenterFrequency = dblCenterFrequency;
    		
    		filterCoefficients = new double[FRAME_LENGTH];
            
    		double dblCenterBarkFrequency =  frequencyToBark(dblCenterFrequency);
    		
            for (int indexFrameLength = 0; indexFrameLength < FRAME_LENGTH; indexFrameLength++) {
                double dblBarkFrequency = frequencyToBark(frequencyBins[indexFrameLength]) - dblCenterBarkFrequency;
                
                if (dblBarkFrequency < -2.5) {
                    filterCoefficients[indexFrameLength] = 0.0;
                } else if (dblBarkFrequency <= -0.5) {
                    filterCoefficients[indexFrameLength] = Math.pow(10.0, dblBarkFrequency + 0.5);
                } else if (dblBarkFrequency <= 0.5) {
                    filterCoefficients[indexFrameLength] = 1.0;
                } else if (dblBarkFrequency <= 1.3) {
                    filterCoefficients[indexFrameLength] = Math.pow(10.0, -2.5 * (dblBarkFrequency - 0.5));
                } else {
                    filterCoefficients[indexFrameLength] = 0.0;
                }
            }
    	}
    	
    	/**
         * Compute the PLP spectrum at the center frequency of this filter for a given power spectrum.
         *
         * @param spectrum - Input power spectrum to be filtered
         * 
         * @return dblPLPSpectrum - PLP spectrum value
         */
        public double filterOutput(double[] spectrum) {
            double dblPLPSpectrum = 0.0;
            
            for (int i = 0; i < FRAME_LENGTH; i++) {
            	dblPLPSpectrum += spectrum[i] * filterCoefficients[i];
            }
            
            return dblPLPSpectrum;
        }
    }
}