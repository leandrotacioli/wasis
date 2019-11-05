package br.unicamp.fnjv.wasis.features;

/**
 * Features.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 23/Out/2017
 */
public abstract class Features {
	public static String POWER_SPECTRUM = "Power_Spectrum";
	public static String MFCC = "MFCC";
	public static String LPC = "LPC";
	public static String LPCC = "LPCC";
	public static String PLP = "PLP";
	public static String MFCC_LPC = "MFCC_LPC";
	public static String MFCC_LPCC = "MFCC_LPCC";
	public static String MFCC_PLP = "MFCC_PLP";
	public static String MFCC_LPC_LPCC_PLP = "MFCC_LPC_LPCC_PLP";
	
	/**
     * Take samples from an audio signal and computes the features.<br>
     * 
     * @param audioSignal
     */
	public abstract void process(double[] audioSignal);
	
	/**
     * Computes the features from audio frames.<br>
     * <br>
     * It assumes that framing have already been performed.
     * 
     * @param frames
     */
	public abstract void processFrames(double[][] frames);
	 
	/**
	 * Returns the feature coefficients.
	 */
	public abstract double[][] getFeature();
	
	/**
	 * Returns the mean of the coefficients.
	 */
	public abstract double[] getMean();
	
	/**
	 * Returns the standard deviation of the coefficients.
	 */
	public abstract double[] getStandardDeviation();
	
	/**
	 * Retorna uma string com todos os coeficientes de uma feature concatenados.<br>
	 * <br>
	 * Obs: Os coeficientes são separados por um ';'.
	 * 
	 * @param featureVector
	 * 
	 * @return strFeatureCoefficients
	 */
	public static String getFeatureCoefficients(double[] featureVector) {
		StringBuffer strBufferMfccVector = new StringBuffer();
		
		for (int indexValue = 0; indexValue < featureVector.length; indexValue++) {
			strBufferMfccVector.append(featureVector[indexValue] + ";");
		}
		
		String strFeatureCoefficients = strBufferMfccVector.toString();
		
		// Remove o último caracter que é um ';'
		if (strFeatureCoefficients.substring(strFeatureCoefficients.length() - 1).equals(";")) {
			strFeatureCoefficients = strFeatureCoefficients.substring(0, strFeatureCoefficients.length() - 1);
		}
		
		return strFeatureCoefficients;
	}
}