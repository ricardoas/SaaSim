package saasim.core.application;


/**
 * Application assembler.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SingleTierApplicationFactory extends AbstractFactory<Application> {

	@Override
	public Application build() {
		return new SingleTierApplication();
	}
	
}
