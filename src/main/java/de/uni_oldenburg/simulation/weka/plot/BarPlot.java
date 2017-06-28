package de.uni_oldenburg.simulation.weka.plot;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import weka.core.Instances;

/**
 * Implements {@link Plot} and extends {@link ApplicationFrame} to enable bar charts. Is abstract to provide general plot utils.
 *
 * @see CollisionBarPlot
 * @see CollisionWithShipsBarPlot
 */
public abstract class BarPlot extends ApplicationFrame implements Plot {

	protected Instances instances;

	public BarPlot() {
		super("Elbe Events");
	}

	protected abstract CategoryDataset createDataset();

	@Override
	public void plot(Instances instances) throws Exception {
		this.instances = instances;
		JFreeChart barChart = ChartFactory.createBarChart3D(
				"",
				"Categories",
				"Values",
				createDataset(),
				PlotOrientation.VERTICAL,
				true, true, false);

		ChartPanel chartPanel = new ChartPanel(barChart);
		chartPanel.setPreferredSize(new java.awt.Dimension(width, height));
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
