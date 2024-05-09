package utils.enums;

/**
 * The RequestStatusEnum enum represents different statuses for a request.
 */
public enum RequestStatusEnum {
	/**
	 * Represents an undefined status.
	 */
	None("None"),
	/**
	 * Represents a pending status.
	 */
	Pending("Pending"),
	/**
	 * Represents an approved status.
	 */
	Approved("Approved"),
	/**
	 * Represents a denied status.
	 */
	Denied("Denied");

	private final String status;

	/**
	 * Constructs a RequestStatusEnum with the specified status.
	 * 
	 * @param status The status associated with the enum constant.
	 */
	RequestStatusEnum(String status) {
		this.status = status;
	}

	/**
	 * Returns the status associated with the enum constant.
	 * 
	 * @return The status.
	 */
	public String getStatus() {
		return this.status;
	}

	/**
	 * Retrieves the RequestStatusEnum constant associated with the given status
	 * string.
	 * 
	 * @param status The status string.
	 * @return The RequestStatusEnum constant corresponding to the status string, or
	 *         None if not found.
	 */
	public static RequestStatusEnum fromString(String status) {
		for (RequestStatusEnum request : RequestStatusEnum.values()) {
			if (request.status.equalsIgnoreCase(status)) {
				return request;
			}
		}
		return None;
	}
}