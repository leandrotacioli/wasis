package br.unicamp.fnjv.wasis.libs;

/**
 * http://introcs.cs.princeton.edu/java/stdlib/StdStats.java.html
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 15/Mai/2017
 */
public class Statistics {

	private Statistics() {
		
	}
	
	/**
     * Returns the average value in the specified array.
     *
     * @param  a the array
     * 
     * @return the average value
     */
    public static double calculateMean(double[] arrayValues) {
        validateNotNull(arrayValues);

        if (arrayValues.length == 0) {
        	return Double.NaN;
        }
        
        double dblSum = calculateSum(arrayValues);
        
        return dblSum / arrayValues.length;
    }
    
    /**
     * Returns the sample standard deviation in the specified array.
     *
     * @param  arrayValues
     * 
     * @return standard deviation
     */
    public static double calculateStandardDeviation(double[] arrayValues) {
        validateNotNull(arrayValues);
        
        return Math.sqrt(calculateVariance(arrayValues));
    }
    
    /**
     * Returns the sample variance in the specified array.
     *
     * @param  a the array
     * 
     * @return the sample variance in the array {@code a[]};
     *         {@code Double.NaN} if no such value
     */
    public static double calculateVariance(double[] arrayValues) {
        validateNotNull(arrayValues);

        if (arrayValues.length == 0) {
        	return Double.NaN;
        }
        
        double dblAverage = calculateMean(arrayValues);
        double dblSum = 0.0;
        
        for (int i = 0; i < arrayValues.length; i++) {
        	dblSum += (arrayValues[i] - dblAverage) * (arrayValues[i] - dblAverage);
        }
        
        return dblSum / (arrayValues.length - 1);
    }
    
    /**
     * Returns the sum of all values in the specified array.
     *
     * @param  a the array
     * 
     * @return the sum of all values in the array
     */
    private static double calculateSum(double[] arrayValues) {
        validateNotNull(arrayValues);
        
        double dblSum = 0.0;
        
        for (int i = 0; i < arrayValues.length; i++) {
        	dblSum += arrayValues[i];
        }
        
        return dblSum;
    }
    
    /**
     * Throw an IllegalArgumentException if x is null
     * 
     * @param x
     */
    private static void validateNotNull(Object x) {
        if (x == null) {
            throw new IllegalArgumentException("Argument is null");
        }
    }
}
