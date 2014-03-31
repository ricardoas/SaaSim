package saasim.core.application;


/**
 * Application assembler.
 *  
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class ApplicationFactory extends AbstractFactory<Application> {
	
	/* (non-Javadoc)
	 * @see saasim.core.application.AbstractFactory#buildApplication()
	 */
	public abstract Application build();
}
