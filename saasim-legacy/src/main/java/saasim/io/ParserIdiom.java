package saasim.io;

import saasim.cloud.Request;

/**
 * Represents the possible values ​​of a file converter received by the simulator, 
 * which will transform the data into values ​​that the application knows to manage.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public enum ParserIdiom {

	GEIST(GEISTWorkloadParser.class);
	
	private final Class<?> idiomParserClass;

	/**
	 * Private default constructor.
	 * @param idiomParserClass the parser
	 */
	private ParserIdiom(Class<?> idiomParserClass){
		this.idiomParserClass = idiomParserClass;
	}

	/**
	 * @return the idiomParserClass
	 */
	public Class<?> getIdiomParserClass() {
		return idiomParserClass;
	}

	/**
	 * Create a unique instance for this {@link ParserIdiom} or returns the existing instance.
	 * @param string the name of parser class
	 * @param shift 
	 * @return The unique instance of {@link ParserIdiom}.
	 */
	@SuppressWarnings("unchecked")
	public WorkloadParser<Request> getInstance(String string, long shift) {
		try {
			return (WorkloadParser<Request>) idiomParserClass.getConstructor(String.class, Long.TYPE).newInstance(string, shift);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
