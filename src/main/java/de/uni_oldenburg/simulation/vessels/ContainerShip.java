/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.vessels;

/*
 * A container ship
 */
public class ContainerShip extends AbstractVessel {
	
	public ContainerShip(boolean directionHamburg, double humanErrorInShipLength, double scale) {
		super(15, 200, 30, 10, directionHamburg, humanErrorInShipLength, scale);
	}
}
