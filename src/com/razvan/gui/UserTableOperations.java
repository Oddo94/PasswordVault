package com.razvan.gui;
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import com.razvan.installation_manager.ApplicationInstallManager;
import com.razvan.io_manager.IOFileManager;
import com.razvan.user_data_security.UserDataSecurityManager;

import java.util.*;



public class UserTableOperations extends MouseAdapter {
	private UserDashboard userDashboard;
	private JTable userDataTable;
	static int userOption;
	private boolean hasCanceledChange;
	private boolean hasCopiedCellContent;
	private String oldCellValue;
	private String appDataPath = ApplicationInstallManager.APP_MAIN_FOLDER_PATH;
	private String userFileName;
	private IOFileManager fileManager = new IOFileManager();
	private UserDataSecurityManager securityManager = new UserDataSecurityManager();

	public UserTableOperations(UserDashboard userDashboard, String userFileName) {
		this.userDashboard = userDashboard;
		this.userFileName = userFileName;
		userOption = -1;
	}

	public UserTableOperations() {

	}
	
	//The method that fills the table using the user data file if this is not empty
	public JTable fillTable(JTable table) {
		DefaultTableModel dtm =(DefaultTableModel) table.getModel();

		//File containing the encrypted userData(it will become a parameter of the fillTable method)-THE FILE CONTAINING USER DATA WILL HAVE TO BE NAMED  AFTER THE USER
		File fileToRead = new File(appDataPath + "/userData/" + userFileName);
		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;
		//File containing the initialization vector used for decryption(it should probably be used directly by the restoreInitializationVector method)
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);

		//Encrypted data read from file
		byte[] encryptedData = fileManager.readEncryptedData(fileToRead);


		//Decrypted data returned as a string by the performAESDecryption method
		String decryptedData = securityManager.performAESDecryption(encryptedData, securityManager.retrieveSecretKey(userFileName), securityManager.restoreInitializationVector(fileManager.readDataForDecryption(ivSource)));

		//The decrypted data is split using \n as separator(should probably move the tableData array inside the if clause for a more readable code)
		String[] tableData = decryptedData.split("\n");

		//Checks to see if the decrypted String contains any values
		if(!"".equals(decryptedData)) {
			/* If it contains at least one value then each entry will be split using "," as
			  separator and the resulting array will be used to create a new table row */
			for (String entry : tableData) {
				dtm.addRow(entry.split(","));
			}
		}

		return table;
	}

	
	/*
	 * The method that creates the table that displays the user data. 
	 * It receives the number of rows and columns as parameters.
	 * This data also used for creating the DefaultTableModel.
	 */
	public JTable createTable(int numRows, int numColumns) {
		String[] columnNames = {"Account name", "User name", "Password","Last password change date"};

		userDataTable = new JTable(numRows,numColumns);

		DefaultTableModel tableModel = new DefaultTableModel(numRows,numColumns);
		tableModel.setColumnIdentifiers(columnNames);
		userDataTable.setModel(tableModel);
		//Enables the sorting option on the columns of the table
		userDataTable.setAutoCreateRowSorter(true);

		for(int i = 0; i < userDataTable.getColumnCount(); i++) {

			TableColumn currentColumn = userDataTable.getTableHeader().getColumnModel().getColumn(i);
			currentColumn.setHeaderValue(columnNames[i]);
		}

		JPanel tablePanel = new JPanel();
		JScrollPane tableScrollPane = new JScrollPane(userDataTable);
		tablePanel.add(tableScrollPane);


		userDashboard.add(tablePanel, new BorderLayout().CENTER);

		return userDataTable;
	}
	
	//The method that adds the table to the user dashboard
	public void addTableToWindow(JTable table) {
		JPanel tablePanel = new JPanel();
		JScrollPane tableScrollPane = new JScrollPane(table);
		tablePanel.add(tableScrollPane);

		userDashboard.add(tablePanel);
	}


	//The method that retrieves the table data after the user has edited it and then updates the user data file
	public void editEntry() {
		//Retrieving table data
		String tableData = getTableData();

		//Create a new IV(it changes at each encryption)
		byte[] initializationVector = securityManager.createInitializationVector();
		
		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;
		
		//File containing the initialization vector used for decryption(it should probably be used directly by the restoreInitializationVector method)
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);

		//The file used to write the encryption data(the name should be taken from the value passed to fillTable method)
		File userDataFile = new File(appDataPath + "/userData/Razvan");
		
		//Secret key retrieval and encryption of the table data	
		SecretKey secretKey = securityManager.retrieveSecretKey(userFileName);	
		byte[] encryptedData = securityManager.performAESEncryption(tableData, secretKey, initializationVector);
		
		//Null check to make sure the encryption process was successful
		if(encryptedData != null) {
			//First the data is written to the user data file
			fileManager.writeEncryptedData(userDataFile, encryptedData);
			
			//Then the initializaion vector is stored in the file
			securityManager.storeInitializationVector(ivSource, initializationVector);
		}
			
	}

	//The method that retrieves the table data after the user has inserted a new row and then updates the user data file
	public void addNewEntry(String tableData) {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();

		//String tableData = getTableData();
		String userDataFolder = "/userData/";
		
		File userDataFile = new File(appDataPath + userDataFolder + userFileName);
		//File ivSource = new File(appDataPath + "/security/iv_storage");
		
		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;
		
		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);

		
		SecretKey secretKey = securityManager.retrieveSecretKey(userFileName);
		byte[] initializationVector = securityManager.createInitializationVector();
		
		//Table data encryption
		byte[] encryptedData = securityManager.performAESEncryption(tableData, secretKey, initializationVector);
		
		if (encryptedData != null) {
		fileManager.writeEncryptedData(userDataFile, encryptedData);
		securityManager.storeInitializationVector(ivSource, initializationVector);
		}
		
		
//		String fileName = "Razvan";
//		fileManager.writeData(fileName, data);
		
	}

	/*
	 * The method that is used to delete a table entry. It receives a String
	 * containing the table data(except the deleted row) as parameter.The string
	 * data is overwritten in the user data file.
	 */
	public void removeEntry(String tableData) {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();
	
		//String tableData = getTableData();
		String userDataFolder = "/userData/";
		
		File userDataFile = new File(appDataPath + userDataFolder + userFileName);
		//File ivSource = new File(appDataPath + "/security/iv_storage");
		
		//Creating the string that represents the name of the user Iv storage file(by concatenating the user name and the suffix "-Iv")
		String suffix = "-Iv";
		String userIvFileName = userFileName + suffix;
		
		//File containing the initialization vector used for decryption(it should probably be used directly by the restoreInitializationVector method)
		File ivSource =  new File(appDataPath + "/security/iv/" + userIvFileName);

		
		SecretKey secretKey = securityManager.retrieveSecretKey(userFileName);
		byte[] initializationVector = securityManager.createInitializationVector();
		
		
		byte[] encryptedData = securityManager.performAESEncryption(tableData, secretKey, initializationVector);
		
		if (encryptedData != null) {
		fileManager.writeEncryptedData(userDataFile, encryptedData);
		securityManager.storeInitializationVector(ivSource, initializationVector);
		}
		
	}

	/*
	 * The method used for adding a MouseListener to the table so that when the user
	 * right clicks the mouse a menu containing the options "Copy" and "Delete" will
	 * appear
	 */
	public void addMouseListenerToTable() {
		JPopupMenu popupMenu = new JPopupMenu();
		
		JMenuItem deleteRowOption = new JMenuItem("Delete");
		JMenuItem copyCellDataOption = new JMenuItem("Copy");
		
		popupMenu.add(copyCellDataOption);
		popupMenu.add(deleteRowOption);

		userDataTable.addMouseListener(new MouseAdapter(){

			public void mouseReleased(MouseEvent e) {
				//Checking if a table row was selected and a right click was performed on it
				if (userDataTable.getSelectedRow() >= 0 && e.getButton() == MouseEvent.BUTTON3) {
					if(e.isPopupTrigger()) {
						popupMenu.show(e.getComponent(), e.getX(), e.getY());
					}
					/*
					 * Checking if a left click was performed on a table cell.In this case the value
					 * contained in the cell is saved in a variable so that it can later be restored
					 * if the user decides to cancel the changes performed to the cell value.
					 */
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					JTable table = (JTable) e.getSource();
					oldCellValue = table.getValueAt(table.getSelectedRow(), table.getSelectedColumn()).toString();
				}
			}
		});

		addMouseListenerToDeleteOption(deleteRowOption, userDataTable.getSelectedRow());
		addMouseListenerToCopyOption(copyCellDataOption, userDataTable.getSelectedRow());
	}

	//Method for adding a TableModelListener which is responsible for managing the changes performed to the table data
	public void addTableModelListener() {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();
		TableModelListener tableListener = new TableModelListener() {

			public void tableChanged(TableModelEvent e) {
				
				if (e.getType() == TableModelEvent.UPDATE && !hasCopiedCellContent) {

					if (hasCanceledChange) {
						hasCanceledChange = false;
						return;
					}

					DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();


					userOption = JOptionPane.showConfirmDialog(null,"Do you want to save the changes?","Data saving",JOptionPane.YES_NO_OPTION);

					if (userOption == 0) {
						editEntry();
					} else {
						hasCanceledChange = true;
						System.out.println("NO option selected");
						dtm.setValueAt(oldCellValue, e.getFirstRow(), e.getColumn());
						System.out.println("Changed row :" +  e.getFirstRow() +"\n" + "Changed column:" + e.getColumn());;
					}

					System.out.println("Table was updated!");

				} else if (e.getType() == TableModelEvent.INSERT) {

					if (userOption == 0) {
						//System.out.println("User option for insertion " + userOption);
						addNewEntry(getTableData());	
					}

				} else if (e.getType() == TableModelEvent.DELETE) {
					removeEntry(getTableData());
				}	
			}
		};	

		dtm.addTableModelListener(tableListener);
	}

	//Adding MouseListener for the delete option displayed when right clicking on a table cell
	public void addMouseListenerToDeleteOption(JMenuItem popupItem, int entryNumber) {

		popupItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {				
				//removeEntry(getSelectedRowData());
				DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();

				userOption = JOptionPane.showConfirmDialog(null, "Are you sure that you want to delete the selected row?", "Delete confirmation", JOptionPane.YES_NO_OPTION);

				if (userOption == 0) {
					dtm.removeRow(userDataTable.getSelectedRow());
				}

			}
		});		
	}
	
	//Adding MouseListener for the copy option displayed when right clicking on a table cell
	public void addMouseListenerToCopyOption(JMenuItem popupItem, int entryNumber) {
		popupItem.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent e) {
				DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();
				
				String dataToCopy = (String) dtm.getValueAt(userDataTable.getSelectedRow(), userDataTable.getSelectedColumn());
			    //System.out.println(dataToCopy);
				
				//Code for copying selected cell content to clipboard
				StringSelection stringSelection = new StringSelection(dataToCopy);
				Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
				clipboard.setContents(stringSelection, null);
				hasCopiedCellContent = true;
			}
		});
	}

	/*
	 * Method used for retrieving table data. The string stores the data in the following format: 
	 * -the cell data contained in a row is separated by a ","
	 * -each row is separated by a newline ("\n") character
	 */
	public String getTableData() {
		DefaultTableModel dtm = (DefaultTableModel) userDataTable.getModel();
		StringBuilder sb = new StringBuilder();
		int numRows =  dtm.getRowCount();
		int numCols = dtm.getColumnCount();


		for (int i = 0; i < numRows; i++) {
			for (int j = 0; j < numCols; j++) {
				if (j == numCols - 1) {
					sb.append(dtm.getValueAt(i, j));
					sb.append("\n");
					break;
				}
				sb.append(dtm.getValueAt(i, j) + ",");
			}
		}

		return sb.toString();
	}

	//The method that returns the values from the selected row
	public String getSelectedRowData() {
		StringBuilder sb = new StringBuilder();
		int row = userDataTable.getSelectedRow();
		int columnCount = userDataTable.getColumnCount();
		
		for (int i = 0; i < columnCount; i++) {
			if (i == columnCount - 1) {
				sb.append(userDataTable.getValueAt(row, i));
				break;
			}
			sb.append(userDataTable.getValueAt(row, i) + ",");
		}


		return sb.toString();
	}

	public DefaultTableModel getUserDataTableModel() {
		return (DefaultTableModel) userDataTable.getModel();
	}

	public JTable getUserDataTable() {
		return this.userDataTable;
	}
}

