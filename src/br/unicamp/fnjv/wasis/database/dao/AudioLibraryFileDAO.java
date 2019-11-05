package br.unicamp.fnjv.wasis.database.dao;

import java.util.List;

import br.unicamp.fnjv.wasis.database.dto.AudioLibraryDTO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryFileDTO;

/**
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 30/Mar/2018
 */
public interface AudioLibraryFileDAO {
	public List<AudioLibraryFileDTO> getAudioLibraryFiles(AudioLibraryDTO objAudioLibrary) throws Exception;
	
	public AudioLibraryFileDTO saveAudioLibraryFile(AudioLibraryDTO objAudioLibrary, AudioLibraryFileDTO objAudioLibraryFile) throws Exception;
	
	public List<AudioLibraryFileDTO> saveAudioLibraryFiles(AudioLibraryDTO objAudioLibrary, List<AudioLibraryFileDTO> lstAudioLibraryFiles) throws Exception;
	
	public void updateAudioLibraryFilePosition(AudioLibraryDTO objAudioLibrary, AudioLibraryFileDTO objAudioLibraryFile) throws Exception;
	
	public void updateAudioLibraryFilesPositions(AudioLibraryDTO objAudioLibrary, int intTotalPositionsToJump) throws Exception;
	
	public void deleteAudioLibraryFile(AudioLibraryDTO objAudioLibrary, AudioLibraryFileDTO objAudioLibraryFile) throws Exception;
}