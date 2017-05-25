package de.uni_oldenburg.simulation.elbe;

import org.junit.Before;
import org.junit.Test;
import sim.engine.SimState;


import static org.junit.Assert.assertEquals;

/**
 * Tests {@link  ElbeWithUI}.
 */
public class ElbeWithUITest {

	private ElbeWithUI elbeWithUI;

	@Before
	public void setUp() {
		elbeWithUI = new ElbeWithUI();
	}

	@Test
	public void loadState() {
		SimState simState = new SimState(0);
		elbeWithUI = new ElbeWithUI(simState);
		assertEquals(elbeWithUI.getSimulationInspectedObject(), simState);
	}

	@Test
	public void getName() {
		assertEquals(elbeWithUI.getName(), "Elbe Simulation");
	}

	@Test
	public void quit() {
		elbeWithUI.quit();
	}
}