package br.unicamp.fnjv.wasis.main;

import java.sql.ResultSet;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;

public class FnjvToWasisDB {
	private final String DATE_DEFAULT = "dd/MM/yyyy";
	private final DateFormat DATE_FORMAT = new SimpleDateFormat(DATE_DEFAULT);
	
	public FnjvToWasisDB() {
		String strFnjvServer = "localhost";
		String strFnjvDBName = "fnjv_mestrado";
		String strFnjvDBUser = "root";
		String strFnjvDBPass = "123456";
		
		DatabaseMySQLConnection objDatabaseConnectionFNJV = new DatabaseMySQLConnection();
		objDatabaseConnectionFNJV.setDatabaseConnection(strFnjvServer, strFnjvDBName, strFnjvDBUser, strFnjvDBPass);
		objDatabaseConnectionFNJV.openConnection();
		
		String strWasisServer = "localhost";
		String strWasisDBName = "wasis_mestrado";
		String strWasisDBUser = "root";
		String strWasisDBPass = "123456";
		
		DatabaseMySQLConnection objDatabaseConnectionWasis = new DatabaseMySQLConnection();
		objDatabaseConnectionWasis.setDatabaseConnection(strWasisServer, strWasisDBName, strWasisDBUser, strWasisDBPass);
		objDatabaseConnectionWasis.openConnection();
		
		try {
			// Consulta os registros do banco da FNJV
			objDatabaseConnectionFNJV.initiliazeStatement();
			objDatabaseConnectionFNJV.sqlCommand("SELECT * ");
			objDatabaseConnectionFNJV.sqlCommandAppend("FROM view_fnjv_collection ");
			objDatabaseConnectionFNJV.sqlCommandAppend("WHERE record_type = 'AUDIO' ");
			objDatabaseConnectionFNJV.sqlCommandAppend("ORDER BY catalog_number_fnjv ");
		
			ResultSet rsFnjvRecord = objDatabaseConnectionFNJV.executeSelectQuery();
			
			objDatabaseConnectionFNJV.rollBackTransaction();
			
			// Padrão do VOUCHER = "FNJV 0000001"
			
			while (rsFnjvRecord.next()) {
				// Verifica se já existe o registro no BD do WASIS
				objDatabaseConnectionWasis.initiliazeStatement();
				objDatabaseConnectionWasis.sqlCommand("SELECT id ");
				objDatabaseConnectionWasis.sqlCommandAppend("FROM audio_files ");
				objDatabaseConnectionWasis.sqlCommandAppend("WHERE voucher_number = ? ");
				objDatabaseConnectionWasis.addParameter("voucher_number", LTDataTypes.STRING, "FNJV " + rsFnjvRecord.getString("catalog_number_fnjv"));
				
				ResultSet rsWasisRecord = objDatabaseConnectionWasis.executeSelectQuery();
				
				int intIdWasisRecord = 0;
				
				while (rsWasisRecord.next()) {
					intIdWasisRecord = rsWasisRecord.getInt("id");
				}
				
				objDatabaseConnectionWasis.rollBackTransaction();
				
				int[] recordDate = getRecordDate(rsFnjvRecord.getString("date_recording"));
				
				// ******************************************************************************************
				// Registro a ser inseridos no banco do WASIS
				if (intIdWasisRecord == 0) {
					objDatabaseConnectionWasis.initiliazeStatement();
					objDatabaseConnectionWasis.sqlCommand("INSERT INTO audio_files (audio_file_path, audio_file_hash, voucher_number, " +
							                                                      " animal_phylum, animal_class, animal_order, " +
							                                                      " animal_family, animal_genus, animal_species, " +
							                                                      " animal_name_portuguese, animal_name_english, " +
							                                                      " location_city, location_state, location_country, " +
							                                                      " date_day, date_month, date_year, " +
							                                                      " time_recording, call_type, recordist, observations) ");
					objDatabaseConnectionWasis.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
					objDatabaseConnectionWasis.addParameter("audio_file_path", LTDataTypes.STRING, rsFnjvRecord.getString("record_file_name"));
					objDatabaseConnectionWasis.addParameter("audio_file_hash", LTDataTypes.STRING, rsFnjvRecord.getString("record_file_hash"));
					objDatabaseConnectionWasis.addParameter("voucher_number", LTDataTypes.STRING, "FNJV " + rsFnjvRecord.getString("catalog_number_fnjv"));
					objDatabaseConnectionWasis.addParameter("animal_phylum", LTDataTypes.STRING, rsFnjvRecord.getString("animal_phylum"));
					objDatabaseConnectionWasis.addParameter("animal_class", LTDataTypes.STRING, rsFnjvRecord.getString("animal_class"));
					objDatabaseConnectionWasis.addParameter("animal_order", LTDataTypes.STRING, rsFnjvRecord.getString("animal_order"));
					objDatabaseConnectionWasis.addParameter("animal_family", LTDataTypes.STRING, rsFnjvRecord.getString("animal_family"));
					objDatabaseConnectionWasis.addParameter("animal_genus", LTDataTypes.STRING, rsFnjvRecord.getString("animal_genus"));
					objDatabaseConnectionWasis.addParameter("animal_species", LTDataTypes.STRING, rsFnjvRecord.getString("animal_species"));
					objDatabaseConnectionWasis.addParameter("animal_name_portuguese", LTDataTypes.STRING, rsFnjvRecord.getString("animal_name_portuguese"));
					objDatabaseConnectionWasis.addParameter("animal_name_english", LTDataTypes.STRING, rsFnjvRecord.getString("animal_name_english"));
					objDatabaseConnectionWasis.addParameter("location_city", LTDataTypes.STRING, rsFnjvRecord.getString("location_city"));
					objDatabaseConnectionWasis.addParameter("location_state", LTDataTypes.STRING, rsFnjvRecord.getString("location_state"));
					objDatabaseConnectionWasis.addParameter("location_country", LTDataTypes.STRING, rsFnjvRecord.getString("location_country"));
					objDatabaseConnectionWasis.addParameter("date_day", LTDataTypes.STRING, recordDate[0]);
					objDatabaseConnectionWasis.addParameter("date_month", LTDataTypes.STRING, recordDate[1]);
					objDatabaseConnectionWasis.addParameter("date_year", LTDataTypes.STRING, recordDate[2]);
					objDatabaseConnectionWasis.addParameter("time_recording", LTDataTypes.STRING, rsFnjvRecord.getString("time_recording"));
					objDatabaseConnectionWasis.addParameter("call_type", LTDataTypes.STRING, rsFnjvRecord.getString("call_type"));
					objDatabaseConnectionWasis.addParameter("recordist", LTDataTypes.STRING, rsFnjvRecord.getString("recordist"));
					objDatabaseConnectionWasis.addParameter("observations", LTDataTypes.STRING, rsFnjvRecord.getString("general_notes"));
					objDatabaseConnectionWasis.executeQuery();
					objDatabaseConnectionWasis.commitTransaction();
					
				// Registro a ser atualizado no banco do WASIS
				} else {
					objDatabaseConnectionWasis.initiliazeStatement();
					objDatabaseConnectionWasis.sqlCommand("UPDATE audio_files SET " +
					                                      "audio_file_path = ?, audio_file_hash = ?, " +
                                                          "animal_phylum = ?, animal_class = ?, animal_order = ?, " +
								                          "animal_family = ?, animal_genus = ?, animal_species = ?, " +
								                          "animal_name_portuguese = ?, animal_name_english = ?, " +
								                          "location_city = ?, location_state = ?, location_country = ?, " +
								                          "date_day = ?, date_month = ?, date_year = ?, " +
								                          "time_recording = ?, call_type = ?, recordist = ?, observations = ? ");
					objDatabaseConnectionWasis.sqlCommandAppend("WHERE id = ? ");
					objDatabaseConnectionWasis.addParameter("audio_file_path", LTDataTypes.STRING, rsFnjvRecord.getString("record_file_name"));
					objDatabaseConnectionWasis.addParameter("audio_file_hash", LTDataTypes.STRING, rsFnjvRecord.getString("record_file_hash"));
					objDatabaseConnectionWasis.addParameter("animal_phylum", LTDataTypes.STRING, rsFnjvRecord.getString("animal_phylum"));
					objDatabaseConnectionWasis.addParameter("animal_class", LTDataTypes.STRING, rsFnjvRecord.getString("animal_class"));
					objDatabaseConnectionWasis.addParameter("animal_order", LTDataTypes.STRING, rsFnjvRecord.getString("animal_order"));
					objDatabaseConnectionWasis.addParameter("animal_family", LTDataTypes.STRING, rsFnjvRecord.getString("animal_family"));
					objDatabaseConnectionWasis.addParameter("animal_genus", LTDataTypes.STRING, rsFnjvRecord.getString("animal_genus"));
					objDatabaseConnectionWasis.addParameter("animal_species", LTDataTypes.STRING, rsFnjvRecord.getString("animal_species"));
					objDatabaseConnectionWasis.addParameter("animal_name_portuguese", LTDataTypes.STRING, rsFnjvRecord.getString("animal_name_portuguese"));
					objDatabaseConnectionWasis.addParameter("animal_name_english", LTDataTypes.STRING, rsFnjvRecord.getString("animal_name_english"));
					objDatabaseConnectionWasis.addParameter("location_city", LTDataTypes.STRING, rsFnjvRecord.getString("location_city"));
					objDatabaseConnectionWasis.addParameter("location_state", LTDataTypes.STRING, rsFnjvRecord.getString("location_state"));
					objDatabaseConnectionWasis.addParameter("location_country", LTDataTypes.STRING, rsFnjvRecord.getString("location_country"));
					objDatabaseConnectionWasis.addParameter("date_day", LTDataTypes.STRING, recordDate[0]);
					objDatabaseConnectionWasis.addParameter("date_month", LTDataTypes.STRING, recordDate[1]);
					objDatabaseConnectionWasis.addParameter("date_year", LTDataTypes.STRING, recordDate[2]);
					objDatabaseConnectionWasis.addParameter("time_recording", LTDataTypes.STRING, rsFnjvRecord.getString("time_recording"));
					objDatabaseConnectionWasis.addParameter("call_type", LTDataTypes.STRING, rsFnjvRecord.getString("call_type"));
					objDatabaseConnectionWasis.addParameter("recordist", LTDataTypes.STRING, rsFnjvRecord.getString("recordist"));
					objDatabaseConnectionWasis.addParameter("observations", LTDataTypes.STRING, rsFnjvRecord.getString("general_notes"));
					objDatabaseConnectionWasis.addParameter("id", LTDataTypes.INTEGER, intIdWasisRecord);
					objDatabaseConnectionWasis.executeQuery();
					objDatabaseConnectionWasis.commitTransaction();
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnectionFNJV.rollBackTransaction();
			objDatabaseConnectionFNJV.closeConnection();
			
			objDatabaseConnectionWasis.rollBackTransaction();
			objDatabaseConnectionWasis.closeConnection();
		}
	}
	
	/**
	 * Retorna a data no formato do WASIS.
	 * 
	 * @param strFnjvDate
	 * 
	 * @return recordDate<br>
	 *         [0] = Dia<br>
	 *         [1] = Mês<br>
	 *         [2] = Ano
	 */
	private int[] getRecordDate(String strDate) {
		int[] recordDate = new int[3];
		
		boolean blnDateValidity = checkDateValidity(strDate);

		try {
			// Data é válida no padrão da FNJV
			if (blnDateValidity) {
				if (!strDate.substring(0, 2).equals("00") || !strDate.substring(3, 5).equals("00")) {
					Date date = (Date) DATE_FORMAT.parse(strDate);  // Transforma a data válida no formato correto
					strDate = DATE_FORMAT.format(date);             // Transforma data validada em string
				}
				
				recordDate[0] = Integer.parseInt(strDate.substring(0, 2));
				recordDate[1] = Integer.parseInt(strDate.substring(3, 5));
				recordDate[2] = Integer.parseInt(strDate.substring(6, 10));
	
			// Data inválida, mas mesmo assim o sistema insere os dados
			} else {
				
				// Somente ano (2010)
				if (strDate.length() == 4) {
					recordDate[0] = 0;
					recordDate[1] = 0;
					recordDate[2] = Integer.parseInt(strDate);
					
				// Mês e ano (05/2010)
				} else if (strDate.length() == 7) {
					recordDate[0] = 0;
					recordDate[1] = Integer.parseInt(strDate.substring(0, 2));
					recordDate[2] = Integer.parseInt(strDate.substring(3, 7));
					
				// Dia, mês e ano (05/11/73)
				} else if (strDate.length() == 8) {
					recordDate[0] = Integer.parseInt(strDate.substring(0, 2));
					recordDate[1] = Integer.parseInt(strDate.substring(3, 5));
					recordDate[2] = Integer.parseInt(strDate.substring(6, 8));
					
				// Dia, mês e ano (05/11/1973)
				} else if (strDate.length() == 10) {
					recordDate[0] = Integer.parseInt(strDate.substring(0, 2));
					recordDate[1] = Integer.parseInt(strDate.substring(3, 5));
					recordDate[2] = Integer.parseInt(strDate.substring(6, 10));
				}
			}
		
		} catch (ParseException e) {
			e.printStackTrace();
		}
			
		return recordDate;
	}
	
	/**
	 * Verifica se a data é válida de acordo 
	 * com o formato estabelecido na FNJV.
	 * 
	 * @param strDate
	 * 
	 * @return blnValidDate
	 */
	private boolean checkDateValidity(String strDate) {
		boolean blnValidDate = false;
		
		try {
			// Tamanho da data é diferente do tamanho padrão
			if (strDate.length() != DATE_DEFAULT.length()) {
				blnValidDate = false;
			} else {
				DATE_FORMAT.parse(strDate);
				blnValidDate = true;
			}
        } catch (ParseException e) {
        	blnValidDate = false;
        }
		
		return blnValidDate;
	}
	
	public static void main(String[] args) {
		new FnjvToWasisDB();
	}
}
