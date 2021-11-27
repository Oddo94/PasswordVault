package com.razvan.action_listener;

import java.awt.event.ActionEvent;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.razvan.gui.LoginWindow;
import com.razvan.user_authentication.PasswordEncryptionManager;
import com.razvan.user_authentication.PasswordResetManager;
import com.razvan.utils.*;
import java.util.*;
import lombok.NonNull;

public class PasswordResetActionListener implements ActionListener {

	private LoginWindow loginWindow;
	private PasswordEncryptionManager pem;
	private JTextField userNameField;
	private JPasswordField newPasswordField;
	private JPasswordField confirmPasswordField;

	public PasswordResetActionListener(@NonNull LoginWindow loginWindow) {
		this.loginWindow = loginWindow;
		this.pem = new PasswordEncryptionManager();
		this.userNameField = loginWindow.getUserNameField();
		this.newPasswordField = loginWindow.getNewPasswordField();
		this.confirmPasswordField = loginWindow.getConfirmPasswordField();
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		int userOption = JOptionPane.showConfirmDialog(loginWindow, "Are you sure that you want to reset your password?", "Password reset", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

		System.out.printf("Selected user option %d\n", userOption);

		//Checks if user wants to reset his password
		if(userOption != 0) {
			return;
		}
		//Checks if the required fields contain data and if this data is correct
		if (checkPasswordResetInputData() == -1) {
			return;
		}

		PasswordResetManager resetManager = new PasswordResetManager(userNameField.getText(), newPasswordField.getPassword(), new PasswordEncryptionManager());


		String userName = userNameField.getText();
		//Map<String,String> senderAccountCredentialsMap = resetManager.retrieveSenderEmailData();//Retrieving credentials for the account that will send the confirmation email
		SenderAccountCredentials accountCredentials = resetManager.retrieveSenderEmailData();
		
		String userEmail = resetManager.retrieveUserEmail(userName, 3);//Retrieving the current user email to which the confirmation code will be sent
		
		//Sending confirmation email
//		int confirmationEmailSendingResult = resetManager.sendConfirmationEmail(senderAccountCredentialsMap, userEmail);
		int confirmationEmailSendingResult = resetManager.sendConfirmationEmail(accountCredentials, userEmail);

		if (confirmationEmailSendingResult == 0) {
			JOptionPane.showMessageDialog(loginWindow, "An email containing the password reset instructions was sent to your email address.", "Password reset", JOptionPane.INFORMATION_MESSAGE);
		} else {
			JOptionPane.showMessageDialog(loginWindow, "Unable to send the confirmation email to the specified user address", "Password reset", JOptionPane.ERROR_MESSAGE);
			return;
		}

		String generatedConfirmationCode = resetManager.getConfirmationCode();
		String userInputConfirmationCode = JOptionPane.showInputDialog(loginWindow, "Please enter the confirmation code received on your email address: ", "Password reset", JOptionPane.INFORMATION_MESSAGE);

		//Checks if the generated and user input confirmation codes match
		if (!resetManager.confirmationCodesMatch(generatedConfirmationCode, userInputConfirmationCode)) {
			JOptionPane.showMessageDialog(loginWindow, "Invalid confirmation code! Please try again.");
			return;
		}
		
		// 
		String userAuthenticationData = pem.readAuthenticationDataFile();
		char[] password = newPasswordField.getPassword();

		byte[] salt = pem.getSalt(16);
		byte[] retrievedSalt = pem.restoreSalt(pem.retrieveUserAuthenticationData(userName)[1]);
		String generatedHashCode = pem.encryptPassword(password, retrievedSalt);

		String saltString = pem.converSaltToString(salt);
		String resetPasswordHashCode = pem.encryptPassword(password, salt);
		
		//The modified data after the password reset if performed
		String[] newData = pem.resetPassword(userName, userAuthenticationData, saltString, resetPasswordHashCode);
		pem.writeAuthenticationDataToFile(newData, false);
		
		JOptionPane.showMessageDialog(loginWindow, "Your password was successfully reset!");

	}



private int checkPasswordResetInputData() {
	String userName = userNameField.getText();
	ArrayList<JTextComponent> fieldList = new ArrayList<>(List.of(userNameField, newPasswordField, confirmPasswordField));
	PasswordEncryptionManager pem = new PasswordEncryptionManager();

	if (!GUIInputChecker.hasDataOnRequiredFields(fieldList)) {
		JOptionPane.showMessageDialog(loginWindow, "Please fill in the username, new password and confirm password fields!" );
		return -1;
	}

	if (!pem.userExists(userName)) {
		JOptionPane.showMessageDialog(loginWindow, "The requested user does not exist! Please try again.");
		return -1;
	}

	if (!AuthenticationDataChecker.checkPasswordStrength(newPasswordField.getPassword())) {			
		JOptionPane.showMessageDialog(loginWindow, "The chosen password is not strong enough! "
				+ "Your password should: \n 1. be at least 10 characters long \n 2. contain lowercase and uppercase letters(a-zA-Z), special characters(!@#*/) and digits(0-9)");

		return -1;
	}


	boolean passwordsMatch = Arrays.equals(newPasswordField.getPassword(), confirmPasswordField.getPassword());

	if (!passwordsMatch) {
		JOptionPane.showMessageDialog(loginWindow, "The new password does not match the confirmation password!");
		return -1;
	}

	if (isSimilarPassword()) {
		JOptionPane.showMessageDialog(loginWindow, "The new password must be significantly different from the old one! Please use another password.");
		return -1;
	}

	return 0;

}

private boolean isSimilarPassword() {
	String userName = userNameField.getText();
	String userAuthenticationData = pem.readAuthenticationDataFile();
	char[] password = newPasswordField.getPassword();

	byte[] salt = pem.getSalt(16);
	byte[] retrievedSalt = pem.restoreSalt(pem.retrieveUserAuthenticationData(userName)[1]);
	String generatedHashCode = pem.encryptPassword(password, retrievedSalt);
	String retrievedHashCode = pem.retrieveUserAuthenticationData(userName)[2];

	String saltString = pem.converSaltToString(salt);
	String resetPasswordHashCode = pem.encryptPassword(password, salt);

	if (pem.hashCodesMatch(generatedHashCode, retrievedHashCode)) {

		return true;
	} 

	return false;
}




}
