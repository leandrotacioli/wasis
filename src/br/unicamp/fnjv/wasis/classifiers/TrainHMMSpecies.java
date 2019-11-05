package br.unicamp.fnjv.wasis.classifiers;

import java.sql.ResultSet;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.classifiers.hmm.HMM;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.features.Features;

/**
 * Geração de modelos de classificação do classificador HMM.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 01/Abr/2018
 */
public class TrainHMMSpecies implements Runnable {
	private Codebook objCodebook;
	
	private long lgnIdModelClassHmm;
	private long lgnIdAnimalTaxonomy;
	private String strFeature;
	
	protected TrainHMMSpecies(Codebook objCodebook, long lgnIdModelClassHmm, long lgnIdAnimalTaxonomy, String strFeature) throws CloneNotSupportedException {
		this.objCodebook = (Codebook) objCodebook.clone();
		
		this.lgnIdModelClassHmm = lgnIdModelClassHmm;
		this.lgnIdAnimalTaxonomy = lgnIdAnimalTaxonomy;
		this.strFeature = strFeature;
	}
	
	@Override
	public void run() {
		if (!ScreenModelBuilder.blnCancelOperation) {
			DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
			objDatabaseConnection.openConnection();
			
			try {
				// Loop através dos segmentos da espécie
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT seg.id_audio_segment ");
				objDatabaseConnection.sqlCommandAppend("FROM audio_files_segments_features   fea ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments      seg   ON   fea.fk_audio_file_segment = seg.id_audio_segment ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files               aud   ON   seg.fk_audio_file         = aud.id_audio_file ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN animal_taxonomies         tax   ON   seg.fk_animal_taxonomy    = tax.id_animal_taxonomy ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.id_audio_file > 0 ");
				objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
				objDatabaseConnection.sqlCommandAppend("AND tax.id_animal_taxonomy = ? ");
				objDatabaseConnection.sqlCommandAppend("GROUP BY seg.id_audio_segment, seg.fk_animal_taxonomy ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id_audio_segment, seg.fk_animal_taxonomy ");
				objDatabaseConnection.addParameter("id_animal_taxonomy", LTDataTypes.LONG, lgnIdAnimalTaxonomy);
				
				ResultSet rsSpeciesSegments = objDatabaseConnection.executeSelectQuery();
				ResultSet rsSpeciesSegmentsFeatures;
				
				// Sequência de treinamento para uma classe (gênero + espécie)
				int[][] trainingSequence = new int[objDatabaseConnection.getTotalRecords()][];   
				
				int intIndexSegment = 0;
				
				double[][] featureVectors = null;
				String[] originalFeatureVector;
				double[] featureVector;
				
				Points[] pointsFeatureVectors;
				String strFeatureVector;
				
				// Loop através dos vetores de um segmento
				while (rsSpeciesSegments.next()) {
					strFeatureVector = "";
					
					if (strFeature.equals(Features.MFCC)) {
						strFeatureVector = "fea.mfcc_vector";
					} else if (strFeature.equals(Features.LPC)) {
						strFeatureVector = "fea.lpc_vector";
					} else if (strFeature.equals(Features.LPCC)) {
						strFeatureVector = "fea.lpcc_vector";
					} else if (strFeature.equals(Features.PLP)) {
						strFeatureVector = "fea.plp_vector";
					} else if (strFeature.equals(Features.MFCC_LPC)) {
						strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.lpc_vector)";
					} else if (strFeature.equals(Features.MFCC_LPCC)) {
						strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.lpcc_vector)";
					} else if (strFeature.equals(Features.MFCC_PLP)) {
						strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.plp_vector)";
					} else if (strFeature.equals(Features.MFCC_LPC_LPCC_PLP)) {
						strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.lpc_vector, ';', fea.lpcc_vector, ';', fea.plp_vector)";
					}
					
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT " + strFeatureVector + " AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_features   fea ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments            seg   ON   fea.fk_audio_file_segment = seg.id_audio_segment ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id_audio_segment = ? ");
					objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY fea.frame_number ");
					objDatabaseConnection.addParameter("id_audio_segment", LTDataTypes.INTEGER, rsSpeciesSegments.getInt("id_audio_segment"));
					
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
					}
					
					pointsFeatureVectors = getPointsFromFeatureVector(featureVectors);
					trainingSequence[intIndexSegment] = objCodebook.quantize(pointsFeatureVectors);
					
					intIndexSegment++;
				}
				
				// ***********************************************************************************************
				// Treinamento
				HMM objHMM = new HMM(5, 256);
				objHMM.train(trainingSequence, 20);
				
				saveHmmSpeciesModel(objHMM);
				
				ScreenModelBuilder.updateTotalRecordsProcessed();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				objDatabaseConnection.rollBackTransaction();
				objDatabaseConnection.closeConnection();
			}
		}
	}
	
	/**
	 * Grava o objeto do Codebook no banco de dados.
	 * 
	 * @param objHMM
	 * 
	 * @return lgnIdModelClassHmmSpecies
	 */
	private long saveHmmSpeciesModel(HMM objHMM) throws Exception {
		long lgnIdModelClassHmmSpecies = 0;
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("INSERT INTO class_models_hmm_species (fk_class_model_hmm, fk_animal_taxonomy, species_model) ");
			objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
			objDatabaseConnection.addParameter("fk_class_model_hmm", LTDataTypes.LONG, lgnIdModelClassHmm);
			objDatabaseConnection.addParameter("fk_animal_taxonomy", LTDataTypes.LONG, lgnIdAnimalTaxonomy);
			objDatabaseConnection.addParameter("species_model", null, objHMM);
			objDatabaseConnection.executeQuery();
			
			lgnIdModelClassHmmSpecies = objDatabaseConnection.getIdentityKey();
			
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lgnIdModelClassHmmSpecies;
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