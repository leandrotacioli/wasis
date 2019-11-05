package br.unicamp.fnjv.wasis.features;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTWindowFunction;
import br.unicamp.fnjv.wasis.libs.RoundNumbers;

/**
 * Feature extraction class used to extract Power Spectrum (PS) from audio signals.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 26/Out/2017
 */
public class PowerSpectrum extends Features {
	/** Number of samples per frame */
    private final int FRAME_LENGTH = 1024;
    
    /** Number of overlapping samples (Usually 50% of the <i>FRAME_LENGTH</i>) */
    private final int OVERLAP_SAMPLES = FRAME_LENGTH / 2;
    
    /** Window Function */
    private final String WINDOW_FUNCTION = FFTWindowFunction.HAMMING;
    
    /** Fast Fourier Transform */
    private FFT objFFT;
    
    private double dblMaximumFrequency;
    private double dblFrequencySamples;
    
    private int intFrequencySamples;
    
	/** Final Power Spectrum Coefficients<br>
	 * ps[0][x] - Frequency values
	 * <br>
	 * ps[1][x] - Decibel values */
    private double[][] ps;
    
	/**
     * Feature extraction class used to extract Power Spectrum (PS) from audio signal.<br>
     * <br>
     * OBS: Assumes initial frequency = 0Hz, and final frequency = 22050Hz.
     */
    public PowerSpectrum(double dblSampleRate) {
    	this(dblSampleRate, 0, (int) dblSampleRate / 2);
    }
    
    /**
     * Feature extraction class used to extract Power Spectrum (PS) from an audio signals.
     * 
     * @param dblSampleRate
     * @param intInitialFrequency
     * @param intFinalFrequency
     */
    public PowerSpectrum(double dblSampleRate, int intInitialFrequency, int intFinalFrequency) {
    	// ******************************************************************************************8
    	// Initiliaze matrix of frequency (Hz) and intensity (dBFS)
        dblMaximumFrequency = dblSampleRate / 2;          // Divides by 2: Teorema Nyquist-Shannon - Default Value = 22050Hz
    	dblFrequencySamples = FRAME_LENGTH / 2;           // Default value = 512
    	intFrequencySamples = (int) dblFrequencySamples;
    	
    	// Computes the final number of coefficients filtering initial and final frequencies
    	List<Integer> lstCoefficients = new ArrayList<Integer>();
    	
    	int intMargin = (int) (dblMaximumFrequency / dblFrequencySamples);   // Margin to take an inferior and superior sample
    	
    	for (int indexFrequency = 0; indexFrequency < intFrequencySamples; indexFrequency++) {
    		double dblFrequency = dblMaximumFrequency - (dblMaximumFrequency / dblFrequencySamples * indexFrequency);
    		
    		if ((dblFrequency >= intInitialFrequency - intMargin) && (dblFrequency <= intFinalFrequency + intMargin)) {
    			lstCoefficients.add((int) dblFrequency);
    		}
        }
    	
    	Collections.sort(lstCoefficients); // Sort the coefficients from lowest to highest frequencies
    	
    	ps = new double[2][lstCoefficients.size()];
    	
    	for (int indexCoefficient = 0; indexCoefficient < lstCoefficients.size(); indexCoefficient++) {
    		ps[0][indexCoefficient] = lstCoefficients.get(indexCoefficient);
    		ps[1][indexCoefficient] = -1000;   // Initiate the coefficients with a very low decibel value
    	}
    }
    
    /**
     * Take samples from an audio signal and computes the Power Spectrum (PS).<br>
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
     * Computes the Power Spectrum (PS) from audio frames.<br>
     * <br>
     * It assumes that framing has already been performed.
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
        
        // Below computations are all based on individual frames
        double[] amplitudes;
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	// Step 3 - FFT
        	objFFT.executeFFT(frames[indexFrame]);
        	
        	amplitudes = objFFT.getAmplitudes();
        	
        	int intLastPowerSpectrumValueIndex = 0;
        	
        	for (int indexFrequency = 0; indexFrequency < intFrequencySamples; indexFrequency++) {
	        	double dblFrequency = dblMaximumFrequency / dblFrequencySamples * indexFrequency;
	    		dblFrequency += dblMaximumFrequency / dblFrequencySamples;
	    		
	    		int intFrequency = (int) dblFrequency;
	    		
        		for (int indexPowerSpectrumValue = intLastPowerSpectrumValueIndex; indexPowerSpectrumValue < ps[0].length; indexPowerSpectrumValue++) {
        			if (intFrequency == ps[0][indexPowerSpectrumValue]) {
        				double dblDecibel = amplitudes[indexFrequency];
        				
        				if (dblDecibel > ps[1][indexPowerSpectrumValue]) {
        					ps[1][indexPowerSpectrumValue] = RoundNumbers.round(dblDecibel, 4);
        					
        					intLastPowerSpectrumValueIndex = indexPowerSpectrumValue;
        					
        					break;
        				}
        			}
        		}
        	}
        }
        
        // dBFS should accept only negative values
        // In case of positive, all the values are adjusted from the difference of the higher value
        double dblHigherValue = -1000;
        
        for (int indexPowerSpectrumValue = 0; indexPowerSpectrumValue < ps[0].length; indexPowerSpectrumValue++) {
        	if (ps[1][indexPowerSpectrumValue] > dblHigherValue) {
        		dblHigherValue = ps[1][indexPowerSpectrumValue];
        	}
        }
        
        if (dblHigherValue >= 0) {
        	for (int indexPowerSpectrumValue = 0; indexPowerSpectrumValue < ps[0].length; indexPowerSpectrumValue++) {
        		ps[1][indexPowerSpectrumValue] = RoundNumbers.round((ps[1][indexPowerSpectrumValue] - dblHigherValue), 4);
        	}
        }
    }
    
    @Override
    public double[][] getFeature() {
    	return ps;
    }
    
    @Override
	public double[] getMean() {
		return null;
	}
	
    @Override
	public double[] getStandardDeviation() {
		return null;
	}
}