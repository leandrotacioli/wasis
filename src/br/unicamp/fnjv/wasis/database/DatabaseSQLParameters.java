package br.unicamp.fnjv.wasis.database;

import com.leandrotacioli.libs.LTDataTypes;

/**
 * Estabelece parâmetros que podem ser utilizados
 * nas instruções SQL do banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 15/Set/2014
 */
public class DatabaseSQLParameters {
	private String strColumnDatabase;
	private LTDataTypes objDataType;
	private Object objValue;
	
	/**
	 * Retorna a coluna do banco de dados.
	 * 
	 * @return strColumnDatabase
	 */
	public String getColumnDatabase() {
		return strColumnDatabase;
	}

	/**
	 * Retorna o tipo de dado.
	 * 
	 * @return objDataType
	 */
	public LTDataTypes getDataType() {
		return objDataType;
	}

	/**
	 * Retorna o valor do parâmetro.
	 * 
	 * @return objValue
	 */
	public Object getValue() {
		return objValue;
	}
	
	/**
	 * Estabelece parâmetros que podem ser utilizados
	 * nas instruções SQL do banco de dados.
	 * 
	 * @param strColumnDatabase - Coluna do banco de dados
	 * @param objDataType       - Tipo de dado
	 * @param objValue          - Valor do parâmetro
	 */
	public DatabaseSQLParameters(String strColumnDatabase, LTDataTypes objDataType, Object objValue) {
		this.strColumnDatabase = strColumnDatabase;
		this.objDataType = objDataType;
		this.objValue = objValue;
	}
}