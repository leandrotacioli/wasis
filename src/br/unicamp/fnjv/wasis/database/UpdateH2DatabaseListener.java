package br.unicamp.fnjv.wasis.database;

/**
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 15/Mai/2015
 */
public interface UpdateH2DatabaseListener {
	
	/**
	 * Atualiza a quantidade de registros processados.
	 * 
	 * @param intProcessedRecords
	 * @param intTotalRecords
	 */
	public void updateProcessedRecords(int intProcessedRecords, int intTotalRecords);
}
