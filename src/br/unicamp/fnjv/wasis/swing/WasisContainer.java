package br.unicamp.fnjv.wasis.swing;

import java.awt.Component;
import java.awt.Container;

import com.leandrotacioli.libs.swing.comboboxfield.LTComboBoxField;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

/**
 * Gerenciamento de Container.
 * 
 * @author Leandro Tacioli
 * @version 1.1 - 31/Mar/2018
 */
public class WasisContainer {

	/**
	 * Gerenciamento de Container.
	 */
	private WasisContainer() {
		
	}
	
	/**
	 * Habilita/desabilita os componentes de um container. Pode ser que haja outros painéis dentro do principal,
	 * então utilizamos recursão para varrer os componentes de todos os containers.
	 * 
	 * @param container  - Painel
	 * @param blnEnabled
	 * <br>
	 * <i>True</i> - Habilita
	 * <br>
	 * <i>False</i> - Desabilita
	 */
	public static void setComponentEnabled(Container container, boolean blnEnabled) {
		try {
			Component[] components = container.getComponents();
			
	        for (Component component : components) {
	        	component.setEnabled(blnEnabled);
	        	
	        	if (component instanceof LTTextField) {
	            	((LTTextField) component).setEnabled(blnEnabled);
	        	} else if (component instanceof LTComboBoxField) {
	            	((LTComboBoxField) component).setEnabled(blnEnabled);
	        	//} else if (component instanceof LTTable) {
	            //	((LTTable) component).setEnabled(blnEnabled);
	            } else if (component instanceof Container) {
	            	setComponentEnabled((Container) component, blnEnabled);
	            }
	        }
	        
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}