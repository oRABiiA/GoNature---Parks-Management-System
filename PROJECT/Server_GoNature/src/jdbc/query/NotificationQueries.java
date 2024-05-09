package jdbc.query;

import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import jdbc.MySqlConnection;
import logic.Order;
import utils.enums.ParkNameEnum;
import utils.enums.UserTypeEnum;

/**
 * This class handles database operations related to notifications for orders.
 * It includes methods for checking and updating order statuses based on notification
 * logic, such as automatic cancellation or marking orders as notified or irrelevant.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */

public class NotificationQueries {
	
	/**
	 * Checks all orders with a 'Notified' status and updates them to 'Cancelled' if the current
	 * date and time surpass the order's enter date. This method is intended to automate
	 * the cancellation process for unconfirmed notified orders.
	 *
	 * @param localDateTime The current local date and time to compare order enter dates against.
	 * @return A list of orders that were automatically cancelled due to being past the enter date.
	 */
	public ArrayList<Order> CheckAllOrdersAndChangeToCancelledIfNeeded(LocalDateTime localDateTime)
	{
		ArrayList<Order> cancelledOrders = new ArrayList<Order>();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
	    String dateTimeString = localDateTime.format(formatter);
		
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT OrderId,ParkId,OwnerId,OwnerType,Email,Phone,FirstName,LastName,Amount,EnterDate FROM preorders WHERE OrderStatus = 'Notified' AND EnterDate <= ?");
			
			stmt.setString(1, dateTimeString);
			ResultSet rs= stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return null;
			}
			
			rs.previous();
			
			while(rs.next()) {
				Order orderToAdd = new Order();
				orderToAdd.setOrderId(rs.getInt(1));
				orderToAdd.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
				orderToAdd.setUserId(String.valueOf(rs.getInt(3)));
				orderToAdd.setOwnerType(UserTypeEnum.fromString(rs.getString(4)));
				orderToAdd.setEmail(rs.getString(5));
				orderToAdd.setTelephoneNumber(rs.getString(6));
				orderToAdd.setFirstName(rs.getString(7));
				orderToAdd.setLastName(rs.getString(8));
				orderToAdd.setNumberOfVisitors(rs.getInt(9));
				orderToAdd.setEnterDate(rs.getTimestamp(10).toLocalDateTime());
				cancelledOrders.add(orderToAdd);
			}
			
			return cancelledOrders;
	
		} catch (SQLException ex) 
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Automatically updates the status of a specified order to 'Cancelled'.
	 * This method is typically used to cancel orders that have not been confirmed by the user
	 * within a specific timeframe after being notified.
	 *
	 * @param order The order to be cancelled.
	 */
	public void automaticallyCancelAllNotifiedOrders(Order order) {
		
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET OrderStatus = 'Cancelled' WHERE OrderId = ?");
			
			stmt.setInt(1, order.getOrderId());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs==0) {
				return;
			}
	
		} catch (SQLException ex) 
		{
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Marks a specified order as 'Irrelevant'. This status change is typically used
	 * for orders that no longer require action or attention, for instance, when an order
	 * is outdated or has been superseded by another order.
	 *
	 * @param order The order to be marked as irrelevant.
	 */
	public void automaticallyMarkOrdersAsIrrelevant(Order order) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET OrderStatus = 'Irrelevant' WHERE OrderId = ?");
			
			stmt.setInt(1, order.getOrderId());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs==0) {
				return;
			}
	
		} catch (SQLException ex) 
		{
			ex.printStackTrace();
			return;
		}
	}
	
	/**
	 * Checks orders with a 'Wait Notify' status to see if their enter date matches the current
	 * date and time, and updates their status to 'Notified' accordingly. This method facilitates
	 * the process of notifying users about their upcoming orders.
	 *
	 * @param localDateTime The current local date and time used to check against order enter dates.
	 * @return A list of orders that have been updated to 'Notified' status.
	 */
	public ArrayList<Order> CheckAllOrdersAndChangeToNotifedfNeeded(LocalDateTime localDateTime)
	{
		Connection con = MySqlConnection.getInstance().getConnection();

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
		String dateTimeString = localDateTime.format(formatter);
		try {
			ArrayList<Order> notifiedOrders = new ArrayList<Order>();
			PreparedStatement stmt = con.prepareStatement("SELECT OrderId,ParkId,OwnerId,OwnerType,Email,Phone,FirstName,LastName,Amount FROM preorders WHERE OrderStatus = 'Wait Notify' AND EnterDate = ?");
			stmt.setString(1, dateTimeString);
			ResultSet rs = stmt.executeQuery();
			
			if(!rs.next())
				return null;
			
			rs.previous();
			while(rs.next()) {
				Order orderToAdd = new Order();
				orderToAdd.setOrderId(rs.getInt(1));
				orderToAdd.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
				orderToAdd.setUserId(String.valueOf(rs.getInt(3)));
				orderToAdd.setOwnerType(UserTypeEnum.fromString(rs.getString(4)));
				orderToAdd.setEmail(rs.getString(5));
				orderToAdd.setTelephoneNumber(rs.getString(6));
				orderToAdd.setFirstName(rs.getString(7));
				orderToAdd.setLastName(rs.getString(8));
				orderToAdd.setNumberOfVisitors(rs.getInt(9));
				notifiedOrders.add(orderToAdd);
			}
			
			return notifiedOrders;
			
		} catch (SQLException ex) 
		{
			ex.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Automatically cancels orders that have been in a 'Notified Waiting List' status
	 * for a specific period (e.g., 2 hours) without confirmation from the user. This method
	 * ensures that unconfirmed orders do not indefinitely occupy space in the waiting list.
	 *
	 * @return A list of orders that were automatically cancelled due to lack of confirmation.
	 */
	public ArrayList<Order> CheckAllWaitingListOrdersAndCancelAutomaticallyIfNotConfirmed()
	{
		ArrayList<Order> cancelledOrders = new ArrayList<Order>();
		
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT p.OrderId, p.ParkId, p.OwnerId, p.OwnerType, p.Email, p.Phone, p.FirstName, p.LastName, p.Amount, p.EnterDate"
					+ " FROM preorders AS p"
					+ " JOIN waitinglist AS w ON p.OrderId = w.orderId"
					+ " WHERE p.OrderStatus = 'Notified Waiting List'"
					+ " AND w.notificationSentTime <= CURRENT_TIMESTAMP - INTERVAL '2' HOUR"
					+ " AND w.notificationSentTime > CURRENT_TIMESTAMP - INTERVAL '2' HOUR - INTERVAL '1' MINUTE");
			
			ResultSet rs= stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return null;
			}
			
			rs.previous();
			
			while(rs.next()) {
				Order orderToAdd = new Order();
				orderToAdd.setOrderId(rs.getInt(1));
				orderToAdd.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
				orderToAdd.setUserId(String.valueOf(rs.getInt(3)));
				orderToAdd.setOwnerType(UserTypeEnum.fromString(rs.getString(4)));
				orderToAdd.setEmail(rs.getString(5));
				orderToAdd.setTelephoneNumber(rs.getString(6));
				orderToAdd.setFirstName(rs.getString(7));
				orderToAdd.setLastName(rs.getString(8));
				orderToAdd.setNumberOfVisitors(rs.getInt(9));
				orderToAdd.setEnterDate(rs.getTimestamp(10).toLocalDateTime());
				cancelledOrders.add(orderToAdd);
			}
			
			return cancelledOrders;
	
		} catch (SQLException ex) 
		{
			ex.printStackTrace();
			return null;
		}
		
	}
	
	/**
	 * Updates the status of specified orders from 'Wait Notify' to 'Notified'. 
	 * This is used to mark orders for which notifications should be sent out to the users,
	 * indicating that they need to take action (e.g., confirm or cancel the order).
	 *
	 * @param orderToUpdate The order whose status is to be updated.
	 */
	public void UpdateAllWaitNotifyOrdersToNotify(Order orderToUpdate) {

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE preorders SET OrderStatus = 'Notified' WHERE OrderId = ?");
			
			stmt.setInt(1, orderToUpdate.getOrderId());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs==0) {
				return;
			}
	
		} catch (SQLException ex) 
		{
			ex.printStackTrace();
			return;
		}
		
}
	
	/**
	 * Checks all orders in the waiting list and marks them as irrelevant if their
	 * enter date is earlier than the specified localDateTime. This cleanup operation
	 * ensures that the waiting list does not contain orders that are no longer valid.
	 *
	 * @param localDateTime The current local date and time used for comparison.
	 * @return A list of orders that were marked as irrelevant.
	 */
	public ArrayList<Order> CheckWaitingListAndRemoveAllIrrelcantOrders(LocalDateTime localDateTime)
	{   
		ArrayList<Order> irrelevantOrders = new ArrayList<Order>();
	    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:00");
	    String dateTimeString = localDateTime.format(formatter);
		
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT OrderId,ParkId,OwnerId,OwnerType,Email,Phone,FirstName,LastName,Amount FROM preorders WHERE (OrderStatus = 'In Waiting List' OR OrderStatus = 'Notified Waiting List') AND EnterDate < ?");
			
			stmt.setString(1, dateTimeString);
			ResultSet rs= stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return null;
			}
			
			rs.previous();
			
			while(rs.next()) {
				Order orderToAdd = new Order();
				orderToAdd.setOrderId(rs.getInt(1));
				orderToAdd.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
				orderToAdd.setUserId(String.valueOf(rs.getInt(3)));
				orderToAdd.setOwnerType(UserTypeEnum.fromString(rs.getString(4)));
				orderToAdd.setEmail(rs.getString(5));
				orderToAdd.setTelephoneNumber(rs.getString(6));
				orderToAdd.setFirstName(rs.getString(7));
				orderToAdd.setLastName(rs.getString(8));
				orderToAdd.setNumberOfVisitors(rs.getInt(9));
				irrelevantOrders.add(orderToAdd);
			}
			
			return irrelevantOrders;
	
		} catch (SQLException ex) 
		{
			ex.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Checks if a specific order with the given OrderId has been in a 'Notified' status
	 * for 24 hours. This method can be used to identify orders that may require further action
	 * such as cancellation or follow-up notification.
	 *
	 * @param OrderId The ID of the order to check.
	 * @return true if the order has been notified for 24 hours; false otherwise.
	 */
	//NOTICE : NOT USED THAT QUERY!!
	public boolean CheckNotifiedFromServer24Hours(int OrderId)
    {

        try {
            Connection con = MySqlConnection.getInstance().getConnection();
            PreparedStatement stmt = con.prepareStatement("SELECT OrderStatus WHERE OrderId=?");

            stmt.setInt(1, OrderId);
            ResultSet rs = stmt.executeQuery();

            // if the query ran successfully, but returned as empty table.
            if (!rs.next()) {
                return false;
            }else if(rs.getString(1).equals("Notified"))
                return true;
            return false;
        } catch (SQLException ex) 
        {
            ex.printStackTrace();
            return false;
        }

    }
	
	
}