package br.unicamp.fnjv.wasis.multimidia.wav;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.SwingWorker;

import br.unicamp.fnjv.wasis.audio.AudioTemporary;
import br.unicamp.fnjv.wasis.libs.FileManager;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.multimidia.ffmpegwrapper.FfmpegEncoder;
import br.unicamp.fnjv.wasis.multimidia.ffmpegwrapper.FfmpegEncoderAttributes;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;

/**
 * Carrega e processa os dados de um arquivo de áudio.
 * 
 * @author Leandro Tacioli
 * @version 3.5 - 27/Out/2017
 */
public class AudioWav implements Cloneable {
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private AudioWavHeader audioWavHeader;       // Especificações do header do arquivo WAV
	
    private AudioInputStream audioInputStream;   // Fluxo do áudio
    
    private int intAudioTemporaryIndex;          // Índice do arquivo WAV na lista de arquivos temporários da memória
    
    private String strAudioFilePathOriginal;     // Caminho do arquivo de áudio original
    private String strAudioFileHashOriginal;     // Hash do arquivo de áudio original
    
    private String strAudioFilePathTemporary;    // Caminho do arquivo de áudio temporário (convertido para o padrão do WASIS)
    private String strAudioFileHashTemporary;    // Hash do arquivo de áudio temporário
    
    private byte[] wavData;                      // Array dos dados do arquivo WAV
    
    private int intWavDataSize;                  // Tamanho total dos dados do arquivo WAV
    private int intWavDataInitialPosition;       // Mostra a posição inicial que o array dos dados está ocupando
    private int intWavDataFinalPosition;         // Mostra a posição final que o array dos dados está ocupando
    
    private int intNumSamples;                   // Número de amostras do arquivo WAV
    private int intNumSamplesPerChannel;         // Número de amostras por canal do arquivo WAV

    private boolean blnStatusLoaded = false;     // True - Carregamento e processamento finalizado
    private boolean blnStatusCancelled = false;  // True - Conversão de arquivo cancelada
    private boolean blnEndOfFile = false;        // True - Todos os dados do arquivo foram lidos
    
    /**
     * Define os diferentes tipos de status do carregamento e processamento do arquivo de áudio. <br>
     * <br>
     * LOAD_STATUS_UNKNOWN    - Desconhecido (atribuído no construtor)<br>
     * LOAD_STATUS_CONVERTING - Convertendo arquivo para WAV<br>
     * LOAD_STATUS_CANCELLED  - Conversão de arquivo cancelada<br>
     * LOAD_STATUS_LOADING    - Carregando e processando informações do arquivo WAV<br>
     * LOAD_STATUS_LOADED     - Carregamento e processamento finalizado
     */
    private int intStatus;                         // Status - Atual
    private final int LOAD_STATUS_UNKNOWN = 0;     // Status - Desconhecido (atribuído no construtor)
    private final int LOAD_STATUS_CONVERTING = 1;  // Status - Convertendo arquivo para WAV
    private final int LOAD_STATUS_CANCELLED = 2;   // Status - Conversão de arquivo cancelada
    private final int LOAD_STATUS_LOADING = 3;     // Status - Carregando e processando informações do arquivo WAV
    private final int LOAD_STATUS_LOADED = 4;      // Status - Carregamento e processamento finalizado
     
    /**
     * Pasta onde serão armazenados os arquivos temporários, caso haja conversão.
     */
    private final String TARGET_PATH = WasisParameters.getInstance().TEMPORARY_FOLDER;
    
    /**
     * Tamanho máximo do array para não sobrecarregar a memória (16,777,216).
     */
    private final int BYTE_ARRAY_MAX_SIZE = (int) Math.pow(2, 24);
    
    /**
     * Comprimento do buffer (4096).
     */
    private final int BUFFER_LENGTH = (int) Math.pow(2, 12);
    
    /**
     * Retorna o índice do arquivo WAV na lista de arquivos temporários da memória.
     * 
     * @return intAudioTemporaryIndex
     */
    public int getAudioTemporaryIndex() {
    	return intAudioTemporaryIndex;
    }
    
    /**
     * Atualiza o índice do arquivo WAV na lista de arquivos temporários da memória.
     */
    public void updateAudioTemporaryIndex() {
    	this.intAudioTemporaryIndex = AudioTemporary.getAudioTemporaryIndex(this);
    }
    
    /**
     * Retorna o caminho do arquivo de áudio original.
     * 
     * @return strAudioFilePathOriginal
     */
    public String getAudioFilePathOriginal() {
    	return strAudioFilePathOriginal;
    }

    /**
     * Retorna o hash do arquivo de áudio original.
     * 
     * @return strAudioFileHashOriginal
     */
    public String getAudioFileHashOriginal() {
    	return strAudioFileHashOriginal;
    }
    
    /**
     * Retorna o tamanho total (em bytes) do arquivo de áudio original.
     * 
     * @return intAudioFileDataSizeOriginal
     */
    public int getAudioFileDataSizeOriginal() {
    	File file = new File(strAudioFilePathOriginal);
		
    	int intAudioFileDataSizeOriginal = (int) file.length();
		
		return intAudioFileDataSizeOriginal;
    }
    
    /**
     * Retorna o caminho do arquivo WAV temporário.
     * 
     * @return strAudioFilePathTemporary
     */
    public String getAudioFilePathTemporary() {
    	return strAudioFilePathTemporary;
    }
    
    /**
     * Retorna o hash do do arquivo WAV temporário.
     * 
     * @return strAudioFileHashTemporary
     */
    public String getAudioFileHashTemporary() {
    	return strAudioFileHashTemporary;
    }
    
    /**
     * Retorna o nome do arquivo de áudio temporário (sem caminho e extensão).
     * 
     * @return strAudioFileNameTemporary
     */
    public String getAudioFileNameTemporary() {
    	String strAudioFileNameTemporary = "";
    	
		File file = new File(strAudioFilePathTemporary);
		
		strAudioFileNameTemporary = file.getName();
		
		// Verifica qual o índice do último caracter antes da extensão do arquivo
        int intLastIndex = strAudioFileNameTemporary.lastIndexOf('.');

        strAudioFileNameTemporary = strAudioFileNameTemporary.substring(0, intLastIndex);
        
    	return strAudioFileNameTemporary;
    }
    
    /**
     * Retorna o tamanho total (em bytes) do arquivo de áudio temporário.
     * 
     * @return intAudioFileDataSizeTemporary
     */
    public int getAudioFileDataSizeTemporary() {
    	File file = new File(strAudioFilePathTemporary);
		
    	int intAudioFileDataSizeTemporary = (int) file.length();
		
		return intAudioFileDataSizeTemporary;
    }

	/**
	 * Retorna o status do carregamento e processamento do arquivo WAV.
	 * 
	 * @return <i>True</i> - Concluído
	 */
	public boolean getStatusLoaded() {
		return blnStatusLoaded;
	}
	
	/**
	 * Retorna o status da conversão do arquivo de áudio.
	 * 
	 * @return <i>True</i> - Cancelado
	 */
	public boolean getStatusCancelled() {
		return blnStatusCancelled;
	}
	
	/**
	 * Retorna o status da leitura de dados do arquivo de áudio.
	 * 
	 * @return <i>True</i> - Fim do arquivo atingido 
	 * 		   <br>
	 *         <i>False</i> - Arquivo em leitura
	 */
	public boolean getEndOfFile() {
		return blnEndOfFile;
	}
	
	/**
     * Retorna o header do arquivo WAV.
     * 
     * @return audioWavHeader
     */
    public AudioWavHeader getWavHeader() {
    	return audioWavHeader;
    }
    
    /**
     * Retorna a posição inicial que o array dos dados está ocupando.
     * 
     * @return intWavDataInitialPosition
     */
    public int getWavDataInitialPosition() {
		return intWavDataInitialPosition;
	}

    /**
     * Retorna a posição final que o array dos dados está ocupando.
     * 
     * @return intWavDataFinalPosition
     */
	public int getWavDataFinalPosition() {
		return intWavDataFinalPosition;
	}
    
    /**
     * Carrega e processa os dados de um arquivo de áudio.
     * 
     * @param strAudioFilePath - Caminho do arquivo de áudio
     */
    public AudioWav(String strAudioFilePath) {
    	try {
    		this.intAudioTemporaryIndex = -1;
    		
    		this.strAudioFilePathOriginal = strAudioFilePath;
			this.strAudioFileHashOriginal = FileManager.getFileHash(new File(strAudioFilePath));
			
			this.strAudioFilePathTemporary = strAudioFilePathOriginal;
			this.strAudioFileHashTemporary = strAudioFileHashOriginal;
			
	    	this.intStatus = LOAD_STATUS_UNKNOWN;
		} catch (Error | Exception e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Carrega header e dados do arquivo WAV.
     * Confirma se o arquivo tem o formato WAV correto, e se necessária é feita a conversão do arquivo para WAV padrão.
     * 
     * @throws FileNotFoundException
     * @throws Exception 
     */
    public void loadAudio() throws FileNotFoundException, Exception {
    	intStatus = LOAD_STATUS_LOADING;
    	
    	try {
    		// Carrega o 'Header' do arquivo 
    		InputStream inputStream = new FileInputStream(strAudioFilePathTemporary);
    		
			audioWavHeader = new AudioWavHeader(inputStream);
			
	        // Verifica se o 'Header' do arquivo WAV é válido
	        if (audioWavHeader.loadHeader()) {
	        	
	        	// Verifica se o arquivo WAV possui a configuração padrão utilizado pelo WASIS
	        	if (audioWavHeader.getSampleRate() != AudioWavFormat.TARGET_SAMPLE_RATE || audioWavHeader.getBitsPerSample() != AudioWavFormat.TARGET_BIT_RATE) {
	        		checkWavConvertion();
	        		
	        	// Arquivo WAV com todas as especificações corretas
	        	} else {
	    	        extractWavData();
	    	        
	    	        intNumSamples = intWavDataSize / audioWavHeader.getBytesPerSample();
	    	        intNumSamplesPerChannel = intNumSamples / audioWavHeader.getChannels();
	    	        intStatus = LOAD_STATUS_LOADED;
	    	        
	    	        updateAudioTemporaryIndex();
	    	        
	    	        blnStatusLoaded = true;
	        	}
	        	
	        // Não é arquivo WAV - então faz a conversão do arquivo
	        } else {
	        	checkWavConvertion();
	        }
	        
	        inputStream.close();
	        
    	} catch (FileNotFoundException e) {
    		throw new FileNotFoundException();
	        
    	} catch (Exception e) {
    		throw new Exception(e);
    	}
    }
    
    /**
     * Fecha o arquivo de áudio.
     */
    public void closeAudio() {   
    	try {
            if (audioInputStream != null) {
            	audioInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Extrai dados do arquivo WAV e armazena no array de bytes 'wavData'.
     */
    private void extractWavData() {
        try {
        	audioInputStream = AudioSystem.getAudioInputStream(new File(strAudioFilePathTemporary));
        	intWavDataSize = getWavDataSize();
        	
        	// Em caso de um arquivo WAV muito longo, 'intWavDataCurrentMaxPosition' 
        	// é alterado para 'BYTE_ARRAY_MAX_SIZE' para não sobrecarregar a memória
        	// É necessário carregar o método 'extractWavDataChunk' para pegar os dados restantes.
        	intWavDataInitialPosition = 0;
        	intWavDataFinalPosition = intWavDataSize;
	        if (intWavDataFinalPosition > BYTE_ARRAY_MAX_SIZE) {
	        	intWavDataFinalPosition = BYTE_ARRAY_MAX_SIZE;   
	        }

	        wavData = new byte[intWavDataFinalPosition];
	        
	        byte[] arrayBuffer = new byte[BUFFER_LENGTH];
	        
	        int intIndex = 0;
	        
	        while (true) {
	            int intBytesRead = audioInputStream.read(arrayBuffer);

	            if (intBytesRead == -1) {
	                break;
	            } else {
        	        for (int indexBytesRead = 0; indexBytesRead < intBytesRead; indexBytesRead++) {
        	        	if (intIndex >= intWavDataFinalPosition) {
        	        		break;
        	        	}
        	        	
        	        	wavData[intIndex] = arrayBuffer[indexBytesRead];
        	        	intIndex++;
        	        }
    	        }
	        }

        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Extrai dados a partir de um pedaço específico do arquivo WAV e armazena no array de bytes 'wavData'.
     * Esse método é utilizado em caso de um arquivo WAV muito grande, 
     * então o arquivo é processado em diferentes partes para não sobrecarregar a CPU e memória.
     * 
     * @param intInitialChunk - Ponto inicial do pedaço específico do arquivo WAV
     */
    public void extractWavDataChunk(int intInitialChunk) {
        try {
        	blnEndOfFile = false;
        	
        	audioInputStream = AudioSystem.getAudioInputStream(new File(strAudioFilePathTemporary));
        	
        	intInitialChunk = getChunkDataPosition(intInitialChunk);
        	
        	int intArraySize = BYTE_ARRAY_MAX_SIZE;
        	
        	intWavDataInitialPosition = intInitialChunk;
        	intWavDataFinalPosition = intInitialChunk + BYTE_ARRAY_MAX_SIZE;
        	
        	if (intWavDataFinalPosition > intWavDataSize) {
        		intWavDataFinalPosition = intWavDataSize;
        		intArraySize = intWavDataFinalPosition - intWavDataInitialPosition;
        	}
        	
	        wavData = new byte[intArraySize];
	        
	        byte[] arrayBuffer = new byte[BUFFER_LENGTH];
	        
	        boolean blnDataExtracted = false;
	        
	        int intIndex = 0;
	        int intTotalBytesRead = 0;
	        
	        while (true) {
	            int intBytesRead = audioInputStream.read(arrayBuffer);

	            if (intBytesRead == -1) {
	                break;
	                
	            } else {
	            	if (intTotalBytesRead + intBytesRead < intInitialChunk) {
	            		intTotalBytesRead += intBytesRead;
	            		
	            	} else {
		            	for (int indexBytesRead = 0; indexBytesRead < intBytesRead; indexBytesRead++) {
		            		if (intTotalBytesRead >= intInitialChunk) {
		            			if (intIndex < intArraySize) {
	            					wavData[intIndex] = arrayBuffer[indexBytesRead];
	            					intIndex++;
	        					} else {
	        						blnDataExtracted = true;
	    	            			break;       // Finishes FOR loop
	        					}
		            		}
		            		
		            		intTotalBytesRead++;
		            	}
		            	
		            	if (blnDataExtracted) {
		            		break;               // Finishes WHILE loop
		            	}
		            }
    	        }
	        }

        } catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Retorna o tamanho total (em bytes) do arquivo WAV a ser processado.<br>
     * <br>
     * O tamanho do <i>Header</i> do arquivo é desconsiderado.
     * 
     * @return intWavDataSize
     */
    public int getWavDataSize() {
    	int intWavDataSize = 0;
    	
    	try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(strAudioFilePathTemporary));
			
	        byte[] arrayBuffer = new byte[BUFFER_LENGTH];
	        
	        while (true) {
	            int intBytesRead = audioInputStream.read(arrayBuffer);

	            if (intBytesRead == -1) {
	                break;
	            } else {
	            	intWavDataSize += intBytesRead;
	            }
	        }
	        
    	} catch (IOException | UnsupportedAudioFileException e) {
            e.printStackTrace();
		}

    	return intWavDataSize;
    }
    
    /**
     * Retorna o número total de amostras por canal.
     * 
     * @return intNumSamplesPerChannel
     */
    public int getNumSamplesPerChannel() {
    	return intNumSamplesPerChannel;
    }
    
  	/**
     * Retorna o tempo total do áudio em milisegundos.
     * 
     * @return intTotalTime
     */
    public int getTotalTime() {
    	int intTotalTime = (int) (intWavDataSize * 1000L / audioWavHeader.getByteRate());
    	
        return intTotalTime;
    }
    
    /**
     * Retorna a posição em bytes de um pedaço do áudio
     * levando em consideração um pedaço da amostra.
     * 
     * @param intChunk - Pedaço da amostra 'sample' baseado no tempo do áudio
     * 
     * @return intChunkDataPosition
     */
    public int getChunkDataPosition(int intChunk) {
    	int intChunkDataPosition = intChunk * audioWavHeader.getChannels() * audioWavHeader.getBytesPerSample();
    	
    	return intChunkDataPosition;
    }
    
    /**
     * Retorna as amplitudes do arquivo WAV, separando elas entre os diferentes canais
     * e retornando apenas as amplitudes do canal parametrizado.
     * Extrai apenas as amplitudes entre os pedaços inicial e final.
     * 
     * @param intInitialChunk - Pedaço inicial (valor da amostra 'sample' baseado no tempo do áudio)
     * @param intFinalChunk   - Pedaço final (valor da amostra 'sample' baseado no tempo do áudio)
     * 
     * @return amplitudes[]
     */
    public double[] getAmplitudesChunk(int intInitialChunk, int intFinalChunk) {
    	return getAmplitudesChunk(1, intInitialChunk, intFinalChunk);
    }

    /**
     * Retorna as amplitudes do arquivo WAV, separando elas entre os diferentes canais
     * e retornando apenas as amplitudes do canal parametrizado.
     * Extrai apenas as amplitudes entre os pedaços inicial e final.
     * 
     * @param intChannel      - Canal do áudio
     * @param intInitialChunk - Pedaço inicial (valor da amostra 'sample' baseado no tempo do áudio)
     * @param intFinalChunk   - Pedaço final (valor da amostra 'sample' baseado no tempo do áudio)
     * 
     * @return amplitudes[]
     */
    private double[] getAmplitudesChunk(int intChannel, int intInitialChunk, int intFinalChunk) {
    	int intInitialChunkData = getChunkDataPosition(intInitialChunk);
        int intFinalChunkData = getChunkDataPosition(intFinalChunk);
        
        int intPointerAmplitudeChunk = intInitialChunkData - intWavDataInitialPosition;
        
    	// Se 'intFinalChunkData' for maior que 'intWavDataCurrentMaxPosition' e 'intFinalChunkData' menor que 'intWavDataSize'
    	// nós temos que extrair novamente os dados do arquivo WAV utilizando o método 'extractWavDataChunk'
        if (intFinalChunkData > intWavDataFinalPosition && intFinalChunkData < intWavDataSize) {
        	extractWavDataChunk(intInitialChunk);
        }
        
        float fltAmplitude = 0;
        
        int intAmplitude = 0;
        int intArrayIndex = 0;
        
        int intChunkSize = intFinalChunk - intInitialChunk + 1;
        int intNumChannels = audioWavHeader.getChannels();
        
        double[] amplitudes = new double[intChunkSize];

        for (int indexTotalChunks = intInitialChunk; indexTotalChunks <= intFinalChunk; indexTotalChunks++) {
        	for (int indexNumChannels = 1; indexNumChannels <= intNumChannels; indexNumChannels++) {
        		// Apenas o canal parametrizado é processado
        		if (intChannel == indexNumChannels) {
        			try {
		        		// 8 bits
		        		if (audioWavHeader.getBitsPerSample() == 8) {
		        			intAmplitude = (short) (wavData[intPointerAmplitudeChunk] & 0xff);
		        			intAmplitude = intAmplitude - 128;
		        		
		        		// 16 bits
		        		} else if (audioWavHeader.getBitsPerSample() == 16) {
		        			intAmplitude = (short) ( wavData[intPointerAmplitudeChunk + 0] & 0xff)  
		        					     | (short) ((wavData[intPointerAmplitudeChunk + 1] & 0xff) << 8);
		        		
		        		// 24 bits
		        		} else if (audioWavHeader.getBitsPerSample() == 24) {
		        			intAmplitude = (int) ( wavData[intPointerAmplitudeChunk + 0] & 0xff)  
						       		     | (int) ((wavData[intPointerAmplitudeChunk + 1] & 0xff) << 8) 
						       		     | (int) ((wavData[intPointerAmplitudeChunk + 2]) << 16);
		        			
		        		// 32 bits
		        		} else if (audioWavHeader.getBitsPerSample() == 32) {
		        			intAmplitude = (int) ( wavData[intPointerAmplitudeChunk + 0] & 0xff) 
	 					       		   	 | (int) ((wavData[intPointerAmplitudeChunk + 1] & 0xff) << 8) 
	 					       		   	 | (int) ((wavData[intPointerAmplitudeChunk + 2] & 0xff) << 16) 
	 					       		   	 | (int) ((wavData[intPointerAmplitudeChunk + 3]) << 24);
		        			
		        			// 32 bits - IEEE Float (0.24 Float Type 3)
		        			if (getWavHeader().getAudioFormat() == AudioWavFormat.WAVE_FORMAT_IEEE_FLOAT) {
		        				fltAmplitude = Float.intBitsToFloat(intAmplitude);
		        				intAmplitude = (int) (fltAmplitude * 2147483647F);
		        			}
		        		}
		        		
		        		if (intAmplitude == 0) {
		        			intAmplitude = 1;
		        		}

		        	// Exceção chamada quando 'intPointerAmplitudeChunk' exceder a posição máxima do array 'wavData'
        			} catch (Exception e) {
        				intAmplitude = 1;    // Atribui valores mínimos à amplitude
        				blnEndOfFile = true;
        			}

        			amplitudes[intArrayIndex] = intAmplitude;
        		}
        		
        		intPointerAmplitudeChunk += audioWavHeader.getBytesPerSample();
        	}

        	intArrayIndex++;
        }

        return amplitudes;
    }
    
    /**
     * Retorna a amostra baseando-se no tempo do áudio passado com parâmetro.
     * 
     * @param intTimeMilliseconds - Tempo em milisegundos
     * 
     * @return intSampleFromTime
     */
    public int getSampleFromTime(int intTimeMilliseconds) {
    	int intSampleFromTime = (int) (((float) getNumSamplesPerChannel() / (float) getTotalTime()) * intTimeMilliseconds);
    	
    	return intSampleFromTime;
    }
    
    /**
     * Verifica se o arquivo já não foi carregado anteriormente no sistema,
     * senão realiza a conversão do arquivo para o formato padrão do WASIS.
     * 
     * @throws Exception
     */
    private void checkWavConvertion() throws Exception {
    	try {
			String strAudioFileFromTemporaryPath = AudioTemporary.getAudioFileFromTemporaryPath(this);
			
			// Caso tenha encontrado o arquivo temporário, só realiza o carregamento do arquivo
			if (strAudioFileFromTemporaryPath != null && strAudioFileFromTemporaryPath.length() > 0) {
				strAudioFilePathTemporary = strAudioFileFromTemporaryPath;
				strAudioFileHashTemporary = FileManager.getFileHash(new File(strAudioFilePathTemporary));
				
				loadAudio();
				
			// Senão realiza a conversão do arquivo
			} else {
				convertAudioFileToDefaultWav();
			}
			
    	} catch (Exception e) {
			throw new Exception(e);
		}
    }
	
	/**
	 * Converte o arquivo de áudio original para o formato WAV padrão.
	 * 
	 * @throws Exception
	 */
	private void convertAudioFileToDefaultWav() throws Exception {
		strAudioFilePathTemporary = TARGET_PATH + createAudioFileTemporaryName() + ".wav";
		
		SwingWorker<Void, Void> swingWorkerConvertion = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					intStatus = LOAD_STATUS_CONVERTING;
					
					File fileSource = new File(strAudioFilePathOriginal);
					File fileTarget = new File(strAudioFilePathTemporary);
					
					// Realiza uma cópia do arquivo original para a pasta temporária (Esta cópia que será processada)
			    	String strAudioFileCopied = TARGET_PATH + fileSource.getName();
			    	File fileAudioFileCopied = new File(strAudioFileCopied);
			    	
			    	boolean blnAudioFileCopied = FileManager.copyFile(fileSource, fileAudioFileCopied);
			    	
			    	String strAudioFilePathCopy = "";
			    	
			    	// Verifica se foi realizada uma cópia do arquivo original na pasta temporária
			    	if (blnAudioFileCopied) {
			    		strAudioFilePathCopy = strAudioFileCopied;
			    		fileSource = new File(strAudioFilePathCopy);
			    	}

					FfmpegEncoderAttributes objFfmpegEncoderAttributes = new FfmpegEncoderAttributes();
					
					FfmpegEncoder objFfmpegEncoder = new FfmpegEncoder(true);
					objFfmpegEncoder.encode(fileSource, fileTarget, objFfmpegEncoderAttributes);
					
					// Aguarda a finalização do processo de conversão
					while (objFfmpegEncoder.getProgress()) {
						/* Não faz nada - Apenas aguarda finalizar a conversão */ 
					}
					
					// Verifica se o processo de conversão foi cancelado
					if (objFfmpegEncoder.getCanceled()) {
						intStatus = LOAD_STATUS_CANCELLED;
						blnStatusCancelled = true;
					
					// Caso não tenha sido cancelado
					} else {
						// Após feita a conversão para WAV, o arquivo é carregado novamente com os atributos padrões do WAV
						strAudioFileHashTemporary = FileManager.getFileHash(new File(strAudioFilePathTemporary));
						
						loadAudio();
						
						// Aguarda a finalização do processo de carregamento do WAV
						while (intStatus != LOAD_STATUS_LOADED) { 
							/* Não faz nada - Apenas aguarda finalizar o carregamento do WAV */
						}
						
						objFfmpegEncoder.updateProgress(100);
						
						// Deleta a cópia do arquivo original feita na pasta temporária
						if (strAudioFilePathCopy != null && strAudioFilePathCopy.length() > 0) {
							// Verifica se a cópia não é o arquivo original
							if (!strAudioFilePathOriginal.equals(strAudioFilePathCopy)) {
								File fileAudioCopy = new File(strAudioFilePathCopy);
								fileAudioCopy.setWritable(true);
								fileAudioCopy.delete();
							}
						}
					}
					
				} catch (Exception e) {
					intStatus = LOAD_STATUS_CANCELLED;
					blnStatusCancelled = true;
					
					throw new Exception(e);
				}
				
				return null;
			}
			
			@Override
			protected void done() {
			    try {
			        get();
			    } catch (final InterruptedException ex) {
			    	WasisMessageBox.showMessageDialog(rsBundle.getString("message_audio_file_invalid"), WasisMessageBox.ERROR_MESSAGE);
	            } catch (final ExecutionException ex) {
	            	WasisMessageBox.showMessageDialog(rsBundle.getString("message_audio_file_invalid"), WasisMessageBox.ERROR_MESSAGE);
			    }
			}
		};
		
		swingWorkerConvertion.execute();
	}
	
	/**
	 * Cria um nome temporário para o arquivo de áudio.<br>
	 * <br>
	 * O nome é criado a partir de um número aleatório + data da instância atual do sistema + hora atual.
	 * 
	 * @return strAudioFileTemporaryName
	 */
	private String createAudioFileTemporaryName() {
		// Número aleatório (1 até 100)
		Random random = new Random();
	    int intRandomNumber = random.nextInt(100);
		
	    // Data da instância atual do sistema
	    Calendar calendar = Calendar.getInstance();
	    calendar.setTime(WasisParameters.getInstance().getDateInstance());
	    
	    int intDay = calendar.get(Calendar.DAY_OF_MONTH);
	    int intMonth = calendar.get(Calendar.MONTH) + 1;   // É adicionado '1' pois 'Calendar' começa com os meses em '0' no Java
	    int intYear = calendar.get(Calendar.YEAR);
	    
	    // Hora atual
	    calendar.setTime(new Date());
	    int intHours = calendar.get(Calendar.HOUR_OF_DAY);
	    int intMinutes = calendar.get(Calendar.MINUTE);
	    int intSeconds = calendar.get(Calendar.SECOND);
	    
	    String strDay = String.format("%02d", intDay);
	    String strMonth = String.format("%02d", intMonth);
	    String strYear = String.format("%04d", intYear);
	    String strHours = String.format("%02d", intHours);
	    String strMinutes = String.format("%02d", intMinutes);
	    String strSeconds = String.format("%02d", intSeconds);
	    
		String strAudioFileTemporaryName = intRandomNumber + "-" + strYear + strMonth + strDay + "-" + strHours + strMinutes + strSeconds;

		return strAudioFileTemporaryName;
	}
	
	/**
	 * Retorna um clone do objeto <i>AudioWav</i>.
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}