package de.uni_oldenburg.simulation.elbe;

import sim.engine.SimState;

/**
 * Created by adrian-jagusch on 09.05.17.
 */
public class Elbe extends SimState {

	public Elbe(long seed) {
		super(seed);
	}

	/**
	 * Start the simulation
	 */
	public void start() {
		super.start();  // clear out the schedule
	}
}