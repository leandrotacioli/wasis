package br.unicamp.fnjv.wasis.graphics.spectrogram;

import java.awt.Color;

import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Define diferentes mapas de cores para a visualização do espectrograma.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 26/Set/2017
 */
public class SpectrogramColorDisplay {
	public static final String SPECTROGRAM_GRAYSCALE = "Grayscale";
	public static final String SPECTROGRAM_GRAYSCALE_REVERSE = "Grayscale (Reverse)";
	public static final String SPECTROGRAM_GRADIENT_1 = "Gradient 1";
	public static final String SPECTROGRAM_GRADIENT_2 = "Gradient 2";
	public static final String SPECTROGRAM_GRADIENT_3 = "Gradient 3";
	
	public static final int[] DECIBEL_VALUES = {0, -10, -20, -30, -40, -50, -60, -70, -80, -90, -100};
	
	private static final int[][] GRAYSCALE = { {0,   0,   0},    // +0 dB
              								   {25,  25,  25},   // -10 dB
              								   {51,  51,  51},   // -20 dB
              								   {76,  76,  76},   // -30 dB
              								   {102, 102, 102},  // -40 dB
              								   {127, 127, 127},  // -50 dB
              								   {153, 153, 153},  // -60 dB
              								   {178, 178, 178},  // -70 dB
              								   {204, 204, 204},  // -80 dB
              								   {229, 229, 229},  // -90 dB
              								   {255, 255, 255},  // -100 dB
		  									 };
	
	private static final int[][] GRAYSCALE_REVERSE = { {255, 255, 255},    // +0 dB
													   {229, 229, 229},    // -10 dB
													   {204, 204, 204},    // -20 dB
													   {178, 178, 178},    // -30 dB
													   {153, 153, 153},    // -40 dB
													   {127, 127, 127},    // -50 dB
													   {102, 102, 102},    // -60 dB
													   {76,  76,  76},     // -70 dB
													   {51,  51,  51},     // -80 dB
													   {25,  25,  25},     // -90 dB
													   {0,   0,   0},      // -100 dB
													 };
	
	private static final int[][] GRADIENT_1 = { {133, 5,   0},    // +0 dB
		  									    {238, 16,  0},    // -10 dB
		  									    {255, 79,  0},    // -20 dB
		  									    {255, 176, 0},    // -30 dB
		  									    {233, 251, 19},   // -40 dB
		  									    {117, 251, 130},  // -50 dB
		  									    {0,   253, 231},  // -60 dB
		  									    {0,   181, 255},  // -70 dB
		  									    {0,   91,  255},  // -80 dB
		  									    {0,   46,  230},  // -90 dB
		  									    {0,   22,  131},  // -100 dB
		  									  };
	
	private static final int[][] GRADIENT_2 = { {254, 254, 254},  // +0 dB
			   								    {255, 254, 136},  // -10 dB
			   								    {255, 215, 21},   // -20 dB
			   								    {254, 123, 0},    // -30 dB
			   								    {239, 0,   2},    // -40 dB
			   								    {209, 0,   65},   // -50 dB
			   								    {166, 0,   110},  // -60 dB
			   								    {107, 0,   127},  // -70 dB
			   								    {45,  0,   109},  // -80 dB
			   								    {0,   0,   63},   // -90 dB
			   								    {1,   1,   4},    // -100 dB
			 								  };
	
	private static final int[][] GRADIENT_3 = { {10,  0,   0},    // +0 dB
			                                    {74,  0,   0},    // -10 dB
			                                    {142, 0,   0},    // -20 dB
			                                    {209, 0,   0},    // -30 dB
			                                    {255, 23,  0},    // -40 dB
			                                    {255, 90,  0},    // -50 dB
			                                    {255, 159, 0},    // -60 dB
			                                    {255, 227, 0},    // -70 dB
			                                    {255, 255, 58},   // -80 dB
			                                    {255, 255, 159},  // -90 dB
			                                    {255, 255, 255},  // -100 dB
			 								  };
	
	private static final Color GRAYSCALE_AUDIO_SEGMENT_BOX = new Color(255, 25, 25);
	private static final Color GRAYSCALE_REVERSE_AUDIO_SEGMENT_BOX = new Color(255, 25, 25);
	private static final Color GRADIENT_1_AUDIO_SEGMENT_BOX = new Color(15, 15, 15);
	private static final Color GRADIENT_2_AUDIO_SEGMENT_BOX = new Color(250, 250, 250);
	private static final Color GRADIENT_3_AUDIO_SEGMENT_BOX = new Color(25, 75, 200);
	
	private static final Color GRAYSCALE_AUDIO_SEGMENT_ID_NOT_SAVED = new Color(10, 125, 5);
	private static final Color GRAYSCALE_AUDIO_SEGMENT_ID_SAVED = new Color(10, 50, 100);
	
	private static final Color GRAYSCALE_REVERSE_AUDIO_SEGMENT_ID_NOT_SAVED = new Color(75, 200, 75);
	private static final Color GRAYSCALE_REVERSE_AUDIO_SEGMENT_ID_SAVED = new Color(75, 125, 225);
	
	private static final Color GRADIENT_1_AUDIO_SEGMENT_ID_NOT_SAVED = new Color(245, 245, 245);
	private static final Color GRADIENT_1_AUDIO_SEGMENT_ID_SAVED = new Color(10, 10, 10);
	
	private static final Color GRADIENT_2_AUDIO_SEGMENT_ID_NOT_SAVED = new Color(250, 250, 250);
	private static final Color GRADIENT_2_AUDIO_SEGMENT_ID_SAVED = new Color(0, 225, 25);
	
	private static final Color GRADIENT_3_AUDIO_SEGMENT_ID_NOT_SAVED = new Color(245, 245, 245);
	private static final Color GRADIENT_3_AUDIO_SEGMENT_ID_SAVED = new Color(10, 10, 10);
	
	private SpectrogramColorDisplay() {
		
	}
	
	/**
	 * Retorna o mapa de cores de visualização do espectrograma.
	 * 
	 * @return colorSelectionBox
	 */
	public static int[][] getSpectrogramColor() {
		int[][] spectrogramColor = null;
		
		if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE)) {
			spectrogramColor = GRAYSCALE;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE_REVERSE)) {
			spectrogramColor = GRAYSCALE_REVERSE;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_1)) {
			spectrogramColor = GRADIENT_1;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_2)) {
			spectrogramColor = GRADIENT_2;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_3)) {
			spectrogramColor = GRADIENT_3;
		}
    	
    	return spectrogramColor;
    }
	
	/**
	 * Retorna a cor das caixas de seleção para os mapas de cores.
	 * 
	 * @return colorAudioSegmentBox
	 */
	public static Color getColorAudioSegmentBox() {
		Color colorAudioSegmentBox = null;
		
    	if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE)) {
    		colorAudioSegmentBox = GRAYSCALE_AUDIO_SEGMENT_BOX;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE_REVERSE)) {
			colorAudioSegmentBox = GRAYSCALE_REVERSE_AUDIO_SEGMENT_BOX;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_1)) {
			colorAudioSegmentBox = GRADIENT_1_AUDIO_SEGMENT_BOX;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_2)) {
			colorAudioSegmentBox = GRADIENT_2_AUDIO_SEGMENT_BOX;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_3)) {
			colorAudioSegmentBox = GRADIENT_3_AUDIO_SEGMENT_BOX;
		}
    	
    	return colorAudioSegmentBox;
    }
	
	/**
	 * Retorna a cor para a identificação dos segmentos de áudio não gravados no banco de dados
	 * para os diversos mapas de cores do espectrograma.
	 * 
	 * @return colorAudioSegmentIdNotSaved
	 */
	public static Color getColorAudioSegmentIdNotSaved() {
		Color colorAudioSegmentIdNotSaved = null;
		
    	if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE)) {
    		colorAudioSegmentIdNotSaved = GRAYSCALE_AUDIO_SEGMENT_ID_NOT_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE_REVERSE)) {
			colorAudioSegmentIdNotSaved = GRAYSCALE_REVERSE_AUDIO_SEGMENT_ID_NOT_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_1)) {
			colorAudioSegmentIdNotSaved = GRADIENT_1_AUDIO_SEGMENT_ID_NOT_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_2)) {
			colorAudioSegmentIdNotSaved = GRADIENT_2_AUDIO_SEGMENT_ID_NOT_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_3)) {
			colorAudioSegmentIdNotSaved = GRADIENT_3_AUDIO_SEGMENT_ID_NOT_SAVED;
		}
    	
    	return colorAudioSegmentIdNotSaved;
    }
	
	/**
	 * Retorna a cor para a identificação dos segmentos de áudio já gravados no banco de dados
	 * para os diversos mapas de cores do espectrograma.
	 * 
	 * @return colorAudioSegmentIdSaved
	 */
	public static Color getColorAudioSegmentIdSaved() {
		Color colorAudioSegmentIdSaved = null;
		
    	if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE)) {
    		colorAudioSegmentIdSaved = GRAYSCALE_AUDIO_SEGMENT_ID_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE_REVERSE)) {
			colorAudioSegmentIdSaved = GRAYSCALE_REVERSE_AUDIO_SEGMENT_ID_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_1)) {
			colorAudioSegmentIdSaved = GRADIENT_1_AUDIO_SEGMENT_ID_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_2)) {
			colorAudioSegmentIdSaved = GRADIENT_2_AUDIO_SEGMENT_ID_SAVED;
		} else if (WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_3)) {
			colorAudioSegmentIdSaved = GRADIENT_3_AUDIO_SEGMENT_ID_SAVED;
		}
    	
    	return colorAudioSegmentIdSaved;
    }
}