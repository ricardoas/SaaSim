package commons.sim;

import java.io.IOException;
import java.util.List;

import provisioning.DPS;
import provisioning.DynamicallyConfigurable;
import provisioning.Monitor;
import provisioning.util.DPSFactory;

import commons.cloud.Provider;
import commons.cloud.Request;
import commons.config.SimulatorConfiguration;
import commons.io.GEISTWorkloadParser;
import commons.io.TimeBasedWorkloadParser;
import commons.io.WorkloadParser;
import commons.sim.components.LoadBalancer;
import commons.sim.components.MachineDescriptor;
import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventHandler;
import commons.sim.jeevent.JEEventScheduler;
import commons.sim.jeevent.JEEventType;
import commons.sim.jeevent.JETime;
import commons.sim.util.ApplicationFactory;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 */
public class SimpleSimulator extends JEAbstractEventHandler implements Simulator, JEEventHandler, DynamicallyConfigurable{

	private final WorkloadParser<List<Request>> workloadParser;
	private final Monitor monitor;
	protected LoadBalancer loadBalancer;
	private List<LoadBalancer> tiers;

	/**
	 * Constructor
	 * @param scheduler TODO
	 * @param list 
	 * @throws IOException 
	 */
	public SimpleSimulator(JEEventScheduler scheduler, WorkloadParser<List<Request>> parser) throws IOException {
		super(scheduler);
		this.workloadParser = parser;
		DPS dps = DPSFactory.INSTANCE.createDPS(scheduler);
		Provider cloudProvider = SimulatorConfiguration.getInstance().getProviders().values().iterator().next();
		dps.setAccountingSystem(new AccountingSystem(cloudProvider.reservationLimit, cloudProvider.onDemandLimit));
		
		this.monitor = dps;
		this.monitor.setConfigurable(this);
		this.tiers = ApplicationFactory.getInstance().createNewApplication(scheduler, getMonitor(), dps.getSetupMachines());
	}
	
	/**
	 * Constructor
	 * @param scheduler TODO
	 * @param list 
	 * @throws IOException 
	 */
	public SimpleSimulator() throws IOException {
		this(new JEEventScheduler(), new TimeBasedWorkloadParser(new GEISTWorkloadParser(), TimeBasedWorkloadParser.HOUR_IN_MILLIS));
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void start() {

		prepareBeforeStart();
		getScheduler().start();
	}

	protected void prepareBeforeStart() {
		send(new JEEvent(JEEventType.READWORKLOAD, this, getScheduler().now()));
	}

	/**
	 * @return
	 */
	public Monitor getMonitor() {
		
		return monitor;
	}

	public AccountingSystem getAccounting(){
		return ((DPS)this.monitor).getAccountingSystem();
	}
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void handleEvent(JEEvent event) {
		switch (event.getType()) {
		case READWORKLOAD:
			try {
				if (workloadParser.hasNext()) {
					List<Request> list = workloadParser.next();
					for (Request request : list) {
						send(parseEvent(request));
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}

	/**
	 * @param request
	 * @return
	 */
	protected JEEvent parseEvent(Request request) {
		return new JEEvent(JEEventType.NEWREQUEST, loadBalancer, new JETime(request.getTimeInMillis()), request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void addServer(int tier, MachineDescriptor machineDescriptor) {
		tiers.get(tier).addServer(machineDescriptor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void removeServer(int tier, MachineDescriptor machineDescriptor,
			boolean force) {
		tiers.get(tier).removeServer(machineDescriptor, force);
	}
}
