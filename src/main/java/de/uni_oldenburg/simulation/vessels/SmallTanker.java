package de.uni_oldenburg.simulation.vessels;

public class SmallTanker extends AbstractVessel {

	public SmallTanker(boolean directionHamburg, double humanErrorInShipLength, double scale, double draught, double length, double width, double targetSpeed) {
		super(draught, length, width, targetSpeed, directionHamburg, humanErrorInShipLength, scale);
	}

}
