package commons.sim;

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
	 * @param numberOfUsers 
	 * @param numberOfProviders 
	 */
	public AccountingSystem(int numberOfUsers, int numberOfProviders){
		this.utilityResult = new UtilityResult(numberOfUsers, numberOfProviders);
	}
	
	/**
	 * @param currentTimeInMillis
	 * @param users
	 * @param providers
	 */
	public void accountPartialUtility(long currentTimeInMillis, User[] users, Provider[] providers){
		UtilityResultEntry entry = new UtilityResultEntry(currentTimeInMillis, users, providers);
		for (User user : users) {
			user.calculatePartialReceipt(entry);
		}
		for (Provider provider : providers) {
			provider.calculateCost(entry, currentTimeInMillis);
		}
		this.utilityResult.addEntry(entry);
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return
	 */
	public UtilityResult calculateUtility(User[] users, Provider[] providers){
		for(Provider provider : providers){
			provider.calculateUniqueCost(utilityResult);
		}
		for(User user : users){
			user.calculateOneTimeFees(utilityResult);
		}
		return utilityResult;
	}

}
