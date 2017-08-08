package br.unicamp.fnjv.wasis.swing;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.Timer;

/**
 * Cria uma extensão para <i>JButton</i> responsável
 * pela exibição de um botão com suas configurações principais.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 30/Jun/2016
 */
public class WasisButton extends JButton implements MouseListener {
	private static final long serialVersionUID = -6149807772350017470L;
	
	private int intButtonType;
	
	private Image imageIcon;
	
	private Color colorCurrentGradientTop;
    private Color colorCurrentGradientBottom;
    
    private long lgnStartFade;
    
    private boolean blnFadeEnter;
    private boolean blnFadeExit;
    
    private final int FADE_ENTER = 1;       // Tipo de Fade - Entrada no botão
    private final int FADE_EXIT = 2;        // Tipo de Fade - Saída do botão
    
	private final Dimension BORDERS = new Dimension(8, 8);  // Dimensões das bordas
	
	private final Color COLOR_BORDER = new Color(100, 100, 100);
	
    private final Color COLOR_GRADIENT_TOP = new Color(0, 0, 0, 0);
    private final Color COLOR_GRADIENT_BOTTOM = new Color(0, 0, 0, 0);
    
    private final Color COLOR_GRADIENT_TOP_FOCUSED = new Color(170, 210, 210, 255);
    private final Color COLOR_GRADIENT_BOTTOM_FOCUSED = new Color(140, 210, 210, 255);
    
	private final int BUTTON_TOOLBAR_WIDTH = 50;
	private final int BUTTON_TOOLBAR_HEIGHT = 50;
	
	private final int BUTTON_PLAYER_WIDTH = 50;
	private final int BUTTON_PLAYER_HEIGHT = 50;
	
	private final int BUTTON_ZOOM_WIDTH = 26;
	private final int BUTTON_ZOOM_HEIGHT = 26;
	
	public static final int BUTTON_TYPE_DEFAULT = 0;
	public static final int BUTTON_TYPE_TOOLBAR = 1;
	public static final int BUTTON_TYPE_PLAYER = 2;
	public static final int BUTTON_TYPE_ZOOM = 3;
	
	/**
	 * Cria uma extensão para <i>JButton</i> responsável
	 * pela exibição de um botão com suas configurações principais.
	 */
	public WasisButton() {
		this(BUTTON_TYPE_DEFAULT, null, null);
	}
	
	/**
	 * Cria uma extensão para <i>JButton</i> responsável
	 * pela exibição de um botão na barra de ferramentas 
	 * com suas configurações principais.
	 * 
	 * @param strToolTipText - Tool tip text do botão
	 * @param imgIcon        - Ícone do botão
	 */
	public WasisButton(final int intButtonType, final String strToolTipText, final Icon imgIcon) {
		this.intButtonType = intButtonType;
		this.imageIcon = ((ImageIcon) imgIcon).getImage();
		
		setToolTipText(strToolTipText);
		setFocusPainted(false);
		setFocusable(false);
		setOpaque(false);
		setContentAreaFilled(false);
		
		if (intButtonType == BUTTON_TYPE_TOOLBAR) {
			setMinimumSize(new Dimension(BUTTON_TOOLBAR_WIDTH, BUTTON_TOOLBAR_HEIGHT));
			setMaximumSize(new Dimension(BUTTON_TOOLBAR_WIDTH, BUTTON_TOOLBAR_HEIGHT));
			
		} else if (intButtonType == BUTTON_TYPE_PLAYER) {
			setMinimumSize(new Dimension(BUTTON_PLAYER_WIDTH, BUTTON_PLAYER_HEIGHT));
			setMaximumSize(new Dimension(BUTTON_PLAYER_WIDTH, BUTTON_PLAYER_HEIGHT));
			
		} else if (intButtonType == BUTTON_TYPE_ZOOM) {
			setMinimumSize(new Dimension(BUTTON_ZOOM_WIDTH, BUTTON_ZOOM_HEIGHT));
			setMaximumSize(new Dimension(BUTTON_ZOOM_WIDTH, BUTTON_ZOOM_HEIGHT));
		}
		
		this.colorCurrentGradientTop = COLOR_GRADIENT_TOP;
        this.colorCurrentGradientBottom = COLOR_GRADIENT_BOTTOM;
		
		this.addMouseListener(this);
	}
	
	// *************************************************************************
	@Override
    protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        
        Graphics2D graphics = (Graphics2D) g;
        
        // Antialiasing das bordas
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Conteúdo do Botão
        GradientPaint gradientPaint = new GradientPaint(0, 0, colorCurrentGradientTop, 0, getHeight(), colorCurrentGradientBottom);
        graphics.setPaint(gradientPaint);
        graphics.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BORDERS.width, BORDERS.height);
        
        // Bordas
        graphics.setColor(COLOR_BORDER);
        graphics.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, BORDERS.width, BORDERS.height);
        
        if (intButtonType == BUTTON_TYPE_TOOLBAR) {
        	graphics.drawImage(imageIcon, 5, 5, 40, 40, null);
        } else if (intButtonType == BUTTON_TYPE_PLAYER) {
        	graphics.drawImage(imageIcon, 0, 0, 50, 50, null);
		} else if (intButtonType == BUTTON_TYPE_ZOOM) {
			graphics.drawImage(imageIcon, 3, 3, 20, 20, null);
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
        int intFadeTime = 200;
        
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
		blnFadeExit = true;
		blnFadeEnter = false;
			
		fadePanel(FADE_EXIT, COLOR_GRADIENT_TOP, COLOR_GRADIENT_BOTTOM, colorCurrentGradientTop, colorCurrentGradientBottom);
	}

	@Override
	public void mousePressed(MouseEvent event) {

	}

	@Override
	public void mouseReleased(MouseEvent event) {

	}
}