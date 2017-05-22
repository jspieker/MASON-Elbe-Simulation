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
	 * Is the length of the Elbe. The parameter is used to determine whether a specific x coordinate is affected by the moon attraction or not.
	 */
	private long elbeLength;

	/**
	 * Tide constructor to initialize the tides context given the parameters.
	 *
	 * @param highTidePeriod  Is the time needed for the high tide in seconds.
	 * @param lowTidePeriod   Is the time needed for the low tide in seconds.
	 * @param isHighTideFirst Determines whether the simulation starts with high tide or not (low tide).
	 * @param elbeLength      Is the length of the Elbe. The parameter is used to determine whether a specific x coordinate is affected by the moon attraction or not.
	 * @throws Exception Is thrown if highTidePeriod or lowTidePeriod are set inappropriate.
	 */
	public Tides(final long highTidePeriod, final long lowTidePeriod, final boolean isHighTideFirst, long elbeLength) throws Exception {
		if (highTidePeriod <= 0) throw new Exception("The high tide period must not be negative or zero.");
		if (lowTidePeriod <= 0) throw new Exception("The low tide period must not be negative or zero.");
		this.highTidePeriod = highTidePeriod;
		this.lowTidePeriod = lowTidePeriod;
		this.moonAttraction = 0.0;
		this.isHighTideFirst = isHighTideFirst;
		this.elbeLength = elbeLength;
	}

	/**
	 * Computes the attraction of the moon with the sinus function: sin((pi/highTide*isHighTide+lowTide*!isHighTide))*x).
	 * Afterwards the moon attraction can be got using {@link Tides#getMoonAttraction()} and used as a multiplier to the current water level.
	 * The switching between low and high tide is done automatically.
	 *
	 * @param time Is the time value at which the moon attraction is wanted in seconds.
	 * @throws Exception Is thrown if the time in seconds is negative.
	 */
	public void computeMoonAttraction(long time) throws Exception {
		if (time < 0) throw new Exception("The time in seconds must not be negative.");
		// we need to reset the time after one whole cycle from low tide to high tide to low tide or from high tide to low tide to high tide.
		time %= (highTidePeriod + lowTidePeriod);
		boolean isHighTide = isHighTide(time);

		if (isHighTideFirst && !isHighTide) { // modulo the number to the seconds within the new one
			time -= highTidePeriod; // lowTideIsNotSet
		} else if (!isHighTideFirst && isHighTide) {
			time -= lowTidePeriod;
		}
		this.moonAttraction = Math.sin((Math.PI / (isHighTide ? highTidePeriod : lowTidePeriod)) * time + (isHighTide ? 0 : Math.PI));
	}

	/**
	 * Computes the water level by using {@link Tides#computeMoonAttraction(long)} and the given water level. The adjustment is then returned.
	 *
	 * @param time              Is the time value at which the moon attraction is wanted.
	 * @param averageWaterLevel Is the average water level that should be adjusted using the current moon attraction.
	 * @param xCoordinate       Is a x coordinate at the length of the elbe. The parameter is used to determine whether the position is affected by the moon attraction or not.
	 * @return The current water level adjusted by the current moon attraction or -1 if the x coordinate is not affected yet.
	 * @throws Exception Is thrown if either the time in seconds or the water level is negative.
	 */
	public double computeWaterLevel(long time, double averageWaterLevel, long xCoordinate) throws Exception {
		if (averageWaterLevel < 0) throw new Exception("The average water level cannot be negative.");
		computeMoonAttraction(time); // throws an exception if and only if time is negative
		if (isAffected(time, xCoordinate)) {
			return averageWaterLevel + averageWaterLevel * this.moonAttraction; // adds or subtracts the current moon attraction
		} else {
			return -1;
		}
	}

	/**
	 * Get the current moonAttraction.
	 *
	 * @return Return the current moonAttraciton value computed by {@link Tides#computeMoonAttraction(long)}
	 */
	public double getMoonAttraction() {
		return moonAttraction;
	}

	private boolean isAffected(long time, long xCoordinate) throws Exception {
		// TODO take a curve with x axis as the  time. if the xCoordinate is less than the y value returned that it is affected. Take into account to divide the time by two and start from the otherside to roll back

		if (time < 0) throw new Exception("The time in seconds must not be negative.");
		// we need to reset the time after one whole cycle from low tide to high tide to low tide or from high tide to low tide to high tide.
		time %= (highTidePeriod + lowTidePeriod);
		boolean isHighTide = isHighTide(time);

		if (isHighTideFirst && !isHighTide) { // modulo the number to the seconds within the new one
			time -= highTidePeriod; // lowTideIsNotSet
		} else if (!isHighTideFirst && isHighTide) {
			time -= lowTidePeriod;
		}

		double xPositionsAffectedAtTime = (elbeLength / (isHighTide ? highTidePeriod : lowTidePeriod)) * time;

		return !isHighTide && xPositionsAffectedAtTime >= xCoordinate /* Tide before the position */ || isHighTide && xPositionsAffectedAtTime < xCoordinate /* Tide behind the position */;

	}

	private boolean isHighTide(long time) {
		if (!isHighTideFirst && time >= lowTidePeriod || isHighTideFirst && time < highTidePeriod) {
			return true;
		} else if (isHighTideFirst && time >= highTidePeriod || !isHighTideFirst && time < lowTidePeriod) {
			return false;
		}
		return false; // should never be entered (check with code coverage)
	}


}
