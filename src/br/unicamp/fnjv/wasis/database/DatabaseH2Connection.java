package br.unicamp.fnjv.wasis.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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

/**
 * Fornece acesso a um banco de dados H2. <br>
 * É possível realizar transações de inclusão, 
 * atualização, deleção e consulta de dados.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 06/Out/2014
 */
public class DatabaseH2Connection extends DatabaseConnection {
	//private String strDatabaseServer;
	private String strDatabaseName; 
	private String strDatabaseUser; 
	private String strDatabasePass;
	
	private ResourceBundle rsBundle = LTParameters.getInstance().getBundle();
	
	private Connection connectionString;
	private PreparedStatement prepStatement;
	private ResultSet rsReturn;
	
	private String strSQLCommand;
	
	private ArrayList<DatabaseSQLParameters> lstParameters;

	private long lgnIdentityKey;        // ID do registro pré-gravado no banco de dados
	
	/**
	 * Fornece acesso a um banco de dados H2. <br>
	 * É possível realizar transações de inclusão, 
	 * atualização, deleção e consulta de dados.
	 */
	protected DatabaseH2Connection() {
		super();
		
		//this.strDatabaseServer = "localhost";
		this.strDatabaseName = "wasis";
		this.strDatabaseUser = "sa";
		this.strDatabasePass = "Root@Wasis2014";
	}
	
	@Override
	public void setDatabaseConnection(String strDatabaseServer, String strDatabaseName, String strDatabaseUser, String strDatabasePass) {
		//this.strDatabaseServer = strDatabaseServer;
		this.strDatabaseName = strDatabaseName;
		//this.strDatabaseUser = strDatabaseUser;
		//this.strDatabasePass = strDatabasePass;
	}
	
	@Override
	public void openConnection() {
		File fileDatabase = new File("data/wasis.h2.db");
		
		String strDatabasePath = fileDatabase.getAbsoluteFile().getParentFile().getAbsolutePath();
		
		String strDatabaseURL = "jdbc:h2:file:" + strDatabasePath + "\\" + strDatabaseName;
		
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
		strSQLCommand = null;
		lstParameters = new ArrayList<DatabaseSQLParameters>();
		lgnIdentityKey = 0;
	}
	
	@Override
	public void sqlCommand(String strSQLCommand) {
		this.strSQLCommand = strSQLCommand; 
	}
	
	@Override
	public void sqlCommandAppend(String strSQLCommandAppend) {
		this.strSQLCommand = this.strSQLCommand + " " + strSQLCommandAppend; 
	}
	
	@Override
	public void addParameter(String strColumnDatabase, LTDataTypes objDataType, Object objValue) {
		lstParameters.add(new DatabaseSQLParameters(strColumnDatabase, objDataType, objValue));
	}
	
	@Override
	public int executeQuery() throws SQLException {
		int intRecordsProcessed = -1;
		
		try {
			prepStatement = connectionString.prepareStatement(strSQLCommand, Statement.RETURN_GENERATED_KEYS);
			
			LTDataTypes objDataType;
			Object objValue;
			
			String strValue;
			int intValue;
			long lgnValue;
			double dblValue;
			Date dataValue;
			Timestamp timeStampValue;
			boolean blnValue;
			
			// Verifica se há parâmetros a ser adicionados na instrução SQL
			for (int intIndex = 0; intIndex < lstParameters.size(); intIndex++) {
				
				objDataType = lstParameters.get(intIndex).getDataType();
				objValue = lstParameters.get(intIndex).getValue();
				
				// Integer
				if (objDataType == LTDataTypes.INTEGER) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						if (strValue.equals("")) {
							prepStatement.setInt(intIndex + 1, 0);
						} else {
							intValue = Integer.valueOf(strValue);
							prepStatement.setInt(intIndex + 1, intValue);
						}
						
					} else if (objValue instanceof Integer) {
						intValue = (int) objValue;
						prepStatement.setInt(intIndex + 1, intValue);
					}
					
				// Long
				} else if (objDataType == LTDataTypes.LONG) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						if (strValue.equals("")) {
							prepStatement.setInt(intIndex + 1, 0);
						} else {
							lgnValue = Long.valueOf(strValue);
							prepStatement.setLong(intIndex + 1, lgnValue);
						}
						
					} else if (objValue instanceof Long) {
				        lgnValue = (long) objValue;
				        prepStatement.setLong(intIndex + 1, lgnValue);
					
					} else if (objValue instanceof Integer) {
						lgnValue = (long) objValue;
						prepStatement.setLong(intIndex + 1, lgnValue);
					}
					
				// Double
				} else if (objDataType == LTDataTypes.DOUBLE) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						
						if (!LTParameters.getInstance().getDecimalMark().equals("COMMA")) {
							strValue = strValue.replace(",", "");
						} else {
							strValue = strValue.replace(".", "");
							strValue = strValue.replace(",", ".");
						}
						
						dblValue = Double.valueOf(strValue);
						prepStatement.setDouble(intIndex + 1, dblValue);
						
					} else if (objValue instanceof Double) {
						dblValue = (double) objValue;
						strValue = String.valueOf(dblValue);

						dblValue = Double.valueOf(strValue);
						prepStatement.setDouble(intIndex + 1, dblValue);
					}
				
				// String
				} else if (objDataType == LTDataTypes.STRING) {
					strValue = (String) objValue;
					prepStatement.setString(intIndex + 1, strValue);
					
				// Text
				} else if (objDataType == LTDataTypes.TEXT) {
					strValue = (String) objValue;
					prepStatement.setString(intIndex + 1, strValue);
					
				// Date
				} else if (objDataType == LTDataTypes.DATE) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						
						if (strValue.equals("") || strValue.substring(0, 1).equals(" ")) {
							prepStatement.setTimestamp(intIndex + 1, null);
						} else {
							dataValue = StringTransformations.setStringToDate(strValue);
							timeStampValue = new Timestamp(dataValue.getTime());
							prepStatement.setTimestamp(intIndex + 1, timeStampValue);
						}
				        
					} else if (objValue instanceof Date) {
						dataValue = (Date) objValue;
						timeStampValue = new Timestamp(dataValue.getTime());
				        prepStatement.setTimestamp(intIndex + 1, timeStampValue);
				        
					} else {
			            prepStatement.setTimestamp(intIndex + 1, null);
			            
					}
				
				// Boolean
				} else if (objDataType == LTDataTypes.BOOLEAN) {
					blnValue = (boolean) objValue;
					prepStatement.setBoolean(intIndex + 1, blnValue);
				}
				
			}
						
			intRecordsProcessed = prepStatement.executeUpdate();

			// Retorna o ID do novo registro adicionado
			ResultSet rs = prepStatement.getGeneratedKeys();
			if (rs.next()) {
				lgnIdentityKey = rs.getLong(1);
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
			prepStatement = connectionString.prepareStatement(strSQLCommand, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
			
			LTDataTypes objDataType;
			Object objValue;
			
			String strValue;
			int intValue;
			long lgnValue;
			double dblValue;
			Date dataValue;
			Timestamp timeStampValue;
			boolean blnValue;
			
			// Verifica se há parâmetros a ser adicionados na instrução SQL
			for (int intIndex = 0; intIndex < lstParameters.size(); intIndex++) {
				
				objDataType = lstParameters.get(intIndex).getDataType();
				objValue = lstParameters.get(intIndex).getValue();
				
				// Integer
				if (objDataType == LTDataTypes.INTEGER) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						if (strValue.equals("")) {
							prepStatement.setInt(intIndex + 1, 0);
						} else {
							intValue = Integer.valueOf(strValue);
							prepStatement.setInt(intIndex + 1, intValue);
						}
						
					} else if (objValue instanceof Integer) {
						intValue = (int) objValue;
						prepStatement.setInt(intIndex + 1, intValue);
					}
					
				// Long
				} else if (objDataType == LTDataTypes.LONG) {
					if (objValue instanceof String) {
						strValue = (String) objValue;						
						if (strValue.equals("")) {
							prepStatement.setInt(intIndex + 1, 0);
						} else {
							lgnValue = Long.valueOf(strValue);
							prepStatement.setLong(intIndex + 1, lgnValue);
						}
						
					} else if (objValue instanceof Long) {
						lgnValue = (long) objValue;
						prepStatement.setLong(intIndex + 1, lgnValue);
						
					} else if (objValue instanceof Integer) {
						lgnValue = Long.valueOf(((Integer) objValue).longValue());
						prepStatement.setLong(intIndex + 1, lgnValue);
					}
					
				// Double
				} else if (objDataType == LTDataTypes.DOUBLE) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						strValue = strValue.replace(".", "");
						strValue = strValue.replace(",", ".");
						
						dblValue = Double.valueOf(strValue);
						prepStatement.setDouble(intIndex + 1, dblValue);
						
					} else if (objValue instanceof Double) {
						dblValue = (double) objValue;
						strValue = String.valueOf(dblValue);

						dblValue = Double.valueOf(strValue);
						prepStatement.setDouble(intIndex + 1, dblValue);
					}
				
				// String
				} else if (objDataType == LTDataTypes.STRING) {
					strValue = (String) objValue;
					prepStatement.setString(intIndex + 1, strValue);
					
				// Text
				} else if (objDataType == LTDataTypes.TEXT) {
					strValue = (String) objValue;
					prepStatement.setString(intIndex + 1, strValue);
					
				// Date
				} else if (objDataType == LTDataTypes.DATE) {
					if (objValue instanceof String) {
						strValue = (String) objValue;
						
						if (strValue.equals("") || strValue.substring(0, 1).equals(" ")) {
							prepStatement.setTimestamp(intIndex + 1, null);
						} else {
							dataValue = StringTransformations.setStringToDate(strValue);
							timeStampValue = new Timestamp(dataValue.getTime());
							prepStatement.setTimestamp(intIndex + 1, timeStampValue);
						}
				        
					} else if (objValue instanceof Date) {
						dataValue = (Date) objValue;
						timeStampValue = new Timestamp(dataValue.getTime());
				        prepStatement.setTimestamp(intIndex + 1, timeStampValue);
				    
					} else {
						prepStatement.setTimestamp(intIndex + 1, null);
					}
				
				// Boolean
				} else if (objDataType == LTDataTypes.BOOLEAN) {
					blnValue = (boolean) objValue;
					prepStatement.setBoolean(intIndex + 1, blnValue);
				}
				
			}
			
			rsReturn = prepStatement.executeQuery();
			
		} catch (SQLException e) {
			throw new SQLException(e);
		}
		
		return rsReturn;
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