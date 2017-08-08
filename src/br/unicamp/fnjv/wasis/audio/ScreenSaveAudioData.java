package br.unicamp.fnjv.wasis.audio;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporary;
import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporarySegments;
import br.unicamp.fnjv.wasis.database.DatabaseConnection;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.libs.FileManager;
import br.unicamp.fnjv.wasis.main.WasisParameters;
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
 * Classe responsável pela exibição de uma tela que
 * possui todos os campos necessários para gravação
 * de uma nova vocalização no banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 08/Mai/2017
 */
public class ScreenSaveAudioData extends JDialog {
	private static final long serialVersionUID = 1232734137198386896L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private GraphicPanel objSpectrogramGraphicPanel;
	
	private List<Integer> lstSegmentsMarked;
	
	private long lgnIdAudioFile;
	private String strAudioFilePath;
	private String strAudioFileHash;
	
	private LTTextField txtAudioFilePath;
	private LTTextField txtVoucherNumber;
	private LTTextField txtAnimalPhylum;
	private LTTextField txtAnimalClass;
	private LTTextField txtAnimalOrder;
	private LTTextField txtAnimalFamily;
	private LTTextField txtAnimalGenus;
	private LTTextField txtAnimalSpecies;
	private LTTextField txtAnimalNamePortuguese;
	private LTTextField txtAnimalNameEnglish;
	
	private LTTextField txtLocationCountry;
	private LTTextField txtLocationState;
	private LTTextField txtLocationCity;

	private LTTextField txtDateDay;
	private LTTextField txtDateMonth;
	private LTTextField txtDateYear;
	private LTTextField txtTimeRecording;
	private LTTextField txtCallType;
	private LTTextField txtRecordist;
	private LTTextField txtObservation;
	
	private LTTable objTableSegments;
	
	private JButton btnSaveData;
	
	private JProgressBar progressBar;
	
	/**
	 * Classe responsável pela exibição de uma tela que
	 * possui todos os campos necessários para gravação
	 * de uma nova vocalização no banco de dados.
 	 * 
 	 * @param objSpectrogramGraphicPanel
	 */
	public ScreenSaveAudioData(GraphicPanel objSpectrogramGraphicPanel) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;
		
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
	 * Classe responsável pela exibição de uma tela que
	 * possui todos os campos necessários para gravação
	 * de uma nova vocalização no banco de dados.
 	 * 
 	 * @param objSpectrogramGraphicPanel
	 * @param lstSegmentsMarked          - Lista com a identificação dos segmentos (ROIs) que deverão ser inseridos no banco de dados
	 */
	protected ScreenSaveAudioData(GraphicPanel objSpectrogramGraphicPanel, List<Integer> lstSegmentsMarked) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;
		this.lstSegmentsMarked = lstSegmentsMarked;
		
		try {
			this.strAudioFilePath = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal();
			this.strAudioFileHash = FileManager.getFileHash(new File(strAudioFilePath));
		} catch (Error | Exception e) {
			e.printStackTrace();
		}

		loadScreen();
		loadAudioData();
		loadAudioSegmentsMarked();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// ***************************************************************************************************************
		// Informações do Arquivo de Áudio
		WasisPanel panelAudioInformation = new WasisPanel(rsBundle.getString("screen_save_audio_data_audio_file_information"));
		panelAudioInformation.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		txtAudioFilePath = new LTTextField(rsBundle.getString("screen_save_audio_file_path"), LTDataTypes.STRING, false, false, 500);
		txtAudioFilePath.setValue(strAudioFilePath);
		
		txtVoucherNumber = new LTTextField(rsBundle.getString("audio_file_voucher_number"), LTDataTypes.STRING, true, false, 200);
		
		// ***************************************************************************************************************
		// Dados da Classificação Científica
		WasisPanel panelScientificClassification = new WasisPanel(rsBundle.getString("screen_save_audio_data_scientific_classification"));
		panelScientificClassification.setLayout(new MigLayout("insets 0", "[grow]", "[][]"));
		
		txtAnimalPhylum = new LTTextField(rsBundle.getString("animal_phylum"), LTDataTypes.STRING, true, false, 200);
		txtAnimalClass = new LTTextField(rsBundle.getString("animal_class"), LTDataTypes.STRING, true, false, 200);
		txtAnimalOrder = new LTTextField(rsBundle.getString("animal_order"), LTDataTypes.STRING, true, false, 200);
		txtAnimalFamily = new LTTextField(rsBundle.getString("animal_family"), LTDataTypes.STRING, true, false, 200);
		txtAnimalGenus = new LTTextField(rsBundle.getString("animal_genus"), LTDataTypes.STRING, true, false, 200);
		txtAnimalSpecies = new LTTextField(rsBundle.getString("animal_species"), LTDataTypes.STRING, true, false, 200);
		txtAnimalNamePortuguese = new LTTextField(rsBundle.getString("animal_name_portuguese"), LTDataTypes.STRING, true, false, 200);
		txtAnimalNameEnglish = new LTTextField(rsBundle.getString("animal_name_english"), LTDataTypes.STRING, true, false, 200);
		
		// ***************************************************************************************************************
		// Dados da Gravação
		WasisPanel panelRecordingInformation = new WasisPanel(rsBundle.getString("screen_save_audio_data_recording_information"));
		panelRecordingInformation.setLayout(new MigLayout("insets 0", "[grow]", "[][][][75.00]"));
		
		txtLocationCountry = new LTTextField(rsBundle.getString("audio_file_location_country"), LTDataTypes.STRING, true, false, 200);
		txtLocationState = new LTTextField(rsBundle.getString("audio_file_location_state"), LTDataTypes.STRING, true, false, 200);
		txtLocationCity = new LTTextField(rsBundle.getString("audio_file_location_city"), LTDataTypes.STRING, true, false, 200);
		txtDateDay = new LTTextField(rsBundle.getString("audio_file_date_day"), LTDataTypes.INTEGER, true, false);
		txtDateMonth = new LTTextField(rsBundle.getString("audio_file_date_month"), LTDataTypes.INTEGER, true, false);
		txtDateYear = new LTTextField(rsBundle.getString("audio_file_date_year"), LTDataTypes.INTEGER, true, false);
		txtTimeRecording = new LTTextField(rsBundle.getString("audio_file_time_recording"), LTDataTypes.STRING, true, false, 10);
		txtCallType = new LTTextField(rsBundle.getString("audio_file_call_type"), LTDataTypes.STRING, true, false, 200);
		txtRecordist = new LTTextField(rsBundle.getString("audio_file_recordist"), LTDataTypes.STRING, true, false, 200);
		txtObservation = new LTTextField(rsBundle.getString("audio_file_observations"), LTDataTypes.TEXT, true, false);
		
		// ***************************************************************************************************************
		// Dados de seleção
		WasisPanel panelSegments = new WasisPanel();
		panelSegments.setLayout(new MigLayout("insets 4", "[grow]", "[grow]"));
		
		objTableSegments = new LTTable(true, false);
		objTableSegments.addColumn("index_temporary", "", LTDataTypes.INTEGER, 0, false);
		objTableSegments.addColumn("sound_unit", rsBundle.getString("audio_file_selection_sound_unit"), LTDataTypes.STRING, 140, false);
		objTableSegments.addColumn("time_initial", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.INTEGER, 0, false);
		objTableSegments.addColumn("time_final", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.INTEGER, 0, false);
		objTableSegments.addColumn("frequency_initial", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.INTEGER, 0, false);
		objTableSegments.addColumn("frequency_final", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.INTEGER, 0, false);
		objTableSegments.addColumn("time_initial_show", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.STRING, 145, false);
		objTableSegments.addColumn("time_final_show", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.STRING, 145, false);
		objTableSegments.addColumn("frequency_initial_show", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.STRING, 145, false);
		objTableSegments.addColumn("frequency_final_show", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.STRING, 145, false);
		objTableSegments.showTable();
		
		// ***************************************************************************************************************
		// Painel de Ferramentas
		JPanel panelTools = new JPanel();
		panelTools.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		btnSaveData = new JButton(rsBundle.getString("screen_save_audio_data_save_audio_data"));
		btnSaveData.setMinimumSize(new Dimension(100, 30));
		btnSaveData.setMaximumSize(new Dimension(400, 30));
		btnSaveData.setIconTextGap(15);
		btnSaveData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSaveData.setIcon(new ImageIcon("res/images/save.png"));
		btnSaveData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				if (WasisParameters.getInstance().getWasisUser().equals("wasis_fnjv")) {
					saveRecordingDataFnjv();
				} else {
					saveRecordingData();
				}
			}
		});
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		
		// ***************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_save_audio_data_screen_description"), false);
		objWasisDialog.setBounds(350, 350, 800, 600);
		objWasisDialog.setMinimumSize(new Dimension(800, 600));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[] 2 [] 2 [] 2 [100.00, grow] 4 []"));
		objWasisDialog.getContentPane().add(panelAudioInformation, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelScientificClassification, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(panelRecordingInformation, "cell 0 2, grow");
		objWasisDialog.getContentPane().add(panelSegments, "cell 0 3, grow");
		objWasisDialog.getContentPane().add(panelTools, "cell 0 4, grow");
		
		panelAudioInformation.add(txtAudioFilePath, "cell 0 0, grow, width 369");
		panelAudioInformation.add(txtVoucherNumber, "cell 0 0, grow");
		
		panelScientificClassification.add(txtAnimalPhylum, "cell 0 0, grow, width 100");
		panelScientificClassification.add(txtAnimalClass, "cell 0 0, grow, width 100");
		panelScientificClassification.add(txtAnimalOrder, "cell 0 0, grow, width 100");
		panelScientificClassification.add(txtAnimalFamily, "cell 0 1, grow, width 100");
		panelScientificClassification.add(txtAnimalGenus, "cell 0 1, grow, width 100");
		panelScientificClassification.add(txtAnimalSpecies, "cell 0 1, grow, width 100");
		panelScientificClassification.add(txtAnimalNamePortuguese, "cell 0 2, grow, width 100");
		panelScientificClassification.add(txtAnimalNameEnglish, "cell 0 2, grow, width 100");
		
		panelRecordingInformation.add(txtLocationCountry, "cell 0 0, grow, width 100");
		panelRecordingInformation.add(txtLocationState, "cell 0 0, grow, width 100");
		panelRecordingInformation.add(txtLocationCity, "cell 0 0, grow, width 100");
		panelRecordingInformation.add(txtDateDay, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtDateMonth, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtDateYear, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtTimeRecording, "cell 0 1, grow, width 100");
		panelRecordingInformation.add(txtCallType, "cell 0 2, grow, width 100");
		panelRecordingInformation.add(txtRecordist, "cell 0 2, grow, width 100");
		panelRecordingInformation.add(txtObservation, "cell 0 3, grow");
		
		panelSegments.add(objTableSegments, "cell 0 0, grow");
		
		panelTools.add(btnSaveData, "cell 0 0");
		panelTools.add(progressBar, "cell 0 0, grow");
		
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
			objDatabaseConnection.sqlCommand("SELECT * ");
			objDatabaseConnection.sqlCommandAppend("FROM audio_files ");
			objDatabaseConnection.sqlCommandAppend("WHERE audio_file_path = ? ");
			//objDatabaseConnection.sqlCommandAppend("AND audio_file_hash = ? ");
			objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePath);
			//objDatabaseConnection.addParameter("audio_file_hash", LTDataTypes.STRING, strAudioFileHash);
			
			ResultSet rsAudioData = objDatabaseConnection.executeSelectQuery();
			
			while (rsAudioData.next()) {
				lgnIdAudioFile = rsAudioData.getLong("id");
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
				txtCallType.setValue(rsAudioData.getString("call_type"));
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
	 * Carrega os dados das seleções existentes no banco de dados.
	 */
	private void loadAudioSegments() {
		List<AudioTemporarySegments> lstAudioSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments();
		
		for (int indexSegment = 0; indexSegment < lstAudioSegments.size(); indexSegment++) {
			if (lstAudioSegments.get(indexSegment).getIdDatabase() == 0) {
				objTableSegments.addRow();
				objTableSegments.addRowData("index_temporary", indexSegment);
				objTableSegments.addRowData("sound_unit", lstAudioSegments.get(indexSegment).getSoundUnit());
				objTableSegments.addRowData("time_initial", lstAudioSegments.get(indexSegment).getInitialTime());
				objTableSegments.addRowData("time_final", lstAudioSegments.get(indexSegment).getFinalTime());
				objTableSegments.addRowData("frequency_initial", lstAudioSegments.get(indexSegment).getInitialFrequency());
				objTableSegments.addRowData("frequency_final", lstAudioSegments.get(indexSegment).getFinalFrequency());
				objTableSegments.addRowData("time_initial_show", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexSegment).getInitialTime()));
				objTableSegments.addRowData("time_final_show", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexSegment).getFinalTime()));
				objTableSegments.addRowData("frequency_initial_show", lstAudioSegments.get(indexSegment).getInitialFrequency() + " Hz");
				objTableSegments.addRowData("frequency_final_show", lstAudioSegments.get(indexSegment).getFinalFrequency() + " Hz");
				
				System.out.println(lstAudioSegments.get(indexSegment).getInitialTime() + " | " + lstAudioSegments.get(indexSegment).getFinalTime());
			}
		}
	}
	
	/**
	 * Carrega os dados das seleções a serem gravados.
	 */
	private void loadAudioSegmentsMarked() {
		List<AudioTemporarySegments> lstSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments();
		
		for (int indexSegmentMarked = 0; indexSegmentMarked < lstSegmentsMarked.size(); indexSegmentMarked++) {
			int intIndexSegmentMarked = lstSegmentsMarked.get(indexSegmentMarked);
			
			objTableSegments.addRow();
			objTableSegments.addRowData("index_temporary", intIndexSegmentMarked);
			objTableSegments.addRowData("sound_unit", lstSegments.get(intIndexSegmentMarked).getSoundUnit());
			objTableSegments.addRowData("time_initial", lstSegments.get(intIndexSegmentMarked).getInitialTime());
			objTableSegments.addRowData("time_final", lstSegments.get(intIndexSegmentMarked).getFinalTime());
			objTableSegments.addRowData("frequency_initial", lstSegments.get(intIndexSegmentMarked).getInitialFrequency());
			objTableSegments.addRowData("frequency_final", lstSegments.get(intIndexSegmentMarked).getFinalFrequency());
			objTableSegments.addRowData("time_initial_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(intIndexSegmentMarked).getInitialTime()));
			objTableSegments.addRowData("time_final_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(intIndexSegmentMarked).getFinalTime()));
			objTableSegments.addRowData("frequency_initial_show", lstSegments.get(intIndexSegmentMarked).getInitialFrequency() + " Hz");
			objTableSegments.addRowData("frequency_final_show", lstSegments.get(intIndexSegmentMarked).getFinalFrequency() + " Hz");
		}
	}
	
	/**
	 * Salva os dados das gravações.
	 */
	private void saveRecordingData() {
		final DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		SwingWorker<Boolean, Boolean> swingWorkerUpdate = new SwingWorker<Boolean, Boolean>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					btnSaveData.setEnabled(false);
					
					progressBar.setVisible(true);
					
					objDatabaseConnection.openConnection();
					objDatabaseConnection.initiliazeStatement();
					objDatabaseConnection.sqlCommand("INSERT INTO audio_files (audio_file_path, audio_file_hash, voucher_number, " +
																   	         " animal_phylum, animal_class, animal_order, animal_family, " +
							                                                 " animal_genus, animal_species, animal_name_portuguese, animal_name_english, " +
																   	         " location_city, location_state, location_country, " +
							                                       	         " date_day, date_month, date_year, time_recording, " + 
																   	         " call_type, recordist, observations) ");
					objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");
					objDatabaseConnection.addParameter("audio_file_path", LTDataTypes.STRING, strAudioFilePath);
					objDatabaseConnection.addParameter("audio_file_hash", LTDataTypes.STRING, strAudioFileHash);
					objDatabaseConnection.addParameter("voucher_number", LTDataTypes.STRING, txtVoucherNumber.getValue());
					objDatabaseConnection.addParameter("animal_phylum", LTDataTypes.STRING, txtAnimalPhylum.getValue());
					objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, txtAnimalClass.getValue());
					objDatabaseConnection.addParameter("animal_order", LTDataTypes.STRING, txtAnimalOrder.getValue());
					objDatabaseConnection.addParameter("animal_family", LTDataTypes.STRING, txtAnimalFamily.getValue());
					objDatabaseConnection.addParameter("animal_genus", LTDataTypes.STRING, txtAnimalGenus.getValue());
					objDatabaseConnection.addParameter("animal_species", LTDataTypes.STRING, txtAnimalSpecies.getValue());
					objDatabaseConnection.addParameter("animal_name_portuguese", LTDataTypes.STRING, txtAnimalNamePortuguese.getValue());
					objDatabaseConnection.addParameter("animal_name_english", LTDataTypes.STRING, txtAnimalNameEnglish.getValue());
					objDatabaseConnection.addParameter("location_city", LTDataTypes.STRING, txtLocationCity.getValue());
					objDatabaseConnection.addParameter("location_state", LTDataTypes.STRING, txtLocationState.getValue());
					objDatabaseConnection.addParameter("location_country", LTDataTypes.STRING, txtLocationCountry.getValue());
					objDatabaseConnection.addParameter("date_day", LTDataTypes.INTEGER, txtDateDay.getValue());
					objDatabaseConnection.addParameter("date_month", LTDataTypes.INTEGER, txtDateMonth.getValue());
					objDatabaseConnection.addParameter("date_year", LTDataTypes.INTEGER, txtDateYear.getValue());
					objDatabaseConnection.addParameter("time_recording", LTDataTypes.STRING, txtTimeRecording.getValue());
					objDatabaseConnection.addParameter("call_type", LTDataTypes.STRING, txtCallType.getValue());
					objDatabaseConnection.addParameter("recordist", LTDataTypes.STRING, txtRecordist.getValue());
					objDatabaseConnection.addParameter("observations", LTDataTypes.STRING, txtObservation.getValue());
					objDatabaseConnection.executeQuery();
					
					long lgnIdAudioFile = (long) objDatabaseConnection.getIdentityKey();
					
					//List<AudioComparisonValues> lstSpectrogramComparisonValues;
					
					for (int indexSegment = 0; indexSegment < objTableSegments.getRowCount(); indexSegment++) {
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments (fk_audio_file, sound_unit, " +
								                                                  	      " time_initial, time_final, " +
																					      " frequency_initial, frequency_final, date_update) ");
						objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?, ?) ");
						objDatabaseConnection.addParameter("fk_audio_file", LTDataTypes.LONG, lgnIdAudioFile);
						objDatabaseConnection.addParameter("sound_unit", LTDataTypes.STRING, objTableSegments.getValue(indexSegment, "sound_unit"));
						objDatabaseConnection.addParameter("time_initial", LTDataTypes.INTEGER, objTableSegments.getValue(indexSegment, "time_initial"));
						objDatabaseConnection.addParameter("time_final", LTDataTypes.INTEGER, objTableSegments.getValue(indexSegment, "time_final"));
						objDatabaseConnection.addParameter("frequency_initial", LTDataTypes.INTEGER, objTableSegments.getValue(indexSegment, "frequency_initial"));
						objDatabaseConnection.addParameter("frequency_final", LTDataTypes.INTEGER, objTableSegments.getValue(indexSegment, "frequency_final"));
						objDatabaseConnection.addParameter("date_update", LTDataTypes.DATE, new Date());
						objDatabaseConnection.executeQuery();
						
						long lgnIdAudioFileSegment = (long) objDatabaseConnection.getIdentityKey();
						
						// Extração do WASIS
						/*
						lstSpectrogramComparisonValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData((int) objTableSegments.getValue(indexSegment, "time_initial"), 
																			                                               (int) objTableSegments.getValue(indexSegment, "time_final"), 
																			                                               (int) objTableSegments.getValue(indexSegment, "frequency_initial"), 
																			                                               (int) objTableSegments.getValue(indexSegment, "frequency_final"));
						
						for (int indexComparisonValues = 0; indexComparisonValues < lstSpectrogramComparisonValues.size(); indexComparisonValues++) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments_ps (fk_audio_file_segment, frequency_value, decibel_value) ");
							objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
							objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioFileSegment);
							objDatabaseConnection.addParameter("frequency_value", LTDataTypes.INTEGER, lstSpectrogramComparisonValues.get(indexComparisonValues).getIndex());
							objDatabaseConnection.addParameter("decibel_value", LTDataTypes.DOUBLE, lstSpectrogramComparisonValues.get(indexComparisonValues).getValue());
							objDatabaseConnection.executeQuery();
						}
						*/
						/*
						// Extração MFCC para rodar HMM
						double[][] mfccVector = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonDataMFCC((int) objTableSegments.getValue(indexSelection, "time_initial"), 
                                                                                                                      (int) objTableSegments.getValue(indexSelection, "time_final"), 
                                                                                                                      (int) objTableSegments.getValue(indexSelection, "frequency_initial"), 
                                                                                                                      (int) objTableSegments.getValue(indexSelection, "frequency_final"));
						
						StringBuffer strMFCCVector;
						String strVector;
						
						for (int indexVector = 0; indexVector < mfccVector.length; indexVector++) {
							strMFCCVector = new StringBuffer();
							
							for (int indexValue = 0; indexValue < mfccVector[0].length; indexValue++) {
								strMFCCVector.append(mfccVector[indexVector][indexValue] + " ");
							}
							
							strVector = strMFCCVector.toString();
							strVector = strVector.trim();
							
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("INSERT INTO audio_files_selections_mfcc (fk_audio_file_selection, mfcc_order, mfcc_vector) ");
							objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
							objDatabaseConnection.addParameter("fk_audio_file_selection", LTDataTypes.LONG, lgnIdAudioFileSelection);
							objDatabaseConnection.addParameter("mfcc_order", LTDataTypes.INTEGER, indexVector);
							objDatabaseConnection.addParameter("mfcc_vector", LTDataTypes.STRING, strVector);
							objDatabaseConnection.executeQuery();
						}
						*/
						
						// Atualiza os dados das seleções de áudios temporárias
						int intIndexSegmentMarked = (int) objTableSegments.getValue(indexSegment, "index_temporary");
						
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments().get(intIndexSegmentMarked).setIdDatabase(lgnIdAudioFileSegment);
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments().get(intIndexSegmentMarked).setAnimalGenus(txtAnimalGenus.getValue().toString());
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments().get(intIndexSegmentMarked).setAnimalSpecies(txtAnimalSpecies.getValue().toString());
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
					
					btnSaveData.setEnabled(true);
	            }
	        }
		};

		swingWorkerUpdate.execute();
	}
	
	/**
	 * Salva os dados das gravações pelos registros da FNJV.
	 */
	private void saveRecordingDataFnjv() {
		final DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		SwingWorker<Boolean, Integer> swingWorkerUpdate = new SwingWorker<Boolean, Integer>() {
			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					btnSaveData.setEnabled(false);
					
					progressBar.setVisible(true);

					objDatabaseConnection.openConnection();
					
					//List<AudioComparisonValues> lstSpectrogramComparisonValues;
					
					for (int indexSelection = 0; indexSelection < objTableSegments.getRowCount(); indexSelection++) {
						objDatabaseConnection.initiliazeStatement();
						objDatabaseConnection.sqlCommand("INSERT INTO audio_files_segments (fk_audio_file, sound_unit, " +
								                                                  	      " time_initial, time_final, " +
																					      " frequency_initial, frequency_final) ");
						objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?, ?, ?) ");
						objDatabaseConnection.addParameter("fk_audio_file", LTDataTypes.LONG, lgnIdAudioFile);
						objDatabaseConnection.addParameter("sound_unit", LTDataTypes.STRING, objTableSegments.getValue(indexSelection, "sound_unit"));
						objDatabaseConnection.addParameter("time_initial", LTDataTypes.INTEGER, objTableSegments.getValue(indexSelection, "time_initial"));
						objDatabaseConnection.addParameter("time_final", LTDataTypes.INTEGER, objTableSegments.getValue(indexSelection, "time_final"));
						objDatabaseConnection.addParameter("frequency_initial", LTDataTypes.INTEGER, objTableSegments.getValue(indexSelection, "frequency_initial"));
						objDatabaseConnection.addParameter("frequency_final", LTDataTypes.INTEGER, objTableSegments.getValue(indexSelection, "frequency_final"));
						objDatabaseConnection.executeQuery();
						
						long lgnIdAudioFileSegment = objDatabaseConnection.getIdentityKey();
						/*
						lstSpectrogramComparisonValues = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonData((int) objTableSegments.getValue(indexSelection, "time_initial"), 
																			   				                               (int) objTableSegments.getValue(indexSelection, "time_final"), 
																			   				                               (int) objTableSegments.getValue(indexSelection, "frequency_initial"), 
																			   				                               (int) objTableSegments.getValue(indexSelection, "frequency_final"));
						
						for (int indexComparisonValues = 0; indexComparisonValues < lstSpectrogramComparisonValues.size(); indexComparisonValues++) {
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("INSERT INTO audio_files_selections_values (fk_audio_file_selection, frequency_value, decibel_value) ");
							objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
							objDatabaseConnection.addParameter("fk_audio_file_selection", LTDataTypes.LONG, lgnIdAudioFileSelection);
							objDatabaseConnection.addParameter("frequency_value", LTDataTypes.INTEGER, lstSpectrogramComparisonValues.get(indexComparisonValues).getIndex());
							objDatabaseConnection.addParameter("decibel_value", LTDataTypes.DOUBLE, lstSpectrogramComparisonValues.get(indexComparisonValues).getValue());
							objDatabaseConnection.executeQuery();
						}
						*/
						
						// Extração MFCC para rodar HMM
						/*
						double[][] mfccVector = objSpectrogramGraphicPanel.getSpectrogram().extractComparisonDataMFCC((int) objTableSegments.getValue(indexSelection, "time_initial"), 
                                                                                                                      (int) objTableSegments.getValue(indexSelection, "time_final"), 
                                                                                                                      (int) objTableSegments.getValue(indexSelection, "frequency_initial"), 
                                                                                                                      (int) objTableSegments.getValue(indexSelection, "frequency_final"));
						
						StringBuffer strMFCCVector;
						String strVector;
						
						for (int indexVector = 0; indexVector < mfccVector.length; indexVector++) {
							strMFCCVector = new StringBuffer();
							
							for (int indexValue = 0; indexValue < mfccVector[0].length; indexValue++) {
								strMFCCVector.append(mfccVector[indexVector][indexValue] + " ");
							}
							
							strVector = strMFCCVector.toString();
							strVector = strVector.trim();
							
							objDatabaseConnection.initiliazeStatement();
							objDatabaseConnection.sqlCommand("INSERT INTO audio_files_selections_mfcc (fk_audio_file_selection, mfcc_order, mfcc_vector) ");
							objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
							objDatabaseConnection.addParameter("fk_audio_file_selection", LTDataTypes.LONG, lgnIdAudioFileSelection);
							objDatabaseConnection.addParameter("mfcc_order", LTDataTypes.INTEGER, indexVector);
							objDatabaseConnection.addParameter("mfcc_vector", LTDataTypes.STRING, strVector);
							objDatabaseConnection.executeQuery();
						}
						*/
						
						// Atualiza os dados dos segmentos (ROIs) de áudios temporários
						int intIndexSegmentMarked = (int) objTableSegments.getValue(indexSelection, "index_temporary");
						
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments().get(intIndexSegmentMarked).setIdDatabase(lgnIdAudioFileSegment);
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments().get(intIndexSegmentMarked).setAnimalGenus(txtAnimalGenus.getValue().toString());
						AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments().get(intIndexSegmentMarked).setAnimalSpecies(txtAnimalSpecies.getValue().toString());
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
					
					btnSaveData.setEnabled(true);
	            }
	        }
		};

		swingWorkerUpdate.execute();
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
		if (AudioTemporary.checkSelectionsNotSaved(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal())) {
			int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("screen_save_audio_data_exit_selections_not_save"), WasisMessageBox.YES_NO_OPTION);
			
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