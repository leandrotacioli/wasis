package br.unicamp.fnjv.wasis.audio;

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
 * Consulta de Dados Taxonômicos.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 22/Set/2017
 */
public class ScreenSearchAnimalTaxonomy extends JDialog {
	private static final long serialVersionUID = -233893933659975511L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private int intIdAnimalTaxonomy;
	
	private LTTextField txtAnimalPhylum;
	private LTTextField txtAnimalClass;
	private LTTextField txtAnimalOrder;
	private LTTextField txtAnimalFamily;
	private LTTextField txtAnimalGenus;
	private LTTextField txtAnimalSpecies;
	private LTTextField txtAnimalNamePortuguese;
	private LTTextField txtAnimalNameEnglish;
	
	private JPanel panelAnimalTaxonomies;
	private LTTable objTableAnimalTaxonomies;
	
	private final int SEARCH_LIMIT = 50;
	
	/**
	 * Retorna o ID do registro selecionado.
	 * 
	 * @return intIdAnimalTaxonomy
	 */
	public int getIdAnimalTaxonomy() {
		return intIdAnimalTaxonomy;
	}
	
	/**
	 * Consulta de Dados Taxonômicos.
	 */
	protected ScreenSearchAnimalTaxonomy() {
		this.intIdAnimalTaxonomy = 0;
		
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		txtAnimalPhylum = new LTTextField(rsBundle.getString("animal_phylum") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalPhylum.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalPhylum);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		txtAnimalClass = new LTTextField(rsBundle.getString("animal_class") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalClass.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalClass);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		txtAnimalOrder = new LTTextField(rsBundle.getString("animal_order") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalOrder.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalOrder);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		txtAnimalFamily = new LTTextField(rsBundle.getString("animal_family") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalFamily.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalFamily);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		txtAnimalGenus = new LTTextField(rsBundle.getString("animal_genus") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalGenus.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalGenus);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		txtAnimalSpecies = new LTTextField(rsBundle.getString("animal_species") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalSpecies.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalSpecies);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		txtAnimalNamePortuguese = new LTTextField(rsBundle.getString("animal_name_portuguese") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalNamePortuguese.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalNamePortuguese);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		txtAnimalNameEnglish = new LTTextField(rsBundle.getString("animal_name_english") + ":", LTDataTypes.STRING, true, false, 200);
		txtAnimalNameEnglish.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadAnimalTaxonomies(txtAnimalNameEnglish);
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		panelAnimalTaxonomies = new JPanel();
		panelAnimalTaxonomies.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		objTableAnimalTaxonomies = new LTTable(true);
		objTableAnimalTaxonomies.addColumn("id_animal_taxonomy", "ID", LTDataTypes.INTEGER, 0, false);
		objTableAnimalTaxonomies.addColumn("animal_phylum", rsBundle.getString("animal_phylum"), LTDataTypes.STRING, 80, false);
		objTableAnimalTaxonomies.addColumn("animal_class", rsBundle.getString("animal_class"), LTDataTypes.STRING, 80, false);
		objTableAnimalTaxonomies.addColumn("animal_order", rsBundle.getString("animal_order"), LTDataTypes.STRING, 100, false);
		objTableAnimalTaxonomies.addColumn("animal_family", rsBundle.getString("animal_family"), LTDataTypes.STRING, 100, false);
		objTableAnimalTaxonomies.addColumn("animal_genus", rsBundle.getString("animal_genus"), LTDataTypes.STRING, 100, false);
		objTableAnimalTaxonomies.addColumn("animal_species", rsBundle.getString("animal_species"), LTDataTypes.STRING, 100, false);
		objTableAnimalTaxonomies.addColumn("animal_name_portuguese", rsBundle.getString("animal_name_portuguese"), LTDataTypes.STRING, 115, false);
		objTableAnimalTaxonomies.addColumn("animal_name_english", rsBundle.getString("animal_name_english"), LTDataTypes.STRING, 115, false);
		objTableAnimalTaxonomies.addMouseListener(new AnimalTaxonomyMouseAdapter());
		objTableAnimalTaxonomies.showTable();
		
		JLabel lblSearchLimit = new JLabel(" " + rsBundle.getString("screen_search_animal_taxonomy_search_limit") + SEARCH_LIMIT + " " + rsBundle.getString("screen_search_animal_taxonomy_search_limit_records"));
		lblSearchLimit.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_search_animal_taxonomy_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 850, 500);
		objWasisDialog.setMinimumSize(new Dimension(850, 500));
		objWasisDialog.setMaximumSize(new Dimension(850, 500));
		objWasisDialog.setResizable(false);
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][][grow]"));
		
		objWasisDialog.getContentPane().add(txtAnimalPhylum, "cell 0 0, grow, width 200");
		objWasisDialog.getContentPane().add(txtAnimalClass, "cell 0 0, grow, width 200");
		objWasisDialog.getContentPane().add(txtAnimalOrder, "cell 0 0, grow, width 200");
		
		objWasisDialog.getContentPane().add(txtAnimalFamily, "cell 0 1, grow, width 200");
		objWasisDialog.getContentPane().add(txtAnimalGenus, "cell 0 1, grow, width 200");
		objWasisDialog.getContentPane().add(txtAnimalSpecies, "cell 0 1, grow, width 200");
		
		objWasisDialog.getContentPane().add(txtAnimalNamePortuguese, "cell 0 2, grow, width 200");
		objWasisDialog.getContentPane().add(txtAnimalNameEnglish, "cell 0 2, grow, width 200");
		
		objWasisDialog.getContentPane().add(panelAnimalTaxonomies, "cell 0 3, grow, gap 0 0 5 0");
		
		panelAnimalTaxonomies.add(objTableAnimalTaxonomies, "cell 0 0, grow");
		panelAnimalTaxonomies.add(lblSearchLimit, "cell 0 1, grow");
		
		loadAnimalTaxonomies(txtAnimalPhylum);
	}
	
	/**
	 * Carrega os dados taxonômicos já gravadas no banco de dados.
	 */
	private void loadAnimalTaxonomies(LTTextField textField) {
		final String strTextField = textField.getValue().toString();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT * ");
			objDatabaseConnection.sqlCommandAppend("FROM animal_taxonomies ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_animal_taxonomy > 0 ");
			
			// Filtro Filo
			if (txtAnimalPhylum.getValue() != null && txtAnimalPhylum.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_phylum LIKE '%" + txtAnimalPhylum.getValue() + "%'");
			}
			
			// Filtro Classe
			if (txtAnimalClass.getValue() != null && txtAnimalClass.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_class LIKE '%" + txtAnimalClass.getValue() + "%'");
			}
			
			// Filtro Ordem
			if (txtAnimalOrder.getValue() != null && txtAnimalOrder.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_order LIKE '%" + txtAnimalOrder.getValue() + "%'");
			}
			
			// Filtro Família
			if (txtAnimalFamily.getValue() != null && txtAnimalFamily.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_family LIKE '%" + txtAnimalFamily.getValue() + "%'");
			}
			
			// Filtro Gênero
			if (txtAnimalGenus.getValue() != null && txtAnimalGenus.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_genus LIKE '%" + txtAnimalGenus.getValue() + "%'");
			}
			
			// Filtro Espécie
			if (txtAnimalSpecies.getValue() != null && txtAnimalSpecies.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_species LIKE '%" + txtAnimalSpecies.getValue() + "%'");
			}
			
			// Filtro Nome Popular Portugues
			if (txtAnimalNamePortuguese.getValue() != null && txtAnimalNamePortuguese.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_name_portuguese LIKE '%" + txtAnimalNamePortuguese.getValue() + "%'");
			}
			
			// Filtro Estado
			if (txtAnimalNameEnglish.getValue() != null && txtAnimalNameEnglish.getValue().toString().trim().length() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND animal_name_english LIKE '%" + txtAnimalNameEnglish.getValue() + "%'");
			}
			
			objDatabaseConnection.sqlCommandAppend("ORDER BY animal_phylum, animal_class, animal_order, animal_family, animal_genus, animal_species ");
			objDatabaseConnection.sqlCommandAppend("LIMIT " + SEARCH_LIMIT);
			
			ResultSet rsAnimalTaxonomy = objDatabaseConnection.executeSelectQuery();
			
			if (strTextField.equals(textField.getValue().toString())) {
				objTableAnimalTaxonomies.deleteRows();
				
				while (rsAnimalTaxonomy.next()) {
					objTableAnimalTaxonomies.addRow();
					objTableAnimalTaxonomies.addRowData("id_animal_taxonomy", rsAnimalTaxonomy.getInt("id_animal_taxonomy"));
					objTableAnimalTaxonomies.addRowData("animal_phylum", rsAnimalTaxonomy.getString("animal_phylum"));
					objTableAnimalTaxonomies.addRowData("animal_class", rsAnimalTaxonomy.getString("animal_class"));
					objTableAnimalTaxonomies.addRowData("animal_order", rsAnimalTaxonomy.getString("animal_order"));
					objTableAnimalTaxonomies.addRowData("animal_family", rsAnimalTaxonomy.getString("animal_family"));
					objTableAnimalTaxonomies.addRowData("animal_genus", rsAnimalTaxonomy.getString("animal_genus"));
					objTableAnimalTaxonomies.addRowData("animal_species", rsAnimalTaxonomy.getString("animal_species"));
					objTableAnimalTaxonomies.addRowData("animal_name_portuguese", rsAnimalTaxonomy.getString("animal_name_portuguese"));
					objTableAnimalTaxonomies.addRowData("animal_name_english", rsAnimalTaxonomy.getString("animal_name_english"));
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
	 * Abre os dados taxonômicos do banco de dados.
	 */
	private void openAnimalTaxonomy() {
		if (objTableAnimalTaxonomies.getRowCount() > 0) {
			int intIndexRow = objTableAnimalTaxonomies.getSelectedRow();
			
			intIdAnimalTaxonomy = (int) objTableAnimalTaxonomies.getValue(intIndexRow, "id_animal_taxonomy");
			
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
	private class AnimalTaxonomyMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 2) {
				if (objTableAnimalTaxonomies.getRowCount() > 0) {
					openAnimalTaxonomy();
				}
			}
	    }
	}
}