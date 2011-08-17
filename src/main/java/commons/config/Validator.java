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

}
