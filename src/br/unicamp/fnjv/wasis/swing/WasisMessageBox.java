package br.unicamp.fnjv.wasis.swing;

import java.util.ResourceBundle;

import javax.swing.JOptionPane;

import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Cria uma extensão para <i>JOptionPane</i>.<br>
 * <br>
 * O <i>owner</i> padrão dá tela será o form principal do WASIS.<br>
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 03/Set/2015
 */
public class WasisMessageBox extends JOptionPane {
	private static final long serialVersionUID = 5604856353032382768L;
	
	private static ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	/**
	 * 
	 */
	private WasisMessageBox() {
		
	}
	
	/**
	 * 
	 * @param strMessage     - Mensagem
	 * @param intMessageType - Tipo de mensagem (Informação, erro, etc.)
	 */
	public static void showMessageDialog(String strMessage, int intMessageType) {
		showMessageDialog(WasisParameters.getInstance().getWasisFrame(), strMessage, rsBundle.getString("wasis"), intMessageType);
	}
	
	/**
	 * 
	 * @param strMessage
	 * @param intMessageType - Tipo de mensagem (Informação, erro, etc.)
	 */
	public static int showConfirmDialog(String strMessage, int intMessageType) {
		return JOptionPane.showConfirmDialog(WasisParameters.getInstance().getWasisFrame(), strMessage, rsBundle.getString("wasis_title"), intMessageType);
	}
}