package com.razvan.gui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.*;

public class SenderAccountCredentialsDialog extends JDialog {
	
	private JLabel infoLabel;
	private JCheckBox dontShowAgainCheckbox;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel infoMessagePanel;
	private JPanel buttonPanel;
	
	
	public SenderAccountCredentialsDialog() {
		initComponents();
		addComponentsToSubPanels();
		addComponentsToMainPanel();
		this.pack();
		this.setVisible(true);
		
		
	}
	
	private void initComponents() {
		this.infoLabel = new JLabel("No data was found inside the sender account credentials file located at:  \n Would you like to set them up now?");
		this.dontShowAgainCheckbox = new JCheckBox("Don't show this message again");
		this.okButton = new JButton("Ok");
		this.cancelButton = new JButton("Cancel");
		this.infoMessagePanel = new JPanel(new BorderLayout());
		this.buttonPanel = new JPanel (new FlowLayout());
		
		this.setTitle("Sender account credentials information");
	}
	
	private void addComponentsToMainPanel() {
		this.getContentPane().setLayout(new BorderLayout());
//		this.add(infoLabel);
//		this.add(dontShowAgainCheckBox);
//		this.add(okButton);
//		this.add(cancelButton);
		this.add(infoMessagePanel, BorderLayout.NORTH);
		this.add(buttonPanel, BorderLayout.SOUTH);
	}
	
	private void addComponentsToSubPanels() {
		infoMessagePanel.add(infoLabel, BorderLayout.NORTH);
		infoMessagePanel.add(dontShowAgainCheckbox, BorderLayout.CENTER);
		
		buttonPanel.add(okButton, FlowLayout.LEFT);
		buttonPanel.add(cancelButton, FlowLayout.CENTER);
	}
}
