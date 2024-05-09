package logic;

import java.io.Serializable;
import java.time.LocalDateTime;

import utils.enums.RequestTypeEnum;
import utils.enums.RequestStatusEnum;

/**
 * Represents a request made for a park.
 */
public class Request implements Serializable {
	/** Serial version UID for serialization. */
	private static final long serialVersionUID = 1609797016982160699L;

	/** The unique identifier for the request. */
	private int requestId;

	/** The identifier of the park associated with the request. */
	private int parkId;

	/** The type of the request. */
	private RequestTypeEnum requestType;

	/** The old value associated with the request. */
	private int oldValue;

	/** The new value associated with the request. */
	private int newValue;

	/** The status of the request. */
	private RequestStatusEnum requestStatus;

	/** The date and time when the request was made. */
	private LocalDateTime requestDate;

	/**
	 * Constructs a Request with the specified parameters.
	 *
	 * @param requestId     The unique identifier for the request.
	 * @param parkId        The identifier of the park associated with the request.
	 * @param requestType   The type of the request.
	 * @param oldValue      The old value associated with the request.
	 * @param newValue      The new value associated with the request.
	 * @param requestStatus The status of the request.
	 * @param requestDate   The date and time when the request was made.
	 */
	public Request(int requestId, int parkId, RequestTypeEnum requestType, int oldValue, int newValue,
			RequestStatusEnum requestStatus, LocalDateTime requestDate) {
		this.requestId = requestId;
		this.parkId = parkId;
		this.requestType = requestType;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.requestStatus = requestStatus;
		this.requestDate = requestDate;
	}

	/**
	 * Default constructor for Request.
	 */
	public Request() {
	}

	/**
	 * Constructs a new Request object with specified parameters.
	 * 
	 * @param parkId      The identifier of the park associated with the request.
	 * @param requestType The type of the request.
	 * @param oldValue    The previous value related to the request.
	 * @param newValue    The new value related to the request.
	 * @param requestDate The date and time when the request was made.
	 */
	public Request(int parkId, RequestTypeEnum requestType, int oldValue, int newValue, LocalDateTime requestDate) {
		this.parkId = parkId;
		this.requestType = requestType;
		this.oldValue = oldValue;
		this.newValue = newValue;
		this.requestDate = requestDate;
		requestStatus = RequestStatusEnum.Pending;
	}

	/**
	 * Retrieves the unique identifier for the request.
	 * 
	 * @return The request identifier.
	 */
	public int getRequestId() {
		return requestId;
	}

	/**
	 * Sets the unique identifier for the request.
	 * 
	 * @param requestId The request identifier to set.
	 */
	public void setRequestId(int requestId) {
		this.requestId = requestId;
	}

	/**
	 * Retrieves the identifier of the park associated with the request.
	 * 
	 * @return The park identifier.
	 */
	public int getParkId() {
		return parkId;
	}

	/**
	 * Sets the identifier of the park associated with the request.
	 * 
	 * @param parkId The park identifier to set.
	 */
	public void setParkId(int parkId) {
		this.parkId = parkId;
	}

	/**
	 * Retrieves the type of the request.
	 * 
	 * @return The request type.
	 */
	public RequestTypeEnum getRequestType() {
		return requestType;
	}

	/**
	 * Sets the type of the request.
	 * 
	 * @param requestType The request type to set.
	 */
	public void setRequestType(RequestTypeEnum requestType) {
		this.requestType = requestType;
	}

	/**
	 * Retrieves the previous value related to the request.
	 * 
	 * @return The previous value.
	 */
	public int getOldValue() {
		return oldValue;
	}

	/**
	 * Sets the previous value related to the request.
	 * 
	 * @param oldValue The previous value to set.
	 */
	public void setOldValue(int oldValue) {
		this.oldValue = oldValue;
	}

	/**
	 * Retrieves the new value related to the request.
	 * 
	 * @return The new value.
	 */
	public int getNewValue() {
		return newValue;
	}

	/**
	 * Sets the new value related to the request.
	 * 
	 * @param newValue The new value to set.
	 */
	public void setNewValue(int newValue) {
		this.newValue = newValue;
	}

	/**
	 * Retrieves the status of the request.
	 * 
	 * @return The request status.
	 */
	public RequestStatusEnum getRequestStatus() {
		return requestStatus;
	}

	/**
	 * Sets the status of the request.
	 * 
	 * @param requestStatus The request status to set.
	 */
	public void setRequestStatus(RequestStatusEnum requestStatus) {
		this.requestStatus = requestStatus;
	}

	/**
	 * Retrieves the date and time when the request was made.
	 * 
	 * @return The request date and time.
	 */
	public LocalDateTime getRequestDate() {
		return requestDate;
	}

	/**
	 * Sets the date and time when the request was made.
	 * 
	 * @param requestDate The request date and time to set.
	 */
	public void setRequestDate(LocalDateTime requestDate) {
		this.requestDate = requestDate;
	}

}