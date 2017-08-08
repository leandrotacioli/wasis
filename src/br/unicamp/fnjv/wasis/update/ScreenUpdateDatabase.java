package br.unicamp.fnjv.wasis.update;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.ResourceBundle;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import net.miginfocom.swing.MigLayout;
import br.unicamp.fnjv.wasis.database.UpdateH2Database;
import br.unicamp.fnjv.wasis.database.UpdateH2DatabaseListener;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Atualizador de banco de dados.
 * 
 * @author Leandro Tacioli
 * @version 1.0 - 15/Mai/2015
 */
public class ScreenUpdateDatabase extends JDialog implements UpdateH2DatabaseListener {
	private static final long serialVersionUID = 1403688108848323158L;

	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private JLabel lblImageUpdate;
	
	private JTextArea txtInstructions;
	
	private WasisPanel panelProgress;
	private JLabel lblProgress;
	private JProgressBar progressBar;
	
	private JButton btnUpdate;
	private JButton btnCancelUpdate;
	
	private String strDatabasePath;
	private final String strDatabaseLink = "http://www2.ib.unicamp.br/wasis/database/wasis-db-update.zip";
	
	/**
	 * Atualizador de banco de dados.
	 */
	public ScreenUpdateDatabase() {
		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_update_database_screen_description"), false);
		objWasisDialog.setBounds(350, 350, 600, 400);
		objWasisDialog.setResizable(false);
		
		// *************************************************************************************************
		// Imagem Atualização
		ImageIcon iconUpdateDatabase = new ImageIcon("res/images/update_database.png");
		lblImageUpdate = new JLabel();
		lblImageUpdate.setIcon(iconUpdateDatabase);
		
		txtInstructions = new JTextArea();
		txtInstructions.setMinimumSize(new Dimension(0, 0));
		txtInstructions.setMaximumSize(new Dimension(350, 80));
		txtInstructions.setBackground(Color.GREEN);
		txtInstructions.setFont(new Font("Tahoma", Font.PLAIN, 14));
		txtInstructions.setLineWrap(true);
		txtInstructions.setWrapStyleWord(true);
		txtInstructions.setOpaque(false);
		txtInstructions.setText(rsBundle.getString("screen_update_database_instructions_1"));
		txtInstructions.append(rsBundle.getString("screen_update_database_instructions_2"));
		txtInstructions.append(rsBundle.getString("screen_update_database_instructions_3"));
		txtInstructions.setEditable(false);
		
		panelProgress = new WasisPanel();
		panelProgress.setLayout(new MigLayout("insets 0", "[375.00]", "[][]"));
		panelProgress.setMinimumSize(new Dimension(10, 100));
		panelProgress.setMaximumSize(new Dimension(375, 100));
		
		lblProgress = new JLabel();
		lblProgress.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblProgress.setMinimumSize(new Dimension(0, 50));
		lblProgress.setMaximumSize(new Dimension(350, 50));
		
		progressBar = new JProgressBar();
		progressBar.setIndeterminate(true);
		progressBar.setMinimumSize(new Dimension(100, 30));
		progressBar.setMaximumSize(new Dimension(350, 30));
		progressBar.setVisible(false);
		
		btnUpdate = new JButton(rsBundle.getString("screen_update_database_button_update"));
		btnUpdate.setMinimumSize(new Dimension(100, 30));
		btnUpdate.setMaximumSize(new Dimension(250, 30));
		btnUpdate.setIconTextGap(15);
		btnUpdate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnUpdate.setIcon(new ImageIcon("res/images/update.png"));
		btnUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				updateDatabase();
			}
		});
		
		btnCancelUpdate = new JButton(rsBundle.getString("screen_update_database_button_cancel"));
		btnCancelUpdate.setMinimumSize(new Dimension(100, 30));
		btnCancelUpdate.setMaximumSize(new Dimension(250, 30));
		btnCancelUpdate.setIconTextGap(15);
		btnCancelUpdate.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnCancelUpdate.setIcon(new ImageIcon("res/images/cancel.png"));
		btnCancelUpdate.setEnabled(false);
		btnCancelUpdate.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				cancelUpdate();
			}
		});
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[] 20 [grow]", "[150.00] 0 [grow]"));
		objWasisDialog.getContentPane().add(lblImageUpdate, "cell 0 0, al center");
		objWasisDialog.getContentPane().add(txtInstructions, "cell 1 0, grow");
		objWasisDialog.getContentPane().add(panelProgress, "cell 1 1");
		objWasisDialog.getContentPane().add(btnUpdate, "cell 1 2");
		//objWasisDialog.getContentPane().add(btnCancelUpdate, "cell 1 2");
		
		panelProgress.add(lblProgress, "cell 0 0, growx, al center");
		panelProgress.add(progressBar, "cell 0 1, growx, al center");
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	public void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Atualiza o banco de dados
	 */
	private void updateDatabase() {
		SwingWorker<Boolean, Boolean> swingWorkerUpdate = new SwingWorker<Boolean, Boolean>() {

			@Override
			protected Boolean doInBackground() throws Exception {
				try {
					btnUpdate.setEnabled(false);
					btnCancelUpdate.setEnabled(true);
					progressBar.setVisible(true);
					
					downloadDatabase();
					unzipDatabase();
					updateDatabaseRecords();
					
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
	            	WasisMessageBox.showMessageDialog(rsBundle.getString("screen_save_audio_data_error_saving_data"), WasisMessageBox.ERROR_MESSAGE);
	            	
	            } finally {
	            	lblProgress.setText("");
	            	btnUpdate.setEnabled(true);
					btnCancelUpdate.setEnabled(false);
	            }
	        }
		};

		swingWorkerUpdate.execute();
	}
	
	/**
	 * Realiza o download do arquivo do banco de dados.
	 * 
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void downloadDatabase() throws MalformedURLException, IOException {
		URL url = new URL(strDatabaseLink);
        URLConnection urlConnection = url.openConnection();
        InputStream inputStream = urlConnection.getInputStream();
        
        long lgnTotalFileLength = urlConnection.getContentLength();
        
        lblProgress.setText(rsBundle.getString("screen_update_database_downloading_database") + " - " + lgnTotalFileLength + " " + rsBundle.getString("screen_update_database_downloading_database_bytes"));
        
		File fileDatabase = new File("data/wasis.h2.db");
		strDatabasePath = fileDatabase.getAbsoluteFile().getParentFile().getAbsolutePath();
        
        BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(new File(strDatabasePath + "/update-db.zip")));
        
        byte[] buffer = new byte[32768];
        int bytesRead = 0;
        long lgnTotalBytesRead = 0;
        
        long lgnPercentage = 0;
        
        while ((bytesRead = inputStream.read(buffer)) != -1) {
        	lgnTotalBytesRead += bytesRead;
            outputStream.write(buffer, 0, bytesRead);
            
            lgnPercentage = (long) (((double) lgnTotalBytesRead / (double) lgnTotalFileLength) * 100);
            
            lblProgress.setText(rsBundle.getString("screen_update_database_downloading_database") + " - " + lgnPercentage + " %");
        }
        
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        
        lblProgress.setText(rsBundle.getString("screen_update_database_downloading_database_completed"));
	}
	
	private void unzipDatabase() throws IOException {
        final int BUFFER = 2048;
        
        BufferedInputStream inputStream = null;
        BufferedOutputStream outputStream = null;
        
        ZipEntry entry;
        @SuppressWarnings("resource")
		ZipFile zipFile = new ZipFile(strDatabasePath + "/update-db.zip");
        Enumeration<?> e = zipFile.entries();
        
        (new File(strDatabasePath)).mkdir();
        
        while(e.hasMoreElements()) {
           entry = (ZipEntry) e.nextElement();
           
           lblProgress.setText(rsBundle.getString("screen_update_database_extracting_database"));
           
           if (entry.isDirectory()) {
               (new File(strDatabasePath + "/" + entry.getName())).mkdir();
               
           } else {
               (new File(strDatabasePath + "/" + entry.getName())).createNewFile();
               
               inputStream = new BufferedInputStream(zipFile.getInputStream(entry));
               
               int count;
               
               byte data[] = new byte[BUFFER];
               
               FileOutputStream fileOutputStream = new FileOutputStream(strDatabasePath + "/" + entry.getName());
               outputStream = new BufferedOutputStream(fileOutputStream, BUFFER);
               
               while ((count = inputStream.read(data, 0, BUFFER)) != -1) {
            	   outputStream.write(data, 0, count);
               }
               
               outputStream.flush();
               outputStream.close();
               inputStream.close();
           }
        }
    }
	
	/**
	 * Atualiza os registros do banco de dados.
	 */
	private void updateDatabaseRecords() {
		UpdateH2Database objUpdateDatabase = new UpdateH2Database();
		objUpdateDatabase.addCollectionListener(ScreenUpdateDatabase.this);
		objUpdateDatabase.updateDatabase();
	}
	
	/**
	 * Cancela a atualização do banco de dados.
	 */
	private void cancelUpdate() {
		
	}

	// Implementa UpdateH2DatabaseListener
	@Override
	public void updateProcessedRecords(int intProcessedRecords, int intTotalRecords) {
		lblProgress.setText(rsBundle.getString("screen_update_database_updating_database_records") + " - " + intProcessedRecords + " " + rsBundle.getString("screen_update_database_updating_database_records_of") + " " + intTotalRecords);
	}
}