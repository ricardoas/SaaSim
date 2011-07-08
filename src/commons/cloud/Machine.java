package commons.cloud;

/**
 * @author Ricardo Araújo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class Machine {
	
	private long id;
	
	/**
	 * @param id
	 */
	public Machine(long id) {
		this.id = id;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Machine other = (Machine) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public void sendRequest(Request request) {
		// TODO Auto-generated method stub
		
	}
	
	
	
}
