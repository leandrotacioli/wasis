package br.unicamp.fnjv.wasis.main.tests;

import java.sql.ResultSet;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.classifiers.hmm.HMM;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;

public class TrainHMMSpecies implements Runnable {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private Codebook objCodebook;
	
	private int intFeature;
	private String strExperimentName;
	private String strTrainingExperiment;
	private String strTestingExperiment;
	private String strAnimalClass;
	private String strAnimalGenus;
	private String strAnimalSpecies;
	
	protected TrainHMMSpecies(Codebook objCodebook, int intFeature, String strExperimentName, String strTrainingExperiment, String strTestingExperiment, String strAnimalClass, String strAnimalGenus, String strAnimalSpecies) throws CloneNotSupportedException {
		this.objCodebook = (Codebook) objCodebook.clone();
		
		this.intFeature = intFeature;
		this.strExperimentName = strExperimentName;
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
			// Loop através dos segmentos da espécie
			objDatabaseConnection.initiliazeStatement();
			
			// MFCC
			if (intFeature == Experiments.MFCC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   fea ");
				
			// LPC
			} else if (intFeature == Experiments.LPC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpc    fea ");
				
			// LPCC
			} else if (intFeature == Experiments.LPCC) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpcc   fea ");
				
			// PLP
			} else if (intFeature == Experiments.PLP) {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_plp    fea ");
				
			// FUSION
			} else {
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   fea ");
			}
			
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file         = aud.id ");
			objDatabaseConnection.sqlCommandAppend("WHERE aud.id > 0 ");
			
			if (!strAnimalClass.equals(Experiments.ALL_CLASSES)) {
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_class = ? ");
				objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
			}
			
			objDatabaseConnection.sqlCommandAppend("AND aud.animal_genus = ? ");
			objDatabaseConnection.sqlCommandAppend("AND aud.animal_species = ? ");
			objDatabaseConnection.sqlCommandAppend("AND aud.id IN (" + strTrainingExperiment + ") ");
			objDatabaseConnection.sqlCommandAppend("AND aud.id NOT IN (" + strTestingExperiment + ") ");
			objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY seg.id ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id ");
			objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, strAnimalGenus);
			objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, strAnimalSpecies);
			
			ResultSet rsSpeciesSegments = objDatabaseConnection.executeSelectQuery();
			ResultSet rsSpeciesSegmentsFeatures;
			
			int[][] trainingSequence = new int[objDatabaseConnection.getTotalRecords()][];   // Sequência de treinamento para uma classe (espécie)
			
			//System.out.println("Total de Segmentos da espécie " + strAnimalGenus + " " + strAnimalSpecies + ":	" + + objDatabaseConnection.getTotalRecords());
			
			int intIndexSegment = 0;
			
			double[][] featureVectors = null;
			String[] originalFeatureVector;
			double[] featureVector;
			
			Points[] pointsFeatureVectors;
			
			//int intTotalFeatureVectors = 0;
			
			while (rsSpeciesSegments.next()) {
				// Loop através dos vetores de um segmento
				// MFCC
				if (intFeature == Experiments.MFCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.mfcc_order, fea.mfcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
					
				} else if (intFeature == Experiments.LPC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.lpc_order, fea.lpc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpc    fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.lpc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
					
				} else if (intFeature == Experiments.LPCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.lpcc_order, fea.lpcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_lpcc   fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.lpcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
					
				} else if (intFeature == Experiments.PLP) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT fea.id AS id_vector, fea.plp_order, fea.plp_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_plp    fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   fea.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.plp_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
					
				} else if (intFeature == Experiments.MFCC_LPC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpc    lpc    ON   lpc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("WHERE mfcc.fk_audio_file_segment = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpc.lpc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
					
				} else if (intFeature == Experiments.MFCC_LPCC) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', lpcc.lpcc_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpcc   lpcc    ON   lpcc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("WHERE mfcc.fk_audio_file_segment = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpcc.lpcc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
					
				} else if (intFeature == Experiments.MFCC_PLP) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', plp.plp_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_plp    plp    ON   plp.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("WHERE mfcc.fk_audio_file_segment = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = plp.plp_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
					
				} else if (intFeature == Experiments.MFCC_LPC_LPCC_PLP) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT CONCAT(mfcc.mfcc_vector, ';', lpc.lpc_vector, ';', lpcc.lpcc_vector, ';', plp.plp_vector) AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpc    lpc    ON   lpc.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_lpcc   lpcc   ON   lpcc.fk_audio_file_segment = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments_plp    plp    ON   plp.fk_audio_file_segment  = mfcc.fk_audio_file_segment ");
					objDatabaseConnection.sqlCommandAppend("WHERE mfcc.fk_audio_file_segment = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpc.lpc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = lpcc.lpcc_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.mfcc_order = plp.plp_order ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND lpc.ind_normalized = 0");
					objDatabaseConnection.sqlCommandAppend("AND lpcc.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("AND plp.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfcc.mfcc_order ");
					objDatabaseConnection.addParameter("id_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_segment"));
				}
				
				rsSpeciesSegmentsFeatures = objDatabaseConnection.executeSelectQuery();
				
				featureVectors = new double[objDatabaseConnection.getTotalRecords()][];
				
				int intIndexFeatureVector = 0;
				
				while (rsSpeciesSegmentsFeatures.next()) {
					originalFeatureVector = rsSpeciesSegmentsFeatures.getString("feature_vector").split(";");
					
					featureVector = new double[originalFeatureVector.length];
					
					for (int indexElement = 0; indexElement < originalFeatureVector.length; indexElement++) {
						featureVector[indexElement] = Double.parseDouble(originalFeatureVector[indexElement]);
					}
					
					featureVectors[intIndexFeatureVector] = featureVector;
					
					intIndexFeatureVector++;
					//intTotalFeatureVectors++;
				}
				
				pointsFeatureVectors = getPointsFromFeatureVector(featureVectors);
				trainingSequence[intIndexSegment] = objCodebook.quantize(pointsFeatureVectors);
				
				intIndexSegment++;
			}
			
			//System.out.println("Total de Vetores da espécie " + strAnimalGenus + " " + strAnimalSpecies + ":	"+ intTotalFeatureVectors);
			
			// ***********************************************************************************************
			// Treinamento
			HMM objHMM = new HMM(5, 256);
			objHMM.train(trainingSequence, 20);
			objHMM.save(strExperimentName, strAnimalGenus + " " + strAnimalSpecies);
			
			//System.out.println("Treinamento Concluído: " + strAnimalGenus + " " + strAnimalSpecies);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * 
	 * @param features
	 * 
	 * @return
	 */
	private Points[] getPointsFromFeatureVector(double[][] featureVector) {
		Points points[] = new Points[featureVector.length];
		
		for (int i = 0; i < featureVector.length; i++) {
			points[i] = new Points(featureVector[i]);
		}
		
		return points;
	}
}