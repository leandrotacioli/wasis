package br.unicamp.fnjv.wasis.audio.library;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;
import br.unicamp.fnjv.wasis.database.DatabaseConnection;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

/**
 * Classe responsável pela exibição de uma tela que
 * possui todos os campos necessários para gravação
 * de uma nova biblioteca no banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 1.2 - 23/Nov/2014
 */
public class SaveAudioLibrary extends JDialog {
	private static final long serialVersionUID = -3601217094327718541L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private AudioLibrary objAudioLibrary;
	private WasisDialog objWasisDialog;
	
	private LTTextField txtLibraryName;
	private LTTextField txtLibraryDescription;
	private LTTextField txtLibraryObservations;
	private LTTable objTableAudioFiles;
	private JButton btnSaveData;
	
	/**
	 * Classe responsável pela exibição de uma tela que
	 * possui todos os campos necessários para gravação
	 * de uma nova biblioteca no banco de dados.
	 * 
	 * @param objAudioLibrary - Objeto da biblioteca de áudio
	 */
	public SaveAudioLibrary(AudioLibrary objAudioLibrary) {
		this.objAudioLibrary = objAudioLibrary;

		loadScreen();
		loadAudioLibraryData();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		txtLibraryName = new LTTextField(rsBundle.getString("save_audio_library_name"), LTDataTypes.STRING, true, true, 50);
		txtLibraryDescription = new LTTextField(rsBundle.getString("save_audio_library_description"), LTDataTypes.STRING, true, true, 400);
		txtLibraryObservations = new LTTextField(rsBundle.getString("save_audio_library_observations"), LTDataTypes.TEXT, true, false);

		objTableAudioFiles = new LTTable(true, false);
		objTableAudioFiles.addColumn("audio_file", rsBundle.getString("save_audio_library_grid_audio_file"), LTDataTypes.STRING, 620, false);
		objTableAudioFiles.showTable();
		
		// Botão Salvar Dados
		btnSaveData = new JButton(rsBundle.getString("save"));
		btnSaveData.setMinimumSize(new Dimension(100, 30));
		btnSaveData.setMaximumSize(new Dimension(400, 30));
		btnSaveData.setIconTextGap(15);
		btnSaveData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSaveData.setIcon(new ImageIcon("res/images/save.png"));
		btnSaveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (objAudioLibrary.getId() == 0) {
					saveAudioLibraryData();
				} else {
					updateAudioLibraryData();
				}
			}
		});
		
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("wasis") + " - " + rsBundle.getString("audio_library"), true);
		objWasisDialog.setBounds(350, 350, 700, 500);
		objWasisDialog.setMinimumSize(new Dimension(700, 500));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][300.00][grow][]"));
		objWasisDialog.getContentPane().add(txtLibraryName, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(txtLibraryDescription, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(txtLibraryObservations, "cell 0 2, grow");
		objWasisDialog.getContentPane().add(objTableAudioFiles, "cell 0 3, grow");
		objWasisDialog.getContentPane().add(btnSaveData, "cell 0 4");
	}
	
	/**
	 * Carrega os dados de uma biblioteca de áudio.
	 */
	private void loadAudioLibraryData() {
		// Biblioteca de áudio já salva no banco de dados
		if (objAudioLibrary.getId() != 0) {
			DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
			
			try {
				objDatabaseConnection.openConnection();
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT library_name, library_description, observations ");
				objDatabaseConnection.sqlCommandAppend("FROM libraries ");
				objDatabaseConnection.sqlCommandAppend("WHERE id = ? ");
				objDatabaseConnection.addParameter("id", LTDataTypes.LONG, objAudioLibrary.getId());
				
				ResultSet rsAudioLibrary = objDatabaseConnection.executeSelectQuery();
				
				while (rsAudioLibrary.next()) {
					txtLibraryName.setValue(rsAudioLibrary.getString(1));
					txtLibraryDescription.setValue(rsAudioLibrary.getString(2));
					txtLibraryObservations.setValue(rsAudioLibrary.getString(3));
					
					// Carrega os áudios da biblioteca
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT audio_file_path, audio_file_name ");
					objDatabaseConnection.sqlCommandAppend("FROM libraries_audio_files ");
					objDatabaseConnection.sqlCommandAppend("WHERE fk_library = ? ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY audio_file_position ");
					objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, objAudioLibrary.getId());
					
					ResultSet rsAudioLibraryFiles = objDatabaseConnection.executeSelectQuery();
					
					File fileFnjv = new File("audio_samples/audio");
					String strFnjvPath = fileFnjv.getAbsoluteFile().getParentFile().getAbsolutePath();

					while (rsAudioLibraryFiles.next()) {
						objTableAudioFiles.addRow();
	
				    	// Biblioteca de amostra
				    	if (objAudioLibrary.getId() == 1) {
				    		
				    		// Arquivos adicionados pelo usuário na biblioteca de amostras
				    		if (rsAudioLibraryFiles.getString(2) == null) {
				    			objTableAudioFiles.addRowData("audio_file", rsAudioLibraryFiles.getString(1));
				    			
				    		// Arquivos da biblioteca de amostras
				    		} else {
				    			objTableAudioFiles.addRowData("audio_file", strFnjvPath + "\\" + rsAudioLibraryFiles.getString(2));
				    		}
				    		
				    	// Bibliotecas do usuário
				    	} else {
				    		objTableAudioFiles.addRowData("audio_file", rsAudioLibraryFiles.getString(1));
				    	}
				    }
				}
				
			} catch (SQLException e) {
				e.printStackTrace();
				
			} finally {
				objDatabaseConnection.rollBackTransaction();
				objDatabaseConnection.closeConnection();
			}
			
		// Biblioteca ainda não salva no banco de dados
		} else {
			if (!objAudioLibrary.getAudioLibraryAlreadyLoaded()) {
				for (int indexAudioLibraryFiles = 0; indexAudioLibraryFiles < objAudioLibrary.getListModelAudioLibrary().getSize(); indexAudioLibraryFiles++) {
					objTableAudioFiles.addRow();
					objTableAudioFiles.addRowData("audio_file", objAudioLibrary.getListModelAudioLibrary().getElementAt(indexAudioLibraryFiles).toString());
				}
			}
		}
	}
	
	/**
	 * Salva os dados da biblioteca de áudio.
	 */
	private void saveAudioLibraryData() {
		String strLibraryName = txtLibraryName.getValue().toString();
		
		// Verifica se o campo de nome está preenchido
		if (strLibraryName.trim().length() == 0) {
			WasisMessageBox.showMessageDialog(rsBundle.getString("save_audio_library_name_mandatory_field"), WasisMessageBox.WARNING_MESSAGE);
			txtLibraryName.setFocus();
			
		} else {
			objAudioLibrary.setName(txtLibraryName.getValue().toString());
			objAudioLibrary.setDescription(txtLibraryDescription.getValue().toString());
			objAudioLibrary.setObservations(txtLibraryObservations.getValue().toString());
			objAudioLibrary.saveAudioLibraryData(SaveAudioLibrary.this);
			
			objWasisDialog.setVisible(false);
		}
	}
	
	/**
	 * Atualiza os dados de uma biblioteca de áudio já criada.
	 */
	private void updateAudioLibraryData() {
		String strLibraryName = txtLibraryName.getValue().toString();
		
		// Verifica se o campo de nome está preenchido
		if (strLibraryName.trim().length() == 0) {
			WasisMessageBox.showMessageDialog(rsBundle.getString("save_audio_library_name_mandatory_field"), WasisMessageBox.WARNING_MESSAGE);
			txtLibraryName.setFocus();
			
		} else {
			objAudioLibrary.setName(txtLibraryName.getValue().toString());
			objAudioLibrary.setDescription(txtLibraryDescription.getValue().toString());
			objAudioLibrary.setObservations(txtLibraryObservations.getValue().toString());
			
			//final Toolkit toolkit = Toolkit.getDefaultToolkit();
			//final Dimension screenSize = toolkit.getScreenSize();
			//final int x = (screenSize.width - getWidth()) / 2;
			//final int y = (screenSize.height - getHeight()) / 2;
			
			objAudioLibrary.updateAudioLibraryData(SaveAudioLibrary.this);
			
			objWasisDialog.setVisible(false);
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
}