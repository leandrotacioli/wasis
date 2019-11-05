package br.unicamp.fnjv.wasis.main;

import java.awt.Color;
import java.awt.Frame;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.leandrotacioli.libs.LTParameters;

import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;

/**
 * Carrega os parâmetros principais do Wasis.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 27/Out/2017
 */
public class WasisParameters {
	private static WasisParameters objWasisParameters;
	
	private ResourceBundle rsBundle;
	
	private String strWasisUser;
	private String strLanguage;
	private String strLastFilePath;
	private boolean blnFullWaveform;
	private String strSpectrogramColorDisplay;

	private boolean blnDatabaseStatus;
	private String strDatabaseEngine;
	private String strDatabaseServer;
	private String strDatabaseName;
	private String strDatabaseUser;
	private String strDatabasePassword;
	
	private Date dateInstance;
	
	private final Color COLOR_COMPONENT = new Color(225, 225, 225);
	
	public static final Color COLOR_BACKGROUND = new Color(225, 225, 225);
	public static final Color COLOR_BACKGROUND_MAIN = new Color(25, 25, 25);
	
	public static final Color COLOR_BACKGROUND_PANEL_TOP_GRADIENT = new Color(190, 190, 190);
	public static final Color COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT = new Color(240, 240, 240);
	
	public static final String LANGUAGUE_PORTUGUESE = "Português";
	public static final String LANGUAGUE_ENGLISH = "English";
	
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
	 * Retorna o usuário que está utilizando o WASIS.
	 * 
	 * @return strLanguage
	 */
	public String getLanguage() {
		return strLanguage;
	}
	
	/**
	 * Retorna a linguagem está utilizando o WASIS.
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
	 * Altera o caminho do último arquivo de áudio carregado no sistema.
	 * 
	 * @param strLastFilePath
	 */
	protected void setLastFilePath(String strLastFilePath) {
		this.strLastFilePath = strLastFilePath;
	}
	
	/**
	 * Retorna o tipo de visualização do oscilograma.
	 * 
	 * @return blnFullWaveform
	 */
	public boolean getFullWaveform() {
		return blnFullWaveform;
	}
	
	/**
	 * Altera o tipo de visualização do oscilograma.
	 * 
	 * @return blnFullWaveform
	 */
	public void setFullWaveform(boolean blnFullWaveform) {
		this.blnFullWaveform = blnFullWaveform;
	}
	
	/**
	 * Retorna o mapa de cores para a visualização do espectrograma.
	 * 
	 * @return strSpectrogramColorDisplay
	 */
	public String getSpectrogramColorDisplay() {
		return strSpectrogramColorDisplay;
	}

	/**
	 * Altera o mapa de cores para a visualização do espectrograma.
	 * 
	 * @param strSpectrogramColorDisplay
	 */
	public void setSpectrogramColorDisplay(String strSpectrogramColorDisplay) {
		this.strSpectrogramColorDisplay = strSpectrogramColorDisplay;
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
	 * Retorna a engine de banco de dados do WASIS.
	 * 
	 * @return strDatabaseEngine
	 */
	public String getDatabaseEngine() {
		return strDatabaseEngine;
	}
	
	/**
	 * Retorna o servidor do banco de dados do WASIS.
	 * 
	 * @return strDatabaseServer
	 */
	public String getDatabaseServer() {
		return strDatabaseServer;
	}
	
	/**
	 * Retorna o nome do banco de dados do WASIS.
	 * 
	 * @return strDatabaseName
	 */
	public String getDatabaseName() {
		return strDatabaseName;
	}

	/**
	 * Retorna o usuário do banco de dados do WASIS.
	 * 
	 * @return strDatabaseUser
	 */
	public String getDatabaseUser() {
		return strDatabaseUser;
	}

	/**
	 * Retorna a senha para acesso ao banco de dados do WASIS.
	 * 
	 * @return strDatabasePassword
	 */
	public String getDatabasePassword() {
		return strDatabasePassword;
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
		this.dateInstance = new Date();
		
		LTParameters.getInstance().setColorComponentPanelBackground(COLOR_COMPONENT);
		
		loadParameters();
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
	 * Carrega os parâmetros do WASIS.
	 */
	protected void loadParameters() {
		try {
			// Lê o arquivo XML
			File fileXML = new File("WASIS-Parameters.xml");
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document document = dBuilder.parse(fileXML);
			
			NodeList nList = document.getElementsByTagName("Config");
			Node node;
			Element element;
			
			for (int index = 0; index < nList.getLength(); index++) {
				node = nList.item(index);
		  
				if (node.getNodeType() == Node.ELEMENT_NODE) {
					element = (Element) node;
					
					strWasisUser = element.getElementsByTagName("WasisUser").item(0).getTextContent();
					strLanguage = element.getElementsByTagName("Language").item(0).getTextContent();
					strLastFilePath = element.getElementsByTagName("LastFilePath").item(0).getTextContent();
					blnFullWaveform = Boolean.parseBoolean(element.getElementsByTagName("FullWaveform").item(0).getTextContent());
					strSpectrogramColorDisplay = element.getElementsByTagName("SpectrogramColorDisplay").item(0).getTextContent();
					strDatabaseEngine = element.getElementsByTagName("DatabaseEngine").item(0).getTextContent();
					strDatabaseServer = element.getElementsByTagName("DatabaseServer").item(0).getTextContent();
					strDatabaseName = element.getElementsByTagName("DatabaseName").item(0).getTextContent();
					strDatabaseUser = element.getElementsByTagName("DatabaseUser").item(0).getTextContent();
					strDatabasePassword = element.getElementsByTagName("DatabasePassword").item(0).getTextContent();
				}
			}
			
			// A linguagem default do WASIS é inglês
			String strLocaleLanguage = "en";
			String strLocaleCountry = "US";
			
			// Verifica se é português
			if (strLanguage.toLowerCase().equals("português")) {
				strLocaleLanguage = "pt";
				strLocaleCountry = "BR";
			}
			
			this.rsBundle = ResourceBundle.getBundle("br.unicamp.fnjv.wasis.internationalization.LabelBundles", new Locale(strLocaleLanguage, strLocaleCountry));

			LTParameters.getInstance().setLocale(strLocaleLanguage, strLocaleCountry);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Salva os parâmetros no arquivo XML.
	 */
	protected void saveParameters() {
		try {
			// XML
			DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			
			// Parameters
			Document doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("WASIS-Parameters");
			doc.appendChild(rootElement);
			
			{
				// Config
				Element configElement = doc.createElement("Config");
				rootElement.appendChild(configElement);
				
				{
					// Wasis User
					Element eWasisUser = doc.createElement("WasisUser");
					eWasisUser.appendChild(doc.createTextNode(strWasisUser));
					configElement.appendChild(eWasisUser);
					
					// Language
					Element eLanguage = doc.createElement("Language");
					eLanguage.appendChild(doc.createTextNode(strLanguage));
					configElement.appendChild(eLanguage);
					
					// Last File Path
					Element eLastFilePath = doc.createElement("LastFilePath");
					eLastFilePath.appendChild(doc.createTextNode(strLastFilePath));
					configElement.appendChild(eLastFilePath);
					
					// FullWaveform
					Element eFullWaveform = doc.createElement("FullWaveform");
					eFullWaveform.appendChild(doc.createTextNode("" + blnFullWaveform));
					configElement.appendChild(eFullWaveform);
					
					// Spectrogram Color Display
					Element eSpectrogramColorDisplay = doc.createElement("SpectrogramColorDisplay");
					eSpectrogramColorDisplay.appendChild(doc.createTextNode(strSpectrogramColorDisplay));
					configElement.appendChild(eSpectrogramColorDisplay);
					
					// Database Engine
					Element eDatabaseEngine = doc.createElement("DatabaseEngine");
					eDatabaseEngine.appendChild(doc.createTextNode(strDatabaseEngine));
					configElement.appendChild(eDatabaseEngine);
					
					// Database Server
					Element eDatabaseServer = doc.createElement("DatabaseServer");
					eDatabaseServer.appendChild(doc.createTextNode(strDatabaseServer));
					configElement.appendChild(eDatabaseServer);
					
					// Database Name
					Element eDatabaseName = doc.createElement("DatabaseName");
					eDatabaseName.appendChild(doc.createTextNode(strDatabaseName));
					configElement.appendChild(eDatabaseName);
					
					// Database User
					Element eDatabaseUser = doc.createElement("DatabaseUser");
					eDatabaseUser.appendChild(doc.createTextNode(strDatabaseUser));
					configElement.appendChild(eDatabaseUser);
					
					// Database Password
					Element eDatabasePassword = doc.createElement("DatabasePassword");
					eDatabasePassword.appendChild(doc.createTextNode(strDatabasePassword));
					configElement.appendChild(eDatabasePassword);
				}
			}

			// Salva arquivo XML
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
			
			DOMSource domSource = new DOMSource(doc);
			StreamResult streamResult = new StreamResult(new File("WASIS-Parameters.xml"));
			
			transformer.transform(domSource, streamResult);
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Verifica se a conexão com o banco de dados está ativa.
	 */
	protected void checkDatabaseConnection() {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			blnDatabaseStatus = objDatabaseConnection.checkConnection();
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