package saasim.core.io;

import saasim.core.application.Application;

public interface TrafficGenerator {

	/**
	 * Start load generation.
	 */
	void start();

	Application getApplication();

}