package de.uni_oldenburg.simulation.WEKA;

import de.uni_oldenburg.simulation.WEKA.Plot.BarPlot;
import de.uni_oldenburg.simulation.WEKA.Plot.Plot;
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
		int numContainerShip = (int) wekaEntry[3];
		int numOtherShip = (int) wekaEntry[4];
		int almostCollisions = (int) wekaEntry[5];
		int collisions = (int) wekaEntry[6];

		double[] rowValuesData = new double[]{timeRun, (isTideActive ? 1 : 0), (isElbeWidened ? 1 : 0), numContainerShip, numOtherShip, almostCollisions, collisions};
		DenseInstance denseInstance = new DenseInstance(1.0, rowValuesData);
		return instances.add(denseInstance);
	}

	@Override
	void prepareWEKAEntry() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		Attribute timeRunAttribute = new Attribute("TimeRun");
		Attribute isTideActiveAttribute = new Attribute("isTideActive");
		Attribute isElbeWidenedAttribute = new Attribute("IsElbeWidened");
		// TODO num of ships normal or heavy ship
		Attribute numContainerShipAttribute = new Attribute("numContainerShip");
		Attribute numOtherShipAttribute = new Attribute("numOtherShip");
		Attribute almostCollisionsAttribute = new Attribute("AlmostCollisions");
		Attribute collisionsAttribute = new Attribute("Collisions");

		attributes.add(timeRunAttribute);
		attributes.add(isTideActiveAttribute);
		attributes.add(isElbeWidenedAttribute);
		attributes.add(numContainerShipAttribute);
		attributes.add(numOtherShipAttribute);
		attributes.add(almostCollisionsAttribute);
		attributes.add(collisionsAttribute);

		instances = new Instances("WaterLevels", attributes, 100000000);
	}

	@Override
	public void plotWEKAEntries() {
		try {
			new BarPlot().plot(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// use waterlevel, collision, xCoordinate, time and tideSet

}

