package gui.controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import gui.view.ApplicationViewType;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import logic.ClientRequestDataContainer;
import logic.EntitiesContainer;
import logic.Guide;
import logic.ICustomer;
import logic.Order;
import logic.SceneLoaderHelper;
import logic.ServerResponseBackToClient;
import logic.Visitor;
import utils.AlertPopUp;
import utils.CurrentDateAndTime;
import utils.ValidationRules;
import utils.enums.ClientRequest;
import utils.enums.OrderStatusEnum;
import utils.enums.OrderTypeEnum;
import utils.enums.ParkNameEnum;

/**
 * Controller class for handling order management operations, such as canceling,
 * confirming, and updating orders.
 */
public class HandleOrderScreenController implements Initializable {
	@FXML
	public Label dateLabel;

	@FXML
	public TextField firstNameField;
	@FXML
	public TextField lastNameField;
	@FXML
	public TextField idField;
	@FXML
	public TextField phoneNumberField;
	@FXML
	public TextField emailField;
	@FXML
	public TextField orderDateOfVisitField;
	@FXML
	public DatePicker pickDate;
	@FXML
	public TextField numberOfVisitorsField;
	@FXML
	public TextField statusField;
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;
	@FXML
	public Button cancelButton;
	@FXML
	public Button confirmButton;
	@FXML
	public Button updateButton;

	private BorderPane screen;
	private ParkNameEnum selectedPark;
	private String selectedTime;
	private OrderTypeEnum selectedVisitType;
	private Order requestedOrder;

	@FXML
	public ComboBox<ParkNameEnum> parksList;
	@FXML
	public ComboBox<String> pickTime;
	@FXML
	public ComboBox<OrderTypeEnum> visitType;

	private ObservableList<ParkNameEnum> parks = FXCollections.observableArrayList(ParkNameEnum.Banias,
			ParkNameEnum.Herodium, ParkNameEnum.Masada);

	private ObservableList<String> timeForVisits = FXCollections.observableArrayList("09:00", "10:00", "11:00", "12:00",
			"13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00", "21:00", "22:00");

	private ObservableList<OrderTypeEnum> visitTypesList = FXCollections.observableArrayList();

	private ICustomer customer;

	/**
	 * Constructor for HandleOrderScreenController.
	 * 
	 * @param screen The BorderPane representing the screen layout.
	 * @param order  The order being managed.
	 * @param info   The customer information associated with the order.
	 */
	public HandleOrderScreenController(BorderPane screen, Object order, ICustomer info) {
		this.screen = screen;
		requestedOrder = (Order) order;
		this.customer = info;
	}

	/**
	 * Initializes the controller.
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		parksList.getItems().addAll(parks);
		parksList.setOnAction(this::onParkChangeSelection);
		pickTime.getItems().addAll(timeForVisits);
		pickTime.setOnAction(this::onTimeChangeSelection);

		switch (customer.getUserType()) {
		case Visitor:
			visitTypesList.add(OrderTypeEnum.Solo_PreOrder);
			visitTypesList.add(OrderTypeEnum.Family_PreOrder);
			customer = (Visitor) customer;
			break;
		case Guide:
			visitTypesList.add(OrderTypeEnum.Group_PreOrder);
			customer = (Guide) customer;
			break;
		}
		visitType.getItems().addAll(visitTypesList);
		visitType.setOnAction(this::onVisitTypeChangeSelection);
		initializeAllGuiFields();
		enableGuiAccordingToStatus();
		hideErrorMessage();
	}

	/**
	 * Enables or disables GUI components based on the status of the requested
	 * order.
	 */
	@SuppressWarnings("incomplete-switch")
	private void enableGuiAccordingToStatus() {
		switch (requestedOrder.getStatus()) {
		case Notified:
		case Notified_Waiting_List:
			updateButton.setDisable(true);
			break;
		case Wait_Notify:
		case In_Waiting_List:
			confirmButton.setDisable(true);
			break;
		case Confirmed:
			confirmButton.setDisable(true);
			updateButton.setDisable(true);
			break;
		}
	}

	/**
	 * Event handler for the park selection change event.
	 * 
	 * @param event The ActionEvent representing the event.
	 */
	private void onParkChangeSelection(ActionEvent event) {
		if (parksList.getValue() instanceof ParkNameEnum) {
			selectedPark = parksList.getValue();
		} else {
			// Handle the error scenario, maybe log a warning or throw a custom exception
		}
	}

	/**
	 * Event handler for the time selection change event.
	 * 
	 * @param event The ActionEvent representing the event.
	 */
	private void onTimeChangeSelection(ActionEvent event) {
		if (pickTime.getValue() instanceof String) {
			selectedTime = pickTime.getValue();
		} else {
			// Handle the error scenario, maybe log a warning or throw a custom exception
		}
	}

	/**
	 * Event handler for the visit type selection change event.
	 * 
	 * @param event The ActionEvent representing the event.
	 */
	private void onVisitTypeChangeSelection(ActionEvent event) {
		if (visitType.getValue() instanceof OrderTypeEnum) {
			selectedVisitType = visitType.getValue();
		} else {
			// Handle the error scenario, maybe log a warning or throw a custom exception
		}

	}

	/**
	 * Initializes all GUI fields with data from the requested order.
	 */
	private void initializeAllGuiFields() {
		parksList.setValue(requestedOrder.getParkName());
		selectedPark = requestedOrder.getParkName();
		firstNameField.setText(requestedOrder.getFirstName());
		lastNameField.setText(requestedOrder.getLastName());
		idField.setText(requestedOrder.getUserId());
		phoneNumberField.setText(requestedOrder.getTelephoneNumber());
		emailField.setText(requestedOrder.getEmail());
		LocalDateTime orderTime = requestedOrder.getEnterDate();
		LocalDate enterTime = orderTime.toLocalDate();
		String time = orderTime.toString().split("T")[1];
		pickDate.setValue(enterTime);
		pickTime.setValue(time);
		selectedTime = time;
		numberOfVisitorsField.setText(String.format("%d", requestedOrder.getNumberOfVisitors()));
		visitType.setValue(requestedOrder.getOrderType());
		selectedVisitType = requestedOrder.getOrderType();
		statusField.setText(requestedOrder.getStatus().toString());

		// initialize date picker up to 1 months forward.
		pickDate.setDayCellFactory(picker -> new DateCell() {
			LocalDate maxDate = requestedOrder.getEnterDate().toLocalDate().plusMonths(1);
			LocalDate minDate = requestedOrder.getEnterDate().toLocalDate();

			@Override
			public void updateItem(LocalDate date, boolean empty) {
				super.updateItem(date, empty);
				setDisable(empty || date.compareTo(minDate) <= 0 || date.compareTo(maxDate) > 0);
			}
		});
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
	 * @param error The error message to be displayed.
	 */
	private void showErrorMessage(String error) {
		errorSection.setVisible(true);
		errorMessageLabel.setText(error);
	}

	/**
	 * Creates an Order object from the data entered in the GUI fields.
	 * 
	 * @return The created Order object.
	 */
	private Order createOrderFromFields() {
		// TODO: check all fields validation.
		Order order = new Order();
		order.setParkName(selectedPark);
		order.setOwnerType((customer.getUserType()));
		order.setFirstName(firstNameField.getText());
		order.setLastName(lastNameField.getText());
		order.setUserId(idField.getText());
		order.setPaid(false);
		order.setStatus(OrderStatusEnum.Wait_Notify);

		order.setTelephoneNumber(phoneNumberField.getText());
		order.setEmail(emailField.getText());
		LocalDate date = pickDate.getValue();
		String fullDateTime = date.toString() + "T" + selectedTime;
		order.setEnterDate(LocalDateTime.parse(fullDateTime));
		order.setNumberOfVisitors(Integer.parseInt(numberOfVisitorsField.getText()));
		order.setOrderType(selectedVisitType);
		return order;
	}

	/**
	 * Validates the input data in the GUI fields.
	 * 
	 * @return True if all fields are valid, false otherwise.
	 */
	private boolean validateGuiFields() {
		if (selectedTime.equals("") || selectedPark == ParkNameEnum.None || selectedVisitType == OrderTypeEnum.None
				|| firstNameField.getText().equals("") || lastNameField.getText().equals("")
				|| idField.getText().equals("") || phoneNumberField.getText().equals("")
				|| emailField.getText().equals("") || pickDate.getValue() == null
				|| numberOfVisitorsField.getText().equals("")) {
			showErrorMessage("All Fields must be filled!");
			return false;
		}

		if (ValidationRules.isValidIsraeliId(idField.getText()) == false) {
			showErrorMessage("ID is not valid israeli ID");
			return false;
		}

		if (ValidationRules.isValidName(firstNameField.getText()) == false
				|| ValidationRules.isValidName(lastNameField.getText()) == false) {
			showErrorMessage("Name should be only letters");
			return false;
		}

		if (ValidationRules.isValidEmail(emailField.getText()) == false) {
			showErrorMessage("Invalid email");
			return false;
		}

		if (ValidationRules.isValidPhone(phoneNumberField.getText()) == false) {
			showErrorMessage("Invalid phone, should be 10 digits");
			return false;
		}

		if (ValidationRules.isPositiveNumeric(numberOfVisitorsField.getText()) == false) {
			showErrorMessage("Number of Visitors should be positive number (above 0)!");
			return false;
		}

		if (selectedVisitType == OrderTypeEnum.Group_PreOrder
				&& Integer.parseInt(numberOfVisitorsField.getText()) > 15) {
			showErrorMessage("Group order is limited up to 15 visitors");
			return false;
		}

		return true;
	}

	/**
	 * Cancels the order when the "Cancel" button is clicked.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onCancelClicked() {
		AlertPopUp alert = new AlertPopUp(AlertType.CONFIRMATION, "Manage Order", "Cancel Order", "Are you sure?",
				ButtonType.YES, ButtonType.CLOSE);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.CLOSE) {
			return;
		}

		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Update_Order_Status_Canceled,
				requestedOrder);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		switch (response.getRensponse()) {
		case Order_Cancelled_Successfully:
			alert = new AlertPopUp(AlertType.INFORMATION, "Notification", "Cancel Order", "Your Order was cancelled");
			alert.showAndWait();
			break;

		case Order_Cancelled_Failed:
			alert = new AlertPopUp(AlertType.ERROR, "Notification", "Cancel Order", "Your request was Failed!");
			alert.showAndWait();
			break;
		}

		AnchorPane view = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/CustomerHomepageScreen.fxml", ApplicationViewType.Customer_Homepage_Screen, null);
		screen.setCenter(view);

	}

	/**
	 * Confirms the order when the "Confirm" button is clicked.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onConfirmClicked() {
		AlertPopUp alert = new AlertPopUp(AlertType.CONFIRMATION, "Manage Order", "Confirm Order", "Are you sure?",
				ButtonType.YES, ButtonType.CLOSE);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.CLOSE) {
			return;
		}

		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Update_Order_Status_Confirmed,
				requestedOrder);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		switch (response.getRensponse()) {
		case Order_Updated_Successfully:
			alert = new AlertPopUp(AlertType.INFORMATION, "Notification", "Confirm Order", "Your Order was confirmed!");
			alert.showAndWait();
			break;

		case Order_Updated_Failed:
			alert = new AlertPopUp(AlertType.ERROR, "Notification", "Confirm Order", "Your request was Failed!");
			alert.showAndWait();
			break;
		}
		AnchorPane view = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/CustomerHomepageScreen.fxml", ApplicationViewType.Customer_Homepage_Screen, null);
		screen.setCenter(view);

	}

	/**
	 * Updates the order when the "Update" button is clicked.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onUpdateClicked() {
		AlertPopUp alert;
		if (!validateGuiFields()) {
			return;
		}

		LocalDateTime relevantDayToNewOrder = requestedOrder.getEnterDate().plusDays(2);
		LocalDate date = pickDate.getValue();
		String fullDateTime = date.toString() + "T" + selectedTime;
		LocalDateTime newOrderDate = LocalDateTime.parse(fullDateTime);
		if (newOrderDate.isBefore(relevantDayToNewOrder)) {
			alert = new AlertPopUp(AlertType.WARNING, "Update New Order", "Date and Time Incorrect",
					"In case you want to update an order, it must be atleast 2 days forward");
			alert.showAndWait();
			return;
		}

		Order newOrder = createOrderFromFields();
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Add_New_Order_If_Available,
				newOrder);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		AnchorPane view;

		alert = new AlertPopUp(AlertType.CONFIRMATION, "Manage Order", "Update Order",
				"Your old order will be deleted, Are you sure?", ButtonType.YES, ButtonType.CLOSE);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == ButtonType.CLOSE) {
			return;
		}

		switch (response.getRensponse()) {
		case Requested_Order_Date_Is_Available:
			request = new ClientRequestDataContainer(ClientRequest.Delete_Old_Order, requestedOrder);
			ClientApplication.client.accept(request);
			view = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
					"/gui/view/OrderSummaryScreen.fxml", ApplicationViewType.Order_Summary_Screen,
					new EntitiesContainer(response.getMessage()));
			screen.setCenter(view);
			break;

		case Requested_Order_Date_Unavaliable:
			alert = new AlertPopUp(AlertType.INFORMATION, "Notification", "Date Unavailable",
					"Sorry, this date is unavailable, your last order still up!");
			alert.showAndWait();
			break;
		case Too_Many_Visitors:

			alert = new AlertPopUp(AlertType.INFORMATION, "Notification", "Order Limit",
					"This are too many visitors for our park,your last order still up!");
			alert.showAndWait();
			break;
		}

	}
}