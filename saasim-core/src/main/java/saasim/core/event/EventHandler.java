package saasim.core.event;

import java.io.Serializable;

import saasim.core.config.Configuration;

/**
 * 
 * 
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface EventHandler extends Serializable{
	
	final static EventScheduler scheduler = Configuration.getInstance().getScheduler();
	
}