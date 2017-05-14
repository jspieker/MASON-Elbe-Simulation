package de.uni_oldenburg.simulation.elbe.models;

/**
 * Computes the models of the elbe by using an adapted sinus function: f(t,y) = sin((pi/highTidePeriod)*t) TODO extend
 */
public class Tides {

	/**
	 * Is the time needed for the high tide in seconds.
	 */
	private long highTidePeriod;
	/**
	 * Is the time needed for the low tide in seconds.
	 */
	private long lowTidePeriod;
	/**
	 * Determines whether the simulation starts with high tide or not (low tide).
	 */
	private boolean isHighTideFirst;
	/**
	 * The moon attraction is set by the sinus function to determine how the water is pulled or pushed away.
	 */
	private double moonAttraction;

	/**
	 * Tide constructor to initialize the tides context given the parameters.
	 *
	 * @param highTidePeriod  Is the time needed for the high tide in seconds.
	 * @param lowTidePeriod   Is the time needed for the low tide in seconds.
	 * @param isHighTideFirst Determines whether the simulation starts with high tide or not (low tide).
	 */
	public Tides(final long highTidePeriod, final long lowTidePeriod, final boolean isHighTideFirst) {
		this.highTidePeriod = highTidePeriod;
		this.lowTidePeriod = lowTidePeriod;
		this.moonAttraction = 0.0;
		this.isHighTideFirst = isHighTideFirst;
	}

	/**
	 * Computes the attraction of the moon with the sinus function: sin((pi/highTide*isHighTide+lowTide*!isHighTide))*x). Afterwards the moon attraction can be got using {@link Tides#getMoonAttraction()} and used as a multiplier to the current water level. The switching between low and high tide is done automatically.
	 *
	 * @param time Is the time value at which the moon attraction is wanted.
	 */
	public void computeMoonAttraction(long time) {
		boolean isHighTide = false;
		// we need to reset the time after one whole cycle from low tide to high tide to low tide or the other way around.
		time %= (highTidePeriod + lowTidePeriod);
		if (!isHighTideFirst && time >= lowTidePeriod || isHighTideFirst && time < highTidePeriod) {
			isHighTide = true;
		} else if (isHighTideFirst && time >= highTidePeriod || !isHighTideFirst && time < lowTidePeriod) {
			isHighTide = false;
		}
		this.moonAttraction = Math.sin((Math.PI / (isHighTide ? highTidePeriod : lowTidePeriod)) * time);
	}

	/**
	 * Computes the water level by using {@link Tides#computeMoonAttraction(long)} and the given water level. The adjustment is then returned.
	 *
	 * @param time       Is the time value at which the moon attraction is wanted.
	 * @param waterLevel Is the previous water level that should be adjusted using the current the moon attraction and to get the current water level.
	 * @return The current water level adjusted by the current moon attraction.
	 */
	public double computeWaterLevel(long time, double waterLevel) {
		computeMoonAttraction(time);
		return waterLevel + waterLevel * this.moonAttraction; // adds or subtracts the current moon attraction
	}

	/**
	 * Get the current moonAttraction.
	 *
	 * @return Return the current moonAttraciton value computed by {@link Tides#computeMoonAttraction(long)}
	 */
	public double getMoonAttraction() {
		return moonAttraction;
	}


}
