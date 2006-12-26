/**
 * 
 */
package net.azib.ipscan.exporters;

/**
 * Exception for throwing in case of problems in Exporters.
 * 
 * @author anton
 */
public class ExporterException extends IllegalArgumentException {
	
	static final long serialVersionUID = 746237846273847L;

	public ExporterException(String message) {
		super(message);
	}
	
	public ExporterException(String message, Throwable cause) {
		super(message);
		initCause(cause);
	}
}
