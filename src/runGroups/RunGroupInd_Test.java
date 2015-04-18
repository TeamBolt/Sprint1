	package runGroups;

import static org.junit.Assert.*;

import org.junit.Test;

import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;

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
		RunGroupInd rg = new RunGroupInd();
		assertEquals("RunGroup was not given correct start channel", 1, rg.startChannel );
		assertEquals("RunGroup was not given correct finish channel", 2, rg.finishChannel );
		assertEquals("RunGroup was not given correct eventType", "IND", rg.eventType );
	}
	
	/**
	 * Tests that trigger works correctly under all circumstances.
	 */
	@Test
	public void testTrigger() {
		rg = new RunGroupInd();
		Printer.getLog().clear();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().add(new Channel(3));
		
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
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().get(1).toggle();
		
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
		assertEquals("Run was not given correct startTime.", 42, rg.finishQueue.peek().getStartTime());
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishQueue.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "inProgress", rg.finishQueue.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Start:  18:00:00.42", Printer.getLog().get(0));
		
		// Trigger the finish channel while disabled (should do nothing).
		ChronoTimer.getChannels().get(1).toggle();
		rg.trigger(2, 0);
		assertEquals("Run was placed in start queue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was moved from finish queue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Enable the finish channel.
		ChronoTimer.getChannels().get(1).toggle();
		
		// Trigger the finish channel (now the run should be completed).
		rg.trigger(2, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Finish: 18:00:09.1", Printer.getLog().get(1));
		
		// Now start two runs.
		rg.completedRuns.clear();
		rg.add(1);
		rg.add(2);
		
		// Make sure they start in order.
		assertEquals("Incorrect run is first to start", 1, rg.startQueue.peek().getBibNum());
		
		// Make sure the first to start is also the first to finish
		rg.trigger(1, 42);
		assertEquals("Incorrect run is first to finish", 1, rg.finishQueue.peek().getBibNum());
		assertEquals("Both runs we started (only one should have).", 1, rg.finishQueue.size());
		
		rg.trigger(1, 43);
		assertEquals("Incorrect run is first to finish", 1, rg.finishQueue.peek().getBibNum());
		assertEquals("The second run wasn't started.", 2, rg.finishQueue.size());
		
		// Make sure second run is in line to finish.
		rg.trigger(2, 9001);
		assertEquals("Both runs were finished (only one should have).", 1, rg.finishQueue.size());
		assertEquals("Incorrect run is first to finish", 2, rg.finishQueue.peek().getBibNum());
		
		// Make sure first run finished correctly
		assertEquals("Both runs were finished (only one should have).", 1, rg.completedRuns.size());
		assertEquals("Incorrect run finished first", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9001, rg.completedRuns.peek().getFinishTime());
		
		// Make sure the second run finishes correctly.
		rg.trigger(2, 9002);
		assertEquals("Second run didn't finish", 0, rg.finishQueue.size());
		assertEquals("Second run didn't finish", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); //Remove the first run so we can look at the second.
		assertEquals("Incorrect run finished first", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9002, rg.completedRuns.peek().getFinishTime());
	}
	
	/**
	 * Tests that cancel correcly moved the most recent run to start (end of the finishQueue)
	 * back to the start of the startQueue (like it never event happened).
	 */
	@Test
	public void testCancel() {
		rg = new RunGroupInd();
		Printer.getLog().clear();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
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
		assertEquals("Run was not given correct state.", "waiting", rg.startQueue.poll().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.getLog().get(1));
		
		// Make sure the run is placed at the beginning of the startqueue.
		rg.add(1);
		rg.add(2);
		rg.trigger(1,0);
		rg.cancel();
		assertEquals("Run 1 was not placed at beginning of startqueue.", 1, rg.startQueue.poll().getBibNum());
		assertEquals("Run 2 not budged back.", 2, rg.startQueue.poll().getBibNum());
		
		// Now with 3 runs.
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(1, 0);
		rg.trigger(1, 0);
		
		rg.cancel();
		rg.cancel();

		// Make sure that startQueue order was maintained.
		assertEquals("Run 1 was not first.", 1, rg.startQueue.poll().getBibNum());
		assertEquals("Run 2 was not second.", 2, rg.startQueue.poll().getBibNum());
		assertEquals("Run 3 was not last.", 3, rg.startQueue.poll().getBibNum());
	}
	
	/**
	 * Tests that dnf correctly gives the run the "dnf" state and moves it to the completedRuns.
	 */
	@Test
	public void testDNF() {
		rg = new RunGroupInd();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		Printer.getLog().clear();
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
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.poll().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.getLog().get(1));
		
		// Test two runs.
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 0);
		rg.trigger(1, 0);
		rg.dnf();
		rg.dnf();
		assertEquals("Run was not placed in completedRuns.", 2, rg.completedRuns.size());
		assertEquals("Run 1 was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Run 1 was not given the correct state.", 1, rg.completedRuns.poll().getBibNum());
		assertEquals("Run 2 was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Run 3 was not given the correct state.", 2, rg.completedRuns.poll().getBibNum());
	}
}
