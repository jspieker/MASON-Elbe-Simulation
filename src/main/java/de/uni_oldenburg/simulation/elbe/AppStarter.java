package de.uni_oldenburg.simulation.elbe;

/**
 * AppStarter to start the MASON application.
 * ElbeWithUI can't be started directly via jar because of MASON restraints
 */
public class AppStarter {

	/**
	 * Enter the program here
	 */
	public static void main(String[] args) {
		new ElbeWithUI().createController();
	}
}