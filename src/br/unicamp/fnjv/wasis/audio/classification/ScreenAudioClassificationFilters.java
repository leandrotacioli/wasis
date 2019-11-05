package br.unicamp.fnjv.wasis.audio.classification;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Tela de filtros de campos para a classificação de áudios.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 30/Out/2017
 */
public class ScreenAudioClassificationFilters extends JDialog {
	private static final long serialVersionUID = 5472785934281400846L;
	
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private JPanel panelAnimalTaxonomy;
	
	private LTTextField txtAnimalPhylum;
	private FilterButton btnAnimalPhylum;
	private FilterButton btnAnimalPhylumClean;
	
	private LTTextField txtAnimalClass;
	private FilterButton btnAnimalClass;
	private FilterButton btnAnimalClassClean;
	
	private LTTextField txtAnimalOrder;
	private FilterButton btnAnimalOrder;
	private FilterButton btnAnimalOrderClean;
	
	private LTTextField txtAnimalFamily;
	private FilterButton btnAnimalFamily;
	private FilterButton btnAnimalFamilyClean;
	
	private LTTextField txtAnimalGenus;
	private FilterButton btnAnimalGenus;
	private FilterButton btnAnimalGenusClean;
	
	private LTTextField txtAnimalSpecies;
	private FilterButton btnAnimalSpecies;
	private FilterButton btnAnimalSpeciesClean;
	
	private JPanel panelRecordingInformation;
	
	private LTTextField txtRecordist;
	private FilterButton btnRecordist;
	private FilterButton btnRecordistClean;
	
	private JPanel panelRecordingInformationDate;
	private LTTextField txtInitialDate;
	private FilterButton btnInitialDateClean;
	
	private LTTextField txtFinalDate;
	private FilterButton btnFinalDateClean;
	
	private LTTextField txtLocationCountry;
	private FilterButton btnLocationCountry;
	private FilterButton btnLocationCountryClean;
	
	private LTTextField txtLocationState;
	private FilterButton btnLocationState;
	private FilterButton btnLocationStateClean;
	
	private LTTextField txtLocationCity;
	private FilterButton btnLocationCity;
	private FilterButton btnLocationCityClean;
	
	private List<String> lstAnimalPhylum;
	private List<String> lstAnimalClass;
	private List<String> lstAnimalOrder;
	private List<String> lstAnimalFamily;
	private List<String> lstAnimalGenus;
	private List<String> lstAnimalSpecies;
	private List<String> lstRecordist;
	private List<String> lstLocationCountry;
	private List<String> lstLocationState;
	private List<String> lstLocationCity;
	
	/**
	 * Retorna o conteúdo completo do campo data inicial.
	 * 
	 * @return txtInitialDate
	 */
	protected LTTextField getInitialDate() {
		return txtInitialDate;
	}

	/**
	 * Retorna o conteúdo completo do campo data final.
	 * 
	 * @return txtFinalDate
	 */
	protected LTTextField getFinalDate() {
		return txtFinalDate;
	}

	/**
	 * Retorna a lista de filos de animais.
	 * 
	 * @return lstAnimalPhylum
	 */
	protected List<String> getAnimalPhylum() {
		return lstAnimalPhylum;
	}
	
	/**
	 * Altera a lista de filos de animais.
	 * 
	 * @param lstAnimalPhylum
	 */
	protected void setAnimalPhylum(List<String> lstAnimalPhylum) {
		this.lstAnimalPhylum = lstAnimalPhylum;
		
		setFilterText(txtAnimalPhylum, lstAnimalPhylum);
	}

	/**
	 * Retorna a lista de classes de animais.
	 * 
	 * @return lstAnimalClass
	 */
	protected List<String> getAnimalClass() {
		return lstAnimalClass;
	}
	
	/**
	 * Altera a lista de classes de animais.
	 * 
	 * @param lstAnimalClass
	 */
	protected void setAnimalClass(List<String> lstAnimalClass) {
		this.lstAnimalClass = lstAnimalClass;
		
		setFilterText(txtAnimalClass, lstAnimalClass);
	}

	/**
	 * Retorna a lista de ordens de animais.
	 * 
	 * @return lstAnimalOrder
	 */
	protected List<String> getAnimalOrder() {
		return lstAnimalOrder;
	}
	
	/**
	 * Altera a lista de ordens de animais.
	 * 
	 * @param lstAnimalOrder
	 */
	protected void setAnimalOrder(List<String> lstAnimalOrder) {
		this.lstAnimalOrder = lstAnimalOrder;
		
		setFilterText(txtAnimalOrder, lstAnimalOrder);
	}

	/**
	 * Retorna a lista de famílias de animais.
	 * 
	 * @return lstAnimalFamily
	 */
	protected List<String> getAnimalFamily() {
		return lstAnimalFamily;
	}
	
	/**
	 * Altera a lista de famílias de animais.
	 * 
	 * @param lstAnimalFamily
	 */
	protected void setAnimalFamily(List<String> lstAnimalFamily) {
		this.lstAnimalFamily = lstAnimalFamily;
		
		setFilterText(txtAnimalFamily, lstAnimalFamily);
	}

	/**
	 * Retorna a lista de gêneros de animais.
	 * 
	 * @return lstAnimalGenus
	 */
	protected List<String> getAnimalGenus() {
		return lstAnimalGenus;
	}
	
	/**
	 * Altera a lista de gêneros de animais.
	 * 
	 * @param lstAnimalGenus
	 */
	protected void setAnimalGenus(List<String> lstAnimalGenus) {
		this.lstAnimalGenus = lstAnimalGenus;
		
		setFilterText(txtAnimalGenus, lstAnimalGenus);
	}

	/**
	 * Retorna a lista de espécies de animais.
	 * 
	 * @return lstAnimalSpecies
	 */
	protected List<String> getAnimalSpecies() {
		return lstAnimalSpecies;
	}
	
	/**
	 * Altera a lista de espécies de animais.
	 * 
	 * @param lstAnimalSpecies
	 */
	protected void setAnimalSpecies(List<String> lstAnimalSpecies) {
		this.lstAnimalSpecies = lstAnimalSpecies;
		
		setFilterText(txtAnimalSpecies, lstAnimalSpecies);
	}

	/**
	 * Retorna a lista de quem gravou.
	 * 
	 * @return lstRecordist
	 */
	protected List<String> getRecordist() {
		return lstRecordist;
	}
	
	/**
	 * Altera a lista de quem gravou.
	 * 
	 * @param lstRecordist
	 */
	protected void setRecordist(List<String> lstRecordist) {
		this.lstRecordist = lstRecordist;
		
		setFilterText(txtRecordist, lstRecordist);
	}

	/**
	 * Retorna a lista de países.
	 * 
	 * @return lstLocationCountry
	 */
	protected List<String> getLocationCountry() {
		return lstLocationCountry;
	}
	
	/**
	 * Altera a lista de países.
	 * 
	 * @param lstLocationCountry
	 */
	protected void setLocationCountry(List<String> lstLocationCountry) {
		this.lstLocationCountry = lstLocationCountry;
		
		setFilterText(txtLocationCountry, lstLocationCountry);
	}

	/**
	 * Retorna a lista de estados.
	 * 
	 * @return lstLocationState
	 */
	protected List<String> getLocationState() {
		return lstLocationState;
	}
	
	/**
	 * Altera a lista de estados.
	 * 
	 * @param lstLocationState
	 */
	protected void setLocationState(List<String> lstLocationState) {
		this.lstLocationState = lstLocationState;
		
		setFilterText(txtLocationState, lstLocationState);
	}

	/**
	 * Retorna a lista de cidades.
	 * 
	 * @return lstLocationCity
	 */
	protected List<String> getLocationCity() {
		return lstLocationCity;
	}
	
	/**
	 * Altera a lista de cidades.
	 * 
	 * @param lstLocationCity
	 */
	protected void setLocationCity(List<String> lstLocationCity) {
		this.lstLocationCity = lstLocationCity;
		
		setFilterText(txtLocationCity, lstLocationCity);
	}
	
	/**
	 * Tela de filtros de campos para a classificação de áudios.
	 */
	protected ScreenAudioClassificationFilters() {
		this.lstAnimalPhylum = new ArrayList<String>();
		this.lstAnimalClass = new ArrayList<String>();
		this.lstAnimalOrder = new ArrayList<String>();
		this.lstAnimalFamily = new ArrayList<String>();
		this.lstAnimalGenus = new ArrayList<String>();
		this.lstAnimalSpecies = new ArrayList<String>();
		this.lstRecordist = new ArrayList<String>();
		this.lstLocationCountry = new ArrayList<String>();
		this.lstLocationState = new ArrayList<String>();
		this.lstLocationCity = new ArrayList<String>();
		
		loadScreen();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			// *****************************************************************************************************************
			// Filtros Dados Animais
			panelAnimalTaxonomy = new WasisPanel();
			panelAnimalTaxonomy.setLayout(new MigLayout("insets 5 ", "[grow] 3 [] 0 [] 12 [grow] 3 [] 0 [] 12 [grow] 3 [] 0 []", "[]"));
			
			ImageIcon imageOpen = new ImageIcon("res/images/open_small.png");
			ImageIcon imageClean = new ImageIcon("res/images/clean_small.png");
			
			// Filo
			txtAnimalPhylum = new LTTextField(rsBundle.getString("animal_phylum") + ":", LTDataTypes.STRING, false, false, 200);

			btnAnimalPhylum = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_phylum_tool_tip"), imageOpen);
			btnAnimalPhylum.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("animal_phylum", txtAnimalPhylum, lstAnimalPhylum);
				}
			});
			
			btnAnimalPhylumClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_phylum_clean_tool_tip"), imageClean);
			btnAnimalPhylumClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setAnimalPhylum(new ArrayList<String>());
				}
			});
			
			// Classe
			txtAnimalClass = new LTTextField(rsBundle.getString("animal_class") + ":", LTDataTypes.STRING, false, false, 200);
			
			btnAnimalClass = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_class_tool_tip"), imageOpen);
			btnAnimalClass.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("animal_class", txtAnimalClass, lstAnimalClass);
				}
			});
			
			btnAnimalClassClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_class_clean_tool_tip"), imageClean);
			btnAnimalClassClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setAnimalClass(new ArrayList<String>());
				}
			});
			
			// Ordem
			txtAnimalOrder = new LTTextField(rsBundle.getString("animal_order") + ":", LTDataTypes.STRING, false, false, 200);
			
			btnAnimalOrder = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_order_tool_tip"), imageOpen);
			btnAnimalOrder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("animal_order", txtAnimalOrder, lstAnimalOrder);
				}
			});
			
			btnAnimalOrderClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_order_clean_tool_tip"), imageClean);
			btnAnimalOrderClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setAnimalOrder(new ArrayList<String>());
				}
			});
			
			// Família
			txtAnimalFamily = new LTTextField(rsBundle.getString("animal_family") + ":", LTDataTypes.STRING, false, false, 200);
			
			btnAnimalFamily = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_family_tool_tip"), imageOpen);
			btnAnimalFamily.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("animal_family", txtAnimalFamily, lstAnimalFamily);
				}
			});
			
			btnAnimalFamilyClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_family_clean_tool_tip"), imageClean);
			btnAnimalFamilyClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setAnimalFamily(new ArrayList<String>());
				}
			});
			
			// Gênero
			txtAnimalGenus = new LTTextField(rsBundle.getString("animal_genus") + ":", LTDataTypes.STRING, false, false, 200);
			
			btnAnimalGenus = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_genus_tool_tip"), imageOpen);
			btnAnimalGenus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("animal_genus", txtAnimalGenus, lstAnimalGenus);
				}
			});
			
			btnAnimalGenusClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_genus_clean_tool_tip"), imageClean);
			btnAnimalGenusClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setAnimalGenus(new ArrayList<String>());
				}
			});
			
			// Espécie
			txtAnimalSpecies = new LTTextField(rsBundle.getString("animal_species") + ":", LTDataTypes.STRING, false, false, 200);
			
			btnAnimalSpecies = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_species_tool_tip"), imageOpen);
			btnAnimalSpecies.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("animal_species", txtAnimalSpecies, lstAnimalSpecies);
				}
			});
			
			btnAnimalSpeciesClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_animal_species_clean_tool_tip"), imageClean);
			btnAnimalSpeciesClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setAnimalSpecies(new ArrayList<String>());
				}
			});
			
			panelAnimalTaxonomy.add(txtAnimalPhylum, "cell 0 0, grow, width 400");
			panelAnimalTaxonomy.add(btnAnimalPhylum, "cell 1 0, aligny bottom");
			panelAnimalTaxonomy.add(btnAnimalPhylumClean, "cell 2 0, aligny bottom");
			
			panelAnimalTaxonomy.add(txtAnimalClass, "cell 3 0, grow, width 400");
			panelAnimalTaxonomy.add(btnAnimalClass, "cell 4 0, aligny bottom");
			panelAnimalTaxonomy.add(btnAnimalClassClean, "cell 5 0, aligny bottom");
			
			panelAnimalTaxonomy.add(txtAnimalOrder, "cell 6 0, grow, width 400");
			panelAnimalTaxonomy.add(btnAnimalOrder, "cell 7 0, aligny bottom");
			panelAnimalTaxonomy.add(btnAnimalOrderClean, "cell 8 0, aligny bottom");
			
			panelAnimalTaxonomy.add(txtAnimalFamily, "cell 0 1, grow, width 400");
			panelAnimalTaxonomy.add(btnAnimalFamily, "cell 1 1, aligny bottom");
			panelAnimalTaxonomy.add(btnAnimalFamilyClean, "cell 2 1, aligny bottom");
			
			panelAnimalTaxonomy.add(txtAnimalGenus, "cell 3 1, grow, width 400");
			panelAnimalTaxonomy.add(btnAnimalGenus, "cell 4 1, aligny bottom");
			panelAnimalTaxonomy.add(btnAnimalGenusClean, "cell 5 1, aligny bottom");
			
			panelAnimalTaxonomy.add(txtAnimalSpecies, "cell 6 1, grow, width 400");
			panelAnimalTaxonomy.add(btnAnimalSpecies, "cell 7 1, aligny bottom");
			panelAnimalTaxonomy.add(btnAnimalSpeciesClean, "cell 8 1, aligny bottom");
			
			// *****************************************************************************************************************
			// Filtros Dados Gravações
			panelRecordingInformation = new WasisPanel();
			panelRecordingInformation.setLayout(new MigLayout("insets 5 ", "[grow] 3 [] 0 [] 12 [grow] 3 [] 0 [] 12 [grow] 3 [] 0 []", "[grow]"));
			
			// Quem Gravou
			txtRecordist = new LTTextField(rsBundle.getString("audio_file_recordist") + ":", LTDataTypes.STRING, false, false, 200);
			
			btnRecordist = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_recordist_tool_tip"), imageOpen);
			btnRecordist.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("recordist", txtRecordist, lstRecordist);
				}
			});
			
			btnRecordistClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_recordist_clean_tool_tip"), imageClean);
			btnRecordistClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setRecordist(new ArrayList<String>());
				}
			});
			
			// *****************************************************************************************************************
			// Filtros Dados Gravações - Datas
			panelRecordingInformationDate = new JPanel();
			panelRecordingInformationDate.setLayout(new MigLayout("insets 0 ", "[grow] 3 [] 12 [grow] 3 []", "[grow]"));
			panelRecordingInformationDate.setBackground(WasisParameters.COLOR_BACKGROUND);
			
			// Data Inicial
			txtInitialDate = new LTTextField("Data Inicial:", LTDataTypes.DATE, true, false);
			
			btnInitialDateClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_date_initial_clean_tool_tip"), imageClean);
			btnInitialDateClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					txtInitialDate.setValue(null);
				}
			});
			
			// Data Final
			txtFinalDate = new LTTextField("Data Final:", LTDataTypes.DATE, true, false);
	
			btnFinalDateClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_date_final_clean_tool_tip"), imageClean);
			btnFinalDateClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					txtFinalDate.setValue(null);
				}
			});
			
			// *****************************************************************************************************************
			// País
			txtLocationCountry = new LTTextField(rsBundle.getString("audio_file_location_country") + ":", LTDataTypes.STRING, false, false);
			
			btnLocationCountry = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_location_country_tool_tip"), imageOpen);
			btnLocationCountry.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("location_country", txtLocationCountry, lstLocationCountry);
				}
			});
			
			btnLocationCountryClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_location_country_clean_tool_tip"), imageClean);
			btnLocationCountryClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setLocationCountry(new ArrayList<String>());
				}
			});
			
			// Estado
			txtLocationState = new LTTextField(rsBundle.getString("audio_file_location_state") + ":", LTDataTypes.STRING, false, false);
			
			btnLocationState = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_location_state_tool_tip"), imageOpen);
			btnLocationState.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("location_state", txtLocationState, lstLocationState);
				}
			});
			
			btnLocationStateClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_location_state_clean_tool_tip"), imageClean);
			btnLocationStateClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setLocationState(new ArrayList<String>());
				}
			});
			
			// Cidade
			txtLocationCity = new LTTextField(rsBundle.getString("audio_file_location_city") + ":", LTDataTypes.STRING, false, false);
			
			btnLocationCity = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_location_city_tool_tip"), imageOpen);
			btnLocationCity.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					loadFilterSearch("location_city", txtLocationCity, lstLocationCity);
				}
			});
			
			btnLocationCityClean = new FilterButton(rsBundle.getString("screen_audio_classification_filters_button_location_city_clean_tool_tip"), imageClean);
			btnLocationCityClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					setLocationCity(new ArrayList<String>());
				}
			});
			
			panelRecordingInformation.add(txtRecordist, "cell 0 0 4 1, grow");
			panelRecordingInformation.add(btnRecordist, "cell 4 0, aligny bottom");
			panelRecordingInformation.add(btnRecordistClean, "cell 5 0, aligny bottom");
			
			panelRecordingInformation.add(panelRecordingInformationDate, "cell 6 0 3 1, grow");
			panelRecordingInformationDate.add(txtInitialDate, "cell 0 0, width 180");
			panelRecordingInformationDate.add(btnInitialDateClean, "cell 1 0, aligny bottom");
			panelRecordingInformationDate.add(txtFinalDate, "cell 2 0, width 180");
			panelRecordingInformationDate.add(btnFinalDateClean, "cell 3 0, aligny bottom");
			
			panelRecordingInformation.add(txtLocationCountry, "cell 0 1, grow, width 400");
			panelRecordingInformation.add(btnLocationCountry, "cell 1 1, aligny bottom");
			panelRecordingInformation.add(btnLocationCountryClean, "cell 2 1, aligny bottom");
			
			panelRecordingInformation.add(txtLocationState, "cell 3 1, grow, width 400");
			panelRecordingInformation.add(btnLocationState, "cell 4 1, aligny bottom");
			panelRecordingInformation.add(btnLocationStateClean, "cell 5 1, aligny bottom");
			
			panelRecordingInformation.add(txtLocationCity, "cell 6 1, grow, width 400");
			panelRecordingInformation.add(btnLocationCity, "cell 7 1, aligny bottom");
			panelRecordingInformation.add(btnLocationCityClean, "cell 8 1, aligny bottom");
			
			// ***********************************************************************************************************************
			// Cria a tela
			objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_classification_filters_screen_description"), true);
			objWasisDialog.setBounds(350, 350, 700, 245);
			objWasisDialog.setMinimumSize(new Dimension(700, 245));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[] 5 []"));
			objWasisDialog.getContentPane().add(panelAnimalTaxonomy, "cell 0 1, grow");
			objWasisDialog.getContentPane().add(panelRecordingInformation, "cell 0 2, grow");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carrega os valores dos filtros.
	 * 
	 * @param txtField
	 * @param lstRecords
	 */
	private void setFilterText(LTTextField txtField, List<String> lstRecords) {
		txtField.setValue(null);
		
		for (int indexFilter = 0; indexFilter < lstRecords.size(); indexFilter++) {
			if (indexFilter == 0) {
				txtField.setValue(lstRecords.get(indexFilter));
			} else {
				txtField.setValue(txtField.getValue() + ", " + lstRecords.get(indexFilter));
			}
		}
	}
	
	/**
	 * Abre a tela de filtro para um determinado campo.
	 * 	
	 * @param strColumnDatabase
	 * @param txtField
	 * @param lstRecords
	 */
	private void loadFilterSearch(String strColumnDatabase, LTTextField txtField, List<String> lstRecords) {
		ScreenAudioClassificationFiltersSearch objFiltersSearch = new ScreenAudioClassificationFiltersSearch(this, strColumnDatabase, txtField.getLabel(), lstRecords);
		objFiltersSearch.showScreen();
		
		// Carrega os dados selecionados quando a tela fechar
		if (lstRecords.size() > 0) {
			Collections.sort(lstRecords);
			
			setFilterText(txtField, lstRecords);
			
			if (txtField.getLabel().equals("animal_phylum")) {
				lstAnimalPhylum = lstRecords;
			} else if (txtField.getLabel().equals("animal_class")) {
				lstAnimalClass = lstRecords;
			} else if (txtField.getLabel().equals("animal_order")) {
				lstAnimalOrder = lstRecords;
			} else if (txtField.getLabel().equals("animal_family")) {
				lstAnimalFamily = lstRecords;
			} else if (txtField.getLabel().equals("animal_genus")) {
				lstAnimalGenus = lstRecords;
			} else if (txtField.getLabel().equals("animal_species")) {
				lstAnimalSpecies = lstRecords;
			} else if (txtField.getLabel().equals("recordist")) {
				lstRecordist = lstRecords;
			} else if (txtField.getLabel().equals("location_country")) {
				lstLocationCountry = lstRecords;
			} else if (txtField.getLabel().equals("location_state")) {
				lstLocationState = lstRecords;
			} else if (txtField.getLabel().equals("location_city")) {
				lstLocationCity = lstRecords;
			}
			
		} else {
			if (strColumnDatabase.equals("animal_phylum")) {
				setAnimalPhylum(new ArrayList<String>());
			} else if (strColumnDatabase.equals("animal_class")) {
				setAnimalClass(new ArrayList<String>());
			} else if (strColumnDatabase.equals("animal_order")) {
				setAnimalOrder(new ArrayList<String>());
			} else if (strColumnDatabase.equals("animal_family")) {
				setAnimalFamily(new ArrayList<String>());
			} else if (strColumnDatabase.equals("animal_genus")) {
				setAnimalGenus(new ArrayList<String>());
			} else if (strColumnDatabase.equals("animal_species")) {
				setAnimalGenus(new ArrayList<String>());
			} else if (strColumnDatabase.equals("recordist")) {
				setRecordist(new ArrayList<String>());
			} else if (strColumnDatabase.equals("location_country")) {
				setLocationCountry(new ArrayList<String>());
			} else if (strColumnDatabase.equals("location_state")) {
				setLocationState(new ArrayList<String>());
			} else if (strColumnDatabase.equals("location_city")) {
				setLocationCity(new ArrayList<String>());
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
	 * Botão padrão para os filtros.
	 */
	private class FilterButton extends JButton {
		private static final long serialVersionUID = 5706706224629322167L;
		
		private FilterButton(String strToolTipText, ImageIcon imageIcon) {
			setMinimumSize(new Dimension(22, 22));
			setMaximumSize(new Dimension(22, 22));
			
			setBackground(WasisParameters.COLOR_BACKGROUND);
			setFocusPainted(false);

			setToolTipText(strToolTipText);
			setIcon(imageIcon);
		}
	}
}