package br.unicamp.fnjv.wasis.libs;

public class RoundNumbers {
	
	/**
	 * Round a number to 6 decimal places.
	 * 
	 * @param dblValue
	 * 
	 * @return dblValueRounded
	 */
	public static double round(double dblValue) {
		return (double) Math.round(dblValue * 100d) / 100d;
	}
}
