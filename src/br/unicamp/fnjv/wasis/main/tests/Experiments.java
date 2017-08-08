package br.unicamp.fnjv.wasis.main.tests;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;
import br.unicamp.fnjv.wasis.libs.ExecutionTime;

public class Experiments {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private final int[] TRAIN_EXPERIMENT_1 = new int[]{2,3,4,6,8,9,10};
	private final int[] TEST_EXPERIMENT_1 = new int[]{1,5,7};
	
	private final int[] TRAIN_EXPERIMENT_2 = new int[]{1,3,4,6,7,9,10};
	private final int[] TEST_EXPERIMENT_2 = new int[]{2,5,8};
	
	private final int[] TRAIN_EXPERIMENT_3 = new int[]{1,2,5,6,7,8,10};
	private final int[] TEST_EXPERIMENT_3 = new int[]{3,4,9};
	
	private final int[] TRAIN_EXPERIMENT_4 = new int[]{2,3,5,6,7,9,10};
	private final int[] TEST_EXPERIMENT_4 = new int[]{1,4,8};
	
	private final int[] TRAIN_EXPERIMENT_5 = new int[]{1,2,3,4,7,8,9};
	private final int[] TEST_EXPERIMENT_5 = new int[]{5,6,10};
	
	private final int[] TRAIN_EXPERIMENT_6 = new int[]{1,2,4,5,7,8,10};
	private final int[] TEST_EXPERIMENT_6 = new int[]{3,6,9};
	
	private final int[] TRAIN_EXPERIMENT_7 = new int[]{3,4,5,6,8,9,10};
	private final int[] TEST_EXPERIMENT_7 = new int[]{1,2,7};
	
	private final int[] TRAIN_EXPERIMENT_8 = new int[]{1,2,3,4,5,7,9};
	private final int[] TEST_EXPERIMENT_8 = new int[]{6,8,10};
	
	private final int[] TRAIN_EXPERIMENT_9 = new int[]{1,2,4,5,6,8,10};
	private final int[] TEST_EXPERIMENT_9 = new int[]{3,7,9};
	
	private final int[] TRAIN_EXPERIMENT_10 = new int[]{1,3,5,6,7,8,9};
	private final int[] TEST_EXPERIMENT_10 = new int[]{2,4,10};
	
	public final static int MFCC = 1;
	public final static int LPC = 2;
	public final static int LPCC = 3;
	public final static int PLP = 4;
	public final static int PS = 5;
	public final static int MFCC_LPC = 12;
	public final static int MFCC_LPCC = 13;
	public final static int MFCC_PLP = 14;
	public final static int MFCC_LPC_LPCC_PLP = 1234;
	
	public final static String AMPHIBIA = "Amphibia";
	public final static String AVES = "Aves";
	public final static String MAMMALIA = "Mammalia";
	public final static String ALL_CLASSES = "All Classes";
	
	public final static int PEARSON = 1000;
	public final static int HMM = 1001;
	
	protected Experiments() {
		
	}
	
	protected int[] getTrainExperiment(int intIndexExperiment) {
		if (intIndexExperiment == 1) {
			return TRAIN_EXPERIMENT_1;
		} else if (intIndexExperiment == 2) {
			return TRAIN_EXPERIMENT_2;
		} else if (intIndexExperiment == 3) {
			return TRAIN_EXPERIMENT_3;
		} else if (intIndexExperiment == 4) {
			return TRAIN_EXPERIMENT_4;
		} else if (intIndexExperiment == 5) {
			return TRAIN_EXPERIMENT_5;
		} else if (intIndexExperiment == 6) {
			return TRAIN_EXPERIMENT_6;
		} else if (intIndexExperiment == 7) {
			return TRAIN_EXPERIMENT_7;
		} else if (intIndexExperiment == 8) {
			return TRAIN_EXPERIMENT_8;
		} else if (intIndexExperiment == 9) {
			return TRAIN_EXPERIMENT_9;
		} else if (intIndexExperiment == 10) {
			return TRAIN_EXPERIMENT_10;
		}
		
		return null;
	}
	
	protected int[] getTestExperiment(int intIndexExperiment) {
		if (intIndexExperiment == 1) {
			return TEST_EXPERIMENT_1;
		} else if (intIndexExperiment == 2) {
			return TEST_EXPERIMENT_2;
		} else if (intIndexExperiment == 3) {
			return TEST_EXPERIMENT_3;
		} else if (intIndexExperiment == 4) {
			return TEST_EXPERIMENT_4;
		} else if (intIndexExperiment == 5) {
			return TEST_EXPERIMENT_5;
		} else if (intIndexExperiment == 6) {
			return TEST_EXPERIMENT_6;
		} else if (intIndexExperiment == 7) {
			return TEST_EXPERIMENT_7;
		} else if (intIndexExperiment == 8) {
			return TEST_EXPERIMENT_8;
		} else if (intIndexExperiment == 9) {
			return TEST_EXPERIMENT_9;
		} else if (intIndexExperiment == 10) {
			return TEST_EXPERIMENT_10;
		}
		
		return null;
	}
	
	/**
	 * 
	 * @param intFeature
	 * @param intIndexExperiment
	 * @param strAnimalClass
	 * 
	 * @return experimentRecords[0] = strTrainingExperiment<br>
	 *         experimentRecords[1] = strTestingExperiment
	 */
	public static String[] getExperimentRecords(int intIndexExperiment, String strAnimalClass) {
		String[] experimentRecords = new String[2];
		String strTrainingExperiment = null;  // Variável para valores a serem treinados
		String strTestingExperiment = null;   // Variável para valores a serem eliminados do treinamento
		
		Experiments objExperiments = new Experiments();
		int[] trainingExperiment = objExperiments.getTrainExperiment(intIndexExperiment);
		int[] testingExperiment = objExperiments.getTestExperiment(intIndexExperiment);
		
		//int intTotalTrainingExperiment = 0;
		//int intTotalTestingExperiment = 0;
		
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		objDatabaseConnection.openConnection();
		
		ExecutionTime objExe = new ExecutionTime();
		objExe.startExecution();
		
		try {
			// Filtra as espécies que tem ROIs e que farão parte do treinamento
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM audio_files_segments_mfcc    fea ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   fea.fk_audio_file_segment = seg.id ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files            aud   ON   seg.fk_audio_file         = aud.id ");
			objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
			
			if (!strAnimalClass.equals(ALL_CLASSES)) {
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
				objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
			}
			
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_genus, aud.animal_species ");
			
			ResultSet rsSpecies = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSpeciesFiles;
			
			while (rsSpecies.next()) {
				int intIndexFiles = 1;  // Índice da consulta
				
				objDatabaseConnection.initiliazeStatement();
				
				// Pega os registros que farão parte do treinamento e teste
				objDatabaseConnection.sqlCommand("SELECT aud.id AS id_audio_file ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc    fea ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments         seg   ON   fea.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                  aud   ON   seg.fk_audio_file         = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
				
				if (!strAnimalClass.equals(ALL_CLASSES)) {
					objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
					objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
				}
				
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_genus = ? ");
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_species = ? ");
				objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
				
				objDatabaseConnection.sqlCommandAppend("GROUP BY aud.id ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY aud.id ");
				objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, rsSpecies.getString("animal_genus"));
				objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, rsSpecies.getString("animal_species"));
				
				rsSpeciesFiles = objDatabaseConnection.executeSelectQuery();
				
				// Armazena o ID dos arquivos que não farão parte do treinamento (= arquivos para testes)
				while (rsSpeciesFiles.next()) {
					// Loop através do vetor de arquivos de treinamento
					for (int indexTrainingExperiment = 0; indexTrainingExperiment < trainingExperiment.length; indexTrainingExperiment++) {
						// Se o indice da consulta for o mesmo que o do treinamento do experimento,
						// armazena o valor numa variável para ser adicionado em uma próxima consulta
						if (intIndexFiles == trainingExperiment[indexTrainingExperiment]) {
							if (strTrainingExperiment == null || strTrainingExperiment.length() == 0) {
								strTrainingExperiment = rsSpeciesFiles.getString("id_audio_file");
							} else {
								strTrainingExperiment = strTrainingExperiment + "," + rsSpeciesFiles.getString("id_audio_file");
							}
							
							//intTotalTrainingExperiment++;
							
							break;
						}
					}
					
					// Loop através do vetor de arquivos de testes
					for (int indexTestingExperiment = 0; indexTestingExperiment < testingExperiment.length; indexTestingExperiment++) {
						// Se o indice da consulta for o mesmo que o do teste do experimento,
						// armazena o valor numa variável para ser eliminado em uma próxima consulta
						if (intIndexFiles == testingExperiment[indexTestingExperiment]) {
							if (strTestingExperiment == null || strTestingExperiment.length() == 0) {
								strTestingExperiment = rsSpeciesFiles.getString("id_audio_file");
							} else {
								strTestingExperiment = strTestingExperiment + "," + rsSpeciesFiles.getString("id_audio_file");
							}
							
							//intTotalTestingExperiment++;
							
							break;
						}
					}
					
					intIndexFiles++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		experimentRecords[0] = strTrainingExperiment;
		experimentRecords[1] = strTestingExperiment;
		
		//System.out.println("Treinamento: " + intTotalTrainingExperiment + "	" + strTrainingExperiment);
		//System.out.println("Teste: " + intTotalTestingExperiment + "	" + strTestingExperiment);
		//System.out.println("");
		
		return experimentRecords;
	}
	
	/**
	 * 
	 * @param intFeature
	 * @param intIndexExperiment
	 * @param strAnimalClass
	 * 
	 * @return experimentRecords[0] = strTrainingExperiment<br>
	 *         experimentRecords[1] = strTestingExperiment
	 */
	public static String[] getExperimentRecordsOrder(int intIndexExperiment, String strAnimalClass) {
		String[] experimentRecords = new String[2];
		String strTrainingExperiment = null;  // Variável para valores a serem treinados
		String strTestingExperiment = null;   // Variável para valores a serem eliminados do treinamento
		
		Experiments objExperiments = new Experiments();
		int[] trainingExperiment = objExperiments.getTrainExperiment(intIndexExperiment);
		int[] testingExperiment = objExperiments.getTestExperiment(intIndexExperiment);
		
		List<Integer> lstTraining = new ArrayList<Integer>(); 
		List<Integer> lstTesting = new ArrayList<Integer>();

		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		objDatabaseConnection.openConnection();
		
		ExecutionTime objExe = new ExecutionTime();
		objExe.startExecution();
		
		try {
			// Filtra as espécies que tem ROIs e que farão parte do treinamento
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM audio_files_segments    seg ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files            aud   ON   seg.fk_audio_file         = aud.id ");
			objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
			
			if (!strAnimalClass.equals(ALL_CLASSES)) {
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
				objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
			}
			
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_genus, aud.animal_species ");
			
			ResultSet rsSpecies = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSpeciesFiles;
			
			while (rsSpecies.next()) {
				int intIndexFiles = 1;  // Índice da consulta
				
				objDatabaseConnection.initiliazeStatement();
				
				// Pega os registros que farão parte do treinamento e teste
				objDatabaseConnection.sqlCommand("SELECT aud.id AS id_audio_file ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments         seg ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                  aud   ON   seg.fk_audio_file         = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
				
				if (!strAnimalClass.equals(ALL_CLASSES)) {
					objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
					objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
				}
				
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_genus = ? ");
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_species = ? ");
				objDatabaseConnection.sqlCommandAppend("GROUP BY aud.id ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY aud.id ");
				objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, rsSpecies.getString("animal_genus"));
				objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, rsSpecies.getString("animal_species"));
				
				rsSpeciesFiles = objDatabaseConnection.executeSelectQuery();
				
				// Armazena o ID dos arquivos que não farão parte do treinamento (= arquivos para testes)
				while (rsSpeciesFiles.next()) {
					// Loop através do vetor de arquivos de treinamento
					for (int indexTrainingExperiment = 0; indexTrainingExperiment < trainingExperiment.length; indexTrainingExperiment++) {
						// Se o indice da consulta for o mesmo que o do treinamento do experimento,
						// armazena o valor numa variável para ser adicionado em uma próxima consulta
						if (intIndexFiles == trainingExperiment[indexTrainingExperiment]) {
							lstTraining.add(Integer.parseInt(rsSpeciesFiles.getString("id_audio_file")));

							break;
						}
					}
					
					// Loop através do vetor de arquivos de testes
					for (int indexTestingExperiment = 0; indexTestingExperiment < testingExperiment.length; indexTestingExperiment++) {
						// Se o indice da consulta for o mesmo que o do teste do experimento,
						// armazena o valor numa variável para ser eliminado em uma próxima consulta
						if (intIndexFiles == testingExperiment[indexTestingExperiment]) {
							lstTesting.add(Integer.parseInt(rsSpeciesFiles.getString("id_audio_file")));
							
							break;
						}
					}
					
					intIndexFiles++;
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		Collections.sort(lstTraining);
		Collections.sort(lstTesting);
		
		for (int i = 0; i < lstTraining.size(); i++) {
			if (strTrainingExperiment == null || strTrainingExperiment.length() == 0) {
				strTrainingExperiment = String.format("%07d", lstTraining.get(i));
			} else {
				strTrainingExperiment = strTrainingExperiment + ", " + String.format("%07d", lstTraining.get(i));
			}
		}
		
		for (int i = 0; i < lstTesting.size(); i++) {
			if (strTestingExperiment == null || strTestingExperiment.length() == 0) {
				strTestingExperiment = String.format("%07d", lstTesting.get(i));
			} else {
				strTestingExperiment = strTestingExperiment + ", " + String.format("%07d", lstTesting.get(i));
			}
		}
		
		experimentRecords[0] = strTrainingExperiment;
		experimentRecords[1] = strTestingExperiment;
		
		//System.out.println("Treinamento: " + intTotalTrainingExperiment + "	" + strTrainingExperiment);
		//System.out.println("Teste: " + intTotalTestingExperiment + "	" + strTestingExperiment);
		//System.out.println("");
		
		return experimentRecords;
	}
}