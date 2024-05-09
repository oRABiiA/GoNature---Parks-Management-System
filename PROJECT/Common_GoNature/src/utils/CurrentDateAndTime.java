package utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * The CurrentDateAndTime class provides methods for retrieving the current date
 * and time in various formats.
 */
public class CurrentDateAndTime {

	/**
	 * Retrieves the current date in the specified pattern.
	 * 
	 * @param pattern The pattern to format the date (e.g., "yyyy-MM-dd").
	 * @return A string representation of the current date.
	 */
	public static String getCurrentDate(String pattern) {
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return currentDate.format(formatter);
	}

	/**
	 * Retrieves the current date and time in the specified pattern.
	 * 
	 * @param pattern The pattern to format the date and time (e.g., "yyyy-MM-dd
	 *                hh:mm").
	 * @return A string representation of the current date and time.
	 */
	public static String getCurrentDateAndTime(String pattern) {
		LocalDateTime currentDate = LocalDateTime.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
		return currentDate.format(formatter);
	}

//	public static String getDateAndTimeFormatted(String pattern) {
//		LocalDateTime formatted = n;
//		DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
//		String formattedDate = formatted.format(formatter);
//		return formattedDate;
//	}

}
