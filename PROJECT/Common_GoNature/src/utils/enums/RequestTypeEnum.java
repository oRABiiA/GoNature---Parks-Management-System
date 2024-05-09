package utils.enums;

/**
 * The RequestTypeEnum enum represents different types of requests.
 */
public enum RequestTypeEnum {
	/**
	 * Represents a request type of None, indicating no specific request type.
	 */
	None("None"),
	/**
	 * Represents a request to reserve spots.
	 */
	ReservedSpots("ReservedSpots"),
	/**
	 * Represents a request concerning maximum capacity.
	 */
	MaxCapacity("MaxCapacity"),
	/**
	 * Represents a request to adjust the estimated visit time.
	 */
	EstimatedVisitTime("EstimatedVisitTime");

	private final String value;

	/**
	 * Constructs a RequestTypeEnum with a specific string value.
	 * 
	 * @param value The string value associated with the enum constant.
	 */
	RequestTypeEnum(String value) {
		this.value = value;
	}

	/**
	 * Returns the string value associated with the enum constant.
	 * 
	 * @return The string value of the enum constant.
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Retrieves the RequestTypeEnum constant associated with the given text string.
	 * 
	 * @param text The text string to match with the enum's value.
	 * @return The RequestTypeEnum constant that matches the given text, or None if
	 *         no match is found.
	 */
	public static RequestTypeEnum fromString(String text) {
		for (RequestTypeEnum request : RequestTypeEnum.values()) {
			if (request.value.equalsIgnoreCase(text)) {
				return request;
			}
		}
		return None;
	}
}
