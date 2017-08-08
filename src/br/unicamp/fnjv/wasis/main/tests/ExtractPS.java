package br.unicamp.fnjv.wasis.main.tests;

import java.util.List;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.database.DatabaseMySQLConnection;
import br.unicamp.fnjv.wasis.features.PowerSpectrum;
import br.unicamp.fnjv.wasis.features.PowerSpectrumValues;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

public class ExtractPS implements Runnable {
	private static String SERVER = "localhost";
	private static String DBNAME = "wasis_mestrado";
	private static String DBUSER = "root";
	private static String DBPASS = "123456";
	
	private AudioWav objAudioWav;
	
	private int intIdAudioSegment;
	private int intInitialTime;
	private int intFinalTime;
	private int intInitialFrequency;
	private int intFinalFrequency;
	
	/**
	 * 
	 * @param objAudioWav
	 * @param intInitialTime
	 * @param intFinalTime
	 * @param intInitialFrequency
	 * @param intFinalFrequency
	 * @throws CloneNotSupportedException
	 */
	protected ExtractPS(AudioWav objAudioWav, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) throws CloneNotSupportedException {
		this.intIdAudioSegment = 0;
		this.objAudioWav = (AudioWav) objAudioWav.clone();
		
		this.intInitialTime = intInitialTime;
		this.intFinalTime = intFinalTime;
		this.intInitialFrequency = intInitialFrequency;
		this.intFinalFrequency = intFinalFrequency;
	}
	
	/**
	 * 
	 * @param intIdAudioSegment
	 * @param objAudioWav
	 * @param intInitialTime
	 * @param intFinalTime
	 * @param intInitialFrequency
	 * @param intFinalFrequency
	 * @throws CloneNotSupportedException
	 */
	protected ExtractPS(int intIdAudioSegment, AudioWav objAudioWav, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) throws CloneNotSupportedException {
		this.intIdAudioSegment = intIdAudioSegment;
		this.objAudioWav = (AudioWav) objAudioWav.clone();
		
		this.intInitialTime = intInitialTime;
		this.intFinalTime = intFinalTime;
		this.intInitialFrequency = intInitialFrequency;
		this.intFinalFrequency = intFinalFrequency;
	}
	
	@Override
	public void run() {
		int intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
		int intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
		
		double[] arrayAmplitudes = objAudioWav.getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
		
		PowerSpectrum objPS = new PowerSpectrum();
		objPS.process(arrayAmplitudes, objAudioWav.getWavHeader().getSampleRate());
		
		List<PowerSpectrumValues> lstPowerSpectrumValues = objPS.filterFrequencies(intInitialFrequency, intFinalFrequency);
		
		/*
		System.out.println(intInitialFrequency + " | " + intFinalFrequency);
		
		for (int indexValue = 0; indexValue < lstPowerSpectrumValues.size(); indexValue++) {
			System.out.println(lstPowerSpectrumValues.get(indexValue).getFrequency() + " " + lstPowerSpectrumValues.get(indexValue).getDecibel());
    	}
		
		
		System.out.println("-----------------------------------");
		*/
		
		// Salva as features no banco de dados
		if (intIdAudioSegment != 0) {
			DatabaseMySQLConnection objDatabaseConnection = new DatabaseMySQLConnection();
			objDatabaseConnection.setDatabaseConnection(SERVER, DBNAME, DBUSER, DBPASS);
			objDatabaseConnection.openConnection();
			
			try {
				for (int indexValue = 0; indexValue < lstPowerSpectrumValues.size(); indexValue++) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments_ps (fk_audio_file_segment, frequency_value, decibel_value) ");
					objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
					objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.INTEGER, intIdAudioSegment);
					objDatabaseConnection.addParameter("frequency_value", LTDataTypes.INTEGER, lstPowerSpectrumValues.get(indexValue).getFrequency());
					objDatabaseConnection.addParameter("decibel_value", LTDataTypes.DOUBLE, lstPowerSpectrumValues.get(indexValue).getDecibel());
					objDatabaseConnection.executeQuery();
				}
				
				objDatabaseConnection.commitTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				objDatabaseConnection.rollBackTransaction();
				objDatabaseConnection.closeConnection();
			}
		}
	}
}