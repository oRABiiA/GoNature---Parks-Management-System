package server;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * The {@code ServerApplication} class serves as the entry point for the server-side application of the GoNature parks and vacations system.
 * It initializes and displays the server GUI using JavaFX, setting up the primary stage and scene for the server control interface.
 * @author Tamer Amer, Gal Bitton, Rabea Lahham, Bahaldeen Swied, Ron Sisso, Nadav Reubens.
 */

public class ServerApplication extends Application {
	
	/**
	 * Starts the server application GUI, loading the FXML layout, setting the scene, and configuring the primary stage.
	 * Upon closing the application window, it ensures the server is properly stopped. This method sets up the initial
	 * GUI state and appearance, making the window non-resizable and setting a custom title.
	 * 
	 * @param primaryStage The primary stage for this application, onto which the application scene is set.
	 * @throws Exception If loading the FXML file fails or other exceptions occur during GUI initialization.
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/gui/view/ServerScreen.fxml"));
		AnchorPane root = fxmlLoader.load();
		Scene scene = new Scene(root);
		
		primaryStage.setScene(scene);
		primaryStage.setResizable(false);
		// disconnect before closing waiting to run on main javaFX thread.
		primaryStage.setOnCloseRequest(e -> Platform.runLater(()->GoNatureServer.stopServer()));
		primaryStage.setTitle("GoNature - Parks&Vacations - Server Side");
		primaryStage.show();
		
	}
	
	/**
	 * The main entry point for the server application. This method launches the JavaFX application.
	 * 
	 * @param args Command-line arguments passed to the application. Not used in this application.
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
