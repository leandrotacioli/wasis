package br.unicamp.fnjv.wasis.graphics.spectrogram;

import br.unicamp.fnjv.wasis.dsp.FFT;
import br.unicamp.fnjv.wasis.dsp.FFTParameters;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

/**
 * Classe que implementa <i>Runnable</i> responsável por transformar o áudio do domínio do tempo para o
 * domínio da frequência (FFT) e enviar os dados de intensidades para a classe <i>SpectrogramRenderer</i>.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 19/Set/2017
 */
public class SpectrogramRendererThread implements Runnable {
	private SpectrogramRenderer objSpectrogramRenderer;

	private AudioWav objAudioWav;
	
	private int intInitialChunk;
	private int intFinalChunk;
	
	/**
	 * Classe que implementa <i>Runnable</i> responsável por transformar o áudio do domínio do tempo para o
	 * domínio da frequência (FFT) e enviar os dados de intensidades para a classe <i>SpectrogramRenderer</i>.
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
	}
	
	@Override
	public void run() {
		// ***********************************************************************************************
		// Retorna as amplitudes do pedaço do áudio
		double[] amplitudes = objAudioWav.getAmplitudesChunk(intInitialChunk, intFinalChunk);
    	
		// ***********************************************************************************************
        // Transforma do domínio de tempo para o domínio da frequência - Utilizando FFT
		FFT objFFT = new FFT(FFTParameters.getInstance().getFFTSampleSize(), FFTParameters.getInstance().getFFTWindowFunction());

		double[] windowing = objFFT.applyWindow(amplitudes);
    	
		objFFT.executeFFT(windowing);
    	
    	double[] frequencies = objFFT.getAmplitudes();
    	
    	// ***********************************************************************************************
    	// Extrai as intensidades
    	double[] intensities = new double[frequencies.length];
    	
    	for (int indexFrequency = 0; indexFrequency < frequencies.length; indexFrequency++) {
        	intensities[indexFrequency] = frequencies[indexFrequency];
        }

		objSpectrogramRenderer.endThread(intInitialChunk, intensities);
	}
}