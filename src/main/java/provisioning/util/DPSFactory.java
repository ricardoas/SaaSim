package provisioning.util;

import provisioning.DPS;

import commons.config.SimulatorConfiguration;


public enum DPSFactory {
	
	INSTANCE;
	
	private DPSFactory() {
		
	}
	
	public DPS createDPS(){
		Class<?> clazz = SimulatorConfiguration.getInstance().getDPSHeuristicClass();
		
		try {
			return (DPS) clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException("Something went wrong when loading "+ clazz.getCanonicalName(), e);
		}
	}

	
}
