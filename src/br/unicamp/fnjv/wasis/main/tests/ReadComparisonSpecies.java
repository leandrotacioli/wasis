package br.unicamp.fnjv.wasis.main.tests;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ReadComparisonSpecies {
	private static String FILE_PATH = "C:\\Users\\Leandro\\Dropbox\\UNICAMP\\Mestrado\\Resultados";
	private static int intTotalExperiments = 10;
	
	public static void main(String[] args) {
		//int intTotalTempo = 4888859;
		readFile(Experiments.PEARSON, Experiments.PS, Experiments.AMPHIBIA);
		
		//System.out.println(ClockTransformations.millisecondsIntoDigitalFormat(intTotalTempo));
		
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
		
		readFile(Experiments.HMM, Experiments.MFCC, Experiments.AMPHIBIA);
		readFile(Experiments.HMM, Experiments.LPC, Experiments.AMPHIBIA);
		readFile(Experiments.HMM, Experiments.LPCC, Experiments.AMPHIBIA);
		readFile(Experiments.HMM, Experiments.PLP, Experiments.AMPHIBIA);
		readFile(Experiments.HMM, Experiments.MFCC_LPC, Experiments.AMPHIBIA);
		readFile(Experiments.HMM, Experiments.MFCC_LPCC, Experiments.AMPHIBIA);
		readFile(Experiments.HMM, Experiments.MFCC_PLP, Experiments.AMPHIBIA);
		readFile(Experiments.HMM, Experiments.MFCC_LPC_LPCC_PLP, Experiments.AMPHIBIA);
		
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
		
		List<ReadComparisonValuesAAA> lstRecords;
		List<SpeciesValuesAAA> lstSpecies;
		
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
		
		for (int intIndexExperiment = 1; intIndexExperiment <= intTotalExperiments; intIndexExperiment++) {
			String strExperimentName = strClassifier + "-" + strAnimalClass + "-" + strFeature + "-" + intIndexExperiment;
			String strExperimentFile = FILE_PATH + "\\" + strAnimalClass + "\\" + strExperimentName + ".txt";
			
			//System.out.println("Arquivo: " + strExperimentFile);
			//System.out.print("Nº Experimento: " + intIndexExperiment + " | Tipo: " + strAnimalClass + " | Classificação: " + strClassifier + " | Feature: " + strFeature);
			
			intTotalRecords = 0;
	    	intTotalPositiveFirst = 0;
			
			try {
				bufferedReader = new BufferedReader(new FileReader(strExperimentFile));
				
				lstRecords = new ArrayList<ReadComparisonValuesAAA>();
				lstSpecies = new ArrayList<SpeciesValuesAAA>();
				
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
			    	
			    	lstRecords.add(new ReadComparisonValuesAAA(strSpecies, intSegment, blnFirst, blnThird, strSpeciesIdentifiedFirst, strSpeciesIdentifiedSecond, strSpeciesIdentifiedThird));
			    	
			    	// Cria uma lista com as espécies existentes
			    	boolean blnSpeciesAlreadySaved = false;
			    	for (int indexSpecies = 0; indexSpecies < lstSpecies.size(); indexSpecies++) {
			    		if (lstSpecies.get(indexSpecies).getSpecies().equals(strSpecies)) {
			    			blnSpeciesAlreadySaved = true;
			    			
			    			break;
			    		}
			    	}
			    	
			    	if (!blnSpeciesAlreadySaved) {
			    		lstSpecies.add(new SpeciesValuesAAA(strSpecies));
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
			    			lstSpecies.get(indexSpecies).addRecord();
			    			
			    			// 1º Segmento identificado corretamente
			    			if (lstRecords.get(indexRecord).getFirst()) {
			    				lstSpecies.get(indexSpecies).addTruePositiveFirst();
			    			}
			    			
			    			break;
			    		}
			    	}
			    	
			    	// Identifica os falsos positivos dos primeiros lugares
			    	if (!lstRecords.get(indexRecord).getFirst()) {
			    		for (int indexSpecies = 0; indexSpecies < lstSpecies.size(); indexSpecies++) {
			    			if (lstRecords.get(indexRecord).getSpeciesIdentifiedFirst().equals(lstSpecies.get(indexSpecies).getSpecies())) {
			    				lstSpecies.get(indexSpecies).addFalsePositiveFirst();
			    				
			    				break;
			    			}
			    		}
			    		
			    	// Identifica os falsos positivos dos três primeiros lugares
			    	} else {
			    		
			    	}
			    }
			    
			    // True Positive Rate (Recall)
			    dblTPRFirst = (double) intTotalPositiveFirst / (double) intTotalRecords;

			    //System.out.println(" | True Positive Rate (TPR):	" + dblTPRFirst);
			    //System.out.println("");
			    
			    // Ordena a lista de espécies
			    ObjectComparatorAAA comparator = new ObjectComparatorAAA();
			    Collections.sort(lstSpecies, comparator);
			    
			    // Precision / Recall / F-Measure
			    for (int indexSpecies = 14; indexSpecies <= 14; indexSpecies++) {
			    	lstSpecies.get(indexSpecies).calculatePrecision();
			    	lstSpecies.get(indexSpecies).calculateRecall();
			    	lstSpecies.get(indexSpecies).calculateFMeasure();
			    	
			    	System.out.println("Espécie:	" + lstSpecies.get(indexSpecies).getSpecies() + "	" + lstSpecies.get(indexSpecies).getPrecisionFirst() + "	" + lstSpecies.get(indexSpecies).getRecallFirst() + "	" + lstSpecies.get(indexSpecies).getFMeasureFirst());
			    }
		    
			    //System.out.println("--------------------------------------------------------");
			    
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("--------------------------------------------------------");
	}
}

class ReadComparisonValuesAAA {
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
	
	protected ReadComparisonValuesAAA(String strSpecies, int intSegment, boolean blnFirst, boolean blnThird, String strSpeciesIdentifiedFirst, String strSpeciesIdentifiedSecond, String strSpeciesIdentifiedThird) {
		this.strSpecies = strSpecies;
		this.intSegment = intSegment;
		this.blnFirst = blnFirst;
		this.blnThird = blnThird;
		this.strSpeciesIdentifiedFirst = strSpeciesIdentifiedFirst;
		this.strSpeciesIdentifiedSecond = strSpeciesIdentifiedSecond;
		this.strSpeciesIdentifiedThird = strSpeciesIdentifiedThird;
	}
}

class SpeciesValuesAAA {
	private static int intTotalExperiments = 10;
	
	private String strSpecies;
	
	private int intTotalRecords;
	
	private int intTotalTruePositiveFirst;   // Positivo verdadeiro - Primeiro
	private int intTotalFalsePositiveFirst;  // Falso positivo - Primeiro
	
	private double dblPrecisionFirst;
	private double dblRecallFirst;
	private double dblFMeasureFirst;
	
	protected String getSpecies() {
		return strSpecies;
	}
	
	protected double getPrecisionFirst() {
		return dblPrecisionFirst;
	}

	protected double getRecallFirst() {
		return dblRecallFirst;
	}

	protected double getFMeasureFirst() {
		return dblFMeasureFirst;
	}
	
	protected void addRecord() {
		intTotalRecords = intTotalRecords + 1;
	}
	
	protected void addTruePositiveFirst() {
		intTotalTruePositiveFirst = intTotalTruePositiveFirst + 1;
	}
	
	protected void addFalsePositiveFirst() {
		intTotalFalsePositiveFirst = intTotalFalsePositiveFirst + 1;
	}
	
	protected void calculatePrecision() {
		dblPrecisionFirst = (double) intTotalTruePositiveFirst / ((double) intTotalTruePositiveFirst + (double) intTotalFalsePositiveFirst);	
	}
	
	protected void calculateRecall() {
		dblRecallFirst = (double) intTotalTruePositiveFirst / (double) intTotalRecords;
	}
	
	protected void calculateFMeasure() {
		dblFMeasureFirst = 2.0d * dblPrecisionFirst * dblRecallFirst / (dblPrecisionFirst + dblRecallFirst);
	
		if (Double.isNaN(dblFMeasureFirst)) {
			dblFMeasureFirst = 0;
		}
	}

	protected SpeciesValuesAAA(String strSpecies) {
		this.strSpecies = strSpecies;
	}
}

class ObjectComparatorAAA implements Comparator<SpeciesValuesAAA> {
    public int compare(SpeciesValuesAAA obj1, SpeciesValuesAAA obj2) {
        return obj1.getSpecies().compareTo(obj2.getSpecies());
    }
}