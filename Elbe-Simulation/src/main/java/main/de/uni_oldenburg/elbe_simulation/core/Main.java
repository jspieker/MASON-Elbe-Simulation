package main.de.uni_oldenburg.elbe_simulation.core;

import sim.engine.SimState;

import static sim.engine.SimState.doLoop;

/**
 * Main class to start the mason application with the main method. The class providing the main method must not extend something or maven or the jar file failes to find the mainclass
 */
public class Main {

	public static void main(String[] args) {

		doLoop(ElbeWithUI.class, args);

		SimState elbe = new ElbeWithUI(System.currentTimeMillis());
		System.exit(0); // ????
	}
}
