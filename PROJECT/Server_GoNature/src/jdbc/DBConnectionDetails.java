package jdbc;


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
 * The DBConnectionDetails class is an entity of the required data in order to be able to connect the database.
 */
public class DBConnectionDetails {

	private String schemeName;
	private String dbUserName;
	private String dbPassword;
	
	public DBConnectionDetails(String name, String username, String password) {
		this.schemeName=name;
		this.dbUserName=username;
		this.dbPassword=password;
	}
	
	public DBConnectionDetails() {}
	
	public String getName() {
		return schemeName;
	}
	
	public void setName(String name) {
		this.schemeName=name;
	}
	
	
	public String getUsername() {
		return dbUserName;
	}
	
	public void setUsername(String username) {
		this.dbUserName=username;
	}
	
	public String getPassword() {
		return dbPassword;
	}
	
	public void setPassword(String password) {
		this.dbPassword=password;
	}
}
