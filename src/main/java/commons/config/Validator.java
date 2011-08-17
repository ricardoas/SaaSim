package commons.config;

public class Validator {

	public static void checkNonNegative(int value) {
		if (value < 0) {
			throw new RuntimeException();
		}
	}

	public static void checkNotEmpty(String value) {
		if (value == null || value.isEmpty()) {
			throw new RuntimeException();
		}
	}

	public static void checkPositive(int value) {
		if (value <= 0) {
			throw new RuntimeException();
		}
	}

}
