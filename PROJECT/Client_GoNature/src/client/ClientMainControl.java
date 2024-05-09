package client;

import java.io.IOException;

import logic.ClientRequestDataContainer;
import ocsf.ChatIF;

/**
 * The ClientMainControl class represents the main control logic for the client
 * application. It serves as an intermediary between the user interface and the
 * client communication.
 */
public class ClientMainControl implements ChatIF {

	/**
	 * The client communication instance for handling communication with the server.
	 */
	private ClientCommunication client;

	/**
	 * Constructs an instance of ClientMainControl.
	 *
	 * @param host The host to connect to.
	 * @param port The port to connect on.
	 */
	public ClientMainControl(String host, int port) {

		try {
			client = new ClientCommunication(host, port, this);
		} catch (IOException exception) {
			System.out.println("Error: Can't setup connection!" + " Terminating client.");
		}
	}

	/**
	 * Displays a message to the console.
	 *
	 * @param message The message to display.
	 */
	@Override
	public void display(String message) {
		System.out.println("> " + message);
	}

	/**
	 * Sends a request message to the server through the client communication.
	 *
	 * @param message The request message to send.
	 */
	public void accept(ClientRequestDataContainer message) {
		client.handleMessageFromClientUI(message);
	}

	/**
	 * Retrieves the client communication instance.
	 *
	 * @return The client communication instance.
	 */
	public ClientCommunication getClient() {
		return client;
	}
}
