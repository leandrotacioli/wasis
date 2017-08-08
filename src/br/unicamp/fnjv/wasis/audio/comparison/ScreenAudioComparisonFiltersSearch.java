package br.unicamp.fnjv.wasis.audio.comparison;

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
import br.unicamp.fnjv.wasis.database.DatabaseConnection;
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
 * @version 1.0 - 12/Mai/2015
 */
public class ScreenAudioComparisonFiltersSearch extends JDialog implements TableListener {
	private static final long serialVersionUID = 2123701085493868859L;
	
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;

	private LTTextField txtDatabaseColumn;
	private String strDatabaseColumn;
	private String strLabel; 
	private List<String> lstRecords;
	
	private JPanel panelDatabaseRecords;
	private LTTable objTableDatabaseRecords;
	
	private JButton btnSelectAllRecords;
	private JButton btnCleanSelections;
	
	private List<String> lstAnimalPhylum;
	private List<String> lstAnimalClass;
	private List<String> lstAnimalOrder;
	private List<String> lstAnimalFamily;
	private List<String> lstAnimalGenus;
	private List<String> lstLocationCountry;
	private List<String> lstLocationState;
	
	private DatabaseConnection objDatabaseConnection;
	
	/**
	 * Quantidade máxima de registros retornados na consulta.
	 */
	private final int SEARCH_LIMIT = 50;
	
	/**
	 * Altera o filtro do <i>Filo</i>.
	 * 
	 * @param lstAnimalPhylum
	 */
	protected void setAnimalPhylum(List<String> lstAnimalPhylum) {
		this.lstAnimalPhylum = lstAnimalPhylum;
	}
	
	/**
	 * Altera o filtro da <i>Classe</i>.
	 * 
	 * @param lstAnimalClass
	 */
	protected void setAnimalClass(List<String> lstAnimalClass) {
		this.lstAnimalClass = lstAnimalClass;
	}
	
	/**
	 * Altera o filtro da <i>Ordem</i>.
	 * 
	 * @param lstAnimalOrder
	 */
	protected void setAnimalOrder(List<String> lstAnimalOrder) {
		this.lstAnimalOrder = lstAnimalOrder;
	}
	
	/**
	 * Altera o filtro da <i>Família</i>.
	 * 
	 * @param lstAnimalFamily
	 */
	protected void setAnimalFamily(List<String> lstAnimalFamily) {
		this.lstAnimalFamily = lstAnimalFamily;
	}
	
	/**
	 * Altera o filtro do <i>Gênero</i>.
	 * 
	 * @param lstAnimalGenus
	 */
	protected void setAnimalGenus(List<String> lstAnimalGenus) {
		this.lstAnimalGenus = lstAnimalGenus;
	}
	
	/**
	 * Altera o filtro do <i>País</i>.
	 * 
	 * @param lstLocationCountry
	 */
	protected void setLocationCountry(List<String> lstLocationCountry) {
		this.lstLocationCountry = lstLocationCountry;
	}
	
	/**
	 * Altera o filtro do <i>Estado</i>.
	 * 
	 * @param lstLocationState
	 */
	protected void setLocationState(List<String> lstLocationState) {
		this.lstLocationState = lstLocationState;
	}

	/**
	 * Filtro de campos da consulta.
	 * 
	 * @param strDatabaseColumn         - Coluna do banco de dados
	 * @param strLabel                  - Label da coluna
	 * @param lstRecords                - Lista de registros a ser manipulada
	 */
	protected ScreenAudioComparisonFiltersSearch(String strDatabaseColumn, String strLabel, List<String> lstRecords) {
		this.strDatabaseColumn = strDatabaseColumn;
		this.strLabel = strLabel;
		this.lstRecords = lstRecords;
		
		this.lstAnimalPhylum = new ArrayList<String>();
		this.lstAnimalClass = new ArrayList<String>();
		this.lstAnimalOrder = new ArrayList<String>();
		this.lstAnimalFamily = new ArrayList<String>();
		this.lstAnimalGenus = new ArrayList<String>();
		this.lstLocationCountry = new ArrayList<String>();
		this.lstLocationState = new ArrayList<String>();
		
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
		panelDatabaseRecords = new JPanel();
		panelDatabaseRecords.setLayout(new MigLayout("insets 0", "[grow]", "[grow][]"));
		
		objTableDatabaseRecords = new LTTable(false);
		objTableDatabaseRecords.addColumn("selection", "", LTDataTypes.BOOLEAN, 30, true);
		objTableDatabaseRecords.addColumn("description", strLabel.replace(":", ""), LTDataTypes.STRING, 440, false);
		objTableDatabaseRecords.addTableListener(ScreenAudioComparisonFiltersSearch.this);
		objTableDatabaseRecords.showTable();
		
		JLabel lblSearchLimit = new JLabel(" " + rsBundle.getString("screen_audio_comparison_filters_search_select_limit_1") 
				                           + SEARCH_LIMIT 
				                           + " " + rsBundle.getString("screen_audio_comparison_filters_search_select_limit_2"));
		
		lblSearchLimit.setFont(new Font("Tahoma", Font.PLAIN, 11));
		
		// ***********************************************************************************************************************
		// Selecionar Todos os Registros
		btnSelectAllRecords = new JButton(rsBundle.getString("screen_audio_comparison_filters_search_button_select_all_records"));
		btnSelectAllRecords.setMinimumSize(new Dimension(250, 30));
		btnSelectAllRecords.setIconTextGap(15);
		btnSelectAllRecords.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSelectAllRecords.setIcon(new ImageIcon("res/images/select_all.png"));
		btnSelectAllRecords.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				for (int intRowIndex = 0; intRowIndex < objTableDatabaseRecords.getRowCount(); intRowIndex++) {
					// Somente seleciona os registros não selecionados anteriormente
					if (objTableDatabaseRecords.getValue(intRowIndex, "selection") == null || (boolean) objTableDatabaseRecords.getValue(intRowIndex, "selection") == false) {
						objTableDatabaseRecords.setValue(true, intRowIndex, "selection");
					}
				}
				
				Collections.sort(lstRecords);
			}
		});
		
		// Limpar Seleções
		btnCleanSelections = new JButton(rsBundle.getString("screen_audio_comparison_filters_search_button_clean_all_records"));
		btnCleanSelections.setMinimumSize(new Dimension(250, 30));
		btnCleanSelections.setIconTextGap(15);
		btnCleanSelections.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCleanSelections.setIcon(new ImageIcon("res/images/clean.png"));
		btnCleanSelections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				for (int intRowIndex = 0; intRowIndex < objTableDatabaseRecords.getRowCount(); intRowIndex++) {
					objTableDatabaseRecords.setValue(false, intRowIndex, "selection");
				}
				
				for (int indexRecord = 0; indexRecord < lstRecords.size(); indexRecord++) {
					lstRecords.remove(indexRecord);
				}
			}
		});
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_comparison_filters_search_screen_description") + " " + strLabel.replace(":", ""), true);
		objWasisDialog.setBounds(350, 350, 550, 400);
		objWasisDialog.setMinimumSize(new Dimension(550, 400));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][]"));
		objWasisDialog.getContentPane().add(txtDatabaseColumn, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelDatabaseRecords, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(btnSelectAllRecords, "cell 0 2, grow");
		objWasisDialog.getContentPane().add(btnCleanSelections, "cell 0 2, grow");
		
		panelDatabaseRecords.add(objTableDatabaseRecords, "cell 0 0, grow");
		panelDatabaseRecords.add(lblSearchLimit, "cell 0 1, grow");
		
		loadData();
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	protected void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Carrega os dados de seleções do áudio armazenados no banco de dados.
	 */
	private void loadData() {
		final String strDatabaseColumnTyped = txtDatabaseColumn.getValue().toString();
		
		SwingWorker<Void, Void> swingWorkerUpdate = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					objDatabaseConnection.openConnection();
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT " + strDatabaseColumn);
					objDatabaseConnection.sqlCommandAppend("FROM audio_files ");
					objDatabaseConnection.sqlCommandAppend("WHERE " + strDatabaseColumn + " IS NOT NULL ");
					objDatabaseConnection.sqlCommandAppend("AND " + strDatabaseColumn + " != '' ");
					
					if (txtDatabaseColumn.getValue() != null && txtDatabaseColumn.getValue().toString().trim().length() > 0) {
						objDatabaseConnection.sqlCommandAppend("AND " + strDatabaseColumn + " LIKE '" + txtDatabaseColumn.getValue() + "%'");
					}
					
					// Filtro Classe
					if (strDatabaseColumn.equals("animal_class")) {
						addDatabaseSelectFilter("animal_phylum", lstAnimalPhylum);
						
					// Filtro Ordem
					} else if (strDatabaseColumn.equals("animal_order")) {
						addDatabaseSelectFilter("animal_phylum", lstAnimalPhylum);
						addDatabaseSelectFilter("animal_class", lstAnimalClass);
						
					// Filtro Família
					} else if (strDatabaseColumn.equals("animal_family")) {
						addDatabaseSelectFilter("animal_phylum", lstAnimalPhylum);
						addDatabaseSelectFilter("animal_class", lstAnimalClass);
						addDatabaseSelectFilter("animal_order", lstAnimalOrder);
						
					// Filtro Gênero
					} else if (strDatabaseColumn.equals("animal_genus")) {
						addDatabaseSelectFilter("animal_phylum", lstAnimalPhylum);
						addDatabaseSelectFilter("animal_class", lstAnimalClass);
						addDatabaseSelectFilter("animal_order", lstAnimalOrder);
						addDatabaseSelectFilter("animal_family", lstAnimalFamily);
						
					// Filtro Espécie
					} else if (strDatabaseColumn.equals("animal_species")) {
						addDatabaseSelectFilter("animal_phylum", lstAnimalPhylum);
						addDatabaseSelectFilter("animal_class", lstAnimalClass);
						addDatabaseSelectFilter("animal_order", lstAnimalOrder);
						addDatabaseSelectFilter("animal_family", lstAnimalFamily);
						addDatabaseSelectFilter("animal_genus", lstAnimalGenus);
						
					// Filtro Estado
					} else if (strDatabaseColumn.equals("location_state")) {
						addDatabaseSelectFilter("location_country", lstLocationCountry);

					// Filtro Cidade
					} else if (strDatabaseColumn.equals("location_city")) {
						addDatabaseSelectFilter("location_country", lstLocationCountry);
						addDatabaseSelectFilter("location_state", lstLocationState);
					}
					
					objDatabaseConnection.sqlCommandAppend("GROUP BY " + strDatabaseColumn);
					objDatabaseConnection.sqlCommandAppend("ORDER BY " + strDatabaseColumn);
					objDatabaseConnection.sqlCommandAppend("LIMIT " + SEARCH_LIMIT);
					
					ResultSet rsDatabaseRecords = objDatabaseConnection.executeSelectQuery();
					
					if (strDatabaseColumnTyped.equals(txtDatabaseColumn.getValue().toString())) {
						objTableDatabaseRecords.deleteRows();
						
						while (rsDatabaseRecords.next()) {
							objTableDatabaseRecords.addRow();
							
							for (int indexRecords = 0; indexRecords < lstRecords.size(); indexRecords++) {
								if (lstRecords.get(indexRecords).equals(rsDatabaseRecords.getString(strDatabaseColumn))) {
									objTableDatabaseRecords.addRowData("selection", true);
									break;
								} else {
									objTableDatabaseRecords.addRowData("selection", false);
								}
							}
							
							objTableDatabaseRecords.addRowData("description", rsDatabaseRecords.getString(strDatabaseColumn));
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
					
					objTableDatabaseRecords.updateTableData();
	    			
				} catch (InterruptedException e) {
					
				} catch (ExecutionException e) {
					
				}
			}
		};

		swingWorkerUpdate.execute();
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
		// Caso o registro tenha sido marcado
		if (objTableDatabaseRecords.getColumnName(intColumnIndex).equals("selection")) {
			
			// Caso ele tinha sido marcado, é adicionado à lista
			if ((boolean) objValue) {
				lstRecords.add((String) objTableDatabaseRecords.getValue(intRowIndex, "description"));
				
			// Caso ele tinha sido desmarcado, é removido da lista
			} else {
				for (int indexRecords = 0; indexRecords < lstRecords.size(); indexRecords++) {
					if (objTableDatabaseRecords.getValue(intRowIndex, "description").equals(lstRecords.get(indexRecords))) {
						lstRecords.remove(indexRecords);
						break;
					}
				}
			}
		}
	}
}