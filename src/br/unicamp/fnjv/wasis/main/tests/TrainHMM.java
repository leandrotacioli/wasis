package br.unicamp.fnjv.wasis.main.tests;

import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;
import br.unicamp.fnjv.wasis.libs.ExecutionTime;

public class TrainHMM {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private static int intNumberProcessors = 1;
	
	public static void main(String[] args) {
		intNumberProcessors = Runtime.getRuntime().availableProcessors();
		
		// 10 repetições para cada cada feature/fusão e classe de animal
		// utilizando diferentes base de dados em cada repetição
		
		//getExperimentRecords(LPC, 1, ALL_CLASSES);
		
		// AMPHIBIA
		trainHMM(Experiments.MFCC, Experiments.AMPHIBIA);
		trainHMM(Experiments.LPC, Experiments.AMPHIBIA);
		trainHMM(Experiments.LPCC, Experiments.AMPHIBIA);
		trainHMM(Experiments.PLP, Experiments.AMPHIBIA);
		trainHMM(Experiments.MFCC_LPC, Experiments.AMPHIBIA);
		trainHMM(Experiments.MFCC_LPCC, Experiments.AMPHIBIA);
		trainHMM(Experiments.MFCC_PLP, Experiments.AMPHIBIA);
		trainHMM(Experiments.MFCC_LPC_LPCC_PLP, Experiments.AMPHIBIA);
		
		// AVES
		trainHMM(Experiments.MFCC, Experiments.AVES);
		trainHMM(Experiments.LPC, Experiments.AVES);
		trainHMM(Experiments.LPCC, Experiments.AVES);
		trainHMM(Experiments.PLP, Experiments.AVES);
		trainHMM(Experiments.MFCC_LPC, Experiments.AVES);
		trainHMM(Experiments.MFCC_LPCC, Experiments.AVES);
		trainHMM(Experiments.MFCC_PLP, Experiments.AVES);
		trainHMM(Experiments.MFCC_LPC_LPCC_PLP, Experiments.AVES);
		
		// ***************************************************************************
		// MAMMALIA
		trainHMM(Experiments.MFCC, Experiments.MAMMALIA);
		trainHMM(Experiments.LPC, Experiments.MAMMALIA);
		trainHMM(Experiments.LPCC, Experiments.MAMMALIA);
		trainHMM(Experiments.PLP, Experiments.MAMMALIA);
		trainHMM(Experiments.MFCC_LPC, Experiments.MAMMALIA);
		trainHMM(Experiments.MFCC_LPCC, Experiments.MAMMALIA);
		trainHMM(Experiments.MFCC_PLP, Experiments.MAMMALIA);
		trainHMM(Experiments.MFCC_LPC_LPCC_PLP, Experiments.MAMMALIA);
		
		// ***************************************************************************
		// TODAS AS CLASSES
		trainHMM(Experiments.MFCC, Experiments.ALL_CLASSES);
		trainHMM(Experiments.LPC, Experiments.ALL_CLASSES);
		trainHMM(Experiments.LPCC, Experiments.ALL_CLASSES);
		trainHMM(Experiments.PLP, Experiments.ALL_CLASSES);
		trainHMM(Experiments.MFCC_LPC, Experiments.ALL_CLASSES);
		trainHMM(Experiments.MFCC_LPCC, Experiments.ALL_CLASSES);
		trainHMM(Experiments.MFCC_PLP, Experiments.ALL_CLASSES);
		trainHMM(Experiments.MFCC_LPC_LPCC_PLP, Experiments.ALL_CLASSES);
	}
	
	private static void trainHMM(int intFeature, String strAnimalClass) {
		String strFeature = "";
		
		if (intFeature == Experiments.MFCC) {
			strFeature = "MFCC";
		} else if (intFeature == Experiments.LPC) {
			strFeature = "LPC";
		} else if (intFeature == Experiments.LPCC) {
			strFeature = "LPCC";
		} else if (intFeature == Experiments.PLP) {
			strFeature = "PLP";
		} else if (intFeature == Experiments.MFCC_LPC) {
			strFeature = "MFCC-LPC";
		} else if (intFeature == Experiments.MFCC_LPCC) {
			strFeature = "MFCC-LPCC";
		} else if (intFeature == Experiments.MFCC_PLP) {
			strFeature = "MFCC-PLP";
		} else if (intFeature == Experiments.MFCC_LPC_LPCC_PLP) {
			strFeature = "MFCC-LPC-LPCC-PLP";
		}
		
		for (int intIndexExperiment = 1; intIndexExperiment <= 10; intIndexExperiment++) {
			String strExperimentName = strAnimalClass + "-" + strFeature + "-" + intIndexExperiment;
			
			String[] experimentRecords = Experiments.getExperimentRecords(intIndexExperiment, strAnimalClass);
			String strTrainingExperiment = experimentRecords[0];    // Variável para valores a serem treinados
			String strTestingExperiment = experimentRecords[1];     // Variável para valores a serem eliminados do treinamento
			
			DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
			objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
			objDatabaseConnection.openConnection();
			
			// Quantidade de repetições
			ExecutionTime objExe = new ExecutionTime();
			objExe.startExecution();
			
			int intTotalTrainedSpecies = 0;
			
			try {
				// MFCC
				if (intFeature == Experiments.MFCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.mfcc_order, fea.mfcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc    fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments         seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                  aud   ON   seg.fk_audio_file         = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTrainingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTestingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					
					if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
						objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
						objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
					}
					
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.id, fea.mfcc_order ");
					
				// LPC
				} else if (intFeature == Experiments.LPC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.lpc_order, fea.lpc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpc    fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file         = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTrainingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTestingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					
					if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
						objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
						objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
					}
					
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.id, fea.lpc_order ");
					
				// LPCC
				} else if (intFeature == Experiments.LPCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.lpcc_order, fea.lpcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpcc   fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file         = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTrainingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTestingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					
					if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
						objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
						objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
					}
					
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.id, fea.lpcc_order ");
					
				// PLP
				} else if (intFeature == Experiments.PLP) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.plp_order, fea.plp_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_plp    fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file         = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTrainingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTestingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					
					if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
						objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
						objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
					}
					
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.id, fea.plp_order ");
					
				// FUSION
				} else {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.mfcc_order, fea.mfcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc    fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments         seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                  aud   ON   seg.fk_audio_file         = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTrainingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTestingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					
					if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
						objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
						objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
					}
					
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.id, fea.mfcc_order ");
				}
				
				ResultSet rsFeatureVectors = objDatabaseConnection.executeSelectQuery();
				
				// ***************************************************************
				// Gera o Codebook
				int intTotalFeatureVectors = objDatabaseConnection.getTotalRecords();
				System.out.println("Total Feature Vectors: " + intTotalFeatureVectors);
				System.out.println("");
				
				Points points[] = new Points[intTotalFeatureVectors];
				String[] originalFeatureVector;
				double[] featureVector;
				int intIndexFeature = 0;
				
				while (rsFeatureVectors.next()) {
					originalFeatureVector = rsFeatureVectors.getString("feature_vector").split(";");
					
					featureVector = new double[originalFeatureVector.length];
					
					for (int indexElement = 0; indexElement < originalFeatureVector.length; indexElement++) {
						featureVector[indexElement] = Double.parseDouble(originalFeatureVector[indexElement]);
					}
					
					points[intIndexFeature] = new Points(featureVector);
					intIndexFeature++;
				}
				
				Codebook objCodebook = new Codebook(points);
				objCodebook.saveToFile(null, strExperimentName);
				
				// ***************************************************************
				// Treina os modelos HMM para cada espécie
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT aud.animal_genus, aud.animal_species ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments    seg ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files             aud   ON   seg.fk_audio_file = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
				
				if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
					objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
					objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
				}
				
				objDatabaseConnection.sqlCommandAppend("GROUP BY aud.animal_genus, aud.animal_species ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_genus, aud.animal_species ");
				
				ResultSet rsSpecies = objDatabaseConnection.executeSelectQuery();
				
				// É criada uma pool com uma thread para cada processador disponível para a extração da feature
				ExecutorService executorService = Executors.newFixedThreadPool(intNumberProcessors);
				
				while (rsSpecies.next()) {
					String strAnimalGenus = rsSpecies.getString("animal_genus");
					String strAnimalSpecies = rsSpecies.getString("animal_species");
					
					TrainHMMSpecies objTrainHMMSpecies = new TrainHMMSpecies(objCodebook, intFeature, strExperimentName, strTrainingExperiment, strTestingExperiment, strAnimalClass, strAnimalGenus, strAnimalSpecies);
					executorService.execute(objTrainHMMSpecies);
					
					intTotalTrainedSpecies++;
				}
				
				executorService.shutdown();
				executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
				
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				objDatabaseConnection.rollBackTransaction();
				objDatabaseConnection.closeConnection();
			}
			
			System.out.print("Experimento: " + intIndexExperiment + " | Feature: " + strFeature + " | Espécies treinadas: " + intTotalTrainedSpecies + " | Tempo total de execução:	");
			objExe.finishExecution();
		}
		
		System.out.println("");
		System.out.println("---------------------------------------------------------------------");
	}
}