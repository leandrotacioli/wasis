package br.unicamp.fnjv.wasis.database.dto;

/**
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 29/Mar/2018
 */
public class AudioLibraryDTO {
	private long lgnIdAudioLibrary;
	private String strLibraryName;
	private String strLibraryDescription;
	private String strLibraryObservations;
	
	public long getIdAudioLibrary() {
		return lgnIdAudioLibrary;
	}

	public void setIdAudioLibrary(long lgnIdAudioLibrary) {
		this.lgnIdAudioLibrary = lgnIdAudioLibrary;
	}

	public String getLibraryName() {
		return strLibraryName;
	}

	public void setLibraryName(String strLibraryName) {
		this.strLibraryName = strLibraryName;
	}

	public String getLibraryDescription() {
		return strLibraryDescription;
	}

	public void setLibraryDescription(String strLibraryDescription) {
		this.strLibraryDescription = strLibraryDescription;
	}

	public String getLibraryObservations() {
		return strLibraryObservations;
	}

	public void setLibraryObservations(String strLibraryObservations) {
		this.strLibraryObservations = strLibraryObservations;
	}

	public AudioLibraryDTO() {

	}
}