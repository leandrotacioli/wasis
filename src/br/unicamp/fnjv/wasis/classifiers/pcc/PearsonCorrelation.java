package br.unicamp.fnjv.wasis.classifiers.pcc;

import java.util.ArrayList;
import java.util.List;

/**
 * Calcula a <i>Coeficiente de Correlação de Pearson</i> entre duas amostras.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 18/Mai/2017
 */
public class PearsonCorrelation {
	private List<PearsonCorrelationValues> lstXSample;   // Amostra X
	private List<PearsonCorrelationValues> lstYSample;   // Amostra Y
	
	private int intMinRegs = 10;
	
	private boolean blnCheckMinRegs;
	
	/**
	 * Retorna a amostra X.
	 * 
	 * @return lstXSample
	 */
	public List<PearsonCorrelationValues> getXSample() {
		return lstXSample;
	}
	
	/**
	 * Retorna a amostra Y.
	 * 
	 * @return lstYSample
	 */
	public List<PearsonCorrelationValues> getYSample() {
		return lstYSample;
	}
	
	/**
	 * Calcula a <i>Correlação de Pearson</i> entre duas amostras.
	 * 
	 * @param lstXSample - Amostra X
	 * @param lstYSample - Amostra Y
	 */
	public PearsonCorrelation(boolean blnCheckMinRegs, List<PearsonCorrelationValues> lstXSample, List<PearsonCorrelationValues> lstYSample) {
		this.blnCheckMinRegs = blnCheckMinRegs;
		this.lstXSample = lstXSample;
		this.lstYSample = lstYSample;
	}
	
	/**
	 * Calcula a soma de todos os elementos de um array.
	 * 
	 * @param array
	 * 
	 * @return dblSumArray
	 */
	private double calculateSum(double[] array) {
		double dblSumArray = 0;
		
		for (int i = 0; i < array.length; i++) {
			dblSumArray += array[i];
		}
		
		return dblSumArray;
	}
	
	/**
	 * Eleva ao quadrado todos os elementos de um array e calcula a soma.
	 * 
	 * @param array
	 * 
	 * @return dblSumSquare
	 */
	private double calculateSumSquare(double[] array) {
		double dblSumSquare = 0;
		
		for (int i = 0; i < array.length; i++) {
			dblSumSquare += array[i] * array[i];
		}
		
		return dblSumSquare;
	}
	
	/**
	 * Multiplica os valores dos arrays (ex: = X1 * Y1) e calcula a soma.
	 * 
	 * @param arrayXSample
	 * @param arrayYSample
	 * 
	 * @return dblSumMultiply
	 */
	private double multiplyArrays(double[] arrayXSample, double[] arrayYSample) {
		double dblSumMultiply = 0;
		
		for (int i = 0; i < arrayXSample.length; i++) {
			dblSumMultiply += arrayXSample[i] * arrayYSample[i];
		}
		
		return dblSumMultiply;
	}
	
	/**
	 * Calcula o coeficiente de correlação.
	 * 
	 * @return dblCorrelationValue
	 */
	public double calculateCorrelationCoeficient() {	
		// Regras para realizar a correlação:
		// - As duas amostras devem conter o mesmo número de registros;
		// - Deve comparar os valores dos mesmos índices nas duas amostras;
		List<PearsonCorrelationSamples> lstSamples = new ArrayList<PearsonCorrelationSamples>();
		
		// Tamanho da amostra X é maior que da amostra Y
		if (lstXSample.size() > lstYSample.size()) {
			for (int indexYSample = 0; indexYSample < lstYSample.size(); indexYSample++) {
				for (int indexXSample = 0; indexXSample < lstXSample.size(); indexXSample++) {
					if (lstXSample.get(indexXSample).getIndex() == lstYSample.get(indexYSample).getIndex()) {
						lstSamples.add(new PearsonCorrelationSamples(lstXSample.get(indexXSample).getIndex(), lstXSample.get(indexXSample).getValue(), lstYSample.get(indexYSample).getValue()));
						
						break;
					}
				}
			}
			
		// Tamanho da amostra X é menor que da amostra Y
		} else if (lstXSample.size() < lstYSample.size()) {
			for (int indexXSample = 0; indexXSample < lstXSample.size(); indexXSample++) {
				for (int indexYSample = 0; indexYSample < lstYSample.size(); indexYSample++) {
					if (lstXSample.get(indexXSample).getIndex() == lstYSample.get(indexYSample).getIndex()) {
						lstSamples.add(new PearsonCorrelationSamples(lstXSample.get(indexXSample).getIndex(), lstXSample.get(indexXSample).getValue(), lstYSample.get(indexYSample).getValue()));
						
						break;
					}
				}
			}
			
		// Tamanho da amostra X é igual ao da amostra Y
		} else if (lstXSample.size() == lstYSample.size()) {
			
			// Teste de força bruta para não Power Spectrum
			if (!blnCheckMinRegs) {
				for (int indexXSample = 0; indexXSample < lstXSample.size(); indexXSample++) {
					lstSamples.add(new PearsonCorrelationSamples(lstXSample.get(indexXSample).getIndex(), lstXSample.get(indexXSample).getValue(), lstYSample.get(indexXSample).getValue()));
				}
				
			} else {
				for (int indexXSample = 0; indexXSample < lstXSample.size(); indexXSample++) {
					for (int indexYSample = 0; indexYSample < lstYSample.size(); indexYSample++) {
						if (lstXSample.get(indexXSample).getIndex() == lstYSample.get(indexYSample).getIndex()) {
							lstSamples.add(new PearsonCorrelationSamples(lstXSample.get(indexXSample).getIndex(), lstXSample.get(indexXSample).getValue(), lstYSample.get(indexYSample).getValue()));
							
							break;
						}
					}
				}
			}
		}
		
		// Atribui valores aos arrays X e Y
		double[] arrayXSample = new double[lstSamples.size()];
		double[] arrayYSample = new double[lstSamples.size()];
		
		lstXSample = new ArrayList<PearsonCorrelationValues>();
		lstYSample = new ArrayList<PearsonCorrelationValues>();
		
		for (int indexValues = 0; indexValues < lstSamples.size(); indexValues++) {
			arrayXSample[indexValues] = lstSamples.get(indexValues).getValueX();
			arrayYSample[indexValues] = lstSamples.get(indexValues).getValueY();
			
			lstXSample.add(new PearsonCorrelationValues(lstSamples.get(indexValues).getIndex(), lstSamples.get(indexValues).getValueX()));
			lstYSample.add(new PearsonCorrelationValues(lstSamples.get(indexValues).getIndex(), lstSamples.get(indexValues).getValueY()));
		}
		
		// Realizar o cálculo da correlação
		double dblCorrelationValue = 0;
		
		if (blnCheckMinRegs) {
			if (arrayXSample.length >= intMinRegs) {
				double dblSumXSample = calculateSum(arrayXSample);
				double dblSumSquareXSample = calculateSumSquare(arrayXSample);
				
				double dblSumYSample = calculateSum(arrayYSample);
				double dblSumSquareYSample = calculateSumSquare(arrayYSample);
				
				double dblSumXYSamples = multiplyArrays(arrayXSample, arrayYSample);
				
				double dblValue1 = arrayXSample.length * dblSumXYSamples;
				double dblValue2 = dblSumXSample * dblSumYSample;
				
				double dblValue3 = Math.sqrt(arrayXSample.length * dblSumSquareXSample - dblSumXSample * dblSumXSample);
				double dblValue4 = Math.sqrt(arrayYSample.length * dblSumSquareYSample - dblSumYSample * dblSumYSample);
				
				dblCorrelationValue = (dblValue1 - dblValue2) / (dblValue3 * dblValue4);
			}
			
		} else {
			double dblSumXSample = calculateSum(arrayXSample);
			double dblSumSquareXSample = calculateSumSquare(arrayXSample);
			
			double dblSumYSample = calculateSum(arrayYSample);
			double dblSumSquareYSample = calculateSumSquare(arrayYSample);
			
			double dblSumXYSamples = multiplyArrays(arrayXSample, arrayYSample);
			
			double dblValue1 = arrayXSample.length * dblSumXYSamples;
			double dblValue2 = dblSumXSample * dblSumYSample;
			
			double dblValue3 = Math.sqrt(arrayXSample.length * dblSumSquareXSample - dblSumXSample * dblSumXSample);
			double dblValue4 = Math.sqrt(arrayYSample.length * dblSumSquareYSample - dblSumYSample * dblSumYSample);
			
			dblCorrelationValue = (dblValue1 - dblValue2) / (dblValue3 * dblValue4);
		}
		
		if (Double.isNaN(dblCorrelationValue)) {
			dblCorrelationValue = 0;
		}
		
		return dblCorrelationValue;
	}
	
	/**
	 * Retorna o coeficiente de determinação (porcentagem).
	 * 
	 * @param dblCorrelationValue
	 * 
	 * @return dblCorrelationPercentage
	 */
	public double getCoefientOfDetermination(double dblCorrelationValue) {
		if (dblCorrelationValue > 0) {
			return dblCorrelationValue * dblCorrelationValue * 100;
		} else {
			return 0;
		}
	}
	
	// **********************************************************************************************
	
	/**
     * Atribui valores para amostra X e Y.
     */
    class PearsonCorrelationSamples {
    	private int intIndex;
    	private double dblValueX;
    	private double dblValueY;
    	
		private int getIndex() {
    		return intIndex;
    	}
    	
    	private double getValueX() {
			return dblValueX;
		}

		private double getValueY() {
			return dblValueY;
		}
    	  
		/**
		 * Atribui valores para amostra X e Y.
		 * 
		 * @param intIndex
		 * @param dblValueX
		 * @param dblValueY
		 */
    	private PearsonCorrelationSamples(int intIndex, double dblValueX, double dblValueY) {
    		this.intIndex = intIndex;
    		this.dblValueX = dblValueX;
    		this.dblValueY = dblValueY;
    	}
    }
}