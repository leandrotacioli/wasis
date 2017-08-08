package br.unicamp.fnjv.wasis.graphics.spectrogram;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporary;
import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporarySegments;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Painel responsável pela exibição do espectrograma.
 * 
 * @author Leandro Tacioli
 * @version 4.1 - 08/Mai/2017
 */
public class SpectrogramGraphicPanel extends GraphicPanel implements MouseListener, MouseMotionListener {
	private static final long serialVersionUID = -7191590714199695177L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private Spectrogram objSpectrogram;           // Objeto do Espectrograma

    private int intInitialTimeTemporary;          // Tempo temporário inicial do áudio que está sendo mostrado na tela
    private int intFinalTimeTemporary;            // Tempo temporário final do áudio que está sendo mostrado na tela
    
    private int intInitialFrequency;              // Frequência inicial do áudio que está sendo mostrada na tela
    private int intInitialFrequencyTemporary;     // Frequência temporária inicial do áudio que está sendo mostrada na tela
    
    private int intFinalFrequency;                // Frequência final do áudio que está sendo mostrada na tela
    private int intFinalFrequencyTemporary;       // Frequência temporária final do áudio que está sendo mostrada na tela
    
	private int intInitialFrequencySelectionBox;  // Frequência inicial quando houver seleção pela 'Box Selection'
	private int intFinalFrequencySelectionBox;    // Frequência final quando houver seleção pela 'Box Selection'
	
	private double dblFrequencyPerPixel;          // Taxa de frequência que deverá ser atribuída para cada pixel da imagem

	private int intInitialHourAxis;               // Tempo em horas inicial que será mostrado no eixo X 
	private int intInitialMinutesAxis;            // Tempo em minutos inicial que será mostrado no eixo X
	private int intInitialSecondsAxis;            // Tempo em segundos inicial que será mostrado no eixo X
	private int intInitialMillisecondsAxis;       // Tempo em milisegundos inicial quando o eixo do tempo estiver mostrando valores em milisegundos
	private boolean blnMillisecondsAxis;          // Visualização do eixo X só compreende valores em milisegundos
	
	private List<SpectrogramGraphicPanelAxesValues> lstTimeAxisValues;       // Lista de valores do eixo de tempo
	private List<SpectrogramGraphicPanelAxesValues> lstFrequencyAxisValues;  // Lista de valores do eixo de frequência

	/**
	 * Ponto âncora quando há uma seleção.
	 */
	private Point pointAnchor;
	
	/**
	 * Lista de todas os segmentos de áudio (ROIS) do espectrograma.
	 */
	private List<AudioTemporarySegments> lstSegments;
	
	/**
	 * Menu acionado com o botão direto do mouse.
	 */
	private JPopupMenu popupMenu;
	private JMenuItem menuItemAddNewSoundUnit;
	private JMenuItem menuItemAddNewSoundUnitForNewSpecies;
	private JMenuItem menuItemZoomCurrentSelection;
	private JMenuItem menuItemShowSelectionList;
	
	/**
	 * Painel responsável pela exibição do espectrograma.
	 * 
	 * @param objSpectrogram - Objeto do Espectrograma
	 */
	public SpectrogramGraphicPanel(Spectrogram objSpectrogram) {
		super(objSpectrogram);
		
		this.objSpectrogram = objSpectrogram;
		this.lstSegments = AudioTemporary.getAudioTemporary().get(objSpectrogram.getAudioTemporaryIndex()).getAudioTemporarySegments();
		
		this.setOpaque(false);
		
		this.createPopupMenu();
		
		this.addMouseListener(this);
		this.addMouseMotionListener(this);
		
		super.setDrawSelectionLine(true);
	}
	
	//*************************************************************************
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		if (!objSpectrogram.getIsRenderingSpectrogram()) {
			Graphics2D graphic2D = (Graphics2D) g;

			super.setInitialTime(objSpectrogram.getInitialTime());
			super.setFinalTime(objSpectrogram.getFinalTime());
	
			intInitialFrequency = objSpectrogram.getInitialFrequency();
			intFinalFrequency = objSpectrogram.getFinalFrequency();
			
			dblFrequencyPerPixel = (double) (intFinalFrequency - intInitialFrequency) / super.getPanelHeight();
			
			// Determina a área que o espectrograma pode ser desenhado
			int intTotalWidth = (int) graphic2D.getClip().getBounds().getMaxX();
			int intTotalHeight = (int) graphic2D.getClip().getBounds().getMaxY();
			
			graphic2D.setClip(AXES_SIZE, 0, intTotalWidth, super.getPanelHeight());
			
			// ******************************************************************************************************
			// Espectrograma
			// Verifica o tamanho do painel - Caso houver alteração no tamanho, é realizada novamente a renderização do espectrograma.
			if (super.getPanelWidth() != super.getPanelWidthTemporary() || super.getPanelHeight() != super.getPanelHeightTemporary()) {
				objSpectrogram.setPanelWidth(super.getPanelWidth());
				objSpectrogram.setPanelHeight(super.getPanelHeight());

				if (super.getPanelWidthTemporary() != 0 && super.getPanelHeightTemporary() != 0) {
					objSpectrogram.scaleFinalImage(objSpectrogram.getSpectrogramFinalImage(), super.getPanelWidth(), super.getPanelHeight());
				}
				
				super.setPanelWidthTemporary(super.getPanelWidth());
				super.setPanelHeightTemporary(super.getPanelHeight());
			}
			
			graphic2D.drawImage(objSpectrogram.getSpectrogramFinalImage(), AXES_SIZE, 0, null);

			// ******************************************************************************************************
			// Linha de seleção
			super.drawSelectionLine(graphic2D);
			
			// ******************************************************************************************************
			// Linha do player
			super.drawPlayerLine(graphic2D);
			
			// ******************************************************************************************************
			// Caixa de seleção
			if (super.getDrawSelectionBox()) {
				int intX_Initial = AXES_SIZE + (int) ((super.getInitialTimeSelectionBox() / super.getTimePerPixel()) - (super.getInitialTime() / super.getTimePerPixel()));
				int intX_Final = AXES_SIZE + (int) ((super.getFinalTimeSelectionBox() / super.getTimePerPixel()) - (super.getInitialTime() / super.getTimePerPixel()));
				int intWidth = intX_Final - intX_Initial;
				
				// Ajusta a posição do tempo, para não mostrar a seleção fora dos limites do espectrograma
				if (intX_Initial == AXES_SIZE) {
					intX_Initial = AXES_SIZE + 1;
					intWidth = intX_Final - intX_Initial;
				}
				
				// Frequência
				int intY_Initial = super.getPanelHeight() - (int) ((intInitialFrequencySelectionBox / dblFrequencyPerPixel) - (intInitialFrequency / dblFrequencyPerPixel));
				int intY_Final = super.getPanelHeight() - (int) ((intFinalFrequencySelectionBox / dblFrequencyPerPixel) - (intInitialFrequency / dblFrequencyPerPixel));
				int intHeight = intY_Initial - intY_Final;
				
				// 'intX_Initial' = Posição referente ao tempo inicial
				// 'intY_Final'   = Posição referente a frequência final (pois a frequência é mostrada com o valor máximo no topo)
				graphic2D.setComposite(super.COMPOSITE_SELECTION_BOX);
				graphic2D.setColor(super.COLOR_SELECTION_BOX);
				graphic2D.fillRect(intX_Initial, intY_Final, intWidth, intHeight);

	            // Desenha uma linha pontilhada ao redor da caixa de seleção
				graphic2D.setComposite(super.COMPOSITE_SELECTION_BOX_BORDER);
	            graphic2D.setStroke(super.STROKE_SELECTION_BOX_BORDER);
	            graphic2D.draw(new Rectangle2D.Double(intX_Initial + 1, intY_Final + 1, intWidth - 2, intHeight - 2));
	        }
			
			// ******************************************************************************************************
			// Lista de segmentos de áudio (ROIs)
			if (lstSegments.size() > 0) {
				for (int indexSelection = 0; indexSelection < lstSegments.size(); indexSelection++) {
					// Tempo
					int intX_Initial = AXES_SIZE + (int) ((lstSegments.get(indexSelection).getInitialTime() / super.getTimePerPixel()) - (super.getInitialTime() / super.getTimePerPixel()));
					int intX_Final = AXES_SIZE + (int) ((lstSegments.get(indexSelection).getFinalTime() / super.getTimePerPixel()) - (super.getInitialTime() / super.getTimePerPixel()));
					int intWidth = intX_Final - intX_Initial;
					
					// Ajusta a posição do tempo, para não mostrar a seleção fora dos limites do espectrograma
					if (intX_Initial == AXES_SIZE) {
						intX_Initial = AXES_SIZE + 1;
						intWidth = intX_Final - intX_Initial;
					}

					// Frequência
					int intY_Initial = super.getPanelHeight() - (int) ((lstSegments.get(indexSelection).getInitialFrequency() / dblFrequencyPerPixel) - (intInitialFrequency / dblFrequencyPerPixel));
					int intY_Final = super.getPanelHeight() - (int) ((lstSegments.get(indexSelection).getFinalFrequency() / dblFrequencyPerPixel) - (intInitialFrequency / dblFrequencyPerPixel));
					int intHeight = intY_Initial - intY_Final;
		            
		            graphic2D.setComposite(super.COMPOSITE_SELECTION_BOX);
		            graphic2D.setColor(super.COLOR_SELECTION_BOX);
		            
		            // 'intX_Initial' = Posição referente ao tempo inicial
					// 'intY_Final'   = Posição referente a frequência final (pois a frequência é mostrada com o valor máximo no topo)
					graphic2D.fillRect(intX_Initial, intY_Final, intWidth, intHeight);
		            
					// Desenha uma linha pontilhada ao redor da caixa de seleção
					graphic2D.setComposite(super.COMPOSITE_SELECTION_BOX_BORDER);
		            graphic2D.setStroke(super.STROKE_SELECTION_BOX_BORDER);
		            graphic2D.draw(new Rectangle2D.Double(intX_Initial + 1, intY_Final + 1, intWidth - 2, intHeight - 2));
		            
		            // Informa a identificação dentro da caixa de seleção
		            String strSoundUnit = lstSegments.get(indexSelection).getSoundUnit();
		            
		            if (lstSegments.get(indexSelection).getIdDatabase() != 0) {
		            	graphic2D.setColor(new Color(65, 50, 225));     // Azul para as seleções armazenadas no banco de dados
		            } else {
		            	graphic2D.setColor(new Color(30, 150, 50));     // Verde para as seleções temporárias
		            }
		            
		            graphic2D.setFont(new Font("Tahoma", Font.BOLD, 12));
		            graphic2D.setComposite(AlphaComposite.SrcOver.derive(0.75f));
		            graphic2D.drawString(strSoundUnit, intX_Initial + 3, intY_Final + 12);
				}
			}
			
			// ******************************************************************************************************
			// Caso houver alteração na visualização dos tempos e frequências inicias e finais
			if (super.getInitialTime() != intInitialTimeTemporary || super.getFinalTime() != intFinalTimeTemporary || intInitialFrequency != intInitialFrequencyTemporary || intFinalFrequency != intFinalFrequencyTemporary) {
				super.updateSpectrogramViewAudio(super.getInitialTime(), super.getFinalTime(), intInitialFrequency, intFinalFrequency);
				
				intInitialTimeTemporary = super.getInitialTime();
				intFinalTimeTemporary = super.getFinalTime();
				
				intInitialFrequencyTemporary = intInitialFrequency;
				intFinalFrequencyTemporary = intFinalFrequency;
				
				extractTimeAxisValues();
				extractFrequencyAxisValues();
			}
			
			// ******************************************************************************************************
			// Desenha os eixos do espectrograma
			// Determina a área que os eixos podem ser desenhados
			graphic2D.setClip(0, 0, intTotalWidth, intTotalHeight);
			
			graphic2D.setComposite(AlphaComposite.SrcOver.derive(1.00f));
			graphic2D.setStroke(new BasicStroke(1.0f));
			graphic2D.setColor(Color.BLACK);
			graphic2D.setFont(new Font("Tahoma", Font.PLAIN, 11));
			
			drawTimeAxis(graphic2D);
			drawFrequencyAxis(graphic2D);

			graphic2D.dispose();
		}
	}
	
	/**
	 * Desenha o eixo X com os respectivos valores de unidade de tempo.
	 * 
	 * @param graphic2D
	 */
	private void drawTimeAxis(Graphics2D graphic2D) {
		// Linha do eixo X
		graphic2D.setColor(new Color(0, 0, 0, 255));
		graphic2D.drawLine(AXES_SIZE, super.getPanelHeight(), super.getPanelWidth() + AXES_SIZE, super.getPanelHeight());
		
		String strLineValue;
		int intLineValue;
		int intLineXPosition;
		int intLastStringXPosition = 0;
		
		final int LINE_HEIGHT = 5;
		final int LINE_HEIGHT_MINOR = 2;
		
		for (int indexTimeAxisValues = 0; indexTimeAxisValues < lstTimeAxisValues.size(); indexTimeAxisValues++) {
			if (indexTimeAxisValues == 0) {
				strLineValue = rsBundle.getString("spectrogram_graphic_panel_x_axis_description");
				intLineXPosition = AXES_SIZE;
				
			} else {
				if (!blnMillisecondsAxis) {
					strLineValue = ClockTransformations.millisecondsIntoClockFormatGraphAxis(lstTimeAxisValues.get(indexTimeAxisValues).getValue());
					intLineXPosition = (int) (AXES_SIZE + ((double) ((lstTimeAxisValues.get(indexTimeAxisValues).getValue() - super.getInitialTime()) / super.getTimePerPixel())));
				} else {
					// Milisegundos
					intLineValue = (intInitialHourAxis * 60 * 60 * 1000) + (intInitialMinutesAxis * 60 * 1000) + (intInitialSecondsAxis * 1000) + lstTimeAxisValues.get(indexTimeAxisValues).getValue();
					strLineValue = ClockTransformations.millisecondsIntoClockFormatGraphAxis(intLineValue);
					intLineXPosition = (int) (AXES_SIZE + ((double) ((lstTimeAxisValues.get(indexTimeAxisValues).getValue() - intInitialMillisecondsAxis) / super.getTimePerPixel())));
				}
			}
			
			if (intLineXPosition >= AXES_SIZE) {
				if (lstTimeAxisValues.get(indexTimeAxisValues).getShowString() || indexTimeAxisValues == 0) {
					graphic2D.drawLine(intLineXPosition, super.getPanelHeight(), intLineXPosition, super.getPanelHeight() + LINE_HEIGHT);
					
					Dimension dmsValueSize = new Dimension(graphic2D.getFontMetrics().stringWidth(strLineValue), graphic2D.getFontMetrics().getHeight());
					
					int intStringXPosition = intLineXPosition - dmsValueSize.width / 2;
					int intStringYPosition = super.getPanelHeight() + LINE_HEIGHT + dmsValueSize.height / 3 + 10;
					
					if (intStringXPosition > intLastStringXPosition) {
						graphic2D.drawString(strLineValue, intStringXPosition, intStringYPosition);
					}
					
					intLastStringXPosition = intStringXPosition + graphic2D.getFontMetrics().stringWidth(strLineValue);
					
				} else {
					graphic2D.drawLine(intLineXPosition, super.getPanelHeight(), intLineXPosition, super.getPanelHeight() + LINE_HEIGHT_MINOR);
				}
			}
		}
	}
	
	/**
	 * Extrai os valores de unidade de tempo que serão visualizados no eixo X.
	 */
	private void extractTimeAxisValues() {
		lstTimeAxisValues = new ArrayList<SpectrogramGraphicPanelAxesValues>();
		
		int intMinValues = 6;        // Quantidade mínima de valores que serão mostrados
		int intNumMajorRegs = 0;     // Número de registros principais que serão mostrados com os valores
		int intNumMinorRegs = 0;     // Número de registros secundários (sem string) que serão mostrados com os valores
		int intMajorValue;           // Valor dos registros principais
		int intMinorValue;           // Valor dos registros secundários
		int intTimeDifference = super.getFinalTime() - super.getInitialTime();  // Diferença entre o valor inicial e final que o zoom se encontra
		
		// Quantidade total de horas, minutos, segundos e milisegundos que está sendo mostrado
		int intTimeTotal = (int) Math.floor(intTimeDifference / 1000);
		int intMinutes = (int) Math.floor(intTimeTotal / 60);
    	int intHours = (int) Math.floor(intMinutes / 60);
    	intMinutes = intMinutes - intHours * 60;
    	int intSeconds = (int) (intTimeTotal - intMinutes * 60 - intHours * 3600);
    	
    	String strTime = String.format("%03d", intTimeDifference);    // Insere 3 zeros a esquerda
    	String strMilliseconds = strTime.substring(strTime.length() - 3, strTime.length());
    	int intMilliseconds = Integer.parseInt(strMilliseconds);
    	
    	// Valor inicial de horas, minutos, segundos e milisegundos (irá ser essencial para quando houver zoom)
    	int intTimeInitialTotal = (int) Math.floor(super.getInitialTime() / 1000);
    	intInitialMinutesAxis = (int) Math.floor(intTimeInitialTotal / 60);
    	intInitialHourAxis = (int) Math.floor(intInitialMinutesAxis / 60);
    	intInitialMinutesAxis = intInitialMinutesAxis - intInitialHourAxis * 60;
    	intInitialSecondsAxis = (int) (intTimeInitialTotal - intInitialMinutesAxis * 60 - intInitialHourAxis * 3600);
    	
    	String strTimeInitial = String.format("%03d", super.getInitialTime()); // Insere 3 zeros a esquerda
    	String strMillisecondsInitial = strTimeInitial.substring(strTimeInitial.length() - 3, strTimeInitial.length());
    	intInitialMillisecondsAxis = Integer.parseInt(strMillisecondsInitial);
    	blnMillisecondsAxis = false;
    	
    	// Implementa uma matriz responsável por incrementar uma quantidade de valores
    	// para que se ache a melhor visualização dos valores no eixo X
    	
    	// Valores para 'hora/minuto' que compreendem os registros principais e os registros secundários (sem string)
    	final float[][] fltIncrementHourMinute = new float[][] { {5.0f, 1.0f,                0.5f,                0.25f},                  // Registros principais
    												             {1.0f, 0.1666666666666667f, 0.0833333333333333f, 0.0833333333333333f} };  // Registros secundários

    	int indexIncrement = 0;
    	
    	while (true) {
    		// Horas
    		if (intHours > 0) {
    			intNumMajorRegs = 0;
    			lstTimeAxisValues = new ArrayList<SpectrogramGraphicPanelAxesValues>();
    			
    			for (float fltX = intInitialHourAxis; fltX <= intInitialHourAxis + intHours + 2; fltX += fltIncrementHourMinute[0][indexIncrement]) {
    				intMajorValue = (int) (fltX * 60 * 60 * 1000);   // 60 * 60 * 1000 = 1 hora 
    				lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMajorValue, true));
					
					if (intMajorValue >= super.getInitialTime() && intMajorValue <= super.getFinalTime()) {
						intNumMajorRegs++;
					}
					
					intNumMinorRegs = (int) (fltIncrementHourMinute[0][indexIncrement] / fltIncrementHourMinute[1][indexIncrement]);
					
					for (int intY = 1; intY <= intNumMinorRegs; intY++) {
						intMinorValue = intMajorValue + (int) (intY * fltIncrementHourMinute[1][indexIncrement] * 60 * 60 * 1000);
						lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMinorValue, false));
					}
    			}
    			
    			indexIncrement++;
    			
    			if (intNumMajorRegs > intMinValues || indexIncrement == fltIncrementHourMinute[0].length) {
    				adjustTimeAxisValues();
    				intNumMajorRegs = getNumRegsMajor(lstTimeAxisValues);
    				
    				if (intNumMajorRegs > intMinValues || indexIncrement == fltIncrementHourMinute[0].length) {
        				break;
        			}
    			}
    		
    		// Minutos
    		} else if (intMinutes > 0) {
    			intNumMajorRegs = 0;
    			lstTimeAxisValues = new ArrayList<SpectrogramGraphicPanelAxesValues>();
    			
    			for (float fltX = intInitialMinutesAxis; fltX <= intInitialMinutesAxis + intMinutes + 2; fltX += fltIncrementHourMinute[0][indexIncrement]) {
    				intMajorValue = (int) (fltX * 60 * 1000);   // 60 * 1000 = 1 minuto 
    				lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMajorValue, true));
					
					if (intMajorValue >= super.getInitialTime() && intMajorValue <= super.getFinalTime()) {
						intNumMajorRegs++;
					}
					
					intNumMinorRegs = (int) (fltIncrementHourMinute[0][indexIncrement] / fltIncrementHourMinute[1][indexIncrement]);
					
					for (int intY = 1; intY <= intNumMinorRegs; intY++) {
						intMinorValue = intMajorValue + (int) (intY * fltIncrementHourMinute[1][indexIncrement] * 60 * 1000);
						lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMinorValue, false));
					}
    			}
    			
    			indexIncrement++;
    			
    			if (intNumMajorRegs > intMinValues || indexIncrement == fltIncrementHourMinute[0].length) {
    				adjustTimeAxisValues();
    				intNumMajorRegs = getNumRegsMajor(lstTimeAxisValues);
    				
    				if (intNumMajorRegs > intMinValues || indexIncrement == fltIncrementHourMinute[0].length) {
        				break;
        			}
    			}
    			
    		// Segundos
    		} else if (intSeconds > 0) {
    	    	// Valores para 'segundo' que compreendem os registros principais e os registros secundários (sem string)
    	    	float[][] fltIncrementSecond = null;
    	    	int intDivider = 1;
    			
    			// Encontra um divisor mais apropriado levando-se em consideração o total de milisegundos da exibição
    			if (intSeconds >= 20 && intSeconds < 60) {
					intDivider = 5;
					
					fltIncrementSecond = new float[][] { {5.0f},             // Registros principais
														 {1.0f} };           // Registros secundários
					
				} else if (intSeconds >= 5 && intSeconds < 20) {
					intDivider = 5;
					
					fltIncrementSecond = new float[][] { {1.0f},             // Registros principais
														 {0.25f} };          // Registros secundários
				
				} else if (intSeconds >= 1 && intSeconds < 5) {
					intDivider = 1;
					
					fltIncrementSecond = new float[][] { {0.50f},            // Registros principais
														 {0.10f} };          // Registros secundários
					
				}
    			
    			intNumMajorRegs = 0;
    			lstTimeAxisValues = new ArrayList<SpectrogramGraphicPanelAxesValues>();
    			
    			for (float fltX = intInitialSecondsAxis; fltX <= intInitialSecondsAxis + intSeconds + 1; fltX += fltIncrementSecond[0][indexIncrement]) {
    				
    				// Se o valor em milisegundos for múltiplo do divisor, mantém o valor
    				if (fltX % intDivider == 0) {
    					intMajorValue = (int) (fltX * 1000);
    					
    				// Senão encontra o valor menor mais próximo de um múltiplo do divisor
    				} else {
    					intMajorValue = (int) (fltX * 1000) - (int) (fltX * 1000) % intDivider;
    				}
    				
    				intMajorValue = (intInitialHourAxis * 60 * 60 * 1000) + (intInitialMinutesAxis * 60 * 1000) + intMajorValue;
    				lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMajorValue, true));

					if (intMajorValue >= super.getInitialTime() && intMajorValue <= super.getFinalTime()) {
						intNumMajorRegs++;
					}
					
					intNumMinorRegs = (int) (fltIncrementSecond[0][indexIncrement] / fltIncrementSecond[1][indexIncrement]);

					for (int intY = 1; intY <= intNumMinorRegs; intY++) {
						intMinorValue = intMajorValue + (int) (intY * fltIncrementSecond[1][indexIncrement] * 1000);
						lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMinorValue, false));
					}
    			}
    			
    			indexIncrement++;
    			
    			if (intNumMajorRegs > intMinValues || indexIncrement == fltIncrementSecond[0].length) {
    				if (intDivider != 1) {
    					adjustTimeAxisValues();
    					intNumMajorRegs = getNumRegsMajor(lstTimeAxisValues);
    				}
    				
    				if (intNumMajorRegs > intMinValues || indexIncrement == fltIncrementSecond[0].length) {
        				break;
        			}
    			}
    			
    		// Milisegundos
    		} else if (intMilliseconds > 0) {
    			blnMillisecondsAxis = true;

    			// Valores para 'milisegundo' que compreendem os registros principais e os registros secundários (sem string)
    			float[][] fltIncrementMillisecond = null;
    			int intDivider = 1;
    			
    			// Encontra um divisor mais apropriado levando-se em consideração o total de milisegundos da exibição
    			if (intMilliseconds >= 1 && intMilliseconds < 15) {
    				intDivider = 1;
					
					fltIncrementMillisecond = new float[][] { {1.0f},             // Registros principais
															  {1.0f} };           // Registros secundários
					
    			} else if (intMilliseconds >= 15 && intMilliseconds < 50) {
					intDivider = 5;
					
					fltIncrementMillisecond = new float[][] { {5.0f},             // Registros principais
															  {1.0f} };           // Registros secundários
					
					
    			} else if (intMilliseconds >= 50 && intMilliseconds < 125) {
					intDivider = 10;
					
					fltIncrementMillisecond = new float[][] { {10.0f},            // Registros principais
															  {5.0f} };           // Registros secundários
					
    			} else if (intMilliseconds >= 125 && intMilliseconds < 250) {
					intDivider = 25;
					
					fltIncrementMillisecond = new float[][] { {25.0f},            // Registros principais
                            								  {5.0f} };           // Registros secundários
					
				} else if (intMilliseconds >= 250 && intMilliseconds < 500) {
					intDivider = 50;
					
					fltIncrementMillisecond = new float[][] { {50.0f},            // Registros principais
                            								  {10.0f} };          // Registros secundários
					
				} else if (intMilliseconds >= 500 && intMilliseconds < 1000) {
					intDivider = 100;
					
					fltIncrementMillisecond = new float[][] { {100.0f, 50.0f},    // Registros principais
							  								  {25.0f,  25.0f} };  // Registros secundários
				}
    			
    			intNumMajorRegs = 0;
    			lstTimeAxisValues = new ArrayList<SpectrogramGraphicPanelAxesValues>();
    			
    			for (float fltX = intInitialMillisecondsAxis; fltX <= intInitialMillisecondsAxis + intMilliseconds + 100; fltX += fltIncrementMillisecond[0][indexIncrement]) {
    				// Se o valor em milisegundos for múltiplo do divisor, mantém o valor
    				if (fltX % intDivider == 0) {
    					intMajorValue = (int) (fltX);
    					
    				// Senão encontra o valor menor mais próximo de um múltiplo do divisor
    				} else {
    					intMajorValue = (int) (fltX) - (int) fltX % intDivider;
    				}
    				
    				lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMajorValue, true));
					intNumMajorRegs++;
					
					intNumMinorRegs = (int) (fltIncrementMillisecond[0][indexIncrement] / fltIncrementMillisecond[1][indexIncrement]);
					
					for (int intY = 1; intY <= intNumMinorRegs; intY++) {
						intMinorValue = intMajorValue + (int) (intY * fltIncrementMillisecond[1][indexIncrement]);
						lstTimeAxisValues.add(new SpectrogramGraphicPanelAxesValues(intMinorValue, false));
					}
    			}
    			
    			indexIncrement++;
    			
    			if (intNumMajorRegs > intMinValues || indexIncrement == fltIncrementMillisecond[0].length) {
    				break;
    			}
    		}
    	}
	}
	
	/**
	 * Realiza ajustes no visualização dos registros primários e secundários do eixo X (tempo).
	 * Por exemplo, se 44s = primário e 45s = secundário, o sistema vai ajustar 
	 * para os números divisíveis por 5 se tornarem primários e vice-versa.
	 */
	private void adjustTimeAxisValues() {
		int intDivisibleIsSecundary = 0;
		int intDivisibleIsPrimary = 0;
		int intNotDivisibleIsPrimary = 0;
		
		for (int indexTimeAxisValues = 0; indexTimeAxisValues < lstTimeAxisValues.size(); indexTimeAxisValues++) {
			// Divisível por 5 e primário
			if (lstTimeAxisValues.get(indexTimeAxisValues).getHourMinuteSecond() % 5 == 0 && lstTimeAxisValues.get(indexTimeAxisValues).getShowString() == true) {
				intDivisibleIsPrimary++;
				
			// Divisível por 5 e secundário
			} else if (lstTimeAxisValues.get(indexTimeAxisValues).getHourMinuteSecond() % 5 != 0 && lstTimeAxisValues.get(indexTimeAxisValues).getShowString() == false) {
				intDivisibleIsSecundary++;
				
			// Não divisível por 5 e primário
			} else if (lstTimeAxisValues.get(indexTimeAxisValues).getHourMinuteSecond() % 5 != 0 && lstTimeAxisValues.get(indexTimeAxisValues).getShowString() == true) {
				intNotDivisibleIsPrimary++;
			}
		}

		// Realiza o ajuste final
		if (intDivisibleIsPrimary == 0 && intNotDivisibleIsPrimary > 0 && intDivisibleIsSecundary > 0) {
			for (int indexTimeAxisValues = 0; indexTimeAxisValues < lstTimeAxisValues.size(); indexTimeAxisValues++) {
				if (lstTimeAxisValues.get(indexTimeAxisValues).getHourMinuteSecond() % 5 == 0) {
					lstTimeAxisValues.get(indexTimeAxisValues).setShowString(true);
				} else {
					lstTimeAxisValues.get(indexTimeAxisValues).setShowString(false);
				}
			}
		}
	}

	/**
	 * Retorna o total de registros primários existentes na lista
	 * que contém os valores que serão visualizados nos eixos.
	 * 
	 * @param lstAxisValues
	 * 
	 * @return intNumRegsMajor
	 */
	private int getNumRegsMajor(List<SpectrogramGraphicPanelAxesValues> lstAxisValues) {
		int intNumRegsMajor = 0;
		
		for (int indexAxisValues = 0; indexAxisValues < lstAxisValues.size(); indexAxisValues++) {
			if (lstAxisValues.get(indexAxisValues).getShowString()) {
				intNumRegsMajor++;
			}
		}
		
		return intNumRegsMajor;
	}
	
	/**
	 * Desenha o eixo Y com os respectivos valores de unidade de frequência.
	 * 
	 * @param graphic2D
	 */
	private void drawFrequencyAxis(Graphics2D graphic2D) {
		// Linha do eixo Y
		graphic2D.drawLine(AXES_SIZE, 0, AXES_SIZE, super.getPanelHeight());
		
		final int LINE_WIDTH = 5;
		final int LINE_WIDTH_MINOR = 2;
		
		// Insere a descrição do eixo Y (frequência)
		String strLineValue = rsBundle.getString("spectrogram_graphic_panel_y_axis_description");
		Dimension dmsValueSize = new Dimension(graphic2D.getFontMetrics().stringWidth(strLineValue), graphic2D.getFontMetrics().getHeight());
		
		graphic2D.drawLine(AXES_SIZE, super.getPanelHeight(), AXES_SIZE - LINE_WIDTH, super.getPanelHeight());
		graphic2D.drawString(strLineValue, AXES_SIZE - dmsValueSize.width - LINE_WIDTH - 1, super.getPanelHeight() + dmsValueSize.height / 3);
		
		int intLastStringYPosition = super.getPanelHeight() - dmsValueSize.height / 2;
		
		for (int indexFrequencyAxisValues = 0; indexFrequencyAxisValues < lstFrequencyAxisValues.size(); indexFrequencyAxisValues++) {
			strLineValue = "" + lstFrequencyAxisValues.get(indexFrequencyAxisValues).getValue();
			int intLineYPosition = (int) (super.getPanelHeight() - (lstFrequencyAxisValues.get(indexFrequencyAxisValues).getValue() - intInitialFrequency) / dblFrequencyPerPixel);
			
			if (intLineYPosition < super.getPanelHeight()) {
				if (lstFrequencyAxisValues.get(indexFrequencyAxisValues).getShowString()) {
					graphic2D.drawLine(AXES_SIZE, intLineYPosition, AXES_SIZE - LINE_WIDTH, intLineYPosition);
					dmsValueSize = new Dimension(graphic2D.getFontMetrics().stringWidth(strLineValue), graphic2D.getFontMetrics().getHeight());
					
					int intStringYPosition = intLineYPosition - dmsValueSize.height / 2;

					if (intStringYPosition < intLastStringYPosition - (dmsValueSize.height / 2)) {
						graphic2D.drawString(strLineValue, AXES_SIZE - dmsValueSize.width - LINE_WIDTH - 1, intLineYPosition + dmsValueSize.height / 3);
					}
					
					intLastStringYPosition = intStringYPosition;
				} else {
					graphic2D.drawLine(AXES_SIZE, intLineYPosition, AXES_SIZE - LINE_WIDTH_MINOR, intLineYPosition);
				}
			}
		}
	}

	/**
	 * Extrai os valores de unidade de frequência que serão visualizados no eixo Y.
	 */
	private void extractFrequencyAxisValues() {
		lstFrequencyAxisValues = new ArrayList<SpectrogramGraphicPanelAxesValues>();
		
		int intDifference = intFinalFrequency - intInitialFrequency;    // Diferença entre o valor inicial e final que o zoom se encontra
		
		int intDivider = 0;
		
		if (intDifference >= 18000) {
			intDivider = 2000;
		} else if (intDifference >= 10000 && intDifference < 18000) {
			intDivider = 1000;
		} else if (intDifference >= 5000 && intDifference < 10000) {
			intDivider = 500;
		} else if (intDifference >= 2000 && intDifference < 5000) {
			intDivider = 250;
		} else if (intDifference >= 1000 && intDifference < 2000) {
			intDivider = 100;
		} else if (intDifference >= 500 && intDifference < 1000) {
			intDivider = 50;
		} else if (intDifference >= 150 && intDifference < 500) {
			intDivider = 25;
		} else if (intDifference >= 10 && intDifference < 150) {
			intDivider = 10;
		} else {
			intDivider = 1;
		}

		for (int intX = intInitialFrequency; intX < intFinalFrequency; intX += intDivider) {
			// Encontra o valor menor mais próximo de um múltiplo do divisor
			if (intX % intDivider != 0) {
				intX = intX - intX % intDivider;
			}
			
			lstFrequencyAxisValues.add(new SpectrogramGraphicPanelAxesValues(intX, true));
			lstFrequencyAxisValues.add(new SpectrogramGraphicPanelAxesValues(intX + (intDivider / 2), false));
	    }
	}

	/**
	 * Ajusta os valores das frequências. <br>
	 * <br>
	 * Realiza a inversão do valor da frequência final selecionada 
	 * caso seja menor do que a frequência inicial selecionada.
	 */
	private void adjustFrequencySelectionValues() {
        if (intFinalFrequencySelectionBox < intInitialFrequencySelectionBox) {
        	int intInitialFrequencySelectionTemp = intInitialFrequencySelectionBox;
        	int intFinalFrequencySelectionTemp = intFinalFrequencySelectionBox;
        	
        	intInitialFrequencySelectionBox = intFinalFrequencySelectionTemp;
        	intFinalFrequencySelectionBox = intInitialFrequencySelectionTemp;
        }
        
        if (intInitialFrequencySelectionBox < intInitialFrequency) {
        	intInitialFrequencySelectionBox = intInitialFrequency;
        }
        
        if (intFinalFrequencySelectionBox > intFinalFrequency) {
        	intFinalFrequencySelectionBox = intFinalFrequency;
        }
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
			super.setChangeTimeSelectionLine(false);
			
			pointAnchor = event.getPoint();
			
			int intCurrentPosition = event.getX() - AXES_SIZE;
			int intCurrentTime = (int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition));
			
			if (intCurrentPosition < 0) {
				intCurrentTime = super.getInitialTime();
			}
			
			super.setTimeSelectionLine(intCurrentTime);
			super.setInitialTimeSelectionBox(intCurrentTime);
			super.setFinalTimeSelectionBox(intCurrentTime);
			
			intInitialFrequencySelectionBox = 0;
			intFinalFrequencySelectionBox = 0;
			
			super.setDrawSelectionLine(true);
			super.setDrawSelectionBox(false);
			super.updateSpectrogramSelectedAudio(intCurrentTime, intCurrentTime, intCurrentTime, intInitialFrequencySelectionBox, intFinalFrequencySelectionBox, intInitialFrequency, intFinalFrequency, false);
			
			popupMenu.setVisible(false);
			
		} else {
			pointAnchor = null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {

	}

	//*************************************************************************
	// Implementa MouseMotionListener
	@Override
	public void mouseDragged(MouseEvent event) {
		if (SwingUtilities.isLeftMouseButton(event)) {
			// Geralmente quando abre o menu e clica novamente no espectrograma
			if (pointAnchor == null) {
				mousePressed(event);
			}
			
			// A seleção é iniciada dentro do espectrograma no eixo de tempo
			// e a posição no eixo de frequência é menor que a altura
			if (pointAnchor.x >= AXES_SIZE && (pointAnchor.y < super.getPanelHeight() || event.getY() < super.getPanelHeight())) {
				int intX = (int) Math.min(pointAnchor.x, event.getX());        // Posição no eixo X na imagem
				
				int intAnchorPosition = pointAnchor.x - AXES_SIZE;
				int intCurrentPosition = event.getX() - AXES_SIZE;
				int intCurrentTime = (int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition));  // Tempo atual do áudio
				
				// **************************************************************************************
				// Ponto de seleção inicial é menor que o ponto de seleção atual
				if (intAnchorPosition < intCurrentPosition) {
					super.setInitialTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * (intX - AXES_SIZE))));
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
				// Permite seleção apenas a partir do início da imagem
				if (intX < AXES_SIZE) {
					intCurrentTime = super.getInitialTime();
					super.setInitialTimeSelectionBox(super.getInitialTime());
					super.setFinalTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intAnchorPosition)));
				}
				
				// Tempo final não pode ser maior que o tempo total do áudio
				if (intCurrentTime > super.getFinalTime()) {
					intCurrentTime = super.getFinalTime();
					super.setFinalTimeSelectionBox(intCurrentTime);
				}

				// **************************************************************************************
				// Se o tempo inicial for igual ao final, é mostrada a linha de seleção
				if (super.getInitialTimeSelectionBox() == super.getFinalTimeSelectionBox()) {
					super.setTimeSelectionLine(super.getInitialTimeSelectionBox());
					
					intInitialFrequencySelectionBox = 0;
					intFinalFrequencySelectionBox = 0;
					
					super.setDrawSelectionLine(true);
					super.setDrawSelectionBox(false);
					
				} else {
					updateFrequencySelectionInitialValues();
					
					intFinalFrequencySelectionBox = (int) (intFinalFrequency - (dblFrequencyPerPixel * event.getY()));

					super.setDrawSelectionLine(false);
					super.setDrawSelectionBox(true);
				}

				super.updateSpectrogramSelectedAudio(intCurrentTime, super.getInitialTimeSelectionBox(), super.getFinalTimeSelectionBox(), intInitialFrequencySelectionBox, intFinalFrequencySelectionBox, intInitialFrequency, intFinalFrequency, false);
			
			// A seleção é iniciada dentro do eixo de frequência
			// e a posição atual é dentro do espectrograma
			} else if (pointAnchor.x < AXES_SIZE && event.getX() >= AXES_SIZE) {
				int intCurrentPosition = event.getX() - AXES_SIZE;
				int intCurrentTime = (int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition));  // Tempo atual do áudio
				
				if (event.getY() > super.getPanelHeight()) {
					if (intInitialFrequencySelectionBox == intFinalFrequencySelectionBox) {
						super.setTimeSelectionLine(intCurrentTime);
						super.setInitialTimeSelectionBox(intCurrentTime);
						super.setFinalTimeSelectionBox(intCurrentTime);
						super.setDrawSelectionLine(true);
						super.setDrawSelectionBox(false);
					}
					
				} else {
					super.setInitialTimeSelectionBox((int) (super.getInitialTime()));
					super.setFinalTimeSelectionBox((int) (super.getInitialTime() + (super.getTimePerPixel() * intCurrentPosition)));

					// Tempo final não pode ser maior que o tempo total do áudio
					if (intCurrentTime > super.getFinalTime()) {
						intCurrentTime = super.getFinalTime();
						super.setFinalTimeSelectionBox(intCurrentTime);
					}
	
					updateFrequencySelectionInitialValues();
					
					intFinalFrequencySelectionBox = (int) (intFinalFrequency - (dblFrequencyPerPixel * event.getY()));
					
					super.setDrawSelectionLine(false);
					super.setDrawSelectionBox(true);
				}
				
				super.updateSpectrogramSelectedAudio(intCurrentTime, super.getInitialTimeSelectionBox(), super.getFinalTimeSelectionBox(), intInitialFrequencySelectionBox, intFinalFrequencySelectionBox, intInitialFrequency, intFinalFrequency, false);
				
			// A seleção é feita dentro do eixo do tempo
			} else {
				int intTimePosition = event.getX() - AXES_SIZE;
				int intTimeMilliseconds = (int) (super.getInitialTime() + (super.getTimePerPixel() * intTimePosition));
				
				if (intTimeMilliseconds < super.getInitialTime()) {
					intTimeMilliseconds = super.getInitialTime();
				}
				
				super.setTimeSelectionLine(intTimeMilliseconds);
				super.setInitialTimeSelectionBox(intTimeMilliseconds);
				super.setFinalTimeSelectionBox(intTimeMilliseconds);

				intInitialFrequencySelectionBox = 0;
				intFinalFrequencySelectionBox = 0;
				
				super.setDrawSelectionLine(true);
				super.setDrawSelectionBox(false);
				
				super.updateSpectrogramSelectedAudio(intTimeMilliseconds, intTimeMilliseconds, intTimeMilliseconds, intInitialFrequencySelectionBox, intFinalFrequencySelectionBox, intInitialFrequency, intFinalFrequency, false);
			}
			
			mouseMoved(event);
			
			adjustFrequencySelectionValues();
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
	        
			// **************************************************************************************
			// Frequência
	        int intFrequency = (int) (intFinalFrequency - (dblFrequencyPerPixel * event.getY()));
	        
	        if (intFrequency < intInitialFrequency) {
	        	intFrequency = intInitialFrequency;
	        } else if (intFrequency > intFinalFrequency) {
	        	intFrequency = intFinalFrequency;
	        }
	        
	        super.updateSpectrogramMousePosition(intTimeMilliseconds, intFrequency);
	        
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//*************************************************************************
	/**
	 * Atualiza os valores inicial e final de seleção da frequência,
	 * de acordo com o ponto inicial clicado pelo usuário.
	 */
	private void updateFrequencySelectionInitialValues() {
		intInitialFrequencySelectionBox = (int) (intFinalFrequency - (dblFrequencyPerPixel * pointAnchor.getY()));
        
        if (intInitialFrequencySelectionBox < 0) {
        	intInitialFrequencySelectionBox = 0;
        } else if (intInitialFrequencySelectionBox > intFinalFrequency) {
        	intInitialFrequencySelectionBox = intFinalFrequency;
        }
        
        intFinalFrequencySelectionBox = intInitialFrequencySelectionBox;
	}

	/**
     * Cria um menu que será ativado quando clicar 
     * com o botão direito do mouse no espectrograma.
     */
    private void createPopupMenu() {
    	popupMenu = new JPopupMenu();
    	
    	// Adiciona uma nova vocalização
    	menuItemAddNewSoundUnit = new JMenuItem(rsBundle.getString("spectrogram_graphic_panel_add_new_sound_unit"));
    	popupMenu.add(menuItemAddNewSoundUnit);
    	menuItemAddNewSoundUnit.addActionListener(new AddNewSoundUnit());
    	
    	// Adiciona uma nova nota a vocalização anterior
    	menuItemAddNewSoundUnitForNewSpecies = new JMenuItem(rsBundle.getString("spectrogram_graphic_panel_add_new_sound_unit_for_new_species"));
    	popupMenu.add(menuItemAddNewSoundUnitForNewSpecies);
    	menuItemAddNewSoundUnitForNewSpecies.addActionListener(new AddNewSoundUnitForNewSpecies());

    	// Insere um zoom na seleção atual
    	menuItemZoomCurrentSelection = new JMenuItem(rsBundle.getString("spectrogram_graphic_panel_zoom_in_selection"));
    	popupMenu.add(menuItemZoomCurrentSelection);
    	menuItemZoomCurrentSelection.addActionListener(new SetZoomSelection());
    	
    	// Separador do menu
    	popupMenu.addSeparator();
    	
    	// Mostra a lista de vocalizações selecionadas
    	menuItemShowSelectionList = new JMenuItem(rsBundle.getString("spectrogram_graphic_panel_show_selection_list"));
    	popupMenu.add(menuItemShowSelectionList);
    	menuItemShowSelectionList.addActionListener(new ShowBoxSelectionList());

    	this.addMouseListener(new PopupListener(popupMenu));
    }
    
    /**
     * Cria <i>MouseAdapter</i> que será ativado quando clicar 
     * com o botão direito do mouse no espectrograma.
     */
    private class PopupListener extends MouseAdapter {
    	JPopupMenu popupMenu;

    	/**
    	 * Cria <i>MouseAdapter</i> que será ativado quando clicar 
    	 * com o botão direito do mouse no espectrograma.
    	 * 
    	 * @param popupMenu
    	 */
        private PopupListener(JPopupMenu popupMenu) {
        	this.popupMenu = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent event) {
        	showPopup(event);
        }

        @Override
        public void mouseReleased(MouseEvent event) {
        	showPopup(event);
        }

        private void showPopup(MouseEvent event) {
        	if (SwingUtilities.isRightMouseButton(event)) {
        		
        		// Habilita/desabilita os componentes do menu de acordo com as necessidades
        		if (getDrawSelectionBox()) {
        			menuItemAddNewSoundUnit.setEnabled(true);
            		menuItemZoomCurrentSelection.setEnabled(true);
            	} else {
            		menuItemAddNewSoundUnit.setEnabled(false);
            		menuItemZoomCurrentSelection.setEnabled(false);
            	}
        		
        		if (lstSegments.size() > 0) {
        			menuItemShowSelectionList.setEnabled(true);
        		} else {
        			menuItemAddNewSoundUnitForNewSpecies.setEnabled(false);
        		}
        		
        		if (lstSegments.size() > 0 && getDrawSelectionBox()) {
        			menuItemAddNewSoundUnitForNewSpecies.setEnabled(true);
            	} else {
            		menuItemAddNewSoundUnitForNewSpecies.setEnabled(false);
            	}
        		
        		if (getDrawSelectionBox() || lstSegments.size() != 0) {
        			popupMenu.show(event.getComponent(), event.getX(), event.getY());
        		}
        	}
        }
    }
    
    /**
     * Adiciona uma nova unidade de som à lista de seleção.
     */
    private class AddNewSoundUnit implements ActionListener {  
    	  
    	@Override
        public void actionPerformed(ActionEvent event) {
    		String strSoundUnit = "A";
    		
    		if (lstSegments.size() != 0) {
	    		strSoundUnit = lstSegments.get(lstSegments.size() - 1).getSoundUnit();
	    		
				char chrSoundUnit[] = strSoundUnit.toCharArray();
				
				// Retira os números da unidade de som
				strSoundUnit = "";
				String strLastSoundUnit = "";
				for (int indexChr = 0; indexChr < chrSoundUnit.length; indexChr++) {
					if (Character.isDigit(chrSoundUnit[indexChr])) {
						strLastSoundUnit = strLastSoundUnit + chrSoundUnit[indexChr];
					}
					
					if (!Character.isDigit(chrSoundUnit[indexChr])) {
						strSoundUnit = strSoundUnit + chrSoundUnit[indexChr];
					}
				}
	    		
				int intLastSoundUnit = Integer.parseInt(strLastSoundUnit);
				intLastSoundUnit = intLastSoundUnit + 1;
	    		
				strSoundUnit = strSoundUnit + intLastSoundUnit;
				
    		} else {
    			strSoundUnit = strSoundUnit + "1";
    		}
			
    		AudioTemporary.createAudioTemporarySegment(objSpectrogram.getAudioWav(), strSoundUnit, getInitialTimeSelectionBox(), getFinalTimeSelectionBox(), intInitialFrequencySelectionBox, intFinalFrequencySelectionBox);
    		
    		setDrawSelectionBox(false);
    		
    		repaint();
        }
    }
    
    /**
     * Adiciona uma nova unidade de som à uma nova espécie.
     */
    private class AddNewSoundUnitForNewSpecies implements ActionListener {  
    	  
    	@Override
        public void actionPerformed(ActionEvent event) {
    		String strSoundUnit = "A";
    		
    		if (lstSegments.size() != 0) {
    			strSoundUnit = lstSegments.get(lstSegments.size() - 1).getSoundUnit();
    			
    			char chrSoundUnit[] = strSoundUnit.toCharArray();
    			
    			strSoundUnit = "";
    			
    			for (int indexChr = 0; indexChr < chrSoundUnit.length; indexChr++) {
    				if (!Character.isDigit(chrSoundUnit[indexChr])) {
    					strSoundUnit = strSoundUnit + chrSoundUnit[indexChr];
    				}
    			}
    			
    			chrSoundUnit = strSoundUnit.toCharArray();
    			
    			if (chrSoundUnit.length == 1) {
    				if (chrSoundUnit[0] == 'Z') {
    					strSoundUnit = "AA";
    				} else {
    					char chrNewIdentification = (char) (chrSoundUnit[0] + 1);
    					strSoundUnit = "" + chrNewIdentification;
    				}
    				
    			} else if (chrSoundUnit.length == 2) {
    				if (chrSoundUnit[1] == 'Z') {
    					char chrNewIdentification = (char) (chrSoundUnit[0] + 1);
    					strSoundUnit = "" + chrNewIdentification + "A"; 
    				} else {
    					char chrNewIdentification = (char) (chrSoundUnit[1] + 1);
    					strSoundUnit = "" + chrSoundUnit[0] + chrNewIdentification; 
    				}
    			}
    		}
    		
    		strSoundUnit = strSoundUnit + "1";
    		
    		AudioTemporary.createAudioTemporarySegment(objSpectrogram.getAudioWav(), strSoundUnit, getInitialTimeSelectionBox(), getFinalTimeSelectionBox(), intInitialFrequencySelectionBox, intFinalFrequencySelectionBox);
    		
    		setDrawSelectionBox(false);
		
    		repaint();
    		
    		createPopupMenu();
        }
    }
    
    /**
     * Executa o zoom na caixa de seleção.
     */
    private class SetZoomSelection implements ActionListener { 
    	@Override
        public void actionPerformed(ActionEvent event) {
    		objSpectrogram.setPanelWidth(getPanelWidth());
    		objSpectrogram.setPanelHeight(getPanelHeight());
    		
    		objSpectrogram.setZoomSelection(getInitialTimeSelectionBox(), getFinalTimeSelectionBox(), intInitialFrequencySelectionBox, intFinalFrequencySelectionBox);
    		
    		repaint();
    		
    		updateSpectrogramSelectedAudio(getInitialTimeSelectionBox(), getInitialTimeSelectionBox(), getFinalTimeSelectionBox(), intInitialFrequencySelectionBox, intFinalFrequencySelectionBox, intInitialFrequency, intFinalFrequency, true);
    	}
    }
    
    /**
     * Mostra todas as seleções realizadas no espectrograma.
     * Seleções normais e temporárias.
     */
    private class ShowBoxSelectionList implements ActionListener {  
    	@Override
        public void actionPerformed(ActionEvent event) {
    		ScreenSpectrogramSelectionList objScreenSpectrogramSelectionList = new ScreenSpectrogramSelectionList(SpectrogramGraphicPanel.this);
    		objScreenSpectrogramSelectionList.showScreen();

    		repaint();
        }
    }
}

/**
 * Define os valores que deverão ser mostradas nos eixos
 * X (tempo) e Y (frequência) do painel gráfico do espectrograma.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 16/Dez/2014
 */
class SpectrogramGraphicPanelAxesValues {
	private int intValue;               // Valor a ser atribuído ao eixo.
	private boolean blnShowString;      // Define se irá ser mostrada uma string com o valor
	
	/**
	 * Retorna o valor a ser atribuído ao eixo.
	 * 
	 * @return intValue
	 */
	protected int getValue() {
		return intValue;
	}

	/**
	 * Retorna a condição que define se irá ser mostrada uma string com o valor no eixo.
	 * 
	 * @return blnShowString
	 */
	protected boolean getShowString() {
		return blnShowString;
	}
	
	/**
	 * Altera a condição que define se irá ser mostrada uma string com o valor no eixo.
	 * 
	 * @param blnShowString
	 */
	protected void setShowString(boolean blnShowString) {
		this.blnShowString = blnShowString;
	}
	
	/**
	 * Retorna o valor a ser atribuído ao eixo. (em horas/minutos/segundos)
	 * 
	 * @return
	 */
	protected double getHourMinuteSecond() {
		return (double) intValue / 1000d;
	}
	
	/**
	 * Define os valores que deverão ser mostradas nos eixos
	 * X (tempo) e Y (frequência) do painel gráfico do espectrograma.
	 * 
	 * @param intValue - Valor a ser atribuído ao eixo (em milisegundos/em frequência)
	 * @param blnShowString - Define se irá ser mostrada uma string com o valor
	 */
	protected SpectrogramGraphicPanelAxesValues(int intValue, boolean blnShowString) {
		this.intValue = intValue;
		this.blnShowString = blnShowString;
	}
}