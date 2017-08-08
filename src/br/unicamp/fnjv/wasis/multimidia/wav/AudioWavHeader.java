package br.unicamp.fnjv.wasis.multimidia.wav;

import java.io.InputStream;

/**
 * Carrega as especificações do header do arquivo WAV.
 * 
 * @author Leandro Tacioli
 * @version 1.1 - 06/Ago/2015
 */
public class AudioWavHeader {
	private InputStream inputStream;
	
    private final int HEADER_BYTE_LENGTH = 65536;
    private byte[] headerBuffer = new byte[HEADER_BYTE_LENGTH];
	
    private final String RIFF_HEADER = "RIFF";
    private final String WAVE_HEADER = "WAVE";
    
    private final int DEFAULT_FMT_SIZE = 16;   // Tamanho padrão (em bytes) do pedaço 'FMT ' do header

    private String strChunkId;      // 4 bytes, big endian
    private int intChunkSize;       // 4 bytes, little endian
    private String strFormat;       // 4 bytes, big endian
    private String strSubChunk1Id;  // 4 bytes, big endian
    private int intSubChunk1Size;   // 4 bytes, little endian
    private int intAudioFormat;     // 2 bytes, little endian
    private int intChannels;        // 2 bytes, little endian
    private int intSampleRate;      // 4 bytes, little endian
    private int intByteRate;        // 4 bytes, little endian
    private int intBlockAlign;      // 2 bytes, little endian
    private int intBitsPerSample;   // 2 bytes, little endian
    private int intBytesPerSample;
	private String strExtraParam;   // N bytes, little endian
    private String strSubChunk2Id;  // 4 bytes, big endian
    private int intSubChunk2Size;   // 4 bytes, little endian
    
    private int intPointer = 0;

    /**
     * Carrega as especificações do header do arquivo WAV.
     * 
     * @param inputStream
     */
    public AudioWavHeader(InputStream inputStream) {
    	this.inputStream = inputStream;
    }

    /**
     * Carrega os dados do header.
     * 
     * @return TRUE - Header válido
     * 
     * @throws Exception
     */
    public boolean loadHeader() throws Exception {
        try {
            inputStream.read(headerBuffer);

            // Chunk ID (4 bytes - Big endian)
            strChunkId = new String(new byte[] { headerBuffer[intPointer++],
                                                 headerBuffer[intPointer++], 
                                                 headerBuffer[intPointer++],
                                                 headerBuffer[intPointer++] });
            
            if (!strChunkId.equals(RIFF_HEADER)) {
            	return false;  // Header inválido - não é um arquivo WAV
            }
            
            // Chunk Size (4 bytes - Little endian)
            intChunkSize = (int) (headerBuffer[intPointer++] & 0xff)
                         | (int) (headerBuffer[intPointer++] & 0xff) << 8
                         | (int) (headerBuffer[intPointer++] & 0xff) << 16
                         | (int) (headerBuffer[intPointer++] & 0xff) << 24;
            
            // Format (4 bytes - Big endian)
            strFormat = new String(new byte[] { headerBuffer[intPointer++],
                                                headerBuffer[intPointer++], 
                                                headerBuffer[intPointer++],
                                                headerBuffer[intPointer++] });
            
            if (!strFormat.equals(WAVE_HEADER)) {
            	return false;  // Header inválido - não é um arquivo WAV
            }
            
            // Subchunk 1 ID (4 bytes - Big endian)
            strSubChunk1Id = new String(new byte[] { headerBuffer[intPointer++],
                                                     headerBuffer[intPointer++], 
                                                     headerBuffer[intPointer++],
                                                     headerBuffer[intPointer++] });
            
            // Subchunk 1 Size (4 bytes - Little endian)
            intSubChunk1Size = (int) (headerBuffer[intPointer++] & 0xff)
                             | (int) (headerBuffer[intPointer++] & 0xff) << 8
                             | (int) (headerBuffer[intPointer++] & 0xff) << 16
                             | (int) (headerBuffer[intPointer++] & 0xff) << 24;
            
            // Subchunk 1 deve ser 'FMT '
            if (strSubChunk1Id.toUpperCase().equals("FMT ") == false) {
	            while (strSubChunk1Id.toUpperCase().equals("FMT ") == false) {
	            	ignoreWavChunk(intSubChunk1Size);
	            	
	            	// Subchunk 1 ID (4 bytes - Big endian)
	            	strSubChunk1Id = new String(new byte[] { headerBuffer[intPointer++],
	                        								 headerBuffer[intPointer++], 
	                        								 headerBuffer[intPointer++],
	                        								 headerBuffer[intPointer++] });
	
	            	// Subchunk 1 Size (4 bytes - Little endian)
	            	intSubChunk1Size = (int) (headerBuffer[intPointer++] & 0xff)
									 | (int) (headerBuffer[intPointer++] & 0xff) << 8
									 | (int) (headerBuffer[intPointer++] & 0xff) << 16
									 | (int) (headerBuffer[intPointer++] & 0xff) << 24;
	            }
            }
            
            // Audio Format (2 bytes - Little endian)
            intAudioFormat = (int) ((headerBuffer[intPointer++] & 0xff) 
            		              | (headerBuffer[intPointer++] & 0xff) << 8);
            
            if (intAudioFormat != AudioWavFormat.WAVE_FORMAT_PCM) {
            	return false;  // Header inválido - formato inválido para os padrões do Wasis
            }
            
            // Number of channels (2 bytes - Little endian)
            intChannels = (int) ((headerBuffer[intPointer++] & 0xff) 
            		           | (headerBuffer[intPointer++] & 0xff) << 8);
            
            // Sample Rate (4 bytes - Little endian)
            intSampleRate = (int) (headerBuffer[intPointer++] & 0xff)
                          | (int) (headerBuffer[intPointer++] & 0xff) << 8
                          | (int) (headerBuffer[intPointer++] & 0xff) << 16
                          | (int) (headerBuffer[intPointer++] & 0xff) << 24;
            
            // Byte Rate (4 bytes - Little endian)
            intByteRate = (int) (headerBuffer[intPointer++] & 0xff)
                        | (int) (headerBuffer[intPointer++] & 0xff) << 8
                        | (int) (headerBuffer[intPointer++] & 0xff) << 16
                        | (int) (headerBuffer[intPointer++] & 0xff) << 24;
            
            // Block Align (2 bytes - Little endian)
            intBlockAlign = (int) ((headerBuffer[intPointer++] & 0xff) 
            		             | (headerBuffer[intPointer++] & 0xff) << 8);
            
            // Bits per Sample (2 bytes - Little endian)
            intBitsPerSample = (int) ((headerBuffer[intPointer++] & 0xff) 
            		                | (headerBuffer[intPointer++] & 0xff) << 8);
            
            intBytesPerSample = intBitsPerSample / 8;
            
            // Informações extras caso 'intSubChunk1Size' for maior que 'DEFAULT_FMT_SIZE'
            strExtraParam = "";
            if (intSubChunk1Size > DEFAULT_FMT_SIZE) {
            	int intExtraParamSize = intSubChunk1Size - DEFAULT_FMT_SIZE;
            	strExtraParam = new String(getDynamicByteArray(intExtraParamSize));
            }
            
            // Subchunk 2 ID (4 bytes - Big endian)
            strSubChunk2Id = new String(new byte[] { headerBuffer[intPointer++],
                                                     headerBuffer[intPointer++], 
                                                     headerBuffer[intPointer++],
                                                     headerBuffer[intPointer++] });
            
            // Subchunk 2 Size (4 bytes - Little endian)
            intSubChunk2Size = (int) (headerBuffer[intPointer++] & 0xff)
                             | (int) (headerBuffer[intPointer++] & 0xff) << 8
                             | (int) (headerBuffer[intPointer++] & 0xff) << 16
                             | (int) (headerBuffer[intPointer++] & 0xff) << 24;
            
            // Subchunk 2 deve ser 'DATA'
            if (strSubChunk2Id.toUpperCase().equals("DATA") == false) {
	            while (strSubChunk2Id.toUpperCase().equals("DATA") == false) {
	            	ignoreWavChunk(intSubChunk2Size);
	            	
	            	// Subchunk 2 ID (4 bytes - Big endian)
	            	strSubChunk2Id = new String(new byte[] { headerBuffer[intPointer++],
	                        								 headerBuffer[intPointer++], 
	                        								 headerBuffer[intPointer++],
	                        								 headerBuffer[intPointer++] });
	
	            	// Subchunk 2 Size (4 bytes - Little endian)
	            	intSubChunk2Size = (int) (headerBuffer[intPointer++] & 0xff)
									 | (int) (headerBuffer[intPointer++] & 0xff) << 8
									 | (int) (headerBuffer[intPointer++] & 0xff) << 16
									 | (int) (headerBuffer[intPointer++] & 0xff) << 24;
	            }
            }
            
            // Updates information
			//AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(strFilename));
			//AudioFormat audioFormat = audioInputStream.getFormat();
			
			//intChannels = audioFormat.getChannels();
			//intSampleRate = (int) audioFormat.getSampleRate();
			//intBitsPerSample = audioFormat.getSampleSizeInBits();
            
            //printWavHeader();
            
            return true;
            
        } catch (Exception e) {
        	throw new Exception(e);
        }
    }
    
    /**
     * Ignora um pedaço do cabeçalho, sendo que o tamanho do pedaço é passado como parâmetro.
     * É utilizado quando um pedaço do cabeçalho é esperado, mas outro é carregado.
     * 
     * @param intChunkSize
     */
    private void ignoreWavChunk(int intChunkSize) {
    	intPointer += intChunkSize;
    }
    
    /**
     * Retorna o array de bytes de um comprimento parametrizado.
     * 
     * @param intArrayLength
     * 
     * @return array - Big endian format
     */
    private byte[] getDynamicByteArray(int intArrayLength) {
    	byte[] array = new byte[intArrayLength];
		
		for (int intIndexArrayLength = 0; intIndexArrayLength < intArrayLength; intIndexArrayLength++) {
			array[intIndexArrayLength] = headerBuffer[intPointer++];
		}
    	
		return array;
    }
    
    /**
     * Returna o formato do áudio.
     * 
     * @return intAudioFormat
     */
    public int getAudioFormat() {
        return intAudioFormat;
    }

    /**
     * Retorna o número de canais. <br>
     * <i>1</i> - Mono
	 * <br>
	 * <i>2</i> - Stereo
     * 
     * @return intChannels
     */
    public int getChannels() {
        return intChannels;
    }

    /**
     * Retorna a taxa de amostragem. <br>
     * <i>48,000</i> - Padrão do WASIS
     * 
     * @return intSampleRate
     */
    public int getSampleRate() {
        return intSampleRate;
    }
    
    /**
     * Retorna a taxa de bytes.
     * 
     * @return intByteRate
     */
    public int getByteRate() {
        return intByteRate;
    }

    /**
     * Retorna o número de bits por amostra.
     * 
     * @return intBitsPerSample
     */
    public int getBitsPerSample() {
        return intBitsPerSample;
    }
    
    /**
     * Retorna o número de bytes por amostra.
     * 
     * @return intBytesPerSample
     */
    public int getBytesPerSample() {
        return intBytesPerSample;
    }
    
    /**
     * Imprime as especificações do arquivo header do arquivo WAV.
     */
    public void printWavHeader() {
        StringBuffer strBuffer = new StringBuffer();
        strBuffer.append("Chunk Id: " + strChunkId + "\n");
        strBuffer.append("Chunk Size: " + intChunkSize + "\n");
        strBuffer.append("Format: " + strFormat + "\n");
        strBuffer.append("SubChunk 1 ID: " + strSubChunk1Id + "\n");
        strBuffer.append("SubChunk 1 Size: " + intSubChunk1Size + "\n");
        strBuffer.append("Audio Format: " + intAudioFormat + "\n");
        strBuffer.append("Channels: " + intChannels + "\n");
        strBuffer.append("Sample Rate: " + intSampleRate + "\n");
        strBuffer.append("Byte Rate: " + intByteRate + "\n");
        strBuffer.append("Block Align: " + intBlockAlign + "\n");
        strBuffer.append("Bits Per Sample: " + intBitsPerSample + "\n");
        if (strExtraParam.length() > 0) {
        	strBuffer.append("Extra Param: " + strExtraParam + "\n");
        }
        strBuffer.append("SubChunk 2 ID: " + strSubChunk2Id + "\n");
        strBuffer.append("SubChunk 2 Size: " + intSubChunk2Size + "\n");
        
        System.out.println(strBuffer);
    }
}