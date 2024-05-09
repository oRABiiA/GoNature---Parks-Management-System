package gui.controller;

import java.net.URL;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import jdbc.DBConnectionDetails;
import logic.ClientConnection;
import ocsf.ConnectionToClient;
import server.GoNatureServer;


/**
 *
 * @Author NadavReubens
 * @Author Gal Bitton
 * @Author Tamer Amer
 * @Author Rabea Lahham
 * @Author Bahaldeen Swied
 * @Author Ron Sisso
 * @version 1.0.0 
 */

/**
 * The ServerGuiController class is the controller which connect the Server Gui Screen 
 * with the GoNatureServer instance (and the user who click on the buttons)
 */
public class ServerScreenController implements Initializable {
	
	//javaFX binding elements
	@FXML
	private Button connectServer;
	@FXML
	private Button disconnectServer;
	@FXML
	private Button importData;
	@FXML
	private TextField portField;
	@FXML
	private TextField dbNameField;
	@FXML
	private TextField dbUsernameField;
	@FXML
	private TextField dbPasswordField;
	@FXML
	private TextArea serverLog;
	@FXML
	private TableView<ClientConnection> clientsTable;
	@FXML
	private TableColumn<ClientConnection,String> ipColumn;
	@FXML
	private TableColumn<ClientConnection,String> hostColumn;
	@FXML
	private TableColumn<ClientConnection,String> statusColumn;
	
	
	// Observable collection in order to bind it to the table view.
	private final ObservableList<ClientConnection> connectedClientsList = FXCollections.observableArrayList();
	// Serial number of lines in log screen.
	private int logLine=1;
	
	// Empty Constructor
	public ServerScreenController() {}
	
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		importData.setVisible(false);
		
	}
	
	public ObservableList<ClientConnection> getClientsList(){
		return connectedClientsList;
	}
	
	/**
	 * This method write to log.
	 * @param msg - The String message we want to write to log screen.
	 */
	public synchronized void printToLogConsole(String msg) {
		System.out.println(msg);
        LocalTime currentTime = LocalTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        String formattedTime = currentTime.format(formatter);
		serverLog.appendText(String.format("%d. [%s] - %s\n", logLine++,formattedTime,msg));
	}
	
	/**
	 * This method called when the connect server button was clicked in the gui screen.
	 * This method get the data from the text fields in gui screen. and create DBConnectionDetails instance
	 * and empty table view.
	 */
	public void onConnectServerClicked() {
		DBConnectionDetails database = new DBConnectionDetails();
		
		Integer portNumber;
		try {
			portNumber=Integer.parseInt(portField.getText());
		}catch(Exception ex) {
			printToLogConsole("Port must be a number");
			return;
		}
		
		try {
			database.setName(dbNameField.getText());
			database.setUsername(dbUsernameField.getText());
			database.setPassword(dbPasswordField.getText());
			// start the server
			GoNatureServer.startServer(database, portNumber, this);
			logLine=1;
			serverLog.clear();
			importData.setVisible(true);
			// initialize the table view
			initializeViewTable();
			
		}catch(Exception ex) {
			printToLogConsole("All field must be filled");
			return;
		}
		

	}
	
	/**
	 * This method called when the disconnect server was clicked in the gui screen.
	 * This method called to stop the server, clear the table view, and update the gui.
	 */
	public void onDisconnectServerClicked() {
		GoNatureServer.stopServer();
		printToLogConsole("Imported data cleared successfully");
		disconnectServer.setDisable(true);
		connectServer.setDisable(false);
		disableFields(false);
		importData.setDisable(false);
		importData.setVisible(false);
		connectedClientsList.clear();

	}
	
    /**
     * This method is called when the import data button is clicked in the GUI screen.
     */
	public void onImportDataClicked() {
		boolean imported = GoNatureServer.importUsersData();
		if(imported) {
			printToLogConsole("User data imported successfully");
			importData.setDisable(true);
			return;
		}
		printToLogConsole("User data import failed");
	}
	/**
	 * This method add specific client to the connected clients table view in the gui screen.
	 * @param client- The ConnectionToClient instance which include the details of the client.
	 * @param username- The connected user's username.
	 */
	public void addToConnected(ConnectionToClient client,String username) {
		connectedClientsList.add(new ClientConnection(username,client));
	}
	
	/**
	 * This method remove specific client from the connected clients table view in the gui screen.
	 * This method called from server when client has been disconnected.
	 * @param client - The ConnectionToClient instance which include the details of the client.
	 */
	public void removeFromConnected(ConnectionToClient client) {
		for(ClientConnection c: connectedClientsList) {
			if(c.getHostIp().equals(client.getInetAddress().getHostAddress())&&c.getHostName().equals(client.getInetAddress().getHostName())) {
				connectedClientsList.remove(c);
				return;
			}
		}
	}
	
	/**
	 * This method enable the disconnect server button and update all other gui elements.
	 */
	public void connectionSuccessfull() {
		disconnectServer.setDisable(false);
		disableFields(true);
	}
	
	/**
	 * This method initialize the table view, and bind it to the connectedClientsList.
	 */
	private void initializeViewTable() {
		ipColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostIp"));
		hostColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("hostName"));
		statusColumn.setCellValueFactory(new PropertyValueFactory<ClientConnection, String>("username"));
		clientsTable.setItems(connectedClientsList);
	}
	
	/**
	 * This method disable/enable all fields, except disconnected.
	 * @param flag - boolean flag, to set the gui elements disabled or enabled.
	 */
	private void disableFields(boolean flag) {
		connectServer.setDisable(flag);
		portField.setEditable(flag);
		dbNameField.setEditable(flag);
		dbUsernameField.setEditable(flag);
		dbPasswordField.setEditable(flag);
	}


	
	
}
