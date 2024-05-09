package gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import client.ClientApplication;
import gui.view.ApplicationViewType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.Button;
import logic.ClientRequestDataContainer;
import logic.Employee;
import logic.EntitiesContainer;
import logic.ExternalUser;
import logic.Park;
import logic.SceneLoaderHelper;
import utils.AlertPopUp;
import utils.CurrentWindow;
import utils.enums.ClientRequest;
import utils.enums.ParkNameEnum;

/**
 * Controller class responsible for managing the UI interactions on the employee
 * screen. Implements the Initializable interface to initialize JavaFX
 * components. Implements the IScreenController interface for screen management.
 */
public class EmployeeScreenController implements Initializable, IScreenController {
	@FXML
	public BorderPane screen;
	@FXML
	public Label userIdLabel;
	@FXML
	public Label employeeTypeLabel;
	@FXML
	public Button homeButton;
	@FXML
	public Button addNewGuideButton;
	@FXML
	public Button parkEntranceButton;
	@FXML
	public Button createReportsButton;
	@FXML
	public Button viewReportsButton;
	@FXML
	public Button parkSettingsButton;
	@FXML
	public Button requestsButton;
	@FXML
	public Button parkSpotsButton;
	@FXML
	public Button logoutButton;

	@SuppressWarnings("unused")
	private ExternalUser user;
	private Employee employee;

	/**
	 * Constructs a new EmployeeScreenController with the given user.
	 * 
	 * @param user The user associated with this controller.
	 */
	public EmployeeScreenController(ExternalUser user) {
		this.user = user;
		employee = (Employee) user;
	}

	/**
	 * Initializes the controller after its root element has been completely
	 * processed. This method is invoked by the FXMLLoader when the associated FXML
	 * file is loaded. It initializes UI components and hides buttons based on the
	 * type of employee.
	 * 
	 * @param location  The location used to resolve relative paths for the root
	 *                  object.
	 * @param resources The resources used to localize the root object.
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		// TODO: hide buttons according to employee type
		initializeScreenAccordingToEmployeeType();
		onHomeClicked();
	}

	/**
	 * Initializes the screen layout based on the type of employee. Hides or shows
	 * specific buttons on the UI depending on the employee's role.
	 */
	@SuppressWarnings("incomplete-switch")
	private void initializeScreenAccordingToEmployeeType() {
		switch (employee.getEmployeeType()) {
		case Department_Manager:
			addNewGuideButton.setVisible(false);
			addNewGuideButton.setManaged(false);
			parkEntranceButton.setVisible(false);
			parkEntranceButton.setManaged(false);
			parkSettingsButton.setVisible(false);
			parkSettingsButton.setManaged(false);
			break;
		case Park_Employee:
			addNewGuideButton.setVisible(false);
			addNewGuideButton.setManaged(false);
			createReportsButton.setVisible(false);
			createReportsButton.setManaged(false);
			viewReportsButton.setVisible(false);
			viewReportsButton.setManaged(false);
			parkSettingsButton.setVisible(false);
			parkSettingsButton.setManaged(false);
			requestsButton.setVisible(false);
			requestsButton.setManaged(false);
			break;
		case Park_Manager:
			addNewGuideButton.setVisible(false);
			addNewGuideButton.setManaged(false);
			parkEntranceButton.setVisible(false);
			parkEntranceButton.setManaged(false);
			requestsButton.setVisible(false);
			requestsButton.setManaged(false);
			break;
		case Service_Employee:
			parkEntranceButton.setVisible(false);
			parkEntranceButton.setManaged(false);
			createReportsButton.setVisible(false);
			createReportsButton.setManaged(false);
			viewReportsButton.setVisible(false);
			viewReportsButton.setManaged(false);
			parkSettingsButton.setVisible(false);
			parkSettingsButton.setManaged(false);
			requestsButton.setVisible(false);
			requestsButton.setManaged(false);
			parkSpotsButton.setVisible(false);
			parkSpotsButton.setManaged(false);
			break;
		}
		userIdLabel.setText(employee.getUserId());
		employeeTypeLabel.setText(employee.getEmployeeType().toString());
	}

	/**
	 * Event handler triggered when the "Logout" button is clicked. Sends a logout
	 * request to the server and navigates the user back to the login screen upon
	 * successful logout.
	 */
	public void onLogoutClicked() {
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Logout, employee);
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
	 * Event handler triggered when the "Home" button is clicked. Loads the employee
	 * homepage screen and sets it as the center content of the BorderPane.
	 */
	public void onHomeClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/EmployeeHomepageScreen.fxml", ApplicationViewType.Employee_Homepage_Screen,
				new EntitiesContainer(employee));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Add New Guide" button is clicked. Loads the
	 * manage guides screen and sets it as the center content of the BorderPane.
	 */
	public void onAddNewGuideClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/ManageGuidesScreen.fxml", ApplicationViewType.Manage_Guides_Screen, null);
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Park Entrance" button is clicked. Loads the
	 * park entrance screen and sets it as the center content of the BorderPane.
	 */
	public void onParkEntranceClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/ParkEntranceScreen.fxml", ApplicationViewType.Park_Entrance_Screen,
				new EntitiesContainer(employee));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Create Reports" button is clicked. Loads
	 * the create reports screen and sets it as the center content of the
	 * BorderPane.
	 */
	public void onCreateReportsClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/CreateReportsScreen.fxml", ApplicationViewType.Create_Reports_Screen,
				new EntitiesContainer(employee));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "View Reports" button is clicked. Loads the
	 * view reports screen and sets it as the center content of the BorderPane.
	 */
	public void onViewReportsClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/ViewReportsScreen.fxml", ApplicationViewType.View_Reports_Screen,
				new EntitiesContainer(employee));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Park Settings" button is clicked. Loads the
	 * park settings screen and sets it as the center content of the BorderPane.
	 */
	public void onParkSettingsClicked() {
		Park park = new Park(employee.getRelatedPark().getParkId());
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/ParkSettingsScreen.fxml", ApplicationViewType.Park_Settings_Screen,
				new EntitiesContainer(park, employee));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Requests" button is clicked. Loads the
	 * request table screen and sets it as the center content of the BorderPane.
	 */
	public void onRequestsClicked() {
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/RequestTableScreen.fxml", ApplicationViewType.Request_Table_Screen,
				new EntitiesContainer(employee));
		screen.setCenter(dashboard);
	}

	/**
	 * Event handler triggered when the "Park Spots" button is clicked. Loads the
	 * park available spots screen and sets it as the center content of the
	 * BorderPane.
	 */
	public void onParkSpotsClicked() {
		Park park = new Park(1, ParkNameEnum.Banias, 100, 50, 4, 20);
		AnchorPane dashboard = SceneLoaderHelper.getInstance().loadRightScreenToBorderPaneWithController(screen,
				"/gui/view/ParkAvailableSpotsScreen.fxml", ApplicationViewType.Park_Available_Spots_Screen,
				new EntitiesContainer(employee, park));
		screen.setCenter(dashboard);
	}

	/**
	 * Handles the process of closing the client application. Initiates a logout
	 * request for the current employee to the server and closes the client's
	 * connection. This method is typically called when the user intends to exit the
	 * application.
	 */
	@Override
	public void onCloseApplication() {
		ClientRequestDataContainer request = new ClientRequestDataContainer(ClientRequest.Logout, employee);
		ClientApplication.client.accept(request);
		try {
			ClientApplication.client.getClient().closeConnection();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
