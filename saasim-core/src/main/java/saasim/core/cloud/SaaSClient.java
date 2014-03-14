package saasim.core.cloud;

import java.io.Serializable;


public interface SaaSClient extends Comparable<SaaSClient>, Serializable{
	
	int getID();
	
	void resetCounters();
}
