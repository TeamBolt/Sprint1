package runGroups;

import static org.junit.Assert.*;
import java.util.concurrent.LinkedBlockingQueue;
import org.junit.Test;

import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;

/**
 * Test file for the shared functions in RunGroupShared. To easier test these functions, we are actually 
 * using the simplest type of RunGroup, RunGroupInd to test some functions, but we are only testing the 
 * functions it inherits from RunGroupShared.
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
		ChronoTimer.getArchive().add(rg);
		
		rg = new RunGroupInd();
		assertEquals("RunGroup was not given correct run num", 2, rg.runNum);
	}
	
	/**
	 * Tests that add correctly creates a run and adds it to the end of the start queue.
	 */
	@Test
	public void testAdd() {
		ChronoTimer.getArchive().clear();
		rg = new RunGroupInd();
		
		// Make sure adding a run works.
		rg.add(1);
		assertEquals("No run was not added to the startqueue", 1, rg.startQueue.size());
		assertEquals("Run was given the wrong bibNum", 1, rg.startQueue.peek().getBibNum());
		assertEquals("Run was given the wrong runNum", 1, rg.startQueue.peek().getRunNum());
		assertEquals("Run was given the wrong state", "waiting", rg.startQueue.peek().getState());
		
		// Make sure adding another run doesn't change who's next in line.
		rg.add(2);
		assertEquals("No run was not added to the startqueue", 2, rg.startQueue.size());
		assertEquals("The next run in line changed", 1, rg.startQueue.peek().getBibNum());
		
		// And make sure that the second run added was correct.
		rg.startQueue.poll();
		assertEquals("Run 2 was given the wrong bibNum", 2, rg.startQueue.peek().getBibNum());
	}
	
	
	/**
	 * Tests that print (and Run.print()) correctly prints all runs from completedRuns, 
	 * then the finishQueue, then the startQueue. (Since this function only calls doPrint,
	 * we are also testing doPrint).
	 */
	@Test
	public void testPrint() {
		ChronoTimer.getArchive().clear();
		rg = new RunGroupInd();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().get(1).toggle();
		
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
		
		Printer.getLog().clear();
		rg.print();
		assertEquals("Printer did not print correct number of lines", 1, Printer.getLog().size());
	}
	
	/**
	 * Tests that end() properly finishes all runs in both the startQueue and finishQueue with state dnf.
	 */
	@Test
	public void testEnd() {
		rg = new RunGroupInd();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 0);
		Printer.getLog().clear();
	
		// Make sure that runs both waiting and running are dnf'd.
		rg.end();
		assertEquals("Run was not placed in completedRuns.", 2, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.poll().getState());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.poll().getState());
		
		// And make sure a message was printed.
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Did Not Finish", Printer.getLog().get(0));
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.getLog().get(1));
	}
	
	/**
	 * Tests that getRun properly return the run number.
	 */
	@Test
	public void testGetRun() {
		rg = new RunGroupInd();
		rg.runNum = 42;
		assertEquals("getRun did not return the correct run number", 42, rg.getRun());
	}
	
	/**
	 * Tests that isEmpty properly return true if it has no runs in any of it's queues
	 * and false otherwise.
	 */
	@Test
	public void testIsEmpty() {
		// RunGroup is empty.
		rg = new RunGroupInd();
		assertEquals("getRun did not correctly return if it is empty.", true, rg.isEmpty());
		
		// Run is startQueue.
		rg.add(1);
		assertEquals("getRun did not correctly return if it is empty.", false, rg.isEmpty());
		
		// Enable channels.
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().get(1).toggle();
		
		// Run in finishQueue.
		rg.trigger(1, 0);
		assertEquals("getRun did not correctly return if it is empty.", false, rg.isEmpty());
		
		// Run in CompletedRuns.
		rg.trigger(2, 0);
		assertEquals("getRun did not correctly return if it is empty.", false, rg.isEmpty());
		
		// And empty again.
		rg.completedRuns.clear();
		assertEquals("getRun did not correctly return if it is empty.", true, rg.isEmpty());
	}
	
	/**
	 * Tests that getStartQueue properly return a COPY of the startQueue.
	 */
	@Test
	public void testGetStartQueue() {
		rg = new RunGroupInd();
		
		// Test empty.
		LinkedBlockingQueue<Run> queue = rg.getStartQueue();
		assertEquals("getStartQueue did not correctly an empty queue.", true, queue.isEmpty());
		
		// Test Copy (modify external).
		queue.add(new Run(1,1));
		assertEquals("getStartQueue did not correctly return a copy, as the internal queue has changed.", true, rg.getStartQueue().isEmpty());
		
		// Test Copy (modify internal).
		queue.clear();
		rg.add(2);
		assertEquals("getStartQueue did not correctly return a populated queue.", false, rg.getStartQueue().isEmpty());
		assertEquals("getStartQueue did not correctly return a copy, as the returned queue has changed.", true, queue.isEmpty());
		
		// Test contents.
		assertEquals("getStartQueue did not return a queue with the correct Run in it.", 2, rg.getStartQueue().peek().getBibNum());
	}
	
	/**
	 * Tests that getFinishQueue properly return a COPY of the finishQueue.
	 */
	@Test
	public void testGetFinishQueue() {
		// Enable channels.
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		
		rg = new RunGroupInd();
		
		// Test empty.
		LinkedBlockingQueue<Run> queue = rg.getFinishQueue();
		assertEquals("getFinishQueue did not correctly an empty queue.", true, queue.isEmpty());
		
		// Test Copy (modify external).
		queue.add(new Run(1,1));
		assertEquals("getFinishQueue did not correctly return a copy, as the internal queue has changed.", true, rg.getFinishQueue().isEmpty());
		
		// Test Copy (modify internal).
		queue.clear();
		rg.add(2);
		rg.trigger(1, 0);
		assertEquals("getFinishQueue did not correctly return a populated queue.", false, rg.getFinishQueue().isEmpty());
		assertEquals("getFinishQueue did not correctly return a copy, as the returned queue has changed.", true, queue.isEmpty());
		
		// Test contents.
		assertEquals("getFinishQueue did not return a queue with the correct Run in it.", 2, rg.getFinishQueue().peek().getBibNum());
		assertEquals("getFinishQueue did not return a queue with the correct Run in it.", 0, rg.getFinishQueue().peek().getStartTime());
	}
	
	/**
	 * Tests that getCompletedRuns properly return a COPY of the completedRuns.
	 */
	@Test
	public void testGetCompletedRuns() {
		// Enable channels.
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().get(1).toggle();
		
		rg = new RunGroupInd();
		
		// Test empty.
		LinkedBlockingQueue<Run> queue = rg.getCompletedRuns();
		assertEquals("getFinishQueue did not correctly an empty queue.", true, queue.isEmpty());
		
		// Test Copy (modify external).
		queue.add(new Run(1,1));
		assertEquals("getFinishQueue did not correctly return a copy, as the internal queue has changed.", true, rg.getCompletedRuns().isEmpty());
		
		// Test Copy (modify internal).
		queue.clear();
		rg.add(2);
		rg.trigger(1, 0);
		rg.trigger(2, 10);
		assertEquals("getFinishQueue did not correctly return a populated queue.", false, rg.getCompletedRuns().isEmpty());
		assertEquals("getFinishQueue did not correctly return a copy, as the returned queue has changed.", true, queue.isEmpty());
		
		// Test contents.
		assertEquals("getFinishQueue did not return a queue with the correct Run in it.", 2, rg.getCompletedRuns().peek().getBibNum());
		assertEquals("getFinishQueue did not return a queue with the correct Run in it.", 0, rg.getCompletedRuns().peek().getStartTime());
		assertEquals("getFinishQueue did not return a queue with the correct Run in it.", 10, rg.getCompletedRuns().peek().getFinishTime());
	}
	
	/**
	 * Tests that getEventType properly returns a COPY of the event type..
	 */
	@Test
	public void testGetEventType() {
		rg = new RunGroupInd();
		
		assertEquals("getEventType did not correctly report the event type", "IND", rg.getEventType());
		
		rg.eventType = "GRP";
		assertEquals("getEventType did not correctly report the event type", "GRP", rg.getEventType());
	}
}
