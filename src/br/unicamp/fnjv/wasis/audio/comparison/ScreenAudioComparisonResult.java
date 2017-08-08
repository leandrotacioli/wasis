package br.unicamp.fnjv.wasis.audio.comparison;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ResourceBundle;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import net.miginfocom.swing.MigLayout;

import com.leandrotacioli.libs.LTDataTypes;
import com.leandrotacioli.libs.swing.textfield.LTTextField;

import br.unicamp.fnjv.wasis.classifiers.pcc.PearsonCorrelationValues;
import br.unicamp.fnjv.wasis.main.WasisParameters;
import br.unicamp.fnjv.wasis.swing.WasisDialog;
import br.unicamp.fnjv.wasis.swing.WasisPanel;

/**
 * Classe responsável pela exibição de uma tela que
 * mostra os resultados da comparação de 2 amostras de áudio.
 * 
 * @author Leandro Tacioli
 * @version 2.2 - 29/Jun/2017
 */
public class ScreenAudioComparisonResult {
	private ResourceBundle rsBundle = WasisParameters.getInstance().getBundle();
	
	private WasisDialog objWasisDialog;
	
	private List<PearsonCorrelationValues> lstValuesX;
	private List<PearsonCorrelationValues> lstValuesY;
	
	/**
	 * Classe responsável pela exibição de uma tela que
	 * mostra os resultados da comparação de 2 amostras de áudio.
 	 * 
 	 * @param lstValuesX - Amostra X
 	 * @param lstValuesY - Amostra Y
	 */
	protected ScreenAudioComparisonResult(List<PearsonCorrelationValues> lstValuesX, List<PearsonCorrelationValues> lstValuesY) {
		this.lstValuesX = lstValuesX;
		this.lstValuesY = lstValuesY;

		loadScreen();
	}
	
	/**
	 * Inicializa todos os componentes da tela.
	 */
	private void loadScreen() {
		// Cria os componentes da tela
		
		// ***********************************************************************************************************************
		// Painel de Dados dos Resultados
		JPanel panelSamples = new JPanel();
		panelSamples.setLayout(new MigLayout("insets 0", "[grow]", "[][]"));
		
		// ***********************************************************************************************************************
		// Dados Amostra 1
		WasisPanel panelSampleX = new WasisPanel(rsBundle.getString("screen_compare_audios_result_sample_x"));
		panelSampleX.setLayout(new MigLayout("insets 0", "[grow]", "[][][]"));
		
		LTTextField txtFilePathX = new LTTextField("File Path:", LTDataTypes.STRING, false, false, 200);
		LTTextField txtAnimalGenusX = new LTTextField("Genus:", LTDataTypes.STRING, false, false, 200);
		LTTextField txtAnimalSpeciesX = new LTTextField("Species:", LTDataTypes.STRING, false, false, 200);
		
		// ***********************************************************************************************************************
		// Dados Amostra 2
		WasisPanel panelSampleY = new WasisPanel(rsBundle.getString("screen_compare_audios_result_sample_y"));
		panelSampleY.setLayout(new MigLayout("insets 0", "[grow]", "[][][]"));
		
		LTTextField txtFilePathY = new LTTextField("File Path:", LTDataTypes.STRING, false, false, 200);
		LTTextField txtAnimalGenusY = new LTTextField("Genus:", LTDataTypes.STRING, false, false, 200);
		LTTextField txtAnimalSpeciesY = new LTTextField("Species:", LTDataTypes.STRING, false, false, 200);
		
		// ***********************************************************************************************************************
		// Gráfico
		WasisPanel panelChart = new WasisPanel(rsBundle.getString("screen_compare_audios_result_graph_comparison"));
		panelChart.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));
		
		XYDataset xyDataSet = createDataset(); 
		JFreeChart jFreeChart = createChart(xyDataSet);
		//JFreeChart jFreeChart = createChart(createDatasetCategory());

		JPanel panelChartResult = new ChartPanel(jFreeChart);
		panelChartResult.setBackground(Color.white);
		
		// ***********************************************************************************************************************
		// Botão Exportar Dados
		JButton btnExportData = new JButton(rsBundle.getString("screen_compare_audios_result_export_data"));
		btnExportData.setMinimumSize(new Dimension(250, 30));
		btnExportData.setMaximumSize(new Dimension(400, 30));
		btnExportData.setIconTextGap(15);
		btnExportData.setFont(new Font("Tahoma", Font.PLAIN, 14));
		btnExportData.setIcon(new ImageIcon("res/images/export.png"));
		btnExportData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				
			}
		});
					
		// ***********************************************************************************************************************
		// Cria a tela
		objWasisDialog = new WasisDialog(rsBundle.getString("screen_compare_audios_result_screen_description"), true);
		objWasisDialog.setBounds(350, 350, 800, 500);
		objWasisDialog.setMinimumSize(new Dimension(800, 500));
		
		objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[0.00][grow]", "[grow][]"));
		//objWasisDialog.getContentPane().setLayout(new MigLayout("insets 5 5 5 5", "[0.00][grow]", "[grow][]"));
		//objWasisDialog.getContentPane().add(panelSamples, "cell 0 0, grow");
		objWasisDialog.getContentPane().add(panelChart, "cell 1 0, grow");
		//objWasisDialog.getContentPane().add(btnExportData, "cell 1 1, grow");
		
		panelSamples.add(panelSampleX, "cell 0 0, grow");
		panelSamples.add(panelSampleY, "cell 0 1, grow");
		
		panelSampleX.add(txtFilePathX, "cell 0 0, grow");
		panelSampleX.add(txtAnimalGenusX, "cell 0 1, grow");
		panelSampleX.add(txtAnimalSpeciesX, "cell 0 1, grow");
		
		panelSampleY.add(txtFilePathY, "cell 0 0, grow");
		panelSampleY.add(txtAnimalGenusY, "cell 0 1, grow");
		panelSampleY.add(txtAnimalSpeciesY, "cell 0 1, grow");

		panelChart.add(panelChartResult, "cell 0 0, grow");
	}
	
	/**
	 * Habilita a visualização da tela.
	 */
	protected void showScreen() {
		objWasisDialog.setVisible(true);
	}
	
	/**
	 * Alimenta o gráfico com os dados utilizados na comparação.
	 * 
	 * @return xySeriesCollection
	 */
	private XYDataset createDataset() {
		// Alterar essa informação
		//XYSeries xySeriesX = new XYSeries(rsBundle.getString("screen_compare_audios_result_sample_x"));
        //XYSeries xySeriesY = new XYSeries(rsBundle.getString("screen_compare_audios_result_sample_y"));
        
        XYSeries xySeriesX = new XYSeries("Unknown Species");
        XYSeries xySeriesY = new XYSeries("Smooth-billed Ani (Crotophaga ani)");
        
        for (int indexValues = 0; indexValues < lstValuesX.size(); indexValues++) {
        	xySeriesX.add(lstValuesX.get(indexValues).getIndex(), lstValuesX.get(indexValues).getValue());
        	xySeriesY.add(lstValuesY.get(indexValues).getIndex(), lstValuesY.get(indexValues).getValue());
        }
        
        XYSeriesCollection xySeriesCollection = new XYSeriesCollection(); 
        xySeriesCollection.addSeries(xySeriesX); 
        xySeriesCollection.addSeries(xySeriesY); 
        
        return xySeriesCollection; 
    }
	
	/**
	 * Cria a interface do gráfico.
	 * 
	 * @param xyDataSet
	 * 
	 * @return jFreeChart
	 */
	private JFreeChart createChart(XYDataset xyDataSet) {
		//createXYAreChart
		JFreeChart jFreeChart = ChartFactory.createXYLineChart("", 
															   rsBundle.getString("screen_compare_audios_result_graph_x_axis"), 
															   rsBundle.getString("screen_compare_audios_result_graph_y_axis"), 
															   xyDataSet, 
															   PlotOrientation.VERTICAL, 
															   true, 
															   true,
															   false);
		
		jFreeChart.setBackgroundPaint(new Color(0, 0, 0, 0)); 
        
		// Sets paint color and thickness for each series
		XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
		renderer.setSeriesPaint(0, Color.RED);
		renderer.setSeriesPaint(1, Color.BLUE);
		renderer.setSeriesStroke(0, new BasicStroke(2.0f));
		renderer.setSeriesStroke(1, new BasicStroke(2.0f));
		renderer.setBaseShapesVisible(false);
		
        XYPlot xyPlot = (XYPlot) jFreeChart.getPlot(); 
        xyPlot.setBackgroundPaint(Color.LIGHT_GRAY);
        xyPlot.setForegroundAlpha(0.75F);
        xyPlot.setRenderer(renderer);
        
        ValueAxis xAxis = xyPlot.getDomainAxis(); 
        xAxis.setTickMarkPaint(Color.BLACK); 
        xAxis.setLowerMargin(0.0D);
        xAxis.setUpperMargin(0.0D);
        xAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 18));
        xAxis.setTickLabelFont(new Font("SansSerif", Font.ITALIC, 14));
        
        ValueAxis yAxis = xyPlot.getRangeAxis(); 
        yAxis.setTickMarkPaint(Color.BLACK);
        //yAxis.setLowerBound(-120.0d);
        yAxis.setLabelFont(new Font("SansSerif", Font.PLAIN, 18));
        yAxis.setTickLabelFont(new Font("SansSerif", Font.ITALIC, 14));
        
        return jFreeChart;
    }
}