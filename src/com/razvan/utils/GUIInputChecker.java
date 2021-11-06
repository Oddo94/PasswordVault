package com.razvan.utils;

import java.util.ArrayList;

import javax.swing.text.JTextComponent;

public class GUIInputChecker {
	
	public static boolean hasDataOnRequiredFields (ArrayList<JTextComponent> fieldList) {
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


}
