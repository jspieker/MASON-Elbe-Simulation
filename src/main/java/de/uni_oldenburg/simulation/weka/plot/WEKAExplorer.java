package de.uni_oldenburg.simulation.weka.plot;

import weka.core.Instances;
import weka.gui.explorer.Explorer;
import weka.gui.explorer.PreprocessPanel;

/**
 * Starts the weka Explorer with the given instances.
 */
public class WEKAExplorer {

	final javax.swing.JFrame jf;

	public WEKAExplorer() throws Exception {
		jf = new javax.swing.JFrame();
		jf.getContentPane().setLayout(new java.awt.BorderLayout());

		jf.addWindowListener(new java.awt.event.WindowAdapter() {
			public void windowClosing(java.awt.event.WindowEvent e) {
				jf.dispose();
			}
		});
		jf.setSize(800, 600);
	}

	public void startWEKAExplorer(Instances instances) throws Exception {

		Explorer explorer = new Explorer();
		PreprocessPanel preprocessPanel = explorer.getPreprocessPanel();
		preprocessPanel.setInstances(instances);

		jf.getContentPane().add(explorer, java.awt.BorderLayout.CENTER);

		jf.setVisible(true);
	}

	public void startKnowLedgeFor(Instances instances) throws Exception {
		weka.gui.beans.AttributeSummarizer attributeSummarizer = new weka.gui.beans.AttributeSummarizer();
		attributeSummarizer.setInstances(instances);
		attributeSummarizer.setGridWidth(800);

		attributeSummarizer.setOffscreenXAxis("TimeRun");
		attributeSummarizer.setOffscreenYAxis("Collisions");

		weka.knowledgeflow.steps.AttributeSummarizer summarizer = new weka.knowledgeflow.steps.AttributeSummarizer();

		System.out.println(attributeSummarizer.getOffscreenRendererName());


		jf.getContentPane().add(attributeSummarizer, java.awt.BorderLayout.CENTER);

		jf.setVisible(true);
	}
}
