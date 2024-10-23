package com.razvan.installation_manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

import com.razvan.gui.ErrorDisplayManager;
import com.razvan.utils.SenderAccountCredentials;
import com.razvan.utils.XMLFileReader;

public class ApplicationInstallManager {
	public static final String APP_MAIN_FOLDER_PATH = System.getProperty("user.home") + "/AppData/Roaming/PasswordVault";

	public static void createAppFoldersAndFiles() {

		createAppFolders();
		createAppSubFolders();
		createAppInitialFiles();
		
	}
	
	private static void createAppFolders() {
		//Main folders path
		Path securityFolderPath = Paths.get(APP_MAIN_FOLDER_PATH,"/security");
		Path authenticationFolderPath = Paths.get(APP_MAIN_FOLDER_PATH, "/userAuthentication");
		Path userDataFolderPath = Paths.get(APP_MAIN_FOLDER_PATH, "/userData");
		Path passwordResetFolderPath = Paths.get(APP_MAIN_FOLDER_PATH,"/passwordReset");

		try {
			//Main folders creation
			Files.createDirectories(securityFolderPath);
			Files.createDirectory(authenticationFolderPath);
			Files.createDirectory(userDataFolderPath);
			Files.createDirectory(passwordResetFolderPath);

		} catch (IOException ex) {
			
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}

	}
	
	private static void createAppSubFolders() {
		//Sub-folders path
		Path ivSubFolderPath = Paths.get(APP_MAIN_FOLDER_PATH,"/security/iv");
		Path skSubFolderPath = Paths.get(APP_MAIN_FOLDER_PATH,"/security/sk");		

		try {
			//Sub-folders creation
			Files.createDirectory(ivSubFolderPath);
			Files.createDirectory(skSubFolderPath);

		} catch (IOException ex) {
			
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
	}

	private static void createAppInitialFiles() {
		//Files path
		Path secretKeyStoreFilePath = Paths.get(APP_MAIN_FOLDER_PATH, "/security/sk/secret_key_store.ks");
		Path authenticationDataFilePath = Paths.get(APP_MAIN_FOLDER_PATH, "/userAuthentication/authentication_data");

		try {
			//File creation
			Files.createFile(secretKeyStoreFilePath);
			Files.createFile(authenticationDataFilePath);
			
			//XML file creation
			//It is not created using the File class since it needs a special structure and this is the special method that handles the job
			createSenderAccountCredentialsFile();
			
		} catch (Exception ex) {
			
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
	}

	public static void createKeyStoreFile() {
		String passwordString = "Ho6-Pq8%";

		try {
			KeyStore keyStore = KeyStore.getInstance("JCEKS");
			char[] password = passwordString.toCharArray();
			String secretKeyStoreFilePath = APP_MAIN_FOLDER_PATH + "/security/sk/secret_key_store.ks";
			keyStore.load(null, password);

			FileOutputStream outStream = new FileOutputStream(secretKeyStoreFilePath);
			keyStore.store(outStream, password);

		} catch (KeyStoreException | NoSuchAlgorithmException | NullPointerException | CertificateException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}
	
	private static void createSenderAccountCredentialsFile() throws Exception {
		//Creates the file containing the email account credentials to which the app will connect to send the password reset confirmation email
		File accountCredentialsFile = new File(APP_MAIN_FOLDER_PATH + "/passwordReset/sender_account_credentials.xml");
		
		XMLFileReader xmlReader = new XMLFileReader(accountCredentialsFile);
		
		xmlReader.createFile();
		
	}
	
	public static boolean hasMainAppFolder() {
		File mainAppFolder = new File(APP_MAIN_FOLDER_PATH);
		
		return mainAppFolder.exists();
	}

}
