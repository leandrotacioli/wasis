package br.unicamp.fnjv.wasis.main.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.libs.ExecutionTime;
import br.unicamp.fnjv.wasis.libs.RoundNumbers;
import br.unicamp.fnjv.wasis.libs.Statistics;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

public class ReadComparison {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private static String FILE_PATH = "C:\\Users\\Leandro\\Dropbox\\UNICAMP\\Mestrado\\Resultados";
	private static int intTotalExperiments = 10;
	
	public static void main(String[] args) {
		//getSegments();
		
		/*
		String[] trainingExperiment = new String[11];
		String[] testingExperiment = new String[11];
		
		System.out.println("Experiment: Primates");
		for (int indexExperiment = 1; indexExperiment <= 10; indexExperiment++) {
			String[] experimentRecords = Experiments.getExperimentRecordsOrder(indexExperiment, Experiments.ALL_CLASSES);
			trainingExperiment[indexExperiment] = experimentRecords[0];  // Variável para valores a serem treinados
			testingExperiment[indexExperiment] = experimentRecords[1];   // Variável para valores a serem eliminados do treinamento
			
			//System.out.println("Dataset: " + indexExperiment);
			System.out.println(indexExperiment + "Training Set	" + trainingExperiment[indexExperiment]);
			System.out.println("Evaluation Set	" + testingExperiment[indexExperiment]);
			//System.out.println("");
		}
		*/
		
		//readFile(Experiments.PEARSON, Experiments.PS, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.MFCC, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.LPC, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.LPCC, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.PLP, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.AMPHIBIA);
		//readFile(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AMPHIBIA);
		
		//readFile(Experiments.PEARSON, Experiments.PS, Experiments.ALL_CLASSES);
		
		/*
		int intTotalTempo = 4888859;
		int intTotalTempoAves = 3180124;
		int intTotalTempoAmphibia = 839444;
		int intTotalTempoMammalia = 869291;
		
		System.out.println("Total: " + ClockTransformations.millisecondsIntoDigitalFormat(intTotalTempo));
		System.out.println("Aves: " + ClockTransformations.millisecondsIntoDigitalFormat(intTotalTempoAves));
		System.out.println("Amphibia: " + ClockTransformations.millisecondsIntoDigitalFormat(intTotalTempoAmphibia));
		System.out.println("Mammalia: " + ClockTransformations.millisecondsIntoDigitalFormat(intTotalTempoMammalia));
		*/
		/*
		System.out.println("------------------------------------------");
		System.out.println("---------------- Amphibia ----------------");
		readFile(Experiments.PEARSON, Experiments.PS, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.MFCC, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.LPC, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.LPCC, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.PLP, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.AMPHIBIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AMPHIBIA);
		*/
		//readFile(Experiments.HMM, Experiments.MFCC, Experiments.AMPHIBIA);
		//readFile(Experiments.HMM, Experiments.LPC, Experiments.AMPHIBIA);
		//readFile(Experiments.HMM, Experiments.LPCC, Experiments.AMPHIBIA);
		//readFile(Experiments.HMM, Experiments.PLP, Experiments.AMPHIBIA);
		//readFile(Experiments.HMM, Experiments.MFCC_LPC, Experiments.AMPHIBIA);
		//readFile(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.AMPHIBIA);
		//readFile(Experiments.HMM, Experiments.MFCC_PLP, Experiments.AMPHIBIA);
		//readFile(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AMPHIBIA);
		
		/*
		System.out.println("------------------------------------------");
		System.out.println("------------------ Aves ------------------");
		
		readFile(Experiments.PEARSON, Experiments.PS, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.MFCC, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.LPC, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.LPCC, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.PLP, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.AVES);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AVES);
		
		readFile(Experiments.HMM, Experiments.MFCC, Experiments.AVES);
		readFile(Experiments.HMM, Experiments.LPC, Experiments.AVES);
		readFile(Experiments.HMM, Experiments.LPCC, Experiments.AVES);
		readFile(Experiments.HMM, Experiments.PLP, Experiments.AVES);
		readFile(Experiments.HMM, Experiments.MFCC_LPC, Experiments.AVES);
		readFile(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.AVES);
		readFile(Experiments.HMM, Experiments.MFCC_PLP, Experiments.AVES);
		readFile(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AVES);
		*/
		/*
		System.out.println("------------------------------------------");
		System.out.println("---------------- Mammalia ----------------");
		
		readFile(Experiments.PEARSON, Experiments.PS, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.MFCC, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.LPC, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.LPCC, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.PLP, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.MAMMALIA);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.MAMMALIA);
		
		readFile(Experiments.HMM, Experiments.MFCC, Experiments.MAMMALIA);
		readFile(Experiments.HMM, Experiments.LPC, Experiments.MAMMALIA);
		readFile(Experiments.HMM, Experiments.LPCC, Experiments.MAMMALIA);
		readFile(Experiments.HMM, Experiments.PLP, Experiments.MAMMALIA);
		readFile(Experiments.HMM, Experiments.MFCC_LPC, Experiments.MAMMALIA);
		readFile(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.MAMMALIA);
		readFile(Experiments.HMM, Experiments.MFCC_PLP, Experiments.MAMMALIA);
		readFile(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.MAMMALIA);
		*/
		
		/*
		System.out.println("---------------------------------------------");
		System.out.println("---------------- All Classes ----------------");
		
		readFile(Experiments.PEARSON, Experiments.PS, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.MFCC, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.LPC, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.LPCC, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.PLP, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPCC, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.MFCC_PLP, Experiments.ALL_CLASSES);
		readFile(Experiments.PEARSON, Experiments.MFCC_LPC_LPCC_PLP, Experiments.ALL_CLASSES);
		
		readFile(Experiments.HMM, Experiments.MFCC, Experiments.ALL_CLASSES);
		readFile(Experiments.HMM, Experiments.LPC, Experiments.ALL_CLASSES);
		readFile(Experiments.HMM, Experiments.LPCC, Experiments.ALL_CLASSES);
		readFile(Experiments.HMM, Experiments.PLP, Experiments.ALL_CLASSES);
		readFile(Experiments.HMM, Experiments.MFCC_LPC, Experiments.ALL_CLASSES);
		readFile(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.ALL_CLASSES);
		readFile(Experiments.HMM, Experiments.MFCC_PLP, Experiments.ALL_CLASSES);
		readFile(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.ALL_CLASSES);
		*/
	}
	
	// Fazer precision e recall para cada espécie do arquivo
	
	private static void readFile(int intClassifier, int intFeature, String strAnimalClass) {
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
		
		BufferedReader bufferedReader = null;
		
		List<ReadComparisonValues> lstRecords;
		List<SpeciesValues> lstSpecies = new ArrayList<SpeciesValues>();
		
		String strLine;
		String[] lineValues;
		
		String strSpecies = "";
    	int intSegment = 0;
    	boolean blnFirst = false;
    	boolean blnThird = false;
    	String strSpeciesIdentifiedFirst = "";
    	String strSpeciesIdentifiedSecond = "";
    	String strSpeciesIdentifiedThird = "";
    	
    	double dblTPRFirst = 0;
    	
    	int intTotalRecords = 0;
    	int intTotalPositiveFirst = 0;
		
    	System.out.println(strClassifier + "-" + strAnimalClass + "-" + strFeature);
    	
		for (int intIndexExperiment = 1; intIndexExperiment <= intTotalExperiments; intIndexExperiment++) {
			String strExperimentName = strClassifier + "-" + strAnimalClass + "-" + strFeature + "-" + intIndexExperiment;
			String strExperimentFile = FILE_PATH + "\\" + strAnimalClass + "\\" + strExperimentName + ".txt";
			
			//System.out.println("Arquivo: " + strExperimentFile);
			//System.out.print("Nº Experimento: " + intIndexExperiment + " | Tipo: " + strAnimalClass + " | Classificação: " + strClassifier + " | Feature: " + strFeature);
			
			intTotalRecords = 0;
	    	intTotalPositiveFirst = 0;
			
			try {
				bufferedReader = new BufferedReader(new FileReader(strExperimentFile));
				
				lstRecords = new ArrayList<ReadComparisonValues>();
				
			    strLine = bufferedReader.readLine();
			    
			    while (strLine != null) {
			    	lineValues = strLine.split("	");
			    	
			    	strSpecies = lineValues[0];
			    	intSegment = Integer.parseInt(lineValues[1]);
			    	blnFirst = Boolean.parseBoolean(lineValues[2]);
			    	blnThird = Boolean.parseBoolean(lineValues[3]);
			    	strSpeciesIdentifiedFirst = lineValues[4];
			    	strSpeciesIdentifiedSecond = lineValues[5];
			    	strSpeciesIdentifiedThird = lineValues[6];
			    	
			    	lstRecords.add(new ReadComparisonValues(strSpecies, intSegment, blnFirst, blnThird, strSpeciesIdentifiedFirst, strSpeciesIdentifiedSecond, strSpeciesIdentifiedThird));
			    	
			    	// Cria uma lista com as espécies existentes
			    	boolean blnSpeciesAlreadySaved = false;
			    	for (int indexSpecies = 0; indexSpecies < lstSpecies.size(); indexSpecies++) {
			    		if (lstSpecies.get(indexSpecies).getSpecies().equals(strSpecies)) {
			    			blnSpeciesAlreadySaved = true;
			    			
			    			break;
			    		}
			    	}
			    	
			    	if (!blnSpeciesAlreadySaved) {
			    		lstSpecies.add(new SpeciesValues(strSpecies));
			    	}
			    	
			    	// Lê a próxima linha do arquivo
			    	strLine = bufferedReader.readLine();
			    }
			    
			    bufferedReader.close();
			    
			    // Loop através das linhas do arquivo para poder fazer os cálculos de precision, recall, acurácia.
			    intTotalRecords = lstRecords.size();
			    
			    for (int indexRecord = 0; indexRecord < lstRecords.size(); indexRecord++) {
			    	// Total de recordes identificados corretamente no 1º lugar
			    	if (lstRecords.get(indexRecord).getFirst()) {
			    		intTotalPositiveFirst = intTotalPositiveFirst + 1;
			    	}
			    	
			    	// Total de recordes identificados corretamente nos 3 primeiros lugares
			    	//if (lstRecords.get(indexRecord).getThird()) {
			    	//	intTotalPositiveThird = intTotalPositiveThird + 1;
			    	//}
			    	
			    	// Adiciona registro a classe (espécie)
			    	// Adiciona os valores positivo verdadeiro
			    	for (int indexSpecies = 0; indexSpecies < lstSpecies.size(); indexSpecies++) {
			    		if (lstRecords.get(indexRecord).getSpecies().equals(lstSpecies.get(indexSpecies).getSpecies())) {
			    			lstSpecies.get(indexSpecies).addRecord(intIndexExperiment);
			    			
			    			// 1º Segmento identificado corretamente
			    			if (lstRecords.get(indexRecord).getFirst()) {
			    				lstSpecies.get(indexSpecies).addTruePositiveFirst(intIndexExperiment);
			    			}
			    			
			    			break;
			    		}
			    	}
			    	
			    	// Identifica os falsos positivos
			    	if (!lstRecords.get(indexRecord).getFirst()) {
			    		for (int indexSpecies = 0; indexSpecies < lstSpecies.size(); indexSpecies++) {
			    			if (lstRecords.get(indexRecord).getSpeciesIdentifiedFirst().equals(lstSpecies.get(indexSpecies).getSpecies())) {
			    				lstSpecies.get(indexSpecies).addFalsePositiveFirst(intIndexExperiment);
			    				
			    				break;
			    			}
			    		}
			    	}
			    }
			    
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Ordena a lista de espécies
	    ObjectComparator comparator = new ObjectComparator();
	    Collections.sort(lstSpecies, comparator);
	    
	    // Precision / Recall / F-Measure
	    double[] dblPrecision;
	    double[] dblRecall;
	    double[] dblFMeasure;
	    
	    double dblMeanPrecision;
	    double dblMeanRecall;
	    double dblMeanFMeasure;
	    
	    double dblSDPrecision;
	    double dblSDRecall;
	    double dblSDFMeasure;
	    
	    DecimalFormat df = new DecimalFormat("#.00");
	    
	    for (int indexSpecies = 0; indexSpecies < lstSpecies.size(); indexSpecies++) {
	    	lstSpecies.get(indexSpecies).calculatePrecision();
	    	lstSpecies.get(indexSpecies).calculateRecall();
	    	lstSpecies.get(indexSpecies).calculateFMeasure();
	    	
	    	dblPrecision = new double[intTotalExperiments];
		    dblRecall = new double[intTotalExperiments];
		    dblFMeasure = new double[intTotalExperiments];
	    	
	    	//System.out.print(lstSpecies.get(indexSpecies).getSpecies());
	    	for (int indexExperiment = 1; indexExperiment <= intTotalExperiments; indexExperiment++) {
	    		dblPrecision[indexExperiment - 1] = lstSpecies.get(indexSpecies).getPrecisionFirst(indexExperiment);
	    		dblRecall[indexExperiment - 1] = lstSpecies.get(indexSpecies).getRecallFirst(indexExperiment);
	    		dblFMeasure[indexExperiment - 1] = lstSpecies.get(indexSpecies).getFMeasureFirst(indexExperiment);
	    		//System.out.print("	" + lstSpecies.get(indexSpecies).getPrecisionFirst(indexExperiment) + "	" + lstSpecies.get(indexSpecies).getRecallFirst(indexExperiment) + "	" + lstSpecies.get(indexSpecies).getFMeasureFirst(indexExperiment));
	    	}
	    	//System.out.println("");
	    	
	    	dblMeanPrecision = Statistics.calculateMean(dblPrecision) * 100;
	    	dblMeanRecall = Statistics.calculateMean(dblRecall) * 100;
	    	dblMeanFMeasure = Statistics.calculateMean(dblFMeasure) * 100;
	    	
	    	dblSDPrecision = Statistics.calculateStandardDeviation(dblPrecision);
	 	    dblSDRecall = Statistics.calculateStandardDeviation(dblRecall);
	 	    dblSDFMeasure = Statistics.calculateStandardDeviation(dblFMeasure);
	    	
	    	System.out.println(lstSpecies.get(indexSpecies).getSpecies() + "	" + df.format(dblMeanPrecision) + " | " + df.format(dblMeanRecall) + " | " + df.format(dblMeanFMeasure));
	 	    //System.out.println(lstSpecies.get(indexSpecies).getSpecies() + "	" + df.format(dblMeanRecall));
	    }
		
		//System.out.println("--------------------------------------------------------");
	}
	
	private static void getSegments() {
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.animal_class, aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_class, aud.animal_genus, aud.animal_species ");
			
			ResultSet rsSpecies = objDatabaseConnection.executeSelectQuery();
			ResultSet rsAudioFiles;
			ResultSet rsSegments;
			
			while (rsSpecies.next()) {
				System.out.println("Class: " + rsSpecies.getString("animal_class") + " | Scientific name: " + rsSpecies.getString("animal_genus") + " " + rsSpecies.getString("animal_species"));
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT aud.id AS id_file ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.animal_class = ? ");
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_genus = ? ");
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_species = ? ");
				objDatabaseConnection.sqlCommandAppend("GROUP BY aud.id ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY aud.id ");
				objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, rsSpecies.getString("animal_class"));
				objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, rsSpecies.getString("animal_genus"));
				objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, rsSpecies.getString("animal_species"));
				
				rsAudioFiles = objDatabaseConnection.executeSelectQuery();
				
				while (rsAudioFiles.next()) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, seg.sound_unit, seg.time_initial, seg.time_final, seg.frequency_initial, seg.frequency_final ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files            aud ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments   seg   ON   seg.fk_audio_file = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE aud.id = ? ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
					objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, rsAudioFiles.getString("id_file"));
					
					rsSegments = objDatabaseConnection.executeSelectQuery();
					
					boolean blnFirstColumn = true;
					
					while (rsSegments.next()) {
						//if (blnFirstColumn) {
							System.out.println(String.format("%07d", rsAudioFiles.getInt("id_file")) + "	" + "	" + ClockTransformations.millisecondsIntoDigitalFormat(rsSegments.getInt("time_initial")) + "	" + ClockTransformations.millisecondsIntoDigitalFormat(rsSegments.getInt("time_final")) + "	" + rsSegments.getString("frequency_initial") + " Hz" + "	" + rsSegments.getString("frequency_final") + " Hz");
						//	blnFirstColumn = false;
						//} else {
						//	System.out.println("	" + "	" + ClockTransformations.millisecondsIntoDigitalFormat(rsSegments.getInt("time_initial")) + "	" + ClockTransformations.millisecondsIntoDigitalFormat(rsSegments.getInt("time_final")) + "	" + rsSegments.getString("frequency_initial") + " Hz" + "	" + rsSegments.getString("frequency_final") + " Hz");
						//	blnFirstColumn = true;
						//}
					}
					
					if (!blnFirstColumn) {
						System.out.println("");
					}
				}
				
				System.out.println("");
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
}

class ReadComparisonValues {
	private String strSpecies;
	private int intSegment;
	private boolean blnFirst;
	private boolean blnThird;
	private String strSpeciesIdentifiedFirst;
	private String strSpeciesIdentifiedSecond;
	private String strSpeciesIdentifiedThird;
	
	protected String getSpecies() {
		return strSpecies;
	}
	
	protected int getSegment() {
		return intSegment;
	}
	
	protected boolean getFirst() {
		return blnFirst;
	}
	
	protected boolean getThird() {
		return blnThird;
	}
	
	protected String getSpeciesIdentifiedFirst() {
		return strSpeciesIdentifiedFirst;
	}
	
	protected String getSpeciesIdentifiedSecond() {
		return strSpeciesIdentifiedSecond;
	}
	
	protected String getSpeciesIdentifiedThird() {
		return strSpeciesIdentifiedThird;
	}
	
	protected ReadComparisonValues(String strSpecies, int intSegment, boolean blnFirst, boolean blnThird, String strSpeciesIdentifiedFirst, String strSpeciesIdentifiedSecond, String strSpeciesIdentifiedThird) {
		this.strSpecies = strSpecies;
		this.intSegment = intSegment;
		this.blnFirst = blnFirst;
		this.blnThird = blnThird;
		this.strSpeciesIdentifiedFirst = strSpeciesIdentifiedFirst;
		this.strSpeciesIdentifiedSecond = strSpeciesIdentifiedSecond;
		this.strSpeciesIdentifiedThird = strSpeciesIdentifiedThird;
	}
}

class SpeciesValues {
	private static int intTotalExperiments = 10;
	
	private String strSpecies;
	
	private int[] intTotalRecords;
	
	private int[] intTotalTruePositiveFirst;   // Positivo verdadeiro - Primeiro
	private int[] intTotalFalsePositiveFirst;  // Falso positivo - Primeiro
	
	private double[] dblPrecisionFirst;
	private double[] dblRecallFirst;
	private double[] dblFMeasureFirst;
	
	protected String getSpecies() {
		return strSpecies;
	}
	
	protected double getPrecisionFirst(int intIndexExperiment) {
		return dblPrecisionFirst[intIndexExperiment];
	}

	protected double getRecallFirst(int intIndexExperiment) {
		return dblRecallFirst[intIndexExperiment];
	}

	protected double getFMeasureFirst(int intIndexExperiment) {
		return dblFMeasureFirst[intIndexExperiment];
	}
	
	protected void addRecord(int intIndexExperiment) {
		intTotalRecords[intIndexExperiment] = intTotalRecords[intIndexExperiment] + 1;
	}
	
	protected void addTruePositiveFirst(int intIndexExperiment) {
		intTotalTruePositiveFirst[intIndexExperiment] = intTotalTruePositiveFirst[intIndexExperiment] + 1;
	}
	
	protected void addFalsePositiveFirst(int intIndexExperiment) {
		intTotalFalsePositiveFirst[intIndexExperiment] = intTotalFalsePositiveFirst[intIndexExperiment] + 1;
	}
	
	protected void calculatePrecision() {
		for (int indexExperiment = 0; indexExperiment <= intTotalExperiments; indexExperiment++) {
			dblPrecisionFirst[indexExperiment] = (double) intTotalTruePositiveFirst[indexExperiment] / ((double) intTotalTruePositiveFirst[indexExperiment] + (double) intTotalFalsePositiveFirst[indexExperiment]);
			
			if (Double.isNaN(dblPrecisionFirst[indexExperiment])) {
				dblPrecisionFirst[indexExperiment] = 0;
			}
		}
	}
	
	protected void calculateRecall() {
		for (int indexExperiment = 0; indexExperiment <= intTotalExperiments; indexExperiment++) {
			dblRecallFirst[indexExperiment] = (double) intTotalTruePositiveFirst[indexExperiment] / (double) intTotalRecords[indexExperiment];
		
			if (Double.isNaN(dblRecallFirst[indexExperiment])) {
				dblRecallFirst[indexExperiment] = 0;
			}
		}
	}
	
	protected void calculateFMeasure() {
		for (int indexExperiment = 0; indexExperiment <= intTotalExperiments; indexExperiment++) {
			dblFMeasureFirst[indexExperiment] = 2.0d * dblPrecisionFirst[indexExperiment] * dblRecallFirst[indexExperiment] / (dblPrecisionFirst[indexExperiment] + dblRecallFirst[indexExperiment]);
		
			if (Double.isNaN(dblFMeasureFirst[indexExperiment])) {
				dblFMeasureFirst[indexExperiment] = 0;
			}
		}
	}

	protected SpeciesValues(String strSpecies) {
		this.strSpecies = strSpecies;
		
		intTotalRecords = new int[11];
		intTotalTruePositiveFirst = new int[11];   // Positivo verdadeiro
		intTotalFalsePositiveFirst= new int[11];   // Falso positivo
		
		dblPrecisionFirst = new double[11];
		dblRecallFirst = new double[11];
		dblFMeasureFirst = new double[11];
	}
}

class ObjectComparator implements Comparator<SpeciesValues> {
    public int compare(SpeciesValues obj1, SpeciesValues obj2) {
        return obj1.getSpecies().compareTo(obj2.getSpecies());
    }
}
