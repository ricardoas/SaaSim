package commons.config;

import java.lang.reflect.Array;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationRuntimeException;
import org.apache.commons.configuration.PropertiesConfiguration;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public abstract class ComplexPropertiesConfiguration extends PropertiesConfiguration{
	
	public static final String ARRAY_SEPARATOR = "\\|";
	
	/**
	 * Private constructor.
	 * 
	 * @param propertiesFileName
	 * @throws ConfigurationException
	 */
	protected ComplexPropertiesConfiguration(String propertiesFileName) throws ConfigurationException {
		super(propertiesFileName);
	}
	
	public String getNonEmptyString(String key){
		try {
			String value = getString(key);
			Validator.checkNotEmpty(key, value);
			return value;
		} catch (ConfigurationException e) {
			throw new ConfigurationRuntimeException(e);
		}

	}

	public int[] getIntegerArray(String propertyName) {
		String[] stringArray = getStringArray(propertyName);
		int [] values = new int[stringArray.length];
		for (int i = 0; i < values.length; i++) {
			values[i] = Integer.valueOf(stringArray[i]);
		}
		return values;
	}

	public long[] getLongArray(String propertyName) {
		return parseLongArray(getStringArray(propertyName));
	}

	public long[][] getLong2DArray(String propertyName) {
		String[] stringArray = getStringArray(propertyName);
		long [][] values = new long[stringArray.length][];
		for (int i = 0; i < values.length; i++) {
			values[i] = parseLongArray(stringArray[i].split(ARRAY_SEPARATOR));
		}
		return values;
	}

	public double[] getDoubleArray(String propertyName) {
		return parseDoubleArray(getStringArray(propertyName));
	}

	public double[][] getDouble2DArray(String propertyName) {
		String[] stringArray = getStringArray(propertyName);
		double [][] values = new double[stringArray.length][];
		for (int i = 0; i < values.length; i++) {
			values[i] = parseDoubleArray(stringArray[i].split(ARRAY_SEPARATOR));
		}
		return values;
	}

	public <T extends Enum<T>> T[] getEnumArray(String propertyName, Class<T> enumClass) {
		return parseArrayEnum(getStringArray(propertyName), enumClass);
	}
	
	public String[][] getString2DArray(String propertyName) {
		String[] stringArray = getStringArray(propertyName);
		String[][] values = new String[stringArray.length][];
		for (int i = 0; i < values.length; i++) {
			values[i] = stringArray[i].split(ARRAY_SEPARATOR);
		}
		return values;
	}

	public String[][] getNonEmptyString2DArray(String propertyName) {
		try {
			String[][] string2dArray = getString2DArray(propertyName);
			Validator.checkIsNonEmptyStringArray(propertyName, string2dArray, ARRAY_SEPARATOR);
			return string2dArray;
		} catch (ConfigurationException e) {
			throw new ConfigurationRuntimeException(e);
		}
	}

	@SuppressWarnings("unchecked")
	public <T extends Enum<T>> T[][] getEnum2DArray(String propertyName, Class<T> enumClass) {
		String[] machines = getStringArray(propertyName);
		T[][] machineTypes = (T[][]) Array.newInstance(enumClass, machines.length, machines.length);
		for(int i = 0; i < machines.length; i++){
			machineTypes[i] = parseArrayEnum(machines[i].split(ARRAY_SEPARATOR), enumClass);
		}
		return machineTypes;
	}

	private static double[] parseDoubleArray(String[] stringValues) {
		double [] doubleValues = new double[stringValues.length];
		for (int j = 0; j < doubleValues.length; j++) {
			doubleValues[j] = Double.valueOf(stringValues[j]);
		}
		return doubleValues;
	}

	private static long[] parseLongArray(String[] stringValues) {
		long [] doubleValues = new long[stringValues.length];
		for (int j = 0; j < doubleValues.length; j++) {
			doubleValues[j] = Long.valueOf(stringValues[j]);
		}
		return doubleValues;
	}

	@SuppressWarnings("unchecked")
	private static <T extends Enum<T>> T[] parseArrayEnum(String[] stringValues, Class<T> enumClass) {
		T [] enumValues = (T[]) Array.newInstance(enumClass, stringValues.length);
		for (int j = 0; j < enumValues.length; j++) {
			enumValues[j] = parseEnum(stringValues[j].trim().toUpperCase(), enumClass);
		}
		return enumValues;
	}

	protected static <T extends Enum<T>> T parseEnum(String value, Class<T> enumClass) {
		return Enum.valueOf(enumClass, value.replace('.', '_'));
	}

	protected void checkSize(String propertyName, String sizePropertyName) throws ConfigurationException {
		String[] values = getStringArray(propertyName);
		int size = getInt(sizePropertyName);
		if (values.length != size){
			throw new ConfigurationException("Check number of values in " + 
					propertyName + ". It must be equals to what is specified at " + 
					sizePropertyName);
		}
	}
	
	
	public String[] getNonEmptyStringArray(String key) {
		try {
			String[] values = super.getStringArray(key);
			Validator.checkIsNonEmptyStringArray(key, values);
			return values;
		} catch (ConfigurationException e) {
			throw new ConfigurationRuntimeException(e);
		}
	}
}