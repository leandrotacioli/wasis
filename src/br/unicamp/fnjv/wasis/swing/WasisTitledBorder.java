package br.unicamp.fnjv.wasis.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.leandrotacioli.libs.LTParameters;

/**
 * Cria uma extensão para <i>TitledBorder</i>
 * que efetua a correção das margens da borda
 * e altera a fonte do título da borda.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 19/Abr/2015
 */
public class WasisTitledBorder extends TitledBorder {
	private static final long serialVersionUID = -1767149886836083431L;

	public WasisTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont, Color titleColor) {
		super(border, title, titleJustification, titlePosition, titleFont, titleColor);
	}

	public WasisTitledBorder(Border border, String title, int titleJustification, int titlePosition, Font titleFont) {
		super(border, title, titleJustification, titlePosition, titleFont);
	}

	public WasisTitledBorder(Border border, String title, int titleJustification, int titlePosition) {
		super(border, title, titleJustification, titlePosition);
		
		setTitleFont(LTParameters.getInstance().getFontComponentLabel());
	}

	public WasisTitledBorder(Border border, String title) {
		super(border, title);
		
		setTitleFont(LTParameters.getInstance().getFontComponentLabel());
	}

	public WasisTitledBorder(Border border) {
		super(border);
	}

	public WasisTitledBorder(String title) {
		super(title);
		
		setTitleFont(LTParameters.getInstance().getFontComponentLabel());
	}
	
	@Override
	public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
		super.paintBorder(c, g, x - 2, y, width + 4, height);
	}

	@Override
	public void setTitle(String strTitle) {
		super.setTitle(strTitle);
		
		setTitleFont(LTParameters.getInstance().getFontComponentLabel());
	}
	
	@Override
	public void setTitleFont(Font titleFont) {
		super.setTitleFont(titleFont);
	}
}