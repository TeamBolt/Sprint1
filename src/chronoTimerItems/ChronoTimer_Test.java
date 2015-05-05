package chronoTimerItems;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;

import runGroups.RunGroup;


/**
 * Test file for ChronoTimer.readCommand()
 * 
 * @author Blake Watzke
 */
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
	public void testDefaultState() {	
		// Test default states.
		ChronoTimer.readCommand(0, "ON");
		assertEquals(true, ChronoTimer.isOn);
		assertEquals("IND", ChronoTimer.eventType);
		assertEquals(1, ChronoTimer.current.getRun());
		
		// Test all channels are disabled on startup.
		for(int i=0; i < ChronoTimer.channels.size(); i++)
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

		// Test that command is in eventLog.
		ChronoTimer.readCommand(0, "ON");
		assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
		ChronoTimer.eventLog.clear();
	}
	
	@Test
	public void testReadCommandON() {
		// Test that command is in eventLog.
		ChronoTimer.readCommand(0, "ON");
	    assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
	    assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	}
	
	@Test
	public void testReadCommandOnOff() {
		ChronoTimer.readCommand(0, "ON");
	    assertEquals("No command found in eventLog.", 1, ChronoTimer.eventLog.size());
	    assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	    ChronoTimer.readCommand(0, "OFF"); // Should clear event log and all data.
	    assertEquals("Found command in eventLog when shouldnt have.", 0, ChronoTimer.eventLog.size());
	}
	
	@Test
	public void testReadCommandTOGGLE() {
		ChronoTimer.readCommand(0, "ON");
	
		ChronoTimer.readCommand(0, "TOGGLE"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TOGGLE 0"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TOGGLE -1"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TOGGLE A"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
	    ChronoTimer.readCommand(0, "TOGGLE 9"); // Channel number out of range.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
       
		// Now valid parameters.
		ChronoTimer.readCommand(0, "TOGGLE 1"); // Valid parameter.
		assertEquals("No command found in eventLog.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TOGGLE 1", ChronoTimer.eventLog.get(1));
		assertEquals("Channel was never toggled.", true, ChronoTimer.channels.get(0).isEnabled());
	}
	
	@Test
	public void testReadCommandTRIG() {
		ChronoTimer.readCommand(0, "ON");
	
		ChronoTimer.readCommand(0, "TRIG"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TRIG 0"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TRIG -1"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "TRIG A"); // Invalid parameter.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
	    ChronoTimer.readCommand(0, "TRIG 9"); // Channel number out of range.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
       
		// Now valid parameters.
		ChronoTimer.readCommand(0, "TRIG 1"); // Valid parameter.
		assertEquals("No command found in eventLog.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TRIG 1", ChronoTimer.eventLog.get(1));
	}
	
	@Test
	public void testReadCommandEVENT()
	{
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "EVENT"); // "EVENT" alone should not be valid.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
		Printer.getLog().clear();
		
		// Now valid parameters.
		ChronoTimer.readCommand(0, "EVENT IND");
		assertEquals("No command was found in eventlog.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "EVENT IND", ChronoTimer.eventLog.get(1));
		ChronoTimer.readCommand(0, "EVENT PARIND");
		assertEquals("No command was found in eventlog.", 3, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "EVENT PARIND", ChronoTimer.eventLog.get(2));
		ChronoTimer.readCommand(0, "EVENT GRP");
		assertEquals("No command was found in eventlog.", 4, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "EVENT GRP", ChronoTimer.eventLog.get(3));
		ChronoTimer.readCommand(0, "EVENT PARGRP");
		assertEquals("No command was found in eventlog.", 5, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "EVENT PARGRP", ChronoTimer.eventLog.get(4));
		
		// Make sure no errors were printed.
		assertEquals("Error was printed.", 0, Printer.getLog().size());
		
		ChronoTimer.readCommand(0, "EVENT APPLES"); // Invalid param, still in event log, but error printed.
		assertEquals("No command was found in eventlog.", 6, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "EVENT APPLES", ChronoTimer.eventLog.get(5));
		assertEquals("No error was printed.", 1, Printer.getLog().size());
		assertEquals("Wrong error printed.", "INVALID EVENT TYPE", Printer.getLog().get(0));
	}
	
	@Test 
	public void testReadCommandNUM() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "NUM");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM A");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM 1A4");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM 0");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "NUM -1");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
        
		ChronoTimer.readCommand(0, "NUM 1");
		assertEquals("Command was found in eventLog (shouldn't have been).", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "NUM 1", ChronoTimer.eventLog.get(1));
        ChronoTimer.readCommand(0, "NUM 1234");
		assertEquals("Command was found in eventLog (shouldn't have been).", 3, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "NUM 1234", ChronoTimer.eventLog.get(2));	
	}
	
	@Test 
	public void testReadCommandRESET() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "RESET 1");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		
		ChronoTimer.readCommand(0, "RESET A");
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "RESET");
		assertEquals("eventLog size does not match the size it should be.", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "RESET", ChronoTimer.eventLog.get(0));	
	}
	
	@Test
	public void testReadCommandCONN() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "CONN EYE 0"); // Test invalid parameter.
		assertEquals("No Command should ben in event log.", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "CONN EYE -1"); // Test invalid parameter.
		assertEquals("No Command should ben in event log.", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "CONN EYE 9"); // Test invalid parameter.
		assertEquals("No Command should ben in event log.", 1, ChronoTimer.eventLog.size());
		
		// Now valid.
		ChronoTimer.readCommand(0, "CONN EYE 2");
		assertEquals("Error connecting EYE sensor to channel 2", ChronoTimer.channels.get(1).getChannelNum(), 2);
		assertEquals("Incorrect command found in eventLog.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "CONN EYE 2", ChronoTimer.eventLog.get(1));
		ChronoTimer.readCommand(0, "CONN GATE 3");
		assertEquals("Error connecting GATE sensor to channel 3", ChronoTimer.channels.get(2).getChannelNum(), 3);
		assertEquals("Incorrect command found in eventLog.", 3, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "CONN GATE 3", ChronoTimer.eventLog.get(2));
		ChronoTimer.readCommand(0, "CONN PAD 4");
		assertEquals("Error connecting PAD sensor to channel 4", ChronoTimer.channels.get(3).getChannelNum(), 4);
		assertEquals("Incorrect command found in eventLog.", 4, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "CONN PAD 4", ChronoTimer.eventLog.get(3));
	}
	
	@Test
	public void testReadCommandDISC() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "DISC 0"); // Test invalid parameter.
		assertEquals("Incorrect command found in eventLog.", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "DISC -1"); // Test invalid parameter.
		assertEquals("Incorrect command found in eventLog.", 1, ChronoTimer.eventLog.size());
		ChronoTimer.readCommand(0, "DISC 9"); // Test invalid parameter.
		assertEquals("Incorrect command found in eventLog.", 1, ChronoTimer.eventLog.size());
		
		ChronoTimer.readCommand(0, "DISC 1"); // Test valid parameter.
		assertEquals("Incorrect command found in eventLog.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "DISC 1", ChronoTimer.eventLog.get(1));
	}
	
	@Test
	public void testReadCommandNEWRUN() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "NEWRUN");
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "NEWRUN", ChronoTimer.eventLog.get(1));
	}

	@Test 
	public void testReadCommandENDRUN() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "ENDRUN");
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ENDRUN", ChronoTimer.eventLog.get(1));
	}
	
	@Test
	public void testReadCommandCANCEL() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "CANCEL");
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "CANCEL", ChronoTimer.eventLog.get(1));
	}
	
	@Test
	public void testReadCommandDNF() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "DNF");
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "DNF", ChronoTimer.eventLog.get(1));
	}
	
	@Test
	public void testReadCommandSTART() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "START");
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "START", ChronoTimer.eventLog.get(1));
	}
	
	@Test
	public void testReadCommandFIN() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "FIN");
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "FIN", ChronoTimer.eventLog.get(1));
	}
	
	@Test
	public void testReadCommandCLR() {
	   ChronoTimer.readCommand(0, "ON");
	   ChronoTimer.readCommand(0, "CLR"); // Invalid, parameter missing.
	   assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
	   assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	   ChronoTimer.readCommand(0, "CLR 0"); // Invalid parameter.
	   assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
	   assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	   ChronoTimer.readCommand(0, "CLR -1"); // Invalid parameter.
	   assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
	   assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
	   ChronoTimer.readCommand(0, "CLR 1"); // Valid.
	   assertEquals("Command was not found in event log.", 2, ChronoTimer.eventLog.size());
	   assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "CLR 1", ChronoTimer.eventLog.get(1));
	}
	
	@Test 
	public void testReadCommandSWAP() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "SWAP");
		assertEquals("Command was not found in event log.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "SWAP", ChronoTimer.eventLog.get(1));  	
	}
	
	@Test 
	public void testReadCommandRCL() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "RCL");
		assertEquals("Command was found in eventLog when shouldnt have been.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "RCL", ChronoTimer.eventLog.get(1));  	
	}
	
	@Test 
	public void testReadCommandPRINT() {
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "NUM 1");
		ChronoTimer.readCommand(0, "ENDRUN");
		ChronoTimer.readCommand(0, "NEWRUN");
		ChronoTimer.readCommand(0, "NUM 2");
		
		// The print command as always valid. If no param, or a bad param is given, it tries to print the current run.
		Printer.getLog().clear();
		ChronoTimer.readCommand(0, "PRINT");
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "PRINT", ChronoTimer.eventLog.get(5));
		assertEquals("Incorrect command found in eventLog.", "RUN      BIB      TIME	    Individual" + "\n" + "2        2      WAITING\n", Printer.getLog().get(0));
		
		Printer.getLog().clear();
		ChronoTimer.readCommand(0, "PRINT A");
		assertEquals("Incorrect command found in eventLog.", "RUN      BIB      TIME	    Individual" + "\n" + "2        2      WAITING\n", Printer.getLog().get(0));

		Printer.getLog().clear();
		ChronoTimer.readCommand(0, "PRINT 0");
		assertEquals("Incorrect command found in eventLog.", "RUN      BIB      TIME	    Individual" + "\n" + "2        2      WAITING\n", Printer.getLog().get(0));
		
		Printer.getLog().clear();
		ChronoTimer.readCommand(0, "PRINT -1");
		assertEquals("Incorrect command found in eventLog.", "RUN      BIB      TIME	    Individual" + "\n" + "2        2      WAITING\n", Printer.getLog().get(0));
	
		Printer.getLog().clear();
		ChronoTimer.readCommand(0, "PRINT 2");
		assertEquals("Incorrect command found in eventLog.", "RUN      BIB      TIME	    Individual" + "\n" + "2        2      WAITING\n", Printer.getLog().get(0));
		
		Printer.getLog().clear();
		ChronoTimer.readCommand(0, "PRINT 1");
		assertEquals("Incorrect command found in eventLog.", "RUN      BIB      TIME	    Individual" + "\n" + "1        1      DNF\n", Printer.getLog().get(0));
	}
	
	@Test
	public void testReadCommandTIME()
	{
		ChronoTimer.readCommand(0, "ON");
		ChronoTimer.readCommand(0, "TIME"); // "EVENT" alone should not be valid.
		assertEquals("Command was found in eventLog (shouldn't have been).", 1, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "ON", ChronoTimer.eventLog.get(0));
		Printer.getLog().clear();
		
		// Now valid parameters.
		ChronoTimer.readCommand(0, "TIME 00:00:00");
		assertEquals("No command was found in eventlog.", 2, ChronoTimer.eventLog.size());
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TIME 00:00:00", ChronoTimer.eventLog.get(1));
		
		// Make sure no errors were printed.
		assertEquals("Error was printed.", 0, Printer.getLog().size());
		
		ChronoTimer.readCommand(0, "TIME APPLES"); // Invalid param, still in event log, but error printed.
		assertEquals("Incorrect command found in eventLog.", "18:00:00.0" + "	" + "TIME APPLES", ChronoTimer.eventLog.get(2));
		assertEquals("No error was printed.", 1, Printer.getLog().size());
		assertEquals("Wrong error printed.", "Invalid time entered.", Printer.getLog().get(0));
	}
}