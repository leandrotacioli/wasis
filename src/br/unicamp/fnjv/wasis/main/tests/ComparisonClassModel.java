package br.unicamp.fnjv.wasis.main.tests;

import java.io.Writer;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.classifiers.hmm.HMM;
import br.unicamp.fnjv.wasis.classifiers.hmm.db.DataBase;
import br.unicamp.fnjv.wasis.classifiers.hmm.db.ObjectIODataBase;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;

public class ComparisonClassModel implements Runnable {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private Writer writer;
	
	private int intFeature;
	private String strExperimentName;
	private String strTrainingExperiment;
	private String strTestingExperiment;
	private String strAnimalClass;
	private String strAnimalGenus;
	private String strAnimalSpecies;
	
	protected ComparisonClassModel(Writer writer, String strExperimentName, int intFeature, String strTrainingExperiment, String strTestingExperiment, String strAnimalClass, String strAnimalGenus, String strAnimalSpecies) throws CloneNotSupportedException {
		this.writer = writer;
		this.strExperimentName = strExperimentName;
		this.intFeature = intFeature;
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
			// Loop através dos segmentos do experimento de comparação
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments        seg ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file = aud.id ");
			objDatabaseConnection.sqlCommandAppend("WHERE seg.id > 0 ");
			
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
			objDatabaseConnection.sqlCommandAppend("GROUP BY seg.id ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
			
			ResultSet rsSpeciesSegmentsTest = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSpeciesSegmentsTestVectors;
			
			String[] originalFeatureVector;
			double[] featureVector;
			double[][] featureVectors;
			
			Points[] pointsFeatureVectors;
			
			Codebook objCodebook = new Codebook(strExperimentName);
			
			int[] quantized;
			
			List<ComparisonOrderClassModel> lstOrder;
			
			String strSpeciesInTest = strAnimalGenus + " " + strAnimalSpecies;
			
			// Carrega os HMM modelos treinados
			DataBase objDB = new ObjectIODataBase();
			objDB.setType("hmm");
			
			String[] species = objDB.readRegistered(strExperimentName);
			HMM[] hmmModels = new HMM[species.length];
			
			for (int indexSpecies = 0; indexSpecies < species.length; indexSpecies++) {
				hmmModels[indexSpecies] = new HMM(strExperimentName, species[indexSpecies]);
			}
			
			while (rsSpeciesSegmentsTest.next()) {
				// MFCC
				if (intFeature == Experiments.MFCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT mfcc.mfcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   mfcc.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
					
				// LPC
				} else if (intFeature == Experiments.LPC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT lpc.lpc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpc    lpc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   lpc.fk_audio_file_segment  = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY lpc.lpc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
					
				// LPCC
				} else if (intFeature == Experiments.LPCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT lpcc.lpcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpcc   lpcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   lpcc.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY lpcc.lpcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
					
				// PLP
				} else if (intFeature == Experiments.PLP) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT plp.plp_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_plp    plp ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   plp.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY plp.plp_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
						
				// MFCC-LPC
				} else if (intFeature == Experiments.MFCC_LPC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpc    lpc    ON   lpc.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpc.lpc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
					
				// MFCC-LPCC
				} else if (intFeature == Experiments.MFCC_LPCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', lpcc.lpcc_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpcc   lpcc    ON  lpcc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpcc.lpcc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
					
				// MFCC-PLP
				} else if (intFeature == Experiments.MFCC_PLP) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', plp.plp_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_plp    plp    ON   plp.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = plp.plp_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
					
				// MFCC-LPC-LPCC-PLP
				} else if (intFeature == Experiments.MFCC_LPC_LPCC_PLP) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector, ';', lpcc.lpcc_vector, ';', plp.plp_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpc    lpc    ON   lpc.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpcc   lpcc   ON   lpcc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_plp    plp    ON   plp.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg    ON   mfcc.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpc.lpc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpcc.lpcc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = plp.plp_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegmentsTest.getInt("id_segment"));
				}
				
				rsSpeciesSegmentsTestVectors = objDatabaseConnection.executeSelectQuery();
				
				featureVectors = new double[objDatabaseConnection.getTotalRecords()][];
				int intIndexFeatureVector = 0;
				
				// Loop através dos vetores de um segmento
				while (rsSpeciesSegmentsTestVectors.next()) {
					originalFeatureVector = rsSpeciesSegmentsTestVectors.getString("feature_vector").split(";");
					
					featureVector = new double[originalFeatureVector.length];
					
					for (int indexElement = 0; indexElement < originalFeatureVector.length; indexElement++) {
						featureVector[indexElement] = Double.parseDouble(originalFeatureVector[indexElement]);
					}
					
					featureVectors[intIndexFeatureVector] = featureVector;
					
					intIndexFeatureVector++;
				}
				
				pointsFeatureVectors = getPointsFromFeatureVector(featureVectors);
				
				// Quantize using Codebook
				quantized = objCodebook.quantize(pointsFeatureVectors);
				
				lstOrder = new ArrayList<ComparisonOrderClassModel>();
				
				// find the likelihood by viterbi decoding of quantized sequence				
				for (int indexSpecies = 0; indexSpecies < species.length; indexSpecies++) {
					double dblLikelihood = hmmModels[indexSpecies].viterbi(quantized);
					
					lstOrder.add(new ComparisonOrderClassModel(species[indexSpecies], dblLikelihood));
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
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * 
	 * @param featureVector
	 * 
	 * @return points
	 */
	private static Points[] getPointsFromFeatureVector(double[][] featureVector) {
		Points points[] = new Points[featureVector.length];
		
		for (int i = 0; i < featureVector.length; i++) {
			points[i] = new Points(featureVector[i]);
		}
		
		return points;
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
class ComparisonOrderClassModel implements Comparable<ComparisonOrderClassModel> {
	private String strSpecies;
	private double dblCorrelation;

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
	protected ComparisonOrderClassModel(String strSpecies, double dblCorrelation) {
		this.strSpecies = strSpecies;
		this.dblCorrelation = dblCorrelation;
	}
	
	@Override
	public int compareTo(ComparisonOrderClassModel objComparisonOrderClassModel) {
		try {
			if (this.dblCorrelation == objComparisonOrderClassModel.dblCorrelation) {
				return 0;
			}
			
			if (this.dblCorrelation > objComparisonOrderClassModel.dblCorrelation) {
	            return -1;
	        }
			
			if (this.dblCorrelation < objComparisonOrderClassModel.dblCorrelation) {
	            return 1;
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			
			return 0;
		}
		
        return 0;
	}
}