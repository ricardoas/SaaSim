package commons.util;

import java.io.IOException;
import java.util.Random;

public class WorkloadGenerator {
	
	public static double[] DEMANDS_IN_MILLIS = new double []{30d, 110d,  50d, 510d, 10d, 11d};
	public static double[] PROB = new double []{5.07/100, 13.15/100, 23.35/100, 40.75/100, 59.45/100, 100/100};
	
	public static void main(String[] args) throws IOException {
//		BufferedWriter writer = new BufferedWriter(new FileWriter("workload.trc"));
//		
//		int id = 0;
//		for(int i = 0; i <= 86400000; i+= 200){
////			0 4566 501208 72 13144 200 200 200 200 200
//			writer.write("0\t"+(id++)+"\t"+i+"\t"+100+"\t"+13144+"\t200\t200\t200\t200\t200\n");
//			writer.write("0\t"+(id++)+"\t"+i+"\t"+100+"\t"+13144+"\t200\t200\t200\t200\t200\n");
//			writer.write("0\t"+(id++)+"\t"+i+"\t"+100+"\t"+13144+"\t200\t200\t200\t200\t200\n");
//			writer.write("0\t"+(id++)+"\t"+i+"\t"+100+"\t"+13144+"\t200\t200\t200\t200\t200\n");
//		}
//		
//		writer.close();
		
		if(args.length != 1){
			System.err.println("usage: <number of lines>");
			System.exit(1);
		}
		
		int numOfValues = Integer.parseInt(args[0]);
		
		Random random = new Random();
		
		for(int i = 0; i < numOfValues; i++){
			double value = random.nextDouble();
			if(value <= PROB[0]){
				System.out.println(DEMANDS_IN_MILLIS[0]+"\t"+DEMANDS_IN_MILLIS[0]+"\t"+DEMANDS_IN_MILLIS[0]+"\t"+DEMANDS_IN_MILLIS[0]+"\t"+DEMANDS_IN_MILLIS[0]);
			}else if(value > PROB[0] && value < PROB[1]){
				System.out.println(DEMANDS_IN_MILLIS[1]+"\t"+DEMANDS_IN_MILLIS[1]+"\t"+DEMANDS_IN_MILLIS[1]+"\t"+DEMANDS_IN_MILLIS[1]+"\t"+DEMANDS_IN_MILLIS[1]);
			}else if(value >= PROB[1] && value < PROB[2]){
				System.out.println(DEMANDS_IN_MILLIS[2]+"\t"+DEMANDS_IN_MILLIS[2]+"\t"+DEMANDS_IN_MILLIS[2]+"\t"+DEMANDS_IN_MILLIS[2]+"\t"+DEMANDS_IN_MILLIS[2]);
			}else if(value >= PROB[2] && value < PROB[3]){
				System.out.println(DEMANDS_IN_MILLIS[3]+"\t"+DEMANDS_IN_MILLIS[3]+"\t"+DEMANDS_IN_MILLIS[3]+"\t"+DEMANDS_IN_MILLIS[3]+"\t"+DEMANDS_IN_MILLIS[3]);
			}else if(value >= PROB[3] && value < PROB[4]){
				System.out.println(DEMANDS_IN_MILLIS[4]+"\t"+DEMANDS_IN_MILLIS[4]+"\t"+DEMANDS_IN_MILLIS[4]+"\t"+DEMANDS_IN_MILLIS[4]+"\t"+DEMANDS_IN_MILLIS[4]);
			}else{
				System.out.println(DEMANDS_IN_MILLIS[5]+"\t"+DEMANDS_IN_MILLIS[5]+"\t"+DEMANDS_IN_MILLIS[5]+"\t"+DEMANDS_IN_MILLIS[5]+"\t"+DEMANDS_IN_MILLIS[5]);
			}
			
		}
	}

}
