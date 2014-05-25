package saasim.core.config;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 */
@RunWith(Parameterized.class)
public class ConfigurationTest {
	
	private static final String SRC_TEST_RESOURCES = "src/test/resources/";
	private String file;


	@Parameters
    public static Collection<Object[]> data() {
    	File[] listOfFiles = new File(SRC_TEST_RESOURCES).listFiles();
    	Object[][] filesnames = new String[listOfFiles.length][1]; 
    	for (int i = 0; i < listOfFiles.length; i++) {
    		try {
				filesnames[i][0] = listOfFiles[i].getCanonicalPath();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	
        return Arrays.asList(filesnames);
    }
    
    public ConfigurationTest(String file) {
		this.file = file;
	}
	
	/**
	 * Test method for {@link saasim.core.config.Configuration#Configuration()}.
	 * @throws ConfigurationException 
	 * @throws IOException 
	 */
	@Test
	public void testConstructor() throws ConfigurationException {
		new Configuration(file);
	}

}
