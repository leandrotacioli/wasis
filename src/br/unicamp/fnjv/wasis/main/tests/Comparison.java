package br.unicamp.fnjv.wasis.main.tests;

import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.sql.ResultSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;
import br.unicamp.fnjv.wasis.libs.ExecutionTime;

public class Comparison {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private static int intTotalExperiments = 10;
	private static int intNumberProcessors = 1;
	
	private static String[] trainingExperiment;
	private static String[] testingExperiment;
	
	public static void main(String[] args) {
		trainingExperiment = new String[11];
		testingExperiment = new String[11];
		
		intNumberProcessors = Runtime.getRuntime().availableProcessors();

		// Amphibia
		{
			for (int indexExperiment = 1; indexExperiment <= intTotalExperiments; indexExperiment++) {
				String[] experimentRecords = Experiments.getExperimentRecords(indexExperiment, Experiments.AMPHIBIA);
				trainingExperiment[indexExperiment] = experimentRecords[0];  // Variável para valores a serem treinados
				testingExperiment[indexExperiment] = experimentRecords[1];   // Variável para valores a serem eliminados do treinamento
			}
		
			intNumberProcessors = Runtime.getRuntime().availableProcessors() - 2;
			compare(Experiments.PEARSON, Experiments.PS, Experiments.AMPHIBIA);
			
			intNumberProcessors = 1;
			compare(Experiments.PEARSON, Experiments.MFCC, Experiments.AMPHIBIA);
			compare(Experiments.PEARSON, Experiments.LPC, Experiments.AMPHIBIA);
			compare(Experiments.PEARSON, Experiments.LPCC, Experiments.AMPHIBIA);
			compare(Experiments.PEARSON, Experiments.PLP, Experiments.AMPHIBIA);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.AMPHIBIA);
			compare(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.AMPHIBIA);
			compare(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.AMPHIBIA);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AMPHIBIA);
			
			intNumberProcessors = Runtime.getRuntime().availableProcessors();
			compare(Experiments.HMM, Experiments.MFCC, Experiments.AMPHIBIA);
			compare(Experiments.HMM, Experiments.LPC, Experiments.AMPHIBIA);
			compare(Experiments.HMM, Experiments.LPCC, Experiments.AMPHIBIA);
			compare(Experiments.HMM, Experiments.PLP, Experiments.AMPHIBIA);
			compare(Experiments.HMM, Experiments.MFCC_LPC, Experiments.AMPHIBIA);
			compare(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.AMPHIBIA);
			compare(Experiments.HMM, Experiments.MFCC_PLP, Experiments.AMPHIBIA);
			compare(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AMPHIBIA);
		}
		
		System.out.println("--------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------");
		
		// Aves
		{
			for (int indexExperiment = 1; indexExperiment <= intTotalExperiments; indexExperiment++) {
				String[] experimentRecords = Experiments.getExperimentRecords(indexExperiment, Experiments.AVES);
				trainingExperiment[indexExperiment] = experimentRecords[0];  // Variável para valores a serem treinados
				testingExperiment[indexExperiment] = experimentRecords[1];   // Variável para valores a serem eliminados do treinamento
			}
			
			intNumberProcessors = Runtime.getRuntime().availableProcessors() - 2;
			compare(Experiments.PEARSON, Experiments.PS, Experiments.AVES);
			
			intNumberProcessors = 1;
			compare(Experiments.PEARSON, Experiments.MFCC, Experiments.AVES);
			compare(Experiments.PEARSON, Experiments.LPC, Experiments.AVES);
			compare(Experiments.PEARSON, Experiments.LPCC, Experiments.AVES);
			compare(Experiments.PEARSON, Experiments.PLP, Experiments.AVES);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.AVES);
			compare(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.AVES);
			compare(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.AVES);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AVES);
			
			intNumberProcessors = Runtime.getRuntime().availableProcessors();
			compare(Experiments.HMM, Experiments.MFCC, Experiments.AVES);
			compare(Experiments.HMM, Experiments.LPC, Experiments.AVES);
			compare(Experiments.HMM, Experiments.LPCC, Experiments.AVES);
			compare(Experiments.HMM, Experiments.PLP, Experiments.AVES);
			compare(Experiments.HMM, Experiments.MFCC_LPC, Experiments.AVES);
			compare(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.AVES);
			compare(Experiments.HMM, Experiments.MFCC_PLP, Experiments.AVES);
			compare(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AVES);
		}
		
		System.out.println("--------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------");
		
		// Mammalia
		{
			for (int indexExperiment = 1; indexExperiment <= intTotalExperiments; indexExperiment++) {
				String[] experimentRecords = Experiments.getExperimentRecords(indexExperiment, Experiments.MAMMALIA);
				trainingExperiment[indexExperiment] = experimentRecords[0];  // Variável para valores a serem treinados
				testingExperiment[indexExperiment] = experimentRecords[1];   // Variável para valores a serem eliminados do treinamento
			}
			
			intNumberProcessors = Runtime.getRuntime().availableProcessors() - 2;
			compare(Experiments.PEARSON, Experiments.PS, Experiments.MAMMALIA);
			
			intNumberProcessors = 1;
			compare(Experiments.PEARSON, Experiments.MFCC, Experiments.MAMMALIA);
			compare(Experiments.PEARSON, Experiments.LPC, Experiments.MAMMALIA);
			compare(Experiments.PEARSON, Experiments.LPCC, Experiments.MAMMALIA);
			compare(Experiments.PEARSON, Experiments.PLP, Experiments.MAMMALIA);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.MAMMALIA);
			compare(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.MAMMALIA);
			compare(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.MAMMALIA);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.MAMMALIA);
			
			intNumberProcessors = Runtime.getRuntime().availableProcessors();
			compare(Experiments.HMM, Experiments.MFCC, Experiments.MAMMALIA);
			compare(Experiments.HMM, Experiments.LPC, Experiments.MAMMALIA);
			compare(Experiments.HMM, Experiments.LPCC, Experiments.MAMMALIA);
			compare(Experiments.HMM, Experiments.PLP, Experiments.MAMMALIA);
			compare(Experiments.HMM, Experiments.MFCC_LPC, Experiments.MAMMALIA);
			compare(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.MAMMALIA);
			compare(Experiments.HMM, Experiments.MFCC_PLP, Experiments.MAMMALIA);
			compare(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.MAMMALIA);
		}
		
		System.out.println("--------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------");
		System.out.println("--------------------------------------------------------------");
		
		// All Classes
		{
			for (int indexExperiment = 1; indexExperiment <= intTotalExperiments; indexExperiment++) {
				String[] experimentRecords = Experiments.getExperimentRecords(indexExperiment, Experiments.ALL_CLASSES);
				trainingExperiment[indexExperiment] = experimentRecords[0];  // Variável para valores a serem treinados
				testingExperiment[indexExperiment] = experimentRecords[1];   // Variável para valores a serem eliminados do treinamento
			}
			
			intNumberProcessors = Runtime.getRuntime().availableProcessors() - 2;
			compare(Experiments.PEARSON, Experiments.PS, Experiments.ALL_CLASSES);
			
			intNumberProcessors = 1;
			compare(Experiments.PEARSON, Experiments.MFCC, Experiments.ALL_CLASSES);
			compare(Experiments.PEARSON, Experiments.LPC, Experiments.ALL_CLASSES);
			compare(Experiments.PEARSON, Experiments.LPCC, Experiments.ALL_CLASSES);
			compare(Experiments.PEARSON, Experiments.PLP, Experiments.ALL_CLASSES);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.ALL_CLASSES);
			compare(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.ALL_CLASSES);
			compare(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.ALL_CLASSES);
			compare(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.ALL_CLASSES);
			
			intNumberProcessors = Runtime.getRuntime().availableProcessors();
			compare(Experiments.HMM, Experiments.MFCC, Experiments.ALL_CLASSES);
			compare(Experiments.HMM, Experiments.LPC, Experiments.ALL_CLASSES);
			compare(Experiments.HMM, Experiments.LPCC, Experiments.ALL_CLASSES);
			compare(Experiments.HMM, Experiments.PLP, Experiments.ALL_CLASSES);
			compare(Experiments.HMM, Experiments.MFCC_LPC, Experiments.ALL_CLASSES);
			compare(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.ALL_CLASSES);
			compare(Experiments.HMM, Experiments.MFCC_PLP, Experiments.ALL_CLASSES);
			compare(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.ALL_CLASSES);
		}
	}
	
	private static void compare(int intClassifier, int intFeature, String strAnimalClass) {
		String strClassifier = "";
		String strFeature = "";
		
		if (intClassifier == Experiments.PEARSON) {
			strClassifier = "Pearson";
		} else if (intClassifier == Experiments.HMM) {
			strClassifier = "HMM";
		}
		
		if (intFeature == Experiments.MFCC) {
			strFeature = "MFCC";
		} else if (intFeature == Experiments.LPC) {
			strFeature = "LPC";
		} else if (intFeature == Experiments.LPCC) {
			strFeature = "LPCC";
		} else if (intFeature == Experiments.PLP) {
			strFeature = "PLP";
		} else if (intFeature == Experiments.PS) {
			strFeature = "PS";
		} else if (intFeature == Experiments.MFCC_LPC) {
			strFeature = "MFCC-LPC";
		} else if (intFeature == Experiments.MFCC_LPCC) {
			strFeature = "MFCC-LPCC";
		} else if (intFeature == Experiments.MFCC_PLP) {
			strFeature = "MFCC-PLP";
		} else if (intFeature == Experiments.MFCC_LPC_LPCC_PLP) {
			strFeature = "MFCC-LPC-LPCC-PLP";
		}
		
		Writer writer = null;
		
		ResultSet rsSpeciesSegmentsComparison = null;
		ResultSet rsSpecies;
		
		DatabaseMySQLConnection objDatabaseConnection;
		
		ExecutionTime objExe;
		
		ExecutorService executorService;
		
		for (int intIndexExperiment = 1; intIndexExperiment <= intTotalExperiments; intIndexExperiment++) {
			String strExperimentName = strAnimalClass + "-" + strFeature + "-" + intIndexExperiment;
			
			System.out.println("Nº Experimento: " + intIndexExperiment + " | Tipo: " + strAnimalClass + " | Classificação: " + strClassifier + " | Feature: " + strFeature);
			
			try {
				writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("results\\" + strClassifier + "-" + strExperimentName + ".txt"), "utf-8"));
				
				objDatabaseConnection = new DatabaseMySQLConnection();
				objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
				objDatabaseConnection.openConnection();
				
				objExe = new ExecutionTime();
				objExe.startExecution();
				
				int intTotalComparedSpecies = 0;
				
				try {
					// Loop através dos segmentos do experimento de comparação
					if (intClassifier == Experiments.PEARSON) {
						if (intFeature == Experiments.MFCC) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, mfcc.mfcc_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
							
						} else if (intFeature == Experiments.LPC) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, lpc.lpc_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpc    lpc ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   lpc.fk_audio_file_segment = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
							
						} else if (intFeature == Experiments.LPCC) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, lpcc.lpcc_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpcc   lpcc ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   lpcc.fk_audio_file_segment = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
							
						} else if (intFeature == Experiments.PLP) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, plp.plp_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_plp    plp ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   plp.fk_audio_file_segment  = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
							
						} else if (intFeature == Experiments.MFCC_LPC) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, ");
							objDatabaseConnection.sqlCommandAppend("CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector) AS mfcc_lpc_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpc    lpc    ON   lpc.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpc.lpc_order ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
							objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
							
						} else if (intFeature == Experiments.MFCC_LPCC) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, ");
							objDatabaseConnection.sqlCommandAppend("CONCAT(mfcc.mfcc_vector, ';', lpcc.lpcc_vector) AS mfcc_lpcc_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpcc   lpcc   ON   lpcc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpcc.lpcc_order ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
							objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
							
						} else if (intFeature == Experiments.MFCC_PLP) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, ");
							objDatabaseConnection.sqlCommandAppend("CONCAT(mfcc.mfcc_vector, ';', plp.plp_vector) AS mfcc_plp_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_plp    plp    ON   plp.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = plp.plp_order ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
							objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
							
						} else if (intFeature == Experiments.MFCC_LPC_LPCC_PLP) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("SELECT aud.id as id_audio_file, aud.animal_class, aud.animal_genus, aud.animal_species, seg.id AS id_segment, ");
							objDatabaseConnection.sqlCommandAppend("CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector, ';', lpcc.lpcc_vector, ';', plp.plp_vector) AS mfcc_lpc_lpcc_plp_vector ");
							objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpc    lpc    ON   lpc.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpcc   lpcc   ON   lpcc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_plp    plp    ON   plp.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
							objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
							objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpc.lpc_order ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpcc.lpcc_order ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = plp.plp_order ");
							objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
							objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 1 ");
							objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 1 ");
							objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 1 ");
							
							if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
								objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
								objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
							}
							
							objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + trainingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + testingExperiment[intIndexExperiment] + ") ");
							objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
							
							rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
						}
					}
					
					// ***************************************************************
					// Compara os dados das espécies
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
					
					rsSpecies = objDatabaseConnection.executeSelectQuery();
					
					// É criada uma pool com uma thread para cada processador disponível para a extração da feature
					executorService = Executors.newFixedThreadPool(intNumberProcessors);
					
					while (rsSpecies.next()) {
						String strAnimalGenus = rsSpecies.getString("animal_genus");
						String strAnimalSpecies = rsSpecies.getString("animal_species");
						
						// Pearson
						if (intClassifier == Experiments.PEARSON) {
							ComparisonBruteForce objComparisonBruteForce = new ComparisonBruteForce(writer, intFeature, rsSpeciesSegmentsComparison, trainingExperiment[intIndexExperiment], testingExperiment[intIndexExperiment], strAnimalClass, strAnimalGenus, strAnimalSpecies);
							executorService.execute(objComparisonBruteForce);
						
						// HMM
						} else if (intClassifier == Experiments.HMM) {
							ComparisonClassModel objComparisonClassModel = new ComparisonClassModel(writer, strExperimentName, intFeature, trainingExperiment[intIndexExperiment], testingExperiment[intIndexExperiment], strAnimalClass, strAnimalGenus, strAnimalSpecies);
							executorService.execute(objComparisonClassModel);
						}
						
						intTotalComparedSpecies++;
					}
					
					executorService.shutdown();
					executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
					
				} catch (Exception e) {
					e.printStackTrace();
					
				} finally {
					objDatabaseConnection.rollBackTransaction();
					objDatabaseConnection.closeConnection();
				}
				
				System.out.print(" | Espécies de " + strAnimalClass + " comparadas: " + intTotalComparedSpecies + " | Tempo total de execução:	");
				objExe.finishExecution();
				
			} catch (UnsupportedEncodingException | FileNotFoundException e1) {
				e1.printStackTrace();
			
			} finally {
				try {
					writer.close();
				} catch (Exception ex) {
					
				}
			}
		}
		
		System.out.println("---------------------------------------------------------------");
	}
}