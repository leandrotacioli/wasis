package br.unicamp.fnjv.wasis.audio;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTabbedPane;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que mostra uma lista de segmentos (ROIs) efetuados no espectrograma
 * que ainda não foram gravadas no banco de dados, e outra lista de segmentos (ROIs) que já foram gravadas no banco de dados.
 * A partir desses segmentos é possível inserir/atualizar/excluir dados no banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 4.0 - 27/Set/2017
 */
public class ScreenSaveAudio {
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private GraphicPanel objSpectrogramGraphicPanel;
	
	private LTTextField txtAudioFilePath;
	
	private JTabbedPane panelTabs;
	private WasisPanel panelAudioSegmentsNotSaved;
	private WasisPanel panelAudioSegmentsAlreadySaved;
	
	private LTTable objTableAudioSegmentsNotSaved;
	private LTTable objTableAudioSegmentsAlreadySaved;
	
	private JButton btnSaveAudioData;
	private JButton btnUpdateAudioData;
	private JButton btnDeleteAudioSegments;
	
	private JProgressBar progressBar;
	
	/**
	 * Classe responsável pela exibição de uma tela que mostra uma lista de segmentos (ROIs) efetuados no espectrograma
	 * que ainda não foram gravadas no banco de dados, e outra lista de segmentos (ROIs) que já foram gravadas no banco de dados.
	 * A partir desses segmentos é possível inserir/atualizar/excluir dados no banco de dados.
 	 * 
	 * @param objSpectrogramGraphicPanel
	 */
	public ScreenSaveAudio(GraphicPanel objSpectrogramGraphicPanel) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;
		
		loadScreen();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		txtAudioFilePath = new LTTextField(rsBundle.getString("audio_file_path") + ":", LTDataTypes.STRING, false, false, 500);
		txtAudioFilePath.setValue(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal());
		
		// ******************************************************************************************
	    // Painel Abas
	    panelTabs = new JTabbedPane();
	    panelTabs.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
	    panelTabs.setFont(new Font("Tahoma", Font.PLAIN, 15));
	    panelTabs.setFocusable(false);
		
		// ***********************************************************************************************************************
		// Segmentos de áudio ainda não gravados no banco de dados
	    panelAudioSegmentsNotSaved = new WasisPanel();
	    panelAudioSegmentsNotSaved.setLayout(new MigLayout("insets 7 4 7 4", "[grow]", "[grow]"));
	    panelAudioSegmentsNotSaved.setBorder(BorderFactory.createLineBorder(panelAudioSegmentsNotSaved.getBackground()));
	    
		objTableAudioSegmentsNotSaved = new LTTable(false, false);
		objTableAudioSegmentsNotSaved.addColumn("selected", "", LTDataTypes.BOOLEAN, 30, true);
		objTableAudioSegmentsNotSaved.addColumn("index_temporary", "", LTDataTypes.INTEGER, 0, false);
		objTableAudioSegmentsNotSaved.addColumn("audio_segment", rsBundle.getString("audio_segment"), LTDataTypes.STRING, 120, false);
		objTableAudioSegmentsNotSaved.addColumn("time_initial", rsBundle.getString("audio_segment_time_initial"), LTDataTypes.STRING, 145, false);
		objTableAudioSegmentsNotSaved.addColumn("time_final", rsBundle.getString("audio_segment_time_final"), LTDataTypes.STRING, 145, false);
		objTableAudioSegmentsNotSaved.addColumn("frequency_initial", rsBundle.getString("audio_segment_frequency_minimum"), LTDataTypes.STRING, 145, false);
		objTableAudioSegmentsNotSaved.addColumn("frequency_final", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.STRING, 145, false);
		objTableAudioSegmentsNotSaved.showTable();
		
		// ***********************************************************************************************************************
		// Botões
		btnSaveAudioData = new JButton(rsBundle.getString("screen_save_audio_save_audio_data"));
		btnSaveAudioData.setFocusable(false);
		btnSaveAudioData.setMinimumSize(new Dimension(250, 30));
		btnSaveAudioData.setMaximumSize(new Dimension(400, 30));
		btnSaveAudioData.setIconTextGap(15);
		btnSaveAudioData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnSaveAudioData.setIcon(new ImageIcon("res/images/save.png"));
		btnSaveAudioData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelAudioSegmentsNotSaved.setComponentEnabled(false);
				
				saveAudioData();
				
				panelAudioSegmentsNotSaved.setComponentEnabled(true);
			}
		});
		
		// ***********************************************************************************************************************
		// Selecões já gravadas no banco de dados
		panelAudioSegmentsAlreadySaved = new WasisPanel();
		panelAudioSegmentsAlreadySaved.setLayout(new MigLayout("insets 7 4 7 4", "[grow]", "[grow]"));
		panelAudioSegmentsAlreadySaved.setBorder(BorderFactory.createLineBorder(panelAudioSegmentsAlreadySaved.getBackground()));
		
		objTableAudioSegmentsAlreadySaved = new LTTable(false, false);
		objTableAudioSegmentsAlreadySaved.addColumn("selected", "", LTDataTypes.BOOLEAN, 30, true);
		objTableAudioSegmentsAlreadySaved.addColumn("index_temporary", "", LTDataTypes.INTEGER, 0, false);
		objTableAudioSegmentsAlreadySaved.addColumn("id_audio_segment", "ID", LTDataTypes.LONG, 0, false);
		objTableAudioSegmentsAlreadySaved.addColumn("audio_segment", rsBundle.getString("audio_segment"), LTDataTypes.STRING, 120, false);
		objTableAudioSegmentsAlreadySaved.addColumn("animal_genus", rsBundle.getString("animal_genus"), LTDataTypes.STRING, 120, false);
		objTableAudioSegmentsAlreadySaved.addColumn("animal_species", rsBundle.getString("animal_species"), LTDataTypes.STRING, 120, false);
		objTableAudioSegmentsAlreadySaved.addColumn("time_initial", rsBundle.getString("audio_segment_time_initial"), LTDataTypes.STRING, 120, false);
		objTableAudioSegmentsAlreadySaved.addColumn("time_final", rsBundle.getString("audio_segment_time_final"), LTDataTypes.STRING, 120, false);
		objTableAudioSegmentsAlreadySaved.addColumn("frequency_initial", rsBundle.getString("audio_segment_frequency_minimum"), LTDataTypes.STRING, 140, false);
		objTableAudioSegmentsAlreadySaved.addColumn("frequency_final", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.STRING, 140, false);
		objTableAudioSegmentsAlreadySaved.showTable();
		
		// ***********************************************************************************************************************
		// Botão Atualizar Dados
		btnUpdateAudioData = new JButton(rsBundle.getString("screen_save_audio_update_audio_data"));
		btnUpdateAudioData.setFocusable(false);
		btnUpdateAudioData.setMinimumSize(new Dimension(250, 30));
		btnUpdateAudioData.setMaximumSize(new Dimension(400, 30));
		btnUpdateAudioData.setIconTextGap(15);
		btnUpdateAudioData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnUpdateAudioData.setIcon(new ImageIcon("res/images/save.png"));
		btnUpdateAudioData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				panelAudioSegmentsNotSaved.setComponentEnabled(false);
				
				updateAudioData();
				
				panelAudioSegmentsNotSaved.setComponentEnabled(true);
			}
		});
		
		// ***********************************************************************************************************************
		// Botão Excluir Segmentos de Áudio
		btnDeleteAudioSegments = new JButton(rsBundle.getString("screen_save_audio_delete_audio_segments"));
		btnDeleteAudioSegments.setFocusable(false);
		btnDeleteAudioSegments.setMinimumSize(new Dimension(250, 30));
		btnDeleteAudioSegments.setMaximumSize(new Dimension(400, 30));
		btnDeleteAudioSegments.setIconTextGap(15);
		btnDeleteAudioSegments.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDeleteAudioSegments.setIcon(new ImageIcon("res/images/delete.png"));
		btnDeleteAudioSegments.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				deleteAudioSegments();
			}
		});
		
		// Barra de progresso
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		
		loadAudioSegments();
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_save_audio_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 800, 450);
		objWasisDialog.setMinimumSize(new Dimension(800, 450));
		objWasisDialog.setMaximumSize(new Dimension(800, 450));
		objWasisDialog.setResizable(false);
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[grow]"));
		objWasisDialog.getContentPane().add(txtAudioFilePath, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelTabs, "cell 0 1, grow");
		
		// ******************************************************************************************
		// Insere abas no painel principal
		panelTabs.addTab(" " + rsBundle.getString("screen_save_audio_segments_not_saved") + " ", panelAudioSegmentsNotSaved);
		panelTabs.addTab(" " + rsBundle.getString("screen_save_audio_segments_already_saved") + " ", panelAudioSegmentsAlreadySaved);
		
		panelAudioSegmentsNotSaved.add(objTableAudioSegmentsNotSaved, "cell 0 0, grow");
		panelAudioSegmentsNotSaved.add(btnSaveAudioData, "cell 0 1, width 300");
		
		panelAudioSegmentsAlreadySaved.add(objTableAudioSegmentsAlreadySaved, "cell 0 0, grow");
		panelAudioSegmentsAlreadySaved.add(btnUpdateAudioData, "cell 0 1, width 300");
		panelAudioSegmentsAlreadySaved.add(btnDeleteAudioSegments, "cell 0 1, width 300");
		panelAudioSegmentsAlreadySaved.add(progressBar, "cell 0 1, grow");
	}
	
	/**
	 * Salva os dados de novos segmentos de áudio.
	 */
	private void saveAudioData() {
		List<Integer> lstAudioSegments = new ArrayList<Integer>();
		
		// Verifica se o registro está selecionado para gravação
		for (int indexSegmentNotSaved = 0; indexSegmentNotSaved < objTableAudioSegmentsNotSaved.getRowCount(); indexSegmentNotSaved++) {
			if ((boolean) objTableAudioSegmentsNotSaved.getValue(indexSegmentNotSaved, "selected")) {
				lstAudioSegments.add((int) objTableAudioSegmentsNotSaved.getValue(indexSegmentNotSaved, "index_temporary"));
			}
		}
		
		// Habilita tela de gravação de dados apenas se alguma seleção estiver marcada
		if (lstAudioSegments.size() > 0) {
			ScreenSaveAudioData objSaveAudioData = new ScreenSaveAudioData(objSpectrogramGraphicPanel, lstAudioSegments);
			objSaveAudioData.showScreen();
		} else {
			WasisMessageBox.showMessageDialog(rsBundle.getString("screen_save_audio_message_no_data_selected_to_be_saved"), WasisMessageBox.WARNING_MESSAGE);
		}
		
		loadAudioSegments();
	}
	
	/**
	 * Atualiza os dados de segmentos de áudio já existente no banco de dados.
	 */
	private void updateAudioData() {
		List<Integer> lstAudioSegments = new ArrayList<Integer>();
		
		// Verifica se o registro está selecionado para gravação
		for (int indexSegmentAlreadySaved = 0; indexSegmentAlreadySaved < objTableAudioSegmentsAlreadySaved.getRowCount(); indexSegmentAlreadySaved++) {
			if ((boolean) objTableAudioSegmentsAlreadySaved.getValue(indexSegmentAlreadySaved, "selected")) {
				lstAudioSegments.add((int) objTableAudioSegmentsAlreadySaved.getValue(indexSegmentAlreadySaved, "index_temporary"));
			}
		}
		
		// Habilita tela de gravação de dados apenas se alguma seleção estiver marcada
		if (lstAudioSegments.size() > 0) {
			ScreenSaveAudioData objSaveAudioData = new ScreenSaveAudioData(objSpectrogramGraphicPanel, lstAudioSegments);
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
		List<AudioSegmentsValues> lstAudioSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).getAudioSegments();
		
		objTableAudioSegmentsNotSaved.deleteRows();
		objTableAudioSegmentsAlreadySaved.deleteRows();
		
		for (int indexAudioSegment = 0; indexAudioSegment < lstAudioSegments.size(); indexAudioSegment++) {
			// Segmentos de áudio temporários
			if (lstAudioSegments.get(indexAudioSegment).getIdDatabase() == 0) {
				objTableAudioSegmentsNotSaved.addRow();
				objTableAudioSegmentsNotSaved.addRowData("selected", true);
				objTableAudioSegmentsNotSaved.addRowData("index_temporary", indexAudioSegment);
				objTableAudioSegmentsNotSaved.addRowData("audio_segment", lstAudioSegments.get(indexAudioSegment).getAudioSegment());
				objTableAudioSegmentsNotSaved.addRowData("time_initial", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexAudioSegment).getInitialTime()));
				objTableAudioSegmentsNotSaved.addRowData("time_final", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexAudioSegment).getFinalTime()));
				objTableAudioSegmentsNotSaved.addRowData("frequency_initial", lstAudioSegments.get(indexAudioSegment).getInitialFrequency() + " Hz");
				objTableAudioSegmentsNotSaved.addRowData("frequency_final", lstAudioSegments.get(indexAudioSegment).getFinalFrequency() + " Hz");
			
			// Segmentos de áudio já gravados no banco de dados
			} else {
				objTableAudioSegmentsAlreadySaved.addRow();
				objTableAudioSegmentsAlreadySaved.addRowData("selected", false);
				objTableAudioSegmentsAlreadySaved.addRowData("index_temporary", indexAudioSegment);
				objTableAudioSegmentsAlreadySaved.addRowData("id_audio_segment", lstAudioSegments.get(indexAudioSegment).getIdDatabase());
				objTableAudioSegmentsAlreadySaved.addRowData("audio_segment", lstAudioSegments.get(indexAudioSegment).getAudioSegment());
				objTableAudioSegmentsAlreadySaved.addRowData("animal_genus", lstAudioSegments.get(indexAudioSegment).getAnimalGenus());
				objTableAudioSegmentsAlreadySaved.addRowData("animal_species", lstAudioSegments.get(indexAudioSegment).getAnimalSpecies());
				objTableAudioSegmentsAlreadySaved.addRowData("time_initial", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexAudioSegment).getInitialTime()));
				objTableAudioSegmentsAlreadySaved.addRowData("time_final", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexAudioSegment).getFinalTime()));
				objTableAudioSegmentsAlreadySaved.addRowData("frequency_initial", lstAudioSegments.get(indexAudioSegment).getInitialFrequency() + " Hz");
				objTableAudioSegmentsAlreadySaved.addRowData("frequency_final", lstAudioSegments.get(indexAudioSegment).getFinalFrequency() + " Hz");
			}
		}
	}
	
	/**
	 * Exclui os segmentos de áudio do banco de dados.
	 */
	private void deleteAudioSegments() {
		// Verifica se existe algum segmento de áudio selecionado
		boolean blnSelectedAudioSegments = false;
		
		for (int indexAudioSegment = 0; indexAudioSegment < objTableAudioSegmentsAlreadySaved.getRowCount(); indexAudioSegment++) {
			if ((boolean) objTableAudioSegmentsAlreadySaved.getValue(indexAudioSegment, "selected")) {
				blnSelectedAudioSegments = true;
				
				break;
			}
		}
		
		if (blnSelectedAudioSegments) {
			int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("screen_save_audio_audio_segment_list_confirm_deletion"), WasisMessageBox.YES_NO_OPTION);
			
			if (intDialogResult == WasisMessageBox.YES_OPTION) {
				SwingWorker<Void, Void> swingWorkerDelete = new SwingWorker<Void, Void>() {	
					@Override
					protected Void doInBackground() throws Exception {
						try {
							btnSaveAudioData.setEnabled(false);
							btnUpdateAudioData.setEnabled(false);
							btnDeleteAudioSegments.setEnabled(false);
							progressBar.setVisible(true);
	
							// Só realiza a desativação/exclusão se os segmentos de áudio forem selecionados pelo usuário
							for (int indexSegment = 0; indexSegment < objTableAudioSegmentsAlreadySaved.getRowCount(); indexSegment++) {
								if ((boolean) objTableAudioSegmentsAlreadySaved.getValue(indexSegment, "selected")) {
									boolean blnDeleteAudio = true;
									
									if ((long) objTableAudioSegmentsAlreadySaved.getValue(indexSegment, "id_audio_segment") != 0) {
										blnDeleteAudio = AudioSegments.deleteAudioSegment((long) objTableAudioSegmentsAlreadySaved.getValue(indexSegment, "id_audio_segment"));
									}
									
									if (blnDeleteAudio) {
										AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).deleteAudioSegment(objTableAudioSegmentsAlreadySaved.getValue(indexSegment, "audio_segment").toString());
									}
								}
							}
					
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
			            	
			            	WasisMessageBox.showMessageDialog(rsBundle.getString("error_deleting_data"), WasisMessageBox.ERROR_MESSAGE);
			            	
			            } finally {
			            	loadAudioSegments();
							
							objSpectrogramGraphicPanel.repaint();
							
							btnSaveAudioData.setEnabled(true);
							btnUpdateAudioData.setEnabled(true);
							btnDeleteAudioSegments.setEnabled(true);
			            }
			        }
				};
	
				swingWorkerDelete.execute();
			}
			
		} else {
			WasisMessageBox.showMessageDialog(rsBundle.getString("screen_save_audio_audio_segment_list_audio_segments_not_selected"), WasisMessageBox.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
}