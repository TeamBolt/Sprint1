package runGroups;
import chronoTimerItems.ChronoTimer;

import java.util.concurrent.LinkedBlockingQueue;

import chronoTimerItems.Printer;

/**
 * This class contains all fields and methods which are shared between the 
 * different types of RunGroups. 
 * 
 * Including:
 * constructor
 * add
 * print 
 * doPrint
 * end
 * getRun
 * getStartSize
 * getFinishSize
 * isEmpty
 * getStartQueue
 * getFinishQueue
 * getCompletedRuns
 * getEventType
 * 
 * @author Chris
 */
public class RunGroupShared {
	// Fields
	protected int runNum;
	protected String eventType;
	protected LinkedBlockingQueue<Run> startQueue;
	protected LinkedBlockingQueue<Run> finishQueue;
	protected LinkedBlockingQueue<Run> completedRuns;
	
	/**
	 * Constructor instantiates data structures, and sets the runNum.
	 */
	public RunGroupShared() {
		// This is 1 if there are no RunGroups in the archive, and increments thereafter.
		runNum = ChronoTimer.archive.size() + 1;
		
		// Instantiations.
		startQueue = new LinkedBlockingQueue<Run>();
		finishQueue = new LinkedBlockingQueue<Run>();
		completedRuns = new LinkedBlockingQueue<Run>();
	}
	
	/**
	 * Adds a new run with the given bib number to the startQueue.
	 * 
	 * @param int bib	bib number for this run.
	 */
	public void add(int bib) {
		for ( Run r : startQueue ) {
			if ( r.getBibNum() == bib ) {
				Printer.print("Error: Bib number already in use");
				return;
			}
		}
		for ( Run r : finishQueue ) {
			if ( r.getBibNum() == bib ) {
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
	 * Prints the result of doPrint to the Printer.
	 */
	public void print() {
		Printer.print(doPrint());
	}
	
	/**
	 * Gets the appropriate print string for display or Printing.
	 */
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
		for ( Run run : finishQueue ) {
			out += run.print() + "\n";
		}
		
		// Print waiting runs.
		for ( Run run : startQueue ) {
			out += run.print() + "\n";
		}
		
		return out;
	}
	
	/**
	 * End all current runs with state dnf.
	 */
	public void end() {
		// End runs waiting to start.
		for ( Run r : startQueue ) {
			r.setState("dnf");
			Printer.print("Bib #" + r.getBibNum() + " Did Not Finish");
			completedRuns.add(r);
		}
		
		// End runs waiting to finish.
		for ( Run r : finishQueue ) {
			r.setState("dnf");
			Printer.print("Bib #" + r.getBibNum() + " Did Not Finish");
			completedRuns.add(r);
		}
	}
	
	/**
	 * Get Run Number.
	 */
	public int getRun() {
		return runNum;
		
	}
	
	/**
	 * Get size of startQueue.
	 */
	public int getStartSize(){
		
		return startQueue.size();
	}
	
	/**
	 * Get size of finishQueue.
	 */
	public int getFinishSize(){
		
		return finishQueue.size();
	}
	
	/**
	 * Get if the RunGroup is empty.
	 */
	public boolean isEmpty(){
		
		return (startQueue.isEmpty() && finishQueue.isEmpty() && completedRuns.isEmpty());
	}
	
	/**
	 * Return a copy of Runs waiting to start.
	 */
	public LinkedBlockingQueue<Run> getStartQueue() {
		return new LinkedBlockingQueue<Run>(startQueue);
	}
	
	/**
	 * Return a copy of Runs waiting to finish.
	 */
	public LinkedBlockingQueue<Run> getFinishQueue() {
		return new LinkedBlockingQueue<Run>(finishQueue);
	}
	
	/**
	 * Return a copy of Runs which have finished.
	 */
	public LinkedBlockingQueue<Run> getCompletedRuns() {
		return new LinkedBlockingQueue<Run>(completedRuns);
	}

	/**
	 * Return the a copy of type of event this is.
	 */
	public String getEventType() {
		return new String(eventType);
	}
}
