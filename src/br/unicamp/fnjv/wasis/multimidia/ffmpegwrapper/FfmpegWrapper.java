package br.unicamp.fnjv.wasis.multimidia.ffmpegwrapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * FFMPEG Wrapper
 * 
 * @author Leandro Tacioli
 * @version 1.1 - 28/Ago/2015
 */
public class FfmpegWrapper {
	private String strFfmpegExecutablePath;

	private Process process = null;
	private ArrayList<String> arrayParameters;
	private Collection<Object> collectionListenerWrapper;
	
	/**
	 * FFMPEG Wrapper.
	 * 
	 * @param ffmpegExecutablePath - Caminho do FFMPEG executável
	 */
	protected FfmpegWrapper(String strFfmpegExecutablePath) {
		this.strFfmpegExecutablePath = strFfmpegExecutablePath;
		this.arrayParameters = new ArrayList<String>();
		this.collectionListenerWrapper = new ArrayList<Object>();
	}

	/**
	 * Adiciona um parâmetro a chamada do executável FFMPEG.
	 * 
	 * @param strParameter
	 */
	protected void addParameter(String strParameter) {
		arrayParameters.add(strParameter);
	}

	/**
	 * Executa o FFMPEG.
	 * 
	 * @throws IOException
	 */
	protected void executeFfmpeg() throws IOException {
		int intParamSize = arrayParameters.size();
		
		String[] command = new String[intParamSize + 1];
		command[0] = strFfmpegExecutablePath;
		
		StringBuffer strBuffer = new StringBuffer();
		strBuffer.append(command[0] + " ");
		
		for (int indexParam = 0; indexParam < intParamSize; indexParam++) {
			command[indexParam + 1] = (String) arrayParameters.get(indexParam);
			strBuffer.append(command[indexParam + 1] + " ");
		}
		
		try {
			process = Runtime.getRuntime().exec(command);

			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(process.getErrorStream());
	
	        // Encontra duração total do arquivo de áudio
	        Pattern durationPattern = Pattern.compile("(?<=Duration: )[^,]*");
	        String strDuration = scanner.findWithinHorizon(durationPattern, 0);
	        
	        if (strDuration == null) {
	        	throw new IOException("Invalid audio file");
	        }
	        
	        String[] duration = strDuration.split(":");
	        double dblTotalSeconds = Integer.parseInt(duration[0]) * 3600 +   // hours
	                          		 Integer.parseInt(duration[1]) * 60 +     // minutes
	                          		 Double.parseDouble(duration[2]);         // seconds
	        
	        // Atualiza o progresso da conversão, baseado no tempo total de duração e o tempo já processado
	        Pattern timePattern = Pattern.compile("(?<=time=)[\\d:.]*");
	        String strMatch;
	        String[] matchSplit;
	        
	        double dblPorcentagePerSecond = 100 / dblTotalSeconds;
	        double dblProgressInSeconds;
	        double dblFinalProgress;
	        
	        while (null != (strMatch = scanner.findWithinHorizon(timePattern, 0))) {
	            matchSplit = strMatch.split(":");
	            
	            dblProgressInSeconds = Integer.parseInt(matchSplit[0]) * 3600 +   // hours
	            			  		   Integer.parseInt(matchSplit[1]) * 60 +     // minutes
	            			  		   Double.parseDouble(matchSplit[2]);         // seconds
	            
	            dblFinalProgress = dblProgressInSeconds * dblPorcentagePerSecond;
	            updateProgress(dblFinalProgress);
	        }
	        
	        // Conversão do arquivo concluída
	        updateProgress(100);
	        
		} catch (IOException e) {
			throw new IOException(e);
		}
	}
	
	//*************************************************************************
	// Wrapper's Collection Listeners
	/**
	 * Retorna a coleçãoo de listeners do Wrapper.
	 * 
	 * @return collectionListenerWrapper
	 */
	protected Collection<Object> getCollectionListenerWrapper() {
		return collectionListenerWrapper;
	}

	/**
     * Adiciona coleção ao wrapper listener
     * 
     * @param wrapperListener
     */
	protected void addWrapperListener(FfmpegWrapperListener wrapperListener) {
    	collectionListenerWrapper.add(wrapperListener);
    }
	
    /**
     * Notifica o listener de uma atualização no progresso da conversão.
     * 
     * @param dblProgress
     */
	private void updateProgress(double dblProgress) {
        Iterator<Object> it = collectionListenerWrapper.iterator();
        FfmpegWrapperListener wrapperListener;
        while (it.hasNext()) {
        	wrapperListener = (FfmpegWrapperListener) it.next();
        	wrapperListener.updateProgress((int) dblProgress);
        }
	}

	/**
	 * Se há uma execução do FFMPEG em progresso, ela é cancelada.
	 */
	protected void destroyFfmpeg() {
		if (process != null) {
			process.destroy();
			process = null;
		}
	}
}