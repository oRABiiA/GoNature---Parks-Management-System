package gui.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import logic.ClientRequestDataContainer;
import logic.Employee;
import logic.Park;
import logic.Request;
import logic.ServerResponseBackToClient;
import utils.AlertPopUp;
import utils.ValidationRules;
import utils.enums.ClientRequest;
import utils.enums.RequestTypeEnum;

/**
 * Controller class for the Park Settings screen, responsible for managing park
 * settings such as estimated visit time, reserved entries, and maximum
 * capacity.
 */
public class ParkSettingsScreenController implements Initializable {

	@FXML
	public Label datelabel;
	@FXML
	public TextField parkField1;
	@FXML
	public TextField parkField2;
	@FXML
	public TextField parkField3;
	@FXML
	public TextField oldCapacityField;
	@FXML
	public TextField oldReservedEntriesField;
	@FXML
	public TextField oldEstimatedVisitTimeField;
	@FXML
	public TextField newCapacityField;
	@FXML
	public TextField newReservedEntriesField;
	@FXML
	public TextField newEstimatedVisitTimeField;
	@FXML
	public Button changeEstimatedVisitTimeRequest;
	@FXML
	public Button changeReservedEntriesRequest;
	@FXML
	public Button changeCapacityRequest;
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;

	@SuppressWarnings("unused")
	private Employee employee;
	private Park park;
	private AlertPopUp alert;

	/**
	 * Constructs a new instance of ParkSettingsScreenController with the specified
	 * park and employee objects.
	 * 
	 * @param park     The park object whose settings are being managed.
	 * @param employee The employee object responsible for managing the park
	 *                 settings.
	 */
	public ParkSettingsScreenController(Object park, Object employee) {
		this.employee = (Employee) employee;
		this.park = (Park) park;
	}

	/**
	 * Initializes the Park Settings screen, populating fields with current park
	 * information. This method fetches the latest park data and updates the UI
	 * accordingly.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Search_For_Specific_Park,
				park);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		park = (Park) response.getMessage();
		parkField1.setText(park.getParkName().toString());
		parkField2.setText(park.getParkName().toString());
		parkField3.setText(park.getParkName().toString());
		oldCapacityField.setText(String.format("%d", park.getCurrentMaxCapacity()));
		oldReservedEntriesField.setText(String.format("%d", park.getCurrentEstimatedReservedSpots()));
		oldEstimatedVisitTimeField.setText(String.format("%d", park.getCurrentEstimatedStayTime()));
		hideErrorMessage();
	}

	/**
	 * Handles the action to request a change in the estimated visit time for the
	 * park. This method sends a request to update the estimated visit time and
	 * notifies the user of the outcome.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onChangeEstimatedVisitTimeRequest() {
		if (!validateGuiFields(3))
			return;
		hideErrorMessage();

		Request parametersRequest = new Request(park.getParkId(), RequestTypeEnum.EstimatedVisitTime,
				Integer.parseInt(oldEstimatedVisitTimeField.getText()),
				Integer.parseInt(newEstimatedVisitTimeField.getText()), LocalDateTime.now());
		ClientRequestDataContainer request = new ClientRequestDataContainer(
				ClientRequest.Make_New_Park_Estimated_Visit_Time_Request, parametersRequest);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;

		switch (response.getRensponse()) {
		case Last_Request_With_Same_Type_Still_Pending:
			alert = new AlertPopUp(AlertType.WARNING, "Warning", "Estimated Visit Time Request",
					"Last Request With Same Type Still Pending");
			alert.showAndWait();
			return;
		case Request_Sent_To_Department_Successfully:
			alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Estimated Visit Time Request",
					"Request has been sent to department manager");
			alert.showAndWait();
			return;
		}

	}

	/**
	 * Handles the action to request a change in the number of reserved entries for
	 * the park. This method sends a request to update the reserved entries and
	 * notifies the user of the outcome.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onChangeReservedEntriesRequest() {
		if (!validateGuiFields(2))
			return;
		hideErrorMessage();

		Request parametersRequest = new Request(park.getParkId(), RequestTypeEnum.ReservedSpots,
				Integer.parseInt(oldReservedEntriesField.getText()),
				Integer.parseInt(newReservedEntriesField.getText()), LocalDateTime.now());
		ClientRequestDataContainer request = new ClientRequestDataContainer(
				ClientRequest.Make_New_Park_Reserved_Entries_Request, parametersRequest);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;

		switch (response.getRensponse()) {
		case Last_Request_With_Same_Type_Still_Pending:
			alert = new AlertPopUp(AlertType.WARNING, "Warning", "Reserved Entries Request",
					"Last Request With Same Type Still Pending");
			alert.showAndWait();
			return;
		case Request_Sent_To_Department_Successfully:
			alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Reserved Entries Request",
					"Request has been sent to department manager");
			alert.showAndWait();
			return;
		}
	}

	/**
	 * Handles the action to request a change in the maximum capacity for the park.
	 * This method sends a request to update the maximum capacity and notifies the
	 * user of the outcome.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onChangeCapacityRequest() {
		if (!validateGuiFields(1))
			return;
		hideErrorMessage();

		Request parametersRequest = new Request(park.getParkId(), RequestTypeEnum.MaxCapacity,
				Integer.parseInt(oldCapacityField.getText()), Integer.parseInt(newCapacityField.getText()),
				LocalDateTime.now());
		ClientRequestDataContainer request = new ClientRequestDataContainer(
				ClientRequest.Make_New_Park_Capacity_Request, parametersRequest);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;

		switch (response.getRensponse()) {
		case Last_Request_With_Same_Type_Still_Pending:
			alert = new AlertPopUp(AlertType.WARNING, "Warning", "Max park Capacity Request",
					"Last Request With Same Type Still Pending");
			alert.showAndWait();
			return;
		case Request_Sent_To_Department_Successfully:
			alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Max park Capacity Request",
					"Request has been sent to department manager");
			alert.showAndWait();
			return;
		}

	}

	/**
	 * Validates specific input fields in the graphical user interface (GUI) based
	 * on the provided request type. Each request type corresponds to a specific
	 * field that needs validation.
	 *
	 * @param request An integer representing the type of validation request: 1 for
	 *                validating new capacity field, 2 for validating new reserved
	 *                entries field, 3 for validating new estimated visit time
	 *                field.
	 * @return {@code true} if the input for the specified field passes validation;
	 *         {@code false} otherwise, along with an error message displayed for
	 *         the invalid field.
	 */
	private boolean validateGuiFields(int request) {
		switch (request) {
		case 1:
			if (!ValidationRules.isPositiveNumeric(newCapacityField.getText())) {
				showErrorMessage("New Capacity should be positive number!");
				return false;
			}
			return true;
		case 2:
			if (!ValidationRules.isPositiveNumeric(newReservedEntriesField.getText())) {
				showErrorMessage("New Reserved Entries should be positive number!");
				return false;
			}
			return true;
		case 3:
			if (!ValidationRules.isPositiveNumeric(newEstimatedVisitTimeField.getText())) {
				showErrorMessage("New Estimated Visit Time should be positive number!");
				return false;
			}
			return true;
		default:
			return false;
		}
	}

	/**
	 * Hides the error message label and error section in the GUI.
	 */
	private void hideErrorMessage() {
		errorMessageLabel.setText("");
		errorSection.setVisible(false);
	}

	/**
	 * Shows an error message in the GUI.
	 *
	 * @param error The error message to be displayed.
	 */
	private void showErrorMessage(String error) {
		errorSection.setVisible(true);
		errorMessageLabel.setText(error);
	}
}
