package br.unicamp.fnjv.wasis.main;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import br.unicamp.fnjv.wasis.database.DatabaseConnection;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.LTParameters;

/**
 * Carrega os parâmetros principais do Wasis.
 * 
 * @author Leandro Tacioli
 * @version 1.2 - 31/Mar/2015 
 */
public class WasisParameters {
	private static WasisParameters objWasisParameters;
	
	private ResourceBundle rsBundle;
	
	private boolean blnDatabaseStatus;
	
	private String strWasisUser;
	private String strLastFilePath;
	//private String strLanguage;
	
	//private Locale locale;
	
	private Date dateInstance;
	
	
	private final Color COLOR_COMPONENT = new Color(225, 225, 225);
	
	public static final Color COLOR_BACKGROUND = new Color(225, 225, 225);
	public static final Color COLOR_BACKGROUND_MAIN = new Color(25, 25, 25);
	
	public static final Color COLOR_BACKGROUND_PANEL_TOP_GRADIENT = new Color(190, 190, 190);
	public static final Color COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT = new Color(240, 240, 240);
	
	/**
	 * Pasta de arquivos temporários.
	 */
	public final String TEMPORARY_FOLDER = "temp" + File.separator;
	
	/**
	 * Retorna o pacote de linguagens do sistema.
	 * 
	 * @return rsBundle
	 */
	public ResourceBundle getBundle() {
		return rsBundle;
	}
	
	/**
	 * Retorna o status de conexão com o banco de dados. <br>
	 * <br>
	 * <i>True</i> - Conexão estabelecida
	 * <br>
	 * <i>False</i> - Conexão falha
	 * 
	 * @return blnDatabaseStatus
	 */
	public boolean getDatabaseStatus() {
		return blnDatabaseStatus;
	}
	
	/**
	 * Retorna o usuário que está utilizando o WASIS.
	 * 
	 * @return strWasisUser
	 */
	public String getWasisUser() {
		return strWasisUser;
	}
	
	/**
	 * Retorna o caminho do último arquivo de áudio carregado no sistema.
	 * 
	 * @return strLastFilePath
	 */
	public String getLastFilePath() {
		return strLastFilePath;
	}
	
	/**
	 * Retorna a data e hora que está rodando a instância atual.
	 * 
	 * @return dateInstance
	 */
	public Date getDateInstance() {
		return dateInstance;
	}
		
	/**
	 * Carrega os parâmetros principais do Wasis.
	 */
	private WasisParameters() {
		this.rsBundle = ResourceBundle.getBundle("br.unicamp.fnjv.wasis.internationalization.LabelBundles", LTParameters.getInstance().getLocale());
		this.dateInstance = new Date();

		LTParameters.getInstance().setColorComponentPanelBackground(COLOR_COMPONENT);
		
		loadDatabaseParameters();
	}
	
	/**
	 * Cria e retorna uma nova instância para a classe (Singleton).
	 * 
	 * @return objWasisParameters
	 */
	public static synchronized WasisParameters getInstance() {
		if (objWasisParameters == null) {
			objWasisParameters = new WasisParameters();
		}
		
		return objWasisParameters;
	}
	
	/**
	 * Carrega os parâmetros existentes no banco de dados.
	 */
	protected void loadDatabaseParameters() {
		this.blnDatabaseStatus = false;
		this.strWasisUser = "wasis_user";
		this.strLastFilePath = "C:/";
		//this.strLanguage = "English";
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT wasis_user, last_file_path, language ");
			objDatabaseConnection.sqlCommandAppend("FROM wasis_parameters ");
			objDatabaseConnection.sqlCommandAppend("WHERE id = 1 ");
			
			ResultSet rsParameters = objDatabaseConnection.executeSelectQuery();
			
			while (rsParameters.next()) {
				this.blnDatabaseStatus = true;
				this.strWasisUser = rsParameters.getString("wasis_user");
				this.strLastFilePath = rsParameters.getString("last_file_path");
				//this.strLanguage = rsParameters.getString("language");
				
				// A linguagem default do WASIS é inglês
				//String strLocaleLanguage = "en";
				//String strLocaleCountry = "US";
				//Locale localeTemp = new Locale(strLocaleLanguage, strLocaleCountry);
				
				// Verifica se é português
				//if (strLanguage.toLowerCase().equals("portugues")) {
				//	strLocaleLanguage = "pt";
				//	strLocaleCountry = "BR";
					
				//	localeTemp = new Locale(strLocaleLanguage, strLocaleCountry);
				//}
				
				// Verifica se houve alteração na linguagem do WASIS
				//if (this.locale != localeTemp) {
				//	LTParameters.getInstance().setLocale(strLocaleLanguage, strLocaleCountry);
				//}
			}

		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Altera o caminho do último arquivo de áudio
	 * carregado no sistema.
	 * 
	 * @param strLastFilePath
	 */
	protected void setLastFilePath(String strLastFilePath) {
		this.strLastFilePath = strLastFilePath;
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE wasis_parameters ");
			objDatabaseConnection.sqlCommandAppend("SET last_file_path = ?");
			objDatabaseConnection.sqlCommandAppend("WHERE id = 1");
			objDatabaseConnection.addParameter("last_file_path", LTDataTypes.STRING, strLastFilePath);
			objDatabaseConnection.executeQuery();
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}

	/**
	 * Retorna o ícone do WASIS que será utilizado nas telas.
	 *
	 * @return imageIcon
	 */
	public Image getWasisIcon() {
		Image image = null;
		
		try {
			File sourceimage = new File("res/images/logo_wasis.png");
			image = ImageIO.read(sourceimage);
		} catch (IOException e) {
			e.printStackTrace();
		}

		return image;
	}
	
	/**
	 * Retorna o frame principal do WASIS.
	 */
	public Frame getWasisFrame() {
		Frame[] frameWasis = Wasis.getFrames();

		return frameWasis[0];
	}
}