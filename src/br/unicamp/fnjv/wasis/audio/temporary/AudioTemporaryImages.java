package br.unicamp.fnjv.wasis.audio.temporary;

/**
 * Modelo para listagens de imagens dos arquivos de áudio temporários.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 24/Fev/2017
 */
public class AudioTemporaryImages {
	private String strSpectrogramImagePath;
	private String strSpectrogramImageHash;
	private int intFFTSamples;
	private int intFFTOverlap;
	private String strFFTWindow;
	private int intInitialTime;
	private int intFinalTime;
	private boolean blnCompleteImage;
	
	public String getSpectrogramImagePath() {
		return strSpectrogramImagePath;
	}
	
	public void setSpectrogramImagePath(String strSpectrogramImagePath) {
		this.strSpectrogramImagePath = strSpectrogramImagePath;
	}
	
	public String getSpectrogramImageHash() {
		return strSpectrogramImageHash;
	}
	
	public void setSpectrogramImageHash(String strSpectrogramImageHash) {
		this.strSpectrogramImageHash = strSpectrogramImageHash;
	}
	
	public int getFFTSamples() {
		return intFFTSamples;
	}
	
	public void setFFTSamples(int intFFTSamples) {
		this.intFFTSamples = intFFTSamples;
	}
	
	public int getFFTOverlap() {
		return intFFTOverlap;
	}
	
	public void setFFTOverlap(int intFFTOverlap) {
		this.intFFTOverlap = intFFTOverlap;
	}
	
	public String getFFTWindow() {
		return strFFTWindow;
	}
	
	public void setFFTWindow(String strFFTWindow) {
		this.strFFTWindow = strFFTWindow;
	}
	
	public int getInitialTime() {
		return intInitialTime;
	}
	
	public void setInitialTime(int intInitialTime) {
		this.intInitialTime = intInitialTime;
	}
	
	public int getFinalTime() {
		return intFinalTime;
	}
	
	public void setFinalTime(int intFinalTime) {
		this.intFinalTime = intFinalTime;
	}
	
	public boolean getCompleteImage() {
		return blnCompleteImage;
	}
	
	public void setCompleteImage(boolean blnCompleteImage) {
		this.blnCompleteImage = blnCompleteImage;
	}
	
	/**
	 * Modelo para listagens de imagens dos arquivos de áudio temporários.
	 * 
	 * @param strSpectrogramImagePath - Caminho da imagem do espectrograma
	 * @param strSpectrogramImageHash - Hash da imagem do espectrograma
	 * @param intFFTSamples           - Número de amostras da FFT
	 * @param intFFTOverlap           - Fator de sobreposição (OVERLAP) da FFT
	 * @param strFFTWindow            - Função de janelamento da FFT
	 * @param intInitialTime          - Tempo inicial da imagem do espectrograma
	 * @param intFinalTime            - Tempo final da imagem do espectrograma
	 * @param blnCompleteImage        - Determina se é a imagem completa do espectrograma
	 */
	public AudioTemporaryImages(String strSpectrogramImagePath, String strSpectrogramImageHash, int intFFTSamples, int intFFTOverlap, String strFFTWindow, int intInitialTime, int intFinalTime, boolean blnCompleteImage) {
		this.strSpectrogramImagePath = strSpectrogramImagePath;
		this.strSpectrogramImageHash = strSpectrogramImageHash;
		this.intFFTSamples = intFFTSamples;
		this.intFFTOverlap = intFFTOverlap;
		this.strFFTWindow = strFFTWindow;
		this.intInitialTime = intInitialTime;
		this.intFinalTime = intFinalTime;
		this.blnCompleteImage = blnCompleteImage;
	}
}