package logic;

import javafx.beans.property.SimpleStringProperty;

/**
 * The GuideInTable class represents a guide's information formatted for display
 * in a table view. It holds properties for a guide's user ID, username, first
 * name, last name, email, phone, and status, facilitating easy binding to table
 * columns in a JavaFX application.
 */
public class GuideInTable {
	private Guide guide;
	private final SimpleStringProperty userId;
	private final SimpleStringProperty username;
	private final SimpleStringProperty firstName;
	private final SimpleStringProperty lastName;
	private final SimpleStringProperty email;
	private final SimpleStringProperty phone;
	private final SimpleStringProperty status; // This will hold the selected status

	/**
	 * Constructs a new GuideInTable object with the specified details.
	 * 
	 * @param userId    The user ID of the guide.
	 * @param username  The username of the guide.
	 * @param firstName The first name of the guide.
	 * @param lastName  The last name of the guide.
	 * @param email     The email address of the guide.
	 * @param phone     The phone number of the guide.
	 * @param status    The status of the guide.
	 */
	public GuideInTable(String userId, String username, String firstName, String lastName, String email, String phone,
			String status) {
		this.userId = new SimpleStringProperty(userId);
		this.username = new SimpleStringProperty(username);
		this.firstName = new SimpleStringProperty(firstName);
		this.lastName = new SimpleStringProperty(lastName);
		this.email = new SimpleStringProperty(email);
		this.phone = new SimpleStringProperty(phone);
		this.status = new SimpleStringProperty(status);
	}

	// Getters and setters

	/**
	 * Gets the user ID of the guide.
	 * 
	 * @return The user ID as a String.
	 */
	public String getUserId() {
		return userId.get();
	}

	/**
	 * Gets the username of the guide.
	 * 
	 * @return The username as a String.
	 */
	public String getUsername() {
		return username.get();
	}

	/**
	 * Gets the email address of the guide.
	 * 
	 * @return The email as a String.
	 */
	public String getEmail() {
		return email.get();
	}

	/**
	 * Gets the phone number of the guide.
	 * 
	 * @return The phone number as a String.
	 */
	public String getPhone() {
		return phone.get();
	}

	/**
	 * Gets the status of the guide.
	 * 
	 * @return The status as a String.
	 */
	public String getStatus() {
		return status.get();
	}

	/**
	 * Sets the status of the guide.
	 * 
	 * @param status The new status as a String.
	 */
	public void setStatus(String status) {
		this.status.set(status);
	}

	/**
	 * Gets the first name of the guide.
	 * 
	 * @return The first name as a String.
	 */
	public String getFirstName() {
		return firstName.get();
	}

	/**
	 * Gets the last name of the guide.
	 * 
	 * @return The last name as a String.
	 */
	public String getLastName() {
		return lastName.get();
	}

	/**
	 * Sets the guide object.
	 * 
	 * @param guide The guide to set.
	 */
	public void setGuide(Guide guide) {
		this.guide = guide;
	}

	/**
	 * Gets the guide object.
	 * 
	 * @return The Guide object.
	 */
	public Guide getGuide() {
		return guide;
	}
}
