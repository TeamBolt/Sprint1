package runGroups;

import java.util.ArrayList;
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
public class RunGroupParGrp extends RunGroupShared implements RunGroup{

	protected Run[] finishList;

	/**
	 * This sets up the RunGroup to the default values, and gives it it's run number.
	 */
	public RunGroupParGrp() {
		super();
		
		finishList = new Run[8];
		
		eventType = "PARGRP";
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
		
		if ( !startQueue.isEmpty() && finishListIsEmpty() ) {
			// Start channel was triggered, all (up to 8) runs are off.
			for (int i = 0; i < 8 && startQueue.peek() != null; ++i ) {
				Run current = startQueue.poll();
				current.setStartTime(timestamp);
				current.setState("inProgress");
				finishList[i] = current;
				Printer.print("Bib #" + current.getBibNum() + " Start:  " + SystemTimer.convertLongToString(timestamp));
			}
		} else if ( finishList[c-1] != null ) {
			// Finish channel triggered, the run is completed.
			Run current = finishList[c-1];
			finishList[c-1] = null;
			current.setFinishTime(timestamp);
			current.setState("finished");
			completedRuns.add(current);
			Printer.print("Bib #" + current.getBibNum() + " Finish: " + SystemTimer.convertLongToString(timestamp));
		}
	}

	/**
	 * Cancels all running races and replaces them in the start queue.
	 */
	public void cancel() {
		// Cancel only makes sense if there is a run waiting to finish.
		if ( finishListIsEmpty() ) return;
		LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
		for ( Run current : finishList ) {
			current.setState("waiting");
			tempQueue.add(current);
			Printer.print("Bib #" + current.getBibNum() + " Canceled");
		}
		
		// Canceled all runs.
		finishList = new Run[8];
		
		if ( !startQueue.isEmpty() ) {
			// If there were people waiting to start, we need to budge in line.
			tempQueue.addAll(startQueue);
		}
		
		startQueue = tempQueue; // Replaces startQueue with newly constructed version.
	}

	/**
	 * DNFs the run that would be first to finish, gives it state "dnf" but no
	 * finishTime and moves it into completedRuns.
	 */
	public void dnf() {
		// DNF only makes sense if there is a run waiting to finish.
		if ( finishListIsEmpty() ) return;
		
		Run current = null;
		for ( Run r : finishList ) {
			if ( r != null ) {
				current = r;
				break;
			}
		}

		current.setState("dnf");
		completedRuns.add(current);
		Printer.print("Bib #" + current.getBibNum() + " Did Not Finish");
	}
	
	/**
	 * Gets the appropriate print string for display or Printing.
	 */
	@Override
	public String doPrint() {
		// Get the event title.
		String event = "";
		switch (eventType) {
			case "IND": event = "Individual";
						break;
			case "GRP": event = "Group";
						break;
			case "PARIND": event = "Parallel Individual";
						   break;
			case "PARGRP": event = "Parallel Group";
						   break;
		    default:	event = "";
		    			break;
		}
		
		String out = "RUN      BIB      TIME	    " + event +"\n";
		
		// Print completed runs.
		for ( Run run : completedRuns ) {
			out += run.print() + "\n";
		}
		
		// Print inProgress runs.
		for ( Run run : finishList ) {
			if ( run != null ) out += run.print() + "\n";
		}
		
		// Print waiting runs.
		for ( Run run : startQueue ) {
			out += run.print() + "\n";
		}
		
		return out;
	}
	
	@Override
	public LinkedBlockingQueue<Run> getFinishQueue() {
		LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
		for ( Run r : finishList ) {
			tempQueue.add(r);
		}
		return tempQueue;
	}
	
	/**
	 * Adds a new run with the given bib number to the startQueue.
	 * 
	 * @param int bib	bib number for this run.
	 */
	@Override
	public void add(int bib) {
		for ( Run r : startQueue ) {
			if ( r.getBibNum() == bib ) {
				Printer.print("Error: Bib number already in use");
				return;
			}
		}
		for ( Run r : finishList ) {
			if ( r != null && r.getBibNum() == bib ) {
				Printer.print("Error: Bib number already in use");
				return;
			}
		}
		for ( Run r : completedRuns ) {
			if ( r.getBibNum() == bib ) {
				Printer.print("Error: Bib number already in use");
				return;
			}
		}
		
		Run run = new Run(runNum, bib);
		startQueue.add(run);
	}
	
	/**
	 * Get if the RunGroup is empty.
	 */
	@Override
	public boolean isEmpty(){
		
		return (startQueue.isEmpty() && finishListIsEmpty() && completedRuns.isEmpty());
	}
	
	//@Override
	public void swap() {
		
	}
	
	//@Override
	public void clear( int bib ) {
		
	}
	
	private boolean finishListIsEmpty() {
		for ( Run r : finishList ) {
			if ( r != null ) return false;
		}
		return true;
	}
}
