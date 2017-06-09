package de.uni_oldenburg.simulation.elbe.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link WaterLevel}.
 */
public class WaterLevelTest {

	private WaterLevel instance;

	private int elbeLength = 507 + 230 + 230 + 200 + 48;
	private double lastKnownWaterLevel = 1.5;

	@Before
	public void setUp() throws Exception {
		instance = new WaterLevel(lastKnownWaterLevel, elbeLength - 1);
	}

	@After
	public void finish() {
		// nothing to do ...
	}

	@Test
	public void updateWaterLevel_AllGetter_passCorrectValues_confirms() throws Exception {
		double waterLevelExpected = 1.5;
		assertEquals("The actual water level must meet the expected one.", waterLevelExpected, instance.getLastKnownWaterLevel(), 0.00000000001);
		waterLevelExpected = 3.5;

		instance.updateWaterLevel(waterLevelExpected);
		assertEquals("The actual water level must meet the expected one.", waterLevelExpected, instance.getLastKnownWaterLevel(), 0.00000000001);

		assertEquals("The actual x coordinate must meet the expected one.", elbeLength - 1, instance.getxCoordinate());
	}

}
