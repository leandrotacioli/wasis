package br.unicamp.fnjv.wasis.libs;

/**
 * Perform matrix operations.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 01/Nov/2017
 */
public class Matrices {
	
	/**
	 * Perform matrix operations.
	 */
	private Matrices() {
		
	}

	/**
	 * Matrix multiplication method.
	 *
	 * @param matrixA - Multiplicand
	 * @param matrixB - Multiplier
	 * 
	 * @return Product
	 */
    public static double[][] multiplyMatrices(double[][] matrixA, double[][] matrixB) {
        int intMatrixAColumnLength = matrixA[0].length;
        int intMatrixBRowLength = matrixB.length;
        
        // Matrix multiplication is not possible
        if (intMatrixAColumnLength != intMatrixBRowLength) {
        	return null;
        }
        
        int intFinalRowLength = matrixA.length;
        int intFinalColumnLength = matrixB[0].length;
        
        double[][] finalMatrix = new double[intFinalRowLength][intFinalColumnLength];
        
        for (int i = 0; i < intFinalRowLength; i++) {                    // Rows from Matrix A
            for (int j = 0; j < intFinalColumnLength; j++) {             // Columns from Matrix B
                for (int k = 0; k < intMatrixAColumnLength; k++) {       // Columns from Matrix A
                	finalMatrix[i][j] += matrixA[i][k] * matrixB[k][j];
                }
            }
        }
        
        return finalMatrix;
    }
}