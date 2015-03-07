import static org.junit.Assert.*;

import org.junit.Test;


public class TestCommands {

	
	@Test
	public void testOn() {
		ChronoTimer ct = new ChronoTimer();
		assertFalse("New ChronoTimer should not default to on", ct.isOn);
		assertTrue("Run group should not be set yet", ct.eventType==null);
		assertNull("Current should be null", ct.current);
		

		long timestamp = SystemTimer.getTime();
		
		
		ChronoTimer x = new ChronoTimer();
		x.readCommand(timestamp, "ON");
		assertTrue("ChronoTimer's On command didn't add 8 channels", x.channels.size()==8);
		assertTrue("ChronoTimer isn't on, but still ran throu On.execute", x.isOn);
		assertTrue("ChronoTimer's run group should be 'IND'", x.eventType.equals("IND"));
		assertNotNull("Current should not be null", x.current);
	}

	
	@Test
	public void testOff(){
		ChronoTimer t = new ChronoTimer();
		long timestamp = SystemTimer.getTime();
		t.readCommand(timestamp, "ON");
		t.readCommand(timestamp+=10000, "OFF");
			
		
		
		
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
		ChronoTimer c = new ChronoTimer();
		long timestamp = SystemTimer.getTime();
		
		c.readCommand(timestamp, "ON");

		assertFalse(c.channels.get(0).enabled);
		c.readCommand(timestamp += 1000, "START");
		assertNotNull("Current should not be null", c.current);
		assertFalse("Channel 1 should not be enabled", c.channels.get(0).enabled);
		
		c.channels.get(0).toggle();
		c.channels.get(1).toggle();
		
		assertTrue("Channel 1 should be enabled now", c.channels.get(0).enabled);
		assertTrue("Channel 2 should be enabled now", c.channels.get(1).enabled);
		
		c.readCommand(timestamp+=100, "START");
		c.current.add(111);
		c.readCommand(timestamp, "START");
		c.readCommand(timestamp+=205, "FIN");
		/*ct.readCommand(timestamp, "PRINT 1");;*/
		assertTrue("archive should have one entry", c.archive.size()==1);
		
		
	}
	

	
	@Test
	public void testToggle(){
		
	}
	

	
	@Test
	public void testTrig(){
		
	}
}
