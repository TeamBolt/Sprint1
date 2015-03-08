import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


public class TestCommands {

	// This
	@Before
	public void setUp() throws Exception {
		ChronoTimer.isOn = false;
		ChronoTimer.eventType = null;
		ChronoTimer.current = null;
		ChronoTimer.archive = new ArrayList<RunGroup>();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.eventLog = new ArrayList<String>();
	}
	
	@Test
	public void testOn() {
		assertFalse("New ChronoTimer should not default to on", ChronoTimer.isOn);
		assertTrue("Run group should not be set yet", ChronoTimer.eventType==null);
		assertNull("Current should be null", ChronoTimer.current);
		
		ChronoTimer.readCommand(0, "ON");
		assertTrue("ChronoTimer's On command didn't add 8 channels", ChronoTimer.channels.size()==8);
		assertTrue("ChronoTimer isn't on, but still ran throu On.execute", ChronoTimer.isOn);
		assertTrue("ChronoTimer's run group should be 'IND'", ChronoTimer.eventType.equals("IND"));
		assertNotNull("Current should not be null", ChronoTimer.current);
	}

	
	@Test
	public void testOff(){
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp+=10000, "OFF");
			
		
		
		
	}

	
	@Test
	public void testCancel(){
		
	}
	

	
	@Test
	public void testConn(){
		
	}
	
	@Test
	public void testDisc(){
		
	}
	

	
	@Test
	public void testDNF(){
		
	}
	

	
	@Test
	public void testFin(){
		
	}
	

	
	@Test
	public void testNum(){
		
	}
	

	
	@Test
	public void testPrint(){
		
	}
	

	
	@Test
	public void testStart(){
		long timestamp = SystemTimer.getTime();
		
		ChronoTimer.readCommand(timestamp, "ON");

		assertFalse(ChronoTimer.channels.get(0).enabled);
		ChronoTimer.readCommand(timestamp += 1000, "START");
		assertNotNull("Current should not be null", ChronoTimer.current);
		assertFalse("Channel 1 should not be enabled", ChronoTimer.channels.get(0).enabled);
		
		// ::NOTE::You should test the toggle command, and then use that.
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		
		assertTrue("Channel 1 should be enabled now", ChronoTimer.channels.get(0).enabled);
		assertTrue("Channel 2 should be enabled now", ChronoTimer.channels.get(1).enabled);
		
		ChronoTimer.readCommand(timestamp+=100, "START");
		
		// ::NOTE:: You should test the NUM command and then use that
		ChronoTimer.current.add(111);
		
		ChronoTimer.readCommand(timestamp, "START");
		ChronoTimer.readCommand(timestamp+=205, "FIN");
		/*ct.readCommand(timestamp, "PRINT 1");;*/
		
		// ::NOTE:: The chronotimer doesn't add RunGroups to the archive until the ENDRUN command is called.
		// assertTrue("archive should have one entry", ChronoTimer.archive.size()==1);
		
		
	}
	

	
	@Test
	public void testToggle(){
		
	}
	

	
	@Test
	public void testTrig(){
		
	}
}
