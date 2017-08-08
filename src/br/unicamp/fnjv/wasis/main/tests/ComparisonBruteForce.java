package br.unicamp.fnjv.wasis.main.tests;

import java.io.Writer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.classifiers.pcc.PearsonCorrelation;
import br.unicamp.fnjv.wasis.classifiers.pcc.PearsonCorrelationValues;
import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;

public class ComparisonBruteForce implements Runnable {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private Writer writer;
	
	private int intFeature;
	private ResultSet rsSpeciesSegmentsComparison;
	private String strTrainingExperiment;
	private String strTestingExperiment;
	private String strAnimalClass;
	private String strAnimalGenus;
	private String strAnimalSpecies;
	
	protected ComparisonBruteForce(Writer writer, int intFeature, ResultSet rsSpeciesSegmentsComparison, String strTrainingExperiment, String strTestingExperiment, String strAnimalClass, String strAnimalGenus, String strAnimalSpecies) throws CloneNotSupportedException {
		this.writer = writer;
		this.intFeature = intFeature;
		this.rsSpeciesSegmentsComparison = rsSpeciesSegmentsComparison;
		this.strTrainingExperiment = strTrainingExperiment;
		this.strTestingExperiment = strTestingExperiment;
		this.strAnimalClass = strAnimalClass;
		this.strAnimalGenus = strAnimalGenus;
		this.strAnimalSpecies = strAnimalSpecies;
	}
	
	@Override
	public void run() {
		DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
		objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
		objDatabaseConnection.openConnection();
		
		try {
			// Loop através dos segmentos da espécie do experimento de comparação
			objDatabaseConnection.initiliazeStatement();
			
			// MFCC
			if (intFeature == Experiments.MFCC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, mfcc.mfcc_vector AS feature_vector ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   mfcc.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file          = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
				
			// LPC
			} else if (intFeature == Experiments.LPC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, lpc.lpc_vector AS feature_vector ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpc    lpc ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   lpc.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file         = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 1 ");
				
			// LPCC
			} else if (intFeature == Experiments.LPCC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, lpcc.lpcc_vector AS feature_vector ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpcc   lpcc ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   lpcc.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file          = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 1 ");
				
			// PLP
			} else if (intFeature == Experiments.PLP) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, plp.plp_vector AS feature_vector ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_plp    plp ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   plp.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file         = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 1 ");
				
			// PS
			} else if (intFeature == Experiments.PS) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_ps     ps ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   ps.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file        = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				
			// MFCC-LPC
			} else if (intFeature == Experiments.MFCC_LPC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector) AS feature_vector ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpc    lpc    ON   lpc.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpc.lpc_order ");
				objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
				objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 1 ");
				
			// MFCC-LPCC
			} else if (intFeature == Experiments.MFCC_LPCC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, CONCAT(mfcc.mfcc_vector, ';', lpcc.lpcc_vector) AS feature_vector ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpcc   lpcc   ON   lpcc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpcc.lpcc_order ");
				objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
				objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 1 ");
				
			// MFCC-PLP
			} else if (intFeature == Experiments.MFCC_PLP) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, CONCAT(mfcc.mfcc_vector, ';', plp.plp_vector) AS feature_vector ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_plp    plp    ON   plp.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud    ON   seg.fk_audio_file          = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = plp.plp_order ");
				objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
				objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 1 ");
				
			// MFCC-LPC-LPCC-PLP
			} else if (intFeature == Experiments.MFCC_LPC_LPCC_PLP) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector, ';', lpcc.lpcc_vector, ';', plp.plp_vector) AS feature_vector ");
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
			}
			
			if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
				objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
			}
			
			objDatabaseConnection.sqlCommandAppend("AND aud.animal_genus = ? ");
			objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, strAnimalGenus);
			
			objDatabaseConnection.sqlCommandAppend("AND aud.animal_species = ? ");
			objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, strAnimalSpecies);
			
			objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTrainingExperiment + ") ");
			objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTestingExperiment + ") ");
			
			if (intFeature == Experiments.PS) {
				objDatabaseConnection.sqlCommandAppend("GROUP BY seg.id ");
			}
			
			objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
			
			ResultSet rsSpeciesSegmentsTest = objDatabaseConnection.executeSelectQuery();
			
			ResultSet rsSpeciesSegmentsTestPowerSpectrum;
			ResultSet rsSpeciesSegmentsComparisonPowerSpectrum;
			
			PearsonCorrelation objPCC;
			List<PearsonCorrelationValues> featureVectorTest;
			List<PearsonCorrelationValues> featureVectorComparison;
			double dblCorrelation = 0;
			
			List<ComparisonOrder> lstOrder;
			
			String strSpeciesInTest = strAnimalGenus + " " + strAnimalSpecies;
			
			// Loop através dos vetores de um segmento
			while (rsSpeciesSegmentsTest.next()) {
				// Power Spectrum
				if (intFeature == Experiments.PS) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT ps.frequency_value, ps.decibel_value ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_ps     ps ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   ps.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY ps.frequency_value ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
					rsSpeciesSegmentsTestPowerSpectrum = objDatabaseConnection.executeSelectQuery();
					
					// Segmento de Teste
					featureVectorTest = new ArrayList<PearsonCorrelationValues>();
					
					while (rsSpeciesSegmentsTestPowerSpectrum.next()) {
						featureVectorTest.add(new PearsonCorrelationValues(rsSpeciesSegmentsTestPowerSpectrum.getInt("frequency_value"), rsSpeciesSegmentsTestPowerSpectrum.getDouble("decibel_value")));	
					}
					
					// Loop através dos segmentos que serão comparados
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, aud.animal_class, aud.animal_genus, aud.animal_species ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_ps     ps ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   ps.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file        = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
					
					if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
						objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
						objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
					}
					
					objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTrainingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTestingExperiment + ") ");
					objDatabaseConnection.sqlCommandAppend("GROUP BY seg.id ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
					
					rsSpeciesSegmentsComparison = objDatabaseConnection.executeSelectQuery();
					
					lstOrder = new ArrayList<ComparisonOrder>();
					
					while (rsSpeciesSegmentsComparison.next()) {
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("SELECT ps.frequency_value, ps.decibel_value ");
						objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_ps     ps ");
						objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   ps.fk_audio_file_segment = seg.id ");
						objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
						objDatabaseConnection.sqlCommandAppend("ORDER BY ps.frequency_value ");
						objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsComparison.getInt("id_segment"));
						rsSpeciesSegmentsComparisonPowerSpectrum = objDatabaseConnection.executeSelectQuery();
						
						// Segmento de Comparação
						featureVectorComparison = new ArrayList<PearsonCorrelationValues>();
						
						while (rsSpeciesSegmentsComparisonPowerSpectrum.next()) {
							featureVectorComparison.add(new PearsonCorrelationValues(rsSpeciesSegmentsComparisonPowerSpectrum.getInt("frequency_value"), rsSpeciesSegmentsComparisonPowerSpectrum.getDouble("decibel_value")));	
						}
						
						objPCC = new PearsonCorrelation(true, featureVectorTest, featureVectorComparison);
						dblCorrelation = objPCC.calculateCorrelationCoeficient();
						
						if (Double.isNaN(dblCorrelation)) {
							dblCorrelation = 0;
						}
						
						lstOrder.add(new ComparisonOrder(rsSpeciesSegmentsComparison.getInt("id_segment"), rsSpeciesSegmentsComparison.getString("animal_genus") + " " + rsSpeciesSegmentsComparison.getString("animal_species"), dblCorrelation));
					}
					
					Collections.sort(lstOrder); // Ordena a lista de resultados
					
					boolean blnFirst = false;
					boolean blnThird = false;
					
					String strSpeciesIdentifiedFirst = "";
					String strSpeciesIdentifiedSecond = "";
					String strSpeciesIdentifiedThird = "";
					
					// Verifica se o primeiro foi identificado corretamente
					for (int indexOrder = 0; indexOrder < 3; indexOrder++) {
						if (indexOrder == 0) {
							strSpeciesIdentifiedFirst = lstOrder.get(indexOrder).getSpecies();
							
							if (strSpeciesInTest.equals(lstOrder.get(indexOrder).getSpecies())) {
								blnFirst = true;
								blnThird = true;
							}
							
						} else if (indexOrder == 1) {
							strSpeciesIdentifiedSecond = lstOrder.get(indexOrder).getSpecies();
							
							if (strSpeciesInTest.equals(lstOrder.get(indexOrder).getSpecies())) {
								blnThird = true;
							}
							
						} else if (indexOrder == 2) {
							strSpeciesIdentifiedThird = lstOrder.get(indexOrder).getSpecies();
							
							if (strSpeciesInTest.equals(lstOrder.get(indexOrder).getSpecies())) {
								blnThird = true;
							}
						}
					}
					
					writer.write(strAnimalGenus + " " + strAnimalSpecies + "	" + rsSpeciesSegmentsTest.getInt("id_segment") + "	" + blnFirst + "	" + blnThird + "	" + strSpeciesIdentifiedFirst + "	" + strSpeciesIdentifiedSecond + "	" + strSpeciesIdentifiedThird + "\n");
					
				// ****************************************************************************************************************
				// Não Power Spectrum
				} else if (intFeature != Experiments.PS) {
					featureVectorTest = getFeatureVector(rsSpeciesSegmentsTest.getString("feature_vector"));
										
					lstOrder = new ArrayList<ComparisonOrder>();
					
					// Segmentos para Comparação
					while (rsSpeciesSegmentsComparison.next()) {
						String strFeatureVector = "";
						
						if (intFeature == Experiments.MFCC) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("mfcc_vector");
						} else if (intFeature == Experiments.LPC) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("lpc_vector");
						} else if (intFeature == Experiments.LPCC) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("lpcc_vector");
						} else if (intFeature == Experiments.PLP) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("plp_vector");
						} else if (intFeature == Experiments.MFCC_LPC) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("mfcc_lpc_vector");
						} else if (intFeature == Experiments.MFCC_LPCC) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("mfcc_lpcc_vector");
						} else if (intFeature == Experiments.MFCC_PLP) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("mfcc_plp_vector");
						} else if (intFeature == Experiments.MFCC_LPC_LPCC_PLP) {
							strFeatureVector = rsSpeciesSegmentsComparison.getString("mfcc_lpc_lpcc_plp_vector");
						}
						
						featureVectorComparison = getFeatureVector(strFeatureVector);
						
						objPCC = new PearsonCorrelation(false, featureVectorTest, featureVectorComparison);
						dblCorrelation = objPCC.calculateCorrelationCoeficient();

						lstOrder.add(new ComparisonOrder(rsSpeciesSegmentsComparison.getInt("id_segment"), rsSpeciesSegmentsComparison.getString("animal_genus") + " " + rsSpeciesSegmentsComparison.getString("animal_species"), dblCorrelation));
					}
					
					rsSpeciesSegmentsComparison.beforeFirst();  // Move cursor before first record
					
					Collections.sort(lstOrder); // Ordena a lista de resultados
					
					boolean blnFirst = false;
					boolean blnThird = false;
					
					String strSpeciesIdentifiedFirst = "";
					String strSpeciesIdentifiedSecond = "";
					String strSpeciesIdentifiedThird = "";
					
					// Verifica se o primeiro foi identificado corretamente
					for (int indexOrder = 0; indexOrder < 3; indexOrder++) {
						if (indexOrder == 0) {
							strSpeciesIdentifiedFirst = lstOrder.get(indexOrder).getSpecies();
							
							if (strSpeciesInTest.equals(lstOrder.get(indexOrder).getSpecies())) {
								blnFirst = true;
								blnThird = true;
							}
							
						} else if (indexOrder == 1) {
							strSpeciesIdentifiedSecond = lstOrder.get(indexOrder).getSpecies();
							
							if (strSpeciesInTest.equals(lstOrder.get(indexOrder).getSpecies())) {
								blnThird = true;
							}
							
						} else if (indexOrder == 2) {
							strSpeciesIdentifiedThird = lstOrder.get(indexOrder).getSpecies();
							
							if (strSpeciesInTest.equals(lstOrder.get(indexOrder).getSpecies())) {
								blnThird = true;
							}
						}
					}
					
					writer.write(strAnimalGenus + " " + strAnimalSpecies + "	" + rsSpeciesSegmentsTest.getInt("id_segment") + "	" + blnFirst + "	" + blnThird + "	" + strSpeciesIdentifiedFirst + "	" + strSpeciesIdentifiedSecond + "	" + strSpeciesIdentifiedThird + "\n");
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Transforma uma lista mo vetor do formato da base de dados para um legível ao Brute Force.
	 * 
	 * @param lstValues
	 */
	private List<PearsonCorrelationValues> getFeatureVector(String strFeature) {
		String[] originalFeatureVector = strFeature.split(";");
		
		List<PearsonCorrelationValues> lstValues = new ArrayList<PearsonCorrelationValues>();
		
		for (int indexElement = 0; indexElement < originalFeatureVector.length; indexElement++) {
			lstValues.add(new PearsonCorrelationValues(indexElement, Double.parseDouble(originalFeatureVector[indexElement])));
		}
		
		return lstValues;
	}
}

/**
 * 
 * 
 * A classe implementa <i>Comparable</i>, pois através do método <i>compareTo</i>
 * é possível ordenar uma lista dessa classe através do campo <i>dblCorrelation</i>.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 08/Jan/2015
 */
class ComparisonOrder implements Comparable<ComparisonOrder> {
	private int intSegment;
	private String strSpecies;
	private double dblCorrelation;

	public int getSegment() {
		return intSegment;
	}
	
	public String getSpecies() {
		return strSpecies;
	}
	
	public double getCorrelation() {
		return dblCorrelation;
	}
	
	/**
	 * A classe implementa <i>Comparable</i>, pois através do método <i>compareTo</i>
	 * é possível ordenar uma lista dessa classe através do campo <i>dblCorrelation</i>.
	 * 
	 * @param intSegment
	 * @param strSpecies
	 * @param dblCorrelation
	 */
	protected ComparisonOrder(int intSegment, String strSpecies, double dblCorrelation) {
		this.intSegment = intSegment;
		this.strSpecies = strSpecies;
		this.dblCorrelation = dblCorrelation;
	}
	
	@Override
	public int compareTo(ComparisonOrder objComparisonOrder) {
		try {
			if (this.dblCorrelation == objComparisonOrder.dblCorrelation) {
				return 0;
			}
			
			if (this.dblCorrelation > objComparisonOrder.dblCorrelation) {
	            return -1;
	        }
			
			if (this.dblCorrelation < objComparisonOrder.dblCorrelation) {
	            return 1;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return 0;
		}
		
        return 0;
	}
}