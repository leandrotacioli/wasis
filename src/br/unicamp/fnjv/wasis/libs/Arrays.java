package br.unicamp.fnjv.wasis.libs;

/**
 * Perform array operations.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 01/Nov/2017
 */
public class Arrays {

	/**
	 * Perform array operations.
	 */
	private Arrays() {
		
	}
	
	/**
	 * Concatenate two arrays.
	 * 
	 * @param arrayA
	 * @param arrayB
	 * 
	 * @return concatenatedArray
	 */
	public static double[] concatenateArrays(double[] arrayA, double[] arrayB) {
		double[] concatenatedArray = new double[arrayA.length + arrayB.length];
		
		System.arraycopy(arrayA, 0, concatenatedArray, 0, arrayA.length);
        System.arraycopy(arrayB, 0, concatenatedArray, arrayA.length, arrayB.length);
		
		return concatenatedArray;
	}
}