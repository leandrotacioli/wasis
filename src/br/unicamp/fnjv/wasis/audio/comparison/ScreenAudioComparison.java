package br.unicamp.fnjv.wasis.audio.comparison;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.SwingWorker.StateValue;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.comboboxfield.LTComboBoxField;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporary;
import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporarySegments;
import br.unicamp.fnjv.wasis.classifiers.pcc.PearsonCorrelation;
import br.unicamp.fnjv.wasis.classifiers.pcc.PearsonCorrelationValues;
import br.unicamp.fnjv.wasis.database.DatabaseConnection;
import br.unicamp.fnjv.wasis.features.MFCC;
import br.unicamp.fnjv.wasis.features.PowerSpectrum;
import br.unicamp.fnjv.wasis.features.PowerSpectrumValues;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisDialogLoadingData;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que
 * exibe a lista de seleções efetuadas no espectrograma
 * e uma outra lista que mostra os resultados da comparações.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 10/Abr/2017
 */
public class ScreenAudioComparison extends JDialog {
	private static final long serialVersionUID = -5765475068476175620L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	private GraphicPanel objSpectrogramGraphicPanel;
	private AudioWav objAudioWav;
	
	private String strAudioFilePath;
	private long lgnIDLibrary;
	
	private LTComboBoxField cmbDataSource;
	private LTComboBoxField cmbFeature;
	private LTComboBoxField cmbClassifier;
	
	private WasisPanel panelSegments;
	private LTTable objTableSegments;
	
	private JButton btnFilters;
	private JButton btnRunComparison;
	private JButton btnShowResults;
	
	private WasisPanel panelResults;
	private LTTable objTableResults;
	
	private boolean blnRunningComparison;
	
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
	
	private LTTextField txtDateInitial;
	private LTTextField txtDateFinal;

	/**
	 * Classe responsável pela exibição de uma tela que
	 * mostra uma lista de seleções efetuadas no espectrograma
	 * que ainda não foram gravadas no banco de dados, e outra
	 * lista de seleções que já foram gravadas no banco de dados.
 	 * 
 	 * @param strAudioFilePath
	 * @param objSpectrogramGraphicPanel
	 * @param lgnIdLibrary
	 */
	public ScreenAudioComparison(String strAudioFilePath, GraphicPanel objSpectrogramGraphicPanel, long lgnIDLibrary) throws CloneNotSupportedException {
		this.strAudioFilePath = strAudioFilePath;
		
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;
		this.objAudioWav = (AudioWav) objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().clone();
		
		this.lgnIDLibrary = lgnIDLibrary;
		
		lstAnimalPhylum = new ArrayList<String>();
		lstAnimalClass = new ArrayList<String>();
		lstAnimalOrder = new ArrayList<String>();
		lstAnimalFamily = new ArrayList<String>();
		lstAnimalGenus = new ArrayList<String>();
		lstAnimalSpecies = new ArrayList<String>();
		lstRecordist = new ArrayList<String>();
		lstLocationCountry = new ArrayList<String>();
		lstLocationState = new ArrayList<String>();
		lstLocationCity = new ArrayList<String>();
		
		txtDateInitial = new LTTextField("date_initial", LTDataTypes.DATE, false, false);
		txtDateFinal = new LTTextField("date_final", LTDataTypes.DATE, false, false);

		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			// Filtros
			cmbDataSource = new LTComboBoxField(rsBundle.getString("data_source"), true, true);
			cmbDataSource.addValues(rsBundle.getString("screen_audio_comparison_filters_database"), rsBundle.getString("screen_audio_comparison_filters_database"));
			cmbDataSource.addValues(rsBundle.getString("screen_audio_comparison_filters_library"), rsBundle.getString("screen_audio_comparison_filters_library"));
			cmbDataSource.addValues(rsBundle.getString("screen_audio_comparison_filters_audio_file"), rsBundle.getString("screen_audio_comparison_filters_audio_file"));
			cmbDataSource.setValue(rsBundle.getString("screen_audio_comparison_filters_database"));
			
			cmbFeature = new LTComboBoxField(rsBundle.getString("feature"), true, true);
			cmbFeature.addValues("Power Spectrum", rsBundle.getString("feature_power_spectrum"));
			cmbFeature.addValues("MFCC", rsBundle.getString("feature_mfcc"));
			cmbFeature.addValues("LPC", rsBundle.getString("feature_lpc"));
			cmbFeature.addValues("LPCC", rsBundle.getString("feature_lpcc"));
			cmbFeature.addValues("PLP", rsBundle.getString("feature_plp"));
			cmbFeature.addValues("MFCC_LPC", "MFCC + LPC");
			cmbFeature.addValues("MFCC_LPCC", "MFCC + LPCC");
			cmbFeature.addValues("MFCC_PLP", "MFCC + PLP");
			cmbFeature.addValues("MFCC_LPC_LPCC_PLP", "MFCC + LPC + LPCC + PLP");
			cmbFeature.setValue("Power Spectrum");
			
			cmbClassifier = new LTComboBoxField(rsBundle.getString("classifier"), true, true);
			cmbClassifier.addValues("Pearson Correlation", rsBundle.getString("classifier_pearson_correlation"));
			cmbClassifier.setValue("Pearson Correlation");

			// Botão Filtro
			btnFilters = new JButton();
			btnFilters.setMinimumSize(new Dimension(40, 40));
			btnFilters.setMaximumSize(new Dimension(40, 40));
			btnFilters.setIcon(new ImageIcon("res/images/filter.png"));
			btnFilters.setToolTipText("Filtros");
			btnFilters.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					ScreenAudioComparisonFilters objAudioComparisonFilters = new ScreenAudioComparisonFilters();
					objAudioComparisonFilters.setAnimalPhylum(lstAnimalPhylum);
					objAudioComparisonFilters.setAnimalClass(lstAnimalClass);
					objAudioComparisonFilters.setAnimalOrder(lstAnimalOrder);
					objAudioComparisonFilters.setAnimalFamily(lstAnimalFamily);
					objAudioComparisonFilters.setAnimalGenus(lstAnimalGenus);
					objAudioComparisonFilters.setAnimalSpecies(lstAnimalSpecies);
					objAudioComparisonFilters.setRecordist(lstRecordist);
					objAudioComparisonFilters.setLocationCountry(lstLocationCountry);
					objAudioComparisonFilters.setLocationState(lstLocationState);
					objAudioComparisonFilters.setLocationCity(lstLocationCity);
					objAudioComparisonFilters.setDateInitial(txtDateInitial.getValue());
					objAudioComparisonFilters.setDateFinal(txtDateFinal.getValue());
					
					objAudioComparisonFilters.showScreen();
					
					lstAnimalPhylum = objAudioComparisonFilters.getAnimalPhylum();
					lstAnimalClass = objAudioComparisonFilters.getAnimalClass();
					lstAnimalOrder = objAudioComparisonFilters.getAnimalOrder();
					lstAnimalFamily = objAudioComparisonFilters.getAnimalFamily();
					lstAnimalGenus = objAudioComparisonFilters.getAnimalGenus();
					lstAnimalSpecies = objAudioComparisonFilters.getAnimalSpecies();
					lstRecordist = objAudioComparisonFilters.getRecordist();
					lstLocationCountry = objAudioComparisonFilters.getLocationCountry();
					lstLocationState = objAudioComparisonFilters.getLocationState();
					lstLocationCity = objAudioComparisonFilters.getLocationCity();
					txtDateInitial = objAudioComparisonFilters.getDateInitialField();
					txtDateFinal = objAudioComparisonFilters.getDateFinalField();
				}
			});
			
			// *********************************************************************************************
			// Segmentos do Áudio (ROIs)
			panelSegments = new WasisPanel(rsBundle.getString("screen_audio_comparison_selections"));
			panelSegments.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			// Seleções realizadas pelo usuário
			objTableSegments = new LTTable(true);
			objTableSegments.addColumn("sound_unit", rsBundle.getString("audio_file_selection_sound_unit"), LTDataTypes.STRING, 140, false);
			objTableSegments.addColumn("time_initial", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.INTEGER, 0, false);
			objTableSegments.addColumn("time_final", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.INTEGER, 0, false);
			objTableSegments.addColumn("frequency_initial", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.INTEGER, 0, false);
			objTableSegments.addColumn("frequency_final", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.INTEGER, 0, false);
			objTableSegments.addColumn("time_initial_show", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.STRING, 145, false);
			objTableSegments.addColumn("time_final_show", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.STRING, 145, false);
			objTableSegments.addColumn("frequency_initial_show", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.STRING, 145, false);
			objTableSegments.addColumn("frequency_final_show", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.STRING, 145, false);
			objTableSegments.addMouseListener(new SegmentMouseAdapter());
			objTableSegments.showTable();

			loadAudioSegments();
			
			// Botão Comparar Áudio
			btnRunComparison = new JButton(rsBundle.getString("screen_audio_comparison_run_comparison"));
			btnRunComparison.setMinimumSize(new Dimension(100, 30));
			btnRunComparison.setMaximumSize(new Dimension(300, 30));
			btnRunComparison.setIconTextGap(15);
			btnRunComparison.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnRunComparison.setIcon(new ImageIcon("res/images/compare_sounds.png"));
			btnRunComparison.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					compareAudios();
				}
			});
			
			// *********************************************************************************************
			// Resultados da comparação
			panelResults = new WasisPanel(rsBundle.getString("screen_audio_comparison_results"));
			panelResults.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableResults = new LTTable(true);
			objTableResults.addColumn("fk_audio_file_segment", rsBundle.getString("audio_file_selection_id_selection"), LTDataTypes.LONG, 0, false);
			objTableResults.addColumn("correlation_result", rsBundle.getString("screen_audio_comparison_results_correlation"), LTDataTypes.DOUBLE, 120, false);
			objTableResults.addColumn("animal_name_english", rsBundle.getString("animal_name_english"), LTDataTypes.STRING, 230, false);
			objTableResults.addColumn("animal_genus", rsBundle.getString("animal_genus"), LTDataTypes.STRING, 185, false);
			objTableResults.addColumn("animal_species", rsBundle.getString("animal_species"), LTDataTypes.STRING, 185, false);
			objTableResults.addColumn("audio_file_path", rsBundle.getString("audio_file_path"), LTDataTypes.STRING, 0, false);
			objTableResults.addColumn("sound_unit", rsBundle.getString("audio_file_selection_sound_unit"), LTDataTypes.STRING, 0, false);
			objTableResults.addMouseListener(new ResultMouseAdapter());
			objTableResults.showTable();
			
			objTableResults.setColumnDoubleFractionDigits("correlation_result", 4);
			
			// Botão Mostrar Resultados
			btnShowResults = new JButton(rsBundle.getString("screen_audio_comparison_show_results"));
			btnShowResults.setMinimumSize(new Dimension(100, 30));
			btnShowResults.setMaximumSize(new Dimension(300, 30));
			btnShowResults.setIconTextGap(15);
			btnShowResults.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnShowResults.setIcon(new ImageIcon("res/images/results.png"));
			btnShowResults.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					showDetailedResults();
				}
			});
			
			// ***********************************************************************************************************************
			// Cria a tela
			objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_comparison_screen_description"), true);
			objWasisDialog.setBounds(350, 350, 800, 500);
			objWasisDialog.setMinimumSize(new Dimension(800, 500));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[60.00][400.00][][][]"));
			objWasisDialog.getContentPane().add(cmbDataSource, "cell 0 0, grow, width 400");
			objWasisDialog.getContentPane().add(cmbFeature, "cell 0 0, grow, width 500");
			objWasisDialog.getContentPane().add(cmbClassifier, "cell 0 0, grow, width 500");
			objWasisDialog.getContentPane().add(btnFilters, "cell 0 0, grow, gap 5 1 2 0");
			objWasisDialog.getContentPane().add(panelSegments, "cell 0 1, grow");
			objWasisDialog.getContentPane().add(btnRunComparison, "cell 0 2, grow");
			objWasisDialog.getContentPane().add(panelResults, "cell 0 3, grow");
			//objWasisDialog.getContentPane().add(btnShowResults, "cell 0 4, grow");
			
			panelSegments.add(objTableSegments, "cell 0 0, grow");
			
			panelResults.add(objTableResults, "cell 0 0, grow");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carrega os dados dos segmentos (ROIs) do áudio.
	 */
	private void loadAudioSegments() {
		List<AudioTemporarySegments> lstSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments();
		
		for (int indexSegment = 0; indexSegment < lstSegments.size(); indexSegment++) {
			objTableSegments.addRow();
			objTableSegments.addRowData("sound_unit", lstSegments.get(indexSegment).getSoundUnit());
			objTableSegments.addRowData("time_initial", lstSegments.get(indexSegment).getInitialTime());
			objTableSegments.addRowData("time_final", lstSegments.get(indexSegment).getFinalTime());
			objTableSegments.addRowData("frequency_initial", lstSegments.get(indexSegment).getInitialFrequency());
			objTableSegments.addRowData("frequency_final", lstSegments.get(indexSegment).getFinalFrequency());
			objTableSegments.addRowData("time_initial_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getInitialTime()));
			objTableSegments.addRowData("time_final_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getFinalTime()));
			objTableSegments.addRowData("frequency_initial_show", lstSegments.get(indexSegment).getInitialFrequency() + " Hz");
			objTableSegments.addRowData("frequency_final_show", lstSegments.get(indexSegment).getFinalFrequency() + " Hz");
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Realiza a comparação dos áudios dos registros do banco de dados.
	 * 
	 * @param intIndexRow  - Índice da seleção
	 * @param lgnIdLibrary - Id da biblioteca
	 */
	private void compareAudiosFromDatabase(int intIndexRow, long lgnIDLibrary) {
		objTableResults.deleteRows();
		
		PearsonCorrelation objPearsonCorrelation;
		
		int intInitialTime = (int) objTableSegments.getValue(intIndexRow, "time_initial");
		int intFinalTime = (int) objTableSegments.getValue(intIndexRow, "time_final");
		int intInitialFrequency = (int) objTableSegments.getValue(intIndexRow, "frequency_initial");
		int intFinalFrequency = (int) objTableSegments.getValue(intIndexRow, "frequency_final");
		
		List<PearsonCorrelationValues> lstValuesX = new ArrayList<PearsonCorrelationValues>();
		List<PearsonCorrelationValues> lstValuesY = new ArrayList<PearsonCorrelationValues>();
		
		if (cmbFeature.getValue().equals("Power Spectrum")) {
			// Novo
			//int intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
			//int intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
			
			//double[] arrayAmplitudes = objAudioWav.getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
			
			//PowerSpectrum objPS = new PowerSpectrum();
			//objPS.process(arrayAmplitudes, objAudioWav.getWavHeader().getSampleRate());
			
			//List<PowerSpectrumValues> lstPowerSpectrumValues = objPS.filterFrequencies(intInitialFrequency, intFinalFrequency);
			
			//for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
			//	lstValuesX.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
			//}
			
			// Antigo
			List<PowerSpectrumValues> lstPowerSpectrumValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
			
			for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
				lstValuesX.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
			}
			
		} else if (cmbFeature.getValue().equals("MFCC")) {
			int intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
			int intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
			
			double[] arrayAmplitudes = objAudioWav.getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
			
			MFCC objMFCC = new MFCC();
			objMFCC.process(arrayAmplitudes, objAudioWav.getWavHeader().getSampleRate());
			
			double[][] mfcc = objMFCC.getMFCC();
			double[] mean = objMFCC.getMean();
			double[] standardDeviation = objMFCC.getStandardDeviation();
			
			int intIndex = 0;
			
			for (int indexValues = 0; indexValues < mean.length; indexValues++) {
				lstValuesX.add(new PearsonCorrelationValues(intIndex++, mean[indexValues]));
			}
			
			for (int indexValues = 0; indexValues < standardDeviation.length; indexValues++) {
				lstValuesX.add(new PearsonCorrelationValues(intIndex++, standardDeviation[indexValues]));
			}
		}
		
		// Compara com outras seleções já armazenadas no banco de dados
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			
			// Banco de dados
			if (lgnIDLibrary == 0) {
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT id_segment, animal_genus, animal_species, animal_name_portuguese, animal_name_english, audio_file_path, sound_unit, date_refined ");
				objDatabaseConnection.sqlCommandAppend("FROM view_audio_comparison ");
				objDatabaseConnection.sqlCommandAppend("WHERE id_segment > 0 ");
				
				// Adiciona os filtros na consulta
				addDatabaseSelectFilter(objDatabaseConnection, "animal_phylum", lstAnimalPhylum);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_class", lstAnimalClass);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_order", lstAnimalOrder);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_family", lstAnimalFamily);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_genus", lstAnimalGenus);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_species", lstAnimalSpecies);
				addDatabaseSelectFilter(objDatabaseConnection, "recordist", lstRecordist);
				addDatabaseSelectFilter(objDatabaseConnection, "location_country", lstLocationCountry);
				addDatabaseSelectFilter(objDatabaseConnection, "location_state", lstLocationState);
				addDatabaseSelectFilter(objDatabaseConnection, "location_city", lstLocationCity);
				
				// Data Inicial
				if (txtDateInitial.getValue() != null && txtDateInitial.getValue().toString().trim().length() > 0) {
					String strDate = (String) txtDateInitial.getValue();
					
					if (!strDate.equals("")) {
						objDatabaseConnection.sqlCommandAppend("AND date_refined >= ? ");
						objDatabaseConnection.addParameter("date_refined", LTDataTypes.DATE, txtDateInitial.getValue());
					}
				}
				
				// Data Final
				if (txtDateFinal.getValue() != null && txtDateFinal.getValue().toString().trim().length() > 0) {
					String strDate = (String) txtDateFinal.getValue();
					
					if (!strDate.equals("")) {
						objDatabaseConnection.sqlCommandAppend("AND date_refined <= ? ");
						objDatabaseConnection.addParameter("date_refined", LTDataTypes.DATE, txtDateFinal.getValue());
					}
				}
				
			// Mesma biblioteca
			} else {
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, aud.audio_file_path, aud.animal_genus, aud.animal_species, aud.animal_name_portuguese, aud.animal_name_english, seg.sound_unit ");
				objDatabaseConnection.sqlCommandAppend("FROM       libraries                        lib ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN libraries_audio_files            lau     ON     lau.fk_library      = lib.id ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                      aud     ON     aud.audio_file_path = lau.audio_file_path ");
				objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments             seg     ON     seg.fk_audio_file   = aud.id ");
				objDatabaseConnection.sqlCommandAppend("WHERE lib.id = ? ");
				objDatabaseConnection.addParameter("id", LTDataTypes.LONG, lgnIDLibrary);
				
				addDatabaseSelectFilter(objDatabaseConnection, "animal_phylum", lstAnimalPhylum);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_class", lstAnimalClass);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_order", lstAnimalOrder);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_family", lstAnimalFamily);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_genus", lstAnimalGenus);
				addDatabaseSelectFilter(objDatabaseConnection, "animal_species", lstAnimalSpecies);
				addDatabaseSelectFilter(objDatabaseConnection, "recordist", lstRecordist);
				addDatabaseSelectFilter(objDatabaseConnection, "location_country", lstLocationCountry);
				addDatabaseSelectFilter(objDatabaseConnection, "location_state", lstLocationState);
				addDatabaseSelectFilter(objDatabaseConnection, "location_city", lstLocationCity);
				
				// Falta inserir o filtro de data
				// Verificar a criação de uma nova view
			}
	
			ResultSet rsAudioFileSegment = objDatabaseConnection.executeSelectQuery();
			ResultSet rsAudioFileSegmentValues;
			
			while (rsAudioFileSegment.next()) {
				if (cmbFeature.getValue().equals("Power Spectrum")) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT frequency_value, decibel_value "); 
					objDatabaseConnection.sqlCommandAppend("FROM audio_files_segments_ps ");
					objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_file_segment = ? ");
					objDatabaseConnection.sqlCommandAppend("ORDER BY frequency_value");
					objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, rsAudioFileSegment.getLong("id_segment"));
					
					rsAudioFileSegmentValues = objDatabaseConnection.executeSelectQuery();
				    lstValuesY = new ArrayList<PearsonCorrelationValues>();
				    
				    while (rsAudioFileSegmentValues.next()) {
				    	lstValuesY.add(new PearsonCorrelationValues(rsAudioFileSegmentValues.getInt("frequency_value"), rsAudioFileSegmentValues.getDouble("decibel_value")));
					}
				    
				} else if (cmbFeature.getValue().equals("MFCC")) {
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("SELECT seg.id AS id_segment, mfcc.mfcc_vector AS feature_vector ");
					objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   mfcc.fk_audio_file_segment = seg.id ");
					objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file          = aud.id ");
					objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
					objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
					objDatabaseConnection.addParameter("id", LTDataTypes.LONG, rsAudioFileSegment.getLong("id_segment"));
					
					rsAudioFileSegmentValues = objDatabaseConnection.executeSelectQuery();
				    
				    while (rsAudioFileSegmentValues.next()) {
				    	lstValuesY = getFeatureVector(rsAudioFileSegmentValues.getString("feature_vector"));
					}
				}
				
				objPearsonCorrelation = new PearsonCorrelation(true, lstValuesX, lstValuesY);
			    double dblCorrelation = objPearsonCorrelation.calculateCorrelationCoeficient();
			    
			    // Adiciona resultados na 'objTableResults'
			    if (dblCorrelation > 0.25) {
				    objTableResults.addRow();
				    objTableResults.addRowData("fk_audio_file_segment", rsAudioFileSegment.getLong("id_segment"));
				    objTableResults.addRowData("correlation_result", dblCorrelation);
				    objTableResults.addRowData("animal_name_english", rsAudioFileSegment.getString("animal_name_english"));
				    objTableResults.addRowData("animal_genus", rsAudioFileSegment.getString("animal_genus"));
				    objTableResults.addRowData("animal_species", rsAudioFileSegment.getString("animal_species"));
				    objTableResults.addRowData("audio_file_path", rsAudioFileSegment.getString("audio_file_path"));
				    objTableResults.addRowData("sound_unit", rsAudioFileSegment.getString("sound_unit"));
			    }
			}
			
			objTableResults.orderColumnData("correlation_result", false);
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			blnRunningComparison = false;
			
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Adiciona um filtro à consulta do banco de dados.
	 * 
	 * @param objDatabaseConnection
	 * @param textField - Campo a ser filtrado
	 * @param lstFilter - Lista com os valores da coluna a serem filtrados
	 */
	private void addDatabaseSelectFilter(DatabaseConnection objDatabaseConnection, String strDatabaseColumn, List<String> lstFilter) {
		// Caso a lista contenha valores, somente ela é analisada (o campo é descartado)
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
			
		//} else {
		//	if (textField.getValue() != null && textField.getValue().toString().trim().length() > 0) {
		//		objDatabaseConnection.sqlCommandAppend(textField.getColumnDatabase() + " = ? ");
		//		objDatabaseConnection.addParameter(textField.getColumnDatabase(), LTDataTypes.STRING, textField.getValue());
		//	}
		}
	}
	
	/**
	 * Realiza a comparação dos áudios dos 
	 * registros do mesmo arquivo de áudio.
	 * 
	 * @param intIndexRow  - Índice da seleção
	 */
	private void compareAudiosFromAudioFile(int intIndexRow) {
		objTableResults.deleteRows();
		
		int intInitialTime = (int) objTableSegments.getValue(intIndexRow, "time_initial");
		int intFinalTime = (int) objTableSegments.getValue(intIndexRow, "time_final");
		int intInitialFrequency = (int) objTableSegments.getValue(intIndexRow, "frequency_initial");
		int intFinalFrequency = (int) objTableSegments.getValue(intIndexRow, "frequency_final");
		
		List<PearsonCorrelationValues> lstValuesX = new ArrayList<PearsonCorrelationValues>();
		List<PearsonCorrelationValues> lstValuesY = new ArrayList<PearsonCorrelationValues>();
		
		List<PowerSpectrumValues> lstPowerSpectrumValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
		
		for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
			lstValuesX.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
		}
		
		PearsonCorrelation objPearsonCorrelation;
		double dblCorrelation;
		
		// Compara com outras seleções já armazenadas no banco de dados
		for (int indexSegment = 0; indexSegment < objTableSegments.getRowCount(); indexSegment++) {
			intInitialTime = (int) objTableSegments.getValue(indexSegment, "time_initial");
			intFinalTime = (int) objTableSegments.getValue(indexSegment, "time_final");
			intInitialFrequency = (int) objTableSegments.getValue(indexSegment, "frequency_initial");
			intFinalFrequency = (int) objTableSegments.getValue(indexSegment, "frequency_final");
			
			lstPowerSpectrumValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
			
			for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
				lstValuesY.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
			}
			
			objPearsonCorrelation = new PearsonCorrelation(true, lstValuesX, lstValuesY);
		    dblCorrelation = objPearsonCorrelation.calculateCorrelationCoeficient();
		    
		    // Adiciona resultados na 'objTableResults'
		    objTableResults.addRow();
		    objTableResults.addRowData("fk_audio_file_selection", 0);
		    objTableResults.addRowData("correlation_result", dblCorrelation);
		    objTableResults.addRowData("animal_name_english", "");
		    objTableResults.addRowData("animal_genus", "");
		    objTableResults.addRowData("animal_species", "");
		    objTableResults.addRowData("audio_file_path", strAudioFilePath);
		    objTableResults.addRowData("sound_unit", objTableSegments.getValue(indexSegment, "sound_unit"));
		    
		    objSpectrogramGraphicPanel.getSpectrogram().extractComparisonDataMFCC(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
		}
			
		objTableResults.orderColumnData("correlation_result", false);
		
		blnRunningComparison = false;
	}
	
	/**
	 * Realiza a comparação dos áudios em background.
	 */
	private void compareAudios() {
		if (objTableSegments.getRowCount() > 0 && objTableSegments.getSelectedRow() >= 0) {
			final WasisDialogLoadingData objWasisDialogLoadingData = new WasisDialogLoadingData(rsBundle.getString("screen_audio_comparison_running_audio_comparison"));
			
			SwingWorker<Boolean, Integer> swingWorkerLoadWav = new SwingWorker<Boolean, Integer>() {
				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						// Mostra uma caixa de diálogo para o usuário
						// perceber que a comparação está sendo feita
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								objWasisDialogLoadingData.showScreen();
							}
						});
						
						blnRunningComparison = true;
						
						// Banco de dados
						if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_comparison_filters_database"))) {
							compareAudiosFromDatabase(objTableSegments.getSelectedRow(), 0);
						
						// Mesma biblioteca
						} else if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_comparison_filters_library"))) {
							compareAudiosFromDatabase(objTableSegments.getSelectedRow(), lgnIDLibrary);
							
						// Mesmo arquivo de áudio
						} else if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_comparison_filters_audio_file"))) {
							compareAudiosFromAudioFile(objTableSegments.getSelectedRow());
						}
						
						// Aguarda finalizar a comparação
						while (blnRunningComparison) {
							
						}
	
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return true;
				}
			};
			
			// Cria um 'PropertyChangeListener' usado para verificar o status de carregamento/processamento/conversão do WAV
			// Caso o carregamento/processamento/conversão tenha sido efetuado do sucesso, é dado continuidade no próximo processo
			PropertyChangeListener listenerConvertion = new PropertyChangeListener() {
		    	public void propertyChange(PropertyChangeEvent event) {
		    		if (event.getNewValue() == StateValue.DONE) {
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								objWasisDialogLoadingData.disableScreen();
							}
						});
		    		}
		    	}
		    };
			
		    swingWorkerLoadWav.addPropertyChangeListener(listenerConvertion);
		    swingWorkerLoadWav.execute();
		}
	}
	
	/**
	 * Mostra os resultados detalhados da comparação.
	 */
	private void showDetailedResults() {
		if (objTableResults.getRowCount() > 0 && objTableResults.getSelectedRow() >= 0) {
			int intIndexRowSegment = objTableSegments.getSelectedRow();
			int intIndexRowResult = objTableResults.getSelectedRow();
			
			List<PearsonCorrelationValues> lstValuesX = new ArrayList<PearsonCorrelationValues>();
			List<PearsonCorrelationValues> lstValuesY = new ArrayList<PearsonCorrelationValues>();
			
			// Banco de dados
			if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_comparison_filters_database")) || cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_comparison_filters_library"))) {
				int intInitialTime = (int) objTableSegments.getValue(intIndexRowSegment, "time_initial");
				int intFinalTime = (int) objTableSegments.getValue(intIndexRowSegment, "time_final");
				int intInitialFrequency = (int) objTableSegments.getValue(intIndexRowSegment, "frequency_initial");
				int intFinalFrequency = (int) objTableSegments.getValue(intIndexRowSegment, "frequency_final");
				
				if (cmbFeature.getValue().equals("Power Spectrum")) {
					// Novo
					//int intInitialChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intInitialTime);
					//int intFinalChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intFinalTime);
					
					//double[] arrayAmplitudes = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
					
					//PowerSpectrum objPS = new PowerSpectrum();
					//objPS.process(arrayAmplitudes, objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
					
					//List<PowerSpectrumValues> lstPowerSpectrumValues = objPS.filterFrequencies(intInitialFrequency, intFinalFrequency);
					
					//for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
					//	lstValuesX.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
					//}
					
					// Antigo
					List<PowerSpectrumValues> lstPowerSpectrumValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
					
					for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
						lstValuesX.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
					}
					
					// Compara com outras seleções já armazenadas no banco de dados
					DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
					
					try {
						objDatabaseConnection.openConnection();
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("SELECT frequency_value, decibel_value "); 
						objDatabaseConnection.sqlCommandAppend("FROM audio_files_segments_ps ");
						objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_file_segment = ? ");
						objDatabaseConnection.sqlCommandAppend("ORDER BY frequency_value ");
						objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, objTableResults.getValue(intIndexRowResult, "fk_audio_file_segment"));
						
						ResultSet rsAudioFileSegmentsValues = objDatabaseConnection.executeSelectQuery();
						
						while (rsAudioFileSegmentsValues.next()) {
						   lstValuesY.add(new PearsonCorrelationValues(rsAudioFileSegmentsValues.getInt("frequency_value"), rsAudioFileSegmentsValues.getDouble("decibel_value")));
						}
						
						PearsonCorrelation objPearsonCorrelation = new PearsonCorrelation(true, lstValuesX, lstValuesY);
						objPearsonCorrelation.calculateCorrelationCoeficient();
					    
					    lstValuesX = objPearsonCorrelation.getXSample();
					    lstValuesY = objPearsonCorrelation.getYSample();
					    
						ScreenAudioComparisonResult objAudioComparisonResult = new ScreenAudioComparisonResult(lstValuesX, lstValuesY);
						objAudioComparisonResult.showScreen();
						
					} catch (Exception e) {
						e.printStackTrace();
						
					} finally {
						objDatabaseConnection.rollBackTransaction();
						objDatabaseConnection.closeConnection();
					}
					
				} else if (cmbFeature.getValue().equals("MFCC")) {
					int intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
					int intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
					
					double[] arrayAmplitudes = objAudioWav.getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
					
					MFCC objMFCC = new MFCC();
					objMFCC.process(arrayAmplitudes, objAudioWav.getWavHeader().getSampleRate());
					
					double[][] mfcc = objMFCC.getMFCC();
					double[] mean = objMFCC.getMean();
					double[] standardDeviation = objMFCC.getStandardDeviation();
					
					int intIndex = 0;
					
					for (int indexValues = 0; indexValues < mean.length; indexValues++) {
						lstValuesX.add(new PearsonCorrelationValues(intIndex++, mean[indexValues]));
					}
					
					for (int indexValues = 0; indexValues < standardDeviation.length; indexValues++) {
						lstValuesX.add(new PearsonCorrelationValues(intIndex++, standardDeviation[indexValues]));
					}
					
					// Compara com outras seleções já armazenadas no banco de dados
					DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
					
					try {
						objDatabaseConnection.openConnection();
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("SELECT mfcc.mfcc_vector AS feature_vector ");
						objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_mfcc   mfcc ");
						objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments        seg   ON   mfcc.fk_audio_file_segment = seg.id ");
						objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                 aud   ON   seg.fk_audio_file          = aud.id ");
						objDatabaseConnection.sqlCommandAppend("WHERE seg.id = ? ");
						objDatabaseConnection.sqlCommandAppend("AND mfcc.ind_normalized = 1 ");
						objDatabaseConnection.addParameter("id", LTDataTypes.LONG, objTableResults.getValue(intIndexRowResult, "fk_audio_file_segment"));
						
						ResultSet rsAudioFileSegmentValues = objDatabaseConnection.executeSelectQuery();
						
						while (rsAudioFileSegmentValues.next()) {
							lstValuesY = getFeatureVector(rsAudioFileSegmentValues.getString("feature_vector"));
						}
						
						PearsonCorrelation objPearsonCorrelation = new PearsonCorrelation(true, lstValuesX, lstValuesY);
						objPearsonCorrelation.calculateCorrelationCoeficient();
					    
					    lstValuesX = objPearsonCorrelation.getXSample();
					    lstValuesY = objPearsonCorrelation.getYSample();
					    
						ScreenAudioComparisonResult objAudioComparisonResult = new ScreenAudioComparisonResult(lstValuesX, lstValuesY);
						objAudioComparisonResult.showScreen();
						
					} catch (Exception e) {
						e.printStackTrace();
						
					} finally {
						objDatabaseConnection.rollBackTransaction();
						objDatabaseConnection.closeConnection();
					}
				}
			
			} else if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_comparison_filters_audio_file"))) {
				int intInitialTime = (int) objTableSegments.getValue(intIndexRowSegment, "time_initial");
				int intFinalTime = (int) objTableSegments.getValue(intIndexRowSegment, "time_final");
				int intInitialFrequency = (int) objTableSegments.getValue(intIndexRowSegment, "frequency_initial");
				int intFinalFrequency = (int) objTableSegments.getValue(intIndexRowSegment, "frequency_final");
				
				// Novo
				//int intInitialChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intInitialTime);
				//int intFinalChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intFinalTime);
				
				//double[] arrayAmplitudes = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
				
				//PowerSpectrum objPS = new PowerSpectrum();
				//objPS.process(arrayAmplitudes, objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
								
				//List<PowerSpectrumValues> lstPowerSpectrumValues = objPS.filterFrequencies(intInitialFrequency, intFinalFrequency);
				
				//for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
				//	lstValuesX.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
				//}
				
				// Antigo
				List<PowerSpectrumValues> lstPowerSpectrumValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
				
				for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
					lstValuesX.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
				}
				
				for (int indexSegment = 0; indexSegment < objTableSegments.getRowCount(); indexSegment++) {
					if (objTableResults.getValue(intIndexRowResult, "sound_unit").equals(objTableSegments.getValue(indexSegment, "sound_unit"))) {
						intInitialTime = (int) objTableSegments.getValue(indexSegment, "time_initial");
						intFinalTime = (int) objTableSegments.getValue(indexSegment, "time_final");
						intInitialFrequency = (int) objTableSegments.getValue(indexSegment, "frequency_initial");
						intFinalFrequency = (int) objTableSegments.getValue(indexSegment, "frequency_final");
						
						// Novo
						//intInitialChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intInitialTime);
						//intFinalChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intFinalTime);
						
						//arrayAmplitudes = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
						
						//objPS = new PowerSpectrum();
						//objPS.process(arrayAmplitudes, objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
						
						//lstPowerSpectrumValues = objPS.filterFrequencies(intInitialFrequency, intFinalFrequency);
						
						//for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
						//	lstValuesY.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
						//}
						
						// Antigo
						lstPowerSpectrumValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData(intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
						
						for (int indexValues = 0; indexValues < lstPowerSpectrumValues.size(); indexValues++) {
							lstValuesY.add(new PearsonCorrelationValues(lstPowerSpectrumValues.get(indexValues).getFrequency(), lstPowerSpectrumValues.get(indexValues).getDecibel()));
						}
						
						PearsonCorrelation objPearsonCorrelation = new PearsonCorrelation(true, lstValuesX, lstValuesY);
						objPearsonCorrelation.calculateCorrelationCoeficient();
					    
					    lstValuesX = objPearsonCorrelation.getXSample();
					    lstValuesY = objPearsonCorrelation.getYSample();
					    
						ScreenAudioComparisonResult objAudioComparisonResult = new ScreenAudioComparisonResult(lstValuesX, lstValuesY);
						objAudioComparisonResult.showScreen();
						
						break;
					}
				}
			}
		}
	}
	
	/**
	 * Transforma uma lista mo vetor do formato da base de dados para um legível ao Brute Force.
	 * 
	 * @param lstValues
	 */
	private List<PearsonCorrelationValues> getFeatureVector(String strFeature) {
		String[] originalFeatureVector = strFeature.split(";");
		
		List<PearsonCorrelationValues> lstValues = new ArrayList<PearsonCorrelationValues>();
		
		for (int indexElement = 0; indexElement < originalFeatureVector.length; indexElement++) {
			lstValues.add(new PearsonCorrelationValues(indexElement, Double.parseDouble(originalFeatureVector[indexElement])));
		}
		
		return lstValues;
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável 
	 * pela execução da comparação de áudios quando houver 
	 * clique em um segmento de áudio (ROI).
	 */
	private class SegmentMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 1) {
				if (objTableSegments.getRowCount() > 0) {
					compareAudios();
				}
			}
	    }
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável 
	 * pelo carregamento da tela de resultados quando houver
	 * duplo clique em um registro.
	 */
	private class ResultMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 2) {
				if (objTableResults.getRowCount() > 0) {
					showDetailedResults();
				}
			}
	    }
	}
}