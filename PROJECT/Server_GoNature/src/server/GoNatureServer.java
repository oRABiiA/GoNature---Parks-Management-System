package server;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Enumeration;

import gui.controller.ServerScreenController;
import javafx.application.Platform;
import jdbc.DBConnectionDetails;
import jdbc.query.QueryControl;
import logic.ClientConnection;
import logic.ClientRequestDataContainer;
import logic.ClientRequestHandler;
import logic.Order;
import logic.ServerResponseBackToClient;
import logic.User;
import logic.Visitor;
import jdbc.MySqlConnection;
import ocsf.AbstractServer;
import ocsf.ConnectionToClient;
import utils.enums.ClientRequest;
import utils.enums.ServerResponse;

/**
 * 
 * @Author NadavReubens
 * @Author Gal Bitton
 * @Author Tamer Amer
 * @Author Rabea Lahham
 * @Author Bahaldeen Swied
 * @Author Ron Sisso
 * @version 1.0.0 
 */

/**
 * The GoNatureServer class is the main system's server class. This class
 * extends AbstractServer ocsf class, and manage all the Client-Server design.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */
public class GoNatureServer extends AbstractServer {

	// Use Singleton DesignPattern -> only 1 server may be running in our system.
	private static GoNatureServer server = null;
	private static ServerScreenController serverController;
	private ClientRequestHandler clientRequestHandler;
	private static Thread sendNotifications24HoursBefore = null;
	private static Thread cancelOrdersNotConfirmedWithin2Hours = null;
	private static Thread cancelTimePassedWaitingListOrders = null;

	/**
	 * Constructor
	 * 
	 * @param port             - The port number to connect on
	 * @param serverController - ServerGuiController to send information to the
	 *                         server gui view.
	 */
	@SuppressWarnings("static-access")
	private GoNatureServer(int port, ServerScreenController serverController) {
		super(port);
		this.serverController = serverController;
		clientRequestHandler = new ClientRequestHandler(this.serverController);
		initializeThreadsAndStartRun();
	}

	/**
	 * This method handle the message from client and sends them to the correct
	 * method according to the sent Object instance.
	 * 
	 * @param msg    - The Object instance the client sent to the server.
	 * @param client - The ConnectionToClient instance which include the details of
	 *               the client in order to be able send him back answer.
	 */
	@Override
	protected void handleMessageFromClient(Object msg, ConnectionToClient client) {
		// get the ip of the client who have sent the message.
		String clientIp = client.getInetAddress().getHostAddress();
		ClientRequestDataContainer data = (ClientRequestDataContainer) msg;
		ClientRequest request = data.getRequest();
		Platform.runLater((()->serverController.printToLogConsole(String.format("Request %s, was received from Client - %s",request,clientIp ))));
		ServerResponseBackToClient response = new ServerResponseBackToClient(ServerResponse.User_Logout_Successfully, null);
		if (request == ClientRequest.Logout) {
			handleUserLogoutFromApplication(data.getData(), client, clientIp);
		} else {
			response = clientRequestHandler.handleRequest(data, client);

			try {
				client.sendToClient(response);
				return;
			} catch (IOException e) {
				Platform.runLater(()->serverController.printToLogConsole(String.format("IOException occured in handleMessageFromClient: %s", e.getMessage())));
			}
		}
	}
	
	/**
	 * Handles the logout process for a user from the application. This method identifies the user type, constructs
	 * an appropriate user ID based on the instance type (Visitor or User), and logs the logout request and completion
	 * in the server's log console. It removes the client's connection from the server's list of active connections
	 * and sends a logout success response to the client. In case of an IOException during the communication process,
	 * it logs the error details.
	 *
	 * @param user The user object attempting to logout, which can be an instance of Visitor or User.
	 * @param client The connection to the client that is requesting the logout.
	 * @param clientIp The IP address of the client requesting the logout, used for logging purposes.
	 */
	private void handleUserLogoutFromApplication(Object user, ConnectionToClient client, String clientIp) {
		try {
			if (!(user == null)) {
				String id = "";
				if (user instanceof Visitor) {
					id = "Visitor" + ((Visitor) user).getCustomerId();

				} else if (user instanceof User) {
					id = ((User) user).getUsername();
				}
				serverController.printToLogConsole(
						String.format("User : '%s' with IP : '%s' : Request Logout from Application", id, clientIp));
				serverController.getClientsList().remove(new ClientConnection("", client));
				serverController.printToLogConsole(
						String.format("User : '%s' with IP : '%s' : Logged Out Successfully", id, clientIp));
			}
			client.sendToClient(new ServerResponseBackToClient(ServerResponse.User_Logout_Successfully, null));
		} catch (IOException ex) {
			Platform.runLater(()->serverController.printToLogConsole(String.format("IOException occured in handleUserLogoutFromApplication: %s", ex.getMessage())));
			serverController.printToLogConsole("Error while sending update message to client");
			return;
		}
	}

	/**
	 * This method handle a specific String message from client for now relevant
	 * only for message when client disconnecting the server.
	 * 
	 * @param client - The ConnectionToClient instance which include the details of
	 *               the client in order to be able send him back answer.
	 */
	@SuppressWarnings("unused")
	private void handleDisconnectMsgFromClient(ConnectionToClient client) {
		clientDisconnected(client);
		return;
	}

	/**
	 * This method write to log screen the server has been started.
	 */
	@Override
	protected void serverStarted() {
		serverController.printToLogConsole(
				String.format("Server listening for connnections on address %s:%s", getServerIpAddress(), getPort()));
	}

	/**
	 * The method enumerate through the network interface eliminate local host and
	 * virtual machines to return the right IP;
	 * 
	 * @return a String containing the correct IP
	 */
	private String getServerIpAddress() {
		String ip;
		try {
			@SuppressWarnings("rawtypes")
			Enumeration e1 = NetworkInterface.getNetworkInterfaces();
			while (e1.hasMoreElements()) {
				NetworkInterface network = (NetworkInterface) e1.nextElement();
				@SuppressWarnings("rawtypes")
				Enumeration e2 = network.getInetAddresses();
				while (e2.hasMoreElements()) {
					InetAddress inetAddress = (InetAddress) e2.nextElement();
					ip = inetAddress.getHostAddress();
					if (ip.contains(".") && !ip.equals("127.0.0.1")
							&& !network.getDisplayName().toLowerCase().contains("virtual")) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			Platform.runLater(()->serverController.printToLogConsole(String.format("SocketExcpetion occured in getServerIpAddress: %s",ex.getMessage())));
		}
		return "Not found network addresses. please use ipconfig in commandline";
	}

	/**
	 * This method write to log screen the server has been stopped.
	 */
	@Override
	protected void serverStopped() {
		Platform.runLater(() -> serverController.printToLogConsole("Server has stopped listening for connections\n"));
	}

	/**
	 * This method write to log screen the server has been stopped.
	 */
	@Override
	protected void serverClosed() {
		Platform.runLater(() -> serverController.printToLogConsole("Server has been closed\n"));
	}

	/**
	 * This method called when new client connect to the server. add the client to
	 * the server table view and write to log.
	 * 
	 * @param client - The ConnectionToClient instance which include the details of
	 *               the client in order to be able send him back answer.
	 */
	@Override
	protected void clientConnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		for (ClientConnection cl : serverController.getClientsList()) {
			if (cl.getConnection().equals(client)) {
				return;
			}
		}
		serverController.printToLogConsole(
				"Client " + details.getHostName() + " with IP:" + details.getHostAddress() + " Connected");
	}

	/**
	 * This method called when a client disconnected from the server. remove the
	 * client from the server's table view and write to log.
	 * 
	 * @param client - The ConnectionToClient instance which include the details of
	 *               the client in order to be able send him back answer.
	 */
	@Override
	synchronized protected void clientDisconnected(ConnectionToClient client) {
		InetAddress details = client.getInetAddress();
		serverController.printToLogConsole(
				"Client " + details.getHostName() + " with IP:" + details.getHostAddress() + " Disconnected");
		serverController.removeFromConnected(client);
	}

	/**
	 * This method stop the server to listen on a specific port and close the
	 * server.
	 */
	public static void stopServer() {
		// there is no server yet
		if (server == null)
			return;

		try {
			// first tell all the clients to disconnect.
			closeAllThreads();
			server.sendToAllClients(new ServerResponseBackToClient(ServerResponse.Server_Disconnected, ""));
			clearImportedData();
			server.stopListening();
			server.close();

		} catch (IOException ex) {
			Platform.runLater(()->serverController.printToLogConsole(String.format("IOException occured in stopServer: %s",ex.getMessage())));
		} finally {
			MySqlConnection.getInstance().closeConnection();
			server = null;
		}
	}
	
	/**
	 * Clears all imported data from the 'users' table in the database. This method establishes a connection to the
	 * database, executes a TRUNCATE TABLE SQL statement to remove all data from the 'users' table, and logs the
	 * operation's success. If a SQLException occurs during the operation, it logs the exception's message using
	 * the server controller's logging mechanism on the JavaFX Application thread.
	 */
	private static void clearImportedData() {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			Statement stmt = con.createStatement();
			stmt.execute("TRUNCATE TABLE users");
			System.out.println("Imported data cleared successfully");
		}catch(SQLException ex) {
			Platform.runLater(()->serverController.printToLogConsole(String.format("SQLException occured in clearImportedData: %s",ex.getMessage())));
		}
	}
	
	/**
	 * Imports user data from a CSV file into the 'users' table in the database. This method constructs a SQL query
	 * for loading data from a predefined CSV file path into the database, including setting specific fields, handling
	 * data enclosed in quotes, and processing line terminators. If the 'ParkId' field is empty, it is set to NULL.
	 * Upon successful execution, it logs a success message and returns {@code true}. If any exception occurs during
	 * the process, it prints the stack trace and returns {@code false}.
	 *
	 * @return {@code true} if the data import is successful, {@code false} otherwise.
	 */
	public static boolean importUsersData() {
		try {
			String desktopPath = System.getProperty("user.home") + File.separator + "Desktop";
			String csvFilePath = desktopPath + File.separator + "usersData.csv";
			String sqlCompatiblePath = csvFilePath.replace("\\", "\\\\");
			
			String sql = "LOAD DATA LOCAL INFILE '" + sqlCompatiblePath + "' INTO TABLE users "
			           + "FIELDS TERMINATED BY ',' ENCLOSED BY '\"' LINES TERMINATED BY '\n' "
			           + "(UserId, Username, Password, FirstName, LastName, Phone, Email, Status, UserType, @ParkId, EmployeeType) "
			           + "SET ParkId = NULLIF(@ParkId, '')";

			Connection conn = MySqlConnection.getInstance().getConnection();
			Statement stmt = conn.createStatement();

			stmt.execute(sql);
			System.out.println("Data imported successfully");
			return true;
		} catch (Exception e) {
			Platform.runLater(()->serverController.printToLogConsole(e.getMessage()));
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Gracefully shuts down all active background threads managed by the application. This method attempts to interrupt
	 * three specific threads - {@code sendNotifications24HoursBefore}, {@code cancelOrdersNotConfirmedWithin2Hours}, and
	 * {@code cancelTimePassedWaitingListOrders} - if they are currently alive. It then waits for each of these threads to
	 * terminate by calling the {@code join} method on them. If the current thread is interrupted while waiting for these
	 * threads to finish, it restores the interrupted status by calling {@code Thread.currentThread().interrupt()}.
	 * Note: The commented-out thread variables {@code sendNotifications24HoursBefore} and
	 * {@code cancelOrdersNotConfirmedWithin2Hours} should be declared and initialized elsewhere in the application for
	 * this method to function as intended.
	 */
	private static void closeAllThreads() {
//		private static Thread sendNotifications24HoursBefore = null;
//		private static Thread cancelOrdersNotConfirmedWithin2Hours = null;
		// Stop the searchAvailableDates thread if it's running
		try {
			if (sendNotifications24HoursBefore != null && sendNotifications24HoursBefore.isAlive())
				sendNotifications24HoursBefore.interrupt();

			if (cancelOrdersNotConfirmedWithin2Hours != null && cancelOrdersNotConfirmedWithin2Hours.isAlive())
				cancelOrdersNotConfirmedWithin2Hours.interrupt();

			if (cancelTimePassedWaitingListOrders != null && cancelTimePassedWaitingListOrders.isAlive())
				cancelTimePassedWaitingListOrders.interrupt();

			sendNotifications24HoursBefore.join(); // Wait for the thread to stop
			cancelOrdersNotConfirmedWithin2Hours.join();
			cancelTimePassedWaitingListOrders.join();
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt(); // Restore interrupted status
		}
	}

	/**
	 * This method start the server to listen on a specific port and connect to
	 * database via database controller.
	 * 
	 * @param db               - The DBConnectionDetails entity which contains all
	 *                         required data to connect the database.
	 * @param port             - The port number to connect on
	 * @param serverController - ServerGuiController to send information to the
	 *                         server gui view.
	 */
	public static void startServer(DBConnectionDetails db, Integer port, ServerScreenController serverController) {
		// try to connect the database
		MySqlConnection.setDBConnectionDetails(db);
		Connection conn = (Connection) MySqlConnection.getInstance(serverController).getConnection();
		// if failed -> can't start the server.
		if (conn == null) {
			serverController.printToLogConsole("Can't start server! Connection to database failed!");
			return;
		}

		clearImportedData();
		
		Platform.runLater(()->serverController.printToLogConsole("Connection to database succeed"));
		// Singleton DesignPattern. Only 1 instance of server is available.
		if (server != null) {
			Platform.runLater(()->serverController.printToLogConsole("There is already a connected server"));
			return;
		}

		server = new GoNatureServer(port, serverController);

		try {
			server.listen();
			// update connection in server gui.
			serverController.connectionSuccessfull();
			// Run the 2 relevant threads
			sendNotifications24HoursBefore.start();
			cancelOrdersNotConfirmedWithin2Hours.start();
			cancelTimePassedWaitingListOrders.start();
		} catch (Exception ex) {
			ex.printStackTrace();
			Platform.runLater(()->{
				serverController.printToLogConsole(String.format("Exception in start Server: %s",ex.getMessage()));
				serverController.printToLogConsole("Error - could not listen for clients!");
			});
			server = null;
		}
	}
	
	/**
	 * Initializes and starts the background threads responsible for sending notifications, cancelling unconfirmed orders,
	 * and managing waiting list orders based on specific time criteria. This method first ensures any previously running
	 * instances of these threads are interrupted and terminated before starting new ones. Each thread performs its
	 * designated task in a loop until interrupted:
	 * 
	 * 1. {@code sendNotifications24HoursBefore} sends notifications for orders 24 hours before their scheduled time.
	 * 2. {@code cancelOrdersNotConfirmedWithin2Hours} cancels orders not confirmed within 2 hours of notification.
	 * 3. {@code cancelTimePassedWaitingListOrders} manages waiting list orders, marking irrelevant ones based on the
	 * current time.
	 * 
	 * If any thread is interrupted during execution, it logs a message to the server console and breaks its execution loop.
	 */
	private void initializeThreadsAndStartRun() {
		if (sendNotifications24HoursBefore != null && sendNotifications24HoursBefore.isAlive()) {
			sendNotifications24HoursBefore.interrupt();
			try {
				sendNotifications24HoursBefore.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Platform.runLater(()->serverController.printToLogConsole("Thread for send notifications was interrupted"));
			}
		}

		if (cancelOrdersNotConfirmedWithin2Hours != null && cancelOrdersNotConfirmedWithin2Hours.isAlive()) {
			cancelOrdersNotConfirmedWithin2Hours.interrupt();
			try {
				cancelOrdersNotConfirmedWithin2Hours.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Platform.runLater(()->serverController.printToLogConsole("Thread for automatically cancelled unconfirmed orders was interrupted"));
			}
		}

		if (cancelTimePassedWaitingListOrders != null && cancelTimePassedWaitingListOrders.isAlive()) {
			cancelTimePassedWaitingListOrders.interrupt();
			try {
				cancelTimePassedWaitingListOrders.join();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				Platform.runLater(()->serverController.printToLogConsole("Thread for cancel time passed waiting list orders was interrupted"));
			}
		}

		sendNotifications24HoursBefore = new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(1000);
					LocalDateTime currentTime = LocalDateTime.now();
					LocalDateTime relevantTimeTomorrow = currentTime.plusDays(1);
					ArrayList<Order> ordersToNotify = QueryControl.notificationQueries
							.CheckAllOrdersAndChangeToNotifedfNeeded(relevantTimeTomorrow);

					if (ordersToNotify != null && !ordersToNotify.isEmpty()) {
						Platform.runLater(() -> serverController
								.printToLogConsole("Confirmation Notification was sent to all order 24 before time"));
						for (Order order : ordersToNotify) {
							QueryControl.notificationQueries.UpdateAllWaitNotifyOrdersToNotify(order);
							String message = String.format(
									"Order: %d, Notification was sent by email to %s and SMS to %s", order.getOrderId(),
									order.getEmail(), order.getTelephoneNumber());
							Platform.runLater(() -> serverController.printToLogConsole(message));
						}
					}

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});

		cancelOrdersNotConfirmedWithin2Hours = new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(1000);
					LocalDateTime currentTime = LocalDateTime.now();
					LocalDateTime relevantTimeTomorrowMinus2Hours = currentTime.plusHours(22);
					ArrayList<Order> ordersToNotify = QueryControl.notificationQueries
							.CheckAllOrdersAndChangeToCancelledIfNeeded(relevantTimeTomorrowMinus2Hours);
					
					ArrayList<Order> ordersNotifiedFromWaitingList = QueryControl.notificationQueries
							.CheckAllWaitingListOrdersAndCancelAutomaticallyIfNotConfirmed();
					
					if (ordersToNotify != null && !ordersToNotify.isEmpty()) {
						for (Order order : ordersToNotify) {
							QueryControl.notificationQueries.automaticallyCancelAllNotifiedOrders(order);
							clientRequestHandler.notifyOrdersFromWaitingList(order.getEnterDate(), order.getParkName().getParkId());
							String message = String.format(
									"Order: %d, Notification on Automatically cancel becuase of unconfirmed order within 2 hours was sent by email to %s and SMS to %s", order.getOrderId(),
									order.getEmail(), order.getTelephoneNumber());
							Platform.runLater(() -> serverController.printToLogConsole(message));
						}
					}
					
					if(ordersNotifiedFromWaitingList!=null && !ordersNotifiedFromWaitingList.isEmpty()) {
						for(Order order: ordersNotifiedFromWaitingList) {
							QueryControl.notificationQueries.automaticallyCancelAllNotifiedOrders(order);
							clientRequestHandler.notifyOrdersFromWaitingList(order.getEnterDate(), order.getParkName().getParkId());
							String message = String.format(
									"Order: %d, Notification on Automatically cancel becuase of unconfirmed order within 2 hours, was sent by email to %s and SMS to %s", order.getOrderId(),
									order.getEmail(), order.getTelephoneNumber());
							Platform.runLater(() -> serverController.printToLogConsole(message));
						}
					}
					
					
					

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});

		cancelTimePassedWaitingListOrders = new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(1000);
					LocalDateTime currentTime = LocalDateTime.now();
					ArrayList<Order> ordersToNotify = QueryControl.notificationQueries
							.CheckWaitingListAndRemoveAllIrrelcantOrders(currentTime);

					if (ordersToNotify != null && !ordersToNotify.isEmpty()) {
						Platform.runLater(() -> serverController.printToLogConsole(String.format(
								"All orders in waiting list for %s marked as irrelevant", currentTime.toString())));
						for (Order order : ordersToNotify) {
							QueryControl.notificationQueries.automaticallyMarkOrdersAsIrrelevant(order);
							String message = String.format(
									"Order: %d, Notification on order Irrelevant because date passed was sent by email to %s and SMS to %s", order.getOrderId(),
									order.getEmail(), order.getTelephoneNumber());
							Platform.runLater(() -> serverController.printToLogConsole(message));
						}
					}

				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
					break;
				}
			}
		});
	}
}
