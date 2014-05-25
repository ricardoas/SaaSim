package saasim.core.config;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConversionException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * {@link PropertiesConfiguration} subclass adding ability to read primitive types from property file.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Configuration extends PropertiesConfiguration{

	public static final String TIER_ID = "default.tier.id";
	public static final String INSTANCE_DESCRIPTOR = "default.tier.descriptor";
	public static final String MACHINE = "default.tier.machine";
	public static final String FORCE = "default.tier.force";
	public static final String ACTION =  "default.action";
	
	public static final String ACTION_INCREASE =  "default.action.increase";
	public static final String ACTION_DECREASE =  "default.action.decrease";
	public static final String ACTION_RECONFIGURE =  "default.action.reconfigure";
	public static final String ACTION_ADMISSION_CONTROL = "default.action.admissioncontrol";

	/**
	 * Default constructor
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(String propertiesFilepath) throws ConfigurationException {
		super(propertiesFilepath);
	}

	/**
	 * Default constructor
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(){
		super();
	}
	
	/**
	 * Default constructor
	 * @param file 
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(File file) throws ConfigurationException{
		super(file);
	}
	
	/**
	 * Default constructor
	 * 
	 * @param propertiesFilepath
	 * @throws ConfigurationException
	 */
	public Configuration(URL url) throws ConfigurationException{
		super(url);
	}
	
	/**
	 * Get an array of <code>int</code> associated with the given configuration key.
     * If the key doesn't map to an existing object, an empty array is returned.
     * 
     * @param key The configuration key.
     * @return The associated <code>int</code> array if key is found.
     *
     * @throws ConversionException is thrown if the key maps to an
     *         object that is not a <code>int</code>/list of <code>int</code>.
     *         
     * @see #getStringArray(String)
     * @see #setListDelimiter(char)
     * @see #setDelimiterParsingDisabled(boolean)
	 */
	public int[] getIntegerArray(String key) {
		String[] stringArray = getStringArray(key);
		int [] values = new int[stringArray.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.valueOf(stringArray[i]);
		}
		return values;
	}

	public long[] getLongArray(String propertyName) {
		String[] stringValues = getStringArray(propertyName);
		long [] values = new long[stringValues.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Long.valueOf(stringValues[i]);
		}
		return values;
	}

	public double[] getDoubleArray(String propertyName) {
		String[] stringValues = getStringArray(propertyName);
		double [] values = new double[stringValues.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Double.valueOf(stringValues[i]);
		}
		return values;
	}

	public boolean[] getBooleanArray(String propertyName) {
		String[] stringValues = getStringArray(propertyName);
		boolean [] values = new boolean[stringValues.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Boolean.valueOf(stringValues[i]);
		}
		return values;
	}
}
