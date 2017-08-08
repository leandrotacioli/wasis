package br.unicamp.fnjv.wasis.features;

/**
 * Model to list the values extracted of Power Spectrum (PS) feature representation.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 18/Mai/2017
 */
public class PowerSpectrumValues {
	private int intFrequency;
	private double dblDecibel;
	
	/**
	 * Return the frequency value.
	 * 
	 * @return intFrequency
	 */
	public int getFrequency() {
		return intFrequency;
	}
	
	/**
	 * Return the dBFS (Decibels relative to full scale) Value.
	 * 
	 * @return dblValue
	 */
	public double getDecibel() {
		return dblDecibel;
	}
	
	/**
	 * Set the dBFS (Decibels relative to full scale) value.
	 * 
	 * @return dblDecibel
	 */
	public void setDecibel(double dblDecibel) {
		this.dblDecibel = dblDecibel;
	}
	
	/**
	 * Modelo para listagem dos valores que serão extraídos para realizar a comparação dos áudios.
	 * 
	 * @param intFrequency - Frequency
	 * @param dblDecibel - dBFS (Decibels relative to full scale)
	 */
	public PowerSpectrumValues(int intFrequency, double dblDecibel) {
		this.intFrequency = intFrequency;
		this.dblDecibel = dblDecibel;
	}
}