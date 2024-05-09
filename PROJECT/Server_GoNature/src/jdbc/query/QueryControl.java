package jdbc.query;


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
 * The DB Controller class is the way our application "talk" with the database.
 * This class use "mysql-connector-java-8.0.13".
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */
public class QueryControl {
	
	
	public static OrderQueries orderQueries = new OrderQueries();
	public static CustomerQueries customerQueries = new CustomerQueries();
	public static EmployeeQueries employeeQueries = new EmployeeQueries();
	public static ParkQueries parkQueries = new ParkQueries();
	public static ReportsQueries reportsQueries = new ReportsQueries();
	public static RequestQueries requestsQueries = new RequestQueries();
	public static NotificationQueries notificationQueries = new NotificationQueries();
	public static OccasionalQueries occasionalQueries = new OccasionalQueries();
	
	
	public QueryControl() {
	}
	
}
