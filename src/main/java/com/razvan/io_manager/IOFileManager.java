package com.razvan.io_manager;
import java.io.*;
import com.razvan.gui.ErrorDisplayManager;
import com.razvan.installation_manager.ApplicationInstallManager;

public class IOFileManager {
	private String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/userData";
	

	//Method used for writing the information that will later be used for decrypting the user data
	public void writeDataForDecryption(File fileToWrite, byte[] inputData) {
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < inputData.length; i++) {
			if(i == inputData.length -1) {
				sb.append(inputData[i]);
				break;
			}
			sb.append(inputData[i] + ",");
		}
		
		try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(fileToWrite))) {
			
			bWriter.write(sb.toString());
			
		} catch (IOException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
	}
	
	
	//Method for reading the information that will be used for decrypting the user data
	public String readDataForDecryption(File fileToRead) {
		String fileData = null;
		
		try (BufferedReader bReader = new BufferedReader(new FileReader(fileToRead))) {
			String line = null;
			while ((line = bReader.readLine()) != null) {
				fileData = line;
			}
		} catch (IOException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
		
		return fileData;
	}
	
	//Method for writing encrypted data to a specified file
	public void writeEncryptedData(File fileToWrite, byte[] inputData) {
		
		try (FileOutputStream outStream = new FileOutputStream(fileToWrite)) {
			outStream.write(inputData);
		} catch (IOException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
	}
	
	//Method for reading encrypted data from a specified file
	public byte[] readEncryptedData(File fileToRead) {
		byte[] fileData = null;
		
		try(FileInputStream inStream = new FileInputStream(fileToRead)) {
			fileData = inStream.readAllBytes();
			
		} catch(IOException ex) {
			ex.printStackTrace();
			String stackTrace = ErrorDisplayManager.getStackTraceMessage(ex);
			ErrorDisplayManager.displayError(null, stackTrace, ErrorDisplayManager.messageType.EXCEPTION);
		}
		
		return fileData;
	}
	
	public String readPlainText(File fileToRead) {
		StringBuilder sb = new StringBuilder();
		
		try (BufferedReader bReader = new BufferedReader(new FileReader(fileToRead))) {
			
			String line = null;
			while ((line = bReader.readLine()) != null) {
				sb.append(line);
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return sb.toString();
	}
	
	//The method that retrieves the data for the selected user or if they don't exist returns null
		public String[] retrieveUserAuthenticationData(String userName) {
			File authenticationDataFile = new File( ApplicationInstallManager.APP_MAIN_FOLDER_PATH  + "/userAuthentication/authentication_data");
			
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
	
		
	public void writeLoggingData(File file, String data) {
		
		try (BufferedWriter bWriter = new BufferedWriter(new FileWriter(file))) {
			bWriter.write(data);
			
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}


}
