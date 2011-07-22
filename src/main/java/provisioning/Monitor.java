package provisioning;

import commons.sim.jeevent.JEEventHandler;

/**
 * 
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public interface Monitor extends JEEventHandler{
	
	void setDPS(DPS dps);

}
