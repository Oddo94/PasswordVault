package com.razvan.gui;
import javax.swing.*;
import javax.swing.GroupLayout.Alignment;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import com.razvan.gui.UserDashboard.PasswordDialog;
//import datechooser.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
public class WindowBuilder extends MouseAdapter {
	private UserDashboard userDashboard;
	private UserTableOperations handler;
	private PasswordDialog passwordDialog;
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
	
	
	
	public WindowBuilder(UserDashboard userDashboard, UserTableOperations handler, PasswordDialog passwordDialog) {
		this.userDashboard = userDashboard;
		this.handler = handler;
		this.passwordDialog = passwordDialog;
	}
	
	public void createNewEntryForm() {
		//Creating field and field name arrays for further processing
		JTextField[] fieldsArray = {accountNameField, userNameField, passwordField, lastChangeDateField};
		String[] fieldNames = {"Account field", "User field", "Password field", "Date field"};
		
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
		hGroup.addGroup(layout.createParallelGroup().addComponent(accountNameField).addComponent(userNameField).addComponent(passwordField).addComponent(lastChangeDateField).addComponent(addNewEntryButton));
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
				addComponent(lastChangeDateLabel).addComponent(lastChangeDateField));
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
//    		userDataTableModel.setRowCount(0);
//    		userDataTableModel.setRowCount(2);
    		
//    		int rowCount = userDataTableModel.getRowCount();
//    		for (int i = rowCount - 1; i >= 0; i--) {
//    			userDataTableModel.removeRow(i);    		
//    	    }
    		
            //userDashboard = null;	
    		
    		userDashboard.setVisible(false);
    		//CHANGE-removes the user dashboard window after logout
    		userDashboard.dispose();
    		passwordDialog.setVisible(true);
    		
    	});
    	
    	exitOption.addActionListener(actionEvent -> {
    		System.exit(0);
    	});
    }
	
    //Method for setting the names of input fields
    private void setNamesToInputFields(JTextField[] inputFields, String[] fieldNames) {
    	if (inputFields.length != fieldNames.length) {
    		return;
    	}
    	
    	int i = 0;
    	for (JTextField currentField : inputFields) {
    		 currentField.setName(fieldNames[i++]);
    	}	
    	
    }
    
	//Method for collecting the data entered by the user 
    private String collectNewEntryData(JTextField[] fieldsArray ) {
    	StringBuilder rowData = new StringBuilder();

    	int length = fieldsArray.length;
    	for (int i = 0; i < length; i++) {
    		if(i == length - 1) {
    			/*If the data field is left empty by the user, the corresponding data cell from the table will
    			 be filled with a string having the default value "null"  */
    			if ("".equals(fieldsArray[length - 1].getText())) {
    				rowData.append("null");
    				break;
    			} else {
    				rowData.append(fieldsArray[i].getText());
    				break;
    			}
    		} 

    		rowData.append(fieldsArray[i].getText() + ",");
    	}

    	return rowData.toString();
    }


	
	public void addActionListenersToFormButtons(JTextField[] fieldsArray) {

		//Adding action listener to the reset button of the entry form
		resetFormButton.addActionListener(actionEvent -> {
			for (JTextField currentField : fieldsArray) {
				currentField.setText("");
			}
		});
		
		//Adding action listener to the add new entry button of the entry form
		addNewEntryButton.addActionListener(actionEvent -> {
			
			for (JTextField currentField : fieldsArray) {
				if (currentField.getText().isEmpty() && !currentField.getName().equals("Date field")) {
					JOptionPane.showMessageDialog(userDashboard, "Please fill in the required fields!");
					return;
				}
			}
			
			String rowData = collectNewEntryData(fieldsArray);
			System.out.println(rowData);

			handler.addNewEntry(rowData.toString());
			UserTableOperations.userOption = JOptionPane.showConfirmDialog(null,"Do you want to insert a new row?","Data saving",JOptionPane.YES_NO_CANCEL_OPTION);
			 if( UserTableOperations.userOption == 0) {
			 handler.getUserDataTableModel().addRow(rowData.toString().split(","));
			}
		});
	}
	

	
	
 
	
	
	
	
	
	
}
