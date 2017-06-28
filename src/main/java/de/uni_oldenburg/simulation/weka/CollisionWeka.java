package de.uni_oldenburg.simulation.weka;

import de.uni_oldenburg.simulation.weka.plot.CollisionBarPlot;
import de.uni_oldenburg.simulation.weka.plot.CollisionWithShipsBarPlot;
import weka.core.Attribute;
import weka.core.DenseInstance;
import weka.core.Instances;

import java.util.ArrayList;

/**
 * Implements {@link Weka}.
 */
public class CollisionWeka extends Weka {

	public CollisionWeka(String path) {
		super(path, "collision");
	}

	@Override
	public boolean addWEKAEntry(Object[] wekaEntry) {
		long timeRun = (long) wekaEntry[0];
		boolean isTideActive = (boolean) wekaEntry[1];
		boolean isElbeExtended = (boolean) wekaEntry[2];
		int numContainerShip = (int) wekaEntry[3];
		int numTankerShip = (int) wekaEntry[4];
		int numOtherShip = (int) wekaEntry[5];
		int collisions = (int) wekaEntry[6];

		double[] rowValuesData = new double[]{timeRun, (isTideActive ? 1 : 0), (isElbeExtended ? 1 : 0), numContainerShip, numTankerShip, numOtherShip, collisions};
		DenseInstance denseInstance = new DenseInstance(1.0, rowValuesData);
		return instances.add(denseInstance);
	}

	@Override
	void prepareWEKAEntry() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		Attribute timeRunAttribute = new Attribute("time");
		Attribute isTideActiveAttribute = new Attribute("tideActive");
		Attribute isElbeExtendedAttribute = new Attribute("elbeExtended");
		// TODO num of ships normal or heavy ship
		Attribute numContainerShipAttribute = new Attribute("containerShip");
		Attribute numTankerShipAttribute = new Attribute("tankerShip");
		Attribute numOtherShipAttribute = new Attribute("otherShip");
		Attribute collisionsAttribute = new Attribute("collisions");

		attributes.add(timeRunAttribute);
		attributes.add(isTideActiveAttribute);
		attributes.add(isElbeExtendedAttribute);
		attributes.add(numContainerShipAttribute);
		attributes.add(numTankerShipAttribute);
		attributes.add(numOtherShipAttribute);
		attributes.add(collisionsAttribute);

		instances = new Instances("BarPlotValues", attributes, 10000000);
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

