import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Test file for RunGroup of type Individual.
 * 
 * @author Chris Harmon
 */
public class RunGroupInd_Test {
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
	 * Tests that trigger works correctly under all circumstances.
	 */
	@Test
	public void testTrigger() {
		rg = new RunGroupInd();
		Printer.log.clear();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.channels.add(new Channel(1));
		ChronoTimer.channels.add(new Channel(2));
		ChronoTimer.channels.add(new Channel(3));
		
		// Add a run to the startqueue
		rg.add(1);
		
		// Trigger an unrelated channel.
		rg.trigger(3, 0);
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Trigger the start channel (but it's disabled)
		rg.trigger(1,0);
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Enable channels.
		ChronoTimer.channels.get(0).toggle();
		ChronoTimer.channels.get(1).toggle();
		
		// Trigger the finish channel (but no one to finish)
		rg.trigger(2, 0);
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Trigger the start channel (now the run should be started).
		rg.trigger(1, 42);
		assertEquals("Run was not removed from startqueue.", 0, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run was not given correct startTime.", 42, rg.finishQueue.peek().startTime);
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishQueue.peek().finishTime);
		assertEquals("Run was not given correct state.", "inProgress", rg.finishQueue.peek().state);
		assertEquals("No message was printed to the printer.", 1, Printer.log.size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Start:  18:00:00.42", Printer.log.get(0));
		
		// Trigger the finish channel while disabled (should do nothing).
		ChronoTimer.channels.get(1).toggle();
		rg.trigger(2, 0);
		assertEquals("Run was placed in start queue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was moved from finish queue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Enable the finish channel.
		ChronoTimer.channels.get(1).toggle();
		
		// Trigger the finish channel (now the run should be completed).
		rg.trigger(2, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().startTime);
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().finishTime);
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().state);
		assertEquals("No message was printed to the printer.", 2, Printer.log.size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Finish: 18:00:09.1", Printer.log.get(1));
		
		// Now start two runs.
		rg.completedRuns.clear();
		rg.add(1);
		rg.add(2);
		
		// Make sure they start in order.
		assertEquals("Incorrect run is first to start", 1, rg.startQueue.peek().bibNum);
		
		// Make sure the first to start is also the first to finish
		rg.trigger(1, 42);
		rg.trigger(1, 43);
		assertEquals("Incorrect run is first to finish", 1, rg.finishQueue.peek().bibNum);
		
		// Make sure second run is in line to finish.
		rg.trigger(2, 9001);
		assertEquals("Incorrect run is first to finish", 2, rg.finishQueue.peek().bibNum);
		
		// Make sure first run finished correctly
		assertEquals("Incorrect run finished first", 1, rg.completedRuns.peek().bibNum);
		assertEquals("Incorrect stoptime stored", 9001, rg.completedRuns.peek().finishTime);
		
		// Make sure the second run finishes correctly.
		rg.trigger(2, 9002);
		assertEquals("Second run didn't finish", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); //Remove the first run so we can look at the second.
		assertEquals("Incorrect run finished first", 2, rg.completedRuns.peek().bibNum);
		assertEquals("Incorrect stoptime stored", 9002, rg.completedRuns.peek().finishTime);
	}
	
	/**
	 * Tests that cancel correcly moved the run back to the start of the startQueue
	 * (I am unsure if it should move the next run to finish, or the last run started)
	 */
	@Test
	public void testCancel() {
		rg = new RunGroupInd();
		Printer.log.clear();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.channels.add(new Channel(1));
		ChronoTimer.channels.get(0).toggle();
		rg.add(1);
		
		// Cancel should do nothing when there is no one waiting to finish.
		rg.cancel();
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Start a run 
		rg.trigger(1, 0);
		
		// Test that cancel works correctly.
		rg.cancel();
		assertEquals("Run was not placed back in startQueue.", 1, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run was not given correct state.", "waiting", rg.startQueue.poll().state);
		assertEquals("No message was printed to the printer.", 2, Printer.log.size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.log.get(1));
		
		// Make sure the run is placed at the beginning of the startqueue.
		rg.add(1);
		rg.add(2);
		rg.trigger(1,0);
		rg.cancel();
		assertEquals("Run 1 was not placed at beginning of startqueue.", 1, rg.startQueue.poll().bibNum);
		assertEquals("Run 2 not budged back.", 2, rg.startQueue.poll().bibNum);
		
		// Now with 3 runs.
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(1, 0);
		rg.trigger(1, 0);
		rg.cancel();
		rg.cancel();
	
		//::NOTE:: Cancel shuffles inputs, should it do that?
		assertEquals("Run 2 was not first.", 2, rg.startQueue.poll().bibNum);
		assertEquals("Run 1 was not second.", 1, rg.startQueue.poll().bibNum);
		assertEquals("Run 3 was not last.", 3, rg.startQueue.poll().bibNum);
	}
	
	/**
	 * Tests that dnf correctly gives the run the "dnf" state and moves it to the completedRuns.
	 */
	@Test
	public void testDNF() {
		rg = new RunGroupInd();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.channels.add(new Channel(1));
		ChronoTimer.channels.get(0).toggle();
		Printer.log.clear();
		rg.add(1);
		
		// dnf should do nothing when there is no one waiting to finish.
		rg.dnf();
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Test one run.
		rg.trigger(1, 0);
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedRuns.", 1, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.poll().state);
		assertEquals("No message was printed to the printer.", 2, Printer.log.size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.log.get(1));
		
		// Test two runs.
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 0);
		rg.trigger(1, 0);
		rg.dnf();
		rg.dnf();
		assertEquals("Run was not placed in completedRuns.", 2, rg.completedRuns.size());
		assertEquals("Run 1 was not given the correct state.", "dnf", rg.completedRuns.peek().state);
		assertEquals("Run 1 was not given the correct state.", 1, rg.completedRuns.poll().bibNum);
		assertEquals("Run 2 was not given the correct state.", "dnf", rg.completedRuns.peek().state);
		assertEquals("Run 3 was not given the correct state.", 2, rg.completedRuns.poll().bibNum);
	}
	
	/**
	 * Tests that print (and Run.print()) correctly prints all runs from completedRuns, 
	 * then the finishQueue, then the startQueue.
	 */
	@Test
	public void testPrint() {
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
		assertEquals("Printer did not print correct number of lines", 5, Printer.log.size());
		assertEquals("RG did not print correct header", "RUN      BIB      TIME", Printer.log.get(0));
		assertEquals("RG did not print correct finished run", "1        1      0.04", Printer.log.get(1));
		assertEquals("RG did not print correct dnf run", "1        2      DNF", Printer.log.get(2));
		assertEquals("RG did not print correct running run", "1        3      RUNNING", Printer.log.get(3));
		assertEquals("RG did not print correct waiting run", "1        4      WAITING", Printer.log.get(4));
	}
}
