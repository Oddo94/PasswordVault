package com.razvan.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import com.toedter.calendar.JDateChooser;

public class GUIInputChecker {

	public static boolean hasDataOnRequiredFields (ArrayList<JTextComponent> fieldList) {
		if (fieldList == null || fieldList.size() == 0) {
			return false;
		}

		for (JTextComponent field : fieldList) {
			if ("".equals(field.getText()) || field.getText().matches("\\s+")) {
				return false;
			}
		}

		return true;
	}

	public static void resetFields(ArrayList<JTextComponent> fieldList) {
		Objects.requireNonNull(fieldList, "The fieldList argument cannot be null.");

		//There's no point in continuing if there are no elements present in the ArrayList
		if (fieldList.size() == 0) {
			return;
		}

		for (JTextComponent field : fieldList) {
			field.setText("");
		}
	}
	
	//Username check method(the username cannot start with _ , it can contain(a-z, A-Z, 0-9, _) and must have between 3 and 10 characters)
	public static boolean checkUsername(String userName, String regexPattern) {
		Objects.requireNonNull(userName, "The user name that has to be validated cannot be null");
		Objects.requireNonNull(regexPattern, "The regex pattern used for user name validation cannot be null.");

		//Pattern pattern = Pattern.compile("[_]{0}[\\w]{3,20}");
		Pattern pattern = Pattern.compile(regexPattern);
		Matcher matcher = pattern.matcher(userName);

		return matcher.matches();
	}


	//Method for checking the if the password contains all the required characters(lowercase/uppercase letters, digits,special characters)
	//It accepts varargs of regex patterns so that the implementation is more flexible and the password can be checked using multiple criteria
	public static boolean checkPasswordStrength(char[] password, int passwordLength, String...regexPatterns) {
		Objects.requireNonNull(password, "The password that has to be validated cannot be null");
		Objects.requireNonNull(regexPatterns, "The regex pattern used for password validation cannot be null.");
		
		//Transforms the password char[] array into a String
		String passwordString = new String(password);

		if (regexPatterns.length == 0) {
			return false;
		}

		//Checks password length before performing the actual regex check
		if (passwordString.length() < passwordLength) {
			return false;
		}
	
		boolean isValid = true;
		for (String regexPattern : regexPatterns) {
			Pattern pattern = Pattern.compile(regexPattern);
			Matcher matcher = pattern.matcher(passwordString);

			//If any of the provided regex pattern does not match the password the validation fails and the false result is returned to the calling method
			if (!matcher.find()) {
				isValid = false;
				break;
			}
		}

		return isValid;
	}
	
	//Method for checking if the email address is valid
	public static boolean isValidEmail(String emailAddress) {
		Objects.requireNonNull(emailAddress, "The email address that has to be validated cannot be null");
		
		try {
			InternetAddress emailAddressObject = new InternetAddress(emailAddress);
			emailAddressObject.validate();
			
		} catch (AddressException ex) {
			return false;
		}
		
		return true;
	}


}
