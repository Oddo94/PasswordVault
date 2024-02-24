package com.razvan.gui;

import javax.swing.*;
import java.awt.*;

public class UserDashboard extends JFrame {
	//private PasswordDialog passwordDialog;

	public UserDashboard(String windowName) {

	}


	public UserDashboard(String userName, boolean hasData) {

		setMainWindow(userName, hasData);

	}

	void setMainWindow(String userName, boolean hasData) {
		//Pass the userName to the UserTableOperations constructor so that all the methods of this class can use it when trying to access the user data file
		UserTableOperations handler = new UserTableOperations(this, userName);
		WindowBuilder builder = new WindowBuilder(this, handler);

		JTable userDataTable = handler.createTable(0, 4);
		/* If there is data to display, the table will be created and filled with the
		 * respective data otherwise an empty table will be displayed
		 */
		if(hasData) {
			handler.fillTable(userDataTable);
			handler.addTableToWindow(userDataTable);
			//handler.addTableToWindow(handler.fillTable(handler.createTable(0,4)));
			
		} else {
			handler.addTableToWindow(userDataTable);
			//handler.addTableToWindow(handler.createTable(0,4));
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
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setVisible(true);
	}

	private void setWindowTitle() {
		JPanel dashboardTitlePanel = new JPanel();
		JLabel dashboardTitleLabel = new JLabel("Account list");
		dashboardTitlePanel.add(dashboardTitleLabel);
		add(dashboardTitlePanel, BorderLayout.NORTH);
	}

	private void addWindowListener() {

	}

}


