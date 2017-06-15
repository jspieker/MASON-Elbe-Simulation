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
	 * Is the time depending on the current tide and the corresponding sinus function interval.
	 */
	private long time;
	/**
	 * A boolean value to determine whether the current tide is high or low.
	 */
	private boolean isHighTide;
	/**
	 * Boolean value indicating whether the tide computation is active or not.
	 */
	private boolean isTideActive;

	/**
	 * Is the average water level that is neither affected by the low nor high tide.
	 * The final value is computed by putting the average high tide water level in relation to the average low tide water level.
	 * The average water level is used to adjust the water level above CD.
	 */
	static final double AVERAGE_LOW_TIDE_WATERLEVEL_ABOVE_CD = 0.466666667; // Low tide minimum
	static final double AVERAGE_HIGH_TIDE_WATERLEVEL_ABOVE_CD = 3.6; // high tide maximum

	static final double AVERAGE_WATERLEVEL_ABOVE_CD_ADDER = 0.466666667;
	static final double AVERAGE_WATERLEVEL_ABOVE_CD_MOON_ATTRACTION_MULTIPLIER = 3.133333333;

	private final double waterLevelHWDelta = 0.233;

	/**
	 * Tide constructor to initialize the tides context given the parameters.
	 *
	 * @param highTidePeriod  Is the time needed for the high tide in seconds.
	 * @param lowTidePeriod   Is the time needed for the low tide in seconds.
	 * @param isHighTideFirst Determines whether the simulation starts with high tide or not (low tide).
	 * @param elbeLength      Is the length of the Elbe. The parameter is used to determine whether a specific x coordinate is affected by the moon attraction or not.
	 * @param isTideActive    Boolean value indicating whether the tide computation is active or not. If not the highest high tide value (Hamburg has the highest one) is always returned on a new water level request.
	 */
	public Tides(final long highTidePeriod, final long lowTidePeriod, final boolean isHighTideFirst, long elbeLength, boolean isTideActive) {
		this.highTidePeriod = highTidePeriod;
		this.lowTidePeriod = lowTidePeriod;
		this.moonAttraction = 0.0;
		this.isHighTideFirst = isHighTideFirst;
		this.elbeLength = elbeLength;
		this.isTideActive = isTideActive;
	}

	/**
	 * Computes the water level by using {@link Tides#computeMoonAttraction()} and the given water level. The adjustment is then returned.
	 *
	 * @param time        Is the time value at which the moon attraction is wanted.
	 * @param xCoordinate Is a x coordinate at the length of the elbe. The parameter is used to determine whether the position is affected by the moon attraction or not.
	 * @return The current water level adjusted by the current moon attraction or -1 if the x coordinate is not affected yet.
	 */
	public double computeWaterLevel(long time, long xCoordinate) {
		if (isTideActive) {
			computeTime(time);
			computeMoonAttraction();
			double waterLevel = (this.moonAttraction * AVERAGE_WATERLEVEL_ABOVE_CD_MOON_ATTRACTION_MULTIPLIER + AVERAGE_WATERLEVEL_ABOVE_CD_ADDER) + computeWaterLevelDelta(xCoordinate);

			return waterLevel;
		} else {
			return AVERAGE_HIGH_TIDE_WATERLEVEL_ABOVE_CD + this.waterLevelHWDelta;
		}

	}

	private double computeWaterLevelDelta(long xCoordinate) {
		double waterLevelDelta;

		if (isHighTide) {
			waterLevelDelta = (((waterLevelHWDelta / elbeLength) * xCoordinate) / highTidePeriod) * time;
		} else {
			waterLevelDelta = ((((waterLevelHWDelta / elbeLength) * xCoordinate) / lowTidePeriod) * (lowTidePeriod - time));
		}

		return waterLevelDelta;
	}

	// private methods

	/**
	 * Computes the attraction of the moon with the cosinus function: (-1*isHighTide)*cos((pi/highTide*isHighTide+lowTide*!isHighTide))*x).
	 * Afterwards the moon attraction can be got using {@link Tides#getMoonAttraction()} and used as a multiplier to the current water level.
	 * The switching between low and high tide is done automatically.
	 */
	private void computeMoonAttraction() {
		this.moonAttraction = Math.sin((Math.PI / (2 * (isHighTide ? highTidePeriod : lowTidePeriod))) * time + (isHighTide ? 0 : (Math.PI / 2)));
	}

	private void computeTime(long time) {
		// we need to reset the time after one whole cycle from low tide to high tide to low tide or from high tide to low tide to high tide.
		time %= (highTidePeriod + lowTidePeriod);
		isHighTide = isHighTide(time);

		if (isHighTideFirst && !isHighTide) { // modulo the number to the seconds within the new one
			time -= highTidePeriod; // lowTideIsNotSet
		} else if (!isHighTideFirst && isHighTide) {
			time -= lowTidePeriod;
		}
		this.time = time;
	}

	private boolean isHighTide(long time) {
		return !isHighTideFirst && time >= lowTidePeriod || isHighTideFirst && time < highTidePeriod;
	}

	// getter and setter

	/**
	 * Get the current moonAttraction.
	 *
	 * @return Return the current moonAttraction value computed by {@link Tides#computeMoonAttraction()}
	 */
	public double getMoonAttraction() {
		return moonAttraction;
	}


	public void setTideActive(boolean tideActive) {
		isTideActive = tideActive;
	}
}