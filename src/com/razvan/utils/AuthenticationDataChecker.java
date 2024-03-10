package com.razvan.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthenticationDataChecker {

	public static boolean checkPasswordStrength(char[] inputPassword) {
		String password = new String(inputPassword);
		Pattern pattern = Pattern.compile("[\\w,.\\/<>?;'\\\\:\\|\\[\\]\\{\\}`~!@#\\$%\\^&\\*\\(\\)\\+=]{10,20}");
		Matcher matcher = pattern.matcher(password);
		
		//Password length check
		return matcher.matches() && password.length() >= 10 ? true : false;
	}
	
}
