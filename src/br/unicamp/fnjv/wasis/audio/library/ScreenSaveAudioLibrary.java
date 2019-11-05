package br.unicamp.fnjv.wasis.audio.library;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;
import br.unicamp.fnjv.wasis.database.DatabaseFactory;
import br.unicamp.fnjv.wasis.database.dao.AudioLibraryFileDAO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryFileDTO;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

/**
 * Classe responsável pela exibição de uma tela que possui todos os campos
 * necessários para gravação de uma nova biblioteca no banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 30/Mar/2018
 */
public class ScreenSaveAudioLibrary extends JDialog {
	private static final long serialVersionUID = -3601217094327718541L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private AudioLibraryController objAudioLibraryController;
	private WasisDialog objWasisDialog;
	
	private LTTextField txtLibraryName;
	private LTTextField txtLibraryDescription;
	private LTTextField txtLibraryObservations;
	private LTTable objTableAudioLibraryFiles;
	private JButton btnSaveData;
	
	/**
	 * Classe responsável pela exibição de uma tela que possui todos os campos
	 * necessários para gravação de uma nova biblioteca no banco de dados.
	 * 
	 * @param objAudioLibraryController - Controller da biblioteca de áudio
	 */
	public ScreenSaveAudioLibrary(AudioLibraryController objAudioLibraryController) {
		this.objAudioLibraryController = objAudioLibraryController;
		
		loadScreen();
		loadAudioLibraryData();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		txtLibraryName = new LTTextField(rsBundle.getString("save_audio_library_name"), LTDataTypes.STRING, true, true, 50);
		txtLibraryDescription = new LTTextField(rsBundle.getString("save_audio_library_description"), LTDataTypes.STRING, true, true, 100);
		txtLibraryObservations = new LTTextField(rsBundle.getString("save_audio_library_observations"), LTDataTypes.TEXT, true, false);
		
		objTableAudioLibraryFiles = new LTTable(true, false);
		objTableAudioLibraryFiles.addColumn("audio_file_path", rsBundle.getString("save_audio_library_grid_audio_file"), LTDataTypes.TEXT, 670, false);
		objTableAudioLibraryFiles.showTable();
		
		// Botão Salvar Dados
		btnSaveData = new JButton(rsBundle.getString("save"));
		btnSaveData.setMinimumSize(new Dimension(100, 30));
		btnSaveData.setMaximumSize(new Dimension(400, 30));
		btnSaveData.setIconTextGap(15);
		btnSaveData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSaveData.setIcon(new ImageIcon("res/images/save.png"));
		btnSaveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveAudioLibrary();
			}
		});
		
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("wasis") + " - " + rsBundle.getString("audio_library"), true);
		objWasisDialog.setBounds(350, 350, 750, 500);
		objWasisDialog.setMinimumSize(new Dimension(750, 500));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][300.00][grow][]"));
		objWasisDialog.getContentPane().add(txtLibraryName, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(txtLibraryDescription, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(txtLibraryObservations, "cell 0 2, grow");
		objWasisDialog.getContentPane().add(objTableAudioLibraryFiles, "cell 0 3, grow");
		objWasisDialog.getContentPane().add(btnSaveData, "cell 0 4");
	}
	
	/**
	 * Carrega os dados de uma biblioteca de áudio.
	 */
	private void loadAudioLibraryData() {
		// Biblioteca de áudio já salva no banco de dados
		if (objAudioLibraryController.getAudioLibrary().getIdAudioLibrary() != 0) {
			txtLibraryName.setValue(objAudioLibraryController.getAudioLibrary().getLibraryName());
			txtLibraryDescription.setValue(objAudioLibraryController.getAudioLibrary().getLibraryDescription());
			txtLibraryObservations.setValue(objAudioLibraryController.getAudioLibrary().getLibraryObservations());
			
			try {
				AudioLibraryFileDAO objAudioLibraryFileDAO = DatabaseFactory.createAudioLibraryFileDAO();
				List<AudioLibraryFileDTO> lstAudioLibraryFiles = objAudioLibraryFileDAO.getAudioLibraryFiles(objAudioLibraryController.getAudioLibrary());
				
				File fileFnjv = new File("audio_samples/audio");
				String strFnjvPath = fileFnjv.getAbsoluteFile().getParentFile().getAbsolutePath();
				
			    for (AudioLibraryFileDTO objAudioLibraryFileDTO : lstAudioLibraryFiles) {
		    		// Arquivos adicionados pelo usuário na biblioteca
		    		if (objAudioLibraryFileDTO.getAudioFileSample() == null) {
		    			objTableAudioLibraryFiles.addRow();
		    			objTableAudioLibraryFiles.addRowData("audio_file_path", objAudioLibraryFileDTO.getAudioFilePath());
		    			
		    		// Arquivos originais da biblioteca de amostras
		    		} else {
		    			objTableAudioLibraryFiles.addRow();
		    			objTableAudioLibraryFiles.addRowData("audio_file_path", strFnjvPath + "\\" + objAudioLibraryFileDTO.getAudioFileSample());
		    		}
			    }
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		// Biblioteca ainda não salva no banco de dados
		} else {
			for (int indexAudioLibraryFiles = 0; indexAudioLibraryFiles < objAudioLibraryController.getListModelAudioLibrary().getSize(); indexAudioLibraryFiles++) {
				objTableAudioLibraryFiles.addRow();
				objTableAudioLibraryFiles.addRowData("audio_file_path", objAudioLibraryController.getListModelAudioLibrary().getElementAt(indexAudioLibraryFiles).toString());
			}
		}
	}
	
	/**
	 * Salva os dados da biblioteca de áudio.
	 */
	private void saveAudioLibrary() {
		if (txtLibraryName.getValue().toString().trim().length() == 0) {
			WasisMessageBox.showMessageDialog(rsBundle.getString("save_audio_library_name_mandatory_field"), WasisMessageBox.WARNING_MESSAGE);
			txtLibraryName.setFocus();
		} else {
			objAudioLibraryController.getAudioLibrary().setLibraryName(txtLibraryName.getValue().toString());
			objAudioLibraryController.getAudioLibrary().setLibraryDescription(txtLibraryDescription.getValue().toString());
			objAudioLibraryController.getAudioLibrary().setLibraryObservations(txtLibraryObservations.getValue().toString());
			objAudioLibraryController.saveAudioLibrary();
			
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