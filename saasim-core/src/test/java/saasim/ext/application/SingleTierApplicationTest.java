/**
 * 
 */
package saasim.ext.application;

import static org.junit.Assert.fail;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import saasim.core.application.Application;
import saasim.core.application.Request;
import saasim.core.application.Tier;
import saasim.core.config.Configuration;
import saasim.core.event.EventScheduler;
import saasim.core.infrastructure.AdmissionControl;
import saasim.core.infrastructure.MonitoringService;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
@Ignore
public class SingleTierApplicationTest {
	
	@Inject
	private Application application;
	
	private MonitoringService monitor;
	private AdmissionControl control;
	private Tier tier;
	private Request request;
	private Configuration configuration;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		configuration = EasyMock.createStrictMock(Configuration.class);
		monitor = EasyMock.createStrictMock(MonitoringService.class);
		control = EasyMock.createStrictMock(AdmissionControl.class);
		tier = EasyMock.createStrictMock(Tier.class);
		request = EasyMock.createStrictMock(Request.class);

		Guice.createInjector(new AbstractModule() {
		      @Override 
		      protected void configure() {
		    	  bind(Application.class).to(TieredApplication.class);
		          bind(MonitoringService.class).toInstance(monitor);
		          bind(AdmissionControl.class).toInstance(control);
		          bind(Tier.class).toInstance(tier);
		          bind(Configuration.class).toInstance(configuration);
		          bind(EventScheduler.class);
		          bind(Request.class).toInstance(request);
		        }
		      }).injectMembers(this);
		}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		EasyMock.verify(monitor, control, tier, configuration, request);
	}

	/**
	 * Test method for {@link saasim.ext.application.TieredApplication#queue(saasim.core.application.Request)}.
	 */
	@Test
	public final void testCanQueue() {
		
		EasyMock.expect(configuration.getLong(EventScheduler.EVENT_SCHEDULER_RANDOM_SEED)).andReturn(0L).once();
		
//		monitor.requestArrived(request);
		EasyMock.expect(control.canAccept(request)).andReturn(true);
		request.setResponseListener(application);
		tier.queue(request);
		EasyMock.replay(monitor, control, tier, configuration, request);
		
		application.queue(request);
	}

	/**
	 * Test method for {@link saasim.ext.application.TieredApplication#queue(saasim.core.application.Request)}.
	 */
	@Test
	public final void testCannotQueue() {
		
		EasyMock.expect(configuration.getLong(EventScheduler.EVENT_SCHEDULER_RANDOM_SEED)).andReturn(0L).once();
		
//		monitor.requestArrived(request);
		EasyMock.expect(control.canAccept(request)).andReturn(false);
//		monitor.requestRejected(request);
		EasyMock.replay(monitor, control, tier, configuration, request);
		
		application.queue(request);
	}

	/**
	 * Test method for {@link saasim.ext.application.TieredApplication#configure(saasim.core.provisioning.TierConfiguration)}.
	 */
	@Test
	public final void testConfig() {
		fail("Not yet implemented");
	}

	/**
	 * Test method for {@link saasim.ext.application.TieredApplication#processDone(saasim.core.application.Request, saasim.core.application.Response)}.
	 */
	@Test
	public final void testProcessDone() {
		fail("Not yet implemented");
	}

}
