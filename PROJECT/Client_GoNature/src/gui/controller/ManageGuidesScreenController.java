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
import javafx.scene.layout.HBox;
import logic.ClientRequestDataContainer;
import logic.Guide;
import logic.GuideInTable;
import logic.ServerResponseBackToClient;
import utils.AlertPopUp;
import utils.CurrentDateAndTime;
import utils.enums.ClientRequest;

/**
 * Controller class for managing guides in the system, including approving
 * pending guides.
 */
public class ManageGuidesScreenController implements Initializable {
	@FXML
	public BorderPane screen;
	@FXML
	public Label dateLabel;
	@FXML
	public TableView<GuideInTable> pendingGuidesTable;
	@FXML
	public TableColumn<GuideInTable, String> userIdCol;
	@FXML
	public TableColumn<GuideInTable, String> usernameCol;
	@FXML
	public TableColumn<GuideInTable, String> firstnameCol;
	@FXML
	public TableColumn<GuideInTable, String> lastnameCol;
	@FXML
	public TableColumn<GuideInTable, String> emailCol;
	@FXML
	public TableColumn<GuideInTable, String> phoneCol;
	@FXML
	public TableColumn<GuideInTable, String> statusCol;
	@FXML
	public Button saveButton;
	@FXML
	public Button searchGuidesButton;
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;

	private ArrayList<Guide> guidesFromDatabase = new ArrayList<Guide>();
	private ObservableList<GuideInTable> guidesList = FXCollections.observableArrayList();

	/**
	 * Constructor for the ManageGuidesScreenController.
	 * 
	 * @param screen The BorderPane representing the screen.
	 */
	public ManageGuidesScreenController(BorderPane screen) {
		this.screen = screen;
	}

	/**
	 * Initializes the controller.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		setupTable();
		hideErrorMessage();
		saveButton.setDisable(true);

	}

	/**
	 * Sets up the TableView with columns and cell value factories.
	 */
	private void setupTable() {
		// User ID Column
		userIdCol.setCellValueFactory(new PropertyValueFactory<>("userId"));

		// Username Column
		usernameCol.setCellValueFactory(new PropertyValueFactory<>("username"));

		// First Name Column
		firstnameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));

		// Full Name Column
		lastnameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));

		// Email Column
		emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));

		// Phone Column
		phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

		// Status Column with ComboBox
		statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
		statusCol.setCellFactory(ComboBoxTableCell.forTableColumn("Pending", "Approved"));
		statusCol.setOnEditCommit((TableColumn.CellEditEvent<GuideInTable, String> t) -> (t.getTableView().getItems()
				.get(t.getTablePosition().getRow())).setStatus(t.getNewValue()));

		pendingGuidesTable.setItems(guidesList);
		pendingGuidesTable.setEditable(true);
	}

	/**
	 * Handles the action when the "Save" button is clicked.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onSaveClicked() {
		AlertPopUp alert;
		ArrayList<Guide> approvedGuides = new ArrayList<Guide>();
		for (GuideInTable guide : guidesList) {
			if (guide.getStatus().equals("Approved")) {
				approvedGuides.add(guide.getGuide());
			}
		}
		if (approvedGuides.isEmpty()) {
			alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Save Changes",
					"You have to select guides to approve");
			alert.showAndWait();
		} else {
			ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Update_Guide_As_Approved,
					approvedGuides);
			ClientApplication.client.accept(request);
			ServerResponseBackToClient response = ClientCommunication.responseFromServer;

			switch (response.getRensponse()) {
			case Updated_Guides_To_Approved_Successfully:
				alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Save Changes",
						"Guides added Successfully");
				alert.showAndWait();
				onSearchGuidesClicked();
				return;
			case Updated_Guides_To_Approved_Failed:
				alert = new AlertPopUp(AlertType.WARNING, "Warning", "Save Changes", "Update failed");
				alert.showAndWait();
				return;
			}
		}

	}

	/**
	 * Handles the action when the "Search Guides" button is clicked.
	 */
	@SuppressWarnings({ "incomplete-switch", "unchecked" })
	public void onSearchGuidesClicked() {
		ArrayList<GuideInTable> observeGuides = new ArrayList<GuideInTable>();
		guidesList.clear();
		guidesFromDatabase.clear();
		ClientRequestDataContainer request = new ClientRequestDataContainer(
				ClientRequest.Search_For_Guides_Status_Pending, guidesFromDatabase);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;

		switch (response.getRensponse()) {
		case Guides_With_Status_Pending_Not_Found:
			AlertPopUp alert = new AlertPopUp(AlertType.INFORMATION, "Information", "Search Guides",
					"There is not guides with status pending");
			alert.showAndWait();
			saveButton.setDisable(true);
			return;

		case Guides_With_Status_Pending_Found:
			guidesFromDatabase = (ArrayList<Guide>) response.getMessage();
			for (Guide guide : guidesFromDatabase) {
				GuideInTable guideToView = new GuideInTable(guide.getUserId(), guide.getUsername(),
						guide.getFirstName(), guide.getLastName(), guide.getEmailAddress(), guide.getPhoneNumber(),
						guide.getUserStatus().name());
				guideToView.setGuide(guide);
				observeGuides.add(guideToView);
			}

			guidesList.addAll(observeGuides);
			pendingGuidesTable.refresh();
			saveButton.setDisable(false);
			return;
		}

	}

	/**
	 * Hides the error message section in the GUI.
	 */
	private void hideErrorMessage() {
		errorMessageLabel.setText("");
		errorSection.setVisible(false);
	}

	/**
	 * Displays an error message in the GUI.
	 * 
	 * @param error The error message to display.
	 */
	@SuppressWarnings("unused")
	private void showErrorMessage(String error) {
		errorSection.setVisible(true);
		errorMessageLabel.setText(error);
	}

}
