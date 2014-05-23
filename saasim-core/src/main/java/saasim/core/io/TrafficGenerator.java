package saasim.core.io;

import saasim.core.saas.Application;

public interface TrafficGenerator {

	/**
	 * Start load generation.
	 */
	void start();

	Application getApplication();

}