package com.razvan.user_authentication;

import java.io.File;

import javax.swing.JOptionPane;

import com.razvan.installation_manager.ApplicationInstallManager;

//Class for checking the login credentials provided by the user
public class LoginCredentialsChecker {
	private String appDataPath;
	private PasswordEncryptionManager pem;
	private String userName;
	private char[] password;


	public LoginCredentialsChecker(String userName, char[] password) {
		this.userName = userName;
		this.password = password;

		appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;
		pem = new PasswordEncryptionManager();

	}


	private boolean hasUserAccountsFile() {
		boolean hasUserAccount = true;
		String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;

		File authenticationData = new File(appDataPath + "/userAuthentication/Authentication_Data");
		if(!authenticationData.exists()) {
			//JOptionPane.showMessageDialog(this, "There are no user accounts.\n Please create one before trying to login!");
			hasUserAccount = false;
		}

		return hasUserAccount;
	}



	public boolean userExists() {
		//Checks if the user accounts file is present and the user exists
		if (hasUserAccountsFile() && pem.userExists(userName)) {
			return true;
		} 

		return false;
	}

	public boolean hasValidCredentials() {
		boolean hasValidCredentials = false;

		//User authentication logic
		String[] userData = pem.retrieveUserAuthenticationData(userName);

		String expectedHashCode = userData[2];
		String actualHashCode = pem.encryptPassword(password, pem.restoreSalt(userData[1]));
		if (pem.hashCodesMatch(expectedHashCode, actualHashCode)) {
			//Sets the flag to true in order to allow the display of the UserDashboard
			hasValidCredentials = true;

		}

		return hasValidCredentials;
	} 


	//Checks if the user has any stored data
	public boolean userHasData() {
		File fileToCheck = new File(appDataPath + "/userData/" + userName);

		return fileToCheck.length() != 0;
	}


}


