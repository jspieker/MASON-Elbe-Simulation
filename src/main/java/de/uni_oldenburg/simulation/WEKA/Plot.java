package de.uni_oldenburg.simulation.WEKA;

import sim.display.ChartUtilities;
import weka.Run;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Utils;
import weka.gui.beans.ModelPerformanceChart;
import weka.gui.beans.ThresholdDataEvent;
import weka.gui.beans.WekaOffscreenChartRenderer;
import weka.gui.visualize.Plot2D;
import weka.gui.visualize.PlotData2D;
import weka.gui.visualize.ThresholdVisualizePanel;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Random;

/**
 * Plots the results
 */
public class Plot {

	public Plot(Instances instances) throws Exception {
		Plot2D plot2D = new Plot2D();
		try {
			plot2D.setInstances(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Run run = new Run();

		final javax.swing.JFrame jf = new javax.swing.JFrame();
		jf.getContentPane().setLayout(new java.awt.BorderLayout());
		final ModelPerformanceChart as = new ModelPerformanceChart();
		PlotData2D pd = new PlotData2D(instances);
		pd.setPlotName(instances.relationName());
		ThresholdDataEvent roc = new ThresholdDataEvent(as, pd);
		as.acceptDataSet(roc);

		jf.getContentPane().add(as, java.awt.BorderLayout.CENTER);
		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
				System.exit(0);
			}
		});
		jf.setSize(800, 600);
		jf.setVisible(true);

		System.out.println(plot2D.getHeight() + " " + plot2D.isDisplayable());
	}

	public void plotChart(Instances instances) throws Exception {
		WekaOffscreenChartRenderer wekaOffscreenChartRenderer = new WekaOffscreenChartRenderer();
		ArrayList<Instances> list = new ArrayList<>();
		list.add(instances);
		BufferedImage bufferedImage = wekaOffscreenChartRenderer.renderXYLineChart(instances.size(), 20, list, "Collisions", new ArrayList<String>());


		JFrame frame = new JFrame("BufferedImage");

		frame.add(new JLabel(new ImageIcon(bufferedImage)));

		frame.pack();
//      frame.setSize(WIDTH, HEIGHT);
		// Better to DISPOSE than EXIT
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setVisible(true);
	}
}


