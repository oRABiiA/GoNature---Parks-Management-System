package gui.controller;

/**
 * Interface for defining common functionalities related to thread management.
 */
public interface IThreadController {

	/**
	 * Method to perform clean-up operations. This method should be implemented to
	 * release resources or perform any necessary clean-up tasks.
	 */
	void cleanUp();
}
