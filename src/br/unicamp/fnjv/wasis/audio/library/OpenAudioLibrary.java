package br.unicamp.fnjv.wasis.audio.library;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;

import br.unicamp.fnjv.wasis.database.DatabaseConnection;
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
 * Classe responsável pela exibição de uma tela que
 * possui todos os campos necessários para abertura
 * de uma biblioteca do banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 1.2 - 23/Nov/2014
 */
public class OpenAudioLibrary extends JDialog {
	private static final long serialVersionUID = -8278206858088517479L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private AudioLibrary objAudioLibrary;
	
	private WasisDialog objWasisDialog;
	private LTTable objTableAudioLibraries;
	private JButton btnOpenAudioLibrary;
	
	/**
	 * Classe responsável pela exibição de uma tela que
	 * possui todos os campos necessários para abertura
	 * de uma biblioteca do banco de dados.
	 * 
	 * @param objAudioLibrary - Objeto da biblioteca de áudio
	 */
	public OpenAudioLibrary(AudioLibrary objAudioLibrary) {
		this.objAudioLibrary = objAudioLibrary;
		
		loadScreen();
		loadAudioLibraries();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {	
		objTableAudioLibraries = new LTTable(true, false);
		objTableAudioLibraries.addColumn("id", "ID", LTDataTypes.LONG, 0, false);
		objTableAudioLibraries.addColumn("library_name", rsBundle.getString("open_audio_library_name"), LTDataTypes.STRING, 300, false);
		objTableAudioLibraries.addColumn("library_description", rsBundle.getString("open_audio_library_description"), LTDataTypes.STRING, 330, false);
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
		objWasisDialog.setBounds(350, 350, 700, 500);
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[grow][]"));
		objWasisDialog.getContentPane().add(objTableAudioLibraries, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(btnOpenAudioLibrary, "cell 0 1");
		objWasisDialog.setVisible(false);
	}
	
	/**
	 * Carrega as bibliotecas de áudio armazenadas no banco de dados.
	 */
	private void loadAudioLibraries() {
		DatabaseConnection objDbConnection = DatabaseConnection.getInstance();
		
		try {
			objDbConnection.openConnection();
			objDbConnection.initiliazeStatement();
			objDbConnection.sqlCommand("SELECT id, library_name, library_description");
			objDbConnection.sqlCommandAppend("FROM libraries");
			objDbConnection.sqlCommandAppend("ORDER BY library_name");

		    ResultSet rsLibraries = objDbConnection.executeSelectQuery();
		    
		    while (rsLibraries.next()) {
		    	objTableAudioLibraries.addRow();
		    	objTableAudioLibraries.addRowData("id", rsLibraries.getInt(1));
		    	objTableAudioLibraries.addRowData("library_name", rsLibraries.getString(2));
		    	objTableAudioLibraries.addRowData("library_description", rsLibraries.getString(3));
		    }
		    
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDbConnection.rollBackTransaction();
			objDbConnection.closeConnection();
		}
	}
	
	/**
	 * Abre a biblioteca do banco de dados.
	 */
	private void openAudioLibrary() {
		if (objTableAudioLibraries.getRowCount() > 0) {
			int intIndexRow = objTableAudioLibraries.getSelectedRow();
			
			objAudioLibrary.openAudioLibrary((long) objTableAudioLibraries.getValue(intIndexRow, "id"));
			
			objWasisDialog.setVisible(false);
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável pelo
	 * carregamento de uma biblioteca quando houver 
	 * duplo clique em um registro.
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