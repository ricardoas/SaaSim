package commons.cloud;

public enum IDGenerator {
	
	GENERATOR;
	
	private long nextID;
	
	private IDGenerator(){
		nextID = 0;
	}
	
	public long next(){
		return nextID++;
	}
	
}
