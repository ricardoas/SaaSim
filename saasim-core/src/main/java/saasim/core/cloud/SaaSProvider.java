package saasim.core.cloud;

import java.io.Serializable;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface SaaSProvider extends Serializable {
	
	int getID();
	
	void subscribe(SaaSClient client, String contract);
	
	void unsubscribe(SaaSClient client);
	
	
}
