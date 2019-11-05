package br.unicamp.fnjv.wasis.database;

import br.unicamp.fnjv.wasis.database.dao.AudioLibraryDAO;
import br.unicamp.fnjv.wasis.database.dao.AudioLibraryDAOImpl;
import br.unicamp.fnjv.wasis.database.dao.AudioLibraryFileDAO;
import br.unicamp.fnjv.wasis.database.dao.AudioLibraryFileDAOImpl;

/**
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 29/Mar/2018
 */
public class DatabaseFactory {

	public static AudioLibraryDAO createAudioLibraryDAO() {
		return new AudioLibraryDAOImpl();
	}

	public static AudioLibraryFileDAO createAudioLibraryFileDAO() {
		return new AudioLibraryFileDAOImpl();
	}
}