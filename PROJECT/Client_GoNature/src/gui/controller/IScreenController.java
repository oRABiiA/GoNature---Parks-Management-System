package gui.controller;

/**
 * Interface defining methods for controlling the screens in the application.
 * Implementing classes are expected to provide implementations for these
 * methods to handle various user interactions and application events.
 */
public interface IScreenController {

	/**
	 * Called when the logout button is clicked in the user interface. Implementing
	 * classes should define the behavior to be performed when the user logs out.
	 */
	void onLogoutClicked();

	/**
	 * Called when the server crashes or becomes unavailable. Implementing classes
	 * should define the appropriate action to take in response to the server crash.
	 */
	void onServerCrashed();

	/**
	 * Called when the application is being closed. Implementing classes should
	 * define the cleanup or closing actions to be performed before the application
	 * exits.
	 */
	void onCloseApplication();
}
