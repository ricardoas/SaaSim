package saasim.ext.cloud.util;

import static saasim.core.config.IaaSPlanProperties.IAAS_PLAN_PROVIDER_NAME;
import static saasim.core.config.IaaSPlanProperties.IAAS_PLAN_PROVIDER_RESERVATION;
import static saasim.core.config.IaaSPlanProperties.IAAS_PLAN_PROVIDER_TYPES;
import static saasim.core.config.IaaSProvidersProperties.*;
import static saasim.core.util.DataUnit.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.configuration.ConfigurationException;

import saasim.core.cloud.IaaSProvider;
import saasim.core.cloud.InstanceType;
import saasim.core.cloud.util.IaaSProviderFactory;
import saasim.core.config.Configuration;
import saasim.core.util.DataUnit;
import saasim.ext.cloud.AmazonEC2;

public class AmazonEC2IaaSProviderFactory extends IaaSProviderFactory {

	public AmazonEC2IaaSProviderFactory() {/*Empty block*/}

	@Override
	public IaaSProvider[] buildIaaSProviders() {
		
		Configuration config = Configuration.getInstance();
		
		int numberOfProviders = config.getInt(IAAS_NUMBER_OF_PROVIDERS);
		
		String[] names = config.getStringArray(IAAS_PROVIDER_NAME);
		int[] onDemandLimits = config.getIntegerArray(IAAS_PROVIDER_ONDEMAND_LIMIT);
		int[] reservedLimits = config.getIntegerArray(IAAS_PROVIDER_RESERVED_LIMIT);
		double[] monitoringCosts = config.getDoubleArray(IAAS_PROVIDER_MONITORING);
		long[][] transferInLimits = DataUnit.convert(config.getLong2DArray(IAAS_PROVIDER_TRANSFER_IN), GB, B);
		double[][] transferInCosts = DataUnit.convert(config.getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_IN), B, GB);
		long[][] transferOutLimits = DataUnit.convert(config.getLong2DArray(IAAS_PROVIDER_TRANSFER_OUT), GB, B);
		double[][] transferOutCosts = DataUnit.convert(config.getDouble2DArray(IAAS_PROVIDER_COST_TRANSFER_OUT), B, GB);
		
		InstanceType[][] machinesType = config.getEnum2DArray(IAAS_PROVIDER_TYPES, InstanceType.class);
		double[][] onDemandCpuCosts = config.getDouble2DArray(IAAS_PROVIDER_ONDEMAND_CPU_COST);
		double[][] reservedCpuCosts = config.getDouble2DArray(IAAS_PROVIDER_RESERVED_CPU_COST);
		double[][] reservationOneYearFees = config.getDouble2DArray(IAAS_PROVIDER_ONE_YEAR_FEE);
		double[][] reservationThreeYearsFees = config.getDouble2DArray(IAAS_PROVIDER_THREE_YEARS_FEE);
		
		List<String> providersWithPlan = Arrays.asList(config.getStringArray(IAAS_PLAN_PROVIDER_NAME));
		InstanceType[][] machines = config.getEnum2DArray(IAAS_PLAN_PROVIDER_TYPES, InstanceType.class);
		long[][] reservations = config.getLong2DArray(IAAS_PLAN_PROVIDER_RESERVATION);
		
		IaaSProvider[] providers = new IaaSProvider[numberOfProviders];
		
		for(int i = 0; i < numberOfProviders; i++){
			
			List<TypeProvider> types = new ArrayList<TypeProvider>();
			
			int providerIndex = providersWithPlan.indexOf(names[i]);
			List<InstanceType> typeList = null;
			
			if(providerIndex != -1){
				typeList = Arrays.asList(machines[providerIndex]);
			}
			
			if(machinesType[i].length != onDemandCpuCosts[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_ONDEMAND_CPU_COST);
			}
		
			if(machinesType[i].length != reservedCpuCosts[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_RESERVED_CPU_COST);
			}
		
			if(machinesType[i].length != reservationOneYearFees[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_ONE_YEAR_FEE);
			}
		
			if(machinesType[i].length != reservationThreeYearsFees[i].length){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TYPES + " and " + IAAS_PROVIDER_THREE_YEARS_FEE);
			}
		
			for (int j = 0; j < machinesType[i].length; j++) {
				long reservation = 0;
				if(typeList != null){
					int index = typeList.indexOf(machinesType[i][j]);
					reservation = (index == -1)? 0: reservations[providerIndex][index];
				}
				types.add(new TypeProvider(i, machinesType[i][j], onDemandCpuCosts[i][j], reservedCpuCosts[i][j], 
						reservationOneYearFees[i][j], reservationThreeYearsFees[i][j], reservation));
			}
			
			if(transferInLimits[i].length != transferInCosts[i].length - 1){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TRANSFER_IN + " and " + IAAS_PROVIDER_COST_TRANSFER_IN);
			}
		
			if(transferOutLimits[i].length != transferOutCosts[i].length - 1){
				throw new ConfigurationException("Check values of " + IAAS_PROVIDER_TRANSFER_OUT + " and " + IAAS_PROVIDER_COST_TRANSFER_OUT);
			}
			
			providers[i] = new AmazonEC2(i, names[i], onDemandLimits[i],
							reservedLimits[i], monitoringCosts[i], transferInLimits[i], 
							transferInCosts[i], transferOutLimits[i], transferOutCosts[i], types);
		}
		
		return providers;
	}

}
