package runGroups;

import java.util.concurrent.LinkedBlockingQueue;

import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;
import chronoTimerItems.SystemTimer;



/**
 * The class represents and individual run group. It knows how to add, start, 
 * finish, cancel, and dnf runs, as well as how to print itself.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Chris Harmon
 */
public class RunGroupInd extends RunGroupShared implements RunGroup{

	protected int startChannel;
	protected int finishChannel;
	
	/**
	 * This sets up the RunGroup to the default values, and gives it it's run number.
	 */
	public RunGroupInd() {
		super();
		
		// Defaults.
		startChannel = 1;
		finishChannel = 2;
		
		eventType = "IND";
	}
	
	/**
	 * Starts and Finishes runs if the channel supplied is one we are interested in
	 * and there is at least one run in the appropriate start or finish queue.
	 * 
	 * @param int channel		The channel which was triggered.
	 * @param long timestamp	The time at which the trigger occured.
	 */
	@Override
	public void trigger(int c, long timestamp) {
		// If the channel is disabled, do nothing.
		Channel channel = ChronoTimer.channels.get( c - 1 );
		if ( channel.enabled == false ) return;
		
		if ( c == startChannel && !startQueue.isEmpty() ) {
			// Start channel was triggered, the run is off!
			Run current = startQueue.poll();
			current.setStartTime(timestamp);
			current.setState("inProgress");
			finishQueue.add(current);
			Printer.print("Bib #" + current.getBibNum() + " Start:  " + SystemTimer.convertLongToString(timestamp));
		} else if ( c == finishChannel && !finishQueue.isEmpty()) {
			// Finish channel triggered, the run is completed.
			Run current = finishQueue.poll();
			current.setFinishTime(timestamp);
			current.setState("finished");
			completedRuns.add(current);
			Printer.print("Bib #" + current.getBibNum() + " Finish: " + SystemTimer.convertLongToString(timestamp));
		}
	}

	/**
	 * Cancels the run which would be next to finish (if there is one) and moves
	 * it back to the front of the startQueue.
	 */
	@Override
	public void cancel() {
		// Cancel only makes sense if there is a run waiting to finish.
		if ( finishQueue.isEmpty() ) return;
		
		Run current = null;
		
		if ( finishQueue.size() == 1 ) {
			current = finishQueue.poll();
		} else {
			// Get the last person out of the finish queue.
			LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
			while ( !finishQueue.isEmpty() ) {
				current = finishQueue.poll();
				
				// Only save the run back into the queue if it's not the last one.
				if ( !finishQueue.isEmpty() ) {
					tempQueue.add(current);
				}
			}
			
			// Replace the finishQueue with the new one.
			finishQueue = tempQueue;
			
			// current is now the run which was at the end of the finishQueue.
		}
		
		current.setState("waiting");
		
		if ( !startQueue.isEmpty() ) {
			// If there were people waiting to start, we need to budge in line.
			LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
			tempQueue.add(current);
			tempQueue.addAll(startQueue);
			startQueue = tempQueue;
		} else {
			// Otherwise we just move the run back into the startQueue.
			startQueue.add(current);
		}
		
		Printer.print("Bib #" + current.getBibNum() + " Canceled");
	}

	/**
	 * DNFs the run that would be first to finish, gives it state "dnf" but no
	 * finishTime and moves it into completedRuns.
	 */
	@Override
	public void dnf() {
		// DNF only makes sense if there is a run waiting to finish.
		if ( finishQueue.isEmpty() ) return;
		
		Run current = finishQueue.poll();
		current.setState("dnf");
		completedRuns.add(current);
		Printer.print("Bib #" + current.getBibNum() + " Did Not Finish");
	}
}
