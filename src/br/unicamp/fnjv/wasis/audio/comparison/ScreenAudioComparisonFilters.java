package br.unicamp.fnjv.wasis.audio.comparison;

import java.awt.Dimension;
import java.awt.Font;
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
 * Tela de filtros da comparação de áudios.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 14/Mai/2015
 */
public class ScreenAudioComparisonFilters extends JDialog {
	private static final long serialVersionUID = 5472785934281400846L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private JPanel panelFilterAnimal;
	
	private LTTextField txtAnimalPhylum;
	private JButton btnAnimalPhylum;
	private JButton btnAnimalPhylumClean;
	
	private LTTextField txtAnimalClass;
	private JButton btnAnimalClass;
	private JButton btnAnimalClassClean;
	
	private LTTextField txtAnimalOrder;
	private JButton btnAnimalOrder;
	private JButton btnAnimalOrderClean;
	
	private LTTextField txtAnimalFamily;
	private JButton btnAnimalFamily;
	private JButton btnAnimalFamilyClean;
	
	private LTTextField txtAnimalGenus;
	private JButton btnAnimalGenus;
	private JButton btnAnimalGenusClean;
	
	private LTTextField txtAnimalSpecies;
	private JButton btnAnimalSpecies;
	private JButton btnAnimalSpeciesClean;
	
	private JPanel panelFilterRecording;
	
	private LTTextField txtRecordist;
	private JButton btnRecordist;
	private JButton btnRecordistClean;
	
	private JPanel panelFilterRecordingDate;
	private LTTextField txtDateInitial;
	private JButton btnDateInitialClean;
	
	private LTTextField txtDateFinal;
	private JButton btnDateFinalClean;
	
	private LTTextField txtLocationCountry;
	private JButton btnLocationCountry;
	private JButton btnLocationCountryClean;
	
	private LTTextField txtLocationState;
	private JButton btnLocationState;
	private JButton btnLocationStateClean;
	
	private LTTextField txtLocationCity;
	private JButton btnLocationCity;
	private JButton btnLocationCityClean;
	
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
	 * @return txtDateInitial
	 */
	protected LTTextField getDateInitialField() {
		return txtDateInitial;
	}
	
	/**
	 * Altera o valor do campo data inicial.
	 * 
	 * @param objDate
	 */
	protected void setDateInitial(Object objDate) {
		this.txtDateInitial.setValue(objDate);
	}

	/**
	 * Retorna o conteúdo completo do campo data final.
	 * 
	 * @return txtDateFinal
	 */
	protected LTTextField getDateFinalField() {
		return txtDateFinal;
	}
	
	/**
	 * Altera o valor do campo data final.
	 * 
	 * @param objDate
	 */
	protected void setDateFinal(Object objDate) {
		this.txtDateFinal.setValue(objDate);
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
		
		loadFilter(txtAnimalPhylum, lstAnimalPhylum);
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
	 * Altera a lista de filos de animais.
	 * 
	 * @param lstAnimalClass
	 */
	protected void setAnimalClass(List<String> lstAnimalClass) {
		this.lstAnimalClass = lstAnimalClass;
		
		loadFilter(txtAnimalClass, lstAnimalClass);
	}

	/**
	 * 
	 * @return lstAnimalOrder
	 */
	protected List<String> getAnimalOrder() {
		return lstAnimalOrder;
	}
	
	/**
	 * 
	 * @param lstAnimalOrder
	 */
	protected void setAnimalOrder(List<String> lstAnimalOrder) {
		this.lstAnimalOrder = lstAnimalOrder;
		
		loadFilter(txtAnimalOrder, lstAnimalOrder);
	}

	/**
	 * 
	 * @return lstAnimalFamily
	 */
	protected List<String> getAnimalFamily() {
		return lstAnimalFamily;
	}
	
	/**
	 * 
	 * @param lstAnimalFamily
	 */
	protected void setAnimalFamily(List<String> lstAnimalFamily) {
		this.lstAnimalFamily = lstAnimalFamily;
		
		loadFilter(txtAnimalFamily, lstAnimalFamily);
	}

	/**
	 * 
	 * @return lstAnimalGenus
	 */
	protected List<String> getAnimalGenus() {
		return lstAnimalGenus;
	}
	
	/**
	 * 
	 * @param lstAnimalGenus
	 */
	protected void setAnimalGenus(List<String> lstAnimalGenus) {
		this.lstAnimalGenus = lstAnimalGenus;
		
		loadFilter(txtAnimalGenus, lstAnimalGenus);
	}

	/**
	 * 
	 * @return lstAnimalSpecies
	 */
	protected List<String> getAnimalSpecies() {
		return lstAnimalSpecies;
	}
	
	/**
	 * 
	 * @param lstAnimalSpecies
	 */
	protected void setAnimalSpecies(List<String> lstAnimalSpecies) {
		this.lstAnimalSpecies = lstAnimalSpecies;
		
		loadFilter(txtAnimalSpecies, lstAnimalSpecies);
	}

	/**
	 * 
	 * @return lstRecordist
	 */
	protected List<String> getRecordist() {
		return lstRecordist;
	}
	
	/**
	 * 
	 * @param lstRecordist
	 */
	protected void setRecordist(List<String> lstRecordist) {
		this.lstRecordist = lstRecordist;
		
		loadFilter(txtRecordist, lstRecordist);
	}

	/**
	 * 
	 * @return lstLocationCountry
	 */
	protected List<String> getLocationCountry() {
		return lstLocationCountry;
	}
	
	/**
	 * 
	 * @param lstLocationCountry
	 */
	protected void setLocationCountry(List<String> lstLocationCountry) {
		this.lstLocationCountry = lstLocationCountry;
		
		loadFilter(txtLocationCountry, lstLocationCountry);
	}

	/**
	 * 
	 * @return lstLocationState
	 */
	protected List<String> getLocationState() {
		return lstLocationState;
	}
	
	/**
	 * 
	 * @param lstLocationState
	 */
	protected void setLocationState(List<String> lstLocationState) {
		this.lstLocationState = lstLocationState;
		
		loadFilter(txtLocationState, lstLocationState);
	}

	/**
	 * 
	 * @return lstLocationCity
	 */
	protected List<String> getLocationCity() {
		return lstLocationCity;
	}
	
	/**
	 * 
	 * @param lstLocationCity
	 */
	protected void setLocationCity(List<String> lstLocationCity) {
		this.lstLocationCity = lstLocationCity;
		
		loadFilter(txtLocationCity, lstLocationCity);
	}
	
	/**
	 * Tela de filtros da comparação de áudios.
	 */
	protected ScreenAudioComparisonFilters() {
		loadScreen();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			// Cria os componentes da tela
			
			// *****************************************************************************************************************
			// Filtros Dados Animais
			panelFilterAnimal = new WasisPanel();
			panelFilterAnimal.setLayout(new MigLayout("insets 5 ", "[grow] 3 [] 0 [] 12 [grow] 3 [] 0 [] 12 [grow] 3 [] 0 []", "[]"));
			
			// Filo
			txtAnimalPhylum = new LTTextField(rsBundle.getString("animal_phylum") + ":", LTDataTypes.STRING, false, false, 200);
			
			// Consultar Filos
			btnAnimalPhylum = new JButton();
			btnAnimalPhylum.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_phylum_tool_tip"));
			btnAnimalPhylum.setMinimumSize(new Dimension(22, 22));
			btnAnimalPhylum.setMaximumSize(new Dimension(22, 22));
			btnAnimalPhylum.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalPhylum.setIcon(new ImageIcon("res/images/open_small.png"));
			btnAnimalPhylum.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("animal_phylum", txtAnimalPhylum, lstAnimalPhylum);
				}
			});
			
			// Limpar Filos
			btnAnimalPhylumClean = new JButton();
			btnAnimalPhylumClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_phylum_clean_tool_tip"));
			btnAnimalPhylumClean.setMinimumSize(new Dimension(22, 22));
			btnAnimalPhylumClean.setMaximumSize(new Dimension(22, 22));
			btnAnimalPhylumClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalPhylumClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnAnimalPhylumClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanAnimalPhylum();
				}
			});
			
			// Classe
			txtAnimalClass = new LTTextField(rsBundle.getString("animal_class") + ":", LTDataTypes.STRING, false, false, 200);
			
			// Consultar Classe
			btnAnimalClass = new JButton();
			btnAnimalClass.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_class_tool_tip"));
			btnAnimalClass.setMinimumSize(new Dimension(22, 22));
			btnAnimalClass.setMaximumSize(new Dimension(22, 22));
			btnAnimalClass.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalClass.setIcon(new ImageIcon("res/images/open_small.png"));
			btnAnimalClass.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("animal_class", txtAnimalClass, lstAnimalClass);
				}
			});
			
			// Limpar Classe
			btnAnimalClassClean = new JButton();
			btnAnimalClassClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_class_clean_tool_tip"));
			btnAnimalClassClean.setMinimumSize(new Dimension(22, 22));
			btnAnimalClassClean.setMaximumSize(new Dimension(22, 22));
			btnAnimalClassClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalClassClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnAnimalClassClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanAnimalClass();
				}
			});
			
			// Ordem
			txtAnimalOrder = new LTTextField(rsBundle.getString("animal_order") + ":", LTDataTypes.STRING, false, false, 200);
			
			// Consultar Ordens
			btnAnimalOrder = new JButton();
			btnAnimalOrder.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_order_tool_tip"));
			btnAnimalOrder.setMinimumSize(new Dimension(22, 22));
			btnAnimalOrder.setMaximumSize(new Dimension(22, 22));
			btnAnimalOrder.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalOrder.setIcon(new ImageIcon("res/images/open_small.png"));
			btnAnimalOrder.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("animal_order", txtAnimalOrder, lstAnimalOrder);
				}
			});
			
			// Limpar Ordem
			btnAnimalOrderClean = new JButton();
			btnAnimalOrderClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_order_clean_tool_tip"));
			btnAnimalOrderClean.setMinimumSize(new Dimension(22, 22));
			btnAnimalOrderClean.setMaximumSize(new Dimension(22, 22));
			btnAnimalOrderClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalOrderClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnAnimalOrderClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanAnimalOrder();
				}
			});
			
			// Família
			txtAnimalFamily = new LTTextField(rsBundle.getString("animal_family") + ":", LTDataTypes.STRING, false, false, 200);
			
			// Consultar Famílias
			btnAnimalFamily = new JButton();
			btnAnimalFamily.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_family_tool_tip"));
			btnAnimalFamily.setMinimumSize(new Dimension(22, 22));
			btnAnimalFamily.setMaximumSize(new Dimension(22, 22));
			btnAnimalFamily.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalFamily.setIcon(new ImageIcon("res/images/open_small.png"));
			btnAnimalFamily.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("animal_family", txtAnimalFamily, lstAnimalFamily);
				}
			});
			
			// Limpar Família
			btnAnimalFamilyClean = new JButton();
			btnAnimalFamilyClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_family_clean_tool_tip"));
			btnAnimalFamilyClean.setMinimumSize(new Dimension(22, 22));
			btnAnimalFamilyClean.setMaximumSize(new Dimension(22, 22));
			btnAnimalFamilyClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalFamilyClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnAnimalFamilyClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanAnimalFamily();
				}
			});
			
			// Gênero
			txtAnimalGenus = new LTTextField(rsBundle.getString("animal_genus") + ":", LTDataTypes.STRING, false, false, 200);
			
			// Consultar Gêneros
			btnAnimalGenus = new JButton();
			btnAnimalGenus.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_genus_tool_tip"));
			btnAnimalGenus.setMinimumSize(new Dimension(22, 22));
			btnAnimalGenus.setMaximumSize(new Dimension(22, 22));
			btnAnimalGenus.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalGenus.setIcon(new ImageIcon("res/images/open_small.png"));
			btnAnimalGenus.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("animal_genus", txtAnimalGenus, lstAnimalGenus);
				}
			});
			
			// Limpar Gênero
			btnAnimalGenusClean = new JButton();
			btnAnimalGenusClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_genus_clean_tool_tip"));
			btnAnimalGenusClean.setMinimumSize(new Dimension(22, 22));
			btnAnimalGenusClean.setMaximumSize(new Dimension(22, 22));
			btnAnimalGenusClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalGenusClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnAnimalGenusClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanAnimalGenus();
				}
			});
			
			// Espécie
			txtAnimalSpecies = new LTTextField(rsBundle.getString("animal_species") + ":", LTDataTypes.STRING, false, false, 200);
			
			// Consultar Espécies
			btnAnimalSpecies = new JButton();
			btnAnimalSpecies.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_species_tool_tip"));
			btnAnimalSpecies.setMinimumSize(new Dimension(22, 22));
			btnAnimalSpecies.setMaximumSize(new Dimension(22, 22));
			btnAnimalSpecies.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalSpecies.setIcon(new ImageIcon("res/images/open_small.png"));
			btnAnimalSpecies.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("animal_species", txtAnimalSpecies, lstAnimalSpecies);
				}
			});
			
			// Limpar Espécie
			btnAnimalSpeciesClean = new JButton();
			btnAnimalSpeciesClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_animal_species_clean_tool_tip"));
			btnAnimalSpeciesClean.setMinimumSize(new Dimension(22, 22));
			btnAnimalSpeciesClean.setMaximumSize(new Dimension(22, 22));
			btnAnimalSpeciesClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnAnimalSpeciesClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnAnimalSpeciesClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanAnimalSpecies();
				}
			});
			
			panelFilterAnimal.add(txtAnimalPhylum, "cell 0 0, grow, width 400");
			panelFilterAnimal.add(btnAnimalPhylum, "cell 1 0, aligny bottom");
			panelFilterAnimal.add(btnAnimalPhylumClean, "cell 2 0, aligny bottom");
			
			panelFilterAnimal.add(txtAnimalClass, "cell 3 0, grow, width 400");
			panelFilterAnimal.add(btnAnimalClass, "cell 4 0, aligny bottom");
			panelFilterAnimal.add(btnAnimalClassClean, "cell 5 0, aligny bottom");
			
			panelFilterAnimal.add(txtAnimalOrder, "cell 6 0, grow, width 400");
			panelFilterAnimal.add(btnAnimalOrder, "cell 7 0, aligny bottom");
			panelFilterAnimal.add(btnAnimalOrderClean, "cell 8 0, aligny bottom");
			
			panelFilterAnimal.add(txtAnimalFamily, "cell 0 1, grow, width 400");
			panelFilterAnimal.add(btnAnimalFamily, "cell 1 1, aligny bottom");
			panelFilterAnimal.add(btnAnimalFamilyClean, "cell 2 1, aligny bottom");
			
			panelFilterAnimal.add(txtAnimalGenus, "cell 3 1, grow, width 400");
			panelFilterAnimal.add(btnAnimalGenus, "cell 4 1, aligny bottom");
			panelFilterAnimal.add(btnAnimalGenusClean, "cell 5 1, aligny bottom");
			
			panelFilterAnimal.add(txtAnimalSpecies, "cell 6 1, grow, width 400");
			panelFilterAnimal.add(btnAnimalSpecies, "cell 7 1, aligny bottom");
			panelFilterAnimal.add(btnAnimalSpeciesClean, "cell 8 1, aligny bottom");
			
			// *****************************************************************************************************************
			// Filtros Dados Gravações
			panelFilterRecording = new WasisPanel();
			panelFilterRecording.setLayout(new MigLayout("insets 5 ", "[grow] 3 [] 0 [] 12 [grow] 3 [] 0 [] 12 [grow] 3 [] 0 []", "[grow]"));
			
			// Quem Gravou
			txtRecordist = new LTTextField(rsBundle.getString("audio_file_recordist") + ":", LTDataTypes.STRING, false, false, 200);
			
			// Consultar Quem Gravou
			btnRecordist = new JButton();
			btnRecordist.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_recordist_tool_tip"));
			btnRecordist.setMinimumSize(new Dimension(22, 22));
			btnRecordist.setMaximumSize(new Dimension(22, 22));
			btnRecordist.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnRecordist.setIcon(new ImageIcon("res/images/open_small.png"));
			btnRecordist.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("recordist", txtRecordist, lstRecordist);
				}
			});
			
			// Limpar Quem Gravou
			btnRecordistClean = new JButton();
			btnRecordistClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_recordist_clean_tool_tip"));
			btnRecordistClean.setMinimumSize(new Dimension(22, 22));
			btnRecordistClean.setMaximumSize(new Dimension(22, 22));
			btnRecordistClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnRecordistClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnRecordistClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanRecordist();
				}
			});
			
			// *****************************************************************************************************************
			// Filtros Dados Gravações - Datas
			panelFilterRecordingDate = new JPanel();
			panelFilterRecordingDate.setLayout(new MigLayout("insets 0 ", "[grow] 3 [] 12 [grow] 3 []", "[grow]"));
			
			// Data Inicial
			txtDateInitial = new LTTextField("Data Inicial:", LTDataTypes.DATE, true, false);
			
			// Limpar Data Inicial
			btnDateInitialClean = new JButton();
			btnDateInitialClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_date_initial_clean_tool_tip"));
			btnDateInitialClean.setMinimumSize(new Dimension(22, 22));
			btnDateInitialClean.setMaximumSize(new Dimension(22, 22));
			btnDateInitialClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnDateInitialClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnDateInitialClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanDateInitial();
				}
			});
			
			// Data Final
			txtDateFinal = new LTTextField("Data Final:", LTDataTypes.DATE, true, false);
	
			// Limpar Data Final
			btnDateFinalClean = new JButton();
			btnDateFinalClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_date_final_clean_tool_tip"));
			btnDateFinalClean.setMinimumSize(new Dimension(22, 22));
			btnDateFinalClean.setMaximumSize(new Dimension(22, 22));
			btnDateFinalClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnDateFinalClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnDateFinalClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanDateFinal();
				}
			});
			
			// *****************************************************************************************************************
			// País
			txtLocationCountry = new LTTextField(rsBundle.getString("audio_file_location_country") + ":", LTDataTypes.STRING, false, false);
			
			// Consultar País
			btnLocationCountry = new JButton();
			btnLocationCountry.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_location_country_tool_tip"));
			btnLocationCountry.setMinimumSize(new Dimension(22, 22));
			btnLocationCountry.setMaximumSize(new Dimension(22, 22));
			btnLocationCountry.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnLocationCountry.setIcon(new ImageIcon("res/images/open_small.png"));
			btnLocationCountry.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("location_country", txtLocationCountry, lstLocationCountry);
				}
			});
			
			// Limpar País
			btnLocationCountryClean = new JButton();
			btnLocationCountryClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_location_country_clean_tool_tip"));
			btnLocationCountryClean.setMinimumSize(new Dimension(22, 22));
			btnLocationCountryClean.setMaximumSize(new Dimension(22, 22));
			btnLocationCountryClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnLocationCountryClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnLocationCountryClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanLocationCountry();
				}
			});
			
			// Estado
			txtLocationState = new LTTextField(rsBundle.getString("audio_file_location_state") + ":", LTDataTypes.STRING, false, false);
			
			// Consultar Estado
			btnLocationState = new JButton();
			btnLocationState.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_location_state_tool_tip"));
			btnLocationState.setMinimumSize(new Dimension(22, 22));
			btnLocationState.setMaximumSize(new Dimension(22, 22));
			btnLocationState.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnLocationState.setIcon(new ImageIcon("res/images/open_small.png"));
			btnLocationState.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("location_state", txtLocationState, lstLocationState);
				}
			});
			
			// Limpar Estado
			btnLocationStateClean = new JButton();
			btnLocationStateClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_location_state_clean_tool_tip"));
			btnLocationStateClean.setMinimumSize(new Dimension(22, 22));
			btnLocationStateClean.setMaximumSize(new Dimension(22, 22));
			btnLocationStateClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnLocationStateClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnLocationStateClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanLocationState();
				}
			});
			
			// Cidade
			txtLocationCity = new LTTextField(rsBundle.getString("audio_file_location_city") + ":", LTDataTypes.STRING, false, false);
			
			// Consultar Cidade
			btnLocationCity = new JButton();
			btnLocationCity.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_location_city_tool_tip"));
			btnLocationCity.setMinimumSize(new Dimension(22, 22));
			btnLocationCity.setMaximumSize(new Dimension(22, 22));
			btnLocationCity.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnLocationCity.setIcon(new ImageIcon("res/images/open_small.png"));
			btnLocationCity.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					searchFilter("location_city", txtLocationCity, lstLocationCity);
				}
			});
			
			// Limpar Cidade
			btnLocationCityClean = new JButton();
			btnLocationCityClean.setToolTipText(rsBundle.getString("screen_audio_comparison_filters_button_location_city_clean_tool_tip"));
			btnLocationCityClean.setMinimumSize(new Dimension(22, 22));
			btnLocationCityClean.setMaximumSize(new Dimension(22, 22));
			btnLocationCityClean.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnLocationCityClean.setIcon(new ImageIcon("res/images/clean_small.png"));
			btnLocationCityClean.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					cleanLocationCity();
				}
			});
			
			panelFilterRecording.add(txtRecordist, "cell 0 0 4 1, grow");
			panelFilterRecording.add(btnRecordist, "cell 4 0, aligny bottom");
			panelFilterRecording.add(btnRecordistClean, "cell 5 0, aligny bottom");
			
			panelFilterRecording.add(panelFilterRecordingDate, "cell 6 0 3 1, grow");
			panelFilterRecordingDate.add(txtDateInitial, "cell 0 0, width 180");
			panelFilterRecordingDate.add(btnDateInitialClean, "cell 1 0, aligny bottom");
			panelFilterRecordingDate.add(txtDateFinal, "cell 2 0, width 180");
			panelFilterRecordingDate.add(btnDateFinalClean, "cell 3 0, aligny bottom");
			
			panelFilterRecording.add(txtLocationCountry, "cell 0 1, grow, width 400");
			panelFilterRecording.add(btnLocationCountry, "cell 1 1, aligny bottom");
			panelFilterRecording.add(btnLocationCountryClean, "cell 2 1, aligny bottom");
			
			panelFilterRecording.add(txtLocationState, "cell 3 1, grow, width 400");
			panelFilterRecording.add(btnLocationState, "cell 4 1, aligny bottom");
			panelFilterRecording.add(btnLocationStateClean, "cell 5 1, aligny bottom");
			
			panelFilterRecording.add(txtLocationCity, "cell 6 1, grow, width 400");
			panelFilterRecording.add(btnLocationCity, "cell 7 1, aligny bottom");
			panelFilterRecording.add(btnLocationCityClean, "cell 8 1, aligny bottom");
			
			// ***********************************************************************************************************************
			// Cria a tela
			objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_comparison_filters_screen_description"), true);
			objWasisDialog.setBounds(350, 350, 700, 245);
			objWasisDialog.setMinimumSize(new Dimension(700, 245));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[] 5 []"));
			objWasisDialog.getContentPane().add(panelFilterAnimal, "cell 0 1, grow");
			objWasisDialog.getContentPane().add(panelFilterRecording, "cell 0 2, grow");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadFilter(LTTextField textFilter, List<String> lstFilter) {
		for (int indexFilter = 0; indexFilter < lstFilter.size(); indexFilter++) {
			if (indexFilter == 0) {
				textFilter.setValue(lstFilter.get(indexFilter));
			} else {
				textFilter.setValue(textFilter.getValue() + ", " + lstFilter.get(indexFilter));
			}
		}
	}
	
	/**
	 * Abre a tela de filtro.
	 * 	
	 * @param strColumnDatabase
	 * @param textFilter
	 * @param lstFilter
	 */
	private void searchFilter(String strColumnDatabase, LTTextField textFilter, List<String> lstFilter) {
		ScreenAudioComparisonFiltersSearch objAudioComparisonFiltersSearch = new ScreenAudioComparisonFiltersSearch(strColumnDatabase, textFilter.getLabel(), lstFilter);
		objAudioComparisonFiltersSearch.setAnimalPhylum(lstAnimalPhylum);
		objAudioComparisonFiltersSearch.setAnimalClass(lstAnimalClass);
		objAudioComparisonFiltersSearch.setAnimalOrder(lstAnimalOrder);
		objAudioComparisonFiltersSearch.setAnimalFamily(lstAnimalFamily);
		objAudioComparisonFiltersSearch.setAnimalGenus(lstAnimalGenus);
		objAudioComparisonFiltersSearch.setLocationCountry(lstLocationCountry);
		objAudioComparisonFiltersSearch.setLocationState(lstLocationState);
		objAudioComparisonFiltersSearch.showScreen();
		
		if (lstFilter.size() > 0) {
			Collections.sort(lstFilter);
			
			for (int indexFilter = 0; indexFilter < lstFilter.size(); indexFilter++) {
				if (indexFilter == 0) {
					textFilter.setValue(lstFilter.get(indexFilter));
				} else {
					textFilter.setValue(textFilter.getValue() + ", " + lstFilter.get(indexFilter));
				}
			}
			
			if (textFilter.getLabel().equals("animal_phylum")) {
				lstAnimalPhylum = lstFilter;
			} else if (textFilter.getLabel().equals("animal_class")) {
				lstAnimalClass = lstFilter;
			} else if (textFilter.getLabel().equals("animal_order")) {
				lstAnimalOrder = lstFilter;
			} else if (textFilter.getLabel().equals("animal_family")) {
				lstAnimalFamily = lstFilter;
			} else if (textFilter.getLabel().equals("animal_genus")) {
				lstAnimalGenus = lstFilter;
			} else if (textFilter.getLabel().equals("animal_species")) {
				lstAnimalSpecies = lstFilter;
			} else if (textFilter.getLabel().equals("recordist")) {
				lstRecordist = lstFilter;
			} else if (textFilter.getLabel().equals("location_country")) {
				lstLocationCountry = lstFilter;
			} else if (textFilter.getLabel().equals("location_state")) {
				lstLocationState = lstFilter;
			} else if (textFilter.getLabel().equals("location_city")) {
				lstLocationCity = lstFilter;
			}
				
		} else {
			clearFilter(strColumnDatabase);
		}
	}
	
	/**
	 * Limpa filtros de um campo passado como parâmetro.
	 * 
	 * @param strColumnDatabase
	 */
	private void clearFilter(String strColumnDatabase) {
		if (strColumnDatabase.equals("animal_phylum")) {
			cleanAnimalPhylum();
		} else if (strColumnDatabase.equals("animal_class")) {
			cleanAnimalClass();
		} else if (strColumnDatabase.equals("animal_order")) {
			cleanAnimalOrder();
		} else if (strColumnDatabase.equals("animal_family")) {
			cleanAnimalFamily();
		} else if (strColumnDatabase.equals("animal_genus")) {
			cleanAnimalGenus();
		} else if (strColumnDatabase.equals("animal_species")) {
			cleanAnimalGenus();
		} else if (strColumnDatabase.equals("recordist")) {
			cleanRecordist();
		} else if (strColumnDatabase.equals("location_country")) {
			cleanLocationCountry();
		} else if (strColumnDatabase.equals("location_state")) {
			cleanLocationState();
		} else if (strColumnDatabase.equals("location_city")) {
			cleanLocationCity();
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Limpa o campo <i>Filo</i>.
	 */
	private void cleanAnimalPhylum() {
		txtAnimalPhylum.setValue("");
		
		lstAnimalPhylum = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Classe</i>.
	 */
	private void cleanAnimalClass() {
		txtAnimalClass.setValue("");
		
		lstAnimalClass = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Ordem</i>.
	 */
	private void cleanAnimalOrder() {
		txtAnimalOrder.setValue("");
		
		lstAnimalOrder = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Família</i>.
	 */
	private void cleanAnimalFamily() {
		txtAnimalFamily.setValue("");
		
		lstAnimalFamily = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Gênero</i>.
	 */
	private void cleanAnimalGenus() {
		txtAnimalGenus.setValue("");
		
		lstAnimalGenus = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Espécie</i>.
	 */
	private void cleanAnimalSpecies() {
		txtAnimalSpecies.setValue("");
		
		lstAnimalSpecies = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Quem Gravou</i>.
	 */
	private void cleanRecordist() {
		txtRecordist.setValue("");
		
		lstRecordist = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Data Inicial</i>.
	 */
	private void cleanDateInitial() {
		txtDateInitial.setValue(null);
	}
	
	/**
	 * Limpa o campo <i>Data Final</i>.
	 */
	private void cleanDateFinal() {
		txtDateFinal.setValue(null);
	}
	
	/**
	 * Limpa o campo <i>País</i>.
	 */
	private void cleanLocationCountry() {
		txtLocationCountry.setValue("");
		
		lstLocationCountry = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Estado</i>.
	 */
	private void cleanLocationState() {
		txtLocationState.setValue("");
		
		lstLocationState = new ArrayList<String>();
	}
	
	/**
	 * Limpa o campo <i>Cidade</i>.
	 */
	private void cleanLocationCity() {
		txtLocationCity.setValue("");
		
		lstLocationCity = new ArrayList<String>();
	}
}