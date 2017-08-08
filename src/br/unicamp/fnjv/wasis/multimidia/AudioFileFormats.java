package br.unicamp.fnjv.wasis.multimidia;

import java.io.File;

/**
 * Define os formatos de áudio suportados pelo Wasis.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 01/Oct/2013
 */
public class AudioFileFormats {
    public final static String[] AUDIO_FORMATS = new String[] {"wav", "aif", "aiff", "aac", "m4a",  
    														   "mpc", "ac3", "flac", "amr", "wma", 
    														   "mp2", "mp3", "ogg"};
    
    /**
     * Define os formatos de áudio suportados pelo Wasis.
     */
    public AudioFileFormats() {
    	
    }
    
    /**
     * Retorna a extensão de um arquivo.
     * 
     * @return strExtension
     */
    public static String getExtension(File file) {
        String strExtension = null;
        
        String strFile = file.getName();
        int intLastIndex = strFile.lastIndexOf('.');

        if (intLastIndex > 0 && intLastIndex < strFile.length() - 1) {
        	strExtension = strFile.substring(intLastIndex + 1).toLowerCase();
        }
        
        return strExtension;
    }
}