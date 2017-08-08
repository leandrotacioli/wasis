package br.unicamp.fnjv.wasis.graphics.waveform;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import javax.swing.SwingUtilities;

import br.unicamp.fnjv.wasis.graphics.GraphicPanel;

/**
 * Painel responsável pela exibição do waveform.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 01/Jul/2016
 */
public class WaveformGraphicPanel extends GraphicPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -3552488562361108767L;

	private Waveform objWaveform;

    /**
     * Status da visualização do waveform.<br>
     * <br> 
     * <i>True</i> - Visualização completa do waveform (todo o áudio)<br>
     * <i>False</i> - Visualização parcial do waveform baseando-se no 
     * tempo inicial e final que está sendo exibido o espectrograma
     */
    private boolean blnViewFullWaveform;
    
    /**
     * Trecho do áudio selecionado manualmente pelo usuário (válido apenas quando 'blnViewFullWaveform' = true).
     */
    private boolean blnManualSelection;
    
    /**
	 * Ponto âncora quando há uma seleção.
	 */
	private Point pointAnchor;
	
	/**
	 * Painel responsável pela exibição do waveform.
	 * 
	 * @param objWaveform - Waveform
	 */
	public WaveformGraphicPanel(Waveform objWaveform) {
		super(objWaveform);
		
		this.objWaveform = objWaveform;

		this.setOpaque(false);
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		super.setDrawSelectionLine(true);
	}
	
	//*************************************************************************
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (!objWaveform.getIsRenderingWaveform()) {
			Graphics2D graphic2D = (Graphics2D) g;
			
			super.setInitialTime(objWaveform.getInitialTime());
			super.setFinalTime(objWaveform.getFinalTime());
			
			// Determina a área que o waveform pode ser desenhado
			int intTotalWidth = (int) graphic2D.getClip().getBounds().getMaxX();
			int intTotalHeight = (int) graphic2D.getClip().getBounds().getMaxY();
			
			graphic2D.setClip(AXES_SIZE, 0, intTotalWidth, intTotalHeight);
			
			// ******************************************************************************************************
			// Verifica o tamanho do painel.
			// Caso houver alteração no tamanho, é realizada novamente a renderização do waveform.
			if (super.getPanelWidth() != super.getPanelWidthTemporary() || super.getPanelHeight() != super.getPanelHeightTemporary()) {
				objWaveform.setPanelWidth(super.getPanelWidth());
				objWaveform.setPanelHeight(super.getPanelHeight());
				
				if (super.getPanelWidthTemporary() != 0 && super.getPanelHeightTemporary() != 0) {
					objWaveform.scaleFinalImage(objWaveform.getWaveformImage(), super.getPanelWidth(), super.getPanelHeight());
				}
				
				super.setPanelWidthTemporary(super.getPanelWidth());
				super.setPanelHeightTemporary(super.getPanelHeight());
			}
			
			graphic2D.drawImage(objWaveform.getWaveformImageFinal(), AXES_SIZE, 0, null);

			// ******************************************************************************************************
			// Linha de seleção
			super.drawSelectionLine(graphic2D);
			
			// ******************************************************************************************************
			// Linha do player
			super.drawPlayerLine(graphic2D);
			
			// ******************************************************************************************************
			// Caixa de seleção
			if (super.getDrawSelectionBox()) {
				if (blnViewFullWaveform && !blnManualSelection) {
					super.setInitialTimeSelectionBox(objWaveform.getInitialTimeSpectrogram());
					super.setFinalTimeSelectionBox(objWaveform.getFinalTimeSpectrogram());
				}

		        // Tempo
				int intX_Initial = AXES_SIZE + (int) ((super.getInitialTimeSelectionBox() / super.getTimePerPixel()) - (super.getInitialTime() / super.getTimePerPixel()));
				int intX_Final = AXES_SIZE + (int) ((super.getFinalTimeSelectionBox() / super.getTimePerPixel()) - (super.getInitialTime() / super.getTimePerPixel()));
				int intWidth = intX_Final - intX_Initial;
				
				// Ajusta a posição do tempo, para não mostrar a seleção fora dos limites do waveform
				if (intX_Initial == AXES_SIZE) {
					intX_Initial = AXES_SIZE + 1;
					intWidth = intX_Final - intX_Initial;
				}
				
				// Intensidade
				int intY_Initial = 0;
				int intHeight = super.getPanelHeight();

	        	if (blnViewFullWaveform) {
	    			graphic2D.setColor(Color.GREEN);
	        	} else {
					graphic2D.setColor(super.COLOR_SELECTION_BOX);
	        	}
	        	
	        	graphic2D.setComposite(super.COMPOSITE_SELECTION_BOX);
	        	graphic2D.fillRect(intX_Initial, intY_Initial, intWidth, intHeight);
	        	
	        	// Desenha uma linha pontilhada ao redor da caixa de seleção
	        	graphic2D.setComposite(super.COMPOSITE_SELECTION_BOX_BORDER);
		        graphic2D.setStroke(super.STROKE_SELECTION_BOX_BORDER);
		        graphic2D.draw(new Rectangle2D.Double(intX_Initial + 1, intY_Initial + 1, intWidth - 2, intHeight - 2));
			}
			
			// ******************************************************************************************************
			// Eixos do waveform
			if (AXES_SIZE > 0) {
				// Determina a área que os eixos podem ser desenhados
				graphic2D.setClip(0, 0, intTotalWidth, intTotalHeight);
				
				graphic2D.setComposite(AlphaComposite.SrcOver.derive(1.00f));
				graphic2D.setStroke(new BasicStroke(1.0f));
				graphic2D.setColor(Color.BLACK);
				graphic2D.setFont(new Font("Tahoma", Font.PLAIN, 11));
				
				drawAxis(graphic2D);
		    }
			
			graphic2D.dispose();
		}
	}
    
    @Override
	public void setViewFullWaveform(boolean blnViewFullWaveform) {
		this.blnViewFullWaveform = blnViewFullWaveform;
		
		if (blnViewFullWaveform) {
			super.setInitialTimeSelectionBox(objWaveform.getInitialTimeSpectrogram());
			super.setFinalTimeSelectionBox(objWaveform.getFinalTimeSpectrogram());
		}
	}
    
    @Override
    public boolean getViewFullWaveform() {
		return blnViewFullWaveform;
	}

    @Override
	public void setManualSelection(boolean blnManualSelection) {
		this.blnManualSelection = blnManualSelection;
	}
    
    @Override
    public boolean getManualSelection() {
		return blnManualSelection;
	}
	
	/**
	 * Desenha os eixos.
	 * 
	 * @param graphic2D
	 */
	private void drawAxis(Graphics2D graphic2D) {
		graphic2D.drawLine(AXES_SIZE, super.getPanelHeight() / 2, AXES_SIZE - 5, super.getPanelHeight() / 2);  // Eixo X
		graphic2D.drawLine(AXES_SIZE, 0, AXES_SIZE, super.getPanelHeight());                                   // Eixo Y
	}

	//*************************************************************************
	// Implementa Mouse Listener
	@Override
	public void mouseClicked(MouseEvent event) {
		
	}

	@Override
	public void mouseEntered(MouseEvent event) {

	}

	@Override
	public void mouseExited(MouseEvent event) {
		
	}

	@Override
	public void mousePressed(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			super.setMouseButtonReleased(true);
			
			pointAnchor = event.getPoint();
			
			int intCurrentPosition = event.getX() - AXES_SIZE;
			int intCurrentTime = (int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition));
			
			if (intCurrentPosition < 0) {
				intCurrentTime = super.getInitialTime();
			}

			super.setTimeSelectionLine(intCurrentTime);
			
			// Visualização completa do waveform (todo o áudio)
			if (blnViewFullWaveform) {
				if (intCurrentTime < super.getInitialTimeSelectionBox()) {
					intCurrentTime = super.getInitialTimeSelectionBox();
				} else if (intCurrentTime > super.getFinalTimeSelectionBox()) {
					intCurrentTime = super.getInitialTimeSelectionBox();
				}
				
			// Visualização parcial do waveform baseando-se no tempo inicial e final que está sendo exibido o espectrograma
			} else {
				super.setDrawSelectionLine(true);
				super.setDrawSelectionBox(false);
			}
			
			super.updateWaveformSelectedAudio(intCurrentTime, intCurrentTime, intCurrentTime);
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			super.setMouseButtonReleased(false);
			
			// Visualização completa do waveform (todo o áudio)
			if (blnViewFullWaveform) {
				// Realiza a comparação do ponto âncora com o ponto de liberação do botão do mouse
				// Ponto âncora
				int intAnchorPosition = pointAnchor.x - AXES_SIZE;
				int intAnchorTime = (int) (super.getInitialTime() + (super.getTimePerPixel() * intAnchorPosition));
				
				if (intAnchorPosition < 0) {
					intAnchorTime = super.getInitialTime();
				}
				
				// Ponto de liberação do botão do mouse
				int intCurrentPosition = event.getX() - AXES_SIZE;
				int intCurrentTime = (int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition));
				
				if (intCurrentPosition < 0) {
					intCurrentTime = super.getInitialTime();
				}
				
				// Não houve uma mudança do ponto âncora com o ponto de liberação do botão do mouse
				// Fica caracterizado que o mouse foi apenas clicado
				if (intAnchorTime == intCurrentTime) {
					if (intCurrentTime < super.getInitialTimeSelectionBox()) {
						intCurrentTime = super.getInitialTimeSelectionBox();
					} else if (intCurrentTime > super.getFinalTimeSelectionBox()) {
						intCurrentTime = super.getInitialTimeSelectionBox();
					}
					
					super.updateWaveformSelectedAudio(intCurrentTime, intCurrentTime, intCurrentTime);
				}
			}
		}
	}

	//*************************************************************************
	// Implementa MouseMotionListener
	@Override
	public void mouseDragged(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			try {
				Thread.sleep(25);  // Dorme por um instante para não sobrecarregar a CPU
			
				// Visualização completa do waveform (todo o áudio)
				if (blnViewFullWaveform) {
					setManualSelection(true);
				}
				
				int intCurrentPosition = event.getX() - AXES_SIZE;
				int intCurrentTime = (int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition)); // Tempo do áudio enquanto arrastando
				
				// Tempo atual não pode ser menor que o tempo inicial que está sendo mostrado na tela
				if (intCurrentTime < super.getInitialTime()) {
					intCurrentTime = super.getInitialTime();
					
				// Tempo atual não pode ser maior que o tempo final que está sendo mostrado na tela
				} else if (intCurrentTime > super.getFinalTime()) {
					intCurrentTime = super.getFinalTime();
				}
				
				// Seleção inicial foi feita a partir do eixo Y (dentro do eixo)
				if (pointAnchor.x < AXES_SIZE) {
					// Ponto de seleção atual está dentro do painel
					if (intCurrentPosition >= 0) {
						super.setInitialTimeSelectionBox(super.getInitialTime());
						super.setFinalTimeSelectionBox(intCurrentTime);

					// Ponto de seleção atual continua fora do painel
					} else {
						// Visualização completa do waveform (todo o áudio)
						if (blnViewFullWaveform) {
							super.setInitialTimeSelectionBox(objWaveform.getInitialTimeSpectrogram());
							super.setFinalTimeSelectionBox(objWaveform.getFinalTimeSpectrogram());
						
						// Visualização parcial do waveform baseando-se no tempo inicial e final que está sendo exibido o espectrograma
						} else {
							super.setInitialTimeSelectionBox(0);
							super.setFinalTimeSelectionBox(0);
						}
					}
	
				// Seleção inicial foi feita dentro do painel
				} else if (pointAnchor.x >= AXES_SIZE) {
					int intInitialPosition = (int) Math.min(pointAnchor.x, event.getX());   // Posição da seleção no eixo X
					int intAnchorPosition = pointAnchor.x - AXES_SIZE;
					
					// **************************************************************************************
					// Ponto de seleção inicial é menor que o ponto de seleção atual
					if (intAnchorPosition < intCurrentPosition) {
						super.setInitialTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * (intInitialPosition - AXES_SIZE))));
						super.setFinalTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition)));
						
					// Ponto de seleção inicial é maior que o ponto de seleção atual
					} else if (intAnchorPosition > intCurrentPosition) {
						super.setInitialTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition)));
						super.setFinalTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intAnchorPosition)));
					
					// Ponto de seleção inicial é igual ao ponto de seleção atual
					} else if (intAnchorPosition == intCurrentPosition) {
						super.setInitialTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intAnchorPosition)));
						super.setFinalTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intAnchorPosition)));
					}
					
					// **************************************************************************************
					// Caso a seleção tenha saído pela esquerda no painel
					if (intInitialPosition < AXES_SIZE) {
						super.setInitialTimeSelectionBox(super.getInitialTime());
						super.setFinalTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intAnchorPosition)));
					}
					
					// **************************************************************************************
					// Caso a seleção tenha saído pela direita no painel
					if (event.getX() > super.getPanelWidth() + AXES_SIZE) {
						super.setInitialTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * (intInitialPosition - AXES_SIZE))));
						super.setFinalTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * super.getPanelWidth())));
					}
				}
				
				// Caso o tempo inicial for igual ao tempo final e a visualização não for completa, é considerada uma linha de simples
				if (super.getInitialTimeSelectionBox() == super.getFinalTimeSelectionBox() && !blnViewFullWaveform) {
					super.setDrawSelectionLine(true);
					super.setDrawSelectionBox(false);
					
				// Senão é considerada uma caixa de seleção
				} else {
					super.setDrawSelectionLine(false);
					super.setDrawSelectionBox(true);
				}
				
				super.updateWaveformSelectedAudio(intCurrentTime, super.getInitialTimeSelectionBox(), super.getFinalTimeSelectionBox());
				super.updateWaveformMousePosition(intCurrentTime);
				
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}	
	}

	@Override
	public void mouseMoved(MouseEvent event) {
		try {
			Thread.sleep(25);  // Dorme por um instante para não sobrecarregar a CPU
			
			// **************************************************************************************
	        // Tempo
			int intTimePosition = event.getX() - AXES_SIZE;
			int intTimeMilliseconds = (int) (super.getInitialTime() + (super.getTimePerPixel() * intTimePosition));
			
			if (intTimeMilliseconds < super.getInitialTime()) {
				intTimeMilliseconds = super.getInitialTime();
			} else if (intTimeMilliseconds > super.getFinalTime()) {
				intTimeMilliseconds = super.getFinalTime();
			}
	        
	        super.updateWaveformMousePosition(intTimeMilliseconds);
	        
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}