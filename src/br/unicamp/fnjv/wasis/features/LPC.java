package br.unicamp.fnjv.wasis.features;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTWindowFunction;
import br.unicamp.fnjv.wasis.libs.RoundNumbers;
import br.unicamp.fnjv.wasis.libs.Statistics;

/**
 * Feature extraction class used to extract Linear Predictive Coding (LPC) and
 * LPCC (Linear Prediction Cepstral Coefficients) from audio signal.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 15/Mai/2017
 */
public class LPC {
	/** LPC Order - Equivalent to the number of LPC coefficients. */
    private final int LPC_ORDER = 24;
    private int intLpcOrder;
    
	/** LPCC Order - Equivalent to the number of LPCC coefficients. */
    private final int LPCC_ORDER = 24;
	private int intLpccOrder;
    
    /** Number of samples per frame. */
    private final int FRAME_LENGTH = 1024;
    
    /** Number of overlapping samples (Usually 50% of the <i>FRAME_LENGTH</i>). */
    private final int OVERLAP_SAMPLES = FRAME_LENGTH / 2;
    
    /** Window Function */
    private final String WINDOW_FUNCTION = FFTWindowFunction.HAMMING;
    
    /** Lambda. */
    private final double LAMBDA = 0.0;
    
    /** Frames from where LPC is computed. */
    private double[][] frames;
    
    /** Reflection coefficients.<br>
     * Each row is related to its respective frame. */
    private double[][] reflectionCoeffs;
    
    /** Autoregressive parameters.
     * Each row is related to its respective frame. */
    private double[][] ARParameters;
    
    /** Alpha - Energy of the frame.
     * Each column is related to its respective frame. */
    private double[] alpha;
    
    /** Performs preprocessing to extract LPC */
    private boolean blnPerformPreprocessing;
    
    private double[][] lpc;
    private double[] meanLPC;
    private double[] standardDeviationLPC;
    
    private double[][] lpcc;
    private double[] meanLPCC;
    private double[] standardDeviationLPCC;
    
    /**
     * Returns the final LPC coefficients.
     * 
     * @return lpc
     */
    public double[][] getLPC() {
    	return lpc;
    }
    
    /**
     * Returns the mean of the LPC coefficients.
     * 
     * @return meanLPC
     */
    public double[] getMeanLPC() {
    	return meanLPC;
    }
    
    /**
     * Returns the standard deviation of the LPC coefficients.
     * 
     * @return standardDeviationLPC
     */
    public double[] getStandardDeviationLPC() {
    	return standardDeviationLPC;
    }
    
    /**
     * Returns the final LPCC coefficients.
     * 
     * @return lpcc
     */
    public double[][] getLPCC() {
    	return lpcc;
    }
    
    /**
     * Returns the mean of the LPCC coefficients.
     * 
     * @return meanLPCC
     */
    public double[] getMeanLPCC() {
    	return meanLPCC;
    }
    
    /**
     * Returns the standard deviation of the LPCC coefficients.
     * 
     * @return standardDeviationLPC
     */
    public double[] getStandardDeviationLPCC() {
    	return standardDeviationLPCC;
    }
    
    /**
     * Feature extraction class used to extract Linear Predictive Coding (LPC) from audio signal.
     */
    public LPC() {
    	this.intLpcOrder = LPC_ORDER;
    	this.intLpccOrder = LPCC_ORDER;
    	this.blnPerformPreprocessing = true;
    	
    	reflectionCoeffs = null;
        ARParameters = null;
        alpha = null;
    }
    
    /**
     * Feature extraction class used to extract Linear Predictive Coding (LPC) and
     * LPCC (Linear Prediction Cepstral Coefficients) from audio signal.
     *
     * @param intLpcOrder  - LPC Order - Equivalent to the number of LPC coefficients
     * @param intLpccOrder - LPCC Order - Equivalent to the number of LPCC coefficients
     * @param blnPerformPreprocessing - This flag allows the preprocessing of
     *                                  the signal (e.g. PreEmphasis, Framing, Autocorrelation).
     *                                  It should not receive <i>TRUE</i> in case of features that make use of LPC,
     *                                  such as Perceptual Linear prediction (PLP).
     *                                  Also, we assume that only one frame will be processed when it is value is <i>FALSE</i>. 
     */
    public LPC(int intLpcOrder, int intLpccOrder, boolean blnPerformPreprocessing) {
    	this.intLpcOrder = intLpcOrder;
    	this.intLpccOrder = intLpccOrder;
    	this.blnPerformPreprocessing = blnPerformPreprocessing;
    	
        reflectionCoeffs = null;
        ARParameters = null;
        alpha = null;
    }
    
    /**
     * Takes an audio signal or an autocorrelated sample, and computes the Linear Predictive Coding (LPC).
     * 
     * @param samples
     */
    public void process(double[] samples) {
    	// LPC default extraction
    	if (blnPerformPreprocessing) {
	    	// Step 1 - Pre-Emphasis
	        double[] preEmphasis = Preprocessing.preEmphasis(samples);
	        
	        // Step 2 - Frame Blocking
	        frames = Preprocessing.framing(preEmphasis, FRAME_LENGTH, OVERLAP_SAMPLES);
	        
	        // Step 3 - Windowing - Apply Hamming Window to all frames
	        FFT objFFT = new FFT(FRAME_LENGTH, WINDOW_FUNCTION);
	        
	        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
	        	frames[indexFrame] = objFFT.applyWindow(frames[indexFrame]);
	        }
	        
	    // LPC extraction without preprocessing
	    // It is supposed to receive an autocorrelated sample
    	} else {
    		frames = new double[1][];
    		frames[0] = samples;
    	}
        
        reflectionCoeffs = new double[frames.length][intLpcOrder + 1];
        ARParameters = new double[frames.length][intLpcOrder + 1];
        alpha = new double[frames.length];
        
        // Below computations are all based on individual frames
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	double[] autoCorrelation = null;
        	
        	// Step 4 - Autocorrelation (LPC default extraction)
        	if (blnPerformPreprocessing) {
        		autoCorrelation = autoCorrelation(frames[indexFrame]);
        	} else {
        		autoCorrelation = frames[indexFrame];    // Autocorrelation already performed
        	}
        	
        	// Step 5 - Levinson Algorithm
        	levinsonDurbin(indexFrame, autoCorrelation);
        }
        
        // Final LPC coefficients
        lpc = new double[frames.length][intLpcOrder];
    	
    	for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
	    	for (int indexOrder = 0; indexOrder < intLpcOrder; indexOrder++) {
	    		lpc[indexFrame][indexOrder] = RoundNumbers.round(ARParameters[indexFrame][indexOrder + 1]);
	    	}
    	}
    	
    	// Calculates mean and standard deviation for each LPC coefficient
        meanLPC = new double[intLpcOrder];
        standardDeviationLPC = new double[intLpcOrder];
        
        double[] coefficientValues;
        
    	for (int indexCoefficient = 0; indexCoefficient < intLpcOrder; indexCoefficient++) {
    		coefficientValues = new double[frames.length];
    		
    		for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
    			coefficientValues[indexFrame] = lpc[indexFrame][indexCoefficient];
    		}
    		
    		meanLPC[indexCoefficient] = Statistics.calculateMean(coefficientValues);
    		standardDeviationLPC[indexCoefficient] = Statistics.calculateStandardDeviation(coefficientValues);
    	}
    }
    
    /**
     * Find the order-P autocorrelation array for the sequence x of length L and warping of lambda.
     * 
     * @param samples
     * 
     * @return autoCorrelation array
     */
    private double[] autoCorrelation(double[] samples) {
		double[] R = new double[intLpcOrder + 1];
		double[] dl = new double[samples.length];
		double[] Rt = new double[samples.length];
		double r1, r2, r1t;
		
		R[0] = 0;
		Rt[0] = 0;
		r1 = 0;
		r2 = 0;
		r1t = 0;
		
		for (int k = 0; k < samples.length; k++) {
			Rt[0] += samples[k] * samples[k];

			dl[k] = r1 - LAMBDA * (samples[k] - r2);
			r1 = samples[k];
			r2 = dl[k];
		}
		
		for (int i = 1; i < R.length; i++) {
			Rt[i] = 0;
			r1 = 0;
			r2 = 0;
			
			for (int k = 0; k < samples.length; k++) {
				Rt[i] += dl[k] * samples[k];

				r1t = dl[k];
				dl[k] = r1 - LAMBDA * (r1t - r2);
				r1 = r1t;
				r2 = dl[k];
			}
		}
		
		for (int i = 0; i < R.length; i++) {
			R[i] = Rt[i];
		}
		
		return R;
	}

    /**
     * Method to compute Linear Prediction Coefficients for a frame using the Levinson-Durbin algorithm.<br>
     * Assumes the following sign convention:
     * <pre>prediction(x[t]) = Sum_i {Ar[i] * x[t-i]}.</pre>
     *
     * @param intIndexFrame   - Frame index
     * @param autoCorrelation - Autocorrelation array
     */
    private void levinsonDurbin(int intIndexFrame, double[] autoCorrelation) {
        double[] backwardPredictor = new double[intLpcOrder + 1];
        
        alpha[intIndexFrame] = autoCorrelation[0];
        reflectionCoeffs[intIndexFrame][1] = -autoCorrelation[1] / autoCorrelation[0];
        ARParameters[intIndexFrame][0] = 1.0;
        ARParameters[intIndexFrame][1] = reflectionCoeffs[intIndexFrame][1];
        alpha[intIndexFrame] *= (1 - reflectionCoeffs[intIndexFrame][1] * reflectionCoeffs[intIndexFrame][1]);
        
        for (int i = 2; i <= intLpcOrder; i++) {
            for (int j = 1; j < i; j++) {
                backwardPredictor[j] = ARParameters[intIndexFrame][i - j];
            }
            
            reflectionCoeffs[intIndexFrame][i] = 0;
            
            for (int j = 0; j < i; j++) {
                reflectionCoeffs[intIndexFrame][i] -= ARParameters[intIndexFrame][j] * autoCorrelation[i - j];
            }
            
            reflectionCoeffs[intIndexFrame][i] /= alpha[intIndexFrame];

            for (int j = 1; j < i; j++) {
                ARParameters[intIndexFrame][j] += reflectionCoeffs[intIndexFrame][i] * backwardPredictor[j];
            }
            
            ARParameters[intIndexFrame][i] = reflectionCoeffs[intIndexFrame][i];
            alpha[intIndexFrame] *= (1 - reflectionCoeffs[intIndexFrame][i] * reflectionCoeffs[intIndexFrame][i]);
        }
    }

    /**
     * Computes the LPC Cepstra (LPCC) from the AR predictor parameters and alpha using a recursion
     * invented by Oppenheim et al. The literature shows the optimal value of cepstral order to be:
     *
     * <pre>0.75 * intLpccOrder <= intLpccOrder <= 1.25 * intLpcOrder</pre>
     * 
     * <b>IMPORTANT: It is necessary to perform the <i>process()</i> method to get the LPCC coefficients.<b>
     */
    public void processLPCC() {
        lpcc = new double[frames.length][intLpccOrder];
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
	        lpcc[indexFrame][0] = RoundNumbers.round(Math.log(alpha[indexFrame]));
	        lpcc[indexFrame][1] = RoundNumbers.round(-ARParameters[indexFrame][1]);
	        
	        int i;
	        double dblSum;
	        
	        for (i = 2; i < Math.min(intLpccOrder, intLpcOrder + 1); i++) {
	        	dblSum = i * ARParameters[indexFrame][i];
	            
	            for (int j = 1; j < i; j++) {
	            	dblSum += ARParameters[indexFrame][j] * lpcc[indexFrame][i - j] * (i - j);
	            }
	            
	            lpcc[indexFrame][i] = RoundNumbers.round(-dblSum / i);
	        }
	        
	        // Only if intLpcOrder > intLpcOrder + 1
	        for (; i < intLpccOrder; i++) {
	        	dblSum = 0;
	            
	            for (int j = 1; j <= intLpcOrder; j++) {
	            	dblSum += ARParameters[indexFrame][j] * lpcc[indexFrame][i - j] * (i - j);
	            }
	            
	            lpcc[indexFrame][i] = RoundNumbers.round(-dblSum / i);
	        }
        }
        
        // Calculates mean and standard deviation for each LPCC coefficient
        meanLPCC = new double[intLpccOrder];
        standardDeviationLPCC = new double[intLpccOrder];
        
        double[] coefficientValues;
        
    	for (int indexCoefficient = 0; indexCoefficient < intLpccOrder; indexCoefficient++) {
    		coefficientValues = new double[frames.length];
    		
    		for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
    			coefficientValues[indexFrame] = lpcc[indexFrame][indexCoefficient];
    		}
    		
    		meanLPCC[indexCoefficient] = Statistics.calculateMean(coefficientValues);
    		standardDeviationLPCC[indexCoefficient] = Statistics.calculateStandardDeviation(coefficientValues);
    	}
    }
}