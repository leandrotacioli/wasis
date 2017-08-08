package br.unicamp.fnjv.wasis.main;

import java.sql.ResultSet;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.classifiers.hmm.HMM;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.DatabaseConnection;

public class WasisTrainHMM {

	public static void main(String[] args) {
		// Gera o Codebook		
		int intTotalMFCC = 0;
		
		// Compara com outras seleções já armazenadas no banco de dados
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.animal_genus, aud.animal_species, sel.sound_unit, mfc.mfcc_order, mfc.mfcc_vector ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files_selections_mfcc   mfc ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_selections        sel   ON   mfc.fk_audio_file_selection = sel.id ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                   aud   ON   sel.fk_audio_file           = aud.id ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_genus, aud.animal_species, sel.sound_unit, mfc.mfcc_order ");
		
			ResultSet rsMFCC = objDatabaseConnection.executeSelectQuery();
			
			intTotalMFCC = objDatabaseConnection.getTotalRecords();
			
			objDatabaseConnection.rollBackTransaction();
			
			System.out.println("Total MFCC: " + intTotalMFCC);
			
			int intIndexMFCC = 0;
			Points pts[] = new Points[intTotalMFCC];
			
			String[] strMfccVector;
			double[] mfccVector;
			
			while (rsMFCC.next()) {
				strMfccVector = rsMFCC.getString("mfcc_vector").split(" ");
				
				mfccVector = new double[strMfccVector.length];
				
				for (int indexElement = 0; indexElement < strMfccVector.length; indexElement++) {
					mfccVector[indexElement] = Double.parseDouble(strMfccVector[indexElement]);
				}
				
				pts[intIndexMFCC] = new Points(mfccVector);
				intIndexMFCC++;
			}
			
			System.out.println("");
			System.out.println("***** Generating Codebook *****");
			Codebook cbk = new Codebook(pts);
			cbk.saveToFile("", "codebook");
			System.out.println("***** Codebook Generated *****");
			
			// Treina os modelos HMM
			HMM mkv_original;
			
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files_selections_mfcc   mfc ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_selections        sel   ON   mfc.fk_audio_file_selection = sel.id ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                   aud   ON   sel.fk_audio_file           = aud.id ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY aud.animal_genus, aud.animal_species ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_genus, aud.animal_species ");
			
			ResultSet rsClass = objDatabaseConnection.executeSelectQuery();
			ResultSet rsClassSelections;
			ResultSet rsClassSelectionsMFCC;
			
			int quantized[][];  // Sequência de treinamento para uma classe
			double[][] mfccVectorSelection = null;
			Points[] ptsClassMFCC;
			
			while (rsClass.next()) {
				String strGenus = rsClass.getString("animal_genus");
				String strSpecies = rsClass.getString("animal_species");
				
				// Loop através das seleções da espécie
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT sel.id AS id_selection ");
				objDatabaseConnection.sqlCommandAppend("FROM       audio_files_selections_mfcc   mfc ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_selections        sel   ON   mfc.fk_audio_file_selection = sel.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                   aud   ON   sel.fk_audio_file           = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE aud.animal_genus = ? ");
				objDatabaseConnection.sqlCommandAppend("AND aud.animal_species = ? ");
				objDatabaseConnection.sqlCommandAppend("GROUP BY aud.animal_genus, aud.animal_species, sel.id ");
				objDatabaseConnection.sqlCommandAppend("ORDER BY aud.animal_genus, aud.animal_species, sel.id ");
				objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, strGenus);
				objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, strSpecies);
				
				rsClassSelections = objDatabaseConnection.executeSelectQuery();
				
				quantized = new int[objDatabaseConnection.getTotalRecords()][];
				
				int intIndexClassSelection = 0;
				
				System.out.println("Registros de " + strGenus + " " + strSpecies + ": " + objDatabaseConnection.getTotalRecords());
				
				while (rsClassSelections.next()) {
					System.out.println("Seleção: " + (intIndexClassSelection + 1));
					
					// Loop através dos vetores de uma seleção
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT mfc.mfcc_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_selections_mfcc   mfc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_selections        sel   ON   mfc.fk_audio_file_selection = sel.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                   aud   ON   sel.fk_audio_file           = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE sel.id = ? ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY mfc.mfcc_order ");
					objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, rsClassSelections.getInt("id_selection"));
					
					rsClassSelectionsMFCC = objDatabaseConnection.executeSelectQuery();
					
					int intIndexClassSelectionMFCC = 0;
					
					mfccVectorSelection = new double[objDatabaseConnection.getTotalRecords()][13];
					
					while (rsClassSelectionsMFCC.next()) {
						strMfccVector = rsClassSelectionsMFCC.getString("mfcc_vector").split(" ");
						
						mfccVector = new double[strMfccVector.length];
						
						for (int indexElement = 0; indexElement < strMfccVector.length; indexElement++) {
							mfccVector[indexElement] = Double.parseDouble(strMfccVector[indexElement]);
						}
						
						mfccVectorSelection[intIndexClassSelectionMFCC] = mfccVector;
						
						intIndexClassSelectionMFCC++;
					}
					
					ptsClassMFCC = getPointsFromFeatureVector(mfccVectorSelection);
					quantized[intIndexClassSelection] = cbk.quantize(ptsClassMFCC);
					
					intIndexClassSelection++;
				}
				
				System.out.println("***** Treinando: " + strGenus + " " + strSpecies + " *****");
				
				mkv_original = new HMM(6, 256);
				mkv_original.train(quantized, 20);
				mkv_original.save("", strGenus + " " + strSpecies);
				
				System.out.println("***** Treinamento Concluído: " + strGenus + " " + strSpecies + " *****");
				System.out.println();
			}
			
			objDatabaseConnection.rollBackTransaction();
			
			System.out.println("*****************************************************************");
			System.out.println("Treinamento de todas as classes concluídas!!!");
			System.out.println("*****************************************************************");
			
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
	 * @return
	 */
	private static Points[] getPointsFromFeatureVector(double[][] mfccVector) {
		Points pts[] = new Points[mfccVector.length];
		
		for (int j = 0; j < mfccVector.length; j++) {
			pts[j] = new Points(mfccVector[j]);
		}
		
		return pts;
	}
}