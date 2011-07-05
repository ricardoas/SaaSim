package commons.cloud;

public class Resource {

	public double startTime;
	public double endTime;
	
	public Resource(double startTime, double endTime){
		this.startTime = startTime;
		this.endTime = endTime;
	}
	
	/**
	 * This method returns the execution time of this machine in hours!
	 * @return
	 */
	public double calcExecutionTime() {
		double executionTime = this.endTime - this.startTime;
		if(executionTime < 0){
			throw new RuntimeException("Invalid resource "+this.toString()+" execution time: "+executionTime);
		}
		return executionTime;
	}
	
}
