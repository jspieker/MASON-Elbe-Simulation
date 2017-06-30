package de.uni_oldenburg.simulation.weka.plot;

import weka.core.Instances;

/**
 * Interface to plot the results given the {@link Instances} providing the data.
 *
 * @see CollisionBarPlot
 * @see Plot2D
 * @see WEKAExplorer
 */
public interface Plot {

	int width = 800;
	int height = 600;

	/**
	 * Plots the data.
	 *
	 * @param instances The {@link Instances} providing the data with attributes and others.
	 * @throws Exception If any error occurs.
	 */
	public void plot(Instances instances) throws Exception;
}


