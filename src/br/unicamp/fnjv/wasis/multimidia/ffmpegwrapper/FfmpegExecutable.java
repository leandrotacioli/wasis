package br.unicamp.fnjv.wasis.multimidia.ffmpegwrapper;

/**
 * Cria o caminho do FFMPEG pré-compilado, verificando o sistema operacional utilizado.
 * 
 * @author Leandro Tacioli
 * @version 1.1 - 12/Jan/2020
 */
public class FfmpegExecutable {
	private final String EXECUTABLE_PATH = "res/";
	private String strFfmpegPath;
	
	/**
	 * Retorna o caminho do executável do FFMPEG.
	 * 
	 * @return strFfmpegPath
	 */
	protected String getFfmpegExecutablePath() {
		return strFfmpegPath;
	}

	/**
	 * Cria o caminho do FFMPEG pré-compilado, verificando o sistema operacional utilizado.
	 */
	protected FfmpegExecutable() {
		String strOS = System.getProperty("os.name").toLowerCase();
		
		String strSuffix = "";
		
		if (strOS.contains("windows")) {
			strSuffix = "ffmpeg-windows.exe";
		} else if (strOS.contains("nux")) {
			strSuffix = "ffmpeg-linux";
		} else if (strOS.contains("mac")) {
			strSuffix = "ffmpeg-mac";
		}
		
		this.strFfmpegPath = EXECUTABLE_PATH + strSuffix;
	}
	
	/**
	 * Retorna uma nova instância do 'FfmpegExecutable', pronto para ser utilizada na chamada do FFMPEG.
	 */
	protected FfmpegWrapper createWrapper() {
		return new FfmpegWrapper(getFfmpegExecutablePath());
	}
}