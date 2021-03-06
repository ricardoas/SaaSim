package saasim.planning.heuristic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import saasim.cloud.MachineType;
import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.config.Configuration;
import saasim.planning.util.MachineUsageData;
import saasim.planning.util.PlanIOHandler;
import saasim.provisioning.DPS;
import saasim.provisioning.Monitor;
import saasim.sim.DynamicConfigurable;
import saasim.sim.SimpleMultiTierApplication;
import saasim.sim.components.LoadBalancer;
import saasim.sim.components.SimpleLoadBalancerWithAdmissionControl;
import saasim.sim.components.Machine;
import saasim.sim.components.MachineDescriptor;
import saasim.sim.core.EventCheckpointer;
import saasim.sim.core.EventScheduler;
import saasim.sim.util.SimulatorProperties;
import saasim.util.TimeUnit;


/**
 * This {@link PlanningHeuristic} makes capacity planning based on historical planning application. 
 * For example, exists a statistic that is based on the use of machines for one year ago, which calculates 
 * the planning for Y years ahead.
 * 
 * @author David Candeia - davidcmm@lsd.ufcg.edu.br
 */
public class HistoryBasedHeuristic implements PlanningHeuristic {

	private Map<MachineType, Integer> plan;
	private static final long YEAR_IN_HOURS = 8640;
	private MachineUsageData machineData;
	private final Monitor monitor;

	/**
	 * Default constructor.
	 * @param scheduler {@link EventScheduler} event scheduler
	 * @param monitor {@link Monitor} for reporting information
	 * @param loadBalancers a set of {@link SimpleLoadBalancerWithAdmissionControl}s of the application
	 */
	public HistoryBasedHeuristic(EventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
		this.monitor = monitor;
		this.plan = new HashMap<MachineType, Integer>();
		
		try {
			machineData = PlanIOHandler.getMachineData();
		} catch (IOException e) {
		} catch (ClassNotFoundException e) {
		}
		if(machineData == null){
			machineData = new MachineUsageData();
		}
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void findPlan(Provider[] cloudProviders, User[] cloudUsers) {
		//Simulating ...
		DPS dps = (DPS) this.monitor;
		SimpleMultiTierApplication simulator = (SimpleMultiTierApplication) Configuration.getInstance().getSimulator();
		dps.registerConfigurable(new DynamicConfigurable[]{simulator});
//		simulator.start();
		
		//Calculating machines use data
		LoadBalancer[] loadBalancers = calculateMachinesUsage(simulator);
		Configuration config = Configuration.getInstance();
		
//		System.out.println("DAy: "+Checkpointer.loadSimulationInfo().getCurrentDay());
		
		if(Configuration.getInstance().getSimulationInfo().isFinishDay()){//Simulation finished!
			calculateMachinesToReserve(config);
			EventCheckpointer.clear();
			PlanIOHandler.clear();
			try {
				PlanIOHandler.createPlanFile(this.plan, cloudProviders);
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
		}else{ //Persist data to other round
			persisDataToNextRound(loadBalancers, config);
		}
	}

	/**
	 * Finds the reservation limits through the {@link Provider} of application.
	 * @param cloudProvider {@link Provider} of application
	 * @return A {@link Map} containing the {@link MachineType}s and the several reservation limits.
	 */
	private Map<MachineType, Double> findReservationLimits(Provider cloudProvider) {
		Map<MachineType, Double> typesLimits = new HashMap<MachineType, Double>();
		MachineType[] machineTypes = cloudProvider.getAvailableTypes();
		
		for(MachineType type : machineTypes){
			double yearFee = cloudProvider.getReservationOneYearFee(type);
			double reservedCpuCost = cloudProvider.getReservedCpuCost(type);
			double onDemandCpuCost = cloudProvider.getOnDemandCpuCost(type);
			
			long minimumHoursToBeUsed = Math.round(yearFee / (onDemandCpuCost - reservedCpuCost));
			double usageProportion = 1.0 * minimumHoursToBeUsed / YEAR_IN_HOURS;
			typesLimits.put(type, usageProportion);
		}
		return typesLimits;
	}

	/**
	 * Calculates the quantity of machines to reserve in the current planning. For this, verifies if any machine
	 * was very used and if the machines could be reserved.
	 * @param config represents the {@link Configuration} of this application 
	 */
	private void calculateMachinesToReserve(Configuration config) {
		Map<MachineType, Map<Long, Double>> map = this.machineData.getMachineUsagePerType();
		Map<MachineType, Double> limits = findReservationLimits(Configuration.getInstance().getProviders()[0]);
		long planningPeriod = config.getLong(SimulatorProperties.PLANNING_PERIOD);
	
		Map<MachineType, List<Double>> typeUse = new HashMap<MachineType, List<Double>>();
		
		for(MachineType type : map.keySet()	){ //Checking if any machine was very used!
			Map<Long, Double> machines = map.get(type);
			typeUse.put(type, new ArrayList<Double>());
			
			for(long machineID : machines.keySet()){
				double machineUsage = machines.get(machineID) / ( type.getNumberOfCores() * planningPeriod * 
						TimeUnit.DAY.getMillis());
				
				if( machineUsage >= limits.get(type) ){
					Integer numberOfServers = this.plan.get(type);
					if(numberOfServers == null ){
						numberOfServers = 0;
					}
					numberOfServers++;
					this.plan.put(type, numberOfServers);
				}else{
					typeUse.get(type).add(machineUsage);
				}
			}
		}
		
		//Trying to aggregate machines use in order to check if other machines could be reserved
		double currentUse = 0;
		List<MachineType> typeList = Arrays.asList(MachineType.values());
		Collections.reverse(typeList);
		
		for(Entry<MachineType, List<Double>> entry : typeUse.entrySet()){
			for(Double use : entry.getValue()){
				currentUse += use;
				for(MachineType type : typeList){
					if(limits.containsKey(type) && currentUse >= limits.get(type)){
						Integer machinesToReserve = this.plan.get(type);
						if(machinesToReserve == null){
							machinesToReserve = 0;
						}
						machinesToReserve++;
						this.plan.put(type, machinesToReserve);
						currentUse -= limits.get(type);
					}
				}
			}
		}
	}

	private void persisDataToNextRound(LoadBalancer[] loadBalancers, Configuration config) {
		try {
			EventCheckpointer.save();
			EventCheckpointer.dumpMachineData(this.machineData);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Calculates the usage of machines to make the planning, and for this, updating the usage of the {@link MachineUsageData}
	 * in this {@link HistoryBasedHeuristic}.
	 * @param simulator {@link SimpleMultiTierApplication} to use for recovered the tiers of application.
	 * @return A set of {@link SimpleLoadBalancerWithAdmissionControl}s of the application.
	 */
	private LoadBalancer[] calculateMachinesUsage(SimpleMultiTierApplication simulator) {
		SimpleLoadBalancerWithAdmissionControl[] loadBalancers = simulator.getTiers();
		
		for(SimpleLoadBalancerWithAdmissionControl lb : loadBalancers){
			List<Machine> servers = lb.getServers();
			for(Machine server : servers){
				MachineDescriptor descriptor = server.getDescriptor();
				this.machineData.addUsage(descriptor.getType(),
					 descriptor.getMachineID(), server.getTotalTimeUsed());
			}
		}
		return loadBalancers;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double getEstimatedProfit(int period) {
		return 0;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		return this.plan;
	}

}
