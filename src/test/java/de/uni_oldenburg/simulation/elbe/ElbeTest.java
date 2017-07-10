package de.uni_oldenburg.simulation.elbe;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests {@link Elbe}.
 */
public class ElbeTest {

	private Elbe instance;
	private long highTidePeriod = 25000;
	private long lowTidePeriod = 20000;
	private int elbeLength = 966 + 704 + 65 + 154 + 135;

	long seed = System.currentTimeMillis();

	@Before
	public void setUp() throws Exception {
		instance = new Elbe(seed);
		instance.initWEKA("src/main/resources/");
	}

	@After
	public void finish() {
		instance = new Elbe(seed);
	}

	@Test
	public void calculateInitialValues_call_confirms() throws Exception {

		assertEquals("The fairway max width must meet the expected one.", 400, instance.getFairwayWidthMax());

		assertEquals("The fairway total length must meet the expected one.", 101200.0, instance.getFairwayLengthTotal(), 0);

		// TODO further test but not in detail
	}

	@Test
	public void start_call_confirms() throws Exception {

		instance.start();
		instance.executeStep();
		// TODO further tests
	}

	@Test
	public void setAndGetIsTideActive_LowTidePassCorrectValues_confirms() throws Exception {
		instance.start();
		instance.executeStep();
		instance.setTideActive(false);
		assertEquals("The must be deactivated.", false, instance.isTideActive());
	}
}
