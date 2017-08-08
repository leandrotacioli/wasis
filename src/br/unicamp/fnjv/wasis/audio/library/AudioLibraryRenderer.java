package br.unicamp.fnjv.wasis.audio.library;

import java.awt.Color;
import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

import br.unicamp.fnjv.wasis.multimidia.AudioFile;

/**
 * Renderiza a lista da biblioteca de áudio.<br>
 * <br>
 * Insere diferentes cores para os registros da biblioteca.
 * 
 * @author Leandro Tacioli
 * @version 1.1 - 03/Set/2015
 */
public class AudioLibraryRenderer extends JLabel implements ListCellRenderer<String> {
	private static final long serialVersionUID = -3360910079865502068L;
	
	private AudioLibrary objAudioLibrary;
	
	/**
	 * Renderiza a lista da biblioteca de áudio.<br>
	 * <br>
	 * Insere diferentes cores para os registros da biblioteca.
	 */
	protected AudioLibraryRenderer(AudioLibrary objAudioLibrary) {
		this.objAudioLibrary = objAudioLibrary;
		
        setOpaque(true);
    }
	
	@Override
	public Component getListCellRendererComponent(JList<? extends String> list, String strValue, int index, boolean isSelected, boolean cellHasFocus) {
		setText(strValue);
		
		if (isSelected) {
			setBackground(new Color(230, 230, 230));
		} else {
			setBackground(new Color(0, 0, 0, 0));
		}
		
		if (strValue.equals(objAudioLibrary.getAudioFilePathLoaded())) {
			setBackground(new Color(100, 180, 220));
		}
		
		if (AudioFile.checkExistingFile(strValue)) {
			setForeground(Color.BLACK);
		} else {
			setForeground(Color.RED);
		}
		
		return this;
	}
}