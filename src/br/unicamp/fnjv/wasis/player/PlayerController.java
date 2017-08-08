package br.unicamp.fnjv.wasis.player;

/**
 * Define os controles do Player
 * 
 * @author Leandro Tacioli
 * @version 1.1 - 19/Nov/2014
 */
public interface PlayerController {
    public void openFile() throws Exception;
    
    public void closeFile() throws Exception;
    
    public void playAudio(int intInitialMilliseconds, int intFinalMilliseconds) throws Exception;
    
    public void pauseAudio() throws Exception;
    
    public void resumeAudio() throws Exception;
    
    public void stopAudio() throws Exception;
    
    public void finishAudio() throws Exception;
}