package chronoTimerItems;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Before;
import org.junit.Test;

import runGroups.Run;
import runGroups.RunGroup;


/**
 * This is the test file for the system as a whole. Whenever possible we should use readCommand to
 * make changes to the system, and only do so with commands which have already been tested.
 * 
 * @author Ben Kingsbury
 */
public class TestCommands {

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
		assertTrue("Run group should not be set yet", ChronoTimer.eventType == null);
		assertNull("Current should be null", ChronoTimer.current);
		
		ChronoTimer.readCommand(0, "ON");
		assertTrue("ChronoTimer's On command didn't add 8 channels", ChronoTimer.channels.size() == 8);
		assertTrue("ChronoTimer isn't on, but still ran through On.execute", ChronoTimer.isOn);
		assertTrue("ChronoTimer's run group should be 'IND'", ChronoTimer.eventType.equals("IND"));
		assertNotNull("Current should be empty, not null", ChronoTimer.current);
	}
	
	@Test
	public void testOff(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		
		// Turn On.  Add channels.  Enable Channels.  Turn Off.  Test Channels - if disabled.
		ChronoTimer.readCommand(timestamp+100, "ON");
		ChronoTimer.channels.get(1).toggle();
		ChronoTimer.channels.get(2).toggle();
		ChronoTimer.readCommand(timestamp+100, "CONN " + timestamp + " EYE 2");
		ChronoTimer.readCommand(timestamp+1000, "OFF");
		
		assertFalse("ChronoTimer still flagged as on", ChronoTimer.isOn);
		assertTrue("Channel array should be 0", ChronoTimer.channels.size() == 0);

		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp+=10000, "OFF");
		
		// Timer is Off.		
		assertFalse(ChronoTimer.isOn);
		
		// Test archive, channels, and eventlog.
		assertTrue("Archive should be empty when off", ChronoTimer.archive.isEmpty());
		assertTrue("Channels should be empty when off", ChronoTimer.channels.isEmpty());
		assertTrue("Eventlog should be empty when off", ChronoTimer.eventLog.isEmpty());
		
		// Test a few commands to ensure they aren't doing anything.
		
		// Try Connecting a Channel.
		ChronoTimer.readCommand(timestamp + 100, "CONN GATE 1");
		assertTrue(ChronoTimer.channels.isEmpty());
		
		// Try starting a NewRun.
		ChronoTimer.readCommand(timestamp + 1000, "NEWRUN");
		assertNull("Current should still be null - system is off",ChronoTimer.current);
		
		// Try adding a Runner.
		ChronoTimer.readCommand(timestamp, "NUM 111");
		assertNull("Current should be null", ChronoTimer.current);
		
		// Try EndRun.
		ChronoTimer.readCommand(timestamp+1000, "ENDRUN");
		assertTrue("Archive Should be Empty", ChronoTimer.archive.isEmpty());
		
		// Turn on, start race, add runners, then turn off. See what happens. 
		// See if it lets me, see if i can call FIN or CANCEL after it is turned off.
		
		ChronoTimer.readCommand(timestamp, "ON");
		assertTrue(ChronoTimer.isOn);
		
		ChronoTimer.channels.get(1).toggle();
		ChronoTimer.channels.get(2).toggle();		
		ChronoTimer.readCommand(timestamp+10, "NEWRUN");
		assertEquals("Current Run should be 1", ChronoTimer.current.getRun(), 1);
		
		ChronoTimer.readCommand(timestamp + 100, "NUM 111");
		ChronoTimer.readCommand(timestamp + 100, "NUM 222");
		ChronoTimer.readCommand(timestamp + 100, "START");
		ChronoTimer.readCommand(timestamp, "START");
		ChronoTimer.readCommand(timestamp + 10, "OFF");
		ChronoTimer.readCommand(timestamp, "FIN");
		ChronoTimer.readCommand(timestamp, "FIN");
		ChronoTimer.readCommand(timestamp, "ENDRUN");
		assertTrue("Archive Should be cleared", ChronoTimer.archive.isEmpty());
		assertNull("Current should be null when off", ChronoTimer.current);
	}

	@Test
	public void testCancel(){
		// Test cancel before start.
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp + 100, "CANCEL");
		
		// Test Cancel in actual Race.
		ChronoTimer.readCommand(timestamp + 100, "OFF");
		ChronoTimer.readCommand(timestamp + 100, "ON");
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		ChronoTimer.readCommand(timestamp, "NEWRUN");
		ChronoTimer.readCommand(timestamp, "NUM 111");
		assertEquals(1, ChronoTimer.current.getStartQueue().size());
		
		// Start racer and check size.
		ChronoTimer.readCommand(timestamp + 100, "START");
		assertEquals(0, ChronoTimer.current.getStartQueue().size());
		
		// Cancel racer - check if added back to startqueue.
		ChronoTimer.readCommand(timestamp + 100, "CANCEL");
		assertEquals(1, ChronoTimer.current.getStartQueue().size());
		
		// Start and finish race.
		ChronoTimer.readCommand(timestamp + 100, "START");
		assertEquals(0, ChronoTimer.current.getStartQueue().size());
		assertEquals(1, ChronoTimer.current.getFinishQueue().size());
		ChronoTimer.readCommand(timestamp + 1000, "FIN");
	}
	
	@Test
	public void testConn(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp + 10, "CONN EYE 1");
		assertEquals("EYE", ChronoTimer.channels.get(0).getSensor().type);
		
		ChronoTimer.readCommand(timestamp, "CONN GATE 1");
		ChronoTimer.readCommand(timestamp, "CONN GATE 2");
		assertEquals("GATE", ChronoTimer.channels.get(1).getSensor().type);
		
		ChronoTimer.readCommand(timestamp, "CONN GATE 10");
		assertEquals("Invalid Command Entered.", Printer.log.get(Printer.log.size()-1));
	}
	
	@Test
	public void testDisc(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp + 10, "CONN EYE 1");
		ChronoTimer.readCommand(timestamp, "CONN GATE 2");
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
				
		ChronoTimer.readCommand(timestamp+100, "DISC 1");
		assertNull(ChronoTimer.channels.get(0).getSensor());
		assertNotNull(ChronoTimer.channels.get(1).getSensor());
		
		ChronoTimer.readCommand(timestamp + 100, "CONN GATE 1");
		ChronoTimer.readCommand(timestamp, "DISC 2");
		assertNotNull(ChronoTimer.channels.get(0).getSensor());
		assertNull(ChronoTimer.channels.get(1).getSensor());
	}
	
	@Test
	public void testDNF(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		ChronoTimer.readCommand(timestamp += 100, "NUM 357");
		
		// Test when finish queue is empty.
		assertEquals(ChronoTimer.current.getFinishQueue().size(), 0);
		assertTrue(Printer.log.isEmpty());
		assertEquals(1,ChronoTimer.current.getStartQueue().size());
		
		// Test normally.
		ChronoTimer.readCommand(timestamp += 1000, "START");
		ChronoTimer.readCommand(timestamp += 2580, "DNF");
		assertEquals("Bib #357 Did Not Finish", Printer.log.get(Printer.log.size() - 1));		
	}
	
	@Test
	public void testFin(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		ChronoTimer.readCommand(timestamp += 100, "NUM 777");

		// Test FIN before starting race.
		ChronoTimer.readCommand(timestamp, "FIN");
		assertEquals(ChronoTimer.current.getFinishQueue().size(), 0);
		assertTrue(Printer.log.isEmpty());
		assertEquals(1,ChronoTimer.current.getStartQueue().size());
		
		// Test conditions - if c <> 2.
		ChronoTimer.current.trigger(3, timestamp += 5678);
		assertTrue(Printer.log.isEmpty());
		ChronoTimer.readCommand(timestamp += 5477, "START");
		ChronoTimer.current.trigger(3, timestamp + 6666);
		assertEquals("Bib #777 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		assertEquals(1, ChronoTimer.current.getFinishQueue().size());
		ChronoTimer.readCommand(timestamp += 55, "FIN");
		
		// Test conditions - empty finishqueue.
		ChronoTimer.readCommand(timestamp, "NUM 444");
		ChronoTimer.readCommand(timestamp += 65648, "START");
		ChronoTimer.readCommand(timestamp += 555, "DNF");
		assertEquals(0, ChronoTimer.current.getFinishQueue().size());
		ChronoTimer.current.trigger(2, timestamp);
		assertEquals("Bib #444 Did Not Finish", Printer.log.get(Printer.log.size() - 1));
		
		// Test normally.
		ChronoTimer.readCommand(timestamp, "NUM 999");
		ChronoTimer.readCommand(timestamp += 100, "START");
		ChronoTimer.readCommand(timestamp += 2500, "FIN");
		assertEquals("Bib #999 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));			
	}
	
	@Test
	public void testNum(){
		// Call the add num command without starting a newrun. Shouldn't let me do it.
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp, "ENDRUN");
		ChronoTimer.readCommand(timestamp + 100, "NUM 999");
		assertNull("'current' should be null", ChronoTimer.current);
		assertSame("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size() - 1));
		
		// Start a newrun. Add num - should work.
		ChronoTimer.readCommand(timestamp, "NEWRUN");
		ChronoTimer.readCommand(timestamp + 100, "NUM 888");
		assertNotNull(ChronoTimer.current);
		assertEquals("ChronoTimer should only have 1 racer", 1, ChronoTimer.current.getStartQueue().size());
		
		ChronoTimer.readCommand(timestamp + 100, "NUM 999");
		assertEquals(2, ChronoTimer.current.getStartQueue().size());
		assertEquals(0, ChronoTimer.current.getFinishQueue().size());
	}
	
	@Test
	public void testPrint(){
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp, "ON");
		
		// Test runnum == 0 and current == null.
		ChronoTimer.current = null;
		ChronoTimer.readCommand(timestamp += 100, "PRINT");
		assertEquals("No Run to print.", Printer.log.get(Printer.log.size() - 1));		
		
		//Test Adding runner then Printing
		ChronoTimer.readCommand(timestamp, "NEWRUN");
		ChronoTimer.readCommand(timestamp += 100, "NUM 481");
		assertNotNull(ChronoTimer.current);
		ChronoTimer.readCommand(timestamp += 100, "PRINT");
		assertEquals("RUN      BIB      TIME	    Individual" + "\n" + "1        481      WAITING" + "\n", Printer.log.get(Printer.log.size() - 1));	
	}
	
	@Test
	public void testStart(){
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		
		ChronoTimer.readCommand(timestamp, "ON");
		assertFalse(ChronoTimer.channels.get(0).isEnabled());
		assertFalse(ChronoTimer.channels.get(7).isEnabled());
		
		// Testing when current is null.
		ChronoTimer.readCommand(timestamp, "ENDRUN");
		ChronoTimer.readCommand(timestamp += 1000, "START");
		assertNull("Current should be null", ChronoTimer.current);
		assertSame("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size() - 1));

		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		assertTrue("Channel 1 should be enabled now", ChronoTimer.channels.get(0).isEnabled());
		assertTrue("Channel 2 should be enabled now", ChronoTimer.channels.get(1).isEnabled());
		
		ChronoTimer.readCommand(timestamp += 100, "START");
		assertNull(ChronoTimer.current);
		
		// Testing when current isn't null.
		ChronoTimer.readCommand(timestamp += 100, "NEWRUN");
		ChronoTimer.readCommand(timestamp += 100, "NUM 877");
		ChronoTimer.readCommand(timestamp, "START");
		assertEquals("Bib #877 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp+=205, "FIN");
		assertEquals("Bib #877 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp+=100, "ENDRUN");	
		
		// Testing start command twice - with one runner.
		ChronoTimer.readCommand(timestamp += 100, "NEWRUN");
		ChronoTimer.readCommand(timestamp += 100, "NUM 555");
		ChronoTimer.readCommand(timestamp, "START");
		assertEquals("Bib #555 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp, "START");
		//should test to make sure the start time is the same as the original.
		ChronoTimer.readCommand(timestamp += 1000, "FIN");
		assertEquals("Bib #555 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
	}
	
	@Test
	public void testToggle(){
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp, "ON");
		
		assertFalse(ChronoTimer.channels.get(0).isEnabled());
		assertFalse(ChronoTimer.channels.get(7).isEnabled());
		
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 8");
		assertTrue(ChronoTimer.channels.get(0).isEnabled());
		assertTrue(ChronoTimer.channels.get(7).isEnabled());
		assertFalse(ChronoTimer.channels.get(2).isEnabled());
		assertFalse(ChronoTimer.channels.get(5).isEnabled());
		
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 6");
		assertFalse(ChronoTimer.channels.get(0).isEnabled());
		assertTrue(ChronoTimer.channels.get(5).isEnabled());
		
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		assertFalse(ChronoTimer.channels.get(1).isEnabled());
	}
	
	@Test
	public void testTrigStart(){
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		
		ChronoTimer.readCommand(timestamp, "ON");
		assertFalse(ChronoTimer.channels.get(0).isEnabled());
		assertFalse(ChronoTimer.channels.get(7).isEnabled());
		
		// Testing when current is null.
		ChronoTimer.readCommand(timestamp, "ENDRUN");
		ChronoTimer.readCommand(timestamp += 1000, "TRIG 1");
		assertNull("Current should be null", ChronoTimer.current);
		assertSame("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size() - 1));

		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		assertTrue("Channel 1 should be enabled now", ChronoTimer.channels.get(0).isEnabled());
		assertTrue("Channel 2 should be enabled now", ChronoTimer.channels.get(1).isEnabled());
		
		ChronoTimer.readCommand(timestamp += 100, "TRIG 1");
		assertNull(ChronoTimer.current);
		
		// Testing when current isn't null.
		ChronoTimer.readCommand(timestamp += 100, "NEWRUN");
		ChronoTimer.readCommand(timestamp += 100, "NUM 877");
		ChronoTimer.readCommand(timestamp, "TRIG 1");
		assertEquals("Bib #877 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp += 205, "FIN");
		assertEquals("Bib #877 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp += 100, "ENDRUN");
		
		// Testing start command twice - with one runner.
		ChronoTimer.readCommand(timestamp += 100, "NEWRUN");
		ChronoTimer.readCommand(timestamp += 100, "NUM 555");
		ChronoTimer.readCommand(timestamp, "TRIG 1");
		assertEquals("Bib #555 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp, "TRIG 1");
		ChronoTimer.readCommand(timestamp += 1000, "FIN");
		assertEquals("Bib #555 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
	}
	
	@Test
	public void testTrigFin(){
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp , "ON");
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		ChronoTimer.readCommand(timestamp += 100, "NUM 777");

		// Test FIN before starting race - Trig 2.
		ChronoTimer.readCommand(timestamp, "TRIG 2");
		assertEquals(ChronoTimer.current.getFinishQueue().size(), 0);
		assertTrue(Printer.log.isEmpty());
		assertEquals(1,ChronoTimer.current.getStartQueue().size());
		
		// Test conditions - if c <> 2.
		ChronoTimer.current.trigger(3, timestamp += 5678);
		assertTrue(Printer.log.isEmpty());
		ChronoTimer.readCommand(timestamp += 5477, "START");
		ChronoTimer.current.trigger(3, timestamp + 6666);
		assertEquals("Bib #777 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));
		assertEquals(1, ChronoTimer.current.getFinishQueue().size());
		ChronoTimer.readCommand(timestamp += 55, "TRIG 2");
		
		// Test conditions - empty finishqueue.
		ChronoTimer.readCommand(timestamp, "NUM 444");
		ChronoTimer.readCommand(timestamp += 65648, "START");
		ChronoTimer.readCommand(timestamp += 555, "DNF");
		assertEquals(0, ChronoTimer.current.getFinishQueue().size());
		ChronoTimer.current.trigger(2, timestamp);
		assertEquals("Bib #444 Did Not Finish", Printer.log.get(Printer.log.size() - 1));
		
		// Test normally.
		ChronoTimer.readCommand(timestamp, "NUM 999");
		ChronoTimer.readCommand(timestamp += 100, "START");
		ChronoTimer.readCommand(timestamp += 2500, "TRIG 2");
		assertEquals("Bib #999 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size() - 1));	
	}
	
	@Test
	public void testSwapOneRunner(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add one runner.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		
		// Call swap before starting.
		ChronoTimer.readCommand(timestamp, "SWAP");
		assertEquals("No Run in progress.  A run must be started first.", Printer.log.get(Printer.log.size() - 1));
		
		// Start race and call swap.
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp, "SWAP");
		assertEquals("Not enough runners to swap.  Please add another runner", Printer.log.get(Printer.log.size() - 1));
		assertEquals(100, ChronoTimer.getCurrent().getFinishQueue().peek().getBibNum());
	}
	
	@Test
	public void testSwapTwoRunners(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add two runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		
		// Test the two runners are in correct order (100, 200).
		LinkedBlockingQueue<Run> s = ChronoTimer.getCurrent().getStartQueue();
		assertEquals(100, s.poll().getBibNum());
		assertEquals(200, s.poll().getBibNum());
		
		// Start race and call Swap.
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp, "SWAP");
		assertEquals("Not enough runners to swap.  Please add another runner", Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp, "START");
		ChronoTimer.readCommand(timestamp, "SWAP");
		
		// Test the order (200, 100).
		LinkedBlockingQueue<Run> f = ChronoTimer.getCurrent().getFinishQueue();
		assertEquals(200, f.poll().getBibNum());
		assertEquals(100, f.poll().getBibNum());			
	}
	
	@Test
	public void testSwapMoreRunners(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
				
		// Add four runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		ChronoTimer.readCommand(timestamp, "NUM 300");
		ChronoTimer.readCommand(timestamp, "NUM 400");
		
		// Check for proper Order (100, 200, 300, 400).
		LinkedBlockingQueue<Run> s = ChronoTimer.getCurrent().getStartQueue();
		assertEquals(100, s.poll().getBibNum());
		assertEquals(200, s.poll().getBibNum());
		assertEquals(300, s.poll().getBibNum());
		assertEquals(400, s.poll().getBibNum());
		
		// Start all four and swap first two.
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		
		// Check for proper Order (200, 100, 300, 400).
		LinkedBlockingQueue<Run> c = ChronoTimer.getCurrent().getFinishQueue();
		assertEquals(200, c.poll().getBibNum());
		assertEquals(100, c.poll().getBibNum());
		assertEquals(300, c.poll().getBibNum());
		assertEquals(400, c.poll().getBibNum());
	}
	
	@Test
	public void testSwapNoRunners(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Test swap command.
		ChronoTimer.readCommand(timestamp, "SWAP");
		assertEquals("No Run in progress.  A run must be started first.", Printer.log.get(Printer.log.size() - 1));
	}
	
	@Test
	public void testSwapMultipleTimes(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add four runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		ChronoTimer.readCommand(timestamp, "NUM 300");
		ChronoTimer.readCommand(timestamp, "NUM 400");

		// Start all four and swap first two.
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		
		// Swap again and test order (100, 200, 300, 400).
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		LinkedBlockingQueue<Run> c = ChronoTimer.getCurrent().getFinishQueue();
		assertEquals(100, c.poll().getBibNum());
		assertEquals(200, c.poll().getBibNum());
		assertEquals(300, c.poll().getBibNum());
		assertEquals(400, c.poll().getBibNum());
		
		// Finish first racer and swap again - then test order (300, 200, 400).
		ChronoTimer.readCommand(timestamp += 1220, "FIN");
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		LinkedBlockingQueue<Run> f = ChronoTimer.getCurrent().getFinishQueue();
		assertEquals(300, f.poll().getBibNum());
		assertEquals(200, f.poll().getBibNum());
		assertEquals(400, f.poll().getBibNum());
		
		// Cancel next racer and swap again - then test order (200, 300).
		ChronoTimer.readCommand(timestamp += 1220, "CANCEL");
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		LinkedBlockingQueue<Run> x = ChronoTimer.getCurrent().getFinishQueue();
		assertEquals(200, x.poll().getBibNum());
		assertEquals(300, x.poll().getBibNum());
		
		// Start racer 400 again and swap - then test order (300, 200, 400).
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		LinkedBlockingQueue<Run> q = ChronoTimer.getCurrent().getFinishQueue();
		assertEquals(300, q.poll().getBibNum());
		assertEquals(200, q.poll().getBibNum());
		assertEquals(400, q.poll().getBibNum());
		
		// Swap one more time - then test order (200, 300, 400).
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		LinkedBlockingQueue<Run> l = ChronoTimer.getCurrent().getFinishQueue();
		assertEquals(200, l.poll().getBibNum());
		assertEquals(300, l.poll().getBibNum());
		assertEquals(400, l.poll().getBibNum());
	}
	
	@Test
	public void testSwapStatuses(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add two runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		
		// Swap before race starts.
		ChronoTimer.readCommand(timestamp, "SWAP");
		assertEquals("No Run in progress.  A run must be started first.", Printer.log.get(Printer.log.size() - 1));
		
		// Start one, then try to swap.
		ChronoTimer.readCommand(timestamp, "START");
		ChronoTimer.readCommand(timestamp, "SWAP");
		assertEquals("Not enough runners to swap.  Please add another runner", Printer.log.get(Printer.log.size() - 1));
		
		// Swap after race ends.
		ChronoTimer.readCommand(timestamp += 20000, "START");
		ChronoTimer.readCommand(timestamp += 20000, "START");
		ChronoTimer.readCommand(timestamp += 25035, "FIN");
		ChronoTimer.readCommand(timestamp += 25035, "FIN");
		ChronoTimer.readCommand(timestamp += 25035, "SWAP");
		assertEquals("No Run in progress.  A run must be started first.", Printer.log.get(Printer.log.size() - 1));
	}
	
	@Test
	public void testSwapParInd(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT PARIND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add and start two runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");

		// Try to swap.
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		assertEquals("Swap command does not apply to parallel events.", Printer.log.get(Printer.log.size() - 1));
	}
	
	@Test
	public void testSwapParGrp(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT PARGRP");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add and start two runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		ChronoTimer.readCommand(timestamp += 1220, "START");
		ChronoTimer.readCommand(timestamp += 1220, "START");

		// Try to swap.
		ChronoTimer.readCommand(timestamp += 1220, "SWAP");
		assertEquals("Swap command does not apply to parallel events.", Printer.log.get(Printer.log.size() - 1));
	}
	
	@Test
	public void testClr(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add a few runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		ChronoTimer.readCommand(timestamp, "NUM 300");
		ChronoTimer.readCommand(timestamp, "NUM 400");

		// Clear out last entry -  test order.
		ChronoTimer.readCommand(timestamp, "CLR 400");
		LinkedBlockingQueue<Run> s = ChronoTimer.getCurrent().getStartQueue();
		assertEquals(100, s.poll().getBibNum());
		assertEquals(200, s.poll().getBibNum());
		assertEquals(300, s.poll().getBibNum());
		
		// Add another racer - test order.
		ChronoTimer.readCommand(timestamp, "NUM 500");
		assertEquals(4, ChronoTimer.getCurrent().getStartQueue().size());
		LinkedBlockingQueue<Run> x = ChronoTimer.getCurrent().getStartQueue();
		assertEquals(100, x.poll().getBibNum());
		assertEquals(200, x.poll().getBibNum());
		assertEquals(300, x.poll().getBibNum());
		assertEquals(500, x.poll().getBibNum());
		
		// Clear out first entry - test order.
		ChronoTimer.readCommand(timestamp, "CLR 100");
		LinkedBlockingQueue<Run> r = ChronoTimer.getCurrent().getStartQueue();
		assertEquals(200, r.poll().getBibNum());
		assertEquals(300, r.poll().getBibNum());
		assertEquals(500, r.poll().getBibNum());
		
		// Clear out middle entry - test order.
		ChronoTimer.readCommand(timestamp, "CLR 300");
		LinkedBlockingQueue<Run> q = ChronoTimer.getCurrent().getStartQueue();
		assertEquals(200, q.poll().getBibNum());
		assertEquals(500, q.poll().getBibNum());
		
		// Clear out non-existing Bib.
		ChronoTimer.readCommand(timestamp, "CLR 999");
		assertEquals("Bib number not found.", Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp, "CLR 300");
		assertEquals("Bib number not found.", Printer.log.get(Printer.log.size() - 1));
			
	}
	
	@Test
	public void testClrInRace(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add a few runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		ChronoTimer.readCommand(timestamp, "NUM 300");
		ChronoTimer.readCommand(timestamp, "NUM 400");
		
		// Start first runner and try to clear him.
		ChronoTimer.readCommand(timestamp += 2000, "START");
		ChronoTimer.readCommand(timestamp += 2000, "CLR 100");
		assertEquals("This runner is already in the race and can't be removed.", Printer.log.get(Printer.log.size() - 1));
		
		// Clear another that hasn't started yet - should let me.
		ChronoTimer.readCommand(timestamp, "CLR 200");
		assertEquals(2, ChronoTimer.getCurrent().getStartQueue().size());
		LinkedBlockingQueue<Run> c = ChronoTimer.getCurrent().getStartQueue();
		assertEquals(300, c.poll().getBibNum());
		assertEquals(400, c.poll().getBibNum());
		
		// Start next runner and try again to cancel bib 100.
		ChronoTimer.readCommand(timestamp += 2000, "START");
		ChronoTimer.readCommand(timestamp += 2000, "CLR 100");
		assertEquals("This runner is already in the race and can't be removed.", Printer.log.get(Printer.log.size() - 1));
		
		// Clear second runner (300).
		ChronoTimer.readCommand(timestamp += 2000, "CLR 300");
		assertEquals("This runner is already in the race and can't be removed.", Printer.log.get(Printer.log.size() - 1));
		
		// Cancel second runner (300) and then clear him.
		ChronoTimer.readCommand(timestamp, "CANCEL");
		ChronoTimer.readCommand(timestamp += 2000, "CLR 300");
		assertEquals(1, ChronoTimer.getCurrent().getStartQueue().size());
		
		// Clear last runner.
		ChronoTimer.readCommand(timestamp += 2000, "CLR 400");
		assertEquals(0, ChronoTimer.getCurrent().getStartQueue().size());
	}
	
	@Test
	public void testClrFinishedRace(){
		// Turn system on.
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		ChronoTimer.readCommand(timestamp, "EVENT IND");
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		
		// Add a few runners.
		ChronoTimer.readCommand(timestamp, "NUM 100");
		ChronoTimer.readCommand(timestamp, "NUM 200");
		ChronoTimer.readCommand(timestamp, "NUM 300");
		ChronoTimer.readCommand(timestamp, "NUM 400");
		
		// Start first two runners.
		ChronoTimer.readCommand(timestamp += 2000, "START");
		ChronoTimer.readCommand(timestamp += 2000, "START");

		// Finish first runner - now have 100 finished, 200 in race, 300 waiting, 400 waiting.
		ChronoTimer.readCommand(timestamp += 2000, "FIN");

		// Clear bib 100 (finished) - shouldn't let me.
		ChronoTimer.readCommand(timestamp += 2000, "CLR 100");
		assertEquals("This runner has already finished this race and can't be removed.", Printer.log.get(Printer.log.size() - 1));
		
		// Clear bib 200 (running) - shouldn't let me.
		ChronoTimer.readCommand(timestamp += 2000, "CLR 200");
		assertEquals("This runner is already in the race and can't be removed.", Printer.log.get(Printer.log.size() - 1));
		ChronoTimer.readCommand(timestamp += 2000, "FIN");
		
		// Clear bib 300 (waiting) - should let me.
		ChronoTimer.readCommand(timestamp += 2000, "CLR 300");
		assertEquals(1, ChronoTimer.getCurrent().getStartQueue().size());
		
		// Start bib 400, DNF, Clear him - shouldn't let me.
		ChronoTimer.readCommand(timestamp += 2000, "START");
		ChronoTimer.readCommand(timestamp += 2000, "DNF");
		ChronoTimer.readCommand(timestamp += 2000, "CLR 400");
		assertEquals("This runner has already finished this race and can't be removed.", Printer.log.get(Printer.log.size() - 1));
		
		// Test completed runs to see if we have right order (100, 200, 400).
		LinkedBlockingQueue<Run> c = ChronoTimer.getCurrent().getCompletedRuns();
		assertEquals(100, c.poll().getBibNum());
		assertEquals(200, c.poll().getBibNum());
		assertEquals(400, c.poll().getBibNum());
	}
}