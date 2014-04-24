/**
 * 
 */
package net.azib.ipscan.config;

import java.util.logging.Logger;

import junit.framework.TestCase;

/**
 * @author Anton Keks
 */
public class LoggerFactoryTest extends TestCase {
	
	private static Logger staticLogger = LoggerFactory.getLogger();                     
	                                                                                 
    public void testAutomaticNameInitialization() {                              
      assertEquals(getClass().getName(), staticLogger.getName());                     
      assertEquals(getClass().getName(), LoggerFactory.getLogger().getName());               
    }

}
