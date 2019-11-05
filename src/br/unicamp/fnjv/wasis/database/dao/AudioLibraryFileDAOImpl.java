package br.unicamp.fnjv.wasis.database.dao;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.dto.AudioLibraryDTO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryFileDTO;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;

/**
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 10/Abr/2018
 */
public class AudioLibraryFileDAOImpl implements AudioLibraryFileDAO {

	@Override
	public List<AudioLibraryFileDTO> getAudioLibraryFiles(AudioLibraryDTO objAudioLibrary) throws Exception {
		List<AudioLibraryFileDTO> lstAudioLibraryFiles = new ArrayList<AudioLibraryFileDTO>();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT id_audio_library_file, audio_file_path, audio_file_position, audio_file_sample ");
			objDatabaseConnection.sqlCommandAppend("FROM audio_libraries_files ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_library = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY audio_file_path ");
			objDatabaseConnection.addParameter("fk_audio_library", LTDataTypes.LONG, objAudioLibrary.getIdAudioLibrary());
			
		    ResultSet rsAudioLibraryFiles = objDatabaseConnection.executeSelectQuery();
		    
		    AudioLibraryFileDTO objAudioLibraryFile;
		    
		    while (rsAudioLibraryFiles.next()) {
		    	objAudioLibraryFile = new AudioLibraryFileDTO();
		    	objAudioLibraryFile.setIdAudioLibraryAudioFile(rsAudioLibraryFiles.getLong("id_audio_library_file"));
		    	objAudioLibraryFile.setAudioFilePath(rsAudioLibraryFiles.getString("audio_file_path"));
		    	objAudioLibraryFile.setAudioFilePosition(rsAudioLibraryFiles.getInt("audio_file_position"));
		    	objAudioLibraryFile.setAudioFileSample(rsAudioLibraryFiles.getString("audio_file_sample"));
		    	
		    	lstAudioLibraryFiles.add(objAudioLibraryFile);
		    }
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lstAudioLibraryFiles;
	}
	
	@Override
	public AudioLibraryFileDTO saveAudioLibraryFile(AudioLibraryDTO objAudioLibrary, AudioLibraryFileDTO objAudioLibraryFile) throws Exception {
		if (objAudioLibrary.getIdAudioLibrary() == 0 || objAudioLibraryFile.getAudioFilePath() == null || objAudioLibraryFile.getAudioFilePath().equals("")) {
			return null;
		}
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("INSERT INTO audio_libraries_files (fk_audio_library, audio_file_path, audio_file_position, audio_file_sample) ");
			objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?) ");
			objDatabaseConnection.addParameter("fk_audio_library", LTDataTypes.LONG, objAudioLibrary.getIdAudioLibrary());
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, objAudioLibraryFile.getAudioFilePath());
			objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.STRING, objAudioLibraryFile.getAudioFilePosition());
			objDatabaseConnection.addParameter("audio_file_sample", LTDataTypes.STRING, objAudioLibraryFile.getAudioFileSample());
			objDatabaseConnection.executeQuery();
			
			objAudioLibraryFile.setIdAudioLibraryAudioFile(objDatabaseConnection.getIdentityKey());
			
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return objAudioLibraryFile;
	}
	
	@Override
	public List<AudioLibraryFileDTO> saveAudioLibraryFiles(AudioLibraryDTO objAudioLibrary, List<AudioLibraryFileDTO> lstAudioLibraryFiles) throws Exception {
		if (objAudioLibrary.getIdAudioLibrary() == 0) {
			return null;
		}
		
		try {
			for (AudioLibraryFileDTO objAudioLibraryFile : lstAudioLibraryFiles) {
				objAudioLibraryFile = saveAudioLibraryFile(objAudioLibrary, objAudioLibraryFile);
			}
			
		} catch (Exception e) {
			throw new Exception(e);
		}
		
		return lstAudioLibraryFiles;
	}

	@Override
	public void updateAudioLibraryFilePosition(AudioLibraryDTO objAudioLibrary, AudioLibraryFileDTO objAudioLibraryFile) throws Exception {
		if (objAudioLibrary.getIdAudioLibrary() == 0 || objAudioLibraryFile.getAudioFilePath() == null || objAudioLibraryFile.getAudioFilePath().equals("")) {
			return;
		}
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE audio_libraries_files ");
			objDatabaseConnection.sqlCommandAppend("SET audio_file_position = ? ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_library = ? ");
			objDatabaseConnection.sqlCommandAppend("AND audio_file_path = ? ");
			objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.INTEGER, objAudioLibraryFile.getAudioFilePosition());
			objDatabaseConnection.addParameter("fk_audio_library", LTDataTypes.LONG, objAudioLibrary.getIdAudioLibrary());
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, objAudioLibraryFile.getAudioFilePath());
			objDatabaseConnection.executeQuery();
			
			// Acerta as posições dos outros registros para não ficar bagunçado no banco de dados
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT id_audio_library_file ");
			objDatabaseConnection.sqlCommandAppend("FROM audio_libraries_files ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_library = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY audio_file_position ");
			objDatabaseConnection.addParameter("fk_audio_library", LTDataTypes.LONG, objAudioLibrary.getIdAudioLibrary());
			
		    ResultSet rsAudioLibraryFiles = objDatabaseConnection.executeSelectQuery();
		    
		    int intIndexPosition = 1;
		    
		    while (rsAudioLibraryFiles.next()) {
		    	objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("UPDATE audio_libraries_files ");
				objDatabaseConnection.sqlCommandAppend("SET audio_file_position = ? ");
				objDatabaseConnection.sqlCommandAppend("WHERE id_audio_library_file = ? ");
				objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.INTEGER, intIndexPosition);
				objDatabaseConnection.addParameter("id_audio_library_file", LTDataTypes.LONG, rsAudioLibraryFiles.getLong("id_audio_library_file"));
				objDatabaseConnection.executeQuery();
				
				intIndexPosition++;
		    }
			
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	@Override
	public void updateAudioLibraryFilesPositions(AudioLibraryDTO objAudioLibrary, int intTotalPositionsToJump) throws Exception {
		if (objAudioLibrary.getIdAudioLibrary() == 0) {
			return;
		}
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE audio_libraries_files ");
			objDatabaseConnection.sqlCommandAppend("SET audio_file_position = audio_file_position + ? ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_library = ? ");
			objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.INTEGER, intTotalPositionsToJump);
			objDatabaseConnection.addParameter("fk_audio_library", LTDataTypes.LONG, objAudioLibrary.getIdAudioLibrary());
			objDatabaseConnection.executeQuery();
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}

	@Override
	public void deleteAudioLibraryFile(AudioLibraryDTO objAudioLibrary, AudioLibraryFileDTO objAudioLibraryFile) throws Exception {
		if (objAudioLibrary.getIdAudioLibrary() == 0 || objAudioLibraryFile.getAudioFilePath() == null || objAudioLibraryFile.getAudioFilePath().equals("")) {
			return;
		}
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("DELETE FROM audio_libraries_files ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_library = ? ");
			objDatabaseConnection.sqlCommandAppend("AND audio_file_path = ? ");
			objDatabaseConnection.sqlCommandAppend("AND audio_file_sample IS NULL ");
			objDatabaseConnection.addParameter("fk_audio_library", LTDataTypes.LONG, objAudioLibrary.getIdAudioLibrary());
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, objAudioLibraryFile.getAudioFilePath());
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