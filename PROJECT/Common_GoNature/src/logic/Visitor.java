package logic;

import utils.enums.UserTypeEnum;

/**
 * Represents a visitor, extending the ExternalUser class and implementing the
 * ICustomer interface. It contains attributes and methods specific to a
 * visitor.
 */
public class Visitor extends ExternalUser implements ICustomer {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Unique identifier for the visitor.
	 */
	private String visitorId;

	/**
	 * Relevant order associated with the visitor.
	 */
	private Order relevantOrder;

	/**
	 * First name of the visitor.
	 */
	private String firstName;

	/**
	 * Last name of the visitor.
	 */
	private String lastName;

	/**
	 * Phone number of the visitor.
	 */
	private String phoneNumber;

	/**
	 * Email address of the visitor.
	 */
	private String emailAddress;

	/**
	 * Constructs a new Visitor object with the specified details.
	 * 
	 * @param visitorId    The unique identifier for the visitor.
	 * @param firstName    The first name of the visitor.
	 * @param lastName     The last name of the visitor.
	 * @param phoneNumber  The phone number of the visitor.
	 * @param emailAddress The email address of the visitor.
	 */
	public Visitor(String visitorId, String firstName, String lastName, String phoneNumber, String emailAddress) {
		super();
		this.visitorId = visitorId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.phoneNumber = phoneNumber;
		this.emailAddress = emailAddress;
		userType = UserTypeEnum.Visitor;
	}

	/**
	 * Constructs a new Visitor object with a specific visitor ID. This constructor
	 * initializes a visitor with only an ID, without specifying additional details.
	 * 
	 * @param visitorId The unique identifier for the visitor.
	 */
	public Visitor(String visitorId) {
		super();
		this.visitorId = visitorId;
	}

	/**
	 * Retrieves the customer ID of the visitor. This method is intended to comply
	 * with the ICustomer interface, where the visitor ID serves as the customer ID.
	 * 
	 * @return The visitor ID which is used as the customer ID.
	 */
	public String getCustomerId() {
		return visitorId;
	}

	/**
	 * Sets the customer ID for the visitor. This method allows for updating the
	 * visitor's ID.
	 * 
	 * @param id The new ID to be set for the visitor.
	 */
	public void setCustomerId(String id) {
		visitorId = id;
	}

	/**
	 * Retrieves the visitor ID.
	 * 
	 * @return The visitor ID.
	 */
	public String getVisitorId() {
		return visitorId;
	}

	/**
	 * Sets the visitor ID.
	 * 
	 * @param visitorId The visitor ID to set.
	 */
	public void setVisitorId(String visitorId) {
		this.visitorId = visitorId;
	}

	/**
	 * Retrieves the first name of the visitor.
	 * 
	 * @return The first name of the visitor.
	 */
	public String getFirstName() {
		return firstName;
	}

	/**
	 * Sets the first name of the visitor.
	 * 
	 * @param firstName The first name to set.
	 */
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	 * Retrieves the last name of the visitor.
	 * 
	 * @return The last name of the visitor.
	 */
	public String getLastName() {
		return lastName;
	}

	/**
	 * Sets the last name of the visitor.
	 * 
	 * @param lastName The last name to set.
	 */
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	 * Retrieves the phone number of the visitor.
	 * 
	 * @return The phone number of the visitor.
	 */
	public String getPhoneNumber() {
		return phoneNumber;
	}

	/**
	 * Sets the phone number of the visitor.
	 * 
	 * @param phoneNumber The phone number to set.
	 */
	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	/**
	 * Retrieves the email address of the visitor.
	 * 
	 * @return The email address of the visitor.
	 */
	public String getEmailAddress() {
		return emailAddress;
	}

	/**
	 * Sets the email address of the visitor.
	 * 
	 * @param emailAddress The email address to set.
	 */
	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	/**
	 * Retrieves the relevant order associated with the visitor.
	 * 
	 * @return The relevant order associated with the visitor.
	 */
	public Order getRelevantOrder() {
		return relevantOrder;
	}

	/**
	 * Sets the relevant order associated with the visitor.
	 * 
	 * @param order The relevant order to set.
	 */
	public void setRelevantOrder(Order order) {
		relevantOrder = order;
	}

}
