package br.unicamp.fnjv.wasis.classifiers;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JProgressBar;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.comboboxfield.LTComboBoxField;
import com.leandrotacioli.libs.swing.table.LTTable;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisPanel;
import net.miginfocom.swing.MigLayout;

public class ScreenModelBuilder extends JDialog {
	private static final long serialVersionUID = -4821611738366154316L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private LTTextField txtDescription;
	private LTTextField txtDateCreation;
	private LTComboBoxField cmbAnimalClass;
	
	private WasisPanel panelFeatures;
	private LTTable objTableFeatures;
	
	private WasisPanel panelClassifiers;
	private LTTable objTableClassifiers;
	
	private JButton btnBuildModels;
	private JButton btnCancelOperation;
	
	private JProgressBar progressBar;
	
	public ScreenModelBuilder() {
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		try {
			txtDescription = new LTTextField("Description:", LTDataTypes.STRING, true, true, 500);
			txtDateCreation = new LTTextField("Date:", LTDataTypes.DATE, true, true);
			
			cmbAnimalClass = new LTComboBoxField("Animal Class:", true, true);
			cmbAnimalClass.addValues("Aves", "Aves");
			cmbAnimalClass.addValues("Amphibia", "Amphibia");
			cmbAnimalClass.setValue("Aves");
			
			panelFeatures = new WasisPanel();
			panelFeatures.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableFeatures = new LTTable(false);
			objTableFeatures.addColumn("selected", "", LTDataTypes.BOOLEAN, 30, true);
			objTableFeatures.addColumn("feature", "Feature", LTDataTypes.STRING, 285, false);
			//objTableFeatures.setAllowSortedRows(false);
			objTableFeatures.showTable();
			
			objTableFeatures.addRow();
			objTableFeatures.addRowData("selected", true);
			objTableFeatures.addRowData("feature", rsBundle.getString("feature_mfcc"));
			
			objTableFeatures.addRow();
			objTableFeatures.addRowData("selected", true);
			objTableFeatures.addRowData("feature", rsBundle.getString("feature_lpc"));
			
			objTableFeatures.addRow();
			objTableFeatures.addRowData("selected", true);
			objTableFeatures.addRowData("feature", rsBundle.getString("feature_lpcc"));
			
			objTableFeatures.addRow();
			objTableFeatures.addRowData("selected", true);
			objTableFeatures.addRowData("feature", rsBundle.getString("feature_plp"));
			
			objTableFeatures.addRow();
			objTableFeatures.addRowData("selected", true);
			objTableFeatures.addRowData("feature", rsBundle.getString("feature_mfcc_initials") + " + " + rsBundle.getString("feature_lpc_initials"));
			
			objTableFeatures.addRow();
			objTableFeatures.addRowData("selected", true);
			objTableFeatures.addRowData("feature", rsBundle.getString("feature_mfcc_initials") + " + " + rsBundle.getString("feature_lpcc_initials"));
			
			objTableFeatures.addRow();
			objTableFeatures.addRowData("selected", true);
			objTableFeatures.addRowData("feature", rsBundle.getString("feature_mfcc_initials") + " + " + rsBundle.getString("feature_plp_initials"));
			
			panelClassifiers = new WasisPanel();
			panelClassifiers.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
			
			objTableClassifiers = new LTTable(false);
			objTableClassifiers.addColumn("selected", "", LTDataTypes.BOOLEAN, 30, true);
			objTableClassifiers.addColumn("classifier", "Classifier", LTDataTypes.STRING, 285, false);
			objTableClassifiers.showTable();

			objTableClassifiers.addRow();
			objTableClassifiers.addRowData("selected", true);
			objTableClassifiers.addRowData("classifier", rsBundle.getString("classifier_hmm"));
			
			//objTableClassifiers.addRow();
			//objTableClassifiers.addRowData("selected", true);
			//objTableClassifiers.addRowData("classifier", rsBundle.getString("classifier_svm"));
			
			// Botão Atualizar Coleção
			btnBuildModels = new JButton("Build Models");
			btnBuildModels.setIconTextGap(15);
			btnBuildModels.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnBuildModels.setIcon(new ImageIcon("res/images/build_models.png"));
			btnBuildModels.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					
				}
			});
			
			// Botão Cancelar Operação
			btnCancelOperation = new JButton("Cancel Operation");
			btnCancelOperation.setToolTipText("Cancela a operação de renomeação dos arquivos de áudio");
			btnCancelOperation.setIconTextGap(15);
			btnCancelOperation.setFont(new Font("Tahoma", Font.PLAIN, 14));
			btnCancelOperation.setIcon(new ImageIcon("res/images/cancel.png"));
			btnCancelOperation.setEnabled(false);
			btnCancelOperation.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					
				}
			});
			
			progressBar = new JProgressBar(0, 100);
			progressBar.setFont(new Font("Tahoma", Font.BOLD, 16));
			progressBar.setStringPainted(true);
			progressBar.setString("");
	
			// ***********************************************************************************************************************
			// Cria a tela
			objWasisDialog = new WasisDialog("Class Model Builder", true);
			objWasisDialog.setBounds(350, 350, 750, 350);
			objWasisDialog.setMinimumSize(new Dimension(750, 350));
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[][][155.00][][]"));
			objWasisDialog.getContentPane().add(txtDescription, "cell 0 0, grow, width 250");
			objWasisDialog.getContentPane().add(txtDateCreation, "cell 0 0, grow, width 50");
			objWasisDialog.getContentPane().add(cmbAnimalClass, "cell 0 1, grow, width 150");
			objWasisDialog.getContentPane().add(panelFeatures, "cell 0 2, grow, width 450");
			objWasisDialog.getContentPane().add(panelClassifiers, "cell 0 2, grow, width 450");
			objWasisDialog.getContentPane().add(btnBuildModels, "cell 0 3, grow, width 400");
			objWasisDialog.getContentPane().add(btnCancelOperation, "cell 0 3, grow, width 400");
			
			objWasisDialog.getContentPane().add(progressBar, "cell 0 4, grow");
			
			panelFeatures.add(objTableFeatures, "cell 0 0, grow");
			panelClassifiers.add(objTableClassifiers, "cell 0 0, grow");
			
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
}
