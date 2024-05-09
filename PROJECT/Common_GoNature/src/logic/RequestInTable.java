package logic;

import java.time.LocalDateTime;

import javafx.beans.property.SimpleStringProperty;
import utils.enums.ParkNameEnum;
import utils.enums.RequestStatusEnum;
import utils.enums.RequestTypeEnum;

/**
 * Represents a request in a format suitable for display in a table.
 */
public class RequestInTable {
	/** The original request object. */
	private Request request;

	/** The ID of the request. */
	private final SimpleStringProperty requestId;

	/** The ID of the park associated with the request. */
	private final SimpleStringProperty parkId;

	/** The type of the request. */
	private final SimpleStringProperty requestType;

	/** The previous value related to the request. */
	private final SimpleStringProperty oldValue;

	/** The new value related to the request. */
	private final SimpleStringProperty newValue;

	/** The status of the request. */
	private final SimpleStringProperty requestStatus;

	/** The date and time when the request was made. */
	private final SimpleStringProperty requestDate;

	/**
	 * Constructs a new RequestInTable object with the provided details.
	 * 
	 * @param requestId     The ID of the request.
	 * @param parkId        The ID of the park associated with the request.
	 * @param requestType   The type of the request.
	 * @param oldValue      The previous value related to the request.
	 * @param newValue      The new value related to the request.
	 * @param requestStatus The status of the request.
	 * @param requestDate   The date and time when the request was made.
	 */
	public RequestInTable(int requestId, int parkId, RequestTypeEnum requestType, int oldValue, int newValue,
			RequestStatusEnum requestStatus, LocalDateTime requestDate) {
		this.requestId = new SimpleStringProperty(String.valueOf(requestId));
		this.parkId = new SimpleStringProperty(ParkNameEnum.fromParkId(parkId).name());
		this.requestType = new SimpleStringProperty(requestType.name());
		this.oldValue = new SimpleStringProperty(String.valueOf(oldValue));
		this.newValue = new SimpleStringProperty(String.valueOf(newValue));
		this.requestStatus = new SimpleStringProperty(requestStatus.name());
		this.requestDate = new SimpleStringProperty(requestDate.toString());
	}

	/**
	 * Retrieves the original request object.
	 * 
	 * @return The original request object.
	 */
	public Request getRequest() {
		return request;
	}

	/**
	 * Sets the original request object.
	 * 
	 * @param request The original request object to be set.
	 */
	public void setRequest(Request request) {
		this.request = request;
	}

	/**
	 * Retrieves the ID of the request.
	 * 
	 * @return The ID of the request.
	 */
	public String getRequestId() {
		return requestId.get();
	}

	/**
	 * Retrieves the ID of the park associated with the request.
	 * 
	 * @return The ID of the park associated with the request.
	 */
	public String getParkId() {
		return parkId.get();
	}

	/**
	 * Retrieves the type of the request.
	 * 
	 * @return The type of the request.
	 */
	public String getRequestType() {
		return requestType.get();
	}

	/**
	 * Retrieves the previous value related to the request.
	 * 
	 * @return The previous value related to the request.
	 */
	public String getOldValue() {
		return oldValue.get();
	}

	/**
	 * Retrieves the new value related to the request.
	 * 
	 * @return The new value related to the request.
	 */
	public String getNewValue() {
		return newValue.get();
	}

	/**
	 * Retrieves the status of the request.
	 * 
	 * @return The status of the request.
	 */
	public String getRequestStatus() {
		return requestStatus.get();
	}

	/**
	 * Retrieves the date and time when the request was made.
	 * 
	 * @return The date and time when the request was made.
	 */
	public String getRequestDate() {
		return requestDate.get();
	}

	/**
	 * Sets the status of the request.
	 * 
	 * @param status The status of the request to be set.
	 */
	public void setStatus(String status) {
		this.requestStatus.set(status);
	}

}
