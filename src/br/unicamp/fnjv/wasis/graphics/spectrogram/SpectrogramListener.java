package br.unicamp.fnjv.wasis.graphics.spectrogram;

/**
 * Interface que define os métodos de chamada que serão notificados
 * para todas as classes que estenderem <i>SpectrogramListener</i>.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 10/Fev/2015
 */
public interface SpectrogramListener {
	
    /**
     * Mostra o tempo e frequência atual enquanto o mouse se move no espectrograma.
     * 
     * @param intTime      - Posição do tempo (em milisegundos)
     * @param intFrequency - Posição da frequência (em Hz)
     */
    public void spectrogramCurrentTimeFrequency(final int intTime, final int intFrequency);
    
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
    public void spectrogramSelectedAudio(final int intCurrentTime, final int intInitialTime, final int intFinalTime, final int intInitialFrequency, final int intFinalFrequency, final boolean blnDrawWaveform);
    
    /**
     * Áudio visto no espectrograma.
     * 
     * @param intInitialTime      - Tempo inicial visto no espectrograma (em milisegundos)
     * @param intFinalTime        - Tempo final visto no espectrograma (em milisegundos)
     * @param intInitialFrequency - Frequência inicial vista no espectrograma (em Hz)
     * @param intFinalFrequency   - Frequência final vista no espectrograma (em Hz)
     */
    public void spectrogramViewAudio(final int intInitialTime, final int intFinalTime, final int intInitialFrequency, final int intFinalFrequency);
}