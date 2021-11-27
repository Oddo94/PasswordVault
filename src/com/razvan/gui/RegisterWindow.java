package com.razvan.gui;

import java.awt.BorderLayout;

import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import com.razvan.action_listener.RegisterActionListener;
import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.user_authentication.PasswordEncryptionManager;
import com.razvan.user_data_security.UserDataSecurityManager;
import com.razvan.utils.GUIInputChecker;
import lombok.*;

@Getter
@Setter
public class RegisterWindow extends JFrame {

	private JLabel titleLabel;
	private JLabel userNameLabel;
	private JLabel passwordLabel;
	private JLabel emailAddressLabel;

	private JTextField userNameField;
	private JPasswordField passwordField;
	private JTextField emailAddressField;

	private JButton registerButton;
	private JButton resetButton;
	private JButton redirectButton;

	private JPanel buttonPanel;
	private	JPanel inputLabelPanel;
	private	JPanel inputFieldPanel;
	private	JPanel containerInputPanel;
	private	JPanel titlePanel;
	private	JPanel authenticationPanel;

	//The application main folder path 
	//private String appDataPath = System.getProperty("user.home") + "/AppData/Roaming/PasswordVault";
	private String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;
	private PasswordEncryptionManager pem = new PasswordEncryptionManager();
	private UserDataSecurityManager securityManager = new UserDataSecurityManager();


	public RegisterWindow() {

		initializeWindowComponents();
		setupSubPanels();
		setupMainPanel();
		setupWindow();

		this.getRootPane().setDefaultButton(registerButton);
	}

	//Method that initializes the window components
	private void initializeWindowComponents() {
		//Panels
		buttonPanel = new JPanel();
		inputLabelPanel = new JPanel(new GridLayout(3,1,10,10));
		inputFieldPanel = new JPanel(new GridLayout(3,1,10,10));
		containerInputPanel = new JPanel();
		titlePanel = new JPanel();
		authenticationPanel = new JPanel(new BorderLayout());

		//Labels
		titleLabel = new JLabel("Register form");
		userNameLabel = new JLabel("Username");
		passwordLabel = new JLabel("Password");
		emailAddressLabel = new JLabel("Email address");

		//Fields
		userNameField = new JTextField(15);
		passwordField = new JPasswordField(15);
		emailAddressField = new JTextField(15);

		//Buttons
		registerButton = new JButton("Register");
		resetButton = new JButton("Reset");
		redirectButton = new JButton("Login");


	}

	private void setupSubPanels() {
		//Adds label to title panel
		titlePanel.add(titleLabel);

		//Adds labels to label container
		inputLabelPanel.add(userNameLabel);
		inputLabelPanel.add(passwordLabel);
		inputLabelPanel.add(emailAddressLabel);

		//Adds fields to field container
		inputFieldPanel.add(userNameField);
		inputFieldPanel.add(passwordField);
		inputFieldPanel.add(emailAddressField);

		//Adds the label and field panels to the container input panel(the one containing the labels and fields)
		containerInputPanel.add(inputLabelPanel);
		containerInputPanel.add(inputFieldPanel);


		//Adds components to the button panel
		buttonPanel.add(redirectButton, FlowLayout.LEFT);
		buttonPanel.add(resetButton, FlowLayout.CENTER);
		buttonPanel.add(registerButton, FlowLayout.RIGHT);

	}

	private void setupMainPanel() {
		//Adds all the sub-panels(title panel, container input panel, button panel) to the main panel 
		authenticationPanel.add(titlePanel, BorderLayout.NORTH);
		authenticationPanel.add(containerInputPanel, BorderLayout.CENTER);
		authenticationPanel.add(buttonPanel, BorderLayout.SOUTH);
	}

	private void setupWindow() {

		setSize(375,300);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		setResizable(false);
		setTitle("Register");
		add(authenticationPanel);
		addActionListenersToButtons();
		addWindowListener();
		setVisible(true);
	}


	public void addWindowListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);

			}
		});

	}

	private void addActionListenersToButtons() {

		resetButton.addActionListener(actionEvent -> {
			ArrayList<JTextComponent> fieldList = new ArrayList<>(Arrays.asList(userNameField, passwordField, emailAddressField));
			GUIInputChecker.resetFields(fieldList);

		});

		redirectButton.addActionListener(actionEvent -> {

			setVisible(false);
			new LoginWindow();

		});
		
		registerButton.addActionListener(new RegisterActionListener(this));
		

	}
}
