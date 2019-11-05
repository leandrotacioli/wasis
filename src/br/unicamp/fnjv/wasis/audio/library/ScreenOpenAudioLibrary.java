package br.unicamp.fnjv.wasis.audio.library;

import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import br.unicamp.fnjv.wasis.database.DatabaseFactory;
import br.unicamp.fnjv.wasis.database.dao.AudioLibraryDAO;
import br.unicamp.fnjv.wasis.database.dto.AudioLibraryDTO;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Classe responsável pela exibição de uma tela que consulta
 * as bibliotecas de áudio e permite a abertura de uma delas.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 30/Mar/2018
 */
public class ScreenOpenAudioLibrary extends JDialog {
	private static final long serialVersionUID = -8278206858088517479L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private AudioLibraryController objAudioLibraryController;
	
	private WasisDialog objWasisDialog;
	private LTTable objTableAudioLibraries;
	private JButton btnOpenAudioLibrary;
	
	/**
	 * Classe responsável pela exibição de uma tela que consulta
	 * as bibliotecas de áudio e permite a abertura de uma delas.
	 * 
	 * @param objAudioLibraryController - Controller da biblioteca de áudio
	 */
	public ScreenOpenAudioLibrary(AudioLibraryController objAudioLibraryController) {
		this.objAudioLibraryController = objAudioLibraryController;
		
		loadScreen();
		loadAudioLibraries();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {	
		objTableAudioLibraries = new LTTable(true, false);
		objTableAudioLibraries.addColumn("id_audio_library", "ID", LTDataTypes.LONG, 0, false);
		objTableAudioLibraries.addColumn("library_name", rsBundle.getString("open_audio_library_name"), LTDataTypes.STRING, 280, false);
		objTableAudioLibraries.addColumn("library_description", rsBundle.getString("open_audio_library_description"), LTDataTypes.STRING, 400, false);
		objTableAudioLibraries.addColumn("library_observations", rsBundle.getString("open_audio_library_observations"), LTDataTypes.STRING, 0, false);
		objTableAudioLibraries.addMouseListener(new AudioLibraryMouseAdapter());
		objTableAudioLibraries.showTable();
		
		btnOpenAudioLibrary = new JButton(rsBundle.getString("open_audio_library_button_open_audio_library"));
		btnOpenAudioLibrary.setMinimumSize(new Dimension(100, 30));
		btnOpenAudioLibrary.setMaximumSize(new Dimension(400, 30));
		btnOpenAudioLibrary.setIconTextGap(15);
		btnOpenAudioLibrary.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnOpenAudioLibrary.setIcon(new ImageIcon("res/images/open.png"));
		btnOpenAudioLibrary.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				openAudioLibrary();
			}
		});
		
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("open_audio_library_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 750, 500);
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[grow][]"));
		objWasisDialog.getContentPane().add(objTableAudioLibraries, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(btnOpenAudioLibrary, "cell 0 1");
		objWasisDialog.setVisible(false);
	}
	
	/**
	 * Carrega as bibliotecas de áudio armazenadas no banco de dados.
	 */
	private void loadAudioLibraries() {
		try {
			AudioLibraryDAO objAudioLibraryDAO = DatabaseFactory.createAudioLibraryDAO();
			
			List<AudioLibraryDTO> lstAudioLibraries = objAudioLibraryDAO.getAudioLibraries();
			
			for (AudioLibraryDTO objAudioLibrary : lstAudioLibraries) {
				objTableAudioLibraries.addRow();
		    	objTableAudioLibraries.addRowData("id_audio_library", objAudioLibrary.getIdAudioLibrary());
		    	objTableAudioLibraries.addRowData("library_name", objAudioLibrary.getLibraryName());
		    	objTableAudioLibraries.addRowData("library_description", objAudioLibrary.getLibraryDescription());
		    	objTableAudioLibraries.addRowData("library_observations", objAudioLibrary.getLibraryObservations());
			}
		    
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Abre a biblioteca do banco de dados.
	 */
	private void openAudioLibrary() {
		if (objTableAudioLibraries.getRowCount() > 0) {
			int intIndexRow = objTableAudioLibraries.getSelectedRow();
			
			if (intIndexRow >= 0) {
				AudioLibraryDTO objAudioLibrary = new AudioLibraryDTO();
				objAudioLibrary.setIdAudioLibrary((long) objTableAudioLibraries.getValue(intIndexRow, "id_audio_library"));
				objAudioLibrary.setLibraryName((String) objTableAudioLibraries.getValue(intIndexRow, "library_name"));
				objAudioLibrary.setLibraryDescription((String) objTableAudioLibraries.getValue(intIndexRow, "library_description"));
				objAudioLibrary.setLibraryObservations((String) objTableAudioLibraries.getValue(intIndexRow, "library_observations"));
				
				objAudioLibraryController.openAudioLibrary(objAudioLibrary);
				
				objWasisDialog.setVisible(false);
			}
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável pelo carregamento 
	 * de uma biblioteca quando houver duplo clique em um registro.
	 */
	private class AudioLibraryMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 2) {
				if (objTableAudioLibraries.getRowCount() > 0) {
					openAudioLibrary();
				}
			}
	    }
	}
}