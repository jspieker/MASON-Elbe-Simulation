package de.uni_oldenburg.simulation.elbe.models;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests {@link Tides}.
 */
public class TidesTest {

	private Tides instance;

	@Before
	public void setUp() {
		long highTidePeriod = 20000;
		long lowTidePeriod = 25000;
		boolean isHighTide = true;
		instance = new Tides(highTidePeriod, lowTidePeriod, isHighTide);
	}

	@After
	public void finish() {
		// nothing to do...
	}

}
