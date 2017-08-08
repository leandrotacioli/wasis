package br.unicamp.fnjv.wasis.swing;

import java.awt.Component;
import java.awt.Graphics;

import javax.swing.BorderFactory;
import javax.swing.JSplitPane;
import javax.swing.plaf.SplitPaneUI;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.plaf.basic.BasicSplitPaneUI;

import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
* Cria uma extensão para <i>JSplitPane</i> 
* que não desenha absolutamente nada.
* 
* @author Leandro Tacioli
* @version 2.1 - 30/Jun/2016
*/
public class WasisInvisibleSplitPane extends JSplitPane {
	private static final long serialVersionUID = -1090476226580418095L;

	public WasisInvisibleSplitPane () {
		super();
	}

	public WasisInvisibleSplitPane(int newOrientation, boolean newContinuousLayout, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newContinuousLayout, newLeftComponent, newRightComponent);
	}
	
	public WasisInvisibleSplitPane(int newOrientation, boolean newContinuousLayout) {
		super(newOrientation, newContinuousLayout );
	}
	
	public WasisInvisibleSplitPane(int newOrientation, Component newLeftComponent, Component newRightComponent) {
		super(newOrientation, newLeftComponent, newRightComponent);
	}
	
	public WasisInvisibleSplitPane(int newOrientation) {
		super(newOrientation);
	}
	
	/**
	* Notification from the <code>UIManager</code> that the L&F has changed.
	* Replaces the current UI object with the latest version from the
	* <code>UIManager</code>.
	* 
	* @see JComponent#updateUI
	*/
	public void updateUI() {
		SplitPaneUI uiSplitPane = new InvisibleSplitPaneUI();
		setUI(uiSplitPane);
		setBackground(WasisParameters.COLOR_BACKGROUND_MAIN);
		setDividerSize(2);
		revalidate();
	}
	
	/**
	* The interface do usuário <i>Look and Feel</i> que não desenha nada.
	*/
	private class InvisibleSplitPaneUI extends BasicSplitPaneUI {
		public InvisibleSplitPaneUI() {
		
		}
	
		@Override
		protected void installDefaults() {
			super.installDefaults();
	
			splitPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		}
		
		/**
		* Cria o divisor padrão.
		*/
		public BasicSplitPaneDivider createDefaultDivider() {
			BasicSplitPaneDivider divider = new BasicSplitPaneDivider(this) {
				private static final long serialVersionUID = 7887607881742345242L;

				@Override
				public void paint(Graphics g) {
				
				}
			};
			
			divider.setBorder(null);
			
			return divider;
		}
	}
}