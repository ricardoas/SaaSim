package commons.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Configuration {

	protected Properties currentProperties = new Properties( System.getProperties() );

	public void loadPropertiesFromFile( String fileName ) throws FileNotFoundException, IOException {

		FileInputStream stream = new FileInputStream( new File( fileName ) );
		try{
			currentProperties.load( stream );
		}finally{
			stream.close();
		}
		
		if(!verifyPropertiesExist()){
			throw new IOException("Missing data in file!");
		}
	}

	public boolean verifyPropertiesExist(){
		return true;
	}
	
	/**
	 * Returns a value for a given key.
	 * @param key The property name.
	 * @return The property value.
	 */
	public String getProperty(String key){
		return currentProperties.getProperty(key);
	}
}
