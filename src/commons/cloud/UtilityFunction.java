package commons.cloud;

/**
 * This class expresses the math utility function developed to represent the receipts and costs
 * considered when evaluating a certain infrastructure.
 * @author davidcmm
 *
 */
public class UtilityFunction {

	public double calculateUtility(Contract contract, User user, Provider provider){
		return calculateTotalReceipt(contract, user) - calculateCost(user.consumedTransference, provider);
	}

	public double calculateCost(double totalTransferred, Provider provider) {
		return provider.calculateCost(totalTransferred);
	}

	public double calculateTotalReceipt(Contract contract, User user) {
		if(contract.setupCost < 0){
			throw new RuntimeException("Negative setupCost "+contract.setupCost+" in total receipt calculation!");
		}
		if(contract.price < 0){
			throw new RuntimeException("Negative setupCost "+contract.setupCost+" in total receipt calculation!");
		}
		
		return contract.setupCost + contract.price + calcExtraReceipt(contract, user);
	}

	public double calcExtraReceipt(Contract contract, User user) {
		double extraReceipt = 0d;
		if(user.consumedCpu < 0){
			throw new RuntimeException("Negative consumed CPU "+user.id+" in utility calculation!");
		}
		if(user.consumedTransference < 0){
			throw new RuntimeException("Negative consumed transference "+user.id+" in utility calculation!");
		}
		if(contract.cpuLimit < 0){
			throw new RuntimeException("Negative cpu limit "+contract.cpuLimit+" in utility calculation!");
		}
		if(contract.transferenceLimit < 0){
			throw new RuntimeException("Negative transference limit "+contract.transferenceLimit+" in utility calculation!");
		}
		
		if(user.consumedCpu > contract.cpuLimit){
			extraReceipt += (user.consumedCpu - contract.cpuLimit) * contract.extraCpuCost;//FIXME: verify input unit
		}
		if(user.consumedTransference > contract.transferenceLimit){
			extraReceipt += (user.consumedTransference - contract.transferenceLimit) * contract.extraTransferenceCost;
		}
		
		return extraReceipt;
	}
}
