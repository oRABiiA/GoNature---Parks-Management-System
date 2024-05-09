package gui.controller;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import gui.view.ApplicationViewType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import logic.ClientRequestDataContainer;
import logic.EntitiesContainer;
import logic.Order;
import logic.SceneLoaderHelper;
import logic.ServerResponseBackToClient;
import utils.AlertPopUp;
import utils.CurrentDateAndTime;
import utils.NotificationMessageTemplate;
import utils.enums.ClientRequest;
import utils.enums.OrderStatusEnum;
import utils.enums.ServerResponse;

/**
 * Controller class for the Reschedule Order screen, responsible for managing
 * the rescheduling of orders.
 */
public class RescheduleOrderScreenController implements Initializable, IThreadController {
	@FXML
	public Label dateLabel;
	@FXML
	public HBox comboBoxHbox;
	@FXML
	public ComboBox<String> selectOptionComboBox;
	private ObservableList<String> options = FXCollections.observableArrayList("Choose New Date", "Enter Waiting List");
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;
	@FXML
	public Button cancelReserveButton;
	@FXML
	public Button confirmButton;
	@FXML
	public HBox waitingListView;
	@FXML
	public Label enterWaitingListMsg;
	@FXML
	public VBox availableDatesView;

	@FXML
	public ListView<LocalDateTime> availableDatesList;
	private ObservableList<LocalDateTime> availableDatesToDisplay = FXCollections.observableArrayList();

	private BorderPane screen;
	private String selectedOption;
	private Order order;
	@SuppressWarnings("unused")
	private LocalDateTime selectedDate = null;
	private Thread searchAvailableDates = null;

	/**
	 * Constructs a new instance of RescheduleOrderScreenController with the
	 * specified screen and order.
	 * 
	 * @param screen The BorderPane object representing the screen layout.
	 * @param order  The Order object representing the order to be rescheduled.
	 */
	public RescheduleOrderScreenController(BorderPane screen, Object order) {
		this.screen = screen;
		this.order = (Order) order;

	}

	/**
	 * Initializes the Reschedule Order screen, setting up the initial UI components
	 * and event handlers.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		selectOptionComboBox.getItems().addAll(options);
		selectOptionComboBox.setOnAction(this::onChangeSelection);

		availableDatesList.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				// newValue contains the selected LocalDate
				selectedDate = newValue;
				// Perform actions based on the selected date here
			}
		});
		hideErrorMessage();

	}

	/**
	 * Handles the action when the selection in the combo box changes.
	 * 
	 * @param event The ActionEvent triggered by the combo box selection change.
	 */
	private void onChangeSelection(ActionEvent event) {
		if (selectOptionComboBox.getValue() == null)
			return;

		selectedOption = selectOptionComboBox.getValue();
		if (selectedOption.equals("Choose New Date")) {
			waitingListView.setVisible(false);
			availableDatesView.setVisible(true);
			// TODO: run query to look for available dates
			availableDatesToDisplay.clear();
			showAvailableDates();
		} else if (selectedOption.equals("Enter Waiting List")) {
			cleanUp();
			waitingListView.setVisible(true);
			availableDatesView.setVisible(false);
			showWaitingListNotificationMessage();
		} else {
			waitingListView.setVisible(false);
			availableDatesView.setVisible(false);
		}
	}

	/**
	 * Retrieves and displays the available dates for rescheduling the order.
	 */
	private void showAvailableDates() {
		// Check if the previous thread is alive and interrupt it
		if (searchAvailableDates != null && searchAvailableDates.isAlive()) {
			searchAvailableDates.interrupt();
			try {
				searchAvailableDates.join(); // Wait for the thread to stop
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // Restore interrupted status
			}
		}

		// Instantiate a new Thread object for searchAvailableDates
		searchAvailableDates = new Thread(() -> {
			while (!Thread.interrupted()) {
				try {
					Thread.sleep(2000); // Wait for 2 seconds, not 10

					// Safely update the list and UI from the JavaFX Application Thread
					Platform.runLater(() -> {
						ClientApplication.client
								.accept(new ClientRequestDataContainer(ClientRequest.Search_For_Available_Date, order));
						ServerResponseBackToClient response = ClientCommunication.responseFromServer;
						if (response.getRensponse() == ServerResponse.Query_Failed)
							Thread.currentThread().interrupt();
						@SuppressWarnings("unchecked")
						List<LocalDateTime> dates = (ArrayList<LocalDateTime>) response.getMessage();
						ObservableList<LocalDateTime> observableDates = FXCollections.observableArrayList(dates);
						availableDatesList.setItems(observableDates);
					});
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt(); // Restore interrupted status
					break; // Exit the loop if the thread was interrupted
				}
			}
		});

		// Start the new thread
		searchAvailableDates.start();
	}

	/**
	 * Displays a notification message for entering the waiting list.
	 */
	private void showWaitingListNotificationMessage() {
		enterWaitingListMsg.setText(NotificationMessageTemplate.enterWaitingListMessage(order.getParkName().toString(),
				order.getOrderDate(), String.format("%d", order.getNumberOfVisitors())));
	}

	/**
	 * Handles the action when the "Cancel Reserve" button is clicked.
	 */
	public void onCancelReserveClicked() {
		AlertPopUp alert = new AlertPopUp(AlertType.CONFIRMATION, "Order Cancel", "Are you sure?",
				"Order will not be saved.", ButtonType.YES, ButtonType.CLOSE);
		Optional<ButtonType> result = alert.showAndWait();

		// Check which button was clicked and act accordingly
		if (result.isPresent() && result.get() == ButtonType.YES) {
			AnchorPane view = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
					"/gui/view/CustomerHomepageScreen.fxml", ApplicationViewType.Customer_Homepage_Screen,
					new EntitiesContainer(order));
			screen.setCenter(view);
		}
	}

	/**
	 * Handles the action when the "Confirm" button is clicked.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onConfirmClicked() {
		// TODO: update new order in database with it's relevant status
		if (selectedOption.equals("Choose New Date")) {
			LocalDateTime selectedNewDate = availableDatesList.getSelectionModel().getSelectedItem();
			if (selectedNewDate == null)
				// TODO: need to choose date
				return;
			order.setEnterDate(selectedNewDate);
			cleanUp();
			AnchorPane view = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
					"/gui/view/OrderSummaryScreen.fxml", ApplicationViewType.Order_Summary_Screen,
					new EntitiesContainer(order));
			screen.setCenter(view);
			return;
		}

		if (selectedOption.equals("Enter Waiting List")) {
			order.setStatus(OrderStatusEnum.In_Waiting_List);
			ClientRequestDataContainer request = new ClientRequestDataContainer(
					ClientRequest.Insert_New_Order_As_Wait_Notify, order);
			ClientApplication.client.accept(request);
			ServerResponseBackToClient response = ClientCommunication.responseFromServer;
			Order orderFullDetailed = (Order) response.getMessage();
			switch (response.getRensponse()) {
			case Order_Added_Successfully:
				String orderSummaryAfterPaymentMessage = NotificationMessageTemplate.orderConfirmMessage(
						orderFullDetailed.getOrderId(), orderFullDetailed.getParkName().name(),
						orderFullDetailed.getEnterDate().toString(), orderFullDetailed.getOrderType().name(),
						orderFullDetailed.getNumberOfVisitors(), orderFullDetailed.getPrice(),
						orderFullDetailed.isPaid());
				enterWaitingListMsg.setText(orderSummaryAfterPaymentMessage);
				confirmButton.setVisible(false);
				comboBoxHbox.setVisible(false);
				break;
			case Order_Added_Failed:
				break;
			}

		}

	}

	/**
	 * Cleans up resources and stops any running threads when the controller is
	 * destroyed.
	 */
	@Override
	public void cleanUp() {
		// Stop the searchAvailableDates thread if it's running
		if (searchAvailableDates != null && searchAvailableDates.isAlive()) {
			searchAvailableDates.interrupt();
			try {
				searchAvailableDates.join(); // Wait for the thread to stop
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt(); // Restore interrupted status
			}
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
