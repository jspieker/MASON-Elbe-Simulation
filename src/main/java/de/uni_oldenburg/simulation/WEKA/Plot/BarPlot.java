package de.uni_oldenburg.simulation.WEKA.Plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import weka.core.Instance;
import weka.core.Instances;

/**
 * Implements {@link Plot} and extends {@link ApplicationFrame} to enable bar charts.
 */
public class BarPlot extends ApplicationFrame implements Plot {

	private Instances instances;

	public BarPlot() {
		super("Elbe Events");
	}

	private CategoryDataset createDataset() {

		final DefaultCategoryDataset dataset =
				new DefaultCategoryDataset();

		for (Instance instance : instances) {
			double[] instanceValues = instance.toDoubleArray();

			dataset.addValue(instanceValues[1], String.valueOf(instanceValues[0]), instance.attribute(1).name());
			dataset.addValue(instanceValues[2], String.valueOf(instanceValues[0]), instance.attribute(2).name());
			dataset.addValue(instanceValues[3], String.valueOf(instanceValues[0]), instance.attribute(3).name());
			dataset.addValue(instanceValues[4], String.valueOf(instanceValues[0]), instance.attribute(4).name());
			dataset.addValue(instanceValues[5], String.valueOf(instanceValues[0]), instance.attribute(5).name());
			dataset.addValue(instanceValues[6], String.valueOf(instanceValues[0]), instance.attribute(6).name());

		}
		return dataset;
	}

	@Override
	public void plot(Instances instances) throws Exception {
		this.instances = instances;
		JFreeChart barChart = ChartFactory.createBarChart(
				"",
				"Categories",
				"Values",
				createDataset(),
				PlotOrientation.VERTICAL,
				true, true, false);

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(800, 600));
		setContentPane(chartPanel);

		pack();
		RefineryUtilities.centerFrameOnScreen(this);
		setVisible(true);
	}

	@Override
	public void windowClosing(java.awt.event.WindowEvent e) {
		dispose();
	}

}
