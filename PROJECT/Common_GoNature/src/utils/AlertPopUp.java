package utils;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

/**
 * The AlertPopUp class represents a customized alert dialog box. It extends the
 * Alert class.
 */
public class AlertPopUp extends Alert {

	/**
	 * Constructs an AlertPopUp object with the specified type, title, header, and
	 * content.
	 * 
	 * @param type    The type of the alert.
	 * @param title   The title of the alert.
	 * @param header  The header text of the alert.
	 * @param content The content text of the alert.
	 */
	public AlertPopUp(AlertType type, String title, String header, String content) {
		super(type);
		this.setTitle(title);
		this.setHeaderText(header);
		this.setContentText(content);
	}

	/**
	 * Constructs an AlertPopUp object with the specified type, title, header,
	 * content, and buttons.
	 * 
	 * @param type    The type of the alert.
	 * @param title   The title of the alert.
	 * @param header  The header text of the alert.
	 * @param content The content text of the alert.
	 * @param buttons The buttons to include in the alert.
	 */
	public AlertPopUp(AlertType type, String title, String header, String content, ButtonType... buttons) {
		super(type);
		this.setTitle(title);
		this.setHeaderText(header);
		this.setContentText(content);
		this.getButtonTypes().setAll(buttons);
	}
}
