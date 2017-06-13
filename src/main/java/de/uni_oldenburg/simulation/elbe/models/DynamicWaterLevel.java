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
	 * Constructor to initialize the waterLevels array with the specific water levels and the Tide to update the water levels.
	 *
	 * @param elbeLength      Is the length of the Elbe. Indicates the maximal index of the internal waterLevels array.
	 * @param highTidePeriod  Is the time needed for the high tide in seconds.
	 * @param lowTidePeriod   Is the time needed for the low tide in seconds.
	 * @param isHighTideFirst Determines whether the simulation starts with high tide or not (low tide).
	 * @param isTideActive    Boolean value indicating whether the tide computation is active or not. If not the highest high tide value (Hamburg has the highest one) is always returned on a new water level request.
	 */
	public DynamicWaterLevel(int elbeLength, final long highTidePeriod, final long lowTidePeriod, final boolean isHighTideFirst, boolean isTideActive) {
		waterLevels = new WaterLevel[elbeLength];
		for (int x = 0; x < elbeLength; x++) {
			if (isHighTideFirst) {
				waterLevels[x] = new WaterLevel(Tides.AVERAGE_LOW_TIDE_WATERLEVEL_ABOVE_CD, x);

			} else {
				waterLevels[x] = new WaterLevel(Tides.AVERAGE_HIGH_TIDE_WATERLEVEL_ABOVE_CD, x);
			}
		}

		tides = new Tides(highTidePeriod, lowTidePeriod, isHighTideFirst, elbeLength, isTideActive);
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
			double newWaterLevel = tides.computeWaterLevel(time, x);
			waterLevels[x].updateWaterLevel(newWaterLevel);
		}
	}


}
