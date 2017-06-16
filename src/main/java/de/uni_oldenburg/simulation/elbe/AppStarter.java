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
		if (args == null || args[0] == null) {
			System.out.println("You have to pass the WEKA path to specify where to store the *.arff files.");
		} else {
			System.out.println(args[0]);
			new ElbeWithUI(args[0]).createController();
		}
	}
}