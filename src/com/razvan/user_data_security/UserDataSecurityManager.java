package com.razvan.user_data_security;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;
import java.security.KeyStore.ProtectionParameter;
import java.security.KeyStore.SecretKeyEntry;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.razvan.gui.ErrorDisplayManager;
import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.io_manager.IOFileManager;

public class UserDataSecurityManager {

	private static final String AES= "AES";
	private static final String AES_CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding";
	private static final String PASSWORD_STRING = "Ho6-Pq8%";
	//Custom file used as KeyStore
	private File secretKeyStorageFile =  new File(ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/security/sk/secret_key_store.ks");
	private IOFileManager fileManager = new IOFileManager();
	

	
	public byte[] createInitializationVector() {
		byte[] initializationVector = new byte[16];
		SecureRandom secureRandom = new SecureRandom();
		secureRandom.nextBytes(initializationVector);
		
		return initializationVector;
		
	}
	
	public SecretKey createAESKey() {
		SecureRandom secureRandom = new SecureRandom();
		KeyGenerator keyGenerator = null;
		
		try {
			keyGenerator = KeyGenerator.getInstance(AES);
		} catch (NoSuchAlgorithmException ex) {
			
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
		
		keyGenerator.init(256,secureRandom);
		
		return keyGenerator.generateKey();
	}
	
	public byte[] performAESEncryption(String plainText, SecretKey secretKey, byte[] initializationVector) {
		try {
		Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
		
		return cipher.doFinal(plainText.getBytes());
		
		} catch(NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
		
		return null;
		
	}
	
	public String performAESDecryption(byte[] cipherText, SecretKey secretKey, byte[] initializationVector) {
		try {
		Cipher cipher = Cipher.getInstance(AES_CIPHER_ALGORITHM);
		IvParameterSpec ivParameterSpec = new IvParameterSpec(initializationVector);
		cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
		
		byte[] decryptionResult = cipher.doFinal(cipherText);
		
		return new String(decryptionResult);
		
		} catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
		
		return null;
	}
	
	public byte[] restoreInitializationVector(String ivString) {
		//Splits the string into a string array which contains all the values
		String[] byteString = ivString.split(",");
		
		//Creates a new byte array
		byte[] restoredInitializationVector = new byte[16];
		
		//Fills the previously created byte array with the byte representation of each element in the string array
		for (int i = 0; i < byteString.length; i++) {
			restoredInitializationVector[i] = Byte.parseByte(byteString[i]);
		}
		
		return restoredInitializationVector;
		
	}
	

	//MODIFY METHOD-it has to receive the userName as parameter so that it can set the alias for the secretKey entry
	public void storeSecretKey(SecretKey secretKey, String userName) {

		try {
		//Creating KeyStore object
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		
		//Loading the KeyStore object
		char[] password = PASSWORD_STRING.toCharArray();
		FileInputStream inputStream = new FileInputStream(secretKeyStorageFile);
		keyStore.load(inputStream,password);
		
		//Creating the KeyStore ProtectionParameter object
		ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);
			
		//Create a SecreKeyEntry object
		KeyStore.SecretKeyEntry secretKeyEntry = new KeyStore.SecretKeyEntry(secretKey);
		
		//MODIFIED CODE!!
		//Set an entry to the KeyStore(the alais for the KeyStore entry is createad by concatenating the name of the user with the "-SK" suffix
		String suffix = "-SK";
		String alias = userName + suffix;
		keyStore.setEntry(alias, secretKeyEntry, protectionParameter);
		
		//MODIFIED CODE!!
		//Storing the KeyStore object
		FileOutputStream outStream = new FileOutputStream(secretKeyStorageFile);
		keyStore.store(outStream, password);
		
		} catch(KeyStoreException | NoSuchAlgorithmException | CertificateException ex ) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		
		} catch(IOException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		} 
			
	}
	
	//MODIFY METHOD-it has to receive the userName as parameter so that it can correctly retrieve the secretKey entry for each user
	public SecretKey retrieveSecretKey(String userName) {
		//Test objects
//		IOFileManager fileManager = new IOFileManager();
//		StringBuilder sb = new StringBuilder();
//		
//		sb.append("ENTERED THE RETRIEVE METHOD\n");
		SecretKey mySecretKey = null;
		try {
		//Creating KeyStore object
		KeyStore keyStore = KeyStore.getInstance("JCEKS");
		
		//Loading the KeyStore object
		char[] password = PASSWORD_STRING.toCharArray();
		FileInputStream inputStream = new FileInputStream(secretKeyStorageFile);
		keyStore.load(inputStream, password);
		
		//Creating the KeyStore ProtectionParameter object
		ProtectionParameter protectionParameter = new KeyStore.PasswordProtection(password);
		
		//MODIFIED CODE!!
		//Creating the KeyStore.SecretKeyEntry object(the alias is created exactly as in the storeSecretKey() method)
		String suffix = "-SK";
		String alias = userName + suffix;
		SecretKeyEntry secretKeyEntry = (SecretKeyEntry)keyStore.getEntry(alias, protectionParameter);
		
		//Creating SecretKey object
		mySecretKey = secretKeyEntry.getSecretKey();
		
		} catch (KeyStoreException | UnrecoverableEntryException | NoSuchAlgorithmException | CertificateException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
			
		} catch (IOException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
//		sb.append("Algorithm used to generate the key: " + mySecretKey.getAlgorithm() +"\n");
//		sb.append("Format used for the key: " + mySecretKey.getFormat() + "\n");
//		
//		sb.append("EXITED THE RETRIEVE METHOD\n");
//		
//		fileManager.writeLoggingData(loggingDataFile,sb.toString());
		
		return mySecretKey;
			
	}
	
	public void storeInitializationVector(File fileToWrite, byte[] initializationVector) {
		fileManager.writeDataForDecryption(fileToWrite, initializationVector);
	}
}
