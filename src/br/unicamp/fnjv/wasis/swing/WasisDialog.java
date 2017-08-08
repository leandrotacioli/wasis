package br.unicamp.fnjv.wasis.swing;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;

import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Cria uma extensão para <i>JDialog</i>.<br>
 * <br>
 * O <i>owner</i> dá tela sempre será o form principal do WASIS.<br>
 * <br>
 * Permite a escolha da tela ser fechada pressionando o botão <i>ESC</i>.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 03/Set/2015
 */
public class WasisDialog extends JDialog {
	private static final long serialVersionUID = -4653297451335659184L;
	
	private boolean blnAllowClosingByPressingEsc;

	/**
	 * Cria uma extensão para <i>JDialog</i>.<br>
	 * <br>
	 * O <i>owner</i> dá tela sempre será o form principal do WASIS.<br>
	 * <br>
	 * Permite a escolha da tela ser fechada pressionando o botão <i>ESC</i>.
	 * 
	 * @param strTitle                     - Título da caixa de diálogo
	 * @param blnAllowClosingByPressingEsc - Permite o fechamento da caixa de diálogo através da tecla <i>ESC</i>
	 */
	public WasisDialog(String strTitle, boolean blnAllowClosingByPressingEsc) {
		super(WasisParameters.getInstance().getWasisFrame());
		
		setTitle(strTitle);
		setIconImage(WasisParameters.getInstance().getWasisIcon());
		setModalityType(ModalityType.APPLICATION_MODAL);

		getContentPane().setBackground(WasisParameters.COLOR_BACKGROUND);
		
		this.blnAllowClosingByPressingEsc = blnAllowClosingByPressingEsc;
	}

	@Override
	protected JRootPane createRootPane() {
		JRootPane rootPane = new JRootPane();
		
	    KeyStroke stroke = KeyStroke.getKeyStroke("ESCAPE");
	    
	    Action actionListener = new AbstractAction() {
			private static final long serialVersionUID = -2175924405365802839L;

			public void actionPerformed(ActionEvent actionEvent) {
	    		if (blnAllowClosingByPressingEsc) {
	    			setVisible(false);
	    		}
	    	}
	    };
	  
	    InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
	    inputMap.put(stroke, "ESCAPE");
	    rootPane.getActionMap().put("ESCAPE", actionListener);
		
	    return rootPane;
	}
	
	@Override
	public void setVisible(boolean arg0) {
		setLocationRelativeTo(WasisParameters.getInstance().getWasisFrame());
		
		super.setVisible(arg0);
	}
}