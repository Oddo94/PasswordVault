package com.razvan.utils;

import java.util.ArrayList;
import java.util.Objects;

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

	public static void resetFields(ArrayList<JTextComponent> fieldList) {
		Objects.requireNonNull(fieldList, "The fieldList argument cannot be null.");
		
		//There's no point in continuing if there are no elements present in the Arraylist
		if (fieldList.size() == 0) {
			return;
		}
		
		for (JTextComponent field : fieldList) {
			field.setText("");
		}
	}

}
