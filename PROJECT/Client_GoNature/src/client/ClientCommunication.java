package client;

import java.io.IOException;

import javafx.application.Platform;
import logic.ClientRequestDataContainer;
import logic.ServerResponseBackToClient;
import ocsf.AbstractClient;
import ocsf.ChatIF;
import utils.enums.ServerResponse;

/**
 * The ClientCommunication class represents the client-side communication logic.
 * It extends the AbstractClient class and handles communication between the
 * client and the server.
 */
public class ClientCommunication extends AbstractClient {
	// Instance variables **********************************************

	/**
	 * The interface type variable. It allows the implementation of the display
	 * method in the client.
	 */

	/** The user interface object for displaying messages. */
	ChatIF clientUI;
//	private Object ServerResponseHandler;

	/** Flag indicating whether a response from the server is awaited. */
	public static boolean awaitResponse = false;
	/** The response received from the server. */
	public static ServerResponseBackToClient responseFromServer;

	/**
	 * Constructs a new instance of ClientCommunication.
	 * 
	 * @param host     The server's host name.
	 * @param port     The port number.
	 * @param clientUI The user interface object.
	 * @throws IOException if an I/O error occurs when opening the connection.
	 */
	public ClientCommunication(String host, int port, ChatIF clientUI) throws IOException {
		super(host, port); // Call the superclass constructor
		this.clientUI = clientUI;
		openConnection();
	}

	/**
	 * Handles the message received from the server.
	 *
	 * @param msg The message from the server.
	 */
	public void handleMessageFromServer(Object msg) {
		awaitResponse = false;
		responseFromServer = (ServerResponseBackToClient) msg;
		if (responseFromServer.getRensponse() == ServerResponse.Server_Disconnected)
			Platform.runLater(() -> ClientApplication.runningController.onServerCrashed());

	}

	/**
	 * Sends a message from the client to the server and awaits response.
	 *
	 * @param message The message to send.
	 */
	public void handleMessageFromClientUI(ClientRequestDataContainer message) {
		try {
			openConnection();// in order to send more than one message
			awaitResponse = true;

			sendToServer(message);
			// wait for response
			while (awaitResponse) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			clientUI.display("Could not send message to server: Terminating client." + e);
			quit();
		}
	}

	/**
	 * Closes the connection and terminates the client.
	 */
	public void quit() {
		try {
			this.closeConnection();
		} catch (IOException e) {
		}
		System.exit(0);
	}
}
