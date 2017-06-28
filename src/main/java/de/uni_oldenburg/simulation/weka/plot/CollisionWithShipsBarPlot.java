package de.uni_oldenburg.simulation.weka.plot;

import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import weka.core.Instance;

/**
 * Implements {@link BarPlot}.
 */
public class CollisionWithShipsBarPlot extends BarPlot {


	protected CategoryDataset createDataset() {

		final DefaultCategoryDataset dataset =
				new DefaultCategoryDataset();

		for (Instance instance : instances) {
			double[] instanceValues = instance.toDoubleArray();

			//dataset.addValue(instanceValues[1], String.valueOf(instanceValues[0]), instance.attribute(1).name()); // isTideActive
			//dataset.addValue(instanceValues[2], String.valueOf(instanceValues[0]), instance.attribute(2).name()); // isElbeExtended
			dataset.addValue(instanceValues[3], String.valueOf(instanceValues[0]), instance.attribute(3).name()); // numContainerShip
			dataset.addValue(instanceValues[4], String.valueOf(instanceValues[0]), instance.attribute(4).name()); // numTankerShip
			//dataset.addValue(instanceValues[5], String.valueOf(instanceValues[0]), instance.attribute(5).name()); // TODO enable other ship types
			dataset.addValue(instanceValues[6], String.valueOf(instanceValues[0]), instance.attribute(6).name()); // numCollisions

		}
		return dataset;
	}
}
