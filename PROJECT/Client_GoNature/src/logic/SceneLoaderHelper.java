package logic;

import java.io.IOException;

import client.ClientApplication;
import gui.controller.OrderSummaryScreenController;
import gui.controller.CreateReportsScreenController;
import gui.controller.CustomerHomepageScreenController;
import gui.controller.EmployeeHomepageScreenController;
import gui.controller.HandleOccasionalVisitorScreenController;
import gui.controller.RescheduleOrderScreenController;
import gui.controller.HandleOrderScreenController;
import gui.controller.IThreadController;
import gui.controller.IdenticationScreenController;
import gui.controller.MakeOrderScreenController;
import gui.controller.ManageGuidesScreenController;
import gui.controller.ParkAvailableSpotsScreenController;
import gui.controller.ParkEntranceScreenController;
import gui.controller.ParkSettingsScreenController;
import gui.controller.PaymentReceiptScreenController;
import gui.controller.RequestTableScreenController;
import gui.controller.ViewReportsScreenController;
import gui.view.ApplicationViewType;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import utils.CurrentWindow;

/**
 * The SceneLoaderHelper class provides utility methods for loading FXML-based
 * views into JavaFX scenes. It handles the dynamic loading of different views
 * based on the specified view type.
 */
public class SceneLoaderHelper {

	private static SceneLoaderHelper guiHelper = null;
	private AnchorPane centerScreen;
	private IThreadController runningController = null;

	/**
	 * Private constructor to prevent instantiation of the SceneLoaderHelper class
	 * directly. Use the {@link #getInstance()} method to obtain an instance.
	 */
	private SceneLoaderHelper() {

	}

	/**
	 * Retrieves the Singleton instance of the SceneLoaderHelper class. If the
	 * instance does not exist, it creates a new one.
	 *
	 * @return The Singleton instance of the SceneLoaderHelper class.
	 */
	public static SceneLoaderHelper getInstance() {
		if (guiHelper == null)
			guiHelper = new SceneLoaderHelper();
		return guiHelper;
	}

	/**
	 * Loads a specific view into a border pane and sets the appropriate controller.
	 *
	 * @param screen     The border pane to load the view into.
	 * @param screenUrl  The URL of the FXML file representing the view.
	 * @param viewToLoad The type of view to load.
	 * @param data       The data to pass to the view controller.
	 * @return The loaded center screen pane.
	 */
	public AnchorPane loadRightScreenToBorderPaneWithController(BorderPane screen, String screenUrl,
			ApplicationViewType viewToLoad, EntitiesContainer data) {
		FXMLLoader loader = new FXMLLoader(getClass().getResource(screenUrl));

		// Close the IThreadController
		if (runningController != null) {
			runningController.cleanUp();
			runningController = null;
		}

		switch (viewToLoad) {

		case Order_Summary_Screen: {
			OrderSummaryScreenController controller = new OrderSummaryScreenController(screen, data.getEntity1());
			loader.setController(controller);
			break;
		}

		case Create_Reports_Screen: {
			CreateReportsScreenController controller = new CreateReportsScreenController(data.getEntity1());
			loader.setController(controller);
			break;
		}

		case Customer_Homepage_Screen: {
			CustomerHomepageScreenController controller = new CustomerHomepageScreenController();
			loader.setController(controller);
			break;
		}
		case Employee_Homepage_Screen: {
			EmployeeHomepageScreenController controller = new EmployeeHomepageScreenController();
			loader.setController(controller);
			break;
		}

		case Reschedule_Order_Screen: {
			RescheduleOrderScreenController controller = new RescheduleOrderScreenController(screen, data.getEntity1());
			runningController = controller;
			loader.setController(controller);
			break;
		}

		case Handle_Order_Screen: {
			HandleOrderScreenController controller = new HandleOrderScreenController(screen, data.getEntity1(),
					data.getCustomerInterface());
			loader.setController(controller);
			break;
		}
		case Identication_Screen: {
			IdenticationScreenController controller = new IdenticationScreenController(screen, data.getEntity1());
			loader.setController(controller);
			break;
		}

		case Make_Order_Screen: {
			MakeOrderScreenController controller = new MakeOrderScreenController(screen, data.getEntity1(),
					data.getCustomerInterface());
			loader.setController(controller);
			break;
		}

		case Manage_Guides_Screen: {
			ManageGuidesScreenController controller = new ManageGuidesScreenController(screen);
			loader.setController(controller);
			break;
		}

		case Park_Available_Spots_Screen: {
			ParkAvailableSpotsScreenController controller = new ParkAvailableSpotsScreenController(data.getEntity1(),
					data.getEntity2());
			loader.setController(controller);
			break;
		}

		case Park_Entrance_Screen: {
			ParkEntranceScreenController controller = new ParkEntranceScreenController(screen, data.getEntity1());
			loader.setController(controller);
			runningController = controller;
			break;
		}

		case Park_Settings_Screen: {
			ParkSettingsScreenController controller = new ParkSettingsScreenController(data.getEntity1(),
					data.getEntity2());
			loader.setController(controller);
			break;
		}

		case Payment_Receipt_Screen: {
			PaymentReceiptScreenController controller = new PaymentReceiptScreenController(data.getEntity1(),
					data.getEntity2(), data.getEntity3());
			loader.setController(controller);
			break;
		}

		case Request_Table_Screen: {
			RequestTableScreenController controller = new RequestTableScreenController(screen);
			loader.setController(controller);
			break;
		}

		case View_Reports_Screen: {
			ViewReportsScreenController controller = new ViewReportsScreenController(data.getEntity1());
			loader.setController(controller);
			break;
		}

		case HandleOccasionalVisitScreen: {
			HandleOccasionalVisitorScreenController controller = new HandleOccasionalVisitorScreenController(screen,
					data.getEntity1());
			loader.setController(controller);
			break;
		}

		default:
			return null;
		}

		try {

			loader.load();
			centerScreen = loader.getRoot();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return centerScreen;
	}

	/**
	 * Sets the screen after logout or back action.
	 */
	public void setScreenAfterLogoutOrBack() {

		try {

			if (runningController != null) {
				runningController.cleanUp();
				runningController = null;
			}

			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/LandingPageScreen.fxml"));
			loader.setController(ClientApplication.landingPageController);
			Scene scene = new Scene(loader.load());
			Stage currentWindow = CurrentWindow.getCurrentWindow();
			currentWindow.setScene(scene);
			currentWindow.setTitle("GoNature Client - Landing Page");
			// primaryStage.getIcons().add(new Image(GoNatureFinals.APP_ICON));
			currentWindow.setResizable(false);
			currentWindow.show();
			CurrentWindow.setCurrentWindow(currentWindow);
			ClientApplication.runningController = ClientApplication.landingPageController;
			Platform.runLater(() -> ClientApplication.landingPageController.setScreenAfterLogout());

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
