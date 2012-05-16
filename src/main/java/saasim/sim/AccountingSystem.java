package saasim.sim;

import java.io.Serializable;

import saasim.cloud.Provider;
import saasim.cloud.User;
import saasim.cloud.UtilityResult;
import saasim.cloud.UtilityResultEntry;


/**
 * Calculates values of {@link UtilityResult}.
 * 
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
	 * @param users an array of {@link User} containing the users of application
	 * @param providers an array of {@link Provider} containing the providers of application
	 */
	public AccountingSystem(User[] users, Provider[] providers) {
		this.utilityResult = new UtilityResult(users, providers);
	}

	/**
	 * Calculates the partial utility of application.
	 * @param currentTimeInMillis the current time in millis
	 * @param users TODO an array of {@link User} containing the users of application
	 * @param providers TODO an array of {@link Provider} containing the providers of application
	 * @return A {@link UtilityResultEntry} encapsulating the utility calculated.
	 */
	public UtilityResultEntry accountPartialUtility(long currentTimeInMillis, User[] users, Provider[] providers){
		UtilityResultEntry entry = new UtilityResultEntry(currentTimeInMillis, users, providers);
		utilityResult.account(entry);
		return entry;
	}
	
	/**
	 * This method calculates the costs incurred by IaaS providers in a unique period. (e.g, one time
	 * during a whole year)  
	 * @return A {@link UtilityResult}.
	 */
	public UtilityResult calculateUtility(){
		return utilityResult;
	}
}
