package gui.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.ComboBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import logic.ClientRequestDataContainer;
import logic.Request;
import logic.RequestInTable;
import logic.ServerResponseBackToClient;
import utils.AlertPopUp;
import utils.CurrentDateAndTime;
import utils.enums.ClientRequest;
import utils.enums.RequestStatusEnum;

/**
 * Controller class for the Request Table screen, responsible for managing and
 * displaying requests.
 */
public class RequestTableScreenController implements Initializable {

	@FXML
	public Button saveButton;
	@FXML
	public Button refreshButton;
	@FXML
	public Label dateLabel;
	@FXML
	public TableView<RequestInTable> requestsTable;
	@FXML
	public TableColumn<RequestInTable, String> requestIdCol;
	@FXML
	public TableColumn<RequestInTable, String> parkCol;
	@FXML
	public TableColumn<RequestInTable, String> requestedTypeCol;
	@FXML
	public TableColumn<RequestInTable, String> oldValueCol;
	@FXML
	public TableColumn<RequestInTable, String> newValueCol;
	@FXML
	public TableColumn<RequestInTable, String> statusCol;
	@FXML
	public TableColumn<RequestInTable, String> requestedDateCol;

	public BorderPane screen;
	private ArrayList<Request> requestsFromDatabase = new ArrayList<Request>();
	private ObservableList<RequestInTable> requestsList = FXCollections.observableArrayList();

	/**
	 * Constructs a new instance of RequestTableScreenController with the specified
	 * screen.
	 * 
	 * @param screen The BorderPane object representing the screen layout.
	 */
	public RequestTableScreenController(BorderPane screen) {
		this.screen = screen;
	}

	/**
	 * Initializes the Request Table screen, setting up the table and disabling the
	 * save button.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		setupTable();
		saveButton.setDisable(true);
	}

	/**
	 * Sets up the columns and cell factories for the requests table.
	 */
	private void setupTable() {
		// User ID Column
		requestIdCol.setCellValueFactory(new PropertyValueFactory<>("requestId"));

		// Username Column
		parkCol.setCellValueFactory(new PropertyValueFactory<>("parkId"));

		// First Name Column
		requestedTypeCol.setCellValueFactory(new PropertyValueFactory<>("requestType"));

		// Full Name Column
		oldValueCol.setCellValueFactory(new PropertyValueFactory<>("oldValue"));

		// Email Column
		newValueCol.setCellValueFactory(new PropertyValueFactory<>("newValue"));

		// Phone Column
		requestedDateCol.setCellValueFactory(new PropertyValueFactory<>("requestDate"));

		// Status Column with ComboBox
		statusCol.setCellValueFactory(new PropertyValueFactory<>("requestStatus"));
		statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("Pending", "Approved", "Denied"));
		statusCol.setOnEditCommit((TableColumn.CellEditEvent<RequestInTable, String> t) -> (t.getTableView().getItems()
				.get(t.getTablePosition().getRow())).setStatus(t.getNewValue()));

		requestsTable.setItems(requestsList);
		requestsTable.setEditable(true);
	}

	/**
	 * Handles the action when the save button is clicked, updating the status of
	 * selected requests.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onSaveClicked() {
		AlertPopUp alert;
		ArrayList<Request> requestsToCheck = new ArrayList<Request>();
		for (RequestInTable req : requestsList) {
			req.getRequest().setRequestStatus(RequestStatusEnum.fromString(req.getRequestStatus()));
			requestsToCheck.add(req.getRequest());
		}

		if (requestsToCheck.isEmpty()) {
			alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Save Changes",
					"You have to select guides to approve");
			alert.showAndWait();
		} else {
			ClientRequestDataContainer request = new ClientRequestDataContainer(
					ClientRequest.Update_Request_In_Database, requestsToCheck);
			ClientApplication.client.accept(request);
			ServerResponseBackToClient response = ClientCommunication.responseFromServer;

			switch (response.getRensponse()) {
			case Updated_Requests_Successfully:
				alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Save Changes",
						"Requests updated Successfully");
				alert.showAndWait();
				onRefreshClicked();
				return;
			case Updated_Requests_Failed:
				alert = new AlertPopUp(AlertType.WARNING, "Warning", "Save Changes", "Update failed");
				alert.showAndWait();
				return;
			}
		}

	}

	/**
	 * Handles the action when the refresh button is clicked, updating the list of
	 * requests.
	 */
	@SuppressWarnings({ "incomplete-switch", "unchecked" })
	public void onRefreshClicked() {
		ArrayList<RequestInTable> observeRequests = new ArrayList<RequestInTable>();
		requestsList.clear();
		requestsFromDatabase.clear();
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Import_All_Pending_Requests,
				requestsFromDatabase);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;

		switch (response.getRensponse()) {
		case There_Are_Not_Pending_Requests:
			AlertPopUp alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Search Requests",
					"There are no requests with status pending");
			alert.showAndWait();
			saveButton.setDisable(true);
			return;
		case Pending_Requests_Found_Successfully:
			requestsFromDatabase = (ArrayList<Request>) response.getMessage();
			for (Request req : requestsFromDatabase) {
				RequestInTable requestToView = new RequestInTable(req.getRequestId(), req.getParkId(),
						req.getRequestType(), req.getOldValue(), req.getNewValue(), req.getRequestStatus(),
						req.getRequestDate());
				requestToView.setRequest(req);
				observeRequests.add(requestToView);
			}
			requestsList.addAll(observeRequests);
			requestsTable.refresh();
			saveButton.setDisable(false);
			return;
		}
	}
}
