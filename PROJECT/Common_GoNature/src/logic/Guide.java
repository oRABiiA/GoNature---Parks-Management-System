package logic;

import utils.enums.UserStatus;
import utils.enums.UserTypeEnum;

/**
 * The Guide class represents a user who is also a guide. It extends the User
 * class and implements the ICustomer interface.
 */
public class Guide extends User implements ICustomer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -469349474445294552L;

	/**
	 * Constructs a new Guide object.
	 */
	public Guide() {
	}

	/**
	 * Constructs a new Guide object with the specified user ID.
	 * 
	 * @param userId The user ID
	 */
	public Guide(String userId) {
		super(userId);
		userType = UserTypeEnum.Guide;
	}

	/**
	 * Constructs a new Guide object with the specified parameters.
	 * 
	 * @param userId       The user ID
	 * @param username     The username
	 * @param password     The password
	 * @param firstName    The first name
	 * @param lastName     The last name
	 * @param phoneNumber  The phone number
	 * @param emailAddress The email address
	 */
	public Guide(String userId, String username, String password, String firstName, String lastName, String phoneNumber,
			String emailAddress) {
		super(userId, username, password, firstName, lastName, phoneNumber, emailAddress);
		userType = UserTypeEnum.Guide;
	}

	/**
	 * Constructs a new Guide object with the specified username and password.
	 * 
	 * @param username The username
	 * @param password The password
	 */
	public Guide(String username, String password) {
		super(username, password);
		userType = UserTypeEnum.Guide;
		userStatus = UserStatus.Approved;
	}

	/**
	 * Returns the customer ID, which is the same as the user ID for a guide.
	 * 
	 * @return The customer ID
	 */
	@Override
	public String getCustomerId() {
		return userId;
	}

	/**
	 * Sets the customer ID.
	 * 
	 * @param id The customer ID to set
	 */
	@Override
	public void setCustomerId(String id) {
		userId = id;
	}

}
