package br.unicamp.fnjv.wasis.multimidia;

import java.io.File;

/**
 * Biblioteca Global 
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 30/Mar/2015
 */
public class AudioFile {
	
	/**
	 * Verifica se existe o arquivo de áudio da biblioteca.
	 * 
	 * @param strAudioFilePath
	 * 
	 * @return blnExistingFile
	 */
	public static boolean checkExistingFile(String strAudioFilePath) {
		boolean blnExistingFile = false;
		
		File file = new File(strAudioFilePath);
		
		if (file.exists() && !file.isDirectory()) {
			blnExistingFile = true;
		}
		
		return blnExistingFile;
	}
}
