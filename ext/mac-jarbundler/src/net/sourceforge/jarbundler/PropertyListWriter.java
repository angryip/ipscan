/*
 * Write the application bundle file: Info.plist
 *
 * Copyright (c) 2006, William A. Gilbert <gilbert@informagen.com> All rights reserved.
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation; either version 2 of the License, or (at your option)
 * any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See  the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package net.sourceforge.jarbundler;

// This package's imports
import net.sourceforge.jarbundler.AppBundleProperties;

// Java I/O
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

// Java Utility
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

// Java language imports
import java.lang.Boolean;
import java.lang.ClassCastException;
import java.lang.Double;
import java.lang.String;
import java.lang.System;

// Apache Ant
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileUtils;

// Java XML DOM creation
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

// W3C DOM
import org.w3c.dom.Document;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Attr;

/**
 * Write out a Java application bundle property list file. For descriptions of
 * the property list keys, see <a
 * href="http://developer.apple.com/documentation/MacOSX/Conceptual/BPRuntimeConfig/Articles/PListKeys.html"
 * >Apple docs</a>.
 */
public class PropertyListWriter {


	// Our application bundle properties
	private AppBundleProperties bundleProperties;

	private double version = 1.3;

	// DOM version of Info.plist file
	private Document document = null;


	private FileUtils fileUtils = FileUtils.getFileUtils();
	
	/**
	 * Create a new Property List writer.
	 */
	public PropertyListWriter(AppBundleProperties bundleProperties) {
		this.bundleProperties = bundleProperties;
		setJavaVersion(bundleProperties.getJVMVersion());
	}

	private void setJavaVersion(String version) {

		if (version == null)
			return;

		this.version = Double.valueOf(version.substring(0, 3)).doubleValue();
	}


	public void writeFile(File fileName) throws BuildException {

		Writer writer = null;

		try {

			this.document = createDOM();
			buildDOM();
			
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			transformer.setOutputProperty(OutputKeys.METHOD, "xml");
			transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(this.document), new StreamResult(fileName));
			
		} catch (Exception pce) {
			throw new BuildException(pce);
		} 
	}

	private Document createDOM() throws ParserConfigurationException {
	
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = factory.newDocumentBuilder();
		DOMImplementation domImpl = documentBuilder.getDOMImplementation();

		// We needed to reference using the full class name here because we already have 
		//  a class named "DocumentType"
		
		org.w3c.dom.DocumentType doctype = domImpl.createDocumentType(
		       "plist",
		       "-//Apple Computer//DTD PLIST 1.0//EN",
		       "http://www.apple.com/DTDs/PropertyList-1.0.dtd");

		return domImpl.createDocument(null, "plist", doctype);
	}


	private void buildDOM()  {

		Element plist = this.document.getDocumentElement();
		plist.setAttribute("version","1.0");		

		// Open the top level dictionary, <dict>
		
		Node dict = createNode("dict", plist);

		// Application short name i.e. About menu name
		writeKeyStringPair("CFBundleName", bundleProperties.getCFBundleName(), dict);

		// Finder 'Version' label, defaults to "1.0"
		writeKeyStringPair("CFBundleShortVersionString", bundleProperties.getCFBundleShortVersionString(), dict);
		
		// Finder 'Get Info'
		writeKeyStringPair("CFBundleGetInfoString", bundleProperties.getCFBundleGetInfoString(), dict);
		
		// Mac OS X required key, defaults to "false"
		writeKeyStringPair("CFBundleAllowMixedLocalizations", 
		     (bundleProperties.getCFBundleAllowMixedLocalizations() ? "true" : "false"),  
		     dict);

		// Mac OS X required, defaults to "6.0"
		writeKeyStringPair("CFBundleInfoDictionaryVersion", 
		     bundleProperties.getCFBundleInfoDictionaryVersion(), dict);

		// Bundle Executable name, required, defaults to "JavaApplicationStub"
		writeKeyStringPair("CFBundleExecutable", bundleProperties.getCFBundleExecutable(), dict);

		// Bundle Development Region, required, defaults to "English"
		writeKeyStringPair("CFBundleDevelopmentRegion", bundleProperties.getCFBundleDevelopmentRegion(), dict);

		// Bundle Package Type, required, defaults tp "APPL"
		writeKeyStringPair("CFBundlePackageType", bundleProperties.getCFBundlePackageType(), dict);

		// Bundle Signature, required, defaults tp "????"
		writeKeyStringPair("CFBundleSignature", bundleProperties.getCFBundleSignature(), dict);

		// Application build number, optional
		if (bundleProperties.getCFBundleVersion() != null) 
			writeKeyStringPair("CFBundleVersion", bundleProperties.getCFBundleVersion(), dict);
		
		// Application Icon file, optional
		if (bundleProperties.getCFBundleIconFile() != null) 
			writeKeyStringPair("CFBundleIconFile", bundleProperties.getCFBundleIconFile(), dict);

		// Bundle Identifier, optional
		if (bundleProperties.getCFBundleIdentifier() != null) 
			writeKeyStringPair("CFBundleIdentifier", bundleProperties.getCFBundleIdentifier(), dict);

		// Help Book Folder, optional
		if (bundleProperties.getCFBundleHelpBookFolder() != null) 
			writeKeyStringPair("CFBundleHelpBookFolder", bundleProperties.getCFBundleHelpBookFolder(), dict);

		// Help Book Name, optional
		if (bundleProperties.getCFBundleHelpBookName() != null) 
			writeKeyStringPair("CFBundleHelpBookName", bundleProperties.getCFBundleHelpBookName(), dict);

		// Document Types, optional
		List documentTypes = bundleProperties.getDocumentTypes();

		if (documentTypes.size() > 0) 
 			writeDocumentTypes(documentTypes, dict);

		// Java entry in the plist dictionary
		writeKey("Java", dict);
		Node javaDict = createNode("dict", dict);

		// Main class, required
		writeKeyStringPair("MainClass", bundleProperties.getMainClass(), javaDict);

		// Target JVM version, optional but recommended
		if (bundleProperties.getJVMVersion() != null) 
			writeKeyStringPair("JVMVersion", bundleProperties.getJVMVersion(), javaDict);


		// Classpath is composed of two types, required
		// 1: Jars bundled into the JAVA_ROOT of the application
		// 2: External directories or files with an absolute path

		List classPath = bundleProperties.getClassPath();
		List extraClassPath = bundleProperties.getExtraClassPath();

		if ((classPath.size() > 0) || (extraClassPath.size() > 0)) 
			writeClasspath(classPath, extraClassPath, javaDict);
		

		// JVM options, optional
		if (bundleProperties.getVMOptions() != null) 
			writeKeyStringPair("VMOptions", bundleProperties.getVMOptions(), javaDict);

		// Working directory, optional
		if (bundleProperties.getWorkingDirectory() != null) 
			writeKeyStringPair("WorkingDirectory", bundleProperties.getWorkingDirectory(), javaDict);

		// Main class arguments, optional
		if (bundleProperties.getArguments() != null) 
			writeKeyStringPair("Arguments", bundleProperties.getArguments(), javaDict);

		// Java properties, optional
		Hashtable javaProperties = bundleProperties.getJavaProperties();

		if (javaProperties.isEmpty() == false) 
 			writeJavaProperties(javaProperties, javaDict);


		// Services, optional
		List services = bundleProperties.getServices();
		if (services.size() > 0) 
 			writeServices(services,dict);
		
	}


	private void writeDocumentTypes(List documentTypes, Node appendTo) {

		writeKey("CFBundleDocumentTypes", appendTo);
		
		Node array = createNode("array", appendTo);

		Iterator itor = documentTypes.iterator();

		while (itor.hasNext()) {

			DocumentType documentType = (DocumentType) itor.next();

			Node documentDict = createNode("dict", array);

			writeKeyStringPair("CFBundleTypeName", documentType.getName(), documentDict);
			writeKeyStringPair("CFBundleTypeRole", documentType.getRole(), documentDict);

			File iconFile = documentType.getIconFile();

			if (iconFile != null)
				writeKeyStringPair("CFBundleTypeIconFile", iconFile.getName(), documentDict);


			List extensions = documentType.getExtensions();

			if (extensions.isEmpty() == false) {
				writeKey("CFBundleTypeExtensions", documentDict);
				writeArray(extensions, documentDict);
			}

			List osTypes = documentType.getOSTypes();

			if (osTypes.isEmpty() == false) {
				writeKey("CFBundleTypeOSTypes", documentDict);
				writeArray(osTypes, documentDict);
			}

			
			List mimeTypes = documentType.getMimeTypes();

			if (mimeTypes.isEmpty() == false) {
				writeKey("CFBundleTypeMIMETypes", documentDict);
				writeArray(mimeTypes, documentDict);
			}

			List UTIs = documentType.getUTIs();
			
			if (UTIs.isEmpty() == false) {
				writeKey("LSItemContentTypes", documentDict);
				writeArray(UTIs, documentDict);
			}
			
			// Only write this key if true
			if (documentType.isBundle()) 
				writeKeyStringPair("LSTypeIsPackage", "true", documentDict);
		}
	}
	
	private void writeServices(List services, Node appendTo) {
	
		writeKey("NSServices",appendTo);
		Node array = createNode("array",appendTo);
		Iterator itor = services.iterator();
		
		while (itor.hasNext()) {
			Service service = (Service)itor.next();
			Node serviceDict = createNode("dict",array);

			String portName = service.getPortName();
            if (portName == null)
            	portName = bundleProperties.getCFBundleName();
			
			writeKeyStringPair("NSPortName", portName, serviceDict);
			writeKeyStringPair("NSMessage",service.getMessage(),serviceDict);
			
			List sendTypes = service.getSendTypes();
			if (!sendTypes.isEmpty()) {
				writeKey("NSSendTypes",serviceDict);
				writeArray(sendTypes,serviceDict);
			}
			
			List returnTypes = service.getReturnTypes();
			if (!returnTypes.isEmpty()) {
				writeKey("NSReturnTypes",serviceDict);
				writeArray(returnTypes,serviceDict);
			}
			
			writeKey("NSMenuItem",serviceDict);
			Node menuItemDict = createNode("dict",serviceDict);
			writeKeyStringPair("default",service.getMenuItem(),menuItemDict);
			
			String keyEquivalent = service.getKeyEquivalent();
			if (null != keyEquivalent) {
				writeKey("NSKeyEquivalent",serviceDict);
				Node keyEquivalentDict = createNode("dict",serviceDict);
				writeKeyStringPair("default",keyEquivalent,keyEquivalentDict);
			}
			
			String userData = service.getUserData();
			if (null != userData)
				writeKeyStringPair("NSUserData", userData, serviceDict);
			
			String timeout = service.getTimeout();
			if (null != timeout)
				writeKeyStringPair("NSTimeout",timeout,serviceDict);
 		}
	}

	private void writeClasspath(List classpath, List extraClasspath, Node appendTo) {
		writeKey("ClassPath", appendTo);
		classpath.addAll(extraClasspath);
		writeArray(classpath, appendTo);
	}


	private void writeJavaProperties(Hashtable javaProperties, Node appendTo) {
	
		writeKey("Properties", appendTo);
		
		Node propertiesDict = createNode("dict", appendTo);

		for (Iterator i = javaProperties.keySet().iterator(); i.hasNext();) {
			String key = (String) i.next();

			if (key.startsWith("com.apple.") && (version >= 1.4)) {
				System.out.println("Deprecated as of 1.4: " + key);
				continue;
			}

			writeKeyStringPair(key, (String)javaProperties.get(key), propertiesDict);
		}
	}

	private Node createNode(String tag, Node appendTo) {
		Node node = this.document.createElement(tag);
		appendTo.appendChild(node);
		return node;
	}


	private void writeKeyStringPair(String key, String string, Node appendTo) {
	
		if (string == null)
			return;
	
		writeKey(key, appendTo);
		writeString(string, appendTo);
	}


	private void writeKey(String key, Node appendTo) {
		Element keyNode = this.document.createElement("key");
		appendTo.appendChild(keyNode);
		keyNode.appendChild(this.document.createTextNode(key));
	}


	private void writeString(String string, Node appendTo) {
		Element stringNode = this.document.createElement("string");
		stringNode.appendChild(this.document.createTextNode(string));
		appendTo.appendChild(stringNode);
	}

	private void writeArray(List stringList, Node appendTo) {
	
		Node arrayNode = createNode("array", appendTo);	

		for (Iterator it = stringList.iterator(); it.hasNext();) 
			writeString((String)it.next(), arrayNode);
		
	}

}
