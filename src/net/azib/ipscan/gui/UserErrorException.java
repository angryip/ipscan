/**
 * 
 */
package net.azib.ipscan.gui;

/**
 * Exception for throwing in case of user errors.
 * These generally result in showing an error message.
 * 
 * @author anton
 */
public class UserErrorException extends RuntimeException {
	
	private static final long serialVersionUID = 123283472834982L;
	
	public UserErrorException(String label) {
		super(label);
	}
	
	public UserErrorException(String label, Throwable cause) {
		super(label);
		initCause(cause);
	}

}
