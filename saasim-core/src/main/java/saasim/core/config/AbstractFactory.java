package saasim.core.config;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 * @param <T>
 */
public abstract class AbstractFactory<T>{
	
	/**
	 * @param args
	 * @return
	 */
	public abstract T build(Object... args);

}