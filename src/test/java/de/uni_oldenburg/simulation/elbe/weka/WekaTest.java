package de.uni_oldenburg.simulation.elbe.weka;

import de.uni_oldenburg.simulation.weka.CollisionWeka;
import de.uni_oldenburg.simulation.weka.plot.CollisionBarPlot;
import de.uni_oldenburg.simulation.weka.plot.Plot;
import de.uni_oldenburg.simulation.weka.plot.Plot2D;
import de.uni_oldenburg.simulation.weka.Weka;
import de.uni_oldenburg.simulation.weka.WaterLevelWeka;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Weka}, {@link CollisionWeka}, {@link WaterLevelWeka}, {@link Plot}, {@link CollisionBarPlot} and {@link de.uni_oldenburg.simulation.weka.plot.Plot2D}.
 */
public class WekaTest {

	private Weka weka;
	private Plot plot;

	Instances collisionInstances;
	Instances waterLevelInstances;

	@Before
	public void setUp() throws Exception {
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setFile(new File("src/main/resources/collision0.arff"));
		collisionInstances = arffLoader.getDataSet();
		arffLoader.setFile(new File("src/main/resources/waterLevel0.arff"));
		waterLevelInstances = arffLoader.getDataSet();
	}

	@After
	public void finish() {
		// nothing to do ...
	}

	@Test
	public void WaterLevelWEKA_confirms() throws Exception {
		weka = new WaterLevelWeka("src/test/resources/");
		weka.addWEKAEntry(new Object[]{1L, 10, 1.0});
		weka.writeWEKAEntries();
		//weka.plotWEKAEntries();
	}

	@Test
	public void CollisionWEKA_confirms() throws Exception {
		weka = new CollisionWeka("src/test/resources/");
		weka.addWEKAEntry(new Object[]{1L, false, true, 10, 20, 1, 15, 10, 15.0, 4, 2.5});
		weka.writeWEKAEntries();
		//weka.plotWEKAEntries();
	}

	@Test
	public void BarPlot_confirms() throws Exception {
		//plot plot = new CollisionBarPlot();
		//plot.plot(collisionInstances);
	}

	@Test
	public void Plot2D_confirms() throws Exception {
		Plot plot = new Plot2D();
		//plot.plot(waterLevelInstances);
	}
}
