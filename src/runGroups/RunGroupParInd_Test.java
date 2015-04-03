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
public class RunGroupParInd_Test {
	RunGroupParInd rg;
	
	/**
	 * Tests that the constructor correctly sets up runNum and channels to watch.
	 */
	@Test
	public void testConstructor() {
		RunGroupParInd rg = new RunGroupParInd();

		assertEquals("RunGroup was not given correct first start channel", 1, rg.startChannelOne );
		assertEquals("RunGroup was not given correct first finish channel", 2, rg.finishChannelOne );
		assertEquals("RunGroup was not given correct second start channel", 3, rg.startChannelTwo );
		assertEquals("RunGroup was not given correct second finish channel", 4, rg.finishChannelTwo );
		assertEquals("RunGroup was not given correct second finish channel", false, rg.racerOneIsRunning );
		assertEquals("RunGroup was not given correct second finish channel", false, rg.racerTwoIsRunning );
		assertEquals("RunGroup was not given correct second finish channel", false, rg.racersSwitched );
		assertEquals("RunGroup was not given correct second finish channel", false, rg.raceInProgress );
		assertEquals("RunGroup was not given correct eventType", "PARIND", rg.eventType );
	}
	
	/**
	 * Tests that trigger works correctly under all circumstances.
	 */
	@Test
	public void testTrigger() {
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().add(new Channel(3));
		ChronoTimer.getChannels().add(new Channel(4));
		
		
		
		////// Test one run at a time.
		// Add a run to the startqueue
		rg.add(1);
		
		// Trigger the start channel (but it's disabled)
		rg.trigger(1,0);
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		// Enable channels.
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().get(1).toggle();
		ChronoTimer.getChannels().get(2).toggle();
		ChronoTimer.getChannels().get(3).toggle();
		
		// Trigger the finish channel (but no one to finish)
		rg.trigger(2, 0);
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		
		
		//// Test only one run all alone.
		// Trigger the second start channel (One Run Only so should do nothing)
		rg.trigger(3, 42);
		assertEquals("Run was wrongly removed from startqueue.", 1, rg.startQueue.size());
		assertEquals("Run was wrongly placed in finishqueue.", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("A message was wrongly printed to the printer.", 0, Printer.getLog().size());
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Trigger the first start channel (One Run Only)
		rg.trigger(1, 42);
		assertEquals("Run was not removed from startqueue.", 0, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Run was not given correct startTime.", 42, rg.finishQueue.peek().getStartTime());
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishQueue.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "inProgress", rg.finishQueue.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Start:  18:00:00.42", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Trigger the second finish channel (should do nothing)
		rg.trigger(4, 0);
		assertEquals("Run was placed in start queue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was moved from finish queue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
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
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		
		//// Test Run1 with Run2 present.
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		Printer.getLog().clear();
		// Trigger the first start channel (One Run Only)
		rg.trigger(1, 42);
		assertEquals("Run was not removed from startqueue.", 1, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Incorrect run was started.", 1, rg.finishQueue.peek().getBibNum());
		assertEquals("Run was not given correct startTime.", 42, rg.finishQueue.peek().getStartTime());
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishQueue.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "inProgress", rg.finishQueue.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Start:  18:00:00.42", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);

		// Trigger the finish channel (now the run should be completed).
		rg.trigger(2, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Finish: 18:00:09.1", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		
		//// Test Run2 with Run1 present.
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		Printer.getLog().clear();
		// Trigger the first start channel (One Run Only)
		rg.trigger(3, 42);
		assertEquals("Run was not removed from startqueue.", 1, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Incorrect run was started.", 2, rg.finishQueue.peek().getBibNum());
		assertEquals("Run was not given correct startTime.", 42, rg.finishQueue.peek().getStartTime());
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishQueue.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "inProgress", rg.finishQueue.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Start:  18:00:00.42", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", true, rg.racersSwitched);

		// Trigger the finish channel (now the run should be completed).
		rg.trigger(4, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Finish: 18:00:09.1", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		
		
		////// Test with two races (in order).
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		
		// Trigger the first start channel
		rg.trigger(1, 42);
		assertEquals("Run was not removed from startqueue.", 1, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Incorrect run was started.", 1, rg.finishQueue.peek().getBibNum());
		assertEquals("Run was not given correct startTime.", 42, rg.finishQueue.peek().getStartTime());
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishQueue.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "inProgress", rg.finishQueue.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Start:  18:00:00.42", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Trigger the first start channel again (should do nothing)
		rg.trigger(1, 0);
		assertEquals("Run was placed in start queue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was moved from finish queue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Start the second racer.
		rg.trigger(3, 99);
		assertEquals("Run was not removed from startqueue.", 0, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 2, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Wrong Run is next to finish.", 1, rg.finishQueue.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Start:  18:00:00.99", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
	
		//// Test Finish Run1 then Run2
		// Test that no more runs can be started.
		Printer.getLog().clear();
		rg.add(3);
		rg.add(4);
		rg.trigger(1, 0);
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Cannot start competitor, race in progress", Printer.getLog().get(0));
		rg.trigger(3, 0);
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Cannot start competitor, race in progress", Printer.getLog().get(1));
		
		// Finish Run1
		rg.trigger(2, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 2, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Wrong run finished.", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 3, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Finish: 18:00:09.1", Printer.getLog().get(2));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Finish Run2
		rg.trigger(4, 9002);
		assertEquals("Run was placed in startqueue (wrongly).", 2, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); // Remove Run1 so we can look at Run2
		assertEquals("Wrong Run finished.", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 99, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9002, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 4, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Finish: 18:00:09.2", Printer.getLog().get(3));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		
		//// Test Finish Run2 then Run1
		// Set up rungroup like before.
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 42);
		rg.trigger(3, 99);
		Printer.getLog().clear();
		
		// Finish Run2
		rg.trigger(4, 9002);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Wrong run was finished.", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 99, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9002, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Finish: 18:00:09.2", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Finish Run1
		rg.trigger(2, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); // Remove Run2 so we can look at Run1
		assertEquals("Wrong run finished.", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Finish: 18:00:09.1", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		
		
		////// Test with two races (switched)
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		
		// Start the second racer.
		rg.trigger(3, 99);
		assertEquals("Run was not removed from startqueue.", 1, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Wrong Run was started.", 2, rg.finishQueue.peek().getBibNum());
		assertEquals("Run was not given correct startTime.", 99, rg.finishQueue.peek().getStartTime());
		assertEquals("Run was given a finishTime (wrongly).", 0, rg.finishQueue.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "inProgress", rg.finishQueue.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Start:  18:00:00.99", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", true, rg.racersSwitched);
		
		// Trigger the second start channel again (should do nothing)
		rg.trigger(3, 0);
		assertEquals("Run was placed in start queue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was moved from finish queue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", true, rg.racersSwitched);
		
		// Trigger the first start channel
		rg.trigger(1, 42);
		assertEquals("Run was not removed from startqueue.", 0, rg.startQueue.size());
		assertEquals("Run was not placed in finishqueue.", 2, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		assertEquals("Incorrect run is next to finish.", 2, rg.finishQueue.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Start:  18:00:00.42", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", true, rg.racersSwitched);
			
		
		//// Test Finish Run1 then Run2
		Printer.getLog().clear();
		
		// Finish Run1
		rg.trigger(2, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Wrong run finished.", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Finish: 18:00:09.1", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Finish Run2
		rg.trigger(4, 9002);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); // Remove Run1 so we can look at Run2
		assertEquals("Wrong Run finished.", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 99, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9002, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Finish: 18:00:09.2", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		
		//// Test Finish Run2 then Run1
		// Set up rungroup like before.
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		rg.trigger(3, 99);
		rg.trigger(1, 42);
		Printer.getLog().clear();
		
		// Finish Run2
		rg.trigger(4, 9002);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 1, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 1, rg.completedRuns.size());
		assertEquals("Wrong run was finished.", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 99, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9002, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 1, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Finish: 18:00:09.2", Printer.getLog().get(0));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Finish Run1
		rg.trigger(2, 9001);
		assertEquals("Run was placed in startqueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedruns.", 2, rg.completedRuns.size());
		rg.completedRuns.poll(); // Remove Run2 so we can look at Run1
		assertEquals("Wrong run finished.", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("Runs startTime was changed (wrongly).", 42, rg.completedRuns.peek().getStartTime());
		assertEquals("Run was not given a finishTime.", 9001, rg.completedRuns.peek().getFinishTime());
		assertEquals("Run was not given correct state.", "finished", rg.completedRuns.peek().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Finish: 18:00:09.1", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
	}
	
	/**
	 * Tests that cancel correctly moves whatever runs are running (1 or 2) back to
	 * the startQueue with the correct state and with startQueue order maintained.
	 */
	@Test
	public void testCancel() {
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().get(1).toggle();
		ChronoTimer.getChannels().add(new Channel(3));
		ChronoTimer.getChannels().get(2).toggle();
		ChronoTimer.getChannels().add(new Channel(4));
		ChronoTimer.getChannels().get(3).toggle();
		
		
		
		////// Test cancel 1.
		
		
		//// Test cancel Run1 with nothing waiting
		rg = new RunGroupParInd();
		Printer.getLog().clear();
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
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		
		//// Test cancel Run1 with Run2 waiting.
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 0);
		rg.cancel();
		assertEquals("Run was not placed back in startQueue.", 2, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		Run run1 = rg.startQueue.poll(); // Get first in line.
		Run run2 = rg.startQueue.poll(); // Get second in line.
		assertEquals("Run was not given correct state.", "waiting", run1.getState());
		assertEquals("Wrong run is first in line to start.", 1, run1.getBibNum());
		assertEquals("Run was not given correct state.", "waiting", run2.getState());
		assertEquals("Wrong run is second in line to start.", 2, run2.getBibNum());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.getLog().get(1));

		
		//// Test cancel Run2 with Run1 waiting.
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		rg.trigger(3, 0);
		rg.cancel();
		assertEquals("Run was not placed back in startQueue.", 2, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		run1 = rg.startQueue.poll(); // Get first in line.
		run2 = rg.startQueue.poll(); // Get second in line.
		assertEquals("Run was not given correct state.", "waiting", run1.getState());
		assertEquals("Wrong run is first in line to start.", 1, run1.getBibNum());
		assertEquals("Run was not given correct state.", "waiting", run2.getState());
		assertEquals("Wrong run is second in line to start.", 2, run2.getBibNum());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Canceled", Printer.getLog().get(1));
		
		
		
		////// Test cancel two.
		
	
		//// Test cancel two runs (in order) with no runs waiting.
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		rg.trigger(1, 0);
		rg.trigger(3, 0);
		rg.cancel();
		assertEquals("Runs were not placed back in startQueue.", 2, rg.startQueue.size());
		assertEquals("Runs were not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Runs were placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		run1 = rg.startQueue.poll(); // Get first in line.
		run2 = rg.startQueue.poll(); // Get second in line.
		assertEquals("Wrong run is first in line to start.", 1, run1.getBibNum());
		assertEquals("Wrong run is second in line to start.", 2, run2.getBibNum());
		assertEquals("No message was printed to the printer.", 4, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.getLog().get(2));
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Canceled", Printer.getLog().get(3));
		
		
		//// Test cancel two runs (switched) with no runs waiting.
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		rg.trigger(3, 0);
		rg.trigger(1, 0);
		rg.cancel();
		assertEquals("Runs were not placed back in startQueue.", 2, rg.startQueue.size());
		assertEquals("Runs were not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Runs were placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		run1 = rg.startQueue.poll(); // Get first in line.
		run2 = rg.startQueue.poll(); // Get second in line.
		assertEquals("Wrong run is first in line to start.", 1, run1.getBibNum());
		assertEquals("Wrong run is second in line to start.", 2, run2.getBibNum());
		assertEquals("No message was printed to the printer.", 4, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.getLog().get(2));
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Canceled", Printer.getLog().get(3));
		
		
		//// Test cancel two runs (in order) with a run waiting.
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(1, 0);
		rg.trigger(3, 0);
		rg.cancel();
		assertEquals("Runs were not placed back in startQueue.", 3, rg.startQueue.size());
		assertEquals("Runs were not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Runs were placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		run1 = rg.startQueue.poll(); // Get first in line.
		run2 = rg.startQueue.poll(); // Get second in line.
		Run run3 = rg.startQueue.poll(); // Get second in line.
		assertEquals("Wrong run is first in line to start.", 1, run1.getBibNum());
		assertEquals("Wrong run is second in line to start.", 2, run2.getBibNum());
		assertEquals("Wrong run is third in line to start.", 3, run3.getBibNum());
		assertEquals("No message was printed to the printer.", 4, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.getLog().get(2));
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Canceled", Printer.getLog().get(3));
		
		
		//// Test cancel two runs (in order) with a run waiting.
		rg = new RunGroupParInd();
		Printer.getLog().clear();
		rg.add(1);
		rg.add(2);
		rg.add(3);
		rg.trigger(3, 0);
		rg.trigger(1, 0);
		rg.cancel();
		assertEquals("Runs were not placed back in startQueue.", 3, rg.startQueue.size());
		assertEquals("Runs were not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Runs were placed in completedruns (wrongly).", 0, rg.completedRuns.size());
		run1 = rg.startQueue.poll(); // Get first in line.
		run2 = rg.startQueue.poll(); // Get second in line.
		run3 = rg.startQueue.poll(); // Get second in line.
		assertEquals("Wrong run is first in line to start.", 1, run1.getBibNum());
		assertEquals("Wrong run is second in line to start.", 2, run2.getBibNum());
		assertEquals("Wrong run is third in line to start.", 3, run3.getBibNum());
		assertEquals("No message was printed to the printer.", 4, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Canceled", Printer.getLog().get(2));
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Canceled", Printer.getLog().get(3));
	}
	
	/**
	 * Tests that dnf correctly gives the next run the "dnf" state and moves it to the completedRuns.
	 */
	@Test
	public void testDNF() {
		rg = new RunGroupParInd();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getChannels().add(new Channel(1));
		ChronoTimer.getChannels().get(0).toggle();
		ChronoTimer.getChannels().add(new Channel(2));
		ChronoTimer.getChannels().get(1).toggle();
		ChronoTimer.getChannels().add(new Channel(3));
		ChronoTimer.getChannels().get(2).toggle();
		ChronoTimer.getChannels().add(new Channel(4));
		ChronoTimer.getChannels().get(3).toggle();
		Printer.getLog().clear();
		
		
		//// Test one run 
		rg.add(1);
		
		// dnf should do nothing when there is no one waiting to finish.
		rg.dnf();
		assertEquals("Run was removed from startqueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Run was placed in finishqueue (wrongly).", 0, rg.finishQueue.size());
		assertEquals("Run was placed in completedruns (wrongly).", 0, rg.completedRuns.size());

		rg.trigger(1, 0);
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedRuns.", 1, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.poll().getState());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		//// Test two runs, run1 in progress 
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		Printer.getLog().clear();
		
		rg.trigger(1, 0);
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Wrong run is still in startQueue.", 2, rg.startQueue.peek().getBibNum());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedRuns.", 1, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Wrong run was dnf'd.", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		//// Test two runs, run2 in progress 
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		Printer.getLog().clear();
		
		rg.trigger(3, 0);
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 1, rg.startQueue.size());
		assertEquals("Wrong run is still in startQueue.", 1, rg.startQueue.peek().getBibNum());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedRuns.", 1, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Wrong run was dnf'd.", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 2, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Did Not Finish", Printer.getLog().get(1));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		//// Test two runs, both in progress (in order)
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		Printer.getLog().clear();
		
		rg.trigger(1, 0);
		rg.trigger(3, 0);
		
		// Dnf run1
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 1, rg.finishQueue.size());
		assertEquals("Wrong run is still in finishQueue.", 2, rg.finishQueue.peek().getBibNum());
		assertEquals("Run was not placed in completedRuns.", 1, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Wrong run was dnf'd.", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 3, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.getLog().get(2));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", true, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Dnf run2
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedRuns.", 2, rg.completedRuns.size());
		assertEquals("Wrong run was dnf'd 1st.", 1, rg.completedRuns.poll().getBibNum());
		assertEquals("Wrong run was dnf'd 2nd.", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 4, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Did Not Finish", Printer.getLog().get(3));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		//// Test two runs, both in progress (switched)
		rg = new RunGroupParInd();
		rg.add(1);
		rg.add(2);
		Printer.getLog().clear();
		
		rg.trigger(3, 0);
		rg.trigger(1, 0);
		
		// Dnf run2
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 1, rg.finishQueue.size());
		assertEquals("Wrong run is still in finishQueue.", 1, rg.finishQueue.peek().getBibNum());
		assertEquals("Run was not placed in completedRuns.", 1, rg.completedRuns.size());
		assertEquals("Run was not given the correct state.", "dnf", rg.completedRuns.peek().getState());
		assertEquals("Wrong run was dnf'd.", 2, rg.completedRuns.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 3, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #2 Did Not Finish", Printer.getLog().get(2));
		assertEquals("raceInProgress was incorrect.", true, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", true, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
		
		// Dnf run1
		rg.dnf();
		assertEquals("Run was placed in startQueue (wrongly).", 0, rg.startQueue.size());
		assertEquals("Run was not removed from finishQueue.", 0, rg.finishQueue.size());
		assertEquals("Run was not placed in completedRuns.", 2, rg.completedRuns.size());
		assertEquals("Wrong run was dnf'd 1st.", 2, rg.completedRuns.poll().getBibNum());
		assertEquals("Wrong run was dnf'd 2nd.", 1, rg.completedRuns.peek().getBibNum());
		assertEquals("No message was printed to the printer.", 4, Printer.getLog().size());
		assertEquals("Incorrect message was printed to the printer.", "Bib #1 Did Not Finish", Printer.getLog().get(3));
		assertEquals("raceInProgress was incorrect.", false, rg.raceInProgress);
		assertEquals("racerOneIsRunning was incorrect.", false, rg.racerOneIsRunning);
		assertEquals("racerTwoIsRunning was incorrect.", false, rg.racerTwoIsRunning);
		assertEquals("racersSwitched was incorrect.", false, rg.racersSwitched);
	}
}
