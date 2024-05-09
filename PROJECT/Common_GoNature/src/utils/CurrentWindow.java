package utils;

import javafx.stage.Stage;

/**
 * The CurrentWindow class provides a mechanism to store and retrieve the
 * current JavaFX stage/window.
 */
public class CurrentWindow {

	private static Stage currentWindow;
	private static final Object lock = new Object();

	/**
	 * Sets the current JavaFX stage/window.
	 * 
	 * @param stage The stage/window to be set as the current window.
	 */
	public static void setCurrentWindow(Stage stage) {
		synchronized (lock) {
			currentWindow = stage;
		}
	}

	/**
	 * Retrieves the current JavaFX stage/window.
	 * 
	 * @return The current stage/window.
	 */
	public static Stage getCurrentWindow() {
		synchronized (lock) {
			return currentWindow;
		}
	}
}
