package com.razvan.utils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.razvan.gui.ErrorDisplayManager;
import com.razvan.gui.ErrorDisplayManager.messageType;

public class XMLFileReader {
	private File xmlFile;

	public XMLFileReader(File xmlFile) {
		if (!isXmlFile(xmlFile)) {
			throw new IllegalArgumentException("The provided file must be of XML type. Please make sure that it has the right extension before trying to process it through the XmlFileReader.");
		}

		this.xmlFile = xmlFile;
	}

	//Method for reading the XML file containing the sender account credentials
	public Map<String,String> readXMLFile() throws Exception{
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
		DocumentBuilder db = dbf.newDocumentBuilder();
		Document document = db.parse(xmlFile);

		NodeList nodeList = document.getElementsByTagName("senderAccountCredentials");

		Element accountCredentials = (Element) nodeList.item(0);

		Map<String, String> credentialsMap = new LinkedHashMap<>();

		credentialsMap.put("accountAddress", accountCredentials.getElementsByTagName("accountAddress").item(0).getTextContent());
		credentialsMap.put("accountPassword", accountCredentials.getElementsByTagName("accountPassword").item(0).getTextContent());

		return credentialsMap;
	}

	//Method for retrieving the sender account credentials as an object
	public SenderAccountCredentials readFileAsObject() throws Exception {

		SenderAccountCredentials accountCredentials = null;

		JAXBContext jaxbContext = JAXBContext.newInstance(SenderAccountCredentials.class);

		Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

		accountCredentials = (SenderAccountCredentials) jaxbUnmarshaller.unmarshal(xmlFile);


		return accountCredentials;

	}

	//Method for creating the empty file that will contain the sender account credentials
	public void createFile() throws Exception {
		SenderAccountCredentials accountCredentials = new SenderAccountCredentials();
		accountCredentials.setAccountAddress("Add email address here");
		accountCredentials.setAccountPassword("Add email password here");
	
		JAXBContext jaxbContext = JAXBContext.newInstance(SenderAccountCredentials.class);		

		Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

		jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true); 

		jaxbMarshaller.marshal(accountCredentials,xmlFile);
		jaxbMarshaller.marshal(accountCredentials, System.out);

		System.out.println("XML file successfuly created!");

	}

	//Validation method used to check if the provided file is of xml type
	private boolean isXmlFile(File file) {
		if (file == null) {
			return false;
		}

		String fileName = file.getName();
		int lastIndexOfDot = fileName.lastIndexOf(".");

		//No "." character found so the method returns false
		if (lastIndexOfDot == -1) {
			return false;
		}

		//Removes the "." character from the file extension string
		String processedExtension = fileName.substring(lastIndexOfDot).replace(".", "");

		return "xml".equalsIgnoreCase(processedExtension);
	}
}
