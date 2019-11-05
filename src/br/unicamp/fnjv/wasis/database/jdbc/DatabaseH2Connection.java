package br.unicamp.fnjv.wasis.database.jdbc;

import java.io.File;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.LTParameters;
import com.leandrotacioli.libs.StringTransformations;

import br.unicamp.fnjv.wasis.main.WasisParameters;

/**
 * Fornece acesso a um banco de dados H2. <br>
 * É possível realizar transações de inclusão, atualização, deleção e consulta de dados.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 29/Mar/2018
 */
public class DatabaseH2Connection extends DatabaseConnection {
	private ResourceBundle rsBundle = LTParameters.getInstance().getBundle();
	
	/**
	 * Fornece acesso a um banco de dados H2. <br>
	 * É possível realizar transações de inclusão, atualização, deleção e consulta de dados.
	 */
	public DatabaseH2Connection() {
		super();
		
		super.strDatabaseServer = WasisParameters.getInstance().getDatabaseServer();
		super.strDatabaseName = WasisParameters.getInstance().getDatabaseName();
		super.strDatabaseUser = WasisParameters.getInstance().getDatabaseUser();
		super.strDatabasePass = WasisParameters.getInstance().getDatabasePassword();
		
		File fileDatabase = new File("data/wasis.mv.db");
		
		String strDatabasePath = fileDatabase.getAbsoluteFile().getParentFile().getAbsolutePath();
		
		super.strDatabaseURL = "jdbc:h2:file:" + strDatabasePath + "\\" + strDatabaseName;
	}
	
	@Override
	public void setDatabaseConnection(String strDatabaseServer, String strDatabaseName, String strDatabaseUser, String strDatabasePass) {
		super.strDatabaseServer = strDatabaseServer;
		super.strDatabaseName = strDatabaseName;
		super.strDatabaseUser = strDatabaseUser;
		super.strDatabasePass = strDatabasePass;
		
		File fileDatabase = new File("data/wasis.mv.db");
		
		String strDatabasePath = fileDatabase.getAbsoluteFile().getParentFile().getAbsolutePath();
		
		strDatabaseURL = "jdbc:h2:file:" + strDatabasePath + "\\" + strDatabaseName;
	}
	
	@Override
	public void openConnection() {
		try {
			Class.forName("org.h2.Driver");
			
			connectionString = DriverManager.getConnection(strDatabaseURL, strDatabaseUser, strDatabasePass);			
			connectionString.setAutoCommit(false);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean checkConnection() {
		try {
			Class.forName("org.h2.Driver");
			
			connectionString = DriverManager.getConnection(strDatabaseURL, strDatabaseUser, strDatabasePass);			
			connectionString.setAutoCommit(false);
			
			closeConnection();
			
			return true;
			
		} catch (ClassNotFoundException e) {
			return false;
			
		} catch(SQLException e) {
			return false;
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
			System.out.println(rsBundle.getString("commit_error"));
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
			System.out.println(rsBundle.getString("rollback_error"));
			e.printStackTrace();
		}
	}
	
	@Override
	public void initiliazeStatement() {
		strSQLCommand = new StringBuffer();
		lstParameters = new ArrayList<DatabaseParameters>();
		lgnIdentityKey = 0;
	}
	
	@Override
	public void sqlCommand(String strSQLCommand) {
		super.strSQLCommand = new StringBuffer(strSQLCommand); 
	}
	
	@Override
	public void sqlCommandAppend(String strSQLCommandAppend) {
		super.strSQLCommand.append(" " + strSQLCommandAppend);
	}
	
	@Override
	public void addParameter(String strColumnDatabase, LTDataTypes objDataType, Object objValue) {
		lstParameters.add(new DatabaseParameters(strColumnDatabase, objDataType, objValue));
	}
	
	@Override
	public int executeQuery() throws SQLException {
		int intRecordsProcessed = -1;
		
		try {
			prepStatement = connectionString.prepareStatement(strSQLCommand.toString(), Statement.RETURN_GENERATED_KEYS);
			
			addParameterValuesToQuery();
						
			intRecordsProcessed = prepStatement.executeUpdate();

			// Retorna o ID do novo registro adicionado
			ResultSet rsResult = prepStatement.getGeneratedKeys();
			if (rsResult.next()) {
				lgnIdentityKey = rsResult.getLong(1);
			}
				
			prepStatement.close();
			
		} catch (SQLException e) {
			System.out.println(prepStatement);
			throw new SQLException(e);
		}

		return intRecordsProcessed;
	}
	
	@Override
	public ResultSet executeSelectQuery() throws SQLException {
		try {
			prepStatement = connectionString.prepareStatement(strSQLCommand.toString(), ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			addParameterValuesToQuery();
			
			rsReturn = prepStatement.executeQuery();
			
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		return rsReturn;
	}
	
	@Override
	public void cancelSelectQuery() throws SQLException {
		try {
			prepStatement.cancel();
			
		} catch (SQLException e) {
			throw new SQLException(e);
		}
	}
	
	/**
	 * Adiciona os valores da lista de parâmetros a instrução SQL.
	 * 
	 * @throws SQLException
	 */
	private void addParameterValuesToQuery() throws SQLException {
		try {
			LTDataTypes objDataType;
			Object objValue;
			
			for (int indexParameter = 0; indexParameter < lstParameters.size(); indexParameter++) {
				objDataType = lstParameters.get(indexParameter).getDataType();
				objValue = lstParameters.get(indexParameter).getValue();
				
				String strValue = null;
				
				if (objValue != null) {
					strValue = objValue.toString();
				}
				
				// Integer
				if (objDataType == LTDataTypes.INTEGER) {
					int intValue = 0;
					
					if (objValue instanceof String) {
						if (strValue != null && strValue.length() > 0) {
							intValue = Integer.valueOf(strValue);
						}
						
					} else if (objValue instanceof Integer) {
						intValue = (int) objValue;
					}
					
					prepStatement.setInt(indexParameter + 1, intValue);
					
				// Long
				} else if (objDataType == LTDataTypes.LONG) {
					long lgnValue = 0;
					
					if (objValue instanceof String) {
						if (strValue != null && strValue.length() > 0) {
							lgnValue = Long.valueOf(strValue);
						}
						
					} else if (objValue instanceof Long) {
						lgnValue = (long) objValue;
					} else if (objValue instanceof Integer) {
						lgnValue = Long.valueOf(((Integer) objValue).longValue());
					}
					
					prepStatement.setLong(indexParameter + 1, lgnValue);
					
				// Double
				} else if (objDataType == LTDataTypes.DOUBLE) {
					double dblValue = 0;
					
					if (objValue instanceof String) {
						strValue = (String) objValue;
						strValue = strValue.replace(".", "");
						strValue = strValue.replace(",", ".");
						
						dblValue = Double.valueOf(strValue);
						
					} else if (objValue instanceof Double) {
						dblValue = (double) objValue;
					}
					
					prepStatement.setDouble(indexParameter + 1, dblValue);
				
				// String
				} else if (objDataType == LTDataTypes.STRING) {
					prepStatement.setString(indexParameter + 1, strValue);
					
				// Text
				} else if (objDataType == LTDataTypes.TEXT) {
					prepStatement.setString(indexParameter + 1, strValue);
					
				// Date
				} else if (objDataType == LTDataTypes.DATE) {
					Date dataValue = null;
					Timestamp timeStampValue = null;
					
					if (objValue instanceof String) {
						if (strValue != null && strValue.length() > 0) {
							dataValue = StringTransformations.setStringToDate(strValue);
							timeStampValue = new Timestamp(dataValue.getTime());
						}
						
					} else if (objValue instanceof Date) {
						dataValue = (Date) objValue;
						timeStampValue = new Timestamp(dataValue.getTime());
					}
					
					prepStatement.setTimestamp(indexParameter + 1, timeStampValue);
				
				// Boolean
				} else if (objDataType == LTDataTypes.BOOLEAN) {
					boolean blnValue = false;
					
					if (objValue instanceof String) {
						if (((String) objValue).toLowerCase().equals("true")) {
							blnValue = true;
						} else {
							blnValue = false;
						}
						
					} else if (objValue instanceof Boolean) {
						blnValue = (boolean) objValue;
					}
					
					prepStatement.setBoolean(indexParameter + 1, blnValue);
					
				// Qualquer objeto
				} else {
					prepStatement.setObject(indexParameter + 1, objValue);
				}
			}
			
		} catch (SQLException e) {
			throw new SQLException(e);
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