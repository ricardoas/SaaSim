package commons.config;


public class Validator {

	public static void checkNonNegative(long value) {
		if (value < 0) {
			throw new RuntimeException();
		}
	}

	public static void checkNotEmpty(String value) {
		if (value == null || value.isEmpty()) {
			throw new RuntimeException();
		}
	}

	public static void checkPositive(long value) {
		if (value <= 0) {
			throw new RuntimeException();
		}
	}

	public static void checkIsPositiveArray(String[] values) {
		for (String value : values) {
			checkPositive(Integer.valueOf(value));
		}
	}

	public static void checkIsNonEmptyStringArray(String[] values) {
		for (String value : values) {
			checkNotEmpty(value);
		}
	}

	public static void checkIsNonNegativeDoubleArray(String[] values) {
		for (String value : values) {
			checkNonNegative(Double.valueOf(value));
		}
	}

	public static void checkNonNegative(Double value) {
		if (value < 0) {
			throw new RuntimeException();
		}
	}

	public static void checkIsNonNegativeArray(String[] values) {
		for (String value : values) {
			checkNonNegative(Integer.valueOf(value));
		}
	}

	public static void checkIsNonNegativeDouble2DArray(String[] values,
			String separator) {
		for (String value : values) {
			checkIsNonNegativeDoubleArray(value.split(separator));
		}
	}

	public static void checkIsNonNegative2DArray(String[] values,
			String separator) {
		for (String value : values) {
			checkIsNonNegativeArray(value.split(separator));
		}
	}

	public static void checkIsPositiveDoubleArray(String[] values) {
		for (String value : values) {
			checkPositive(Double.valueOf(value));
		}
	}

	public static void checkPositive(Double value) {
		if (value <= 0) {
			throw new RuntimeException();
		}
	}

	public static <T extends Enum<T>> void checkIsEnumArray(String[] values,
			Class<T> enumClass) {
		for (String value : values) {
			Enum.valueOf(enumClass, value);
		}
	}

}
