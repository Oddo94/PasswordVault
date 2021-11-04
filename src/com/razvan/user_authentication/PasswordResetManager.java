package com.razvan.user_authentication;

import com.razvan.gui.ErrorDisplayManager;
import com.razvan.gui.ErrorDisplayManager.messageType;
import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.io_manager.IOFileManager;
import com.razvan.utils.*;


import java.io.*;
import java.util.*;

public class PasswordResetManager {
	private String userName;
	private char[] password;
	private PasswordEncryptionManager pem;
	private File file;

	public PasswordResetManager(String userName, char[] password, PasswordEncryptionManager pem) {
		this.userName = userName;
		this.password = password;
		this.pem = pem;
		this.file = new File(ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/passwordReset/sender_account_credentials.xml");
	}

	public int resetPassword() {

		try {
		XMLFileReader xmlReader = new XMLFileReader(file);
		IOFileManager fileManager = new IOFileManager();

		Map<String, String> senderAccountCredentialsMap = xmlReader.readXMLFile();

		String userEmail = retrieveUserEmail(userName, 3);
		
		sendConfirmationEmail(senderAccountCredentialsMap, userEmail);
		
		} catch (Exception ex) {
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, messageType.EXCEPTION);
			
			return -1;
		}

		return 0;
	}

	private void sendConfirmationEmail(Map<String,String> senderAccountCredentialsMap, String userEmail) throws Exception {
		if (senderAccountCredentialsMap == null || userEmail == null) {
			return;
		}
		CodeGenerator codeGenerator = new CodeGenerator();
		
		String from = senderAccountCredentialsMap.get("accountAddress");
	    String to = userEmail;
	    String subject = "Password Vault-password reset confirmation";
	    String confirmationCode = codeGenerator.getConfirmationCode(64);
	    String message = String.format("A password reset was requested for the PasswordVault application account associated with this email address.Please use the following confirmation code to finish the password reset process: %s.\nIf you are not the intended recipient of this message please delete it immediately.", confirmationCode);
	    String sendingAccountPassword = senderAccountCredentialsMap.get("accountPassword");
	    
	    EmailSender mailSender = new EmailSender(from, to, subject, message, sendingAccountPassword);
	    
	    mailSender.sendEmail();
	   	
	}

	private String retrieveUserEmail(String userName, int dataIndex) {
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




}
