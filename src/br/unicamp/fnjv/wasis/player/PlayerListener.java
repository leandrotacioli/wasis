package br.unicamp.fnjv.wasis.player;

/**
 * This interface defines callbacks methods that will be notified
 * for all registered PlayerListener of Player.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 25/Set/2017
 */
public interface PlayerListener {
	
	/**
     * Status do Player (OPENED, PLAYING, PAUSING, PAUSED, STOPPED, UNKNOWN).
     * 
     * @param strStatusPlayer
     * @param intTimeMilliseconds
     */
    public void playerStatus(int intStatusPlayer, int intTimeMilliseconds);
	
    /**
     * Tempo decorrido enquanto o áudio está tocando (em milisegundos).
     * 
     * @param intTimeMilliseconds - Posição em milisegundos
     */
    public void playerTimeElapsed(int intTimeMilliseconds);
}