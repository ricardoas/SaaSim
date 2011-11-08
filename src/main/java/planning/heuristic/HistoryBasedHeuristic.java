package planning.heuristic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import planning.util.MachineUsageData;
import planning.util.PlanIOHandler;
import provisioning.DPS;
import provisioning.Monitor;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.User;
import commons.config.Configuration;
import commons.io.Checkpointer;
import commons.sim.SimpleSimulator;
import commons.sim.components.LoadBalancer;
import commons.sim.components.Machine;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.util.SimulatorProperties;
import commons.util.TimeUnit;

public class HistoryBasedHeuristic implements PlanningHeuristic{

	private Map<MachineType, Integer> plan;
	
	private static final long YEAR_IN_HOURS = 8640;
	
	private MachineUsageData machineData;

	private final Monitor monitor;

	public HistoryBasedHeuristic(JEEventScheduler scheduler, Monitor monitor, LoadBalancer[] loadBalancers){
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
	
	@Override
	public void findPlan(Provider[] cloudProviders, User[] cloudUsers) {
		
		//Simulating ...
		DPS dps = (DPS) this.monitor;
		
//		SimpleSimulator simulator = (SimpleSimulator) SimulatorFactory.buildSimulator(this.scheduler);
		SimpleSimulator simulator = (SimpleSimulator) Checkpointer.loadApplication();
		
		dps.registerConfigurable(simulator);
		
		simulator.start();
		
		//Calculating machines use data
		LoadBalancer[] loadBalancers = calculateMachinesUsage(simulator);
		Configuration config = Configuration.getInstance();
		
//		System.out.println("DAy: "+Checkpointer.loadSimulationInfo().getCurrentDay());
		
		if(Checkpointer.loadSimulationInfo().isFinishDay()){//Simulation finished!
			
			calculateMachinesToReserve(config);
			Checkpointer.clear();
			PlanIOHandler.clear();
			try {
				PlanIOHandler.createPlanFile(this.plan, Checkpointer.loadProviders());
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			
			
		}else{//Persist data to other round
			
			persisDataToNextRound(loadBalancers, config);
		}
	}
	
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

	private void calculateMachinesToReserve(Configuration config) {
		Map<MachineType, Map<Long, Double>> map = this.machineData.getMachineUsagePerType();
		Map<MachineType, Double> limits = findReservationLimits(Checkpointer.loadProviders()[0]);
		long planningPeriod = config.getLong(SimulatorProperties.PLANNING_PERIOD);
	
		Map<MachineType, List<Double>> typeUse = new HashMap<MachineType, List<Double>>();
		
		for(MachineType type : map.keySet()	){//Checking if any machine was very used!
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

	private void persisDataToNextRound(LoadBalancer[] loadBalancers,
			Configuration config) {
		try {
			Checkpointer.save();
			Checkpointer.dumpMachineData(this.machineData);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private LoadBalancer[] calculateMachinesUsage(SimpleSimulator simulator) {
		LoadBalancer[] loadBalancers = simulator.getTiers();
		
		for(LoadBalancer lb : loadBalancers){
			List<Machine> servers = lb.getServers();
			for(Machine server : servers){
				MachineDescriptor descriptor = server.getDescriptor();
				this.machineData.addUsage(descriptor.getType(),
						descriptor.getMachineID(), server.getTotalTimeUsed());
			}
		}
		return loadBalancers;
	}

	@Override
	public double getEstimatedProfit(int period) {
		return 0;
	}

	@Override
	public Map<MachineType, Integer> getPlan(User[] cloudUsers) {
		return this.plan;
	}

}
