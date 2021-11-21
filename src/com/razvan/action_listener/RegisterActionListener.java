package com.razvan.action_listener;

import java.awt.TrayIcon.MessageType;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import java.util.*;

import com.razvan.gui.RegisterWindow;
import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.user_authentication.PasswordEncryptionManager;
import com.razvan.user_data_security.UserDataSecurityManager;
import com.razvan.utils.GUIInputChecker;

import lombok.NonNull;

public class RegisterActionListener implements ActionListener {

	private RegisterWindow registerWindow;
	private JTextField userNameField;
	private JPasswordField passwordField;
	private JTextField emailAddressField;

	//The application main folder path 
	//private String appDataPath = System.getProperty("user.home") + "/AppData/Roaming/PasswordVault";
	private String appDataPath;
	private PasswordEncryptionManager pem;
	private UserDataSecurityManager securityManager;
	private ArrayList<JTextComponent> inputFieldsList;

	public RegisterActionListener (@NonNull RegisterWindow registerWindow) {
		this.registerWindow = registerWindow;
		this.userNameField = registerWindow.getUserNameField();
		this.passwordField = registerWindow.getPasswordField();
		this.emailAddressField = registerWindow.getEmailAddressField();

		this.appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;
		this.pem = new PasswordEncryptionManager();
		this.securityManager = new UserDataSecurityManager();
		this.inputFieldsList = new ArrayList<>(Arrays.asList(userNameField, passwordField, emailAddressField));
	}




	@Override
	public void actionPerformed(ActionEvent e) {
		//If no data is present on fields or the input data does not conform to the requirements the methodreturns 
		if (!hasPassedInputChecks()) {
			return;
		}
		
		//JOptionPane.showMessageDialog(null,  "All checks were successfully passed!", "Register test message", JOptionPane.INFORMATION_MESSAGE );
		
		int userCreationOption = JOptionPane.showConfirmDialog(null, "Are you sure that you want to create a new user based on the provided data?", "Register", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		//System.out.println("USER OPTION: " + userCreationOption);
		//Checks if the user selected the "No" option or if he closed the confirmation dialog
		if (userCreationOption == 1 || userCreationOption == -1) {
			return;
		}
			
	    //JOptionPane.showMessageDialog(null,  "The user confirmed the creation option", "Register test message", JOptionPane.INFORMATION_MESSAGE);
		String userName = userNameField.getText();
		char[] password = passwordField.getPassword();
		String emailAddress = emailAddressField.getText();
		
		//Calls the user creation method with the validated data
		createNewUser(userName, password, emailAddress);
	   
	}


	private boolean hasPassedInputChecks() {
		//Checks if all the required field(user name, password, email address) contain data
		if (!GUIInputChecker.hasDataOnRequiredFields(inputFieldsList)) {
			JOptionPane.showMessageDialog(null, "All fields are mandatory! Please fill in all the required data before proceeding.", "Register", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		String userName = userNameField.getText();
		String userNameRegexPattern = "^(?!_)[\\w]{3,20}";

		//Checks if the username respects the imposed rules
		if(!GUIInputChecker.checkUsername(userName, userNameRegexPattern)) {
			JOptionPane.showMessageDialog(null, "The chosen username doesn't respect the rules! Username should: \n 1. be at least 3 characters long \n 2. should contain only lowercase,uppercase letters, underscore(a-zA-Z_) or digits(0-9) \n 3. it cannot start with an underscore", "Register", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		char[] password = passwordField.getPassword();
		int minimumPasswordLength = 10;
		String[] passwordRegexPatterns = new String[]{"[\\w+]", "[\\d+]","[^\\w\\d\\s]", "[.*{10, 20}]"};
		//Checks the password complexity
		if (!GUIInputChecker.checkPasswordStrength(password, minimumPasswordLength, passwordRegexPatterns)) {
			JOptionPane.showMessageDialog(null, "The chosen password is not strong enough! Your password should: \n 1. be at least 10 characters long \n 2. contain lowercase and uppercase letters(a-zA-Z), special characters(!@#*/) and digits(0-9)", "Register", JOptionPane.INFORMATION_MESSAGE);
			return false;
		}

		String emailAddress = emailAddressField.getText();
		//Checks if the email address is valid
		if (!GUIInputChecker.isValidEmail(emailAddress)) {
			JOptionPane.showMessageDialog(null, "Invalid email address! Please check for typing errors or use another address and try again.");
			return false;
		}
		
		return true;
	}
	
	private void createNewUser(String userName, char[] password, String emailAddress) {
		if(userName == null || password == null || emailAddress == null) {
			return;
		}
			
		if (pem.userExists(userName)) {
			JOptionPane.showMessageDialog(null, "The entered user already exists!", "Register", JOptionPane.WARNING_MESSAGE);
		} else {
			
			//The array containing the authentication data for the newly created user
			String[] userData = pem.prepareAuthenticationData(userName, password, emailAddress);
			String formattedUserData = formatNewUserData(userData);
			String[] newUserAuthenticationData = new String[] {formattedUserData};
			//Writes the data to the file containing the authentication data of all the users
			pem.writeAuthenticationDataToFile(newUserAuthenticationData, true);
			//Creates the rest of the files needed for the newly created user
			createUserDataFile(userName);
			createUserIvFile(userName);
			setSecretKeyForUser(userName);
			
			JOptionPane.showMessageDialog(null, "Your user was successfully created!", "Register", JOptionPane.INFORMATION_MESSAGE);
			GUIInputChecker.resetFields(inputFieldsList);
		}
		
		
	}
	
	//The method for creating the new user data file
		private void createUserDataFile(String userName) {
			
			try {
				File file = new File(appDataPath + "/userData/" + userName);
				file.createNewFile();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//The method for creating the new user initialization vector file
		private void createUserIvFile(String userName) {
			String suffix = "-Iv";
			String userIvFileName = userName + suffix;
			
			try {
				
				File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);
				ivSource.createNewFile();

			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		
		//The method for creating a secret key for the new user
		public void setSecretKeyForUser(String userName) {
			
			securityManager.storeSecretKey(securityManager.createAESKey(), userName);
		}
		
		
		private String formatNewUserData(String[] data) {
			Objects.requireNonNull(data, "The data provided for formatting cannot be null");
			
			StringBuilder formattedData = new StringBuilder();
			for (int i = 0; i < data.length; i++) {
				if (i < data.length - 1) {
					formattedData.append(data[i] + ";");
					continue;
				}
				
				formattedData.append(data[i] + "\n");	
			}
			
			return formattedData.toString();
		}
}
