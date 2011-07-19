package commons.sim.util;

import provisioning.Monitor;

import commons.sim.components.LoadBalancer;
import commons.sim.jeevent.JEEventScheduler;

public abstract class ApplicationFactory {
	
	private static ApplicationFactory instance;
	
	public static ApplicationFactory getInstance(){
		
		if(instance == null){
			String className = "";
			try {
				instance = (ApplicationFactory) Class.forName(className).newInstance();
			} catch (Exception e) {
				throw new RuntimeException("Something went wrong when loading "+ className, e);
			}
		}
		return instance;
	}

	public abstract LoadBalancer createNewApplication(JEEventScheduler scheduler, Monitor monitor);
}
