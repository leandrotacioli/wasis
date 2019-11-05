package br.unicamp.fnjv.wasis.audio.classification;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.table.TableListener;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

/**
 * Filtro de campos da consulta.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 03/Abr/2018
 */
public class ScreenAudioClassificationFiltersSearch extends JDialog implements TableListener {
	private static final long serialVersionUID = 2123701085493868859L;
	
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private ScreenAudioClassificationFilters objFilters;
	
	private WasisDialog objWasisDialog;

	private LTTextField txtDatabaseColumn;
	private String strDatabaseColumn;
	private String strLabel; 
	private List<String> lstRecords;
	
	private JPanel panelRecords;
	private LTTable objTableRecords;
	
	private JButton btnSelectAllRecords;
	private JButton btnCleanSelections;
	
	private DatabaseConnection objDatabaseConnection;
	
	/** Quantidade máxima de registros retornados na consulta. */
	private final int SEARCH_LIMIT = 50;
	
	/** Determina se foi o usuário que fez alteração na tabela **/
	private boolean blnUserTableUpdate;

	/**
	 * Filtro de campos da consulta.
	 * 
	 * @param ScreenAudioClassificationFilters - Objeto pai desta classe
	 * @param strDatabaseColumn                - Coluna do banco de dados
	 * @param strLabel                         - Label da coluna
	 * @param lstRecords                       - Lista de registros a ser manipulada
	 */
	protected ScreenAudioClassificationFiltersSearch(ScreenAudioClassificationFilters objFilters, String strDatabaseColumn, String strLabel, List<String> lstRecords) {
		this.objFilters = objFilters;
		this.strDatabaseColumn = strDatabaseColumn;
		this.strLabel = strLabel;
		this.lstRecords = lstRecords;
		
		objDatabaseConnection = DatabaseConnection.getInstance();

		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// Cria os componentes da tela
		txtDatabaseColumn = new LTTextField(strLabel, LTDataTypes.STRING, true, false, 200);
		txtDatabaseColumn.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent event) {
				
			}
			
			@Override
			public void keyReleased(KeyEvent event) {
				loadData();
			}
			
			@Override
			public void keyPressed(KeyEvent event) {
				
			}
		});
		
		// ***********************************************************************************************************************
		// Registros do banco de dados
		panelRecords = new JPanel();
		panelRecords.setLayout(new MigLayout("insets 0", "[grow]", "[grow][]"));
		panelRecords.setBackground(WasisParameters.COLOR_BACKGROUND);
		
		objTableRecords = new LTTable(false);
		objTableRecords.addColumn("selection", "", LTDataTypes.BOOLEAN, 30, true);
		objTableRecords.addColumn("description", strLabel, LTDataTypes.STRING, 440, false);
		objTableRecords.addTableListener(ScreenAudioClassificationFiltersSearch.this);
		objTableRecords.showTable();
		
		JLabel lblSearchLimit = new JLabel(" " + rsBundle.getString("screen_audio_classification_filters_search_select_limit_1") 
				                           + SEARCH_LIMIT 
				                           + " " + rsBundle.getString("screen_audio_classification_filters_search_select_limit_2"));
		
		lblSearchLimit.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		// ***********************************************************************************************************************
		// Selecionar Todos os Registros
		btnSelectAllRecords = new JButton(rsBundle.getString("screen_audio_classification_filters_search_button_select_all_records"));
		btnSelectAllRecords.setMinimumSize(new Dimension(250, 30));
		btnSelectAllRecords.setIconTextGap(15);
		btnSelectAllRecords.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSelectAllRecords.setIcon(new ImageIcon("res/images/select_all.png"));
		btnSelectAllRecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				for (int intRowIndex = 0; intRowIndex < objTableRecords.getRowCount(); intRowIndex++) {
					blnUserTableUpdate = false;
					
					// Somente seleciona os registros não selecionados anteriormente
					if (objTableRecords.getValue(intRowIndex, "selection") == null || (boolean) objTableRecords.getValue(intRowIndex, "selection") == false) {
						objTableRecords.setValue(true, intRowIndex, "selection");
					}
					
					blnUserTableUpdate = true;
				}
				
				Collections.sort(lstRecords);
			}
		});
		
		// Limpar Seleções
		btnCleanSelections = new JButton(rsBundle.getString("screen_audio_classification_filters_search_button_clean_all_records"));
		btnCleanSelections.setMinimumSize(new Dimension(250, 30));
		btnCleanSelections.setIconTextGap(15);
		btnCleanSelections.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCleanSelections.setIcon(new ImageIcon("res/images/clean.png"));
		btnCleanSelections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				blnUserTableUpdate = false;
				
				for (int intRowIndex = 0; intRowIndex < objTableRecords.getRowCount(); intRowIndex++) {
					objTableRecords.setValue(false, intRowIndex, "selection");
				}
				
				lstRecords = new ArrayList<String>();
				
				blnUserTableUpdate = true;
			}
		});
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_classification_filters_search_screen_description") + " " + strLabel.replace(":", ""), true);
		objWasisDialog.setBounds(350, 350, 550, 400);
		objWasisDialog.setMinimumSize(new Dimension(550, 400));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][]"));
		objWasisDialog.getContentPane().add(txtDatabaseColumn, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelRecords, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(btnSelectAllRecords, "cell 0 2, grow");
		objWasisDialog.getContentPane().add(btnCleanSelections, "cell 0 2, grow");
		
		panelRecords.add(objTableRecords, "cell 0 0, grow");
		panelRecords.add(lblSearchLimit, "cell 0 1, grow");
		
		loadData();
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	protected void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Carrega as informações dos filtros com os dados já existentes no banco de dados.
	 */
	private void loadData() {
		final String strDatabaseColumnTyped = txtDatabaseColumn.getValue().toString();
		
		SwingWorker<Void, Void> swingWorkerSearch = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					blnUserTableUpdate = false;
					
					objDatabaseConnection.openConnection();
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT " + strDatabaseColumn);
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files           aud ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments  seg   ON   seg.fk_audio_file      = aud.id_audio_file ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN animal_taxonomies     tax   ON   seg.fk_animal_taxonomy = tax.id_animal_taxonomy ");
					objDatabaseConnection.sqlCommandAppend("WHERE " + strDatabaseColumn + " IS NOT NULL ");
					objDatabaseConnection.sqlCommandAppend("AND " + strDatabaseColumn + " != '' ");
					
					if (txtDatabaseColumn.getValue() != null && txtDatabaseColumn.getValue().toString().trim().length() > 0) {
						objDatabaseConnection.sqlCommandAppend("AND " + strDatabaseColumn + " LIKE '" + txtDatabaseColumn.getValue() + "%'");
					}
					
					// Filtro Classe
					if (strDatabaseColumn.equals("animal_class")) {
						addDatabaseSelectFilter("animal_phylum", objFilters.getAnimalPhylum());
						
					// Filtro Ordem
					} else if (strDatabaseColumn.equals("animal_order")) {
						addDatabaseSelectFilter("animal_phylum", objFilters.getAnimalPhylum());
						addDatabaseSelectFilter("animal_class", objFilters.getAnimalClass());
						
					// Filtro Família
					} else if (strDatabaseColumn.equals("animal_family")) {
						addDatabaseSelectFilter("animal_phylum", objFilters.getAnimalPhylum());
						addDatabaseSelectFilter("animal_class", objFilters.getAnimalClass());
						addDatabaseSelectFilter("animal_order", objFilters.getAnimalOrder());
						
					// Filtro Gênero
					} else if (strDatabaseColumn.equals("animal_genus")) {
						addDatabaseSelectFilter("animal_phylum", objFilters.getAnimalPhylum());
						addDatabaseSelectFilter("animal_class", objFilters.getAnimalClass());
						addDatabaseSelectFilter("animal_order", objFilters.getAnimalOrder());
						addDatabaseSelectFilter("animal_family", objFilters.getAnimalFamily());
						
					// Filtro Espécie
					} else if (strDatabaseColumn.equals("animal_species")) {
						addDatabaseSelectFilter("animal_phylum", objFilters.getAnimalPhylum());
						addDatabaseSelectFilter("animal_class", objFilters.getAnimalClass());
						addDatabaseSelectFilter("animal_order", objFilters.getAnimalOrder());
						addDatabaseSelectFilter("animal_family", objFilters.getAnimalFamily());
						addDatabaseSelectFilter("animal_genus", objFilters.getAnimalGenus());
						
					// Filtro Estado
					} else if (strDatabaseColumn.equals("location_state")) {
						addDatabaseSelectFilter("location_country", objFilters.getLocationCountry());

					// Filtro Cidade
					} else if (strDatabaseColumn.equals("location_city")) {
						addDatabaseSelectFilter("location_country", objFilters.getLocationCountry());
						addDatabaseSelectFilter("location_state", objFilters.getLocationState());
					}
					
					objDatabaseConnection.sqlCommandAppend("GROUP BY " + strDatabaseColumn);
					objDatabaseConnection.sqlCommandAppend("ORDER BY " + strDatabaseColumn);
					objDatabaseConnection.sqlCommandAppend("LIMIT " + SEARCH_LIMIT);
					
					ResultSet rsDatabaseRecords = objDatabaseConnection.executeSelectQuery();
					
					if (strDatabaseColumnTyped.equals(txtDatabaseColumn.getValue().toString())) {
						objTableRecords.deleteRows();
						
						while (rsDatabaseRecords.next()) {
							objTableRecords.addRow();
							
							for (int indexRecords = 0; indexRecords < lstRecords.size(); indexRecords++) {
								if (lstRecords.get(indexRecords).equals(rsDatabaseRecords.getString(strDatabaseColumn))) {
									objTableRecords.addRowData("selection", true);
									break;
								} else {
									objTableRecords.addRowData("selection", false);
								}
							}
							
							objTableRecords.addRowData("description", rsDatabaseRecords.getString(strDatabaseColumn));
						}
					}

				} catch (SQLException e) {
					e.printStackTrace();
					
				} finally {
					objDatabaseConnection.rollBackTransaction();
					objDatabaseConnection.closeConnection();
				}
		
				return null;
			}
			
			@Override
			protected void done() {
			    try {
					get();
					
					objTableRecords.updateTableData();
					
					blnUserTableUpdate = true;
	    			
				} catch (InterruptedException e) {
					
				} catch (ExecutionException e) {
					
				}
			}
		};

		swingWorkerSearch.execute();
	}
	
	/**
	 * Adiciona um filtro à consulta do banco de dados.
	 * 
	 * @param strDatabaseColumn - Coluna a ser filtrada
	 * @param lstFilter         - Lista com os valores da coluna a serem filtrados
	 */
	private void addDatabaseSelectFilter(String strDatabaseColumn, List<String> lstFilter) {
		try {
			if (lstFilter.size() > 0) {
				objDatabaseConnection.sqlCommandAppend("AND ( ");
				
				for (int indexFilter = 0; indexFilter < lstFilter.size(); indexFilter++) {
					if (indexFilter > 0) {
						objDatabaseConnection.sqlCommandAppend("OR ");
					}
					
					objDatabaseConnection.sqlCommandAppend(strDatabaseColumn + " = ? ");
					objDatabaseConnection.addParameter(strDatabaseColumn, LTDataTypes.STRING, lstFilter.get(indexFilter));
				}
				
				objDatabaseConnection.sqlCommandAppend(") ");
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// ***********************************************************************************************************************
	// Implementa TableListener
	@Override
	public void cellValueUpdated(Object objValue, int intRowIndex, int intColumnIndex) {
		if (objValue != null && blnUserTableUpdate) {
			// Caso o registro tenha sido marcado
			if (objTableRecords.getColumnName(intColumnIndex).equals("selection")) {
				
				// Caso ele tinha sido marcado, é adicionado à lista
				if ((boolean) objValue) {
					lstRecords.add((String) objTableRecords.getValue(intRowIndex, "description"));
					
				// Caso ele tinha sido desmarcado, é removido da lista
				} else {
					for (int indexRecords = 0; indexRecords < lstRecords.size(); indexRecords++) {
						if (objTableRecords.getValue(intRowIndex, "description").equals(lstRecords.get(indexRecords))) {
							lstRecords.remove(indexRecords);
							break;
						}
					}
				}
			}
		}
	}
}