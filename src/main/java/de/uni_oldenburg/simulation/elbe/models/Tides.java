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
	 * @throws Exception Is thrown if highTidePeriod or lowTidePeriod are set inappropriate.
	 */
	public Tides(final long highTidePeriod, final long lowTidePeriod, final boolean isHighTideFirst) throws Exception {
		if (highTidePeriod <= 0) throw new Exception("The high tide period must not be negative or zero.");
		if (lowTidePeriod <= 0) throw new Exception("The low tide period must not be negative or zero.");
		this.highTidePeriod = highTidePeriod;
		this.lowTidePeriod = lowTidePeriod;
		this.moonAttraction = 0.0;
		this.isHighTideFirst = isHighTideFirst;
	}

	/**
	 * Computes the attraction of the moon with the sinus function: sin((pi/highTide*isHighTide+lowTide*!isHighTide))*x). Afterwards the moon attraction can be got using {@link Tides#getMoonAttraction()} and used as a multiplier to the current water level. The switching between low and high tide is done automatically.
	 *
	 * @param time Is the time value at which the moon attraction is wanted in seconds.
	 * @throws Exception Is thrown if the time in seconds is negative.
	 */
	public void computeMoonAttraction(long time) throws Exception {
		if (time < 0) throw new Exception("The time in seconds must not be negative.");
		boolean isHighTide = false;
		// we need to reset the time after one whole cycle from low tide to high tide to low tide or the other way around.
		time %= (highTidePeriod + lowTidePeriod);
		if (!isHighTideFirst && time >= lowTidePeriod || isHighTideFirst && time < highTidePeriod) {
			isHighTide = true;
		} else if (isHighTideFirst && time >= highTidePeriod || !isHighTideFirst && time < lowTidePeriod) {
			isHighTide = false;
		}

		if (isHighTideFirst && !isHighTide) { // modulu the number to the seconds within the new one
			time -= highTidePeriod; // lowTideIsNotSet
		} else if (!isHighTideFirst && isHighTide) {
			time -= lowTidePeriod;
		}
		this.moonAttraction = Math.sin((Math.PI / (isHighTide ? highTidePeriod : lowTidePeriod)) * time + (isHighTide ? 0 : Math.PI));
	}

	/**
	 * Computes the water level by using {@link Tides#computeMoonAttraction(long)} and the given water level. The adjustment is then returned.
	 *
	 * @param time       Is the time value at which the moon attraction is wanted.
	 * @param waterLevel Is the previous water level that should be adjusted using the current the moon attraction and to get the current water level.
	 * @return The current water level adjusted by the current moon attraction.
	 * @throws Exception Is thrown if either the time in seconds or the water level is negative.
	 */
	public double computeWaterLevel(long time, double waterLevel) throws Exception {
		if (waterLevel < 0) throw new Exception("The water level cannot be negative.");
		computeMoonAttraction(time); // throws an exception if and only if time is negative
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
