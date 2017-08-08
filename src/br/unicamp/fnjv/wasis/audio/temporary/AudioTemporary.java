package br.unicamp.fnjv.wasis.audio.temporary;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.unicamp.fnjv.wasis.libs.FileManager;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

/**
 * Classe responsável pelo controle de inclusão/atualização/exclusão
 * dos arquivos de áudio temporários.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 23/Fev/2016
 */
public class AudioTemporary {
	private static List<AudioTemporaryValues> lstAudioTemporary = new ArrayList<AudioTemporaryValues>();
	
	/**
	 * Retorna a lista de áudios temporários.
	 * 
	 * @return lstAudioTemporary
	 */
	public static List<AudioTemporaryValues> getAudioTemporary() {
		return lstAudioTemporary;
	}

	/**
	 * Classe responsável pelo controle de inclusão/atualização/exclusão
	 * dos arquivos de áudio temporários.
	 */
	private AudioTemporary() {
		
	}
	
	/**
	 * Imprime a lista temporária.
	 */
	public static void getTemporaryList() {
		System.out.println("------------------------------------------------------------------------------------------------------");
		System.out.println("------------------------------------------ Lista Temporária ------------------------------------------");
		
		// Loop na lista de áudios
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			System.out.println("");
			
			System.out.println(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal() + " | " + lstAudioTemporary.get(indexAudioTemporary).getAudioFileHashOriginal() + " | " + lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathTemporary() + " | " + lstAudioTemporary.get(indexAudioTemporary).getAudioFileHashTemporary());
			
			if (lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().size() > 0) {
				System.out.println("");
			}
			
			// Loop na lista de imagens dos áudios
			for (int indexImages = 0; indexImages < lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().size(); indexImages++) {
				System.out.println("Imagem: " +
								   lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getSpectrogramImagePath() + " | " +
								   lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getSpectrogramImageHash() + " | " +
						           lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getFFTSamples() + " | " +
						           lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getFFTOverlap() + " | " +
						           lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getFFTWindow() + " | " +
						           lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getInitialTime() + " | " +
						           lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getFinalTime());
			}
			
			if (lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().size() > 0) {
				System.out.println("");
			}
			
			// Loop na lista de segmentos (ROIs) dos áudios
			for (int indexSegment = 0; indexSegment < lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().size(); indexSegment++) {
				System.out.println("Seleção: " +
									lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().get(indexSegment).getIdDatabase() + " | " +
									lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().get(indexSegment).getSoundUnit() + " | " +
									lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().get(indexSegment).getInitialTime()  + " | " +
									lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().get(indexSegment).getFinalTime()  + " | " +
									lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().get(indexSegment).getInitialFrequency()  + " | " +
									lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().get(indexSegment).getFinalFrequency());
			}
			
			if (indexAudioTemporary != lstAudioTemporary.size() - 1) {
				System.out.println("______________________________________________________________________________________________________");
			}
		}
		
		System.out.println("");
		System.out.println("---------------------------------------- Fim Lista Temporária ----------------------------------------");
		System.out.println("");
	}
	
	/**
	 * Verifica se já existe um arquivo temporário para o arquivo de
	 * áudio em processamento na lista da memória (<i>lstAudioTemporary</i>).
	 * 
	 * @param strAudioFilePath
	 * 
	 * @return strAudioFilePathTemporary
	 */
	public static String getAudioFileFromTemporaryPath(AudioWav objAudioWav) {
		String strAudioFilePathTemporary = null;
		
		String strAudioFilePath = objAudioWav.getAudioFilePathTemporary();
		
		try {
			File fileAudioTemporaryFolder;
			
			for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
				if (strAudioFilePath.equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
					String strAudioFileHashOriginalMemory = lstAudioTemporary.get(indexAudioTemporary).getAudioFileHashOriginal();    // Hash do arquivo original na lista de memória
					String strAudioFilePathTemporaryMemory = lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathTemporary();  // Caminho do arquivo temporário na lista de memória
					String strAudioFileHashTemporaryMemory = lstAudioTemporary.get(indexAudioTemporary).getAudioFileHashTemporary();  // Hash do arquivo temporário na lista de memória
					
					// Pega o hash do arquivo em processamento
					String strAudioProcessingHash = objAudioWav.getAudioFileHashTemporary();
				    
				    // Verifica se o hash do arquivo em processamento é o mesmo do arquivo do caminho original da lista da memória
				    if (strAudioProcessingHash.equals(strAudioFileHashOriginalMemory)) {
				    	
				    	// Pega o hash do arquivo na pasta temporária
				    	fileAudioTemporaryFolder = new File(strAudioFilePathTemporaryMemory);
				    	String strAudioFileHashTemporaryFolder = FileManager.getFileHash(fileAudioTemporaryFolder);
				    	
					    // Verifica se o hash do arquivo temporário existente na pasta temporária é igual da lista da memória
					    if (strAudioFileHashTemporaryMemory.equals(strAudioFileHashTemporaryFolder)) {
					    	strAudioFilePathTemporary = strAudioFilePathTemporaryMemory;
					    }
				    }
					
					break;
				}
			}
			
		} catch (Error | Exception e) {
			e.printStackTrace();
		}

		return strAudioFilePathTemporary;
	}
	
	/**
	 * Retorna o índice do áudio temporário da lista da memória.
	 * 
	 * @param objAudioWav - Objeto do arquivo WAV
	 * 
	 * @return intAudioTemporaryIndex
	 */
	public static int getAudioTemporaryIndex(AudioWav objAudioWav) {
		int intAudioTemporaryIndex = -1;
		
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			if (objAudioWav.getAudioFilePathOriginal().equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
				intAudioTemporaryIndex = indexAudioTemporary;
				
				break;
			}
		}

		return intAudioTemporaryIndex;
	}
	
	/**
	 * Insere/atualiza o arquivo de áudio da lista da memória (<i>lstAudioTemporary</i>).
	 * 
	 * @param objAudioWav - Objeto do arquivo WAV
	 */
	public static void createAudioTemporary(AudioWav objAudioWav) {
		boolean blnAudioFileLoaded = false;
		
		try {
			// Verifica se o arquivo já foi carregado anteriormente na lista da memória
			for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
				if (objAudioWav.getAudioFilePathOriginal().equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
					// Verifica se houve alguma alteração do arquivo atual para um já adicionado anteriormente na lista da memória
					if (!lstAudioTemporary.get(indexAudioTemporary).getAudioFileHashOriginal().equals(objAudioWav.getAudioFileHashOriginal()) ||
							!lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathTemporary().equals(objAudioWav.getAudioFilePathTemporary()) ||
								!lstAudioTemporary.get(indexAudioTemporary).getAudioFileHashTemporary().equals(objAudioWav.getAudioFileHashTemporary())) {
						
						lstAudioTemporary.get(indexAudioTemporary).setAudioFileHashOriginal(objAudioWav.getAudioFileHashOriginal());
						lstAudioTemporary.get(indexAudioTemporary).setAudioFilePathTemporary(objAudioWav.getAudioFilePathTemporary());
						lstAudioTemporary.get(indexAudioTemporary).setAudioFileHashTemporary(objAudioWav.getAudioFileHashTemporary());
						
						lstAudioTemporary.get(indexAudioTemporary).clearAudioTemporaryImages();
						lstAudioTemporary.get(indexAudioTemporary).clearAudioTemporarySegments();
					}
					
					blnAudioFileLoaded = true;
					
					break;
				}
			}
			
			// Arquivo ainda não carregado da lista da memória
			if (!blnAudioFileLoaded) {
				lstAudioTemporary.add(new AudioTemporaryValues(objAudioWav.getAudioFilePathOriginal(), 
						                                       objAudioWav.getAudioFileHashOriginal(), 
						                                       objAudioWav.getAudioFilePathTemporary(),
						                                       objAudioWav.getAudioFileHashTemporary()));
			}
			
		} catch (Error | Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cria as imagens temporárias do espectrograma para 
	 * o índice do áudio temporário em carregamento da lista da memória.
	 * 
	 * @param objAudioWav             - Objeto do arquivo WAV
	 * @param strSpectrogramImagePath - Caminho da imagem do espectrograma
	 * @param strSpectrogramImageHash - Hash da imagem do espectrograma
	 * @param intFFTSamples           - Número de amostras da FFT
	 * @param intFFTOverlap           - Fator de sobreposição (OVERLAP) da FFT
	 * @param strFFTWindow            - Função de janelamento da FFT
	 * @param intInitialTime          - Tempo inicial da imagem do espectrograma
	 * @param intFinalTime            - Tempo final da imagem do espectrograma
	 * @param blnCompleteImage        - Determina se é a imagem completa do espectrograma
	 */
	public static void createAudioTemporaryImage(AudioWav objAudioWav, String strSpectrogramImagePath, String strSpectrogramImageHash, int intFFTSamples, int intFFTOverlap, String strFFTWindow, int intInitialTime, int intFinalTime, boolean blnCompleteImage) {
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			if (objAudioWav.getAudioFilePathOriginal().equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
				lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().add(new AudioTemporaryImages(strSpectrogramImagePath, strSpectrogramImageHash, intFFTSamples, intFFTOverlap, strFFTWindow, intInitialTime, intFinalTime, blnCompleteImage));
			}
		}
	}
	
	/**
	 * Cria os segmentos (ROIs) temporários do espectrograma para 
	 * o índice do áudio temporário em carregamento da lista da memória.
	 * 
	 * @param objAudioWav         - Objeto do arquivo WAV
	 * @param strSoundUnit        - Unidade de som - Ex: A1, A2, B1
	 * @param intInitialTime      - Tempo inicial da seleção temporária
	 * @param intFinalTime        - Tempo final da seleção temporária
	 * @param intInitialFrequency - Frequência inicial da seleção temporária
	 * @param intFinalFrequency   - Frequência final da seleção temporária
	 */
	public static void createAudioTemporarySegment(AudioWav objAudioWav, String strSoundUnit, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) {
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			if (objAudioWav.getAudioFilePathOriginal().equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
				lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments().add(new AudioTemporarySegments(0, strSoundUnit, intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency));
			}
		}
	}
    
    /**
	 * Verifica se existem seleções temporárias de qualquer arquivo de áudio.
	 * 
	 * @return blnSelectionsNotSaved
	 */
	public static boolean checkSelectionsNotSaved() {
		boolean blnSelectionsNotSaved = false;
		
		List<AudioTemporarySegments> lstSegments;
		
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			lstSegments = lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments();
			
			for (int indexSegment = 0; indexSegment < lstSegments.size(); indexSegment++) {
				if (lstSegments.get(indexSegment).getIdDatabase() == 0) {
					blnSelectionsNotSaved = true;
					
					break;
				}
			}
			
			if (blnSelectionsNotSaved) {
				break;
			}
		}
		
		return blnSelectionsNotSaved;
	}
	
	/**
	 * Verifica se existem segmentos (ROIs) temporários de um arquivo de áudio.
	 * 
	 * @param objAudioWav - Objeto do arquivo WAV
	 * 
	 * @return blnSelectionsNotSaved
	 */
	public static boolean checkSelectionsNotSaved(String strAudioFilePath) {
		boolean blnSelectionsNotSaved = false;
		
		List<AudioTemporarySegments> lstSegments;
				
		if (strAudioFilePath != null) {
			for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
				if (strAudioFilePath.equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
					lstSegments = lstAudioTemporary.get(indexAudioTemporary).getAudioTemporarySegments();
					
					for (int indexSegment = 0; indexSegment < lstSegments.size(); indexSegment++) {
						if (lstSegments.get(indexSegment).getIdDatabase() == 0) {
							blnSelectionsNotSaved = true;
							
							break;
						}
					}
					
					break;
				}
			}
		}
			
		return blnSelectionsNotSaved;
	}
	
	/**
	 * Exclui todos os arquivos de áudio temporários.
	 */
	public static void deleteTemporaryFiles() {
		File fileToDelete;
		
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			// Exclui somente arquivos que foram criados temporariamente, mantendo os arquivos originais
			if (lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal() != lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathTemporary()) {
				fileToDelete = new File(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathTemporary());
				fileToDelete.setWritable(true);
				fileToDelete.delete();
			}
			
			for (int indexImages = 0; indexImages < lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().size(); indexImages++) {
				fileToDelete = new File(lstAudioTemporary.get(indexAudioTemporary).getAudioTemporaryImages().get(indexImages).getSpectrogramImagePath());
				fileToDelete.setWritable(true);
				fileToDelete.delete();
			}
		}
	}
}