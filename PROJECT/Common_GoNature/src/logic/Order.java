package logic;

import java.io.Serializable;
import java.time.LocalDateTime;

import utils.CurrentDateAndTime;
import utils.enums.OrderStatusEnum;
import utils.enums.OrderTypeEnum;
import utils.enums.ParkNameEnum;
import utils.enums.UserTypeEnum;

/**
 * Represents an order for a visit to a park. Serializable to allow for object
 * serialization.
 */
public class Order implements Serializable {

	/** Unique identifier for serialization. */
	private static final long serialVersionUID = -4399591568409663001L;

	/** Unique identifier for the order. */
	private int orderId;

	/** Type of the order. */
	private OrderTypeEnum orderType;

	/** Name of the park associated with the order. */
	private ParkNameEnum parkName;

	/** First name of the person making the order. */
	private String firstName;

	/** Last name of the person making the order. */
	private String lastName;

	/** User ID associated with the order. */
	private String userId;

	/** Type of the user placing the order. */
	private UserTypeEnum ownerType;

	/** Telephone number of the person making the order. */
	private String telephoneNumber;

	/** Email address of the person making the order. */
	private String email;

	/** Date and time of entering the park. */
	private LocalDateTime enterDate;

	/** Date and time of exiting the park. */
	private LocalDateTime exitDate;

	/** Number of visitors included in the order. */
	private int numberOfVisitors;

	/** Date of creation of the order. */
	private String creationDate;

	/** Current status of the order. */
	private OrderStatusEnum status;

	/** Date and time when the status of the order was last updated. */
	private String lastStatusUpdatedTime;

	/** Date and time when the order was confirmed. */
	private String confirmationTime;

	/** Flag indicating if the order has been paid for. */
	private boolean paid = false;

	/** Price of the order. */
	private double price;

	/** Time of visit to the park. */
	private String timeOfVisit;

	/**
	 * Default constructor.
	 */
	public Order() {
	}

	/**
	 * Constructor with orderId.
	 * 
	 * @param orderId The unique identifier for the order.
	 */
	public Order(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * Constructor with orderId as String.
	 * 
	 * @param orderId The unique identifier for the order as a String.
	 */
	public Order(String orderId) {
		Integer id = Integer.parseInt(orderId);
		this.orderId = id;
	}

	/**
	 * Constructor with essential order details.
	 * 
	 * @param orderId          The unique identifier for the order.
	 * @param orderType        The type of the order.
	 * @param parkName         The name of the park.
	 * @param userId           The user ID associated with the order.
	 * @param userType         The type of user.
	 * @param telephoneNumber  The telephone number of the user.
	 * @param email            The email address of the user.
	 * @param timeOfVisit      The time of visit.
	 * @param numberOfVisitors The number of visitors for the order.
	 * @param creationDate     The creation date of the order.
	 */
	public Order(int orderId, OrderTypeEnum orderType, ParkNameEnum parkName, String userId, UserTypeEnum userType,
			String telephoneNumber, String email, String timeOfVisit, int numberOfVisitors, String creationDate) {
		this.orderId = orderId;
		this.orderType = orderType;
		this.parkName = parkName;
		this.userId = userId;
		this.telephoneNumber = telephoneNumber;
		this.email = email;
		this.timeOfVisit = timeOfVisit;
		this.numberOfVisitors = numberOfVisitors;
		this.creationDate = creationDate;
		status = OrderStatusEnum.Wait_Notify;
		lastStatusUpdatedTime = CurrentDateAndTime.getCurrentDateAndTime("yyyy-MM-dd hh:mm");
	}

	/**
	 * Constructor with detailed order information.
	 * 
	 * @param orderId          The unique identifier for the order.
	 * @param parkName         The name of the park.
	 * @param enterDate        The enter date.
	 * @param exitDate         The exit date.
	 * @param status           The status of the order.
	 * @param email            The email address of the user.
	 * @param telephoneNumber  The telephone number of the user.
	 * @param firstName        The first name of the user.
	 * @param lastName         The last name of the user.
	 * @param orderType        The type of the order.
	 * @param numberOfVisitors The number of visitors for the order.
	 * @param price            The price of the order.
	 */
	public Order(int orderId, ParkNameEnum parkName, LocalDateTime enterDate, LocalDateTime exitDate,
			OrderStatusEnum status, String email, String telephoneNumber, String firstName, String lastName,
			OrderTypeEnum orderType, int numberOfVisitors, double price) {
		this.orderId = orderId;
		this.orderType = orderType;
		this.parkName = parkName;
		this.firstName = firstName;
		this.lastName = lastName;
		this.telephoneNumber = telephoneNumber;
		this.email = email;
		this.enterDate = enterDate;
		this.exitDate = exitDate;
		this.numberOfVisitors = numberOfVisitors;
		this.status = status;
		this.price = price;
	}

	/**
	 * Constructs an Order object with detailed information.
	 * 
	 * @param orderId          The unique identifier for the order.
	 * @param parkName         The name of the park associated with the order.
	 * @param userId           The user ID associated with the order.
	 * @param ownerType        The type of user placing the order.
	 * @param enterDate        The date and time when the visitor enters the park.
	 * @param exitDate         The date and time when the visitor exits the park.
	 * @param paid             An integer indicating whether the order has been paid
	 *                         for (1 for true, 0 for false). If paid is 1, it
	 *                         indicates the order has been paid for; otherwise,
	 *                         false.
	 * @param status           The current status of the order.
	 * @param email            The email address of the person making the order.
	 * @param telephoneNumber  The telephone number of the person making the order.
	 * @param firstName        The first name of the person making the order.
	 * @param lastName         The last name of the person making the order.
	 * @param orderType        The type of the order.
	 * @param numberOfVisitors The number of visitors included in the order.
	 * @param price            The price of the order.
	 */
	public Order(int orderId, ParkNameEnum parkName, String userId, UserTypeEnum ownerType, LocalDateTime enterDate,
			LocalDateTime exitDate, int paid, OrderStatusEnum status, String email, String telephoneNumber,
			String firstName, String lastName, OrderTypeEnum orderType, int numberOfVisitors, double price) {
		this.orderId = orderId;
		this.parkName = parkName;
		this.userId = userId;
		this.ownerType = ownerType;
		this.enterDate = enterDate;
		this.exitDate = exitDate;
		this.paid = paid == 1 ? true : false;
		this.status = status;
		this.email = email;
		this.telephoneNumber = telephoneNumber;
		this.firstName = firstName;
		this.lastName = lastName;
		this.orderType = orderType;
		this.numberOfVisitors = numberOfVisitors;
		this.price = price;
	}

	/**
	 * Retrieves the unique identifier of the order.
	 * 
	 * @return The order ID.
	 */
	public int getOrderId() {
		return orderId;
	}

	/**
	 * Sets the unique identifier of the order.
	 * 
	 * @param orderId The order ID to set.
	 */
	public void setOrderId(int orderId) {
		this.orderId = orderId;
	}

	/**
	 * Retrieves the price of the order.
	 * 
	 * @return The price of the order.
	 */
	public double getPrice() {
		return price;
	}

	/**
	 * Sets the price of the order.
	 * 
	 * @param price The price to set.
	 */
	public void setPrice(double price) {
		this.price = price;
	}

	/**
	 * Retrieves the name of the park associated with the order.
	 * 
	 * @return The park name enum.
	 */
	public ParkNameEnum getParkName() {
		return parkName;
	}

	/**
	 * Sets the name of the park associated with the order.
	 * 
	 * @param parkName The park name enum to set.
	 */
	public void setParkName(ParkNameEnum parkName) {
		this.parkName = parkName;
	}

	/**
	 * Retrieves the user ID associated with the order.
	 * 
	 * @return The user ID.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user ID associated with the order.
	 * 
	 * @param userId The user ID to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Retrieves the telephone number associated with the order.
	 * 
	 * @return The telephone number.
	 */
	public String getTelephoneNumber() {
		return telephoneNumber;
	}

	/**
	 * Sets the telephone number associated with the order.
	 * 
	 * @param telephoneNumber The telephone number to set.
	 */
	public void setTelephoneNumber(String telephoneNumber) {
		this.telephoneNumber = telephoneNumber;
	}

	/**
	 * Retrieves the email address associated with the order.
	 * 
	 * @return The email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Sets the email address associated with the order.
	 * 
	 * @param email The email address to set.
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Retrieves the time of visit associated with the order.
	 * 
	 * @return The time of visit.
	 */
	public String getTimeOfVisit() {
		return timeOfVisit;
	}

	/**
	 * Sets the time of visit associated with the order.
	 * 
	 * @param timeOfVisit The time of visit to set.
	 */
	public void setTimeOfVisit(String timeOfVisit) {
		this.timeOfVisit = timeOfVisit;
	}

	/**
	 * Retrieves the number of visitors associated with the order.
	 * 
	 * @return The number of visitors.
	 */
	public int getNumberOfVisitors() {
		return numberOfVisitors;
	}

	/**
	 * Sets the number of visitors associated with the order.
	 * 
	 * @param numberOfVisitors The number of visitors to set.
	 */
	public void setNumberOfVisitors(int numberOfVisitors) {
		this.numberOfVisitors = numberOfVisitors;
	}

	/**
	 * Retrieves the creation date of the order.
	 * 
	 * @return The creation date.
	 */
	public String getOrderDate() {
		return creationDate;
	}

	/**
	 * Sets the creation date of the order.
	 * 
	 * @param orderDate The creation date to set.
	 */
	public void setOrderDate(String orderDate) {
		this.creationDate = orderDate;
	}

	/**
	 * Retrieves the confirmation time of the order.
	 * 
	 * @return The confirmation time.
	 */
	public String getOrderConfirmationTime() {
		return confirmationTime;
	}

	/**
	 * Sets the confirmation time of the order.
	 * 
	 * @param confirmationTime The confirmation time to set.
	 */
	public void setOrderConfirmationTime(String confirmationTime) {
		this.confirmationTime = confirmationTime;
	}

	/**
	 * Retrieves the type of the order.
	 * 
	 * @return The order type enum.
	 */
	public OrderTypeEnum getOrderType() {
		return orderType;
	}

	/**
	 * Sets the type of the order.
	 * 
	 * @param orderType The order type enum to set.
	 */
	public void setOrderType(OrderTypeEnum orderType) {
		this.orderType = orderType;
	}

	/**
	 * Retrieves the status of the order.
	 * 
	 * @return The order status enum.
	 */
	public OrderStatusEnum getStatus() {
		return status;
	}

	/**
	 * Sets the status of the order.
	 * 
	 * @param status The order status enum to set.
	 */
	public void setStatus(OrderStatusEnum status) {
		this.status = status;
	}

	/**
	 * Retrieves the type of user associated with the order.
	 * 
	 * @return The user type enum.
	 */
	public UserTypeEnum getOwnerType() {
		return ownerType;
	}

	/**
	 * Sets the type of user associated with the order.
	 * 
	 * @param ownerType The user type enum to set.
	 */
	public void setOwnerType(UserTypeEnum ownerType) {
		this.ownerType = ownerType;
	}

	/**
	 * Retrieves the last status updated time of the order.
	 * 
	 * @return The last status updated time.
	 */
	public String getLastStatusUpdatedTime() {
		return lastStatusUpdatedTime;
	}

	/**
	 * Sets the last status updated time of the order.
	 * 
	 * @param lastStatusUpdatedTime The last status updated time to set.
	 */
	public void setLastStatusUpdatedTime(String lastStatusUpdatedTime) {
		this.lastStatusUpdatedTime = lastStatusUpdatedTime;
	}

	/**
	 * Indicates whether some other object is "equal to" this one. Two orders are
	 * considered equal if they have the same orderId.
	 * 
	 * @param obj The reference object with which to compare.
	 * @return {@code true} if this order is the same as the obj argument;
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		Order order = (Order) obj;
		return orderId == order.orderId;
	}

	/**
	 * Retrieves the date and time when the visitor enters the park.
	 * 
	 * @return The enter date and time.
	 */
	public LocalDateTime getEnterDate() {
		return enterDate;
	}

	/**
	 * Sets the date and time when the visitor enters the park.
	 * 
	 * @param enterDate The enter date and time to set.
	 */
	public void setEnterDate(LocalDateTime enterDate) {
		this.enterDate = enterDate;
	}

	/**
	 * Retrieves the date and time when the visitor exits the park.
	 * 
	 * @return The exit date and time.
	 */
	public LocalDateTime getExitDate() {
		return exitDate;
	}

	/**
	 * Sets the date and time when the visitor exits the park.
	 * 
	 * @param exitDate The exit date and time to set.
	 */
	public void setExitDate(LocalDateTime exitDate) {
		this.exitDate = exitDate;
	}

	/**
	 * Checks if the order has been paid for.
	 * 
	 * @return {@code true} if the order has been paid for; {@code false} otherwise.
	 */
	public boolean isPaid() {
		return paid;
	}

	/**
	 * Sets whether the order has been paid for.
	 * 
	 * @param paid The boolean value indicating if the order has been paid for.
	 */
	public void setPaid(boolean paid) {
		this.paid = paid;
	}

	/**
	 * Retrieves the first name of the person making the order.
	 * 
	 * @return The first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name of the person making the order.
	 * 
	 * @param firstName The first name to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Retrieves the last name of the person making the order.
	 * 
	 * @return The last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name of the person making the order.
	 * 
	 * @param lastName The last name to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
}