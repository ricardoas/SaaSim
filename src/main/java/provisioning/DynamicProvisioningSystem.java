package provisioning;

import java.util.HashMap;
import java.util.Map;

import commons.cloud.Request;
import commons.cloud.UtilityResult;
import commons.sim.AccountingSystem;
import commons.sim.components.MachineDescriptor;
import commons.sim.provisioningheuristics.RanjanStatistics;

/**
 * 
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class DynamicProvisioningSystem implements DPS{

	protected long availableIDs;
	
	protected AccountingSystem accountingSystem;
	
	protected DynamicConfigurable configurable;
	
	protected final Map<Long, DynamicConfigurable> configurables;

	private UtilityResult utilityResult;

	/**
	 * Default constructor.
	 */
	public DynamicProvisioningSystem() {
		this.availableIDs = 0;
		this.configurables = new HashMap<Long, DynamicConfigurable>();
		this.accountingSystem = new AccountingSystem();
	}
	
	@Override
	public void registerConfigurable(DynamicConfigurable configurable) {
		this.accountingSystem.setUpConfigurables(configurable);
	}

	@Override
	public void reportRequestFinished(Request request) {
		this.accountingSystem.reportRequestFinished(request);
	}
	
	@Override
	public void setAccountingSystem(AccountingSystem system){
		this.accountingSystem = system;
	}

	@Override
	public AccountingSystem getAccountingSystem() {
		return this.accountingSystem;
	}
	
	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		
	}

	@Override
	public void evaluateUtilization(long now, RanjanStatistics statistics, int tier) {
		
	}

	@Override
	public void machineTurnedOff(MachineDescriptor machineDescriptor) {
		this.accountingSystem.reportMachineFinish(machineDescriptor);
	}

	@Override
	public UtilityResult calculateUtility() {
		return utilityResult;
	}

	@Override
	public void chargeUsers(long currentTimeInMillis) {
		this.utilityResult = this.accountingSystem.calculateUtility(currentTimeInMillis);
	}
}
