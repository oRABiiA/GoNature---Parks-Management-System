package logic;

import java.io.Serializable;

import utils.enums.ParkNameEnum;

/**
 * Represents a summary of daily park statistics.
 */
public class ParkDailySummary implements Serializable {

	/** Serial version UID for serialization. */
	private static final long serialVersionUID = -932253738615990688L;

	/** The day of the summary. */
	private int day;

	/** The number of canceled orders for the day. */
	private int cancelsOrders;

	/** The number of orders that passed their scheduled time for the day. */
	private int timePassedOrders;

	/** The total number of orders for the day. */
	private int totalOrders;

	/** The park associated with the summary. */
	private ParkNameEnum park;

	/**
	 * Constructs a ParkDailySummary with the given parameters.
	 * 
	 * @param day              The day of the summary.
	 * @param cancelsOrders    The number of canceled orders for the day.
	 * @param timePassedOrders The number of orders that passed their scheduled time
	 *                         for the day.
	 * @param totalOrders      The total number of orders for the day.
	 * @param park             The park associated with the summary.
	 */
	public ParkDailySummary(int day, int cancelsOrders, int timePassedOrders, int totalOrders, ParkNameEnum park) {
		super();
		this.day = day;
		this.cancelsOrders = cancelsOrders;
		this.timePassedOrders = timePassedOrders;
		this.totalOrders = totalOrders;
		this.park = park;
	}

	/**
	 * Default constructor for ParkDailySummary.
	 */
	public ParkDailySummary() {
	}

	/**
	 * Retrieves the day of the summary.
	 * 
	 * @return The day of the summary.
	 */
	public int getDay() {
		return day;
	}

	/**
	 * Sets the day of the summary.
	 * 
	 * @param day The day of the summary to set.
	 */
	public void setDay(int day) {
		this.day = day;
	}

	/**
	 * Retrieves the number of canceled orders for the day.
	 * 
	 * @return The number of canceled orders for the day.
	 */
	public int getCancelsOrders() {
		return cancelsOrders;
	}

	/**
	 * Sets the number of canceled orders for the day.
	 * 
	 * @param cancelsOrders The number of canceled orders for the day to set.
	 */
	public void setCancelsOrders(int cancelsOrders) {
		this.cancelsOrders = cancelsOrders;
	}

	/**
	 * Retrieves the number of orders that passed their scheduled time for the day.
	 * 
	 * @return The number of orders that passed their scheduled time for the day.
	 */
	public int getTimePassedOrders() {
		return timePassedOrders;
	}

	/**
	 * Sets the number of orders that passed their scheduled time for the day.
	 * 
	 * @param timePassedOrders The number of orders that passed their scheduled time
	 *                         for the day to set.
	 */
	public void setTimePassedOrders(int timePassedOrders) {
		this.timePassedOrders = timePassedOrders;
	}

	/**
	 * Retrieves the total number of orders for the day.
	 * 
	 * @return The total number of orders for the day.
	 */
	public int getTotalOrders() {
		return totalOrders;
	}

	/**
	 * Sets the total number of orders for the day.
	 * 
	 * @param totalOrders The total number of orders for the day to set.
	 */
	public void setTotalOrders(int totalOrders) {
		this.totalOrders = totalOrders;
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

	/**
	 * Builds a report as a BLOB
	 */
	public void BuildReportAsBlob() {
	}

}
