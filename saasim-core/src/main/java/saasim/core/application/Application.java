package saasim.core.application;

import saasim.core.event.EventHandler;

/**
 * Application abstraction.
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Application extends EventHandler{
	
	void config(int d);

	void process(Request request, ResponseListener callback);
}
