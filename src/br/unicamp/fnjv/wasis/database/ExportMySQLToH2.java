package br.unicamp.fnjv.wasis.database;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.jdbc.DatabaseH2Connection;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseMySQLConnection;

/**
 * Exporta os dados do banco de dados MySQL para o H2.
 * 
 * @author Leandro
 *
 */
public class ExportMySQLToH2 {

	public static void main(String args[]) {
		try {
			DatabaseMySQLConnection objDatabaseMySQL = new DatabaseMySQLConnection();
			DatabaseH2Connection objDatabaseH2 = new DatabaseH2Connection();
			
			objDatabaseMySQL.openConnection();
			objDatabaseH2.openConnection();
			
			// Exclui os arquivo do banco H2
			objDatabaseH2.initiliazeStatement();
			objDatabaseH2.sqlCommand("DELETE FROM audio_files_selections_values ");
			objDatabaseH2.executeQuery();
			
			objDatabaseH2.initiliazeStatement();
			objDatabaseH2.sqlCommand("DELETE FROM audio_files_selections ");
			objDatabaseH2.executeQuery();
			
			objDatabaseH2.initiliazeStatement();
			objDatabaseH2.sqlCommand("DELETE FROM audio_files ");
			objDatabaseH2.executeQuery();
			
			objDatabaseH2.commitTransaction();
			
			// Audio Files
			objDatabaseMySQL.initiliazeStatement();
			objDatabaseMySQL.sqlCommand("SELECT aud.* ");
			objDatabaseMySQL.sqlCommandAppend("FROM             audio_files                  aud ");
			objDatabaseMySQL.sqlCommandAppend("RIGHT OUTER JOIN audio_files_selections       sel    ON     sel.fk_audio_file = aud.id ");
			objDatabaseMySQL.sqlCommandAppend("GROUP BY aud.id ");
			
			ResultSet rsAudioData = objDatabaseMySQL.executeSelectQuery();

			while (rsAudioData.next()) {
				System.out.println(rsAudioData.getString("voucher_number") + " | " + rsAudioData.getString("animal_genus") + " " + rsAudioData.getString("animal_species"));
				
				objDatabaseH2.initiliazeStatement();
				objDatabaseH2.sqlCommand("INSERT INTO audio_files (audio_file_path, audio_file_data_size, voucher_number, animal_phylum, animal_class, " +
															   	 " animal_order, animal_family, animal_genus, animal_species, animal_name_portuguese, " +
															   	 " location_city, location_state, location_country, " +
															   	 " location_locality, location_latitude, location_longitude, " +
						                                       	 " date_day, date_month, date_year, time_recording, call_type, recordist, observations) ");
				objDatabaseH2.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
				objDatabaseH2.addParameter("audio_file_path", LTDataTypes.STRING, rsAudioData.getString("audio_file_path"));
				objDatabaseH2.addParameter("audio_file_data_size", LTDataTypes.INTEGER, rsAudioData.getInt("audio_file_data_size"));
				objDatabaseH2.addParameter("voucher_number", LTDataTypes.STRING, rsAudioData.getString("voucher_number"));
				objDatabaseH2.addParameter("animal_phylum", LTDataTypes.STRING, rsAudioData.getString("animal_phylum"));
				objDatabaseH2.addParameter("animal_class", LTDataTypes.STRING, rsAudioData.getString("animal_class"));
				objDatabaseH2.addParameter("animal_order", LTDataTypes.STRING, rsAudioData.getString("animal_order"));
				objDatabaseH2.addParameter("animal_family", LTDataTypes.STRING, rsAudioData.getString("animal_family"));
				objDatabaseH2.addParameter("animal_genus", LTDataTypes.STRING, rsAudioData.getString("animal_genus"));
				objDatabaseH2.addParameter("animal_species", LTDataTypes.STRING, rsAudioData.getString("animal_species"));
				objDatabaseH2.addParameter("animal_name_portuguese", LTDataTypes.STRING, rsAudioData.getString("animal_name_portuguese"));
				objDatabaseH2.addParameter("location_city", LTDataTypes.STRING, rsAudioData.getString("location_city"));
				objDatabaseH2.addParameter("location_state", LTDataTypes.STRING, rsAudioData.getString("location_state"));
				objDatabaseH2.addParameter("location_country", LTDataTypes.STRING, rsAudioData.getString("location_country"));
				objDatabaseH2.addParameter("location_locality", LTDataTypes.STRING, rsAudioData.getString("location_locality"));
				objDatabaseH2.addParameter("location_latitude", LTDataTypes.STRING, rsAudioData.getString("location_latitude"));
				objDatabaseH2.addParameter("location_longitude", LTDataTypes.STRING, rsAudioData.getString("location_longitude"));
				objDatabaseH2.addParameter("date_day", LTDataTypes.INTEGER, rsAudioData.getInt("date_day"));
				objDatabaseH2.addParameter("date_month", LTDataTypes.INTEGER, rsAudioData.getInt("date_month"));
				objDatabaseH2.addParameter("date_year", LTDataTypes.INTEGER, rsAudioData.getInt("date_year"));
				objDatabaseH2.addParameter("time_recording", LTDataTypes.STRING, rsAudioData.getString("time_recording"));
				objDatabaseH2.addParameter("call_type", LTDataTypes.STRING, rsAudioData.getString("call_type"));
				objDatabaseH2.addParameter("recordist", LTDataTypes.STRING, rsAudioData.getString("recordist"));
				objDatabaseH2.addParameter("observations", LTDataTypes.STRING, rsAudioData.getString("observations"));
				objDatabaseH2.executeQuery();
				
				long lgnIdAudioFile = objDatabaseH2.getIdentityKey();
				
				// Insere seleções
				// Audio Files - Selections
				objDatabaseMySQL.initiliazeStatement();
				objDatabaseMySQL.sqlCommand("SELECT * ");
				objDatabaseMySQL.sqlCommandAppend("FROM audio_files_selections ");
				objDatabaseMySQL.sqlCommandAppend("WHERE fk_audio_file = ? ");
				objDatabaseMySQL.addParameter("fk_audio_file", LTDataTypes.LONG, rsAudioData.getLong("id"));
				
				ResultSet rsAudioDataSelections = objDatabaseMySQL.executeSelectQuery();
				
				while (rsAudioDataSelections.next()) {
					objDatabaseH2.initiliazeStatement();
					objDatabaseH2.sqlCommand("INSERT INTO audio_files_selections (fk_audio_file, sound_unit, " +
											                                    " time_initial, time_final, " +
									                                            " frequency_initial, frequency_final, " +
									                                             "date_update, fk_audio_file_selection_fnjv) ");
					objDatabaseH2.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");
					objDatabaseH2.addParameter("fk_audio_file", LTDataTypes.LONG, lgnIdAudioFile);
					objDatabaseH2.addParameter("sound_unit", LTDataTypes.STRING, rsAudioDataSelections.getString("sound_unit"));
					objDatabaseH2.addParameter("time_initial", LTDataTypes.INTEGER, rsAudioDataSelections.getInt("time_initial"));
					objDatabaseH2.addParameter("time_final", LTDataTypes.INTEGER, rsAudioDataSelections.getInt("time_final"));
					objDatabaseH2.addParameter("frequency_initial", LTDataTypes.INTEGER, rsAudioDataSelections.getInt("frequency_initial"));
					objDatabaseH2.addParameter("frequency_final", LTDataTypes.INTEGER, rsAudioDataSelections.getInt("frequency_final"));
					objDatabaseH2.addParameter("date_update", LTDataTypes.DATE, rsAudioDataSelections.getDate("date_update"));
					objDatabaseH2.addParameter("fk_audio_file_selection_fnjv", LTDataTypes.LONG, rsAudioDataSelections.getLong("id"));
					objDatabaseH2.executeQuery();
					
					long lgnIdAudioFileSelection = objDatabaseH2.getIdentityKey();
					
					// Audio Files - Values
					objDatabaseMySQL.initiliazeStatement();
					objDatabaseMySQL.sqlCommand("SELECT * ");
					objDatabaseMySQL.sqlCommandAppend("FROM audio_files_selections_values ");
					objDatabaseMySQL.sqlCommandAppend("WHERE fk_audio_file_selection = ? ");
					objDatabaseMySQL.addParameter("fk_audio_file_selection", LTDataTypes.LONG, rsAudioDataSelections.getLong("id"));
					
					ResultSet rsAudioDataSelectionsValues = objDatabaseMySQL.executeSelectQuery();
					
					while (rsAudioDataSelectionsValues.next()) {
						objDatabaseH2.initiliazeStatement();
						objDatabaseH2.sqlCommand("INSERT INTO audio_files_selections_values (fk_audio_file_selection, frequency_value, decibel_value) ");
						objDatabaseH2.sqlCommandAppend("VALUES (?, ?, ?) ");
						objDatabaseH2.addParameter("fk_audio_file_selection", LTDataTypes.LONG, lgnIdAudioFileSelection);
						objDatabaseH2.addParameter("frequency_value", LTDataTypes.INTEGER, rsAudioDataSelectionsValues.getInt("frequency_value"));
						objDatabaseH2.addParameter("decibel_value", LTDataTypes.DOUBLE, rsAudioDataSelectionsValues.getDouble("decibel_value"));
						objDatabaseH2.executeQuery();
					}
				}
				
				objDatabaseH2.commitTransaction();
			}
			
			objDatabaseMySQL.rollBackTransaction();
			objDatabaseMySQL.closeConnection();
			
			objDatabaseH2.closeConnection();
			
			System.out.println("Fim");
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		
	}
}
