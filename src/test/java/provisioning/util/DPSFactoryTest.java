/**
 * 
 */
package provisioning.util;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;

import util.ValidConfigurationTest;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class DPSFactoryTest extends ValidConfigurationTest{
	
	/**
	 * Test method for {@link provisioning.util.DPSFactory#createDPS(java.lang.Object[])}.
	 * @throws ConfigurationException 
	 */
	@Test
	public void testCreateDPS() throws ConfigurationException {
		buildFullConfiguration();
		DPSFactory.createDPS();
	}

}
