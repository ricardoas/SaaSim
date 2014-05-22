package saasim.ext.application;

import saasim.core.application.Request;
import saasim.core.application.Tier;
import saasim.core.infrastructure.Monitorable;


/**
 * Abstract implementation of {@link Tier}. It only defines {@link Request} processing methods.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public abstract class AbstractTier implements Tier{
	
	private int id;
	
	@Override
	public int getID() {
		return id;
	}
	
	@Override
	public void setID(int id) {
		this.id = id;
	}
}