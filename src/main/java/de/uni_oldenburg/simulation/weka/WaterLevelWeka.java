package de.uni_oldenburg.simulation.weka;

import de.uni_oldenburg.simulation.weka.plot.Plot2D;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Implements {@link Weka}.
 */
public class WaterLevelWeka extends Weka {

	public WaterLevelWeka(String path) {
		super(path, "waterLevel");
	}

	@Override
	public boolean addWEKAEntry(Object[] wekaEntry) {
		long time = (long) wekaEntry[0];
		int xCoordinate = (int) wekaEntry[1];
		double waterLevel = (double) wekaEntry[2];

		double[] rowValuesData = new double[]{time, xCoordinate, waterLevel};
		DenseInstance denseInstance = new DenseInstance(1.0, rowValuesData);
		return instances.add(denseInstance);
	}

	@Override
	void prepareWEKAEntry() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		Attribute waterLevelAttribute = new Attribute("waterLevel");
		Attribute xCoordinateAttribute = new Attribute("xCoordinate");
		Attribute timeAttribute = new Attribute("time");


		attributes.add(timeAttribute);
		attributes.add(xCoordinateAttribute);
		attributes.add(waterLevelAttribute);

		instances = new Instances("WaterLevels", attributes, 10000000);
		try {
			plot = new Plot2D();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void plotWEKAEntries() {
		try {
			plot.plot(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}


	}

}
