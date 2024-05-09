package gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import client.ClientApplication;
import client.ClientCommunication;
import client.ClientMainControl;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import logic.ClientRequestDataContainer;
import logic.Employee;
import logic.ExternalUser;
import logic.Guide;
import logic.ServerResponseBackToClient;
import logic.Visitor;
import utils.AlertPopUp;
import utils.CurrentWindow;
import utils.ValidationRules;
import utils.enums.ClientRequest;
import utils.enums.UserTypeEnum;


/**
* LandingPageScreenController is a controller class that handles the landing page screen of the GUI application.
* It implements the Initializable and IScreenController interfaces.
*/
public class LandingPageScreenController implements Initializable,IScreenController {
	
	 // FXML fields for various UI components
	
	@FXML
	public ImageView icon;
	@FXML
	public Button backToOptionsButton;
	@FXML
	public ImageView backImage;
	
	/* Server Connect Section */
	@FXML
	public Button connectButton;
	@FXML
	public VBox connectToServerVbox;
	@FXML
	public TextField serverIpField;
	@FXML
	public TextField serverPortField;
	
	/* Choose Options Section */
	@FXML
	public Button optionLoginButton;
	@FXML
	public Button optionMakeOrderButton;
	@FXML
	public VBox optionsVbox;
	
	/* Login Application Section */
	@FXML
	public Button loginButton;
	@FXML
	public ComboBox<UserTypeEnum> accountTypeComboBox;
	@FXML
	public TextField visitorField;
	@FXML
	public TextField usernameField;
	@FXML
	public TextField passwordField;
	@FXML
	public HBox employeeOrGuideHbox;
	@FXML
	public VBox loginVbox;
	
	/*Error Section*/
	@FXML
	public Pane errorPane;
	@FXML
	public Label errorMessageLabel;
	
	/* Additional Members*/
	private ObservableList<UserTypeEnum> accountTypes = FXCollections.observableArrayList(
			UserTypeEnum.Visitor,
			UserTypeEnum.Guide,
			UserTypeEnum.Employee
			);
	
	private UserTypeEnum currentUser;
	
	public LandingPageScreenController() {}
	
	
   /**
    * Initializes the controller class and sets up the necessary components.
    *
    * @param location  The location used to resolve relative paths for the root object, or null if the location is not known.
    * @param resources The resources used to localize the root object, or null if the root object was not localized.
    */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		accountTypeComboBox.getItems().addAll(accountTypes);
		accountTypeComboBox.setOnAction(this::onChangeSelection);
		hideErrorMessage();
	}
	
	/**
    * Handles the action when the user selects an account type from the combo box.
    *
    * @param event The action event triggered by the combo box selection.
    */
	@SuppressWarnings("incomplete-switch")
	private void onChangeSelection(ActionEvent event) {
		UserTypeEnum account = accountTypeComboBox.getValue();
		switch(account) {
		case Employee:
		case Guide:
			employeeOrGuideHbox.setVisible(true);
			visitorField.setVisible(false);
			currentUser=account;
			return;
		case Visitor:
			employeeOrGuideHbox.setVisible(false);
			visitorField.setVisible(true);
			currentUser=account;
			return;
		}
	}
	
	/**
    * Handles the action when the user clicks the "Connect to Server" button.
    */
	public void onConnectToServerClicked() {
		String message="";
		boolean isValidIp=ValidationRules.isValidIp(serverIpField.getText());
		boolean isValidPort=ValidationRules.isValidPort(serverPortField.getText());
		if(!isValidIp||!isValidPort) {
			message = (!isValidIp)? message + "Server IP isn't valid\n":message;
			message= (!isValidPort)? message + "Server Port isn't valid\n":message;
			errorPane.setVisible(true);
			errorMessageLabel.setText(message);
			return;
		}
		
		ClientApplication.client = new ClientMainControl(serverIpField.getText(), Integer.parseInt(serverPortField.getText()));
		if(ClientApplication.client.getClient()==null) {
			AlertPopUp alert = new AlertPopUp(AlertType.ERROR, "Error", "Connect To Server", "Failed to connect the server");
			alert.showAndWait();
			return;
		}
		AlertPopUp alert = new AlertPopUp(AlertType.INFORMATION,"Success","Connect To Server","Connected Success!");
		alert.showAndWait();
		connectToServerVbox.setVisible(false);
		optionsVbox.setVisible(true);
		hideErrorMessage();
	}
	
	/**
    * Hides the error message on the GUI.
    */
	private void hideErrorMessage() {
		errorMessageLabel.setText("");
		errorPane.setVisible(false);
	}
	
	/**
    * Shows the error message on the GUI.
    *
    * @param error The error message to be displayed.
    */
	private void showErrorMessage(String error) {
		errorPane.setVisible(true);
		errorMessageLabel.setText(error);
	}
	
	/**
    * Handles the action when the user clicks the "Login" option.
    */
	public void onOptionLoginClicked() {
		clearLoginFields();
		optionsVbox.setVisible(false);
		loginVbox.setVisible(true);
		backToOptionsButton.setVisible(true);
		hideErrorMessage();
	}
	
	/**
    * Handles the action when the user clicks the "Make Order" option.
    */
	public void onOptionMakeOrderClicked() {
		//open screen for new visitor
		currentUser=UserTypeEnum.ExternalUser;
		switchMainScreenAccordingToUserLogin(new ExternalUser());
	}
	
	/**
    * Validates the input fields on the GUI.
    *
    * @param fields An ArrayList to store the input field values.
    * @return true if the input fields are valid, false otherwise.
    */
	private boolean validateGuiFields(ArrayList<String> fields) {
		String message="";
		boolean areFieldsEmpty=true;
		boolean isValidUsername=true;
		boolean isValidPassword=true;
		boolean isValidId=true;
		
		if(null==accountTypeComboBox.getValue()) {
			showErrorMessage("Must Select Account");
			return false;
		}
		
		if(currentUser==UserTypeEnum.Visitor) {
			fields.add(visitorField.getText());
			isValidId=ValidationRules.isValidId(fields.get(0));
		}
		else {
			fields.add(usernameField.getText());
			fields.add(passwordField.getText());
			isValidUsername = ValidationRules.isValidUsername(fields.get(0));
			isValidPassword = ValidationRules.isValidPassword(fields.get(1));
		}
		
		areFieldsEmpty = ValidationRules.areFieldsEmpty(fields);

		if(areFieldsEmpty) {
			showErrorMessage("All Fields Must be Filled!");
			return false;
		}
		
		if(!isValidId) {
			showErrorMessage("ID can contain only digits!");
			return false;
		}
		
		if(!isValidUsername || !isValidPassword) {
			message = (isValidUsername) ? message : message+"Username must be Upper,Lower case or digits\n";
			message = (isValidPassword) ? message : message+"Password must be 6 digits atleast";
			showErrorMessage(message);
			return false;
		}
		
		return true;

	}
	
	/**
    * Handles the action when the user clicks the "Login" button.
    */
	@SuppressWarnings("incomplete-switch")
	public void onLoginClicked() {
		ArrayList<String> fields = new ArrayList<String>();
		if(!validateGuiFields(fields)) {
			return;
		}
		
		ExternalUser requestedUser;
		ClientRequestDataContainer requestMessage=new ClientRequestDataContainer();
		switch(currentUser) {
			case Employee:
				requestedUser = new Employee(fields.get(0),fields.get(1));
				requestMessage.setRequest(ClientRequest.Login_As_Employee);
				requestMessage.setMessage(requestedUser);
				break;
			case Guide:
				requestedUser = new Guide(fields.get(0),fields.get(1));
				requestMessage.setRequest(ClientRequest.Login_As_Guide);
				requestMessage.setMessage(requestedUser);
				break;
			case Visitor:
				requestedUser = new Visitor(fields.get(0));
				requestMessage.setRequest(ClientRequest.Login_As_Visitor);
				requestMessage.setMessage(requestedUser);
				break;
		}
		
		ClientApplication.client.accept(requestMessage);
		ServerResponseBackToClient response = ClientCommunication.responseFromServer;
		switch(response.getRensponse()) {
			case Password_Incorrect:
				showErrorMessage("Password is incorrect!");
				return;
			case User_Already_Connected:
				showErrorMessage("User already connected!");
				return;
			case User_Does_Not_Found:
				showErrorMessage("Such user does not exists!");
				return;
			case Guide_Status_Pending:
				showErrorMessage("Not approved yet!");
				return;
			case Visitor_Have_No_Orders_Yet:
				showErrorMessage("Such ID does not exists");
				return;
			case Visitor_Connected_Successfully:
				switchMainScreenAccordingToUserLogin((Visitor)response.getMessage());
				return;
			case Guide_Connected_Successfully:
				switchMainScreenAccordingToUserLogin((Guide)response.getMessage());
				return;
			case Employee_Connected_Successfully:
				switchMainScreenAccordingToUserLogin((Employee)response.getMessage());
				return;
			case Query_Failed:
				showErrorMessage("Failed to fetch data from database");
		}
		
	}

	/**
    * Switches the main screen based on the user's login credentials.
    *
    * @param client The ExternalUser object representing the logged-in user.
    */
	private void switchMainScreenAccordingToUserLogin(ExternalUser client) {
		try {
			CurrentWindow.setCurrentWindow((Stage) icon.getScene().getWindow());
			IScreenController controller;
			FXMLLoader loader;
			switch(currentUser) {
			case Visitor:
			case Guide:
				loader = new FXMLLoader(getClass().getResource("/gui/view/CustomerScreen.fxml"));
				controller = new CustomerScreenController(client);
				break;
			case Employee:
				loader = new FXMLLoader(getClass().getResource("/gui/view/EmployeeScreen.fxml"));
				// TODO: cast to employee
				controller = new EmployeeScreenController(client);
				break;
			case ExternalUser:
				loader = new FXMLLoader(getClass().getResource("/gui/view/CustomerScreen.fxml"));
				controller = new CustomerScreenController(client);
				break;
			default:
				loader = new FXMLLoader(getClass().getResource("/gui/view/CustomerScreen.fxml"));
				controller = new CustomerScreenController(client);
				break;
			}
			
			loader.setController(controller);
			loader.load();
			Parent p = loader.getRoot();
	
			CurrentWindow.getCurrentWindow().setTitle("GoNature - Client Screen");
			CurrentWindow.getCurrentWindow().setScene(new Scene(p));
			CurrentWindow.getCurrentWindow().setResizable(false);
			CurrentWindow.getCurrentWindow().setOnCloseRequest(e->controller.onCloseApplication());
			ClientApplication.runningController=controller;
			CurrentWindow.getCurrentWindow().show();

		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
    * Handles the action when the user clicks the "Back" button.
    */
	public void onBackClicked() {
		loginVbox.setVisible(false);
		optionsVbox.setVisible(true);
		backToOptionsButton.setVisible(false);
		errorMessageLabel.setText("");
		errorPane.setVisible(false);
	}
	
   /**
    * Clears the login fields on the GUI.
    */
	private void clearLoginFields() {
		usernameField.clear();
		passwordField.clear();
		visitorField.clear();
	}
	
	/**
    * Sets the screen after the user logs out.
    */
	public void setScreenAfterLogout() {
	connectToServerVbox.setVisible(false);
	optionsVbox.setVisible(true);
	errorMessageLabel.setText("");
	errorPane.setVisible(false);
}


	/**
    * Handles the action when the user clicks the "Logout" button.
    * This method is implemented from the IScreenController interface.
    */
	@Override
	public void onLogoutClicked() {
		ClientRequestDataContainer request;
		if(!connectToServerVbox.isVisible()) {
			request = new ClientRequestDataContainer(ClientRequest.Logout, null);
			ClientApplication.client.accept(request);
		}
	}
	
	/**
    * Handles the action when the user close the Application by "X" button.
    * This method is implemented from the IScreenController interface
    */
	@Override
	public void onCloseApplication() {
		ClientRequestDataContainer request;
		if(!connectToServerVbox.isVisible()) {
			request = new ClientRequestDataContainer(ClientRequest.Logout, null);
			ClientApplication.client.accept(request);
			try {
				ClientApplication.client.getClient().closeConnection();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

	/**
    * Handles the situation when the server crashes.
    * This method is implemented from the IScreenController interface.
    */
	@Override
	public void onServerCrashed() {
		AlertPopUp alert = new AlertPopUp(AlertType.ERROR,"FATAL ERROR","Server is Down","Server Crashed - The application will be closed.");
		alert.showAndWait();
		try {
			ClientApplication.client.getClient().closeConnection();
			Platform.runLater(()->CurrentWindow.getCurrentWindow().close());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	

}
