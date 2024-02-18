package com.razvan.user_authentication;

//import static org.junit.jupiter.api.Assertions.*;
//
//import java.io.UnsupportedEncodingException;
//import java.nio.charset.StandardCharsets;
//
//import org.junit.jupiter.api.Test;

//Clasa de test pentru metodele de securizare a parolei
class PasswordEncryptionManagerTest {
	private PasswordEncryptionManager pem = new PasswordEncryptionManager();
	
//	@Test
//	void testPasswordEncryption() throws UnsupportedEncodingException {
//		String[] authenticationData = pem.retrieveUserAuthenticationData("TEST5");
//		System.out.println("USERNAME:" + authenticationData[0]);
//		System.out.println("SALT:" + authenticationData[1]);
//		System.out.println("HASHED PASSWORD:" + authenticationData[2]);
//		
//		
//		String[] inputSalt = authenticationData[1].split(",");
//		byte[] salt = new byte[16];
//		
//		for (int i = 0; i < inputSalt.length; i++) {
//			salt[i] = Byte.parseByte(inputSalt[i]);
//		}
//		
//		
//		String expectedEncryptedPassword = authenticationData[2];
//		String actualEncryptedPassword = pem.encryptPassword("24AzF#ABC1",salt);
//		System.out.println("EXPECTED:" + expectedEncryptedPassword);
//		System.out.println("ACTUAL:" + actualEncryptedPassword);
//		assertEquals(expectedEncryptedPassword, actualEncryptedPassword);
//		
//		
//	}

}
