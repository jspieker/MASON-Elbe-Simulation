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
		instance = new Tides(highTidePeriod, lowTidePeriod, true, elbeLength);
	}

	@After
	public void finish() {
	}

	@Test
	public void computeWaterLevel_passCorrectValueWithHighTide_confirms() throws Exception {
		long time = 10000;
		double waterLevel = 4.0; // 4.0 meters

		double moonAttractionExpected = 0.9510565162951536; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D0+and+x%3D15000
		double newWaterLevelExpected = waterLevel + waterLevel * moonAttractionExpected;

		double newWaterLevelTest = instance.computeWaterLevel(time, waterLevel, 100);

		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);
	}

	@Test
	public void computeWaterLevel_passCorrectValueWithLowTide_confirms() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, false, elbeLength);
		double moonAttractionExpected = -0.7071067811865475; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D1+and+x%3D15000
		double waterLevel = 4.0; // 4.0 meters

		double newWaterLevelExpected = waterLevel + waterLevel*moonAttractionExpected;
		double newWaterLevelTest = instance.computeWaterLevel(5000, waterLevel, elbeLength-50);

		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);
	}

	@Test
	public void computeWaterLevel_passCorrectValueButNotYetAffectedWitHighTide_confirms() throws Exception {
		long time = 5000;
		double waterLevel = 1.80; // 1.8 meters

		double newWaterLevelExpected = -1;

		double newWaterLevelTest = instance.computeWaterLevel(time, waterLevel, elbeLength /* Hamburg */);

		assertEquals("The expected water level must be -1.", newWaterLevelExpected, newWaterLevelTest);

	}

	@Test
	public void computeWaterLevel_passCorrectValueButNotYetAffectedWitLowTide_confirms() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, false, elbeLength);

		long time = 5000;
		double waterLevel = 1.80; // 1.8 meters

		double newWaterLevelExpected = -1;

		double newWaterLevelTest = instance.computeWaterLevel(time, waterLevel, 0 /* Elbe entry */);

		assertEquals("The expected water level must be -1.", newWaterLevelExpected, newWaterLevelTest);

	}

	@Test
	public void computeWaterLevel_passCorrectLowTideValueThenSwitchToHighTide_confirms() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, false, elbeLength);
		double moonAttractionExpected = -0.7071067811865477; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D1+and+x%3D15000
		double waterLevel = 4.0; // 4.0 meters

		double newWaterLevelExpected = waterLevel + waterLevel*moonAttractionExpected;
		double newWaterLevelTest = instance.computeWaterLevel(15000, waterLevel, 0);

		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);

		// switch tide
		moonAttractionExpected = 0.587785252292473;// see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D0+and+x%3D25000-20000
		newWaterLevelExpected = waterLevel + waterLevel*moonAttractionExpected;
		newWaterLevelTest = instance.computeWaterLevel(25000, waterLevel, 0);
		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);
	}

	@Test
	public void computeWaterLevel_passCorrectHighTideValueThenSwitchToLowTide_confirms() throws Exception {
		double waterLevel = 4.0; // 4.0 meters

		double moonAttractionExpected = 0.9510565162951536; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D0+and+x%3D15000
		double newWaterLevelExpected = waterLevel + waterLevel*moonAttractionExpected;
		double newWaterLevelTest = instance.computeWaterLevel(10000, waterLevel, 500);

		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);

		// switch tide
		moonAttractionExpected = -0.7071067811865475; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D1+and+x%3D15000
		newWaterLevelExpected = waterLevel + waterLevel*moonAttractionExpected;
		newWaterLevelTest = instance.computeWaterLevel(30000, waterLevel, 500);
		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);
	}
}
