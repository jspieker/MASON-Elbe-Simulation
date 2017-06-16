package de.uni_oldenburg.simulation.elbe.WEKA;

import de.uni_oldenburg.simulation.WEKA.CollisionWEKA;
import de.uni_oldenburg.simulation.WEKA.Plot.BarPlot;
import de.uni_oldenburg.simulation.WEKA.Plot.Plot;
import de.uni_oldenburg.simulation.WEKA.Plot.Plot2D;
import de.uni_oldenburg.simulation.WEKA.WEKA;
import de.uni_oldenburg.simulation.WEKA.WaterLevelWEKA;
import de.uni_oldenburg.simulation.elbe.models.DynamicWaterLevel;
import de.uni_oldenburg.simulation.elbe.models.Tides;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import java.io.File;

/**
 * Tests {@link WEKA}, {@link CollisionWEKA}, {@link WaterLevelWEKA}, {@link Plot}, {@link de.uni_oldenburg.simulation.WEKA.Plot.BarPlot} and {@link de.uni_oldenburg.simulation.WEKA.Plot.Plot2D}.
 */
public class WEKATests {

	private WEKA weka;
	private Plot plot;

	Instances collisionInstances;
	Instances waterLevelInstances;

	@Before
	public void setUp() throws Exception {
		ArffLoader arffLoader = new ArffLoader();
		arffLoader.setFile(new File("src\\main\\resources\\collision.arff"));
		collisionInstances = arffLoader.getDataSet();
		arffLoader.setFile(new File("src\\main\\resources\\waterLevel.arff"));
		waterLevelInstances = arffLoader.getDataSet();
	}

	@After
	public void finish() {
		// nothing to do ...
	}

	@Test
	public void WaterLevelWEKA_confirms() throws Exception {
		weka = new WaterLevelWEKA("src\\test\\resources\\");
		weka.addWEKAEntry(new Object[]{1L, 10, 1.0});
		weka.writeWEKAEntries();
		weka.plotWEKAEntries();
	}

	@Test
	public void CollisionWEKA_confirms() throws Exception {
		weka = new CollisionWEKA("src\\test\\resources\\");
		weka.addWEKAEntry(new Object[]{1L, false, true, 10, 1, 10, 1});
		weka.writeWEKAEntries();
		weka.plotWEKAEntries();
	}

	@Test
	public void BarPlot_confirms() throws Exception {
		Plot plot = new BarPlot();
		plot.plot(collisionInstances);
	}

	@Test
	public void Plot2D_confirms() throws Exception {
		Plot plot = new Plot2D();
		plot.plot(waterLevelInstances);
	}
}
