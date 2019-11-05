package br.unicamp.fnjv.wasis.dsp;

/**
 * Transforma amostra do domínio de tempo para o domínio de frequência.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 27/Set/2017
 */
public class FFT {
    private FFTWindowFunction objFFTWindowFunction;
    private FFTColumbia objFFTColumbia;
    
    /**
     * Transforma amostra do domínio de tempo para o domínio de frequência.
     * 
     * @param intFFTSampleSize  - Número de amostras da FFT (Potência de 2) 
     * @param strWindowFunction - Função de janelamento
     */
    public FFT(int intFFTSampleSize, String strWindowFunction) {
    	this.objFFTWindowFunction = new FFTWindowFunction(strWindowFunction);
    	this.objFFTColumbia = new FFTColumbia(intFFTSampleSize);
    }
    
    /**
     * Aplica a função de janelamento.
     * 
     * @param data - Amostra no domínio de tempo
     * 
     * @return windowing
     */
    public double[] applyWindow(double[] data) {
    	double[] windowing = objFFTWindowFunction.applyWindow(data);
    	
    	return windowing;
    }

	/**
     * Executa a FFT, transformando amostra no domínio de tempo para o domínio de frequência.<br>
     * <br>
     * <b>IMPORTANTE:</b> Desejável executar a função de janelamento <i>applyWindow()</i> antes
     * desta operação.
     * 
     * @param timeData - Amostra no domínio de tempo
     */
    public void executeFFT(double[] timeData) {
    	objFFTColumbia.fft(timeData);
    }
    
    /**
     * Retorna a parte real.<br>
	 * <br>
	 * <b>IMPORTANTE:</b> Os primeiros valores correspondem às frequências mais baixas,
	 * enquanto os últimos valores correspondem às frequências mais altas.
     * 
     * @return real
     */
    public double[] getReal() {
    	return objFFTColumbia.getReal();	
    }
    
    /**
     * Retorna a parte imaginária.<br>
	 * <br>
	 * <b>IMPORTANTE:</b> Os primeiros valores correspondem às frequências mais baixas,
	 * enquanto os últimos valores correspondem às frequências mais altas.
     * 
     * @return imag
     */
    public double[] getImag() {
    	return objFFTColumbia.getImag();	
    }
    
    /**
     * Retorna as amplitudes em dBFS.<br>
	 * <br>
	 * <b>IMPORTANTE:</b> Os primeiros valores são correspondentes às frequências mais baixas,
	 * enquanto os últimos valores são correspondentes às frequências mais altas.
     * 
     * @return amplitude
     */
    public double[] getAmplitudes() {
    	return objFFTColumbia.getAmplitudes();
    }
}