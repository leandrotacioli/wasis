package br.unicamp.fnjv.wasis.swing;

import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Cria uma extensão para <i>JOptionPane</i>.<br>
 * <br>
 * O <i>owner</i> padrão dá tela será o form principal do WASIS.<br>
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 29/Mar/2018
 */
public class WasisMessageBox extends JOptionPane {
	private static final long serialVersionUID = 5604856353032382768L;
	
	private static ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	/**
	 * Cria uma extensão para <i>JOptionPane</i>.
	 */
	private WasisMessageBox() {
		
	}
	
	/**
	 * Traz uma tela de diálogo exibindo uma mensagem, especificando todos os parâmetros.
	 * 
	 * @param strMessage     - Mensagem
	 * @param intMessageType - Tipo de mensagem - <i>INFORMATION_MESSAGE</i>, <i>WARNING_MESSAGE</i>, <i>ERROR_MESSAGE</i>
	 */
	public static void showMessageDialog(String strMessage, int intMessageType) {
		showMessageDialog(WasisParameters.getInstance().getWasisFrame(), strMessage, rsBundle.getString("wasis"), intMessageType, getIcon(intMessageType, false));
	}
	
	/**
	 * Traz uma tela de diálogo exibindo uma mensagem e seu tipo.
	 * @param strMessage
	 * @param intMessageType - Tipo de mensagem - <i>YES_NO_OPTION</i>, <i>YES_NO_CANCEL_OPTION</i>, <i>OK_CANCEL_OPTION</i>
	 */
	public static int showConfirmDialog(String strMessage, int intMessageType) {
		return JOptionPane.showConfirmDialog(WasisParameters.getInstance().getWasisFrame(), strMessage, rsBundle.getString("wasis_title"), intMessageType, intMessageType, getIcon(intMessageType, true));
	}
	
	/**
	 * Determina o ícone que será exibido na tela de diálogo.
	 * 
	 * @param intMessageType - Tipo de mensagem
	 * @param blnConfirmDialog - É tela de diálogo de confirmação
	 * 
	 * @return icon
	 */
	private static ImageIcon getIcon(int intMessageType, boolean blnConfirmDialog) {
		ImageIcon icon = null;
		
		if (!blnConfirmDialog) {
			if (intMessageType == INFORMATION_MESSAGE) {
				icon = new ImageIcon("res/images/dialog/information.png");
			} else if (intMessageType == PLAIN_MESSAGE) {
				icon = new ImageIcon("res/images/dialog/information.png");
			} else if (intMessageType == WARNING_MESSAGE) {
				icon = new ImageIcon("res/images/dialog/warning.png");
			} else if (intMessageType == ERROR_MESSAGE) {
				icon = new ImageIcon("res/images/dialog/error.png");
			} else if (intMessageType == QUESTION_MESSAGE) {
				icon = new ImageIcon("res/images/dialog/question.png");
			}
		} else {
			icon = new ImageIcon("res/images/dialog/question.png");
		}
		
		return icon;
	}
}