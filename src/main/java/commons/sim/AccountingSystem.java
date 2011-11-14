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
	
	/**
	 * Default constructor.
	 * @param users
	 * @param providers
	 */
	public AccountingSystem(User[] users, Provider[] providers) {
		this.utilityResult = new UtilityResult(users, providers);
	}

	/**
	 * @param currentTimeInMillis
	 * @param users TODO
	 * @param providers TODO
	 * @return 
	 */
	public UtilityResultEntry accountPartialUtility(long currentTimeInMillis, User[] users, Provider[] providers){
		UtilityResultEntry entry = new UtilityResultEntry(currentTimeInMillis, users, providers);
		utilityResult.account(entry);
		return entry;
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return
	 */
	public UtilityResult calculateUtility(){
		return utilityResult;
	}
}
