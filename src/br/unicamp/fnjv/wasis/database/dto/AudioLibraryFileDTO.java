package br.unicamp.fnjv.wasis.database.dto;

public class AudioLibraryFileDTO {
	private long lgnIdAudioLibraryAudioFile;
	private String strAudioFilePath;
	private int strAudioFilePosition;
	private String strAudioFileSample;
	
	public long getIdAudioLibraryAudioFile() {
		return lgnIdAudioLibraryAudioFile;
	}
	
	public void setIdAudioLibraryAudioFile(long lgnIdAudioLibraryAudioFile) {
		this.lgnIdAudioLibraryAudioFile = lgnIdAudioLibraryAudioFile;
	}
	
	public String getAudioFilePath() {
		return strAudioFilePath;
	}
	
	public void setAudioFilePath(String strAudioFilePath) {
		this.strAudioFilePath = strAudioFilePath;
	}
	
	public int getAudioFilePosition() {
		return strAudioFilePosition;
	}
	
	public void setAudioFilePosition(int strAudioFilePosition) {
		this.strAudioFilePosition = strAudioFilePosition;
	}
	
	public String getAudioFileSample() {
		return strAudioFileSample;
	}
	
	public void setAudioFileSample(String strAudioFileSample) {
		this.strAudioFileSample = strAudioFileSample;
	}
	
	public AudioLibraryFileDTO() {
		
	}
}