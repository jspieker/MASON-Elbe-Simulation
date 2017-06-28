/*
 * 
 * 
 */
package de.uni_oldenburg.simulation.vessels;

/*
 * A container ship
 */
public class Tanker extends AbstractVessel {

	public Tanker(boolean directionHamburg,double humanErrorInShipLength, double scale) {
		super(100, 150, 25, 7, directionHamburg, humanErrorInShipLength, scale);
	}
}
