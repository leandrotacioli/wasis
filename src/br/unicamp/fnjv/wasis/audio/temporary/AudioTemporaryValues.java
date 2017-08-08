package br.unicamp.fnjv.wasis.audio.temporary;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseConnection;

/**
 * Modelo para listagens de arquivos de áudio temporário.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 08/Mai/2017
 */
public class AudioTemporaryValues {
	private String strAudioFilePathOriginal;
	private String strAudioFileHashOriginal;
	
	private String strAudioFilePathTemporary;
	private String strAudioFileHashTemporary;
	
	private List<AudioTemporaryImages> lstAudioTemporaryImages;
	private List<AudioTemporarySegments> lstAudioTemporarySegments;
	
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
	 * Retorna a lista de imagens do arquivo de áudio temporário.
	 * 
	 * @return lstAudioTemporaryImages
	 */
	public List<AudioTemporaryImages> getAudioTemporaryImages() {
		return lstAudioTemporaryImages;
	}

	/**
	 * Retorna a lista de segmentos selecionados (ROIs) do arquivo de áudio temporário.<br>
	 * <br>
	 * Esta lista incluirá tanto os segmentos já armazenadas no banco de dados, 
	 * quanto os segmentos temporários.
	 * 
	 * @return lstAudioTemporarySegments
	 */
	public List<AudioTemporarySegments> getAudioTemporarySegments() {
		return lstAudioTemporarySegments;
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
		
		this.lstAudioTemporaryImages = new ArrayList<AudioTemporaryImages>();
		this.lstAudioTemporarySegments = new ArrayList<AudioTemporarySegments>();
		
		getAudioTemporarySegmentsFromDatabase();
	}
	
	/**
	 * Retorna a lista de segmentos (ROIS) do banco de dados relacionada ao arquivo de áudio temporário.
	 * 
	 * @return lstDatabaseSegments
	 */
	private List<AudioTemporarySegments> getAudioTemporarySegmentsFromDatabase() {
		List<AudioTemporarySegments> lstDatabaseSegments = new ArrayList<AudioTemporarySegments>();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, aud.animal_genus, aud.animal_species, seg.sound_unit, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files             aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments    seg   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("WHERE aud.audio_file_path = ? ");
			objDatabaseConnection.sqlCommandAppend("AND aud.audio_file_hash = ? ");
			objDatabaseConnection.sqlCommandAppend("AND seg.ind_active = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY sound_unit ");
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePathOriginal);
			objDatabaseConnection.addParameter("audio_file_hash", LTDataTypes.STRING, strAudioFileHashTemporary);
			objDatabaseConnection.addParameter("ind_active", LTDataTypes.BOOLEAN, true);
			
			ResultSet rsSegments = objDatabaseConnection.executeSelectQuery();
			
			while (rsSegments.next()) {
				lstDatabaseSegments.add(new AudioTemporarySegments(rsSegments.getInt("id_segment"), 
																   rsSegments.getString("sound_unit"), 
																   rsSegments.getInt("time_initial"), 
																   rsSegments.getInt("time_final"), 
																   rsSegments.getInt("frequency_initial"), 
																   rsSegments.getInt("frequency_final"),
																   rsSegments.getString("animal_genus"),
																   rsSegments.getString("animal_species")));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		this.lstAudioTemporarySegments = new ArrayList<AudioTemporarySegments>();
		this.lstAudioTemporarySegments = lstDatabaseSegments;
		
		return lstDatabaseSegments;
	}
	
	/**
	 * Limpa a lista de imagens temporárias.
	 */
	public void clearAudioTemporaryImages() {
		this.lstAudioTemporaryImages = new ArrayList<AudioTemporaryImages>();
	}
	
	/**
	 * Limpa a lista de segmentos temporáris.
	 */
	public void clearAudioTemporarySegments() {
		this.lstAudioTemporarySegments = new ArrayList<AudioTemporarySegments>();
		
		getAudioTemporarySegmentsFromDatabase();
	}
	
	/**
	 * Exclui um segmento (ROI) da lista de seleções temporárias.
	 * 
	 * @param strSoundUnit - Unidade de som
	 */
	public void deleteAudioTemporarySelection(String strSoundUnit) {
		for (int indexSegment = 0; indexSegment < lstAudioTemporarySegments.size(); indexSegment++) {
			if (strSoundUnit.equals(lstAudioTemporarySegments.get(indexSegment).getSoundUnit())) {
				lstAudioTemporarySegments.remove(indexSegment);
				
				break;
			}
		}
	}
}