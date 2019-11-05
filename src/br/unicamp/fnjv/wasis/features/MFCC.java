package br.unicamp.fnjv.wasis.features;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTWindowFunction;
import br.unicamp.fnjv.wasis.libs.Matrices;
import br.unicamp.fnjv.wasis.libs.RoundNumbers;
import br.unicamp.fnjv.wasis.libs.Statistics;

/**
 * Feature extraction class used to extract Mel-Frequency Cepstral Coefficients (MFFC) from audio signals.
 *
 * @author Leandro Tacioli
 * @version 5.0 - 26/Out/2017
 */
public class MFCC extends Features {
	/** Number of MFCCs coefficients per frame.<br>
     * <br>
     * <b>IMPORTANT 1:</b> The 0th coefficient will be discarded because it is
     * considered as a collection of average energies of the frequency bands.<br>
     * <b>IMPORTANT 2:</b> Delta & Delta Delta will also be computed,
     * returning a total of 36 coefficients */
    private final int MFFC_COEFFICIENTS = 13;
    
    /** Number of Mel filters */
    private final int MEL_FILTERS = 23;
	
    /** Number of samples per frame */
    private final int FRAME_LENGTH = 1024;
    
    /** Number of overlapping samples (Usually 50% of the <i>FRAME_LENGTH</i>) */
    private final int OVERLAP_SAMPLES = FRAME_LENGTH / 2;
    
    /** Window Function */
    private final String WINDOW_FUNCTION = FFTWindowFunction.HAMMING;
    
    /** Lower limit of the filter */
    private final double LOWER_FILTER_FREQUENCY = 45.0;
    
    /** Upper limit of the filter (usually half of the Sample Rate) */
	//private final double UPPER_FILTER_FREQUENCY = 22050.00;
    
    /** Sample rate */
    private double dblSampleRate;
    
    /** Delta N */
    private final int DELTA_N = 2;
    
    /** Arithmetic progression of Delta */
    private double[][] deltaProgression;
    
    /** Delta denominator */
    private double dblDeltaDenominator;
    
    /** Fast Fourier Transform */
    private FFT objFFT;
    
    /** Final MFCC Coefficients */
    private double[][] mfcc;
    
    private double[] mean;
    private double[] standardDeviation;
    
    /**
     * Feature extraction class used to extract Mel-Frequency Cepstral Coefficients (MFFCs) from audio signals.
     */
    public MFCC(double dblSampleRate) {
    	this.dblSampleRate = dblSampleRate;
    	
    	calculateDeltaValues();
    }
    
    /**
     * Calculates Arithmetic progression and denominator of Delta.
     */
    private void calculateDeltaValues() {
    	// Calculate Delta Arithmetic Progression
    	// Ex: {-2, -1, 0, 1, 2} - when N = 2
    	deltaProgression = new double[1][2 * DELTA_N + 1];
    	
    	for (int indexProgression = -DELTA_N; indexProgression <= DELTA_N; indexProgression++) {
    		deltaProgression[0][indexProgression + DELTA_N] = indexProgression;
    	}
    	
    	// Calculate Delta Denominator
    	dblDeltaDenominator = 0;
    	
    	for (int indexDelta = 0; indexDelta <= DELTA_N; indexDelta++) {
    		dblDeltaDenominator += Math.pow(indexDelta, 2);
    	}
    	
    	dblDeltaDenominator = 2 * dblDeltaDenominator;
    }
    
    /**
     * Take samples from an audio signal and computes the Mel-Frequency Cepstral Coefficients (MFCCs).<br>
     * <br>
     * It starts processing the samples by performing pre-emphasis and framing.
     * 
     * @param audioSignal
     */
    @Override
    public void process(double[] audioSignal) {
        // Step 1 - Pre-Emphasis
        double[] preEmphasis = Preprocessing.preEmphasis(audioSignal);
        
        // Step 2 - Frame Blocking
        double[][] frames = Preprocessing.framing(preEmphasis, FRAME_LENGTH, OVERLAP_SAMPLES);
        
        processFrames(frames);
    }
    
    /**
     * Computes the Mel-Frequency Cepstral Coefficients (MFCCs) from audio frames.<br>
     * <br>
     * It assumes that pre-emphasis and framing have already been performed.
     * 
     * @param frames
     */
    @Override
    public void processFrames(double[][] frames) {
        // Step 3 - Windowing - Apply Hamming Window to all frames
        objFFT = new FFT(FRAME_LENGTH, WINDOW_FUNCTION);
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	frames[indexFrame] = objFFT.applyWindow(frames[indexFrame]);
        }
        
        // 0th coefficient will be discarded, hence 'MFFC_COEFFICIENTS - 1'
        // Total of static coefficients - Not considering Delta and Delta-Delta
        int intTotalStaticCoefficients = MFFC_COEFFICIENTS - 1;
        
        // Initializes the MFCC matrix
        double[][] initialMfcc = new double[frames.length][intTotalStaticCoefficients];
        double[] magnitudeSpectrum;
        double[] melFilterBank;
        double[] naturalLogarithm;
        double[] cepstralCoefficients;
        
        // Below computations are all based on individual frames
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
            // Step 4 - Magnitude Spectrum (FFT)
            magnitudeSpectrum = magnitudeSpectrum(frames[indexFrame]);
            
            // Step 5 - Mel Filter Bank
            melFilterBank = melFilterBank(magnitudeSpectrum);
            
            // Step 6 - Logarithm
            naturalLogarithm = naturalLogarithm(melFilterBank);
            
            // Step 7 - DCT - Cepstral coefficients
            cepstralCoefficients = cepstralCoefficients(naturalLogarithm);
            
            // Add resulting MFCC to array
            // 0th coefficient is discarded
            for (int indexCoefficient = 1; indexCoefficient < MFFC_COEFFICIENTS; indexCoefficient++) {
            	initialMfcc[indexFrame][indexCoefficient - 1] = cepstralCoefficients[indexCoefficient];
            }
        }
        
        // Step 8 - Delta & Delta Delta
        double[][] delta = performDelta(initialMfcc);   // Differential Coefficients
        double[][] deltaDelta = performDelta(delta);    // Acceleration Coefficients
        
        // Step 9 - Final MFCC feature
        int intTotalMfccCoefficients = intTotalStaticCoefficients * 3;   // Considering Delta and Delta-Delta
        mfcc = new double[frames.length][intTotalMfccCoefficients];
        
        // Concatenates MFCC + Delta + Delta Delta
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	for (int indexCoefficient = 0; indexCoefficient < intTotalStaticCoefficients; indexCoefficient++) {
        		mfcc[indexFrame][indexCoefficient] = RoundNumbers.round(initialMfcc[indexFrame][indexCoefficient], 4);                                 // MFCC
        		mfcc[indexFrame][indexCoefficient + intTotalStaticCoefficients] = RoundNumbers.round(delta[indexFrame][indexCoefficient], 4);          // Delta
        		mfcc[indexFrame][indexCoefficient + intTotalStaticCoefficients * 2] = RoundNumbers.round(deltaDelta[indexFrame][indexCoefficient], 4); // Delta Delta
        	}
        }
        
        // Calculates mean and standard deviation for each coefficient
        mean = new double[intTotalMfccCoefficients];
        standardDeviation = new double[intTotalMfccCoefficients];
        
        double[] coefficientValues;
        
    	for (int indexCoefficient = 0; indexCoefficient < intTotalMfccCoefficients; indexCoefficient++) {
    		coefficientValues = new double[frames.length];
    		
    		for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
    			coefficientValues[indexFrame] = mfcc[indexFrame][indexCoefficient];
    		}
    		
    		mean[indexCoefficient] = RoundNumbers.round(Statistics.calculateMean(coefficientValues), 4);
    		standardDeviation[indexCoefficient] = RoundNumbers.round(Statistics.calculateStandardDeviation(coefficientValues), 4);
    	}
    }
    
    /**
     * Returns the final MFCC Coefficients.
     * 
     * @return mfcc
     */
    @Override
    public double[][] getFeature() {
    	return mfcc;
    }
    
    @Override
    public double[] getMean() {
    	return mean;
    }
    
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
        
        for (int k = 0; k < frame.length; k++) {
        	magnitudeSpectrum[k] = Math.pow(objFFT.getReal()[k] * objFFT.getReal()[k] + objFFT.getImag()[k] * objFFT.getImag()[k], 0.5);
        }
        
        return magnitudeSpectrum;
    }
    
    /**
     * Calculates the Mel filter bank.
     * 
     * @param magnitudeSpectrum - Magnitude Spectrum
     * 
     * @return melFilterBank
     */
    private double[] melFilterBank(double[] magnitudeSpectrum) {
    	int[] fftBinIndices = fftBinIndices();
    	
        double[] temp = new double[MEL_FILTERS + 2];
        
        for (int k = 1; k <= MEL_FILTERS; k++) {
            double dblNum1 = 0;
            double dblNum2 = 0;
            
            for (int i = fftBinIndices[k - 1]; i <= fftBinIndices[k]; i++) {
            	dblNum1 += ((i - fftBinIndices[k - 1] + 1) / (fftBinIndices[k] - fftBinIndices[k-1] + 1)) * magnitudeSpectrum[i];
            }

            for (int i = fftBinIndices[k] + 1; i <= fftBinIndices[k + 1]; i++) {
            	dblNum2 += (1 - ((i - fftBinIndices[k]) / (fftBinIndices[k + 1] - fftBinIndices[k] + 1))) * magnitudeSpectrum[i];
            }

            temp[k] = dblNum1 + dblNum2;
        }
        
        double[] melFilterBank = new double[MEL_FILTERS];
        
        for (int i = 0; i < MEL_FILTERS; i++) {
        	melFilterBank[i] = temp[i + 1];
        }
        
        return melFilterBank;
    }
    
    /**
     * Calculates the FFT bin indices.
     * 
     * @return fftBinIndices - FFT bin indices
     */
    private int[] fftBinIndices() {
        int[] fftBinIndices = new int[MEL_FILTERS + 2];
        
        fftBinIndices[0] = (int) Math.round(LOWER_FILTER_FREQUENCY / dblSampleRate * FRAME_LENGTH);
        fftBinIndices[fftBinIndices.length - 1] = (int) (FRAME_LENGTH / 2);
        
        for (int indexMelFilter = 1; indexMelFilter <= MEL_FILTERS; indexMelFilter++) {
            double dblCenterFrequency = centerFrequency(indexMelFilter);
            
            fftBinIndices[indexMelFilter] = (int) Math.round(dblCenterFrequency / dblSampleRate * FRAME_LENGTH);
        }
        
        return fftBinIndices;
    }
    
    /**
     * Computes the natural logarithm.
     * 
     * @param melFilterBank - Mel Filter Bank
     * 
     * @return naturalLogarithm - Natural log
     */
    private double[] naturalLogarithm(double[] melFilterBank) {
        double[] naturalLogarithm = new double[melFilterBank.length];
        
        final double FLOOR = -50;
        
        for (int i = 0; i < melFilterBank.length; i++) {
        	naturalLogarithm[i] = Math.log(melFilterBank[i]);
            
            // check if ln() returns a value less than the floor
            if (naturalLogarithm[i] < FLOOR) {
            	naturalLogarithm[i] = FLOOR;
            }
        }
        
        return naturalLogarithm;
    }
    
    /**
     * Cepstral coefficients are calculated from the Mel log powers (DCT).
     * 
     * @param naturalLogarithm - Natural logarithm
     * 
     * @return Cepstral Coefficients
     */
    private double[] cepstralCoefficients(double[] naturalLogarithm) {
        double[] cepstralCoefficients = new double[MFFC_COEFFICIENTS];
        
        for (int i = 0; i < cepstralCoefficients.length; i++) {
            for (int j = 1; j <= MEL_FILTERS; j++) {
            	cepstralCoefficients[i] += naturalLogarithm[j - 1] * Math.cos(Math.PI * i / MEL_FILTERS * (j - 0.5));
            }
        }
        
        return cepstralCoefficients;
    }
    
    /**
     * Calculates logarithm with base 10.
     * 
     * @param dblValue - Number to take the log of
     * 
     * @return Base 10 logarithm
     */
    private double log10(double dblValue) {
        return Math.log(dblValue) / Math.log(10);
    }
    
    /**
     * Calculates center frequency.
     * 
     * @param indexMelFilter - Index of Mel filters
     * 
     * @return Center Frequency
     */
    private double centerFrequency(int indexMelFilter) {
        double[] mel = new double[2];
        mel[0] = frequencyToMel(LOWER_FILTER_FREQUENCY);
        mel[1] = frequencyToMel(dblSampleRate / 2);
        
        // take inverse mel of:
        double dblValueToInvert = mel[0] + ((mel[1] - mel[0]) / (MEL_FILTERS + 1)) * indexMelFilter;
        
        return inverseMel(dblValueToInvert);
    }
    
    /**
     * Calculates the inverse of Mel Frequency.
     * 
     * @param dblMelFrequency
     * 
     * @return dblInverse
     */
    private double inverseMel(double dblMelFrequency) {
        double dblInverse = Math.pow(10, dblMelFrequency / 2595) - 1;
        dblInverse = 700 * dblInverse;
        
        return dblInverse;
    }
    
    /**
     * Convert Frequency to Mel-Frequency.
     * 
     * @param dblFrequency
     * 
     * @return Mel-Frequency
     */
    private double frequencyToMel(double dblFrequency) {
        return 2595 * log10(1 + dblFrequency / 700);
    }
    
    /**
     * Performs Delta computation.<br>
     * <br>
     * <i>Delta</i>       - Differential Coefficient.<br>
     * <i>Delta Delta</i> - Acceleration Coefficient.
     * 
     * @param data
     * 
     * @return delta
     */
    private double[][] performDelta(double[][] data) {
    	double[][] delta = new double[data.length][data[0].length];
    	
    	double[][] paddedData = padding(data);
    	
    	double[][] paddedToProcess;
    	double[][] numerator;
    	
    	for (int indexFrame = 0; indexFrame < delta.length; indexFrame++) {
    		int intPaddedToProcess = 0;
    		
    		paddedToProcess = new double[2 * DELTA_N + 1][data[0].length];
    		
    		for (int indexPadded = indexFrame; indexPadded < indexFrame + 2 * DELTA_N + 1; indexPadded++) {
    			paddedToProcess[intPaddedToProcess] = paddedData[indexPadded];
    			intPaddedToProcess++;
    		}
    		
    		numerator = Matrices.multiplyMatrices(deltaProgression, paddedToProcess);
    		
    		for (int indexNumerator = 0; indexNumerator < numerator[0].length; indexNumerator++) {
    			numerator[0][indexNumerator] = numerator[0][indexNumerator] / dblDeltaDenominator;
    		}
    		
    		delta[indexFrame] = numerator[0];
    	}
    	
    	return delta;
    }
    
    /**
     * Pad out the data by repeating the border values,
     * according to the <i>DELTA_N</i> value.
     * 
     * @param data
     * 
     * @return paddedData
     */
    private double[][] padding(double[][] data) {
    	int intTotalRows = data.length;
    	int intTotalColumns = data[0].length;
    	int intTotalPaddedRows = data.length + (DELTA_N * 2);
    	
    	double[][] paddedData = new double[intTotalPaddedRows][intTotalColumns];
    	
    	// Initial & final padding
    	for (int i = 0; i < DELTA_N; i++) {
    		paddedData[i] = data[0];                                          // Initial padding
    		paddedData[intTotalRows + DELTA_N + i] = data[intTotalRows - 1];  // Final padding
    	}
    	
    	// Middle
    	for (int i = 0; i < intTotalRows; i++) {
    		paddedData[i + DELTA_N] = data[i];
    	}
		
    	return paddedData;
    }
}