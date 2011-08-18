package commons.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * Validation rules.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Validator {

	public static void checkNonNegative(String propertyName, long value) throws ConfigurationException {
		if (value < 0) {
			throw new ConfigurationException(propertyName + " must be a non negative integer.");
		}
	}

	public static void checkNotEmpty(String propertyName, String value) throws ConfigurationException {
		if (value == null || value.isEmpty()) {
			throw new ConfigurationException(propertyName + " can't be empty.");
		}
	}

	public static void checkPositive(String propertyName, long value) throws ConfigurationException {
		if (value <= 0) {
			throw new ConfigurationException(propertyName + " must be a positive integer.");
		}
	}

	public static void checkIsPositiveArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkPositive(propertyName, Long.valueOf(value));
			}catch (ConfigurationException e) {
				throw new ConfigurationException(propertyName + " must be an array of positive integers. " + value + " value is invalid.", e);
			}
		}
	}

	public static void checkIsNonEmptyStringArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkNotEmpty(propertyName, value);
			}catch (ConfigurationException e) {
				throw new ConfigurationException(propertyName + " must be an array of non empty values. " + value + " value is invalid.", e);
			}
		}
	}

	public static void checkIsNonNegativeDoubleArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkNonNegative(propertyName, Double.valueOf(value));
			}catch (ConfigurationException e) {
				throw new ConfigurationException(propertyName + " must be an array of non negative floating-point numbers. " + value + " value is invalid.", e);
			}
		}
	}

	public static void checkNonNegative(String propertyName, Double value) throws ConfigurationException {
		if (value < 0) {
			throw new ConfigurationException(propertyName + " must be a non negative floating-point number.");
		}
	}

	public static void checkIsNonNegativeArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkNonNegative(propertyName, Integer.valueOf(value));
			}catch (ConfigurationException e) {
				throw new ConfigurationException(propertyName + " must be an array of non negative integers. " + value + " value is invalid.", e);
			}
		}
	}

	public static void checkIsNonNegativeDouble2DArray(String propertyName, String[] values,
			String separator) throws ConfigurationException {
		for (String value : values) {
			checkIsNonNegativeDoubleArray(propertyName, value.split(separator));
		}
	}

	public static void checkIsNonNegative2DArray(String propertyName, String[] values,
			String separator) throws ConfigurationException {
		for (String value : values) {
			checkIsNonNegativeArray(propertyName, value.split(separator));
		}
	}

	public static void checkIsPositiveDoubleArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkPositive(propertyName, Double.valueOf(value));
			}catch (ConfigurationException e) {
				throw new ConfigurationException(propertyName + " must be an array of positive floating-point numbers. " + value + " value is invalid.", e);
			}
		}
	}

	public static void checkPositive(String propertyName, Double value) throws ConfigurationException {
		if (value <= 0) {
			throw new ConfigurationException(propertyName + " must be a positive floating-point number.");
		}
	}

	public static <T extends Enum<T>> void checkIsEnumArray(String propertyName, String[] values,
			Class<T> enumClass) throws ConfigurationException {
		for (String value : values) {
			try{
				Enum.valueOf(enumClass, value);
			}catch(Exception e){
				throw new ConfigurationException(propertyName + " must be a valid member of enum " + enumClass.getCanonicalName() + ". " + value + " value is invalid.", e);
			}
		}
	}

}
