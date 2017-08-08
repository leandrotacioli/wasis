package br.unicamp.fnjv.wasis.graphics.spectrogram;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
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

import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporary;
import br.unicamp.fnjv.wasis.audio.temporary.AudioTemporarySegments;
import br.unicamp.fnjv.wasis.database.DatabaseConnection;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que
 * mostra a lista de seleções feitas no espectrograma.<br>
 * <br>
 * É possível também excluir as seleções.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 02/Set/2015
 */
public class ScreenSpectrogramSelectionList extends JDialog {
	private static final long serialVersionUID = 731194796806678298L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private GraphicPanel objSpectrogramGraphicPanel;

	private WasisPanel panelSelection;
	private LTTable objTableSelection;
	
	private JButton btnDeleteSelections;
	private JProgressBar progressBar;
	
	/**
	 * Classe responsável pela exibição de uma tela que
	 * mostra a lista de seleções feitas no espectrograma.<br>
	 * <br>
	 * É possível também excluir as seleções.
	 * 
	 * @param objSpectrogramGraphicPanel
	 */
	protected ScreenSpectrogramSelectionList(GraphicPanel objSpectrogramGraphicPanel) {
		this.objSpectrogramGraphicPanel = objSpectrogramGraphicPanel;

		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// Cria os componentes da tela
		LTTextField txtAudioFilePath = new LTTextField(rsBundle.getString("screen_spectrogram_selection_list_audio_file_path"), LTDataTypes.STRING, false, false, 2000);
		txtAudioFilePath.setValue(objSpectrogramGraphicPanel.getSpectrogram().getAudioWav().getAudioFilePathOriginal());
		
		// ***********************************************************************************************************************
		// Selecões já gravadas no banco de dados
		panelSelection = new WasisPanel(rsBundle.getString("screen_spectrogram_selection_list_selection_list"));
		panelSelection.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		objTableSelection = new LTTable(false, false);
		objTableSelection.addColumn("marked", "", LTDataTypes.BOOLEAN, 30, true);
		objTableSelection.addColumn("id_selection", rsBundle.getString("audio_file_selection_id_selection"), LTDataTypes.INTEGER, 0, false);
		objTableSelection.addColumn("sound_unit", rsBundle.getString("audio_file_selection_sound_unit"), LTDataTypes.STRING, 120, false);
		objTableSelection.addColumn("time_initial", rsBundle.getString("audio_file_selection_time_initial"), LTDataTypes.STRING, 130, false);
		objTableSelection.addColumn("time_final", rsBundle.getString("audio_file_selection_time_final"), LTDataTypes.STRING, 130, false);
		objTableSelection.addColumn("frequency_initial", rsBundle.getString("audio_file_selection_frequency_minimum"), LTDataTypes.STRING, 130, false);
		objTableSelection.addColumn("frequency_final", rsBundle.getString("audio_file_selection_frequency_maximum"), LTDataTypes.STRING, 130, false);
		objTableSelection.showTable();
		
		// ***********************************************************************************************************************
		// Botão Excluir Seleções
		btnDeleteSelections = new JButton(rsBundle.getString("screen_spectrogram_selection_list_delete_selections"));
		btnDeleteSelections.setMinimumSize(new Dimension(250, 30));
		btnDeleteSelections.setMaximumSize(new Dimension(400, 30));
		btnDeleteSelections.setIconTextGap(15);
		btnDeleteSelections.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnDeleteSelections.setIcon(new ImageIcon("res/images/delete.png"));
		btnDeleteSelections.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				deleteSelections();
			}
		});

		// Barra de progresso
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setVisible(false);
		
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_spectrogram_selection_list_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 750, 500);
		objWasisDialog.setMinimumSize(new Dimension(750, 500));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][]"));
		objWasisDialog.getContentPane().add(txtAudioFilePath, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelSelection, "cell 0 1, grow");
		objWasisDialog.getContentPane().add(btnDeleteSelections, "cell 0 2");
		objWasisDialog.getContentPane().add(progressBar, "cell 0 2, grow");
		
		panelSelection.add(objTableSelection, "cell 0 0, grow");
		
		loadSelectionList();
	}
	
	/**
	 * Carrega a lista de seleções do espectrograma.
	 */
	private void loadSelectionList() {
		objTableSelection.deleteRows();
		
		List<AudioTemporarySegments> lstSegments = AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).getAudioTemporarySegments();
		
		for (int indexSegment = 0; indexSegment < lstSegments.size(); indexSegment++) {
			objTableSelection.addRow();
			objTableSelection.addRowData("marked", false);
			objTableSelection.addRowData("id_selection", lstSegments.get(indexSegment).getIdDatabase());
			objTableSelection.addRowData("sound_unit", lstSegments.get(indexSegment).getSoundUnit());
			objTableSelection.addRowData("time_initial", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getInitialTime()));
			objTableSelection.addRowData("time_final", ClockTransformations.millisecondsIntoDigitalFormat(lstSegments.get(indexSegment).getFinalTime()));
			objTableSelection.addRowData("frequency_initial", lstSegments.get(indexSegment).getInitialFrequency() + " Hz");
			objTableSelection.addRowData("frequency_final", lstSegments.get(indexSegment).getFinalFrequency() + " Hz");
		}
		
		//objTableSelection.orderColumnData("sound_unit", true);
	}
	
	/**
	 * Exclui as seleções marcadas do banco de dados.
	 */
	private void deleteSelections() {
		// Verifica se existe alguma seleção marcada
		boolean blnMarkedSelections = false;
		
		for (int indexSelection = 0; indexSelection < objTableSelection.getRowCount(); indexSelection++) {
			if ((boolean) objTableSelection.getValue(indexSelection, "marked")) {
				blnMarkedSelections = true;
				break;
			}
		}
		
		if (blnMarkedSelections) {
			int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("screen_spectrogram_selection_list_confirm_deletion"), WasisMessageBox.YES_NO_OPTION);
			
			if (intDialogResult == WasisMessageBox.YES_OPTION) {
				final DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
				
				SwingWorker<Void, Void> swingWorkerUpdate = new SwingWorker<Void, Void>() {
	
					@Override
					protected Void doInBackground() throws Exception {
						try {
							btnDeleteSelections.setEnabled(false);
							progressBar.setVisible(true);
	
							objDatabaseConnection.openConnection();
				
							for (int indexSelection = 0; indexSelection < objTableSelection.getRowCount(); indexSelection++) {
								
								// Só realiza a desativação/exclusão se as seleções forem marcadas pelo usuário
								if ((boolean) objTableSelection.getValue(indexSelection, "marked")) {
								
									// Para as seleções normais gravados no banco de dados - os registros são desativados
									// Somente os valores da tabela 'audio_files_selections_values' são excluídos
									if ((int) objTableSelection.getValue(indexSelection, "id_selection") != 0) {
										objDatabaseConnection.initiliazeStatement();
										objDatabaseConnection.sqlCommand("UPDATE audio_files_selections ");
										objDatabaseConnection.sqlCommandAppend("SET ind_active = ?, date_update = ? ");
										objDatabaseConnection.sqlCommandAppend("WHERE id = ? ");
										objDatabaseConnection.addParameter("ind_active", LTDataTypes.BOOLEAN, false);
										objDatabaseConnection.addParameter("date_update", LTDataTypes.DATE, new Date());
										objDatabaseConnection.addParameter("id", LTDataTypes.INTEGER, (int) objTableSelection.getValue(indexSelection, "id_selection"));
										objDatabaseConnection.executeQuery();
										
										objDatabaseConnection.initiliazeStatement();
										objDatabaseConnection.sqlCommand("DELETE FROM audio_files_selections_values ");
										objDatabaseConnection.sqlCommandAppend("WHERE fk_audio_file_selection = ? ");
										objDatabaseConnection.addParameter("fk_audio_file_selection", LTDataTypes.INTEGER, (int) objTableSelection.getValue(indexSelection, "id_selection"));
										objDatabaseConnection.executeQuery();
									}
									
									AudioTemporary.getAudioTemporary().get(objSpectrogramGraphicPanel.getSpectrogram().getAudioTemporaryIndex()).deleteAudioTemporarySelection(objTableSelection.getValue(indexSelection, "sound_unit").toString());
								}
							}
							
							objDatabaseConnection.commitTransaction();
					
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
			            	objDatabaseConnection.rollBackTransaction();
							objDatabaseConnection.closeConnection();
							
							loadSelectionList();
							
							objSpectrogramGraphicPanel.repaint();
							
							btnDeleteSelections.setEnabled(true);
			            }
			        }
				};
	
				swingWorkerUpdate.execute();
			}
			
		} else {
			WasisMessageBox.showMessageDialog(rsBundle.getString("screen_spectrogram_selection_list_selections_not_marked"), WasisMessageBox.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	protected void showScreen() {
		objWasisDialog.setVisible(true);
	}
}