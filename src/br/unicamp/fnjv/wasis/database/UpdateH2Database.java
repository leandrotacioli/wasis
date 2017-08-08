package br.unicamp.fnjv.wasis.database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;

import com.leandrotacioli.libs.LTDataTypes;

/**
 * Atualiza banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 15/Mai/2015
 */
public class UpdateH2Database {
	private DatabaseH2Connection objDatabaseH2;      // Conexão padrão do banco de dados
	private DatabaseH2Connection objDatabaseH2Temp;  // Conexão temporária do banco de dados
	
	private Collection<Object> collectionListener;
	
	/**
	 * Atualiza banco de dados.
	 */
	public UpdateH2Database() {
		this.collectionListener = new ArrayList<Object>();
		
		objDatabaseH2 = new DatabaseH2Connection();
		
		objDatabaseH2Temp = new DatabaseH2Connection();
		objDatabaseH2Temp.setDatabaseConnection("localhost", "wasis-update", "", "");
	}
	
	/**
	 * Atualiza os registros do banco de dados.
	 */
	public void updateDatabase() {
		try {
			objDatabaseH2.openConnection();
			objDatabaseH2Temp.openConnection();
			
			// Verifica a partir de qual registro o banco de dados deve atualizar
			// Verifica o último registro importado do banco de dados da FNJV
			objDatabaseH2.initiliazeStatement();
			objDatabaseH2.sqlCommand("SELECT max(fk_audio_file_selection_fnjv) AS fk_audio_file_selection_fnjv ");
			objDatabaseH2.sqlCommandAppend("FROM audio_files_selections ");
			
			ResultSet rsSelection = objDatabaseH2.executeSelectQuery();
			
			long lgnLastRecordFNJV = 0;
			
			while (rsSelection.next()) {
				lgnLastRecordFNJV = rsSelection.getLong("fk_audio_file_selection_fnjv");
				break;
			}
			
			// Insere uma data inicial caso o banco não tenha nada
			String strDateLastUpdate = "2010-01-01 00:00:00";
			SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateLastUpdate = null;
			
			try {
				dateLastUpdate = dateFormatter.parse(strDateLastUpdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if (lgnLastRecordFNJV > 0) {
				// Verifica o último registro importado do banco de dados da FNJV
				objDatabaseH2.initiliazeStatement();
				objDatabaseH2.sqlCommand("SELECT max(date_update) ");
				objDatabaseH2.sqlCommandAppend("FROM audio_files_selections ");
				objDatabaseH2.executeSelectQuery();
				
				dateLastUpdate = (Date) objDatabaseH2.getFirstValue(LTDataTypes.DATE);
			}
			
			// Busca no banco de dados temporário os registros que precisam ser atualizados
			// baseando-se no último registro da importado da FNJV e data da última atualização
			objDatabaseH2Temp.initiliazeStatement();
			objDatabaseH2Temp.sqlCommand("SELECT aud.*, sel.*, sel.id as id_selection ");
			objDatabaseH2Temp.sqlCommandAppend("FROM       audio_files                aud ");
			objDatabaseH2Temp.sqlCommandAppend("INNER JOIN audio_files_selections     sel   ON   sel.fk_audio_file = aud.id ");
			objDatabaseH2Temp.sqlCommandAppend("WHERE sel.id > ? ");
			objDatabaseH2Temp.sqlCommandAppend("OR sel.date_update >= ? ");
			objDatabaseH2Temp.addParameter("id", LTDataTypes.LONG, lgnLastRecordFNJV);
			objDatabaseH2Temp.addParameter("date_update", LTDataTypes.DATE, dateLastUpdate);
			
			ResultSet rsAudioData = objDatabaseH2Temp.executeSelectQuery();

			int intTotalRecords = objDatabaseH2Temp.getTotalRecords();
			int intProcessedRecords = 0;
			
			while (rsAudioData.next()) {
				intProcessedRecords = intProcessedRecords + 1;
				updateProcessedRecords(intProcessedRecords, intTotalRecords);
				
				// Verifica se a seleção já consta no banco de dados
				objDatabaseH2.initiliazeStatement();
				objDatabaseH2.sqlCommand("SELECT id ");
				objDatabaseH2.sqlCommandAppend("FROM audio_files_selections ");
				objDatabaseH2.sqlCommandAppend("WHERE fk_audio_file_selection_fnjv = ? ");
				objDatabaseH2.addParameter("fk_audio_file_selection_fnjv", LTDataTypes.LONG, rsAudioData.getLong("id_selection"));
				
				ResultSet rsSelectionFound = objDatabaseH2.executeSelectQuery();
				
				long lgnIDAudioFileSelection = 0;
				
				while (rsSelectionFound.next()) {
					lgnIDAudioFileSelection = rsSelectionFound.getLong("id");
					break;
				}
				
				// Atualiza os dados
				if (lgnIDAudioFileSelection != 0) {
					
					try {
						objDatabaseH2.initiliazeStatement();
						objDatabaseH2.sqlCommand("UPDATE audio_files_selections ");
						objDatabaseH2.sqlCommandAppend("SET ind_active = ?, date_update = ? ");
						objDatabaseH2.sqlCommandAppend("WHERE id = ? ");
						objDatabaseH2.addParameter("ind_active", LTDataTypes.BOOLEAN, rsAudioData.getBoolean("ind_active"));
						objDatabaseH2.addParameter("date_update", LTDataTypes.DATE, rsAudioData.getDate("date_update"));
						objDatabaseH2.addParameter("id", LTDataTypes.LONG, lgnIDAudioFileSelection);
						objDatabaseH2.executeQuery();
						
						if (!rsAudioData.getBoolean("ind_active")) {
							objDatabaseH2.initiliazeStatement();
							objDatabaseH2.sqlCommand("DELETE FROM audio_files_selections_values ");
							objDatabaseH2.sqlCommandAppend("WHERE fk_audio_file_selection = ? ");
							objDatabaseH2.addParameter("fk_audio_file_selection", LTDataTypes.LONG, lgnIDAudioFileSelection);
							objDatabaseH2.executeQuery();
						}
						
						objDatabaseH2.commitTransaction();
						
					} catch (SQLException e) {
						objDatabaseH2.rollBackTransaction();
					}
					
				// Insere os dados
				} else {

					try {
						
						// Insere os dados apenas se os registros estiverem ativos
						if (rsAudioData.getBoolean("ind_active")) {
							// Inicialmente insere os dados da tabela 'audio_files'
							objDatabaseH2.initiliazeStatement();
							objDatabaseH2.sqlCommand("INSERT INTO audio_files (audio_file_path, audio_file_data_size, voucher_number, animal_phylum, animal_class, " +
																		     " animal_order, animal_family, animal_genus, animal_species, " +
																		     " location_city, location_state, location_country, " +
																		     " location_locality, location_latitude, location_longitude, " +
									                                         " date_day, date_month, date_year, time_recording, call_type, recordist, observations) ");
							objDatabaseH2.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
							objDatabaseH2.addParameter("audio_file_path", LTDataTypes.STRING, rsAudioData.getString("audio_file_path"));
							objDatabaseH2.addParameter("audio_file_data_size", LTDataTypes.INTEGER, rsAudioData.getString("audio_file_data_size"));
							objDatabaseH2.addParameter("voucher_number", LTDataTypes.STRING, rsAudioData.getString("voucher_number"));
							objDatabaseH2.addParameter("animal_phylum", LTDataTypes.STRING, rsAudioData.getString("animal_phylum"));
							objDatabaseH2.addParameter("animal_class", LTDataTypes.STRING, rsAudioData.getString("animal_class"));
							objDatabaseH2.addParameter("animal_order", LTDataTypes.STRING, rsAudioData.getString("animal_order"));
							objDatabaseH2.addParameter("animal_family", LTDataTypes.STRING, rsAudioData.getString("animal_family"));
							objDatabaseH2.addParameter("animal_genus", LTDataTypes.STRING, rsAudioData.getString("animal_genus"));
							objDatabaseH2.addParameter("animal_species", LTDataTypes.STRING, rsAudioData.getString("animal_species"));
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
							
							long lgnIDAudioFile = objDatabaseH2.getIdentityKey();
							
							// Depois insere os dados da tabela 'audio_files_selections'
							objDatabaseH2.initiliazeStatement();
							objDatabaseH2.sqlCommand("INSERT INTO audio_files_selections (fk_audio_file, sound_unit, " +
													                                    " time_initial, time_final, " +
											                                            " frequency_initial, frequency_final, " +
											                                            " ind_active, date_update, fk_audio_file_selection_fnjv ) ");
							objDatabaseH2.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
							objDatabaseH2.addParameter("fk_audio_file", LTDataTypes.LONG, lgnIDAudioFile);
							objDatabaseH2.addParameter("sound_unit", LTDataTypes.STRING, rsAudioData.getString("sound_unit"));
							objDatabaseH2.addParameter("time_initial", LTDataTypes.INTEGER, rsAudioData.getInt("time_initial"));
							objDatabaseH2.addParameter("time_final", LTDataTypes.INTEGER, rsAudioData.getInt("time_final"));
							objDatabaseH2.addParameter("frequency_initial", LTDataTypes.INTEGER, rsAudioData.getInt("frequency_initial"));
							objDatabaseH2.addParameter("frequency_final", LTDataTypes.INTEGER, rsAudioData.getInt("frequency_final"));
							objDatabaseH2.addParameter("ind_active", LTDataTypes.BOOLEAN, rsAudioData.getBoolean("ind_active"));
							objDatabaseH2.addParameter("date_update", LTDataTypes.DATE, rsAudioData.getDate("date_update"));
							objDatabaseH2.addParameter("fk_audio_file_selection_fnjv", LTDataTypes.LONG, rsAudioData.getLong("id_selection"));
							objDatabaseH2.executeQuery();
							
							lgnIDAudioFileSelection = objDatabaseH2.getIdentityKey();
							
							// Consulta os dados da tabela 'audio_files_selections_values' e insere no banco de dados padrão
							objDatabaseH2Temp.initiliazeStatement();
							objDatabaseH2Temp.sqlCommand("SELECT * ");
							objDatabaseH2Temp.sqlCommandAppend("FROM     audio_files_selections_values ");
							objDatabaseH2Temp.sqlCommandAppend("WHERE fk_audio_file_selection = ? ");
							objDatabaseH2Temp.addParameter("fk_audio_file_selection", LTDataTypes.LONG, rsAudioData.getLong("id_selection"));
							
							ResultSet rsAudioFileSelectionValues = objDatabaseH2Temp.executeSelectQuery();
							
							while (rsAudioFileSelectionValues.next()) {
								objDatabaseH2.initiliazeStatement();
								objDatabaseH2.sqlCommand("INSERT INTO audio_files_selections_values (fk_audio_file_selection, " +
														                                           " frequency_value, decibel_value) ");
								objDatabaseH2.sqlCommandAppend("VALUES (?, ?, ?) ");
								objDatabaseH2.addParameter("fk_audio_file_selection", LTDataTypes.LONG, lgnIDAudioFileSelection);
								objDatabaseH2.addParameter("frequency_value", LTDataTypes.DOUBLE, rsAudioFileSelectionValues.getDouble("frequency_value"));
								objDatabaseH2.addParameter("decibel_value", LTDataTypes.DOUBLE, rsAudioFileSelectionValues.getDouble("decibel_value"));
								objDatabaseH2.executeQuery();
							}
							
							objDatabaseH2.commitTransaction();
						}
						
					// Caso de algum erro na inclusão de um registro,
					// cancela a transação, mas continua atualizando os outros registros
					} catch (SQLException e) {
						e.printStackTrace();
						
						objDatabaseH2.rollBackTransaction();
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseH2.rollBackTransaction();
			objDatabaseH2Temp.rollBackTransaction();
			
			objDatabaseH2.closeConnection();
			objDatabaseH2Temp.closeConnection();
		}
	}
	
	/**
     * Adiciona um 'UpdateH2DatabaseListener' parametrizado à 'collection listener'.
     * 
     * @param h2DatabaseListener
     */
    public void addCollectionListener(UpdateH2DatabaseListener h2DatabaseListener) {
    	collectionListener.add(h2DatabaseListener);
    }
    
    /**
	 * Notifica o 'WaveformListener' de uma atualização na posição atual do mouse no waveform.
	 * 
	 * @param intTime - Posição do mouse no tempo (em milisegundos)
	 */
	protected void updateProcessedRecords(int intProcessedRecords, int intTotalRecords) {
        Iterator<Object> it = collectionListener.iterator();
        UpdateH2DatabaseListener h2DatabaseListener;
        while (it.hasNext()) {
        	h2DatabaseListener = (UpdateH2DatabaseListener) it.next();
        	h2DatabaseListener.updateProcessedRecords(intProcessedRecords, intTotalRecords);
        }
	}
}