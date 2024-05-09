package jdbc.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import jdbc.MySqlConnection;
import logic.Employee;
import logic.Guide;
import utils.enums.EmployeeTypeEnum;
import utils.enums.ParkNameEnum;
import utils.enums.ServerResponse;
import utils.enums.UserStatus;
import utils.enums.UserTypeEnum;

/**
 * This class is responsible for executing queries related to employees and guides
 * in the system. It handles operations such as searching for approved employees,
 * checking visitor order statuses, updating guide statuses, and retrieving lists
 * of guides with specific statuses.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */

public class EmployeeQueries {

	public EmployeeQueries() {
	}
	
	/**
	 * Searches for an employee by username and verifies their credentials. It updates
	 * the provided Employee object with additional details if the employee exists and
	 * is approved.
	 *
	 * @param employee An Employee object containing the username and password to be verified.
	 * @return A ServerResponse indicating the outcome of the search and verification process.
	 */
	public ServerResponse searchForApprovedEmployee(Employee employee) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE Username = ? AND UserType = 'Employee' ");
			stmt.setString(1, employee.getUsername());
			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.next()) {
				return ServerResponse.User_Does_Not_Found;
			}
			
			if(!employee.getPassword().equals(rs.getString(3))) {
				return ServerResponse.Password_Incorrect;
			}
			
			employee.setUserId(rs.getString(1));
			employee.setFirstName(rs.getString(4));
			employee.setLastName(rs.getString(5));
			employee.setPhoneNumber(rs.getString(6));
			employee.setEmailAddress(rs.getString(7));
			employee.setUserStatus(UserStatus.Approved);
			employee.setUserType(UserTypeEnum.Employee);
			employee.setRelatedPark(ParkNameEnum.fromParkId(rs.getInt(10)));
			employee.setEmployeeType(EmployeeTypeEnum.fromString(rs.getString(11).trim()));
			
			return ServerResponse.Employee_Connected_Successfully;
			
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for user failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	/**
	 * Checks if a visitor's order is paid and confirmed.
	 *
	 * @param orderId The ID of the order to check.
	 * @return A ServerResponse indicating whether the order is paid and confirmed, not paid,
	 *         not confirmed, or if the order was not found.
	 */
	//NOTICE : NOT USED THAT QUERY!!
	public ServerResponse checkIfVisitorPaidAndConfirmed(int orderId) {
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT PayStatus, OrderStatus FROM preorders WHERE orderId = ?");
			stmt.setInt(1, orderId);
			ResultSet rs = stmt.executeQuery();

			if(!rs.next()) {
				return ServerResponse.Order_Not_Found;
			}

			int isPaid = rs.getInt("PayStatus");
			String orderStatus = rs.getString("OrderStatus");

			if(orderStatus.equals("Confirmed")) {
				if(isPaid == 1) {
					return ServerResponse.Order_Paid_And_Confirmed;
				}
				return ServerResponse.Order_Not_Paid;
			}
			return ServerResponse.Order_Not_Confirmed;


		} catch (SQLException ex) {
			ex.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Updates the status of a guide from pending to approved.
	 *
	 * @param guide A Guide object containing the ID of the guide to be approved.
	 * @return A ServerResponse indicating whether the guide's status was successfully updated
	 *         to approved or if the operation failed.
	 */
	public ServerResponse UpdateGuideStatusToApprove(Guide guide) //Update guide permission from Pending to Approve (Tamir/Siso)
	{
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE users SET Status = 'Approved' WHERE UserId = ?");
			stmt.setString(1, guide.getUserId());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs==0) {
				return ServerResponse.Updated_Guides_To_Approved_Failed;
			}

			return ServerResponse.Updated_Guides_To_Approved_Successfully;

		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for user failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	/**
	 * Retrieves all guides with a status of 'Pending' and adds them to the provided list.
	 *
	 * @param guideList An ArrayList of Guide objects to be populated with the retrieved guides.
	 * @return A ServerResponse indicating whether guides with a pending status were found
	 *         and added to the list.
	 */
	public ServerResponse ShowAllGuidesWithPendingStatus(ArrayList<Guide> guideList) //Method to pull all the requests with pending status. (Tamir/Siso)
	{
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM users WHERE Status = 'Pending'");

			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.first()) {
				return ServerResponse.Guides_With_Status_Pending_Not_Found;
			}

			rs.previous();
			while (rs.next()) {

	            Guide guide = new Guide();

	            guide.setUserId(rs.getString(1));
	            guide.setUsername(rs.getString(2));
	            guide.setPassword(rs.getString(3));
	            guide.setFirstName(rs.getString(4));
	            guide.setLastName(rs.getString(5));
	            guide.setPhoneNumber(rs.getString(6));
	            guide.setEmailAddress(rs.getString(7));

	            String statusStr = rs.getString(8); // Use column name or index as appropriate
	            UserStatus status = UserStatus.fromString(statusStr); // This returns a UserStatus enum
	            guide.setUserStatus(status);

	            String userType = rs.getString(9);
	            UserTypeEnum type = UserTypeEnum.fromString(userType);
	            guide.setUserType(type);

	            guideList.add(guide);
	        }

			return ServerResponse.Guides_With_Status_Pending_Found;

		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for user failed");
			return ServerResponse.Query_Failed;
		}
	}

}
