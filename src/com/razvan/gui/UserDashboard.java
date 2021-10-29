package com.razvan.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.*;
import javax.swing.text.JTextComponent;

import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.user_authentication.PasswordEncryptionManager;

import java.io.*;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;

public class UserDashboard extends JFrame {
	//private PasswordDialog passwordDialog;

	public UserDashboard(String windowName) {

	}


	public UserDashboard(String userName, boolean hasData) {
//		//creaza fereastra de login
//		passwordDialog = new PasswordDialog(this, true);
//		passwordDialog.setSize(375, 300);
//		passwordDialog.setResizable(false);
//		passwordDialog.setLocationRelativeTo(null);
//		passwordDialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
//		passwordDialog.setTitle("Login");
//		passwordDialog.setVisible(true);
//
//		//creaza fereastra principala-dashboardul userului
//		//setMainWindow();
		
		setMainWindow(userName, hasData);

	}

	///CODE MODIFICATION FOR LETTING THE USER LOGIN EVEN IF THERE ARE NO SAVED ACCOUNTS YET
	void setMainWindow(String userName, boolean hasData) {
		//Pass the userName to the UserTableOperations constructor so that all the methods of this class can use it when trying to access the user data file
		UserTableOperations handler = new UserTableOperations(this, userName);
		WindowBuilder builder = new WindowBuilder(this, handler);

		/* If there is data to display, the table will be created and filled with the
		 * respective data otherwise an empty table will be displayed
		 */
		if(hasData) {
			handler.addTableToWindow(handler.fillTable(handler.createTable(0,4)));
		} else {
			handler.addTableToWindow(handler.createTable(0,4));
		}
		
		handler.addMouseListenerToTable();
		handler.addTableModelListener();
		builder.setUpMenu();
		builder.createNewEntryForm();
		
		setSize(700, 700);
		setTitle("Password Vault-" + userName + "'s" + " dashboard");
		setWindowTitle();
		setIconImage(new ImageIcon("resources/app_icon_64.png").getImage());
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}
	


	private void setWindowTitle() {
		JPanel dashboardTitlePanel = new JPanel();
		JLabel dashboardTitleLabel = new JLabel("Account list");
		dashboardTitlePanel.add(dashboardTitleLabel);
		add(dashboardTitlePanel, BorderLayout.NORTH);
	}


//	class PasswordDialog extends JDialog {
//		//Dashboard window frame
//		private JFrame parentWindow;
//		//Labels for all the fields
//		private JLabel titleLabel = new JLabel("Password Vault");
//		private JLabel userNameLabel = new JLabel("Username");
//		private JLabel passwordLabel = new JLabel("Password");
//		private JLabel newPasswordLabel = new JLabel("New Password");
//		private JLabel newConfirmPasswordLabel = new JLabel("Confirm Password");
//		private JLabel blankLabel = new JLabel();
//
//		//Username text field
//		private JTextField userNameField = new JTextField(15);
//
//		//Password fields(for login and reset forms)
//		private JPasswordField passwordField = new JPasswordField(15);
//		private JPasswordField newPasswordField = new JPasswordField(15);
//		private JPasswordField confirmPasswordField = new JPasswordField(15);
//
//		//Checkbox for toggling the password reset form on/off
//		private JCheckBox changePasswordCheckbox =  new JCheckBox("Change password");
//
//		//Window buttons
//		private JButton loginButton = new JButton("Login");
//		private JButton resetButton = new JButton("Reset");
//		private JButton registerButton = new JButton("Register");
//		private JButton resetPasswordButton = new JButton("Reset password");
//
//		//The panel containing the window title
//		private JPanel titlePanel = new JPanel();
//		//The panel containing both the login and reset forms
//		private JPanel containerInputPanel = new JPanel(new BorderLayout());
//		//Panel for the user login form
//		private JPanel credentialsInputPanel = new JPanel(new GridLayout(3,2,0,5));
//		//Panel for the reset password form
//		private JPanel resetPasswordPanel = new JPanel(new GridLayout(2,2,0,5));
//		//Panel for the window buttons
//		private JPanel buttonPanel = new JPanel();
//
//		//Panel containing the whole login form panel(used for easier positioning inside the main panel) 
//		private JPanel passwordInputPanel = new JPanel();
//		//JPanel checkBoxPanel = new JPanel();
//		//Panel containing the whole reset form panel(used for easier positioning inside the main panel) 
//		private JPanel changePasswordInputPanel = new JPanel();
//
//		//The main window panel containing all the components
//		JPanel authenticationPanel = new JPanel(new BorderLayout());
//
////		private String appDataPath = System.getProperty("user.home") + "/AppData/Roaming/PasswordVault";
//		private String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;
//		private String userName;
//
//		
//		private PasswordEncryptionManager pem = new PasswordEncryptionManager();
//
//
//		public PasswordDialog(JFrame parentWindow, boolean modal) {
//			super(parentWindow, modal);
//			this.parentWindow = parentWindow;
//			this.getRootPane().setDefaultButton(loginButton);//sets the login button as default so that it is automatically activated when pressing the Enter key
//
//			this.initPasswordDialog();
//		}
//
//		public void resetPassword(File userAuthenticationData, String username, String hashCode) {
//			StringBuilder result = new StringBuilder();
//
//			try (BufferedReader bReader = new BufferedReader(new FileReader(userAuthenticationData))) {
//
//				String line = null;
//				while((line = bReader.readLine()) != null) {
//					//if ()
//					result.append(line);
//				}
//
//			} catch (IOException ex) {
//				ex.printStackTrace();
//			}
//		}
//
//		private void initPasswordDialog() {
//			//Creating the arrays containing the password dialog components
//			ArrayList<Component> loginFormComponents = new ArrayList<Component>(Arrays.asList(userNameLabel, userNameField, passwordLabel, passwordField, blankLabel, changePasswordCheckbox));
//			ArrayList<Component> resetFormComponents = new ArrayList<Component>(Arrays.asList(newPasswordLabel, newPasswordField, newConfirmPasswordLabel, confirmPasswordField));
//			ArrayList<Component> windowButtons = new ArrayList<Component>(Arrays.asList(newPasswordLabel, newPasswordField, newConfirmPasswordLabel, confirmPasswordField));
//
//			//Adding components to panels
//			credentialsInputPanel.setBorder(new EmptyBorder(20,20,20,20));//Setting a border at the top of the panel for spacing
//			this.addComponentsToPanel(loginFormComponents, credentialsInputPanel);
//
//			this.addComponentsToPanel(resetFormComponents, resetPasswordPanel);
//			resetPasswordPanel.setVisible(false);//Hiding the reset password panel
//
//			this.addComponentsToButtonPanel();
//			resetPasswordButton.setVisible(false);//Hiding the reset password button
//
//			//Adding form panels to sub-panels
//			this.setupSubPanels();
//
//			//Adding sub-panels to main panel
//			this.setupMainPanel();
//
//			//Adding main panel to window
//			this.add(authenticationPanel);
//
//			//Adding action listeners to buttons
//			this.addActionListenersToFormButtons(this);
//
//		}
//
//		private void addComponentsToButtonPanel() {
//			buttonPanel.add(resetPasswordButton, FlowLayout.LEFT);
//			buttonPanel.add(loginButton, FlowLayout.LEFT);
//			buttonPanel.add(resetButton, FlowLayout.CENTER);
//			buttonPanel.add(registerButton, FlowLayout.RIGHT);
//		}
//
//		private void setupSubPanels() {
//			titlePanel.add(titleLabel);
//
//			passwordInputPanel.add(credentialsInputPanel);
//			changePasswordInputPanel.add(resetPasswordPanel);
//
//			containerInputPanel.add(passwordInputPanel, BorderLayout.NORTH);
//			containerInputPanel.add(changePasswordInputPanel, BorderLayout.CENTER);
//		}
//
//		private void setupMainPanel() {
//			authenticationPanel.add(titlePanel, BorderLayout.NORTH);
//			authenticationPanel.add(containerInputPanel, BorderLayout.CENTER);
//			authenticationPanel.add(buttonPanel, BorderLayout.SOUTH);	
//		}
//
//		public void addComponentsToPanel(ArrayList<Component> components, JPanel panel) {
//			for (Component currentComponent : components) {
//				panel.add(currentComponent);
//			}
//		}
//
//
//		private void addActionListenersToFormButtons(PasswordDialog pDialog) {
//
//			changePasswordCheckbox.addActionListener(actionEvent ->{
//				if (changePasswordCheckbox.isSelected()) {
//					resetPasswordPanel.setVisible(true);
//					loginButton.setVisible(false);
//					resetPasswordButton.setVisible(true);
//				} else {
//					resetPasswordPanel.setVisible(false);
//					loginButton.setVisible(true);
//					resetPasswordButton.setVisible(false);
//				}
//			});
//
//			loginButton.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					System.out.println("Login button pressed!");
//
//				}
//
//			});
//
//			resetPasswordButton.addActionListener(new ActionListener() {
//				@Override
//				public void actionPerformed(ActionEvent e) {
//					String userName = userNameField.getText();
//					char[] password = passwordField.getPassword();
//
//					//Checks if the user name and password fields contain data
//					if (pDialog.isEmpty(userNameField, passwordField)) {
//						JOptionPane.showMessageDialog(pDialog, "Please provide a username and/or password!");
//						return;
//					}
//
//					//Checks if the provided user exists
//					if (!pem.userExists(userName)) {
//						JOptionPane.showMessageDialog(pDialog, "The requested user doesn't exist!Please try again.");
//						return;
//					}
//
//					//Checks if the login credentials are correct
//					if(!pDialog.hasValidCredentials(userName, password)) {
//						JOptionPane.showMessageDialog(pDialog, "Invalid user name and/or password! Please try again.");
//						return;
//					}
//
//
//					int newPasswordLength = newPasswordField.getPassword().length;
//					int confirmPasswordLength =  confirmPasswordField.getPassword().length;
//
//					//Checks if the password and confirm password fields contain data
//					if (newPasswordLength == 0 || confirmPasswordLength == 0 ) {
//						JOptionPane.showMessageDialog(pDialog, "Please fill in the new password and confirm password fields!");
//						return;
//					}
//					
//				
//
//					char[] newPassword = newPasswordField.getPassword();
//					char[] confirmPassword = confirmPasswordField.getPassword();
//					System.out.println(newPassword);
//					System.out.println(confirmPassword);
//
//					boolean isMatch = Arrays.equals(newPassword, confirmPassword) ? true : false;
//					System.out.println(isMatch);
//					
//					//Checks the password strength
//					if (!RegisterDialog.checkPasswordStrength(String.valueOf(newPassword))) {
//						System.out.println("INSIDE PASSWORD STRENGTH CHECK IF STATEMENT");
//						JOptionPane.showMessageDialog(pDialog, "The chosen password is not strong enough! "
//						+ "Your password should: \n 1. be at least 10 characters long \n 2. contain lowercase and uppercase letters(a-zA-Z), special characters(!@#*/) and digits(0-9)");
//						return;
//					}
//
//
//					if (isMatch) {
//						String userAuthenticationData = pem.readAuthenticationDataFile();
//						byte[] salt = pem.getSalt(16);
//						byte[] retrievedSalt = pem.restoreSalt(pem.retrieveUserAuthenticationData(userName)[1]);
//						String generatedHashCode = pem.encryptPassword(new String(newPasswordField.getPassword()), retrievedSalt);
//						String retrievedHashCode = pem.retrieveUserAuthenticationData(userName)[2];
//						
//						String saltString = pem.converSaltToString(salt);
//						//MODIFICATION
//						String resetPasswordHashCode = pem.encryptPassword(new String(newPasswordField.getPassword()), salt);
//
//						if (!pem.hashCodesMatch(generatedHashCode, retrievedHashCode)) {
//
//							System.out.println("Reset password button pressed!");
//							//pDialog.writeAuthenticationDataToFile(pDialog.resetPassword(userName, userAuthenticationData, saltString, generatedHashCode));
//							//MODIFICATION
//							pem.writeAuthenticationDataToFile(pem.resetPassword(userName, userAuthenticationData, saltString, resetPasswordHashCode));
//							pDialog.resetAllFields();
//							JOptionPane.showMessageDialog(pDialog, "Your password was successfully reset!");
//							resetPasswordPanel.setVisible(false);
//							changePasswordCheckbox.setSelected(false);
//							resetPasswordButton.setVisible(false);
//
//							loginButton.setVisible(true);
//							
//
//						} else {
//							JOptionPane.showMessageDialog(pDialog, "Please enter a different password from the old one!");
//						}
//
//					} else {
//						JOptionPane.showMessageDialog(pDialog, "The new password and the confirmation password don't match!");
//					}
//				}
//			});
//
//			resetButton.addActionListener(actionEvent -> {
//				resetAllFields();
//			});
//			
//			
//			registerButton.addActionListener(actionEvent -> {
//
//				setVisible(false);
//				new RegisterDialog(parentWindow,this, true);
//
//			});
//
//			loginButton.addActionListener(actionEvent -> {
//				boolean isFilled = true;
//				String userName = userNameField.getText();
//
//				if (userNameField.getText().isEmpty() && passwordField.getPassword().length == 0) {
//
//					JOptionPane.showMessageDialog(this, "Please fill in the username and password fields!");
//					isFilled = false;
//					return;
//
//				} else if(userNameField.getText().isEmpty()) {
//
//					JOptionPane.showMessageDialog(this, "Please fill in the username field!");
//					isFilled = false;
//					return;
//
//				} else if(passwordField.getPassword().length == 0) {
//
//					JOptionPane.showMessageDialog(this, "Please fill in the password field!");
//					isFilled = false;
//					return;
//
//				}
//
//
//
//				File authenticationData = new File(appDataPath + "/userAuthentication/Authentication_Data");
//				if(!authenticationData.exists()) {
//					JOptionPane.showMessageDialog(this, "There are no user accounts.\n Please create one before trying to login!");
//					return;
//				}
//
//				//User authentication logic
//				if (pem.userExists(userName)) {
//					String[] userData = pem.retrieveUserAuthenticationData(userName);
//
//					String expectedHashCode = userData[2];
//					String actualHashCode = pem.encryptPassword(new String(passwordField.getPassword()), pem.restoreSalt(userData[1]));
//					if (pem.hashCodesMatch(expectedHashCode, actualHashCode)) {
//						//This is where the user dashboard is created and it is made visible
//						//CODE MODIFICATION!!
//						setMainWindow(userNameField.getText(), userHasData(userName));
//						resetAllFields();
//						this.setVisible(false);
//						parentWindow.setVisible(true);
//						
//						
//					} else {
//						resetAllFields();
//						JOptionPane.showMessageDialog(this, "Invalid username/password! Please try again.");
//					}
//
//				} else {
//					JOptionPane.showMessageDialog(this,  "The requested user account does not exist! Please try again.");
//					resetAllFields();
//				}
//
//			});
//
//
//
//			addWindowListener(new WindowAdapter() {
//				@Override
//				public void windowClosing(WindowEvent e) {
//
//					System.exit(0);
//
//				}
//			});	
//			
//		}
//
//
//		private boolean isEmpty(JTextComponent firstComponent, JTextComponent secondComponent) {
//			if ("".equals(firstComponent.getText()) || "".equals(secondComponent.getText())) {
//				return true;
//			}
//
//			return false;
//
//		}
//
//		//Method for checking if the user has entered the correct password
//		private boolean hasValidCredentials(String userName, char[] password) {
//			String[] userData = pem.retrieveUserAuthenticationData(userName);
//
//			String expectedHashCode = userData[2];
//			String actualHashCode = pem.encryptPassword(new String(password), pem.restoreSalt(userData[1]));
//
//			return expectedHashCode.equals(actualHashCode);
//		}
//		
//
//		private void resetAllFields() {
//			userNameField.setText("");
//			passwordField.setText("");
//			newPasswordField.setText("");
//			confirmPasswordField.setText("");
//		}
//	
//
//		//Checks if the user has any stored data
//		private boolean userHasData(String userName) {
//			File fileToCheck = new File(appDataPath + "/userData/" + userName);
//
//			return fileToCheck.length() != 0;
//		}
	}


