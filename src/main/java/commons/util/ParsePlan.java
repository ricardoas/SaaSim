package commons.util;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;

import commons.sim.util.IaaSPlanProperties;

public class ParsePlan {
	
	private static String[] PLANNED_TYPES = {"M1_SMALL", "M1_LARGE", "C1_MEDIUM", "M1_XLARGE"};
	
	public static void main(String[] args) throws ConfigurationException {
		
		if(args.length != 1){
			System.err.println("usage: <plan file>");
			System.exit(1);
		}
		
		try{
			PropertiesConfiguration config = new PropertiesConfiguration(args[0]);
			String[] types = config.getString(IaaSPlanProperties.IAAS_PLAN_PROVIDER_TYPES).split("\\|");
			String[] reserved = config.getString(IaaSPlanProperties.IAAS_PLAN_PROVIDER_RESERVATION).split("\\|");
			
			Map<String, String> reservations = new HashMap<String, String>();
			for(int i = 0; i < types.length; i++){
				reservations.put(types[i], reserved[i]);
			}
			
			StringBuffer titles = new StringBuffer();
			StringBuffer values = new StringBuffer();
			
			for(int i = 0; i < PLANNED_TYPES.length; i++){
				titles.append(PLANNED_TYPES[i]+"\t");
				if(reservations.containsKey(PLANNED_TYPES[i].toLowerCase())){
					values.append(reservations.get(PLANNED_TYPES[i].toLowerCase())+"\t");
				}else{
					values.append("0\t");
				}
			}
			
			System.out.println(titles.toString());
			System.out.println(values.toString());
			
		}catch(Exception e){
		}
	}

}
