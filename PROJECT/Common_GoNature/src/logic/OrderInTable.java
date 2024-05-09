package logic;

import javafx.beans.property.SimpleStringProperty;

/**
 * Represents an order in a table format.
 */
public class OrderInTable {
	/** Property for the order ID. */
	private SimpleStringProperty orderId;

	/** Property for indicating whether the order is paid. */
	private SimpleStringProperty isPaid;

	/** Property for the amount of visitors in the order. */
	private SimpleStringProperty amountOfVisitors;

	/** Property for the owner's phone number. */
	private SimpleStringProperty ownerPhone;

	/** Property for the estimated enter time of the order. */
	private SimpleStringProperty estimatedEnterTime;

	/** Property for the estimated exit time of the order. */
	private SimpleStringProperty estimatedExitTime;

	/** Property for the status of the order. */
	private SimpleStringProperty status;

	/** Property for the type of order (occasional or preorder). */
	private SimpleStringProperty orderTable;

	/**
	 * Constructs an OrderInTable object with specified parameters.
	 * 
	 * @param orderId            The order ID.
	 * @param amountOfVisitors   The number of visitors in the order.
	 * @param isPaid             A boolean indicating whether the order is paid.
	 * @param ownerPhone         The owner's phone number.
	 * @param estimatedEnterTime The estimated enter time of the order.
	 * @param estimatedExitTime  The estimated exit time of the order.
	 * @param status             The status of the order.
	 */
	public OrderInTable(String orderId, String amountOfVisitors, boolean isPaid, String ownerPhone,
			String estimatedEnterTime, String estimatedExitTime, String status) {
		this.orderId = new SimpleStringProperty(orderId);
		this.amountOfVisitors = new SimpleStringProperty(amountOfVisitors);
		this.isPaid = ((isPaid) ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No"));
		this.ownerPhone = new SimpleStringProperty(ownerPhone);
		this.estimatedEnterTime = new SimpleStringProperty(estimatedEnterTime);
		this.estimatedExitTime = new SimpleStringProperty(estimatedExitTime);
		this.status = new SimpleStringProperty(status);
	}

	/**
	 * Constructs an OrderInTable object based on an existing Order object.
	 * 
	 * @param order The Order object to convert into an OrderInTable.
	 */
	public OrderInTable(Order order) {
		this.orderId = new SimpleStringProperty(String.valueOf(order.getOrderId()));
		this.amountOfVisitors = new SimpleStringProperty(String.valueOf(order.getNumberOfVisitors()));
		this.isPaid = (order.isPaid()) ? new SimpleStringProperty("Yes") : new SimpleStringProperty("No");
		this.ownerPhone = new SimpleStringProperty(order.getTelephoneNumber());
		this.estimatedEnterTime = new SimpleStringProperty(order.getEnterDate().toString());
		this.estimatedExitTime = new SimpleStringProperty(order.getExitDate().toString());
		this.status = new SimpleStringProperty(order.getStatus().toString());
		this.orderTable = new SimpleStringProperty(
				(order.getOrderType().toString().contains("Occasional")) ? "Occasional" : "Preorder");
	}

	/**
	 * Default constructor for OrderInTable.
	 */
	public OrderInTable() {
	}

	/**
	 * Retrieves the order ID.
	 * 
	 * @return The order ID.
	 */
	public String getOrderId() {
		return orderId.get();
	}

	/**
	 * Sets the order ID.
	 * 
	 * @param orderId The order ID to set.
	 */
	public void setOrderId(String orderId) {
		this.orderId.set(orderId);
	}

	/**
	 * Retrieves the number of visitors in the order.
	 * 
	 * @return The number of visitors.
	 */
	public String getAmountOfVisitors() {
		return amountOfVisitors.get();
	}

	/**
	 * Sets the number of visitors in the order.
	 * 
	 * @param amountOfVisitors The number of visitors to set.
	 */
	public void setAmountOfVisitors(String amountOfVisitors) {
		this.amountOfVisitors.set(amountOfVisitors);
	}

	/**
	 * Retrieves the owner's phone number.
	 * 
	 * @return The owner's phone number.
	 */
	public String getOwnerPhone() {
		return ownerPhone.get();
	}

	/**
	 * Sets the owner's phone number.
	 * 
	 * @param ownerPhone The owner's phone number to set.
	 */
	public void setOwnerPhone(String ownerPhone) {
		this.ownerPhone.set(ownerPhone);
	}

	/**
	 * Retrieves the estimated enter time of the order.
	 * 
	 * @return The estimated enter time.
	 */
	public String getEstimatedEnterTime() {
		return estimatedEnterTime.get();
	}

	/**
	 * Sets the estimated enter time of the order.
	 * 
	 * @param estimatedEnterTime The estimated enter time to set.
	 */
	public void setEstimatedEnterTime(String estimatedEnterTime) {
		this.estimatedEnterTime.set(estimatedEnterTime);
	}

	/**
	 * Retrieves the estimated exit time of the order.
	 * 
	 * @return The estimated exit time.
	 */
	public String getEstimatedExitTime() {
		return estimatedExitTime.get();
	}

	/**
	 * Sets the estimated exit time of the order.
	 * 
	 * @param estimatedExitTime The estimated exit time to set.
	 */
	public void setEstimatedExitTime(String estimatedExitTime) {
		this.estimatedExitTime.set(estimatedExitTime);
	}

	/**
	 * Indicates whether some other object is "equal to" this one.
	 * 
	 * @param obj The reference object with which to compare.
	 * @return {@code true} if this object is the same as the obj argument;
	 *         {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return this.getOrderId().equals(((OrderInTable) obj).getOrderId());
	}

	/**
	 * Retrieves the payment status of the order.
	 * 
	 * @return The payment status of the order.
	 */
	public String getIsPaid() {
		return isPaid.get();
	}

	/**
	 * Sets the payment status of the order.
	 * 
	 * @param isPaid The payment status to set.
	 */
	public void setIsPaid(String isPaid) {
		this.isPaid.set(isPaid);
	}

	/**
	 * Retrieves the status of the order.
	 * 
	 * @return The status of the order.
	 */
	public String getStatus() {
		return status.get();
	}

	/**
	 * Sets the status of the order.
	 * 
	 * @param status The status to set.
	 */
	public void setStatus(String status) {
		this.status.set(status);
	}

	/**
	 * Retrieves the type of order (occasional or preorder).
	 * 
	 * @return The type of order.
	 */
	public String getOrderTable() {
		return orderTable.get();
	}

	/**
	 * Sets the type of order (occasional or preorder).
	 * 
	 * @param orderTable The type of order to set.
	 */
	public void setOrderTable(String orderTable) {
		this.orderTable.set(orderTable);
	}

}