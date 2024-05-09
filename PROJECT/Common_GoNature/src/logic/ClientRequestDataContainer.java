package logic;

import java.io.Serializable;

import utils.enums.ClientRequest;

/**
 * A container class for storing client request data. It encapsulates the
 * request details and the associated data in a serializable format for easy
 * transmission or storage. This class implements the {@link Serializable}
 * interface to allow its instances to be easily serialized and deserialized,
 * facilitating network transmission or file storage.
 */
public class ClientRequestDataContainer implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1429099659854753004L;

	/**
	 * The client request.
	 */
	private ClientRequest request;

	/**
	 * The data associated with the client request.
	 */
	private Object data;

	/**
	 * Constructs a new ClientRequestDataContainer with the specified client request
	 * and data.
	 * 
	 * @param request The client request
	 * @param data    The data associated with the client request
	 */
	public ClientRequestDataContainer(ClientRequest request, Object data) {
		this.request = request;
		this.data = data;
	}

	/**
	 * Default constructor for creating an empty ClientRequestDataContainer.
	 */
	public ClientRequestDataContainer() {
	}

	/**
	 * Sets the client request.
	 * 
	 * @param request The client request to set
	 */
	public void setRequest(ClientRequest request) {
		this.request = request;
	}

	/**
	 * Returns the client request.
	 * 
	 * @return The client request
	 */
	public ClientRequest getRequest() {
		return request;
	}

	/**
	 * Sets the data associated with the client request.
	 * 
	 * @param data The data to set
	 */
	public void setMessage(Object data) {
		this.data = data;
	}

	/**
	 * Returns the data associated with the client request.
	 * 
	 * @return The data associated with the client request
	 */
	public Object getData() {
		return data;
	}

}
