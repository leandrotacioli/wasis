package br.unicamp.fnjv.wasis.audio.classification;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.comboboxfield.LTComboBoxField;
import com.leandrotacioli.libs.swing.table.LTTable;

import br.unicamp.fnjv.wasis.audio.AudioSegmentsValues;
import br.unicamp.fnjv.wasis.audio.AudioTemporary;
import br.unicamp.fnjv.wasis.classifiers.pcc.PearsonCorrelation;
import br.unicamp.fnjv.wasis.classifiers.pcc.PearsonCorrelationValues;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.features.Features;
import br.unicamp.fnjv.wasis.features.LPC;
import br.unicamp.fnjv.wasis.features.MFCC;
import br.unicamp.fnjv.wasis.features.PLP;
import br.unicamp.fnjv.wasis.features.PowerSpectrum;
import br.unicamp.fnjv.wasis.features.Preprocessing;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisDialogLoadingData;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que exibe a lista de áudio segmentos
 * selecionados no espectrograma e uma outra lista que mostra os resultados da classificação.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 26/Out/2017
 */
public class ScreenAudioClassificationBruteForce extends JDialog {
	private static final long serialVersionUID = -5765475068476175620L;
	
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private ScreenAudioClassificationFilters objFilters;
	
	private WasisDialog objWasisDialog;
	
	private AudioWav objAudioWav;
	
	private long lgnIdLibrary;
	
	private LTComboBoxField cmbDataSource;
	private LTComboBoxField cmbFeature;
	private LTComboBoxField cmbClassifier;
	
	private WasisPanel panelAudioSegments;
	private LTTable objTableAudioSegments;
	
	private JButton btnFilters;
	private JButton btnRunClassification;
	private JButton btnShowResults;
	
	private LTComboBoxField cmbBestResults;
	
	private WasisPanel panelResults;
	private LTTable objTableResults;
	
	private List<PearsonCorrelationResults> lstPccResults;
	
	private boolean blnRunningClassification;

	/**
	 * Classe responsável pela exibição de uma tela que exibe a lista de áudio segmentos
	 * selecionados no espectrograma e uma outra lista que mostra os resultados da classificação.
	 * 
	 * @param objAudioWav
	 * 
	 * @throws CloneNotSupportedException
	 */
	public ScreenAudioClassificationBruteForce(AudioWav objAudioWav) throws CloneNotSupportedException {
		this(objAudioWav, 0);
	}
	
	/**
	 * Classe responsável pela exibição de uma tela que exibe a lista de áudio segmentos
	 * selecionados no espectrograma e uma outra lista que mostra os resultados da classificação.
 	 * 
 	 * @param objAudioWav
	 * @param lgnIdLibrary
	 * 
	 * @throws CloneNotSupportedException
	 */
	public ScreenAudioClassificationBruteForce(AudioWav objAudioWav, long lgnIdLibrary) throws CloneNotSupportedException {
		//this.objAudioWav = (AudioWav) objAudioWav.clone();
		this.objAudioWav = objAudioWav;
		this.lgnIdLibrary = lgnIdLibrary;
		
		objFilters = new ScreenAudioClassificationFilters();
		
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			// Filtros
			cmbDataSource = new LTComboBoxField(rsBundle.getString("data_source"), true, true);
			cmbDataSource.addValues(rsBundle.getString("screen_audio_classification_brute_force_filters_database"), rsBundle.getString("screen_audio_classification_brute_force_filters_database"));
			//cmbDataSource.addValues(rsBundle.getString("screen_audio_classification_brute_force_filters_library"), rsBundle.getString("screen_audio_classification_brute_force_filters_library"));
			//cmbDataSource.addValues(rsBundle.getString("screen_audio_classification_brute_force_filters_audio_file"), rsBundle.getString("screen_audio_classification_brute_force_filters_audio_file"));
			cmbDataSource.setValue(rsBundle.getString("screen_audio_classification_brute_force_filters_database"));
			
			cmbFeature = new LTComboBoxField(rsBundle.getString("feature") + ":", true, true);
			cmbFeature.addValues(Features.POWER_SPECTRUM, rsBundle.getString("feature_power_spectrum"));
			cmbFeature.addValues(Features.MFCC, rsBundle.getString("feature_mfcc"));
			cmbFeature.addValues(Features.LPC, rsBundle.getString("feature_lpc"));
			cmbFeature.addValues(Features.LPCC, rsBundle.getString("feature_lpcc"));
			cmbFeature.addValues(Features.PLP, rsBundle.getString("feature_plp"));
			cmbFeature.addValues(Features.MFCC_LPC, rsBundle.getString("feature_mfcc_lpc"));
			cmbFeature.addValues(Features.MFCC_LPCC, rsBundle.getString("feature_mfcc_lpcc"));
			cmbFeature.addValues(Features.MFCC_PLP, rsBundle.getString("feature_mfcc_plp"));
			cmbFeature.addValues(Features.MFCC_LPC_LPCC_PLP, rsBundle.getString("feature_mfcc_lpc_lpcc_plp"));
			cmbFeature.setValue(Features.POWER_SPECTRUM);
			
			cmbClassifier = new LTComboBoxField(rsBundle.getString("classifier") + ":", true, true);
			cmbClassifier.addValues("Pearson_Correlation", rsBundle.getString("classifier_pearson_correlation"));
			cmbClassifier.setValue("Pearson_Correlation");
			
			// Botão Filtro
			btnFilters = new JButton();
			btnFilters.setMinimumSize(new Dimension(38, 38));
			btnFilters.setMaximumSize(new Dimension(38, 38));
			btnFilters.setIcon(new ImageIcon("res/images/filter.png"));
			btnFilters.setToolTipText("Filtros");
			btnFilters.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					objFilters.showScreen();
				}
			});
			
			// *********************************************************************************************
			// Segmentos do Áudio (ROIs)
			panelAudioSegments = new WasisPanel(rsBundle.getString("screen_audio_classification_brute_force_audio_segments"));
			panelAudioSegments.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableAudioSegments = new LTTable(true);
			objTableAudioSegments.addColumn("audio_segment", rsBundle.getString("audio_segment"), LTDataTypes.STRING, 165, false);
			objTableAudioSegments.addColumn("time_initial", rsBundle.getString("audio_segment_time_initial"), LTDataTypes.INTEGER, 0, false);
			objTableAudioSegments.addColumn("time_final", rsBundle.getString("audio_segment_time_final"), LTDataTypes.INTEGER, 0, false);
			objTableAudioSegments.addColumn("frequency_initial", rsBundle.getString("audio_segment_frequency_minimum"), LTDataTypes.INTEGER, 0, false);
			objTableAudioSegments.addColumn("frequency_final", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.INTEGER, 0, false);
			objTableAudioSegments.addColumn("time_initial_show", rsBundle.getString("audio_segment_time_initial"), LTDataTypes.STRING, 150, false);
			objTableAudioSegments.addColumn("time_final_show", rsBundle.getString("audio_segment_time_final"), LTDataTypes.STRING, 150, false);
			objTableAudioSegments.addColumn("frequency_initial_show", rsBundle.getString("audio_segment_frequency_minimum"), LTDataTypes.STRING, 150, false);
			objTableAudioSegments.addColumn("frequency_final_show", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.STRING, 150, false);
			objTableAudioSegments.addMouseListener(new AudioSegmentMouseAdapter());
			objTableAudioSegments.showTable();
			
			loadAudioSegments();
			
			// Botão Comparar Áudio
			btnRunClassification = new JButton(rsBundle.getString("screen_audio_classification_brute_force_run_comparison"));
			btnRunClassification.setMinimumSize(new Dimension(100, 40));
			btnRunClassification.setMaximumSize(new Dimension(300, 40));
			btnRunClassification.setIconTextGap(15);
			btnRunClassification.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnRunClassification.setIcon(new ImageIcon("res/images/compare_sounds.png"));
			btnRunClassification.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					classifyAudio();
				}
			});
			
			// Total de resultados a serem mostrados
			cmbBestResults = new LTComboBoxField(rsBundle.getString("screen_audio_classification_brute_force_show_best_results"), true, true);
			cmbBestResults.addValues("1", "");
			cmbBestResults.addValues("5", "5");
			cmbBestResults.addValues("10", "10");
			cmbBestResults.addValues("25", "25");
			cmbBestResults.addValues("50", "50");
			cmbBestResults.setValue("10");
			
			// *********************************************************************************************
			// Resultados da comparação
			panelResults = new WasisPanel(rsBundle.getString("screen_audio_classification_brute_force_results"));
			panelResults.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableResults = new LTTable(true);
			objTableResults.addColumn("id_audio_segment", "id_audio_segment", LTDataTypes.LONG, 0, false);
			objTableResults.addColumn("correlation_result", rsBundle.getString("screen_audio_classification_brute_force_results_correlation"), LTDataTypes.DOUBLE, 125, false);
			objTableResults.addColumn("animal_common_name", rsBundle.getString("animal_common_name"), LTDataTypes.STRING, 240, false);
			objTableResults.addColumn("animal_binomial_name", rsBundle.getString("animal_species"), LTDataTypes.STRING, 400, false);
			objTableResults.addMouseListener(new ResultMouseAdapter());
			objTableResults.showTable();
			
			objTableResults.setColumnDoubleFractionDigits("correlation_result", 4);
			
			// Botão Mostrar Resultados
			btnShowResults = new JButton(rsBundle.getString("screen_audio_classification_brute_force_show_results"));
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
			objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_classification_brute_force_screen_description"), true);
			objWasisDialog.setBounds(350, 350, 850, 550);
			objWasisDialog.setMinimumSize(new Dimension(850, 550));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][400.00][][][]"));
			objWasisDialog.getContentPane().add(cmbDataSource, "cell 0 0, grow, width 475");
			objWasisDialog.getContentPane().add(cmbFeature, "cell 0 0, grow, width 500");
			objWasisDialog.getContentPane().add(cmbClassifier, "cell 0 0, grow, width 475");
			objWasisDialog.getContentPane().add(btnFilters, "cell 0 0, grow, gap 5 1 2 0");
			objWasisDialog.getContentPane().add(panelAudioSegments, "cell 0 1, grow");
			objWasisDialog.getContentPane().add(btnRunClassification, "cell 0 2, grow");
			objWasisDialog.getContentPane().add(cmbBestResults, "cell 0 2, width 200");
			objWasisDialog.getContentPane().add(panelResults, "cell 0 3, grow");
			//objWasisDialog.getContentPane().add(btnShowResults, "cell 0 4, grow");
			
			panelAudioSegments.add(objTableAudioSegments, "cell 0 0, grow");
			
			panelResults.add(objTableResults, "cell 0 0, grow");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Carrega os dados dos segmentos (ROIs) do áudio.
	 */
	private void loadAudioSegments() {
		List<AudioSegmentsValues> lstSegments = AudioTemporary.getAudioTemporary().get(objAudioWav.getAudioTemporaryIndex()).getAudioSegments();
		
		for (int indexSegment = 0; indexSegment < lstSegments.size(); indexSegment++) {
			objTableAudioSegments.addRow();
			objTableAudioSegments.addRowData("audio_segment", lstSegments.get(indexSegment).getAudioSegment());
			objTableAudioSegments.addRowData("time_initial", lstSegments.get(indexSegment).getInitialTime());
			objTableAudioSegments.addRowData("time_final", lstSegments.get(indexSegment).getFinalTime());
			objTableAudioSegments.addRowData("frequency_initial", lstSegments.get(indexSegment).getInitialFrequency());
			objTableAudioSegments.addRowData("frequency_final", lstSegments.get(indexSegment).getFinalFrequency());
			objTableAudioSegments.addRowData("time_initial_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getInitialTime()));
			objTableAudioSegments.addRowData("time_final_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getFinalTime()));
			objTableAudioSegments.addRowData("frequency_initial_show", lstSegments.get(indexSegment).getInitialFrequency() + " Hz");
			objTableAudioSegments.addRowData("frequency_final_show", lstSegments.get(indexSegment).getFinalFrequency() + " Hz");
		}
	}
	
	/**
	 * Realiza em background a classificação dos segmentos de áudio.
	 */
	private void classifyAudio() {
		if (objTableAudioSegments.getRowCount() > 0 && objTableAudioSegments.getSelectedRow() >= 0) {
			final WasisDialogLoadingData objWasisDialogRunning = new WasisDialogLoadingData(rsBundle.getString("screen_audio_classification_brute_force_running_audio_classification"));
			
			SwingWorker<Boolean, Boolean> swingWorkerClassify = new SwingWorker<Boolean, Boolean>() {
				@Override
				protected Boolean doInBackground() throws Exception {
					try {
						// Mostra uma caixa de diálogo para o usuário perceber que a classificação está sendo feita
						SwingUtilities.invokeLater(new Runnable() {
							public void run() {
								objWasisDialogRunning.showScreen();
							}
						});
						
						blnRunningClassification = true;
						
						objTableResults.deleteRows();
						
						List<PearsonCorrelationValues> lstPccValues = extractFeaturesFromAudioSegment(objTableAudioSegments.getSelectedRow());
						
						lstPccResults = new ArrayList<PearsonCorrelationResults>();
						
						// 'Banco de dados' e 'Mesma biblioteca'
						if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_classification_brute_force_filters_database")) || 
								cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_classification_brute_force_filters_library"))) {
							classifyAudioFromDatabase(lstPccValues);
						
						// Mesmo arquivo de áudio
						} else if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_classification_brute_force_filters_audio_file"))) {
							//compareAudiosFromAudioFile(objTableAudioSegments.getSelectedRow());
						}
						
						// Aguarda finalizar a comparação
						while (blnRunningClassification) {
							
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return null;
				}
				
				@Override
		        protected void done() {
		            try {
		                get();

		            } catch (Exception e) {
		            	e.printStackTrace();
		            	
		            } finally {
		            	objWasisDialogRunning.disableScreen();
		            }
		        }
			};
			
			swingWorkerClassify.execute();
		}
	}
	
	/**
	 * Extrai as features do segmento de áudio.
	 * 
	 * @param intIndexRow  - Índice do segmento de áudio
	 * 
	 * @return lstPcc - Lista com as features do segmento de áudio a ser classificado usando Pearson Correlation.
	 */
	private List<PearsonCorrelationValues> extractFeaturesFromAudioSegment(int intIndexRow) {
		List<PearsonCorrelationValues> lstPcc = new ArrayList<PearsonCorrelationValues>();
		
		int intInitialTime = (int) objTableAudioSegments.getValue(intIndexRow, "time_initial");
		int intFinalTime = (int) objTableAudioSegments.getValue(intIndexRow, "time_final");
		int intInitialFrequency = (int) objTableAudioSegments.getValue(intIndexRow, "frequency_initial");
		int intFinalFrequency = (int) objTableAudioSegments.getValue(intIndexRow, "frequency_final");
		
		int intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
		int intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
		
		double[] arrayAmplitudes = objAudioWav.getAmplitudesChunk(intInitialChunkToProcess, intFinalChunkToProcess);
		
		double[] preEmphasis = Preprocessing.preEmphasis(arrayAmplitudes);
		
		double[][] framesWithPreemphasis = Preprocessing.framing(preEmphasis);          // Utilizado na MFCC, LPC, LPCC
		double[][] framesWithoutPreemphasis = Preprocessing.framing(arrayAmplitudes);   // Utilizado na PS, PLP
		
		// Power Spectrum (PS)
		if (cmbFeature.getValue().equals(Features.POWER_SPECTRUM)) {
			PowerSpectrum objPowerSpectrum = new PowerSpectrum(objAudioWav.getWavHeader().getSampleRate(), intInitialFrequency, intFinalFrequency);
			objPowerSpectrum.processFrames(framesWithoutPreemphasis);
			
			lstPcc = getPccFromFeatureCoefficients(objPowerSpectrum.getFeature());
			
		// MFCC
		} else if (cmbFeature.getValue().equals(Features.MFCC)) {
			MFCC objMFCC = new MFCC(objAudioWav.getWavHeader().getSampleRate());
			objMFCC.processFrames(framesWithPreemphasis);
			
			lstPcc = getPccFromFeatureMeanSd(objMFCC.getMean(), objMFCC.getStandardDeviation());
			
		// LPC e LPCC
		} else if (cmbFeature.getValue().equals(Features.LPC) || cmbFeature.getValue().equals(Features.LPCC)) {
			LPC objLPC = new LPC();
			objLPC.processFrames(framesWithPreemphasis);
			
			if (cmbFeature.getValue().equals(Features.LPC)) {
				lstPcc = getPccFromFeatureMeanSd(objLPC.getMean(), objLPC.getStandardDeviation());
			} else if (cmbFeature.getValue().equals(Features.LPCC)) {
				lstPcc = getPccFromFeatureMeanSd(objLPC.getMeanLpcc(), objLPC.getStandardDeviationLpcc());
			}
			
		// PLP
		} else if (cmbFeature.getValue().equals(Features.PLP)) {
			PLP objPLP = new PLP(objAudioWav.getWavHeader().getSampleRate());
			objPLP.processFrames(framesWithoutPreemphasis);
			
			lstPcc = getPccFromFeatureMeanSd(objPLP.getMean(), objPLP.getStandardDeviation());
			
		// Fusion (MFCC + **)
		} else {
			MFCC objMFCC = new MFCC(objAudioWav.getWavHeader().getSampleRate());
			objMFCC.processFrames(framesWithPreemphasis);
			
			List<PearsonCorrelationValues> lstA = getPccFromFeatureMeanSd(objMFCC.getMean(), objMFCC.getStandardDeviation());
			List<PearsonCorrelationValues> lstB;
			
			if (cmbFeature.getValue().equals(Features.MFCC_LPC)) {
				LPC objLPC = new LPC();
				objLPC.processFrames(framesWithPreemphasis);
				
				lstB = getPccFromFeatureMeanSd(objLPC.getMean(), objLPC.getStandardDeviation());
				lstPcc = getPccFromFeatureFusion(lstA, lstB);
				
			} else if (cmbFeature.getValue().equals(Features.MFCC_LPCC)) {
				LPC objLPCC = new LPC();
				objLPCC.processFrames(framesWithPreemphasis);
				
				lstB = getPccFromFeatureMeanSd(objLPCC.getMeanLpcc(), objLPCC.getStandardDeviationLpcc());
				lstPcc = getPccFromFeatureFusion(lstA, lstB);
				
			} else if (cmbFeature.getValue().equals(Features.MFCC_PLP)) {
				PLP objPLP = new PLP(objAudioWav.getWavHeader().getSampleRate());
				objPLP.processFrames(framesWithoutPreemphasis);
				
				lstB = getPccFromFeatureMeanSd(objPLP.getMean(), objPLP.getStandardDeviation());
				lstPcc = getPccFromFeatureFusion(lstA, lstB);
				
			} else if (cmbFeature.getValue().equals(Features.MFCC_LPC_LPCC_PLP)) {
				LPC objLPC = new LPC();
				objLPC.processFrames(framesWithPreemphasis);
				
				lstB = getPccFromFeatureMeanSd(objLPC.getMean(), objLPC.getStandardDeviation());
				lstPcc = getPccFromFeatureFusion(lstA, lstB);
				
				lstB = getPccFromFeatureMeanSd(objLPC.getMeanLpcc(), objLPC.getStandardDeviationLpcc());
				lstPcc = getPccFromFeatureFusion(lstPcc, lstB);
				
				PLP objPLP = new PLP(objAudioWav.getWavHeader().getSampleRate());
				objPLP.processFrames(framesWithoutPreemphasis);
				
				lstB = getPccFromFeatureMeanSd(objPLP.getMean(), objPLP.getStandardDeviation());
				lstPcc = getPccFromFeatureFusion(lstPcc, lstB);
			}
		}
		
		return lstPcc;
	}
	
	/**
	 * Extrai as features do segmento de áudio armazenados no banco de dados.
	 * 
	 * @param intIdAudioSegment  - ID do segmento de áudio
	 * 
	 * @return lstPcc - Lista com as features do segmento de áudio a ser classificado usando Pearson Correlation.
	 */
	private List<PearsonCorrelationValues> extractFeaturesFromAudioSegmentDatabase(long lgnIdAudioSegment) {
		List<PearsonCorrelationValues> lstPcc = new ArrayList<PearsonCorrelationValues>();
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
		
			// Power Spectrum
			if (cmbFeature.getValue().equals(Features.POWER_SPECTRUM)) {
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT frequency_vector, decibel_vector "); 
				objDatabaseConnection.sqlCommandAppend("FROM audio_files_segments_features_ps ");
				objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_file_segment = ? ");
				objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioSegment);
				
				ResultSet rsAudioFileSegmentValues = objDatabaseConnection.executeSelectQuery();
			    
			    while (rsAudioFileSegmentValues.next()) {
			    	lstPcc = getPowerSpectrumFeatureVector(rsAudioFileSegmentValues.getString("frequency_vector"), rsAudioFileSegmentValues.getString("decibel_vector"));
				}
			    
			// Descritores extraídos através de frames
			} else {
				String strFeatureVector = "";
				
				if (cmbFeature.getValue().equals(Features.MFCC)) {
					strFeatureVector = "mfcc_vector";
				} else if (cmbFeature.getValue().equals(Features.LPC)) {
					strFeatureVector = "lpc_vector";
				} else if (cmbFeature.getValue().equals(Features.LPCC)) {
					strFeatureVector = "lpcc_vector";
				} else if (cmbFeature.getValue().equals(Features.PLP)) {
					strFeatureVector = "plp_vector";
				} else if (cmbFeature.getValue().equals(Features.MFCC_LPC)) {
					strFeatureVector = "CONCAT(mfcc_vector, ';', lpc_vector)";
				} else if (cmbFeature.getValue().equals(Features.MFCC_LPCC)) {
					strFeatureVector = "CONCAT(mfcc_vector, ';', lpcc_vector)";
				} else if (cmbFeature.getValue().equals(Features.MFCC_PLP)) {
					strFeatureVector = "CONCAT(mfcc_vector, ';', plp_vector)";
				} else if (cmbFeature.getValue().equals(Features.MFCC_LPC_LPCC_PLP)) {
					strFeatureVector = "CONCAT(mfcc_vector, ';', lpc_vector, ';', lpcc_vector, ';', plp_vector)";
				}
				
				objDatabaseConnection.initiliazeStatement();
				objDatabaseConnection.sqlCommand("SELECT " + strFeatureVector + " AS feature_vector "); 
				objDatabaseConnection.sqlCommandAppend("FROM audio_files_segments_features ");
				objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_file_segment = ? ");
				objDatabaseConnection.sqlCommandAppend("AND ind_normalized = 1 ");
				objDatabaseConnection.addParameter("fk_audio_file_segment", LTDataTypes.LONG, lgnIdAudioSegment);
				
				ResultSet rsAudioFileSegmentValues = objDatabaseConnection.executeSelectQuery();
				
			    while (rsAudioFileSegmentValues.next()) {
			    	lstPcc = getFramedFeatureVector(rsAudioFileSegmentValues.getString("feature_vector"));
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lstPcc;
	}
	
	/**
	 * Configura a lista para ser aceita no formato pré-estabelecido do algoritmo de Pearson Correlation.
	 * 
	 * @param featureCoefficients
	 * 
	 * @return lstPcc - Lista com as features do segmento de áudio a ser classificado usando Pearson Correlation.
	 */
	private List<PearsonCorrelationValues> getPccFromFeatureCoefficients(double[][] featureCoefficients) {
		List<PearsonCorrelationValues> lstPcc = new ArrayList<PearsonCorrelationValues>();
		
		for (int indexCoefficient = 0; indexCoefficient < featureCoefficients[0].length; indexCoefficient++) {
			lstPcc.add(new PearsonCorrelationValues((int) featureCoefficients[0][indexCoefficient], featureCoefficients[1][indexCoefficient]));
		}
		
		return lstPcc;
	}
	
	/**
	 * Configura a lista para ser aceita no formato pré-estabelecido do algoritmo de Pearson Correlation.
	 * 
	 * @param mean
	 * @param standardDeviation
	 * 
	 * @return lstPcc - Lista com as features do segmento de áudio a ser classificado usando Pearson Correlation.
	 */
	private List<PearsonCorrelationValues> getPccFromFeatureMeanSd(double[] mean, double[] standardDeviation) {
		List<PearsonCorrelationValues> lstPcc = new ArrayList<PearsonCorrelationValues>();
		
		for (int indexValues = 0; indexValues < mean.length; indexValues++) {
			lstPcc.add(new PearsonCorrelationValues(lstPcc.size() + 1, mean[indexValues]));
		}
		
		for (int indexValues = 0; indexValues < standardDeviation.length; indexValues++) {
			lstPcc.add(new PearsonCorrelationValues(lstPcc.size() + 1, standardDeviation[indexValues]));
		}
		
		return lstPcc;
	}
	
	/**
	 * Realiza a fusão de listas dos coeficientes de diferentes algoritmos (descritores).
	 * 
	 * @param lstA
	 * @param lstB
	 */
	private List<PearsonCorrelationValues> getPccFromFeatureFusion(List<PearsonCorrelationValues> lstA, List<PearsonCorrelationValues> lstB) {
		List<PearsonCorrelationValues> lstPcc = new ArrayList<PearsonCorrelationValues>();
		
		for (int indexA = 0; indexA < lstA.size(); indexA++) {
			lstPcc.add(new PearsonCorrelationValues(lstPcc.size() + 1, lstA.get(indexA).getValue()));
		}
		
		for (int indexB = 0; indexB < lstB.size(); indexB++) {
			lstPcc.add(new PearsonCorrelationValues(lstPcc.size() + 1, lstB.get(indexB).getValue()));
		}
		
		return lstPcc;
	}
	
	/**
	 * Realiza a classificação do áudio através dos registros do banco de dados.
	 * 
	 * @param lstPccX - Valores do segmento a ser classificado
	 */
	private void classifyAudioFromDatabase(List<PearsonCorrelationValues> lstPccX) {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		// MySQL
		String strDateRecording = " STR_TO_DATE(CAST(IF (aud.date_year = 0, NULL, " +
				                  " IF (aud.date_month = 0, CONCAT('01/01/', aud.date_year), " +
				                  " IF (aud.date_day = 0, CONCAT('01/', aud.date_month, '/', aud.date_year), CONCAT(aud.date_day, '/', aud.date_month, '/', aud.date_year)))) " +
				                  " AS char CHARSET utf8),'%d/%m/%Y') ";
		
		// H2 
		if (WasisParameters.getInstance().getDatabaseEngine().equals("H2")) {
			strDateRecording = " parsedatetime(CASE aud.date_year " +
			                   " WHEN 0 THEN NULL " +
					           " ELSE (CASE aud.date_month " +
			                   " WHEN 0 THEN CONCAT('01/01/', aud.date_year) " +
					           " ELSE (CASE aud.date_day " +
			                   " WHEN 0 THEN CONCAT('01/', aud.date_month, '/', aud.date_year) " +
					           " ELSE CONCAT(aud.date_day, '/', aud.date_month, '/', aud.date_year) " +
			                   " END) " +
					           " END) " +
			                   " END, 'dd/MM/yyyy') ";
		}
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT id_audio_segment, audio_segment, audio_file_path, animal_genus, animal_species, animal_name_portuguese, animal_name_english, date_recording ");
			objDatabaseConnection.sqlCommandAppend("FROM ( ");
			objDatabaseConnection.sqlCommandAppend("SELECT seg.id_audio_segment, seg.audio_segment, aud.audio_file_path, " +
					                               "       tax.animal_phylum, tax.animal_class, tax.animal_order, tax.animal_family, tax.animal_genus, tax.animal_species, " +
					                               "     tax.animal_name_portuguese, tax.animal_name_english, aud.recordist, aud.location_country, aud.location_state, aud.location_city, " +
					                               "     alf.fk_audio_library, " + strDateRecording + " AS date_recording ");
			objDatabaseConnection.sqlCommandAppend("FROM            audio_files           aud ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN      audio_files_segments  seg   ON   seg.fk_audio_file      = aud.id_audio_file ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN      animal_taxonomies     tax   ON   seg.fk_animal_taxonomy = tax.id_animal_taxonomy ");
			objDatabaseConnection.sqlCommandAppend("LEFT OUTER JOIN audio_libraries_files alf   ON   alf.audio_file_path    = aud.audio_file_path ");
			objDatabaseConnection.sqlCommandAppend(") AS view_audio_segments_for_classification ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_audio_segment > 0 ");
			
			// Adiciona os filtros na consulta
			addDatabaseSelectFilter(objDatabaseConnection, "animal_phylum", objFilters.getAnimalPhylum());
			addDatabaseSelectFilter(objDatabaseConnection, "animal_class", objFilters.getAnimalClass());
			addDatabaseSelectFilter(objDatabaseConnection, "animal_order", objFilters.getAnimalOrder());
			addDatabaseSelectFilter(objDatabaseConnection, "animal_family", objFilters.getAnimalFamily());
			addDatabaseSelectFilter(objDatabaseConnection, "animal_genus", objFilters.getAnimalGenus());
			addDatabaseSelectFilter(objDatabaseConnection, "animal_species", objFilters.getAnimalSpecies());
			addDatabaseSelectFilter(objDatabaseConnection, "recordist", objFilters.getRecordist());
			addDatabaseSelectFilter(objDatabaseConnection, "location_country", objFilters.getLocationCountry());
			addDatabaseSelectFilter(objDatabaseConnection, "location_state", objFilters.getLocationState());
			addDatabaseSelectFilter(objDatabaseConnection, "location_city", objFilters.getLocationCity());
			
			// Biblioteca
			if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_classification_brute_force_filters_library"))) {
				if (lgnIdLibrary != 0) {
					objDatabaseConnection.sqlCommandAppend("AND fk_library = ? ");
					objDatabaseConnection.addParameter("fk_library", LTDataTypes.LONG, lgnIdLibrary);
				}
			}
			
			// Data Inicial
			if (objFilters.getInitialDate().getValue() != null && objFilters.getInitialDate().getValue().toString().trim().length() > 0) {
				String strDate = (String) objFilters.getInitialDate().getValue();
				
				if (!strDate.equals("")) {
					objDatabaseConnection.sqlCommandAppend("AND date_recording >= ? ");
					objDatabaseConnection.addParameter("date_recording", LTDataTypes.DATE, objFilters.getInitialDate().getValue());
				}
			}
			
			// Data Final
			if (objFilters.getFinalDate().getValue() != null && objFilters.getFinalDate().getValue().toString().trim().length() > 0) {
				String strDate = (String) objFilters.getFinalDate().getValue();
				
				if (!strDate.equals("")) {
					objDatabaseConnection.sqlCommandAppend("AND date_recording <= ? ");
					objDatabaseConnection.addParameter("date_recording", LTDataTypes.DATE, objFilters.getFinalDate().getValue());
				}
			}
			
			objDatabaseConnection.sqlCommandAppend("GROUP BY id_audio_segment, audio_segment, audio_file_path, animal_genus, animal_species, animal_name_portuguese, animal_name_english, date_recording ");
			
			ResultSet rsAudioFileSegments = objDatabaseConnection.executeSelectQuery();
			
			// É criada uma pool com uma thread para cada processador disponível para a classificação
			ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
			
			while (rsAudioFileSegments.next()) {
				List<PearsonCorrelationValues> lstPccY = extractFeaturesFromAudioSegmentDatabase(rsAudioFileSegments.getLong("id_audio_segment"));
				
				String strCommonName = rsAudioFileSegments.getString("animal_name_portuguese");
				
				if (WasisParameters.getInstance().getLanguage().equals(WasisParameters.LANGUAGUE_ENGLISH)) {
					strCommonName = rsAudioFileSegments.getString("animal_name_english");
				}
				
				PearsonCorrelationClassification objClassification = new PearsonCorrelationClassification(lstPccX, lstPccY);
				objClassification.setAudioSegmentInformation(rsAudioFileSegments.getLong("id_audio_segment"),
						                                     strCommonName,
						                                     rsAudioFileSegments.getString("animal_genus"),
						                                     rsAudioFileSegments.getString("animal_species"));
				executorService.execute(objClassification);
			}
			
			executorService.shutdown();
			executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS); // Aguarda finalizar todas as threads
			
		} catch (Exception e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
			
			// Retornas os melhores resultados da classificação
			Collections.sort(lstPccResults);
			
			for (int indexResult = 0; indexResult < Integer.parseInt(cmbBestResults.getValue()); indexResult++) {
				try {
					if (lstPccResults.get(indexResult).getIdAudioSegment() != 0) {
						objTableResults.addRow();
					    objTableResults.addRowData("id_audio_segment", lstPccResults.get(indexResult).getIdAudioSegment());
					    objTableResults.addRowData("correlation_result", lstPccResults.get(indexResult).getCorrelation());
					    objTableResults.addRowData("animal_common_name", lstPccResults.get(indexResult).getAnimalCommonName());
					    objTableResults.addRowData("animal_binomial_name", lstPccResults.get(indexResult).getAnimalGenus() + " " + lstPccResults.get(indexResult).getAnimalSpecies());
					}
				} catch (IndexOutOfBoundsException e) {
					break;
				}
			}
			
			objTableResults.orderColumnData("correlation_result", false);
			
			blnRunningClassification = false;
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
	}
	
	/**
	 * Mostra os resultados detalhados da comparação.
	 */
	private void showDetailedResults() {
		if (objTableResults.getRowCount() > 0 && objTableResults.getSelectedRow() >= 0) {
			int intIndexRowSegment = objTableAudioSegments.getSelectedRow();
			int intIndexRowResult = objTableResults.getSelectedRow();
			
			long lgnIdAudioSegmentResult = (long) objTableResults.getValue(intIndexRowResult, "id_audio_segment");
			String strIdentificationAudioSegmentResult = (String) objTableResults.getValue(intIndexRowResult, "animal_binomial_name");
			
			String strCommonNameAudioSegmentResult = (String) objTableResults.getValue(intIndexRowResult, "animal_common_name");
			
			if (!strCommonNameAudioSegmentResult.equals("")) {
				strIdentificationAudioSegmentResult = strIdentificationAudioSegmentResult + " (" + strCommonNameAudioSegmentResult + ")";
			}
			
			List<PearsonCorrelationValues> lstValuesX = extractFeaturesFromAudioSegment(intIndexRowSegment);
			List<PearsonCorrelationValues> lstValuesY = extractFeaturesFromAudioSegmentDatabase(lgnIdAudioSegmentResult);
			
			PearsonCorrelation objPearsonCorrelation = new PearsonCorrelation(true, lstValuesX, lstValuesY);
			objPearsonCorrelation.calculateCorrelationCoeficient();
			
			ScreenAudioClassificationBruteForceResults objResults = new ScreenAudioClassificationBruteForceResults(objPearsonCorrelation.getXSample(), objPearsonCorrelation.getYSample(), strIdentificationAudioSegmentResult);
			objResults.showScreen();
		}
	}
	
	/**
	 * Transforma uma lista do vetor do formato da base de dados para um legível ao Brute Force.
	 * Utilizado nos algoritmos que extrai os coeficientes em diversos frames.
	 * 
	 * @param strFeature - Array de coeficientes armazenado no BD (armazenado em string)
	 */
	private List<PearsonCorrelationValues> getFramedFeatureVector(String strFeature) {
		String[] originalFeatureVector = strFeature.split(";");
		
		List<PearsonCorrelationValues> lstPccValues = new ArrayList<PearsonCorrelationValues>();
		
		for (int indexElement = 0; indexElement < originalFeatureVector.length; indexElement++) {
			lstPccValues.add(new PearsonCorrelationValues(indexElement, Double.parseDouble(originalFeatureVector[indexElement])));
		}
		
		return lstPccValues;
	}
	
	/**
	 * Transforma uma lista do vetor do algoritmo de <i>Power Spectrum</i> do formato da
	 * base de dados para um legível ao Brute Force.
	 * 
	 * @param strFrequency - Array de frequências armazenado no BD (armazenado em string)
	 * @param strDecibel   - Array de decibels armazenado no BD (armazenado em string)
	 */
	private List<PearsonCorrelationValues> getPowerSpectrumFeatureVector(String strFrequency, String strDecibel) {
		String[] originalFrequencyVector = strFrequency.split(";");
		String[] originalDecibelVector = strDecibel.split(";");
		
		List<PearsonCorrelationValues> lstPccValues = new ArrayList<PearsonCorrelationValues>();
		
		if (originalFrequencyVector.length == originalDecibelVector.length) {
			for (int indexElement = 0; indexElement < originalFrequencyVector.length; indexElement++) {
				lstPccValues.add(new PearsonCorrelationValues(Integer.parseInt(originalFrequencyVector[indexElement]), Double.parseDouble(originalDecibelVector[indexElement])));
			}
		}
		
		return lstPccValues;
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável pela execução da comparação de áudios
	 * quando houver clique em um segmento de áudio (ROI).
	 */
	private class AudioSegmentMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 1) {
				if (objTableAudioSegments.getRowCount() > 0) {
					classifyAudio();
				}
			}
	    }
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável pelo carregamento da tela de resultados
	 * quando houver duplo clique em um registro.
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
	
	/**
	 * Realiza a classificação através da Correlação de Pearson.
	 */
	private class PearsonCorrelationClassification implements Runnable {
		private List<PearsonCorrelationValues> lstXSample;
		private List<PearsonCorrelationValues> lstYSample;
		
		private long lgnIdAudioSegment;
		private String strAnimalCommonName;
		private String strAnimalGenus;
		private String strAnimalSpecies;
		
		protected PearsonCorrelationClassification(List<PearsonCorrelationValues> lstXSample, List<PearsonCorrelationValues> lstYSample) {
			this.lstXSample = lstXSample;
			this.lstYSample = lstYSample;
		}
		
		protected void setAudioSegmentInformation(long lgnIdAudioSegment, String strAnimalCommonName, String strAnimalGenus, String strAnimalSpecies) {
			this.lgnIdAudioSegment = lgnIdAudioSegment;
			this.strAnimalCommonName = strAnimalCommonName;
			this.strAnimalGenus = strAnimalGenus;
			this.strAnimalSpecies = strAnimalSpecies;
		}
		
		@Override
		public void run() {
			try {
				PearsonCorrelation objPearsonCorrelation = new PearsonCorrelation(true, lstXSample, lstYSample);
				double dblCorrelation = objPearsonCorrelation.calculateCorrelationCoeficient();
				
				// Adiciona resultados na 'objTableResults'
			    if (cmbDataSource.getValue().equals(rsBundle.getString("screen_audio_classification_brute_force_filters_database"))) {
			    	lstPccResults.add(new PearsonCorrelationResults(lgnIdAudioSegment, dblCorrelation, strAnimalCommonName, strAnimalGenus, strAnimalSpecies));
			    }
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
	    }
	}
	
	private class PearsonCorrelationResults implements Comparable<PearsonCorrelationResults> {
		private long lgnIdAudioSegment;
		private double dblCorrelation;
		private String strAnimalCommonName;
		private String strAnimalGenus;
		private String strAnimalSpecies;
		
		long getIdAudioSegment() {
			return lgnIdAudioSegment;
		}
		
		double getCorrelation() {
			return dblCorrelation;
		}
		
		String getAnimalCommonName() {
			return strAnimalCommonName;
		}
		
		String getAnimalGenus() {
			return strAnimalGenus;
		}
		
		String getAnimalSpecies() {
			return strAnimalSpecies;
		}
		
		PearsonCorrelationResults(long lgnIdAudioSegment, double dblCorrelation, String strAnimalCommonName, String strAnimalGenus, String strAnimalSpecies) {
			this.lgnIdAudioSegment = lgnIdAudioSegment;
			this.dblCorrelation = dblCorrelation;
			this.strAnimalCommonName = strAnimalCommonName;
			this.strAnimalGenus = strAnimalGenus;
			this.strAnimalSpecies = strAnimalSpecies;
		}
		
		@Override
		public int compareTo(PearsonCorrelationResults objPearsonCorrelationResults) {
			if (this.dblCorrelation < objPearsonCorrelationResults.dblCorrelation) {
	            return 1;//-1;
	        }
			
	        if (this.dblCorrelation > objPearsonCorrelationResults.dblCorrelation) {
	            return -1; //1;
	        }
	        
	        return 0;
		}
	}
}