package commons.io;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;

import planning.util.MachineUsageData;

import commons.cloud.Provider;
import commons.cloud.User;
import commons.sim.components.Machine;
import commons.util.SimulationInfo;

public class Checkpointer {

	public static final String USERS_DUMP = "users.txt";
	public static final String PROVIDERS_DUMP = "providers.txt";
	public static final String MACHINES_DUMP = "machines.txt";
	public static final String SIMULATION_DUMP = "simulation.txt";
	public static final String MACHINE_DATA_DUMP = "machineData.txt";
	
	public static void dumpObjects(SimulationInfo info, User[] users, Provider[] providers,
			List<Machine> machines) throws IOException{
		
		dumpInfo(info);
		dumpUsers(users);
		dumpProviders(providers);
		dumpMachines(machines);
	}

	private static void dumpMachines(List<Machine> machines) throws IOException {
		if(machines == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(MACHINES_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		for(Machine machine : machines){
			out.writeObject(machine);
		}
		
		out.close();
		fout.close();
	}

	private static void dumpProviders(Provider[] providers) throws IOException {
		if(providers == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(PROVIDERS_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		for(Provider provider: providers){
			out.writeObject(provider);
		}
		
		out.close();
		fout.close();
	}

	private static void dumpUsers(User[] users) throws IOException {
		if(users == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(USERS_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		for(User user : users){
			out.writeObject(user);
		}
		
		out.close();
		fout.close();
	}

	private static void dumpInfo(SimulationInfo info) throws IOException {
		if(info == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(SIMULATION_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(info);
		
		out.close();
		fout.close();
	}

	public static void dumpMachineData(MachineUsageData machineData) throws IOException {
		if(machineData == null){
			return;
		}
		
		FileOutputStream fout = new FileOutputStream(new File(MACHINE_DATA_DUMP));
		ObjectOutputStream out = new ObjectOutputStream(fout);
		
		out.writeObject(machineData);
		
		out.close();
		fout.close();
	}
}
