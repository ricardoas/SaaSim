package saasim.core.sim;



public interface TrafficGeneratorFactory{
	
	TrafficGenerator create(int tenantID);

}
