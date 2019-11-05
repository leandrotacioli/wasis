package br.unicamp.fnjv.wasis.audio.library;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.miginfocom.swing.MigLayout;

import br.unicamp.fnjv.wasis.audio.AudioTemporary;
import br.unicamp.fnjv.wasis.database.DatabaseFactory;
import br.unicamp.fnjv.wasis.database.dao.AudioLibraryDAO;
import br.unicamp.fnjv.wasis.database.dao.AudioLibraryFileDAO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryDTO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryFileDTO;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.multimidia.AudioFile;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanelRounded;

/**
 * Classe responsável por todo o controle da biblioteca de áudio.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 30/Mar/2018
 */
public class AudioLibraryController extends WasisPanelRounded { 
	private static final long serialVersionUID = 3945481882108193120L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
    private AudioLibraryDTO objAudioLibrary;
	
    private JLabel lblLibrary;
    
    private List<AudioLibraryFileDTO> lstAudioLibraryFiles;
    
	private JList<String> listAudioLibrary;
    private DefaultListModel<String> listModelAudioLibrary;
    
    /** Status de um arquivo de áudio já carregado anteriormente. */
    private boolean blnAudioAlreadyLoaded;
    
    /** Nome do arquivo de áudio que está sendo carregado na biblioteca. */
    private String strAudioFilePathLoaded;
    
    /** Nome do arquivo de áudio que está sendo selecionado na biblioteca. */
    private String strAudioFilePathSelected;
    
    /** Índice do arquivo de áudio que está sendo selecionado na biblioteca. */
    private int intAudioFileSelectedIndex;
    
    private JPopupMenu popupMenuLibrary;
    private JMenuItem popupMenuItemLibraryOpenAudio;
	private JMenuItem popupMenuItemLibraryCloseAudio;
	
    private Collection<Object> collectionListenerAudioLibrary;
    
    /**
     * Retorna o DTO da biblioteca de áudio.
     * 
     * @return objAudioLibrary
     */
    public AudioLibraryDTO getAudioLibrary() {
		return objAudioLibrary;
	}
    
    /**
     * Altera o DTO da biblioteca de áudio.
     * 
     * @return objAudioLibrary
     */
    public void setAudioLibrary(AudioLibraryDTO objAudioLibrary) {
		this.objAudioLibrary = objAudioLibrary;
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
	public AudioLibraryController() {
		super(null);

		objAudioLibrary = new AudioLibraryDTO();
		
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
		
		JButton btnUpdate = new JButton();
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
		
		lstAudioLibraryFiles = new ArrayList<AudioLibraryFileDTO>();
		
		listModelAudioLibrary = new DefaultListModel<String>();
		
		listAudioLibrary = new JList<String>(listModelAudioLibrary);
		listAudioLibrary.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		listAudioLibrary.setOpaque(false);
		
		this.activateCellRenderer(true);
		
		JScrollPane scrollPaneAudioLibrary = new JScrollPane(listAudioLibrary);
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
				listAudioLibrary.setCellRenderer(new AudioLibraryRenderer(AudioLibraryController.this));
			} else {
				listAudioLibrary.setCellRenderer(null);
			}
		}
	}
	
	/**
	 * Abre uma biblioteca de áudio já existente no banco de dados.
	 * 
	 * @param objAudioLibrary - Objeto DTO da biblioteca de áudio
	 */
	public void openAudioLibrary(AudioLibraryDTO objAudioLibrary) {
		this.objAudioLibrary = objAudioLibrary;
		
		lblLibrary.setText(rsBundle.getString("audio_library_label_description") + ": " + objAudioLibrary.getLibraryName());
		lblLibrary.setToolTipText(objAudioLibrary.getLibraryDescription());
		
		loadAudioLibraryFiles();
	}
	
	/**
	 * Carrega os arquivos de áudio existentes na biblioteca.
	 */
	public void loadAudioLibraryFiles() {
		try {
			blnAudioAlreadyLoaded = false;
			
			clearAudioLibraryFiles();
			
			AudioLibraryFileDAO objAudioLibraryFileDAO = DatabaseFactory.createAudioLibraryFileDAO();
			
			lstAudioLibraryFiles = objAudioLibraryFileDAO.getAudioLibraryFiles(objAudioLibrary);
			
	    	// Caso seja biblioteca com amostras de sons, os caminhos dos arquivos 
	    	// serão alterados para o caminho onde está a pasta do sistema
			File fileFnjv = new File("audio_samples/audio");
			String strFnjvPath = fileFnjv.getAbsoluteFile().getParentFile().getAbsolutePath();
			
			for (int indexAudioLibraryFile = 0; indexAudioLibraryFile < lstAudioLibraryFiles.size(); indexAudioLibraryFile++) {
				if (lstAudioLibraryFiles.get(indexAudioLibraryFile).getAudioFileSample() != null && 
						lstAudioLibraryFiles.get(indexAudioLibraryFile).getAudioFileSample().length() > 0) {
					lstAudioLibraryFiles.get(indexAudioLibraryFile).setAudioFilePath(strFnjvPath + "\\" + lstAudioLibraryFiles.get(indexAudioLibraryFile).getAudioFileSample());
				}
			}
			
			if (lstAudioLibraryFiles.size() > 0) {
				strAudioFilePathSelected = lstAudioLibraryFiles.get(0).getAudioFilePath();
			}
			
			sortList();
			assignValues();
			loadAudioFile();
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Salva os dados da biblioteca de áudio no banco de dados.
	 */
	protected void saveAudioLibrary() {
		try {
			AudioLibraryDAO objAudioLibraryDAO = DatabaseFactory.createAudioLibraryDAO();
			
			List<AudioLibraryFileDTO> lstAudioLibraryFiles = new ArrayList<AudioLibraryFileDTO>();
			AudioLibraryFileDTO objAudioLibraryFile;
			
			for (int indexAudioLibraryFiles = 0; indexAudioLibraryFiles < getListModelAudioLibrary().getSize(); indexAudioLibraryFiles++) {
				objAudioLibraryFile = new AudioLibraryFileDTO();
				objAudioLibraryFile.setAudioFilePath(getListModelAudioLibrary().getElementAt(indexAudioLibraryFiles).toString());
				objAudioLibraryFile.setAudioFilePosition(indexAudioLibraryFiles);
				
				lstAudioLibraryFiles.add(objAudioLibraryFile);
			}
			
			if (objAudioLibrary.getIdAudioLibrary() == 0) {
				objAudioLibrary = objAudioLibraryDAO.saveAudioLibrary(objAudioLibrary, lstAudioLibraryFiles);
			} else {
				objAudioLibraryDAO.updateAudioLibrary(objAudioLibrary);
			}
			
			lblLibrary.setText(rsBundle.getString("audio_library_label_description") + ": " + objAudioLibrary.getLibraryName());
			lblLibrary.setToolTipText(objAudioLibrary.getLibraryDescription());
			
			WasisMessageBox.showMessageDialog(rsBundle.getString("operation_completed"), WasisMessageBox.INFORMATION_MESSAGE);
			
		} catch (Exception e) {
			e.printStackTrace();
			WasisMessageBox.showMessageDialog(rsBundle.getString("error_saving_data"), WasisMessageBox.ERROR_MESSAGE);
		}
	}
	
	/**
	 * Adiciona uma lista de arquivos de áudio na biblioteca de áudio.
	 * 
	 * @param fileAudioList - Lista com os arquivos de áudio
	 */
	public void addAudioFileListToAudioLibrary(File[] fileAudioList) {
		this.strAudioFilePathLoaded = fileAudioList[0].getAbsolutePath();
		
		try {
			this.activateCellRenderer(false);
			
			if (fileAudioList != null && fileAudioList.length > 0) {
				AudioLibraryFileDAO objAudioLibraryFileDAO = DatabaseFactory.createAudioLibraryFileDAO();
				AudioLibraryFileDTO objAudioLibraryFile;
				
				// Loop através dos arquivos da lista
				for (int indexAudio = 0; indexAudio < fileAudioList.length; indexAudio++) {
					objAudioLibraryFile = new AudioLibraryFileDTO();
					objAudioLibraryFile.setAudioFilePath(fileAudioList[indexAudio].getAbsolutePath());
					
					boolean blnInsertAudio = true;
					
					// Verifica se o arquivo sonoro já foi adicionado anteriormente
					for (int indexAudioLibraryFile = 0; indexAudioLibraryFile < lstAudioLibraryFiles.size(); indexAudioLibraryFile++) {
			        	if (objAudioLibraryFile.getAudioFilePath().equals(lstAudioLibraryFiles.get(indexAudioLibraryFile).getAudioFilePath())) {
			        		blnInsertAudio = false;
			        		
			        		break;
			        	}
			        }
					
					if (blnInsertAudio) {
						// Insere o arquivo de áudio vinculado à biblioteca de áudio no banco de dados
						if (objAudioLibrary.getIdAudioLibrary() != 0) {
							objAudioLibraryFile = objAudioLibraryFileDAO.saveAudioLibraryFile(objAudioLibrary, objAudioLibraryFile);
						}
						
						// Adiciona áudio no próximo índice da lista da biblioteca de áudio
						lstAudioLibraryFiles.add(objAudioLibraryFile);
					}
				}
				
				strAudioFilePathSelected = strAudioFilePathLoaded;
				
				sortList();
				assignValues();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			this.activateCellRenderer(true);
		}
	}
	
	/**
	 * Exclui um arquivo de áudio da biblioteca de áudio.
	 * 
	 * @param strAudioFilePath - Arquivo de áudio a ser excluído
	 */
	public void deleteAudioFileFromAudioLibrary(String strAudioFilePath) {
		try {
			// Remove o item da lista da biblioteca de áudio
	        for (int indexAudioLibraryFile = 0; indexAudioLibraryFile < lstAudioLibraryFiles.size(); indexAudioLibraryFile++) {
	        	if (strAudioFilePath.equals(lstAudioLibraryFiles.get(indexAudioLibraryFile).getAudioFilePath())) {
	        		lstAudioLibraryFiles.remove(indexAudioLibraryFile);
	        		listModelAudioLibrary.remove(indexAudioLibraryFile);
	        	}
	        }
	        
	        // Deleta arquivo de áudio do banco de dados
	        if (objAudioLibrary.getIdAudioLibrary() != 0) {
				AudioLibraryFileDTO objAudioLibraryFile = new AudioLibraryFileDTO();
				objAudioLibraryFile.setAudioFilePath(strAudioFilePath);
				
				AudioLibraryFileDAO objAudioLibraryFileDAO = DatabaseFactory.createAudioLibraryFileDAO();
				objAudioLibraryFileDAO.deleteAudioLibraryFile(objAudioLibrary, objAudioLibraryFile);
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * Cria um menu que será ativado quando clicar com o botão direito do mouse na biblioteca.
     */
    private void createPopupMenuAudioLibrary() {
    	popupMenuLibrary = new JPopupMenu();
    	
    	// **************************************************************************************************************
    	// Abrir arquivo de áudio
    	popupMenuItemLibraryOpenAudio = new JMenuItem(rsBundle.getString("audio_library_popup_menu_open_audio_file"));
    	popupMenuItemLibraryOpenAudio.addActionListener(new ActionListener() {
    		@Override
            public void actionPerformed(ActionEvent event) {
                openAudioFile();
            }
        });
    	
    	// **************************************************************************************************************
    	// Fechar arquivo de áudio
    	popupMenuItemLibraryCloseAudio = new JMenuItem(rsBundle.getString("audio_library_popup_menu_close_audio_file"));
    	popupMenuItemLibraryCloseAudio.addActionListener(new ActionListener() {
    		@Override
            public void actionPerformed(ActionEvent event) {
    			closeSelectedAudioFile();
            }
        });
    	
    	popupMenuLibrary.add(popupMenuItemLibraryOpenAudio);
    	popupMenuLibrary.add(popupMenuItemLibraryCloseAudio);
    	
    	listAudioLibrary.addMouseListener(new AudioLibraryMouseAdapter(popupMenuLibrary));
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
        	intAudioFileSelectedIndex = -1;
        	strAudioFilePathSelected = null;
        	listAudioLibrary.clearSelection();
        	
        	Rectangle rectangle = list.getCellBounds(0, list.getLastVisibleIndex());
        	if (rectangle != null && rectangle.contains(event.getPoint())) { 
        		intAudioFileSelectedIndex = list.locationToIndex(event.getPoint());
        		strAudioFilePathSelected = listModelAudioLibrary.getElementAt(intAudioFileSelectedIndex).toString();
        		listAudioLibrary.setSelectedIndex(intAudioFileSelectedIndex);
        	}
        	
        	// Botão esquerdo do mouse
        	if (SwingUtilities.isLeftMouseButton(event)) {
        		// Duplo clique
				if (event.getClickCount() == 2 && intAudioFileSelectedIndex != -1) {
					if (AudioFile.checkExistingFile(strAudioFilePathSelected)) {
						loadAudioFile();
						listAudioLibrary.setSelectedIndex(intAudioFileSelectedIndex);
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
					if (intAudioFileSelectedIndex == -1) {
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
			if (lstAudioLibraryFiles.size() > 0) {
				if (strAudioFilePathLoaded == null) {
					strAudioFilePathLoaded = listModelAudioLibrary.getElementAt(0).toString();
				}
				
				if (!strAudioFilePathLoaded.equals(strAudioFilePathSelected) || !blnAudioAlreadyLoaded) {
					strAudioFilePathLoaded = strAudioFilePathSelected;
					
			        listAudioLibrary.setSelectedIndex(intAudioFileSelectedIndex);
			        
			        // Notifica o 'AudioLibraryListener' para carregar o arquivo de áudio.
			        Iterator<Object> it = collectionListenerAudioLibrary.iterator();
			        AudioLibraryListener audioLibraryListener;
			        
			        while (it.hasNext()) {
			        	audioLibraryListener = (AudioLibraryListener) it.next();
			        	audioLibraryListener.loadAudioFileFromAudioLibrary(strAudioFilePathLoaded);
			        }
			        
			        blnAudioAlreadyLoaded = true;
				}
				
				listAudioLibrary.updateUI();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Fecha o arquivo de áudio que está aberto (em execução) na biblioteca de áudio.
	 * 
	 * @return blnAudioFileClosed
	 */
	public boolean closeOpenedAudioFile() {
		boolean blnAudioFileClosed = true;
		
		if (listModelAudioLibrary.getSize() > 0) {
			String strAudioFilePath = listModelAudioLibrary.getElementAt(0).toString();
			
			// Verifica se o arquivo possui segmentos de áudio não salvos
			if (AudioTemporary.checkAudioSegmentsNotSaved(strAudioFilePath)) {
				int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("close_audio_file_audio_segments_not_saved"), WasisMessageBox.YES_NO_CANCEL_OPTION);
				
				if (intDialogResult == WasisMessageBox.YES_OPTION) {
					deleteOpenedAudioFile();   // Exclui o arquivo com confirmação do usuário
				} else {
					blnAudioFileClosed = false;
				}
				
			// Se não houver seleções não salvas, exclui o arquivo sem qualquer confirmação do usuário
			} else {
				deleteOpenedAudioFile();
			}
		}
		
		return blnAudioFileClosed;
	}
	
	/**
	 * Exclui o arquivo de áudio que está aberto (em execução) na biblioteca de áudio.
	 */
	private void deleteOpenedAudioFile() {
		deleteAudioFileFromAudioLibrary(listModelAudioLibrary.getElementAt(0).toString());
		
		// Caso o primeiro registro da biblioteca de áudio tenha sido fechado,
		// o segundo registro é automaticamente carregado (se a biblioteca conter mais de um registro)
		if (listModelAudioLibrary.getSize() != 0) {
			intAudioFileSelectedIndex = 0;
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
		deleteAudioFileFromAudioLibrary(listModelAudioLibrary.getElementAt(intAudioFileSelectedIndex).toString());
		
		if (intAudioFileSelectedIndex == 0) {
			// Caso o primeiro registro da biblioteca de áudio tenha sido fechado,
			// o segundo registro é automaticamente carregado (se a biblioteca conter mais de um registro)
			if (listModelAudioLibrary.getSize() != 0) {
				strAudioFilePathSelected = listModelAudioLibrary.getElementAt(intAudioFileSelectedIndex).toString();
				
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
	public void clearAudioLibraryLoadingFile() {
		intAudioFileSelectedIndex = 0;
		strAudioFilePathLoaded = null;
		strAudioFilePathSelected = null;
		
		blnAudioAlreadyLoaded = false;
		
		this.activateCellRenderer(true);
	}
	
	/**
	 * Limpa todos os dados da biblioteca de áudio.
	 */
	public void clearAudioLibrary() {
		objAudioLibrary = new AudioLibraryDTO();
		
		lblLibrary.setText(rsBundle.getString("audio_library_label_description"));
		lblLibrary.setToolTipText("");
		
		clearAudioLibraryFiles();
	}
	
	/**
	 * Limpa todos os arquivos de áudio da lista da biblioteca de áudio.
	 */
	private void clearAudioLibraryFiles() {
		intAudioFileSelectedIndex = 0;
		strAudioFilePathLoaded = null;
		strAudioFilePathSelected = null;
		
		lstAudioLibraryFiles = new ArrayList<AudioLibraryFileDTO>();
		listModelAudioLibrary.clear();
	}
	
	/**
	 * Ordena a lista de arquivos sonoros por ordem alfabética.
	 */
	private void sortList() {
		if (lstAudioLibraryFiles.size() > 0) {
			Collections.sort(lstAudioLibraryFiles, new Comparator<AudioLibraryFileDTO>() {
			    @Override
			    public int compare(final AudioLibraryFileDTO object1, final AudioLibraryFileDTO object2) {
			    	return object1.getAudioFilePath().compareTo(object2.getAudioFilePath());
			    }
			});
		}
	}
	
	/**
	 * Limpa a lista e adiciona os registros novamente baseado.
	 */
	private void assignValues() {
		listModelAudioLibrary.clear();
		
		for (int indexAudioLibraryFile = 0; indexAudioLibraryFile < lstAudioLibraryFiles.size(); indexAudioLibraryFile++) {
			listModelAudioLibrary.addElement(lstAudioLibraryFiles.get(indexAudioLibraryFile).getAudioFilePath());	
			
			if (strAudioFilePathSelected.equals(lstAudioLibraryFiles.get(indexAudioLibraryFile).getAudioFilePath())) {
				listAudioLibrary.setSelectedIndex(indexAudioLibraryFile);
			}
		}
	}
}