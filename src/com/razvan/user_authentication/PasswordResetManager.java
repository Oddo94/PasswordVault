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

//	public int performPasswordReset() {
//		Map<String,String> senderAccountCredentialsMap = retrieveSenderEmailData();
//		String userEmail = retrieveUserEmail(userName, 3);
//		
//		
//		if (senderAccountCredentialsMap != null) {
//			return -1;
//		}
//		
//		sendConfirmationEmail(senderAccountCredentialsMap, userEmail);
//		
//		
//		
//	}
//	
//	
//	public int resetPassword() {
//
//
//	}
//
	
//	public Map<String,String> retrieveSenderEmailData() {
//		Map<String,String> senderAccountCredentialsMap;
//		
//		try {
//			
//		XMLFileReader xmlReader = new XMLFileReader(file);
//		IOFileManager fileManager = new IOFileManager();
//
//		senderAccountCredentialsMap = xmlReader.readXMLFile();
//
//		} catch (Exception ex) {
//			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
//			ErrorDisplayManager.displayError(null, stackTrace, messageType.EXCEPTION);
//			
//			return null;
//		}
//
//		return senderAccountCredentialsMap;
//	}
	
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

//	public int sendConfirmationEmail(Map<String,String> senderAccountCredentialsMap, String userEmail) {
//		if (senderAccountCredentialsMap == null || userEmail == null) {
//			return - 1;
//		}
//		
//		try {
//			CodeGenerator codeGenerator = new CodeGenerator();
//			
//			String from = senderAccountCredentialsMap.get("accountAddress");
//		    String to = userEmail;
//		    String subject = "Password Vault-password reset confirmation";
//		    confirmationCode = codeGenerator.getConfirmationCode(64);
//		    String message = String.format("A password reset was requested for the PasswordVault application account associated with this email address.Please use the following confirmation code to finish the password reset process: %s.\nIf you are not the intended recipient of this message please delete it immediately.", confirmationCode);
//		    String sendingAccountPassword = senderAccountCredentialsMap.get("accountPassword");
//		    
//		    EmailSender mailSender = new EmailSender(from, to, subject, message, sendingAccountPassword);
//		    
//		    mailSender.sendEmail();
//			
//		} catch (Exception ex){
//			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
//			ErrorDisplayManager.displayError(null, stackTrace, messageType.EXCEPTION);
//			
//			return -1;
//		}
//		
//		return 0;
//	   	
//	}
	
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
