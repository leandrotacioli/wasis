package br.unicamp.fnjv.wasis.libs;

/**
 * Biblioteca Global - Utilizada para transformação de horas
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 06/Mai/2014
 *
 */
public class ClockTransformations {
	
	/**
	 * Transforma tempo (em milisegundos) no formato de relógio digital (ex: 01:02,399)
	 * 
	 * @param intTimeMilliseconds - Tempo em milisegundos
	 * 
	 * @return strTimeTransformed
	 */
	public static String millisecondsIntoDigitalFormat(int intTimeMilliseconds) {
		String strTimeTransformed = null;
	  	
		int intTimeSeconds = (int) Math.round(intTimeMilliseconds / 1000);
		
	    if (intTimeMilliseconds > 0) {
	    	int intMinutes = (int) Math.floor(intTimeSeconds / 60);
	    	int intHours = (int) Math.floor(intMinutes / 60);
	    	intMinutes = intMinutes - intHours * 60;
	    	int intSeconds = (int) (intTimeSeconds - intMinutes * 60 - intHours * 3600);
          
	    	String strTime = String.format("%03d", intTimeMilliseconds);
	    	String strMilliseconds = strTime.substring(strTime.length() - 3, strTime.length());
          
	    	String strSeconds = String.format("%02d", intSeconds);
	    	String strMinutes = String.format("%02d", intMinutes);
	    		
	    	strTimeTransformed = intHours + ":" + strMinutes + ":" + strSeconds + "." + strMilliseconds;
	    } else {
	    	strTimeTransformed = "0:00:00.000";
	    }
      
	    return strTimeTransformed;
	}
	
	/**
	 * Transforma tempo (em milisegundos) no formato de relógio digital 
	 * para exibição no eixo X do espectrograma/waveform 
	 * 
	 * @param intTimeMilliseconds - Tempo em milisegundos
	 * 
	 * @return strTimeTransformed
	 */
	public static String millisecondsIntoClockFormatGraphAxis(int intTimeMilliseconds) {
		String strTimeTransformed = "0";
	  	
		int intTimeSeconds = (int) Math.round(intTimeMilliseconds / 1000);
		
		if (intTimeMilliseconds > 0) {
			int intMinutes = (int) Math.floor(intTimeSeconds / 60);
	    	int intHours = (int) Math.floor(intMinutes / 60);
	    	intMinutes = intMinutes - intHours * 60;
	    	int intSeconds = (int) (intTimeSeconds - intMinutes * 60 - intHours * 3600);
          
	    	String strTime = String.format("%03d", intTimeMilliseconds); // Insere 3 zeros a esquerda
	    	String strMilliseconds = strTime.substring(strTime.length() - 3, strTime.length());
	    	int intMilliseconds = Integer.parseInt(strMilliseconds);
			
	    	String strHours = "00h";
	    	String strMinutes = "00m";
	    	String strSeconds = "00s";
	    	strMilliseconds = "";
	    	
	    	// Horas
	    	if (intHours > 0) {
	    		strHours = String.format("%02d", intMinutes) + "h";
	    		
	    		if (intMinutes > 0) {
	    			strMinutes = String.format("%02d", intMinutes) + "m";
	    			
	    			if (intSeconds > 0) {
		    			strSeconds = String.format("%02d", intSeconds) + "s";
		    			
		    			if (intMilliseconds > 0) {
			    			strMilliseconds = String.format("%03d", intMilliseconds) + "ms";
			    		}
			    	}
	    		}
	    		
	    		strTimeTransformed = strHours + strMinutes + strSeconds + strMilliseconds;
	    	
	    	// Minutos
	    	} else if (intMinutes > 0) {
	    		strMinutes = String.format("%02d", intMinutes) + "m";
	    		
	    		if (intSeconds > 0) {
	    			strSeconds = String.format("%02d", intSeconds) + "s";
	    			
	    			if (intMilliseconds > 0) {
		    			strMilliseconds = String.format("%03d", intMilliseconds) + "ms";
		    		}
		    	}
	    		
	    		strTimeTransformed = strMinutes + strSeconds + strMilliseconds;
	    		
	    	// Segundos
	    	} else if (intSeconds > 0) {
	    		strSeconds = String.format("%02d", intSeconds) + "s";
	    		
	    		if (intMilliseconds > 0) {
	    			strMilliseconds = String.format("%03d", intMilliseconds) + "ms";
	    		}
	    		
	    		strTimeTransformed = strSeconds + strMilliseconds;
	    	
	    	// Milisegundos
	    	} else if (intMilliseconds > 0) {
	    		strMilliseconds = String.format("%03d", intMilliseconds) + "ms";
	    		
	    		strTimeTransformed = strMilliseconds;
	    	}
		}
		
		return strTimeTransformed;
	}
}