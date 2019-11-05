package br.unicamp.fnjv.wasis.swing;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

import br.unicamp.fnjv.wasis.graphics.spectrogram.SpectrogramColorDisplay;

public class WasisLabel extends JLabel {
	private static final long serialVersionUID = 7818607043563106444L;
	
	private boolean blnDashedBorder;
	
	public void setDashedBorder(boolean blnDashedBorder) {
		this.blnDashedBorder = blnDashedBorder;
	}
	
	public WasisLabel(String strLabel) {
		setText(strLabel);
		setFont(new Font("Tahoma", Font.PLAIN, 11));
		setVerticalAlignment(SwingConstants.CENTER);
		setHorizontalAlignment(SwingConstants.CENTER);
	}

	@Override
    protected void paintComponent(Graphics g) {
		Graphics2D graphic2D = (Graphics2D) g;
		
		// Linha pontilhada na borda
		if (blnDashedBorder) {
			graphic2D.setComposite(AlphaComposite.SrcOver.derive(0.75f));
			graphic2D.setColor(SpectrogramColorDisplay.getColorAudioSegmentBox());
			graphic2D.setStroke(new BasicStroke(3.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 0.0f, new float[] {6.0f}, 0.0f));
			graphic2D.draw(new Rectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1));
			graphic2D.setComposite(AlphaComposite.SrcOver.derive(1.00f));
		}
		
		super.paintComponent(graphic2D);
	}
}