package br.unicamp.fnjv.wasis.main.tests;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

public class SaveFeatures {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	//private static String PATH_AUDIO_FILES = "C:\\Users\\LaHNAB\\Desktop\\Sons";
	private static String PATH_AUDIO_FILES_CONVERTED = "C:\\Users\\LaHNAB\\Desktop\\Sons-Convertidos";
	//private static String PATH_AUDIO_FILES = "C:\\Users\\Leandro\\Desktop\\Mestrado";
	//private static String PATH_AUDIO_FILES_CONVERTED = "C:\\Users\\Leandro\\Desktop\\Convertidos";
	
	private static int intNumberProcessors = 1;
	
	public static void main(String[] args) {
		//intNumberProcessors = Runtime.getRuntime().availableProcessors();
		
		//extractPS();
		//extractMFCC();
		//extractLPC();
		//extractLPCC();
		//extractPLP();
	}
	
	private static void extractPS() {
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		
		int intRecordsExtracted = 0;

		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.id AS id_audio_file, aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_class, aud.animal_genus, aud.animal_species, aud.id ");
			
			ResultSet rsAudioFiles = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSegments;
			
			while (rsAudioFiles.next()) {
				String strAudioFilePath = PATH_AUDIO_FILES_CONVERTED + "\\" + rsAudioFiles.getString("animal_class") + "\\" + rsAudioFiles.getString("animal_genus") + " " + rsAudioFiles.getString("animal_species") +  "\\" + rsAudioFiles.getString("audio_file_path");
				
				AudioWav objAudioWav = new AudioWav(strAudioFilePath);
				objAudioWav.loadAudio();
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, seg.sound_unit, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id = ? ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
				objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, rsAudioFiles.getInt("id_audio_file"));
				
				rsSegments = objDatabaseConnection.executeSelectQuery();
				
				// É criada uma pool com uma thread para cada processador disponível para a extração da feature
				ExecutorService executorService = Executors.newFixedThreadPool(intNumberProcessors);
				
				while (rsSegments.next()) {
					int intInitialTime = rsSegments.getInt("time_initial");
					int intFinalTime = rsSegments.getInt("time_final");
					int intInitialFrequency = rsSegments.getInt("frequency_initial");
					int intFinalFrequency = rsSegments.getInt("frequency_final");
					
					ExtractPS objExtractPS = new ExtractPS(rsSegments.getInt("id_segment"), objAudioWav, intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
					executorService.execute(objExtractPS);
					
					intRecordsExtracted++;
				}
				
				executorService.shutdown();
				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}

		System.out.print("End Power Spectrum extraction | Registro extraídos: " + intRecordsExtracted);
		System.out.println("--------------------------------------------------------------------");
	}
	
	private static void extractMFCC() {
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		
		int intRecordsExtracted = 0;
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.id AS id_audio_file, aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_class, aud.animal_genus, aud.animal_species, aud.id ");
			
			ResultSet rsAudioFiles = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSegments;
			
			while (rsAudioFiles.next()) {
				String strAudioFilePath = PATH_AUDIO_FILES_CONVERTED + "\\" + rsAudioFiles.getString("animal_class") + "\\" + rsAudioFiles.getString("animal_genus") + " " + rsAudioFiles.getString("animal_species") +  "\\" + rsAudioFiles.getString("audio_file_path");
				
				AudioWav objAudioWav = new AudioWav(strAudioFilePath);
				objAudioWav.loadAudio();
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, seg.sound_unit, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id = ? ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
				objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, rsAudioFiles.getInt("id_audio_file"));
				
				rsSegments = objDatabaseConnection.executeSelectQuery();
				
				// É criada uma pool com uma thread para cada processador disponível para a extração da feature
				ExecutorService executorService = Executors.newFixedThreadPool(intNumberProcessors);
				
				while (rsSegments.next()) {
					int intInitialTime = rsSegments.getInt("time_initial");
					int intFinalTime = rsSegments.getInt("time_final");
					
					ExtractMFCC objExtractMFCC = new ExtractMFCC(rsSegments.getInt("id_segment"), objAudioWav, intInitialTime, intFinalTime);
					executorService.execute(objExtractMFCC);
					
					intRecordsExtracted++;
				}
				
				executorService.shutdown();
				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
			
		System.out.print("End MFCC extraction | Registro extraídos: " + intRecordsExtracted);
		System.out.println("");
	}
	
	private static void extractLPC() {
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		
		int intRecordsExtracted = 0;
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.id AS id_audio_file, aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_class, aud.animal_genus, aud.animal_species, aud.id ");
			
			ResultSet rsAudioFiles = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSegments;
			
			while (rsAudioFiles.next()) {
				String strAudioFilePath = PATH_AUDIO_FILES_CONVERTED + "\\" + rsAudioFiles.getString("animal_class") + "\\" + rsAudioFiles.getString("animal_genus") + " " + rsAudioFiles.getString("animal_species") +  "\\" + rsAudioFiles.getString("audio_file_path");
				
				AudioWav objAudioWav = new AudioWav(strAudioFilePath);
				objAudioWav.loadAudio();
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, seg.sound_unit, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id = ? ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
				objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, rsAudioFiles.getInt("id_audio_file"));
				
				rsSegments = objDatabaseConnection.executeSelectQuery();
				
				// É criada uma pool com uma thread para cada processador disponível para a extração da feature
				ExecutorService executorService = Executors.newFixedThreadPool(intNumberProcessors);
				
				while (rsSegments.next()) {
					int intInitialTime = rsSegments.getInt("time_initial");
					int intFinalTime = rsSegments.getInt("time_final");
					
					ExtractLPC objExtractLPC = new ExtractLPC(rsSegments.getInt("id_segment"), objAudioWav, intInitialTime, intFinalTime);
					executorService.execute(objExtractLPC);
					
					intRecordsExtracted++;
				}
				
				executorService.shutdown();
				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
			
		System.out.print("End LPC extraction | Registro extraídos: " + intRecordsExtracted);
		System.out.println("");
	}
	
	private static void extractLPCC() {
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		
		int intRecordsExtracted = 0;
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.id AS id_audio_file, aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_class, aud.animal_genus, aud.animal_species, aud.id ");
			
			ResultSet rsAudioFiles = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSegments;
			
			while (rsAudioFiles.next()) {
				String strAudioFilePath = PATH_AUDIO_FILES_CONVERTED + "\\" + rsAudioFiles.getString("animal_class") + "\\" + rsAudioFiles.getString("animal_genus") + " " + rsAudioFiles.getString("animal_species") +  "\\" + rsAudioFiles.getString("audio_file_path");
				
				AudioWav objAudioWav = new AudioWav(strAudioFilePath);
				objAudioWav.loadAudio();
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, seg.sound_unit, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id = ? ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
				objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, rsAudioFiles.getInt("id_audio_file"));
				
				rsSegments = objDatabaseConnection.executeSelectQuery();
				
				// É criada uma pool com uma thread para cada processador disponível para a extração da feature
				ExecutorService executorService = Executors.newFixedThreadPool(intNumberProcessors);
				
				while (rsSegments.next()) {
					int intInitialTime = rsSegments.getInt("time_initial");
					int intFinalTime = rsSegments.getInt("time_final");
					
					ExtractLPCC objExtractLPCC = new ExtractLPCC(rsSegments.getInt("id_segment"), objAudioWav, intInitialTime, intFinalTime);
					executorService.execute(objExtractLPCC);
					
					intRecordsExtracted++;
				}
				
				executorService.shutdown();
				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
			
		System.out.print("End LPCC extraction | Registro extraídos: " + intRecordsExtracted);
		System.out.println("");
	}
	
	private static void extractPLP() {
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		
		int intRecordsExtracted = 0;
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.id AS id_audio_file, aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.audio_file_path, aud.audio_file_hash, aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_class, aud.animal_genus, aud.animal_species, aud.id ");
			
			ResultSet rsAudioFiles = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSegments;
			
			while (rsAudioFiles.next()) {
				String strAudioFilePath = PATH_AUDIO_FILES_CONVERTED + "\\" + rsAudioFiles.getString("animal_class") + "\\" + rsAudioFiles.getString("animal_genus") + " " + rsAudioFiles.getString("animal_species") +  "\\" + rsAudioFiles.getString("audio_file_path");
				
				AudioWav objAudioWav = new AudioWav(strAudioFilePath);
				objAudioWav.loadAudio();
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, seg.sound_unit, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id = ? ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
				objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, rsAudioFiles.getInt("id_audio_file"));
				
				rsSegments = objDatabaseConnection.executeSelectQuery();
				
				// É criada uma pool com uma thread para cada processador disponível para a extração da feature
				ExecutorService executorService = Executors.newFixedThreadPool(intNumberProcessors);
				
				while (rsSegments.next()) {
					int intInitialTime = rsSegments.getInt("time_initial");
					int intFinalTime = rsSegments.getInt("time_final");
					
					ExtractPLP objExtractPLP = new ExtractPLP(rsSegments.getInt("id_segment"), objAudioWav, intInitialTime, intFinalTime);
					executorService.execute(objExtractPLP);
					
					intRecordsExtracted++;
				}
				
				executorService.shutdown();
				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
			
		System.out.print("End PLP extraction | Registro extraídos: " + intRecordsExtracted);
		System.out.println("");
	}
}