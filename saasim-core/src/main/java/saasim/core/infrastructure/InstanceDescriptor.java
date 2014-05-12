package saasim.core.infrastructure;

/**
 * Instance descriptor abstraction. This is the machine as seen by an instance provider.
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public interface InstanceDescriptor {
	
	int getNumberOfCPUCores();
	
	boolean isOn();

	void turnOff();

	void setMachine(Machine machine);

}