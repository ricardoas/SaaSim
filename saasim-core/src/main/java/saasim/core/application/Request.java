package saasim.core.application;


/**
 * Request abstractions must describe an interaction with an Application (and possibly each tier of it). 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface Request {

	long getArrivalTimeInMillis();

}
