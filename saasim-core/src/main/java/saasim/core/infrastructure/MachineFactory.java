package saasim.core.infrastructure;


public interface MachineFactory{
	
	Machine create(InstanceDescriptor descriptor);

}
