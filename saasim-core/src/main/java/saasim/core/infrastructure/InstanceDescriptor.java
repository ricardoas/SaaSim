package saasim.core.infrastructure;

import saasim.core.saas.Application;

/**
 * Instance descriptor abstraction. This is the machine as seen by an instance provider.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface InstanceDescriptor {
	
	int getNumberOfCPUCores();
	
	boolean isOn();

	void turnOff(long now);

	void setMachine(Machine machine);

	void turnOn(long now);

	Application getApplication();

	void setApplication(Application application);

	Machine getMachine();

	long getFinishTime();

	long getCreationTime();

	int getTier();

	void setTier(int id);
}