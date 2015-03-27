import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;


public class ChronoTimer_Test {
	
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
	public void testReadCommand() {	
		// Test that commands don't work before the system is on.
		ChronoTimer.readCommand(0, "START");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		// Clear the eventLog between each case.
		ChronoTimer.eventLog.clear();
		// Test that command is in eventLog.
		ChronoTimer.readCommand(0, "ON");
		assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
		
		// Clear the eventLog between each case.
		ChronoTimer.eventLog.clear();
		
		ChronoTimer.readCommand(0, "TOGGLE");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TOGGLE A");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TOGGLE 1");
		assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 1", ChronoTimer.eventLog.get(0));
		
		ChronoTimer.eventLog.clear();	
		
	}
	@Test
	public void testDefaultState(){	
		//test default states
		ChronoTimer.readCommand(0, "ON");
		assertEquals(true, ChronoTimer.isOn);
		assertEquals("IND", ChronoTimer.eventType);
		assertEquals(1, ChronoTimer.current.getRun());
		for(int i=0; i < ChronoTimer.channels.size(); i++) //test all channels are disabled on startup
		{
			assertEquals(false, ChronoTimer.channels.get(i).enabled);
		}
		ChronoTimer.eventLog.clear();	
	}
	@Test
	public void testOffCommand(){
		
		ChronoTimer.readCommand(0, "ON");
		assertEquals(true, ChronoTimer.isOn);
		ChronoTimer.readCommand(0, "OFF");
		assertEquals(false, ChronoTimer.isOn);
		ChronoTimer.eventLog.clear();	
	}
	
	
}


