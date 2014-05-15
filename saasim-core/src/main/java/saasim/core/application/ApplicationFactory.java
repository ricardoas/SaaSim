package saasim.core.application;

import com.google.inject.Guice;



/**
 * {@link Guice} appliance to build {@link Application}s.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface ApplicationFactory{
	
	/**
	 * @return a new {@link Application}
	 */
	Application create();

}
