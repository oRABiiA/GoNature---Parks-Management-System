package gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import gui.view.ApplicationViewType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import logic.ClientRequestDataContainer;
import logic.EntitiesContainer;
import logic.ExternalUser;
import logic.Guide;
import logic.ICustomer;
import logic.Order;
import logic.SceneLoaderHelper;
import logic.ServerResponseBackToClient;
import logic.Visitor;
import utils.AlertPopUp;
import utils.CurrentWindow;
import utils.enums.ClientRequest;
import utils.enums.OrderStatusEnum;
import utils.enums.UserTypeEnum;

/**
 * Controller class responsible for managing the UI interactions on the customer
 * screen. Implements the Initializable interface to initialize JavaFX
 * components. Implements the IScreenController interface for screen management.
 */
public class CustomerScreenController implements Initializable, IScreenController {

	@FXML
	public BorderPane screen;
	@FXML
	public Label userIdLabel;
	@FXML
	public Label accountTypeLabel;
	@FXML
	public Button homeButton;
	@FXML
	public Button makeOrderButton;
	@FXML
	public Button manageOrdersButton;
	@FXML
	public Button backButton;
	@FXML
	public Button logoutButton;
	@FXML
	public Button notificationButton;

//	private Customer customer;
	private ExternalUser customer;
	@SuppressWarnings("unused")
	private UserTypeEnum currentCustomer;
	private ICustomer customerDetails = null;

	/**
	 * Constructs a new CustomerScreenController with the given customer.
	 * 
	 * @param customer The customer associated with this controller.
	 */
	public CustomerScreenController(ExternalUser customer) {
		this.customer = customer;
		currentCustomer = customer.getUserType();
	}

	/**
	 * Initializes the controller after its root element has been completely
	 * processed. This method is invoked by the FXMLLoader when the associated FXML
	 * file is loaded. It initializes UI components and hides buttons based on the
	 * type of customer.
	 * 
	 * @param location  The location used to resolve relative paths for the root
	 *                  object.
	 * @param resources The resources used to localize the root object.
	 */
	@SuppressWarnings("incomplete-switch")
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO: hide buttons according to user type
		switch (customer.getUserType()) {
		case ExternalUser:
			updateNewVisitorMenu(customer);
			break;
		case Guide:
			updateGuideMenu((Guide) customer);
			break;
		case Visitor:
			updateVisitorMenu((Visitor) customer);
			break;
		}
		onHomeClicked();

	}

	/**
	 * Updates the UI to display the menu for a visitor user.
	 * 
	 * @param visitor The visitor user.
	 */
	private void updateVisitorMenu(Visitor visitor) {
		userIdLabel.setText(visitor.getVisitorId());
		accountTypeLabel.setText(visitor.getUserType().toString());
		logoutButton.setVisible(false);
		customerDetails = (Visitor) customer;
	}

	/**
	 * Updates the UI to display the menu for a guide user.
	 * 
	 * @param guide The guide user.
	 */
	private void updateGuideMenu(Guide guide) {
		userIdLabel.setText(guide.getUserId());
		accountTypeLabel.setText(guide.getUserType().toString());
		backButton.setVisible(false);
		customerDetails = (Guide) customer;
	}

	/**
	 * Updates the UI to display the menu for a new visitor.
	 * 
	 * @param newVisitor The new visitor user.
	 */
	private void updateNewVisitorMenu(ExternalUser newVisitor) {
		userIdLabel.setText("???");
		accountTypeLabel.setText("New Guest");
		manageOrdersButton.setVisible(false);
		notificationButton.setVisible(false);
		notificationButton.setManaged(false);
		logoutButton.setVisible(false);
	}

	/**
	 * Event handler triggered when the "Back" button is clicked. Navigates back to
	 * the previous screen or performs logout.
	 */
	public void onBackClicked() {
		if (customer.getUserType() == UserTypeEnum.ExternalUser) {
			SceneLoaderHelper.getInstance().setScreenAfterLogoutOrBack();
		} else
			onLogoutClicked();
	}

	/**
	 * Event handler triggered when the "Home" button is clicked. Loads the customer
	 * homepage screen.
	 */
	public void onHomeClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/CustomerHomepageScreen.fxml", ApplicationViewType.Customer_Homepage_Screen,
				new EntitiesContainer(customer));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Make Order" button is clicked. Loads the
	 * make order screen.
	 */
	public void onMakeOrderClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/MakeOrderScreen.fxml", ApplicationViewType.Make_Order_Screen,
				new EntitiesContainer(customer, customerDetails));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Manage Orders" button is clicked. Loads the
	 * manage orders screen.
	 */
	public void onManageOrdersClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/IdenticationScreen.fxml", ApplicationViewType.Identication_Screen,
				new EntitiesContainer(customer));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Logout" button is clicked. Performs logout.
	 */
	public void onLogoutClicked() {
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Logout, customer);
		ClientApplication.client.accept(request);
		SceneLoaderHelper.getInstance().setScreenAfterLogoutOrBack();
	}

	/**
	 * Event handler triggered when the server crashes. Displays a notification to
	 * the user and closes the application.
	 */
	public void onServerCrashed() {
		AlertPopUp alert = new AlertPopUp(AlertType.ERROR, "FATAL ERROR", "Server is Down",
				"Server Crashed - The application will be closed.");
		alert.showAndWait();
		try {
			ClientApplication.client.getClient().closeConnection();
			Platform.runLater(() -> CurrentWindow.getCurrentWindow().close());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Event handler triggered when the "Notification" button is clicked. Retrieves
	 * notifications for the customer from the server. Displays the notifications to
	 * the user.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onNotificationButtonClicked() {
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Search_For_Notified_Orders,
				customerDetails);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		AlertPopUp alert;

		switch (response.getRensponse()) {
		case No_Notifications_Found:
			alert = new AlertPopUp(AlertType.INFORMATION, "Notification", "There is no Notifications", "");
			alert.showAndWait();
			return;

		case Notifications_Found:
			StringBuilder sb = new StringBuilder();
			@SuppressWarnings("unchecked")
			ArrayList<Order> ordersWithNotification = (ArrayList<Order>) response.getMessage();
			int line = 1;
			for (Order order : ordersWithNotification) {
				if (order.getStatus() == OrderStatusEnum.Notified_Waiting_List) {
					sb.append(String.format(
							"%d. Order : %d, to %s at %s of %d, have available spot from waiting list. wait for confirmation\n",
							line++, order.getOrderId(), order.getParkName().name(), order.getEnterDate().toString(),
							order.getNumberOfVisitors()));
				} else
					// OrderId,ParkId,EnterDate,PayStatus,Amount
					sb.append(String.format("%d. Order : %d, to %s at %s of %d participants wait for confirmation.\n",
							line++, order.getOrderId(), order.getParkName().name(), order.getEnterDate().toString(),
							order.getNumberOfVisitors()));
			}

			alert = new AlertPopUp(AlertType.INFORMATION, "Notification", "You have new Notification", sb.toString());
			alert.showAndWait();
			return;
		}

	}

	/**
	 * Handles the process of closing the client application. Initiates a logout
	 * request to the server and closes the client's connection. This method is
	 * typically called when the user intends to exit the application.
	 */
	@Override
	public void onCloseApplication() {
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Logout, customer);
		ClientApplication.client.accept(request);
		try {
			ClientApplication.client.getClient().closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
