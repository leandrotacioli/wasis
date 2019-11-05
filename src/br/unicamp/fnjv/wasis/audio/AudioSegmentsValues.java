package br.unicamp.fnjv.wasis.audio;

/**
 * Modelo para listagens de segmentos de áudio (ROIs) dos arquivos de áudio.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 22/Set/2017
 */
public class AudioSegmentsValues {
	private long lgnIdDatabase;          // ID do segmento de áudio no banco de dados
	private String strAudioSegment;      // Segmento de áudio. Ex: A1, A2, B1
	private int intInitialTime;          // Tempo inicial da seleção temporária
	private int intFinalTime;            // Tempo final da seleção temporária
	private int intInitialFrequency;     // Frequência inicial da seleção temporária
	private int intFinalFrequency;       // Frequência final da seleção temporária
	private String strAnimalGenus;       // Gênero do animal da seleção temporária
	private String strAnimalSpecies;     // Espécie do animal da seleção temporária
	
	/**
	 * Retorna o ID do segmento de áudio no banco de dados.
	 * 
	 * @return lgnIdDatabase
	 */
	public long getIdDatabase() {
		return lgnIdDatabase;
	}
	
	/**
	 * Altera o ID do segmento de áudio no banco de dados.
	 * 
	 * @param lgnIdDatabase
	 */
	public void setIdDatabase(long lgnIdDatabase) {
		this.lgnIdDatabase = lgnIdDatabase;
	}
	
	/**
	 * Retorna o segmento de áudio.
	 * 
	 * @return strAudioSegment
	 */
	public String getAudioSegment() {
		return strAudioSegment;
	}

	/**
	 * Retorna o tempo inicial.
	 * 
	 * @return intInitialTime
	 */
	public int getInitialTime() {
		return intInitialTime;
	}

	/**
	 * Retorna o tempo final.
	 * 
	 * @return intFinalTime
	 */
	public int getFinalTime() {
		return intFinalTime;
	}

	/**
	 * Retorna a frequência inicial.
	 * 
	 * @return intInitialFrequency
	 */
	public int getInitialFrequency() {
		return intInitialFrequency;
	}

	/**
	 * Retorna a frequência inicial.
	 * 
	 * @return intFinalFrequency
	 */
	public int getFinalFrequency() {
		return intFinalFrequency;
	}
	
	/**
	 * Retorna o gênero do animal.
	 * 
	 * @return strAnimalGenus
	 */
	public String getAnimalGenus() {
		return strAnimalGenus;
	}
	
	/**
	 * Altera o gênero do animal.
	 * 
	 * @param strAnimalGenus
	 */
	public void setAnimalGenus(String strAnimalGenus) {
		this.strAnimalGenus = strAnimalGenus;
	}
	
	/**
	 * Retorna a espécie do animal.
	 * 
	 * @return strAnimalSpecies
	 */
	public String getAnimalSpecies() {
		return strAnimalSpecies;
	}
	
	/**
	 * Altera a espécie do animal.
	 * 
	 * @param strAnimalSpecies
	 */
	public void setAnimalSpecies(String strAnimalSpecies) {
		this.strAnimalSpecies = strAnimalSpecies;
	}
	
	/**
	 * Modelo para listagens de segmentos de áudio (ROIs) dos arquivos de áudio.
	 * 
	 * @param lgnIdDatabase       - ID do segmento de áudio no banco de dados
	 * @param strAudioSegment     - Segmento de áudio - Ex: A1, A2, B1
	 * @param intInitialTime      - Tempo inicial
	 * @param intFinalTime        - Tempo final
	 * @param intInitialFrequency - Frequência inicial
	 * @param intFinalFrequency   - Frequência final
	 */
	public AudioSegmentsValues(long lgnIdDatabase, String strAudioSegment, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) {
		this.lgnIdDatabase = lgnIdDatabase;
		this.strAudioSegment = strAudioSegment;
		this.intInitialTime = intInitialTime;
		this.intFinalTime = intFinalTime;
		this.intInitialFrequency = intInitialFrequency;
		this.intFinalFrequency = intFinalFrequency;
	}
	
	/**
	 * Modelo para listagens de segmentos de áudio (ROIs) dos arquivos de áudio.
	 * 
	 * @param intIdDatabase       - ID do segmento de áudio no banco de dados
	 * @param strAudioSegment     - Segmento de áudio - Ex: A1, A2, B1
	 * @param intInitialTime      - Tempo inicial
	 * @param intFinalTime        - Tempo final
	 * @param intInitialFrequency - Frequência inicial
	 * @param intFinalFrequency   - Frequência final
	 * @param strAnimalGenus      - Gênero do animal
	 * @param strAnimalSpecies    - Espécie do animal
	 */
	public AudioSegmentsValues(long lgnIdDatabase, String strAudioSegment, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency, String strAnimalGenus, String strAnimalSpecies) {
		this.lgnIdDatabase = lgnIdDatabase;
		this.strAudioSegment = strAudioSegment;
		this.intInitialTime = intInitialTime;
		this.intFinalTime = intFinalTime;
		this.intInitialFrequency = intInitialFrequency;
		this.intFinalFrequency = intFinalFrequency;
		this.strAnimalGenus = strAnimalGenus;
		this.strAnimalSpecies = strAnimalSpecies;
	}
}