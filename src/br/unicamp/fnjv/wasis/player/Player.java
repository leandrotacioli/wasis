package br.unicamp.fnjv.wasis.player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWavFormat;

/**
 * Player de Áudio.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 12/Fev/2015
 */
public class Player implements Runnable, PlayerController {
	private AudioWav objAudioWav;
	
	private AudioInputStream audioInputStream;
	private AudioFormat audioFormat;
	private SourceDataLine sourceDataLine;
	
	private Thread threadPlayer;
	private ThreadPlayerTimeElapsed threadPlayerTimeElapsed;
	
	private Collection<Object> collectionListenerPlayer;
	
	private int intAudioLengthToPlay;  // Tempo total que irá ser tocado (considerando ou não a seleção de um pedaço do áudio)
	private int intInitialMillisecondsToPlay;
	private int intFinalMillisecondsToPlay;
	
	private long lgnFinalByteToRead;
	private long lgnBytesAlreadyRead;
	
	private final int BUFFER_LENGTH = 2048;
	
	// Determina todos os status que o player pode ter
	public final int STATUS_UNKNOWN = -1;
	public final int STATUS_OPENED = 0;
	public final int STATUS_PLAYING = 1;
	public final int STATUS_PAUSING = 2;
	public final int STATUS_PAUSED = 3;
	public final int STATUS_STOPPED = 4;
	private int intPlayerStatus = STATUS_UNKNOWN;
	
	private boolean blnAllowResumeAudio; // True - Apenas se o player estiver STATUS_PAUSED
	
	private int intTimeElapsed;
	
	/**
	 * Retorna o status do player.
	 * 
	 * @return intPlayerStatus
	 * <br>
	 * -1 = STATUS_UNKNOWN<br>
	 * 0 = STATUS_OPENED<br>
	 * 1 = STATUS_PLAYING<br>
	 * 2 = STATUS_PAUSING<br>
	 * 3 = STATUS_PAUSED<br>
	 * 4 = STATUS_STOPPED<br>
	 */
	public int getPlayerStatus() {
		return intPlayerStatus;
	}
	
	/**
	 * Retorna o status informando que é possível resumir o áudio do local onde foi parado.
	 * 
	 * @return blnAllowResumeAudio
	 */
	public boolean getAllowResumeAudio() {
		return blnAllowResumeAudio;
	}
	
	/**
	 * Altera o status para resumir o áudio do local onde foi parado.
	 * 
	 * @param blnAllowResumeAudio
	 */
	public void setAllowResumeAudio(boolean blnAllowResumeAudio) {
		this.blnAllowResumeAudio = blnAllowResumeAudio;
		
		updatePlayerStatus(STATUS_STOPPED, intInitialMillisecondsToPlay);
	}
	
	/**
	 * Player de Áudio.
	 * 
	 * @param objAudioWav
	 */
	public Player(AudioWav objAudioWav) {
		this.objAudioWav = objAudioWav;
		
		this.collectionListenerPlayer = new ArrayList<Object>();
	}
	
	/**
	 * Inicializa AudioInputStream.
	 */
	private void initAudioInputStream() {
        try {
        	File audioFile = new File(objAudioWav.getAudioFilePathTemporary());
        	audioInputStream = AudioSystem.getAudioInputStream(audioFile);
        	audioFormat = audioInputStream.getFormat();
        	intPlayerStatus = STATUS_OPENED;
        	updatePlayerStatus(STATUS_OPENED, 0);
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
	
    /**
     * Fecha AudioInputStream.
     */
    private void closeAudioInputStream() {   	
    	try {
            if (audioInputStream != null) {
            	audioInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
	 * Inicializa SourceDataLine.
	 */
    private void initSourceDataLine() {
    	try {
        	int intNumSampleSizeInBits = audioFormat.getSampleSizeInBits();
            
            if ((audioFormat.getEncoding() == AudioFormat.Encoding.ULAW) || (audioFormat.getEncoding() == AudioFormat.Encoding.ALAW)) {
            	intNumSampleSizeInBits = 16;
            }
            
            if ((intNumSampleSizeInBits <= 0) || (intNumSampleSizeInBits != 8)) {
            	intNumSampleSizeInBits = 16;
            }

            // Transforma o áudio em um formato que esteja habilitado a ser tocado pelo Java
            AudioFormat targetFormat = new AudioFormat(AudioWavFormat.TARGET_ENCODING, 
            										   AudioWavFormat.TARGET_SAMPLE_RATE,
            										   intNumSampleSizeInBits, 
            										   audioFormat.getChannels(), 
            										   audioFormat.getChannels() * intNumSampleSizeInBits / 8, 
            										   AudioWavFormat.TARGET_SAMPLE_RATE,
            										   AudioWavFormat.TARGET_HANDLING_MEMORY_STORAGE);
            
            // Cria o stream decodificado
            audioInputStream = AudioSystem.getAudioInputStream(targetFormat, audioInputStream);
            audioFormat = audioInputStream.getFormat();
            
            // Cria SourceDataLine
            DataLine.Info dataLineInfo = new DataLine.Info(SourceDataLine.class, audioFormat, AudioSystem.NOT_SPECIFIED);
			sourceDataLine = (SourceDataLine) AudioSystem.getLine(dataLineInfo);
			sourceDataLine.open(audioFormat);
			
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
    }
    
    /**
     * Retorna a posição do áudio em bytes a partir de um tempo passado como parâmetro.
     * 
     * @param intTimeMilliseconds - Tempo em milisegundos
     * 
     * @return lgnBytePosition
     */
    private long getAudioBytePosition(int intTimeMilliseconds) {
        long lgnSamplesToSkip = (long) intTimeMilliseconds * objAudioWav.getWavHeader().getSampleRate() / 1000;
        long lgnBytePosition = lgnSamplesToSkip * objAudioWav.getWavHeader().getBytesPerSample() * objAudioWav.getWavHeader().getChannels();
    	
    	return lgnBytePosition;
    }

	//*************************************************************************
	// Implementa PlayerController
	@Override
	public void openFile() {
		closeAudioInputStream();
		initAudioInputStream();
	}
	
	@Override
	public void closeFile() {
		closeAudioInputStream();
	}
	
	@Override
	public void playAudio(int intInitialMilliseconds, int intFinalMilliseconds) {
		// Pára o áudio se ainda estiver tocando
		if (intPlayerStatus == STATUS_PLAYING) {
        	stopAudio();
        	
        	// CHEAT - Dorme por um instante, para que a thread concorrente 'ThreadPlayerTimeElapsed' possa terminar
    		try {
            	Thread.sleep(50);  
            } catch (InterruptedException e) {
            	e.printStackTrace();
            }
		}
		
		initAudioInputStream();
		
		intAudioLengthToPlay = intFinalMilliseconds - intInitialMilliseconds;
		intInitialMillisecondsToPlay = intInitialMilliseconds;
		intFinalMillisecondsToPlay = intFinalMilliseconds;
		
        if (intPlayerStatus == STATUS_OPENED) {
            initSourceDataLine();

            // Encontra a posição inicial do pedaço de áudio, para que o streaming saiba em qual local será iniciado o playback
			lgnBytesAlreadyRead = getAudioBytePosition(intInitialMilliseconds);
			
			// Encontra a posição final do pedaço do áudio, para que o streaming saiba em qual local será finalizado o playback
			lgnFinalByteToRead = getAudioBytePosition(intFinalMilliseconds);
			
            intPlayerStatus = STATUS_PLAYING;
            updatePlayerStatus(STATUS_PLAYING, intInitialMillisecondsToPlay);
            
            // Inicia a thread responsável pelo streaming do áudio
            threadPlayer = new Thread(this, "PlayerThread");
            threadPlayer.start();
            
            threadPlayerTimeElapsed = null;
        }
	}

	@Override
	public void pauseAudio() {
		if (sourceDataLine != null) {
            if (intPlayerStatus == STATUS_PLAYING) {
            	sourceDataLine.stop();
            	intPlayerStatus = STATUS_PAUSING;
            	updatePlayerStatus(STATUS_PAUSING, (intTimeElapsed + intInitialMillisecondsToPlay));
            }
        }
	}

	@Override
	public void resumeAudio() {
		if (sourceDataLine != null) {
			if (blnAllowResumeAudio) {
				playAudio(intInitialMillisecondsToPlay, intFinalMillisecondsToPlay);
				blnAllowResumeAudio = false;
			}
        }
	}
	
	@Override
	public void finishAudio() {
        if (sourceDataLine != null) {
			if (intPlayerStatus == STATUS_PLAYING) {
				sourceDataLine.drain();
            	sourceDataLine.close();
	            intPlayerStatus = STATUS_STOPPED;
	            closeAudioInputStream();
            }
			
			blnAllowResumeAudio = false;
        }
        
        updatePlayerStatus(STATUS_STOPPED, intInitialMillisecondsToPlay);
	}
	
	@Override
	public void stopAudio() {
        if (sourceDataLine != null) {
			if (intPlayerStatus == STATUS_PLAYING || intPlayerStatus == STATUS_PAUSED) {
				sourceDataLine.stop();
            	sourceDataLine.close();
	            intPlayerStatus = STATUS_STOPPED;
	            closeAudioInputStream();
            }
			
			blnAllowResumeAudio = false;
        }
        
        updatePlayerStatus(STATUS_STOPPED, intInitialMillisecondsToPlay);
	}

	//*************************************************************************
	// Implementa Runnable
	@Override
	public void run() {
        int intBytesRead = 0;
        byte[] arrayBuffer = new byte[BUFFER_LENGTH];
        
        long lgnBytesSkipped = 0;
        boolean blnBytesSkipped = false;
		
		while (intPlayerStatus != STATUS_STOPPED) {

			if (sourceDataLine.isOpen()) {
	            sourceDataLine.start();

	            while (true) {
	            	try {
	            		// Playing
	    	        	if (intPlayerStatus == STATUS_PLAYING) {
	    	        		
	    	        		// Desconsidera os bytes iniciais, caso seja necessário tocar o áudio
	    	        		// a partir de uma posição que não seja o início do áudio.
	    	    			if (!blnBytesSkipped) {
	    	    				try {
	    	    					while (lgnBytesSkipped < lgnBytesAlreadyRead) {
	    	    						lgnBytesSkipped += audioInputStream.skip(lgnBytesAlreadyRead - lgnBytesSkipped);
	    	    					}
	    	    					
	    	    					blnBytesSkipped = true;
	    	    				} catch (IOException e) {
	    	    					e.printStackTrace();
	    	    				}
	    	    			}
	    	        			    	        		
		            		intBytesRead = audioInputStream.read(arrayBuffer);
		            		lgnBytesAlreadyRead += intBytesRead;

		            		if (lgnBytesAlreadyRead <= lgnFinalByteToRead) {
			            		if (intBytesRead >= 0) {
			            			sourceDataLine.write(arrayBuffer, 0, intBytesRead);
			            		} else {
			            			finishAudio();
			    	                break;
			            		}
		            		} else {
		            			finishAudio();
		    	                break;
		            		}
		            		
		            		// Inicia a thread responsável pela atualização do tempo decorrido do player
		            		if (threadPlayerTimeElapsed == null) {
		            			threadPlayerTimeElapsed = new ThreadPlayerTimeElapsed(System.currentTimeMillis());
		            			threadPlayerTimeElapsed.start();
		            		}
		            		
		            	// Paused
	    	            } else if (intPlayerStatus == STATUS_PAUSED) {
	    	            	stopAudio();
	    	            	blnAllowResumeAudio = true;
	    	            	updatePlayerStatus(STATUS_STOPPED, intInitialMillisecondsToPlay);
	    	            	break;
	    	                
	    	            // Stopped
	    	            } else if (intPlayerStatus == STATUS_STOPPED) {
	    	            	stopAudio();
	    	            	break;
	    	            }	
	    	            
	            	} catch (IOException e) {
	                    e.printStackTrace();
	                }
		        }
			}
		}
	}
	
	//*************************************************************************
	// Player's Collection Listeners
	/**
	 * Retorna a 'collection listener' do Player.
	 * 
	 * @return collectionListenersPlayer
	 */
	public Collection<Object> getCollectionListenerPlayer() {
		return collectionListenerPlayer;
	}

	/**
     * Adiciona um 'PlayerListener' parametrizado à 'collection listener'.
     * 
     * @param playerListener
     */
    public void addPlayerListener(PlayerListener playerListener) {
    	collectionListenerPlayer.add(playerListener);
    }
	
    /**
     * Notifica o 'PlayerListener' de uma atualização no status do Player.
     * 
     * @param strStatus
     */
	private void updatePlayerStatus(int intStatusPlayer, int intTimeMilliseconds) {
        Iterator<Object> it = collectionListenerPlayer.iterator();
        PlayerListener playerListener;
        while (it.hasNext()) {
            playerListener = (PlayerListener) it.next();
            playerListener.playerStatus(intStatusPlayer, intTimeMilliseconds);
        }
	}
	
	/**
	 * Notifica o 'PlayerListener' de uma atualização no tempo decorrido.
	 * 
	 * @param intMilliseconds
	 */
	private void updatePlayerTimeElapsed(int intMilliseconds) {
        Iterator<Object> it = collectionListenerPlayer.iterator();
        PlayerListener playerListener;
        while (it.hasNext()) {
            playerListener = (PlayerListener) it.next();
            playerListener.playerTimeElapsed(intMilliseconds);
        }
	}
	
	/**
	 * Thread responsável por atualizar o tempo decorrido.
	 */
	class ThreadPlayerTimeElapsed extends Thread {
		private long lgnInitialTimeElapsed;
		private long lgnCurrentTimeElapsed;
		
		/**
		 * Atualiza o tempo decorrido.
		 * 
		 * @param lgnInitialTimeElapsed
		 */
		public ThreadPlayerTimeElapsed(long lgnInitialTimeElapsed) {
			this.lgnInitialTimeElapsed = lgnInitialTimeElapsed;
		}
		
		@Override
		public void run() {
			while ((intPlayerStatus == STATUS_PLAYING) || (intPlayerStatus == STATUS_PAUSING)) {
				
				// Playing
				if (intPlayerStatus == STATUS_PLAYING) {
					lgnCurrentTimeElapsed = System.currentTimeMillis();
					intTimeElapsed = (int) (lgnCurrentTimeElapsed - lgnInitialTimeElapsed);
					
					if (intTimeElapsed <= intAudioLengthToPlay) {
						updatePlayerTimeElapsed(intTimeElapsed + intInitialMillisecondsToPlay);
					}
					
					// Thread dorme por um instante para não sobrecarregar a CPU
					try {
	                	Thread.sleep(25);
	                } catch (InterruptedException e) {
	                	e.printStackTrace();
	                }
					
				// Pausing
				} else if (intPlayerStatus == STATUS_PAUSING) {
					intInitialMillisecondsToPlay = intTimeElapsed + intInitialMillisecondsToPlay;
					intPlayerStatus = STATUS_PAUSED;
					updatePlayerStatus(STATUS_PAUSED, intInitialMillisecondsToPlay);
				}
			}
		}
	}
}