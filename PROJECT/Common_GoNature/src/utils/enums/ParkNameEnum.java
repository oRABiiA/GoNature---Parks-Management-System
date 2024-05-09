package utils.enums;

/**
 * The ParkNameEnum enum represents different park names along with their
 * corresponding IDs.
 */
public enum ParkNameEnum {

	/**
	 * Default value representing no specific park.
	 */
	None(0),
	/**
	 * Represents the Banias park with ID 1.
	 */
	Banias(1),
	/**
	 * Represents the Masada park with ID 2.
	 */
	Masada(2),
	/**
	 * Represents the Herodium park with ID 3.
	 */
	Herodium(3),

	/**
	 * Represents the North District
	 */
	North(4),

	/**
	 * Represents the South District
	 */
	South(5);

	private final Integer parkId;

	/**
	 * Constructs a new ParkNameEnum with the specified park ID.
	 * 
	 * @param parkId The ID of the park.
	 */
	ParkNameEnum(Integer parkId) {
		this.parkId = parkId;
	}

	/**
	 * Returns the ID of the park.
	 * 
	 * @return The ID of the park.
	 */
	public Integer getParkId() {
		return this.parkId;
	}

	/**
	 * Returns the ParkNameEnum associated with the specified park ID.
	 * 
	 * @param parkId The ID of the park.
	 * @return The ParkNameEnum associated with the specified park ID, or
	 *         ParkNameEnum.None if not found.
	 */
	public static ParkNameEnum fromParkId(Integer parkId) {
		for (ParkNameEnum parkName : ParkNameEnum.values()) {
			if ((parkId == null && parkName.getParkId() == null)
					|| (parkId != null && parkId.equals(parkName.getParkId()))) {
				return parkName;
			}
		}
		return None;
	}
}
