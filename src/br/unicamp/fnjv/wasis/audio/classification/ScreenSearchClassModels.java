package br.unicamp.fnjv.wasis.audio.classification;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;

import net.miginfocom.swing.MigLayout;

/**
 * Consulta de Modelos de Classes.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 02/Abr/2018
 */
public class ScreenSearchClassModels extends JDialog {
	private static final long serialVersionUID = -863524619647221438L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private int intIdClassModel;
	
	private LTTextField txtClassModelDescription;
	
	private JPanel panelClassModels;
	private LTTable objTableClassModels;
	
	private final int SEARCH_LIMIT = 50;
	
	/**
	 * Retorna o ID do registro selecionado.
	 * 
	 * @return intIdClassModel
	 */
	protected int getIdClassModel() {
		return intIdClassModel;
	}
	
	/**
	 * Consulta de Modelos de Classes.
	 */
	protected ScreenSearchClassModels() {
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		txtClassModelDescription = new LTTextField("Descrição:", LTDataTypes.STRING, true, false, 200);
		txtClassModelDescription.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadClassModels(txtClassModelDescription);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		panelClassModels = new JPanel();
		panelClassModels.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		objTableClassModels = new LTTable(true);
		objTableClassModels.addColumn("id_class_model", "ID", LTDataTypes.INTEGER, 0, false);
		objTableClassModels.addColumn("class_model_description", "Descrição", LTDataTypes.STRING, 360, false);
		objTableClassModels.addColumn("date_creation", "Data Criação", LTDataTypes.DATE, 110, false);
		objTableClassModels.addColumn("animal_class", rsBundle.getString("animal_class"), LTDataTypes.STRING, 210, false);
		objTableClassModels.addMouseListener(new ClassModelMouseAdapter());
		objTableClassModels.showTable();
		
		JLabel lblSearchLimit = new JLabel(" " + rsBundle.getString("screen_search_animal_taxonomy_search_limit") + SEARCH_LIMIT + " " + rsBundle.getString("screen_search_animal_taxonomy_search_limit_records"));
		lblSearchLimit.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog("Consulta de Modelos de Classes", true);
		objWasisDialog.setBounds(350, 350, 750, 450);
		objWasisDialog.setMinimumSize(new Dimension(750, 450));
		objWasisDialog.setMaximumSize(new Dimension(750, 450));
		objWasisDialog.setResizable(false);
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][grow]"));
		
		objWasisDialog.getContentPane().add(txtClassModelDescription, "cell 0 0, grow, width 200");
		
		objWasisDialog.getContentPane().add(panelClassModels, "cell 0 3, grow, gap 0 0 5 0");
		
		panelClassModels.add(objTableClassModels, "cell 0 0, grow");
		panelClassModels.add(lblSearchLimit, "cell 0 1, grow");
		
		loadClassModels(txtClassModelDescription);
	}
	
	/**
	 * Carrega os dados taxonômicos já gravadas no banco de dados.
	 */
	private void loadClassModels(LTTextField textField) {
		final String strTextField = textField.getValue().toString();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT * ");
			objDatabaseConnection.sqlCommandAppend("FROM class_models ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_class_model > 0 ");
			objDatabaseConnection.sqlCommandAppend("AND ind_active = 1 ");
			
			// Filtro Estado
			if (txtClassModelDescription.getValue() != null && txtClassModelDescription.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND class_model_description LIKE '%" + txtClassModelDescription.getValue() + "%'");
			}
			
			objDatabaseConnection.sqlCommandAppend("ORDER BY class_model_description, date_creation DESC ");
			objDatabaseConnection.sqlCommandAppend("LIMIT " + SEARCH_LIMIT);
			
			ResultSet rsClassModels = objDatabaseConnection.executeSelectQuery();
			
			if (strTextField.equals(textField.getValue().toString())) {
				objTableClassModels.deleteRows();
				
				while (rsClassModels.next()) {
					objTableClassModels.addRow();
					objTableClassModels.addRowData("id_class_model", rsClassModels.getInt("id_class_model"));
					objTableClassModels.addRowData("class_model_description", rsClassModels.getString("class_model_description"));
					objTableClassModels.addRowData("date_creation", rsClassModels.getString("date_creation"));
					objTableClassModels.addRowData("animal_class", rsClassModels.getString("animal_class"));
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Abre os dados do banco de dados.
	 */
	private void openClassModel() {
		if (objTableClassModels.getRowCount() > 0) {
			int intIndexRow = objTableClassModels.getSelectedRow();
			
			intIdClassModel = (int) objTableClassModels.getValue(intIndexRow, "id_class_model");
			
			objWasisDialog.setVisible(false);
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	protected void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável pelo carregamento de uma descrição quando houver duplo clique em um registro.
	 */
	private class ClassModelMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 2) {
				if (objTableClassModels.getRowCount() > 0) {
					openClassModel();
				}
			}
	    }
	}
}