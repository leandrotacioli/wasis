package br.unicamp.fnjv.wasis.database.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseFactory;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryDTO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryFileDTO;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;

/**
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 29/Mar/2018
 */
public class AudioLibraryDAOImpl implements AudioLibraryDAO {

	public AudioLibraryDAOImpl() {
		
	}

	@Override
	public AudioLibraryDTO getAudioLibrary(long lgnIdAudioLibrary) throws Exception {
		AudioLibraryDTO objAudioLibrary = new AudioLibraryDTO();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT id_audio_library, library_name, library_description, library_observations");
			objDatabaseConnection.sqlCommandAppend("FROM audio_libraries");
			objDatabaseConnection.sqlCommandAppend("WHERE id_audio_library = ?");
			objDatabaseConnection.addParameter("id_audio_library", LTDataTypes.LONG, lgnIdAudioLibrary);
			
		    ResultSet rsAudioLibraries = objDatabaseConnection.executeSelectQuery();
		    
		    while (rsAudioLibraries.next()) {
		    	objAudioLibrary.setIdAudioLibrary(rsAudioLibraries.getLong("id_audio_library"));
		    	objAudioLibrary.setLibraryName(rsAudioLibraries.getString("library_name"));
		    	objAudioLibrary.setLibraryDescription(rsAudioLibraries.getString("library_description"));
		    	objAudioLibrary.setLibraryObservations(rsAudioLibraries.getString("library_observations"));
		    }
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return objAudioLibrary;
	}

	@Override
	public List<AudioLibraryDTO> getAudioLibraries() throws Exception {
		List<AudioLibraryDTO> lstAudioLibraries = new ArrayList<AudioLibraryDTO>();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();

		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT id_audio_library, library_name, library_description, library_observations");
			objDatabaseConnection.sqlCommandAppend("FROM audio_libraries");
			objDatabaseConnection.sqlCommandAppend("ORDER BY library_name");
			
		    ResultSet rsAudioLibraries = objDatabaseConnection.executeSelectQuery();
		    
		    AudioLibraryDTO objAudioLibrary;
		    
		    while (rsAudioLibraries.next()) {
		    	objAudioLibrary = new AudioLibraryDTO();
		    	objAudioLibrary.setIdAudioLibrary(rsAudioLibraries.getLong("id_audio_library"));
		    	objAudioLibrary.setLibraryName(rsAudioLibraries.getString("library_name"));
		    	objAudioLibrary.setLibraryDescription(rsAudioLibraries.getString("library_description"));
		    	objAudioLibrary.setLibraryObservations(rsAudioLibraries.getString("library_observations"));
		    	
		    	lstAudioLibraries.add(objAudioLibrary);
		    }
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lstAudioLibraries;
	}

	@Override
	public AudioLibraryDTO saveAudioLibrary(AudioLibraryDTO objAudioLibrary) throws Exception {
		objAudioLibrary = saveAudioLibrary(objAudioLibrary, null);
		
		return objAudioLibrary;
	}
	
	@Override
	public AudioLibraryDTO saveAudioLibrary(AudioLibraryDTO objAudioLibrary, List<AudioLibraryFileDTO> lstAudioLibraryFiles) throws Exception {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("INSERT INTO audio_libraries (library_name, library_description, library_observations) ");
			objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
			objDatabaseConnection.addParameter("library_name", LTDataTypes.STRING, objAudioLibrary.getLibraryName());
			objDatabaseConnection.addParameter("library_description", LTDataTypes.STRING, objAudioLibrary.getLibraryDescription());
			objDatabaseConnection.addParameter("library_observations", LTDataTypes.STRING, objAudioLibrary.getLibraryObservations());
			objDatabaseConnection.executeQuery();
			
			objAudioLibrary.setIdAudioLibrary(objDatabaseConnection.getIdentityKey());
			
			objDatabaseConnection.commitTransaction();
			
			AudioLibraryFileDAO objAudioLibraryFile = DatabaseFactory.createAudioLibraryFileDAO();
			objAudioLibraryFile.saveAudioLibraryFiles(objAudioLibrary, lstAudioLibraryFiles);
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return objAudioLibrary;
	}

	@Override
	public void updateAudioLibrary(AudioLibraryDTO objAudioLibrary) throws Exception {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE audio_libraries ");
			objDatabaseConnection.sqlCommandAppend("SET library_name = ?, library_description = ?, library_observations = ? ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_audio_library = ? ");
			objDatabaseConnection.addParameter("library_name", LTDataTypes.STRING, objAudioLibrary.getLibraryName());
			objDatabaseConnection.addParameter("library_description", LTDataTypes.STRING, objAudioLibrary.getLibraryDescription());
			objDatabaseConnection.addParameter("library_observations", LTDataTypes.STRING, objAudioLibrary.getLibraryObservations());
			objDatabaseConnection.addParameter("id_audio_library", LTDataTypes.LONG, objAudioLibrary.getIdAudioLibrary());
			objDatabaseConnection.executeQuery();
			objDatabaseConnection.commitTransaction();
				
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
}