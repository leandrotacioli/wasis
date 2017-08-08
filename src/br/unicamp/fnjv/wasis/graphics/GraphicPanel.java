package br.unicamp.fnjv.wasis.graphics;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;

import br.unicamp.fnjv.wasis.graphics.spectrogram.Spectrogram;
import br.unicamp.fnjv.wasis.graphics.spectrogram.SpectrogramListener;
import br.unicamp.fnjv.wasis.graphics.waveform.Waveform;
import br.unicamp.fnjv.wasis.graphics.waveform.WaveformListener;
import br.unicamp.fnjv.wasis.player.Player;
import br.unicamp.fnjv.wasis.player.PlayerListener;

/**
 * Cria um painel que renderiza o espectrograma/waveform.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 10/Mar/2015
 */
public class GraphicPanel extends JPanel implements PlayerListener {
	private static final long serialVersionUID = -568284228421520526L;
	
	private Player objPlayer;
	private Waveform objWaveform;
	private Spectrogram objSpectrogram;
	
	private Collection<Object> collectionListenerWaveform;
    private Collection<Object> collectionListenerSpectrogram; 
	
    private int intPanelWidth;                   // Comprimento do painel
    private int intPanelHeight;                  // Altura do painel
    
    private int intPanelWidthTemporary;          // Comprimento temporário do painel
    private int intPanelHeightTemporary;         // Altura temporária do painel

    private int intInitialTime;                  // Tempo inicial do áudio que está sendo mostrado na tela
    private int intFinalTime;                    // Tempo final do áudio que está sendo mostrado na tela
    
    private int intInitialTimeSelectionBox;      // Tempo inicial quando houver caixa de seleção
    private int intFinalTimeSelectionBox;        // Tempo final quando houver caixa de seleção
    
    private double dblTimePerPixel;              // Tempo que deverá ser atribuído para cada pixel da imagem.

    private int intTimePlayerLine;               // Tempo da linha do player enquanto o áudio é tocado
    private int intTimeSelectionLine;            // Tempo da linha de seleção (apenas quando clica com o mouse sobre o painel)
	
	private boolean blnDrawPlayerLine;           // Status para o desenho da linha do player
	private boolean blnDrawSelectionLine;        // Status para o desenho da linha de seleção
	private boolean blnDrawSelectionBox;         // Status para o desenho da caixa de seleção
	
	private boolean blnChangeTimeSelectionLine;  // Status para mudança do tempo da linha de seleção

	private boolean blnMouseButtonReleased;      // Status para a liberação do botão do mouse
	
	/**
	 * Tamanho dos eixos que serão desenhados no painel (em pixels).
	 */
	protected final int AXES_SIZE = 45;
	
	/**
	 * Cor de background do painel.
	 */
	protected final Color COLOR_PANEL_BACKGROUND = getBackground();
	
	/**
	 * Cor da linha de seleção.
	 */
	protected final Color COLOR_SELECTION_LINE = new Color(50, 50, 250);
	
	/**
	 * Cor da caixa de seleção.
	 */
	protected final Color COLOR_SELECTION_BOX = Color.RED;
	
	protected final Composite COMPOSITE_SELECTION_BOX = AlphaComposite.SrcOver.derive(0.20f);
	protected final Composite COMPOSITE_SELECTION_BOX_BORDER = AlphaComposite.SrcOver.derive(0.40f);
	protected final Stroke STROKE_SELECTION_BOX_BORDER = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0.0f, new float[] {10.0f}, 0.0f);
	
	/**
	 * Retorna o objeto do espectrograma.
	 * 
	 * @return objSpectrogram
	 */
	public Spectrogram getSpectrogram() {
		if (objSpectrogram != null) {
			return objSpectrogram;
		} else {
			return null;
		}
	}
	
	/**
     * Retorna o comprimento do painel onde o waveform/espectrograma será desenhado.
     * 
     * @return intPanelWidth
     */
	public int getPanelWidth() {
		return intPanelWidth;
	}
	
	/**
	 * Altera o comprimento do painel onde o waveform/espectrograma será desenhado.<br>
	 * <br>
	 * O comprimento final do painel subtrai o tamanho do eixo.
	 * 
	 * @param intPainelWidth
	 */
	public void setPanelWidth(int intPanelWidth) {
		this.intPanelWidth = intPanelWidth - AXES_SIZE;
	}
	
	/**
	 * Retorna a altura do painel onde o waveform/espectrograma será desenhado.
	 * 
	 * @return intPanelHeight
	 */
	public int getPanelHeight() {
		return intPanelHeight;
	}
	
	/**
	 * Altera a altura do painel onde o waveform/espectrograma será desenhado.<br>
	 * <br>
	 * A altura final do painel subtrai o tamanho do eixo (apenas para o espectrograma).
	 * 
	 * @param intPanelHeight
	 */
	public void setPanelHeight(int intPanelHeight) {
		this.intPanelHeight = intPanelHeight;
		
		if (this.objSpectrogram != null) {
			this.intPanelHeight = intPanelHeight - AXES_SIZE;
		}
	}
	
	/**
     * Retorna o comprimento temporário do painel onde o waveform/espectrograma será desenhado.
     * 
     * @return intPanelWidthTemporary
     */
	protected int getPanelWidthTemporary() {
		return intPanelWidthTemporary;
	}
	
	/**
	 * Altera o comprimento temporário do painel onde o waveform/espectrograma será desenhado.
	 * 
	 * @param intPanelWidthTemporary
	 */
	protected void setPanelWidthTemporary(int intPanelWidthTemporary) {
		this.intPanelWidthTemporary = intPanelWidthTemporary;
	}
	
	/**
	 * Retorna a altura temporária do painel onde o waveform/espectrograma será desenhado.
	 * 
	 * @return intPanelHeightTemporary
	 */
	protected int getPanelHeightTemporary() {
		return intPanelHeightTemporary;
	}
	
	/**
	 * Altera a altura temporária do painel onde o waveform/espectrograma será desenhado.
	 * 
	 * @param intPanelHeightTemporary
	 */
	protected void setPanelHeightTemporary(int intPanelHeightTemporary) {
		this.intPanelHeightTemporary = intPanelHeightTemporary;
	}
	
	/**
     * Retorna o tempo inicial do áudio que está sendo mostrado na tela.
     * 
     * @return intInitialTime
     */
	protected int getInitialTime() {
		return intInitialTime;
	}
	
	/**
	 * Altera o tempo inicial do áudio que está sendo mostrado na tela.
	 * 
	 * @param intInitialTime
	 */
	protected void setInitialTime(int intInitialTime) {
		this.intInitialTime = intInitialTime;
		this.dblTimePerPixel = (double) (this.intFinalTime - this.intInitialTime) / this.intPanelWidth;
	}
	
	/**
     * Retorna o tempo final do áudio que está sendo mostrado na tela.
     * 
     * @return intFinalTime
     */
	protected int getFinalTime() {
		return intFinalTime;
	}
	
	/**
	 * Altera o tempo final do áudio que está sendo mostrado na tela.
	 * 
	 * @param intFinalTime
	 */
	protected void setFinalTime(int intFinalTime) {
		this.intFinalTime = intFinalTime;
		this.dblTimePerPixel = (double) (this.intFinalTime - this.intInitialTime) / this.intPanelWidth;
	}
	
	/**
     * Retorna o tempo inicial quando houver caixa de seleção.
     * 
     * @return intInitialTimeSelectionBox
     */
	protected int getInitialTimeSelectionBox() {
		return intInitialTimeSelectionBox;
	}
	
	/**
	 * Altera o tempo inicial quando houver caixa de seleção.
	 * 
	 * @param intInitialTimeSelectionBox
	 */
	protected void setInitialTimeSelectionBox(int intInitialTimeSelectionBox) {
		this.intInitialTimeSelectionBox = intInitialTimeSelectionBox;
	}
	
	/**
     * Retorna o tempo inicial quando houver caixa de seleção.
     * 
     * @return intFinalTimeSelectionBox
     */
	protected int getFinalTimeSelectionBox() {
		return intFinalTimeSelectionBox;
	}
	
	/**
	 * Altera o tempo inicial quando houver caixa de seleção.
	 * 
	 * @param intFinalTimeSelectionBox
	 */
	protected void setFinalTimeSelectionBox(int intFinalTimeSelectionBox) {
		this.intFinalTimeSelectionBox = intFinalTimeSelectionBox;
	}
	
	/**
     * Retorna o tempo que deverá ser atribuído para cada pixel da imagem.
     * 
     * @return dblTimePerPixel
     */
	protected double getTimePerPixel() {
		return dblTimePerPixel;
	}
	
	/**
	 * Altera o tempo da linha de seleção.
	 * 
	 * @param intTimeSelectionLine
	 */
	protected void setTimeSelectionLine(int intTimeSelectionLine) {
		this.intTimeSelectionLine = intTimeSelectionLine;
	}
	
	/**
     * Retorna o tempo da linha de seleção.
     * 
     * @return intTimeSelectionLine
     */
	public int getTimeSelectionLine() {
		return intTimeSelectionLine;
	}
	
	/**
     * Retorna o status para o desenho da linha de seleção.
     * 
     * @return blnDrawSelectionLine
     */
	public boolean getDrawSelectionLine() {
		return blnDrawSelectionLine;
	}
	
	/**
	 * Altera o status para o desenho da linha de seleção.
	 * 
	 * @param blnDrawSelectionLine
	 */
	protected void setDrawSelectionLine(boolean blnDrawSelectionLine) {
		this.blnDrawSelectionLine = blnDrawSelectionLine;
	}
	
	/**
     * Retorna o status para o desenho da caixa de seleção.
     * 
     * @return blnDrawSelectionBox
     */
	public boolean getDrawSelectionBox() {
		return blnDrawSelectionBox;
	}
	
	/**
	 * Altera o status para o desenho da caixa de seleção.
	 * 
	 * @param blnDrawSelectionBox
	 */
	protected void setDrawSelectionBox(boolean blnDrawSelectionBox) {
		this.blnDrawSelectionBox = blnDrawSelectionBox;
	}
	
	/**
     * Retorna o status para mudança do tempo da linha de seleção.<br>
     * <br>
     * Será utilizado exclusivamente quando arrastar a scrollbar horizontal
     * e a linha de seleção acompanhe a visualização do espectrograma.
     * 
     * @return blnChangeTimeSelectionLine
     */
	public boolean getChangeTimeSelectionLine() {
		return blnChangeTimeSelectionLine;
	}
	
	/**
     * Altera o status para mudança do tempo da linha de seleção.<br>
     * <br>
     * Será utilizado exclusivamente quando arrastar a scrollbar horizontal
     * e a linha de seleção acompanhe a visualização do espectrograma.
	 * 
	 * @param blnChangeTimeSelectionLine
	 */
	public void setChangeTimeSelectionLine(boolean blnChangeTimeSelectionLine) {
		this.blnChangeTimeSelectionLine = blnChangeTimeSelectionLine;
	}

	/**
     * Retorna o status para a liberação do botão do mouse.
     * 
     * @return blnMouseButtonReleased
     */
	public boolean getMouseButtonReleased() {
		return blnMouseButtonReleased;
	}
	
	/**
	 * Altera o status para a liberação do botão do mouse.
	 * 
	 * @param blnMouseButtonReleased
	 */
	protected void setMouseButtonReleased(boolean blnMouseButtonReleased) {
		this.blnMouseButtonReleased = blnMouseButtonReleased;
	}
	
	/**
     * Cria um JPanel que renderiza o waveform.
     * 
     * @param objWaveform - Objeto do Waveform
     */
	public GraphicPanel(Waveform objWaveform) {
		this.objWaveform = objWaveform;

		this.collectionListenerWaveform = new ArrayList<Object>();
	}
	
    /**
     * Cria um JPanel que renderiza o espectrograma.
     * 
     * @param objSpectrogram - Objeto do Espectrograma
     */
	public GraphicPanel(Spectrogram objSpectrogram) {
		this.objSpectrogram = objSpectrogram;

		this.collectionListenerSpectrogram = new ArrayList<Object>();
	}

	//*************************************************************************
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}
	
	/**
	 * Altera o valor da linha de seleção.
	 * 
	 * @param blnDrawSelectionLine
	 * @param intTimeSelectionLine
	 */
	public void setSelectionLine(boolean blnDrawSelectionLine, int intTimeSelectionLine) {
		this.blnDrawSelectionLine = blnDrawSelectionLine;
		this.intTimeSelectionLine = intTimeSelectionLine;
	}
	
	/**
	 * Altera os valores da caixa de seleção.
	 * 
	 * @param blnDrawSelectionBox
	 * @param intInitialTimeSelection
	 * @param intFinalTimeSelection
	 */
	public void setSelectionBox(boolean blnDrawSelectionBox, int intInitialTimeSelection, int intFinalTimeSelection) {
		this.blnDrawSelectionBox = blnDrawSelectionBox;
		
		if (blnDrawSelectionBox) {
			this.intInitialTimeSelectionBox = intInitialTimeSelection;
			this.intFinalTimeSelectionBox = intFinalTimeSelection;
		}
	}

	/**
     * Retorna o status da visualização do waveform.<br>
     * <br> 
     * <i>True</i> - Visualização completa do waveform (todo o áudio)<br>
     * <i>False</i> - Visualização parcial do waveform baseando-se no 
     * tempo inicial e final que está sendo exibido o espectrograma
	 */
	public boolean getViewFullWaveform() {
		return false;
	}

	/**
	 * Altera o status da visualização do waveform.<br>
     * <br> 
     * <i>True</i> - Visualização completa do waveform (todo o áudio)<br>
     * <i>False</i> - Visualização parcial do waveform baseando-se no 
     * tempo inicial e final que está sendo exibido o espectrograma
	 * 
	 * @param blnViewFullWaveform
	 */
	public void setViewFullWaveform(boolean blnViewFullWaveform) {
		
	}

	/**
	 * Altera o status da seleção manual de parte do áudio pelo waveform.
	 * 
	 * @param blnManualSelection
	 */
	public void setManualSelection(boolean blnManualSelection) {
		
	}
	
	/**
	 * Retorna o status da seleção manual de parte do áudio pelo waveform.
	 */
	public boolean getManualSelection() {
		return false;
	}
	
	/**
	 * Desenha a linha do player.
	 * 
	 * @param graphic2D - Gráfico onde será desenhado a linha do player
	 */
	protected void drawPlayerLine(Graphics2D graphic2D) {
		if (blnDrawPlayerLine) {
			graphic2D.setColor(Color.RED);
			graphic2D.drawLine(AXES_SIZE + intTimePlayerLine, 0, AXES_SIZE + intTimePlayerLine, intPanelHeight);
		}
	}
	
	/**
	 * Desenha a linha do player.
	 * 
	 * @param graphic2D - Gráfico onde será desenhado a linha do player
	 */
	protected void drawSelectionLine(Graphics2D graphic2D) {
		if (blnDrawSelectionLine) {
			int intX_Initial = AXES_SIZE + (int) ((intTimeSelectionLine / dblTimePerPixel) - (intInitialTime / dblTimePerPixel));
			
			if (intX_Initial == AXES_SIZE) {
				intX_Initial = intX_Initial + 1;
			}
			
			drawDashedLine(graphic2D, intX_Initial, intX_Initial + 1, intPanelHeight);
		}
	}
	
	/**
	 * Desenha uma linha pontilhada para a linha de seleção.<br>
	 * <br>
	 * Esse método foi criado em substituição ao método <i>setStroke</i> 
	 * da classe <i>Graphics2D</i> que apresenta alguns problemas de 
	 * visualização quando o tamanho do painel é alterado.
	 * 
	 * @param graphic2D      - Gráfico onde será desenhado a linha pontilhada
	 * @param intX_Initial   - Posição inicial no eixo X a ser desenhada a linha
	 * @param intX_Final     - Posição final no eixo X a ser desenhada a linha
	 * @param intPanelHeight - Altura do painel
	 */
	protected void drawDashedLine(Graphics2D graphic2D, int intX_Initial, int intX_Final, int intPanelHeight) {
		final int SPACE_BETWEEN_DASHES = 4;   // Espaçamento entre a parte a ser desenhada e a parte vazia
		
		graphic2D.setColor(COLOR_SELECTION_LINE);
		
		for (int indexPanelHeight = 0; indexPanelHeight < intPanelHeight;) {
			for (int indexX = intX_Initial; indexX <= intX_Final; indexX++) {
				graphic2D.drawLine(indexX, indexPanelHeight, indexX, indexPanelHeight + SPACE_BETWEEN_DASHES);
			}
			
			indexPanelHeight = indexPanelHeight + (SPACE_BETWEEN_DASHES * 2);
		}
	}

	/**
	 * Adiciona uma instância do objeto <i>Player</i> à classe atual.
	 * 
	 * @param objPlayer
	 */
    public void addPlayer(Player objPlayer) {
    	this.objPlayer = objPlayer;
    	this.objPlayer.addPlayerListener(this);
    }

	/**
     * Adiciona um 'WaveformListener' parametrizado à 'collection listener'.
     * 
     * @param waveformListener
     */
    public void addWaveformListener(WaveformListener waveformListener) {
    	collectionListenerWaveform.add(waveformListener);
    }
    
	/**
	 * Notifica o 'WaveformListener' de uma atualização na posição atual do mouse no waveform.
	 * 
	 * @param intTime - Posição do mouse no tempo (em milisegundos)
	 */
	protected void updateWaveformMousePosition(int intTime) {
        Iterator<Object> it = collectionListenerWaveform.iterator();
        WaveformListener waveformListener;
        while (it.hasNext()) {
            waveformListener = (WaveformListener) it.next();
            waveformListener.waveformCurrentTime(intTime);
        }
	}
	
	/**
	 * Notifica o 'WaveformListener' de uma atualização na posição de áudio selecionada.
	 * 
	 * @param intCurrentTime     - Posição atual do mouse no tempo (em milisegundos)
	 * @param intInitialTime     - Tempo inicial selecionado (em milisegundos)
	 * @param intFinalTime       - Tempo final selecionado (em milisegundos)
	 */
	protected void updateWaveformSelectedAudio(int intCurrentTime, int intInitialTime, int intFinalTime) {
		Iterator<Object> it = collectionListenerWaveform.iterator();
        WaveformListener waveformListener;
        while (it.hasNext()) {
        	waveformListener = (WaveformListener) it.next();
        	waveformListener.waveformSelectedAudio(intCurrentTime, intInitialTime, intFinalTime);
        }
	}
	
	/**
     * Adiciona um 'SpectrogramListener' parametrizado à 'collection listener'.
     * 
     * @param spectrogramListener
     */
    public void addSpectrogramListener(SpectrogramListener spectrogramListener) {
    	collectionListenerSpectrogram.add(spectrogramListener);
    }
	
	/**
	 * Notifica o 'SpectrogramListener' de uma atualização na posição atual do mouse no espectrograma.
	 * 
	 * @param intTime      - Posição do mouse no tempo (em milisegundos)
	 * @param intFrequency - Posição do mouse na frequência (em Hz)
	 */
	protected void updateSpectrogramMousePosition(int intTime, int intFrequency) {
        Iterator<Object> it = collectionListenerSpectrogram.iterator();
        SpectrogramListener spectrogramListener;
        while (it.hasNext()) {
            spectrogramListener = (SpectrogramListener) it.next();
            spectrogramListener.spectrogramCurrentTimeFrequency(intTime, intFrequency);
        }
	}
	
	/**
	 * Notifica o 'SpectrogramListener' de uma atualização na posição de áudio selecionada no espectrograma.
	 * 
	 * @param intCurrentTime      - Posição atual do mouse no tempo (em milisegundos)
	 * @param intInitialTime      - Tempo inicial selecionado (em milisegundos)
	 * @param intFinalTime        - Tempo final selecionado (em milisegundos)
	 * @param intInitialFrequency - Frequência inicial selecionada (em Hz)
	 * @param intFinalFrequency   - Frequência final selecionada (em Hz)
	 * @param intMinimumFrequency - Frequência mínima do áudio (em Hz)
	 * @param intMaximumFrequency - Frequência máxima do áudio (em Hz)
	 * @param blnDrawWaveform     - Desenha novamente o waveform ao selecionar parte do áudio
	 */
	protected void updateSpectrogramSelectedAudio(int intCurrentTime, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency, int intMinimumFrequency, int intMaximumFrequency, boolean blnDrawWaveform) {
		// Inverte valores se frequência final for menor que a frequência inicial
        if (intFinalFrequency < intInitialFrequency) {
        	int intFrequencyInitialTemp = intInitialFrequency;
        	int intFrequencyFinalTemp = intFinalFrequency;
        	
        	intInitialFrequency = intFrequencyFinalTemp;
        	intFinalFrequency = intFrequencyInitialTemp;
        }
        
        if (intInitialFrequency < intMinimumFrequency) {
        	intInitialFrequency = intMinimumFrequency;
        }
        
        if (intFinalFrequency > intMaximumFrequency) {
        	intFinalFrequency = intMaximumFrequency;
        }
		
		Iterator<Object> it = collectionListenerSpectrogram.iterator();
        SpectrogramListener spectrogramListener;
        while (it.hasNext()) {
        	spectrogramListener = (SpectrogramListener) it.next();
        	spectrogramListener.spectrogramSelectedAudio(intCurrentTime, intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency, blnDrawWaveform);
        }
	}
	
	/**
	 * Notifica o 'SpectrogramListener' de uma atualização na posição de áudio visualizada no espectrograma.
	 * 
	 * @param intInitialTime      - Tempo inicial visualizado (em milisegundos)
	 * @param intFinalTime        - Tempo final visualizado (em milisegundos)
	 * @param intInitialFrequency - Frequência inicial visualizada (em Hz)
	 * @param intFinalFrequency   - Frequência final visualizada (em Hz)
	 */
	protected void updateSpectrogramViewAudio(int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) {
		Iterator<Object> it = collectionListenerSpectrogram.iterator();
        SpectrogramListener spectrogramListener;
        while (it.hasNext()) {
        	spectrogramListener = (SpectrogramListener) it.next();
        	spectrogramListener.spectrogramViewAudio(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
        }
	}
	
	/**
	 * Retorna o tempo total do áudio por pixel.
	 * 
	 * @param intPanelWidth       - Comprimento do painel
	 * @param intTimeMilliseconds - Tempo total (em milisegundos)
	 * 
	 * @return fltTimeLengthPerPixel
	 */
	private float getTimeLengthPerPixel(int intPanelWidth, int intTimeMilliseconds) {
		float fltTimeLengthPerPixel = (float) intTimeMilliseconds / (float) intPanelWidth;
		fltTimeLengthPerPixel = (float) (Math.round(fltTimeLengthPerPixel * 1000.0) / 1000.0);
		
		return fltTimeLengthPerPixel;
	}

	//*************************************************************************
	// Implementa PlayerListener
	@Override
	public void playerStatus(int intStatusPlayer, int intTimeMilliseconds) {
		if (intStatusPlayer != objPlayer.STATUS_STOPPED || objPlayer.getAllowResumeAudio()) {
			updatePlayerTimeElapsed(intTimeMilliseconds);
		} else {
			blnDrawPlayerLine = false;
			repaint();
		}
	}

	@Override
	public void playerTimeElapsed(int intTimeMilliseconds) {
		updatePlayerTimeElapsed(intTimeMilliseconds);
	}
	
	/**
	 * Atualiza a posição do player no painel.
	 * 
	 * @param intTimeMilliseconds
	 */
	private void updatePlayerTimeElapsed(int intTimeMilliseconds) {
		int intTimePlayer = 0;
		
		// Waveform
		if (objWaveform != null) {
			double dblTimePerPixel = getTimeLengthPerPixel(intPanelWidth, objWaveform.getFinalTime() - objWaveform.getInitialTime());
			intTimePlayer = (int) ((intTimeMilliseconds - objWaveform.getInitialTime()) / dblTimePerPixel);
		
		// Espectrograma
		} else if (objSpectrogram != null) {
			double dblTimePerPixel = getTimeLengthPerPixel(intPanelWidth, objSpectrogram.getFinalTime() - objSpectrogram.getInitialTime());
			intTimePlayer = (int) ((intTimeMilliseconds - objSpectrogram.getInitialTime()) / dblTimePerPixel);
		}
		
		// Verifica se a posição atual do player é diferente da última
		if (intTimePlayer != intTimePlayerLine || objPlayer.getAllowResumeAudio()) {
			intTimePlayerLine = intTimePlayer;
			
			if (!blnDrawPlayerLine) {
				blnDrawPlayerLine = true;
			}
			
			repaint();
		}
	}
}