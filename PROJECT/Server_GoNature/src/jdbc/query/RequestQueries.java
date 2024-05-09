package jdbc.query;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import jdbc.MySqlConnection;
import logic.Request;
import utils.enums.RequestStatusEnum;
import utils.enums.RequestTypeEnum;
import utils.enums.ServerResponse;

/**
 * Handles database queries related to requests, including fetching, updating, and inserting new requests.
 * This class is primarily used for managing park manager requests, such as changes in park parameters or settings.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */
public class RequestQueries {
	private ParkQueries parkQueries= new ParkQueries();
	
	/**
	 * Retrieves all pending requests from the database. This method is intended for park managers to view
	 * and act on requests that are currently awaiting approval.
	 *
	 * @param request An empty {@link ArrayList} of {@link Request} that will be filled with the found requests.
	 * @return {@link ServerResponse} indicating the outcome of the fetch operation, whether it was successful,
	 *         there are no pending requests, or the query failed.
	 */
	public ServerResponse ShowAllParkManagerRequests(ArrayList<Request> request) //Method to pull all the requests with pending status. (Tamir/Siso)
	{
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("SELECT * FROM requests WHERE RequestStatus = 'Pending'");

			ResultSet rs = stmt.executeQuery();

			// if the query ran successfully, but returned as empty table.
			if (!rs.first()) {
				return ServerResponse.There_Are_Not_Pending_Requests;
			}
			
			rs.previous();
			while (rs.next()) {

	            Request newRequest = new Request();
	            
	            newRequest.setRequestId(rs.getInt(1)); 
	            newRequest.setParkId(rs.getInt(2));
	            newRequest.setRequestType(RequestTypeEnum.fromString(rs.getString(3)));
	            newRequest.setOldValue(rs.getInt(4));
	            newRequest.setNewValue(rs.getInt(5));
	            newRequest.setRequestStatus(RequestStatusEnum.fromString(rs.getString(6)));
	            newRequest.setRequestDate(rs.getTimestamp(7).toLocalDateTime());

	            request.add(newRequest);
	        }
			
			return ServerResponse.Pending_Requests_Found_Successfully;
			
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for user failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	/**
	 * Updates the status of a specific request in the database to a new status provided by the caller. If the new status
	 * is 'Approved', it also triggers the update of the relevant park parameter to the new value specified in the request.
	 *
	 * @param request The {@link Request} object containing the request details, including the request ID.
	 * @param status The new status to be set for the request, typically 'Approved' or 'Denied'.
	 * @return {@link ServerResponse} indicating whether the update was successful, failed, or if the query resulted in no changes.
	 */
	public ServerResponse UpdateStatusRequest(Request request,String status)
	{
		try {
			Connection con = MySqlConnection.getInstance().getConnection();
			PreparedStatement stmt = con.prepareStatement("UPDATE requests SET RequestStatus = ? WHERE RequestId = ?");
			stmt.setString(1, status);
			stmt.setInt(2,request.getRequestId());
			int rs = stmt.executeUpdate();

			// if the query ran successfully, but returned as empty table.
			if (rs==0) {
				return ServerResponse.Updated_Requests_Failed;
			}
			
			if(status.equals("Approved")) {
				parkQueries.InsertNewValueInRequestedPark(request);
			}

			return ServerResponse.Updated_Requests_Successfully;
			
		} catch (SQLException ex) {
//			serverController.printToLogConsole("Query search for user failed");
			return ServerResponse.Query_Failed;
		}
	}
	
	/**
	 * Inserts a new request into the database, provided there's no existing pending request of the same type for the same park.
	 * This method ensures that duplicate requests are not created while a similar request is still under consideration.
	 *
	 * @param request The {@link Request} object to be inserted, containing all necessary details such as park ID, request type,
	 *                old and new values, request status, and request date.
	 * @return A boolean value indicating the success of the insert operation. Returns {@code true} if the insert was successful,
	 *         or {@code false} if a matching pending request already exists or if the insert operation failed.
	 */
	public boolean InsertNewRequest(Request request) {
	    try {
	        Connection con = MySqlConnection.getInstance().getConnection();

	        // Check if a pending request of the same type for the same park exists
	        String checkSql = "SELECT 1 FROM requests WHERE ParkId = ? AND RequestType = ? AND RequestStatus = 'Pending'";
	        PreparedStatement checkStmt = con.prepareStatement(checkSql);
	        checkStmt.setInt(1, request.getParkId());
	        checkStmt.setString(2, request.getRequestType().name());
	        ResultSet checkRs = checkStmt.executeQuery();
	        if (checkRs.next()) {
	            // A matching pending request exists, so do not insert a new one
	            return false;
	        }

	        // No matching pending request exists, proceed with insert
	        String insertSql = "INSERT INTO requests (ParkId, RequestType, OldValue, NewValue, RequestStatus, RequestDate) VALUES (?, ?, ?, ?, ?, ?)";
	        PreparedStatement stmt = con.prepareStatement(insertSql);
	        stmt.setInt(1, request.getParkId());
	        stmt.setString(2, request.getRequestType().name());
	        stmt.setInt(3, request.getOldValue());
	        stmt.setInt(4, request.getNewValue());
	        stmt.setString(5, request.getRequestStatus().name());
	        
	        // Convert LocalDateTime to Timestamp
	        Timestamp requestDateTimestamp = Timestamp.valueOf(request.getRequestDate());
	        stmt.setTimestamp(6, requestDateTimestamp);

	        int rs = stmt.executeUpdate();

	        // if the query ran successfully, but returned as empty table.
	        if (rs == 0) {
	            return false;
	        }

	        return true;
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        return false;
	    }
	}
	
}
	