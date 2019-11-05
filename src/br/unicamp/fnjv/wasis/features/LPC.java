package br.unicamp.fnjv.wasis.features;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTWindowFunction;
import br.unicamp.fnjv.wasis.libs.RoundNumbers;
import br.unicamp.fnjv.wasis.libs.Statistics;

/**
 * Feature extraction class used to extract Linear Predictive Coding (LPC) and
 * LPCC (Linear Prediction Cepstral Coefficients) from audio signals.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 27/Out/2017
 */
public class LPC extends Features {
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
    
    private double[][] lpc;
    private double[] lpcMean;
    private double[] lpcStandardDeviation;
    
    private double[][] lpcc;
    private double[] lpccMean;
    private double[] lpccStandardDeviation;
    
    /**
     * Feature extraction class used to extract Linear Predictive Coding (LPC) and
     * LPCC (Linear Prediction Cepstral Coefficients) from audio signals.
     */
    public LPC() {
    	this.intLpcOrder = LPC_ORDER;
    	this.intLpccOrder = LPCC_ORDER;
    	
    	reflectionCoeffs = null;
        ARParameters = null;
        alpha = null;
    }
    
    /**
     * Feature extraction class used to extract Linear Predictive Coding (LPC) and
     * LPCC (Linear Prediction Cepstral Coefficients) from audio signals.
     *
     * @param intLpcOrder  - LPC Order - Equivalent to the number of LPC coefficients
     * @param intLpccOrder - LPCC Order - Equivalent to the number of LPCC coefficients
     */
    public LPC(int intLpcOrder, int intLpccOrder) {
    	this.intLpcOrder = intLpcOrder;
    	this.intLpccOrder = intLpccOrder;
    	
        reflectionCoeffs = null;
        ARParameters = null;
        alpha = null;
    }
    
    /**
     * Take samples from an audio signal and computes the Linear Predictive Coding (LPC)
     * and LPCC (Linear Prediction Cepstral Coefficients).<br>
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
        frames = Preprocessing.framing(preEmphasis, FRAME_LENGTH, OVERLAP_SAMPLES);
	        
        processFrames(frames);
    }
        
	/**
     * Computes the Linear Predictive Coding (LPC) and the LPCC (Linear Prediction Cepstral Coefficients) from audio frames.<br>
     * <br>
     * It assumes that pre-emphasis and framing have already been performed.
     * 
     * @param frames
     */
    @Override
    public void processFrames(double[][] frames) {
    	this.frames = frames;
    	
        // Step 3 - Windowing - Apply Hamming Window to all frames
        FFT objFFT = new FFT(FRAME_LENGTH, WINDOW_FUNCTION);
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
        	frames[indexFrame] = objFFT.applyWindow(frames[indexFrame]);
        }
        
        reflectionCoeffs = new double[frames.length][intLpcOrder + 1];
        ARParameters = new double[frames.length][intLpcOrder + 1];
        alpha = new double[frames.length];
        
        double[] autoCorrelation;
        
        // Below computations are all based on individual frames
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
            // Step 4 - Autocorrelation
        	autoCorrelation = autoCorrelation(frames[indexFrame]);
        	
        	// Step 5 - Levinson Algorithm
        	levinsonDurbin(indexFrame, autoCorrelation);
        }
        
        computeFinalLPC();
        computeFinalLPCC();
    }
    
    /**
     * Returns the final LPC coefficients.
     * 
     * @return lpc
     */
    @Override
    public double[][] getFeature() {
    	return lpc;
    }
    
    /**
     * Returns the mean of the LPC coefficients.
     * 
     * @return lpcMean
     */
    @Override
    public double[] getMean() {
    	return lpcMean;
    }
    
    /**
     * Returns the standard deviation of the LPC coefficients.
     * 
     * @return lpcStandardDeviation
     */
    @Override
    public double[] getStandardDeviation() {
    	return lpcStandardDeviation;
    }
    
    /**
     * Returns the final LPCC coefficients.
     * 
     * @return lpcc
     */
    public double[][] getFeatureLpcc() {
    	return lpcc;
    }
    
    /**
     * Returns the mean of the LPCC coefficients.
     * 
     * @return lpccMean
     */
    public double[] getMeanLpcc() {
    	return lpccMean;
    }
    
    /**
     * Returns the standard deviation of the LPCC coefficients.
     * 
     * @return lpcStandardDeviation
     */
    public double[] getStandardDeviationLpcc() {
    	return lpccStandardDeviation;
    }
    
    /**
     * Computes the Linear Predictive Coding (LPC) and the LPCC (Linear Prediction Cepstral Coefficients) from
     * an autocorrelated frame.<br>
     * <br>
     * Basically, it is used in the Perceptual Linear Prediction (PLP) computation.
     * 
     * @param autocorrelatedFrame
     */
    public void processAutocorrelatedFrame(double[] autocorrelatedFrame) {
    	this.frames = new double[1][];
		this.frames[0] = autocorrelatedFrame;
    	
    	reflectionCoeffs = new double[1][intLpcOrder + 1];
        ARParameters = new double[1][intLpcOrder + 1];
        alpha = new double[1];
        
        levinsonDurbin(0, autocorrelatedFrame);
        
        computeFinalLPC();
        computeFinalLPCC();
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
     * Computes the final Linear Predictive Coding (LPC) coefficients.
     */
    private void computeFinalLPC() {
        lpc = new double[frames.length][intLpcOrder];
    	
    	for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
	    	for (int indexOrder = 0; indexOrder < intLpcOrder; indexOrder++) {
	    		lpc[indexFrame][indexOrder] = RoundNumbers.round(ARParameters[indexFrame][indexOrder + 1], 4);
	    	}
    	}
    	
    	// Calculates mean and standard deviation for each LPC coefficient
        lpcMean = new double[intLpcOrder];
        lpcStandardDeviation = new double[intLpcOrder];
        
        double[] coefficientValues;
        
    	for (int indexCoefficient = 0; indexCoefficient < intLpcOrder; indexCoefficient++) {
    		coefficientValues = new double[frames.length];
    		
    		for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
    			coefficientValues[indexFrame] = lpc[indexFrame][indexCoefficient];
    		}
    		
    		lpcMean[indexCoefficient] = RoundNumbers.round(Statistics.calculateMean(coefficientValues), 4);
    		lpcStandardDeviation[indexCoefficient] = RoundNumbers.round(Statistics.calculateStandardDeviation(coefficientValues), 4);
    	}
    }

    /**
     * Computes the LPC Cepstra (LPCC) from the AR predictor parameters and alpha using a recursion
     * invented by Oppenheim et al. The literature shows the optimal value of cepstral order to be:
     *
     * <pre>0.75 * intLpccOrder <= intLpccOrder <= 1.25 * intLpcOrder</pre>
     */
    private void computeFinalLPCC() {
        lpcc = new double[frames.length][intLpccOrder];
        
        for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
	        lpcc[indexFrame][0] = RoundNumbers.round(Math.log(alpha[indexFrame]), 4);
	        lpcc[indexFrame][1] = RoundNumbers.round(-ARParameters[indexFrame][1], 4);
	        
	        int i;
	        double dblSum;
	        
	        for (i = 2; i < Math.min(intLpccOrder, intLpcOrder + 1); i++) {
	        	dblSum = i * ARParameters[indexFrame][i];
	            
	            for (int j = 1; j < i; j++) {
	            	dblSum += ARParameters[indexFrame][j] * lpcc[indexFrame][i - j] * (i - j);
	            }
	            
	            lpcc[indexFrame][i] = RoundNumbers.round((-dblSum / i), 4);
	        }
	        
	        // Only if intLpcOrder > intLpcOrder + 1
	        for (; i < intLpccOrder; i++) {
	        	dblSum = 0;
	            
	            for (int j = 1; j <= intLpcOrder; j++) {
	            	dblSum += ARParameters[indexFrame][j] * lpcc[indexFrame][i - j] * (i - j);
	            }
	            
	            lpcc[indexFrame][i] = RoundNumbers.round((-dblSum / i), 4);
	        }
        }
        
        // Calculates mean and standard deviation for each LPCC coefficient
        lpccMean = new double[intLpccOrder];
        lpccStandardDeviation = new double[intLpccOrder];
        
        double[] coefficientValues;
        
    	for (int indexCoefficient = 0; indexCoefficient < intLpccOrder; indexCoefficient++) {
    		coefficientValues = new double[frames.length];
    		
    		for (int indexFrame = 0; indexFrame < frames.length; indexFrame++) {
    			coefficientValues[indexFrame] = lpcc[indexFrame][indexCoefficient];
    		}
    		
    		lpccMean[indexCoefficient] = RoundNumbers.round(Statistics.calculateMean(coefficientValues), 4);
    		lpccStandardDeviation[indexCoefficient] = RoundNumbers.round(Statistics.calculateStandardDeviation(coefficientValues), 4);
    	}
    }
}