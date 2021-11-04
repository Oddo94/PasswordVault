package com.razvan.utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class XMLFileReader {
private File file;
	
	public XMLFileReader(File file) {
		this.file = file;
	}
	
	public Map<String,String> readXMLFile() throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(file);
		
		NodeList nodeList = document.getElementsByTagName("senderAccountCredentials");
		
		Element accountCredentials = (Element) nodeList.item(0);
				
		Map<String, String> credentialsMap = new LinkedHashMap<>();
		
		credentialsMap.put("accountAddress", accountCredentials.getElementsByTagName("accountAddress").item(0).getTextContent());
		credentialsMap.put("accountPassword", accountCredentials.getElementsByTagName("accountPassword").item(0).getTextContent());
		
		return credentialsMap;
	}
	

}
