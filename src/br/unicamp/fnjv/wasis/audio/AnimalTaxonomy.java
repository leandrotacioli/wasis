package br.unicamp.fnjv.wasis.audio;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;

/**
 * Classe responsável pelo gerenciamento de taxonomias.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 21/Set/2017
 */
public class AnimalTaxonomy {

	/**
	 * Classe responsável pelo gerenciamento de taxonomias.
	 */
	private AnimalTaxonomy() {
		
	}
	
	/**
	 * Gerencia as taxonomias de animais, verificando a necessidade de
	 * inclusão de um novo registro e/ou atualização dos nomes populares.
	 * 
	 * @param strAnimalPhylum
	 * @param strAnimalClass
	 * @param strAnimalOrder
	 * @param strAnimalFamily
	 * @param strAnimalGenus
	 * @param strAnimalSpecies
	 * @param strAnimalNamePortuguese
	 * @param strAnimalNameEnglish
	 * 
	 * @return lgnIdAnimalTaxonomy
	 */
	public static long manageAnimalTaxonomy(String strAnimalPhylum, String strAnimalClass, String strAnimalOrder,
			                                String strAnimalFamily, String strAnimalGenus, String strAnimalSpecies,
			                                String strAnimalNamePortuguese, String strAnimalNameEnglish) {
		long lgnIdAnimalTaxonomy = 0;
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		// Algumas verificações para campos vazios
		if (strAnimalPhylum == null  || strAnimalPhylum.equals("")  || strAnimalPhylum.length() == 0)  strAnimalPhylum  = "Unidentified";
		if (strAnimalClass == null   || strAnimalClass.equals("")   || strAnimalClass.length() == 0)   strAnimalClass   = "Unidentified";
		if (strAnimalOrder == null   || strAnimalOrder.equals("")   || strAnimalOrder.length() == 0)   strAnimalOrder   = "Unidentified";
		if (strAnimalFamily == null  || strAnimalFamily.equals("")  || strAnimalFamily.length() == 0)  strAnimalFamily  = "Unidentified";
		if (strAnimalGenus == null   || strAnimalGenus.equals("")   || strAnimalGenus.length() == 0)   strAnimalGenus   = "Unidentified";
		if (strAnimalSpecies == null || strAnimalSpecies.equals("") || strAnimalSpecies.length() == 0) strAnimalSpecies = "sp.";
		
		try {
			objDatabaseConnection.openConnection();
			
			// ******************************************************************************************
			// Verificações dos dados taxonômicos
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT id_animal_taxonomy, animal_name_portuguese, animal_name_english ");
			objDatabaseConnection.sqlCommandAppend("FROM animal_taxonomies ");
			objDatabaseConnection.sqlCommandAppend("WHERE animal_phylum = ? ");
			objDatabaseConnection.sqlCommandAppend("AND animal_class = ? ");
			objDatabaseConnection.sqlCommandAppend("AND animal_order = ? ");
			objDatabaseConnection.sqlCommandAppend("AND animal_family = ? ");
			objDatabaseConnection.sqlCommandAppend("AND animal_genus = ? ");
			objDatabaseConnection.sqlCommandAppend("AND animal_species = ? ");
			objDatabaseConnection.addParameter("animal_phylum", LTDataTypes.STRING, strAnimalPhylum);
			objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
			objDatabaseConnection.addParameter("animal_order", LTDataTypes.STRING, strAnimalOrder);
			objDatabaseConnection.addParameter("animal_family", LTDataTypes.STRING, strAnimalFamily);
			objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, strAnimalGenus);
			objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, strAnimalSpecies);
			
			ResultSet rsAnimalTaxonomy = objDatabaseConnection.executeSelectQuery();
			
			String strAnimalNamePortugueseExisting = "";
			String strAnimalNameEnglishExisting = "";
			
			while (rsAnimalTaxonomy.next()) {
				lgnIdAnimalTaxonomy = rsAnimalTaxonomy.getLong("id_animal_taxonomy");
				strAnimalNamePortugueseExisting = rsAnimalTaxonomy.getString("animal_name_portuguese");
				strAnimalNameEnglishExisting = rsAnimalTaxonomy.getString("animal_name_english");
			}
			
			objDatabaseConnection.rollBackTransaction();
			
			// ******************************************************************************************
			// Dados taxonômicos a ser inseridos
			if (lgnIdAnimalTaxonomy == 0) {
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("INSERT INTO animal_taxonomies (animal_phylum, animal_class, animal_order, " +
						                                                       " animal_family, animal_genus, animal_species, " +
						                                                       " animal_name_portuguese, animal_name_english) ");
				objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?) ");
				objDatabaseConnection.addParameter("animal_phylum", LTDataTypes.STRING, strAnimalPhylum);
				objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
				objDatabaseConnection.addParameter("animal_order", LTDataTypes.STRING, strAnimalOrder);
				objDatabaseConnection.addParameter("animal_family", LTDataTypes.STRING, strAnimalFamily);
				objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, strAnimalGenus);
				objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, strAnimalSpecies);
				objDatabaseConnection.addParameter("animal_name_portuguese", LTDataTypes.STRING, strAnimalNamePortuguese);
				objDatabaseConnection.addParameter("animal_name_english", LTDataTypes.STRING, strAnimalNameEnglish);
				objDatabaseConnection.executeQuery();
				
				lgnIdAnimalTaxonomy = objDatabaseConnection.getIdentityKey();
				
				objDatabaseConnection.commitTransaction();
			
			// ******************************************************************************************
			// Atualiza os nomes populares caso sejam informados e estejam diferentes no banco de dados
			} else {
				if (!strAnimalNamePortuguese.equals(strAnimalNamePortugueseExisting) || !strAnimalNameEnglish.equals(strAnimalNameEnglishExisting)) {
					// Atualiza os nomes populares
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("UPDATE animal_taxonomies ");
					objDatabaseConnection.sqlCommandAppend("SET animal_name_portuguese = ?, animal_name_english = ? ");
					objDatabaseConnection.sqlCommandAppend("WHERE id_animal_taxonomy = ? ");
					objDatabaseConnection.addParameter("animal_name_portuguese", LTDataTypes.STRING, strAnimalNamePortuguese);
					objDatabaseConnection.addParameter("animal_name_english", LTDataTypes.STRING, strAnimalNameEnglish);
					objDatabaseConnection.addParameter("id_animal_taxonomy", LTDataTypes.LONG, lgnIdAnimalTaxonomy);
					objDatabaseConnection.executeQuery();
					objDatabaseConnection.commitTransaction();
				}
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lgnIdAnimalTaxonomy;
	}
	
	/**
	 * Retorna o nome científico.
	 * 
	 * @param lgnIdAnimalTaxonomy
	 * 
	 * @return strScientificName[0] = Gênero<br>
	 *         strScientificName[1] = Espécie<br>
	 */
	public static String[] getScientificName(long lgnIdAnimalTaxonomy) {
		String[] strScientificName = new String[2];
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT animal_genus, animal_species ");
			objDatabaseConnection.sqlCommandAppend("FROM animal_taxonomies ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_animal_taxonomy = ? ");
			objDatabaseConnection.addParameter("id_animal_taxonomy", LTDataTypes.LONG, lgnIdAnimalTaxonomy);
			
			ResultSet rsAnimalTaxonomy = objDatabaseConnection.executeSelectQuery();
			
			while(rsAnimalTaxonomy.next()) {
				strScientificName[0] = rsAnimalTaxonomy.getString("animal_genus");
				strScientificName[1] = rsAnimalTaxonomy.getString("animal_species");
						
				break;
			}
	
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return strScientificName;
	}
}