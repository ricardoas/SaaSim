package saasim.ext.infrastructure;

import java.util.Map;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.InstanceDescriptor;
import saasim.core.saas.Request;
import saasim.ext.saas.WebAppRequest;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Module;

public class RoundRobinMachineTest {

//	@Inject private RoundRobinMachine machine;
//	
//	private InstanceDescriptor descriptor;
//	private EventScheduler scheduler;
//	private Configuration conf;
//	
//	@Before
//	public void setUp() {
//		descriptor = EasyMock.createMock(InstanceDescriptor.class);
//		conf = EasyMock.createMock(Configuration.class);
//		scheduler = new EventScheduler(conf);
//
//		Guice.createInjector(getTestModule()).injectMembers(this);
//	}
//
//	private Module getTestModule() {
//		return new AbstractModule() {
//			@Override
//			protected void configure() {
//				bind(InstanceDescriptor.class).toInstance(descriptor);
//				bind(EventScheduler.class).toInstance(scheduler);
//				bind(Configuration.class).toInstance(conf);
//			}
//		};
//	}
//
//	@Test
//	public void test() {
//		Request request = new WebAppRequest(0, 0, 0, 0, 0, 0, new long[]{10, 900}); 
//		machine.queue(request);
//		Map<String, Double> info = machine.collect(1000, 1000);
////		Assert.equals(0.01, info.get("util"), )
//		
//	}

}
