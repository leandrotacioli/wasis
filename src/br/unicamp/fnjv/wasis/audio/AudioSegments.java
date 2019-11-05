package br.unicamp.fnjv.wasis.audio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Classe responsável pelo gerenciamento de segmentos de áudio.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 22/Set/2017
 */
public class AudioSegments {

	/**
	 * Classe responsável pelo gerenciamento de segmentos de áudio.
	 */
	private AudioSegments() {
		
	}
	
	/**
	 * Retorna a lista de segmentos (ROIs) do banco de dados relacionada ao arquivo de áudio.
	 * 
	 * @param strAudioFilePath
	 * @param strAudioFileHash
	 * 
	 * @return lstAudioSegments
	 */
	public static List<AudioSegmentsValues> getAudioSegmentsFromDatabase(String strAudioFilePath, String strAudioFileHash) {
		List<AudioSegmentsValues> lstAudioSegments = new ArrayList<AudioSegmentsValues>();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT seg.id_audio_segment, tax.animal_genus, tax.animal_species, seg.audio_segment, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files             aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments    seg   ON   seg.fk_audio_file      = aud.id_audio_file ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN animal_taxonomies       tax   ON   seg.fk_animal_taxonomy = tax.id_animal_taxonomy ");
			objDatabaseConnection.sqlCommandAppend("WHERE aud.audio_file_path = ? ");
			objDatabaseConnection.sqlCommandAppend("AND aud.audio_file_hash = ? ");
			objDatabaseConnection.sqlCommandAppend("AND seg.ind_active = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id_audio_segment ");
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePath);
			objDatabaseConnection.addParameter("audio_file_hash", LTDataTypes.STRING, strAudioFileHash);
			objDatabaseConnection.addParameter("ind_active", LTDataTypes.BOOLEAN, true);
			
			ResultSet rsSegments = objDatabaseConnection.executeSelectQuery();
			
			while (rsSegments.next()) {
				lstAudioSegments.add(new AudioSegmentsValues(rsSegments.getLong("id_audio_segment"), 
														     rsSegments.getString("audio_segment"), 
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
		
		return lstAudioSegments;
	}
	
	/**
	 * Exclui um segmento de áudio do banco de dados.<br>
	 * <br>
	 * Para os segmentos de áudio gravados no banco de dados da FNJV,
	 * os registros são desativados para futura atualização nas bases de dados dos usuários.
	 * Neste caso, somente os dados das tabelas onde são armazenadas as features são excluídos.
	 * 
	 * @param lgnIdAudioSegment
	 * 
	 * @return blnAudioSegmentDeleted
	 */
	public static boolean deleteAudioSegment(long lgnIdAudioSegment) {
		boolean blnAudioSegmentDeleted = false;
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			
			// Caso o usuário seja da FNJV, o segmento é desativado para futura atualização nas bases de dados dos usuários
			if (WasisParameters.getInstance().getWasisUser().equals("wasis_fnjv")) {
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("UPDATE audio_files_segments ");
				objDatabaseConnection.sqlCommandAppend("SET ind_active = ?, date_update = ? ");
				objDatabaseConnection.sqlCommandAppend("WHERE id_audio_segment = ? ");
				objDatabaseConnection.addParameter("ind_active", LTDataTypes.BOOLEAN, false);
				objDatabaseConnection.addParameter("date_update", LTDataTypes.DATE, new Date());
				objDatabaseConnection.addParameter("id_audio_segment", LTDataTypes.LONG, lgnIdAudioSegment);
				objDatabaseConnection.executeQuery();
				
				// Exclui os dados das features extraídas do segmento de áudio
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("DELETE FROM audio_files_segments_features ");
				objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_file_segment = ? ");
				objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioSegment);
				objDatabaseConnection.executeQuery();
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("DELETE FROM audio_files_segments_features_ps ");
				objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_file_segment = ? ");
				objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioSegment);
				objDatabaseConnection.executeQuery();
			
			// Para usuários padrões, exclui o segmento e automaticamente as features
			} else {
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("DELETE FROM audio_files_segments ");
				objDatabaseConnection.sqlCommandAppend("WHERE id_audio_segment = ? ");
				objDatabaseConnection.addParameter("id_audio_segment", LTDataTypes.LONG, lgnIdAudioSegment);
				objDatabaseConnection.executeQuery();
			}
			
			objDatabaseConnection.commitTransaction();
			
			blnAudioSegmentDeleted = true;
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return blnAudioSegmentDeleted;
	}
}