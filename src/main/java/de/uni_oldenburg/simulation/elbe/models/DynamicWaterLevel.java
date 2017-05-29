package de.uni_oldenburg.simulation.elbe.models;


/**
 * Simply put this class mirrors the {@link de.uni_oldenburg.simulation.elbe.Elbe#tidesMap} and calls the {@link Tides} class to compute the low or high tide. In addition, this class updates the {@link WaterLevel} for each x-Coordinate.
 */
public class DynamicWaterLevel {

	/**
	 * Is an array of water levels for each x-Coordinate indicated by the index of the array.
	 */
	private WaterLevel waterLevels[];

	/**
	 * Is an instance of the tides to compute the low or high tide depending on the time and the x-Coordinate.
	 */
	private Tides tides;

	/**
	 * Is the default waterLevel at the beginning of the simulation. The default water level value is used to calculate the water levels depending on the moon attraction.
	 */
	private double defaultWaterLevel;

	/**
	 * Constructor to initialize the waterLevels array with the specific water levels and the Tide to update the water levels.
	 *
	 * @param elbeLength        Is the length of the Elbe. Indicates the maximal index of the internal waterLevels array.
	 * @param defaultWaterLevel Is the default waterLevel at the beginning of the simulation. The default water level value is used to calculate the water levels depending on the moon attraction.
	 * @param highTidePeriod    Is the time needed for the high tide in seconds.
	 * @param lowTidePeriod     Is the time needed for the low tide in seconds.
	 * @param isHighTideFirst   Determines whether the simulation starts with high tide or not (low tide).
	 */
	public DynamicWaterLevel(int elbeLength, double defaultWaterLevel, final long highTidePeriod, final long lowTidePeriod, final boolean isHighTideFirst) {
		this.defaultWaterLevel = defaultWaterLevel;

		waterLevels = new WaterLevel[elbeLength];
		for (int x = 0; x < elbeLength; x++) {
			// TODO extra default Value for the waterLevel?
			waterLevels[x] = new WaterLevel(0, x);
		}

		tides = new Tides(highTidePeriod, lowTidePeriod, isHighTideFirst, elbeLength);
	}

	/**
	 * Getter mehtod to get the recent (newly) computed water levels at each x-Coordinate. Internally, the method calls an update-method to recompute all water levels.
	 *
	 * @param time Is the time value at which the moon attraction is wanted.
	 * @return The water level at each x-Coordinate (indicated by the index).
	 */
	public double[] getCurrentWaterLevels(long time) {
		updateWaterLevels(time);

		double[] currentWaterLevels = new double[this.waterLevels.length];
		for (int x = 0; x < currentWaterLevels.length; x++) {
			currentWaterLevels[x] = waterLevels[x].getLastKnownWaterLevel();
		}
		return currentWaterLevels;
	}

	/**
	 * Updates the all water levels at every x-Coordinate.
	 *
	 * @param time Is the time value at which the moon attraction is wanted.
	 */
	private void updateWaterLevels(long time) {
		for (int x = 0; x < waterLevels.length; x++) {
			double currentWaterLevel = tides.computeWaterLevel(time, this.defaultWaterLevel, x);
			if (currentWaterLevel >= 0) waterLevels[x].updateWaterLevel(currentWaterLevel);
		}
	}


}
