package br.unicamp.fnjv.wasis.graphics.spectrogram;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTParameters;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

/**
 * Classe que implementa <i>Runnable</i> responsável
 * por transformar o áudio do domínio do tempo para o
 * domínio da frequência (FFT) e enviar os dados de intensidades
 * para a classe <i>SpectrogramRenderer</i>.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 14/Fev/2017
 */
public class SpectrogramRendererThread implements Runnable {
	private SpectrogramRenderer objSpectrogramRenderer;

	private AudioWav objAudioWav;
	
	private int intChannel;
	
	private int intInitialChunk;
	private int intFinalChunk;
	
	/**
	 * Classe que implementa <i>Runnable</i> responsável
	 * por transformar o áudio do domínio do tempo para o
	 * domínio da frequência (FFT) e enviar os dados de intensidades
	 * para a classe <i>SpectrogramRenderer</i>.
	 * 
	 * @param objSpectrogramRenderer
	 * @param intInitialChunk
	 * @param intFinalChunk
	 * 
	 * @throws CloneNotSupportedException 
	 */
	protected SpectrogramRendererThread(SpectrogramRenderer objSpectrogramRenderer, int intInitialChunk, int intFinalChunk) throws CloneNotSupportedException {
		this.objSpectrogramRenderer = objSpectrogramRenderer;
		this.intInitialChunk = intInitialChunk;
		this.intFinalChunk = intFinalChunk;
		
		this.objAudioWav = (AudioWav) objSpectrogramRenderer.getSpectrogram().getAudioWav().clone();
		
		this.intChannel = 1;
	}
	
	@Override
	public void run() {
		// ***********************************************************************************************
		// Retorna as amplitudes do pedaço do áudio
		double[] amplitudes = objAudioWav.getAmplitudesChunk(intChannel, intInitialChunk, intFinalChunk);
    	
		// ***********************************************************************************************
        // Transforma do domínio de tempo para o domínio da frequência - Utilizando FFT
		FFT objFFT = new FFT(FFTParameters.getInstance().getFFTSampleSize(), FFTParameters.getInstance().getFFTWindowFunction());
		
		// Verifica se o tipo de renderização do espectrograma for 'RENDER_COMPARISON'
		if (objSpectrogramRenderer.getRenderType() == objSpectrogramRenderer.getSpectrogram().RENDER_COMPARISON) {
			objFFT = new FFT(FFTParameters.FFT_SAMPLE_SIZE_COMPARISON, FFTParameters.FFT_WINDOW_FUNCTION_COMPARISON);
		}
		
		double[] windowing = objFFT.applyWindow(amplitudes);
    	
		objFFT.executeFFT(windowing);
    	
    	double[] frequencies = objFFT.getAmplitudes();
    	
    	// ***********************************************************************************************
    	// Extrai as intensidades
    	double[] intensities = new double[frequencies.length];
    	
    	for (int indexFrequency = 0; indexFrequency < frequencies.length; indexFrequency++) {
    		double dblDBFS = frequencies[indexFrequency];
    		
        	// Atualiza a intensidade mínima
    		if (dblDBFS < objSpectrogramRenderer.getMinimumIntensity()) {
        		objSpectrogramRenderer.setMinimumIntensity(dblDBFS);
            }
    		
    		// Atualiza a intensidade máxima
        	if (dblDBFS > objSpectrogramRenderer.getMaximumIntensity()) {
        		objSpectrogramRenderer.setMaximumIntensity(dblDBFS);
            }
        	
        	intensities[indexFrequency] = dblDBFS;
        	
        	// ***********************************************************************************************
        	// Dados para comparação
        	if (objSpectrogramRenderer.getRenderType() == objSpectrogramRenderer.getSpectrogram().RENDER_COMPARISON) {
	        	int intFrequencySamples = FFTParameters.FFT_SAMPLE_SIZE_COMPARISON / 2;
	        	double dblFrequency = (double) objSpectrogramRenderer.getMaximumFrequency() / (double) intFrequencySamples * indexFrequency;
	    		dblFrequency += (double) objSpectrogramRenderer.getMaximumFrequency() / (double) intFrequencySamples;
	    		
	    		int intFrequency = (int) dblFrequency;
	    		
        		for (int indexFrequencyDecibelValues = 0; indexFrequencyDecibelValues < objSpectrogramRenderer.getAudioComparisonValues().size(); indexFrequencyDecibelValues++) {
        			if (intFrequency == objSpectrogramRenderer.getAudioComparisonValues().get(indexFrequencyDecibelValues).getFrequency()) {
        				if (dblDBFS > objSpectrogramRenderer.getAudioComparisonValues().get(indexFrequencyDecibelValues).getDecibel()) {
        					objSpectrogramRenderer.getAudioComparisonValues().get(indexFrequencyDecibelValues).setDecibel(dblDBFS);
        					
        					break;
        				}
        			}
        		}
        	}
        }

		objSpectrogramRenderer.endThread(intInitialChunk, intensities);
	}
}