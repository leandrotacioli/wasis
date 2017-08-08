package br.unicamp.fnjv.wasis.audio.library;

/**
 * Interface que define os métodos de chamada que serão notificados
 * para todas as classes que estenderem <i>AudioLibraryListener</i>.
 * 
 * @author Leandro Tacioli
 * @version 1.1 - 21/Nov/2014
 */
public interface AudioLibraryListener {
	
    /**
     * Abre um novo arquivo de áudio através da biblioteca de áudio.
     */
    public void openAudioFileFromAudioLibrary();
    
    /**
     * Carrega o arquivo de áudio através da biblioteca de áudio.
     * 
     * @param strAudioFilePath - Caminho do arquivo de áudio
     */
    public void loadAudioFileFromAudioLibrary(final String strAudioFilePath);
    
    /**
     * Reseta os valores quando não há nenhum áudio a ser carregado.
     */
    public void resetValuesFromAudioLibrary();
}