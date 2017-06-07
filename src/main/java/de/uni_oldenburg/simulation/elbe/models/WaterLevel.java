package de.uni_oldenburg.simulation.elbe.models;

/**
 * Saves the last water level at each x-Coordinate.
 */
public class WaterLevel {

	/**
	 * Is the last known water level at x-coordinate.
	 */
	private double lastKnownWaterLevel;
	/**
	 * Is the x-Coordinate of this water level.
	 */
	private int xCoordinate;

	/**
	 * Is the constructor to save the last known water level at the passed x-Coordinate
	 *
	 * @param lastKnownWaterLevel Is the last known water level at x-coordinate.
	 * @param xCoordinate         Is the x-Coordinate of this water level.
	 */
	public WaterLevel(double lastKnownWaterLevel, int xCoordinate) {
		this.lastKnownWaterLevel = lastKnownWaterLevel;
		this.xCoordinate = xCoordinate;
	}

	/**
	 * Updates the last known water level to the current water level.
	 *
	 * @param waterLevel The current water level.
	 */
	public void updateWaterLevel(double waterLevel) {
		this.lastKnownWaterLevel = waterLevel;
	}

	/**
	 * Getter method to get the x-Coordinate at where the water level has to be saved.
	 *
	 * @return The x-Coordinate of this water level.
	 */
	public int getxCoordinate() {
		return xCoordinate;
	}

	/**
	 * Getter method to get the last known water level of this x-Coordinate.
	 *
	 * @return The water level of this x-Coordinate.
	 */
	public double getLastKnownWaterLevel() {
		return lastKnownWaterLevel;
	}
}
