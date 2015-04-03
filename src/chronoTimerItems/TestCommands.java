package chronoTimerItems;
import static org.junit.Assert.*;

import java.util.ArrayList;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import runGroups.RunGroup;


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
		assertTrue("ChronoTimer isn't on, but still ran through On.execute", ChronoTimer.isOn);
		assertTrue("ChronoTimer's run group should be 'IND'", ChronoTimer.eventType.equals("IND"));
		assertNotNull("Current should be empty, not null", ChronoTimer.current);
	}

	
	@Test
	public void testOff(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		
		//Turn On.  Add channels.  Enable Channels.  Turn Off.  Test Channels - if disabled.
		ChronoTimer.readCommand(timestamp+100, "ON");
		ChronoTimer.channels.get(1).toggle();
		ChronoTimer.channels.get(2).toggle();
		ChronoTimer.readCommand(timestamp+100, "CONN " + timestamp + " EYE 2");
		ChronoTimer.readCommand(timestamp+1000, "OFF");
		
		assertFalse("ChronoTimer still flagged as on", ChronoTimer.isOn);
		assertTrue("Channel array should be 0", ChronoTimer.channels.size()==0);

		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp+=10000, "OFF");
		
		//Timer is Off.		
		assertFalse(ChronoTimer.isOn);
		
		//Test archive, channels, and eventlog
		assertTrue("Archive should be empty when off", ChronoTimer.archive.isEmpty());
		assertTrue("Channels should be empty when off", ChronoTimer.channels.isEmpty());
		assertTrue("Eventlog should be empty when off", ChronoTimer.eventLog.isEmpty());
		
		// TEST A FEW COMMANDS TO MAKE SURE THEY AREN'T DOING ANYTHING		
		
		//Try Connecting a Channel
		ChronoTimer.readCommand(timestamp+100, "CONN GATE 1");
		assertTrue(ChronoTimer.channels.isEmpty());
		
		//Try starting a NewRun
		ChronoTimer.readCommand(timestamp+1000, "NEWRUN");
		assertNull("Current should still be null - system is off",ChronoTimer.current);
		
		//Try adding a Runner
		ChronoTimer.readCommand(timestamp, "NUM 111");
		assertNull("Current should be null", ChronoTimer.current);
		
		//Try EndRun
		ChronoTimer.readCommand(timestamp+1000, "ENDRUN");
		assertTrue("Archive Should be Empty", ChronoTimer.archive.isEmpty());
		
		
		
		//turn on.  Start race.  Add runners.  then turn off.  See what happens. 
		//		see if it lets me, see if i can call FIN or CANCEL after it is turned off.
		
		ChronoTimer.readCommand(timestamp, "ON");
		assertTrue(ChronoTimer.isOn);
		
		ChronoTimer.channels.get(1).toggle();
		ChronoTimer.channels.get(2).toggle();		
		
		ChronoTimer.readCommand(timestamp+10, "NEWRUN");
		
		assertEquals("Current Run should be 1", ChronoTimer.current.getRun(),1);
		
		ChronoTimer.readCommand(timestamp+100, "NUM 111");
		ChronoTimer.readCommand(timestamp+100, "NUM 222");
		
		ChronoTimer.readCommand(timestamp+100, "START");
		ChronoTimer.readCommand(timestamp, "START");
		
		
		ChronoTimer.readCommand(timestamp+10, "OFF");
		
		ChronoTimer.readCommand(timestamp, "FIN");
		ChronoTimer.readCommand(timestamp, "FIN");
		ChronoTimer.readCommand(timestamp, "ENDRUN");
		
		assertTrue("Archive Should be cleared", ChronoTimer.archive.isEmpty());
		assertNull("Current should be null when off", ChronoTimer.current);
		
		
	}

	
	@Test
	public void testCancel(){
		
		//Test cancel before start
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp+100, "CANCEL");
		//assertSame("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size()-1));
		
		//Test Cancel in actual Race
		
		ChronoTimer.readCommand(timestamp+100, "OFF");
		ChronoTimer.readCommand(timestamp+100, "ON");
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		ChronoTimer.readCommand(timestamp, "NEWRUN");
		ChronoTimer.readCommand(timestamp, "NUM 111");
		
		assertEquals(1, ChronoTimer.current.getStartSize());
		
		//start racer and check size
		ChronoTimer.readCommand(timestamp+100, "START");
		assertEquals(0, ChronoTimer.current.getStartSize());
		
		//cancel racer - check if added back to startqueue
		ChronoTimer.readCommand(timestamp+100, "CANCEL");
		assertEquals(1, ChronoTimer.current.getStartSize());
		
		//start and finish race
		ChronoTimer.readCommand(timestamp+100, "START");
		assertEquals(0, ChronoTimer.current.getStartSize());
		assertEquals(1, ChronoTimer.current.getFinishSize());
		ChronoTimer.readCommand(timestamp+1000, "FIN");
		
	}
	
	
	@Test
	public void testConn(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp+10, "CONN EYE 1");
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
		ChronoTimer.readCommand(timestamp+10, "CONN EYE 1");
		ChronoTimer.readCommand(timestamp, "CONN GATE 2");
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
				
		ChronoTimer.readCommand(timestamp+100, "DISC 1");
		assertNull(ChronoTimer.channels.get(0).getSensor());
		assertNotNull(ChronoTimer.channels.get(1).getSensor());
		
		ChronoTimer.readCommand(timestamp+100, "CONN GATE 1");
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
		ChronoTimer.readCommand(timestamp+=100, "NUM 357");
		
		//Test when finish queue is empty
		assertEquals(ChronoTimer.current.getFinishSize(), 0);
		assertTrue(Printer.log.isEmpty());
		assertEquals(1,ChronoTimer.current.getStartSize());
		
		//Test normally
		ChronoTimer.readCommand(timestamp+=1000, "START");
		ChronoTimer.readCommand(timestamp+=2580, "DNF");
		assertEquals("Bib #357 Did Not Finish", Printer.log.get(Printer.log.size()-1));		
		
	}
	

	
	@Test
	public void testFin(){
		Printer.log.clear();
		long timestamp = SystemTimer.getTime();
		ChronoTimer.readCommand(timestamp , "ON");
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		ChronoTimer.readCommand(timestamp+=100, "NUM 777");

		//Test FIN before starting race
		ChronoTimer.readCommand(timestamp, "FIN");
		assertEquals(ChronoTimer.current.getFinishSize(), 0);
		assertTrue(Printer.log.isEmpty());
		assertEquals(1,ChronoTimer.current.getStartSize());
		
		//Test conditions - if c <> 2
		ChronoTimer.current.trigger(3, timestamp+=5678);
		assertTrue(Printer.log.isEmpty());
		ChronoTimer.readCommand(timestamp+=5477, "START");
		ChronoTimer.current.trigger(3, timestamp+6666);
		assertEquals("Bib #777 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		assertEquals(1, ChronoTimer.current.getFinishSize());
		ChronoTimer.readCommand(timestamp+=55, "FIN");
		
		//Test conditions - empty finishqueue
		ChronoTimer.readCommand(timestamp, "NUM 444");
		ChronoTimer.readCommand(timestamp+=65648, "START");
		ChronoTimer.readCommand(timestamp+=555, "DNF");
		assertEquals(0, ChronoTimer.current.getFinishSize());
		ChronoTimer.current.trigger(2, timestamp);
		assertEquals("Bib #444 Did Not Finish", Printer.log.get(Printer.log.size()-1));
		
		//Test normally
		ChronoTimer.readCommand(timestamp, "NUM 999");
		ChronoTimer.readCommand(timestamp+=100, "START");
		ChronoTimer.readCommand(timestamp+=2500, "FIN");
		assertEquals("Bib #999 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));			
	}
	

	
	@Test
	public void testNum(){
	
		//call the add num command without starting a newrun. Shouldn't let me do it.
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		
		ChronoTimer.readCommand(timestamp, "ON");
		ChronoTimer.readCommand(timestamp, "ENDRUN");

		ChronoTimer.readCommand(timestamp+100, "NUM 999");
		assertNull("'current' should be null", ChronoTimer.current);
		assertSame("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size()-1));
		
		
		//start a newrun.  Add num - should work.
		ChronoTimer.readCommand(timestamp, "NEWRUN");
		ChronoTimer.readCommand(timestamp+100, "NUM 888");
		assertNotNull(ChronoTimer.current);
		assertEquals("ChronoTimer should only have 1 racer", 1, ChronoTimer.current.getStartSize());
		
		ChronoTimer.readCommand(timestamp+100, "NUM 999");
		assertEquals(2, ChronoTimer.current.getStartSize());
		assertEquals(0, ChronoTimer.current.getFinishSize());
		
	}
	

	
	@Test
	public void testPrint(){
		
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		
		ChronoTimer.readCommand(timestamp, "ON");
		
		//Test runnum==0 and current==null
		ChronoTimer.current=null;
		ChronoTimer.readCommand(timestamp+=100, "PRINT");
		assertEquals("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size()-1));		
		
		//Test Adding runner then Printing
		ChronoTimer.readCommand(timestamp, "NEWRUN");
		ChronoTimer.readCommand(timestamp+=100, "NUM 481");
		assertNotNull(ChronoTimer.current);
		ChronoTimer.readCommand(timestamp+=100, "PRINT");
		assertEquals("RUN      BIB      TIME	    Individual" + "\n" + "1        481      WAITING" + "\n", Printer.log.get(Printer.log.size()-1));
		//Test runnum==2 and archive size = 2
		
		
		
		
	}
	

	
	@Test
	public void testStart(){
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		
		ChronoTimer.readCommand(timestamp, "ON");
		assertFalse(ChronoTimer.channels.get(0).isEnabled());
		assertFalse(ChronoTimer.channels.get(7).isEnabled());
		
		//Testing when current is null
		ChronoTimer.readCommand(timestamp, "ENDRUN");
		ChronoTimer.readCommand(timestamp += 1000, "START");
		assertNull("Current should be null", ChronoTimer.current);
		assertSame("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size()-1));

		
		// ::NOTE::You should test the toggle command, and then use that.
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		
		assertTrue("Channel 1 should be enabled now", ChronoTimer.channels.get(0).isEnabled());
		assertTrue("Channel 2 should be enabled now", ChronoTimer.channels.get(1).isEnabled());
		
		ChronoTimer.readCommand(timestamp+=100, "START");
		assertNull(ChronoTimer.current);
		
		
		//Testing when current isn't null
		ChronoTimer.readCommand(timestamp+=100, "NEWRUN");
		ChronoTimer.readCommand(timestamp+=100, "NUM 877");
		ChronoTimer.readCommand(timestamp, "START");
		assertEquals("Bib #877 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		ChronoTimer.readCommand(timestamp+=205, "FIN");
		assertEquals("Bib #877 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		ChronoTimer.readCommand(timestamp+=100, "ENDRUN");
		
		//Testing start command twice - with one runner
		ChronoTimer.readCommand(timestamp+=100, "NEWRUN");
		ChronoTimer.readCommand(timestamp+=100, "NUM 555");
		ChronoTimer.readCommand(timestamp, "START");
		assertEquals("Bib #555 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		ChronoTimer.readCommand(timestamp, "START");
		ChronoTimer.readCommand(timestamp+=1000, "FIN");
		assertEquals("Bib #555 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		
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
		
		//RETEST START COMMANDS
		
		//Testing when current is null
		ChronoTimer.readCommand(timestamp, "ENDRUN");
		ChronoTimer.readCommand(timestamp += 1000, "TRIG 1");
		assertNull("Current should be null", ChronoTimer.current);
		assertSame("No Current Run, please enter the NEWRUN command", Printer.log.get(Printer.log.size()-1));

		
		// ::NOTE::You should test the toggle command, and then use that.
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		
		assertTrue("Channel 1 should be enabled now", ChronoTimer.channels.get(0).isEnabled());
		assertTrue("Channel 2 should be enabled now", ChronoTimer.channels.get(1).isEnabled());
		
		ChronoTimer.readCommand(timestamp+=100, "TRIG 1");
		assertNull(ChronoTimer.current);
		
		//Testing when current isn't null
		ChronoTimer.readCommand(timestamp+=100, "NEWRUN");
		ChronoTimer.readCommand(timestamp+=100, "NUM 877");
		ChronoTimer.readCommand(timestamp, "TRIG 1");
		assertEquals("Bib #877 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		ChronoTimer.readCommand(timestamp+=205, "FIN");
		assertEquals("Bib #877 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		ChronoTimer.readCommand(timestamp+=100, "ENDRUN");
		
		//Testing start command twice - with one runner
		ChronoTimer.readCommand(timestamp+=100, "NEWRUN");
		ChronoTimer.readCommand(timestamp+=100, "NUM 555");
		ChronoTimer.readCommand(timestamp, "TRIG 1");
		assertEquals("Bib #555 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		ChronoTimer.readCommand(timestamp, "TRIG 1");
		ChronoTimer.readCommand(timestamp+=1000, "FIN");
		assertEquals("Bib #555 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		
	}
	
	@Test
	public void testTrigFin(){
		//RETEST FINISH COMMANDS
		
		long timestamp = SystemTimer.getTime();
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp , "ON");
		Printer.log.clear();
		ChronoTimer.readCommand(timestamp, "TOGGLE 1");
		ChronoTimer.readCommand(timestamp, "TOGGLE 2");
		ChronoTimer.readCommand(timestamp+=100, "NUM 777");

		//Test FIN before starting race - Trig 2
		ChronoTimer.readCommand(timestamp, "TRIG 2");
		assertEquals(ChronoTimer.current.getFinishSize(), 0);
		assertTrue(Printer.log.isEmpty());
		assertEquals(1,ChronoTimer.current.getStartSize());
		
		//Test conditions - if c <> 2
		ChronoTimer.current.trigger(3, timestamp+=5678);
		assertTrue(Printer.log.isEmpty());
		ChronoTimer.readCommand(timestamp+=5477, "START");
		ChronoTimer.current.trigger(3, timestamp+6666);
		assertEquals("Bib #777 Start:  " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));
		assertEquals(1, ChronoTimer.current.getFinishSize());
		ChronoTimer.readCommand(timestamp+=55, "TRIG 2");
		
		//Test conditions - empty finishqueue
		ChronoTimer.readCommand(timestamp, "NUM 444");
		ChronoTimer.readCommand(timestamp+=65648, "START");
		ChronoTimer.readCommand(timestamp+=555, "DNF");
		assertEquals(0, ChronoTimer.current.getFinishSize());
		ChronoTimer.current.trigger(2, timestamp);
		assertEquals("Bib #444 Did Not Finish", Printer.log.get(Printer.log.size()-1));
		
		//Test normally
		ChronoTimer.readCommand(timestamp, "NUM 999");
		ChronoTimer.readCommand(timestamp+=100, "START");
		ChronoTimer.readCommand(timestamp+=2500, "TRIG 2");
		assertEquals("Bib #999 Finish: " + SystemTimer.convertLongToString(timestamp), Printer.log.get(Printer.log.size()-1));			
	}
}
