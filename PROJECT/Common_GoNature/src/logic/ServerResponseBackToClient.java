package logic;

import java.io.Serializable;

import utils.enums.ServerResponse;

/**
 * Represents a response sent from the server back to the client, containing a
 * server response object and a message.
 */
public class ServerResponseBackToClient implements Serializable {
	/** The serial version UID for serialization. */
	private static final long serialVersionUID = -4087076871424183043L;

	/** The server response object. */
	private ServerResponse response;

	/** The message associated with the response. */
	private Object message;

	/**
	 * Constructs a new ServerResponseBackToClient object with the given server
	 * response and message.
	 * 
	 * @param response The server response object.
	 * @param message  The message associated with the response.
	 */
	public ServerResponseBackToClient(ServerResponse response, Object message) {
		this.response = response;
		this.message = message;
	}

	/**
	 * Retrieves the server response object.
	 * 
	 * @return The server response object.
	 */
	public ServerResponse getRensponse() {
		return response;
	}

	/**
	 * Sets the server response object.
	 * 
	 * @param response The server response object to be set.
	 */
	public void setRensponse(ServerResponse response) {
		this.response = response;
	}

	/**
	 * Retrieves the message associated with the response.
	 * 
	 * @return The message associated with the response.
	 */
	public Object getMessage() {
		return message;
	}

	/**
	 * Sets the message associated with the response.
	 * 
	 * @param message The message to be set.
	 */
	public void setMessage(Object message) {
		this.message = message;
	}

}
