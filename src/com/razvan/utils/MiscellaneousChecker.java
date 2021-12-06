package com.razvan.utils;

import java.io.File;
import java.util.regex.*;

import com.razvan.installation_manager.ApplicationInstallManager;

public class MiscellaneousChecker {

	//method for checking the content of the sender_account_credentials.xml file 
	public static boolean hasDataInsideSenderAccountCredentialsFile() {
		File senderAccountCredentialsFile = new File(ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/passwordReset/sender_account_credentials.xml");

		XMLFileReader xmlReader = new XMLFileReader(senderAccountCredentialsFile);

		try {
			//Reads file and transforms it into the object containing the required fields
			SenderAccountCredentials senderAccountCredentials = xmlReader.readFileAsObject();

			//Extracts the account address and account password data from the object
			String accountAddress = senderAccountCredentials.getAccountAddress();
			String accountPassword = senderAccountCredentials.getAccountAddress();

			//Performs several checks to see if the two fields are null or contain an empty string
			if ( accountAddress != null && !"".equals(accountAddress)) {
				if (accountPassword!= null && !"".equals(accountPassword) ) {
					if (!"Add email address here".equals(accountAddress) && !"Add email password here".equals(accountPassword)) {
						//Checks if the fields are made up only of white spaces by checking the string content against a regex pattern
						Pattern pattern = Pattern.compile("\\s+");
						Matcher addressMatcher = pattern.matcher(accountAddress);
						Matcher passwordMatcher = pattern.matcher(accountPassword);

						//If there's no match (meaning that the fields contain some other data too) the method will consider that the file contains data
						if (!addressMatcher.matches() && !passwordMatcher.matches()) {
							return true; 
						}

					}
				}
			}
		} catch (Exception ex) {
			return false;

		}

		return false;

	}

}
