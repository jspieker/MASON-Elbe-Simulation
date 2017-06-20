/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.vessels;

import de.uni_oldenburg.simulation.elbe.Observer;

/*
 * A container ship
 */
public class Tanker extends AbstractVessel {

	public Tanker(boolean directionHamburg, Observer observer) {
		super(100, 150, 25, 7, directionHamburg, observer);
	}
}
