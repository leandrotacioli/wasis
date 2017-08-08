package br.unicamp.fnjv.wasis.multimidia.ffmpegwrapper;
import java.io.Serializable;

import br.unicamp.fnjv.wasis.multimidia.wav.AudioWavFormat;

/**
 * Atributos do Encoder.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 12/Mai/2017
 */
public class FfmpegEncoderAttributes implements Serializable {
	private static final long serialVersionUID = 1084409437335075525L;
	
	private static String TARGET_FORMAT = "wav";
	private static String TARGET_CODEC = "pcm_s16le";

	private String strTargetFormat = TARGET_FORMAT;
	private String strTargetCodec = TARGET_CODEC;
	
	private int intTargetSampleRate = (int) AudioWavFormat.TARGET_SAMPLE_RATE;
	private int intTargetChannels = AudioWavFormat.TARGET_CHANNEL_MONO;
	
	/**
	 * 
	 * @return
	 */
	public String getTargetFormat() {
		return strTargetFormat;
	}
	
	/**
	 * 
	 * @param strTargetFormat
	 */
	public void setTargetFormat(String strTargetFormat) {
		this.strTargetFormat = strTargetFormat;
	}

	/**
	 * 
	 * @return
	 */
	public String getTargetCodec() {
		return strTargetCodec;
	}

	/**
	 * 
	 * @param strTargetCodec
	 */
	public void setTargetCodec(String strTargetCodec) {
		this.strTargetCodec = strTargetCodec;
	}

	/**
	 * 
	 * @return intTargetSampleRate
	 */
	public int getTargetSampleRate() {
		return intTargetSampleRate;
	}

	/**
	 * 
	 * @return intTargetChannels
	 */
	public int getTargetChannels() {
		return intTargetChannels;
	}
}