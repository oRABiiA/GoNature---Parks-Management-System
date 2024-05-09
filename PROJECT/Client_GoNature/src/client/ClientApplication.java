package client;

import gui.controller.IScreenController;
import gui.controller.LandingPageScreenController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The ClientApplication class represents the main entry point for the client
 * application. It extends the JavaFX Application class and initializes the
 * application's user interface.
 */
public class ClientApplication extends Application {
	/**
	 * The main control for the client application.
	 */
	public static ClientMainControl client;
	/**
	 * The currently running screen controller.
	 */
	public static IScreenController runningController;
	/**
	 * The controller for the landing page screen.
	 */
	public static LandingPageScreenController landingPageController;

	/**
	 * Starts the JavaFX application by loading the landing page screen.
	 *
	 * @param primaryStage The primary stage of the application.
	 * @throws Exception if an error occurs during application startup.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		try {
			FXMLLoader loader = new FXMLLoader(getClass().getResource("/gui/view/LandingPageScreen.fxml"));
			landingPageController = new LandingPageScreenController();
			loader.setController(landingPageController);
			runningController = landingPageController;
			loader.load();
			Parent p = loader.getRoot();
			primaryStage.setTitle("GoNature Client - Landing Page");
			primaryStage.setOnCloseRequest(e -> runningController.onCloseApplication());
			primaryStage.setScene(new Scene(p));
			primaryStage.setResizable(false);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * The main entry point of the client application.
	 *
	 * @param args Command-line arguments.
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
