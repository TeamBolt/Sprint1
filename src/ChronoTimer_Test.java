import static org.junit.Assert.*;

import org.junit.Test;


public class ChronoTimer_Test {

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
	}

}
