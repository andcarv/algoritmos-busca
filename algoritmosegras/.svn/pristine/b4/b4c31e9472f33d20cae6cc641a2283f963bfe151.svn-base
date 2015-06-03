package kernel;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Formatter;
import java.util.Iterator;
import java.util.SortedSet;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.labels.StandardXYZToolTipGenerator;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.DefaultXYZDataset;

public class CurvaROC extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;

	private JPanel jPanel = null;
	
	private double[][] dadosXY;
	SortedSet<Double> limiares;

	/**
	 * This is the default constructor
	 */
	public CurvaROC(double[] x, double[] y, String titulo, SortedSet<Double> l) {
		super();
		dadosXY = new double[3][y.length];
		dadosXY[0] = x;
		dadosXY[1] = y;
		limiares = l;
		
		// Colocando a lista de limiares no conjunto como informativo...
		double temp[] = new double[limiares.size()];
		int i = temp.length-1;
		for (Iterator<Double> iterator = limiares.iterator(); iterator.hasNext();) {
			Double limiar = (Double) iterator.next();
			temp[i] = limiar;
			i--;
		}
		dadosXY[2] = temp;
		
		jPanel = createPanel(titulo);  
		initialize();
	
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	public void initialize() {
		this.setSize(300, 200);
		this.setContentPane(getJContentPane());
		this.setTitle("Curva ROC - TPR - FPR - LIMIAR");
		this.addWindowListener(new CurvaROCWindowAdapters(this));
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJPanel(), BorderLayout.CENTER);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJPanel() {
		if (jPanel == null) {
			jPanel = new JPanel();
			jPanel.setLayout(new GridBagLayout());
		}
		return jPanel;
	}
	
	public JFreeChart createChart(String titulo){
		DefaultXYZDataset xyzDataSet = new DefaultXYZDataset();
		xyzDataSet.addSeries("(TPR,FPR,L)", dadosXY);
		JFreeChart chart = ChartFactory.createScatterPlot(titulo, "TP", "FP", xyzDataSet, PlotOrientation.HORIZONTAL, false, true, false);
		
	
		// alterando o gerador de informações do gráfico (a tabela de é tridimensional)
		XYItemRenderer renderer = (XYItemRenderer) chart.getXYPlot().getRenderer();
		renderer.setBaseToolTipGenerator(new StandardXYZToolTipGenerator() );
        //renderer.setToolTipGenerator(new StandardCategoryToolTipGenerator( "{0}({1}) ", NumberFormat.getNumberInstance()));
		
		// Gerando arquivo com os pontos da curva ROC mais os limiares
		try{
			Formatter formatador_de_texto = new Formatter();
			FileWriter file = new FileWriter("matriz.txt"); // mudar para o caminho para a pasta resultados/nom_da_base
			file.write(formatador_de_texto.format("%14s%12s\t\t%14s%12s\t\t%17s\n", "TPR", "", "FPR", "", "LIMIAR").toString());
			DecimalFormat formatador_de_valores = new DecimalFormat();
			formatador_de_valores.setMaximumFractionDigits(24);
			formatador_de_valores.setMinimumFractionDigits(24);
			for (int i = dadosXY[0].length-1; i > 0; i--) {
				 String linha = formatador_de_valores.format(Double.valueOf(dadosXY[0][i])) + "\t\t";
				 linha += formatador_de_valores.format(Double.valueOf(dadosXY[1][i])) + "\t\t";
				 linha += formatador_de_valores.format(Double.valueOf(dadosXY[2][i])) + "\n";
				 file.write(linha);
			}			
			file.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		chart.setBorderPaint(Color.WHITE);
		return chart;
	}
	
	public  JPanel createPanel(String titulo) {  
		JFreeChart chart = createChart(titulo);  
		return new ChartPanel(chart);  
	}  
	

}

class CurvaROCWindowAdapters extends java.awt.event.WindowAdapter
{
	
	public JFrame janela = null;
	
	public CurvaROCWindowAdapters(JFrame j){
		janela = j;
	}
	public void windowClosing(java.awt.event.WindowEvent e) {
		janela.dispose();
	}	

}
