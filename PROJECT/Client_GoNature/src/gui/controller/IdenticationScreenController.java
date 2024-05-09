package gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import gui.view.ApplicationViewType;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import logic.ClientRequestDataContainer;
import logic.EntitiesContainer;
import logic.ExternalUser;
import logic.Guide;
import logic.ICustomer;
import logic.Order;
import logic.SceneLoaderHelper;
import logic.ServerResponseBackToClient;
import logic.Visitor;
import utils.CurrentDateAndTime;
import utils.ValidationRules;
import utils.enums.ClientRequest;
import utils.enums.UserTypeEnum;

/**
 * Controller class for the Identification Screen.
 */
public class IdenticationScreenController implements Initializable {

	@FXML
	public Label dateLabel;
	@FXML
	public TextField orderIdField;
	@FXML
	public Button searchButton;
	@FXML
	public HBox errorSection;
	@FXML
	public Label errorMessageLabel;

	private BorderPane screen;
	private ExternalUser customer;

	/**
	 * Constructs a new IdenticationScreenController.
	 * 
	 * @param screen   The BorderPane representing the screen layout.
	 * @param customer The ExternalUser object representing the current user.
	 */
	public IdenticationScreenController(BorderPane screen, Object customer) {
		this.screen = screen;
		this.customer = (ExternalUser) customer;
	}

	/**
	 * Initializes the controller.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		dateLabel.setText(CurrentDateAndTime.getCurrentDate("'Today' yyyy-MM-dd"));
		hideErrorMessage();

	}

	/**
	 * Event handler for the search button click event.
	 */
	@SuppressWarnings("incomplete-switch")
	public void onSearchClicked() {
		if (ValidationRules.isFieldEmpty(orderIdField.getText())) {
			showErrorMessage("Order ID cannot be empty!");
			return;
		}
		if (!ValidationRules.isNumeric(orderIdField.getText())) {
			showErrorMessage("Order ID must contain only digits");
			return;
		}
		Order order = new Order(orderIdField.getText());
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Search_For_Relevant_Order,
				order);
		ClientApplication.client.accept(request);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;

		switch (response.getRensponse()) {
		case Order_Not_Found:
			showErrorMessage("Such Order does not exist!");
			return;
		case Order_Found:
			ICustomer currentCustomer = null;
			if (((Order) response.getMessage()).getOwnerType().name().equals("Visitor")
					&& customer.getUserType() == UserTypeEnum.Visitor) {
				currentCustomer = (Visitor) customer;
				if (!currentCustomer.getCustomerId().equals(((Order) response.getMessage()).getUserId())) {
					showErrorMessage("This Order does not belong to you");
					return;
				}
			} else if (((Order) response.getMessage()).getOwnerType().name().equals("Guide")
					&& customer.getUserType() == UserTypeEnum.Guide) {
				currentCustomer = (Guide) customer;
				if (!currentCustomer.getCustomerId().equals(((Order) response.getMessage()).getUserId())) {
					showErrorMessage("This Order does not belong to you");
					return;
				}
			} else {
				showErrorMessage("This Order does not belong to you");
				return;
			}

			AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
					"/gui/view/HandleOrderScreen.fxml", ApplicationViewType.Handle_Order_Screen,
					new EntitiesContainer((Order) response.getMessage(), currentCustomer));
			screen.setCenter(dashboard);
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
	 * @param error The error message to be displayed.
	 */
	private void showErrorMessage(String error) {
		errorSection.setVisible(true);
		errorMessageLabel.setText(error);
	}

}
