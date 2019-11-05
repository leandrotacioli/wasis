package br.unicamp.fnjv.wasis.database.dao;

import java.util.List;

import br.unicamp.fnjv.wasis.database.dto.AudioLibraryDTO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryFileDTO;

/**
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 30/Mar/2018
 */
public interface AudioLibraryDAO {
	public AudioLibraryDTO getAudioLibrary(long lgnIdAudioLibrary) throws Exception;
	
	public List<AudioLibraryDTO> getAudioLibraries() throws Exception;
	
	public AudioLibraryDTO saveAudioLibrary(AudioLibraryDTO objAudioLibrary) throws Exception;
	
	public AudioLibraryDTO saveAudioLibrary(AudioLibraryDTO objAudioLibrary, List<AudioLibraryFileDTO> lstAudioFiles) throws Exception;
	
	public void updateAudioLibrary(AudioLibraryDTO objAudioLibrary) throws Exception;
}
