package br.unicamp.fnjv.wasis.multimidia.ffmpegwrapper;

import java.io.File;
import java.io.IOException;
import java.util.ResourceBundle;

import javax.swing.JButton;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;

import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JProgressBar;

import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;

/**
 * FFMPEG Encoder.
 * 
 * @author Leandro Tacioli
 * @version 2.0 - 12/Mai/2017
 */
public class FfmpegEncoder implements FfmpegWrapperListener {
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private FfmpegExecutable objFfmpegExecutable;     // Executável do FFMPEG utilizado pelo encoder
	private FfmpegWrapper objFfmpegWrapper;
	
	private WasisDialog objWasisDialog;
	
	private JButton btnCancel;
	private JProgressBar progressBar;
	
	private boolean blnAllowDialog;
	private boolean blnProgress = true;
	private boolean blnCanceled = false;
	
	/**
	 * Retorna status do encoder.
	 * 
	 * @return <i>True</i> - Em progresso
	 */
	public boolean getProgress() {
		return blnProgress;
	}
	
	/**
	 * Retorna status caso haja cancelamento do encoder.
	 * 
	 * @return <i>True</i> - Cancelado
	 */
	public boolean getCanceled() {
		return blnCanceled;
	}
	
	/**
	 * Constrói o encoder FFMPEG.
	 * 
	 * @param blnAllowDialog
	 */
	public FfmpegEncoder(boolean blnAllowDialog) {
		this.blnAllowDialog = blnAllowDialog;
		
		objFfmpegExecutable = new FfmpegExecutable();
		
		if (blnAllowDialog) {
			progressBar = new JProgressBar(0, 100);
			progressBar.setStringPainted(true);
			progressBar.setFont(new Font("Tahoma", Font.BOLD, 16));
			
			btnCancel = new JButton(rsBundle.getString("ffmpeg_encoder_cancel_converting_audio_file"));
			btnCancel.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent event) {
					objFfmpegWrapper.destroyFfmpeg();
					blnCanceled = true;
					objWasisDialog.setVisible(false);
				}
			});
			
			objWasisDialog = new WasisDialog(rsBundle.getString("ffmpeg_encoder_converting_audio_file"), false);
			objWasisDialog.setBounds(275, 275, 275, 105);
			objWasisDialog.setResizable(false);
			objWasisDialog.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
			
			objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[grow]", "[40.00][]"));
			objWasisDialog.getContentPane().add(progressBar, "cell 0 0, grow");
			objWasisDialog.getContentPane().add(btnCancel, "cell 0 1, alignx center");
	
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					objWasisDialog.setVisible(true);
				}
			});
		}
	}

	/**
	 * Converte o arquivo baseado nos atributos passados como parâmetros.
	 * 
	 * @param fileSource                 - Arquivo original que irá ser convertido
	 * @param fileTarget                 - Arquivo final já convertido
	 * @param objFfmpegEncoderAttributes - Conjunto de atributos utilizado no processo de conversão
	 * 
	 * @throws EncoderException
	 */
	public void encode(File fileSource, File fileTarget, FfmpegEncoderAttributes objFfmpegEncoderAttributes) throws Exception {
		try {
			// Cria o Wrapper
			objFfmpegWrapper = objFfmpegExecutable.createWrapper();
			objFfmpegWrapper.addWrapperListener(FfmpegEncoder.this);
			
			// Source File
			objFfmpegWrapper.addParameter("-i");
			objFfmpegWrapper.addParameter(fileSource.getAbsolutePath());
			
			// Target Format (WAV)
			objFfmpegWrapper.addParameter("-f");
			objFfmpegWrapper.addParameter(objFfmpegEncoderAttributes.getTargetFormat());
			
			// Target Codec
			objFfmpegWrapper.addParameter("-acodec");
			objFfmpegWrapper.addParameter(objFfmpegEncoderAttributes.getTargetCodec());
			
			// Target Sample Rate
			objFfmpegWrapper.addParameter("-ar");
			objFfmpegWrapper.addParameter(String.valueOf(objFfmpegEncoderAttributes.getTargetSampleRate()));
			
			// Target Channels
			objFfmpegWrapper.addParameter("-ac");
			objFfmpegWrapper.addParameter(String.valueOf(objFfmpegEncoderAttributes.getTargetChannels()));
			
			// Target File
			objFfmpegWrapper.addParameter("-y");
			objFfmpegWrapper.addParameter(fileTarget.getAbsolutePath());
		
			// Executa o FFMPEG
			objFfmpegWrapper.executeFfmpeg();

		} catch (IOException e) {
			if (blnAllowDialog) {
				objWasisDialog.setVisible(false);
			}
			
			throw new Exception(e);
		}
	}

	// Implementa FfmpegWrapperListener
	@Override
	public void updateProgress(int intProgress) {
		if (blnAllowDialog) {
			progressBar.setValue(intProgress); // Atualiza a barra de progresso
		}
		
		if (intProgress >= 100) {
			blnProgress = false;
			
			if (blnAllowDialog) {
				objWasisDialog.setVisible(false);
			}
		}
	}
}