package utils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * The ValidationRules class provides static methods for performing various
 * validation checks.
 */
public class ValidationRules {

	/**
	 * Validates if the provided string represents a valid IP address.
	 * 
	 * @param ip The IP address to validate.
	 * @return {@code true} if the IP address is valid, {@code false} otherwise.
	 */
	public static boolean isValidIp(String ip) {
		try {
			if (ip.equals(""))
				return false;
			@SuppressWarnings("unused")
			InetAddress inetAddress = InetAddress.getByName(ip);
			return true;
		} catch (UnknownHostException e) {
			return false;
		}
	}

	/**
	 * Validates if the provided string represents a valid port number.
	 * 
	 * @param port The port number to validate.
	 * @return {@code true} if the port number is valid, {@code false} otherwise.
	 */
	public static boolean isValidPort(String port) {
		try {
			int serverPort = Integer.parseInt(port);
			if (serverPort >= 0 && serverPort <= 65535)
				return true;
			return false;
		} catch (NumberFormatException e) {
			return false;
		}
	}

	/**
	 * Validates if the provided string represents a valid email address.
	 * 
	 * @param email The email address to validate.
	 * @return {@code true} if the email address is valid, {@code false} otherwise.
	 */
	public static boolean isValidEmail(String email) {
		String emailPattern = "[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}";
		return Pattern.matches(emailPattern, email);
	}

	/**
	 * Validates if the provided string represents a valid phone number.
	 * 
	 * @param phoneNumber The phone number to validate.
	 * @return {@code true} if the phone number is valid, {@code false} otherwise.
	 */
	public static boolean isValidPhone(String phoneNumber) {
		String phonePattern = "\\d{10}";
		return Pattern.matches(phonePattern, phoneNumber);
	}

	/**
	 * Checks if any of the fields in the provided list are empty.
	 * 
	 * @param fieldsInput The list of fields to check.
	 * @return {@code true} if any field is empty, {@code false} otherwise.
	 */
	public static boolean areFieldsEmpty(ArrayList<String> fieldsInput) {
		for (String input : fieldsInput) {
			if (input.equals(""))
				return true;
		}
		return false;
	}

	/**
	 * Validates if the provided string represents a valid name.
	 * 
	 * @param name The name to validate.
	 * @return {@code true} if the name is valid, {@code false} otherwise.
	 */
	public static boolean isValidName(String name) {
		String namePattern = "^[a-zA-Z]+$";
		return Pattern.matches(namePattern, name);
	}

	/**
	 * Checks if the provided string is empty.
	 * 
	 * @param field The string to check.
	 * @return {@code true} if the string is empty, {@code false} otherwise.
	 */
	public static boolean isFieldEmpty(String field) {
		return field.equals("");
	}

	/**
	 * Validates if the provided string represents a valid password.
	 * 
	 * @param password The password to validate.
	 * @return {@code true} if the password is valid, {@code false} otherwise.
	 */
	public static boolean isValidPassword(String password) {
		String passwordPattern = "\\d{6,}";
		return Pattern.matches(passwordPattern, password);
	}

	/**
	 * Validates if the provided string represents a valid username.
	 * 
	 * @param username The username to validate.
	 * @return {@code true} if the username is valid, {@code false} otherwise.
	 */
	public static boolean isValidUsername(String username) {
		String usernamePattern = "^[a-zA-Z0-9]+$";
		return Pattern.matches(usernamePattern, username);
	}

	/**
	 * Validates if the provided string represents a valid ID.
	 * 
	 * @param id The ID to validate.
	 * @return {@code true} if the ID is valid, {@code false} otherwise.
	 */
	public static boolean isValidId(String id) {
		return isNumeric(id);
	}

	/**
	 * Validates if the provided ID number is Israeli valid ID.
	 * 
	 * @param id The Israeli ID number to validate.
	 * @return {@code true} if the Israeli ID number is valid, {@code false}
	 *         otherwise.
	 */
	public static boolean isValidIsraeliId(String id) {
		// Pad the ID to 9 digits
		String paddedId = String.format("%9s", id).replace(' ', '0');

		// Check if the padded ID is exactly 9 digits and all are numbers
		if (!paddedId.matches("\\d{9}")) {
			return false;
		}

		int sum = 0;
		// Process each digit
		for (int i = 0; i < 9; i++) {
			// Convert char to int ('0' character has an int value of 48)
			int digit = paddedId.charAt(i) - '0';

			// Even positions (from human perspective, odd indexes in zero-based indexing)
			// are multiplied by 2
			int product = (i % 2 == 0) ? digit : digit * 2;

			// If product is 10 or more, sum its digits (equivalent to subtracting 9 for
			// numbers 10-18)
			sum += (product > 9) ? product - 9 : product;
		}

		// The ID is valid if the sum is divisible by 10
		return sum % 10 == 0;
	}

	/**
	 * Checks if the provided string contains only numeric characters.
	 * 
	 * @param str The string to check.
	 * @return {@code true} if the string contains only numeric characters,
	 *         {@code false} otherwise.
	 */
	public static boolean isNumeric(String str) {
		String numericPattern = "^[0-9]+$";
		return Pattern.matches(numericPattern, str);
	}

	/**
	 * Checks if the provided numeric string is positive.
	 * 
	 * @param number The numeric string to check.
	 * @return {@code true} if the numeric string is positive, {@code false}
	 *         otherwise.
	 */
	public static boolean isPositiveNumeric(String number) {
		if (isNumeric(number)) {
			try {
				Integer.parseInt(number);
			}catch(Exception ex) {
				return false;
			}
			return Integer.parseInt(number) > 0;
		}
		return false;
	}

}
