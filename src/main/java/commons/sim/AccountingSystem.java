package commons.sim;

import java.util.Map;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AccountingSystem {
	
	private UtilityResult utilityResult;
	
	/**
	 * Default constructor.
	 */
	public AccountingSystem(){
		this.utilityResult = new UtilityResult();
	}
	
	public void accountPartialUtility(long currentTimeInMillis, Map<Integer, User> users, Map<String, Provider> providers){
		UtilityResultEntry entry = new UtilityResultEntry(currentTimeInMillis, users, providers);
		for (User user : users.values()) {
			user.calculatePartialReceipt(entry);
		}
		for (Provider provider : providers.values()) {
			provider.calculateCost(entry, currentTimeInMillis);
		}
		this.utilityResult.addEntry(entry);
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return
	 */
	public UtilityResult calculateUtility(Map<Integer, User> users, Map<String, Provider> providers){
		for(Provider provider : providers.values()){
			provider.calculateUniqueCost(utilityResult);
		}
		for(User user : users.values()){
			user.calculateOneTimeFees(utilityResult);
		}
		return utilityResult;
	}

}
