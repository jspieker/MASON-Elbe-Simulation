package de.uni_oldenburg.simulation.elbe.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

/**
 * Tests {@link Tides}.
 */
public class TidesTest {

	private Tides instance;
	private long highTidePeriod = 25000;
	private long lowTidePeriod = 20000;
	private int elbeLength = 507 + 230 + 230 + 200 + 48;

	@Before
	public void setUp() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, true, elbeLength, true);
	}

	@After
	public void finish() {
	}

	@Test
	public void computeWaterLevel_passCorrectValueWithHighTide_confirms() throws Exception {
		long time = 25000;
		double waterLevel = Tides.AVERAGE_HIGH_TIDE_WATERLEVEL_ABOVE_CD;

		double moonAttractionExpected = 1.0;
		double newWaterLevelExpected = waterLevel * moonAttractionExpected;

		double newWaterLevelTest = instance.computeWaterLevel(time, 100);

		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);
	}

	@Test
	public void computeWaterLevel_passCorrectValueWithLowTide_confirms() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, false, elbeLength, true);

		long time = 20000;
		double newWaterLevelExpected = Tides.AVERAGE_LOW_TIDE_WATERLEVEL_ABOVE_CD;

		double newWaterLevelTest = instance.computeWaterLevel(time, elbeLength - 50);

		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);
	}

	@Test
	public void others_passCorrectValueWithLowTide_confirms() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, false, elbeLength, true);

		assertEquals("The actual moon attraction must meet the expected one.", 0.0, instance.getMoonAttraction());

		long time = 15000;
		double newWaterLevelExpected = Tides.AVERAGE_LOW_TIDE_WATERLEVEL_ABOVE_CD;

		double newWaterLevelTest = instance.computeWaterLevel(time, elbeLength - 50);

	}
}
