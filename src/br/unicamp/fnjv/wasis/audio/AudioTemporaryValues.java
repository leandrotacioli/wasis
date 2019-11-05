package br.unicamp.fnjv.wasis.audio;

import java.util.ArrayList;
import java.util.List;

/**
 * Modelo para listagens de arquivos de áudio temporário.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 31/Mar/2018
 */
public class AudioTemporaryValues {
	private String strAudioFilePathOriginal;
	private String strAudioFileHashOriginal;
	
	private String strAudioFilePathTemporary;
	private String strAudioFileHashTemporary;
	
	private List<AudioImagesValues> lstAudioImages;
	private List<AudioSegmentsValues> lstAudioSegments;
	
	/**
	 * Retorna o caminho do arquivo de áudio original.
	 * 
	 * @return strAudioFilePathOriginal
	 */
	public String getAudioFilePathOriginal() {
		return strAudioFilePathOriginal;
	}
	
	/**
	 * Retorna o hash do arquivo de áudio original.
	 * 
	 * @return strAudioFileHashOriginal
	 */
	public String getAudioFileHashOriginal() {
		return strAudioFileHashOriginal;
	}
	
	/**
	 * Retorna o hash do arquivo de áudio original.
	 * 
	 * @param strAudioFileHashOriginal
	 */
	public void setAudioFileHashOriginal(String strAudioFileHashOriginal) {
		this.strAudioFileHashOriginal = strAudioFileHashOriginal;
	}
	
	/**
	 * Retorna o caminho do arquivo de áudio temporário.
	 * 
	 * @return strAudioFilePathTemporary
	 */
	public String getAudioFilePathTemporary() {
		return strAudioFilePathTemporary;
	}
	
	/**
	 * Altera o caminho do arquivo de áudio temporário.
	 * 
	 * @param strAudioFilePathTemporary
	 */
	public void setAudioFilePathTemporary(String strAudioFilePathTemporary) {
		this.strAudioFilePathTemporary = strAudioFilePathTemporary;
	}
	
	/**
	 * Retorna o hash do arquivo de áudio temporário.
	 * 
	 * @return strAudioFileHashTemporary
	 */
	public String getAudioFileHashTemporary() {
		return strAudioFileHashTemporary;
	}
	
	/**
	 * Altera o o hash do arquivo de áudio temporário.
	 * 
	 * @param strAudioFileHashTemporary
	 */
	public void setAudioFileHashTemporary(String strAudioFileHashTemporary) {
		this.strAudioFileHashTemporary = strAudioFileHashTemporary;
	}
	
	/**
	 * Retorna a lista de imagens do arquivo de áudio.
	 * 
	 * @return lstAudioImages
	 */
	public List<AudioImagesValues> getAudioImages() {
		return lstAudioImages;
	}

	/**
	 * Retorna a lista de segmentos selecionados (ROIs) do arquivo de áudio.<br>
	 * <br>
	 * Esta lista incluirá tanto os segmentos já armazenadas no banco de dados, 
	 * quanto os segmentos temporários.
	 * 
	 * @return lstAudioSegments
	 */
	public List<AudioSegmentsValues> getAudioSegments() {
		return lstAudioSegments;
	}
	
	/**
	 * Modelo para listagens de arquivos de áudio temporário.
	 * 
	 * @param strAudioFilePathOriginal  - Caminho do arquivo de áudio original
	 * @param strAudioFileHashOriginal  - Hash do arquivo de áudio original
	 * @param strAudioFilePathTemporary - Caminho do arquivo de áudio temporário
	 * @param strAudioFileHashTemporary - Hash do arquivo de áudio temporário
	 */
	public AudioTemporaryValues(String strAudioFilePathOriginal, String strAudioFileHashOriginal, String strAudioFilePathTemporary, String strAudioFileHashTemporary) {
		this.strAudioFilePathOriginal = strAudioFilePathOriginal;
		this.strAudioFileHashOriginal = strAudioFileHashOriginal;
		this.strAudioFilePathTemporary = strAudioFilePathTemporary;
		this.strAudioFileHashTemporary = strAudioFileHashTemporary;
		
		this.lstAudioImages = new ArrayList<AudioImagesValues>();
		this.lstAudioSegments = AudioSegments.getAudioSegmentsFromDatabase(strAudioFilePathOriginal, strAudioFileHashOriginal);
	}
	
	/**
	 * Limpa a lista de imagens temporárias.
	 */
	public void clearAudioImages() {
		this.lstAudioImages = new ArrayList<AudioImagesValues>();
	}
	
	/**
	 * Limpa a lista de segmentos de áudio.
	 */
	public void clearAudioSegments() {
		this.lstAudioSegments = AudioSegments.getAudioSegmentsFromDatabase(strAudioFilePathOriginal, strAudioFileHashOriginal);
	}
	
	/**
	 * Exclui um segmento (ROI) da lista de segmentos de áudio.
	 * 
	 * @param strAudioSegment - Segmento de áudio
	 */
	public void deleteAudioSegment(String strAudioSegment) {
		for (int indexAudioSegment = 0; indexAudioSegment < lstAudioSegments.size(); indexAudioSegment++) {
			if (strAudioSegment.equals(lstAudioSegments.get(indexAudioSegment).getAudioSegment())) {
				lstAudioSegments.remove(indexAudioSegment);
				
				break;
			}
		}
	}
}