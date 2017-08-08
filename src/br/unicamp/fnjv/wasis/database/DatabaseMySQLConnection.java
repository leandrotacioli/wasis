package br.unicamp.fnjv.wasis.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.StringTransformations;
import com.mysql.jdbc.exceptions.MySQLStatementCancelledException;

/**
 * Fornece acesso a um banco de dados MySQL. <br>
 * É possível realizar transações de inclusão, 
 * atualização, deleção e consulta de dados.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 06/Out/2014
 */
public class DatabaseMySQLConnection extends DatabaseConnection {
	private String strDatabaseServer;
	private String strDatabaseName; 
	private String strDatabaseUser; 
	private String strDatabasePass;
	
	private Connection connectionString;
	private PreparedStatement prepStatement;
	private ResultSet rsReturn;
	
	private StringBuffer strSQLCommand;
	
	private ArrayList<DatabaseSQLParameters> lstParameters;

	private long lgnIdentityKey;        // ID do registro pré-gravado no banco de dados
	
	/**
	 * Fornece acesso a um banco de dados MySQL. <br>
	 * É possível realizar transações de inclusão, 
	 * atualização, deleção e consulta de dados.
	 */
	public DatabaseMySQLConnection() {
		super();
		
		this.strDatabaseServer = "localhost";
		this.strDatabaseName = "wasis_mestrado";
		this.strDatabaseUser = "root";
		this.strDatabasePass = "123456";
	}
	
	@Override
	public void setDatabaseConnection(String strDatabaseServer, String strDatabaseName, String strDatabaseUser, String strDatabasePass) {
		this.strDatabaseServer = strDatabaseServer;
		this.strDatabaseName = strDatabaseName;
		this.strDatabaseUser = strDatabaseUser;
		this.strDatabasePass = strDatabasePass;
	}
	
	@Override
	public void openConnection() {
		String strDatabaseURL = "jdbc:mysql://" + strDatabaseServer + "/" + strDatabaseName + "?useUnicode=true&characterEncoding=UTF-8";
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			connectionString = DriverManager.getConnection(strDatabaseURL, strDatabaseUser, strDatabasePass);			
			connectionString.setAutoCommit(false);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void closeConnection() {
		try {
			if (connectionString != null && !connectionString.isClosed()) {
				connectionString.close();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void commitTransaction() {
		try {
			if (connectionString != null && !connectionString.isClosed()) {
				connectionString.commit();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void rollBackTransaction() {
		try {
			if (connectionString != null && !connectionString.isClosed()) {
				connectionString.rollback();
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initiliazeStatement() {
		strSQLCommand = new StringBuffer();
		lstParameters = new ArrayList<DatabaseSQLParameters>();
		lgnIdentityKey = 0;
	}
	
	@Override
	public void sqlCommand(String strSQLCommand) {
		this.strSQLCommand = new StringBuffer(strSQLCommand); 
	}
	
	@Override
	public void sqlCommandAppend(String strSQLCommandAppend) {
		this.strSQLCommand.append(" " + strSQLCommandAppend);
	}
	
	@Override
	public void addParameter(String strColumnDatabase, LTDataTypes objDataType, Object objValue) {
		lstParameters.add(new DatabaseSQLParameters(strColumnDatabase, objDataType, objValue));
	}
	
	@Override
	public int executeQuery() throws SQLException {
		int intRecordsProcessed = -1;
		
		try {
			String strSQL = strSQLCommand.toString();
			prepStatement = connectionString.prepareStatement(strSQL, Statement.RETURN_GENERATED_KEYS);
			
			addParameterValuesToQuery();
			
			intRecordsProcessed = prepStatement.executeUpdate();

			// Retorna o ID do novo registro adicionado
			ResultSet rsResult = prepStatement.getGeneratedKeys();
			if (rsResult.next()) {
				lgnIdentityKey = rsResult.getLong(1);
			}
				
			prepStatement.close();
			
		} catch (SQLException e) {
			throw new SQLException(e);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return intRecordsProcessed;
	}
	
	@Override
	public ResultSet executeSelectQuery() throws SQLException {		
		try {
			String strSQL = strSQLCommand.toString();
			//prepStatement = connectionString.prepareStatement(strSQL, Statement.RETURN_GENERATED_KEYS);
			prepStatement = connectionString.prepareStatement(strSQL, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			addParameterValuesToQuery();
			
			//System.out.println(prepStatement);
			
			rsReturn = prepStatement.executeQuery();

		// Processamento cancelado pelo usuário
		} catch (MySQLStatementCancelledException e) {
			return null;
			
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		return rsReturn;
	}
	
	/**
	 * Adiciona os valores da lista de parâmetros a instrução SQL.
	 * 
	 * @throws SQLException
	 */
	private void addParameterValuesToQuery() throws SQLException {
		try {
			for (int indexParameter = 0; indexParameter < lstParameters.size(); indexParameter++) {
				LTDataTypes objDataType = lstParameters.get(indexParameter).getDataType();
				Object objValue = lstParameters.get(indexParameter).getValue();
				String strValue = null;
				
				if (objValue != null) {
					strValue = objValue.toString();
				}
				
				// Integer
				if (objDataType == LTDataTypes.INTEGER) {
					if (objValue instanceof String) {
						if (strValue == null || strValue.length() == 0) {
							prepStatement.setInt(indexParameter + 1, 0);
						} else {
							int intValue = Integer.valueOf(strValue);
							prepStatement.setInt(indexParameter + 1, intValue);
						}
						
					} else if (objValue instanceof Integer) {
						int intValue = (int) objValue;
						prepStatement.setInt(indexParameter + 1, intValue);
					}
					
				// Long
				} else if (objDataType == LTDataTypes.LONG) {
					if (objValue instanceof String) {
						if (strValue == null || strValue.length() == 0) {
							prepStatement.setInt(indexParameter + 1, 0);
						} else {
							long lgnValue = Long.valueOf(strValue);
							prepStatement.setLong(indexParameter + 1, lgnValue);
						}
						
					} else if (objValue instanceof Long) {
						long lgnValue = (long) objValue;
						prepStatement.setLong(indexParameter + 1, lgnValue);
						
					} else if (objValue instanceof Integer) {
						long lgnValue = Long.valueOf(((Integer) objValue).longValue());
						prepStatement.setLong(indexParameter + 1, lgnValue);
					}
					
				// Double
				} else if (objDataType == LTDataTypes.DOUBLE) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						strValue = strValue.replace(".", "");
						strValue = strValue.replace(",", ".");
						
						double dblValue = Double.valueOf(strValue);
						prepStatement.setDouble(indexParameter + 1, dblValue);
						
					} else if (objValue instanceof Double) {
						double dblValue = (double) objValue;
						strValue = String.valueOf(dblValue);
	
						dblValue = Double.valueOf(strValue);
						prepStatement.setDouble(indexParameter + 1, dblValue);
					}
				
				// String
				} else if (objDataType == LTDataTypes.STRING) {
					prepStatement.setString(indexParameter + 1, strValue);
					
				// Text
				} else if (objDataType == LTDataTypes.TEXT) {
					prepStatement.setString(indexParameter + 1, strValue);
					
				// Date
				} else if (objDataType == LTDataTypes.DATE) {
					if (objValue instanceof String) {
						if (strValue == null || strValue.length() == 0) {
							prepStatement.setTimestamp(indexParameter + 1, null);
						} else {
							Date dataValue = StringTransformations.setStringToDate(strValue);
							Timestamp timeStampValue = new Timestamp(dataValue.getTime());
							prepStatement.setTimestamp(indexParameter + 1, timeStampValue);
						}
				        
					} else if (objValue instanceof Date) {
						Date dataValue = (Date) objValue;
						Timestamp timeStampValue = new Timestamp(dataValue.getTime());
				        prepStatement.setTimestamp(indexParameter + 1, timeStampValue);
				    
					} else {
						prepStatement.setTimestamp(indexParameter + 1, null);
					}
				
				// Boolean
				} else if (objDataType == LTDataTypes.BOOLEAN) {
					boolean blnValue = (boolean) objValue;
					prepStatement.setBoolean(indexParameter + 1, blnValue);
				}
			}
			
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}
	
	@Override
	public void cancelSelectQuery() {
		try {
			prepStatement.cancel();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public Object getFirstValue(LTDataTypes objDataType) {
		Object objValue = null;
		
		try {
			rsReturn.beforeFirst();
			
			while (rsReturn.next()) {
				if (objDataType == LTDataTypes.INTEGER) {
					objValue = rsReturn.getInt(1);
				} else if (objDataType == LTDataTypes.LONG) {
					objValue = rsReturn.getLong(1);
				} else if (objDataType == LTDataTypes.DOUBLE) {
					objValue = rsReturn.getDouble(1);
				} else if (objDataType == LTDataTypes.STRING) {
					objValue = rsReturn.getString(1);
				} else if (objDataType == LTDataTypes.TEXT) {
					objValue = rsReturn.getString(1);
				} else if (objDataType == LTDataTypes.DATE) {
					objValue = rsReturn.getDate(1);
				} else if (objDataType == LTDataTypes.BOOLEAN) {
					objValue = rsReturn.getBoolean(1);
				}
				
				break;
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return objValue;
	}
	
	@Override
	public long getIdentityKey() {
		return lgnIdentityKey;
	}
	
	@Override
	public int getTotalRecords() {
		int intTotalRecords = 0;
		
		try {
			rsReturn.last();
			
			intTotalRecords = rsReturn.getRow();
			
			rsReturn.beforeFirst();
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return intTotalRecords;
	}
}