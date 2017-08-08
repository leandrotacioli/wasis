package br.unicamp.fnjv.wasis.multimidia;

import java.io.File;
import java.util.ResourceBundle;

import javax.swing.filechooser.FileFilter;

import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Cria um filtro responsável por habilitar todos os formatos de áudio suportados pelo Wasis.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 01/Oct/2013
 */
public class AudioFileFilter extends FileFilter {
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	/**
	 * Cria um filtro responsável por habilitar todos os formatos de áudio suportados pelo Wasis.
	 */
	public AudioFileFilter() {
		
	}

	// Aceita todos os diretórios e todos os arquivos de áudio válidos.
	@Override
    public boolean accept(File file) {
        if (file.isDirectory()) {
            return true;
        }

        String strExtension = AudioFileFormats.getExtension(file);
        if (strExtension != null) {
        	for (int intIndex = 0; intIndex < AudioFileFormats.AUDIO_FORMATS.length; intIndex++) {
        		if (strExtension.equals(AudioFileFormats.AUDIO_FORMATS[intIndex])) {
        			return true;
        		}
        	}
        }

        return false;
    }

    /**
     * Descrição do filtro
     */
	@Override
    public String getDescription() {
        return rsBundle.getString("audio_files_filter_description");
    }
}