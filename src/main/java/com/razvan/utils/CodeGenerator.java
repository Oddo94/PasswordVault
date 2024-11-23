package com.razvan.utils;

import java.security.SecureRandom;

public class CodeGenerator {
	
	public static String getConfirmationCode(int length) {
		if (length <= 0) {
			throw new IllegalArgumentException("The input length for the generated string cannot be less than or equal to zero.");
		}
		
		String characterSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklnmopqrstuvwxyz1234567890";
		
		SecureRandom secureRandom = new SecureRandom();
		
		StringBuilder sb = new StringBuilder();
		
		for (int i = 0; i < length; i++) {
			int generatedIndex = secureRandom.nextInt(characterSet.length());
			char selectedCharacter = characterSet.charAt(generatedIndex);
			
			sb.append(selectedCharacter);
		}
		
		return sb.toString();
		
	}


}
