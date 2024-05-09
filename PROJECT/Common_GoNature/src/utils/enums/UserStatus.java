package utils.enums;

/**
 * The UserStatus enum represents the status of a user in the system.
 */
public enum UserStatus {
	/**
	 * Represents a user status of None, indicating that the user is not registered
	 * in the system.
	 */
	None("None"),
	/**
	 * Represents a user status of Pending, indicating that the user's registration
	 * is pending approval.
	 */
	Pending("Pending"),
	/**
	 * Represents a user status of Approved, indicating that the user's registration
	 * has been approved.
	 */
	Approved("Approved"),
	/**
	 * Represents a user status of Denied, indicating that the user's registration
	 * has been denied.
	 */
	Denied("Denied");

	private final String value;

	/**
	 * Constructs a UserStatus enum with the specified value.
	 *
	 * @param value The string representation of the user status.
	 */
	UserStatus(String value) {
		this.value = value;
	}

	/**
	 * Gets the string value of the user status.
	 *
	 * @return The string value of the user status.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Converts a string value to its corresponding UserStatus enum value.
	 *
	 * @param text The string value to convert.
	 * @return The UserStatus enum value corresponding to the given string value, or
	 *         UserStatus.None if no match is found.
	 */
	public static UserStatus fromString(String text) {
		for (UserStatus status : UserStatus.values()) {
			if (status.value.equalsIgnoreCase(text)) {
				return status;
			}
		}
		return None;
	}
}
