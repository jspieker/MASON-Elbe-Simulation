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
		try {
			System.out.println("Using " + args[0] + " as *.arff storage dir.");
			new ElbeWithUI(args[0]).createController();
		} catch (IndexOutOfBoundsException e) {
			System.out.println("You have to pass the WEKA path to specify where to store the *.arff files. E.g. \"src/main/resources/\" (without quotation marks)");
		}
	}
}