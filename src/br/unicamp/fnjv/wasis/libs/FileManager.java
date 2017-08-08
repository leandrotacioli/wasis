package br.unicamp.fnjv.wasis.libs;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

import javax.xml.bind.DatatypeConverter;

/**
 * Gerenciamento de arquivos.
 * 
 * @author Leandro Tacioli
 * @version 1.4 - 16/Jun/2016
 */
public class FileManager {

	/**
	 * Gerenciamento de arquivos.
	 */
	private FileManager() {
		
	}
	
	/**
	 * Copia um arquivo de um local para outro.
	 * 
	 * @param fileSource      - Caminho do arquivo original
	 * @param fileDestination - Caminho do arquivo final
	 * 
	 * @return blnCopied
	 * 
	 * @throws IOException
	 */
	@SuppressWarnings("resource")
	public static boolean copyFile(File fileSource, File fileDestination) throws IOException {
		boolean blnCopied = false;
		
		FileChannel inputChannel = null;
		FileChannel outputChannel = null;
		
	    try {
	        inputChannel = new FileInputStream(fileSource).getChannel();
	        outputChannel = new FileOutputStream(fileDestination).getChannel();
	        outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
	        blnCopied = true;
	    } finally {
	    	inputChannel.close();
	        outputChannel.close();
	    }
		
		return blnCopied;
	}
	
	/**
     * Retorna o nome de um arquivo sem seu caminho e extensão.
     * 
     * @param file - Arquivo original
     * 
     * @return strFileName
     */
    public static String getFileName(File file) {
        String strFileName = file.getName();

		int intExtensionStart = strFileName.lastIndexOf(".");
		if (intExtensionStart > 0) {
			strFileName = strFileName.substring(0, intExtensionStart);
		}
        
        return strFileName;
    }
	
	/**
     * Retorna a extensão de um arquivo.
     * 
     * @param file - Arquivo original
     * 
     * @return strExtension
     */
    public static String getFileExtension(File file) {
        String strExtension = null;
        
        String strFile = file.getName();
        int intLastIndex = strFile.lastIndexOf('.');

        if (intLastIndex > 0 && intLastIndex < strFile.length() - 1) {
        	strExtension = strFile.substring(intLastIndex + 1).toLowerCase();
        }
        
        return strExtension;
    }
    
    /**
     * Retorna o hash MD5 de um arquivo.
     * 
     * @param file
     * 
     * @return strHash
     */
    public static String getFileHash(File file) throws Error, Exception {
    	String strHash = "";
    	
    	try {
    		FileInputStream fileInputStream = new FileInputStream(file);
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            
            byte[] buffer = new byte[8192];
            int intNumberBytesRead;
            
            while ((intNumberBytesRead = fileInputStream.read(buffer)) > 0) {
            	messageDigest.update(buffer, 0, intNumberBytesRead);
            }
            
            byte[] fileHash = messageDigest.digest();
            
            strHash = DatatypeConverter.printHexBinary(fileHash).toLowerCase();
            
            fileInputStream.close();
    		
    	} catch (Error e) {
        	throw new Error(e);
        	
        } catch (Exception e) {
        	throw new Exception(e);
        }
    	
    	return strHash;
    }
}