package saasim.ext.application;

import saasim.core.application.Application;
import saasim.core.config.AbstractFactory;


/**
 * Application assembler.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SingleTierApplicationFactory extends AbstractFactory<Application> {

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.config.AbstractFactory#build()
	 */
	@Override
	public Application build(Object... args) {
		return new SingleTierApplication();
	}
}
