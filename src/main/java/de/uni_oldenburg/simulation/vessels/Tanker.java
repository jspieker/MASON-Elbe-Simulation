/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.vessels;

/*
 * A container ship
 */
public class Tanker extends AbstractVessel {

	public Tanker(double draught, double length, double width,double targetSpeed, boolean directionHamburg,double humanErrorInShipLength, double scale)  {
		super(draught, length, width, targetSpeed, directionHamburg, humanErrorInShipLength, scale);
	}
}
