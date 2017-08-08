package br.unicamp.fnjv.wasis.about;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import com.leandrotacioli.libs.LTParameters;

import net.miginfocom.swing.MigLayout;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;

import java.awt.Desktop;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

/**
 * Classe responsável pela exibição da tela de About.
 */
public class ScreenAbout {
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();

	private WasisDialog objWasisDialog;
	
	private JLabel lblLogoWASIS;
	
	private JPanel panelLogos;
	private JLabel lblLogoFNJV;
	private JLabel lblLogoZUEC;
	private JLabel lblLogoLaHNAB;
	private JLabel lblLogoLIS;
	
	private JTextArea txtDescription;
	private JTextArea txtCitation;
	
	/**
	 * Classe responsável pela exibição da tela de About.
	 */
	public ScreenAbout() {
		loadScreen();
	}

	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_about_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 875, 500);
		objWasisDialog.setResizable(false);
		
		// *************************************************************************************************
		// Logo WASIS
		ImageIcon iconLogo = new ImageIcon("res/images/logo_wasis.png");
		lblLogoWASIS = new JLabel();
		lblLogoWASIS.setIcon(iconLogo);
		
		// *************************************************************************************************
		// Descrição
		txtDescription = new JTextArea();
		txtDescription.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtDescription.setOpaque(false);
		txtDescription.setText(rsBundle.getString("screen_about_description_1"));
		txtDescription.append(rsBundle.getString("screen_about_description_2"));
		txtDescription.append(rsBundle.getString("screen_about_description_3"));
		txtDescription.append(rsBundle.getString("screen_about_description_4"));
		txtDescription.append(rsBundle.getString("screen_about_description_5"));
		txtDescription.append(rsBundle.getString("screen_about_description_6"));
		txtDescription.append(rsBundle.getString("screen_about_description_7"));
		txtDescription.append(rsBundle.getString("screen_about_description_8"));
		txtDescription.append(rsBundle.getString("screen_about_description_9"));
		txtDescription.append(rsBundle.getString("screen_about_description_10"));
		txtDescription.append(rsBundle.getString("screen_about_description_11"));
		txtDescription.append(rsBundle.getString("screen_about_description_12"));
		txtDescription.append(rsBundle.getString("screen_about_description_13"));
		txtDescription.append(rsBundle.getString("screen_about_description_14"));
		txtDescription.append(rsBundle.getString("screen_about_description_15"));
		txtDescription.setEditable(false);
		
		// *************************************************************************************************
		// Citação
		txtCitation = new JTextArea();
		txtCitation.setFont(new Font("Tahoma", Font.PLAIN, 11));
		txtCitation.setOpaque(false);
		txtCitation.setText(rsBundle.getString("screen_about_citation_1"));
		txtCitation.append(rsBundle.getString("screen_about_citation_2"));
		txtCitation.append(rsBundle.getString("screen_about_citation_3"));
		txtCitation.append(rsBundle.getString("screen_about_citation_4"));
		txtCitation.append(rsBundle.getString("screen_about_citation_5"));
		txtCitation.append(rsBundle.getString("screen_about_citation_6"));
		txtCitation.append(rsBundle.getString("screen_about_citation_7"));
		txtCitation.append(rsBundle.getString("screen_about_citation_8"));
		txtCitation.append(rsBundle.getString("screen_about_citation_9"));
		txtCitation.append(rsBundle.getString("screen_about_citation_10"));
		txtCitation.append(rsBundle.getString("screen_about_citation_11"));
		txtCitation.append(rsBundle.getString("screen_about_citation_12"));
		txtCitation.setEditable(false);
		
		// *************************************************************************************************
		// Logos
		panelLogos = new JPanel();
		panelLogos.setLayout(new MigLayout("insets 5 5 5 5", "[] 20 []", "[][]"));
		panelLogos.setBackground(LTParameters.getInstance().getColorComponentPanelBackground());
		
		iconLogo = new ImageIcon("res/images/logo_fnjv.png");
		lblLogoFNJV = new JLabel();
		lblLogoFNJV.setIcon(iconLogo);
		lblLogoFNJV.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event) {

			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				try {
                    Desktop.getDesktop().browse(new URI("http://www2.ib.unicamp.br/fnjv/"));
                } catch (URISyntaxException | IOException e) {

                }
			}
			
			@Override
			public void mouseExited(MouseEvent event) {

			}
			
			@Override
			public void mouseEntered(MouseEvent event) {

			}
			
			@Override
			public void mouseClicked(MouseEvent event) {

			}
		});
		
		iconLogo = new ImageIcon("res/images/logo_zuec.png");
		lblLogoZUEC = new JLabel();
		lblLogoZUEC.setIcon(iconLogo);
		lblLogoZUEC.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event) {

			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				try {
                    Desktop.getDesktop().browse(new URI("http://www.ib.unicamp.br/museu_zoologia/front-page"));
                } catch (URISyntaxException | IOException e) {

                }
			}
			
			@Override
			public void mouseExited(MouseEvent event) {

			}
			
			@Override
			public void mouseEntered(MouseEvent event) {

			}
			
			@Override
			public void mouseClicked(MouseEvent event) {

			}
		});
		
		iconLogo = new ImageIcon("res/images/logo_lahnab.png");
		lblLogoLaHNAB = new JLabel();
		lblLogoLaHNAB.setIcon(iconLogo);
		lblLogoLaHNAB.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event) {

			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				try {
                    Desktop.getDesktop().browse(new URI("http://www.naturalhistory.com.br/"));
                } catch (URISyntaxException | IOException e) {

                }
			}
			
			@Override
			public void mouseExited(MouseEvent event) {

			}
			
			@Override
			public void mouseEntered(MouseEvent event) {

			}
			
			@Override
			public void mouseClicked(MouseEvent event) {

			}
		});
		
		iconLogo = new ImageIcon("res/images/logo_lis.png");
		lblLogoLIS = new JLabel();
		lblLogoLIS.setIcon(iconLogo);
		lblLogoLIS.addMouseListener(new MouseListener() {
			@Override
			public void mouseReleased(MouseEvent event) {

			}
			
			@Override
			public void mousePressed(MouseEvent event) {
				try {
                    Desktop.getDesktop().browse(new URI("http://www.lis.ic.unicamp.br/"));
                } catch (URISyntaxException | IOException e) {

                }
			}
			
			@Override
			public void mouseExited(MouseEvent event) {

			}
			
			@Override
			public void mouseEntered(MouseEvent event) {

			}
			
			@Override
			public void mouseClicked(MouseEvent event) {

			}
		});
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[300.00] 20 [grow]", "[300.00] 20 [grow]"));
		objWasisDialog.getContentPane().add(lblLogoWASIS, "cell 0 0, al center");
		objWasisDialog.getContentPane().add(txtDescription, "cell 1 0, aligny center");
		objWasisDialog.getContentPane().add(txtCitation, "cell 0 1");
		objWasisDialog.getContentPane().add(panelLogos, "cell 1 1, alignx center");
		
		panelLogos.add(lblLogoFNJV, "cell 0 0, alignx center");
		panelLogos.add(lblLogoZUEC, "cell 1 0, alignx center");
		panelLogos.add(lblLogoLaHNAB, "cell 0 1, alignx center");
		panelLogos.add(lblLogoLIS, "cell 1 1, alignx center");
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
}