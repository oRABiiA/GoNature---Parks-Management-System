package jdbc.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;

import jdbc.MySqlConnection;
import logic.Order;
import logic.Park;
import utils.enums.OrderStatusEnum;
import utils.enums.OrderTypeEnum;
import utils.enums.ParkNameEnum;
import utils.enums.ServerResponse;
import utils.enums.UserTypeEnum;

/**
 * Handles all database operations related to orders, including fetching, inserting, updating, and deleting orders within the park management system. This class provides methods to interact with both preorder and occasional visit tables, manage order statuses, search for available dates, and more, ensuring the orders are correctly processed and managed in the database.
 * Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */
public class OrderQueries {

	private ParkQueries parkQueries = new ParkQueries();

	public OrderQueries() {
	}

	/**
	 * Gets an order and checks if it's in DB
	 * 
	 * @param order - the order to search for. *already initialized*
	 * @return on Success: returns Order_Found_Successfully on Failure: returns
	 *         Such_Order_Does_Not_Exists exception: returns Exception_Was_Thrown
	 */
	public ServerResponse fetchOrderByOrderID(Order order) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM preorders WHERE orderId = ?"
					+ " AND OrderStatus!='Cancelled' AND OrderStatus!='Completed'" + " AND OrderStatus!='Time Passed'"
					+ " AND OrderStatus!='In Park' AND OrderStatus!='Irrelevant'");
			stmt.setInt(1, order.getOrderId());
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return ServerResponse.Order_Not_Found;
			}

			order.setOrderId(rs.getInt(1));
			order.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
			order.setUserId(String.format("%d", rs.getInt(3)));
			order.setOwnerType(UserTypeEnum.fromString(rs.getString(4)));
			order.setEnterDate(rs.getTimestamp(5).toLocalDateTime());
			order.setExitDate(rs.getTimestamp(6).toLocalDateTime());
			order.setPaid(rs.getBoolean(7));
			order.setStatus(OrderStatusEnum.fromString(rs.getString(8)));
			order.setEmail(rs.getString(9));
			order.setTelephoneNumber(rs.getString(10));
			order.setFirstName(rs.getString(11));
			order.setLastName(rs.getString(12));
			order.setOrderType(OrderTypeEnum.fromString(rs.getString(13)));
			order.setNumberOfVisitors(rs.getInt(14));
			order.setPrice(rs.getDouble(15));

			return ServerResponse.Order_Found;

		} catch (SQLException ex) {
			return ServerResponse.Query_Failed;
		}
	}

	/**
	 * gets an order and checks if the current date and time is available for a specific park
	 * 
	 * @param timeToCheck The LocalDateTime representing the specific hour to check for available spots.
	 * @param parkId The Integer ID of the park for which to check the available spots.
	 * @return An array of Integer, where the first element is the number of spots currently occupied (null if no data is found)
	 * and the second element is the calculated number of available spots at the given time. Returns null in case of a database error.
	 */
	public Integer[] checkAvailableSpotInParkAtSpecificHour(LocalDateTime timeToCheck, Integer parkId) {
		Integer[] amountAndCapacity = new Integer[2];

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT " + "(SELECT SUM(Amount) " + "FROM preorders "
					+ "WHERE EnterDate <= ? AND " + "ExitDate > ? AND " + "(OrderStatus = 'Wait Notify' OR "
					+ "OrderStatus = 'Notified Waiting List' OR " + "OrderStatus = 'Notified' OR "
					+ "OrderStatus = 'Confirmed' OR " + "OrderStatus = 'In Park') " + "AND parkId = ?) AS Count, "
					+ "p.MaxCapacity,p.ReservedSpots " + "FROM parks p " + "WHERE p.parkId = ?;");
			stmt.setString(1, timeToCheck.toString());
			stmt.setString(2, timeToCheck.toString());
			stmt.setInt(3, parkId);
			stmt.setInt(4, parkId);

			ResultSet rs = stmt.executeQuery();
			if (!rs.next()) {
				amountAndCapacity[0] = null;
				amountAndCapacity[1] = rs.getInt(2) - rs.getInt(3);
				return amountAndCapacity;
			}

			amountAndCapacity[0] = rs.getInt(1);
			amountAndCapacity[1] = rs.getInt(2) - rs.getInt(3);
			return amountAndCapacity;

		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}

	}
	
	/**
	 * Searches for available dates for an order within the next 7 days from the specified enter date in the order. This method checks the availability based on the park's current capacity and estimated visit time, ensuring there are enough spots for the number of visitors in the order.
	 *
	 * @param order The order for which the available dates are being searched. The order must contain the park's ID, enter date, and the number of visitors.
	 * @return A list of LocalDateTime objects representing the available dates and times for the next 7 days where the order can be placed. The list will be empty if no available dates are found.
	 */
	public ArrayList<LocalDateTime> searchForAvailableDates7DaysForward(Order order) {
		ArrayList<LocalDateTime> availableDates = new ArrayList<LocalDateTime>();
		int parkId = order.getParkName().getParkId();
		LocalDateTime enterTime = order.getEnterDate();
		int amountOfVisitors = order.getNumberOfVisitors();

		for (int i = 0; i < 7; i++) {
			if (isThisDateAvailable(parkId, enterTime.plusDays(i), amountOfVisitors)) {
				availableDates.add(enterTime.plusDays(i));
			}
		}
		return availableDates;
	}
	
	/**
	 * Checks if a specific date and time is available for a new order in a given park, considering the number of visitors and the park's capacity at that time.
	 *
	 * @param parkId The ID of the park where the availability is being checked.
	 * @param enterTime The LocalDateTime representing the desired entry date and time for the visit.
	 * @param amountOfVisitors The number of visitors for which the availability needs to be checked.
	 * @return true if the date and time are available for the specified number of visitors, false otherwise.
	 */
	public boolean isThisDateAvailable(int parkId, LocalDateTime enterTime, int amountOfVisitors) {
		Park requestedPark = new Park(parkId);
		boolean foundPark = parkQueries.getParkById(requestedPark);
		if (foundPark) {
			double estimatedVisitTime = requestedPark.getCurrentEstimatedStayTime();
			// Split the estimatedVisitTime into whole days and fractional day components
			long estimatedVisitTimeInHours = (long) (estimatedVisitTime);

			LocalDateTime exitTime = enterTime.plusHours(estimatedVisitTimeInHours);

			// Assuming a check every hour
			long frequencyInHours = 1;

			// Calculate the duration between enterTime and exitTime in hours
			long hoursBetween = java.time.Duration.between(enterTime, exitTime).toHours();

			// Calculate the number of checks needed
			long numberOfChecks = hoursBetween / frequencyInHours;

			for (int hour = 0; hour < numberOfChecks; hour++) {
				Integer[] ret = checkAvailableSpotInParkAtSpecificHour(enterTime.plusHours(hour), parkId);
				if (ret[0] != null && ret[0] + amountOfVisitors > ret[1])
					return false;
			}
			return true;
		}
		return false;
	}

	/**
	 * Searches an order in DB using ownerId, ownerId is the user which owns the
	 * order
	 * 
	 * @param order - contains the ownerId
	 * @return on success returns Order_Found_Successfully on failure returns
	 *         Such_Order_Does_Not_Exists exception: returns Excpetion_Was_Thrown
	 */

	// NOTICE : NOT USED THAT QUERY!!
	public ServerResponse fetchOrderByOwnerID(Order order) {

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con
					.prepareStatement("SELECT * FROM preorders WHERE ownerId = ? AND OrderStatus!='Cancelled'");
			stmt.setString(1, order.getUserId());
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return ServerResponse.Order_Not_Found;
			}

			order.setOrderId(rs.getInt(1));
			order.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
			order.setUserId(String.format("%d", rs.getInt(3)));
			order.setOwnerType(UserTypeEnum.fromString(rs.getString(4)));
			order.setEnterDate(rs.getTimestamp(5).toLocalDateTime());
			order.setExitDate(rs.getTimestamp(6).toLocalDateTime());
			order.setPaid(rs.getBoolean(7));
			order.setStatus(OrderStatusEnum.fromString(rs.getString(8)));
			order.setEmail(rs.getString(9));
			order.setTelephoneNumber(rs.getString(10));
			order.setFirstName(rs.getString(11));
			order.setLastName(rs.getString(12));
			order.setOrderType(OrderTypeEnum.fromString(rs.getString(13)));
			order.setNumberOfVisitors(rs.getInt(14));
			order.setPrice(rs.getDouble(15));

			return ServerResponse.Order_Found;

		} catch (SQLException ex) {
			return ServerResponse.Query_Failed;
		}
	}

	/**
	 * gets an order and changes its status according statusToUpdate in DB, also
	 * changes the status in the given entity order.
	 * 
	 * @param order - the order where the change of its status will be made
	 * @param statusToUpdate - the status we should change the order to.
	 * @return on success returns true on failure returns false
	 */
	public boolean updateOrderStatus(Order order, OrderStatusEnum statusToUpdate) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con
					.prepareStatement("UPDATE preorders SET OrderStatus = ?,PayStatus = '1' WHERE (OrderId = ?);");
			stmt.setString(1, statusToUpdate.toString());
			stmt.setInt(2, order.getOrderId());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return false;
			}
			order.setStatus(statusToUpdate);
			order.setLastStatusUpdatedTime(LocalDateTime.now().toString());

			return true;

		} catch (SQLException ex) {
			return false;
		}
	}
	
	/**
	 * Checks if a new order can be placed on the requested date considering the park's capacity and the number of visitors already scheduled for that day.
	 * It calculates if the number of visitors in the park, including the potential new order, would exceed the park's capacity at any time during the visit.
	 *
	 * @param order The order to be checked, containing the park ID, the number of visitors, and the enter date.
	 * @return ServerResponse indicating whether the requested date is available, unavailable, has too many visitors, or if a failure occurred during the process.
	 */
	public ServerResponse checkIfNewOrderAvailableAtRequestedDate(Order order) {
		Park requestedPark = new Park(order.getParkName().getParkId());
		ServerResponse response = ServerResponse.Requested_Order_Date_Is_Available;
		boolean foundPark = parkQueries.getParkById(requestedPark);
		if (foundPark) {
			if (order.getNumberOfVisitors() > requestedPark.getCurrentMaxCapacity()) {
				return ServerResponse.Too_Many_Visitors;
			}
			double estimatedVisitTime = requestedPark.getCurrentEstimatedStayTime();
			// Split the estimatedVisitTime into whole days and fractional day components
			long estimatedVisitTimeInHours = (long) (estimatedVisitTime);

			LocalDateTime enterTime = order.getEnterDate();
			LocalDateTime exitTime = enterTime.plusHours(estimatedVisitTimeInHours);
			order.setExitDate(exitTime);
			order.setPrice(requestedPark.getPrice());

			// Assuming a check every hour
			long frequencyInHours = 1;

			// Calculate the duration between enterTime and exitTime in hours
			long hoursBetween = java.time.Duration.between(enterTime, exitTime).toHours();

			// Calculate the number of checks needed
			long numberOfChecks = hoursBetween / frequencyInHours;

			for (int hour = 0; hour < numberOfChecks; hour++) {
				Integer[] ret = checkAvailableSpotInParkAtSpecificHour(order.getEnterDate().plusHours(hour),
						order.getParkName().getParkId());
				if (ret[0] != null && ret[0] + order.getNumberOfVisitors() > ret[1])
					return ServerResponse.Requested_Order_Date_Unavaliable;
			}

			order.setPrice(requestedPark.getPrice());
			return ServerResponse.Requested_Order_Date_Is_Available;

		}

		return response;
	}

	/**
	 * Gets an order and adds it to the pre-order table in DB
	 * 
	 * @param order - the requested order to add in DB
	 * @return on Success: returns Order_Added_Into_Table on Failure: returns Failed
	 *         exception: returns Exception_Was_Thrown
	 */
	public synchronized boolean insertOrderIntoDB(Order order) {

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"INSERT INTO preorders (ParkId, OwnerId, OwnerType, EnterDate, ExitDate, PayStatus, OrderStatus, Email, Phone, FirstName, LastName, OrderType, Amount, Price) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);

			stmt.setInt(1, order.getParkName().getParkId());
			stmt.setString(2, order.getUserId());
			stmt.setString(3, order.getOwnerType().name());
			stmt.setString(4, order.getEnterDate().toString());
			LocalDate date = order.getEnterDate().toLocalDate();
			LocalTime time = order.getExitDate().toLocalTime();
			LocalDateTime newExitTime = date.atTime(time);
			order.setExitDate(newExitTime);
			;
			stmt.setString(5, order.getExitDate().toString());
			int isPaid = order.isPaid() ? 1 : 0;
			stmt.setInt(6, isPaid); // insert as not paid yet
			stmt.setString(7, order.getStatus().toString());
			stmt.setString(8, order.getEmail());
			stmt.setString(9, order.getTelephoneNumber());
			stmt.setString(10, order.getFirstName());
			stmt.setString(11, order.getLastName());
			stmt.setString(12, order.getOrderType().toString());
			stmt.setInt(13, order.getNumberOfVisitors());
			stmt.setDouble(14, order.getPrice());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return false;
			}

			try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
				if (generatedKeys.next()) {
					long orderId = generatedKeys.getLong(1); // Retrieve the first field in the ResultSet
					order.setOrderId((int) orderId);
				}
			} catch (SQLException ex) {
				return false;
			}

			return true;

		} catch (SQLException ex) {
			return false;
		}
	}

	/**
	 * Gets an order and updates its phone number in the DB
	 * 
	 * @param order - must be already initialized with the updated phone number
	 * @return on success returns Order_PhoneNumber_Updated on Failure: returns
	 *         Failed exception: returns Exception_Was_Thrown
	 */

	// NOTICE : NOT USED THAT QUERY!!
	public ServerResponse updateOrderPhoneNumber(Order order) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET Phone = ? WHERE (OrderId = ?);");
			stmt.setString(1, order.getTelephoneNumber());
			stmt.setInt(2, order.getOrderId());
			int rs = stmt.executeUpdate();
			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return ServerResponse.Query_Failed;
			}
			return ServerResponse.Order_PhoneNumber_Updated;

		} catch (SQLException ex) {
			return ServerResponse.Exception_Was_Thrown;
		}
	}

	/**
	 * Gets an order and updates its Email address in the DB
	 * 
	 * @param order - must be already initialized with the updated Email
	 * @return on success returns Order_Email_Updated on Failure: returns Failed
	 *         exception: returns Exception_Was_Thrown
	 */

	// NOTICE : NOT USED THAT QUERY!!
	public ServerResponse updateOrderEmail(Order order) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET Email = ? WHERE (OrderId = ?);");
			stmt.setString(1, order.getEmail());
			stmt.setInt(2, order.getOrderId());
			int rs = stmt.executeUpdate();
			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return ServerResponse.Query_Failed;
			}
			return ServerResponse.Order_Email_Updated;

		} catch (SQLException ex) {
			return ServerResponse.Exception_Was_Thrown;
		}
	}

	/**
	 * Gets an order and updates its number of visitors (amount) in the DB
	 * 
	 * @param order - must be already initialized with the updated amount
	 * @return on success returns Order_Number_Of_Visitors_Updated on Failure:
	 *         returns Failed exception: returns Exception_Was_Thrown
	 */
	// NOTICE : NOT USED THAT QUERY!!
	public ServerResponse updateOrderNumberOfVisitors(Order order) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET Amount = ? WHERE (OrderId = ?);");
			stmt.setInt(1, order.getNumberOfVisitors());
			stmt.setInt(2, order.getOrderId());
			int rs = stmt.executeUpdate();
			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return ServerResponse.Query_Failed;
			}
			return ServerResponse.Order_Number_Of_Visitors_Updated;

		} catch (SQLException ex) {
			return ServerResponse.Exception_Was_Thrown;
		}
	}


	// NOTICE : NOT USED THAT QUERY!!
	public ServerResponse updateOrderType(Order order, OrderTypeEnum requestedType) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET OrderType = ? WHERE (OrderId = ?);");
			stmt.setString(1, order.getOrderType().toString());
			stmt.setInt(2, order.getOrderId());
			int rs = stmt.executeUpdate();
			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return ServerResponse.Query_Failed;
			}
			return ServerResponse.Order_Type_Updated;

		} catch (SQLException ex) {
			return ServerResponse.Query_Failed;
		}
	}

	// NOTICE : NOT USED THAT QUERY!!
	public ServerResponse updateOrderEnterDate(Order order, LocalDateTime enterDate) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET EnterDate = ? WHERE (OrderId = ?);");
			stmt.setString(1, enterDate.toString());
			stmt.setInt(2, order.getOrderId());
			int rs = stmt.executeUpdate();
			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return ServerResponse.Query_Failed;
			}
			return ServerResponse.Order_EnterDate_Updated;

		} catch (SQLException ex) {
			return ServerResponse.Exception_Was_Thrown;
		}
	}


	// NOTICE : NOT USED THAT QUERY!!
	public ServerResponse updateOrderExitDate(Order order, LocalDateTime exitDate) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET ExitDate = ? WHERE (OrderId = ?);");
			stmt.setString(1, exitDate.toString());
			stmt.setInt(2, order.getOrderId());
			int rs = stmt.executeUpdate();
			// if the query ran successfully, but returned as empty table.
			if (rs == 0) {
				return ServerResponse.Query_Failed;
			}
			return ServerResponse.Order_ExitDate_Updated;

		} catch (SQLException ex) {
			return ServerResponse.Exception_Was_Thrown;
		}
	}


	// NOTICE : NOT USED THAT QUERY!!
	public int returnTotalPreOrdersWithStatus(OrderStatusEnum status) {
		int ordersCount = 0;

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con
					.prepareStatement("SELECT COUNT(*) AS orderCount FROM preorders WHERE OrderStatus = ?");
			stmt.setString(1, status.toString());
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return ordersCount;
			}

			ordersCount = rs.getInt("orderCount");
			return ordersCount;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ordersCount;
	}
	
	/**
	 * Retrieves a list of orders that have been notified or are in the notified waiting list for a specific client.
	 * This method is useful for fetching orders that might require action or acknowledgment from the client's side.
	 *
	 * @param customerId The ID of the customer whose notified orders are to be retrieved.
	 * @return An ArrayList of Order objects that have been either notified or are in the notified waiting list for the specified customer. Returns null if an SQLException occurs or if there are no such orders.
	 */
	public ArrayList<Order> searchForNotifiedOrdersOfSpecificClient(String customerId) {
		ArrayList<Order> retList = new ArrayList<Order>();

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT OrderId,ParkId,EnterDate,PayStatus,Amount,OrderStatus FROM preorders WHERE (OrderStatus = 'Notified' OR OrderStatus = 'Notified Waiting List') AND OwnerId = ?");
			stmt.setString(1, customerId);

			ResultSet rs = stmt.executeQuery();

			if (!rs.next())
				return null;
			rs.previous();

			while (rs.next()) {
				Order orderToAdd = new Order();
				orderToAdd.setOrderId(rs.getInt(1));
				orderToAdd.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
				orderToAdd.setEnterDate(rs.getTimestamp(3).toLocalDateTime());
				orderToAdd.setPaid((rs.getInt(4) == 1 ? true : false));
				orderToAdd.setNumberOfVisitors(rs.getInt(5));
				orderToAdd.setStatus(OrderStatusEnum.fromString(rs.getString(6)));
				retList.add(orderToAdd);
			}

			return retList;

		} catch (SQLException ex) {
			return null;
		}
	}
	
	/**
	 * Notifies the next orders in the waiting list for a specific park and enter date. This method is typically called
	 * when there is a cancellation, and spots open up in the park, allowing waiting list orders to be potentially moved to confirmed status.
	 *
	 * @param enterDate The enter date for which the waiting list orders are being notified.
	 * @param parkId The ID of the park where the waiting list orders are placed.
	 * @return An ArrayList of Order objects that are next in line on the waiting list for the specified enter date and park. Returns null if an SQLException occurs or if there are no orders in the waiting list.
	 */
	public ArrayList<Order> notifyTheNextOrdersInWaitingList(LocalDateTime enterDate, int parkId) {
		ArrayList<Order> ordersInWaitingList = new ArrayList<Order>();
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con
					.prepareStatement("SELECT p.OrderId, p.ParkId, w.enterListTime, p.EnterDate,p.Amount "
							+ "FROM preorders p " + "JOIN waitinglist w ON p.OrderId = w.orderId "
							+ "WHERE p.ParkId = ? AND p.EnterDate = ? AND p.OrderStatus = 'In Waiting List'"
							+ "ORDER BY w.enterListTime");

			stmt.setInt(1, parkId);
			stmt.setString(2, enterDate.toString());
			ResultSet rs = stmt.executeQuery();

			if (!rs.next())
				return null;

			rs.previous();
			while (rs.next()) {
				Order orderToAdd = new Order();
				orderToAdd.setOrderId(rs.getInt(1));
				orderToAdd.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
				orderToAdd.setEnterDate(rs.getTimestamp(4).toLocalDateTime());
				orderToAdd.setNumberOfVisitors(rs.getInt(5));
				ordersInWaitingList.add(orderToAdd);
			}

			return ordersInWaitingList;

		} catch (SQLException ex) {
			return null;
		}
	}
	
	/**
	 * Retrieves all orders, both from preorders and occasional visits, that are scheduled for today at a specific park.
	 * This method is useful for park management to get a quick overview of all the expected visits for the current day.
	 *
	 * @param parkId The ID of the park for which the orders are to be retrieved.
	 * @return An ArrayList of Order objects that are scheduled for today for the specified park. Returns an empty list if there are no orders for today or null if an SQLException occurs.
	 */
	public ArrayList<Order> importAllOrdersForToday(int parkId) {
		ArrayList<Order> retList = new ArrayList<Order>();

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement(
					"SELECT OrderId AS orderId, 1 AS isPaid, Amount AS amountOfVisitors, Phone AS ownerPhone, EnterDate AS EnterTime, ExitDate AS ExitTime, OrderStatus, OrderType "
							+ "FROM occasionalvisits "
							+ "WHERE DATE(EnterDate) = CURDATE() AND (OrderStatus = 'Confirmed' OR OrderStatus = 'In Park') AND ParkId = ? "
							+ "UNION ALL "
							+ "SELECT OrderId, PayStatus AS isPaid, Amount AS amountOfVisitors, Phone AS ownerPhone, EnterDate AS EnterTime, ExitDate AS ExitTime, OrderStatus, OrderType "
							+ "FROM preorders "
							+ "WHERE DATE(EnterDate) = CURDATE() AND (OrderStatus = 'Confirmed' OR OrderStatus = 'In Park') AND ParkId = ?");
			stmt.setInt(1, parkId);
			stmt.setInt(2, parkId);

			ResultSet rs = stmt.executeQuery();
			if (!rs.next())
				return retList;

			rs.previous();

			while (rs.next()) {
				Order orderToAdd = new Order();
				orderToAdd.setOrderId(rs.getInt(1));
				orderToAdd.setPaid(rs.getBoolean(2));
				orderToAdd.setNumberOfVisitors(rs.getInt(3));
				orderToAdd.setTelephoneNumber(rs.getString(4));
				orderToAdd.setEnterDate(rs.getTimestamp(5).toLocalDateTime());
				orderToAdd.setExitDate(rs.getTimestamp(6).toLocalDateTime());
				orderToAdd.setStatus(OrderStatusEnum.fromString(rs.getString(7)));
				orderToAdd.setOrderType(OrderTypeEnum.fromString(rs.getString(8)));
				retList.add(orderToAdd);
			}

			return retList;

		} catch (SQLException ex) {
			return null;
		}
	}
	
	/**
	 * Deletes a specific order from the preorders table in the database. This operation is irreversible and should
	 * be used with caution, typically in scenarios where an order is cancelled or needs to be removed for some reason.
	 *
	 * @param order The Order object containing the ID of the order to be deleted.
	 * @return true if the order was successfully deleted; false if the deletion failed, either because the order does not exist or due to an SQLException.
	 */
	public boolean deleteOrderFromTable(Order order) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("DELETE FROM preorders WHERE OrderId = ?");

			stmt.setInt(1, order.getOrderId());

			int rs = stmt.executeUpdate();
			if (rs == 0)
				return false;

			return true;

		} catch (SQLException ex) {
			return false;
		}
	}

}