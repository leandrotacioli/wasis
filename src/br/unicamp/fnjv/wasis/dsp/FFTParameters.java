package br.unicamp.fnjv.wasis.dsp;

/**
 * Parâmetros do FFT.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 02/Mar/2015
 */
public class FFTParameters {
	private static FFTParameters objFFTParameters;
	
	private int intFFTSampleSize;        // Número de amostras da FFT (Potência de 2)
	private int intFFTOverlapFactor;     // Fator de sobreposição (OVERLAP) - Valor em porcentagem (0% a 99%)
	private String strFFTWindowFuntion;  // Função de janelamento
	
	/**
	 * Valores de amostras da FFT que poderão ser utilizadas no sistema.<br>
	 * <br>
	 * A 1º linha representa os valores das amostras da FFT. <br>
	 * <br>
	 * A 2º linha representa a quantidade de pixels por segundo 
	 * do respectivo valor da amostra na geração do espectrograma.
	 */
	public static final double[][] FFT_SAMPLES = new double[][] { {64,  128, 256, 512, 1024, 2048, 4096, 8192, 16384},    // Número de amostras da FFT
															      {800, 400, 200, 100, 50,   25,   12.5, 6.25, 3.125} };  // Quantidade de pixels por segundo do respectivo valor da amostra na geração do espectrograma
	
	
	/**
	 * Retorna o valor em milisegundos que cada pixel gerado no espectrograma terá
	 * de acordo com o valor designado na matriz de valores de amostras da FFT.
	 * 
	 * @param intFFTSampleSize
	 * 
	 * @return dblMillisecondsPerPixel
	 */
	public double getMillisecondsPerPixel(int intFFTSampleSize) {
		double dblMillisecondsPerPixel = 0;
		
		for (int indexFFTSamples = 0; indexFFTSamples < FFT_SAMPLES[0].length; indexFFTSamples++) {
			if (intFFTSampleSize == FFT_SAMPLES[0][indexFFTSamples]) {
				dblMillisecondsPerPixel = (double) 1000 / FFT_SAMPLES[1][indexFFTSamples];
				break;
			}
		}
		
		return dblMillisecondsPerPixel;
	}
	
	/**
	 * Número de amostras da FFT que será utilizada para
	 * análise de comparação de dados e inclusão no banco de dados.
	 */
	public static final int FFT_SAMPLE_SIZE_COMPARISON = 1024;
	
	/**
	 * Fator de sobreposição (OVERLAP) que será utilizado para
	 * análise de comparação de dados e inclusão no banco de dados.
	 */
	public static final int FFT_OVERLAP_FACTOR_COMPARISON = 50;
	
	/**
	 * Função de janelamento (HANNING) que será utilizada para
	 * análise de comparação de dados e inclusão no banco de dados.
	 */
	public static final String FFT_WINDOW_FUNCTION_COMPARISON = "HANNING"; 
	
	/**
	 * Retorna o número de amostras da FFT (Potência de 2). 
	 * 
	 * @return intFFTSampleSize
	 */
	public int getFFTSampleSize() {
		return intFFTSampleSize;
	}
	
	/**
	 * Altera o número de amostras da FFT (Potência de 2). 
	 * 
	 * @param intFFTSampleSize
	 */
	public void setFFTSampleSize(int intFFTSampleSize) {
		this.intFFTSampleSize = intFFTSampleSize;
	}
	
	/**
	 * Retorna o fator de sobreposição (OVERLAP) - Valor em porcentagem (0% a 99%).
	 * 
	 * @return intFFTOverlapFactor
	 */
	public int getFFTOverlapFactor() {
		return intFFTOverlapFactor;
	}
	
	/**
	 * Altera o fator de sobreposição (OVERLAP) - Valor em porcentagem (0% a 99%).
	 * 
	 * @param intFFTOverlapFactor
	 */
	public void setFFTOverlapFactor(int intFFTOverlapFactor) {
		this.intFFTOverlapFactor = intFFTOverlapFactor;
	}
	
	/**
	 * Retorna a função de janelamento.
	 * 
	 * @return strFFTWindowFuntion
	 */
	public String getFFTWindowFunction() {
		return strFFTWindowFuntion;
	}
	
	/**
	 * Altera a função de janelamento.
	 * 
	 * @param strFFTWindowFuntion
	 */
	public void setFFTWindowFunction(String strFFTWindowFuntion) {
		this.strFFTWindowFuntion = strFFTWindowFuntion.toUpperCase();
	}
	
	/**
	 * Cria uma nova instância para a classe de parâmetros do FFT, 
	 * ou retorna a instância já criada anteriormente.
	 * 
	 * @return objFFTParameters
	 */
	public static synchronized FFTParameters getInstance() {
		if (objFFTParameters == null) {
			objFFTParameters = new FFTParameters();
		}
		
		return objFFTParameters;
	}
	
	/**
	 * Parâmetros do FFT.
	 */
	private FFTParameters() {
		setFFTSampleSize(1024);
		setFFTOverlapFactor(50);
		setFFTWindowFunction("HANNING");
	}
}