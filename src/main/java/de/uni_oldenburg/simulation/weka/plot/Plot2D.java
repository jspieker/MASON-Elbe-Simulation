package de.uni_oldenburg.simulation.weka.plot;

import weka.core.Instances;
import weka.gui.beans.ModelPerformanceChart;
import weka.gui.beans.ThresholdDataEvent;
import weka.gui.visualize.PlotData2D;

/**
 * Implements {@link Plot} to enable a 2D plot of the data. You can switch the axis.
 */
public class Plot2D implements Plot {

	private javax.swing.JFrame jf;

	@Override
	public void plot(Instances instances) throws Exception {
		jf = new javax.swing.JFrame();
		jf.getContentPane().setLayout(new java.awt.BorderLayout());

		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});
		jf.setSize(width, height);
		weka.gui.visualize.Plot2D plot2D = new weka.gui.visualize.Plot2D();
		plot2D.setInstances(instances);
		final ModelPerformanceChart as = new ModelPerformanceChart();
		PlotData2D pd = new PlotData2D(instances);
		pd.setPlotName(instances.relationName());
		ThresholdDataEvent roc = new ThresholdDataEvent(as, pd);
		as.acceptDataSet(roc);
		jf.getContentPane().add(as, java.awt.BorderLayout.CENTER);

		jf.setVisible(true);
	}

}
