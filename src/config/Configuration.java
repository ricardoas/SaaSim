package config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Configuration {
	
	protected Properties currentProperties = new Properties( System.getProperties() );
	
	public void loadPropertiesFromFile( String file ) throws FileNotFoundException, IOException {

		/** Get an abstraction for the properties file */
		File propertiesFile = new File( file );

		/* load the properties file, if it exists */
		currentProperties.load( new FileInputStream( propertiesFile ) );
		if(!verifyPropertiesExist()){
			throw new IOException("Missing data in file!");
		}
	}
	
	public boolean verifyPropertiesExist(){
		return true;
	}
	
	public String getProperty(String property){
		return currentProperties.getProperty(property);
	}
}
