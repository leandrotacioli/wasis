package br.unicamp.fnjv.wasis.graphics.waveform;

/**
 * This interface defines callbacks methods that will be notified
 * for all registered WaveformListener of Waveform.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 25/Set/2017
 */
public interface WaveformListener {
    /**
     * Mostra o tempo atual enquanto mouse está se movendo.
     * 
     * @param intTime - Posição do tempo (em milisegundos)
     */
    public void waveformCurrentTime(int intTime);
    
    /**
     * Áudio selecionado do waveform.
     * 
     * @param intCurrentTime     - Posição atual do mouse no tempo (em milisegundos)
     * @param intInitialTime     - Tempo inicial selecionado (em milisegundos)
     * @param intFinalTime       - Tempo final selecionado (em milisegundos)
     */
    public void waveformSelectedAudio(int intCurrentTime, int intInitialTime, int intFinalTime);
}