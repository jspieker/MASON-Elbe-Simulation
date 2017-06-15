package de.uni_oldenburg.simulation.WEKA;

import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Implements {@link WEKA}.
 */
public class WaterLevelWEKA extends WEKA {

	public WaterLevelWEKA(String path) {
		super(path + "waterLevel.arff");
	}

	@Override
	public boolean addWEKAEntry(double waterLevel, int xCoordinate, int time) {
		double[] rowValuesData = new double[]{time, xCoordinate, waterLevel};
		DenseInstance denseInstance = new DenseInstance(1.0, rowValuesData);
		return instances.add(denseInstance);
	}

	@Override
	void prepareWEKAEntry() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		Attribute waterLevelAttribute = new Attribute("WaterLevel");
		Attribute xCoordinateAttribute = new Attribute("xCoordinate");
		Attribute timeAttribute = new Attribute("Time");

		attributes.add(timeAttribute);
		attributes.add(xCoordinateAttribute);
		attributes.add(waterLevelAttribute);

		instances = new Instances("WaterLevels", attributes, 100000000);

	}

	@Override
	public void writeWEKAEntries() {
		File file = new File(path);
		try {
			arffSaver.setFile(file);
			arffSaver.writeBatch();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
