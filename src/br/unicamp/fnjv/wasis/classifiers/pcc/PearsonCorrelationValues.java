package br.unicamp.fnjv.wasis.classifiers.pcc;

/**
 * @author Leandro Tacioli
 * @version 1.0 - 18/Mai/2017
 */
public class PearsonCorrelationValues {
	private int intIndex;
	private double dblValue;
	
	/**
	 * Return the index.
	 * 
	 * @return intIndex
	 */
	public int getIndex() {
		return intIndex;
	}
	
	/**
	 * Return the Value.
	 * 
	 * @return dblValue
	 */
	public double getValue() {
		return dblValue;
	}
	
	/**
	 * Set the value.
	 * 
	 * @return dblValue
	 */
	public void setValue(double dblValue) {
		this.dblValue = dblValue;
	}
	
	/**
	 * @param intIndex - Index
	 * @param dblValue - Value
	 */
	public PearsonCorrelationValues(int intIndex, double dblValue) {
		this.intIndex = intIndex;
		this.dblValue = dblValue;
	}
}