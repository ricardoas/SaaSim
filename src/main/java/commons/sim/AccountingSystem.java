package commons.sim;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;
import commons.config.Configuration;
import commons.io.Checkpointer;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AccountingSystem {
	
	private static AccountingSystem instance;

	public static AccountingSystem getInstance(){
		if(instance == null){
			instance = Checkpointer.hasCheckpoint()?
					Checkpointer.loadAccountingSystem():
					new AccountingSystem(Configuration.getInstance().getUsers(), 
							Configuration.getInstance().getProviders());
		}
		return instance;
	}
	
	private UtilityResult utilityResult;
	private final User[] users;
	private final Provider[] providers;
	
	private AccountingSystem(User[] users, Provider[] providers) {
		this.users = users;
		this.providers = providers;
		this.utilityResult = new UtilityResult(users.length, providers.length);
	}

	/**
	 * @param currentTimeInMillis
	 */
	public void accountPartialUtility(long currentTimeInMillis){
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
	public UtilityResult calculateUtility(){
		for(Provider provider : providers){
			provider.calculateUniqueCost(utilityResult);
		}
		for(User user : users){
			user.calculateOneTimeFees(utilityResult);
		}
		return utilityResult;
	}

}
