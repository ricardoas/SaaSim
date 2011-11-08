package commons.config;

import org.apache.commons.configuration.ConfigurationException;

import commons.cloud.MachineType;


/**
 * Validation rules.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class Validator {

	public static void checkNonNegative(String propertyName, String value) throws ConfigurationException {
		try{
			Long valueOf = Long.valueOf(value);
			if (valueOf < 0) {
				throw new ConfigurationException(propertyName + " must be a non negative integer.");
			}
		}catch(NumberFormatException e){
			throw new ConfigurationException(propertyName + " must be a non negative integer.", e);
		}
	}

	public static void checkNotEmpty(String propertyName, String value) throws ConfigurationException {
		if (value == null || value.isEmpty()) {
			throw new ConfigurationException(propertyName + " can't be empty.");
		}
	}

	public static void checkPositive(String propertyName, String value) throws ConfigurationException {
		try{
			Long valueOf = Long.valueOf(value);
			if (valueOf <= 0) {
				throw new ConfigurationException(propertyName + " must be a positive integer.");
			}
		}catch(NumberFormatException e){
			throw new ConfigurationException(propertyName + " must be a positive integer.", e);
		}
	}

	public static void checkIsPositiveArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkPositive(propertyName, value);
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
				checkNonNegativeDouble(propertyName, value);
			}catch (ConfigurationException e) {
				throw new ConfigurationException(propertyName + " must be an array of non negative floating-point numbers. " + value + " value is invalid.", e);
			}
		}
	}

	public static void checkNonNegativeDouble(String propertyName, String value) throws ConfigurationException {
		try{
			Double valueOf = Double.valueOf(value);
			if (valueOf < 0) {
				throw new ConfigurationException(propertyName + " must be a non negative floating-point number.");
			}
		}catch(NumberFormatException e){
			throw new ConfigurationException(propertyName + " must be a non negative floating-point number.", e);
		}
	}

	public static void checkIsNonNegativeArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkNonNegative(propertyName, value);
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

	public static void checkIsNonEmptyStringArray(String propertyName,
			String[][] values) throws ConfigurationException {
		for (String[] value : values) {
			for (String string : value) {
				try {
					checkNonNegative(propertyName, string);
				} catch (ConfigurationException e) {
					throw new ConfigurationException(propertyName
							+ " must be an array of non negative integers. "
							+ value + " value is invalid.", e);
				}
			}
		}
	}

	public static void checkIsPositiveDoubleArray(String propertyName, String[] values) throws ConfigurationException {
		for (String value : values) {
			try{
				checkPositiveDouble(propertyName, value);
			}catch (ConfigurationException e) {
				throw new ConfigurationException(propertyName + " must be an array of positive floating-point numbers. " + value + " value is invalid.", e);
			}
		}
	}

	public static void checkPositiveDouble(String propertyName, String value) throws ConfigurationException {
		try{
			Double valueOf = Double.valueOf(value);
			if (valueOf <= 0) {
				throw new ConfigurationException(propertyName + " must be a positive floating-point number.");
			}
		}catch(NumberFormatException e){
			throw new ConfigurationException(propertyName + " must be a positive floating-point number.", e);
		}
	}

	public static <T extends Enum<T>> void checkIsEnumArray(String propertyName, String[] values,
			Class<T> enumClass) throws ConfigurationException {
		for (String value : values) {
			checkEnum(propertyName, value, enumClass);
		}
	}

	public static <T extends Enum<T>> void checkEnum(String propertyName, String value,
			Class<T> enumClass) throws ConfigurationException {
		try{
			Enum.valueOf(enumClass, value.trim().toUpperCase().replace('.', '_'));
		}catch(RuntimeException e){
			throw new ConfigurationException(propertyName + " must be a valid member of enum " + enumClass.getCanonicalName() + ". " + value + " value is invalid.", e);
		}
	}

	public static void checkIsEnum2DArray(String propertyName,
			String[] values, Class<MachineType> enumClass,
			String separator) throws ConfigurationException {
		for (String value : values) {
			checkIsEnumArray(propertyName, value.split(separator), enumClass);
		}
	}

}
