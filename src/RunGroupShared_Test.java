import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Test file for the shared functions in RunGroupShared. To easier test these functions, we are actually 
 * using the simplest type of RunGroup, RunGroupInd to test some functions, but we are only testing the 
 * functions it inherits from RunGroupShared.
 * 
 *  TODO
 * doPrint
 * getRun
 * getStartSize
 * getFinishSize
 * isEmpty
 * getStartQueue
 * getFinishQueue
 * getCompletedRuns
 * getEventType
 * 
 * @author Chris Harmon
 */
public class RunGroupShared_Test {
	RunGroupInd rg;
	
	/**
	 * Tests that the constructor correctly sets up runNum and channels to watch.
	 */
	@Test
	public void testConstructor() {
		rg = new RunGroupInd();
		assertEquals("RunGroup was not given correct run num", 1, rg.runNum);
		assertEquals("RunGroup was not given correct default start channel", 1, rg.startChannel);
		assertEquals("RunGroup was not given correct default finish channel", 2, rg.finishChannel);
		
		// Add a rungroup to the archive and make sure that the next run group's runNum is incremented.
		ChronoTimer.archive.add(rg);
		
		rg = new RunGroupInd();
		assertEquals("RunGroup was not given correct run num", 2, rg.runNum);
	}
	
	/**
	 * Tests that add correctly creates a run and adds it to the end of the start queue.
	 */
	@Test
	public void testAdd() {
		ChronoTimer.archive.clear();
		rg = new RunGroupInd();
		
		// Make sure adding a run works.
		rg.add(1);
		assertEquals("No run was not added to the startqueue", 1, rg.startQueue.size());
		assertEquals("Run was given the wrong bibNum", 1, rg.startQueue.peek().bibNum);
		assertEquals("Run was given the wrong runNum", 1, rg.startQueue.peek().runNum);
		assertEquals("Run was given the wrong state", "waiting", rg.startQueue.peek().state);
		
		// Make sure adding another run doesn't change who's next in line.
		rg.add(2);
		assertEquals("No run was not added to the startqueue", 2, rg.startQueue.size());
		assertEquals("The next run in line changed", 1, rg.startQueue.peek().bibNum);
		
		// And make sure that the second run added was correct.
		rg.startQueue.poll();
		assertEquals("Run 2 was given the wrong bibNum", 2, rg.startQueue.peek().bibNum);
	}
	
	
	/**
	 * Tests that print (and Run.print()) correctly prints all runs from completedRuns, 
	 * then the finishQueue, then the startQueue.
	 */
	@Test
	public void testPrint() {
		ChronoTimer.archive.clear();
		rg = new RunGroupInd();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.channels.add(new Channel(1));
		ChronoTimer.channels.add(new Channel(2));
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		
		// Set up a rungroup that will test all print locations.
		rg.add(1);
		rg.trigger(1, 42);
		rg.trigger(2, 84); 	// Now there is one in the completedRuns with a finishTime
		
		rg.add(2);
		rg.trigger(1, 88);
		rg.dnf();			// Now there is one in the completedRuns with a dnf.
		
		rg.add(3);
		rg.trigger(1, 9001);// Now there is one in finishQueue with state "inProgress"
		
		rg.add(4);			// And finally one in startQueue with state "waiting"
		
		Printer.log.clear();
		rg.print();
		assertEquals("Printer did not print correct number of lines", 1, Printer.log.size());
	}
	
	@Test
	public void testEnd() {
		rg = new RunGroupInd();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.channels.add(new Channel(1));
		ChronoTimer.channels.get(0).toggle();
		
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 0);
		Printer.log.clear();
	
		// Make sure that runs both waiting and running are dnf'd.
		rg.end();
		assertEquals("Run was not placed in completedRuns.", 2, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.poll().state);
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.poll().state);
		
		// And make sure a message was printed.
		assertEquals("No message was printed to the printer.", 2, Printer.log.size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Did Not Finish", Printer.log.get(0));
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.log.get(1));
	}
}
