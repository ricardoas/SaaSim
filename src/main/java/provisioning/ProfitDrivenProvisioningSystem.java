package provisioning;

import commons.cloud.Request;

public class ProfitDrivenProvisioningSystem extends DynamicProvisioningSystem{

	public ProfitDrivenProvisioningSystem() {
		super();
	}
	
	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		if(accountingSystem.canBuyMachine()){
			configurable.addServer(tier, accountingSystem.buyMachine(), true);
		}else{
			accountingSystem.reportRequestLost(request);
		}
	}
}
