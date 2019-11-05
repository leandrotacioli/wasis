package br.unicamp.fnjv.wasis.audio;

/**
 * Modelo para listagens de imagens dos arquivos de áudio.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 25/Set/2017
 */
public class AudioImagesValues {
	private String strSpectrogramColorDisplay;
	private String strSpectrogramImagePath;
	private String strSpectrogramImageHash;
	private int intFFTSamples;
	private int intFFTOverlap;
	private String strFFTWindow;
	private int intInitialTime;
	private int intFinalTime;
	private boolean blnCompleteImage;
	
	public String getSpectrogramColorDisplay() {
		return strSpectrogramColorDisplay;
	}
	
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
	 * Modelo para listagens de imagens dos arquivos de áudio.
	 * 
	 * @param strSpectrogramColorDisplay - Mapa de cores do espectrograma
	 * @param strSpectrogramImagePath    - Caminho da imagem do espectrograma
	 * @param strSpectrogramImageHash    - Hash da imagem do espectrograma
	 * @param intFFTSamples              - Número de amostras da FFT
	 * @param intFFTOverlap              - Fator de sobreposição (OVERLAP) da FFT
	 * @param strFFTWindow               - Função de janelamento da FFT
	 * @param intInitialTime             - Tempo inicial da imagem do espectrograma
	 * @param intFinalTime               - Tempo final da imagem do espectrograma
	 * @param blnCompleteImage           - Determina se é a imagem completa do espectrograma
	 */
	public AudioImagesValues(String strSpectrogramColorDisplay, String strSpectrogramImagePath, String strSpectrogramImageHash, int intFFTSamples, int intFFTOverlap, String strFFTWindow, int intInitialTime, int intFinalTime, boolean blnCompleteImage) {
		this.strSpectrogramColorDisplay = strSpectrogramColorDisplay;
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