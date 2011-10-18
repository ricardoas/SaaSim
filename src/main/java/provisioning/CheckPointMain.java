/**
 * 
 */
package provisioning;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

import commons.sim.jeevent.JEAbstractEventHandler;
import commons.sim.jeevent.JEEvent;
import commons.sim.jeevent.JEEventScheduler;

/**
 * @author Ricardo Ara&uacute;jo Santos - ricardo@lsd.ufcg.edu.br
 *
 */
public class CheckPointMain {
	
	private static class EH extends JEAbstractEventHandler{
		
		/**
		 * Default constructor.
		 */
		public EH() {
			super();
		}

		/**
		 * Default constructor.
		 */
		public EH(JEEventScheduler scheduler) {
			super(scheduler);
		}

		/**
		 * Default constructor.
		 * @param id 
		 */
		public EH(JEEventScheduler scheduler, int id) {
			super(scheduler, id);
		}

		/**
		 * {@inheritDoc}
		 */
		@Override
		public void handleEvent(JEEvent event) {
			System.out.println("CheckPointMain.EH.handleEvent()");
			System.out.println(event);
		}
		
		/**
		 * {@inheritDoc}
		 */
		@Override
		public JEEventScheduler getScheduler() {
			// TODO Auto-generated method stub
			return super.getScheduler();
		}
		
	}
	
	public static void main(String[] args) throws Exception {		
		
//		String a = new String("asdasdgasdjkfa");
//		
//		ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(new File(".test.dat")));
//		out.writeObject(a);
//		out.close();
//
//		out = new ObjectOutputStream(new FileOutputStream(new File(".test.dat.bkp")));
//		out.writeObject(a);
//		out.close();
		
		
		
		ObjectInputStream out = new ObjectInputStream(new FileInputStream(new File(".test.dat")));
		String a = (String) out.readObject();
		out.close();

		out = new ObjectInputStream(new FileInputStream(new File(".test.dat.bkp")));
		String b = (String) out.readObject();
		out.close();
		
		System.out.println(a == b);
	
		
//		JEEventScheduler.load();
		
//		System.out.println(JEEventScheduler.INSTANCE);
//		EH handler = new EH(JEEventScheduler.INSTANCE);
		
//		System.out.println(JEEventScheduler.INSTANCE.getHandler(109257626));
		
//		handler.send(new JEEvent(JEEventType.ADD_SERVER, handler , 0));
		
//		System.out.println(JEEventScheduler.INSTANCE);
		
//		JEEventScheduler.INSTANCE.start();

//		ObjectInputStream out = new ObjectInputStream(new FileInputStream(new File(".handler.dat")));
//		EH saved = (EH) out.readObject();
//		out.close();
//		
//		saved.send(new JEEvent(JEEventType.READWORKLOAD, JEEventScheduler.INSTANCE.getHandler(1458276599) , 0));
//		
//		System.out.println(JEEventScheduler.INSTANCE == saved.getScheduler());
//		
//		JEEventScheduler.save();
		

	}

}
