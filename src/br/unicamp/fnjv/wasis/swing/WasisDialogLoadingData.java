package br.unicamp.fnjv.wasis.swing;

import java.util.ResourceBundle;

import javax.swing.JDialog;

import net.miginfocom.swing.MigLayout;

import br.unicamp.fnjv.wasis.main.WasisParameters;

import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import java.awt.Font;

/**
 * Cria uma extensão para <i>JDialog</i> responsável pela
 * exibição de uma tela como forma de espera de algum processo.
 * 
 * @author Leandro Tacioli
 * @version 3.0 - 03/Set/2015
 */
public class WasisDialogLoadingData extends JDialog {
	private static final long serialVersionUID = -3165561128389957672L;

	/**
	 * Cria uma extensão para <i>JDialog</i> responsável pela
	 * exibição de uma tela como forma de espera de algum processo.
	 * 
	 * @param strTitle - Título da tela de espera
	 */
	public WasisDialogLoadingData(String strTitle) {
		super(WasisParameters.getInstance().getWasisFrame());
		
		// Cria os componentes da tela
		ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
		
		JLabel lblNewLabel = new JLabel(rsBundle.getString("message_please_wait"));
		lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 14));

		JProgressBar progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		
		// ***********************************************************************************************************************
		// Cria a tela
		setTitle(strTitle);
		setIconImage(WasisParameters.getInstance().getWasisIcon());
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
		setBounds(275, 275, 275, 105);
		setResizable(false);
		setLocationRelativeTo(WasisParameters.getInstance().getWasisFrame());
		
		getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[35.00][]"));
		getContentPane().setBackground(WasisParameters.COLOR_BACKGROUND);
		getContentPane().add(lblNewLabel, "cell 0 0, alignx center, aligny center");
		getContentPane().add(progressBar, "cell 0 1, growx, aligny center");
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				setVisible(true);
			}
		});
	}
	
	/**
	 * Desabilita a visualização da tela.
	 */
	public void disableScreen() {
		setVisible(false);
	}
 }