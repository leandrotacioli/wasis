package br.unicamp.fnjv.wasis.audio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.features.Features;
import br.unicamp.fnjv.wasis.features.LPC;
import br.unicamp.fnjv.wasis.features.MFCC;
import br.unicamp.fnjv.wasis.features.PLP;
import br.unicamp.fnjv.wasis.features.PowerSpectrum;
import br.unicamp.fnjv.wasis.features.Preprocessing;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.Arrays;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.libs.FileManager;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisContainer;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;
import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

/**
 * Classe responsável pela exibição de uma tela que possui todos os campos necessários
 * para gravação de um arquivo sonoro, ROIs e suas features no banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 5.0 - 27/Out/2017
 */
public class ScreenSaveAudioData extends JDialog {
	private static final long serialVersionUID = 1232734137198386896L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private GraphicPanel objSpectrogramGraphicPanel;
	
	private List<Integer> lstAudioSegmentsSelected;
	
	private long lgnIdAudioFile;
	private String strAudioFilePath;
	private String strAudioFileHash;
	
	private WasisPanel panelAudioInformation;
	private LTTextField txtAudioFilePath;
	private LTTextField txtVoucherNumber;
	
	private WasisPanel panelScientificClassification;
	private JButton btnSearchAnimalTaxonomy;
	private LTTextField txtAnimalPhylum;
	private LTTextField txtAnimalClass;
	private LTTextField txtAnimalOrder;
	private LTTextField txtAnimalFamily;
	private LTTextField txtAnimalGenus;
	private LTTextField txtAnimalSpecies;
	private LTTextField txtAnimalNamePortuguese;
	private LTTextField txtAnimalNameEnglish;
	
	private WasisPanel panelRecordingInformation;
	private LTTextField txtLocationCountry;
	private LTTextField txtLocationState;
	private LTTextField txtLocationCity;
	private LTTextField txtDateDay;
	private LTTextField txtDateMonth;
	private LTTextField txtDateYear;
	private LTTextField txtTimeRecording;
	private LTTextField txtRecordist;
	private LTTextField txtObservation;
	
	private WasisPanel panelAudioSegments;
	private LTTable objTableAudioSegments;
	
	private JButton btnSaveData;
	
	private JProgressBar progressBar;
	
	/**
	 * Classe responsável pela exibição de uma tela que possui todos os campos necessários
	 * para gravação de um arquivo sonoro, ROIs e suas features no banco de dados.
 	 * 
 	 * @param objSpectrogramGraphicPanel
	 * @param lstAudioSegmentsSelected   - Lista com a identificação dos segmentos (ROIs) que deverão ser inseridos no banco de dados
	 */
	protected ScreenSaveAudioData(GraphicPanel objSpectrogramGraphicPanel, List<Integer> lstAudioSegmentsSelected) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;
		this.lstAudioSegmentsSelected = lstAudioSegmentsSelected;
		
		try {
			this.strAudioFilePath = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal();
			this.strAudioFileHash = FileManager.getFileHash(new File(strAudioFilePath));
		} catch (Error | Exception e) {
			e.printStackTrace();
		}

		loadScreen();
		loadAudioData();
		loadAudioSegments();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// ***************************************************************************************************************
		// Informações do Arquivo de Áudio
		panelAudioInformation = new WasisPanel(rsBundle.getString("screen_save_audio_data_audio_file_information") + ":");
		panelAudioInformation.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		txtAudioFilePath = new LTTextField(rsBundle.getString("audio_file_path") + ":", LTDataTypes.STRING, false, false, 500);
		txtAudioFilePath.setValue(strAudioFilePath);
		
		txtVoucherNumber = new LTTextField(rsBundle.getString("audio_file_voucher_number") + ":", LTDataTypes.STRING, true, false, 200);
		
		// ***************************************************************************************************************
		// Dados da Classificação Científica
		panelScientificClassification = new WasisPanel(rsBundle.getString("screen_save_audio_data_scientific_classification") + ":");
		panelScientificClassification.setLayout(new MigLayout("insets 0", "[100.00] 1.00 [grow]", "[][][]"));
		
		btnSearchAnimalTaxonomy = new JButton();
		btnSearchAnimalTaxonomy.setFocusable(false);
		btnSearchAnimalTaxonomy.setToolTipText(rsBundle.getString("screen_search_animal_taxonomy_screen_description"));
		btnSearchAnimalTaxonomy.setIconTextGap(15);
		btnSearchAnimalTaxonomy.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSearchAnimalTaxonomy.setIcon(new ImageIcon("res/images/search.png"));
		btnSearchAnimalTaxonomy.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				ScreenSearchAnimalTaxonomy objScreenSearchAnimalTaxonomy = new ScreenSearchAnimalTaxonomy();
				objScreenSearchAnimalTaxonomy.showScreen();
	    		
				loadAnimalTaxonomy(objScreenSearchAnimalTaxonomy.getIdAnimalTaxonomy());
			}
		});
		
		txtAnimalPhylum = new LTTextField(rsBundle.getString("animal_phylum") + ":", LTDataTypes.STRING, true, false, 100);
		txtAnimalClass = new LTTextField(rsBundle.getString("animal_class") + ":", LTDataTypes.STRING, true, false, 100);
		txtAnimalOrder = new LTTextField(rsBundle.getString("animal_order") + ":", LTDataTypes.STRING, true, false, 100);
		txtAnimalFamily = new LTTextField(rsBundle.getString("animal_family") + ":", LTDataTypes.STRING, true, false, 100);
		txtAnimalGenus = new LTTextField(rsBundle.getString("animal_genus") + ":", LTDataTypes.STRING, true, false, 100);
		txtAnimalSpecies = new LTTextField(rsBundle.getString("animal_species") + ":", LTDataTypes.STRING, true, false, 100);
		txtAnimalNamePortuguese = new LTTextField(rsBundle.getString("animal_name_portuguese") + ":", LTDataTypes.STRING, true, false, 100);
		txtAnimalNameEnglish = new LTTextField(rsBundle.getString("animal_name_english") + ":", LTDataTypes.STRING, true, false, 100);
		
		// ***************************************************************************************************************
		// Dados da Gravação
		panelRecordingInformation = new WasisPanel(rsBundle.getString("screen_save_audio_data_recording_information") + ":");
		panelRecordingInformation.setLayout(new MigLayout("insets 0", "[grow]", "[][][][75.00]"));
		
		txtLocationCountry = new LTTextField(rsBundle.getString("audio_file_location_country") + ":", LTDataTypes.STRING, true, false, 100);
		txtLocationState = new LTTextField(rsBundle.getString("audio_file_location_state") + ":", LTDataTypes.STRING, true, false, 100);
		txtLocationCity = new LTTextField(rsBundle.getString("audio_file_location_city") + ":", LTDataTypes.STRING, true, false, 100);
		txtDateDay = new LTTextField(rsBundle.getString("audio_file_date_day") + ":", LTDataTypes.INTEGER, true, false);
		txtDateMonth = new LTTextField(rsBundle.getString("audio_file_date_month") + ":", LTDataTypes.INTEGER, true, false);
		txtDateYear = new LTTextField(rsBundle.getString("audio_file_date_year") + ":", LTDataTypes.INTEGER, true, false);
		txtTimeRecording = new LTTextField(rsBundle.getString("audio_file_time_recording") + ":", LTDataTypes.STRING, true, false, 20);
		txtRecordist = new LTTextField(rsBundle.getString("audio_file_recordist") + ":", LTDataTypes.STRING, true, false, 100);
		txtObservation = new LTTextField(rsBundle.getString("audio_file_observations") + ":", LTDataTypes.TEXT, true, false);
		
		// ***************************************************************************************************************
		// Dados de seleção
		panelAudioSegments = new WasisPanel();
		panelAudioSegments.setLayout(new MigLayout("insets 4", "[grow]", "[grow]"));
		
		objTableAudioSegments = new LTTable(true, false);
		objTableAudioSegments.addColumn("index_temporary", "", LTDataTypes.INTEGER, 0, false);
		objTableAudioSegments.addColumn("id_audio_segment", "ID Audio Segment", LTDataTypes.LONG, 0, false);
		objTableAudioSegments.addColumn("audio_segment", rsBundle.getString("audio_segment"), LTDataTypes.STRING, 135, false);
		objTableAudioSegments.addColumn("time_initial", rsBundle.getString("audio_segment_time_initial"), LTDataTypes.INTEGER, 0, false);
		objTableAudioSegments.addColumn("time_final", rsBundle.getString("audio_segment_time_final"), LTDataTypes.INTEGER, 0, false);
		objTableAudioSegments.addColumn("frequency_initial", rsBundle.getString("audio_segment_frequency_minimum"), LTDataTypes.INTEGER, 0, false);
		objTableAudioSegments.addColumn("frequency_final", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.INTEGER, 0, false);
		objTableAudioSegments.addColumn("time_initial_show", rsBundle.getString("audio_segment_time_initial"), LTDataTypes.STRING, 150, false);
		objTableAudioSegments.addColumn("time_final_show", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.STRING, 150, false);
		objTableAudioSegments.addColumn("frequency_initial_show", rsBundle.getString("audio_segment_frequency_minimum"), LTDataTypes.STRING, 150, false);
		objTableAudioSegments.addColumn("frequency_final_show", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.STRING, 150, false);
		objTableAudioSegments.showTable();
		
		// ***************************************************************************************************************
		btnSaveData = new JButton(rsBundle.getString("screen_save_audio_data_save_audio_data"));
		btnSaveData.setFocusable(false);
		btnSaveData.setMinimumSize(new Dimension(100, 30));
		btnSaveData.setMaximumSize(new Dimension(400, 30));
		btnSaveData.setIconTextGap(15);
		btnSaveData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSaveData.setIcon(new ImageIcon("res/images/save.png"));
		btnSaveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				saveRecordingData();
			}
		});
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		
		// ***************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_save_audio_data_screen_description"), false);
		objWasisDialog.setBounds(350, 350, 800, 575);
		objWasisDialog.setMinimumSize(new Dimension(800, 575));
		objWasisDialog.setMaximumSize(new Dimension(800, 575));
		objWasisDialog.setResizable(false);
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[] 2 [] 2 [] 2 [100.00, grow] 4 []"));
		objWasisDialog.getContentPane().add(panelAudioInformation, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelScientificClassification, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(panelRecordingInformation, "cell 0 2, grow");
		objWasisDialog.getContentPane().add(panelAudioSegments, "cell 0 3, grow");
		objWasisDialog.getContentPane().add(btnSaveData, "cell 0 4, width 255");
		objWasisDialog.getContentPane().add(progressBar, "cell 0 4, grow");
		
		panelAudioInformation.add(txtAudioFilePath, "cell 0 0, grow, width 369");
		panelAudioInformation.add(txtVoucherNumber, "cell 0 0, grow");
		
		panelScientificClassification.add(btnSearchAnimalTaxonomy, "cell 0 0 0 3, width 40, growy");
		panelScientificClassification.add(txtAnimalPhylum, "cell 1 0, grow, width 250");
		panelScientificClassification.add(txtAnimalClass, "cell 1 0, grow, width 250");
		panelScientificClassification.add(txtAnimalOrder, "cell 1 0, grow, width 250");
		panelScientificClassification.add(txtAnimalFamily, "cell 1 1, grow, width 250");
		panelScientificClassification.add(txtAnimalGenus, "cell 1 1, grow, width 250");
		panelScientificClassification.add(txtAnimalSpecies, "cell 1 1, grow, width 250");
		panelScientificClassification.add(txtAnimalNamePortuguese, "cell 1 2, grow, width 250");
		panelScientificClassification.add(txtAnimalNameEnglish, "cell 1 2, grow, width 250");
		
		panelRecordingInformation.add(txtLocationCountry, "cell 0 0, grow, width 100");
		panelRecordingInformation.add(txtLocationState, "cell 0 0, grow, width 100");
		panelRecordingInformation.add(txtLocationCity, "cell 0 0, grow, width 100");
		panelRecordingInformation.add(txtDateDay, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtDateMonth, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtDateYear, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtTimeRecording, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtRecordist, "cell 0 2, grow, width 100");
		panelRecordingInformation.add(txtObservation, "cell 0 3, grow");
		
		panelAudioSegments.add(objTableAudioSegments, "cell 0 0, grow");
		
		// ***************************************************************************************************************
		// Executa o método 'exitScreen()' ao clicar no botão fechar
		objWasisDialog.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent event) {
            	exitScreen();
            }
        });
	}
	
	/**
	 * Carrega os dados do arquivo de áudio do banco de dados.
	 */
	private void loadAudioData() {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT aud.*, tax.* ");
			objDatabaseConnection.sqlCommandAppend("FROM            audio_files             aud ");
			objDatabaseConnection.sqlCommandAppend("LEFT OUTER JOIN audio_files_segments    seg   ON   seg.fk_audio_file      = aud.id_audio_file ");
			objDatabaseConnection.sqlCommandAppend("LEFT OUTER JOIN animal_taxonomies       tax   ON   seg.fk_animal_taxonomy = tax.id_animal_taxonomy ");
			objDatabaseConnection.sqlCommandAppend("WHERE aud.audio_file_path = ? ");
			objDatabaseConnection.sqlCommandAppend("AND aud.audio_file_hash = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY seg.id_audio_segment LIMIT 1 ");
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePath);
			objDatabaseConnection.addParameter("audio_file_hash", LTDataTypes.STRING, strAudioFileHash);
			
			ResultSet rsAudioData = objDatabaseConnection.executeSelectQuery();
			
			while (rsAudioData.next()) {
				lgnIdAudioFile = rsAudioData.getLong("id_audio_file");
				txtVoucherNumber.setValue(rsAudioData.getString("voucher_number"));
				txtAnimalPhylum.setValue(rsAudioData.getString("animal_phylum"));
				txtAnimalClass.setValue(rsAudioData.getString("animal_class"));
				txtAnimalOrder.setValue(rsAudioData.getString("animal_order"));
				txtAnimalFamily.setValue(rsAudioData.getString("animal_family"));
				txtAnimalGenus.setValue(rsAudioData.getString("animal_genus"));
				txtAnimalSpecies.setValue(rsAudioData.getString("animal_species"));
				txtAnimalNamePortuguese.setValue(rsAudioData.getString("animal_name_portuguese"));
				txtAnimalNameEnglish.setValue(rsAudioData.getString("animal_name_english"));
				txtLocationCity.setValue(rsAudioData.getString("location_city"));
				txtLocationState.setValue(rsAudioData.getString("location_state"));
				txtLocationCountry.setValue(rsAudioData.getString("location_country"));
				txtDateDay.setValue(rsAudioData.getString("date_day"));
				txtDateMonth.setValue(rsAudioData.getString("date_month"));
				txtDateYear.setValue(rsAudioData.getString("date_year"));
				txtTimeRecording.setValue(rsAudioData.getString("time_recording"));
				txtRecordist.setValue(rsAudioData.getString("recordist"));
				txtObservation.setValue(rsAudioData.getString("observations"));
			}
		
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Carrega os dados dos segmentos de áudio selecionados e que serão gravados.
	 */
	private void loadAudioSegments() {
		List<AudioSegmentsValues> lstAudioSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).getAudioSegments();
		
		for (int indexAudioSegment = 0; indexAudioSegment < lstAudioSegmentsSelected.size(); indexAudioSegment++) {
			int intIndexAudioSegment = lstAudioSegmentsSelected.get(indexAudioSegment);
			
			objTableAudioSegments.addRow();
			objTableAudioSegments.addRowData("index_temporary", intIndexAudioSegment);
			objTableAudioSegments.addRowData("id_audio_segment", lstAudioSegments.get(intIndexAudioSegment).getIdDatabase());
			objTableAudioSegments.addRowData("audio_segment", lstAudioSegments.get(intIndexAudioSegment).getAudioSegment());
			objTableAudioSegments.addRowData("time_initial", lstAudioSegments.get(intIndexAudioSegment).getInitialTime());
			objTableAudioSegments.addRowData("time_final", lstAudioSegments.get(intIndexAudioSegment).getFinalTime());
			objTableAudioSegments.addRowData("frequency_initial", lstAudioSegments.get(intIndexAudioSegment).getInitialFrequency());
			objTableAudioSegments.addRowData("frequency_final", lstAudioSegments.get(intIndexAudioSegment).getFinalFrequency());
			objTableAudioSegments.addRowData("time_initial_show", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(intIndexAudioSegment).getInitialTime()));
			objTableAudioSegments.addRowData("time_final_show", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(intIndexAudioSegment).getFinalTime()));
			objTableAudioSegments.addRowData("frequency_initial_show", lstAudioSegments.get(intIndexAudioSegment).getInitialFrequency() + " Hz");
			objTableAudioSegments.addRowData("frequency_final_show", lstAudioSegments.get(intIndexAudioSegment).getFinalFrequency() + " Hz");
		}
	}
	
	/**
	 * Preenche os dados taxonômicos.
	 * 
	 * @param intIdAnimalTaxonomy
	 */
	private void loadAnimalTaxonomy(int intIdAnimalTaxonomy) {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT * ");
			objDatabaseConnection.sqlCommandAppend("FROM animal_taxonomies ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_animal_taxonomy = ? ");
			objDatabaseConnection.addParameter("id_animal_taxonomy", LTDataTypes.INTEGER, intIdAnimalTaxonomy);
			
			ResultSet rsRecord = objDatabaseConnection.executeSelectQuery();
			
			while (rsRecord.next()) {
				txtAnimalPhylum.setValue(rsRecord.getString("animal_phylum"));
				txtAnimalClass.setValue(rsRecord.getString("animal_class"));
				txtAnimalOrder.setValue(rsRecord.getString("animal_order"));
				txtAnimalFamily.setValue(rsRecord.getString("animal_family"));
				txtAnimalGenus.setValue(rsRecord.getString("animal_genus"));
				txtAnimalSpecies.setValue(rsRecord.getString("animal_species"));
				txtAnimalNamePortuguese.setValue(rsRecord.getString("animal_name_portuguese"));
				txtAnimalNameEnglish.setValue(rsRecord.getString("animal_name_english"));
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Salva os dados das gravações.
	 */
	private void saveRecordingData() {
		final DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		SwingWorker<Boolean, Boolean> swingWorkerSave = new SwingWorker<Boolean, Boolean>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					setComponentEnabled(false);
					
					progressBar.setVisible(true);
					
					long lgnIdAnimalTaxonomy = AnimalTaxonomy.manageAnimalTaxonomy(txtAnimalPhylum.getValue().toString(), txtAnimalClass.getValue().toString(), txtAnimalOrder.getValue().toString(), 
                                                                                   txtAnimalFamily.getValue().toString(), txtAnimalGenus.getValue().toString(), txtAnimalSpecies.getValue().toString(),
                                                                                   txtAnimalNamePortuguese.getValue().toString(), txtAnimalNameEnglish.getValue().toString());
					
					objDatabaseConnection.openConnection();
					
					// Insere novo arquivo de áudio
					if (lgnIdAudioFile == 0) {
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("INSERT INTO audio_files (audio_file_path, audio_file_hash, voucher_number, fk_animal_taxonomy, " +
																	   	         " location_city, location_state, location_country, " +
								                                       	         " date_day, date_month, date_year, time_recording, " + 
																	   	         " recordist, observations, date_update) ");
						objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
						objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePath);
						objDatabaseConnection.addParameter("audio_file_hash", LTDataTypes.STRING, strAudioFileHash);
						objDatabaseConnection.addParameter("voucher_number", LTDataTypes.STRING, txtVoucherNumber.getValue());
						objDatabaseConnection.addParameter("fk_animal_taxonomy", LTDataTypes.LONG, lgnIdAnimalTaxonomy);
						objDatabaseConnection.addParameter("location_city", LTDataTypes.STRING, txtLocationCity.getValue());
						objDatabaseConnection.addParameter("location_state", LTDataTypes.STRING, txtLocationState.getValue());
						objDatabaseConnection.addParameter("location_country", LTDataTypes.STRING, txtLocationCountry.getValue());
						objDatabaseConnection.addParameter("date_day", LTDataTypes.INTEGER, txtDateDay.getValue());
						objDatabaseConnection.addParameter("date_month", LTDataTypes.INTEGER, txtDateMonth.getValue());
						objDatabaseConnection.addParameter("date_year", LTDataTypes.INTEGER, txtDateYear.getValue());
						objDatabaseConnection.addParameter("time_recording", LTDataTypes.STRING, txtTimeRecording.getValue());
						objDatabaseConnection.addParameter("recordist", LTDataTypes.STRING, txtRecordist.getValue());
						objDatabaseConnection.addParameter("observations", LTDataTypes.STRING, txtObservation.getValue());
						objDatabaseConnection.addParameter("date_update", LTDataTypes.DATE, new Date());
						objDatabaseConnection.executeQuery();
						
						lgnIdAudioFile = objDatabaseConnection.getIdentityKey();
						
					// Atualiza arquivo de áudio
					} else {
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("UPDATE audio_files ");
						objDatabaseConnection.sqlCommandAppend("SET voucher_number = ?, location_city = ?, location_state = ?, location_country = ?, " + 
						                                       "    date_day = ?, date_month = ?, date_year = ?, time_recording = ?, recordist = ?, observations = ?, date_update = ? ");
						objDatabaseConnection.sqlCommandAppend("WHERE id_audio_file = ? ");
						objDatabaseConnection.addParameter("voucher_number", LTDataTypes.STRING, txtVoucherNumber.getValue());
						objDatabaseConnection.addParameter("location_city", LTDataTypes.STRING, txtLocationCity.getValue());
						objDatabaseConnection.addParameter("location_state", LTDataTypes.STRING, txtLocationState.getValue());
						objDatabaseConnection.addParameter("location_country", LTDataTypes.STRING, txtLocationCountry.getValue());
						objDatabaseConnection.addParameter("date_day", LTDataTypes.INTEGER, txtDateDay.getValue());
						objDatabaseConnection.addParameter("date_month", LTDataTypes.INTEGER, txtDateMonth.getValue());
						objDatabaseConnection.addParameter("date_year", LTDataTypes.INTEGER, txtDateYear.getValue());
						objDatabaseConnection.addParameter("time_recording", LTDataTypes.STRING, txtTimeRecording.getValue());
						objDatabaseConnection.addParameter("recordist", LTDataTypes.STRING, txtRecordist.getValue());
						objDatabaseConnection.addParameter("observations", LTDataTypes.STRING, txtObservation.getValue());
						objDatabaseConnection.addParameter("date_update", LTDataTypes.DATE, new Date());
						objDatabaseConnection.addParameter("id_audio_file", LTDataTypes.LONG, lgnIdAudioFile);
						objDatabaseConnection.executeQuery();
					}
					
					double[] arrayAmplitudes;
					double[] preEmphasis;
					double[][] framesWithPreemphasis;
					double[][] framesWithoutPreemphasis;
					
					PowerSpectrum objPowerSpectrum;
					double[][] powerSpectrum;
					
					MFCC objMFCC;
					double[][] mfcc;
					double[] mfccMeanStandardDeviation;
					
					LPC objLPC;
					double[][] lpc;
					double[] lpcMeanStandardDeviation;
					
					double[][] lpcc;
					double[] lpccMeanStandardDeviation;
					
					PLP objPLP;
					double[][] plp;
					double[] plpMeanStandardDeviation;
					
					// Grava os segmentos de áudio
					for (int indexAudioSegment = 0; indexAudioSegment < objTableAudioSegments.getRowCount(); indexAudioSegment++) {
						long lgnIdAudioFileSegment = (long) objTableAudioSegments.getValue(indexAudioSegment, "id_audio_segment");
						
						// Adiciona um novo segmento de áudio
						if (lgnIdAudioFileSegment == 0) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments (fk_audio_file, fk_animal_taxonomy, audio_segment, " +
									                         "                                  time_initial, time_final, frequency_initial, frequency_final, ind_active, date_update) ");
							objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) ");
							objDatabaseConnection.addParameter("fk_audio_file", LTDataTypes.LONG, lgnIdAudioFile);
							objDatabaseConnection.addParameter("fk_animal_taxonomy", LTDataTypes.LONG, lgnIdAnimalTaxonomy);
							objDatabaseConnection.addParameter("audio_segment", LTDataTypes.STRING, objTableAudioSegments.getValue(indexAudioSegment, "audio_segment"));
							objDatabaseConnection.addParameter("time_initial", LTDataTypes.INTEGER, objTableAudioSegments.getValue(indexAudioSegment, "time_initial"));
							objDatabaseConnection.addParameter("time_final", LTDataTypes.INTEGER, objTableAudioSegments.getValue(indexAudioSegment, "time_final"));
							objDatabaseConnection.addParameter("frequency_initial", LTDataTypes.INTEGER, objTableAudioSegments.getValue(indexAudioSegment, "frequency_initial"));
							objDatabaseConnection.addParameter("frequency_final", LTDataTypes.INTEGER, objTableAudioSegments.getValue(indexAudioSegment, "frequency_final"));
							objDatabaseConnection.addParameter("ind_active", LTDataTypes.BOOLEAN, true);
							objDatabaseConnection.addParameter("date_update", LTDataTypes.DATE, new Date());
							objDatabaseConnection.executeQuery();
							
							lgnIdAudioFileSegment = (long) objDatabaseConnection.getIdentityKey();
							
							// Extração de Features
							int intInitialChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime((int) objTableAudioSegments.getValue(indexAudioSegment, "time_initial"));
							int intFinalChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime((int) objTableAudioSegments.getValue(indexAudioSegment, "time_final"));
							
							arrayAmplitudes = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAmplitudesChunk(intInitialChunkToProcess, intFinalChunkToProcess);
							
							preEmphasis = Preprocessing.preEmphasis(arrayAmplitudes);
							
							framesWithPreemphasis = Preprocessing.framing(preEmphasis);          // Utilizado na MFCC, LPC, LPCC
							framesWithoutPreemphasis = Preprocessing.framing(arrayAmplitudes);   // Utilizado na PS, PLP
							
							// Total de frames gerados para extrair as features
							// O total de vetores extraídos pelas features MFCC, LPC, LPCC e PLP devem ser igual ao total de frames
							int intTotalFrames = framesWithPreemphasis.length;
							
							if (framesWithPreemphasis.length != framesWithoutPreemphasis.length) {
								new Exception("Número de frames gerados na extração das features é diferente para as diversas representações");
							}
							
							// Power Spectrum (PS)
							objPowerSpectrum = new PowerSpectrum(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate(), (int) objTableAudioSegments.getValue(indexAudioSegment, "frequency_initial"), (int) objTableAudioSegments.getValue(indexAudioSegment, "frequency_final"));
							objPowerSpectrum.processFrames(framesWithoutPreemphasis);
							
							powerSpectrum = objPowerSpectrum.getFeature();
							
							// Valores de frequência armazenados como inteiros - Decibel como double
							String strFrequencyVector = Features.getFeatureCoefficients(powerSpectrum[0]);
							strFrequencyVector = strFrequencyVector.replace(".0", "");
							
							String strDecibelVector = Features.getFeatureCoefficients(powerSpectrum[1]);
							
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments_features_ps (fk_audio_file_segment, frequency_vector, decibel_vector) ");
							objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
							objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioFileSegment);
							objDatabaseConnection.addParameter("frequency_vector", LTDataTypes.STRING, strFrequencyVector);
							objDatabaseConnection.addParameter("decibel_vector", LTDataTypes.STRING, strDecibelVector);
							objDatabaseConnection.executeQuery();
							
							// Mel Frequency Cepstral Coefficients (MFCC)
							objMFCC = new MFCC(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
							objMFCC.processFrames(framesWithPreemphasis);
							
							mfcc = objMFCC.getFeature();
							mfccMeanStandardDeviation = Arrays.concatenateArrays(objMFCC.getMean(), objMFCC.getStandardDeviation());
							
							// Linear Predictive Coding (LPC)
							objLPC = new LPC();
							objLPC.processFrames(framesWithPreemphasis);
							
						    lpc = objLPC.getFeature();
						    lpcMeanStandardDeviation = Arrays.concatenateArrays(objLPC.getMean(), objLPC.getStandardDeviation());
							
							// Linear Prediction Cepstral fficients (LPCC)
							lpcc = objLPC.getFeatureLpcc();
							lpccMeanStandardDeviation = Arrays.concatenateArrays(objLPC.getMeanLpcc(), objLPC.getStandardDeviationLpcc());
							
							// Perceptual Linear Predictive (PLP)
							objPLP = new PLP(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
							objPLP.processFrames(framesWithoutPreemphasis);
							
							plp = objPLP.getFeature();
							plpMeanStandardDeviation = Arrays.concatenateArrays(objPLP.getMean(), objPLP.getStandardDeviation());
							
							// Checa se todas as features tem a mesma quantidade de frames
							if (intTotalFrames == mfcc.length && intTotalFrames == lpc.length && intTotalFrames == lpcc.length && intTotalFrames == plp.length) {
								// Grava os frames das features
								for (int indexFrame = 0; indexFrame < intTotalFrames; indexFrame++) {
									String strMfccVector = Features.getFeatureCoefficients(mfcc[indexFrame]);
									String strLpcVector = Features.getFeatureCoefficients(lpc[indexFrame]);
									String strLpccVector = Features.getFeatureCoefficients(lpcc[indexFrame]);
									String strPlpVector = Features.getFeatureCoefficients(plp[indexFrame]);
									
									objDatabaseConnection.initiliazeStatement();
									objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments_features (fk_audio_file_segment, ind_normalized, frame_number, mfcc_vector, lpc_vector, lpcc_vector, plp_vector) ");
									objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?) ");
									objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioFileSegment);
									objDatabaseConnection.addParameter("ind_normalized", LTDataTypes.BOOLEAN, Boolean.FALSE);
									objDatabaseConnection.addParameter("frame_number", LTDataTypes.INTEGER, indexFrame + 1);
									objDatabaseConnection.addParameter("mfcc_vector", LTDataTypes.STRING, strMfccVector);
									objDatabaseConnection.addParameter("lpc_vector", LTDataTypes.STRING, strLpcVector);
									objDatabaseConnection.addParameter("lpcc_vector", LTDataTypes.STRING, strLpccVector);
									objDatabaseConnection.addParameter("plp_vector", LTDataTypes.STRING, strPlpVector);
									objDatabaseConnection.executeQuery();
								}
								
								// Grava a média e o desvio padrão concatenados
								String strMfccMeanSD = Features.getFeatureCoefficients(mfccMeanStandardDeviation);
								String strLpcVectorMeanSD = Features.getFeatureCoefficients(lpcMeanStandardDeviation);
								String strLpccVectorMeanSD = Features.getFeatureCoefficients(lpccMeanStandardDeviation);
								String strPlpVectorMeanSD = Features.getFeatureCoefficients(plpMeanStandardDeviation);
								
								objDatabaseConnection.initiliazeStatement();
								objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments_features (fk_audio_file_segment, ind_normalized, frame_number, mfcc_vector, lpc_vector, lpcc_vector, plp_vector) ");
								objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?) ");
								objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioFileSegment);
								objDatabaseConnection.addParameter("ind_normalized", LTDataTypes.BOOLEAN, Boolean.TRUE);
								objDatabaseConnection.addParameter("frame_number", LTDataTypes.INTEGER, 0);
								objDatabaseConnection.addParameter("mfcc_vector", LTDataTypes.STRING, strMfccMeanSD);
								objDatabaseConnection.addParameter("lpc_vector", LTDataTypes.STRING, strLpcVectorMeanSD);
								objDatabaseConnection.addParameter("lpcc_vector", LTDataTypes.STRING, strLpccVectorMeanSD);
								objDatabaseConnection.addParameter("plp_vector", LTDataTypes.STRING, strPlpVectorMeanSD);
								objDatabaseConnection.executeQuery();
								
							} else {
								new Exception("Número de frames gerados na extração das features é diferente para as diversas representações");
							}
							
						// Atualiza um segmento de áudio já existente no banco de dados
						// Não é necessário realizar novamente a extração das features
						} else {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("UPDATE audio_files_segments ");
							objDatabaseConnection.sqlCommandAppend("SET fk_animal_taxonomy = ?, date_update = ? ");
							objDatabaseConnection.sqlCommandAppend("WHERE id_audio_segment = ? ");
							objDatabaseConnection.addParameter("fk_animal_taxonomy", LTDataTypes.LONG, lgnIdAnimalTaxonomy);
							objDatabaseConnection.addParameter("date_update", LTDataTypes.DATE, new Date());
							objDatabaseConnection.addParameter("id_audio_segment", LTDataTypes.LONG, lgnIdAudioFileSegment);
							objDatabaseConnection.executeQuery();
						}
						
						// Atualiza os dados dos segmentos de áudios temporários
						int intIndexAudioSegment = (int) objTableAudioSegments.getValue(indexAudioSegment, "index_temporary");
						
						String[] strScientificName = AnimalTaxonomy.getScientificName(lgnIdAnimalTaxonomy);
						
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).getAudioSegments().get(intIndexAudioSegment).setIdDatabase(lgnIdAudioFileSegment);
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).getAudioSegments().get(intIndexAudioSegment).setAnimalGenus(strScientificName[0]);
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).getAudioSegments().get(intIndexAudioSegment).setAnimalSpecies(strScientificName[1]);
					}
					
					objDatabaseConnection.commitTransaction();
					
					objWasisDialog.setVisible(false);
			
				} catch (Exception e) {
					throw new Exception(e);
				}
	            
				return null;
			}
			
			@Override
	        protected void done() {
	            try {
	                get();
	                
	                progressBar.setVisible(false);
	                
	                WasisMessageBox.showMessageDialog(rsBundle.getString("operation_completed"), WasisMessageBox.INFORMATION_MESSAGE);

	            } catch (Exception e) {
	            	e.printStackTrace();
	            	
	            	progressBar.setVisible(false);
	            	
	            	WasisMessageBox.showMessageDialog(rsBundle.getString("screen_save_audio_data_error_saving_data"), WasisMessageBox.ERROR_MESSAGE);
	            	
	            } finally {
	            	objDatabaseConnection.rollBackTransaction();
					objDatabaseConnection.closeConnection();
					
					objSpectrogramGraphicPanel.repaint();
					
					setComponentEnabled(true);
	            }
	        }
		};

		swingWorkerSave.execute();
	}
	
	/**
	 * Habilita/desabilita componentes da tela.
	 * 
	 * @param blnEnabled - <i>True</i> - Componentes habilitados
	 */
	private void setComponentEnabled(boolean blnEnabled) {
		WasisContainer.setComponentEnabled(panelAudioInformation, blnEnabled);
		WasisContainer.setComponentEnabled(panelScientificClassification, blnEnabled);
		WasisContainer.setComponentEnabled(panelRecordingInformation, blnEnabled);
		WasisContainer.setComponentEnabled(panelAudioSegments, blnEnabled);
		
		txtAudioFilePath.setEnabled(false);
		
		btnSaveData.setEnabled(blnEnabled);
	}

	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Fecha a tela.
	 */
	private void exitScreen() {
		if (AudioTemporary.checkAudioSegmentsNotSaved(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal())) {
			int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("screen_save_audio_data_exit_audio_segments_not_saved"), WasisMessageBox.YES_NO_OPTION);
			
			if (intDialogResult == WasisMessageBox.YES_OPTION) {
				objWasisDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
				objWasisDialog.setVisible(false);
			} else {
				objWasisDialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			}
			
		} else {
			objWasisDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			objWasisDialog.setVisible(false);
		}
	}
}