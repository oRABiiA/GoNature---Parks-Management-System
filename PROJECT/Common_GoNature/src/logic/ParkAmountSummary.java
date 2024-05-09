
package logic;

import java.io.Serializable;

import utils.enums.ParkNameEnum;

/**
 * Represents a summary of park visit amounts.
 */
public class ParkAmountSummary implements Serializable {

	/** Serial version UID for serialization. */
	private static final long serialVersionUID = -8702175099372264200L;

	/** The month of the summary. */
	private int month;

	/** The year of the summary. */
	private int year;

	/** The amount of solo visitors. */
	private int amountSolo;

	/** The amount of family visitors. */
	private int amountFamily;

	/** The amount of group visitors. */
	private int amountGroup;

	/** The park associated with the summary. */
	private ParkNameEnum park;

	/**
	 * Default constructor for ParkAmountSummary.
	 */
	public ParkAmountSummary() {
	}

	/**
	 * Constructs a ParkAmountSummary with the given parameters.
	 * 
	 * @param month        The month of the summary.
	 * @param year         The year of the summary.
	 * @param amountSolo   The amount of solo visitors.
	 * @param amountFamily The amount of family visitors.
	 * @param amountGroup  The amount of group visitors.
	 * @param park         The park associated with the summary.
	 */
	public ParkAmountSummary(int month, int year, int amountSolo, int amountFamily, int amountGroup,
			ParkNameEnum park) {
		this.month = month;
		this.year = year;
		this.amountSolo = amountSolo;
		this.amountFamily = amountFamily;
		this.amountGroup = amountGroup;
		this.park = park;
	}

	/**
	 * Retrieves the month of the summary.
	 * 
	 * @return The month of the summary.
	 */
	public int getMonth() {
		return month;
	}

	/**
	 * Sets the month of the summary.
	 * 
	 * @param month The month of the summary to set.
	 */
	public void setMonth(int month) {
		this.month = month;
	}

	/**
	 * Retrieves the amount of solo visitors in the summary.
	 * 
	 * @return The amount of solo visitors.
	 */
	public int getAmountSolo() {
		return amountSolo;
	}

	/**
	 * Sets the amount of solo visitors in the summary.
	 * 
	 * @param amountSolo The amount of solo visitors to set.
	 */
	public void setAmountSolo(int amountSolo) {
		this.amountSolo = amountSolo;
	}

	/**
	 * Retrieves the amount of family visitors in the summary.
	 * 
	 * @return The amount of family visitors.
	 */
	public int getAmountFamily() {
		return amountFamily;
	}

	/**
	 * Sets the amount of family visitors in the summary.
	 * 
	 * @param amountFamily The amount of family visitors to set.
	 */
	public void setAmountFamily(int amountFamily) {
		this.amountFamily = amountFamily;
	}

	/**
	 * Retrieves the amount of group visitors in the summary.
	 * 
	 * @return The amount of group visitors.
	 */
	public int getAmountGroup() {
		return amountGroup;
	}

	/**
	 * Sets the amount of group visitors in the summary.
	 * 
	 * @param amountGroup The amount of group visitors to set.
	 */
	public void setAmountGroup(int amountGroup) {
		this.amountGroup = amountGroup;
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
	 * @param park The park to set.
	 */
	public void setPark(ParkNameEnum park) {
		this.park = park;
	}

	/**
	 * Retrieves the year of the summary.
	 * 
	 * @return The year of the summary.
	 */
	public int getYear() {
		return year;
	}

	/**
	 * Sets the year of the summary.
	 * 
	 * @param year The year of the summary to set.
	 */
	public void setYear(int year) {
		this.year = year;
	}

}
