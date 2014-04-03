package saasim.ext.cloud;

import saasim.core.cloud.IaaSProvider;
import saasim.core.config.AbstractFactory;


/**
 * Application assembler.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AWSProviderFactory extends AbstractFactory<IaaSProvider> {

	/**
	 * {@inheritDoc}
	 *
	 * @see saasim.core.config.AbstractFactory#build()
	 */
	@Override
	public IaaSProvider build(Object... args) {
		return new AmazonEC2();
	}
}
