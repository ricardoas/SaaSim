package commons.sim;

import java.io.Serializable;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.cloud.UtilityResult;
import commons.cloud.UtilityResultEntry;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class AccountingSystem implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4731959933590610261L;
	private UtilityResult utilityResult;
	private final User[] users;
	private final Provider[] providers;
	
	/**
	 * Default constructor.
	 * @param users
	 * @param providers
	 */
	public AccountingSystem(User[] users, Provider[] providers) {
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
