package de.uni_oldenburg.simulation.elbe;

import de.uni_oldenburg.simulation.WEKA.WaterLevelWEKA;

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
		WaterLevelWEKA waterLevelWEKA = new WaterLevelWEKA("C:\\Users\\Icebreaker\\Desktop\\git\\Elbe\\Simulation\\src\\main\\resources\\");
		waterLevelWEKA.addWEKAEntry(19,1,10);
		waterLevelWEKA.writeWEKAEntries();
	}
}