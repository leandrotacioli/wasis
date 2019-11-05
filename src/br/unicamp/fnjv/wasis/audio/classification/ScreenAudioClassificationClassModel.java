package br.unicamp.fnjv.wasis.audio.classification;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
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

import br.unicamp.fnjv.wasis.audio.AudioSegmentsValues;
import br.unicamp.fnjv.wasis.audio.AudioTemporary;
import br.unicamp.fnjv.wasis.classifiers.hmm.HMM;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.CodebookDictionary;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.features.Features;
import br.unicamp.fnjv.wasis.features.LPC;
import br.unicamp.fnjv.wasis.features.MFCC;
import br.unicamp.fnjv.wasis.features.PLP;
import br.unicamp.fnjv.wasis.features.Preprocessing;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisDialogLoadingData;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que
 * exibe a lista de seleções efetuadas no espectrograma
 * e uma outra lista que mostra os resultados da comparações.
 * Utiliza HMM
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 01/Abr/2018
 */
public class ScreenAudioClassificationClassModel extends JDialog {
	private static final long serialVersionUID = -5765475068476175620L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	private AudioWav objAudioWav;
	
	private long lgnIdClassModel;
	
	private LTTextField txtClassModelDescription;
	private LTTextField txtClassModelDateCreation;
	private LTTextField txtClassModelAnimalClass;
	private JButton btnClassModel;
	
	private LTComboBoxField cboFeature;
	private LTComboBoxField cboClassifier;
	
	private WasisPanel panelAudioSegments;
	private LTTable objTableAudioSegments;
	
	private WasisPanel panelResults;
	private LTTable objTableResults;
	
	private LTTextField txtResult;
	
	private boolean blnRunningComparison;

	/**
	 * Classe responsável pela exibição de uma tela que
	 * mostra uma lista de seleções efetuadas no espectrograma
	 * que ainda não foram gravadas no banco de dados, e outra
	 * lista de seleções que já foram gravadas no banco de dados.
 	 * 
 	 * @param objAudioWav
 	 *
	 * @throws CloneNotSupportedException
	 */
	public ScreenAudioClassificationClassModel(AudioWav objAudioWav)  throws CloneNotSupportedException {
		this.objAudioWav = (AudioWav) objAudioWav.clone();
		
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			txtClassModelDescription = new LTTextField("Descrição do Modelo de Classe:", LTDataTypes.STRING, false, true, 500);
			txtClassModelDateCreation = new LTTextField("Data de Criação:", LTDataTypes.DATE, false, true);
			txtClassModelAnimalClass = new LTTextField("Classe:", LTDataTypes.STRING, false, true, 500);
			
			btnClassModel = new JButton();
			btnClassModel.setToolTipText("Consulta Modelos Treinados");
			btnClassModel.setMinimumSize(new Dimension(23, 23));
			btnClassModel.setMaximumSize(new Dimension(23, 23));
			btnClassModel.setFocusable(false);
			btnClassModel.setIcon(new ImageIcon("res/images/open.png"));
			btnClassModel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					ScreenSearchClassModels objScreenSearchClassModels = new ScreenSearchClassModels();
					objScreenSearchClassModels.showScreen();
		    		
					lgnIdClassModel = objScreenSearchClassModels.getIdClassModel();
					
					loadClassModel();
				}
			});
			
			cboFeature = new LTComboBoxField(rsBundle.getString("feature"), true, true);
			cboFeature.setEnabled(false);
			
			cboClassifier = new LTComboBoxField(rsBundle.getString("classifier"), true, true);
			cboClassifier.addValues(rsBundle.getString("classifier_hmm"), rsBundle.getString("classifier_hmm"));
			cboClassifier.setValue(rsBundle.getString("classifier_hmm"));
			cboClassifier.setEnabled(false);
			
			// *********************************************************************************************
			// Seleções
			panelAudioSegments = new WasisPanel(rsBundle.getString("screen_audio_classification_class_model_audio_segments"));
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
			
			// *********************************************************************************************
			// Resultados da comparação
			txtResult = new LTTextField(rsBundle.getString("screen_audio_classification_class_model_results_suggestion"), LTDataTypes.STRING, false, false);
			
			panelResults = new WasisPanel(rsBundle.getString("screen_audio_classification_class_model_results"));
			panelResults.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableResults = new LTTable(true);
			objTableResults.addColumn("comparation_result", rsBundle.getString("screen_audio_classification_class_model_results_comparation"), LTDataTypes.DOUBLE, 130, false);
			objTableResults.addColumn("animal_species", rsBundle.getString("animal_species"), LTDataTypes.STRING, 365, false);
			objTableResults.addColumn("animal_common_name", rsBundle.getString("animal_name_portuguese"), LTDataTypes.STRING, 270, false);
			objTableResults.showTable();
			
			objTableResults.setColumnDoubleFractionDigits("comparation_result", 4);
			
			// ***********************************************************************************************************************
			// Cria a tela
			objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_classification_class_model_screen_description"), true);
			objWasisDialog.setBounds(350, 350, 850, 550);
			objWasisDialog.setMinimumSize(new Dimension(850, 550));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][grow][][grow]"));
			objWasisDialog.getContentPane().add(txtClassModelDescription, "cell 0 0, grow, width 250");
			objWasisDialog.getContentPane().add(txtClassModelDateCreation, "cell 0 0, grow, width 50");
			objWasisDialog.getContentPane().add(txtClassModelAnimalClass, "cell 0 0, grow, width 100");
			objWasisDialog.getContentPane().add(btnClassModel, "cell 0 0, gap 3 0 16 0");
			objWasisDialog.getContentPane().add(cboFeature, "cell 0 1, grow, width 400");
			objWasisDialog.getContentPane().add(cboClassifier, "cell 0 1, grow, width 400");
			objWasisDialog.getContentPane().add(panelAudioSegments, "cell 0 2, grow");
			objWasisDialog.getContentPane().add(panelAudioSegments, "cell 0 2, grow");
			objWasisDialog.getContentPane().add(txtResult, "cell 0 3, grow");
			objWasisDialog.getContentPane().add(panelResults, "cell 0 4, grow, gap 0 0 5 0");
			
			panelAudioSegments.add(objTableAudioSegments, "cell 0 0, grow");
			
			panelResults.add(objTableResults, "cell 0 0, grow");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carrega os dados das seleções.
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
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
		
		btnClassModel.setRequestFocusEnabled(true);
		btnClassModel.requestFocus();
		btnClassModel.requestFocusInWindow();
	}
	
	/**
	 * Realiza a comparação dos áudios dos registros do banco de dados.
	 * 
	 * @param intIndexRow  - Índice da seleção
	 */
	private void compareAudiosFromDatabase(int intIndexRow) {
		objTableResults.deleteRows();
		txtResult.setValue("");
		
		int intInitialTime = (int) objTableAudioSegments.getValue(intIndexRow, "time_initial");
		int intFinalTime = (int) objTableAudioSegments.getValue(intIndexRow, "time_final");
		//int intInitialFrequency = (int) objTableAudioSegments.getValue(intIndexRow, "frequency_initial");
		//int intFinalFrequency = (int) objTableAudioSegments.getValue(intIndexRow, "frequency_final");
		
		int intInitialChunkToProcess = objAudioWav.getSampleFromTime(intInitialTime);
		int intFinalChunkToProcess = objAudioWav.getSampleFromTime(intFinalTime);
		
		double[] arrayAmplitudes = objAudioWav.getAmplitudesChunk(intInitialChunkToProcess, intFinalChunkToProcess);
		
		double[] preEmphasis = Preprocessing.preEmphasis(arrayAmplitudes);
		
		double[][] framesWithPreemphasis = Preprocessing.framing(preEmphasis);          // Utilizado na MFCC, LPC, LPCC
		double[][] framesWithoutPreemphasis = Preprocessing.framing(arrayAmplitudes);   // Utilizado na PS, PLP
		
		double[][] featureVectors = null;
		double[][] featureVectorsToBeFused;
		
		// MFCC
		if (cboFeature.getValue().equals(Features.MFCC)) {
			MFCC objMFCC = new MFCC(objAudioWav.getWavHeader().getSampleRate());
			objMFCC.processFrames(framesWithPreemphasis);
			
			featureVectors = objMFCC.getFeature();
			
		// LPC e LPCC
		} else if (cboFeature.getValue().equals(Features.LPC) || cboFeature.getValue().equals(Features.LPCC)) {
			LPC objLPC = new LPC();
			objLPC.processFrames(framesWithPreemphasis);
			
			if (cboFeature.getValue().equals(Features.LPC)) {
				featureVectors = objLPC.getFeature();
			} else if (cboFeature.getValue().equals(Features.LPCC)) {
				featureVectors = objLPC.getFeatureLpcc();
			}
			
		// PLP
		} else if (cboFeature.getValue().equals(Features.PLP)) {
			PLP objPLP = new PLP(objAudioWav.getWavHeader().getSampleRate());
			objPLP.processFrames(framesWithoutPreemphasis);
			
			featureVectors = objPLP.getFeature();
			
		// Fusion (MFCC + **)
		} else {
			MFCC objMFCC = new MFCC(objAudioWav.getWavHeader().getSampleRate());
			objMFCC.processFrames(framesWithPreemphasis);
			
			featureVectors = objMFCC.getFeature();
			
			if (cboFeature.getValue().equals(Features.MFCC_LPC)) {
				LPC objLPC = new LPC();
				objLPC.processFrames(framesWithPreemphasis);
				
				featureVectorsToBeFused = objLPC.getFeature();
				featureVectors = getFusedFeatures(featureVectors, featureVectorsToBeFused);
				
			} else if (cboFeature.getValue().equals(Features.MFCC_LPCC)) {
				LPC objLPCC = new LPC();
				objLPCC.processFrames(framesWithPreemphasis);
				
				featureVectorsToBeFused = objLPCC.getFeatureLpcc();
				featureVectors = getFusedFeatures(featureVectors, featureVectorsToBeFused);
				
			} else if (cboFeature.getValue().equals(Features.MFCC_PLP)) {
				PLP objPLP = new PLP(objAudioWav.getWavHeader().getSampleRate());
				objPLP.processFrames(framesWithoutPreemphasis);
				
				featureVectorsToBeFused = objPLP.getFeature();
				featureVectors = getFusedFeatures(featureVectors, featureVectorsToBeFused);
				
			} else if (cboFeature.getValue().equals(Features.MFCC_LPC_LPCC_PLP)) {
				LPC objLPC = new LPC();
				objLPC.processFrames(framesWithPreemphasis);
				
				featureVectorsToBeFused = objLPC.getFeature();
				featureVectors = getFusedFeatures(featureVectors, featureVectorsToBeFused);
				
				featureVectorsToBeFused = objLPC.getFeatureLpcc();
				featureVectors = getFusedFeatures(featureVectors, featureVectorsToBeFused);
				
				PLP objPLP = new PLP(objAudioWav.getWavHeader().getSampleRate());
				objPLP.processFrames(framesWithoutPreemphasis);
				
				featureVectorsToBeFused = objPLP.getFeature();
				featureVectors = getFusedFeatures(featureVectors, featureVectorsToBeFused);
			}
		}
		
		// Carrega o codebook do banco de dados
		Codebook objCodebook = loadCodebook(cboFeature.getValue());
				
		Points[] points = getPointsFromFeatureVector(featureVectors);
		int[] quantized = objCodebook.quantize(points);
		
		// Carrega os modelos HMM do banco de dados
		List<SpeciesHMM> lstSpeciesHmm = loadSpeciesHmm(cboFeature.getValue());
		
		// find the likelihood by viterbi decoding of quantized sequence
		for (int indexSpecies = 0; indexSpecies < lstSpeciesHmm.size(); indexSpecies++) {
			double dblLikelihood = lstSpeciesHmm.get(indexSpecies).getHMM().viterbi(quantized);
			
			lstSpeciesHmm.get(indexSpecies).setLikelihood(dblLikelihood);
			
			objTableResults.addRow();
			objTableResults.addRowData("comparation_result", dblLikelihood);
			objTableResults.addRowData("animal_species", lstSpeciesHmm.get(indexSpecies).getAnimalBinomialNomenclature());
			objTableResults.addRowData("animal_common_name", lstSpeciesHmm.get(indexSpecies).getAnimalCommonName());
		}
		
		// Ordena os resultados finais
		Collections.sort(lstSpeciesHmm);
		
		txtResult.setValue(lstSpeciesHmm.get(0).getAnimalBinomialNomenclature() + " (" + lstSpeciesHmm.get(0).getAnimalCommonName() + ")");
		
		objTableResults.orderColumnData("comparation_result", false);
		
		blnRunningComparison = false;
	}
	
	/**
	 * 
	 * @param features
	 * @return
	 */
	private static Points[] getPointsFromFeatureVector(double[][] mfccVector) {
		Points pts[] = new Points[mfccVector.length];
		
		for (int j = 0; j < mfccVector.length; j++) {
			pts[j] = new Points(mfccVector[j]);
		}
		
		return pts;
	}
	
	/**
	 * Realiza a comparação dos áudios em background.
	 */
	private void compareAudios() {
		if (lgnIdClassModel == 0) {
			WasisMessageBox.showMessageDialog("Selecione um modelo de classes para efetuar a classificação.", WasisMessageBox.WARNING_MESSAGE);
			btnClassModel.doClick();
		} else if (cboFeature.getValue().equals("")) {
			WasisMessageBox.showMessageDialog("Selecione um descritor para efetuar a classificação.", WasisMessageBox.WARNING_MESSAGE);
			cboFeature.requestFocus();
		} else {
			if (objTableAudioSegments.getRowCount() > 0 && objTableAudioSegments.getSelectedRow() >= 0) {
				final WasisDialogLoadingData objWasisDialogLoadingData = new WasisDialogLoadingData(rsBundle.getString("screen_audio_classification_class_model_running_audio_classification"));
				
				SwingWorker<Boolean, Integer> swingWorkerAudioClassification = new SwingWorker<Boolean, Integer>() {
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
	
							compareAudiosFromDatabase(objTableAudioSegments.getSelectedRow());
							
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
				
			    swingWorkerAudioClassification.addPropertyChangeListener(listenerConvertion);
			    swingWorkerAudioClassification.execute();
			}
		}
	}
	
	private double[][] getFusedFeatures(double[][] featureVectorA, double[][] featureVectorB) {
		double[][] fusedFeature = null;
		
		// Só é possível realizar a fusão se a quantidade de linhas das matrizes forem iguais
		if (featureVectorA.length == featureVectorB.length) {
			fusedFeature = new double[featureVectorA.length][featureVectorA[0].length + featureVectorB[0].length];
			
			// Loop através das linhas
			for (int indexRow = 0; indexRow < featureVectorA.length; indexRow++) {
				// Loop através das colunas da matriz A
				for (int indexColumnA = 0; indexColumnA < featureVectorA[0].length; indexColumnA++) {
					fusedFeature[indexRow][indexColumnA] = featureVectorA[indexRow][indexColumnA];
				}
				
				// Loop através das colunas da matriz B
				for (int indexColumnB = 0; indexColumnB < featureVectorB[0].length; indexColumnB++) {
					fusedFeature[indexRow][featureVectorA[0].length + indexColumnB] = featureVectorB[indexRow][indexColumnB];
				}
			}
		}
		
		return fusedFeature;
	}
	
	/**
	 * Preenche os dados do modelo de classe.
	 */
	private void loadClassModel() {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT * ");
			objDatabaseConnection.sqlCommandAppend("FROM class_models ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_class_model = ? ");
			objDatabaseConnection.addParameter("id_class_model", LTDataTypes.LONG, lgnIdClassModel);
			
			ResultSet rsRecord = objDatabaseConnection.executeSelectQuery();
			
			objDatabaseConnection.rollBackTransaction();
			
			while (rsRecord.next()) {
				txtClassModelDescription.setValue(rsRecord.getString("class_model_description"));
				txtClassModelDateCreation.setValue(rsRecord.getString("date_creation"));
				txtClassModelAnimalClass.setValue(rsRecord.getString("animal_class"));
			}
			
			// Carrega os descritores do modelo de classe
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT feature_representation ");
			objDatabaseConnection.sqlCommandAppend("FROM class_models_hmm ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_class_model = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY id_class_model_hmm ");
			objDatabaseConnection.addParameter("fk_class_model", LTDataTypes.LONG, lgnIdClassModel);
			
			ResultSet rsFeatures = objDatabaseConnection.executeSelectQuery();
			
			objDatabaseConnection.rollBackTransaction();
			
			String strFirstFeature = "";
			
			while (rsFeatures.next()) {
				if (strFirstFeature.equals("")) {
					strFirstFeature = rsFeatures.getString("feature_representation");
				}
				
				String strFeature = rsFeatures.getString("feature_representation");
				String strFeatureDescription = rsFeatures.getString("feature_representation");
				
				if (strFeature.equals(Features.MFCC)) {
					strFeatureDescription = rsBundle.getString("feature_mfcc");
				} else if (strFeature.equals(Features.LPC)) {
					strFeatureDescription = rsBundle.getString("feature_lpc");
				} else if (strFeature.equals(Features.LPCC)) {
					strFeatureDescription = rsBundle.getString("feature_lpcc");
				} else if (strFeature.equals(Features.PLP)) {
					strFeatureDescription = rsBundle.getString("feature_plp");
				} else if (strFeature.equals(Features.MFCC_LPC)) {
					strFeatureDescription = rsBundle.getString("feature_mfcc_lpc");
				} else if (strFeature.equals(Features.MFCC_LPCC)) {
					strFeatureDescription = rsBundle.getString("feature_mfcc_lpcc");
				} else if (strFeature.equals(Features.MFCC_PLP)) {
					strFeatureDescription = rsBundle.getString("feature_mfcc_plp");
				} else if (strFeature.equals(Features.MFCC_LPC_LPCC_PLP)) {
					strFeatureDescription = rsBundle.getString("feature_mfcc_lpc_lpcc_plp");
				}
				
				cboFeature.addValues(strFeature, strFeatureDescription);
			}
			
			cboFeature.setValue(strFirstFeature);
			cboFeature.setEnabled(true);
			cboClassifier.setEnabled(true);
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Carrega o codebook do Modelo de Classe selecionado.
	 * 
	 * @return objCodebook
	 */
	private Codebook loadCodebook(String strFeature) {
		Codebook objCodebook = new Codebook();
		CodebookDictionary objCodebookDictionary = null;
		
		ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT codebook ");
			objDatabaseConnection.sqlCommandAppend("FROM class_models_hmm ");
			objDatabaseConnection.sqlCommandAppend("WHERE fk_class_model = ? ");
			objDatabaseConnection.sqlCommandAppend("AND feature_representation = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY id_class_model_hmm ");
			objDatabaseConnection.addParameter("fk_class_model", LTDataTypes.LONG, lgnIdClassModel);
			objDatabaseConnection.addParameter("feature_representation", LTDataTypes.STRING, strFeature);
			
			ResultSet rsCodebook = objDatabaseConnection.executeSelectQuery();
			
			objDatabaseConnection.rollBackTransaction();
			
			byte[] bais;
			
			while (rsCodebook.next()) {
				bais = rsCodebook.getBytes("codebook");
				
	            bis = new ByteArrayInputStream(bais);
	            ois = new ObjectInputStream(bis);
	            objCodebookDictionary = (CodebookDictionary) ois.readObject();
	            
	            objCodebook.loadCodebookDictionary(objCodebookDictionary);
			}
			
			if (bis != null) {
				bis.close();
            }
			
            if (ois != null) {
                ois.close();
            }
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return objCodebook;
	}
	
	private List<SpeciesHMM> loadSpeciesHmm(String strFeature) {
		List<SpeciesHMM> lstSpeciesHmm = new ArrayList<SpeciesHMM>();
		
		ByteArrayInputStream bis = null;
        ObjectInputStream ois = null;
        
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT spe.species_model, CONCAT(tax.animal_genus, ' ', tax.animal_species) AS binomial_nomenclature, tax.animal_name_portuguese ");
			objDatabaseConnection.sqlCommandAppend("FROM       class_models_hmm           hmm ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN class_models_hmm_species   spe  ON  spe.fk_class_model_hmm = hmm.id_class_model_hmm ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN animal_taxonomies          tax  ON  spe.fk_animal_taxonomy = tax.id_animal_taxonomy ");
			objDatabaseConnection.sqlCommandAppend("WHERE hmm.fk_class_model = ? ");
			objDatabaseConnection.sqlCommandAppend("AND hmm.feature_representation = ? ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY tax.animal_genus, tax.animal_species ");
			objDatabaseConnection.addParameter("fk_class_model", LTDataTypes.LONG, lgnIdClassModel);
			objDatabaseConnection.addParameter("feature_representation", LTDataTypes.STRING, strFeature);
			
			ResultSet rsSpeciesModels = objDatabaseConnection.executeSelectQuery();
			
			objDatabaseConnection.rollBackTransaction();
			
			byte[] bais;
			
			String strAnimalBinomialNomenclature;
			String strAnimalCommonName;
			HMM objHMM;

			while (rsSpeciesModels.next()) {
				bais = rsSpeciesModels.getBytes("species_model");
				
	            bis = new ByteArrayInputStream(bais);
	            ois = new ObjectInputStream(bis);
	            objHMM = (HMM) ois.readObject();
	            
	            strAnimalBinomialNomenclature = rsSpeciesModels.getString("binomial_nomenclature");
	            strAnimalCommonName = rsSpeciesModels.getString("animal_name_portuguese");
	            
	            lstSpeciesHmm.add(new SpeciesHMM(strAnimalBinomialNomenclature, strAnimalCommonName, objHMM));
	            
	            if (bis != null) {
					bis.close();
	            }
				
	            if (ois != null) {
	                ois.close();
	            }
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} catch (IOException e) {
			e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lstSpeciesHmm;
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável 
	 * pela execução da comparação de áudios quando houver clique em um registro.
	 */
	private class AudioSegmentMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 1) {
				if (objTableAudioSegments.getRowCount() > 0) {
					compareAudios();
				}
			}
	    }
	}
	
	/**
	 * 
	 * @author Leandro Tacioli
	 * @version 1.0 - 02/Abr/2018
	 */
	private class SpeciesHMM implements Comparable<SpeciesHMM> {
		private String strAnimalBinomialNomenclature;
		private String strAnimalCommonName;
		private HMM objHMM;
		private double dblLikelihood;
		
		String getAnimalBinomialNomenclature() {
			return strAnimalBinomialNomenclature;
		}

		String getAnimalCommonName() {
			return strAnimalCommonName;
		}

		HMM getHMM() {
			return objHMM;
		}
		
		//double getLikelihood() {
		//	return dblLikelihood;
		//}
		
		void setLikelihood(double dblLikelihood) {
			this.dblLikelihood = dblLikelihood;
		}
		
		SpeciesHMM(String strAnimalBinomialNomenclature, String strAnimalCommonName, HMM objHMM) {
			this.strAnimalBinomialNomenclature = strAnimalBinomialNomenclature;
			this.strAnimalCommonName = strAnimalCommonName;
			this.objHMM = objHMM;
		}
		
		@Override
		public int compareTo(SpeciesHMM objSpeciesHMM) {
			if (this.dblLikelihood < objSpeciesHMM.dblLikelihood) {
	            return 1;//-1;
	        }
			
	        if (this.dblLikelihood > objSpeciesHMM.dblLikelihood) {
	            return -1; //1;
	        }
	        
	        return 0;
		}
	}
}