package br.unicamp.fnjv.wasis.swing;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Cria painéis com bordas arredondas e colorações alternativas. 
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 30/Jun/2016
 */
public class WasisPanelRounded extends JPanel implements MouseListener {
	private static final long serialVersionUID = 8529995216206862092L;

	private String strPanelName;
	
	private Color colorCurrentGradientTop;
    private Color colorCurrentGradientBottom;
    
    private long lgnStartFade;
    
    private boolean blnFadeEnter;
    private boolean blnFadeExit;
    
    private final int FADE_ENTER = 1;       // Tipo de Fade - Entrada no painel
    private final int FADE_EXIT = 2;        // Tipo de Fade - Saída do painel
	
    private final int STROKE_SIZE = 1;      // Tamanho do stroke
    private final int SHADOW_GAP = 2;       // Distância entre a borda da sombra e a borda do painel
    private final int SHADOW_OFFSET = 2;    // Offset da sombra
    
    private final Dimension BORDERS = new Dimension(8, 8);              // Dimensões das bordas
    
    private final Color COLOR_BORDER = new Color(100, 100, 100);
    private final Color COLOR_SHADOW = new Color(230, 230, 230);        // Cor da sombra
    
    private final Color COLOR_TOP_PANEL_TOP = new Color(110, 110, 110);
    private final Color COLOR_TOP_PANEL_BOTTOM = new Color(180, 180, 180);
    private final Color COLOR_TOP_PANEL_NAME = new Color(255, 255, 255);
    
    private final Color COLOR_GRADIENT_TOP = new Color(230, 230, 230, 0);
    private final Color COLOR_GRADIENT_BOTTOM = new Color(245, 245, 245, 0);
    
    private final Color COLOR_GRADIENT_TOP_FOCUSED = new Color(230, 230, 230, 255);
    private final Color COLOR_GRADIENT_BOTTOM_FOCUSED = new Color(245, 245, 245, 255);

    /**
     * Cria painéis com bordas arredondas e colorações alternativas.
     * 
     * @param strPanelName
     */
    public WasisPanelRounded(String strPanelName) {
        super();
        
        this.strPanelName = strPanelName;
        
        this.colorCurrentGradientTop = COLOR_GRADIENT_TOP;
        this.colorCurrentGradientBottom = COLOR_GRADIENT_BOTTOM;

        if (strPanelName == null || strPanelName.length() == 0) {
        	this.setBorder(BorderFactory.createEmptyBorder(0, -4, 2, -2));
        } else {
        	this.setBorder(BorderFactory.createEmptyBorder(15, -4, 0, -2));
        }
        
        this.setOpaque(false);
        this.setForeground(COLOR_BORDER);
        this.setFont(new Font("Arial", Font.BOLD, 12));
        
        this.addMouseListener(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D graphics = (Graphics2D) g;

        // Antialiasing das bordas
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Sombra das bordas
        graphics.setColor(COLOR_SHADOW);
        
        // Sombra lateral direita
        graphics.fillRoundRect(getWidth() - SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_OFFSET, getHeight() - STROKE_SIZE - SHADOW_OFFSET, BORDERS.width, BORDERS.height);

        // Sombra inferior
        graphics.fillRoundRect(SHADOW_OFFSET, 
        	                   getHeight() - SHADOW_OFFSET, 
        		               getWidth() - STROKE_SIZE - SHADOW_OFFSET, 
        		               SHADOW_OFFSET, BORDERS.width, BORDERS.height);
        
        // Topo do Painel
        int intPainelTop = (int) BORDERS.getHeight();
        GradientPaint gradientPaintTop = new GradientPaint(0, 0, COLOR_TOP_PANEL_TOP, 0, 15, COLOR_TOP_PANEL_BOTTOM);
        
        if (strPanelName == null || strPanelName.length() == 0) {
        	intPainelTop = 0;
        } else {
        	graphics.setPaint(gradientPaintTop);
        	graphics.fillRoundRect(0, 0, getWidth() - SHADOW_GAP, 17, BORDERS.width, BORDERS.height);
        }

        // Conteúdo do Painel
        GradientPaint gradientPaint = new GradientPaint(0, 0, colorCurrentGradientTop, 0, getHeight(), colorCurrentGradientBottom);
        graphics.setPaint(gradientPaint);
        graphics.fillRoundRect(0, intPainelTop, getWidth() - SHADOW_GAP, getHeight() - SHADOW_GAP - intPainelTop, BORDERS.width, BORDERS.height);
        
        graphics.setColor(getForeground());
        graphics.setStroke(new BasicStroke(STROKE_SIZE));
        graphics.drawRoundRect(0, 0, getWidth() - SHADOW_GAP, getHeight() - SHADOW_GAP, BORDERS.width, BORDERS.height);
        
        // Acerto do topo do painel
        if (intPainelTop > 0) {
	        graphics.setPaint(gradientPaintTop);
	        graphics.fillRect(1, intPainelTop, getWidth() - SHADOW_GAP - 1, BORDERS.height);
	        
	        // Insere o nome do painel
	        graphics.setColor(COLOR_TOP_PANEL_NAME);
	        
	        int intNameLength = (int) graphics.getFontMetrics().getStringBounds(strPanelName, graphics).getWidth() + 1;
	        int intStart = getWidth() / 2 - intNameLength / 2;
	        
	        graphics.drawString(strPanelName, intStart, 13);
	        
	        // Insere uma linha de divisão entre o topo e o conteúdo do painel
	        graphics.setColor(COLOR_BORDER);
	        graphics.drawLine(1, intPainelTop * 2, getWidth() - SHADOW_GAP - 1, intPainelTop * 2);
        }
    }

    /**
     * Executa um fade para alteração de cores quando o painel recebe/perde o foco pelo mouse.
     * 
     * @param intFadeType
     * @param colorNewGradientTop
     * @param colorNewGradientBottom
     * @param colorOldGradientTop
     * @param colorOldGradientBottom
     */
    public void fadePanel(int intFadeType, Color colorNewGradientTop, Color colorNewGradientBottom, Color colorOldGradientTop, Color colorOldGradientBottom) {
    	// Tempo do fade
        int intFadeTime = 500;

        int intDifferenceRedTop = Math.abs(colorNewGradientTop.getRed() - colorOldGradientTop.getRed());
        double dblTimeVariationRedTop = (double) intDifferenceRedTop / intFadeTime;
        
        int intDifferenceRedBottom = Math.abs(colorNewGradientBottom.getRed() - colorOldGradientBottom.getRed());
        double dblTimeVariationRedBottom = (double) intDifferenceRedBottom / intFadeTime;
        
        int intDifferenceGreenTop = Math.abs(colorNewGradientTop.getGreen() - colorOldGradientTop.getGreen());
        double dblTimeVariationGreenTop = (double) intDifferenceGreenTop / intFadeTime;
        
        int intDifferenceGreenBottom = Math.abs(colorNewGradientBottom.getGreen() - colorOldGradientBottom.getGreen());
        double dblTimeVariationGreenBottom = (double) intDifferenceGreenBottom / intFadeTime;
        
        int intDifferenceBlueTop = Math.abs(colorNewGradientTop.getBlue() - colorOldGradientTop.getBlue());
        double dblTimeVariationBlueTop = (double) intDifferenceBlueTop / intFadeTime;
        
        int intDifferenceBlueBottom = Math.abs(colorNewGradientBottom.getBlue() - colorOldGradientBottom.getBlue());
        double dblTimeVariationBlueBottom = (double) intDifferenceBlueBottom / intFadeTime;
        
        int intDifferenceAlphaTop = Math.abs(colorNewGradientTop.getAlpha() - colorOldGradientTop.getAlpha());
        double dblTimeVariationAlphaTop = (double) intDifferenceAlphaTop / intFadeTime;
        
        int intDifferenceAlphaBottom = Math.abs(colorNewGradientBottom.getAlpha() - colorOldGradientBottom.getAlpha());
        double dblTimeVariationAlphaBottom = (double) intDifferenceAlphaBottom / intFadeTime;

        this.lgnStartFade = System.currentTimeMillis();
        
        final Timer TIMER_FADE = new Timer(0, null);

        TIMER_FADE.addActionListener(new ActionListener() {
        	@Override
            public void actionPerformed(ActionEvent e) {
                long lgnTimeElapsed = System.currentTimeMillis() - lgnStartFade;

                if (lgnTimeElapsed >= intFadeTime || (intFadeType == FADE_ENTER && blnFadeExit) || (intFadeType == FADE_EXIT && blnFadeEnter)) {
                    colorCurrentGradientTop = colorNewGradientTop;
                    colorCurrentGradientBottom = colorNewGradientBottom;
                    
                    repaint();

                    TIMER_FADE.stop();

                } else {
                	// Gradiente da parte de cima
                	int intOldRed = colorOldGradientTop.getRed();
                    int intOldGreen = colorOldGradientTop.getGreen();
                    int intOldBlue = colorOldGradientTop.getBlue();
                    int intOldAlpha = colorOldGradientTop.getAlpha();
                    int intNewRed = (int) (intOldRed + (lgnTimeElapsed * dblTimeVariationRedTop));
                    int intNewGreen = (int) (intOldGreen + (lgnTimeElapsed * dblTimeVariationGreenTop));
                    int intNewBlue = (int) (intOldBlue + (lgnTimeElapsed * dblTimeVariationBlueTop));
                    int intNewAlpha = (int) (intOldAlpha + (lgnTimeElapsed * dblTimeVariationAlphaTop));
                    
                    if (colorNewGradientTop.getRed() - colorOldGradientTop.getRed() < 0) {
                    	intNewRed = (int) (intOldRed - (lgnTimeElapsed * dblTimeVariationRedTop));
                    }
                    
                    if (colorNewGradientTop.getGreen() - colorOldGradientTop.getGreen() < 0) {
                    	intNewGreen = (int) (intOldGreen - (lgnTimeElapsed * dblTimeVariationGreenTop));
                    }
                    
                    if (colorNewGradientTop.getBlue() - colorOldGradientTop.getBlue() < 0) {
                    	intNewBlue = (int) (intOldBlue - (lgnTimeElapsed * dblTimeVariationBlueTop));
                    }
                    
                    if (colorNewGradientTop.getAlpha() - colorOldGradientTop.getAlpha() < 0) {
                    	intNewAlpha = (int) (intOldAlpha - (lgnTimeElapsed * dblTimeVariationAlphaTop));
                    }

                	colorCurrentGradientTop = new Color(intNewRed, intNewGreen, intNewBlue, intNewAlpha);
                	
                	// Gradiente da parte de baixo
                	intOldRed = colorOldGradientBottom.getRed();
                    intOldGreen = colorOldGradientBottom.getGreen();
                    intOldBlue = colorOldGradientBottom.getBlue();
                    intNewRed = (int) (intOldRed + (lgnTimeElapsed * dblTimeVariationRedBottom));
                    intNewGreen = (int) (intOldGreen + (lgnTimeElapsed * dblTimeVariationGreenBottom));
                    intNewBlue = (int) (intOldBlue + (lgnTimeElapsed * dblTimeVariationBlueBottom));
                    intNewAlpha = (int) (intOldAlpha + (lgnTimeElapsed * dblTimeVariationAlphaBottom));
                    
                    if (colorNewGradientBottom.getRed() - colorOldGradientBottom.getRed() < 0) {
                    	intNewRed = (int) (intOldRed - (lgnTimeElapsed * dblTimeVariationRedBottom));
                    }
                    
                    if (colorNewGradientBottom.getGreen() - colorOldGradientBottom.getGreen() < 0) {
                    	intNewGreen = (int) (intOldGreen - (lgnTimeElapsed * dblTimeVariationGreenBottom));
                    }
                    
                    if (colorNewGradientBottom.getBlue() - colorOldGradientBottom.getBlue() < 0) {
                    	intNewBlue = (int) (intOldBlue - (lgnTimeElapsed * dblTimeVariationBlueBottom));
                    }
                    
                    if (colorNewGradientBottom.getAlpha() - colorOldGradientBottom.getAlpha() < 0) {
                    	intNewAlpha = (int) (intOldAlpha - (lgnTimeElapsed * dblTimeVariationAlphaBottom));
                    }
                	
                    colorCurrentGradientBottom = new Color(intNewRed, intNewGreen, intNewBlue, intNewAlpha);
                	
                    repaint();
                }
            }
        });
        
        TIMER_FADE.start();
    }
    
    /**
	 * Verifica se algum componente dentro do painel recebeu o foco.
	 * 
	 * @param point
	 * 
	 * @return TRUE - Componente dentro do painel<br>
	 *         FALSE - Componente não está dentro do painel
	 */
	private boolean checkComponentInPanel(Point2D point) {
	    return (this.contains((Point) point));
	}

	// *************************************************************************
	// Implementa MouseListener
	@Override
	public void mouseClicked(MouseEvent event) {

	}

	@Override
	public void mouseEntered(MouseEvent event) {
		blnFadeEnter = true;
		blnFadeExit = false;
		
		fadePanel(FADE_ENTER, COLOR_GRADIENT_TOP_FOCUSED, COLOR_GRADIENT_BOTTOM_FOCUSED, colorCurrentGradientTop, colorCurrentGradientBottom);
	}

	@Override
	public void mouseExited(MouseEvent event) {
		if (!checkComponentInPanel(event.getPoint())) {
			blnFadeExit = true;
			blnFadeEnter = false;
			
			fadePanel(FADE_EXIT, COLOR_GRADIENT_TOP, COLOR_GRADIENT_BOTTOM, colorCurrentGradientTop, colorCurrentGradientBottom);
		}
	}

	@Override
	public void mousePressed(MouseEvent event) {

	}

	@Override
	public void mouseReleased(MouseEvent event) {

	}
}