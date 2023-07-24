package br.unicamp.fnjv.wasis.main;

import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollBar;
import javax.swing.JSplitPane;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.Timer;
import javax.swing.UIManager;
import javax.swing.WindowConstants;

import net.miginfocom.swing.MigLayout;
import br.unicamp.fnjv.wasis.about.ScreenAbout;
import br.unicamp.fnjv.wasis.audio.AudioTemporary;
import br.unicamp.fnjv.wasis.audio.ScreenSaveAudio;
import br.unicamp.fnjv.wasis.audio.classification.ScreenAudioClassificationBruteForce;
import br.unicamp.fnjv.wasis.audio.classification.ScreenAudioClassificationClassModel;
import br.unicamp.fnjv.wasis.audio.library.AudioLibraryController;
import br.unicamp.fnjv.wasis.audio.library.AudioLibraryListener;
import br.unicamp.fnjv.wasis.audio.library.ScreenOpenAudioLibrary;
import br.unicamp.fnjv.wasis.audio.library.ScreenSaveAudioLibrary;
import br.unicamp.fnjv.wasis.classifiers.ScreenModelBuilder;
import br.unicamp.fnjv.wasis.dsp.FFTParameters;
import br.unicamp.fnjv.wasis.dsp.FFTWindowFunction;
import br.unicamp.fnjv.wasis.graphics.GraphicPanel;
import br.unicamp.fnjv.wasis.graphics.spectrogram.Spectrogram;
import br.unicamp.fnjv.wasis.graphics.spectrogram.SpectrogramColorDisplay;
import br.unicamp.fnjv.wasis.graphics.spectrogram.SpectrogramGraphicPanel;
import br.unicamp.fnjv.wasis.graphics.spectrogram.SpectrogramListener;
import br.unicamp.fnjv.wasis.graphics.waveform.Waveform;
import br.unicamp.fnjv.wasis.graphics.waveform.WaveformGraphicPanel;
import br.unicamp.fnjv.wasis.graphics.waveform.WaveformListener;
import br.unicamp.fnjv.wasis.libs.ClockTransformations;
import br.unicamp.fnjv.wasis.multimidia.AudioFileFilter;
import br.unicamp.fnjv.wasis.multimidia.wav.AudioWav;
import br.unicamp.fnjv.wasis.player.Player;
import br.unicamp.fnjv.wasis.player.PlayerListener;
import br.unicamp.fnjv.wasis.swing.WasisButton;
import br.unicamp.fnjv.wasis.swing.WasisDialogLoadingData;
import br.unicamp.fnjv.wasis.swing.WasisPanelGradient;
import br.unicamp.fnjv.wasis.swing.WasisInvisibleSplitPane;
import br.unicamp.fnjv.wasis.swing.WasisLabel;
import br.unicamp.fnjv.wasis.swing.WasisMessageBox;
import br.unicamp.fnjv.wasis.swing.WasisPanel;
import br.unicamp.fnjv.wasis.swing.WasisPanelRounded;
import br.unicamp.fnjv.wasis.swing.WasisScrollBar;
import br.unicamp.fnjv.wasis.update.ScreenUpdateDatabase;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.awt.Font;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.LTParameters;
import com.leandrotacioli.libs.swing.comboboxfield.LTComboBoxField;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

/**
 * Sistema WASIS.
 * 
 * @author Leandro Tacioli
 * @version 2.1.0 - 24/Jul/2023
 */
public class Wasis extends JFrame implements KeyListener, AudioLibraryListener, SpectrogramListener, WaveformListener, PlayerListener {
	private static final long serialVersionUID = -5829047685955210342L;
	
	// Versão do Software
	private final String WASIS_VERSION = "2.1.0 - 24/07/2023";
	
	// Pacote de linguagens
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	// FFT - Parâmetros
	private LTComboBoxField cmbFFTSamples;
	private LTTextField txtFFTOverlap;
	private LTComboBoxField cmbFFTWindow;
	
	// Controller da Biblioteca de áudio
	private AudioLibraryController objAudioLibraryController;
	
	// Áudio WAV
	private AudioWav objAudioWav;
	private File[] fileAudioList;
	private String strAudioFilePath;
	private boolean blnAudioFileLoadedFromLibrary;
	
    // Waveform
	private WasisPanel panelWaveform;
    private Waveform objWaveform;
	private GraphicPanel objWaveformGraphicPanel;
	
	// Espectrograma
	private WasisPanel panelSpectrogram;
	private Spectrogram objSpectrogram;
	private GraphicPanel objSpectrogramGraphicPanel;
	
	// Scrollbar do painel de visualização dos áudios (waveform e espectrograma)
	private WasisPanel panelScrollbarVerticalVisualization;
	private WasisScrollBar scrollBarVerticalVisualization;
	private WasisScrollBar scrollBarHorizontalVisualization;
	
	private int intLastTimeScroll;
	private boolean blnScrollDragged;
	private boolean blnDrawScrollbarVertical;
	private boolean blnDrawScrollbarHorizontal;
	
	// Player
	private Player objPlayer;
	private int intStatusPlayer;
	
	// Tempo atual
	private JLabel lblCurrentTime;
	private int intCurrentTime;
	
	// Visão / Seleção ativa
	private WasisLabel lblLabelView;
	private WasisLabel lblLabelSelection;
	
	private WasisLabel lblInitialTimeLabel;
	private WasisLabel lblFinalTimeLabel;
	private WasisLabel lblLengthTimeLabel;
	
	private WasisLabel lblInitialFrequencyLabel;
	private WasisLabel lblFinalFrequencyLabel;
	private WasisLabel lblLengthFrequencyLabel;

	private WasisLabel lblInitialTimeView;
	private WasisLabel lblFinalTimeView;
	private WasisLabel lblLengthTimeView;
	
	private WasisLabel lblInitialTimeSelection;
	private WasisLabel lblFinalTimeSelection;
	private WasisLabel lblLengthTimeSelection;
	
	private WasisLabel lblInitialFrequencyView;
	private WasisLabel lblFinalFrequencyView;
	private WasisLabel lblLengthFrequencyView;
	
	private WasisLabel lblInitialFrequencySelection;
	private WasisLabel lblFinalFrequencySelection;
	private WasisLabel lblLengthFrequencySelection;
	
	private int intInitialTimeView;
	private int intFinalTimeView;
	
	private int intInitialTimeSelection;
	private int intFinalTimeSelection;
	
	private int intInitialFrequencyView;
	private int intFinalFrequencyView;
	
	private int intInitialFrequencySelection;
	private int intFinalFrequencySelection;
	
	// Painel do rodapé do sistema
	private JLabel lblMousePosition;
	
	// Define os tipos de zooms que podem ser utilizados no recarregamento do arquivo de áudio
	private final int ZOOM_TIME = 0;
	private final int ZOOM_FREQUENCY = 1;
	
	private final int ZOOM_IN = 101;
	private final int ZOOM_OUT = 102;
	private final int ZOOM_RESET = 103;
	
	/**
	 * Carrega a aplicação.
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					new Wasis();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Construtor.
	 */
	private Wasis() {
		loadScreen();
	}

	/**
	 * Inicializa o conteúdo do frame principal do sistema.
	 */
	private void loadScreen() {
		try {
			setTitle(rsBundle.getString("wasis_title"));                  // Título do sistema
			setIconImage(WasisParameters.getInstance().getWasisIcon());   // Ícone do sistema
			
			// Atribui um "Look and Feel" padrão ao sistema
			// "Look" refere-se à aparência dos widgets GUI (JComponents) 
			// and "feel" refere-se ao modo que os widgets se comportam
			// Pode se comportar diferente dependendo do sistema operacional
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			
			// Configurações básicas de carregamento do layout do frame principal do sistema
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
			setMinimumSize(new Dimension(1024, 551));
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			// Estabelece a cor padrão do background
			getContentPane().setBackground(WasisParameters.COLOR_BACKGROUND_MAIN);
			
			// Atalhos no teclado para executar determinadas funções
			KeyboardFocusManager keyboardManager = KeyboardFocusManager.getCurrentKeyboardFocusManager();
			
			keyboardManager.addKeyEventDispatcher(new KeyEventDispatcher() {
		    	@Override
		        public boolean dispatchKeyEvent(KeyEvent e) {
		    		if (keyboardManager.getCurrentFocusCycleRoot() == Wasis.this) {
			    		// Executa player ao pressionar a barra de espaço
			    		if (e.getID() == KeyEvent.KEY_RELEASED && e.getKeyCode() == KeyEvent.VK_SPACE) {
			    			if (objAudioWav != null && objPlayer != null) {
						 		if (objPlayer.getPlayerStatus() != objPlayer.STATUS_PLAYING) {
		 							playAudio();
								} else {
									stopAudio();
								}
							}
			    		// Zoom In no espectrograma no eixo de tempo - 'CTRL +' ou 'CTRL ='
			    		} else if (e.getID() == KeyEvent.KEY_RELEASED && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) && (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS || e.getKeyCode() == 107)) {
			    			processAudioZoomTimeFrequency(ZOOM_TIME, ZOOM_IN);
			            // Zoom Out no espectrograma no eixo de tempo - 'CTRL -'
			    		} else if (e.getID() == KeyEvent.KEY_RELEASED && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) && (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == 109)) {
			    			processAudioZoomTimeFrequency(ZOOM_TIME,ZOOM_OUT);
			            // Zoom Reset no espectrograma no eixo de tempo - 'CTRL 0'
			    		} else if (e.getID() == KeyEvent.KEY_RELEASED && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0) && (e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_NUMPAD0)) {
			    			processAudioZoomTimeFrequency(ZOOM_TIME,ZOOM_RESET);
			    		// Zoom In no espectrograma no eixo de frequência - 'ALT +' ou 'ALT ='
			    		} else if (e.getID() == KeyEvent.KEY_RELEASED && ((e.getModifiers() & KeyEvent.ALT_MASK) != 0) && (e.getKeyCode() == KeyEvent.VK_PLUS || e.getKeyCode() == KeyEvent.VK_EQUALS || e.getKeyCode() == 107)) {
			    			processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_IN);
			            // Zoom Out no espectrograma no eixo de frequência - 'ALT -'
			    		} else if (e.getID() == KeyEvent.KEY_RELEASED && ((e.getModifiers() & KeyEvent.ALT_MASK) != 0) && (e.getKeyCode() == KeyEvent.VK_MINUS || e.getKeyCode() == 109)) {
			    			processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_OUT);
			            // Zoom Reset no espectrograma no eixo de frequência - 'ALT 0'
			    		} else if (e.getID() == KeyEvent.KEY_RELEASED && ((e.getModifiers() & KeyEvent.ALT_MASK) != 0) && (e.getKeyCode() == KeyEvent.VK_0 || e.getKeyCode() == KeyEvent.VK_NUMPAD0)) {
			    			processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_RESET);
			            }
		    		}
		            
		            return false;
		        }
		    });
			
			// Executa o método 'exitSystem()' ao clicar no botão fechar
			addWindowListener(new WindowAdapter() {
	            @Override
	            public void windowClosing(WindowEvent event) {
	            	exitSystem();
	            }
	        });
			
			// Cria a barra de menu
			setJMenuBar(createMenuBar());
			
			// Layout do frame principal
			getContentPane().setLayout(new MigLayout("insets 0", "[grow]", "[] 2 " +        // Barra de ferramentas
					                                                       "[grow] 2 "  +   // Painel de visualização de áudios
					                                                       "[62.00] 2 " +   // Barra de ferramentas de controles
					                                                       "[22.00]"));     // Rodapé
			
			// *********************************************************************************************
			// Barra de ferramentas
			{
				WasisPanelGradient panelToolBar = new WasisPanelGradient(WasisParameters.COLOR_BACKGROUND_PANEL_TOP_GRADIENT, WasisParameters.COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT);
				panelToolBar.setLayout(new MigLayout("insets 5", "[] 5 [] 5 [] 5 []", "6 [grow]"));
				
				getContentPane().add(panelToolBar, "cell 0 0, grow");
				
				// Tamanhos dos painéis da barra de ferramentas
				final int PANEL_TOOL_BAR_WIDTH_MIN = 120;
				final int PANEL_TOOL_BAR_WIDTH_MAX = 600;
				final int PANEL_TOOL_BAR_HEIGHT = 75;
				
				// *********************************************************************************************
				// Painel Biblioteca de Áudio
				WasisPanelRounded panelToolBarAudioLibrary = new WasisPanelRounded(rsBundle.getString("tool_bar_audio_library_description"));
				panelToolBarAudioLibrary.setMinimumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MIN, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarAudioLibrary.setMaximumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MAX, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarAudioLibrary.setLayout(new MigLayout("insets 0", "8 [] 3 [] 3 [] 8", "[grow]"));
				
				// Nova Biblioteca de Áudio
				WasisButton btnNewAudioLibrary = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("tool_bar_audio_library_new_audio_library_button_tool_tip"), new ImageIcon("res/images/toolbar/new_library.png"));
				btnNewAudioLibrary.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						createAudioLibrary();
					}
				});
				
				// Abrir Biblioteca de Áudio
				WasisButton btnOpenAudioLibrary = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("tool_bar_audio_library_open_audio_library_button_tool_tip"), new ImageIcon("res/images/toolbar/open_library.png"));
				btnOpenAudioLibrary.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						openAudioLibrary();
					}
				});
				
				// Salvar Biblioteca de Áudio
				WasisButton btnSaveAudioLibrary = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("tool_bar_audio_library_save_audio_library_button_tool_tip"), new ImageIcon("res/images/toolbar/save_library.png"));
				btnSaveAudioLibrary.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						saveAudioLibrary();
					}
				});
				
				// *********************************************************************************************
				// Painel Audio
				WasisPanelRounded panelToolBarAudio = new WasisPanelRounded(rsBundle.getString("tool_bar_audio_description"));
				panelToolBarAudio.setMinimumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MIN, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarAudio.setMaximumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MAX, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarAudio.setLayout(new MigLayout("insets 0", "8 [] 3 [] 3 [] 8", "[grow]"));
				
				// Abrir Arquivo de Áudio
				WasisButton btnOpenAudioFile = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("tool_bar_audio_open_audio_file_button_tool_tip"), new ImageIcon("res/images/toolbar/open_audio.png"));
				btnOpenAudioFile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						openAudioFile();
					}
				});
				
				// Salvar Arquivo de Áudio
				WasisButton btnSaveAudioFile = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("tool_bar_audio_save_audio_file_button_tool_tip"), new ImageIcon("res/images/toolbar/save_audio.png"));
				btnSaveAudioFile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						saveAudioFile();
					}
				});
				
				// Fechar Arquivo de Áudio
				WasisButton btnCloseAudioFile = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("tool_bar_audio_close_audio_file_button_tool_tip"), new ImageIcon("res/images/toolbar/close_audio.png"));
				btnCloseAudioFile.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						closeAudioFile();
					}
				});
				
				// *********************************************************************************************
				// Painel Parâmetros FFT
				WasisPanelRounded panelToolBarFFTParameters = new WasisPanelRounded(rsBundle.getString("tool_bar_fft_parameters"));
				panelToolBarFFTParameters.setMinimumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MIN, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarFFTParameters.setMaximumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MAX, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarFFTParameters.setLayout(new MigLayout("insets 0", "8 [] 3 [] 3 [] 8", "[grow]"));
				
				// FFT Samples
				cmbFFTSamples = new LTComboBoxField(rsBundle.getString("tool_bar_fft_samples"), true, true);
				for (int indexFFTSample = 0; indexFFTSample < FFTParameters.FFT_SAMPLES[0].length; indexFFTSample++) {
					cmbFFTSamples.addValues("" + (int) FFTParameters.FFT_SAMPLES[0][indexFFTSample], "" + (int) FFTParameters.FFT_SAMPLES[0][indexFFTSample]);
				}
				cmbFFTSamples.setValue("" + FFTParameters.getInstance().getFFTSampleSize());
				cmbFFTSamples.addFocusListener(new UpdateFFTParameters());
				
				// FFT Overlap
				txtFFTOverlap = new LTTextField(rsBundle.getString("tool_bar_fft_overlap"), LTDataTypes.INTEGER, true, false);
				txtFFTOverlap.setValue("" + FFTParameters.getInstance().getFFTOverlapFactor());
				txtFFTOverlap.addFocusListener(new UpdateFFTParameters());
				
				// FFT Window
				cmbFFTWindow = new LTComboBoxField(rsBundle.getString("tool_bar_fft_window_function"), true, true);
				for (int indexFFTWindow = 0; indexFFTWindow < FFTWindowFunction.WINDOW_FUNCTIONS[0].length; indexFFTWindow++) {
					cmbFFTWindow.addValues((String) FFTWindowFunction.WINDOW_FUNCTIONS[0][indexFFTWindow], (String) FFTWindowFunction.WINDOW_FUNCTIONS[0][indexFFTWindow]);
				}
				cmbFFTWindow.setValue(FFTParameters.getInstance().getFFTWindowFunction());
				cmbFFTWindow.addFocusListener(new UpdateFFTParameters());

				// *********************************************************************************************
				// Painel de Identificação
				WasisPanelRounded panelToolBarIdentification = new WasisPanelRounded(rsBundle.getString("tool_bar_identification_description"));
				panelToolBarIdentification.setMinimumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MIN, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarIdentification.setMaximumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MAX, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarIdentification.setLayout(new MigLayout("insets 0", "8 [] 8", "[grow]"));
				
				// Classificação de Áudio - Bruta Força
				WasisButton btnClassificationBruteForce = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("menu_identification_brute_force"), new ImageIcon("res/images/toolbar/compare_brute_force.png"));
				btnClassificationBruteForce.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						audioClassificationBruteForce();
					}
				});
				
				// Classificação de Áudio - Modelo de Classe
				WasisButton btnClassificationClassModel = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("menu_identification_class_model"), new ImageIcon("res/images/toolbar/compare_class_model.png"));
				btnClassificationClassModel.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						audioClassificationClassModel();
					}
				});
				
				// *********************************************************************************************
				// Painel de Atualização
				WasisPanelRounded panelToolBarUpdate = new WasisPanelRounded(rsBundle.getString("tool_bar_update_description"));
				panelToolBarUpdate.setMinimumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MIN, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarUpdate.setMaximumSize(new Dimension(PANEL_TOOL_BAR_WIDTH_MAX, PANEL_TOOL_BAR_HEIGHT));
				panelToolBarUpdate.setLayout(new MigLayout("insets 0", "8 [] 3 [] 8", "[grow]"));
				
				// Atualizar Banco de Dados
				WasisButton btnUpdateDatabase = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("menu_update_database"), new ImageIcon("res/images/toolbar/update.png"));
				btnUpdateDatabase.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						updateDatabase();
					}
				});
				
				// Atualizar Banco de Dados
				WasisButton btnUpdateWasis = new WasisButton(WasisButton.BUTTON_TYPE_TOOLBAR, rsBundle.getString("menu_update_wasis"), new ImageIcon("res/images/update_wasis.png"));
				btnUpdateWasis.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						
					}
				});
				btnUpdateWasis.setEnabled(false);
				
				// *********************************************************************************************
				panelToolBar.add(panelToolBarAudioLibrary, "cell 0 0");
				panelToolBarAudioLibrary.add(btnNewAudioLibrary, "cell 0 0");
				panelToolBarAudioLibrary.add(btnOpenAudioLibrary, "cell 1 0");
				panelToolBarAudioLibrary.add(btnSaveAudioLibrary, "cell 2 0");
				
				panelToolBar.add(panelToolBarAudio, "cell 1 0");
				panelToolBarAudio.add(btnOpenAudioFile, "cell 0 0");
				panelToolBarAudio.add(btnSaveAudioFile, "cell 1 0");
				panelToolBarAudio.add(btnCloseAudioFile, "cell 2 0");
				
				panelToolBar.add(panelToolBarFFTParameters, "cell 2 0");
				panelToolBarFFTParameters.add(cmbFFTSamples, "cell 0 0, width 75, gap 4 0 0 4");
				panelToolBarFFTParameters.add(txtFFTOverlap, "cell 0 0, width 75, gap 4 0 0 4");
				panelToolBarFFTParameters.add(cmbFFTWindow, "cell 0 0, width 150, gap 4 0 0 4");
				
				panelToolBar.add(panelToolBarIdentification, "cell 3 0");
				panelToolBarIdentification.add(btnClassificationBruteForce, "cell 0 0, gap 3 0 0 0");
				panelToolBarIdentification.add(btnClassificationClassModel, "cell 0 0, gap 5 2 0 0");
				
				//panelToolBar.add(panelToolBarUpdate, "cell 3 0");
				//panelToolBarUpdate.add(btnUpdateDatabase, "cell 0 0, gap 3 0 0 0");
				//panelToolBarUpdate.add(btnUpdateWasis, "cell 0 0, gap 5 2 0 0");
			}
			
			// *********************************************************************************************
			// Visualização dos arquivos de áudio
			// Inclui biblioteca, waveform e espectrograma
			{
				WasisPanelGradient panelAudioLibrary = new WasisPanelGradient(WasisParameters.COLOR_BACKGROUND_PANEL_TOP_GRADIENT, WasisParameters.COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT);
				panelAudioLibrary.setLayout(new MigLayout("insets 5", "[grow]", "[grow]"));
				
				// *********************************************************************************************
				// Biblioteca de áudio
				objAudioLibraryController = new AudioLibraryController();
				objAudioLibraryController.setMinimumSize(new Dimension(263, 25));
				objAudioLibraryController.addAudioLibraryListener(Wasis.this);
				
				panelAudioLibrary.add(objAudioLibraryController, "cell 0 0, grow");
				
				// *********************************************************************************************
				// Painel de visualização do áudio (Waveform e Espectrograma)
				WasisPanelGradient panelAudioVisualization = new WasisPanelGradient(WasisParameters.COLOR_BACKGROUND_PANEL_TOP_GRADIENT, WasisParameters.COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT);
				panelAudioVisualization.setLayout(new MigLayout("insets 5", "[grow] 2 [10.00]", "[90.00] 2 [grow]"));
				panelAudioVisualization.setMinimumSize(new Dimension(600, 25));
				
				// Scroll horizontal e vertical
				scrollBarHorizontalVisualization = new WasisScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
				scrollBarVerticalVisualization = new WasisScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
				
				// Painel scroll vertical
				panelScrollbarVerticalVisualization = new WasisPanel();
				panelScrollbarVerticalVisualization.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
				panelScrollbarVerticalVisualization.add(scrollBarVerticalVisualization, "cell 0 0, grow, gap 1 3 1 3");
				
				// Waveform
				panelWaveform = new WasisPanel();
				panelWaveform.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
				
				// Espectrograma
				panelSpectrogram = new WasisPanel();
				panelSpectrogram.setLayout(new MigLayout("insets 0", "[grow]", "[grow] 0 [10.00]"));
				panelSpectrogram.add(scrollBarHorizontalVisualization, "cell 0 1, grow, gap 1 2 0 3");
				
				// *********************************************************************************************
				// Painel de divisão entre o painel da biblioteca de áudio e o painel da visualização do áudio
				WasisInvisibleSplitPane panelSplit = new WasisInvisibleSplitPane(JSplitPane.HORIZONTAL_SPLIT, panelAudioLibrary, panelAudioVisualization);
				
				getContentPane().add(panelSplit, "cell 0 1, grow");
				
				panelAudioVisualization.add(panelWaveform, "cell 0 0, grow");
				panelAudioVisualization.add(panelSpectrogram, "cell 0 1, grow");
				panelAudioVisualization.add(panelScrollbarVerticalVisualization, "cell 1 0 1 2, grow");
			}
			
			// *********************************************************************************************
			// Barra de ferramentas 
			{
				// *********************************************************************************************
				// Painel de controle
				// Player, zoom
				WasisPanelGradient panelControlToolBar = new WasisPanelGradient(WasisParameters.COLOR_BACKGROUND_PANEL_TOP_GRADIENT, WasisParameters.COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT);
				panelControlToolBar.setLayout(new MigLayout("insets 5", "[] 5 []", "3 [grow] 3"));
				
				getContentPane().add(panelControlToolBar, "cell 0 2");
				
				// *********************************************************************************************
				// Player
				WasisPanelRounded panelPlayer = new WasisPanelRounded("");
				panelPlayer.setLayout(new MigLayout("insets 0", "8 [] 3 [] 3 [] 8", "[grow]"));
				
				// Play
				WasisButton btnPlayerPlay = new WasisButton(WasisButton.BUTTON_TYPE_PLAYER, rsBundle.getString("player_play"), new ImageIcon("res/images/player/play.png"));
				btnPlayerPlay.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						playAudio();
					}
				});
				
				// Pause
				WasisButton btnPlayerPause = new WasisButton(WasisButton.BUTTON_TYPE_PLAYER, rsBundle.getString("player_pause"), new ImageIcon("res/images/player/pause.png"));
				btnPlayerPause.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						pauseAudio();
					}
				});
				
				// Stop
				WasisButton btnPlayerStop = new WasisButton(WasisButton.BUTTON_TYPE_PLAYER, rsBundle.getString("player_stop"), new ImageIcon("res/images/player/stop.png"));
				btnPlayerStop.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						stopAudio();
					}
				});

				panelControlToolBar.add(panelPlayer, "cell 0 0, grow");
				panelPlayer.add(btnPlayerPlay, "cell 0 0");
				panelPlayer.add(btnPlayerPause, "cell 1 0");
				panelPlayer.add(btnPlayerStop, "cell 2 0");
				
				// *********************************************************************************************
				// Zoom
				WasisPanelRounded panelZoom = new WasisPanelRounded("");
				panelZoom.setLayout(new MigLayout("insets 0", "8 [] 2 [] 2 [] 8", "3 [] 2 [] 3"));
				
				// Zoom In - Tempo
				WasisButton btnZoomInTime = new WasisButton(WasisButton.BUTTON_TYPE_ZOOM, rsBundle.getString("zoom_in_time"), new ImageIcon("res/images/zoom/zoom-in-time.png"));
				btnZoomInTime.setToolTipText(rsBundle.getString("zoom_in_time"));
				btnZoomInTime.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						processAudioZoomTimeFrequency(ZOOM_TIME, ZOOM_IN);
					}
				});
				
				// Zoom Out - Tempo
				WasisButton btnZoomOutTime = new WasisButton(WasisButton.BUTTON_TYPE_ZOOM, rsBundle.getString("zoom_out_time"), new ImageIcon("res/images/zoom/zoom-out-time.png"));
				btnZoomOutTime.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						processAudioZoomTimeFrequency(ZOOM_TIME, ZOOM_OUT);
					}
				});
				
				// Zoom Reset - Tempo
				WasisButton btnZoomResetTime = new WasisButton(WasisButton.BUTTON_TYPE_ZOOM, rsBundle.getString("zoom_reset_time"), new ImageIcon("res/images/zoom/zoom-reset-time.png"));
				btnZoomResetTime.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						processAudioZoomTimeFrequency(ZOOM_TIME, ZOOM_RESET);
					}
				});
				
				// Zoom In - Frequência
				WasisButton btnZoomInFrequency = new WasisButton(WasisButton.BUTTON_TYPE_ZOOM, rsBundle.getString("zoom_in_frequency"), new ImageIcon("res/images/zoom/zoom-in-frequency.png"));
				btnZoomInFrequency.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_IN);
					}
				});
				
				// Zoom Out - Frequência
				WasisButton btnZoomOutFrequency = new WasisButton(WasisButton.BUTTON_TYPE_ZOOM, rsBundle.getString("zoom_out_frequency"), new ImageIcon("res/images/zoom/zoom-out-frequency.png"));
				btnZoomOutFrequency.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_OUT);
					}
				});
				
				// Zoom Reset - Frequência
				WasisButton btnZoomResetFrequency = new WasisButton(WasisButton.BUTTON_TYPE_ZOOM, rsBundle.getString("zoom_reset_frequency"), new ImageIcon("res/images/zoom/zoom-reset-frequency.png"));
				btnZoomResetFrequency.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_RESET);
					}
				});
				
				panelControlToolBar.add(panelZoom, "cell 1 0, grow");
				panelZoom.add(btnZoomInTime, "cell 0 0");
				panelZoom.add(btnZoomOutTime, "cell 1 0");
				panelZoom.add(btnZoomResetTime, "cell 2 0");
				panelZoom.add(btnZoomInFrequency, "cell 0 1");
				panelZoom.add(btnZoomOutFrequency, "cell 1 1");
				panelZoom.add(btnZoomResetFrequency, "cell 2 1");
			
				// *********************************************************************************************
				// Ferramentas de visualização 
				// Tempo atual, visualizado e selecionado
				WasisPanelGradient panelVisualizationToolBar = new WasisPanelGradient(WasisParameters.COLOR_BACKGROUND_PANEL_TOP_GRADIENT, WasisParameters.COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT);
				panelVisualizationToolBar.setLayout(new MigLayout("insets 5", "[grow] 5 [500.00]", "3 [grow] 3"));
				
				getContentPane().add(panelVisualizationToolBar, "cell 0 2, grow, gap 2 0 0 0");
				
				// *********************************************************************************************
				// Tempo atual
				WasisPanelRounded panelCurrentTime = new WasisPanelRounded("");
				panelCurrentTime.setLayout(new MigLayout("insets 0", "3 [grow] 3", "[grow]"));
				
				lblCurrentTime = new JLabel(ClockTransformations.millisecondsIntoDigitalFormat(0));
				lblCurrentTime.setFont(new Font("Tahoma", Font.BOLD, 30));
				lblCurrentTime.setVerticalAlignment(SwingConstants.CENTER);
				lblCurrentTime.setHorizontalAlignment(SwingConstants.CENTER);
				
				panelVisualizationToolBar.add(panelCurrentTime, "cell 0 0, grow");
				panelCurrentTime.add(lblCurrentTime, "cell 0 0, grow, gap 0 0 0 1");
				
				// *********************************************************************************************
				// Visão / Seleção ativa - Tempo
				WasisPanelRounded panelViewSelectionTime = new WasisPanelRounded("");
				panelViewSelectionTime.setLayout(new MigLayout("insets 0", "8 [grow] 2 [75.00] 2 [75.00] 2 [75.00] 2 [60.00] 2 [60.00] 2 [60.00] 8", "3 [] 5 [] 5 [] 3"));
				
				// Descrição - Visão e Seleção Ativa
				lblLabelView = new WasisLabel(rsBundle.getString("view_label"));
				lblLabelSelection = new WasisLabel(rsBundle.getString("selection_label"));
				lblLabelSelection.setDashedBorder(true);
				
				// Descrição - Tempo
				lblInitialTimeLabel = new WasisLabel(rsBundle.getString("start_label"));
				lblFinalTimeLabel = new WasisLabel(rsBundle.getString("end_label"));
				lblLengthTimeLabel = new WasisLabel(rsBundle.getString("length_label"));
				
				// Descrição - Frequência
				lblInitialFrequencyLabel = new WasisLabel(rsBundle.getString("start_label"));
				lblFinalFrequencyLabel = new WasisLabel(rsBundle.getString("end_label"));
				lblLengthFrequencyLabel = new WasisLabel(rsBundle.getString("length_label"));

				// Visão - Tempo
				lblInitialTimeView = new WasisLabel(ClockTransformations.millisecondsIntoDigitalFormat(0));
				lblFinalTimeView = new WasisLabel(ClockTransformations.millisecondsIntoDigitalFormat(0));
				lblLengthTimeView = new WasisLabel(ClockTransformations.millisecondsIntoDigitalFormat(0));

				// Seleção ativa - Tempo
				lblInitialTimeSelection = new WasisLabel(ClockTransformations.millisecondsIntoDigitalFormat(0));
				lblFinalTimeSelection = new WasisLabel(ClockTransformations.millisecondsIntoDigitalFormat(0));
				lblLengthTimeSelection = new WasisLabel(ClockTransformations.millisecondsIntoDigitalFormat(0));

				// Visão - Frequência
				lblInitialFrequencyView = new WasisLabel("0 Hz");
				lblFinalFrequencyView = new WasisLabel("0 Hz");
				lblLengthFrequencyView = new WasisLabel("0 Hz");

				// Seleção ativa - Frequência
				lblInitialFrequencySelection = new WasisLabel("0 Hz");
				lblFinalFrequencySelection = new WasisLabel("0 Hz");
				lblLengthFrequencySelection = new WasisLabel("0 Hz");

				panelVisualizationToolBar.add(panelViewSelectionTime, "cell 1 0, grow");
				
				panelViewSelectionTime.add(lblLabelView, "cell 0 1, grow");
				panelViewSelectionTime.add(lblLabelSelection, "cell 0 2, grow");
				
				panelViewSelectionTime.add(lblInitialTimeLabel, "cell 1 0, grow");
				panelViewSelectionTime.add(lblFinalTimeLabel, "cell 2 0, grow");
				panelViewSelectionTime.add(lblLengthTimeLabel, "cell 3 0, grow");
				
				panelViewSelectionTime.add(lblInitialTimeView, "cell 1 1, grow");
				panelViewSelectionTime.add(lblFinalTimeView, "cell 2 1, grow");
				panelViewSelectionTime.add(lblLengthTimeView, "cell 3 1, grow");
				
				panelViewSelectionTime.add(lblInitialTimeSelection, "cell 1 2, grow");
				panelViewSelectionTime.add(lblFinalTimeSelection, "cell 2 2, grow");
				panelViewSelectionTime.add(lblLengthTimeSelection, "cell 3 2, grow");
				
				panelViewSelectionTime.add(lblInitialFrequencyLabel, "cell 4 0, grow");
				panelViewSelectionTime.add(lblFinalFrequencyLabel, "cell 5 0, grow");
				panelViewSelectionTime.add(lblLengthFrequencyLabel, "cell 6 0, grow");
				
				panelViewSelectionTime.add(lblInitialFrequencyView, "cell 4 1, grow");
				panelViewSelectionTime.add(lblFinalFrequencyView, "cell 5 1, grow");
				panelViewSelectionTime.add(lblLengthFrequencyView, "cell 6 1, grow");
				
				panelViewSelectionTime.add(lblInitialFrequencySelection, "cell 4 2, grow");
				panelViewSelectionTime.add(lblFinalFrequencySelection, "cell 5 2, grow");
				panelViewSelectionTime.add(lblLengthFrequencySelection, "cell 6 2, grow");
			}
			
			// *********************************************************************************************
			// Rodapé
			{
				WasisPanelGradient panelFooter = new WasisPanelGradient(WasisParameters.COLOR_BACKGROUND_PANEL_TOP_GRADIENT, WasisParameters.COLOR_BACKGROUND_PANEL_BOTTOM_GRADIENT);
				panelFooter.setLayout(new MigLayout("insets 0", "5.00 [] 10.00 [] 10.00 [grow] 10.00 [] 10.00 [] 5.00", "[grow]"));

				JLabel lblVersion = new JLabel(rsBundle.getString("software_version") + " " + WASIS_VERSION);
				lblVersion.setFont(LTParameters.getInstance().getFontComponentLabel());
				
				ImageIcon iconFooterDivision = new ImageIcon("res/images/footer_division.png");
		 		JLabel lblFooterDivision = new JLabel();
		 		lblFooterDivision.setIcon(iconFooterDivision);
		 		lblFooterDivision.setVisible(true);
		 		
		 		WasisParameters.getInstance().checkDatabaseConnection();
		 		
				JLabel lblDatabaseConnection = new JLabel("      " + rsBundle.getString("database_connection") + " " + WasisParameters.getInstance().getDatabaseEngine() + " - " + WasisParameters.getInstance().getDatabaseServer()) {
					private static final long serialVersionUID = -1477135321972453268L;
					
					@Override
					public void paintComponent(Graphics g) {
						 super.paintComponent(g);
					     
						 if (WasisParameters.getInstance().getDatabaseStatus()) {
							 g.setColor(new Color(0, 200, 0));
							 setToolTipText(rsBundle.getString("database_connection_established"));
						 } else {
							 g.setColor(new Color(200, 0, 0));
							 setToolTipText(rsBundle.getString("database_connection_failed"));
						 }
						 
					     g.drawOval(0, 3, 10, 10);
					     g.fillOval(0, 3, 10, 10);
					}
				};
				lblDatabaseConnection.setFont(LTParameters.getInstance().getFontComponentLabel());
				
				// A cada 15 segundos verifica se a conexão com o banco de dados está ativa e exibe no rodapé
				Timer timer = new Timer(15000, new ActionListener() {
				    @Override
				    public void actionPerformed(ActionEvent e) {
				    	WasisParameters.getInstance().checkDatabaseConnection();
				    	lblDatabaseConnection.repaint();
				    }
				});
				timer.start();
				
		 		JLabel lblFooterDivision2 = new JLabel();
		 		lblFooterDivision2.setIcon(iconFooterDivision);
		 		lblFooterDivision2.setVisible(true);
				
				lblMousePosition = new JLabel();
				lblMousePosition.setHorizontalAlignment(SwingConstants.RIGHT);
				
				getContentPane().add(panelFooter, "cell 0 3, grow");
				
				panelFooter.add(lblVersion, "cell 0 0, aligny center");
				panelFooter.add(lblFooterDivision, "cell 1 0, aligny center");
				panelFooter.add(lblDatabaseConnection, "cell 2 0, aligny center");
				panelFooter.add(lblFooterDivision2, "cell 3 0, aligny center");
				panelFooter.add(lblMousePosition, "cell 4 0, width 150.00, gap 0 22 0 0");
			}

			setVisible(true);
			requestFocusInWindow();
			
        } catch(Exception e) {
        	e.printStackTrace();
        }
	}
	
	/**
	 * Cria a barra de menus.
	 * 
	 * @return menuBar
	 */
	private JMenuBar createMenuBar() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.add(Box.createHorizontalStrut(7));
		
		// Menu File
		{
	 		JMenu menuFile = new JMenu(rsBundle.getString("menu_file"));
	 		
	 		// Nova Biblioteca de Áudio
	 		JMenuItem menuFileNewLibrary = new JMenuItem(rsBundle.getString("menu_file_new_audio_library"));
	 		menuFileNewLibrary.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				createAudioLibrary();
	 			}
	        });
	 		
	 		// Abrir Biblioteca de Áudio
	 		JMenuItem menuFileOpenLibrary = new JMenuItem(rsBundle.getString("menu_file_open_audio_library"));
	 		menuFileOpenLibrary.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				openAudioLibrary();
	 			}
	        });
	 		
	 		// Salvar Biblioteca de Áudio
	 		JMenuItem menuFileSaveLibrary = new JMenuItem(rsBundle.getString("menu_file_save_audio_library"));
	 		menuFileSaveLibrary.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				saveAudioLibrary();
	 			}
	        });

	 		// Abrir Arquivo de Áudio
	 		JMenuItem menuFileOpenAudioFile = new JMenuItem(rsBundle.getString("menu_file_open_audio_file"));
	 		menuFileOpenAudioFile.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				openAudioFile();
	 			}
	        });
	 		
	 		// Salvar Arquivo de Áudio
	 		JMenuItem menuFileSaveAudioFile = new JMenuItem(rsBundle.getString("menu_file_save_audio_file"));
	 		menuFileSaveAudioFile.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				saveAudioFile();
	 			}
	        });
	 		
	 		// Fechar Arquivo de Áudio
	 		JMenuItem menuFileCloseAudioFile = new JMenuItem(rsBundle.getString("menu_file_close_audio_file"));
	 		menuFileCloseAudioFile.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				closeAudioFile();
	 			}
	        });

	 		// Sair
	 		JMenuItem menuFileExit = new JMenuItem(rsBundle.getString("menu_file_exit"));
	 		menuFileExit.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				exitSystem();
	            }
	        });
	 		
	 		menuBar.add(menuFile);
	 		menuFile.add(menuFileNewLibrary);
	 		menuFile.add(menuFileOpenLibrary);
	 		menuFile.add(menuFileSaveLibrary);
	 		menuFile.addSeparator();
	 		menuFile.add(menuFileOpenAudioFile);
	 		menuFile.add(menuFileSaveAudioFile);
	 		menuFile.add(menuFileCloseAudioFile);
	 		menuFile.addSeparator();
	 		menuFile.add(menuFileExit);
		}
		
		menuBar.add(Box.createHorizontalStrut(7));
		
		// Menu Exibição
		{
			JMenu menuView = new JMenu(rsBundle.getString("menu_view"));
			
			// Full Waveform
			JCheckBoxMenuItem menuViewFullWaveform = new JCheckBoxMenuItem(rsBundle.getString("menu_view_full_waveform"));
			menuViewFullWaveform.setSelected(WasisParameters.getInstance().getFullWaveform());
			menuViewFullWaveform.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				if (objWaveformGraphicPanel != null) {
		 				if (menuViewFullWaveform.isSelected()) {
		 					WasisParameters.getInstance().setFullWaveform(true);
		 					objWaveformGraphicPanel.setViewFullWaveform(true);
		 				} else {
		 					WasisParameters.getInstance().setFullWaveform(false);
		 					objWaveformGraphicPanel.setViewFullWaveform(false);
		 				}
	 				}
	 				
	 				processWaveformVisualization();
	            }
	        });
			
			// Spectrogram Color Display
			JMenu menuSpectrogramColorDisplay = new JMenu(rsBundle.getString("menu_view_spectrogram_color_display"));
			
			// Grayscale
	 		JMenuItem menuSpectrogramColorDisplayGrayscale = new JMenuItem(rsBundle.getString("menu_view_spectrogram_color_display_grayscale"), new ImageIcon("res/images/spectrogram/grayscale.png"));
	 		menuSpectrogramColorDisplayGrayscale.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				if (objAudioWav != null) {
	 					if (!WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE)) {
	 						processSpectrogramColorDisplay(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE);
	 					}
	 				}
	            }
	        });
	 		
	 		// Grayscale Reverse
	 		JMenuItem menuSpectrogramColorDisplayGrayscaleReverse = new JMenuItem(rsBundle.getString("menu_view_spectrogram_color_display_grayscale_reverse"), new ImageIcon("res/images/spectrogram/grayscale-reverse.png"));
	 		menuSpectrogramColorDisplayGrayscaleReverse.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				if (objAudioWav != null) {
	 					if (!WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE_REVERSE)) {
	 						processSpectrogramColorDisplay(SpectrogramColorDisplay.SPECTROGRAM_GRAYSCALE_REVERSE);
	 					}
	 				}
	            }
	        });
	 		
	 		// Gradient 1
	 		JMenuItem menuSpectrogramColorDisplayGradient1 = new JMenuItem(rsBundle.getString("menu_view_spectrogram_color_display_gradient_1"), new ImageIcon("res/images/spectrogram/gradient-1.png"));
	 		menuSpectrogramColorDisplayGradient1.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				if (objAudioWav != null) {
	 					if (!WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_1)) {
	 						processSpectrogramColorDisplay(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_1);
	 					}
	 				}
	            }
	        });
	 		
	 		// Gradient 2
	 		JMenuItem menuSpectrogramColorDisplayGradient2 = new JMenuItem(rsBundle.getString("menu_view_spectrogram_color_display_gradient_2"), new ImageIcon("res/images/spectrogram/gradient-2.png"));
	 		menuSpectrogramColorDisplayGradient2.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				if (objAudioWav != null) {
	 					if (!WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_2)) {
	 						processSpectrogramColorDisplay(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_2);
	 					}
	 				}
	            }
	        });
	 		
	 		// Gradient 3
	 		JMenuItem menuSpectrogramColorDisplayGradient3 = new JMenuItem(rsBundle.getString("menu_view_spectrogram_color_display_gradient_3"), new ImageIcon("res/images/spectrogram/gradient-3.png"));
	 		menuSpectrogramColorDisplayGradient3.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				if (objAudioWav != null) {
	 					if (!WasisParameters.getInstance().getSpectrogramColorDisplay().equals(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_3)) {
	 						processSpectrogramColorDisplay(SpectrogramColorDisplay.SPECTROGRAM_GRADIENT_3);
	 					}
	 				}
	            }
	        });
	 		
	 		// Zoom In Time
	 		JMenuItem menuZoomInTime = new JMenuItem(rsBundle.getString("zoom_in_time"));
	 		menuZoomInTime.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('+'), Event.CTRL_MASK));
	 		menuZoomInTime.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				processAudioZoomTimeFrequency(ZOOM_TIME, ZOOM_IN);
	            }
	        });
	 		
	 		// Zoom Out Time
	 		JMenuItem menuZoomOutTime = new JMenuItem(rsBundle.getString("zoom_out_time"));
	 		menuZoomOutTime.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('-'), ActionEvent.CTRL_MASK));
	 		menuZoomOutTime.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				processAudioZoomTimeFrequency(ZOOM_TIME, ZOOM_OUT);
	            }
	        });
	 		
	 		// Zoom Reset Time
	 		JMenuItem menuZoomResetTime = new JMenuItem(rsBundle.getString("zoom_reset_time"));
	 		menuZoomResetTime.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, ActionEvent.CTRL_MASK));
	 		menuZoomResetTime.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				processAudioZoomTimeFrequency(ZOOM_TIME, ZOOM_RESET);
	            }
	        });
	 		
	 		// Zoom In Frequency
	 		JMenuItem menuZoomInFrequency = new JMenuItem(rsBundle.getString("zoom_in_frequency"));
	 		menuZoomInFrequency.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('+'), Event.ALT_MASK));
	 		menuZoomInFrequency.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_IN);
	            }
	        });
	 		
	 		// Zoom Out Frequency
	 		JMenuItem menuZoomOutFrequency = new JMenuItem(rsBundle.getString("zoom_out_frequency"));
	 		menuZoomOutFrequency.setAccelerator(KeyStroke.getKeyStroke(Character.valueOf('-'), ActionEvent.ALT_MASK));
	 		menuZoomOutFrequency.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_OUT);
	            }
	        });
	 		
	 		// Zoom Reset Frequency
	 		JMenuItem menuZoomResetFrequency = new JMenuItem(rsBundle.getString("zoom_reset_frequency"));
	 		menuZoomResetFrequency.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_0, ActionEvent.ALT_MASK));
	 		menuZoomResetFrequency.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				processAudioZoomTimeFrequency(ZOOM_FREQUENCY, ZOOM_RESET);
	            }
	        });
			
			menuBar.add(menuView);
			menuView.add(menuViewFullWaveform);
			menuView.addSeparator();
			menuView.add(menuSpectrogramColorDisplay);
			menuSpectrogramColorDisplay.add(menuSpectrogramColorDisplayGrayscale);
			menuSpectrogramColorDisplay.add(menuSpectrogramColorDisplayGrayscaleReverse);
			menuSpectrogramColorDisplay.add(menuSpectrogramColorDisplayGradient1);
			menuSpectrogramColorDisplay.add(menuSpectrogramColorDisplayGradient2);
			menuSpectrogramColorDisplay.add(menuSpectrogramColorDisplayGradient3);
			menuView.addSeparator();
			menuView.add(menuZoomInTime);
			menuView.add(menuZoomOutTime);
			menuView.add(menuZoomResetTime);
			menuView.add(menuZoomInFrequency);
			menuView.add(menuZoomOutFrequency);
			menuView.add(menuZoomResetFrequency);
		}
		
		menuBar.add(Box.createHorizontalStrut(7));
		
		// Menu Identificação
		{
			JMenu menuIdentification = new JMenu(rsBundle.getString("menu_identification"));
			
			// Força Bruta
	 		JMenuItem menuIdentificationBruteForce = new JMenuItem(rsBundle.getString("menu_identification_brute_force"));
	 		menuIdentificationBruteForce.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				audioClassificationBruteForce();
	            }
	        });
	 		
	 		// Modelo de Classe
	 		JMenuItem menuIdentificationClassModel = new JMenuItem(rsBundle.getString("menu_identification_class_model"));
	 		menuIdentificationClassModel.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				audioClassificationClassModel();
	            }
	        });
	 		
	 		// Treinar
	 		JMenuItem menuClassifierModelBuilder = new JMenuItem(rsBundle.getString("menu_train_classifier_model_builder"));
	 		menuClassifierModelBuilder.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				classifierModelBuilder();
	            }
	        });
	 		
	 		menuBar.add(menuIdentification);
	 		menuIdentification.add(menuIdentificationBruteForce);
	 		menuIdentification.add(menuIdentificationClassModel);
	 		menuIdentification.addSeparator();
	 		menuIdentification.add(menuClassifierModelBuilder);
		}
		
		menuBar.add(Box.createHorizontalStrut(7));
		
		// Menu Atualizar
		{
			JMenu menuUpdate = new JMenu(rsBundle.getString("menu_update"));
			
			// Atualizar Banco de Dados
	 		JMenuItem menuUpdateDatabase = new JMenuItem(rsBundle.getString("menu_update_database"));
	 		menuUpdateDatabase.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				updateDatabase();
	            }
	        });
	 		menuUpdateDatabase.setEnabled(false);
	 		
			// Atualizar Wasis
	 		JMenuItem menuUpdateWasis = new JMenuItem(rsBundle.getString("menu_update_wasis"));
	 		menuUpdateWasis.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				
	            }
	        });
	 		menuUpdateWasis.setEnabled(false);
	 		
	 		menuBar.add(menuUpdate);
	 		menuUpdate.add(menuUpdateDatabase);
	 		menuUpdate.add(menuUpdateWasis);
		}
		
		menuBar.add(Box.createHorizontalStrut(7));
		
		// Menu Help
		{
	 		JMenu menuHelp = new JMenu(rsBundle.getString("menu_help"));
	 		
	 		// Manual
	 		JMenuItem menuHelpManual = new JMenuItem(rsBundle.getString("menu_help_manual"));
	 		menuHelpManual.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				openManual();
	            }
	        });
	 		
	 		// About
	 		JMenuItem menuHelpAbout = new JMenuItem(rsBundle.getString("menu_help_about"));
	 		menuHelpAbout.addActionListener(new ActionListener() {
	 			public void actionPerformed(ActionEvent event) {
	 				openAbout();
	            }
	        });
	 		
	 		menuBar.add(menuHelp);
	 		menuHelp.add(menuHelpManual);
	 		menuHelp.addSeparator();
	 		menuHelp.add(menuHelpAbout);
		}
	 		
 		return menuBar;
	}
	
	/**
	 * Cria uma nova biblioteca de áudio.
	 */
	private void createAudioLibrary() {
		// Verifica se tem arquivos de áudio abertos de alguma biblioteca não gravada
		if (objAudioLibraryController.getAudioLibrary().getIdAudioLibrary() == 0) {
			if (objAudioLibraryController.getListModelAudioLibrary().getSize() > 0) {
				int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("audio_library_create_while_not_saved"), WasisMessageBox.YES_NO_OPTION);
				
				// Deleta todo o conteúdo existente na biblioteca
				if (intDialogResult == WasisMessageBox.YES_OPTION) {
					objAudioLibraryController.clearAudioLibrary();
					resetScreenValues();	
				}
			}
		} else {
			if (objAudioLibraryController.getListModelAudioLibrary().getSize() > 0) {
				int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("audio_library_delete_all_content"), WasisMessageBox.YES_NO_OPTION);
				
				// Deleta todo o conteúdo existente na biblioteca
				if (intDialogResult == WasisMessageBox.YES_OPTION) {
					objAudioLibraryController.clearAudioLibrary();
					resetScreenValues();
				}
			}
		}
	}
	
	/**
	 * Abre uma biblioteca de áudio existente no banco de dados.
	 */
	private void openAudioLibrary() {
		boolean blnSaveAudioLibrary = false;
		
		// Verifica se tem arquivos de áudio abertos de alguma biblioteca não gravada
		if (objAudioLibraryController.getAudioLibrary().getIdAudioLibrary() == 0) {
			if (objAudioLibraryController.getListModelAudioLibrary().getSize() > 0) {
				int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("audio_library_open_while_not_saved"), WasisMessageBox.YES_NO_OPTION);
				
				// Abre a tela para a gravação da nova biblioteca de áudio
				if (intDialogResult == WasisMessageBox.YES_OPTION) {
					blnSaveAudioLibrary = true;
					
					saveAudioLibrary();
				}
			}
		}
		
		if (!blnSaveAudioLibrary) {
			ScreenOpenAudioLibrary objScreenOpenAudioLibrary = new ScreenOpenAudioLibrary(objAudioLibraryController);
			objScreenOpenAudioLibrary.showScreen();
			
			if (objAudioLibraryController.getAudioLibrary().getIdAudioLibrary() != 0) {
				resetScreenValues();
			}
		}
	}
	
	/**
	 * Salva dados da biblioteca de áudio.
	 */
	private void saveAudioLibrary() {
		ScreenSaveAudioLibrary objScreenSaveAudioLibrary = new ScreenSaveAudioLibrary(objAudioLibraryController);
		objScreenSaveAudioLibrary.showScreen();
	}

    /**
     * Implementa um <i>FocusListener</i> responsável pela atualização dos parâmetros de FFT quando solicitado.
     */
    private class UpdateFFTParameters implements FocusListener {
		@Override
		public void focusGained(FocusEvent event) {
			
		}

		@Override
		public void focusLost(FocusEvent event) {
			int intFFTSamples = Integer.parseInt(cmbFFTSamples.getValue().toString());
			int intFFTOverlap = (int) txtFFTOverlap.getValue();
			String strFFTWindow = (String) cmbFFTWindow.getValue().toString();
			
			if (intFFTOverlap >= 100) {
				txtFFTOverlap.setValue(FFTParameters.getInstance().getFFTOverlapFactor());
				WasisMessageBox.showMessageDialog(rsBundle.getString("tool_bar_fft_overlap_invalid_value"), WasisMessageBox.WARNING_MESSAGE);
				
			} else {
				// Só realiza o recarregamento do áudio se o valor das amostras, valor do overlap ou função de janelamento foram alterados
				if (objAudioWav != null) {
					if (intFFTSamples != FFTParameters.getInstance().getFFTSampleSize() || intFFTOverlap != FFTParameters.getInstance().getFFTOverlapFactor() || !strFFTWindow.equals(FFTParameters.getInstance().getFFTWindowFunction())) {
						FFTParameters.getInstance().setFFTSampleSize(intFFTSamples);
			            FFTParameters.getInstance().setFFTOverlapFactor(intFFTOverlap);
			            FFTParameters.getInstance().setFFTWindowFunction(strFFTWindow);
						
			            processAudio();
					}
				}
			}
		}
    }
    
    /**
     * Caso o espectrograma já esteja sendo visualizado na tela, é feita novamente a renderização.
     */
    private void processAudio() {
    	final WasisDialogLoadingData objWasisDialogLoadingAudioFile = new WasisDialogLoadingData(rsBundle.getString("message_loading_audio_file"));
		
		SwingWorker<Void, Void> swingWorkerRenderSpectrogram = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					// Mostra uma caixa de diálogo para o usuário perceber que o carregamento do arquivo está sendo feito
					SwingUtilities.invokeLater(new Runnable() {
						@Override
						public void run() {
							objWasisDialogLoadingAudioFile.showScreen();
						}
					});
					
					objSpectrogram.renderSpectrogram();

					objSpectrogramGraphicPanel.repaint();

					objWasisDialogLoadingAudioFile.disableScreen();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
		};

		swingWorkerRenderSpectrogram.execute();
    }
    
    /**
     * Atualiza a visualização da barra de scroll horizontal do espectrograma (eixo do tempo).
     */
    private void updateHorizontalScrollBar() {
    	if (objAudioWav != null && objSpectrogram != null && blnDrawScrollbarHorizontal) {
    		// Remove a scrollbar horizontal se já houver alguma
    		if (scrollBarHorizontalVisualization.getParent() == panelSpectrogram) {
    			panelSpectrogram.remove(scrollBarHorizontalVisualization);
    		}
    		
    		final int AUDIO_TOTAL_TIME = objAudioWav.getTotalTime();
    		
    		// Áudio completo sendo visualizado (scrollbar aparece desabilitada)
    		if (intFinalTimeView - intInitialTimeView >= AUDIO_TOTAL_TIME) {
    			scrollBarHorizontalVisualization = new WasisScrollBar(JScrollBar.HORIZONTAL, 0, 0, 0, 0);
    			
    		// Parte do áudio sendo visualizada
    		} else {
        		final int SCROLL_MINIMUM = 0;
        		final int SCROLL_MAXIMUM = AUDIO_TOTAL_TIME;
        		final int SCROLL_LENGTH = intFinalTimeView - intInitialTimeView;
        		final int SCROLL_UNIT_INCREMENT = (int) (SCROLL_LENGTH * 0.1);
        		final int SCROLL_BLOCK_INCREMENT = SCROLL_LENGTH / 2;
        		
        		int intValue = intInitialTimeView / (AUDIO_TOTAL_TIME / SCROLL_MAXIMUM);
        		if (intValue + SCROLL_LENGTH > SCROLL_MAXIMUM) {
        			intValue = intValue - (intValue + SCROLL_LENGTH - SCROLL_MAXIMUM);
        		}
        		
    			scrollBarHorizontalVisualization = new WasisScrollBar(JScrollBar.HORIZONTAL, intValue, SCROLL_LENGTH, SCROLL_MINIMUM, SCROLL_MAXIMUM);
    			scrollBarHorizontalVisualization.setUnitIncrement(SCROLL_UNIT_INCREMENT);
    			scrollBarHorizontalVisualization.setBlockIncrement(SCROLL_BLOCK_INCREMENT);
    			scrollBarHorizontalVisualization.allowSetValue(true);
    			
    			scrollBarHorizontalVisualization.addAdjustmentListener(new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent event) {
						// Em caso de uma imagem temporária de tamanho pequeno
						if (objSpectrogram != null) {
							if (objSpectrogram.getIsShortTemporaryImage()) {
								blnDrawScrollbarHorizontal = false;
								
								int intInitialTime = event.getValue();
								int intFinalTime = intInitialTime + SCROLL_LENGTH;
								
								if (intInitialTime + SCROLL_LENGTH > SCROLL_MAXIMUM) {
									intInitialTime = intInitialTime - (intInitialTime + SCROLL_LENGTH - SCROLL_MAXIMUM);
									intFinalTime = intInitialTime + SCROLL_LENGTH;
				        		}
								
								if (intInitialTime != intInitialTimeView && intFinalTime != intFinalTimeView) {
									processAudioByTime(intInitialTime, intFinalTime);
								}
							
							// Em caso de uma imagem temporária de tamanho grande
							} else {
								// Alteração do valor da scrollbar ainda não foi finalizada
								if (event.getValueIsAdjusting()) {
									blnScrollDragged = true;
									scrollBarHorizontalVisualization.allowSetValue(true);
									
								// Alteração do valor da scrollbar a ser finalizada
								} else {
									int intInitialTime = event.getValue();
									int intFinalTime = intInitialTime + SCROLL_LENGTH;
									
									if (intInitialTime + SCROLL_LENGTH > SCROLL_MAXIMUM) {
										intInitialTime = intInitialTime - (intInitialTime + SCROLL_LENGTH - SCROLL_MAXIMUM);
										intFinalTime = intInitialTime + SCROLL_LENGTH;
					        		}
									
									// A mudança da scrollbar foi feita arrastando o cursor
									if (blnScrollDragged) {
										processAudioByTime(intInitialTime, intFinalTime);
										
										blnScrollDragged = false;
										scrollBarHorizontalVisualization.allowSetValue(true);
										
									// A mudança da scrollbar foi feita pela seta esquerda/direta 
									} else {
										// Move scrollbar para a direita
										if (intInitialTime > intLastTimeScroll) {
											intInitialTime = (int) (intInitialTime + SCROLL_UNIT_INCREMENT);
										
										// Move scrollbar para a esquerda
										} else {
											intInitialTime = (int) (intInitialTime - SCROLL_UNIT_INCREMENT);  
										}
		
										// Faz a verificação e acertos dos valores que estão fora do range da scrollbar
										if (intInitialTime < 0) {
											intInitialTime = 0;
										} else if (intInitialTime + SCROLL_LENGTH > SCROLL_MAXIMUM) {
											intInitialTime = intInitialTime - (intInitialTime + SCROLL_LENGTH - SCROLL_MAXIMUM);
						        		}
										
										intFinalTime = (int) (intInitialTime + SCROLL_LENGTH);
		
										processAudioByTime(intInitialTime, intFinalTime);
										
										scrollBarHorizontalVisualization.allowSetValue(false);
									}
									
									intLastTimeScroll = intInitialTime;
								}
							}
						}
					}
				});
    		}
    		
    		// Insere novamente a scrollbar horizontal no painel do espectrograma
    		panelSpectrogram.add(scrollBarHorizontalVisualization, "cell 0 1, grow, gap 1 2 0 3");
    		
    		blnDrawScrollbarHorizontal = true;
    	}
    }
    
    /**
     * Atualiza a visualização da barra de scroll vertical do espectrograma (eixo das frequências).
     */
    private void updateVerticalScrollBar() {
    	if (objAudioWav != null && objSpectrogram != null && blnDrawScrollbarVertical) {
    		// Remove a scrollbar vertical se já houver alguma
    		if (scrollBarVerticalVisualization.getParent() == panelScrollbarVerticalVisualization) {
    			panelScrollbarVerticalVisualization.remove(scrollBarVerticalVisualization);
    		}
    		
    		final int AUDIO_MAXIMUM_FREQUENCY = objSpectrogram.getMaximumFrequency();

    		// Áudio completo sendo visualizado (scrollbar aparece desabilitada)
    		if (intFinalFrequencyView - intInitialFrequencyView >= AUDIO_MAXIMUM_FREQUENCY) {
    			scrollBarVerticalVisualization = new WasisScrollBar(JScrollBar.VERTICAL, 0, 0, 0, 0);
    			
    		// Parte do áudio sendo visualizada
    		} else {
    			final int SCROLL_MINIMUM = 0;
        		final int SCROLL_MAXIMUM = AUDIO_MAXIMUM_FREQUENCY;
        		final int SCROLL_LENGTH = intFinalFrequencyView - intInitialFrequencyView;
        		final int SCROLL_VALUE = SCROLL_MAXIMUM - SCROLL_LENGTH - intInitialFrequencyView;
        		final int SCROLL_UNIT_INCREMENT = (int) (SCROLL_LENGTH * 0.1);
        		final int SCROLL_BLOCK_INCREMENT = SCROLL_LENGTH / 2;
        		
        		scrollBarVerticalVisualization = new WasisScrollBar(JScrollBar.VERTICAL, SCROLL_VALUE, SCROLL_LENGTH, SCROLL_MINIMUM, SCROLL_MAXIMUM);
        		scrollBarVerticalVisualization.setUnitIncrement(SCROLL_UNIT_INCREMENT);
        		scrollBarVerticalVisualization.setBlockIncrement(SCROLL_BLOCK_INCREMENT);
        		scrollBarVerticalVisualization.allowSetValue(true);
        		
        		scrollBarVerticalVisualization.addAdjustmentListener(new AdjustmentListener() {
					@Override
					public void adjustmentValueChanged(AdjustmentEvent event) {
						blnDrawScrollbarVertical = false;
						
						int intFinalFrequency = SCROLL_MAXIMUM - event.getValue();
						if (intFinalFrequency > AUDIO_MAXIMUM_FREQUENCY) {
							intFinalFrequency = AUDIO_MAXIMUM_FREQUENCY;
						}
						
						int intInitialFrequency = intFinalFrequency - SCROLL_LENGTH;

						if (intInitialFrequency != intInitialFrequencyView && intFinalFrequency != intFinalFrequencyView) {
							processAudioByFrequency(intInitialFrequency, intFinalFrequency);
						}
					}
				});
    		}
    		
    		// Insere novamente a scrollbar vertical no painel de visualização
    		panelScrollbarVerticalVisualization.add(scrollBarVerticalVisualization, "cell 0 0, grow, gap 1 3 1 3");

    		blnDrawScrollbarVertical = true;
    	}
    }
    
    /**
     * Executa o processamento de parte do áudio levando em consideração o tempo.
     * 
     * @param intInitialTime - Tempo inicial a ser processado
     * @param intFinalTime   - Tempo final a ser processado
     */
    private void processAudioByTime(int intInitialTime, int intFinalTime) {
    	final WasisDialogLoadingData objWasisDialogLoadingData = new WasisDialogLoadingData(rsBundle.getString("message_loading_audio_file"));
		
		SwingWorker<Void, Void> swingWorkerRenderSpectrogram = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					objSpectrogram.reloadSpectrogramByTimeSelection(intInitialTime, intFinalTime);
					
					// Verifica se é necessário renderizar novamente o espectrograma
					if (objSpectrogram.getAllowRenderSpectrogram()) {
						// Mostra uma caixa de diálogo para o usuário perceber que o carregamento do arquivo está sendo feito
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								objWasisDialogLoadingData.showScreen();
							}
						});
						
						objSpectrogram.renderSpectrogram();
					}

					objSpectrogramGraphicPanel.repaint();

					objWasisDialogLoadingData.disableScreen();

				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
		};

		swingWorkerRenderSpectrogram.execute();
    }
    
    /**
     * Executa o processamento de parte do áudio levando em consideração a frequência.
     * 
     * @param intInitialFrequency - Frequência inicial a ser processada
     * @param intFinalFrequency   - Frequência final a ser processada
     */
    private void processAudioByFrequency(int intInitialFrequency, int intFinalFrequency) {
    	if (objSpectrogram != null) {
			objSpectrogram.reloadSpectrogramByFrequencySelection(intInitialFrequency, intFinalFrequency);
			objSpectrogramGraphicPanel.repaint();
    	}
    }
    
    /**
     * Executa o zoom no eixo do tempo/frequência.
     * 
     * @param intAxis - Eixo<br>
     * <i>0 = Time</i> <br>
     * <i>1 = Frequency</i>
     * 
     * @param intTypeZoom - Tipo de zoom<br>
     * <i>101 = Zoom In</i> <br>
     * <i>102 = Zoom Out</i> <br>
     * <i>103 = Zoom Reset</i>
     */
    private void processAudioZoomTimeFrequency(int intAxis, int intTypeZoom) {
		final WasisDialogLoadingData objWasisDialogLoadingAudioFile = new WasisDialogLoadingData(rsBundle.getString("message_loading_audio_file"));
		
		SwingWorker<Void, Void> swingWorkerRenderSpectrogram = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					if (objSpectrogram != null) {
						// Eixo tempo
						if (intAxis == ZOOM_TIME) {
							blnDrawScrollbarHorizontal = true;
							
							if (intTypeZoom == ZOOM_IN) {
								objSpectrogram.setTimeZoomIn();
							} else if (intTypeZoom == ZOOM_OUT) {
								objSpectrogram.setTimeZoomOut();
							} else if (intTypeZoom == ZOOM_RESET) {
								objSpectrogram.setTimeZoomReset();
							}
							
						// Eixo frequência
						} else if (intAxis == ZOOM_FREQUENCY) {
							blnDrawScrollbarVertical = true;
					    	
							if (intTypeZoom == ZOOM_IN) {
								objSpectrogram.setFrequencyZoomIn();
							} else if (intTypeZoom == ZOOM_OUT) {
								objSpectrogram.setFrequencyZoomOut();
							} else if (intTypeZoom == ZOOM_RESET) {
								objSpectrogram.setFrequencyZoomReset();
							}
						}
						
						// Verifica se é necessário renderizar novamente o espectrograma
						if (objSpectrogram.getAllowRenderSpectrogram()) {
							// Mostra uma caixa de diálogo para o usuário perceber que o carregamento do arquivo está sendo feito
							SwingUtilities.invokeLater(new Runnable() {
								@Override
								public void run() {
									objWasisDialogLoadingAudioFile.showScreen();
								}
							});
							
							objSpectrogram.renderSpectrogram();
						}
						
						if (intAxis == ZOOM_TIME) {
							processWaveformVisualization();
						}
						
						objSpectrogramGraphicPanel.repaint();
						
						objWasisDialogLoadingAudioFile.disableScreen();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
		};

		swingWorkerRenderSpectrogram.execute();
    }
    
    /**
     * Processa o espectrograma com um novo mapa de cores.
     * 
     * @param strSpectrogramColorDisplay
     */
    private void processSpectrogramColorDisplay(String strSpectrogramColorDisplay) {
    	WasisParameters.getInstance().setSpectrogramColorDisplay(strSpectrogramColorDisplay);

		lblLabelSelection.repaint();
			
		processAudio();
    }
    
    /**
	 * Verifica o status da visualização do Waveform.
	 */
	private void processWaveformVisualization() {
		if (objWaveform != null && objSpectrogram != null) {
			objWaveform.setInitialTimeSpectrogram(objSpectrogram.getInitialTime());
			objWaveform.setFinalTimeSpectrogram(objSpectrogram.getFinalTime());
			
			// Visualização completa do waveform (todo o áudio)
			if (objWaveformGraphicPanel.getViewFullWaveform()) {
				objWaveform.setTimeZoomReset();
				objWaveformGraphicPanel.setAudioSegment(true, objSpectrogram.getInitialTime(), objSpectrogram.getFinalTime());
				
				// Linha de seleção
				if (objWaveformGraphicPanel.getDrawSelectionLine() && objSpectrogramGraphicPanel.getDrawSelectionLine()) {
					objSpectrogramGraphicPanel.setAudioSegment(false, 0, 0);
					
					// A linha de seleção acompanha a parte que está sendo visualizada do áudio
					if (objSpectrogram.getInitialTime() >= objSpectrogramGraphicPanel.getTimeSelectionLine() || objSpectrogram.getFinalTime() <= objSpectrogramGraphicPanel.getTimeSelectionLine() || objSpectrogramGraphicPanel.getChangeTimeSelectionLine()) {
						objWaveformGraphicPanel.setSelectionLine(true, objSpectrogram.getInitialTime());
						objSpectrogramGraphicPanel.setSelectionLine(true, objSpectrogram.getInitialTime());
						objSpectrogramGraphicPanel.setChangeTimeSelectionLine(true);
						
						intCurrentTime = objSpectrogram.getInitialTime();
						lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intCurrentTime));
					}
				
				// Caixa de seleção / Segmento de áudio
				} else {
					// Segmento de áudio gerado no espectrograma
					if (objSpectrogramGraphicPanel.getDrawAudioSegment()) {
						objWaveformGraphicPanel.setSelectionLine(false, 0);
						
					// Caso haja alguma seleção anteriormente gerada no waveform, ela é descartada
					} else {
						selectedAudio(0, objSpectrogram.getInitialTime(), objSpectrogram.getInitialTime(), 0, 0);
					}
				}
				
			// Visualização parcial do waveform baseando-se no tempo inicial e final que está sendo exibido o espectrograma
			} else {
				objWaveform.setTimeZoom(objSpectrogram.getInitialTime(), objSpectrogram.getFinalTime());
				
				// Linha de seleção
				if (objWaveformGraphicPanel.getDrawSelectionLine() && objSpectrogramGraphicPanel.getDrawSelectionLine()) {
					objWaveformGraphicPanel.setAudioSegment(false, 0, 0);
					objSpectrogramGraphicPanel.setAudioSegment(false, 0, 0);
					
					// A linha de seleção acompanha a parte que está sendo visualizada do áudio
					if (objSpectrogram.getInitialTime() >= objSpectrogramGraphicPanel.getTimeSelectionLine() || objSpectrogram.getFinalTime() <= objSpectrogramGraphicPanel.getTimeSelectionLine() || objSpectrogramGraphicPanel.getChangeTimeSelectionLine()) {
						objWaveformGraphicPanel.setSelectionLine(true, objSpectrogram.getInitialTime());
						objSpectrogramGraphicPanel.setSelectionLine(true, objSpectrogram.getInitialTime());
						objSpectrogramGraphicPanel.setChangeTimeSelectionLine(true);
						
						intCurrentTime = objSpectrogram.getInitialTime();
						lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intCurrentTime));
					}
					
				// Caixa de seleção
				} else {
					// Caixa de seleção gerada no espectrograma
					if (objSpectrogramGraphicPanel.getDrawAudioSegment()) {
						objWaveformGraphicPanel.setAudioSegment(false, 0, 0);
						
					// Caixa de seleção gerada no waveform
					} else if (objWaveformGraphicPanel.getDrawAudioSegment()) {
						objWaveformGraphicPanel.setAudioSegment(true, intInitialTimeSelection, intFinalTimeSelection);
					}
				}
				
				objSpectrogramGraphicPanel.repaint();
				objWaveformGraphicPanel.repaint();
			}
		}
	}
	
	/**
	 * Áudio selecionado do waveform/espectrograma.<br>
	 * <br>
	 * Os valores de tempo atual e visão/seleção ativa são atualizados de acordo com os parâmetros.
	 * 
	 * @param intCurrentTime      - Posição atual do mouse no tempo (em milisegundos)
	 * @param intInitialTime      - Tempo inicial selecionado (em milisegundos)
	 * @param intFinalTime        - Tempo final selecionado (em milisegundos)
	 * @param intInitialFrequency - Frequência inicial selecionada (em Hz)
	 * @param intFinalFrequency   - Frequência final selecionada (em Hz)
	 */
	private void selectedAudio(int intCurrentTime, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) {
		// Atribui valores para o label de tempo atual
		this.intCurrentTime = intCurrentTime;
		this.lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intCurrentTime));
		
		// Atribui valores para os labels de tempo inicial, final e total selecionado
		this.intInitialTimeSelection = intInitialTime;
		this.intFinalTimeSelection = intFinalTime;
		
		if (intInitialTimeSelection == intFinalTimeSelection) {
			this.intInitialTimeSelection = 0;
			this.intFinalTimeSelection = 0;
		}
		
		this.lblInitialTimeSelection.setText(ClockTransformations.millisecondsIntoDigitalFormat(this.intInitialTimeSelection));
		this.lblFinalTimeSelection.setText(ClockTransformations.millisecondsIntoDigitalFormat(this.intFinalTimeSelection));
		this.lblLengthTimeSelection.setText(ClockTransformations.millisecondsIntoDigitalFormat(this.intFinalTimeSelection - this.intInitialTimeSelection));
		
		// Atribui valores para os labels de frequência inicial, final e total selecionado
		this.intInitialFrequencySelection = intInitialFrequency;
		this.intFinalFrequencySelection = intFinalFrequency;
		
		if (intInitialFrequency == intFinalFrequency) {
			this.intInitialFrequencySelection = 0;
			this.intFinalFrequencySelection = 0;
		}
		
		this.lblInitialFrequencySelection.setText(this.intInitialFrequencySelection + " Hz");
		this.lblFinalFrequencySelection.setText(this.intFinalFrequencySelection + " Hz");
		this.lblLengthFrequencySelection.setText((this.intFinalFrequencySelection - this.intInitialFrequencySelection) + " Hz");
		
		this.objPlayer.setAllowResumeAudio(false);
	}

    /**
	 * Abre o arquivo de áudio.
	 */
	private void openAudioFile() {
		AudioFileFilter audioFileFilter = new AudioFileFilter();
		JFileChooser fileChooser = new JFileChooser(WasisParameters.getInstance().getLastFilePath());
		fileChooser.setDialogTitle(rsBundle.getString("audio_file_chooser_title"));
		fileChooser.setMultiSelectionEnabled(true);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.addChoosableFileFilter(audioFileFilter);
		fileAudioList = null;
		
		int intReturn = fileChooser.showOpenDialog(Wasis.this);
		
		if (intReturn == JFileChooser.APPROVE_OPTION) {
			// Verifica se a conexão do banco de dados está ativa
			WasisParameters.getInstance().checkDatabaseConnection();
	 		
			if (WasisParameters.getInstance().getDatabaseStatus()) {
				// Armazena todos os arquivo de áudio selecionados em uma lista
				fileAudioList = fileChooser.getSelectedFiles();
				Arrays.sort(fileAudioList);
	 			
				// Apenas o primeiro arquivo da lista de áudio selecionada será o processado
				// Mas todos os arquivo de áudio serão adicionados na biblioteca de áudio posteriormente
				strAudioFilePath = fileAudioList[0].getAbsolutePath();
				
	         	WasisParameters.getInstance().setLastFilePath(strAudioFilePath);
	            
	        	// Parâmetros de FFT
	            int intFFTSampleSize = Integer.parseInt((String) cmbFFTSamples.getValue());
	            FFTParameters.getInstance().setFFTSampleSize(intFFTSampleSize);
	             
	            int intFFTOverlapFactor = (int) txtFFTOverlap.getValue();
	            FFTParameters.getInstance().setFFTOverlapFactor(intFFTOverlapFactor);
	             
	            String strFFTWindowFunction = (String) cmbFFTWindow.getValue();
	            FFTParameters.getInstance().setFFTWindowFunction(strFFTWindowFunction);
	            
	            loadAudio();
			} else {
				WasisMessageBox.showMessageDialog(rsBundle.getString("error_loading_audio_file_database"), WasisMessageBox.ERROR_MESSAGE);
			}
		}
	}
	
	/**
	 * Salva os dados dos segmentos selecionados do arquivo de áudio.
	 */
	private void saveAudioFile() {
		if (objAudioWav != null) {
			ScreenSaveAudio objSaveAudio = new ScreenSaveAudio(objSpectrogramGraphicPanel);
			objSaveAudio.showScreen();
		}
	}
	
	/**
	 * Fecha o arquivo de áudio.
	 */
	private void closeAudioFile() {
		if (objAudioWav != null) {
			if (objAudioLibraryController.closeOpenedAudioFile()) {
				resetScreenValues();
			}
		}
	}

	/**
	 * Carrega arquivo de áudio.
	 */
	private void loadAudio() {
		final WasisDialogLoadingData objWasisDialogLoadingAudioFile = new WasisDialogLoadingData(rsBundle.getString("message_loading_audio_file"));
		
		SwingWorker<Void, Void> swingWorkerLoadAudio = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					// Fecha o objeto do áudio anterior, se houver
					if (objAudioWav != null) {
						objAudioWav.closeAudio();
						
						// Pára o player, caso esteja tocando
						if (objPlayer != null) {
							objPlayer.stopAudio();
						}
						
						// Fecha o objeto do espectrograma anterior, se houver
						if (objSpectrogram != null) {
							objSpectrogram.closeSpectrogram();
						}
					}
					
					// Carrega o arquivo de áudio
					objAudioWav = new AudioWav(strAudioFilePath);
					objAudioWav.loadAudio();
					
					// Aguarda finalizar o carregamento/conversão do arquivo de áudio
					while (!objAudioWav.getStatusLoaded() && !objAudioWav.getStatusCancelled()) {
						Thread.sleep(25); // Dorme por um instante para não sobrecarregar a CPU
					}

					// Caso haja cancelamento na conversão do arquivo
					if (objAudioWav.getStatusCancelled()) {
						strAudioFilePath = null;
						
						objAudioLibraryController.clearAudioLibraryLoadingFile();
						
					// Caso não haja cancelamento na conversão, continua carregando o arquivo de áudio
					} else {
						objWasisDialogLoadingAudioFile.showScreen();
						
						resetViewSelectionValues();
						
						if (!blnAudioFileLoadedFromLibrary) {
							objAudioLibraryController.addAudioFileListToAudioLibrary(fileAudioList);
						} else {
							blnAudioFileLoadedFromLibrary = false;
						}
						
						AudioTemporary.createAudioTemporary(objAudioWav);
						
						loadPlayer();

						// Carrega o waveform em uma nova thread
						SwingWorker<Void, Void> swingWorkerLoadWaveform = new SwingWorker<Void, Void>() { 
							@Override
							protected Void doInBackground() throws Exception { 
								loadWaveform();
								
								return null;
							}
						};
						swingWorkerLoadWaveform.execute();
						
						// Carrega o espectrograma em uma nova thread
						SwingWorker<Void, Void> swingWorkerLoadSpectrogram = new SwingWorker<Void, Void>() { 
							@Override
							protected Void doInBackground() throws Exception { 
								loadSpectrogram();
								
								return null;
							}
						};
						swingWorkerLoadSpectrogram.execute();
						
						// Verifica se o waveform e espectrograma ainda estão em carregamento
						while (!swingWorkerLoadWaveform.isDone() || !swingWorkerLoadSpectrogram.isDone()) {
							Thread.sleep(50);     // Dorme por um instante para não sobrecarregar a CPU
						}
					}
					
				} catch (FileNotFoundException e) {
					WasisMessageBox.showMessageDialog(rsBundle.getString("message_audio_file_not_found") + ". \n" +
													  rsBundle.getString("message_audio_file_not_found_check"),
													  WasisMessageBox.ERROR_MESSAGE);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				return null;
			}
			
			@Override
			protected void done() {
			    try {
					get();
					
					// Caso o carregamento não tenha sido finalizado ou a conversão seja cancelada, 
	    			// todo o processo posterior é desconsiderado
	    			if (objAudioWav.getStatusLoaded() && !objAudioWav.getStatusCancelled()) {
	    				SwingUtilities.invokeLater(new Runnable() {
	    					@Override
	    					public void run() {
	    						objWaveformGraphicPanel.revalidate();
	    						objSpectrogramGraphicPanel.revalidate();

	    						objWasisDialogLoadingAudioFile.disableScreen();
	    					}
	    				});
	    			}
	    			
				} catch (ExecutionException | InterruptedException e) {
					objWasisDialogLoadingAudioFile.disableScreen();
					WasisMessageBox.showMessageDialog(rsBundle.getString("error_loading_audio_file"), WasisMessageBox.ERROR_MESSAGE);
				}
			}
		};
		
	    swingWorkerLoadAudio.execute();
	}

	/**
	 * Carrega o Player.
	 */
	private void loadPlayer() {
		if (objAudioWav != null) {
			objPlayer = new Player(objAudioWav);
			objPlayer.addPlayerListener(Wasis.this);
		}
	}
	
	/**
	 * Carrega o Waveform.
	 * 
	 * @throws CloneNotSupportedException 
	 */
	private void loadWaveform() throws CloneNotSupportedException {
		if (objAudioWav != null) {
			resetWaveform();
			
			AudioWav objAudioWavWaveform = (AudioWav) objAudioWav.clone();
			
			objWaveform = new Waveform(objAudioWavWaveform);
			
			objWaveformGraphicPanel = new WaveformGraphicPanel(panelWaveform, objWaveform);
			objWaveformGraphicPanel.setViewFullWaveform(WasisParameters.getInstance().getFullWaveform());
			objWaveformGraphicPanel.addWaveformListener(Wasis.this);
			objWaveformGraphicPanel.addPlayer(objPlayer);
	        
	        objWaveform.renderWaveform();
	        
	        // Insere o painel do waveform no frame principal
	        panelWaveform.add(objWaveformGraphicPanel, "cell 0 0, grow, gap 1 2 1 2");
		}
	}
	
	/**
	 * Carrega o Espectrograma.
	 * 
	 * @throws CloneNotSupportedException 
	 */
	private void loadSpectrogram() throws CloneNotSupportedException {
		if (objAudioWav != null) {
	    	resetSpectrogram();
	    	
	    	AudioWav objAudioWavSpectrogram = (AudioWav) objAudioWav.clone();
	    	
	    	objSpectrogram = new Spectrogram(objAudioWavSpectrogram);
	    	
	    	objSpectrogramGraphicPanel = new SpectrogramGraphicPanel(panelSpectrogram, objSpectrogram);
	    	objSpectrogramGraphicPanel.addSpectrogramListener(Wasis.this);
	    	objSpectrogramGraphicPanel.addPlayer(objPlayer);
	    	
	    	objSpectrogram.renderSpectrogram();
	    	
	    	// Insere o painel do espectrograma no frame principal
	    	panelSpectrogram.add(objSpectrogramGraphicPanel, "cell 0 0, grow, gap 1 2 1 0");
	    	panelSpectrogram.add(scrollBarHorizontalVisualization, "cell 0 1, grow, gap 1 2 0 3");
		}
	}
	
	/**
	 * Reseta os valores dos componentes principais da tela.
	 */
	private void resetScreenValues() {
		resetAudioFile();
		resetPlayer();
		resetWaveform();
		resetSpectrogram();
		resetViewSelectionValues();
	}
	
	/**
	 * Reseta arquivo de áudio.
	 */
	private void resetAudioFile() {
		if (objAudioWav != null) {
			objAudioWav.closeAudio();
			objAudioWav = null;
			strAudioFilePath = null;
		}
	}
	
	/**
	 * Reseta o Player.
	 */
	private void resetPlayer() {
		if (objPlayer != null) {
			objPlayer.stopAudio();
			objPlayer.closeFile();
			objPlayer = null;
		}
	}
	
	/**
	 * Reseta o Waveform.
	 */
	private void resetWaveform() {
		objWaveform = null;
		
		if (objWaveformGraphicPanel != null && objWaveformGraphicPanel.getParent() == panelWaveform) {
			panelWaveform.remove(objWaveformGraphicPanel);
			panelWaveform.repaint();
		}
		
		objWaveformGraphicPanel = null;
	}
	
	/**
	 * Reseta o Espectrograma.
	 */
	private void resetSpectrogram() {
		if (objSpectrogram != null) {
			objSpectrogram.closeSpectrogram();
			objSpectrogram = null;
		}
		
		if (objSpectrogramGraphicPanel != null && objSpectrogramGraphicPanel.getParent() == panelSpectrogram) {
			resetScrollbars();
			
			panelSpectrogram.remove(objSpectrogramGraphicPanel);
			panelSpectrogram.repaint();
		}
		
		objSpectrogramGraphicPanel = null;
	}
	
	/**
	 * Reseta as scrollbars.
	 */
	private void resetScrollbars() {
		scrollBarHorizontalVisualization.setValues(0, 0, 0, 0);
		scrollBarVerticalVisualization.setValues(0, 0, 0, 0);
	}
	
	/**
	 * Reseta os valores de visão e seleção ativa.
	 */
	private void resetViewSelectionValues() {
		intCurrentTime = 0;
		intInitialTimeSelection = 0;
		intFinalTimeSelection = 0;
	    intInitialTimeView = 0;
	    intFinalTimeView = 0;
	    lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intCurrentTime));
	    lblInitialTimeSelection.setText(ClockTransformations.millisecondsIntoDigitalFormat(intInitialTimeSelection));
	    lblFinalTimeSelection.setText(ClockTransformations.millisecondsIntoDigitalFormat(intFinalTimeSelection));
	    lblLengthTimeSelection.setText(ClockTransformations.millisecondsIntoDigitalFormat(intFinalTimeSelection - intInitialTimeSelection));
	    lblInitialTimeView.setText(ClockTransformations.millisecondsIntoDigitalFormat(intInitialTimeView));
	    lblFinalTimeView.setText(ClockTransformations.millisecondsIntoDigitalFormat(intFinalTimeView));
	    lblLengthTimeView.setText(ClockTransformations.millisecondsIntoDigitalFormat(intFinalTimeView - intInitialTimeView));
	    lblInitialFrequencySelection.setText("0 Hz");
	    lblFinalFrequencySelection.setText("0 Hz");
	    lblLengthFrequencySelection.setText("0 Hz");
	    lblInitialFrequencyView.setText("0 Hz");
	    lblFinalFrequencyView.setText("0 Hz");
	    lblLengthFrequencyView.setText("0 Hz");
	    lblMousePosition.setText("");
	}
	
	/**
	 * Realiza a classificação dos áudios pela técnica de Força Bruta.
	 */
	private void audioClassificationBruteForce() {
		if (objAudioWav != null && AudioTemporary.getAudioTemporary().get(objAudioWav.getAudioTemporaryIndex()).getAudioSegments().size() > 0) {
			try {
				ScreenAudioClassificationBruteForce objAudioClassificationBruteForce = new ScreenAudioClassificationBruteForce(objAudioWav, objAudioLibraryController.getAudioLibrary().getIdAudioLibrary());
				objAudioClassificationBruteForce.showScreen();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		} else {
			WasisMessageBox.showMessageDialog(rsBundle.getString("warning_select_audio_identification"), WasisMessageBox.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Realiza a classificação dos áudios pela técnica de Modelos de Classes.
	 */
	private void audioClassificationClassModel() {
		if (objAudioWav != null && AudioTemporary.getAudioTemporary().get(objAudioWav.getAudioTemporaryIndex()).getAudioSegments().size() > 0) {
			try {
				ScreenAudioClassificationClassModel objScreenAudioClassificationClassModel = new ScreenAudioClassificationClassModel(objAudioWav);
				objScreenAudioClassificationClassModel.showScreen();
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
		} else {
			WasisMessageBox.showMessageDialog(rsBundle.getString("warning_select_audio_identification"), WasisMessageBox.WARNING_MESSAGE);
		}
	}
	
	/**
	 * Realiza a comparação dos áudios.
	 */
	private void classifierModelBuilder() {
		ScreenModelBuilder objScreenModelBuilder = new ScreenModelBuilder();
		objScreenModelBuilder.showScreen();
	}
	
	/**
	 * Atualiza o banco de dados.
	 */
	private void updateDatabase() {
		ScreenUpdateDatabase objUpdateDatabase = new ScreenUpdateDatabase();
		objUpdateDatabase.showScreen();
	}
	
	/**
	 * Abre o manual do usuário (PDF) do sistema.
	 */
	private void openManual() {
		try {
            Desktop.getDesktop().browse(new URI("https://www2.ib.unicamp.br/fnjv/wasis_manual.pdf"));
        } catch (URISyntaxException | IOException e) {

        }
		
		/*
		if (Desktop.isDesktopSupported()) {
		    try {
		        File myFile = new File("res/wasis_manual.pdf");
		        Desktop.getDesktop().open(myFile);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}
		*/
	}
	
	/**
	 * Abre a tela de <i>About</i> do sistema.
	 */
	private void openAbout() {
		ScreenAbout objScreenAbout = new ScreenAbout();
		objScreenAbout.showScreen();
	}
	
	// ********************************************************************************************************
	// Controles do Player
	/**
	 * Toca o Áudio.
	 */
	private void playAudio() {
		try {
			if (objPlayer != null) {
				if (!objPlayer.getAllowResumeAudio()) {
					// Se não houver seleção, é executado o áudio referente à visão do espectrograma
					if (intInitialTimeSelection + intFinalTimeSelection == 0) {
						if (intCurrentTime == 0) {
							objPlayer.playAudio(intInitialTimeView, intFinalTimeView);
						} else {
							objPlayer.playAudio(intCurrentTime, intFinalTimeView);
						}
						
					// Se houver seleção, é executado o áudio referente à seleção
					} else {
						objPlayer.playAudio(intInitialTimeSelection, intFinalTimeSelection);
					}
				} else {
					resumeAudio();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pausa o Áudio.
	 */
	private void pauseAudio() {
		try {
			if (objPlayer != null) {
				objPlayer.pauseAudio();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Resume o Áudio.
	 */
	private void resumeAudio() {
		try {
			if (objPlayer != null) {
				objPlayer.resumeAudio();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Pára o Áudio.
	 */
	private void stopAudio() {
		try {
			if (objPlayer != null) {
				objPlayer.stopAudio();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// ********************************************************************************************************
	// Implementa KeyListener
	@Override
	public void keyPressed(KeyEvent event) {
		
	}

	@Override
	public void keyReleased(KeyEvent event) {
		
	}

	@Override
	public void keyTyped(KeyEvent event) {
		
	}
	
	// ********************************************************************************************************
	// Implementa AudioLibraryListener
	@Override
	public void openAudioFileFromAudioLibrary() {
		openAudioFile();
	}
	
	@Override
	public void loadAudioFileFromAudioLibrary(String strAudioFilePath) {
		this.fileAudioList = new File[1];
		this.fileAudioList[0] = new File(strAudioFilePath);
		
		resetScreenValues();
		
		this.strAudioFilePath = strAudioFilePath;
		
		blnAudioFileLoadedFromLibrary = true;
		
		loadAudio();
	}
	
	@Override
	public void resetValuesFromAudioLibrary() {
		resetScreenValues();
	}
	
	// ********************************************************************************************************
	// Implementa PlayerListener
	@Override
	public void playerStatus(int intStatusPlayer, int intTimeMilliseconds) {
		this.intStatusPlayer = intStatusPlayer;
		
		if (objPlayer != null) {
			if (this.intStatusPlayer == objPlayer.STATUS_STOPPED) {
				// Se não houver seleção, é mostrado o valor referente à visão do espectrograma
				if (intInitialTimeSelection + intFinalTimeSelection == 0) {
					if (intCurrentTime == 0) {
						lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intInitialTimeView));
					} else {
						lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intCurrentTime));
					}
					
				// Se houver seleção, é mostrado o valor referente à seleção
				} else {
					lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intInitialTimeSelection));
				}
			}
		}
	}
	
	@Override
	public void playerTimeElapsed(int intMilliseconds) {
		lblCurrentTime.setText(ClockTransformations.millisecondsIntoDigitalFormat(intMilliseconds));
	}
	
	// ********************************************************************************************************
	// Implementa SpectrogramListener
	@Override
	public void spectrogramCurrentTimeFrequency(int intTime, int intFrequency) {
		lblMousePosition.setText(intFrequency + " Hz   @   " + ClockTransformations.millisecondsIntoDigitalFormat(intTime));
	}

	@Override
	public void spectrogramSelectedAudio(int intCurrentTime, int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency, boolean blnDrawWaveform) {
		selectedAudio(intCurrentTime, intInitialTime, intFinalTime, intInitialFrequency, intFinalFrequency);
		
		// Visualização completa do waveform (todo o áudio)
		if (objWaveformGraphicPanel.getViewFullWaveform()) {
			// Linha de seleção
			if (intInitialTime == intFinalTime) {
				objWaveformGraphicPanel.setSelectionLine(true, intInitialTime);
				objWaveformGraphicPanel.setAudioSegment(true, objSpectrogram.getInitialTime(), objSpectrogram.getFinalTime());
			// Caixa de seleção
			} else {
				objWaveformGraphicPanel.setSelectionLine(false, 0);
				objWaveformGraphicPanel.setAudioSegment(true, objSpectrogram.getInitialTime(), objSpectrogram.getFinalTime());
			}
			
		// Visualização parcial do waveform baseando-se no tempo inicial e final que está sendo exibido o espectrograma
		} else {
			// Linha de seleção
			if (intInitialTime == intFinalTime) {
				objWaveformGraphicPanel.setSelectionLine(true, intInitialTime);
				objWaveformGraphicPanel.setAudioSegment(false, 0, 0);
			// Caixa de seleção
			} else {
				objWaveformGraphicPanel.setSelectionLine(false, 0);
				objWaveformGraphicPanel.setAudioSegment(false, 0, 0);
			}
		}
		
		// Desenha o waveform ao selecionar parte do áudio
		if (blnDrawWaveform) {
			processWaveformVisualization();
		}
		
		objWaveformGraphicPanel.repaint();
		objSpectrogramGraphicPanel.repaint();
	}
	
	@Override
	public void spectrogramViewAudio(int intInitialTime, int intFinalTime, int intInitialFrequency, int intFinalFrequency) {
		this.intInitialTimeView = intInitialTime;
		this.intFinalTimeView = intFinalTime;

		this.lblInitialTimeView.setText(ClockTransformations.millisecondsIntoDigitalFormat(intInitialTime));
		this.lblFinalTimeView.setText(ClockTransformations.millisecondsIntoDigitalFormat(intFinalTime));
		this.lblLengthTimeView.setText(ClockTransformations.millisecondsIntoDigitalFormat(intFinalTime - intInitialTime));
		
		this.intInitialFrequencyView = intInitialFrequency;
		this.intFinalFrequencyView = intFinalFrequency;
		
		this.lblInitialFrequencyView.setText(intInitialFrequency + " Hz");
		this.lblFinalFrequencyView.setText(intFinalFrequency + " Hz");
		this.lblLengthFrequencyView.setText((intFinalFrequency - intInitialFrequency) + " Hz");
		
		updateHorizontalScrollBar();
		updateVerticalScrollBar();
		
		processWaveformVisualization();
	}

	// ********************************************************************************************************
	// Implementa WaveformListener
	@Override
	public void waveformCurrentTime(int intTime) {
		lblMousePosition.setText(ClockTransformations.millisecondsIntoDigitalFormat(intTime));
	}
	
	@Override
	public void waveformSelectedAudio(int intCurrentTime, int intInitialTime, int intFinalTime) {
		// Visualização completa do waveform (todo o áudio)
		if (objWaveformGraphicPanel.getViewFullWaveform()) {
			
			// Linha de seleção
			if (intInitialTime == intFinalTime) {
				objWaveformGraphicPanel.setAudioSegment(true, objSpectrogram.getInitialTime(), objSpectrogram.getFinalTime());

				if (!objSpectrogramGraphicPanel.getDrawAudioSegment() || (objSpectrogramGraphicPanel.getDrawAudioSegment() && !objWaveformGraphicPanel.getMouseButtonReleased())) {
					objWaveformGraphicPanel.setSelectionLine(true, intInitialTime);
					objSpectrogramGraphicPanel.setSelectionLine(true, intInitialTime);
					objSpectrogramGraphicPanel.setAudioSegment(false, 0, 0);
					
					selectedAudio(intCurrentTime, intInitialTime, intFinalTime, 0, 0);
				}
				
			// Caixa de seleção
			} else {
				processAudioByTime(intInitialTime, intFinalTime);
				
				objWaveformGraphicPanel.setAudioSegment(true, intInitialTime, intFinalTime);
				
				if (!objSpectrogramGraphicPanel.getDrawAudioSegment()) {
					objWaveformGraphicPanel.setSelectionLine(true, intInitialTime);
					objSpectrogramGraphicPanel.setSelectionLine(true, intInitialTime);
					objSpectrogramGraphicPanel.setAudioSegment(false, 0, 0);
				}
				
				blnDrawScrollbarHorizontal = true;
				updateHorizontalScrollBar();
				blnDrawScrollbarHorizontal = false;
			}
			
		// Visualização parcial do waveform baseando-se no tempo inicial e final que está sendo exibido o espectrograma
		} else {
			selectedAudio(intCurrentTime, intInitialTime, intFinalTime, 0, 0);
			
			// Linha de seleção
			if (intInitialTime == intFinalTime) {
				objSpectrogramGraphicPanel.setSelectionLine(true, intInitialTime);
				objSpectrogramGraphicPanel.setAudioSegment(false, 0, 0);
				
			// Caixa de seleção
			} else {
				objSpectrogramGraphicPanel.setSelectionLine(false, 0);
				objSpectrogramGraphicPanel.setAudioSegment(false, 0, 0);
			}
		}
		
		objWaveformGraphicPanel.repaint();
		objSpectrogramGraphicPanel.repaint();
	}
	
	/**
	 * Finaliza o sistema.
	 */
	private void exitSystem() {
		// Verifica se tem arquivos de áudio abertos de alguma biblioteca não gravada
		boolean blnSaveAudioLibrary = false;
		
		/*
		if (objAudioLibraryController.getAudioLibrary().getIdAudioLibrary() == 0) {
			if (objAudioLibraryController.getListModelAudioLibrary().getSize() > 0) {
				int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("exit_system_audio_library_not_saved"), WasisMessageBox.YES_NO_OPTION);
				
				// Abre a tela para a gravação da nova biblioteca de áudio
				if (intDialogResult == WasisMessageBox.YES_OPTION) {
					blnSaveAudioLibrary = true;
					
					saveAudioLibrary();
					
					setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
			}
		}
		*/
		
		if (!blnSaveAudioLibrary) {
			// Verifica se existem segmentos de áudio não gravados
			if (AudioTemporary.checkAudioSegmentsNotSaved(strAudioFilePath)) {
				int intDialogResult = WasisMessageBox.showConfirmDialog(rsBundle.getString("exit_system_audio_segments_not_saved"), WasisMessageBox.YES_NO_OPTION);
				
				// Fecha sistema
				if (intDialogResult == WasisMessageBox.YES_OPTION) {
					setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
					
					resetScreenValues();
					AudioTemporary.deleteTemporaryFiles();
					WasisParameters.getInstance().saveParameters();
					
					System.exit(0);
					
				// Mantem o sistema aberto
				} else {
					setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
				}
				
			} else {
				setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
				
				resetScreenValues();
				AudioTemporary.deleteTemporaryFiles();
				WasisParameters.getInstance().saveParameters();
				
				System.exit(0);
			}
		}
	}
}