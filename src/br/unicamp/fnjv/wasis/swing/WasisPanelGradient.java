package br.unicamp.fnjv.wasis.swing;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;

import javax.swing.JPanel;

/**
 * Cria um painel com plano de fundo em degradê.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 30/Jun/2016
 */
public class WasisPanelGradient extends JPanel {
	private static final long serialVersionUID = 1884483808668353269L;
	
	private Color colorGradientTop;
	private Color colorGradientBottom;

	/**
	 * Cria um painel com plano de fundo em degradê.
	 * 
	 * @param colorGradientTop
	 * @param colorGradientBottom
	 */
	public WasisPanelGradient(Color colorGradientTop, Color colorGradientBottom) {
		super();
		
		this.colorGradientTop = colorGradientTop;
		this.colorGradientBottom = colorGradientBottom;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
        
        Graphics2D graphics = (Graphics2D) g;

		GradientPaint gradientPaint = new GradientPaint(0, 0, colorGradientTop, 0, getHeight() / 2, colorGradientBottom);
		graphics.setPaint(gradientPaint);
		graphics.fillRect(0, 0, getWidth(), getHeight() / 2);
		
		GradientPaint gradientPaint2 = new GradientPaint(0, getHeight() / 2, colorGradientBottom, 0, getHeight(), colorGradientTop);
		graphics.setPaint(gradientPaint2);
		graphics.fillRect(0, getHeight() / 2, getWidth(), getHeight());
	}
}
