package br.unicamp.fnjv.wasis.audio.temporary;

/**
 * Modelo para listagens de seleções dos arquivos de áudio temporários.
 * 
 * @author Leandro Tacioli
 * @version 1.2 - 08/Mai/2017
 */
public class AudioTemporarySegments {
	private long lgnIdDatabase;          // ID da seleção no banco de dados
	private String strSoundUnit;         // Unidade de som. Ex: A1, A2, B1
	private int intInitialTime;          // Tempo inicial da seleção temporária
	private int intFinalTime;            // Tempo final da seleção temporária
	private int intInitialFrequency;     // Frequência inicial da seleção temporária
	private int intFinalFrequency;       // Frequência final da seleção temporária
	private String strAnimalGenus;       // Gênero do animal da seleção temporária
	private String strAnimalSpecies;     // Espécie do animal da seleção temporária
	
	/**
	 * Retorna o ID da seleção no banco de dados.
	 * 
	 * @return lgnIdDatabase
	 */
	public long getIdDatabase() {
		return lgnIdDatabase;
	}
	
	/**
	 * Altera o ID da seleção no banco de dados.
	 * 
	 * @param lgnIdDatabase
	 */
	public void setIdDatabase(long lgnIdDatabase) {
		this.lgnIdDatabase = lgnIdDatabase;
	}
	
	/**
	 * Retorna a unidade de som da seleção temporária.
	 * 
	 * @return strSoundUnit
	 */
	public String getSoundUnit() {
		return strSoundUnit;
	}

	/**
	 * Retorna o tempo inicial da seleção temporária.
	 * 
	 * @return intInitialTime
	 */
	public int getInitialTime() {
		return intInitialTime;
	}

	/**
	 * Retorna o tempo final da seleção temporária.
	 * 
	 * @return intFinalTime
	 */
	public int getFinalTime() {
		return intFinalTime;
	}

	/**
	 * Retorna a frequência inicial da seleção temporária.
	 * 
	 * @return intInitialFrequency
	 */
	public int getInitialFrequency() {
		return intInitialFrequency;
	}

	/**
	 * Retorna a frequência inicial da seleção temporária.
	 * 
	 * @return intFinalFrequency
	 */
	public int getFinalFrequency() {
		return intFinalFrequency;
	}
	
	/**
	 * Retorna o gênero do animal da seleção temporária.
	 * 
	 * @return strAnimalGenus
	 */
	public String getAnimalGenus() {
		return strAnimalGenus;
	}
	
	/**
	 * Altera o gênero do animal da seleção temporária.
	 * 
	 * @param strAnimalGenus
	 */
	public void setAnimalGenus(String strAnimalGenus) {
		this.strAnimalGenus = strAnimalGenus;
	}
	
	/**
	 * Retorna a espécie do animal da seleção temporária.
	 * 
	 * @return strAnimalSpecies
	 */
	public String getAnimalSpecies() {
		return strAnimalSpecies;
	}
	
	/**
	 * Altera a espécie do animal da seleção temporária.
	 * 
	 * @param strAnimalSpecies
	 */
	public void setAnimalSpecies(String strAnimalSpecies) {
		this.strAnimalSpecies = strAnimalSpecies;
	}
	
	/**
	 * Modelo para listagens de seleções dos arquivos de áudio temporários.
	 * 
	 * @param lgnIdDatabase       - ID da seleção no banco de dados
	 * @param strSoundUnit        - Unidade de som - Ex: A1, A2, B1
	 * @param intInitialTime      - Tempo inicial da seleção temporária
	 * @param intFinalTime        - Tempo final da seleção temporária
	 * @param intInitialFrequency - Frequência inicial da seleção temporária
	 * @param intFinalFrequency   - Frequência final da seleção temporária
	 */
	public AudioTemporarySegments(long lgnIdDatabase, String strSoundUnit, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) {
		this.lgnIdDatabase = lgnIdDatabase;
		this.strSoundUnit = strSoundUnit;
		this.intInitialTime = intInitialTime;
		this.intFinalTime = intFinalTime;
		this.intInitialFrequency = intInitialFrequency;
		this.intFinalFrequency = intFinalFrequency;
	}
	
	/**
	 * Modelo para listagens de seleções dos arquivos de áudio temporários.
	 * 
	 * @param intIdDatabase       - ID da seleção no banco de dados
	 * @param strSoundUnit        - Unidade de som - Ex: A1, A2, B1
	 * @param intInitialTime      - Tempo inicial da seleção temporária
	 * @param intFinalTime        - Tempo final da seleção temporária
	 * @param intInitialFrequency - Frequência inicial da seleção temporária
	 * @param intFinalFrequency   - Frequência final da seleção temporária
	 * @param strAnimalGenus      - Gênero do animal da seleção temporária
	 * @param strAnimalSpecies    - Espécie do animal da seleção temporária
	 */
	public AudioTemporarySegments(long lgnIdDatabase, String strSoundUnit, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency, String strAnimalGenus, String strAnimalSpecies) {
		this.lgnIdDatabase = lgnIdDatabase;
		this.strSoundUnit = strSoundUnit;
		this.intInitialTime = intInitialTime;
		this.intFinalTime = intFinalTime;
		this.intInitialFrequency = intInitialFrequency;
		this.intFinalFrequency = intFinalFrequency;
		this.strAnimalGenus = strAnimalGenus;
		this.strAnimalSpecies = strAnimalSpecies;
	}
}