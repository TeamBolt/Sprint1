package runGroups;

import java.util.concurrent.LinkedBlockingQueue;

import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;
import chronoTimerItems.SystemTimer;

/**
 * The class represents and Group run group. It knows how to start, 
 * finish, cancel, and dnf runs.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Chris Harmon
 */
public class RunGroupGrp extends RunGroupShared implements RunGroup{

	protected int startChannel;
	protected int finishChannel;
	protected int groupSize;

	/**
	 * This sets up the RunGroup to the default values, and gives it it's run number.
	 */
	public RunGroupGrp() {
		super();
		
		// Defaults.
		startChannel = 1;
		finishChannel = 2;
		groupSize = 0;
		
		eventType = "GRP";
	}
	
	/**
	 * Starts all runs if start channel is triggered and there are races waiting to start.
	 * Finishes one run at a time as the finish channel is triggeres (and it is enabled, and there are races waiting to finish)
	 * 
	 * @param int channel		The channel which was triggered.
	 * @param long timestamp	The time at which the trigger occured.
	 */
	public void trigger(int c, long timestamp) {
		// If the channel is disabled, do nothing.
		Channel channel = ChronoTimer.getChannels().get( c - 1 );
		if ( channel.isEnabled() == false ) return;
		
		if ( c == startChannel && !startQueue.isEmpty() ) {
			if ( groupSize > 0 ) {
				Printer.print("Cannot start competitor, race in progress.");
				return;
			}
			// Start channel was triggered, all runs are off!
			for ( Run current : startQueue ) {
				current.setStartTime(timestamp);
				current.setState("inProgress");
				finishQueue.add(current);
				Printer.print("Bib #" + current.getBibNum() + " Start:  " + SystemTimer.convertLongToString(timestamp));
			}
			groupSize = startQueue.size();
			startQueue.clear(); // All races have started.
		} else if ( c == finishChannel && !finishQueue.isEmpty()) {
			// Finish channel triggered, the run is completed.
			Run current = finishQueue.poll();
			current.setFinishTime(timestamp);
			current.setState("finished");
			completedRuns.add(current);
			Printer.print("Bib #" + current.getBibNum() + " Finish: " + SystemTimer.convertLongToString(timestamp));
			if ( finishQueue.isEmpty() ) {groupSize = 0;}
		}
	}

	/**
	 * Cancels all running races and replaces them in the start queue.
	 */
	public void cancel() {
		// Cancel only makes sense if there is a run waiting to finish.
		if ( finishQueue.isEmpty() ) return;
		LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
		
		for ( int i = 0; i < groupSize - finishQueue.size(); i++ ) {
			Run r = completedRuns.poll();
			r.setState("waiting");
			tempQueue.add(r);
			Printer.print("Bib #" + r.getBibNum() + " Canceled");
		}
		
		for ( Run current : finishQueue ) {
			current.setState("waiting");
			tempQueue.add(current);
			Printer.print("Bib #" + current.getBibNum() + " Canceled");
		}
		
		// Canceled all runs.
		finishQueue.clear();
		
		if ( !startQueue.isEmpty() ) {
			// If there were people waiting to start, we need to budge in line.
			tempQueue.addAll(startQueue);
		}
		groupSize = 0;
		startQueue = tempQueue; // Replaces startQueue with newly constructed version.
	}

	/**
	 * DNFs the run that would be first to finish, gives it state "dnf" but no
	 * finishTime and moves it into completedRuns.
	 */
	public void dnf() {
		// DNF only makes sense if there is a run waiting to finish.
		if ( finishQueue.isEmpty() ) return;
		
		Run current = finishQueue.poll();
		if (finishQueue.isEmpty()) groupSize = 0;
		current.setState("dnf");
		completedRuns.add(current);
		Printer.print("Bib #" + current.getBibNum() + " Did Not Finish");
	}

}
