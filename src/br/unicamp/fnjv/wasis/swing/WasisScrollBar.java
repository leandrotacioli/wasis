package br.unicamp.fnjv.wasis.swing;

import javax.swing.JScrollBar;

/**
 * Cria uma extensão para <i>JScrollBar</i>
 * que será utilizada nas scroll horizontal e vertical
 * do espectrograma.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 09/Fev/2015
 */
public class WasisScrollBar extends JScrollBar {
	private static final long serialVersionUID = 1355675713039667021L;
	
	private boolean blnAllowSetValue;
	
	/**
	 * Permite a alteração do valor da scrollbar.
	 * 
	 * @param blnAllowSetValue
	 */
	public void allowSetValue(boolean blnAllowSetValue) {
		this.blnAllowSetValue = blnAllowSetValue;
	}

	public WasisScrollBar() {
		super();
	}

	public WasisScrollBar(int orientation, int value, int extent, int min, int max) {
		super(orientation, value, extent, min, max);
	}

	public WasisScrollBar(int orientation) {
		super(orientation);
	}
	
	@Override
	public void setValue(int intValue) {
		if (blnAllowSetValue) {
			super.setValue(intValue);
		}
	}
}