package br.unicamp.fnjv.wasis.multimidia.wav;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

/**
 * Formato padrão do arquivo WAV utilizado pelo WASIS.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 12/Mai/2017
 */
public final class AudioWavFormat {
	/**
	 * Formato padrão do arquivo WAV utilizado pelo WASIS.
	 */
	private AudioWavFormat() {
		
	}
	
	/**
	 * Tipo de arquivo padrão.
	 */
	public static final AudioFileFormat.Type TARGET_FILE_FORMAT_TYPE = AudioFileFormat.Type.WAVE;
	
	/**
	 * Encoding padrão.
	 */
	public static final AudioFormat.Encoding TARGET_ENCODING = AudioFormat.Encoding.PCM_SIGNED;
	
	/**
	 * Taxa de amostragem padrão.
	 */
	public static final float TARGET_SAMPLE_RATE = 44100F;
	
	/**
	 * Taxa de amostragem padrão.
	 */
	public static final float TARGET_BIT_RATE = 16;
	
	/**
	 * Quantidade de canais = Mono.
	 */
	public static final int TARGET_CHANNEL_MONO = 1;
	
	/**
	 * Arquitetura para armazenamento de memória. <br>
	 * <i>Big Endian</i> = true <br>
	 * <i>Little Endian</i> = false <br>
	 */
	public static final boolean TARGET_HANDLING_MEMORY_STORAGE = false;
	
	// Formatos WAV
	protected static final int WAVE_FORMAT_PCM = 0x0001;
	protected static final int WAVE_FORMAT_IEEE_FLOAT = 0x0003;
	protected static final int WAVE_FORMAT_ALAW = 0x0006;
	protected static final int WAVE_FORMAT_MULAW = 0x0007;
	protected static final int WAVE_FORMAT_EXTENSIBLE = 0xFFFE;
}