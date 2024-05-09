package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The OrderTypeEnum enum represents different types of orders.
 */
public enum OrderTypeEnum {
	/**
	 * Default value representing no specific order type.
	 */
	None("None"),
	/**
	 * Solo visit preordered.
	 */
	Solo_PreOrder("Solo Preorder"),
	/**
	 * Solo visit occasional.
	 */
	Solo_Occasional("Solo Occasional"),
	/**
	 * Family visit preordered.
	 */
	Family_PreOrder("Family Preorder"),
	/**
	 * Family visit occasional.
	 */
	Family_Occasional("Family Occasional"),
	/**
	 * Group visit preordered.
	 */
	Group_PreOrder("Group Preorder"),
	/**
	 * Group visit occasional.
	 */
	Group_Occasional("Group Occasional");

	private String name;
	// Map to store enum values by name
	private static final Map<String, OrderTypeEnum> enumMap = new HashMap<>();

	// Static block to initialize enumMap
	static {
		for (OrderTypeEnum orderTypeEnum : OrderTypeEnum.values()) {
			enumMap.put(orderTypeEnum.name, orderTypeEnum);
		}
	}

	/**
	 * Constructs a new OrderTypeEnum with the specified name.
	 * 
	 * @param name The name of the order type.
	 */
	private OrderTypeEnum(String name) {
		this.name = name;
	}

	/**
	 * Returns the string representation of the order type.
	 * 
	 * @return The string representation of the order type.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the OrderTypeEnum associated with the specified name.
	 * 
	 * @param name The name of the order type.
	 * @return The OrderTypeEnum associated with the specified name, or {@code null}
	 *         if not found.
	 */
	public static OrderTypeEnum fromString(String name) {
		return enumMap.get(name);
	}
}
