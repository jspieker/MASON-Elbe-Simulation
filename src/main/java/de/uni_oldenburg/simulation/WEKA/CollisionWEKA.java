package de.uni_oldenburg.simulation.WEKA;

import de.uni_oldenburg.simulation.WEKA.Plot.CollisionBarPlot;
import de.uni_oldenburg.simulation.WEKA.Plot.CollisionWithShipsBarPlot;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Implements {@link WEKA}.
 */
public class CollisionWEKA extends WEKA {

	public CollisionWEKA(String path) {
		super(path, "collision");
	}

	@Override
	public boolean addWEKAEntry(Object[] wekaEntry) {
		long timeRun = (long) wekaEntry[0];
		boolean isTideActive = (boolean) wekaEntry[1];
		boolean isElbeWidened = (boolean) wekaEntry[2];
		boolean isElbeDeepened = (boolean) wekaEntry[3];
		int numContainerShip = (int) wekaEntry[4];
		int numTankerShip = (int) wekaEntry[5];
		int numOtherShip = (int) wekaEntry[6];
		int almostCollisions = (int) wekaEntry[7];
		int collisions = (int) wekaEntry[8];

		double[] rowValuesData = new double[]{timeRun, (isTideActive ? 1 : 0), (isElbeWidened ? 1 : 0), (isElbeDeepened ? 1 : 0), numContainerShip, numTankerShip, numOtherShip, almostCollisions, collisions};
		DenseInstance denseInstance = new DenseInstance(1.0, rowValuesData);
		return instances.add(denseInstance);
	}

	@Override
	void prepareWEKAEntry() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		Attribute timeRunAttribute = new Attribute("TimeRun");
		Attribute isTideActiveAttribute = new Attribute("isTideActive");
		Attribute isElbeWidenedAttribute = new Attribute("isElbeWidened");
		Attribute isElbeDeepenedAttribute = new Attribute("isElbeDeepended");
		// TODO num of ships normal or heavy ship
		Attribute numContainerShipAttribute = new Attribute("numContainerShip");
		Attribute numTankerShipAttribute = new Attribute("numTankerShip");
		Attribute numOtherShipAttribute = new Attribute("numOtherShip");
		Attribute almostCollisionsAttribute = new Attribute("AlmostCollisions");
		Attribute collisionsAttribute = new Attribute("Collisions");

		attributes.add(timeRunAttribute);
		attributes.add(isTideActiveAttribute);
		attributes.add(isElbeWidenedAttribute);
		attributes.add(isElbeDeepenedAttribute);
		attributes.add(numContainerShipAttribute);
		attributes.add(numTankerShipAttribute);
		attributes.add(numOtherShipAttribute);
		attributes.add(almostCollisionsAttribute);
		attributes.add(collisionsAttribute);

		instances = new Instances("WaterLevels", attributes, 10000000);
	}

	@Override
	public void plotWEKAEntries() {
		try {
			new CollisionBarPlot().plot(instances);
			new CollisionWithShipsBarPlot().plot(instances);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// use waterlevel, collision, xCoordinate, time and tideSet

}

