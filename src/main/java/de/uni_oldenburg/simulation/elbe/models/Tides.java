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
	 */
	public Tides(final long highTidePeriod, final long lowTidePeriod, final boolean isHighTideFirst, long elbeLength) {
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
	 */
	public void computeMoonAttraction(long time) {

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
	 */
	public double computeWaterLevel(long time, double averageWaterLevel, long xCoordinate) {

		computeMoonAttraction(time);
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

	private boolean isAffected(long time, long xCoordinate) {
		// TODO rework!!!!
		// we need to reset the time after one whole cycle from low tide to high tide to low tide or from high tide to low tide to high tide.
		time %= (highTidePeriod + lowTidePeriod);
		boolean isHighTide = isHighTide(time);

		if (isHighTideFirst && !isHighTide) { // modulo the number to the seconds within the new one
			time -= highTidePeriod; // lowTideIsNotSet
		} else if (!isHighTideFirst && isHighTide) {
			time -= lowTidePeriod;
		}

		if (isHighTide && time >= ((double) highTidePeriod) / 2) { // backwards
			double xPositionsAffectedAtTime = elbeLength * 2 - ((double) elbeLength / ((double) (highTidePeriod / 2))) * time; // the time is divided by two because at the top of the sinus function e.g. Hamburg is affected already

			if (xPositionsAffectedAtTime <= xCoordinate) {
				System.out.println(xCoordinate + " true");
				return true;
			} else {
				return false;
			}
		} else if (!isHighTide && time >= ((double) lowTidePeriod) / 2) { // backwards
			double xPositionsAffectedAtTime = elbeLength * 2 - ((double) elbeLength / ((double) (lowTidePeriod / 2))) * time; // the time is divided by two because at the top of the sinus function e.g. Hamburg is affected already

			if (xPositionsAffectedAtTime > xCoordinate) {
				System.out.println(xCoordinate + " true");
				return true;
			} else {
				return false;
			}
		} else { // forwards
			double xPositionsAffectedAtTime = ((double) elbeLength / (isHighTide ? ((double) highTidePeriod) / 2 : ((double) lowTidePeriod) / 2)) * time; // the time is divided by two because at the top of the sinus function e.g. Hamburg is affected already

			if (isHighTide && xPositionsAffectedAtTime >= xCoordinate) {
				return true;
			} else if (!isHighTide && xPositionsAffectedAtTime < xCoordinate) {
				return true;
			} else {
				return false;
			}
		}
	}

	private boolean isHighTide(long time) {
		if (!isHighTideFirst && time >= lowTidePeriod || isHighTideFirst && time < highTidePeriod) {
			return true;
		} //else if (isHighTideFirst && time >= highTidePeriod || !isHighTideFirst && time < lowTidePeriod) {
		return false; // else to satisfy the interpreter
	}
}