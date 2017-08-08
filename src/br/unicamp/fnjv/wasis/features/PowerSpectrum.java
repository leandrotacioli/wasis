package br.unicamp.fnjv.wasis.features;

import java.util.ArrayList;
import java.util.List;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTWindowFunction;
import br.unicamp.fnjv.wasis.libs.RoundNumbers;

/**
 * Feature extraction class used to extract Power Spectrum (PS) from audio signal.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 18/Mai/2017
 */
public class PowerSpectrum {
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
    
    /**
	 * List of frequency (Hz) and intensity (dBFS) for each bin.
	 */
	private List<PowerSpectrumValues> lstPowerSpectrumValues;
	
	/**
	 * Retorna a lista com os valores de frequência 
	 * e a intensidade máxima (dB) para cada faixa de frequência.
	 * 
	 * @return lstPowerSpectrumValues
	 */
	public List<PowerSpectrumValues> getPS() {
		return lstPowerSpectrumValues;
	}
    
	/**
     * Feature extraction class used to extract Power Spectrum (PS) from audio signal.
     */
    public PowerSpectrum() {
    	
    }
    
    public void process(double[] samples, double dblSampleRate) {
    	// Initiliaze list of frequency (Hz) and intensity (dBFS)
        dblMaximumFrequency = dblSampleRate / 2;        // Divides by 2: Teorema Nyquist-Shannon - Default Value = 22050Hz
    	dblFrequencySamples = FRAME_LENGTH / 2;         // Default value = 512
    	int intFrequencySamples = (int) dblFrequencySamples;
    	
    	lstPowerSpectrumValues = new ArrayList<PowerSpectrumValues>();
    	
    	for (int indexFrequency = intFrequencySamples - 1; indexFrequency >= 0; indexFrequency--) {   // Lower frequencies are assigned first
    		double dblFrequency = dblMaximumFrequency - (dblMaximumFrequency / dblFrequencySamples * indexFrequency);
    		
    		lstPowerSpectrumValues.add(new PowerSpectrumValues((int) dblFrequency, -1000));
        }
    	
    	// Step 1 - Frame Blocking
        double[][] frames = Preprocessing.framing(samples, FRAME_LENGTH, OVERLAP_SAMPLES);
        
        // Step 2 - Windowing - Apply Hamming Window to all frames
        objFFT = new FFT(FRAME_LENGTH, WINDOW_FUNCTION);
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	frames[indexFrame] = objFFT.applyWindow(frames[indexFrame]);
        }
        
        // Below computations are all based on individual frames
        double[] amplitudes;
        double dblDecibel;
        double dblFrequency;
        int intFrequency;
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	// Step 3 - FFT
        	objFFT.executeFFT(frames[indexFrame]);
        	
        	amplitudes = objFFT.getAmplitudes();
        	
        	int intLastPowerSpectrumValueIndex = 0;
        	
        	for (int indexFrequency = 0; indexFrequency < intFrequencySamples; indexFrequency++) {
        		dblDecibel = RoundNumbers.round(amplitudes[indexFrequency]);
        		
	        	dblFrequency = dblMaximumFrequency / dblFrequencySamples * indexFrequency;
	    		dblFrequency += dblMaximumFrequency / dblFrequencySamples;
	    		
	    		intFrequency = (int) dblFrequency;
        		
        		for (int indexPowerSpectrumValue = intLastPowerSpectrumValueIndex; indexPowerSpectrumValue < lstPowerSpectrumValues.size(); indexPowerSpectrumValue++) {
        			if (intFrequency == lstPowerSpectrumValues.get(indexPowerSpectrumValue).getFrequency()) {
        				if (dblDecibel > lstPowerSpectrumValues.get(indexPowerSpectrumValue).getDecibel()) {
        					lstPowerSpectrumValues.get(indexPowerSpectrumValue).setDecibel(dblDecibel);
        					
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

        for (int indexPowerSpectrumValue = 0; indexPowerSpectrumValue < lstPowerSpectrumValues.size(); indexPowerSpectrumValue++) {
        	if (lstPowerSpectrumValues.get(indexPowerSpectrumValue).getDecibel() > dblHigherValue) {
        		dblHigherValue = lstPowerSpectrumValues.get(indexPowerSpectrumValue).getDecibel();
        	}
        }
        
        if (dblHigherValue >= 0) {
        	for (int indexPowerSpectrumValue = 0; indexPowerSpectrumValue < lstPowerSpectrumValues.size(); indexPowerSpectrumValue++) {
        		lstPowerSpectrumValues.get(indexPowerSpectrumValue).setDecibel(RoundNumbers.round(lstPowerSpectrumValues.get(indexPowerSpectrumValue).getDecibel() - dblHigherValue));
        	}
        }
    }
    
    /**
     * Filter Power Spectrum values from a frequency range.
     * 
     * @param intInitialFrequency
     * @param intFinalFrequency
     * 
     * @return lstPowerSpectrumFilteredValues
     */
    public List<PowerSpectrumValues> filterFrequencies(int intInitialFrequency, int intFinalFrequency) {
    	List<PowerSpectrumValues> lstPowerSpectrumFilteredValues = lstPowerSpectrumValues;
    	
    	// Delete values that do not belong within the initial and final frequencies (delete from the end to the beginning)
        int intMargin = (int) (dblMaximumFrequency / dblFrequencySamples); // Margin to take an inferior and superior sample
        
        for (int indexPowerSpectrumFilteredValue = lstPowerSpectrumFilteredValues.size() - 1; indexPowerSpectrumFilteredValue >= 0; indexPowerSpectrumFilteredValue--) {
        	if (lstPowerSpectrumFilteredValues.get(indexPowerSpectrumFilteredValue).getFrequency() > intFinalFrequency + intMargin) {
        		lstPowerSpectrumFilteredValues.remove(indexPowerSpectrumFilteredValue);
        	} else if (lstPowerSpectrumFilteredValues.get(indexPowerSpectrumFilteredValue).getFrequency() < intInitialFrequency - intMargin) {
        		lstPowerSpectrumFilteredValues.remove(indexPowerSpectrumFilteredValue);
        	}
        }
    	
    	return lstPowerSpectrumFilteredValues;
    }
}