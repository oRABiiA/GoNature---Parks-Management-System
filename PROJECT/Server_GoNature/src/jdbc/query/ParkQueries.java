package jdbc.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;

import jdbc.MySqlConnection;
import jdbc.QueryType;
import logic.Park;
import logic.Request;
import utils.enums.ParkNameEnum;
import utils.enums.ServerResponse;

/**
 * Handles database operations related to parks within the system.
 * This class provides methods for retrieving and updating park information,
 * including fetching park details by ID, updating park parameters based on approved requests,
 * and managing the fullness status of parks on specific dates. It operates by executing SQL queries
 * against a MySQL database, utilizing the MySqlConnection class to establish connections.
 * 
 * Methods in this class include operations such as:
 * - Retrieving park details by park ID or park name.
 * - Updating park parameters like maximum capacity, estimated stay time, and the number of reserved spots
 *   based on requests.
 * - Inserting and updating records in the parkfulldates table to track when parks are fully booked.
 * 
 * This class is essential for maintaining accurate and up-to-date information about parks,
 * which is critical for both administrative and operational functionalities of the system.
 * Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */

public class ParkQueries {
	
	/**
	 * Retrieves the details of a park based on its ID and updates the provided Park object with these details.
	 * This method searches for a park in the database using its unique ID. If found, it updates the Park object
	 * with information such as park name, maximum capacity, estimated stay time, estimated reserved spots,
	 * current number of visitors in the park, and the price.
	 *
	 * @param park The Park object to be updated with the retrieved details. This object must have its parkId set.
	 * @return true if the park details were successfully retrieved and the Park object was updated; false if the park
	 *         could not be found or if an SQL exception occurred.
	 */
	public boolean getParkById(Park park) {
		
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM parks WHERE ParkId = ?");
			stmt.setInt(1, park.getParkId());
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return false;
			}
			
			park.setParkId(rs.getInt(1));
			park.setParkName(ParkNameEnum.fromParkId(rs.getInt(1)));
			park.setCurrentMaxCapacity(rs.getInt(3));
			park.setCurrentEstimatedStayTime(rs.getInt(4));
			park.setCurrentEstimatedReservedSpots(rs.getInt(5));
			park.setCurrentInPark(rs.getInt(6));
			park.setPrice(rs.getInt(7));
			
			return true;
			
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for park failed");
			return false;
		}
	}
	
	//NOTICE : NOT USED THAT QUERY!!
	/**
	 * Retrieves park details by park name from the database.
	 * 
	 * @param park The Park object containing the name of the park for which details are being fetched.
	 * @return A ServerResponse indicating the outcome of the operation. Possible responses include
	 * Fetched_Park_Details_Successfully if the park details are retrieved successfully,
	 * Fetched_Park_Details_Failed if the park does not exist, and Query_Failed if an SQL exception occurs.
	 */
	public ServerResponse getParkByName(Park park) {
		
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM parks WHERE ParkName = ?");
			stmt.setInt(1, park.getParkName().getParkId());
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return ServerResponse.Fetched_Park_Details_Failed;
			}
			
			park.setParkId(rs.getInt(1));
			park.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
			park.setCurrentMaxCapacity(rs.getInt(3));
			park.setCurrentEstimatedStayTime(rs.getInt(4));
			park.setCurrentEstimatedReservedSpots(rs.getInt(5));
			park.setCurrentInPark(rs.getInt(6));
			park.setPrice(rs.getInt(7));
			
			return ServerResponse.Fetched_Park_Details_Successfully;
			
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for park failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	//NOTICE : NOT USED THAT QUERY!!
	/**
	 * Retrieves a list of park names from the database.
	 * 
	 * @param parkList An ArrayList of ParkNameEnum to be populated with the names of the parks.
	 * @return A ServerResponse indicating the outcome of the operation. Possible responses include
	 * Park_List_Names_Is_Created if the park names are successfully retrieved and populated into parkList,
	 * Park_Table_Is_Empty if no parks exist in the database, and Query_Failed if an SQL exception occurs.
	 */
	public ServerResponse getParksNames(ArrayList<ParkNameEnum> parkList) {
		
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT ParkName FROM parks");
			
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return ServerResponse.Park_Table_Is_Empty;
			}
			
			while(rs.next())
			{
				parkList.add(ParkNameEnum.fromParkId(rs.getInt(2)));
			}
			
			return ServerResponse.Park_List_Names_Is_Created;
			
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for park failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	
	//NOTICE : NOT USED THAT QUERY!!
	/**
	 * Fetches the current price for a given park.
	 * 
	 * @param park The Park object containing the name of the park for which the price is being queried.
	 * @return A ServerResponse indicating the outcome of the operation. Possible responses include
	 * Park_Price_Returned_Successfully if the park price is successfully retrieved,
	 * Fetched_Park_Details_Failed if the park does not exist, and Query_Failed if an SQL exception occurs.
	 */
	public ServerResponse returnParkPrice(Park park)
	{
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT PRICE FROM parks WHERE ParkName = ?");
			stmt.setInt(1, park.getParkName().getParkId());
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return ServerResponse.Fetched_Park_Details_Failed;
			}
			
			park.setParkId(rs.getInt(1));
			park.setParkName(ParkNameEnum.fromParkId(rs.getInt(2)));
			park.setCurrentMaxCapacity(rs.getInt(3));
			park.setCurrentEstimatedStayTime(rs.getInt(4));
			park.setCurrentEstimatedReservedSpots(rs.getInt(5));
			park.setCurrentInPark(rs.getInt(6));
			park.setPrice(rs.getInt(7));
			
			return ServerResponse.Park_Price_Returned_Successfully;
			
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for park failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	/**
	 * Updates a specific field of a park in the database based on a request. The field to be updated is determined
	 * by the type of the request, and the new value is applied as specified in the request. This method is typically
	 * used to process approved requests that modify park parameters such as maximum capacity, estimated stay time,
	 * or the number of reserved spots.
	 *
	 * @param request The Request object containing details about the update, including the park ID, the new value,
	 *                and the type of request which determines the field to be updated.
	 * @return ServerResponse.Updated_Requests_Successfully if the update was successful, 
	 *         ServerResponse.Fetched_Park_Details_Failed if the update failed due to the park not being found,
	 *         or ServerResponse.Query_Failed if an SQL exception occurred during the operation.
	 */
	public ServerResponse InsertNewValueInRequestedPark(Request request)
	{
		try {

			String columnName = request.getRequestType().getValue(); // to get the field we want to update

			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE parks SET " + columnName + " = ? WHERE ParkId = ?");

			stmt.setInt(1, request.getNewValue());
			stmt.setInt(2, request.getParkId());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs==0) {
				return ServerResponse.Fetched_Park_Details_Failed;
			}
			return ServerResponse.Updated_Requests_Successfully;

		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for park failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	/**
	 * Retrieves the current capacity and the number of visitors currently in the park.
	 * @param parkId The ID of the park.
	 * @return An array containing two integers: the first is the maximum capacity of the park, and the second is the current number of visitors in the park. Returns null if an error occurs.
	 */
	public int[] returnCapacityCurrentInParkForPark(int parkId) {
		int[] values = new int[2];

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement selectStmt = con
					.prepareStatement("SELECT MaxCapacity, CurrentInPark From parks WHERE ParkId = ?; ");
			selectStmt.setInt(1, parkId);
			ResultSet rs = selectStmt.executeQuery();

			if (!rs.next()) {
				return null;
			}
			values[0] = rs.getInt("MaxCapacity");
			values[1] = rs.getInt("CurrentInPark");
			return values;

		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}

	}
	
	/**
	 * Updates the park full dates table based on the operation specified. It can insert a new full date or update an existing one.
	 * @param operation The type of operation to perform (Insert or Update).
	 * @param date The date to be updated or inserted as full.
	 * @param parkName The name of the park.
	 * @return true if the operation is successful, false if an error occurs.
	 */
	public boolean updateParkFullDateTable(QueryType operation, LocalDate date, String parkName) {

		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			if (operation.name().equals("Insert")) {
				PreparedStatement insertStmt = con
						.prepareStatement("INSERT INTO parkfulldates (Date, ?) VALUES (?, 1);");
				insertStmt.setString(1, parkName);
				insertStmt.setString(2, date.toString());
				int insertRS = insertStmt.executeUpdate();
				if (insertRS == 0) {
					return false;
				}
				return true;
			} else if (operation.name().equals("Update")) {
				PreparedStatement updateStmt = con.prepareStatement("UPDATE parkfulldates SET ? = 1 WHERE (Date = ?);");
				updateStmt.setString(1, parkName);
				updateStmt.setString(2, date.toString());
				int updateRS = updateStmt.executeUpdate();
				if (updateRS == 0) {
					return false;
				}
			}
			return true;

		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}
	
	
//	/**
//	 * @param order
//	 * @param direction - if true means the customer is entering the park, false
//	 *                  means customer is exiting the park
//	 * @return
//	 */
//	
//	//NOTICE : NOT USED THAT QUERY!!
//	public ServerResponse updateCurrentInParkValue(Order order, boolean direction) {
//		try {
//			Connection con = MySqlConnection.getInstance().getConnection();
//			PreparedStatement selectStmt = con.prepareStatement("SELECT CurrentInPark FROM parks WHERE (ParkId = ?);");
//			selectStmt.setInt(1, order.getParkName().getParkId());
//			ResultSet rs = selectStmt.executeQuery();
//
//			if (!rs.next()) {
//				return ServerResponse.Such_Park_Does_Not_Exists;
//			}
//
//			rs.previous();
//			int currentInParkUpdated = rs.getInt("CurrentInPark");
//			if (direction) {
//				currentInParkUpdated += order.getNumberOfVisitors();
//				QueryControl.orderQueries.updateOrderStatus(order, OrderStatusEnum.In_Park); // update the order to be in park
//			} else {
//				currentInParkUpdated -= order.getNumberOfVisitors();
//				QueryControl.orderQueries.updateOrderStatus(order, OrderStatusEnum.Completed); // update the order to be completed
//			}
//			selectStmt.close();
//			rs.close();
//			PreparedStatement updateStmt = con.prepareStatement("UPDATE parks SET CurrentInPark = ? WHERE ParkId = ?;");
//			updateStmt.setInt(1, currentInParkUpdated);
//			int updateRS = updateStmt.executeUpdate();
//
//			if (updateRS == 0) {
//				return DatabaseResponse.Current_In_Park_Update_Failed;
//			}
//			return DatabaseResponse.Current_In_Park_Updated_Successfully;
//
//		} catch (SQLException ex) {
//			ex.printStackTrace();
//			return DatabaseResponse.Failed;
//		}
//	}
	

}