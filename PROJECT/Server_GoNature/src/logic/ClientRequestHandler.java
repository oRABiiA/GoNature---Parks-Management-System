package logic;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

import gui.controller.ServerScreenController;
import javafx.application.Platform;
import jdbc.query.QueryControl;
import ocsf.ConnectionToClient;
import utils.enums.ClientRequest;
import utils.enums.OrderStatusEnum;
import utils.enums.ServerResponse;

/**
 * Handles client requests for the server application, routing each request to the appropriate handler method
 * based on the type of request. This class is responsible for processing a wide range of requests, from user
 * login and order management to report generation and park management tasks.
 * Each request handling method receives a {@link ClientRequestDataContainer} containing the request data and
 * a {@link ConnectionToClient} object for communication back to the client. It returns a
 * {@link ServerResponseBackToClient} object encapsulating the response to be sent.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */
public class ClientRequestHandler {

	private ServerScreenController serverController;
	
	/**
	 * Constructs a new ClientRequestHandler with a reference to the server's main controller.
	 * This controller is used for logging and managing the server's state and active connections.
	 *
	 * @param serverController the main controller of the server application, responsible for UI updates and connection management
	 */
	public ClientRequestHandler(ServerScreenController serverController) {
		this.serverController = serverController;
	}
	
	/**
	 * Processes the incoming request from a client, directing it to the appropriate handler based on the request type.
	 * This method acts as a router, delegating tasks such as login, order processing, and report generation to specific
	 * handlers, and constructs a response to be sent back to the client.
	 *
	 * @param data the data container holding the request and its associated data
	 * @param client the client connection through which the request was received
	 * @return a {@link ServerResponseBackToClient} object representing the outcome of the request processing, which can include success, failure, or data payloads
	 */
	public ServerResponseBackToClient handleRequest(ClientRequestDataContainer data,ConnectionToClient client) {
		ClientRequest request = data.getRequest();
		ServerResponseBackToClient response = null;
		
		switch (request) {
		case Login_As_Employee:
			response = handleLoginAsEmployee(data, client);
			if (response.getRensponse() == ServerResponse.User_Already_Connected
					|| response.getRensponse() == ServerResponse.User_Does_Not_Found
					|| response.getRensponse() == ServerResponse.Password_Incorrect)
				break;
			serverController.addToConnected(client, ((Employee) response.getMessage()).getUsername());
			break;

		case Login_As_Guide:
			response = handleLoginAsGuide(data, client);
			if (response.getRensponse() == ServerResponse.User_Already_Connected
					|| response.getRensponse() == ServerResponse.Guide_Status_Pending
					|| response.getRensponse() == ServerResponse.Password_Incorrect
					|| response.getRensponse() == ServerResponse.User_Does_Not_Found)
				break;
			serverController.addToConnected(client, ((Guide) response.getMessage()).getUsername());
			break;

		case Login_As_Visitor:
			response = handleLoginAsVisitor(data, client);
			if (response.getRensponse() == ServerResponse.User_Already_Connected
					|| response.getRensponse() == ServerResponse.Visitor_Have_No_Orders_Yet)
				break;
			serverController.addToConnected(client, "Visitor " + ((Visitor) response.getMessage()).getCustomerId());
			break;

		case Search_For_Relevant_Order:
			response = handleSearchForRelevantOrder(data, client);
			break;

		case Add_New_Order_If_Available:
			response = handleAddNewOrderIfAvailable(data, client);
			break;

		case Insert_New_Order_As_Wait_Notify:
			response = handleInsertNewOrderAsWaitNotify(data, client);
			break;

		case Search_For_Available_Date:
			response = handleSearchForAvailableDates(data, client);
			break;

		case Update_Order_Status_Canceled:
			response = handleUpdateOrderStatusCanceled(data, client);
			break;
		case Update_Order_Status_Confirmed:
			response = handleUpdateOrderStatusConfirmed(data,client);
			break;

		// Service Employee Section
		case Update_Guide_As_Approved:
			response = handleUpdateGuideAsApproved(data, client);
			break;
		case Search_For_Guides_Status_Pending:
			response = handleSearchForGuidesWithStatusPending(data, client);
			break;

		// Park Section
		case Search_For_Specific_Park:
			response = handleSearchForSpecificPark(data, client);
			break;

		case Update_Order_Status_Completed:
			response = handleUpdateOrderStatusCompleted(data, client);
			break;

		case Update_Order_Status_Time_Passed:
			response = handleUpdateOrderStatusTimePassed(data, client);
			break;

		case Update_Order_Status_In_Park:
			response = handleUpdateOrderStatusInPark(data, client);
			break;

		case Prepare_New_Occasional_Order:
			response = handlePrepareNewOccasionalOrder(data,client);
			break;
			
		case Add_Occasional_Visit_As_In_Park:
			response = handleAddOccasionalVisitAsInPark(data,client);
			break;
			
		// Requests Section
		case Make_New_Park_Estimated_Visit_Time_Request:
			response = handleMakeNewParkEstimatedVisitTimeRequest(data, client);
			break;
		case Make_New_Park_Reserved_Entries_Request:
			response = handleMakeNewParkReservedEntriesRequest(data, client);
			break;
		case Make_New_Park_Capacity_Request:
			response = handleMakeNewParkCapacityRequest(data, client);
			break;
		case Import_All_Pending_Requests:
			response = handleImportAllPendingRequests(data, client);
			break;
		case Update_Request_In_Database:
			response = handleUpdateRequestInDatabase(data, client);
			break;

		// Reports Section
		case Create_Visits_Report:
			response = handleCreateVisitsReport(data, client);
			break;
			
		case Import_Visits_Report:
			response = handleImportVisitsReport(data, client);
			break;

		case Create_Cancellations_Report:
			response = handleCreateCancellationsReport(data, client);
			break;

		case Import_Cancellations_Report:
			response = handleImportCancellationsReport(data, client);
			break;

		case Create_Usage_Report:
			response = handleCreateUsageReport(data, client);
			break;
		case Import_Usage_Report:
			response = handleImportUsageReport(data, client);
			break;

		case Import_All_Orders_For_Now:
			response = handleImportAllOrdersForNow(data, client);
			break;

		case Create_Total_Visitors_Report:
			response = handleCreateTotalAmountDivisionReport(data, client);
			break;

		case Import_Total_Visitors_Report:
			response = handleImportTotalAmountDivisionReport(data, client);
			break;

		case Search_For_Notified_Orders:
			response = handleSearchForNotifiedOrders(data, client);
			break;

		case Show_Payment_At_Entrance:
			response = handleShowPaymentAtEntrance(data, client);
			break;
			
		case Delete_Old_Order:
			response = handleDeleteOldOrder(data,client);
			break;

		default:
			break;
		}
		// Print to Log
		String message = String.format("Client: %s, Sent request: %s, Server Response: %s",client.getInetAddress().getHostAddress(),request,response.getRensponse());
		Platform.runLater(()->serverController.printToLogConsole(message));
		return response;
	}
	
	/**
	 * Handles the deletion of an existing order from the database. This method attempts to delete the specified order
	 * by invoking a deletion query. Based on the outcome of this operation, it constructs and returns a response object
	 * indicating either success or failure of the deletion process.
	 * 
	 * @param data The {@link ClientRequestDataContainer} containing the order to be deleted. The contained data
	 *             must be an instance of {@link Order}.
	 * @param client The {@link ConnectionToClient} representing the connection to the client requesting the deletion.
	 *               This parameter is currently not used in the method but allows for future extensions, such as
	 *               sending acknowledgments or error messages directly to the client.
	 * @return A {@link ServerResponseBackToClient} object indicating the result of the operation. Returns
	 *         ServerResponse.Order_Deleted_Successfully if the order was successfully deleted, or
	 *         ServerResponse.Order_Deleted_Failed if the deletion failed.
	 */
	private ServerResponseBackToClient handleDeleteOldOrder(ClientRequestDataContainer data, ConnectionToClient client) {
		Order order = (Order)data.getData();
		boolean isDeleted = QueryControl.orderQueries.deleteOrderFromTable(order);
		if(isDeleted) {
			// notify next in waiting list.
			notifyOrdersFromWaitingList(order.getEnterDate(), order.getParkName().getParkId());
			return new ServerResponseBackToClient(ServerResponse.Order_Deleted_Successfully, order);
		}
		else
			return new ServerResponseBackToClient(ServerResponse.Order_Deleted_Failed, order);
	}
	
	/**
	 * Prepares a new occasional order based on the requested park and visit details provided in the request data.
	 * It calculates the exit time based on the park's current estimated stay time and sets the order price according
	 * to the park's pricing. If the park specified in the order can be found and all calculations are successfully
	 * performed, it marks the order as ready.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the {@link Order} to be prepared.
	 * @param client The {@link ConnectionToClient} object representing the client making the request. Not used in the method but available for future needs.
	 * @return A {@link ServerResponseBackToClient} object with the result of the operation, either indicating the order is ready or that the query failed.
	 */
	private ServerResponseBackToClient handlePrepareNewOccasionalOrder(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		Park requestedPark = new Park(order.getParkName().getParkId());
		ServerResponseBackToClient response;
		boolean foundPark = QueryControl.parkQueries.getParkById(requestedPark);
		if (foundPark) {
			double estimatedVisitTime = requestedPark.getCurrentEstimatedStayTime();
			// Split the estimatedVisitTime into whole days and fractional day components
			long estimatedVisitTimeInHours = (long) (estimatedVisitTime);

			LocalDateTime enterTime = order.getEnterDate();
			LocalDateTime exitTime = enterTime.plusHours(estimatedVisitTimeInHours);
			order.setExitDate(exitTime);
			order.setPrice(requestedPark.getPrice());
			if(requestedPark.getCurrentInPark()+order.getNumberOfVisitors()>requestedPark.getCurrentMaxCapacity())
				response = new ServerResponseBackToClient(ServerResponse.Park_Is_Full_For_Such_Occasional_Order, order);
			else
				response = new ServerResponseBackToClient(ServerResponse.Occasional_Visit_Order_Ready, order);
		} else
			response = new ServerResponseBackToClient(ServerResponse.Query_Failed, order);
		return response;
	}
	
	/**
	 * Processes the addition of an occasional visit as an in-park visit. It attempts to insert the provided order
	 * into the database as an occasional visit. This method is typically called after an occasional visitor arrives
	 * at the park and their order needs to be recorded as such.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the {@link Order} to be added as an in-park visit.
	 * @param client The {@link ConnectionToClient} object for the client making the request. This parameter is currently not used directly.
	 * @return A {@link ServerResponseBackToClient} indicating success or failure of adding the visit to the database.
	 */
	private ServerResponseBackToClient handleAddOccasionalVisitAsInPark(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		ServerResponseBackToClient response;
		QueryControl.occasionalQueries.insertOccasionalOrder(order);
		response = new ServerResponseBackToClient(ServerResponse.Occasional_Visit_Added_Successfully, order);
		return response;
	}
	
	/**
	 * Handles the request to show payment details at the park entrance for a specific order. This method fetches the
	 * order by its ID and prepares the details for display or further processing at the park entrance.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the ID of the order for which payment details are requested.
	 * @param client The {@link ConnectionToClient} object for the client making the request. Not used directly in this method.
	 * @return A {@link ServerResponseBackToClient} object containing the order details if found, or an indication of failure if the order cannot be retrieved.
	 */
	private ServerResponseBackToClient handleShowPaymentAtEntrance(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Integer orderId = (Integer) data.getData();
		Order order = new Order(orderId);
		ServerResponseBackToClient response;
		ServerResponse DbResponse = QueryControl.orderQueries.fetchOrderByOrderID(order);
		if(DbResponse==ServerResponse.Query_Failed) {
			serverController.printToLogConsole("SQL Exception was thrown during search relevant order query");
			response = new ServerResponseBackToClient(ServerResponse.Query_Failed, null);
		}
		else
			response = new ServerResponseBackToClient(DbResponse,order);
		return response;

	}
	
	/**
	 * Updates the status of an order to 'Completed'. This method determines the type of order (regular or occasional)
	 * based on input details and updates its status accordingly. A successful update returns a confirmation response,
	 * while a failure in the update process returns an error response.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the order ID and the table (order type) it belongs to.
	 * @param client The {@link ConnectionToClient} object for communication with the client. This is not directly used within the method.
	 * @return A {@link ServerResponseBackToClient} object indicating the result of the update operation.
	 */
	private ServerResponseBackToClient handleUpdateOrderStatusCompleted(ClientRequestDataContainer data,
			ConnectionToClient client) {
		@SuppressWarnings("unchecked")
		ArrayList<Object> details = (ArrayList<Object>) data.getData();
		Integer orderId = (Integer) details.get(0);
		String orderTable = (String) details.get(1);
		ServerResponseBackToClient response;
		boolean isUpdated;
		if (orderTable.equals("Occasional")) {
			isUpdated = QueryControl.occasionalQueries.UpdateOccasionalOrderStatus(new Order(orderId),
					OrderStatusEnum.Completed);
			if (isUpdated)
				response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Successfully, null);
			else
				response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Failed, null);
		} else {
			isUpdated = QueryControl.orderQueries.updateOrderStatus(new Order(orderId), OrderStatusEnum.Completed);
			if (isUpdated)
				response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Successfully, null);
			else
				response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Failed, null);
		}
		return response;

	}
	
	/**
	 * Updates the status of an order to 'Time Passed', indicating that the time slot for the order has expired.
	 * This operation is typically invoked when an order's scheduled time has passed without confirmation or completion.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the order ID to be updated.
	 * @param client The {@link ConnectionToClient} object for the client request. Not directly used within the method.
	 * @return A {@link ServerResponseBackToClient} object reflecting the success or failure of the status update.
	 */
	private ServerResponseBackToClient handleUpdateOrderStatusTimePassed(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Integer orderId = (Integer) data.getData();
		ServerResponseBackToClient response;
		boolean isUpdated;
		isUpdated = QueryControl.orderQueries.updateOrderStatus(new Order(orderId), OrderStatusEnum.Time_Passed);
		if (isUpdated)
			response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Successfully, null);
		else
			response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Failed, null);
		return response;
	}
	
	/**
	 * Updates the status of an order to 'In Park', signifying that the visitors associated with the order are currently
	 * in the park. This status update is part of managing park capacity and tracking visitor presence.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the order ID to be updated.
	 * @param client The {@link ConnectionToClient} connection through which the request was received. Not directly utilized in this method.
	 * @return A {@link ServerResponseBackToClient} object indicating the outcome of the update.
	 */
	private ServerResponseBackToClient handleUpdateOrderStatusInPark(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Integer orderId = (Integer) data.getData();
		ServerResponseBackToClient response;
		boolean isUpdated;
		isUpdated = QueryControl.orderQueries.updateOrderStatus(new Order(orderId), OrderStatusEnum.In_Park);
		if (isUpdated)
			response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Successfully, null);
		else
			response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Failed, null);
		return response;
	}
	
	/**
	 * Imports all orders for the current day for a specified park. This method is typically used for operational
	 * planning and real-time park management, allowing staff to view and manage the day's orders.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the park ID for which orders are requested.
	 * @param client The {@link ConnectionToClient} object for the client making the request. Not used within the method.
	 * @return A {@link ServerResponseBackToClient} object containing a list of all orders for the day or an appropriate error message.
	 */
	@SuppressWarnings("unused")
	private ServerResponseBackToClient handleImportAllOrdersForNow(ClientRequestDataContainer data,
			ConnectionToClient client) {
		int parkId = (int) data.getData();
		ServerResponseBackToClient response;
		ArrayList<Order> listOfOrders = QueryControl.orderQueries.importAllOrdersForToday(parkId);
		if(listOfOrders.isEmpty())
			response = new ServerResponseBackToClient(ServerResponse.No_Orders_For_Today, listOfOrders);
		
		else if (listOfOrders == null)
			response = new ServerResponseBackToClient(ServerResponse.Query_Failed, null);
		
		else
			response = new ServerResponseBackToClient(ServerResponse.Import_All_Orders_Successfully, listOfOrders);
		
		return response;

	}
	
	/**
	 * Updates the status of an order to 'Confirmed', indicating that the order has been officially accepted and confirmed
	 * by the visitor or system. This is a crucial step in the booking process, securing the visitor's spot for the scheduled time.
	 *
	 * @param data The {@link ClientRequestDataContainer} containing the order to be confirmed.
	 * @param client The {@link ConnectionToClient} object representing the client making the request. Not used within this method.
	 * @return A {@link ServerResponseBackToClient} object reflecting the success or failure of confirming the order.
	 */
	private ServerResponseBackToClient handleUpdateOrderStatusConfirmed(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		ServerResponseBackToClient response;
		OrderStatusEnum statusToUpdate = OrderStatusEnum.Confirmed;
		if(order.getStatus()==OrderStatusEnum.Notified_Waiting_List) {
			statusToUpdate = OrderStatusEnum.Wait_Notify;
			Duration tillOrder = Duration.between(order.getEnterDate(), LocalDateTime.now());
			if(tillOrder.toHours()<24)
				statusToUpdate=OrderStatusEnum.Confirmed;
		}
		boolean isUpdated = QueryControl.orderQueries.updateOrderStatus(order, statusToUpdate);
		if (isUpdated) {
			response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Successfully, order);
			String message;
			if(statusToUpdate==OrderStatusEnum.Wait_Notify)
				message = String.format("Order: %d, Was created successfully, a confirmation message has been sent by email to %s and SMS to %s"
						,order.getOrderId(),order.getEmail(),order.getTelephoneNumber());
			else
				message = String.format("Order: %d, Was confirmed successfully, a summary order message has been sent by email to %s and SMS to %s",
						order.getOrderId(),order.getEmail(),order.getTelephoneNumber());
			Platform.runLater(()->serverController.printToLogConsole(message));
		} else
			response = new ServerResponseBackToClient(ServerResponse.Order_Updated_Failed, order);

		return response;
	}
	
	/**
	 * Updates the status of an order to 'Canceled'. After successfully updating the order status,
	 * it attempts to notify the next orders in the waiting list for the same time slot and park.
	 *
	 * @param data The data container with the order to be canceled.
	 * @param client The client connection making the request.
	 * @return A server response indicating whether the order was successfully canceled or if the operation failed.
	 */
	private ServerResponseBackToClient handleUpdateOrderStatusCanceled(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		ServerResponseBackToClient response;
		boolean isUpdated = QueryControl.orderQueries.updateOrderStatus(order, OrderStatusEnum.Cancelled);
		if (isUpdated) {
			response = new ServerResponseBackToClient(ServerResponse.Order_Cancelled_Successfully, order);
			// TODO: notify next in waiting list.
			notifyOrdersFromWaitingList(order.getEnterDate(), order.getParkName().getParkId());

		} else
			response = new ServerResponseBackToClient(ServerResponse.Order_Cancelled_Failed, order);

		return response;
	}
	
	/**
	 * Notifies the next orders in the waiting list about available spots based on the order's date and park ID.
	 * This method is called after canceling an order to fill the available slot with orders from the waiting list.
	 *
	 * @param time The date and time of the canceled order.
	 * @param parkId The ID of the park where the order was scheduled.
	 */
	public void notifyOrdersFromWaitingList(LocalDateTime time, int parkId) {
		ArrayList<Order> ordersFromWaitingList = QueryControl.orderQueries.notifyTheNextOrdersInWaitingList(time,
				parkId);
		if (ordersFromWaitingList == null || ordersFromWaitingList.isEmpty())
			return;

		for (Order order : ordersFromWaitingList) {
			boolean canAdd = QueryControl.orderQueries.isThisDateAvailable(parkId, time, order.getNumberOfVisitors());
			if (canAdd) {
				QueryControl.orderQueries.updateOrderStatus(order, OrderStatusEnum.Notified_Waiting_List);
				serverController.printToLogConsole(String
						.format("Order :%d, notified about available spots from waiting list", order.getOrderId()));
			}
		}

		return;
	}
	
	/**
	 * Searches for orders of a specific client that have been notified about available spots from the waiting list.
	 * This helps clients to keep track of any changes or updates to their waiting list status.
	 *
	 * @param data The container with the client's ID whose notified orders are being searched.
	 * @param client The client connection making the request.
	 * @return A server response containing the list of notified orders or a message indicating no notifications found.
	 */
	private ServerResponseBackToClient handleSearchForNotifiedOrders(ClientRequestDataContainer data,
			ConnectionToClient client) {
		String customerId = ((ICustomer) data.getData()).getCustomerId();
		ServerResponseBackToClient response;
		ArrayList<Order> notifiedOrders = QueryControl.orderQueries.searchForNotifiedOrdersOfSpecificClient(customerId);
		if (notifiedOrders == null)
			response = new ServerResponseBackToClient(ServerResponse.No_Notifications_Found, notifiedOrders);
		else
			response = new ServerResponseBackToClient(ServerResponse.Notifications_Found, notifiedOrders);
		return response;
	}
	
	/**
	 * Inserts a new order into the database with a status indicating it's waiting for notification.
	 * This status is used for orders that couldn't be immediately confirmed due to lack of availability
	 * but are placed on a waiting list for potential openings.
	 *
	 * @param data The data container with the order to be inserted.
	 * @param client The client connection making the request.
	 * @return A server response indicating whether the order was added successfully or if the operation failed.
	 */
	private ServerResponseBackToClient handleInsertNewOrderAsWaitNotify(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		ServerResponseBackToClient response;
		boolean addedToDatabase = QueryControl.orderQueries.insertOrderIntoDB(order);
		if (addedToDatabase) {
			response = new ServerResponseBackToClient(ServerResponse.Order_Added_Successfully, order);
			String message = String.format("Order: %d, Was created successfully, a confirmation message has been sent by email to %s and SMS to %s"
					,order.getOrderId(),order.getEmail(),order.getTelephoneNumber());
			Platform.runLater(()->serverController.printToLogConsole(message));
		}
		else
			response = new ServerResponseBackToClient(ServerResponse.Order_Added_Failed, order);
		return response;
	}
	
	/**
	 * Retrieves details for a specific park based on the park ID provided in the request.
	 * This information might include park capacity, opening hours, and other relevant park details.
	 *
	 * @param data The data container with the park ID to search for.
	 * @param client The client connection making the request.
	 * @return A server response containing the park details or an indication that the park was not found.
	 */
	private ServerResponseBackToClient handleSearchForSpecificPark(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Park park = (Park) data.getData();
		ServerResponseBackToClient response;
		boolean foundPark = QueryControl.parkQueries.getParkById(park);
		if (foundPark)
			response = new ServerResponseBackToClient(ServerResponse.Fetched_Park_Details_Successfully, park);
		else
			response = new ServerResponseBackToClient(ServerResponse.Fetched_Park_Details_Failed, park);
		return response;

	}
	
	/**
	 * Handles the request to change the estimated visit time for a park. This can be initiated by park managers
	 * to adjust visit durations based on various factors like park capacity or special events.
	 *
	 * @param data The data container with the request details.
	 * @param client The client connection making the request.
	 * @return A server response indicating the success of sending the request to the department or if there's a pending request of the same type.
	 */
	private ServerResponseBackToClient handleMakeNewParkEstimatedVisitTimeRequest(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Request request = (Request) data.getData();
		ServerResponseBackToClient response;
		boolean success = QueryControl.requestsQueries.InsertNewRequest(request);
		if (success)
			response = new ServerResponseBackToClient(ServerResponse.Request_Sent_To_Department_Successfully, request);
		else
			response = new ServerResponseBackToClient(ServerResponse.Last_Request_With_Same_Type_Still_Pending,
					request);
		return response;
	}
	
	/**
	 * Imports all pending requests for park managers' review. This typically includes requests for changing park parameters
	 * like capacity or visit time, waiting for approval or denial.
	 *
	 * @param data The data container potentially including a filter or specific request to import.
	 * @param client The client connection making the request.
	 * @return A server response containing all pending requests or an error message if the operation fails.
	 */
	@SuppressWarnings("incomplete-switch")
	private ServerResponseBackToClient handleImportAllPendingRequests(ClientRequestDataContainer data,
			ConnectionToClient client) {
		@SuppressWarnings("unchecked")
		ArrayList<Request> requestList = (ArrayList<Request>) data.getData();
		ServerResponseBackToClient response = null;
		ServerResponse dbResponse = QueryControl.requestsQueries.ShowAllParkManagerRequests(requestList);
		
		if(dbResponse==ServerResponse.Query_Failed) {
			return new ServerResponseBackToClient(dbResponse, null);
		}
		response = new ServerResponseBackToClient(dbResponse, requestList);
		return response;
		

	}
	
	/**
	 * Updates the status of requests in the database, such as approving or denying pending park manager requests.
	 * Each request's new status is determined by the park manager's review.
	 *
	 * @param data The data container with the list of requests and their updated statuses.
	 * @param client The client connection making the request.
	 * @return A server response indicating the success or failure of updating the requests.
	 */
	private ServerResponseBackToClient handleUpdateRequestInDatabase(ClientRequestDataContainer data,
			ConnectionToClient client) {
		@SuppressWarnings("unchecked")
		ArrayList<Request> requestList = (ArrayList<Request>) data.getData();
		ServerResponse dbResponse;
		for (int i = 0; i < requestList.size(); i++) {
			dbResponse = QueryControl.requestsQueries.UpdateStatusRequest(requestList.get(i),
					requestList.get(i).getRequestStatus().name());
			if (dbResponse == ServerResponse.Query_Failed || dbResponse == ServerResponse.Updated_Requests_Failed)
				return new ServerResponseBackToClient(ServerResponse.Updated_Requests_Failed, null);
		}
		return new ServerResponseBackToClient(ServerResponse.Updated_Requests_Successfully, null);
	}
	
	/**
	 * Processes a request to modify the reserved entries for a park. It attempts to insert a new request
	 * into the system for departmental review and approval.
	 *
	 * @param data The data container holding the request details.
	 * @param client The client connection through which the request was made. Not used directly in the method.
	 * @return A response object indicating whether the request was successfully sent for departmental review or if a similar request is still pending.
	 */
	private ServerResponseBackToClient handleMakeNewParkReservedEntriesRequest(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Request request = (Request) data.getData();
		ServerResponseBackToClient response;
		boolean success = QueryControl.requestsQueries.InsertNewRequest(request);
		if (success)
			response = new ServerResponseBackToClient(ServerResponse.Request_Sent_To_Department_Successfully, request);
		else
			response = new ServerResponseBackToClient(ServerResponse.Last_Request_With_Same_Type_Still_Pending,
					request);
		return response;
	}
	
	/**
	 * Handles a request to change the park capacity. Similar to reserved entries requests, it inserts the request
	 * into the system for administrative review.
	 *
	 * @param data The data container with the park capacity change request.
	 * @param client The connection to the client making the request. This parameter is not utilized within the method.
	 * @return A response indicating the outcome of the request submission, either successful submission or a notification of a pending request of the same type.
	 */
	private ServerResponseBackToClient handleMakeNewParkCapacityRequest(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Request request = (Request) data.getData();
		ServerResponseBackToClient response;
		boolean success = QueryControl.requestsQueries.InsertNewRequest(request);
		if (success)
			response = new ServerResponseBackToClient(ServerResponse.Request_Sent_To_Department_Successfully, request);
		else
			response = new ServerResponseBackToClient(ServerResponse.Last_Request_With_Same_Type_Still_Pending,
					request);
		return response;
	}

	/**
	 * Initiates the creation of a Total Amount Division Report for a park. This report details the division of visitors
	 * by category within a specified time frame.
	 *
	 * @param data The data container with the information needed to generate the report.
	 * @param client The client requesting the report generation. Not directly used in the method.
	 * @return A server response indicating whether the report was successfully generated or if the report generation failed.
	 */
	private ServerResponseBackToClient handleCreateTotalAmountDivisionReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		AmountDivisionReport report = (AmountDivisionReport) data.getData();
		ServerResponseBackToClient response;
		boolean result = QueryControl.reportsQueries.generateTotalAmountDivisionReport(report);
		if (result)
			response = new ServerResponseBackToClient(ServerResponse.Report_Generated_Successfully, report);
		else
			response = new ServerResponseBackToClient(ServerResponse.Report_Failed_Generate, report);

		return response;
	}

	/**
	 * Retrieves a previously generated Total Amount Division Report, returning it as a byte array suitable for
	 * client-side rendering or processing.
	 *
	 * @param data The data container identifying the specific report to be retrieved.
	 * @param client The client connection requesting the report. Not used directly in the method.
	 * @return A response containing the report as a byte array if found, or an indication that the report could not be found.
	 */
	private ServerResponseBackToClient handleImportTotalAmountDivisionReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		AmountDivisionReport report = (AmountDivisionReport) data.getData();
		ServerResponseBackToClient response;
		byte[] blobInBytes = QueryControl.reportsQueries.getRequestedTotalAmountReport(report);
		if (blobInBytes == null)
			response = new ServerResponseBackToClient(ServerResponse.Such_Report_Not_Found, blobInBytes);
		else {
			response = new ServerResponseBackToClient(ServerResponse.Cancellations_Report_Found, blobInBytes);
		}
		return response;
	}
	
	/**
	 * Fetches a Visits Report as a byte array, allowing for client-side presentation or further analysis. The report
	 * provides details on park visits over a specified period.
	 *
	 * @param data The request data specifying the report to be fetched.
	 * @param client The client connection making the request. Not used within the method.
	 * @return A server response containing the requested report as a byte array, or an error if the report is not found.
	 */
	private ServerResponseBackToClient handleImportVisitsReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		VisitsReport report = (VisitsReport) data.getData();
		ServerResponseBackToClient response;
		byte[] blobInBytes = QueryControl.reportsQueries.getRequestedVisitsReport(report);
		if (blobInBytes == null)
			response = new ServerResponseBackToClient(ServerResponse.Such_Report_Not_Found, blobInBytes);
		else {
			response = new ServerResponseBackToClient(ServerResponse.Cancellations_Report_Found, blobInBytes);
		}
		return response;
	}
	
	/**
	 * Creates a Visits Report detailing park visitations within a specified timeframe, including data on visitor
	 * numbers and visit durations.
	 *
	 * @param data The data container with the parameters for report generation.
	 * @param client The client connection requesting the report. Not directly utilized in the method.
	 * @return A response indicating the success or failure of the report generation process.
	 */
	private ServerResponseBackToClient handleCreateVisitsReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		VisitsReport report = (VisitsReport) data.getData();
		ServerResponseBackToClient response;
		boolean result = QueryControl.reportsQueries.generateVisitsReport(report);
		if (result)
			response = new ServerResponseBackToClient(ServerResponse.Report_Generated_Successfully, report);
		else
			response = new ServerResponseBackToClient(ServerResponse.Report_Failed_Generate, report);

		return response;
	}
	
	/**
	 * Initiates the generation of a cancellations report based on the specified criteria in the report object.
	 * This report includes details on all cancellations within a given time frame.
	 *
	 * @param data The container holding the cancellations report request data.
	 * @param client The client connection requesting the report generation. Not used directly.
	 * @return A response indicating the success or failure of generating the cancellations report.
	 */
	private ServerResponseBackToClient handleCreateCancellationsReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		CancellationsReport report = (CancellationsReport) data.getData();
		ServerResponseBackToClient response;
		boolean result = QueryControl.reportsQueries.generateCancellationsReport(report);
		if (result)
			response = new ServerResponseBackToClient(ServerResponse.Report_Generated_Successfully, report);
		else
			response = new ServerResponseBackToClient(ServerResponse.Report_Failed_Generate, report);

		return response;
	}
	
	/**
	 * Retrieves a previously generated cancellations report as a byte array, allowing for download or display.
	 *
	 * @param data The container with the cancellations report to be retrieved.
	 * @param client The client connection requesting the report. Not used directly.
	 * @return A response containing the cancellations report as a byte array or an error if the report cannot be found.
	 */
	private ServerResponseBackToClient handleImportCancellationsReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		CancellationsReport report = (CancellationsReport) data.getData();
		ServerResponseBackToClient response;
		byte[] blobInBytes = QueryControl.reportsQueries.getRequestedCancellationsReport(report);
		if (blobInBytes == null)
			response = new ServerResponseBackToClient(ServerResponse.Such_Report_Not_Found, blobInBytes);
		else {
			response = new ServerResponseBackToClient(ServerResponse.Cancellations_Report_Found, blobInBytes);
		}
		return response;
	}

	/**
	 * Generates a usage report detailing park usage statistics over a specified period. This report helps in understanding
	 * the park's operational efficiency and visitor distribution.
	 *
	 * @param data The data container with information necessary for creating the usage report.
	 * @param client The client connection making the request. Not used directly.
	 * @return A response indicating the outcome of the report generation, either successful or failed.
	 */
	private ServerResponseBackToClient handleCreateUsageReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		UsageReport report = (UsageReport) data.getData();
		ServerResponseBackToClient response;
		boolean result = QueryControl.reportsQueries.generateUsageReport(report);
		if (result)
			response = new ServerResponseBackToClient(ServerResponse.Report_Generated_Successfully, report);
		else
			response = new ServerResponseBackToClient(ServerResponse.Report_Failed_Generate, report);

		return response;
	}
	
	/**
	 * Fetches a previously generated usage report, providing the data as a byte array for client-side processing or display.
	 *
	 * @param data The container specifying which usage report to retrieve.
	 * @param client The client connection requesting the report. Not utilized directly.
	 * @return A server response containing the usage report as a byte array, or an error if the report is not found.
	 */
	private ServerResponseBackToClient handleImportUsageReport(ClientRequestDataContainer data,
			ConnectionToClient client) {
		UsageReport report = (UsageReport) data.getData();
		ServerResponseBackToClient response;
		byte[] blobInBytes = QueryControl.reportsQueries.getRequestedUsageReport(report);
		if (blobInBytes == null)
			response = new ServerResponseBackToClient(ServerResponse.Such_Report_Not_Found, blobInBytes);
		else {
			response = new ServerResponseBackToClient(ServerResponse.Cancellations_Report_Found, blobInBytes);
		}
		return response;
	}
	
	/**
	 * Searches for guides with a pending approval status. This method is typically used by park managers or administrators
	 * to review guides awaiting approval.
	 *
	 * @param data The data container potentially specifying criteria for the search.
	 * @param client The client connection making the request. Not directly used.
	 * @return A response with a list of guides who have pending status, or an error if the search fails.
	 */
	@SuppressWarnings("incomplete-switch")
	private ServerResponseBackToClient handleSearchForGuidesWithStatusPending(ClientRequestDataContainer data,
			ConnectionToClient client) {
		@SuppressWarnings("unchecked")
		ArrayList<Guide> guidesList = (ArrayList<Guide>) data.getData();
		ServerResponseBackToClient response = null;
		ServerResponse dbResponse = QueryControl.employeeQueries.ShowAllGuidesWithPendingStatus(guidesList);
		if(dbResponse==ServerResponse.Query_Failed)
			return new ServerResponseBackToClient(dbResponse, dbResponse);
		
		response = new ServerResponseBackToClient(dbResponse, guidesList);
		return response;


	}
	
	/**
	 * Approves one or more guides, updating their status in the system. This operation is usually performed by park managers
	 * or administrators to grant new guides access to the system.
	 *
	 * @param data The container with a list of guides to be approved.
	 * @param client The client connection requesting the update. Not used directly.
	 * @return A response indicating the success or failure of the guide approval process.
	 */
	private ServerResponseBackToClient handleUpdateGuideAsApproved(ClientRequestDataContainer data,
			ConnectionToClient client) {
		@SuppressWarnings("unchecked")
		ArrayList<Guide> guidesList = (ArrayList<Guide>) data.getData();
		ServerResponse dbResponse;
		for (int i = 0; i < guidesList.size(); i++) {
			dbResponse = QueryControl.employeeQueries.UpdateGuideStatusToApprove(guidesList.get(i));
			if (dbResponse != ServerResponse.Updated_Guides_To_Approved_Successfully)
				return new ServerResponseBackToClient(ServerResponse.Updated_Guides_To_Approved_Failed, null);
		}
		return new ServerResponseBackToClient(ServerResponse.Updated_Guides_To_Approved_Successfully, null);
	}
	
	/**
	 * Searches for available dates for new orders within a 7-day window from a specified starting point. This method is
	 * helpful for visitors or agents looking to find open slots for park visits.
	 *
	 * @param data The data container with the initial date from which to search for availability.
	 * @param client The client connection making the request. Not utilized within the method.
	 * @return A response with a list of available dates or a null response if there are no available dates.
	 */
	private ServerResponseBackToClient handleSearchForAvailableDates(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		ArrayList<LocalDateTime> availableDates = QueryControl.orderQueries.searchForAvailableDates7DaysForward(order);
		return new ServerResponseBackToClient(null, availableDates);
	}
	
	/**
	 * Attempts to add a new order if the requested date and time are available. This method checks the park's capacity
	 * and existing bookings before confirming the new order.
	 *
	 * @param data The data container with the new order details.
	 * @param client The client connection attempting to place a new order. Not used directly.
	 * @return A response indicating whether the new order could be added or if it was not possible due to lack of availability.
	 */
	private ServerResponseBackToClient handleAddNewOrderIfAvailable(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		ServerResponseBackToClient response;
		ServerResponse DbResponse = QueryControl.orderQueries.checkIfNewOrderAvailableAtRequestedDate(order);
		response = new ServerResponseBackToClient(DbResponse, order);
		return response;

	}
	
	/**
	 * Handles the login request for an employee. It verifies the employee's credentials and approval status in the system.
	 * If the employee is already connected, it updates the response to indicate this status.
	 *
	 * @param data The container with the employee login information.
	 * @param client The connection to the client making the request.
	 * @return A response indicating the outcome of the login attempt, including success, failure due to SQL exception, or already connected status.
	 */
	private ServerResponseBackToClient handleLoginAsEmployee(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Employee employee = (Employee) data.getData();
		ServerResponseBackToClient response;
		ServerResponse DbResponse = QueryControl.employeeQueries.searchForApprovedEmployee(employee);
		
		if(DbResponse==ServerResponse.Employee_Connected_Successfully) {
			response = new ServerResponseBackToClient(ServerResponse.Employee_Connected_Successfully, employee);
			for (ClientConnection cl : serverController.getClientsList()) {
				if (cl.isAlreadyConnected(new ClientConnection(employee.getUsername(), client))) {
					response.setRensponse(ServerResponse.User_Already_Connected);
					break;
				}
			}
		}
		else if(DbResponse==ServerResponse.Query_Failed) {
			serverController.printToLogConsole("SQL Exception was thrown during search for approved employee query");
			response = new ServerResponseBackToClient(ServerResponse.Query_Failed, null);
		}
		else {
			response = new ServerResponseBackToClient(DbResponse, employee);
		}
		return response;
	
	}
	
	/**
	 * Processes the login request for a guide. It checks if the guide is approved in the system and not already connected.
	 * Responses vary based on the verification outcome and any SQL exceptions encountered.
	 *
	 * @param data The data container with the guide's login information.
	 * @param client The client connection requesting the login.
	 * @return A server response indicating the result of the login attempt, which could be successful, failed due to SQL issues, or already connected status.
	 */
	private ServerResponseBackToClient handleLoginAsGuide(ClientRequestDataContainer data, ConnectionToClient client) {
		Guide guide = (Guide) data.getData();
		ServerResponseBackToClient response;
		ServerResponse DbResponse = QueryControl.customerQueries.searchForApprovedGuide(guide);
		
		if(DbResponse == ServerResponse.Guide_Connected_Successfully) {
			response = new ServerResponseBackToClient(ServerResponse.Guide_Connected_Successfully, guide);
			for (ClientConnection cl : serverController.getClientsList()) {
				if (cl.isAlreadyConnected(new ClientConnection(guide.getUsername(), client))) {
					response.setRensponse(ServerResponse.User_Already_Connected);
					break;
				}
			}
		}
		else if(DbResponse == ServerResponse.Query_Failed) {
			serverController.printToLogConsole("SQL Exception was thrown during search for approved guide query");
			response = new ServerResponseBackToClient(ServerResponse.Query_Failed, null);
		}
		else {
			response = new ServerResponseBackToClient(DbResponse, guide);
		}
	
		return response;
	}
	
	/**
	 * Manages the login process for a visitor. This includes verifying the visitor's access rights and handling any SQL exceptions.
	 *
	 * @param data The data container with the visitor's information.
	 * @param client The connection to the client attempting to login.
	 * @return A response detailing the outcome of the login attempt, such as failure due to SQL exceptions or success.
	 */
	private ServerResponseBackToClient handleLoginAsVisitor(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Visitor visitor = (Visitor) data.getData();
		ServerResponseBackToClient response;
		ServerResponse DbResponse = QueryControl.customerQueries.searchAccessForVisitor(visitor);
		
		if(DbResponse==ServerResponse.Query_Failed) {
			serverController.printToLogConsole("SQL Exception was thrown during search for login visitor query");
			response = new ServerResponseBackToClient(ServerResponse.Query_Failed, null);
		}
		else {
			response = new ServerResponseBackToClient(DbResponse, visitor);
		}
		return response;
		
	}
	
	/**
	 * Searches for an order based on its ID. This method is utilized for retrieving specific order details,
	 * handling cases where the search may fail due to SQL exceptions.
	 *
	 * @param data The container with the order ID to search for.
	 * @param client The client connection making the request.
	 * @return A response with the order details if found or an error message indicating the search failure.
	 */
	private ServerResponseBackToClient handleSearchForRelevantOrder(ClientRequestDataContainer data,
			ConnectionToClient client) {
		Order order = (Order) data.getData();
		ServerResponseBackToClient response;
		ServerResponse DbResponse = QueryControl.orderQueries.fetchOrderByOrderID(order);
		if(DbResponse==ServerResponse.Query_Failed) {
			response = new ServerResponseBackToClient(ServerResponse.Query_Failed, null);
		}
		else {
			response = new ServerResponseBackToClient(DbResponse, order);
		}
		return response;
		

	}
}
