/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.vessels;

import de.uni_oldenburg.simulation.elbe.Observer;

/*
 * A container ship
 */
public class ContainerShip extends AbstractVessel {
	
	public ContainerShip(boolean directionHamburg, Observer observer) {
		super(100, 200, 30, 10, directionHamburg, observer);
	}
}
