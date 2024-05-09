package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The EmployeeTypeEnum enum represents different types of employees.
 */
public enum EmployeeTypeEnum {
	/**
	 * Not an employee.
	 */
	Not_Employee("Not Employee"),
	/**
	 * Department manager.
	 */
	Department_Manager("Department Manager"),
	/**
	 * Park manager.
	 */
	Park_Manager("Park Manager"),
	/**
	 * Park employee.
	 */
	Park_Employee("Park Employee"),
	/**
	 * Service employee.
	 */
	Service_Employee("Service Employee");

	private String name;
	// Map to store enum values by name
	private static final Map<String, EmployeeTypeEnum> enumMap = new HashMap<>();

	// Static block to initialize enumMap
	static {
		for (EmployeeTypeEnum employeeType : EmployeeTypeEnum.values()) {
			enumMap.put(employeeType.name, employeeType);
		}
	}

	/**
	 * Constructs a new EmployeeTypeEnum with the specified name.
	 * 
	 * @param name The name of the employee type.
	 */
	private EmployeeTypeEnum(String name) {
		this.name = name;
	}

	/**
	 * Returns the string representation of the employee type.
	 * 
	 * @return The string representation of the employee type.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Returns the EmployeeTypeEnum associated with the specified name.
	 * 
	 * @param name The name of the employee type.
	 * @return The EmployeeTypeEnum associated with the specified name, or
	 *         {@code null} if not found.
	 */
	public static EmployeeTypeEnum fromString(String name) {
		return enumMap.get(name);
	}
}
