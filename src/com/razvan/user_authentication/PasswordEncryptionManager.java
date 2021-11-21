package com.razvan.user_authentication;

import java.io.*;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import com.razvan.installation_manager.ApplicationInstallManager;

public class PasswordEncryptionManager {
	//Fisierul in care se realizeaza
//	private File authenticationDataFile = new File(System.getProperty("user.home") + "/AppData/Roaming/PasswordVault/userAuthentication/authentication_data");
	private File authenticationDataFile = new File(ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/userAuthentication/authentication_data");
	
		
	
	//The method that prepares a string array containing the username, initialization vector and password hashcode
//	public String[] prepareAuthenticationData(String userName,String password) {
//		//Generating salt
//		byte[] salt = getSalt(16);
//		
//		//Adding the salt array element to a StringBuilder and creating a String representation of it
//		StringBuilder sb = new StringBuilder();
//		
//		//The values are separated by ","
//		for (int i = 0; i < salt.length; i++) {	
//			sb.append(salt[i] + ",");
//		}
//		
//		String saltString = sb.toString();
//		
//		//Generating a String containing the password hashcode
//		String hashedPassword = encryptPassword(password, salt);
//		
//		//Generating the String array containing the user authentication data
//		String[] authenticationData = {userName, saltString, hashedPassword};
//		
//	    
//		return authenticationData;
//	}
	
//	public String[] prepareAuthenticationData(String userName, char[] password) {
//		//Generating salt
//		byte[] salt = getSalt(16);
//		
//		//Adding the salt array element to a StringBuilder and creating a String representation of it
//		StringBuilder sb = new StringBuilder();
//		
//		//The values are separated by ","
//		for (int i = 0; i < salt.length; i++) {	
//			sb.append(salt[i] + ",");
//		}
//		
//		String saltString = sb.toString();
//		
//		//Generating a String containing the password hashcode
//		String hashedPassword = encryptPassword(password, salt);
//		
//		//Generating the String array containing the user authentication data
//		String[] authenticationData = {userName, saltString, hashedPassword};
//		
//	    
//		return authenticationData;
//	}
	
	public String[] prepareAuthenticationData(String userName, char[] password, String emailAddress) {
		Objects.requireNonNull(userName, "The user name provided for preparing the authentication data cannot be null");
		Objects.requireNonNull(password, "The password provided for preparing the authentication data cannot be null");
		Objects.requireNonNull(password, "The email address provided for preparing the authentication data cannot be null");
		
		//Generating salt
		byte[] salt = getSalt(16);
		
		//Adding the salt array element to a StringBuilder and creating a String representation of it
		StringBuilder sb = new StringBuilder();
		
		//The values are separated by ","
		for (int i = 0; i < salt.length; i++) {	
			sb.append(salt[i] + ",");
		}
		
		String saltString = sb.toString();
		
		//Generating a String containing the password hashcode
		String hashedPassword = encryptPassword(password, salt);
		
		//Generating the String array containing the user authentication data
		String[] authenticationData = {userName, saltString, hashedPassword, emailAddress};
		
	    
		return authenticationData;
	}
	
	
	
	
	//Method used for reading user authentication data
	public String[] readAuthenticationData() {
		byte[] salt = new byte[16];
		String[] authenticationData = null;
		
		try (FileReader fReader = new FileReader(authenticationDataFile);
			 BufferedReader bReader = new BufferedReader(fReader)) {
			
			authenticationData = bReader.readLine().split(";");
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return authenticationData;
	}
	

	//Method used for writing authentication data to file
//	public void writeAuthenticationDataToFile(String[] data) {
//
//		try (FileWriter fWriter = new FileWriter(authenticationDataFile, false);//the data is overwritten in the file(the false value); IF THE FILE IS OVERWRITTEN THEN ALL PREVIOUS USERS WILL BE DELETED!!!
//			 BufferedWriter bWriter = new BufferedWriter(fWriter)) {
//			
//			for (int i = 0; i < data.length; i++) {
//				
//				if (i == data.length - 1) {
//					bWriter.write(data[i] + "\n");
//					break;
//				}
//				
//				bWriter.write(data[i] + ";");
//			}
//			
//		}catch(IOException ex) {
//			ex.printStackTrace();
//		}	
//	}
//	
	
	//Method for writing the authentication data to file
	//It receives the data to be written and a flag indicating whether the data should be appended to file(e.g: at user creation) or overwritten(e.g: at password reset)
	public void writeAuthenticationDataToFile(String[] data, boolean appendData) {
	
		
		try (FileWriter fWriter = new FileWriter(authenticationDataFile, appendData);//the data is overwritten in the file(the false value); IF THE FILE IS OVERWRITTEN THEN ALL PREVIOUS USERS WILL BE DELETED!!!
			 BufferedWriter bWriter = new BufferedWriter(fWriter)) {
			
			for (int i = 0; i < data.length; i++) {
				
				if (i < data.length - 1) {
					bWriter.write(data[i] + "\n");
					continue;
				}
				
				if(appendData == true) {
					bWriter.write(data[i]);	
				} else {
					bWriter.write(data[i] + ";");	
				}
						
			}
			
			
		}catch(IOException ex) {
			ex.printStackTrace();
		}	
	}
	
	//The method that retrieves the data for the selected user or if they don't exist returns null
	public String[] retrieveUserAuthenticationData(String userName) {
		
		try(FileReader fReader = new FileReader(authenticationDataFile);
			BufferedReader bReader = new BufferedReader(fReader)) {

			String line = null;
			while((line = bReader.readLine()) != null) {
				String[] lineData = line.split(";");
				if(lineData[0].equals(userName)) {
					return lineData;
				}
			}

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return null;
	}
	
	//The method for reading the file containing the user authentication data
	public String readAuthenticationDataFile() {
		StringBuilder fileContent = new StringBuilder();
		try (BufferedReader bReader = new BufferedReader(new FileReader(authenticationDataFile))){

			String line = null;
			while((line = bReader.readLine()) != null) {
				fileContent.append(line + "\n");
			}

			int lastNewLineIndex = fileContent.lastIndexOf("\n");
			fileContent.delete(lastNewLineIndex, lastNewLineIndex + 1);

		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return fileContent.toString();
	}
	
	
	//Checking if the user exists or not
	public boolean userExists(String userName) {
		String[] existingUserData = retrieveUserAuthenticationData(userName);
		
		if (existingUserData != null) {
			return true;
		}
		
		return false;
	}
	
	//The method used for converting the salt data contained in a String to a byte array
	public byte[] restoreSalt(String inputSalt) {
		String[] saltString = inputSalt.split(",");
		if (saltString.length == 0) {
			return null;
		}
		
		byte[] salt = new byte[16];
		
		for (int i = 0; i < saltString.length; i++) {
			salt[i] = Byte.parseByte(saltString[i]);
		}
		
		return salt;
	}
	
	//The method that transforms the byte array containing the salt data to String so that it can later be written to a file
	public String converSaltToString(byte[] salt) {
		//Adaugare elemente din sirul salt in StringBuilder si creare String pt salt
		StringBuilder sb = new StringBuilder();

		//Adding the elements from the byte array to the StringBuilder
		for (int i = 0; i < salt.length; i++) {
			//If the last element of the array is reached no separator is added
			if (i == salt.length - 1) {
				sb.append(salt[i]);
				break;	
			}
			//Otherwise the "," is added after each element
			sb.append(salt[i] + ",");
		}

		return sb.toString();	
	}
	
	
	//Generating salt for the password hashing process
	public byte[] getSalt(int arraySize) {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[arraySize];
		random.nextBytes(salt);
		
		return salt;
	}
	
	//Generating the password hash using the previously created salt
//	public String encryptPassword(String password, byte[] salt) {		
//		StringBuilder encryptedPassword = new StringBuilder();
//		//System.out.println("ENCRYPT PASSWORD METHOD SALT PARAMETER: " + new String(Arrays.toString(salt)));
//		
//		try {
//			PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 65536, 128);
//			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
//			
//			byte[] hash = factory.generateSecret(spec).getEncoded();
//			//System.out.println("PARTIALLLY PROCESSED HASH BYTE ARRAY:" + new String(hash));
//			
//			BigInteger number = new BigInteger(1,hash);
//			String partiallyProcessedPassword = number.toString(16);
//			//System.out.println("PARTIALLY PROCESSED PASSWORD:" + partiallyProcessedPassword);
//			encryptedPassword.append(partiallyProcessedPassword);
//			while (encryptedPassword.length() < 32) {
//				encryptedPassword.append("0");
//			}
//			
//		} catch(NoSuchAlgorithmException ex) {
//			ex.printStackTrace();
//		} catch(InvalidKeySpecException ex) {
//			ex.printStackTrace();
//		}
//		
//		return encryptedPassword.toString();
//		
//	}
	
	public String encryptPassword(char[] password, byte[] salt) {		
		StringBuilder encryptedPassword = new StringBuilder();
		//System.out.println("ENCRYPT PASSWORD METHOD SALT PARAMETER: " + new String(Arrays.toString(salt)));
		
		try {
																	
			PBEKeySpec spec = new PBEKeySpec(password, salt, 65536, 128);
			SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
			
			byte[] hash = factory.generateSecret(spec).getEncoded();
			//System.out.println("PARTIALLLY PROCESSED HASH BYTE ARRAY:" + new String(hash));
			
			BigInteger number = new BigInteger(1,hash);
			String partiallyProcessedPassword = number.toString(16);
			//System.out.println("PARTIALLY PROCESSED PASSWORD:" + partiallyProcessedPassword);
			encryptedPassword.append(partiallyProcessedPassword);
			while (encryptedPassword.length() < 32) {
				encryptedPassword.append("0");
			}
			
		} catch(NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch(InvalidKeySpecException ex) {
			ex.printStackTrace();
		}
		
		return encryptedPassword.toString();
		
	}
	
	//The method used for password reset
	public String[] resetPassword(String userName, String userAuthenticationData, String iv, String newHashCode) {
		//Splitting the authentication data String using \n as a separator
		String[] contentToModify = userAuthenticationData.split("\n");
		StringBuilder modifiedContent = new StringBuilder();

		int length = contentToModify.length;
		for (int i = 0; i < length; i++) {
			//Splitting each entry String using ";" as separator to get the data for a single user at a time
			String[] recordEntry = contentToModify[i].split(";");
			//When the user name is found the old IV and hashcode values are replaced with the new ones provided as arguments
			if (userName.equals(recordEntry[0])) {
				recordEntry[1] = iv;
				recordEntry[2] = newHashCode;
				contentToModify[i] = convertToString(recordEntry);//the modified data is transformed to the standard format using ";" as separator
			}
		}

		return contentToModify;	
	}
	
	//The method that transforms the modified data contained in a String array to a String having its contents separated by ";"
	public String convertToString(String[] data) {
		StringBuilder result = new StringBuilder();

		int length = data.length;
		for (int i = 0; i < length; i++) {
			//If the last element of the array is reached the separator is not added
			if (i == length - 1) {
				result.append(data[i]);
				break;
			}
			//Otherwise the separator is added after each element
			result.append(data[i] + ";");
		}

		return result.toString();

	}
	
	
	//Checks if the hashcode of the provided password is equal to the one that was previously stored
	public boolean hashCodesMatch(String expectedHashCode, String actualHashCode) {
		//Refactor using Object equals method to avoid NPE in case one of the arguments is null
		if (expectedHashCode.equals(actualHashCode)) {
			return true;
		}
		
		return false;
	}
	
	private char[] convertToCharArray(byte[] inputArray) {
		if (inputArray == null) {
			return null;
		}
		
		if (inputArray.length == 0) {
			return new char[0];
		}
		
		char[] resultArray = new char[inputArray.length];
		for (int i = 0; i < inputArray.length; i++) {
			resultArray[i] = (char) inputArray[i];
		}
		
		return resultArray;
	}
}
