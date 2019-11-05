package br.unicamp.fnjv.wasis.dsp;

/*
 *  Copyright 2006-2007 Columbia University.
 *
 *  This file is part of MEAPsoft.
 *
 *  MEAPsoft is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 2 as
 *  published by the Free Software Foundation.
 *
 *  MEAPsoft is distributed in the hope that it will be useful, but
 *  WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with MEAPsoft; if not, write to the Free Software
 *  Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 *  02110-1301 USA
 *
 *  See the file "COPYING" for the text of the license.
 */

/**
 * Utility class to perform a Fast Fourier Transform without allocating any extra memory.
 *
 * @author Mike Mandel (mim@ee.columbia.edu)
 */
public class FFTColumbia {
	private int n, m;
  
	// Lookup tables - Only need to recompute when size of FFT changes.
	private double[] cos;
	private double[] sin;
	
	/** Real Part. */
	private double[] real;
	
	/** Imaginary Part. */
	private double[] imag;
	
	/** Amplitude - dBFS */
	private double[] amplitudes;
	
	/**
	 * Returns the Real Part.<br>
	 * <br>
	 * <b>IMPORTANT:</b> The first values correspond to lower frequencies,
	 * while the last values correspond to higher frequencies.
	 * 
	 * @return real
	 */
	public double[] getReal() {
		return real;
	}
	
	/**
	 * Returns the Imaginary Part.<br>
	 * <br>
	 * <b>IMPORTANT:</b> The first values correspond to lower frequencies,
	 * while the last values correspond to higher frequencies.
	 * 
	 * @return imag
	 */
	public double[] getImag() {
		return imag;
	}
	
	/**
	 * Returns the Amplitudes in dBFS.<br>
	 * <br>
	 * <b>IMPORTANT:</b> The first values correspond to lower frequencies,
	 * while the last values correspond to higher frequencies.
	 * 
	 * @return amplitudes
	 */
	public double[] getAmplitudes() {
		return amplitudes;
	}

	/**
	 * 
	 * @param n
	 */
	public FFTColumbia(int n) {
		this.n = n;
		this.m = (int) (Math.log(n) / Math.log(2));

		// Make sure n is a power of 2
		if (n != (1 << m)) {
			throw new RuntimeException("FFT length must be power of 2");
		}
		
	    // Precompute tables
	    cos = new double[n / 2];
	    sin = new double[n / 2];
	    
	    for (int i = 0; i < n / 2; i++) {
	    	cos[i] = Math.cos(-2 * Math.PI * i / n);
	    	sin[i] = Math.sin(-2 * Math.PI * i / n);
	    }
	}

	/***************************************************************
	 * fft.c
	 * Douglas L. Jones 
	 * University of Illinois at Urbana-Champaign 
	 * January 19, 1992 
	 * http://cnx.rice.edu/content/m12016/latest/
	 * 
	 *   FFT: in-place radix-2 DIT DFT of a complex input 
	 * 
	 *   input: 
	 * n: length of FFT: must be a power of two 
	 * m: n = 2**m 
	 *   input/output 
	 * x: double array of length n with real part of data 
	 * y: double array of length n with imag part of data 
	 * 
	 *   Permission to copy and use this program is granted 
	 *   as long as this header is included. 
	 ****************************************************************/
	public void fft(double[] x) {
		fft(x, null);
	}
	
	public void fft(double[] x, double[] y) {
		int i, j, k, n1, n2, a;
		double c, s, t1, t2;
		
		/*
         * Create complex input sequence equivalent to the real
         * input sequence.
         * If the number of points is less than the window size,
         * we incur in aliasing. If it's greater, we pad the input
         * sequence with zeros.
         */
		double[] xNew = new double[n];
		double[] yNew = new double[n];
		
		if (n < x.length) {
            int index = 0;
            
            for (; index < n; index++) {
            	xNew[index] = x[index];
            	yNew[index] = 0;
            }
            
            for (; index < x.length; index++) {
            	xNew[index % n] = x[index % n] + x[index];
            }
            
        } else {
            int index = 0;
            
            for (; index < x.length; index++) {
            	xNew[index] = x[index];
            	yNew[index] = 0;
            }
            
            for (; index < n; index++) {
            	xNew[index] = 0;
            	yNew[index] = 0;
            }
        }
		
		x = xNew;
		y = yNew;
  
	    // Bit-reverse
	    j = 0;
	    n2 = n / 2;
	    
	    for (i = 1; i < n - 1; i++) {
	    	n1 = n2;
	      
	    	while (j >= n1) {
	    		j = j - n1;
	    		n1 = n1 / 2;
	    	}
	    	
	    	j = j + n1;
	    
	    	if (i < j) {
	    		t1 = x[i];
				x[i] = x[j];
				x[j] = t1;
				t1 = y[i];
				y[i] = y[j];
				y[j] = t1;
	    	}
	    }
	
	    // FFT
	    n1 = 0;
	    n2 = 1;
	  
	    for (i = 0; i < m; i++) {
	    	n1 = n2;
	    	n2 = n2 + n2;
	    	a = 0;
	    
	    	for (j = 0; j < n1; j++) {
	    		c = cos[a];
	    		s = sin[a];
	    		a +=  1 << (m - i - 1);
	
	    		for (k = j; k < n; k = k + n2) {
	    			t1 = c * x[k + n1] - s * y[k + n1];
	    			t2 = s * x[k + n1] + c * y[k + n1];
	    			x[k + n1] = x[k] - t1;
	    			y[k + n1] = y[k] - t2;
	    			x[k] = x[k] + t1;
	    			y[k] = y[k] + t2;
	    		}
	    	}
	    }
	    
	    real = x;   // Real Part
	    imag = y;   // Imaginary Part
	    
	    // Amplitudes - dBFS
	    amplitudes = new double[n / 2];
        
        for (int index = 0; index < n / 2; index++) {
        	double dblSquaredMagnitude = (x[index] * x[index] + y[index] * y[index]) / n;
        	
        	double dblAmplitudeMagnitude = 10 * Math.log10(dblSquaredMagnitude);
        	dblAmplitudeMagnitude = dblAmplitudeMagnitude - 96.00d;      // 96dB range for 16 bits audio format
            
            amplitudes[index] = dblAmplitudeMagnitude;
        }
	}
}