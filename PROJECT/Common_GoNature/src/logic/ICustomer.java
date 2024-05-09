package logic;

import utils.enums.UserTypeEnum;

/**
 * The ICustomer interface represents a customer entity with basic information
 * such as name, contact details, and user type. It defines methods to get and
 * set various properties of a customer.
 */
public interface ICustomer {

	/**
	 * Gets the first name of the customer.
	 * 
	 * @return The first name as a String.
	 */
	String getFirstName();

	/**
	 * Sets the first name of the customer.
	 * 
	 * @param firstName The first name to set.
	 */
	void setFirstName(String firstName);

	/**
	 * Gets the last name of the customer.
	 * 
	 * @return The last name as a String.
	 */
	String getLastName();

	/**
	 * Sets the last name of the customer.
	 * 
	 * @param lastName The last name to set.
	 */
	void setLastName(String lastName);

	/**
	 * Gets the user type of the customer.
	 * 
	 * @return The user type as a {@link UserTypeEnum}.
	 */
	UserTypeEnum getUserType();

	/**
	 * Sets the user type of the customer.
	 * 
	 * @param type The user type to set as a {@link UserTypeEnum}.
	 */
	void setUserType(UserTypeEnum type);

	/**
	 * Gets the phone number of the customer.
	 * 
	 * @return The phone number as a String.
	 */
	String getPhoneNumber();

	/**
	 * Sets the phone number of the customer.
	 * 
	 * @param phoneNumber The phone number to set.
	 */
	void setPhoneNumber(String phoneNumber);

	/**
	 * Gets the email address of the customer.
	 * 
	 * @return The email address as a String.
	 */
	String getEmailAddress();

	/**
	 * Sets the email address of the customer.
	 * 
	 * @param emailAddress The email address to set.
	 */
	void setEmailAddress(String emailAddress);

	/**
	 * Gets the customer ID.
	 * 
	 * @return The customer ID as a String.
	 */
	String getCustomerId();

	/**
	 * Sets the customer ID.
	 * 
	 * @param id The customer ID to set.
	 */
	void setCustomerId(String id);
}
