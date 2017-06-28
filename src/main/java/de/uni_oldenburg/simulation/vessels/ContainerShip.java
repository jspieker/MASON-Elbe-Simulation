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
		super(100, 200, 30, 10, directionHamburg, humanErrorInShipLength, scale);
	}
}
