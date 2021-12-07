package com.razvan.gui;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import org.jdatepicker.DateModel;
import org.jdatepicker.JDatePicker;

//import com.razvan.gui.UserDashboard.PasswordDialog;
//import datechooser.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
public class WindowBuilder extends MouseAdapter {
	private UserDashboard userDashboard;
	private UserTableOperations handler;
	//private PasswordDialog passwordDialog;
	private JPanel parentPanel = new JPanel(new BorderLayout());
	//Creating the entry form panel
	private JPanel newEntryForm = new JPanel();

	//Field labels
	private JLabel accountNameLabel = new JLabel("Account name");
	private JLabel userNameLabel = new JLabel("User name");
	private JLabel passwordLabel = new JLabel("Password");
	private JLabel lastChangeDateLabel = new JLabel("Last change date");

	//Form input fields
	private JTextField accountNameField = new JTextField();
	private JTextField userNameField = new JTextField();
	private JTextField passwordField = new JTextField(15);
	private JTextField lastChangeDateField = new JTextField(15);

	//Form buttons
	private JButton addNewEntryButton = new JButton("Add entry");
	private JButton resetFormButton = new JButton("Reset");


	private JDatePicker datePicker = new JDatePicker();




	public WindowBuilder(UserDashboard userDashboard, UserTableOperations handler) {
		this.userDashboard = userDashboard;
		this.handler = handler;
		//this.passwordDialog = passwordDialog;

		datePicker.setMaximumSize(new Dimension(150, 25));
		accountNameField.setMaximumSize(new Dimension(300, 25));
		userNameField.setMaximumSize(new Dimension(300, 25));
		passwordField.setMaximumSize(new Dimension(300, 25));
	}

	public void createNewEntryForm() {
		//Creating field and field name arrays for further processing
		JComponent[] fieldsArray = {accountNameField, userNameField, passwordField, datePicker};
		String[] fieldNames = {"Account field", "User field", "Password field"};

		//Setting field names for further checks regarding the state of each field(empty or not)
		setNamesToInputFields(fieldsArray, fieldNames);

		//Setting the form layout
		setFormComponentsLayout(newEntryForm);

		/*Adding the entry form panel to the parent panel
	    and the parent panel to the main window */
		parentPanel.add(newEntryForm,BorderLayout.CENTER);
		userDashboard.add(parentPanel, BorderLayout.SOUTH);

		//Adding action listeners
		addActionListenersToFormButtons(fieldsArray);	
	}

	private void setFormComponentsLayout(JPanel newEntryForm) {
		GroupLayout layout = new GroupLayout(newEntryForm);
		newEntryForm.setLayout(layout);

		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		//Adding the components horizontally, one after the other into two groups
		GroupLayout.SequentialGroup hGroup = layout.createSequentialGroup();
		hGroup.addGroup(layout.createParallelGroup().addComponent(accountNameLabel).addComponent(userNameLabel).addComponent(passwordLabel).addComponent(lastChangeDateLabel).addComponent(resetFormButton));
		//		hGroup.addGroup(layout.createParallelGroup().addComponent(accountNameField).addComponent(userNameField).addComponent(passwordField).addComponent(lastChangeDateField).addComponent(addNewEntryButton));
		hGroup.addGroup(layout.createParallelGroup().addComponent(accountNameField).addComponent(userNameField).addComponent(passwordField).addComponent(datePicker).addComponent(addNewEntryButton));
		layout.setHorizontalGroup(hGroup);

		//Setting vertical alignment of the form components
		GroupLayout.SequentialGroup vGroup = layout.createSequentialGroup();
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(accountNameLabel).addComponent(accountNameField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(userNameLabel).addComponent(userNameField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				addComponent(passwordLabel).addComponent(passwordField));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).
				//				addComponent(lastChangeDateLabel).addComponent(lastChangeDateField));
				addComponent(lastChangeDateLabel).addComponent(datePicker));
		vGroup.addGroup(layout.createParallelGroup(Alignment.BASELINE).addComponent(resetFormButton).addComponent(addNewEntryButton));
		layout.setVerticalGroup(vGroup);
	}

	//Top menu creation
	public void setUpMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu optionMenu = new JMenu("Options");

		JMenuItem logoutOption = new JMenuItem("Logout");
		JMenuItem exitOption = new JMenuItem("Exit");

		optionMenu.add(logoutOption);
		optionMenu.add(exitOption);

		menuBar.add(optionMenu);

		addActionListenersToMenuOptions(logoutOption, exitOption);
		userDashboard.setJMenuBar(menuBar);

	}


	public void addActionListenersToMenuOptions(JMenuItem logoutOption, JMenuItem exitOption) {
		logoutOption.addActionListener(actionEvent ->{
			//CHANGE-removes all the rows from the user data table when the user logs out of his account
			DefaultTableModel userDataTableModel = handler.getUserDataTableModel();
			LoginWindow loginWindow = new LoginWindow();

			userDashboard.setVisible(false);
			//CHANGE-removes the user dashboard window after logout
			userDashboard.dispose();
			loginWindow.setVisible(true);

		});

		exitOption.addActionListener(actionEvent -> {
			System.exit(0);
		});
	}

	//Method for setting the names of input fields
	private void setNamesToInputFields(JComponent[] inputFields, String[] fieldNames) {
		if (inputFields.length != fieldNames.length) {
			return;
		}

		int i = 0;
		for (JComponent currentField : inputFields) {
			currentField.setName(fieldNames[i++]);
		}	

	}

	//Method for collecting the data entered by the user 
	private String collectNewEntryData(JComponent[] fieldsArray ) {
		StringBuilder rowData = new StringBuilder();

		//    	int length = fieldsArray.length;
		//    	for (int i = 0; i < length; i++) {
		//    		if(i == length - 1) {
		//    			/*If the data field is left empty by the user, the corresponding data cell from the table will
		//    			 be filled with a string having the default value "null"  */
		//    			if ("".equals(fieldsArray[length - 1].getText())) {
		//    				rowData.append("null");
		//    				break;
		//    			} else {
		//    				rowData.append(fieldsArray[i].getText());
		//    				break;
		//    			}
		//    		} 
		//
		//    		rowData.append(fieldsArray[i].getText() + ",");
		//    	}
		//    	
		//    	LocalDate inputDate = LocalDate.of(datePicker.getModel().getYear(), datePicker.getModel().getMonth(), datePicker.getModel().getDay());
		//    	if ("".equals(inputDate.toString())) {
		//    		rowData.append("null");
		//    	} else {
		//    		rowData.append("," + inputDate.toString());
		//    	}

		for (int i = 0; i < fieldsArray.length; i++) {
			String fieldContent = "";
			if (fieldsArray[i].getClass() == JTextField.class) {
				fieldContent = ((JTextField) fieldsArray[i]).getText();

			} else if (fieldsArray[i].getClass() == JDatePicker.class) {
				DateModel dateModel = datePicker.getModel();
				int year = dateModel.getYear();
				int month = dateModel.getMonth();
				int day = dateModel.getDay();

				LocalDate inputDate = LocalDate.of(year, month, day);
				DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
				fieldContent = dtf.format(inputDate);
			}

			if (i != fieldsArray.length - 1) {
				if ("".equals(fieldContent)) {
					rowData.append("null" + ",");
				} else {
					rowData.append(fieldContent + ",");

				}
			} else {
				if ("".equals(fieldContent)) {
					rowData.append("null");
				} else {
					rowData.append(fieldContent);

				}
			}


		}

		return rowData.toString();
	}



	public void addActionListenersToFormButtons(JComponent[] fieldsArray) {

		//Adding action listener to the reset button of the entry form
		resetFormButton.addActionListener(actionEvent -> {
			for (JComponent currentField : fieldsArray) {
				if (currentField.getClass() == JTextField.class) {
					((JTextField) currentField).setText("");

				} else if (currentField.getClass() == JDatePicker.class) {
					DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy");
					LocalDate currentDate = LocalDate.now();
					((JDatePicker) currentField).getModel().setDate(currentDate.getYear(), currentDate.getMonthValue(), currentDate.getDayOfMonth());
				}
			}
		});

		//Adding action listener to the add new entry button of the entry form
		addNewEntryButton.addActionListener(actionEvent -> {

			for (JComponent currentField : fieldsArray) {
				if (currentField.getClass() == JTextField.class) {
					if (((JTextField) currentField).getText().isEmpty()) {
						JOptionPane.showMessageDialog(userDashboard, "Please fill in the required fields!");
						return;
					}
				}

			}

			String rowData = collectNewEntryData(fieldsArray);

			handler.addNewEntry(rowData.toString());
			UserTableOperations.userOption = JOptionPane.showConfirmDialog(null,"Do you want to insert a new row?","Data saving",JOptionPane.YES_NO_CANCEL_OPTION);
			if( UserTableOperations.userOption == 0) {
				handler.getUserDataTableModel().addRow(rowData.toString().split(","));
			}
		});
	}











}
