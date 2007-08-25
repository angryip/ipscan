package net.sourceforge.jarbundler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



/**
 * Represents an Info.plist Service specifying a service provided by the application.
 * 
 * Port Name - The name of the port the application monitors for incoming service requests.
 * 
 
 * Message - The name of the instance method to invoke for the service. 
 * In Objective-C, the instance method must be of the form messageName:userData:error:.
 * In Java, the instance method must be of the form messageName(NSPasteBoard,String).
 * 
 * 
 * Menu Item - The text to add to the Services menu. The value must be unique.
 * You can use a slash character "/" to specify a submenu. For example, Mail/Send
 * would appear in the Services Menu as a menu named Mail with an item named Send.
 * 
 * 
 * Send Types - A list of the data type names that can be read by the service.
 *   The NSPasteboard class description lists several common data types.
 *
 *
 * Return Types - A list of the data type names that can be returned by the service.
 * The NSPasteboard class description lists several common data types.
 * You must specify either Return Types, Send Types or both.
 * 
 * You must specify either Send Types, Return Types or both.
 *  
 * 
 * Key Equivalent - This attribute is optional. The keyboard equivalent used to invoke
 * the service menu command. The value has to be a single character. Users invoke this
 * keyboard equivalent by pressing the Command and Shift key modifiers along with the character.
 * 
 * 
 * User Data - This attribute is optional. The value is free choosable and is passed
 * to the method as second parameter.
 * 
 * 
 * Timeout - This attribute is optional. It indicates the number of milliseconds
 * Services should wait for a response from the application providing
 * a service when a respond is required.
 * 
 * 
 * <service portname="jarBundler"
 *          message="processRequest"
 *          menuitem="JarBundler/Process Request"
 *          sendtypes="NSStringPboardType,NSFilenamesPboardType"
 *          returntypes="NSStringPboardType"
 *          keyequivalent="p"
 *          userdata="a string passed to the method"
 *          timeout="5000" />
 */
public class Service {
	private static final List EMPTYLIST = new ArrayList(0);

	
	/** The name of the port the application monitors for incoming service requests. */
	private String portName = null;
	

	/** 

	 * The name of the instance method to invoke for the service. 
	 * In Objective-C, the instance method must be of the form messageName:userData:error:.

	 * In Java, the instance method must be of the form messageName(NSPasteBoard,String).
	 */
	private String message = null;
	

	/** 

	 * The text to add to the Services menu. The value must be unique.

	 * You can use a slash character "/" to specify a submenu. For example, Mail/Send

	 * would appear in the Services Menu as a menu named Mail with an item named Send.
	 */
	private String menuItem = null;
	
	/**
	 * A list of the data type names that can be read by the service.

	 * The NSPasteboard class description lists several common data types.

	 * You must specify either Send Types, Return Types or both.
	 */
	private String[] sendTypes = null;
	

	/**
	 * A list of the data type names that can be returned by the service.

	 * The NSPasteboard class description lists several common data types.

	 * You must specify either Return Types, Send Types or both.
	 */
	private String[] returnTypes = null;
	

	/**
	 * This attribute is optional. The keyboard equivalent used to invoke

	 * the service menu command. The value has to be a single character. Users invoke this

	 * keyboard equivalent by pressing the Command and Shift key modifiers along with the character.

	 */
	private String keyEquivalent = null;
	

	/** 

	 * This attribute is optional. The value is free choosable and is passed

	 * to the method as second parameter.

	 */
	private String userData = null;
	

	/** 

	 * This attribute is optional. It indicates the number of milliseconds

	 * Services should wait for a response from the application providing

	 * a service when a respond is required.

	 */
	private String timeout = null;
	

	public void setPortName(String portName) {
		this.portName = portName;
	}
	
	public String getPortName() {
		return portName;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMenuItem(String menuItem) {
		this.menuItem = menuItem;

	}
	
	public String getMenuItem() {
		return menuItem;
	}
	
	public void setSendTypes(String sendTypes) {
		this.sendTypes = sendTypes.split("[\\s,]");
	}
	
	public List getSendTypes() {
		return (sendTypes == null) ? EMPTYLIST : Arrays.asList(sendTypes);
	}
	
	public void setReturnTypes(String returnTypes) {
		this.returnTypes = returnTypes.split("[\\s,]");
	}
	
	public List getReturnTypes() {
		return (returnTypes == null) ? EMPTYLIST : Arrays.asList(returnTypes);
	}
	
	public void setKeyEquivalent(String keyEquivalent) {
		this.keyEquivalent = keyEquivalent;
	}
	
	public String getKeyEquivalent() {
		return keyEquivalent;
	}
	
	public void setUserData(String userData) {
		this.userData = userData;
	}
	
	public String getUserData() {
		return userData;
	}
	
	public void setTimeout(String timeout) {
		this.timeout = timeout;
	}
	
	public String getTimeout() {
		return timeout;
	}
}
