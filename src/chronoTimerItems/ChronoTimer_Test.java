package chronoTimerItems;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import runGroups.RunGroup;


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
	public void testDefaultState(){	
		//test default states
		ChronoTimer.readCommand(0, "ON");
		assertEquals(true, ChronoTimer.isOn);
		assertEquals("IND", ChronoTimer.eventType);
		assertEquals(1, ChronoTimer.current.getRun());
		for(int i=0; i < ChronoTimer.channels.size(); i++) //test all channels are disabled on startup
		{
			assertEquals(false, ChronoTimer.channels.get(i).isEnabled());
		}
		ChronoTimer.eventLog.clear();	
	}

	@Test
	public void testReadCommandBeforeOn() {	
		// Test that commands don't work before the system is on.
		ChronoTimer.readCommand(0, "START");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TIME");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "OFF");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "CONN");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TOGGLE");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TRIG");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "FIN");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "DNF");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "PRINT");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "DISC");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "CANCEL");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "EVENT");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NEWRUN");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "ENDRUN");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "EXPORT");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "CLR");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "SWAP");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "RCL");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "RESET");
		assertEquals("Command was found in eventLog (shouldn't have been).", 0, ChronoTimer.eventLog.size());
		
		// Clear the eventLog between each case.
		//ChronoTimer.eventLog.clear();
		
		// Test that command is in eventLog.
		ChronoTimer.readCommand(0, "ON");
		assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
		ChronoTimer.eventLog.clear();
		
	/*	ChronoTimer.readCommand(0, "ON");
		assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
		ChronoTimer.readCommand(0, "OFF"); // this will clear event log and data
		assertEquals("No command found in eventLog.", 0, ChronoTimer.eventLog.size());
		ChronoTimer.eventLog.clear();

		*/
		
	}
	@Test
	public void testReadCommandOn()
	{
		// Test that command is in eventLog.
		ChronoTimer.readCommand(0, "ON");
	    assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
	    assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	   // ChronoTimer.eventLog.clear();	
		
	}
	@Test
	public void testReadCommandOnOff()
	{
		ChronoTimer.readCommand(0, "ON");
	    assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
	    assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	    ChronoTimer.readCommand(0, "OFF"); //should clear event log and all data
	  //  assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "OFF", ChronoTimer.eventLog.get(0));
	    assertEquals("Found command in eventLog when shouldnt have.", 0, ChronoTimer.eventLog.size());
	}
	@Test
	public void testReadCommandTOGGLE()
	{
		ChronoTimer.readCommand(0, "ON");
	
		ChronoTimer.readCommand(0, "TOGGLE"); //invalid parameter
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TOGGLE A"); //invalid parameter
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
	   // ChronoTimer.readCommand(0, "TOGGLE 14"); //channel number out of range
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
       
		//make sure all channels can be toggled
		ChronoTimer.readCommand(0, "TOGGLE 1"); //valid parameter
		assertEquals("No command found in eventLog.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 1", ChronoTimer.eventLog.get(1));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(0).isEnabled());
		
		ChronoTimer.readCommand(0, "TOGGLE 2");
		assertEquals("Command was found in eventLog (shouldn't have been).", 3, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 2", ChronoTimer.eventLog.get(2));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(1).isEnabled());


		ChronoTimer.readCommand(0, "TOGGLE 3");
		assertEquals("Command was found in eventLog (shouldn't have been).", 4, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 3", ChronoTimer.eventLog.get(3));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(2).isEnabled());

		ChronoTimer.readCommand(0, "TOGGLE 4");
		assertEquals("Command was found in eventLog (shouldn't have been).", 5, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 4", ChronoTimer.eventLog.get(4));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(3).isEnabled());

		ChronoTimer.readCommand(0, "TOGGLE 5");
		assertEquals("Command was found in eventLog (shouldn't have been).", 6, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 5", ChronoTimer.eventLog.get(5));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(4).isEnabled());

		ChronoTimer.readCommand(0, "TOGGLE 6");
		assertEquals("Command was found in eventLog (shouldn't have been).", 7, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 6", ChronoTimer.eventLog.get(6));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(5).isEnabled());

		ChronoTimer.readCommand(0, "TOGGLE 7");
		assertEquals("Command was found in eventLog (shouldn't have been).", 8, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 7", ChronoTimer.eventLog.get(7));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(6).isEnabled());

		ChronoTimer.readCommand(0, "TOGGLE 8");
		assertEquals("Command was found in eventLog (shouldn't have been).", 9, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 8", ChronoTimer.eventLog.get(8));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(7).isEnabled());

/*	
		ChronoTimer.readCommand(0, "TOGGLE 9");
		assertEquals("Command was found in eventLog (shouldn't have been).", 10, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 9", ChronoTimer.eventLog.get(9));

		ChronoTimer.readCommand(0, "TOGGLE 10");
		assertEquals("Command was found in eventLog (shouldn't have been).", 11, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 10", ChronoTimer.eventLog.get(10));

		ChronoTimer.readCommand(0, "TOGGLE 11");
		assertEquals("Command was found in eventLog (shouldn't have been).", 12, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 11", ChronoTimer.eventLog.get(11));

		ChronoTimer.readCommand(0, "TOGGLE 12");
		assertEquals("Command was found in eventLog (shouldn't have been).", 13, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 12", ChronoTimer.eventLog.get(12));
		*/
	}
	@Test
	public void testReadCommandEVENT()
	{
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "EVENT"); //"EVENT" alone should not be valid
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	}
	@Test 
	public void testReadCommandNUM()
	{
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "NUM");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM A");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM 1A4");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM 12A");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM B33");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
        ChronoTimer.readCommand(0, "NUM 123");
		assertEquals("Command was found in eventLog (shouldn't have been).", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "NUM 123", ChronoTimer.eventLog.get(1));
        ChronoTimer.readCommand(0, "NUM 1234");
		assertEquals("Command was found in eventLog (shouldn't have been).", 3, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "NUM 1234", ChronoTimer.eventLog.get(2));	
	}
	@Test 
	public void testReadCommandRESET()
	{
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "RESET 1");
		System.out.println(ChronoTimer.eventLog.get(ChronoTimer.eventLog.size() - 1));
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		
		ChronoTimer.readCommand(0, "RESET A");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "RESET");
		assertEquals("eventLog size does not match the size it should be.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "RESET", ChronoTimer.eventLog.get(1));	

		



	}

	
}


