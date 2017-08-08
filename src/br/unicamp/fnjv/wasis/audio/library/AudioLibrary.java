package br.unicamp.fnjv.wasis.audio.library;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;

import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporary;
import br.unicamp.fnjv.wasis.database.DatabaseConnection;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.multimidia.AudioFile;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanelRounded;

/**
 * Classe responsável por todo o controle da biblioteca de áudio.
 * 
 * @author Leandro Tacioli
 * @version 3.1 - 03/Mar/2017
 */
public class AudioLibrary extends WasisPanelRounded { 
	private static final long serialVersionUID = 3945481882108193120L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
    private long lgnId;
    private String strName;
    private String strDescription;
    private String strObservations;
	
    private JLabel lblLibrary;
    
    private JButton btnUpdate;
    
	private JList<String> listAudioLibrary;
    private DefaultListModel<String> listModelAudioLibrary;
    
    private JScrollPane scrollPaneAudioLibrary;
    
    /** Status de um arquivo de áudio já carregado anteriormente. */
    private boolean blnAudioAlreadyLoaded;
    
    /** Status de uma biblioteca de áudio já ter sido carregada anteriormente. */
    private boolean blnAudioLibraryAlreadyLoaded;
    
    /** Nome do arquivo de áudio que está carregado na biblioteca. */
    private String strAudioFilePathLoaded;
    
    /** Nome do arquivo de áudio que está sendo selecionado na biblioteca. */
    private String strAudioFilePathSelected;
    
    /** Índice do arquivo de áudio que está sendo selecionado na biblioteca. */
    private int intAudioSelectedIndex;
    
    private JPopupMenu popupMenuLibrary;
    
    private JMenuItem popupMenuItemLibraryOpenAudio;
	private JMenuItem popupMenuItemLibraryCloseAudio;
	
    private MouseListener popupMenuLibraryListener;
    
    private Collection<Object> collectionListenerAudioLibrary;
    
    /**
     * Retorna o ID da biblioteca de áudio.
     * 
     * @return lgnId
     */
    public long getId() {
		return lgnId;
	}

	/**
	 * Retorna o nome da biblioteca de áudio.
	 * 
	 * @return strName
	 */
	public String getName() {
		return strName;
	}
	
	/**
	 * Altera o nome da biblioteca de áudio.
	 * 
	 * @param strName
	 */
	public void setName(String strName) {
		this.strName = strName;
	}
	
	/**
	 * Retorna a descrição da biblioteca de áudio.
	 * 
	 * @return strDescription
	 */
	public String getDescription() {
		return strDescription;
	}

	/**
	 * Altera a descrição da biblioteca de áudio.
	 * 
	 * @param strDescription
	 */
	public void setDescription(String strDescription) {
		this.strDescription = strDescription;
	}

	/**
	 * Retorna as observações da biblioteca de áudio.
	 * 
	 * @return strDescription
	 */
	public String getObservations() {
		return strObservations;
	}

	/**
	 * Altera as observações da biblioteca de áudio.
	 * 
	 * @param strObservations
	 */
	public void setObservations(String strObservations) {
		this.strObservations = strObservations;
	}
	
	/**
     * Retorna o status de uma biblioteca de áudio já ter sido carregada anteriormente.<br>
     * <br>
     * Esse método será utilizado no método <i>loadAudioLibraryData()</i> 
     * da classe <i>SaveAudioLibrary</i> para verificar se alguma biblioteca 
     * de áudio já está sendo carregada, sendo assim não há necessidade de 
     * carregar os dados dos áudios quando solicitar para inserir nova biblioteca.
     * 
     * @return blnAudioLibraryAlreadyLoaded
     */
    public boolean getAudioLibraryAlreadyLoaded() {
		return blnAudioLibraryAlreadyLoaded;
	}
    
    /**
	 * Retorna o arquivo de áudio que está sendo carregado da biblioteca de áudio.
	 * 
	 * @return strAudioFilePathLoaded
	 */
	public String getAudioFilePathLoaded() {
		return strAudioFilePathLoaded;
	}

    /**
	 * Retorna o arquivo de áudio que está sendo selecionado na biblioteca de áudio.
	 * 
	 * @return strAudioFilePathSelected
	 */
	public String getAudioFilePathSelected() {
		return strAudioFilePathSelected;
	}
	
	/**
	 * Retorna o modelo da biblioteca de áudio.
	 * 
	 * @return listModelAudioLibrary
	 */
	public DefaultListModel<String> getListModelAudioLibrary() {
		return listModelAudioLibrary;
	}
    
    /**
     * Classe responsável por todo o controle da biblioteca de áudio.
     */
	public AudioLibrary() {
		super(null);

		loadComponents();
		createPopupMenuAudioLibrary();
	}
	
	/**
	 * Carrega os componentes que será utilizados na biblioteca de áudio.
	 */
	private void loadComponents() {
		final int BUTTON_UPDATE_WIDTH = 25;
		final int BUTTON_UPDATE_HEIGHT = 25;
		
		lblLibrary = new JLabel(rsBundle.getString("audio_library_label_description"));
		lblLibrary.setVerticalAlignment(SwingConstants.CENTER);
		lblLibrary.setHorizontalAlignment(SwingConstants.CENTER);
		lblLibrary.setMinimumSize(new Dimension(this.getWidth() - BUTTON_UPDATE_WIDTH, BUTTON_UPDATE_HEIGHT));
		
		btnUpdate = new JButton();
		btnUpdate.setToolTipText(rsBundle.getString("audio_library_update"));
		btnUpdate.setIcon(new ImageIcon("res/images/update.png"));
		btnUpdate.setFocusPainted(false);
		btnUpdate.setFocusable(false);
		btnUpdate.setContentAreaFilled(false);
		btnUpdate.setMinimumSize(new Dimension(BUTTON_UPDATE_WIDTH, BUTTON_UPDATE_HEIGHT));
		btnUpdate.setMaximumSize(new Dimension(BUTTON_UPDATE_WIDTH, BUTTON_UPDATE_HEIGHT));
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				activateCellRenderer(true);
				
				listAudioLibrary.updateUI();
			}
		});
		
		collectionListenerAudioLibrary = new ArrayList<Object>();
		
		listModelAudioLibrary = new DefaultListModel<String>();
		
		listAudioLibrary = new JList<String>(listModelAudioLibrary);
		listAudioLibrary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listAudioLibrary.setOpaque(false);
		
		this.activateCellRenderer(true);
		
		scrollPaneAudioLibrary = new JScrollPane(listAudioLibrary);
		scrollPaneAudioLibrary.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPaneAudioLibrary.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPaneAudioLibrary.setBorder(null);
		scrollPaneAudioLibrary.setOpaque(false);
		scrollPaneAudioLibrary.getViewport().setOpaque(false);

		// ********************************************************************************************
		// Configurações do painel
		this.setLayout(new MigLayout("insets 1", "[grow] 0 []", "[25.00] 0 [grow]"));
		
		this.add(lblLibrary, "cell 0 0, align center, gap 10 2 0 0");
		this.add(btnUpdate, "cell 1 0, gap 0 2 0 0");
		this.add(scrollPaneAudioLibrary, "cell 0 1 2 1, grow, gap 6 5 0 0");
	}
	
	/**
	 * Reseta os valores padrões da biblioteca de áudio.
	 */
	public void resetValues() {
		lgnId = 0;
		strName = null;
		strObservations = null;
	}
	
	/**
	 * Ativa/desativa a renderização da lista da biblioteca de áudio.<br>
	 * 
	 * @param blnActivate
	 * <br>
	 * <i>True</i> - Habilita a renderização
	 * <br>
	 * <i>False</i> - Desabilita a renderização
	 */
	public void activateCellRenderer(boolean blnActivate) {
		if (listAudioLibrary != null) {
			if (blnActivate) {
				listAudioLibrary.setCellRenderer(new AudioLibraryRenderer(AudioLibrary.this));
			} else {
				listAudioLibrary.setCellRenderer(null);
			}
		}
	}
	
	/**
	 * Abre uma biblioteca de áudio já existente no banco de dados.
	 * 
	 * @param intId - ID da biblioteca de áudio
	 */
	public void openAudioLibrary(long lgnId) {
		this.lgnId = lgnId;
		this.blnAudioLibraryAlreadyLoaded = true;
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();

		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT library_name ");
			objDatabaseConnection.sqlCommandAppend("FROM libraries ");
			objDatabaseConnection.sqlCommandAppend("WHERE id = ?");
			objDatabaseConnection.addParameter("id", LTDataTypes.LONG, lgnId);
			objDatabaseConnection.executeSelectQuery();
			
			strName = (String) objDatabaseConnection.getFirstValue(LTDataTypes.STRING);
			
			lblLibrary.setText(rsBundle.getString("audio_library_label_description") + ": " + strName);
			lblLibrary.setToolTipText(rsBundle.getString("audio_library_label_description") + ": " + strName);
			
			loadAudioLibraryRecords();
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Carrega os registros de áudio existentes na biblioteca.
	 */
	public void loadAudioLibraryRecords() {
		blnAudioAlreadyLoaded = false;
		
		clearAudioLibrary();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();

		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT audio_file_path, audio_file_name ");
			objDatabaseConnection.sqlCommandAppend("FROM libraries_audio_files ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY audio_file_position ");
			objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);

		    ResultSet rsAudioLibraryFiles = objDatabaseConnection.executeSelectQuery();
		    
	    	// Caso seja a biblioteca de amostra do sistema, os caminhos dos arquivos 
	    	// serão alterados para o caminho onde está a pasta do sistema.
			// A biblioteca de amostra tem ID = 1 no banco de dados.
			File fileFnjv = new File("audio_samples/audio");
			String strFnjvPath = fileFnjv.getAbsoluteFile().getParentFile().getAbsolutePath();
			
		    while (rsAudioLibraryFiles.next()) {
		    	
		    	// Biblioteca de amostra
		    	if (lgnId == 1) {
		    		
		    		// Arquivos adicionados pelo usuário na biblioteca de amostras
		    		if (rsAudioLibraryFiles.getString(2) == null) {
		    			listModelAudioLibrary.addElement(rsAudioLibraryFiles.getString("audio_file_path"));	
		    			
		    		// Arquivos originais da biblioteca de amostras
		    		} else {
		    			listModelAudioLibrary.addElement(strFnjvPath + "\\" + rsAudioLibraryFiles.getString("audio_file_name"));
		    		}
		    	
		    	// Bibliotecas do usuário
		    	} else {
		    		listModelAudioLibrary.addElement(rsAudioLibraryFiles.getString("audio_file_path"));
		    	}
		    }
		    
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Salva os dados da biblioteca de áudio no banco de dados.
	 * 
	 * @param dialogOwner
	 */
	public void saveAudioLibraryData(JDialog dialogOwner) {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("INSERT INTO libraries (library_name, library_description, observations) ");
			objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
			objDatabaseConnection.addParameter("library_name", LTDataTypes.STRING, strName);
			objDatabaseConnection.addParameter("library_description", LTDataTypes.STRING, strDescription);
			objDatabaseConnection.addParameter("observations", LTDataTypes.STRING, strObservations);
			objDatabaseConnection.executeQuery();
			
			lgnId = objDatabaseConnection.getIdentityKey();
			
			for (int indexAudioFile = 0; indexAudioFile < listModelAudioLibrary.getSize(); indexAudioFile++) {
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("INSERT INTO libraries_audio_files (fk_library, audio_file_path, audio_file_position) ");
				objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
				objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
				objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, listModelAudioLibrary.getElementAt(indexAudioFile).toString());
				objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.INTEGER, indexAudioFile);
				objDatabaseConnection.executeQuery();
			}
			
			objDatabaseConnection.commitTransaction();
			
			blnAudioLibraryAlreadyLoaded = true;
			
			lblLibrary.setText(rsBundle.getString("audio_library_label_description") + ": " + strName);
			lblLibrary.setToolTipText(rsBundle.getString("audio_library_label_description") + ": " + strName);
			
			WasisMessageBox.showMessageDialog(rsBundle.getString("operation_completed"), WasisMessageBox.INFORMATION_MESSAGE);

		} catch (Exception e) {
			e.printStackTrace();
			WasisMessageBox.showMessageDialog(rsBundle.getString("error_saving_data"), WasisMessageBox.ERROR_MESSAGE);
		
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Atualiza os dados da biblioteca de áudio no banco de dados.
	 */
	public void updateAudioLibraryData(JDialog dialogOwner) {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE libraries ");
			objDatabaseConnection.sqlCommandAppend("SET library_name = ?, library_description = ?, observations = ? ");
			objDatabaseConnection.sqlCommandAppend("WHERE id = ? ");
			objDatabaseConnection.addParameter("library_name", LTDataTypes.STRING, strName);
			objDatabaseConnection.addParameter("library_description", LTDataTypes.STRING, strDescription);
			objDatabaseConnection.addParameter("observations", LTDataTypes.STRING, strObservations);
			objDatabaseConnection.addParameter("id", LTDataTypes.LONG, lgnId);
			objDatabaseConnection.executeQuery();
			objDatabaseConnection.commitTransaction();

			lblLibrary.setText(rsBundle.getString("audio_library_label_description") + ": " + strName);
			lblLibrary.setToolTipText(rsBundle.getString("audio_library_label_description") + ": " + strName);

			WasisMessageBox.showMessageDialog(rsBundle.getString("operation_completed"), WasisMessageBox.INFORMATION_MESSAGE);
			
		} catch (Exception e) {
			e.printStackTrace();
			WasisMessageBox.showMessageDialog(rsBundle.getString("error_saving_data"), WasisMessageBox.ERROR_MESSAGE);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Adiciona um arquivo de áudio à biblioteca de áudio.
	 * 
	 * @param strAudioFilePath - Arquivo de áudio a ser adicionado
	 */
	public void addAudioFileToAudioLibrary(String strAudioFilePath) {
		this.strAudioFilePathLoaded = strAudioFilePath;

	    deleteAudioFileFromAudioLibrary(strAudioFilePath);
	    insertAudioFileIntoAudioLibrary(strAudioFilePath);
	}
	
	/**
	 * Adiciona uma lista de arquivos de áudio biblioteca de áudio.
	 * 
	 * @param fileAudioList - Lista com os arquivos de áudio
	 */
	public void addAudioFileListToAudioLibrary(File[] fileAudioList) {
		this.strAudioFilePathLoaded = fileAudioList[0].getAbsolutePath();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			this.activateCellRenderer(false);
			
			objDatabaseConnection.openConnection();

			if (fileAudioList != null && fileAudioList.length > 0) {
				boolean blnUpdatedPosition = false;   // Verifica se a posição dos arquivos foi alterada na biblioteca
				
				// É necessário inserir do último registro da lista para o primeiro
				for (int indexAudio = fileAudioList.length - 1; indexAudio >= 0; indexAudio--) {
					String strAudioFile = fileAudioList[indexAudio].getAbsolutePath();
					
					// Remove o item da lista da biblioteca de áudio caso já tenha sido adicionado anteriormente
					for (int indexLibraryItem = 0; indexLibraryItem < listModelAudioLibrary.getSize(); indexLibraryItem++) {
			        	if (strAudioFile.equals(listModelAudioLibrary.getElementAt(indexLibraryItem).toString())) {
			        		listModelAudioLibrary.remove(indexLibraryItem);
			        	}
			        }
					
					// Adiciona áudio primeiro índice da lista da biblioteca de áudio
			    	listModelAudioLibrary.insertElementAt(strAudioFile, 0);
					
			        // Caso a biblioteca já tenha sido criada no banco de dados
					if (lgnId != 0) {
						// Altera a posição de todos os arquivos da biblioteca atual
						if (!blnUpdatedPosition) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("UPDATE libraries_audio_files ");
							objDatabaseConnection.sqlCommandAppend("SET audio_file_position = audio_file_position + ? ");
							objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ? ");
							objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.INTEGER, fileAudioList.length);
							objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
							objDatabaseConnection.executeQuery();
							
							blnUpdatedPosition = true;
						}
						
						// Exclui o arquivo de áudio da biblioteca
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("DELETE FROM libraries_audio_files ");
						objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ? ");
						objDatabaseConnection.sqlCommandAppend("AND audio_file_path = ? ");
						objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
						objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFile);
						objDatabaseConnection.executeQuery();
						
						// Insere o arquivo de áudio à biblioteca
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("INSERT INTO libraries_audio_files (fk_library, audio_file_path, audio_file_position) ");
						objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
						objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
						objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFile);
						objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.INTEGER, indexAudio);
						objDatabaseConnection.executeQuery();
					}
				}
				
				if (lgnId != 0) {
					objDatabaseConnection.commitTransaction();
				}
				
				listAudioLibrary.setSelectedIndex(0);
				
				strAudioFilePathSelected = strAudioFilePathLoaded;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
			
			this.activateCellRenderer(true);
		}
	}
	
	/**
	 * Exclui um arquivo de áudio da biblioteca de áudio
	 * (exclusão também realizada no banco de dados).
	 * 
	 * @param strAudioFilePath - Arquivo de áudio a ser excluído
	 */
	public void deleteAudioFileFromAudioLibrary(String strAudioFilePath) {
		// Remove o item da lista da biblioteca de áudio
        for (int indexLibraryItem = 0; indexLibraryItem < listModelAudioLibrary.getSize(); indexLibraryItem++) {
        	if (strAudioFilePath.equals(listModelAudioLibrary.getElementAt(indexLibraryItem).toString())) {
        		listModelAudioLibrary.remove(indexLibraryItem);
        	}
        }
        
        // Caso a biblioteca já tenha sido criada no banco de dados, 
	    // o áudio de áudio é deletado do banco de dados
        if (lgnId != 0) {
			DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
			
			try {
				objDatabaseConnection.openConnection();
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("DELETE FROM libraries_audio_files ");
				objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ? ");
				objDatabaseConnection.sqlCommandAppend("AND audio_file_path = ? ");
				objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
				objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePath);
				objDatabaseConnection.executeQuery();
				objDatabaseConnection.commitTransaction();
				
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				objDatabaseConnection.rollBackTransaction();
				objDatabaseConnection.closeConnection();
			}
        }
	}
	
	/**
	 * Insere arquivo de áudio à uma biblioteca de áudio 
	 * (inclusão também realizada no banco de dados).
	 * 
	 * @param strAudioFilePath - Arquivo de áudio a ser inserido
	 */
	private void insertAudioFileIntoAudioLibrary(String strAudioFilePath) {
        // Adiciona áudio primeiro índice da lista da biblioteca de áudio
    	listModelAudioLibrary.insertElementAt(strAudioFilePath, 0);
		listAudioLibrary.setSelectedIndex(0);
	    strAudioFilePathSelected = strAudioFilePath;
	    
        // Caso a biblioteca já tenha sido criada no banco de dados, 
	    // o áudio é adicionado ao banco de dados
	    if (lgnId != 0) {
			DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
			
			try {
				objDatabaseConnection.openConnection();
				
				// Altera a posição dos outros arquivos da biblioteca (adiciona 1 posição na lista)
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("UPDATE libraries_audio_files");
				objDatabaseConnection.sqlCommandAppend("SET audio_file_position = audio_file_position + 1");
				objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ?");
				objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
				objDatabaseConnection.executeQuery();
	
				// Insere arquivo novamente à biblioteca
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("INSERT INTO libraries_audio_files (fk_library, audio_file_path, audio_file_position)");
				objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?)");
				objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
				objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePath);
				objDatabaseConnection.addParameter("audio_file_position", LTDataTypes.INTEGER, 0);
				objDatabaseConnection.executeQuery();
				objDatabaseConnection.commitTransaction();
	
			} catch (Exception e) {
				e.printStackTrace();
				
			} finally {
				objDatabaseConnection.rollBackTransaction();
				objDatabaseConnection.closeConnection();
			}
	    }
	}
	
	/**
	 * Atualiza a posição do arquivo atual 
	 * na primeira posição da biblioteca de áudio
	 * (atualização também realizada no banco de dados).
	 */
	public void updateAudioFilePosition() {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			
			// Altera a posição dos outros arquivos da biblioteca (adiciona 1 posição na lista)
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE libraries_audio_files ");
			objDatabaseConnection.sqlCommandAppend("SET audio_file_position = audio_file_position + 1 ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ? ");
			objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
			objDatabaseConnection.executeQuery();
			
			// Altera a posição dos outros arquivos da biblioteca (adiciona 1 posição na lista)
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE libraries_audio_files ");
			objDatabaseConnection.sqlCommandAppend("SET audio_file_position = 0 ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ? ");
			objDatabaseConnection.sqlCommandAppend("AND audio_file_path = ? ");
			objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnId);
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePathLoaded);
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
     * Cria um menu que será ativado quando clicar 
     * com o botão direito do mouse na biblioteca.
     */
    private void createPopupMenuAudioLibrary() {
    	popupMenuLibrary = new JPopupMenu();

    	// **************************************************************************************************************
    	// Abrir arquivo de áudio
    	popupMenuItemLibraryOpenAudio = new JMenuItem(rsBundle.getString("audio_library_popup_menu_open_audio_file"));
    	popupMenuLibrary.add(popupMenuItemLibraryOpenAudio);
    	popupMenuItemLibraryOpenAudio.addActionListener(new ActionListener() {
    		@Override
            public void actionPerformed(ActionEvent event) {
                openAudioFile();
            }
        });
    	
    	// **************************************************************************************************************
    	// Fechar arquivo de áudio
    	popupMenuItemLibraryCloseAudio = new JMenuItem(rsBundle.getString("audio_library_popup_menu_close_audio_file"));
    	popupMenuLibrary.add(popupMenuItemLibraryCloseAudio);
    	popupMenuItemLibraryCloseAudio.addActionListener(new ActionListener() {
    		@Override
            public void actionPerformed(ActionEvent event) {
    			closeSelectedAudioFile();
            }
        });

    	popupMenuLibraryListener = new AudioLibraryMouseAdapter(popupMenuLibrary);
    	listAudioLibrary.addMouseListener(popupMenuLibraryListener);
    }
    
    /**
     * Adiciona um 'WaveformListener' parametrizado à 'collection listener'.
     * 
     * @param waveformListener
     */
    public void addAudioLibraryListener(AudioLibraryListener audioLibraryListener) {
    	collectionListenerAudioLibrary.add(audioLibraryListener);
    }
    
    /**
	 * Cria um <i>MouseAdapter</i> que será ativado quando clicar com o mouse na biblioteca.
	 */
	private class AudioLibraryMouseAdapter extends MouseAdapter {
		JPopupMenu popupMenuLibrary;
		
		/**
    	 * Cria <i>MouseAdapter</i> que será ativado quando clicar com o mouse na biblioteca.
    	 * 
    	 * @param popupMenuLibrary
    	 */
        private AudioLibraryMouseAdapter(JPopupMenu popupMenuLibrary) {
        	this.popupMenuLibrary = popupMenuLibrary;
        }
        
		@Override
		public void mouseClicked(MouseEvent event) {
			super.mouseClicked(event);
			
			popupMenuLibrary.setVisible(false);
		}

        @Override
        public void mousePressed(MouseEvent event) {
        	@SuppressWarnings("unchecked")
			JList<String> list = (JList<String>) event.getSource();
        	
        	// Verifica qual é o índice que foi selecionado
        	// Se for clicado em uma parte em branco da biblioteca, o índice será -1
        	intAudioSelectedIndex = -1;
        	strAudioFilePathSelected = null;
        	listAudioLibrary.clearSelection();
        	
        	Rectangle rectangle = list.getCellBounds(0, list.getLastVisibleIndex());
        	if (rectangle != null && rectangle.contains(event.getPoint())) { 
        		intAudioSelectedIndex = list.locationToIndex(event.getPoint());
        		strAudioFilePathSelected = listModelAudioLibrary.getElementAt(intAudioSelectedIndex).toString();
        		listAudioLibrary.setSelectedIndex(intAudioSelectedIndex);
        	}

        	// Botão esquerdo do mouse
        	if (SwingUtilities.isLeftMouseButton(event)) {
        		// Duplo clique
				if (event.getClickCount() == 2 && intAudioSelectedIndex != -1) {
					if (AudioFile.checkExistingFile(strAudioFilePathSelected)) {
						loadAudioFile();
					} else {
						WasisMessageBox.showMessageDialog(rsBundle.getString("message_audio_file_not_found") + ". \n" +
								                          rsBundle.getString("message_audio_file_not_found_check"),
								                          WasisMessageBox.ERROR_MESSAGE);
					}
				}
			
			// Botão direito do mouse
			} else if (SwingUtilities.isRightMouseButton(event)) {
				// Único clique
				if (event.getClickCount() == 1) {
					if (intAudioSelectedIndex == -1) {
						popupMenuItemLibraryCloseAudio.setEnabled(false);
					} else {
						popupMenuItemLibraryCloseAudio.setEnabled(true);
					}
					
					popupMenuLibrary.show(event.getComponent(), event.getX(), event.getY());
				}
			}
        }
	}
	
	/**
	 * Notifica o 'AudioLibraryListener' para abrir um novo arquivo de áudio.
	 */
	private void openAudioFile() {
		Iterator<Object> it = collectionListenerAudioLibrary.iterator();
        AudioLibraryListener audioLibraryListener;
        
        while (it.hasNext()) {
        	audioLibraryListener = (AudioLibraryListener) it.next();
        	audioLibraryListener.openAudioFileFromAudioLibrary();
        }
	}
	
	/**
	 * Carrega um arquivo de áudio que foi selecionado pelo usuário,
	 * ou o arquivo mais acima na lista caso o primeiro tenha sido excluído.
	 */
	private void loadAudioFile() {
		try {
			if (strAudioFilePathLoaded == null) {
				strAudioFilePathLoaded = listModelAudioLibrary.getElementAt(0).toString();
			}
			
			if (!strAudioFilePathLoaded.equals(strAudioFilePathSelected) || !blnAudioAlreadyLoaded) {
				strAudioFilePathLoaded = strAudioFilePathSelected;
				
				// Exclui o áudio da lista da biblioteca para inseri-lo novamente na primeira posição
				listModelAudioLibrary.remove(intAudioSelectedIndex);
				listModelAudioLibrary.insertElementAt(strAudioFilePathLoaded, 0);
		        listAudioLibrary.setSelectedIndex(0);
		        
		        updateAudioFilePosition();
		        
		        // Notifica o 'AudioLibraryListener' para carregar o arquivo de áudio.
		        Iterator<Object> it = collectionListenerAudioLibrary.iterator();
		        AudioLibraryListener audioLibraryListener;
		        
		        while (it.hasNext()) {
		        	audioLibraryListener = (AudioLibraryListener) it.next();
		        	audioLibraryListener.loadAudioFileFromAudioLibrary(strAudioFilePathLoaded);
		        }
		        
		        blnAudioAlreadyLoaded = true;
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fecha o arquivo de áudio que está aberto (em execução) na biblioteca de áudio.
	 */
	public void closeOpenedAudioFile() {
		if (listModelAudioLibrary.getSize() > 0) {
			String strAudioFilePath = listModelAudioLibrary.getElementAt(0).toString();
		
			// Verifica se o arquivo possui seleções não salvas
			if (AudioTemporary.checkSelectionsNotSaved(strAudioFilePath)) {
				int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("close_audio_file_selections_not_saved"), WasisMessageBox.YES_NO_CANCEL_OPTION);
				
				if (intDialogResult == WasisMessageBox.YES_OPTION) {
					deleteOpenedAudioFile();   // Exclui o arquivo com confirmação do usuário
				}
				
			// Se não houver seleções não salvas, exclui o arquivo sem qualquer confirmação do usuário
			} else {
				deleteOpenedAudioFile();
			}
		}
	}
	
	/**
	 * Exclui o arquivo de áudio que está aberto (em execução) na biblioteca de áudio.
	 */
	private void deleteOpenedAudioFile() {
		deleteAudioFileFromAudioLibrary(listModelAudioLibrary.getElementAt(0).toString());
		
		// Caso o primeiro registro da biblioteca de áudio tenha sido fechado,
		// o segundo registro é automaticamente carregado (se a biblioteca conter mais de um registro)
		if (listModelAudioLibrary.getSize() != 0) {
			intAudioSelectedIndex = 0;
			strAudioFilePathSelected = listModelAudioLibrary.getElementAt(0).toString();
			
			loadAudioFile();
			
		// Caso não haja mais arquivos na biblioteca de áudio, reseta os valores padrões
		} else {
			strAudioFilePathLoaded = null;
			strAudioFilePathSelected = null;
			
			// Notifica o 'AudioLibraryListener' para resetar os valores
			// quando não há nenhum arquivo de áudio a ser carregado na biblioteca
	        Iterator<Object> it = collectionListenerAudioLibrary.iterator();
	        AudioLibraryListener audioLibraryListener;
	        
	        while (it.hasNext()) {
	        	audioLibraryListener = (AudioLibraryListener) it.next();
	        	audioLibraryListener.resetValuesFromAudioLibrary();
	        }
		}
	}
	
	/**
	 * Fecha o arquivo de áudio selecionado da biblioteca de áudio.
	 */
	private void closeSelectedAudioFile() {
		String strAudioFileSelected = listModelAudioLibrary.getElementAt(intAudioSelectedIndex).toString();
		deleteAudioFileFromAudioLibrary(strAudioFileSelected);

		if (intAudioSelectedIndex == 0) {
			// Caso o primeiro registro da biblioteca de áudio tenha sido fechado,
			// o segundo registro é automaticamente carregado (se a biblioteca conter mais de um registro)
			if (listModelAudioLibrary.getSize() != 0) {
				strAudioFilePathSelected = listModelAudioLibrary.getElementAt(intAudioSelectedIndex).toString();
				
				loadAudioFile();
				
			// Caso não haja mais arquivos na biblioteca de áudio, reseta os valores padrões
			} else {
				strAudioFilePathLoaded = null;
				strAudioFilePathSelected = null;
				
				// Notifica o 'AudioLibraryListener' para resetar os valores
				// quando não há nenhum arquivo de áudio a ser carregado na biblioteca
		        Iterator<Object> it = collectionListenerAudioLibrary.iterator();
		        AudioLibraryListener audioLibraryListener;
		        
		        while (it.hasNext()) {
		        	audioLibraryListener = (AudioLibraryListener) it.next();
		        	audioLibraryListener.resetValuesFromAudioLibrary();
		        }
			}
		}
	}
	
	/**
	 * Limpa o arquivo de áudio que estava sendo carregado na biblioteca de áudio.
	 */
	public void clearAudioLibraryLoadedFile() {
		intAudioSelectedIndex = 0;
		strAudioFilePathLoaded = null;
		strAudioFilePathSelected = null;
		
		blnAudioAlreadyLoaded = false;
		
		this.activateCellRenderer(true);
	}
	
	/**
	 * Limpa todos os arquivos de áudio da lista da biblioteca de áudio.
	 */
	public void clearAudioLibrary() {
		intAudioSelectedIndex = 0;
		strAudioFilePathLoaded = null;
		strAudioFilePathSelected = null;
		
		listModelAudioLibrary.clear();
	}
}