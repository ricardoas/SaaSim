package saasim.sim.components;


public class ConcurrencyCorrectionFactor implements CorrectionFactor {
	
	private final double[] values;
	private final long idleness;
	private long previousArrival;
	private int memory;

	public ConcurrencyCorrectionFactor(double[] values, long idleness) {
		this.values = values;
		this.idleness = idleness;
		this.previousArrival = 0 - (idleness + 1);
	}
	
	@Override
	public double getFactor(int concurrentThreads, long arrival) {
		
		if(concurrentThreads > 0){
			memory++;
		}else{
			if(concurrentThreads == 0 && arrival - previousArrival > idleness){
				memory = 0;
			}
		}
		
		if(memory >= values.length){
			return values[values.length-1];
		}
		
		return values[memory];
		
//		if(concurrentThreads == 0 && arrival - previousArrival < idleness){
//			previousArrival = arrival;
//			return values[1];
//		}
//
//		previousArrival = arrival;
//		return values[concurrentThreads];
	}

}
