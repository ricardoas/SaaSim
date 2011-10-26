package util;

import org.junit.Ignore;


/**
 * Super class of tests which need a mocked configuration. Such tests usually uses
 * PowerMock as mock engine. As this API has been proven to be quite slow this class
 * of tests should disappear in a near future and being replaced by {@link ValidConfigurationTest}. 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@Deprecated
@Ignore
public class MockedConfigurationTest {
	/* Mark up interface */
}
