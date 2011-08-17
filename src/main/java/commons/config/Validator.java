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

	public static void checkIsPositiveIntegerArray(String[] values) {
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

	private static void checkNonNegative(Double value) {
		if (value < 0) {
			throw new RuntimeException();
		}
	}

	public static void checkIsNonNegativeIntegerArray(String[] values) {
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

	public static void checkInNonNegativeInteger2DArray(String[] values,
			String separator) {
		for (String value : values) {
			checkIsNonNegativeIntegerArray(value.split(separator));
		}
	}

}
