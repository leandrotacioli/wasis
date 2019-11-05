package br.unicamp.fnjv.wasis.audio;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import br.unicamp.fnjv.wasis.libs.FileManager;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

/**
 * Classe responsável pelo controle de inclusão/atualização/exclusão dos arquivos de áudio temporários.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 27/Set/2017
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
	 * Classe responsável pelo controle de inclusão/atualização/exclusão dos arquivos de áudio temporários.
	 */
	private AudioTemporary() {
		
	}
	
	/**
	 * Verifica se já existe um arquivo temporário para o arquivo de áudio em processamento na lista da memória.
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
	 * Insere/atualiza o arquivo de áudio da lista da memória.
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
						
						lstAudioTemporary.get(indexAudioTemporary).clearAudioImages();
						lstAudioTemporary.get(indexAudioTemporary).clearAudioSegments();
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
				
				objAudioWav.updateAudioTemporaryIndex();
			}
			
		} catch (Error | Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Cria as imagens temporárias do espectrograma para o índice do áudio temporário em carregamento da lista da memória.
	 * 
	 * @param objAudioWav                - Objeto do arquivo WAV
	 * @param strSpectrogramColorDisplay - Mapa de cores do espectrograma
	 * @param strSpectrogramImagePath    - Caminho da imagem do espectrograma
	 * @param strSpectrogramImageHash    - Hash da imagem do espectrograma
	 * @param intFFTSamples              - Número de amostras da FFT
	 * @param intFFTOverlap              - Fator de sobreposição (OVERLAP) da FFT
	 * @param strFFTWindow               - Função de janelamento da FFT
	 * @param intInitialTime             - Tempo inicial da imagem do espectrograma
	 * @param intFinalTime               - Tempo final da imagem do espectrograma
	 * @param blnCompleteImage           - Determina se é a imagem completa do espectrograma
	 */
	public static void createAudioImage(AudioWav objAudioWav, String strSpectrogramColorDisplay, String strSpectrogramImagePath, String strSpectrogramImageHash, int intFFTSamples, int intFFTOverlap, String strFFTWindow, int intInitialTime, int intFinalTime, boolean blnCompleteImage) {
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			if (objAudioWav.getAudioFilePathOriginal().equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
				lstAudioTemporary.get(indexAudioTemporary).getAudioImages().add(new AudioImagesValues(strSpectrogramColorDisplay, strSpectrogramImagePath, strSpectrogramImageHash, intFFTSamples, intFFTOverlap, strFFTWindow, intInitialTime, intFinalTime, blnCompleteImage));
			}
		}
	}
	
	/**
	 * Cria os segmentos (ROIs) temporários do espectrograma para o índice do áudio temporário em carregamento da lista da memória.
	 * 
	 * @param objAudioWav         - Objeto do arquivo WAV
	 * @param strAudioSegment     - Segmento de Áudio - Ex: A1, A2, B1
	 * @param intInitialTime      - Tempo inicial da seleção temporária
	 * @param intFinalTime        - Tempo final da seleção temporária
	 * @param intInitialFrequency - Frequência inicial da seleção temporária
	 * @param intFinalFrequency   - Frequência final da seleção temporária
	 */
	public static void createAudioSegment(AudioWav objAudioWav, String strAudioSegment, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) {
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			if (objAudioWav.getAudioFilePathOriginal().equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
				lstAudioTemporary.get(indexAudioTemporary).getAudioSegments().add(new AudioSegmentsValues(0, strAudioSegment, intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency));
			}
		}
	}
    
    /**
	 * Verifica se existem segmentos de áudio não gravados de qualquer arquivo de áudio.
	 * 
	 * @return blnAudioSegmentsNotSaved
	 */
	public static boolean checkAudioSegmentsNotSaved() {
		boolean blnAudioSegmentsNotSaved = false;
		
		List<AudioSegmentsValues> lstSegments;
		
		for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
			lstSegments = lstAudioTemporary.get(indexAudioTemporary).getAudioSegments();
			
			for (int indexSegment = 0; indexSegment < lstSegments.size(); indexSegment++) {
				if (lstSegments.get(indexSegment).getIdDatabase() == 0) {
					blnAudioSegmentsNotSaved = true;
					
					break;
				}
			}
			
			if (blnAudioSegmentsNotSaved) {
				break;
			}
		}
		
		return blnAudioSegmentsNotSaved;
	}
	
	/**
	 * Verifica se existem segmentos de áudio não gravados de um arquivo de áudio.
	 * 
	 * @param objAudioWav - Objeto do arquivo WAV
	 * 
	 * @return blnAudioSegmentsNotSaved
	 */
	public static boolean checkAudioSegmentsNotSaved(String strAudioFilePath) {
		boolean blnAudioSegmentsNotSaved = false;
		
		List<AudioSegmentsValues> lstAudioSegments;
				
		if (strAudioFilePath != null) {
			for (int indexAudioTemporary = 0; indexAudioTemporary < lstAudioTemporary.size(); indexAudioTemporary++) {
				if (strAudioFilePath.equals(lstAudioTemporary.get(indexAudioTemporary).getAudioFilePathOriginal())) {
					lstAudioSegments = lstAudioTemporary.get(indexAudioTemporary).getAudioSegments();
					
					for (int indexSegment = 0; indexSegment < lstAudioSegments.size(); indexSegment++) {
						if (lstAudioSegments.get(indexSegment).getIdDatabase() == 0) {
							blnAudioSegmentsNotSaved = true;
							
							break;
						}
					}
					
					break;
				}
			}
		}
			
		return blnAudioSegmentsNotSaved;
	}
	
	/**
	 * Exclui todos os arquivos de áudio temporários, bem como os espectrogramas gerados.
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
			
			for (int indexImages = 0; indexImages < lstAudioTemporary.get(indexAudioTemporary).getAudioImages().size(); indexImages++) {
				fileToDelete = new File(lstAudioTemporary.get(indexAudioTemporary).getAudioImages().get(indexImages).getSpectrogramImagePath());
				fileToDelete.setWritable(true);
				fileToDelete.delete();
			}
		}
	}
}