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
public class CollisionWEKA extends WEKA {

	public CollisionWEKA(String path) {
		super(path + "collision.arff");
	}

	@Override
	public boolean addWEKAEntry(Object[] wekaEntry) {
		long timeRun = (long) wekaEntry[0];
		boolean isTideActive = (boolean) wekaEntry[1];
		boolean isElbeWidened = (boolean) wekaEntry[2];
		int almostCollisions = (int) wekaEntry[3];
		int collisions = (int) wekaEntry[4];

		double[] rowValuesData = new double[]{timeRun, (isTideActive ? 1 : 0), (isElbeWidened ? 1 : 0), almostCollisions, collisions};
		DenseInstance denseInstance = new DenseInstance(1.0, rowValuesData);
		return instances.add(denseInstance);
	}

	@Override
	void prepareWEKAEntry() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		Attribute timeRunAttribute = new Attribute("TimeRun");
		Attribute isTideActiveAttribute = new Attribute("isTideActive");
		Attribute isElbeWidenedAttribute = new Attribute("IsElbeWidened");
		Attribute almostCollisionsAttribute = new Attribute("AlmostCollisions");
		Attribute collisionsAttribute = new Attribute("Collisions");

		attributes.add(timeRunAttribute);
		attributes.add(isTideActiveAttribute);
		attributes.add(isElbeWidenedAttribute);
		attributes.add(almostCollisionsAttribute);
		attributes.add(collisionsAttribute);

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

	@Override
	public void plotWEKAEntries() {
		try {
			Plot plot = new Plot(instances);
			plot.plotChart(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// use waterlevel, collision, xCoordinate, time and tideSet

}

