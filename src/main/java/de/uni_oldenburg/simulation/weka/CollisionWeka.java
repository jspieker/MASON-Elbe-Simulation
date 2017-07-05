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
		int numLargeContainerShips = (int) wekaEntry[3];
		int numSmallContainerShips = (int) wekaEntry[4];
		int numLargeTanker = (int) wekaEntry[5];
		int numSmallTanker = (int) wekaEntry[6];
		int collisions = (int) wekaEntry[7];
		double humanError = (double) wekaEntry[8];
		int waitingShips = (int) wekaEntry[9];

		double[] rowValuesData = new double[]{timeRun, (isTideActive ? 1 : 0), (isElbeExtended ? 1 : 0), numLargeContainerShips, numSmallContainerShips, numLargeTanker, numSmallTanker, collisions, humanError, waitingShips};
		DenseInstance denseInstance = new DenseInstance(1.0, rowValuesData);
		return instances.add(denseInstance);
	}

	@Override
	void prepareWEKAEntry() {
		ArrayList<Attribute> attributes = new ArrayList<>();
		Attribute timeRunAttribute = new Attribute("time");
		Attribute isTideActiveAttribute = new Attribute("tideActive");
		Attribute isElbeExtendedAttribute = new Attribute("elbeExtended");
		Attribute numLargeContainerShipAttribute = new Attribute("largeContainerShips");
		Attribute numSmallContainerShipAttribute = new Attribute("smallContainerShips");
		Attribute numLargeTankerAttribute = new Attribute("largeTanker");
		Attribute numSmallTankerAttribute = new Attribute("smallTanker");
		Attribute collisionsAttribute = new Attribute("collisions");
		Attribute humanErrorInShipLength = new Attribute("humanError");
		Attribute waitingShips = new Attribute("waitingShips");

		attributes.add(timeRunAttribute);
		attributes.add(isTideActiveAttribute);
		attributes.add(isElbeExtendedAttribute);
		attributes.add(numLargeContainerShipAttribute);
		attributes.add(numSmallContainerShipAttribute);
		attributes.add(numLargeTankerAttribute);
		attributes.add(numSmallTankerAttribute);
		attributes.add(collisionsAttribute);
		attributes.add(humanErrorInShipLength);
		attributes.add(waitingShips);

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

}

