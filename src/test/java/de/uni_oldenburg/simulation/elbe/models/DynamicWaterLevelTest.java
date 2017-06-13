package de.uni_oldenburg.simulation.elbe.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link DynamicWaterLevel}.
 */
public class DynamicWaterLevelTest {

	private DynamicWaterLevel instance;
	private long highTidePeriod = 25000;
	private long lowTidePeriod = 20000;
	private int elbeLength = 507 + 230 + 230 + 200 + 48;

	@Before
	public void setUp() throws Exception {
		instance = new DynamicWaterLevel(elbeLength, highTidePeriod, lowTidePeriod, true, true);
	}

	@After
	public void finish() {
		// nothing to do ...
	}

	@Test
	public void getCurrentWaterLevels_HighTidePassCorrectValues_confirms() throws Exception {

		long time = 0;

		double[] waterLevels = instance.getCurrentWaterLevels(time);

		for (double waterLevel : waterLevels) {
			assertEquals("The actual water level must meet the expected one.", Tides.AVERAGE_LOW_TIDE_WATERLEVEL_ABOVE_CD, waterLevel, 0.01);
		}
	}

	@Test
	public void getCurrentWaterLevels_LowTidePassCorrectValues_confirms() throws Exception {

		instance = new DynamicWaterLevel(elbeLength, highTidePeriod, lowTidePeriod, false, true);
		long time = 0;
		double[] waterLevels = instance.getCurrentWaterLevels(time);

		for (double waterLevel : waterLevels) {
			assertEquals("The actual water level must meet the expected one.", Tides.AVERAGE_HIGH_TIDE_WATERLEVEL_ABOVE_CD, waterLevel, 0.01);
		}
	}
}
