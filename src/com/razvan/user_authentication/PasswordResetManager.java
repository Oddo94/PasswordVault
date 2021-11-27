package com.razvan.user_authentication;

import com.razvan.gui.ErrorDisplayManager;
import com.razvan.gui.ErrorDisplayManager.messageType;
import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.io_manager.IOFileManager;
import com.razvan.utils.*;

import lombok.Getter;

import java.io.*;
import java.util.*;

@Getter
public class PasswordResetManager {
	private String userName;
	private char[] password;
	private PasswordEncryptionManager pem;
	private File file;
	private String confirmationCode;

	public PasswordResetManager(String userName, char[] password, PasswordEncryptionManager pem) {
		this.userName = userName;
		this.password = password;
		this.pem = pem;
		this.file = new File(ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/passwordReset/sender_account_credentials.xml");
		this.confirmationCode = null;
	}


	//Method for retrieving the data necessary to connect to the email address used for sending confirmation code to the user
	public SenderAccountCredentials retrieveSenderEmailData() {

		SenderAccountCredentials accountCredentials = null;
		try {

			XMLFileReader xmlReader = new XMLFileReader(file);
			IOFileManager fileManager = new IOFileManager();

			accountCredentials = xmlReader.readFileAsObject();

		} catch (Exception ex) {
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, messageType.EXCEPTION);

			return null;
		}

		return accountCredentials;
	}


	//Method for sending the confirmation email to the specified user email address
	public int sendConfirmationEmail(SenderAccountCredentials accountCredentials, String userEmail) {
		if (accountCredentials == null || userEmail == null) {
			return - 1;
		}

		try {
			CodeGenerator codeGenerator = new CodeGenerator();

			String from = accountCredentials.getAccountAddress();
			String to = userEmail;
			String subject = "Password Vault-password reset confirmation";
			confirmationCode = codeGenerator.getConfirmationCode(64);
			String message = String.format("A password reset was requested for the PasswordVault application account associated with this email address.Please use the following confirmation code to finish the password reset process: %s\nIf you are not the intended recipient of this message please delete it immediately.", confirmationCode);
			String sendingAccountPassword = accountCredentials.getAccountPassword();

			EmailSender mailSender = new EmailSender(from, to, subject, message, sendingAccountPassword);

			mailSender.sendEmail();

		} catch (Exception ex){
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, messageType.EXCEPTION);

			return -1;
		}

		return 0;

	}

	//Method used for retrieving the user email to which the confirmation code will be sent
	public String retrieveUserEmail(String userName, int dataIndex) {
		IOFileManager fileManager = new IOFileManager();

		String[] userAuthenticationData = fileManager.retrieveUserAuthenticationData(userName);

		if (userAuthenticationData == null || userAuthenticationData.length == 0) {
			return null;
		}

		if (dataIndex < 0 || dataIndex >= userAuthenticationData.length) {
			return null;
		}


		return userAuthenticationData[dataIndex];

	}

	public boolean confirmationCodesMatch(String generatedConfirmationCode, String userInputConfirmationCode) {

		return Objects.equals(generatedConfirmationCode, userInputConfirmationCode);

	}



}
