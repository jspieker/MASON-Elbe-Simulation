package main.de.uni_oldenburg.elbe_simulation.core;

import sim.engine.SimState;

/**
 * Starts the graphical interface (GUI) of MASON by extending the {@link SimState} and others.
 */
public class ElbeWithUI extends SimState {

	/**
	 * Private properties do not need documentation
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Constructor to start the Elbe sumlation
	 *
	 * @param seed is the seed for the MersenneTwister
	 */
	public ElbeWithUI(long seed) {
		super(seed);
		// TODO Auto-generated constructor stub
	}

}
