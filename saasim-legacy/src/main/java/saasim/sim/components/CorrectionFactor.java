package saasim.sim.components;

public interface CorrectionFactor {
	
	double getFactor(int concurrentThreads, long arrival);

}
