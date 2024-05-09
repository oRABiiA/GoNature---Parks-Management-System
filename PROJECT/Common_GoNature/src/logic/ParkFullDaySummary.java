package logic;

import java.io.Serializable;

import utils.enums.ParkNameEnum;

/**
 * Represents a summary of park occupancy for a specific hour.
 */
public class ParkFullDaySummary implements Serializable {
	/** Serial version UID for serialization. */
	private static final long serialVersionUID = -4838116767497819824L;

	/** The hour of the summary. */
	private int hour;

	/** The number of times the park was full during the specific hour. */
	private int timesFullInSpecificHour;

	/** The park associated with the summary. */
	private ParkNameEnum park;

	/**
	 * Constructs a ParkFullDaySummary with the given parameters.
	 * 
	 * @param hour                    The hour of the summary.
	 * @param timesFullInSpecificHour The number of times the park was full during
	 *                                the specific hour.
	 * @param park                    The park associated with the summary.
	 */
	public ParkFullDaySummary(int hour, int timesFullInSpecificHour, ParkNameEnum park) {
		this.hour = hour;
		this.timesFullInSpecificHour = timesFullInSpecificHour;
		this.park = park;
	}

	/**
	 * Default constructor for ParkFullDaySummary.
	 */
	public ParkFullDaySummary() {
	}

	/**
	 * Retrieves the hour of the summary.
	 * 
	 * @return The hour of the summary.
	 */
	public int getHour() {
		return hour;
	}

	/**
	 * Sets the hour of the summary.
	 * 
	 * @param hour The hour of the summary to set.
	 */
	public void setHour(int hour) {
		this.hour = hour;
	}

	/**
	 * Retrieves the number of times the park was full during the specific hour.
	 * 
	 * @return The number of times the park was full during the specific hour.
	 */
	public int getTimesFullInSpecificHour() {
		return timesFullInSpecificHour;
	}

	/**
	 * Sets the number of times the park was full during the specific hour.
	 * 
	 * @param timesFullInSpecificHour The number of times the park was full during
	 *                                the specific hour to set.
	 */
	public void setTimesFullInSpecificHour(int timesFullInSpecificHour) {
		this.timesFullInSpecificHour = timesFullInSpecificHour;
	}

	/**
	 * Retrieves the park associated with the summary.
	 * 
	 * @return The park associated with the summary.
	 */
	public ParkNameEnum getPark() {
		return park;
	}

	/**
	 * Sets the park associated with the summary.
	 * 
	 * @param park The park associated with the summary to set.
	 */
	public void setPark(ParkNameEnum park) {
		this.park = park;
	}
}