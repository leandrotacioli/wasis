package br.unicamp.fnjv.wasis.swing;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.leandrotacioli.libs.swing.comboboxfield.LTComboBoxField;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

/**
 * Painéis padrão do sistema.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 20/Fev/2015
 */
public class WasisPanel extends JPanel {
	private static final long serialVersionUID = -4427069945337238300L;
	
	private String strTitle;
	
	private final int STROKE_SIZE = 1;      // Tamanho do stroke
    private final int SHADOW_GAP = 2;       // Distância entre a borda da sombra e a borda do painel
    private final int SHADOW_OFFSET = 2;    // Offset da sombra
    
    private final Color COLOR_BORDER = new Color(100, 100, 100);
    private final Color COLOR_SHADOW = new Color(230, 230, 230);        // Cor da sombra
    
    private final Color COLOR_TOP_PANEL_TOP = new Color(110, 110, 110, 0);
    private final Color COLOR_TOP_PANEL_BOTTOM = new Color(180, 180, 180, 0);
	
    public int getSizeBorders() {
    	return STROKE_SIZE + SHADOW_OFFSET;
    }
    
	/**
	 * Painéis padrão do sistema.
	 */
	public WasisPanel() {
		this.setOpaque(false);
        this.setForeground(COLOR_BORDER);
	}
	
	/**
	 * Painéis padrão do sistema.
	 * 
	 * @param strTitle - Título que irá constar na borda do painel.
	 */
	public WasisPanel(String strTitle) {
		this.strTitle = strTitle;
		
		Border border = BorderFactory.createLineBorder(COLOR_BORDER);
		
		WasisTitledBorder titledBorder = new WasisTitledBorder(border, strTitle);
		
        this.setBorder(titledBorder);
        this.setBackground(new Color(0, 0, 0, 0));
	}
	
	@Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Graphics2D graphics = (Graphics2D) g;
        
        if (strTitle == null || strTitle.length() == 0) {
	        // Antialiasing das bordas
	        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
	        
	        // Sombra das bordas
	        graphics.setColor(COLOR_SHADOW);
	        
	        // Sombra lateral direita
	        graphics.fillRect(getWidth() - SHADOW_OFFSET, SHADOW_OFFSET, SHADOW_OFFSET, getHeight() - STROKE_SIZE - SHADOW_OFFSET);
	
	        // Sombra inferior
	        graphics.fillRect(SHADOW_OFFSET, getHeight() - SHADOW_OFFSET, getWidth() - STROKE_SIZE - SHADOW_OFFSET, SHADOW_OFFSET);
	
	        // Conteúdo do Painel
	        
	        GradientPaint gradientPaint = new GradientPaint(0, 0, COLOR_TOP_PANEL_TOP, 0, getHeight(), COLOR_TOP_PANEL_BOTTOM);
	        graphics.setPaint(gradientPaint);
	        graphics.fillRect(0, 0, getWidth() - SHADOW_GAP, getHeight() - SHADOW_GAP);
	        
	        graphics.setColor(getForeground());
	        graphics.setStroke(new BasicStroke(STROKE_SIZE));
	        graphics.drawRect(0, 0, getWidth() - SHADOW_GAP, getHeight() - SHADOW_GAP);
        }
    }
	
	/**
	 * Habilita/desabilita os componentes do painel.
	 * 
	 * @param blnEnabled
	 * <br>
	 * <i>True</i> - Habilita
	 * <br>
	 * <i>False</i> - Desabilita
	 */
	public void setComponentEnabled(boolean blnEnabled) {
		setComponentEnabled(WasisPanel.this, blnEnabled);
	}
	
	/**
	 * Habilita/desabilita os componentes do painel.
	 * Pode ser que haja outros painéis dentro do principal,
	 * então utilizamos recursão para varrer os componentes
	 * de todos os painéis.
	 * 
	 * @param container  - Painel
	 * @param blnEnabled
	 * <br>
	 * <i>True</i> - Habilita
	 * <br>
	 * <i>False</i> - Desabilita
	 */
	private void setComponentEnabled(Container container, boolean blnEnabled) {
		try {
			Component[] components = container.getComponents();
			
	        for (Component component : components) {
	        	component.setEnabled(blnEnabled);
	        	
	        	if (component instanceof LTTextField) {
	            	((LTTextField) component).setEnabled(blnEnabled);
	        	} else if (component instanceof LTComboBoxField) {
	            	((LTComboBoxField) component).setEnabled(blnEnabled);
	        	} else if (component instanceof LTTable) {
	            	((LTTable) component).setEnabled(blnEnabled);
	            } else if (component instanceof Container) {
	            	setComponentEnabled((Container) component, blnEnabled);
	            }
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}