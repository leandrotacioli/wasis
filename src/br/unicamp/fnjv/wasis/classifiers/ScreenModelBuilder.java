package br.unicamp.fnjv.wasis.classifiers;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingWorker;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.comboboxfield.LTComboBoxField;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Codebook;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.CodebookDictionary;
import br.unicamp.fnjv.wasis.classifiers.hmm.vq.Points;
import br.unicamp.fnjv.wasis.database.jdbc.DatabaseConnection;
import br.unicamp.fnjv.wasis.features.Features;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisContainer;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

import net.miginfocom.swing.MigLayout;

/**
 * Construtor de Modelos de Classificação.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 02/Abr/2018
 */
public class ScreenModelBuilder extends JDialog {
	private static final long serialVersionUID = -4821611738366154316L;

	private static ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private JPanel panelMain;
	private LTTextField txtDescription;
	private LTTextField txtDateCreation;
	private LTComboBoxField cboAnimalClass;
	
	private WasisPanel panelFeatures;
	private LTTable objTableFeatures;
	
	private WasisPanel panelClassifiers;
	private LTTable objTableClassifiers;
	
	private JButton btnBuildModels;
	private JButton btnCancelOperation;
	
	private static JProgressBar progressBar;
	private static float fltTotalProgress;
	private static int intTotalRecordsToProcess;
	private static int intTotalRecordsProcessed;
	private static String strCurrentFeatureInProcess;
	protected static boolean blnCancelOperation;
	
	private boolean blnHasEnoughTrainingPoints;
	
	private long lgnIdModelClass;
	private int intTotalFeaturesSelected;
	
	/**
	 * Construtor de Modelos de Classificação.
	 */
	public ScreenModelBuilder() {
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			panelMain = new JPanel();
		    panelMain.setLayout(new MigLayout("insets 1", "[grow]", "[][grow]"));
		    panelMain.setBackground(WasisParameters.COLOR_BACKGROUND);
		    
			txtDescription = new LTTextField(rsBundle.getString("screen_model_builder_screen_model_descripton"), LTDataTypes.STRING, true, true, 50);
			txtDateCreation = new LTTextField(rsBundle.getString("screen_model_builder_screen_model_date"), LTDataTypes.DATE, true, true);
			
			// Deve retornar dados dos banco de dados
			cboAnimalClass = new LTComboBoxField(rsBundle.getString("screen_model_builder_screen_animal_class"), true, true);
			
			panelFeatures = new WasisPanel();
			panelFeatures.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableFeatures = new LTTable(false);
			objTableFeatures.addColumn("selected", "", LTDataTypes.BOOLEAN, 30, true);
			objTableFeatures.addColumn("feature", rsBundle.getString("feature"), LTDataTypes.STRING, 0, false);
			objTableFeatures.addColumn("feature_description", rsBundle.getString("feature"), LTDataTypes.STRING, 300, false);
			objTableFeatures.showTable();
			
			panelClassifiers = new WasisPanel();
			panelClassifiers.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableClassifiers = new LTTable(true);
			objTableClassifiers.addColumn("selected", "", LTDataTypes.BOOLEAN, 30, true);
			objTableClassifiers.addColumn("classifier", rsBundle.getString("classifier"), LTDataTypes.STRING, 300, false);
			objTableClassifiers.showTable();
			
			loadClasses();
			loadFeatures();
			loadClassifiers();
			
			// Botão Atualizar Coleção
			btnBuildModels = new JButton(rsBundle.getString("screen_model_builder_screen_button_build_models"));
			btnBuildModels.setIconTextGap(15);
			btnBuildModels.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnBuildModels.setIcon(new ImageIcon("res/images/build_models.png"));
			btnBuildModels.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					if (txtDescription.getValue().equals("") || txtDateCreation.getValue().equals("") || cboAnimalClass.getValue().equals("")) {
						WasisMessageBox.showMessageDialog(rsBundle.getString("mandatory_fields"), WasisMessageBox.WARNING_MESSAGE);
						txtDescription.requestFocusInWindow();
					} else {
						buildModels();
					}
				}
			});
			
			// Botão Cancelar Operação
			btnCancelOperation = new JButton(rsBundle.getString("screen_model_builder_screen_button_cancel_operation"));
			btnCancelOperation.setIconTextGap(15);
			btnCancelOperation.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnCancelOperation.setIcon(new ImageIcon("res/images/cancel.png"));
			btnCancelOperation.setEnabled(false);
			btnCancelOperation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("screen_model_builder_screen_cancel_operation"), WasisMessageBox.YES_NO_OPTION);
					
					// Fecha sistema
					if (intDialogResult == WasisMessageBox.YES_OPTION) {
						blnCancelOperation = true;
						btnCancelOperation.setEnabled(false);
					}
				}
			});
			
			progressBar = new JProgressBar(0, 100);
			progressBar.setFont(new Font("Tahoma", Font.BOLD, 16));
			progressBar.setStringPainted(true);
			progressBar.setString("");
	
			// ***********************************************************************************************************************
			// Cria a tela
			objWasisDialog = new WasisDialog(rsBundle.getString("screen_model_builder_screen_description"), true);
			objWasisDialog.setBounds(350, 350, 800, 425);
			objWasisDialog.setMinimumSize(new Dimension(800, 425));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5", "[grow]", "[][][]"));
			objWasisDialog.getContentPane().add(panelMain, "cell 0 0, grow, width 400");
			objWasisDialog.getContentPane().add(btnBuildModels, "cell 0 1, grow, width 400");
			objWasisDialog.getContentPane().add(btnCancelOperation, "cell 0 1, grow, width 400");
			objWasisDialog.getContentPane().add(progressBar, "cell 0 2, grow");
			
			panelMain.add(txtDescription, "cell 0 0, grow, width 300");
			panelMain.add(txtDateCreation, "cell 0 0, grow, width 50");
			panelMain.add(cboAnimalClass, "cell 0 0, grow, width 150");
			panelMain.add(panelFeatures, "cell 0 1, grow, width 450");
			panelMain.add(panelClassifiers, "cell 0 1, grow, width 450");
			
			panelFeatures.add(objTableFeatures, "cell 0 0, grow");
			panelClassifiers.add(objTableClassifiers, "cell 0 0, grow");
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void loadFeatures() {
		objTableFeatures.deleteRows();
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.MFCC);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_mfcc"));
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.LPC);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_lpc"));
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.LPCC);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_lpcc"));
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.PLP);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_plp"));
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.MFCC_LPC);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_mfcc_initials") + " + " + rsBundle.getString("feature_lpc_initials"));
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.MFCC_LPCC);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_mfcc_initials") + " + " + rsBundle.getString("feature_lpcc_initials"));
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.MFCC_PLP);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_mfcc_initials") + " + " + rsBundle.getString("feature_plp_initials"));
		
		objTableFeatures.addRow();
		objTableFeatures.addRowData("selected", true);
		objTableFeatures.addRowData("feature", Features.MFCC_LPC_LPCC_PLP);
		objTableFeatures.addRowData("feature_description", rsBundle.getString("feature_mfcc_initials") + " + " + rsBundle.getString("feature_lpc_initials") + " + " + rsBundle.getString("feature_lpcc_initials") + " + " + rsBundle.getString("feature_plp_initials"));
	}
	
	private void loadClassifiers() {
		objTableClassifiers.deleteRows();
		
		objTableClassifiers.addRow();
		objTableClassifiers.addRowData("selected", true);
		objTableClassifiers.addRowData("classifier", rsBundle.getString("classifier_hmm"));
		
		//objTableClassifiers.addRow();
		//objTableClassifiers.addRowData("selected", true);
		//objTableClassifiers.addRowData("classifier", rsBundle.getString("classifier_svm"));
	}
	
	private void loadClasses() {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("SELECT tax.animal_class ");
			objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments  seg ");
			objDatabaseConnection.sqlCommandAppend("INNER JOIN animal_taxonomies     tax  ON  seg.fk_animal_taxonomy = tax.id_animal_taxonomy ");
			objDatabaseConnection.sqlCommandAppend("GROUP BY tax.animal_class ");
			objDatabaseConnection.sqlCommandAppend("ORDER BY tax.animal_class ");
			
			ResultSet rsAnimalClasses = objDatabaseConnection.executeSelectQuery();
			
			objDatabaseConnection.rollBackTransaction();
			
			String strFirstClass = "";
			
			while (rsAnimalClasses.next()) {
				if (strFirstClass.equals("")) {
					strFirstClass = rsAnimalClasses.getString("animal_class");
				}
				
				cboAnimalClass.addValues(rsAnimalClasses.getString("animal_class"), rsAnimalClasses.getString("animal_class"));
			}
			
			cboAnimalClass.setValue(strFirstClass);
			
		} catch (SQLException e) {
			e.printStackTrace();
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Constrói os modelos de classificação.
	 */
	private void buildModels() {
		intTotalFeaturesSelected = 0;
		
		for (int indexFeature = 0; indexFeature < objTableFeatures.getRowCount(); indexFeature++) {
			if ((boolean) objTableFeatures.getValue(indexFeature, "selected")) {
				intTotalFeaturesSelected++;
			}
		}
		
		// Nenhuma feature selecionada
		if (intTotalFeaturesSelected == 0) {
			WasisMessageBox.showMessageDialog(rsBundle.getString("screen_model_builder_screen_no_features_selected"), WasisMessageBox.WARNING_MESSAGE);
		
		// Pelo menos uma feature selecionada
		} else {
			DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
			
			SwingWorker<Void, Void> swingWorkerBuildModels = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception, Error {
					try {
						blnCancelOperation = false;
						
						intTotalRecordsToProcess = 0;
						intTotalRecordsProcessed = 0;
						
						fltTotalProgress = 0.0f;
						
						progressBar.setValue(0);
						progressBar.setString(String.format("%.2f", fltTotalProgress) + " % - " +
						                      rsBundle.getString("screen_model_builder_screen_progress_bar"));
						
						setComponentEnabled(false);
						
						objDatabaseConnection.openConnection();
						
						int intNumberProcessors = Runtime.getRuntime().availableProcessors() - 1;
						String strAnimalClass = cboAnimalClass.getValue();
						
						ResultSet rsFeatureVectors;
						ResultSet rsSpecies;
						
						Points[] points;
						String[] originalFeatureVector;
						double[] featureVector;
						
						Codebook objCodebook;
						CodebookDictionary objCodebookDictionary;
						ExecutorService executorService;
						TrainHMMSpecies objTrainHMMSpecies;
						
						lgnIdModelClass = saveModelClass();
						
						// Loop através das features a partir das quais serão gerados os modelos de classificação
						for (int indexFeature = 0; indexFeature < objTableFeatures.getRowCount(); indexFeature++) {
							if ((boolean) objTableFeatures.getValue(indexFeature, "selected")) {
								if (!blnCancelOperation) {
									strCurrentFeatureInProcess = (String) objTableFeatures.getValue(indexFeature, "feature_description");
									
									//updateTotalRecordsProcessed();
									
									String strFeature = (String) objTableFeatures.getValue(indexFeature, "feature");
									String strFeatureVector = "";
									
									if (strFeature.equals(Features.MFCC)) {
										strFeatureVector = "fea.mfcc_vector";
									} else if (strFeature.equals(Features.LPC)) {
										strFeatureVector = "fea.lpc_vector";
									} else if (strFeature.equals(Features.LPCC)) {
										strFeatureVector = "fea.lpcc_vector";
									} else if (strFeature.equals(Features.PLP)) {
										strFeatureVector = "fea.plp_vector";
									} else if (strFeature.equals(Features.MFCC_LPC)) {
										strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.lpc_vector)";
									} else if (strFeature.equals(Features.MFCC_LPCC)) {
										strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.lpcc_vector)";
									} else if (strFeature.equals(Features.MFCC_PLP)) {
										strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.plp_vector)";
									} else if (strFeature.equals(Features.MFCC_LPC_LPCC_PLP)) {
										strFeatureVector = "CONCAT(fea.mfcc_vector, ';', fea.lpc_vector, ';', fea.lpcc_vector, ';', fea.plp_vector)";
									}
									
									objDatabaseConnection.initiliazeStatement();
									objDatabaseConnection.sqlCommand("SELECT " + strFeatureVector + " AS feature_vector ");
									objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_features    fea ");
									objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments             seg   ON   fea.fk_audio_file_segment = seg.id_audio_segment ");
									objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                      aud   ON   seg.fk_audio_file         = aud.id_audio_file ");
									objDatabaseConnection.sqlCommandAppend("INNER JOIN animal_taxonomies                tax   ON   seg.fk_animal_taxonomy    = tax.id_animal_taxonomy ");
									objDatabaseConnection.sqlCommandAppend("WHERE aud.id_audio_file > 0 ");
									objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
									objDatabaseConnection.sqlCommandAppend("AND tax.animal_class = ? ");
									objDatabaseConnection.sqlCommandAppend("ORDER BY fea.fk_audio_file_segment, fea.frame_number ");
									objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
									
									rsFeatureVectors = objDatabaseConnection.executeSelectQuery();
									
									// ***************************************************************
									// Gera o Codebook
									int intTotalFeatureVectors = objDatabaseConnection.getTotalRecords();
									
									points = new Points[intTotalFeatureVectors];
									int intIndexFeature = 0;
									
									while (rsFeatureVectors.next()) {
										originalFeatureVector = rsFeatureVectors.getString("feature_vector").split(";");
										
										featureVector = new double[originalFeatureVector.length];
										
										for (int indexElement = 0; indexElement < originalFeatureVector.length; indexElement++) {
											featureVector[indexElement] = Double.parseDouble(originalFeatureVector[indexElement]);
										}
										
										points[intIndexFeature] = new Points(featureVector);
										intIndexFeature++;
									}
									
									// Grava o codebook no banco de dados
									objCodebook = new Codebook(points);
									objCodebookDictionary = objCodebook.getCodebookDictionary();
									
									blnHasEnoughTrainingPoints = objCodebook.getHasEnoughTrainingPoints();
									
									// Há pontos de treinamento suficientes para gerar os modelos
									if (blnHasEnoughTrainingPoints) {
										long lgnIdModelClassHmm = saveCodebook(lgnIdModelClass, strFeature, objCodebookDictionary);
										
										// ***************************************************************
										// Treina os modelos HMM para cada espécie existente
										objDatabaseConnection.initiliazeStatement();
										objDatabaseConnection.sqlCommand("SELECT tax.id_animal_taxonomy ");
										objDatabaseConnection.sqlCommandAppend("FROM       audio_files_segments_features    fea ");
										objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files_segments             seg   ON   fea.fk_audio_file_segment = seg.id_audio_segment ");
										objDatabaseConnection.sqlCommandAppend("INNER JOIN audio_files                      aud   ON   seg.fk_audio_file         = aud.id_audio_file ");
										objDatabaseConnection.sqlCommandAppend("INNER JOIN animal_taxonomies                tax   ON   seg.fk_animal_taxonomy    = tax.id_animal_taxonomy ");
										objDatabaseConnection.sqlCommandAppend("WHERE aud.id_audio_file > 0 ");
										objDatabaseConnection.sqlCommandAppend("AND fea.ind_normalized = 0 ");
										objDatabaseConnection.sqlCommandAppend("AND tax.animal_class = ? ");
										objDatabaseConnection.sqlCommandAppend("GROUP BY tax.animal_genus, tax.animal_species ");
										objDatabaseConnection.sqlCommandAppend("ORDER BY tax.animal_genus, tax.animal_species ");
										objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, strAnimalClass);
										
										rsSpecies = objDatabaseConnection.executeSelectQuery();
										
										if (intTotalRecordsToProcess == 0) {
											int intTotalSpecies = objDatabaseConnection.getTotalRecords();
											
											intTotalRecordsToProcess = intTotalFeaturesSelected * intTotalSpecies;
										}
										
										// É criada uma pool com uma thread para cada processador disponível para a extração da feature
										executorService = Executors.newFixedThreadPool(intNumberProcessors);
										
										while (rsSpecies.next()) {
											long lgnIdAnimalTaxonomy = rsSpecies.getLong("id_animal_taxonomy");
											
											objTrainHMMSpecies = new TrainHMMSpecies(objCodebook, lgnIdModelClassHmm, lgnIdAnimalTaxonomy, strFeature);
											executorService.execute(objTrainHMMSpecies);
										}
										
										executorService.shutdown();
										executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);  // Aguarda finalizar todas as threads
									}
								}
							}
							
							// Ativa o modelo de classe criado
							activateModelClass();
						}
						
					} catch (Error e) {
						deleteModelClass(lgnIdModelClass);
						
						throw new Error(e);
						
					} catch (Exception e) {
						deleteModelClass(lgnIdModelClass);
						
						throw new Exception(e);
					}
		            
					return null;
				}
				
				@Override
		        protected void done() {
		            try {
		                get();
		                
		                if (!blnHasEnoughTrainingPoints) {
		                	progressBar.setValue(0);
		    				progressBar.setString("");
		    				
		    				deleteModelClass(lgnIdModelClass);
		    				
							WasisMessageBox.showMessageDialog(rsBundle.getString("screen_model_builder_screen_insuficient_data_01") + "\n" +
												              rsBundle.getString("screen_model_builder_screen_insuficient_data_02") + " '" + cboAnimalClass.getValue() + "'.", WasisMessageBox.WARNING_MESSAGE);
		                
		                } else if (blnCancelOperation) {
		    				progressBar.setValue(0);
		    				progressBar.setString(rsBundle.getString("screen_model_builder_screen_cancel_progress"));
		    				
		    				deleteModelClass(lgnIdModelClass);
		    				
		    				WasisMessageBox.showMessageDialog(rsBundle.getString("screen_model_builder_screen_cancelled"), WasisMessageBox.INFORMATION_MESSAGE);
		    				
		    			} else {
			                WasisMessageBox.showMessageDialog(rsBundle.getString("screen_model_builder_screen_saved"), WasisMessageBox.INFORMATION_MESSAGE);
			                
			                txtDescription.setValue("");
			                txtDateCreation.setValue("");
			                
			                loadFeatures();
			    			loadClassifiers();
		    			}
		                
		            } catch (Error e) {
		            	e.printStackTrace();
		            	WasisMessageBox.showMessageDialog(rsBundle.getString("screen_model_builder_screen_error"), WasisMessageBox.ERROR_MESSAGE);
		            	
		            } catch (Exception e) {
		            	e.printStackTrace();
		            	WasisMessageBox.showMessageDialog(rsBundle.getString("screen_model_builder_screen_error"), WasisMessageBox.ERROR_MESSAGE);
		            	
		            } finally {
		            	objDatabaseConnection.rollBackTransaction();
						objDatabaseConnection.closeConnection();
						
						setComponentEnabled(true);
						
						progressBar.setString("");
			            progressBar.setValue(0);
			            
			            lgnIdModelClass = 0;
		            }
		        }
			};
	
			swingWorkerBuildModels.execute();
		}
	}
	
	/**
	 * Habilita/desabilita componentes da tela.
	 * 
	 * @param blnEnabled - <i>True</i> - Componentes habilitados
	 */
	private void setComponentEnabled(boolean blnEnabled) {
		WasisContainer.setComponentEnabled(panelMain, blnEnabled);
		
		btnBuildModels.setEnabled(blnEnabled);
		btnCancelOperation.setEnabled(!blnEnabled);
	}
	
	/**
	 * Grava o modelo no banco de dados.
	 * 
	 * @return lgnIdModelClass
	 * @throws Exception 
	 */
	private long saveModelClass() throws Exception {
		long lgnIdModelClass = 0;
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("INSERT INTO class_models (class_model_description, date_creation, animal_class, ind_active) ");
			objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?, ?) ");
			objDatabaseConnection.addParameter("class_model_description", LTDataTypes.STRING, txtDescription.getValue());
			objDatabaseConnection.addParameter("date_creation", LTDataTypes.DATE, txtDateCreation.getValue());
			objDatabaseConnection.addParameter("animal_class", LTDataTypes.STRING, cboAnimalClass.getValue());
			objDatabaseConnection.addParameter("ind_active", LTDataTypes.BOOLEAN, false);
			objDatabaseConnection.executeQuery();
			
			lgnIdModelClass = objDatabaseConnection.getIdentityKey();
			
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lgnIdModelClass;
	}
	
	private void activateModelClass() throws Exception {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("UPDATE class_models ");
			objDatabaseConnection.sqlCommandAppend("SET ind_active = ? ");
			objDatabaseConnection.addParameter("ind_active", LTDataTypes.BOOLEAN, true);
			objDatabaseConnection.executeQuery();
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Delete um modelo de classe caso haja algum erro.
	 * 
	 * @param lgnIdModelClass
	 * 
	 * @throws Exception
	 */
	private void deleteModelClass(long lgnIdModelClass) throws Exception {
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("DELETE FROM class_models ");
			objDatabaseConnection.sqlCommandAppend("WHERE id_class_model = ? ");
			objDatabaseConnection.addParameter("id_class_model", LTDataTypes.LONG, lgnIdModelClass);
			objDatabaseConnection.executeQuery();
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
	}
	
	/**
	 * Grava o objeto do Codebook no banco de dados.
	 * 
	 * @param lgnIdModelClass
	 * @param strFeature
	 * @param objCodebookDictionary
	 * 
	 * @return lgnIdModelClassHmm
	 */
	private long saveCodebook(long lgnIdModelClass, String strFeature, CodebookDictionary objCodebookDictionary) throws Exception {
		long lgnIdModelClassHmm = 0;
		
		DatabaseConnection objDatabaseConnection = DatabaseConnection.getInstance();
		
		try {
			objDatabaseConnection.openConnection();
			objDatabaseConnection.initiliazeStatement();
			objDatabaseConnection.sqlCommand("INSERT INTO class_models_hmm (fk_class_model, feature_representation, codebook) ");
			objDatabaseConnection.sqlCommandAppend("VALUES (?, ?, ?) ");
			objDatabaseConnection.addParameter("fk_class_model", LTDataTypes.LONG, lgnIdModelClass);
			objDatabaseConnection.addParameter("feature_representation", LTDataTypes.STRING, strFeature);
			objDatabaseConnection.addParameter("codebook", null, objCodebookDictionary);
			objDatabaseConnection.executeQuery();
			
			lgnIdModelClassHmm = objDatabaseConnection.getIdentityKey();
			
			objDatabaseConnection.commitTransaction();
			
		} catch (Exception e) {
			throw new Exception(e);
			
		} finally {
			objDatabaseConnection.rollBackTransaction();
			objDatabaseConnection.closeConnection();
		}
		
		return lgnIdModelClassHmm;
	}
	
	/**
	 * Atualiza o total de registros processados.
	 */
	protected static void updateTotalRecordsProcessed() {
		if (!blnCancelOperation) {
			intTotalRecordsProcessed = intTotalRecordsProcessed + 1;
			
			// Atualiza a barra de progresso
			fltTotalProgress = (float) intTotalRecordsProcessed / (float) intTotalRecordsToProcess * 100.0f;
			if (fltTotalProgress > 100) {
				fltTotalProgress = 100;
			}
			
			progressBar.setValue((int) fltTotalProgress);
			progressBar.setString(String.format("%.2f", fltTotalProgress) + " % - " + 
			                      rsBundle.getString("screen_model_builder_screen_progress_bar") + " (" + 
			                      strCurrentFeatureInProcess + ")");
			
		} else {
			progressBar.setValue(0);
			progressBar.setString(rsBundle.getString("screen_model_builder_screen_cancel_progress"));
		}
	}
}