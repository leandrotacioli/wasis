package br.unicamp.fnjv.wasis.graphics.spectrogram;

/**
 * Interface que define os métodos de chamada que serão notificados
 * para todas as classes que estenderem <i>SpectrogramListener</i>.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 25/Set/2017
 */
public interface SpectrogramListener {
	
    /**
     * Mostra o tempo e frequência atual enquanto o mouse se move no espectrograma.
     * 
     * @param intTime      - Posição do tempo (em milisegundos)
     * @param intFrequency - Posição da frequência (em Hz)
     */
    public void spectrogramCurrentTimeFrequency(int intTime, int intFrequency);
    
    /**
     * Áudio selecionado do espectrograma.
     * 
     * @param intCurrentTime      - Posição atual do mouse no tempo (em milisegundos)
     * @param intInitialTime      - Tempo inicial selecionado (em milisegundos)
     * @param intFinalTime        - Tempo final selecionado (em milisegundos)
     * @param intInitialFrequency - Frequência inicial selecionada (em Hz)
     * @param intFinalFrequency   - Frequência final selecionada (em Hz)
     * @param blnDrawWaveform     - Desenha novamente o waveform ao selecionar parte do áudio
     */
    public void spectrogramSelectedAudio(int intCurrentTime, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency, boolean blnDrawWaveform);
    
    /**
     * Áudio visto no espectrograma.
     * 
     * @param intInitialTime      - Tempo inicial visto no espectrograma (em milisegundos)
     * @param intFinalTime        - Tempo final visto no espectrograma (em milisegundos)
     * @param intInitialFrequency - Frequência inicial vista no espectrograma (em Hz)
     * @param intFinalFrequency   - Frequência final vista no espectrograma (em Hz)
     */
    public void spectrogramViewAudio(int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency);
}