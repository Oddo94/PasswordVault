package com.razvan.gui;

import com.razvan.action_listener.PasswordResetActionListener;
import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.user_authentication.LoginCredentialsChecker;
import com.razvan.user_authentication.PasswordEncryptionManager;
import com.razvan.utils.AuthenticationDataChecker;
import com.razvan.utils.MiscellaneousChecker;
import lombok.Getter;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class LoginWindow extends JFrame {
	//Labels for all the fields
	private JLabel titleLabel = new JLabel("Password Vault");
	private JLabel userNameLabel = new JLabel("Username");
	private JLabel passwordLabel = new JLabel("Password");
	private JLabel newPasswordLabel = new JLabel("New Password");
	private JLabel newConfirmPasswordLabel = new JLabel("Confirm Password");
	private JLabel blankLabel = new JLabel();
	private EmptyBorder emptyBorder = new EmptyBorder(10, 20, 20 ,20);


	//Username text field
	private JTextField userNameField = new JTextField(15);

	//Password fields(for login and reset forms)
	private JPasswordField passwordField = new JPasswordField(15);
	private JPasswordField newPasswordField = new JPasswordField(15);
	private JPasswordField confirmPasswordField = new JPasswordField(15);

	//Checkbox for toggling the password reset form on/off
	private JCheckBox changePasswordCheckbox =  new JCheckBox("Change password");

	//Window buttons
	private JButton loginButton = new JButton("Login");
	private JButton resetButton = new JButton("Reset");
	private JButton registerButton = new JButton("Register");
	private JButton resetPasswordButton = new JButton("Reset password");

	//The panel containing the window title
	private JPanel titlePanel = new JPanel();
	//The panel containing both the login and reset forms
	private JPanel containerInputPanel = new JPanel(new BorderLayout());
	//Panel for the user login form
	private JPanel credentialsInputPanel = new JPanel(new GridLayout(3,2,0,5));
	//Panel for the reset password form
	private JPanel resetPasswordPanel = new JPanel(new GridLayout(2,2,0,5));
	//Panel for the window buttons
	private JPanel buttonPanel = new JPanel();

	//Panel containing the whole login form panel(used for easier positioning inside the main panel) 
	private JPanel passwordInputPanel = new JPanel();
	//JPanel checkBoxPanel = new JPanel();
	//Panel containing the whole reset form panel(used for easier positioning inside the main panel) 
	private JPanel changePasswordInputPanel = new JPanel();

	//The main window panel containing all the components
	JPanel authenticationPanel = new JPanel(new BorderLayout());

	public LoginWindow() {

		initLoginWindow();
		setSize(375, 300);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setTitle("Login");
		add(authenticationPanel);
		this.getRootPane().setDefaultButton(loginButton);
		setVisible(true);

		checkSenderAccountCredentialsExistence();


	}

	private void initLoginWindow() {
		//Creating the arrays containing the password dialog components
		ArrayList<Component> loginFormComponents = new ArrayList<Component>(Arrays.asList(userNameLabel, userNameField, passwordLabel, passwordField, blankLabel, changePasswordCheckbox));
		ArrayList<Component> resetFormComponents = new ArrayList<Component>(Arrays.asList(newPasswordLabel, newPasswordField, newConfirmPasswordLabel, confirmPasswordField));
		ArrayList<Component> windowButtons = new ArrayList<Component>(Arrays.asList(newPasswordLabel, newPasswordField, newConfirmPasswordLabel, confirmPasswordField));

		//Adding components to panels
		credentialsInputPanel.setBorder(new EmptyBorder(20,20,20,20));//Setting a border at the top of the panel for spacing
		this.addComponentsToPanel(loginFormComponents, credentialsInputPanel);

		this.addComponentsToPanel(resetFormComponents, resetPasswordPanel);
		resetPasswordPanel.setVisible(false);//Hiding the reset password panel

		this.addComponentsToButtonPanel();
		resetPasswordButton.setVisible(false);//Hiding the reset password button

		//Adding form panels to sub-panels
		this.setupSubPanels();

		//Adding sub-panels to main panel
		this.setupMainPanel();

		//Adding main panel to window
		this.add(authenticationPanel);

		//Adding action listeners to buttons
		//this.addActionListenersToFormButtons(this);
		addActionListenerToCheckBox();
		addActionListenerToButtons();

	}


	private void addComponentsToButtonPanel() {
		buttonPanel.add(resetPasswordButton, FlowLayout.LEFT);
		buttonPanel.add(registerButton, FlowLayout.LEFT);
		buttonPanel.add(resetButton, FlowLayout.CENTER);
		buttonPanel.add(loginButton, FlowLayout.RIGHT);
	}

	private void setupSubPanels() {
		titlePanel.add(titleLabel);

		passwordInputPanel.add(credentialsInputPanel);
		changePasswordInputPanel.add(resetPasswordPanel);

		containerInputPanel.add(passwordInputPanel, BorderLayout.NORTH);
		containerInputPanel.add(changePasswordInputPanel, BorderLayout.CENTER);
	}

	private void setupMainPanel() {
		authenticationPanel.add(titlePanel, BorderLayout.NORTH);
		authenticationPanel.add(containerInputPanel, BorderLayout.CENTER);
		authenticationPanel.add(buttonPanel, BorderLayout.SOUTH);	
	}

	public void addComponentsToPanel(ArrayList<Component> components, JPanel panel) {
		for (Component currentComponent : components) {
			panel.add(currentComponent);
		}
	}


	private void addActionListenerToCheckBox() {
		changePasswordCheckbox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (changePasswordCheckbox.isSelected()) {
					resetPasswordPanel.setVisible(true);
					loginButton.setVisible(false);
					resetPasswordButton.setVisible(true);
					passwordField.setEnabled(false);
				} else {
					resetPasswordPanel.setVisible(false);
					loginButton.setVisible(true);
					resetPasswordButton.setVisible(false);
					passwordField.setEnabled(true);
				}
			}


		});
	}

	private void addActionListenerToButtons() {
	
		resetPasswordButton.addActionListener(new PasswordResetActionListener(this));

		loginButton.addActionListener(actionEvent -> {
			//Checks if the user has provided the credentials before trying to login
			if (!hasDataOnRequiredFields()) {
				return;
			}

			String userName = userNameField.getText();
			char[] password = passwordField.getPassword();

			LoginCredentialsChecker loginCredentialsChecker = new LoginCredentialsChecker(userName, password);		

			if (!loginCredentialsChecker.userExists()) {
				JOptionPane.showMessageDialog(this,  "The requested user account does not exist! Please try again.");
				return;
			}

			if (!loginCredentialsChecker.hasValidCredentials()) {
				JOptionPane.showMessageDialog(this, "Invalid username/password! Please try again.");
				return;
			}


			boolean userHasData = loginCredentialsChecker.userHasData();
			this.setVisible(false);
			this.dispose();
			new UserDashboard(userName, userHasData);

		});

		resetButton.addActionListener(actionEvent -> {
			resetAllFields();
		});

		registerButton.addActionListener(actionEvent ->  {
			this.setVisible(false);

			new RegisterWindow();

		});

	}


	private int checkPasswordResetInputData() {
		String userName = userNameField.getText();
		ArrayList<JTextComponent> fieldList = new ArrayList<>(List.of(userNameField, newPasswordField, confirmPasswordField));
		PasswordEncryptionManager pem = new PasswordEncryptionManager();

		if (!hasDataOnRequiredFields(fieldList)) {
			JOptionPane.showMessageDialog(this, "Please fill in the username, new password and confirm password fields!" );
			return -1;
		}

		if (!pem.userExists(userName)) {
			JOptionPane.showMessageDialog(this, "The requested user does not exist! Please try again.");
			return -1;
		}

		if (!AuthenticationDataChecker.checkPasswordStrength(newPasswordField.getPassword())) {			
			JOptionPane.showMessageDialog(this, "The chosen password is not strong enough! "
					+ "Your password should: \n 1. be at least 10 characters long \n 2. contain lowercase and uppercase letters(a-zA-Z), special characters(!@#*/) and digits(0-9)");

			return -1;
		}


		boolean passwordsMatch = Arrays.equals(newPasswordField.getPassword(), confirmPasswordField.getPassword());

		if (!passwordsMatch) {
			JOptionPane.showMessageDialog(this, "The new password does not match the confirmation password!");
			return -1;
		}

		return 0;

	}

	//Checks if all the credentials are provided by the user before the login
	private boolean hasDataOnRequiredFields() {
		boolean isFilled = true;
		String userName = userNameField.getText();

		if (userNameField.getText().isEmpty() && passwordField.getPassword().length == 0) {

			JOptionPane.showMessageDialog(this, "Please fill in the username and password fields!");
			isFilled = false;			

		} else if(userNameField.getText().isEmpty()) {

			JOptionPane.showMessageDialog(this, "Please fill in the username field!");
			isFilled = false;

		} else if(passwordField.getPassword().length == 0) {

			JOptionPane.showMessageDialog(this, "Please fill in the password field!");
			isFilled = false;		

		}

		return isFilled;

	}

	public void resetAllFields() {
		userNameField.setText("");
		passwordField.setText("");
		newPasswordField.setText("");
		confirmPasswordField.setText("");
	}

	private boolean hasDataOnRequiredFields (ArrayList<JTextComponent> fieldList) {
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

	//Method for checking if the sender_account_credentials file contains(at application startup)
	private void checkSenderAccountCredentialsExistence() {
		if (!MiscellaneousChecker.hasDataInsideSenderAccountCredentialsFile()) {
			String fileLocation = ApplicationInstallManager.APP_MAIN_FOLDER_PATH + "/passwordReset/sender_account_credentials.xml";

			//Creates custom message dialog which contains the message displayed to the user and a check box which will allow him to avoid the future display of the dialog 
			JCheckBox dontShowAgainCheckbox = new JCheckBox("Don't show this message again");//TO BE IMPLEMENTED-needs action listener and a way to persist the user option
			String message = String.format("No data was found inside the sender account credentials file located at:\n%s\nWould you like to set them up now?", fileLocation);
			Object[] components = new Object[] {message, dontShowAgainCheckbox};
			int userOption = JOptionPane.showConfirmDialog(this, components, "Sender account credentials file info", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

			if (userOption == 0) {

				new SenderAccountCredentialsWindow();
			}
		}
	}
}
