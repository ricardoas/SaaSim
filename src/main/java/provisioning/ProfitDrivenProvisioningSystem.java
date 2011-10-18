package provisioning;

import java.util.List;

import commons.cloud.MachineType;
import commons.cloud.Provider;
import commons.cloud.Request;

public class ProfitDrivenProvisioningSystem extends DynamicProvisioningSystem{

	public ProfitDrivenProvisioningSystem() {
		super();
	}
	
	@Override
	public void requestQueued(long timeMilliSeconds, Request request, int tier) {
		List<Provider> providers = canBuyMachine(MachineType.M1_SMALL, false);
		if(!providers.isEmpty()){
			configurable.addServer(tier, buyMachine(providers.get(0), MachineType.M1_SMALL, false), true);
		}else{
			reportLostRequest(request);
		}
	}
}
