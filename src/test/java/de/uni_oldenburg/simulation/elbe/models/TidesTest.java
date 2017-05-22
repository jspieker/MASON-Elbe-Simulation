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
	private long elbeLength = 507 + 230 + 230 + 200 + 48;

	@Before
	public void setUp() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, true, elbeLength);
	}

	@After
	public void finish() {
	}

	// init...
	@Test(expected = Exception.class)
	public void TidesConstructor_passWrongParams_throwsException() throws Exception {
		try { // catch the first exception to test the second parameter
			new Tides(-1, 1, true, elbeLength);
		} catch (Exception e) {
			new Tides(1, -1, false, elbeLength);
		}
	}

	// computeMoonAttraction()
	@Test(expected = Exception.class)
	public void computeMoonAttraction_passNegativeTime_throwsException() throws Exception {
		long time = -1;
		instance.computeMoonAttraction(time);
	}

	@Test
	public void computeMoonAttraction_passCorrectHighTideValue_confirms() throws Exception {
		double moonAttractionExpected = 0.9510565162951536; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D0+and+x%3D15000
		instance.computeMoonAttraction(15000);
		assertEquals("The expected moon attraction must met the computed one.", moonAttractionExpected, instance.getMoonAttraction());
	}

	@Test
	public void computeMoonAttraction_passCorrectLowTideValue_confirms() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, false, elbeLength);
		double moonAttractionExpected = -0.7071067811865477; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D1+and+x%3D15000
		instance.computeMoonAttraction(15000);
		assertEquals("The expected moon attraction must met the computed one.", moonAttractionExpected, instance.getMoonAttraction());
	}

	@Test
	public void computeMoonAttraction_passCorrectLowTideValueThenSwitchToHighTide_confirms() throws Exception {
		instance = new Tides(highTidePeriod, lowTidePeriod, false, elbeLength);
		double moonAttractionExpected = -0.7071067811865477; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D1+and+x%3D15000
		instance.computeMoonAttraction(15000);
		assertEquals("The expected moon attraction must met the computed one.", moonAttractionExpected, instance.getMoonAttraction());

		// switch tide
		moonAttractionExpected = 0.587785252292473;// see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D0+and+x%3D25000-20000
		instance.computeMoonAttraction(25000);
		assertEquals("The expected moon attraction must met the computed one.", moonAttractionExpected, instance.getMoonAttraction());
	}

	@Test
	public void computeMoonAttraction_passCorrectHighTideValueThenSwitchToLowTide_confirms() throws Exception {
		double moonAttractionExpected = 0.9510565162951536; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D0+and+x%3D15000
		instance.computeMoonAttraction(15000);
		assertEquals("The expected moon attraction must met the computed one.", moonAttractionExpected, instance.getMoonAttraction());

		// switch tide
		moonAttractionExpected = -1.0;// see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D1+and+x%3D35000-25000
		instance.computeMoonAttraction(35000);
		assertEquals("The expected moon attraction must met the computed one.", moonAttractionExpected, instance.getMoonAttraction());

	}

	// computeWaterLevel():
	@Test(expected = Exception.class)
	public void computeWaterLevel_passNegativeTime_throwsException() throws Exception {
		long time = -1;
		double waterLevel = 1.0;
		instance.computeWaterLevel(time, waterLevel, 100);
	}

	@Test(expected = Exception.class)
	public void computeWaterLevel_passNegativeWaterLevel_throwsException() throws Exception {
		long time = 1;
		double waterLevel = -1.0;
		instance.computeWaterLevel(time, waterLevel, 100);
	}

	@Test
	public void computeWaterLevel_passCorrectValue_confirms() throws Exception {
		long time = 15000;
		double waterLevel = 4.0; // 4.0 meters

		double moonAttractionExpected = 0.9510565162951536; // see wolfram alpha at https://www.wolframalpha.com/input/?i=sin((pi%2F(25000-y5000))*x+%2B+(pi*y)),+where+y%3D0+and+x%3D15000
		double newWaterLevelExpected = waterLevel + waterLevel * moonAttractionExpected;

		double newWaterLevelTest = instance.computeWaterLevel(time, waterLevel, 100);

		assertEquals("The expected water level must met the computed one.", newWaterLevelExpected, newWaterLevelTest);
	}


	// getMoonAttraction(): (implicitly tested)

}
