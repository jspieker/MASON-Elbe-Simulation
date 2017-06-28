/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.vessels;

/*
 * A container ship
 */
public class ContainerShip extends AbstractVessel {
	

	public ContainerShip(double draught, double length, double width,double targetSpeed, boolean directionHamburg, double humanErrorInShipLength, double scale) {
		super(draught, length, width, targetSpeed, directionHamburg, humanErrorInShipLength, scale);
		
	}
}
