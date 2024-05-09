package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The OrderStatusEnum enum represents different statuses of an order.
 */
public enum OrderStatusEnum {
	/**
	 * Order created and waiting for notification to be sent 24 hours before
	 * arrival.
	 */
	Wait_Notify("Wait Notify"),
	/**
	 * Notification sent 24 hours before arrival/leave waiting list, client should
	 * confirm.
	 */
	Notified("Notified"),
	/**
	 * Order confirmed.
	 */
	Confirmed("Confirmed"),
	/**
	 * Notification sent while in waiting list.
	 */
	Notified_Waiting_List("Notified Waiting List"),
	/**
	 * Occasional order created by park employee.
	 */
	Occasional_Order("Occasional Order"),
	/**
	 * Order cancelled.
	 */
	Cancelled("Cancelled"),
	/**
	 * Order in waiting list.
	 */
	In_Waiting_List("In Waiting List"),
	/**
	 * Visitors entered park, and order has been completed.
	 */
	In_Park("In Park"),
	/**
	 * Visitors left park.
	 */
	Completed("Completed"),
	/**
	 * Time has passed and visitors did not come to the order or cancelled it.
	 */
	Time_Passed("Time Passed"),
	/**
	 * Irrelevant status for orders in waiting list which did not get applied.
	 */
	Irrelevant("Irrelevant");

	private String name;
	// Map to store enum values by name
	private static final Map<String, OrderStatusEnum> enumMap = new HashMap<>();

	// Static block to initialize enumMap
	static {
		for (OrderStatusEnum orderStatusEnum : OrderStatusEnum.values()) {
			enumMap.put(orderStatusEnum.name, orderStatusEnum);
		}
	}

	/**
	 * Constructs a new OrderStatusEnum with the specified name.
	 * 
	 * @param name The name of the order status.
	 */
	private OrderStatusEnum(String name) {
		this.name = name;
	}

	/**
	 * Returns the string representation of the order status.
	 * 
	 * @return The string representation of the order status.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the OrderStatusEnum associated with the specified name.
	 * 
	 * @param name The name of the order status.
	 * @return The OrderStatusEnum associated with the specified name, or
	 *         {@code null} if not found.
	 */
	public static OrderStatusEnum fromString(String name) {
		return enumMap.get(name);
	}
}
