package logic;

import java.io.Serializable;

import utils.enums.UserStatus;
import utils.enums.UserTypeEnum;

/**
 * Represents a user in the system, extending the ExternalUser class and
 * implementing Serializable.
 */
public class User extends ExternalUser implements Serializable {

	/** The serial version UID for serialization. */
	private static final long serialVersionUID = 3069940714226102732L;

	/** The unique identifier for the user. */
	protected String userId;

	/** The username of the user. */
	protected String username;

	/** The password of the user. */
	protected String password;

	/** The first name of the user. */
	protected String firstName;

	/** The last name of the user. */
	protected String lastName;

	/** The phone number of the user. */
	protected String phoneNumber;

	/** The email address of the user. */
	protected String emailAddress;

	/** The status of the user. */
	protected UserStatus userStatus = UserStatus.None;

	/**
	 * Constructs a new User object with the provided user details.
	 * 
	 * @param userId       The unique identifier of the user.
	 * @param username     The username of the user.
	 * @param password     The password of the user.
	 * @param firstName    The first name of the user.
	 * @param lastName     The last name of the user.
	 * @param phoneNumber  The phone number of the user.
	 * @param emailAddress The email address of the user.
	 */
	public User(String userId, String username, String password, String firstName, String lastName, String phoneNumber,
			String emailAddress) {
		super();
		this.userId = userId;
		this.username = username;
		this.password = password;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
	}

	/**
	 * Constructs a new User object with the provided user ID, username, and
	 * password.
	 * 
	 * @param userId   The unique identifier of the user.
	 * @param username The username of the user.
	 * @param password The password of the user.
	 */
	public User(String userId, String username, String password) {
		this.userId = userId;
		this.username = username;
		this.password = password;
	}

	/**
	 * Constructs a new User object with the provided user ID.
	 * 
	 * @param userId The unique identifier of the user.
	 */
	public User(String userId) {
		this.userId = userId;
	}

	/**
	 * Constructs a new User object with the provided username and password.
	 * 
	 * @param username The username of the user.
	 * @param password The password of the user.
	 */
	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * Constructs a new User object with the specified user type.
	 * 
	 * @param userType The type of the user.
	 */
	public User(UserTypeEnum userType) {
		this.userType = userType;
	}

	/** Default constructor for the User class. */
	public User() {
	}

	/**
	 * Gets the user ID.
	 * 
	 * @return The user ID.
	 */
	public String getUserId() {
		return userId;
	}

	/**
	 * Sets the user ID.
	 * 
	 * @param userId The user ID to set.
	 */
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	 * Gets the username.
	 * 
	 * @return The username.
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 * 
	 * @param username The username to set.
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 * 
	 * @return The password.
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * 
	 * @param password The password to set.
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the first name.
	 * 
	 * @return The first name.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name.
	 * 
	 * @param firstName The first name to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Gets the last name.
	 * 
	 * @return The last name.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name.
	 * 
	 * @param lastName The last name to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Gets the phone number.
	 * 
	 * @return The phone number.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the phone number.
	 * 
	 * @param phoneNumber The phone number to set.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Gets the email address.
	 * 
	 * @return The email address.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the email address.
	 * 
	 * @param emailAddress The email address to set.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Gets the user status.
	 * 
	 * @return The user status.
	 */
	public UserStatus getUserStatus() {
		return userStatus;
	}

	/**
	 * Sets the user status.
	 * 
	 * @param userStatus The user status to set.
	 */
	public void setUserStatus(UserStatus userStatus) {
		this.userStatus = userStatus;
	}

	/**
	 * Checks if this user is equal to another object. Equality is determined based
	 * on the username and user ID.
	 * 
	 * @param obj The object to compare with.
	 * @return {@code true} if the objects are equal, {@code false} otherwise.
	 */
	@Override
	public boolean equals(Object obj) {
		return this.username.equals(((User) obj).username) && this.userId.equals(((User) obj).userId);
	}

}
