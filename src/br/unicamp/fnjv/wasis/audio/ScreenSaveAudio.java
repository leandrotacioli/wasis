package br.unicamp.fnjv.wasis.audio;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporary;
import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporarySegments;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que mostra uma lista de segmentos (ROIs) efetuados no espectrograma
 * que ainda não foram gravadas no banco de dados, e outra lista de segmentos (ROIs) que já foram gravadas no banco de dados.
 * A partir desses segmentos é possível inserir e/ou atualizar dados no banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 3.1 - 08/Mai/2017
 */
public class ScreenSaveAudio {
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private GraphicPanel objSpectrogramGraphicPanel;
	
	private String strAudioFilePath;

	private WasisPanel panelSegmentsNotSaved;
	private LTTable objTableSegmentsNotSaved;
	
	private WasisPanel panelSegmentsAlreadySaved;
	private LTTable objTableSegmentsAlreadySaved;
	
	/**
	 * Classe responsável pela exibição de uma tela que
	 * mostra uma lista de seleções efetuadas no espectrograma
	 * que ainda não foram gravadas no banco de dados, e outra
	 * lista de seleções que já foram gravadas no banco de dados.
	 * A partir dessas seleções é possível inserir e/ou atualizar
	 * dados no banco de dados.
 	 * 
	 * @param objSpectrogramGraphicPanel
	 */
	public ScreenSaveAudio(GraphicPanel objSpectrogramGraphicPanel) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;
		
		this.strAudioFilePath = objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal();
		
		loadScreen();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// Cria os componentes da tela
		LTTextField txtAudioFilePath = new LTTextField(rsBundle.getString("screen_save_audio_file_path"), LTDataTypes.STRING, false, false, 500);
		txtAudioFilePath.setValue(strAudioFilePath);
		
		// ***********************************************************************************************************************
		// Selecões ainda não gravadas no banco de dados
		panelSegmentsNotSaved = new WasisPanel(rsBundle.getString("screen_save_audio_selections_not_saved"));
		panelSegmentsNotSaved.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		objTableSegmentsNotSaved = new LTTable(false, false);
		objTableSegmentsNotSaved.addColumn("marked", "", LTDataTypes.BOOLEAN, 30, true);
		objTableSegmentsNotSaved.addColumn("index_temporary", "", LTDataTypes.INTEGER, 0, false);
		objTableSegmentsNotSaved.addColumn("sound_unit", rsBundle.getString("audio_file_selection_sound_unit"), LTDataTypes.STRING, 90, false);
		objTableSegmentsNotSaved.addColumn("time_initial", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.STRING, 150, false);
		objTableSegmentsNotSaved.addColumn("time_final", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.STRING, 150, false);
		objTableSegmentsNotSaved.addColumn("frequency_initial", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.STRING, 150, false);
		objTableSegmentsNotSaved.addColumn("frequency_final", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.STRING, 150, false);
		objTableSegmentsNotSaved.showTable();
		
		// ***********************************************************************************************************************
		// Botões
		JButton btnSaveAudioData = new JButton(rsBundle.getString("screen_save_audio_save_audio_data"));
		btnSaveAudioData.setMinimumSize(new Dimension(250, 30));
		btnSaveAudioData.setMaximumSize(new Dimension(400, 30));
		btnSaveAudioData.setIconTextGap(15);
		btnSaveAudioData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSaveAudioData.setIcon(new ImageIcon("res/images/save.png"));
		btnSaveAudioData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelSegmentsNotSaved.setComponentEnabled(false);
				
				saveAudioData();
				
				panelSegmentsNotSaved.setComponentEnabled(true);
			}
		});
		
		// ***********************************************************************************************************************
		// Selecões já gravadas no banco de dados
		panelSegmentsAlreadySaved = new WasisPanel(rsBundle.getString("screen_save_audio_selections_already_saved"));
		panelSegmentsAlreadySaved.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		objTableSegmentsAlreadySaved = new LTTable(true);
		//objTableSegmentsAlreadySaved.addColumn("marked", "", LTDataTypes.BOOLEAN, 30, true);
		objTableSegmentsAlreadySaved.addColumn("index_temporary", "", LTDataTypes.INTEGER, 0, false);
		objTableSegmentsAlreadySaved.addColumn("sound_unit", rsBundle.getString("audio_file_selection_sound_unit"), LTDataTypes.STRING, 90, false);
		objTableSegmentsAlreadySaved.addColumn("animal_genus", rsBundle.getString("animal_genus"), LTDataTypes.STRING, 100, false);
		objTableSegmentsAlreadySaved.addColumn("animal_species", rsBundle.getString("animal_species"), LTDataTypes.STRING, 100, false);
		objTableSegmentsAlreadySaved.addColumn("time_initial", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.STRING, 100, false);
		objTableSegmentsAlreadySaved.addColumn("time_final", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.STRING, 100, false);
		objTableSegmentsAlreadySaved.addColumn("frequency_initial", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.STRING, 125, false);
		objTableSegmentsAlreadySaved.addColumn("frequency_final", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.STRING, 125, false);
		objTableSegmentsAlreadySaved.showTable();
		
		// ***********************************************************************************************************************
		// Botão Update
		JButton btnUpdateAudioData = new JButton(rsBundle.getString("screen_save_audio_update_audio_data"));
		btnUpdateAudioData.setMinimumSize(new Dimension(250, 30));
		btnUpdateAudioData.setMaximumSize(new Dimension(400, 30));
		btnUpdateAudioData.setIconTextGap(15);
		btnUpdateAudioData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnUpdateAudioData.setIcon(new ImageIcon("res/images/save.png"));
		btnUpdateAudioData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				//saveRecordingData();
			}
		});
		
		loadAudioSegments();
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_save_audio_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 800, 450);
		objWasisDialog.setMinimumSize(new Dimension(800, 450));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][] 5.00 []"));
		objWasisDialog.getContentPane().add(txtAudioFilePath, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelSegmentsNotSaved, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(btnSaveAudioData, "cell 0 2");
		objWasisDialog.getContentPane().add(panelSegmentsAlreadySaved, "cell 0 3, grow");
		//objWasisDialog.getContentPane().add(btnUpdateAudioData, "cell 0 4");
		
		panelSegmentsNotSaved.add(objTableSegmentsNotSaved, "cell 0 0, grow");
		
		panelSegmentsAlreadySaved.add(objTableSegmentsAlreadySaved, "cell 0 0, grow");
	}
	
	/**
	 * Salva os dados do arquivo de áudio.
	 */
	private void saveAudioData() {
		List<Integer> lstSegmentsMarked = new ArrayList<Integer>();
		
		// Verifica se o registro está selecionado para gravação
		for (int indexSegmentNotSaved = 0; indexSegmentNotSaved < objTableSegmentsNotSaved.getRowCount(); indexSegmentNotSaved++) {
			if ((boolean) objTableSegmentsNotSaved.getValue(indexSegmentNotSaved, "marked")) {
				lstSegmentsMarked.add((int) objTableSegmentsNotSaved.getValue(indexSegmentNotSaved, "index_temporary"));
			}
		}
		
		// Habilita tela de gravação de dados apenas se alguma seleção estiver marcada
		if (lstSegmentsMarked.size() > 0) {
			ScreenSaveAudioData objSaveAudioData = new ScreenSaveAudioData(objSpectrogramGraphicPanel, lstSegmentsMarked);
			objSaveAudioData.showScreen();
		} else {
			WasisMessageBox.showMessageDialog(rsBundle.getString("screen_save_audio_message_no_data_selected_to_be_saved"), WasisMessageBox.WARNING_MESSAGE);
		}
		
		loadAudioSegments();
	}

	/**
	 * Carrega os segmentos (ROIs) do arquivo de áudio.
	 */
	private void loadAudioSegments() {
		List<AudioTemporarySegments> lstSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments();
		
		objTableSegmentsNotSaved.deleteRows();
		objTableSegmentsAlreadySaved.deleteRows();
		
		for (int indexSegment = 0; indexSegment < lstSegments.size(); indexSegment++) {
			
			// Seleções temporárias
			if (lstSegments.get(indexSegment).getIdDatabase() == 0) {
				objTableSegmentsNotSaved.addRow();
				objTableSegmentsNotSaved.addRowData("marked", true);
				objTableSegmentsNotSaved.addRowData("index_temporary", indexSegment);
				objTableSegmentsNotSaved.addRowData("sound_unit", lstSegments.get(indexSegment).getSoundUnit());
				objTableSegmentsNotSaved.addRowData("time_initial", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getInitialTime()));
				objTableSegmentsNotSaved.addRowData("time_final", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getFinalTime()));
				objTableSegmentsNotSaved.addRowData("frequency_initial", lstSegments.get(indexSegment).getInitialFrequency() + " Hz");
				objTableSegmentsNotSaved.addRowData("frequency_final", lstSegments.get(indexSegment).getFinalFrequency() + " Hz");
			
			// Seleções já gravadas no banco de dados
			} else {
				objTableSegmentsAlreadySaved.addRow();
				objTableSegmentsAlreadySaved.addRowData("marked", true);
				objTableSegmentsAlreadySaved.addRowData("index_temporary", indexSegment);
				objTableSegmentsAlreadySaved.addRowData("sound_unit", lstSegments.get(indexSegment).getSoundUnit());
				objTableSegmentsAlreadySaved.addRowData("animal_genus", lstSegments.get(indexSegment).getAnimalGenus());
				objTableSegmentsAlreadySaved.addRowData("animal_species", lstSegments.get(indexSegment).getAnimalSpecies());
				objTableSegmentsAlreadySaved.addRowData("time_initial", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getInitialTime()));
				objTableSegmentsAlreadySaved.addRowData("time_final", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getFinalTime()));
				objTableSegmentsAlreadySaved.addRowData("frequency_initial", lstSegments.get(indexSegment).getInitialFrequency() + " Hz");
				objTableSegmentsAlreadySaved.addRowData("frequency_final", lstSegments.get(indexSegment).getFinalFrequency() + " Hz");
			}
		}
		
		objTableSegmentsNotSaved.orderColumnData("sound_unit", true);
		objTableSegmentsAlreadySaved.orderColumnData("sound_unit", true);
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
}