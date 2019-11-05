package br.unicamp.fnjv.wasis.libs;

public class RoundNumbers {
	
	/**
	 * Round a number to N decimal places.<br>
	 * OBS: Up to six decimal places.
	 * 
	 * @param dblValue
	 * @param intDecimalPlaces
	 * 
	 * @return dblValueRounded
	 */
	public static double round(double dblValue, int intDecimalPlaces) {
		if (intDecimalPlaces == 0) {
			int intValue = (int) dblValue;
			return (double) intValue;
		} else {
			double dblDecimalPlaces = 10;
			
			for (int indexDecimalPlaces = 1; indexDecimalPlaces < intDecimalPlaces; indexDecimalPlaces++) {
				dblDecimalPlaces = dblDecimalPlaces * 10;
			}
			
			return (double) Math.round(dblValue * dblDecimalPlaces) / dblDecimalPlaces;
		}
	}
}