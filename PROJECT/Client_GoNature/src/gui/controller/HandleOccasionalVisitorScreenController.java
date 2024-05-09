package gui.controller;

import java.net.URL;
import java.time.Duration;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import logic.ClientRequestDataContainer;
import logic.Employee;
import logic.EntitiesContainer;
import logic.Order;
import logic.SceneLoaderHelper;
import logic.ServerResponseBackToClient;
import utils.AlertPopUp;
import utils.CurrentDateAndTime;
import utils.EntranceDiscount;
import utils.NotificationMessageTemplate;
import utils.ValidationRules;
import utils.enums.ClientRequest;
import utils.enums.OrderStatusEnum;
import utils.enums.OrderTypeEnum;
import utils.enums.ParkNameEnum;
import utils.enums.ServerResponse;
import utils.enums.UserTypeEnum;

/**
 * Controller class for handling occasional visitor requests and processing new
 * occasional visits.
 */
public class HandleOccasionalVisitorScreenController implements Initializable {

	@FXML
	public Label dateLabel;
	@FXML
	public ComboBox<ParkNameEnum> parksList;
	@FXML
	public TextField firstNameField;
	@FXML
	public TextField lastNameField;
	@FXML
	public TextField idField;
	@FXML
	public TextField phoneNumberField;
	@FXML
	public TextField numberOfVisitorsField;
	@FXML
	public ComboBox<OrderTypeEnum> visitType;
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;
	@FXML
	public Button newVisitButton;

	private BorderPane screen;
	private Employee employee;
	private ParkNameEnum selectedPark = ParkNameEnum.None;
	private OrderTypeEnum selectedVisitType = OrderTypeEnum.None;
	private ObservableList<ParkNameEnum> parks = FXCollections.observableArrayList();
	private ObservableList<OrderTypeEnum> visitsList = FXCollections.observableArrayList(OrderTypeEnum.Solo_Occasional,
			OrderTypeEnum.Family_Occasional, OrderTypeEnum.Group_Occasional);

	/**
	 * Constructor for HandleOccasionalVisitorScreenController.
	 * 
	 * @param screen   The BorderPane representing the screen layout.
	 * @param employee The employee managing the occasional visitor request.
	 */
	public HandleOccasionalVisitorScreenController(BorderPane screen, Object employee) {
		this.screen = screen;
		this.employee = (Employee) employee;
	}

	/**
	 * Initializes the controller.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		parks.add(employee.getRelatedPark());
		visitType.setItems(visitsList);
		visitType.setOnAction(this::onVisitTypeChangeSelection);
		parksList.setItems(parks);
		parksList.setValue(employee.getRelatedPark());
		selectedPark = employee.getRelatedPark();
		hideErrorMessage();
	}

	/**
	 * Event handler triggered when the visit type selection is changed.
	 * 
	 * @param event The ActionEvent representing the event.
	 */
	private void onVisitTypeChangeSelection(ActionEvent event) {
		selectedVisitType = visitType.getValue();
	}

	/**
	 * Event handler triggered when the "New Visit" button is clicked.
	 */
	public void onNewVisitClicked() {
		if (!validateGuiFields()) {
			return;
		}

		Order order = createOrderFromFields();
		AlertPopUp alert;
		ClientRequestDataContainer requestMessage = new ClientRequestDataContainer(
				ClientRequest.Prepare_New_Occasional_Order, order);
		ClientApplication.client.accept(requestMessage);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		if(response.getRensponse()==ServerResponse.Park_Is_Full_For_Such_Occasional_Order) {
			alert = new AlertPopUp(AlertType.WARNING,"Warning","Can't create Occasional Order","Can't make such order, you exceed the park max capacity");
			alert.showAndWait();
			return;
		}
		
		order = (Order) response.getMessage();

		ButtonType payNow = new ButtonType("Pay Now");
		double price = calculatePriceByOrderType(order);
		Duration duration = Duration.between(order.getEnterDate(), order.getExitDate());
		long estimatedVisitTime = duration.toHours();

		String paymentReceipt = NotificationMessageTemplate.entrancePaymentReceiptMessage(order.getParkName(),
				order.getOrderType(), order.getFirstName(), order.getLastName(), order.getNumberOfVisitors(), price,
				estimatedVisitTime);

		alert = new AlertPopUp(AlertType.CONFIRMATION, "Payment Notification", "Pay Now", paymentReceipt,
				payNow, ButtonType.CLOSE);
		Optional<ButtonType> result = alert.showAndWait();

		if (result.isPresent() && result.get() == payNow) {
			order.setPrice(price);
			requestMessage = new ClientRequestDataContainer(ClientRequest.Add_Occasional_Visit_As_In_Park, order);
			ClientApplication.client.accept(requestMessage);
			response = ClientCommunication.responseFromServer;
		}

		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/ParkEntranceScreen.fxml", ApplicationViewType.Park_Entrance_Screen,
				new EntitiesContainer(employee));
		screen.setCenter(dashboard);
	}

	/**
	 * Validates the input fields in the GUI.
	 * 
	 * @return True if all fields are valid, false otherwise.
	 */
	private boolean validateGuiFields() {
		if (selectedPark == ParkNameEnum.None || selectedVisitType == OrderTypeEnum.None
				|| firstNameField.getText().equals("") || lastNameField.getText().equals("")
				|| idField.getText().equals("") || phoneNumberField.getText().equals("")
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

		if (ValidationRules.isValidPhone(phoneNumberField.getText()) == false) {
			showErrorMessage("Invalid phone, should be 10 digits");
			return false;
		}

		if (ValidationRules.isPositiveNumeric(numberOfVisitorsField.getText()) == false) {
			showErrorMessage("Number of Visitors should be positive number (above 0)!");
			return false;
		}

		if (selectedVisitType == OrderTypeEnum.Group_Occasional
				&& Integer.parseInt(numberOfVisitorsField.getText()) > 15) {
			showErrorMessage("Group order is limited up to 15 visitors");
			return false;
		}

		if (Integer.parseInt(numberOfVisitorsField.getText()) > 1
				&& selectedVisitType == OrderTypeEnum.Solo_Occasional) {
			showErrorMessage("Solo Occasional should be an order for only 1 person");
			return false;
		}

		return true;
	}

	/**
	 * Creates an Order object from the input fields in the GUI.
	 * 
	 * @return The created Order object.
	 */
	private Order createOrderFromFields() {
		// TODO: check all fields validation.
		Order order = new Order();
		order.setParkName(selectedPark);
		order.setOwnerType(UserTypeEnum.Occasional);
		order.setFirstName(firstNameField.getText());
		order.setLastName(lastNameField.getText());
		order.setUserId(idField.getText());
		order.setPaid(false);
		order.setStatus(OrderStatusEnum.In_Park);

		order.setTelephoneNumber(phoneNumberField.getText());
		order.setEnterDate(LocalDateTime.now());
		order.setNumberOfVisitors(Integer.parseInt(numberOfVisitorsField.getText()));
		order.setOrderType(selectedVisitType);
		return order;
	}

	/**
	 * Hides the error message section.
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

	/**
	 * Calculates the price for the occasional visit order based on its type.
	 * 
	 * @param order The Order object for which to calculate the price.
	 * @return The calculated price.
	 */
	@SuppressWarnings("incomplete-switch")
	private double calculatePriceByOrderType(Order order) {
		double priceAtEntrance = 0;

		switch (order.getOrderType()) {
		case Solo_Occasional:
		case Family_Occasional:
			priceAtEntrance = order.getPrice() * order.getNumberOfVisitors()
					* EntranceDiscount.SOLO__FAMILY_OCCASIONAL_DISCOUNT;
			break;
		case Group_Occasional:
			priceAtEntrance = order.getPrice() * (order.getNumberOfVisitors() + 1)
					* EntranceDiscount.GROUP_OCCASIONAL_DISCOUNT;
			break;
		}
		return priceAtEntrance;

	}
}
