package br.unicamp.fnjv.wasis.graphics.spectrogram;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.audio.AudioSegments;
import br.unicamp.fnjv.wasis.audio.AudioSegmentsValues;
import br.unicamp.fnjv.wasis.audio.AudioTemporary;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que mostra a
 * lista de segmentos de áudio selecionados no espectrograma.<br>
 * <br>
 * É possível também excluir os segmentos de áudio.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 03/Out/2017
 */
public class ScreenSpectrogramAudioSegmentList extends JDialog {
	private static final long serialVersionUID = 731194796806678298L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private GraphicPanel objSpectrogramGraphicPanel;
	
	private WasisPanel panelAudioSegments;
	private LTTable objTableAudioSegments;
	
	private JButton btnDeleteAudioSegments;
	private JProgressBar progressBar;
	
	/**
	 * Classe responsável pela exibição de uma tela que mostra a
	 * lista de segmentos de áudio selecionados no espectrograma.<br>
	 * <br>
	 * É possível também excluir os segmentos de áudio.
	 * 
	 * @param objSpectrogramGraphicPanel
	 */
	protected ScreenSpectrogramAudioSegmentList(GraphicPanel objSpectrogramGraphicPanel) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;
		
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		LTTextField txtAudioFilePath = new LTTextField(rsBundle.getString("audio_file_path") + ":", LTDataTypes.STRING, false, false, 2000);
		txtAudioFilePath.setValue(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal());
		
		panelAudioSegments = new WasisPanel(rsBundle.getString("screen_spectrogram_audio_segment_list_audio_segment_list") + ":");
		panelAudioSegments.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		objTableAudioSegments = new LTTable(false, false);
		objTableAudioSegments.addColumn("selected", "", LTDataTypes.BOOLEAN, 30, true);
		objTableAudioSegments.addColumn("id_audio_segment", "ID", LTDataTypes.LONG, 0, false);
		objTableAudioSegments.addColumn("audio_segment", rsBundle.getString("audio_segment"), LTDataTypes.STRING, 110, false);
		objTableAudioSegments.addColumn("time_initial", rsBundle.getString("audio_segment_time_initial"), LTDataTypes.STRING, 135, false);
		objTableAudioSegments.addColumn("time_final", rsBundle.getString("audio_segment_time_final"), LTDataTypes.STRING, 135, false);
		objTableAudioSegments.addColumn("frequency_initial", rsBundle.getString("audio_segment_frequency_minimum"), LTDataTypes.STRING, 135, false);
		objTableAudioSegments.addColumn("frequency_final", rsBundle.getString("audio_segment_frequency_maximum"), LTDataTypes.STRING, 135, false);
		objTableAudioSegments.showTable();
		
		// ***********************************************************************************************************************
		// Botão Excluir Segmentos de Áudio
		btnDeleteAudioSegments = new JButton(rsBundle.getString("screen_spectrogram_audio_segment_list_delete_audio_segments"));
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
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_spectrogram_audio_segment_list_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 750, 400);
		objWasisDialog.setMinimumSize(new Dimension(750, 400));
		objWasisDialog.setMaximumSize(new Dimension(750, 400));
		objWasisDialog.setResizable(false);
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][]"));
		objWasisDialog.getContentPane().add(txtAudioFilePath, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelAudioSegments, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(btnDeleteAudioSegments, "cell 0 2");
		objWasisDialog.getContentPane().add(progressBar, "cell 0 2, grow");
		
		panelAudioSegments.add(objTableAudioSegments, "cell 0 0, grow");
		
		loadAudioSegmentList();
	}
	
	/**
	 * Carrega a lista de segmentos de áudio do espectrograma.
	 */
	private void loadAudioSegmentList() {
		objTableAudioSegments.deleteRows();
		
		List<AudioSegmentsValues> lstAudioSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).getAudioSegments();
		
		for (int indexAudioSegment = 0; indexAudioSegment < lstAudioSegments.size(); indexAudioSegment++) {
			objTableAudioSegments.addRow();
			objTableAudioSegments.addRowData("selected", false);
			objTableAudioSegments.addRowData("id_audio_segment", lstAudioSegments.get(indexAudioSegment).getIdDatabase());
			objTableAudioSegments.addRowData("audio_segment", lstAudioSegments.get(indexAudioSegment).getAudioSegment());
			objTableAudioSegments.addRowData("time_initial", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexAudioSegment).getInitialTime()));
			objTableAudioSegments.addRowData("time_final", ClockTransformations.millisecondsIntoDigitalFormat(lstAudioSegments.get(indexAudioSegment).getFinalTime()));
			objTableAudioSegments.addRowData("frequency_initial", lstAudioSegments.get(indexAudioSegment).getInitialFrequency() + " Hz");
			objTableAudioSegments.addRowData("frequency_final", lstAudioSegments.get(indexAudioSegment).getFinalFrequency() + " Hz");
		}
	}
	
	/**
	 * Exclui os segmentos de áudio do banco de dados.
	 */
	private void deleteAudioSegments() {
		// Verifica se existe algum segmento de áudio selecionado
		boolean blnSelectedAudioSegments = false;
		
		for (int indexAudioSegment = 0; indexAudioSegment < objTableAudioSegments.getRowCount(); indexAudioSegment++) {
			if ((boolean) objTableAudioSegments.getValue(indexAudioSegment, "selected")) {
				blnSelectedAudioSegments = true;
				
				break;
			}
		}
		
		if (blnSelectedAudioSegments) {
			int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("screen_spectrogram_audio_segment_list_confirm_deletion"), WasisMessageBox.YES_NO_OPTION);
			
			if (intDialogResult == WasisMessageBox.YES_OPTION) {
				SwingWorker<Void, Void> swingWorkerDelete = new SwingWorker<Void, Void>() {	
					@Override
					protected Void doInBackground() throws Exception {
						try {
							btnDeleteAudioSegments.setEnabled(false);
							progressBar.setVisible(true);
	
							// Só realiza a desativação/exclusão se os segmentos de áudio forem selecionados pelo usuário
							for (int indexSegment = 0; indexSegment < objTableAudioSegments.getRowCount(); indexSegment++) {
								if ((boolean) objTableAudioSegments.getValue(indexSegment, "selected")) {
									boolean blnDeleteAudio = true;
									
									if ((long) objTableAudioSegments.getValue(indexSegment, "id_audio_segment") != 0) {
										blnDeleteAudio = AudioSegments.deleteAudioSegment((long) objTableAudioSegments.getValue(indexSegment, "id_audio_segment"));
									}
									
									if (blnDeleteAudio) {
										AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioTemporaryIndex()).deleteAudioSegment(objTableAudioSegments.getValue(indexSegment, "audio_segment").toString());
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
							loadAudioSegmentList();
							
							objSpectrogramGraphicPanel.repaint();
							
							btnDeleteAudioSegments.setEnabled(true);
			            }
			        }
				};
	
				swingWorkerDelete.execute();
			}
			
		} else {
			WasisMessageBox.showMessageDialog(rsBundle.getString("screen_spectrogram_audio_segment_list_audio_segments_not_selected"), WasisMessageBox.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	protected void showScreen() {
		objWasisDialog.setVisible(true);
	}
}