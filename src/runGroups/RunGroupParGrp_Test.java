package runGroups;

import static org.junit.Assert.*;

import java.util.concurrent.LinkedBlockingQueue;

import org.junit.Test;

import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;

/**
 * Test file for RunGroup of type Individual.
 * 
 * @author Chris Harmon
 */
public class RunGroupParGrp_Test {
	RunGroupParGrp rg;
	
	/**
	 * Tests that the constructor correctly sets up runNum and channels to watch.
	 */
	@Test
	public void testConstructor() {
		RunGroupParGrp rg = new RunGroupParGrp();
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("RunGroup was not given correct eventType", "PARGRP", rg.eventType );
	}
	
	/**
	 * Tests that trigger works correctly for all circumstances.
	 */
	@Test
	public void testTrigger() {
		rg = new RunGroupParGrp();
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
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Trigger the start channel (but it's disabled)
		rg.trigger(1,0);
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Enable channels.
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().get(1).toggle();
		ChronoTimer.getChannels().get(2).toggle();
		
		// Trigger the start channel (now the run should be started).
		rg.trigger(1, 42);
		assertEquals("RunGroup was not given correct group size", 1, rg.groupSize );
		assertEquals("Run was not removed from startqueue.", 0, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 1, rg.finishListSize());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run was not given correct startTime.", 42, rg.finishList[0].getStartTime());
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishList[0].getFinishTime());
		assertEquals("Run was not given correct state.", "inProgress", rg.finishList[0].getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Start:  18:00:00.42", Printer.getLog().get(0));
		
		// Trigger the finish channel (now the run should be completed).
		rg.trigger(1, 9001);
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
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
		
		// Make sure the runs were started correctly.
		rg.trigger(1, 42);
		assertEquals("RunGroup was not given correct group size", 2, rg.groupSize );
		assertEquals("Incorrect run is first to finish", 1, rg.finishList[0].getBibNum());
		assertEquals("Incorrect run is first to finish", 2, rg.finishList[1].getBibNum());
		assertEquals("Only one run was started (both should have)", 2, rg.finishListSize());

		// Make sure first run finished correctly
		rg.trigger(1, 9001);
		assertEquals("Both runs were finished (only one should have).", 1, rg.finishListSize());
		assertEquals("RunGroup was not given correct group size", 2, rg.groupSize );
		assertEquals("Both runs were finished (only one should have).", 1, rg.completedRuns.size());
		assertEquals("Incorrect run finished first", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9001, rg.completedRuns.peek().getFinishTime());
		
		// Triggering a channel with the associated racer already completed should do nothing.
		rg.trigger(1, 9001);
		assertEquals("Both runs were finished (only one should have).", 1, rg.finishListSize());
		assertEquals("RunGroup was not given correct group size", 2, rg.groupSize );
		assertEquals("Both runs were finished (only one should have).", 1, rg.completedRuns.size());
		assertEquals("Incorrect run finished first", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9001, rg.completedRuns.peek().getFinishTime());
		
		// Make sure the second run finishes correctly.
		rg.trigger(2, 9002);
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Second run didn't finish", 0, rg.finishListSize());
		assertEquals("Second run didn't finish", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); //Remove the first run so we can look at the second.
		assertEquals("Incorrect run finished first", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9002, rg.completedRuns.peek().getFinishTime());
		
		
		// Now three runs out of order.
		rg.completedRuns.clear();
		rg.add(1);
		rg.add(2);
		rg.add(3);
		
		// Start the race.
		rg.trigger(1, 42);
		assertEquals("RunGroup was not given correct group size", 3, rg.groupSize );
		assertEquals("Incorrect bib num", 1, rg.finishList[0].getBibNum());
		assertEquals("Incorrect bib num", 2, rg.finishList[1].getBibNum());
		assertEquals("Incorrect bib num", 3, rg.finishList[2].getBibNum());
		assertEquals("All runs should have started.", 3, rg.finishListSize());
		
		// Make sure second racer finished correctly.
		rg.trigger(2, 9001);
		assertEquals("Run was not removed from finishList", null, rg.finishList[1]);
		assertEquals("RunGroup was not given correct group size", 3, rg.groupSize );
		assertEquals("Both runs were finished (only one should have).", 1, rg.completedRuns.size());
		assertEquals("Incorrect run finished first", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9001, rg.completedRuns.peek().getFinishTime());
		
		// Make sure the third run finishes correctly.
		rg.trigger(3, 9002);
		assertEquals("Run was not removed from finishList", null, rg.finishList[2]);
		assertEquals("RunGroup was not given correct group size", 3, rg.groupSize );
		assertEquals("Second run didn't finish", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); //Remove the first run so we can look at the second.
		assertEquals("Incorrect run finished first", 3, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9002, rg.completedRuns.peek().getFinishTime());
		
		// Make sure the first run finishes correctly.
		rg.trigger(1, 9003);
		assertEquals("Run was not removed from finishList", null, rg.finishList[0]);
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Second run didn't finish", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); //Remove the first run so we can look at the second.
		assertEquals("Incorrect run finished first", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Incorrect stoptime stored", 9003, rg.completedRuns.peek().getFinishTime());
	}
	
	/**
	 * Tests that cancel correctly moved the most recent run to start (end of the finishQueue)
	 * back to the start of the startQueue (like it never event happened).
	 */
	@Test
	public void testCancel() {
		rg = new RunGroupParGrp();
		Printer.getLog().clear();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().get(1).toggle();
		ChronoTimer.getChannels().add(new Channel(3));
		ChronoTimer.getChannels().get(2).toggle();
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
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Run was not placed back in startQueue.", 1, rg.startQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run was not given correct state.", "waiting", rg.startQueue.poll().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.getLog().get(1));
		
		// Now with 3 runs.
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(1, 0);
		
		rg.cancel();

		// Make sure that all runs were canceled, and order maintained.
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Runs were not placed back in startQueue.", 3, rg.startQueue.size());
		assertEquals("Runs were not removed from finishQueue.", 0, rg.finishListSize());
		assertEquals("Runs were placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run 1 was not first.", 1, rg.startQueue.poll().getBibNum());
		assertEquals("Run 2 was not second.", 2, rg.startQueue.poll().getBibNum());
		assertEquals("Run 3 was not last.", 3, rg.startQueue.poll().getBibNum());
		
		// Now with runs finished (just #2).
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(1, 0);
		rg.trigger(2, 1);
		assertEquals("Run not finised.", 1, rg.completedRuns.size());
		rg.cancel();

		// Make sure that all runs were canceled, and order maintained.
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Runs were not placed back in startQueue.", 3, rg.startQueue.size());
		assertEquals("Runs were not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Runs were placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run 1 was not first.", 1, rg.startQueue.poll().getBibNum());
		assertEquals("Run 2 was not second.", 2, rg.startQueue.poll().getBibNum());
		assertEquals("Run 3 was not last.", 3, rg.startQueue.poll().getBibNum());
		
		// Now with runs finished (#3 then #1).
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(1, 0);
		rg.trigger(3, 1);
		rg.trigger(1, 1);
		assertEquals("Runs not finised.", 2, rg.completedRuns.size());
		rg.cancel();

		// Make sure that all runs were canceled, and order maintained.
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Runs were not placed back in startQueue.", 3, rg.startQueue.size());
		assertEquals("Runs were not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Runs were placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run 1 was not first.", 1, rg.startQueue.poll().getBibNum());
		assertEquals("Run 2 was not second.", 2, rg.startQueue.poll().getBibNum());
		assertEquals("Run 3 was not last.", 3, rg.startQueue.poll().getBibNum());
	}
	
	/**
	 * Tests that dnf correctly gives the run the "dnf" state and moves it to the completedRuns.
	 * (DNF works identically between RunGroupInd and RunGroupParGrp)
	 */
	@Test
	public void testDNF() {
		rg = new RunGroupParGrp();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().get(1).toggle();
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
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
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
		rg.dnf();
		assertEquals("RunGroup was not given correct group size", 2, rg.groupSize );
		rg.dnf();
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Run was not placed in completedRuns.", 2, rg.completedRuns.size());
		assertEquals("Run 1 was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Run 1 was not given the correct state.", 1, rg.completedRuns.poll().getBibNum());
		assertEquals("Run 2 was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Run 3 was not given the correct state.", 2, rg.completedRuns.poll().getBibNum());
		
		// Test three runs, one completed.
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(1, 0);
		rg.dnf();
		rg.trigger(2, 9000);
		assertEquals("RunGroup was not given correct group size", 3, rg.groupSize );
		rg.dnf();
		assertEquals("RunGroup was not given correct group size", 0, rg.groupSize );
		assertEquals("Run was not placed in completedRuns.", 3, rg.completedRuns.size());
		assertEquals("Run 1 was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Run 1 was not given the correct state.", 1, rg.completedRuns.poll().getBibNum());
		assertEquals("Run 2 was not given the correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("Run 3 was not given the correct state.", 2, rg.completedRuns.poll().getBibNum());
		assertEquals("Run 2 was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Run 3 was not given the correct state.", 3, rg.completedRuns.poll().getBibNum());
	}
	
	/**
	 * Tests that isEmpty properly return true if it has no runs in any of it's queues
	 * and false otherwise.
	 */
	@Test
	public void testIsEmpty() {
		// RunGroup is empty.
		rg = new RunGroupParGrp();
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
		rg.trigger(1, 0);
		assertEquals("getRun did not correctly return if it is empty.", false, rg.isEmpty());
		
		// And empty again.
		rg.completedRuns.clear();
		assertEquals("getRun did not correctly return if it is empty.", true, rg.isEmpty());
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
		
		rg = new RunGroupParGrp();
		
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
	 * Tests that end() properly finishes all runs in both the startQueue and finishQueue with state dnf.
	 */
	@Test
	public void testEnd() {
		rg = new RunGroupParGrp();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		
		rg.add(1);
		rg.trigger(1, 0);
		rg.add(2);
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
	 * Tests that add correctly creates a run and adds it to the end of the start queue.
	 */
	@Test
	public void testAdd() {
		ChronoTimer.getArchive().clear();
		rg = new RunGroupParGrp();
		
		// Make sure adding a run works.
		rg.add(1);
		Printer.getLog().clear();
		// Make sure we can;t add duplicate bib numbers.
		rg.add(1);
		assertEquals("No error was printed.", "Error: Bib number already in use", Printer.getLog().get(0));
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
		
		// Make sure we can;t add duplicate bib numbers.
		rg.trigger(1, 0);
		Printer.getLog().clear();
		rg.add(2);
		assertEquals("No error was printed.", "Error: Bib number already in use", Printer.getLog().get(0));
	}
	
	/**
	 * Tests that swap is disabled for this event type.
	 */
	@Test
	public void testSwap() { 
		rg = new RunGroupParGrp();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 0);
		
		Printer.getLog().clear();
		rg.swap();
		assertEquals("Printer did not print correct message.", "Swap command does not apply to parallel events.", Printer.getLog().get(0));
	}
}
