package commons.cloud;

public class Contract {
	
	public String name;
	public double price;//in $
	public double setupCost;//in $
	public double cpuLimit;// in hours
	public double extraCpuCost;// in $/hour
	public double transferenceLimit;//in GB
	public double extraTransferenceCost;// in $/GB
	
	public Contract(String planName, Double setupCost, Double price,
			Double cpuLimit, Double extraCpuCost) {
			this.name = planName;
			this.setupCost = setupCost;
			this.price = price;
			this.cpuLimit = cpuLimit;
			this.extraCpuCost = extraCpuCost;
	}

}
