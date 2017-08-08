package br.unicamp.fnjv.wasis.audio.comparison;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
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
import br.unicamp.fnjv.wasis.classifiers.hmm.HMM;
import br.unicamp.fnjv.wasis.classifiers.hmm.db.DataBase;
import br.unicamp.fnjv.wasis.classifiers.hmm.db.ObjectIODataBase;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.DatabaseConnection;
import br.unicamp.fnjv.wasis.features.LPC;
import br.unicamp.fnjv.wasis.features.MFCC;
import br.unicamp.fnjv.wasis.features.PLP;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisDialogLoadingData;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que
 * exibe a lista de seleções efetuadas no espectrograma
 * e uma outra lista que mostra os resultados da comparações.
 * Utiliza HMM
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 06/Jul/2016
 */
public class ScreenAudioComparisonHMM extends JDialog {
	private static final long serialVersionUID = -5765475068476175620L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	private GraphicPanel objSpectrogramGraphicPanel;
	
	private LTTextField txtDescription;
	private LTTextField txtDateCreation;
	private JButton btnDescription;
	private LTComboBoxField cmbAnimalClass;
	private LTComboBoxField cmbFeature;
	private LTComboBoxField cmbClassifier;
	
	private WasisPanel panelSelections;
	private LTTable objTableSelections;
	
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
 	 * @param strAudioFilePath
	 * @param objSpectrogramGraphicPanel
	 * @param lgnIdLibrary
	 */
	public ScreenAudioComparisonHMM(GraphicPanel objSpectrogramGraphicPanel) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;

		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			txtDescription = new LTTextField("Description:", LTDataTypes.STRING, true, true, 500);
			txtDescription.setValue("FNJV");
			txtDateCreation = new LTTextField("Date:", LTDataTypes.DATE, true, true);
			txtDateCreation.setValue("01/06/2017");
			
			btnDescription = new JButton();
			btnDescription.setToolTipText("Consulta Modelos Treinados");
			btnDescription.setMinimumSize(new Dimension(23, 23));
			btnDescription.setMaximumSize(new Dimension(23, 23));
			btnDescription.setFocusable(false);
			btnDescription.setIcon(new ImageIcon("res/images/open.png"));
			btnDescription.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					
				}
			});
			
			cmbAnimalClass = new LTComboBoxField("Animal Class:", true, true);
			cmbAnimalClass.addValues("All Classes", "All Classes");
			cmbAnimalClass.addValues("Aves", "Aves");
			cmbAnimalClass.addValues("Amphibia", "Amphibia");
			cmbAnimalClass.setValue("All Classes");
			
			cmbFeature = new LTComboBoxField(rsBundle.getString("feature"), true, true);
			cmbFeature.addValues("MFCC", rsBundle.getString("feature_mfcc"));
			cmbFeature.addValues("LPC", rsBundle.getString("feature_lpc"));
			cmbFeature.addValues("LPCC", rsBundle.getString("feature_lpcc"));
			cmbFeature.addValues("PLP", rsBundle.getString("feature_plp"));
			cmbFeature.addValues("MFCC-LPC", "MFCC + LPC");
			cmbFeature.addValues("MFCC-LPCC", "MFCC + LPCC");
			cmbFeature.addValues("MFCC-PLP", "MFCC + PLP");
			cmbFeature.addValues("MFCC-LPC-LPCC-PLP", "MFCC + LPC + LPCC + PLP");
			cmbFeature.setValue("MFCC");
			
			cmbClassifier = new LTComboBoxField(rsBundle.getString("classifier"), true, true);
			cmbClassifier.addValues(rsBundle.getString("classifier_hmm"), rsBundle.getString("classifier_hmm"));
			//cmbClassifier.addValues(rsBundle.getString("classifier_svm"), rsBundle.getString("classifier_svm"));
			cmbClassifier.setValue(rsBundle.getString("classifier_hmm"));
			
			// *********************************************************************************************
			// Seleções
			panelSelections = new WasisPanel(rsBundle.getString("screen_audio_comparison_selections"));
			panelSelections.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableSelections = new LTTable(true);
			objTableSelections.addColumn("sound_unit", rsBundle.getString("audio_file_selection_sound_unit"), LTDataTypes.STRING, 140, false);
			objTableSelections.addColumn("time_initial", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.INTEGER, 0, false);
			objTableSelections.addColumn("time_final", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.INTEGER, 0, false);
			objTableSelections.addColumn("frequency_initial", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.INTEGER, 0, false);
			objTableSelections.addColumn("frequency_final", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.INTEGER, 0, false);
			objTableSelections.addColumn("time_initial_show", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.STRING, 145, false);
			objTableSelections.addColumn("time_final_show", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.STRING, 145, false);
			objTableSelections.addColumn("frequency_initial_show", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.STRING, 145, false);
			objTableSelections.addColumn("frequency_final_show", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.STRING, 145, false);
			objTableSelections.addMouseListener(new SelectionMouseAdapter());
			objTableSelections.showTable();

			loadSelections();
			
			// *********************************************************************************************
			// Resultados da comparação
			txtResult = new LTTextField(rsBundle.getString("screen_audio_comparison_hmm_results_suggestion"), LTDataTypes.STRING, false, false);
			
			panelResults = new WasisPanel(rsBundle.getString("screen_audio_comparison_results"));
			panelResults.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableResults = new LTTable(true);
			objTableResults.addColumn("comparation_result", rsBundle.getString("screen_audio_comparison_hmm_results_comparation"), LTDataTypes.DOUBLE, 150, false);
			objTableResults.addColumn("animal_species", rsBundle.getString("animal_species"), LTDataTypes.STRING, 300, false);
			objTableResults.addColumn("animal_name_english", rsBundle.getString("animal_name_english"), LTDataTypes.STRING, 270, false);
			objTableResults.showTable();
			
			objTableResults.setColumnDoubleFractionDigits("comparation_result", 6);
			
			// ***********************************************************************************************************************
			// Cria a tela
			objWasisDialog = new WasisDialog(rsBundle.getString("screen_audio_comparison_hmm_screen_description"), true);
			objWasisDialog.setBounds(350, 350, 800, 550);
			objWasisDialog.setMinimumSize(new Dimension(800, 550));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][grow][][grow]"));
			objWasisDialog.getContentPane().add(txtDescription, "cell 0 0, grow, width 250");
			objWasisDialog.getContentPane().add(txtDateCreation, "cell 0 0, grow, width 50");
			objWasisDialog.getContentPane().add(btnDescription, "cell 0 0, gap 3 0 16 0");
			objWasisDialog.getContentPane().add(cmbAnimalClass, "cell 0 1, grow, width 270");
			objWasisDialog.getContentPane().add(cmbFeature, "cell 0 1, grow, width 400");
			objWasisDialog.getContentPane().add(cmbClassifier, "cell 0 1, grow, width 400");
			objWasisDialog.getContentPane().add(panelSelections, "cell 0 2, grow");
			objWasisDialog.getContentPane().add(panelSelections, "cell 0 2, grow");
			objWasisDialog.getContentPane().add(txtResult, "cell 0 3, grow");
			objWasisDialog.getContentPane().add(panelResults, "cell 0 4, grow, gap 0 0 5 0");
			
			panelSelections.add(objTableSelections, "cell 0 0, grow");
			
			panelResults.add(objTableResults, "cell 0 0, grow");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Carrega os dados das seleções.
	 */
	private void loadSelections() {
		List<AudioTemporarySegments> lstSelections = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments();
		
		for (int indexSelection = 0; indexSelection < lstSelections.size(); indexSelection++) {
			objTableSelections.addRow();
			objTableSelections.addRowData("sound_unit", lstSelections.get(indexSelection).getSoundUnit());
			objTableSelections.addRowData("time_initial", lstSelections.get(indexSelection).getInitialTime());
			objTableSelections.addRowData("time_final", lstSelections.get(indexSelection).getFinalTime());
			objTableSelections.addRowData("frequency_initial", lstSelections.get(indexSelection).getInitialFrequency());
			objTableSelections.addRowData("frequency_final", lstSelections.get(indexSelection).getFinalFrequency());
			objTableSelections.addRowData("time_initial_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSelections.get(indexSelection).getInitialTime()));
			objTableSelections.addRowData("time_final_show", ClockTransformations.millisecondsIntoDigitalFormat(lstSelections.get(indexSelection).getFinalTime()));
			objTableSelections.addRowData("frequency_initial_show", lstSelections.get(indexSelection).getInitialFrequency() + " Hz");
			objTableSelections.addRowData("frequency_final_show", lstSelections.get(indexSelection).getFinalFrequency() + " Hz");
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
		txtResult.setValue("");
		
		int intInitialTime = (int) objTableSelections.getValue(intIndexRow, "time_initial");
		int intFinalTime = (int) objTableSelections.getValue(intIndexRow, "time_final");
		//int intInitialFrequency = (int) objTableSelections.getValue(intIndexRow, "frequency_initial");
		//int intFinalFrequency = (int) objTableSelections.getValue(intIndexRow, "frequency_final");
		
		// Extração dos descritores
		double[][] featureVectors;
		
		int intInitialChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intInitialTime);
		int intFinalChunkToProcess = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getSampleFromTime(intFinalTime);
		
		double[] arrayAmplitudes = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAmplitudesChunk(1, intInitialChunkToProcess, intFinalChunkToProcess);
		
		if (cmbFeature.getValue().equals("MFCC")) {
			MFCC objMFCC = new MFCC();
			objMFCC.process(arrayAmplitudes, objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
			
			featureVectors = objMFCC.getMFCC();
			
		} else if (cmbFeature.getValue().equals("LPC")) {
			LPC objLPC = new LPC();
			objLPC.process(arrayAmplitudes);
			
			featureVectors = objLPC.getLPC();
			
		} else if (cmbFeature.getValue().equals("LPCC")) {
			LPC objLPCC = new LPC();
			objLPCC.process(arrayAmplitudes);
			objLPCC.processLPCC();
			
			featureVectors = objLPCC.getLPCC(); 
			
		} else if (cmbFeature.getValue().equals("PLP")) {
			PLP objPLP = new PLP();
			objPLP.process(arrayAmplitudes, objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
			
			featureVectors = objPLP.getPLP();
		
		} else {
			MFCC objMFCC = new MFCC();
			objMFCC.process(arrayAmplitudes, objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getWavHeader().getSampleRate());
			
			featureVectors = objMFCC.getMFCC();
		}
		
		String strExperimentName = cmbAnimalClass.getValue() + "-" + cmbFeature.getValue();
		
		// Quantize using Codebook
		Codebook objCodebook = new Codebook(strExperimentName);
		Points[] points = getPointsFromFeatureVector(featureVectors);
		int[] quantized = objCodebook.quantize(points);
		
		// Carrega os HMM modelos treinados
		DataBase objDB = new ObjectIODataBase();
		objDB.setType("hmm");
		
		String[] species = objDB.readRegistered(strExperimentName);
		HMM[] hmmModels = new HMM[species.length];
		
		for (int indexSpecies = 0; indexSpecies < species.length; indexSpecies++) {
			hmmModels[indexSpecies] = new HMM(strExperimentName, species[indexSpecies]);
		}
		
		// find the likelihood by viterbi decoding of quantized sequenc
		double likelihoods[] = new double[species.length];
		
		for (int indexSpecies = 0; indexSpecies < species.length; indexSpecies++) {
			likelihoods[indexSpecies] = hmmModels[indexSpecies].viterbi(quantized);
			
			objTableResults.addRow();
			objTableResults.addRowData("comparation_result", likelihoods[indexSpecies]);
			objTableResults.addRowData("animal_species", species[indexSpecies]);
			objTableResults.addRowData("animal_name_english", getAnimalName(species[indexSpecies]));
		}
		
		// find the largest likelihood
		double dblHighest = Double.NEGATIVE_INFINITY;
		int intIndex = -1;
		
		for (int j = 0; j < species.length; j++) {
			if (likelihoods[j] > dblHighest) {
				dblHighest = likelihoods[j];
				intIndex = j;
			}
		}
		
		txtResult.setValue(species[intIndex]);
		
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
		if (objTableSelections.getRowCount() > 0 && objTableSelections.getSelectedRow() >= 0) {
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

						compareAudiosFromDatabase(objTableSelections.getSelectedRow(), 0);
						
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
	 * 
	 * @param strGenusSpecies
	 */
	private String getAnimalName(String strGenusSpecies) {
		String strAnimalName = "";
		
		String[] splitGenusSpecies = strGenusSpecies.split("\\s+");
		
		DatabaseConnection objDbConnection = DatabaseConnection.getInstance();
		
		try {
			objDbConnection.openConnection();
			objDbConnection.initiliazeStatement();
			objDbConnection.sqlCommand("SELECT animal_name_english");
			objDbConnection.sqlCommandAppend("FROM audio_files");
			objDbConnection.sqlCommandAppend("WHERE LENGTH(animal_name_english) > 3");
			objDbConnection.sqlCommandAppend("AND animal_genus = ?");
			objDbConnection.sqlCommandAppend("AND animal_species = ?");
			objDbConnection.sqlCommandAppend("ORDER BY animal_name_english");
			objDbConnection.sqlCommandAppend("LIMIT 1");
			objDbConnection.addParameter("animal_genus", LTDataTypes.STRING, splitGenusSpecies[0]);
			objDbConnection.addParameter("animal_species", LTDataTypes.STRING, splitGenusSpecies[1]);
			
		    ResultSet rsRecord = objDbConnection.executeSelectQuery();
		    
		    while (rsRecord.next()) {
		    	strAnimalName = rsRecord.getString("animal_name_english");
		    }
		    
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDbConnection.rollBackTransaction();
			objDbConnection.closeConnection();
		}
		
		return strAnimalName;
	}
	
	/**
	 * Cria um <i>MouseAdapter</i> responsável 
	 * pela execução da comparação de áudios quando houver 
	 * clique em um registro.
	 */
	private class SelectionMouseAdapter extends MouseAdapter {
		@Override
	    public void mouseClicked(MouseEvent event) {
			if (event.getClickCount() == 1) {
				if (objTableSelections.getRowCount() > 0) {
					compareAudios();
				}
			}
	    }
	}
}