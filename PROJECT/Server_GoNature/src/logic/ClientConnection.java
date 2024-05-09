package logic;

import javafx.beans.property.SimpleStringProperty;
import ocsf.ConnectionToClient;


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
 * The ClientConnection class is an entity which contains the client information.
 * This class use SimpleStringProperty in order to be able to observe it smoothly in UI.
 */
public class ClientConnection {
	private SimpleStringProperty hostIp;
	private SimpleStringProperty hostName;
	private SimpleStringProperty username;
	private ConnectionToClient connection;
	
	public ClientConnection(String username,ConnectionToClient connection) {
		this.hostIp=new SimpleStringProperty(connection.getInetAddress().getHostAddress());
		this.hostName=new SimpleStringProperty(connection.getInetAddress().getHostName());
		this.username=new SimpleStringProperty(username);
		this.setConnection(connection);
	}
	
	public String getHostName() {
		return hostName.get();
	}
	
	public void setHostName(String hostName) {
		this.hostName.set(hostName);
	}
	
	public String getHostIp() {
		return hostIp.get();
	}
	
	public void setHostIp(String hostIp) {
		this.hostIp.set(hostIp);
	}
	
	public String getUsername() {
		return username.get();
	}
	
	public void setUsername(String username) {
		this.username.set(username);
	}

	public ConnectionToClient getConnection() {
		return connection;
	}

	public void setConnection(ConnectionToClient connection) {
		this.connection = connection;
	}
	
	@Override
		public boolean equals(Object obj) {
			return connection.equals(((ClientConnection)obj).getConnection());
		}
	
	public boolean isAlreadyConnected(ClientConnection newConnect) {
		return username.get().equals(newConnect.getUsername());
	}
	

}
