package logic;

import utils.enums.EmployeeTypeEnum;
import utils.enums.ParkNameEnum;
import utils.enums.UserStatus;
import utils.enums.UserTypeEnum;

/**
 * The Employee class represents an employee user within the system. It extends
 * the User class and provides additional properties specific to employees.
 */
public class Employee extends User {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4509648140746139466L;

	/** The park related to the employee. */
	ParkNameEnum relatedPark;

	/** The type of employee. */
	EmployeeTypeEnum employeeType;

	/**
	 * Constructs a new Employee with the specified related park and employee type.
	 * 
	 * @param relatedPark  The park related to the employee
	 * @param employeeType The type of employee
	 */
	public Employee(ParkNameEnum relatedPark, EmployeeTypeEnum employeeType) {
		super(UserTypeEnum.Employee);
		this.relatedPark = relatedPark;
		this.employeeType = employeeType;
	}

	/**
	 * Constructs a new Employee with the specified parameters.
	 * 
	 * @param relatedPark  The park related to the employee
	 * @param employeeType The type of employee
	 * @param userId       The user ID
	 * @param username     The username
	 * @param password     The password
	 * @param firstName    The first name
	 * @param lastName     The last name
	 * @param phoneNumber  The phone number
	 * @param emailAddress The email address
	 */
	public Employee(ParkNameEnum relatedPark, EmployeeTypeEnum employeeType, String userId, String username,
			String password, String firstName, String lastName, String phoneNumber, String emailAddress) {
		super(userId, username, password, firstName, lastName, phoneNumber, emailAddress);
		this.relatedPark = relatedPark;
		this.employeeType = employeeType;
		userStatus = UserStatus.Approved;
	}

	/**
	 * Constructs a new Employee with the specified username and password.
	 * 
	 * @param username The username
	 * @param password The password
	 */
	public Employee(String username, String password) {
		super(username, password);
		userStatus = UserStatus.Approved;
	}

	/**
	 * Constructs a new Employee with default values.
	 */
	public Employee() {
		super(UserTypeEnum.Employee);
	}

	/**
	 * Gets the park related to the employee.
	 * 
	 * @return The related park
	 */
	public ParkNameEnum getRelatedPark() {
		return relatedPark;
	}

	/**
	 * Sets the park related to the employee.
	 * 
	 * @param relatedPark The related park to set
	 */
	public void setRelatedPark(ParkNameEnum relatedPark) {
		this.relatedPark = relatedPark;
	}

	/**
	 * Gets the type of employee.
	 * 
	 * @return The employee type
	 */
	public EmployeeTypeEnum getEmployeeType() {
		return employeeType;
	}

	/**
	 * Sets the type of employee.
	 * 
	 * @param employeeType The employee type to set
	 */
	public void setEmployeeType(EmployeeTypeEnum employeeType) {
		this.employeeType = employeeType;
	}
}
