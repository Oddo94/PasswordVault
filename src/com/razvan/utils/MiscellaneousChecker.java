package com.razvan.utils;

import java.io.File;
import java.util.regex.*;

import com.razvan.installation_manager.ApplicationInstallManager;

public class MiscellaneousChecker {

	public static boolean hasDataInsideSenderAccountCredentialsFile() {
		File senderAccountCredentialsFile = new File(ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/passwordReset/sender_account_credentials.xml");

		XMLFileReader xmlReader = new XMLFileReader(senderAccountCredentialsFile);

		try {

			SenderAccountCredentials senderAccountCredentials = xmlReader.readFileAsObject();

			String accountAddress = senderAccountCredentials.getAccountAddress();
			String accountPassword = senderAccountCredentials.getAccountAddress();

			if ( accountAddress != null && !"".equals(accountAddress)) {
				if (accountPassword!= null && !"".equals(accountPassword) ) {
					if (!"Add email address here".equals(accountAddress) && !"Add email password here".equals(accountPassword)) {
						Pattern pattern = Pattern.compile("\\s+");
						Matcher addressMatcher = pattern.matcher(accountAddress);
						Matcher passwordMatcher = pattern.matcher(accountPassword);

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
