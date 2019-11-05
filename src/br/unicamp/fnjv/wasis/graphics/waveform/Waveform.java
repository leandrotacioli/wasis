package br.unicamp.fnjv.wasis.graphics.waveform;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;

/**
 * Processa o waveform de um arquivo de áudio.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 27/Out/2017
 */
public class Waveform {
	private AudioWav objAudioWav;               // Objeto do Áudio WAV

	private int intPanelWidth;                  // Comprimento do painel onde o waveform será desenhado
    private int intPanelHeight;                 // Altura do painel onde o waveform será desenhado
    
    private int intInitialChunkToProcess;       // Pedaço inicial do áudio que será processado
    private int intFinalChunkToProcess;         // Pedaço final do áudio que será processado
    
    private int intMaximumTime;                 // Tempo total (em milisegundos) do arquivo de áudio
    private int intInitialTime;                 // Tempo inicial (em milisegundos) que está sendo mostrado na tela (atualizada com zoom in/out)
    private int intFinalTime;                   // Tempo final (em milisegundos) que está sendo mostrado na tela (atualizada com zoom in/out)
    
    private int intInitialTimeSpectrogram;      // Tempo inicial (em milisegundos) que está sendo mostrado no espectrograma
    private int intFinalTimeSpectrogram;        // Tempo final (em milisegundos) que está sendo mostrado no espectrograma
    
    private boolean blnIsRenderingWaveform;     // Status de renderização do waveform.
	
    /**
	 * Imagem original do waveform (gerada na renderização)
	 */
	private BufferedImage waveformImage;
    
	/**
	 * Imagem final do waveform (considerando um 'resize' para se ajustar ao painel)
	 */
	private BufferedImage waveformImageFinal;

	/**
	 * Retorna o comprimento do painel onde o waveform será desenhado.
	 * 
	 * @return intPainelWidth
	 */
	public int getPanelWidth() {
		return intPanelWidth;
	}
	
	/**
	 * Altera o comprimento do componente onde o waveform será desenhado.
	 * 
	 * @param intPanelWidth
	 */
	public void setPanelWidth(int intPanelWidth) {
		 this.intPanelWidth = intPanelWidth;
	}
	
	/**
	 * Retorna a altura do painel onde o waveform será desenhado.
	 * 
	 * @return intPanelHeight
	 */
	public int getPanelHeight() {
		return intPanelHeight;
	}

	/**
	 * Altera a altura do componente onde o waveform será desenhado.
	 * 
	 * @param intPanelHeight
	 */
	public void setPanelHeight(int intPanelHeight) {
		this.intPanelHeight = intPanelHeight;
	}
	
	/**
	 * Retorna o tempo inicial que está sendo mostrado na imagem.
	 * 
	 * @return intInitialTime
	 */
	public int getInitialTime() {
		return intInitialTime;
	}
	
	/**
	 * Retorna o tempo final que está sendo mostrado na imagem.
	 * 
	 * @return intFinalTime
	 */
	public int getFinalTime() {
		return intFinalTime;
	}
	
	/**
	 * Retorna o tempo inicial que está sendo mostrado no espectrograma.
	 * 
	 * @return intTimeSpectrogramInitial
	 */
	public int getInitialTimeSpectrogram() {
		return intInitialTimeSpectrogram;
	}

	/**
	 * Altera o tempo inicial que está sendo mostrado no espectrograma.
	 * 
	 * @param intInitialTimeSpectrogram
	 */
	public void setInitialTimeSpectrogram(int intInitialTimeSpectrogram) {
		this.intInitialTimeSpectrogram = intInitialTimeSpectrogram;
	}

	/**
	 * Retorna o tempo final que está sendo mostrado no espectrograma.
	 * 
	 * @return intFinalTimeSpectrogram
	 */
	public int getFinalTimeSpectrogram() {
		return intFinalTimeSpectrogram;
	}

	/**
	 * Altera o tempo final que está sendo mostrado no espectrograma.
	 * 
	 * @param intFinalTimeSpectrogram
	 */
	public void setFinalTimeSpectrogram(int intFinalTimeSpectrogram) {
		this.intFinalTimeSpectrogram = intFinalTimeSpectrogram;
	}
	
    /**
	 * Retorna o status de renderização do waveform. <br>
	 * <i>True</i> - Em renderização <br>
	 * <i>False</i> - Já renderizado
	 * 
	 * @return blnIsRenderingWaveform
	 */
	public boolean getIsRenderingWaveform() {
		return blnIsRenderingWaveform;
	}
	
	/**
     * Retorna a imagem original do waveform.
     * 
     * @return waveformImage
     */
	protected BufferedImage getWaveformImage() {
		return waveformImage;
	}
	
	/**
     * Retorna a imagem final do waveform.
     * 
     * @return waveformImageFinal
     */
	protected BufferedImage getWaveformImageFinal() {
		return waveformImageFinal;
	}

	/**
     * Processa o waveform de um arquivo de áudio.
     * 
     * @param objAudioWav - Objeto do áudio WAV
     * 
	 * @throws CloneNotSupportedException 
     */
	public Waveform(AudioWav objAudioWav) throws CloneNotSupportedException {
		this.objAudioWav = (AudioWav) objAudioWav.clone();
		
		this.intInitialChunkToProcess = 0;
        this.intFinalChunkToProcess = objAudioWav.getNumSamplesPerChannel();
		
		this.intMaximumTime = objAudioWav.getTotalTime();
		this.intInitialTime = 0;
		this.intFinalTime = intMaximumTime;
	}

	/**
     * Renderiza o waveform.
     */
    public void renderWaveform() {
    	try {
    		blnIsRenderingWaveform = true;
    	
	    	waveformImage = new BufferedImage(intPanelWidth, intPanelHeight, BufferedImage.TYPE_INT_RGB);
	    	
	        Graphics2D graphics2D = waveformImage.createGraphics();
	        graphics2D.setPaint(new Color(255, 255, 255));
	        graphics2D.fillRect(0, 0, intPanelWidth, intPanelHeight);
	    	
	        // Amplitude máxima de acordo com o número de bits do arquivo
	    	long lgnMaxAmplitude = 1 << (objAudioWav.getWavHeader().getBitsPerSample() - 1);
	    	
	    	// Determina quantas amostras serão utilizadas para cada pixel do eixo Y
	    	int intNumSamplesPerPixel = (int) (Math.abs(lgnMaxAmplitude * 2) / intPanelHeight);   
	    	
	        // 8 bits
	        if (objAudioWav.getWavHeader().getBitsPerSample() == 8) {
	        	lgnMaxAmplitude <<= 1;
	        	intNumSamplesPerPixel = (int) (Math.abs(lgnMaxAmplitude * 2) / intPanelHeight);
	        	
	        	if (intNumSamplesPerPixel == 0) {
	        		intNumSamplesPerPixel = 1;
	        	}
	        }
	        
	        // Valores inicial e final para o primeiro pedaço de áudio
	        int intNumTotalSamples = intFinalChunkToProcess - intInitialChunkToProcess;
	        int intChunkSize = intNumTotalSamples / intPanelWidth;                // Tamanho do pedaço que será processado por vez
	        int intInitialChunk = intInitialChunkToProcess;
	        int intFinalChunk = intInitialChunk + intChunkSize - 1;
	        
	        int intWaveformMiddle = intPanelHeight / 2;                           // Meio do waveform
	        double dblVariationColorPerPixel = (double) intWaveformMiddle / 150;  // Há uma variação para mostrar um efeito degradê
	        
	        int[] arrayAmplitudeMaxValues;
	        
	        objAudioWav.extractWavDataChunk(intInitialChunk);
	        
	        // ***********************************************************************************************
	    	for (int indexWidth = 0; indexWidth < intPanelWidth; indexWidth++) {
	    		arrayAmplitudeMaxValues = getAmplitudesMaximumValues(intInitialChunk, intFinalChunk);

	        	int intMaxPosition = intWaveformMiddle - ((int) (Math.abs(arrayAmplitudeMaxValues[0]) / intNumSamplesPerPixel));
	    		int intMinPosition = intWaveformMiddle + ((int) (Math.abs(arrayAmplitudeMaxValues[1]) / intNumSamplesPerPixel));
	    		
	    		for (int indexPosition = intMaxPosition; indexPosition <= intMinPosition; indexPosition++) {
	    			double dblVariationWave = 0;
	    			
	    			if (indexPosition < intWaveformMiddle) {
	    				dblVariationWave = intWaveformMiddle - indexPosition;  // Positivas
	    			} else {
	    				dblVariationWave = indexPosition - intWaveformMiddle;  // Negativas
	    			}

	    			waveformImage.setRGB(indexWidth, indexPosition, getRGBColor(dblVariationWave, dblVariationColorPerPixel));
	    		}
	    		
	        	// Valores inicial e final do pedaço são atualizado a cada loop
	        	intInitialChunk += intChunkSize;
	        	intFinalChunk += intChunkSize;
	    	}
	    	
	        // Desenha uma linha central no waveform
	        for (int indexMiddleLine = 0; indexMiddleLine < intPanelWidth; indexMiddleLine++) {
	        	waveformImage.setRGB(indexMiddleLine, intWaveformMiddle, 0x000000); // Cor preta
	        }
	        
	        waveformImageFinal = waveformImage;
        
    	} catch (Exception e) {
    		e.printStackTrace();
    		
    	} finally {
    		blnIsRenderingWaveform = false;
    	}
    }
    
    /**
	 * Retorna os valores máximos das amplitudes.
	 * 
	 * @param intInitialChunk
	 * @param intFinalChunk
	 * 
	 * @return arrayAmplitudeMaxValues[0] - Amplitude positiva = parte de cima do waveform<br>
     *	       arrayAmplitudeMaxValues[1] - Amplitude negativa = parte de baixo do waveform
	 * 
	 */
	private int[] getAmplitudesMaximumValues(int intInitialChunk, int intFinalChunk) {
        double dblPositiveAmplitude = 0;
        double dblNegativeAmplitude = 0;
        
        double[] originalAmplitudes = objAudioWav.getAmplitudesChunk(intInitialChunk, intFinalChunk);
        
        for (int indexOriginalAmplitudes = 0; indexOriginalAmplitudes < originalAmplitudes.length; indexOriginalAmplitudes++) {
        	double dblAmplitude = originalAmplitudes[indexOriginalAmplitudes];
            
            // Amplitudes positivas
            if (dblAmplitude > 0) {
            	if (dblAmplitude > dblPositiveAmplitude) {
            		dblPositiveAmplitude = dblAmplitude;
            	}
            	
            // Amplitudes negativas
            } else if (dblAmplitude < 0) {
            	if (dblAmplitude < dblNegativeAmplitude) {
            		dblNegativeAmplitude = dblAmplitude;
            	}
            }
        }

    	int[] arrayAmplitudeMaxValues = new int[2];               // O array sempre terá somente 2 valores
    	arrayAmplitudeMaxValues[0] = (int) dblPositiveAmplitude;  // Amplitude positiva = parte de cima do waveform
    	arrayAmplitudeMaxValues[1] = (int) dblNegativeAmplitude;  // Amplitude negativa = parte de baixo do waveform
    	
    	return arrayAmplitudeMaxValues;
	}
    
    /**
     * Retorna a cor RGB final.
     * 
     * @param dblVariationWave
     * @param dblVariationColorPerPixel
     * 
     * @return intRGB
     */
    private int getRGBColor(double dblVariationWave, double dblVariationColorPerPixel) {
    	int intFinalColor = 0;
    	
		if (dblVariationWave != 0) {
			intFinalColor = (int) Math.ceil(dblVariationWave / dblVariationColorPerPixel);
		}
		
		int[] arrayRGB = new int[3];
		
        arrayRGB[0] = 0 + intFinalColor;
        arrayRGB[1] = 50 + intFinalColor;
        arrayRGB[2] = 100 + intFinalColor;
        
        if (arrayRGB[0] > 255) arrayRGB[0] = 255;
    	if (arrayRGB[1] > 255) arrayRGB[1] = 255;
    	if (arrayRGB[2] > 255) arrayRGB[2] = 255;
    	
    	int intRGB = ((255 & 0xFF) << 24) | (((int) arrayRGB[0] & 0xFF) << 16) | (((int) arrayRGB[1] & 0xFF) << 8) | (((int) arrayRGB[2] & 0xFF) << 0);
    	
    	return intRGB;
    }
    
    /**
     * Amplia/reduz o zoom no eixo do tempo.
     * 
     * @param intInitialTime - Tempo inicial com o zoom já aplicado
     * @param intTimeFinal   - Tempo final com o zoom já aplicado
     */
    public void setTimeZoom(int intInitialTime, int intFinalTime) {
    	if (this.intInitialTime != intInitialTime || this.intFinalTime != intFinalTime) {
	    	this.intInitialTime = intInitialTime;
	    	this.intFinalTime = intFinalTime;
	    	
	    	this.intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
	    	this.intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
	    	
	    	renderWaveform();
	    }
    }
    
    /**
     * Reseta o zoom no eixo do tempo.
     */
    public void setTimeZoomReset() {
    	if (intInitialTime != 0 || intFinalTime != intMaximumTime) {
    		this.intInitialTime = 0;
    		this.intFinalTime = intMaximumTime;
        	
    		this.intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
    		this.intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
        	
        	renderWaveform();
    	}
    }
    
    /**
     * Ajusta o tamanho do waveform final no painel.<br>
     * <br>
     * A variável <i>waveformImageFinal</i> é alterada para o tamanho final.
     */
    protected void scaleFinalImage() {
        BufferedImage imageToScale = new BufferedImage(intPanelWidth, intPanelHeight, BufferedImage.TYPE_INT_RGB);
    	
        final Graphics2D graphics2D = imageToScale.createGraphics();
        graphics2D.setComposite(AlphaComposite.Src);
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        graphics2D.drawImage(waveformImage, 0, 0, intPanelWidth, intPanelHeight, null);
        graphics2D.dispose();
        
        waveformImageFinal = imageToScale;
    }
    
    /**
     * Exporta o waveform para um arquivo PNG
     * 
     * @param strAudioFilePath
     */
    public void exportWaveform(final BufferedImage waveformImageToExport, String strAudioFilePath) {
        try {
            ImageIO.write(waveformImageToExport, "png", new File(strAudioFilePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}