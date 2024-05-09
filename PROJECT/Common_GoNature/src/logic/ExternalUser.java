package logic;

import java.io.Serializable;

import utils.enums.UserTypeEnum;

/**
 * The ExternalUser class represents a user of type "ExternalUser". It
 * implements the Serializable interface to allow instances of this class to be
 * serialized.
 */
public class ExternalUser implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 5371858634046221075L;
	protected UserTypeEnum userType = UserTypeEnum.ExternalUser;

	/**
	 * Constructs a new ExternalUser object.
	 */
	public ExternalUser() {
	}

	/**
	 * Gets the type of user.
	 * 
	 * @return The type of user
	 */
	public UserTypeEnum getUserType() {
		return userType;
	}

	/**
	 * Sets the type of user.
	 * 
	 * @param type The type of user to set
	 */
	public void setUserType(UserTypeEnum type) {
		this.userType = type;
	}

}
