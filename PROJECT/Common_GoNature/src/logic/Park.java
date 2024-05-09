package logic;

import java.io.Serializable;

import utils.enums.ParkNameEnum;

/**
 * Represents a park entity.
 */
public class Park implements Serializable {

	/** Serial version UID for serialization. */
	private static final long serialVersionUID = -6759762004224132621L;

	/** The unique identifier of the park. */
	private int parkId;

	/** The name of the park. */
	private ParkNameEnum parkName;

	/** The current maximum capacity of the park. */
	private int currentMaxCapacity;

	/** The current number of visitors in the park. */
	private int currentInPark;

	/** The current estimated stay time for visitors in the park. */
	private int currentEstimatedStayTime;

	/** The current estimated reserved spots in the park. */
	private int currentEstimatedReservedSpots;

	/** The entrance fee of the park. */
	private double parkEntranceFee;

	/** The district where the park is located. */
	private String district;

	/** The price of the park. */
	private int price;

	/**
	 * Constructs a park with the given ID.
	 * 
	 * @param parkId The ID of the park.
	 */
	public Park(int parkId) {
		this.parkId = parkId;
	}

	/**
	 * Constructs a park with the given parameters.
	 * 
	 * @param parkId                        The ID of the park.
	 * @param parkName                      The name of the park.
	 * @param currentMaxCapacity            The current maximum capacity of the
	 *                                      park.
	 * @param currentInPark                 The current number of visitors in the
	 *                                      park.
	 * @param currentEstimatedStayTime      The current estimated stay time for
	 *                                      visitors in the park.
	 * @param currentEstimatedReservedSpots The current estimated reserved spots in
	 *                                      the park.
	 */
	public Park(int parkId, ParkNameEnum parkName, int currentMaxCapacity, int currentInPark,
			int currentEstimatedStayTime, int currentEstimatedReservedSpots) {
		this.parkId = parkId;
		this.parkName = parkName;
		this.currentMaxCapacity = currentMaxCapacity;
		this.currentInPark = currentInPark;
		this.currentEstimatedStayTime = currentEstimatedStayTime;
		this.currentEstimatedReservedSpots = currentEstimatedReservedSpots;
	}

	/**
	 * Constructs a park with the given ID and name.
	 * 
	 * @param parkId   The ID of the park.
	 * @param parkName The name of the park.
	 */
	public Park(int parkId, ParkNameEnum parkName) {
		this.parkId = parkId;
		this.parkName = parkName;
	}

	/**
	 * Retrieves the current number of visitors in the park.
	 * 
	 * @return The current number of visitors.
	 */
	public int getCurrentInPark() {
		return currentInPark;
	}

	/**
	 * Sets the current number of visitors in the park.
	 * 
	 * @param currentInPark The current number of visitors to set.
	 */
	public void setCurrentInPark(int currentInPark) {
		this.currentInPark = currentInPark;
	}

	/**
	 * Retrieves the ID of the park.
	 * 
	 * @return The ID of the park.
	 */
	public int getParkId() {
		return parkId;
	}

	/**
	 * Sets the ID of the park.
	 * 
	 * @param parkId The ID of the park to set.
	 */
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	/**
	 * Retrieves the price of the park.
	 * 
	 * @return The price of the park.
	 */
	public int getPrice() {
		return price;
	}

	/**
	 * Sets the price of the park.
	 * 
	 * @param price The price to set for the park.
	 */
	public void setPrice(int price) {
		this.price = price;
	}

	/**
	 * Retrieves the name of the park.
	 * 
	 * @return The name of the park.
	 */
	public ParkNameEnum getParkName() {
		return parkName;
	}

	/**
	 * Sets the name of the park.
	 * 
	 * @param parkName The name of the park to set.
	 */
	public void setParkName(ParkNameEnum parkName) {
		this.parkName = parkName;
	}

	/**
	 * Retrieves the current maximum capacity of the park.
	 * 
	 * @return The current maximum capacity of the park.
	 */
	public int getCurrentMaxCapacity() {
		return currentMaxCapacity;
	}

	/**
	 * Sets the current maximum capacity of the park.
	 * 
	 * @param currentMaxCapacity The current maximum capacity to set for the park.
	 */
	public void setCurrentMaxCapacity(int currentMaxCapacity) {
		this.currentMaxCapacity = currentMaxCapacity;
	}

	/**
	 * Retrieves the current estimated stay time for visitors in the park.
	 * 
	 * @return The current estimated stay time for visitors in the park.
	 */
	public int getCurrentEstimatedStayTime() {
		return currentEstimatedStayTime;
	}

	/**
	 * Sets the current estimated stay time for visitors in the park.
	 * 
	 * @param currentEstimatedStayTime The current estimated stay time to set for
	 *                                 visitors in the park.
	 */
	public void setCurrentEstimatedStayTime(int currentEstimatedStayTime) {
		this.currentEstimatedStayTime = currentEstimatedStayTime;
	}

	/**
	 * Retrieves the current estimated reserved spots in the park.
	 * 
	 * @return The current estimated reserved spots in the park.
	 */
	public int getCurrentEstimatedReservedSpots() {
		return currentEstimatedReservedSpots;
	}

	/**
	 * Sets the current estimated reserved spots in the park.
	 * 
	 * @param currentEstimatedReservedSpots The current estimated reserved spots to
	 *                                      set in the park.
	 */
	public void setCurrentEstimatedReservedSpots(int currentEstimatedReservedSpots) {
		this.currentEstimatedReservedSpots = currentEstimatedReservedSpots;
	}

	/**
	 * Retrieves the entrance fee of the park.
	 * 
	 * @return The entrance fee of the park.
	 */
	public double getParkEntranceFee() {
		return parkEntranceFee;
	}

	/**
	 * Sets the entrance fee of the park.
	 * 
	 * @param parkEntranceFee The entrance fee to set for the park.
	 */
	public void setParkEntranceFee(double parkEntranceFee) {
		this.parkEntranceFee = parkEntranceFee;
	}

	/**
	 * Retrieves the district where the park is located.
	 * 
	 * @return The district where the park is located.
	 */
	public String getDistrict() {
		return district;
	}

	/**
	 * Sets the district where the park is located.
	 * 
	 * @param district The district to set where the park is located.
	 */
	public void setDistrict(String district) {
		this.district = district;
	}

}