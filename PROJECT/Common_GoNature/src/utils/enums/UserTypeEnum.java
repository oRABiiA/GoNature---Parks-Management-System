package utils.enums;

import java.util.HashMap;
import java.util.Map;

/**
 * The UserTypeEnum enum represents the different types of users in the system.
 */
public enum UserTypeEnum {
	/**
	 * Represents an external user, someone who is not regularly a part of the
	 * system but interacts with it.
	 */
	ExternalUser("External User"),
	/**
	 * Represents an occasional user.
	 */
	Occasional("Occasional"),
	/**
	 * Represents an employee, someone who works within the system.
	 */
	Employee("Employee"),
	/**
	 * Represents a visitor, typically someone who visits a location managed by the
	 * system.
	 */
	Visitor("Visitor"),
	/**
	 * Represents a guide.
	 */
	Guide("Guide");

	private static final Map<String, UserTypeEnum> enumMap = new HashMap<>();

	static {
		for (UserTypeEnum userTypeEnum : UserTypeEnum.values()) {
			enumMap.put(userTypeEnum.name, userTypeEnum);
		}
	}

	private String name;

	/**
	 * Constructs a UserTypeEnum with the specified name.
	 *
	 * @param name The name of the user type.
	 */
	private UserTypeEnum(String name) {
		this.name = name;
	}

	/**
	 * Returns the string representation of this UserTypeEnum.
	 *
	 * @return The name of the user type.
	 */
	@Override
	public String toString() {
		return name;
	}

	/**
	 * Converts a string name to its corresponding UserTypeEnum value.
	 *
	 * @param name The name of the user type as a string.
	 * @return The UserTypeEnum corresponding to the given name.
	 */
	public static UserTypeEnum fromString(String name) {
		return enumMap.get(name);
	}
}
