package de.uni_oldenburg.simulation.elbe;

import de.uni_oldenburg.simulation.elbe.models.DynamicWaterLevel;
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
	private int elbeLength = 507 + 230 + 230 + 200 + 48;

	@Before
	public void setUp() throws Exception {
		long seed = System.currentTimeMillis();
		instance = new Elbe(seed);
	}

	@After
	public void finish() {
		// nothing to do ...
	}

	@Test
	public void calculateInitialValues_call_confirms() throws Exception {

		assertEquals("The fairway max width must meet the expected one.", 400, instance.getFairwayWidthMax());

		assertEquals("The fairway total length must meet the expected one.", elbeLength, instance.getFairwayLengthTotal());

		// TODO further test but not in detail
	}

	@Test
	public void start_call_confirms() throws Exception {

		instance.start();
		instance.executeStep();
		// TODO further tests
	}
}
