package saasim.util;

/**
 * This class containing representation for the unit of data in the application and
 * them values. 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum DataUnit {

	B(1), 
	KB(1024 * B.getBytes()),
	MB(1024 * KB.getBytes()),
	GB(1024 * MB.getBytes()),
	TB(1024 * GB.getBytes());
	
	private final long dataInBytes;
	
	/**
	 * Default private constructor. 
	 * @param dataInBytes value of data in bytes
	 */
	private DataUnit(long dataInBytes) {
		this.dataInBytes = dataInBytes;
	}

	/**
	 * Gets the value of data in bytes.
	 * @return the data in bytes
	 */
	public long getBytes() {
		return dataInBytes;
	}
	
	/**
	 * Realizes the conversion between two units, about double values.
	 * @param values an array of array containing the values to convert
	 * @param from {@link DataUnit} to be converted
	 * @param to {@link DataUnit} in which data will be converted
	 * @return The values converted.
	 */
	public static double [][] convert(double values[][], DataUnit from, DataUnit to){
		double rate = 1.0 * from.dataInBytes / to.dataInBytes;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				values[i][j] = values[i][j] * rate; 
			}
		}
		return values;
	}
	
	/**
	 * Realizes the conversion between two units, about double values.
	 * @param values an array containing the values to convert
	 * @param from {@link DataUnit} to be converted
	 * @param to {@link DataUnit} in which data will be converted
	 * @return The values converted.
	 */
	public static double [] convert(double values[], DataUnit from, DataUnit to){
		double rate = 1.0 * from.dataInBytes / to.dataInBytes;
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i] * rate; 
		}
		return values;
	}

	/**
	 * Realizes the conversion between two units, about long values.
	 * @param values an array of array containing the values to convert
	 * @param from {@link DataUnit} to be converted
	 * @param to {@link DataUnit} in which data will be converted
	 * @return The values converted.
	 */
	public static long [][] convert(long values[][], DataUnit from, DataUnit to){
		long rate = from.dataInBytes / to.dataInBytes;
		for (int i = 0; i < values.length; i++) {
			for (int j = 0; j < values[i].length; j++) {
				values[i][j] = values[i][j] * rate; 
			}
		}
		return values;
	}

	/**
	 * Realizes the conversion between two units, about long values.
	 * @param values an array containing the values to convert
	 * @param from {@link DataUnit} to be converted
	 * @param to {@link DataUnit} in which data will be converted
	 * @return The values converted.
	 */
	public static long [] convert(long values[], DataUnit from, DataUnit to){
		long rate = from.dataInBytes / to.dataInBytes;
		for (int i = 0; i < values.length; i++) {
			values[i] = values[i] * rate; 
		}
		return values;
	}
}
