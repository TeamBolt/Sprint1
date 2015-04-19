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
	protected LinkedBlockingQueue<Run> tempQueue;
	protected Run secondRunner;
	
	/**
	 * Constructor instantiates data structures, and sets the runNum.
	 */
	public RunGroupShared() {
		// This is 1 if there are no RunGroups in the archive, and increments thereafter.
		runNum = ChronoTimer.getArchive().size() + 1;
		
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
		startQueue.clear();
		
		// End runs waiting to finish.
		for ( Run r : finishQueue ) {
			r.setState("dnf");
			Printer.print("Bib #" + r.getBibNum() + " Did Not Finish");
			completedRuns.add(r);
		}
		finishQueue.clear();
	}
	
	/**
	 * Get Run Number.
	 */
	public int getRun() {
		return runNum;
		
	}
	
	/**
	 * Swaps first two positions in finish queue
	 */
	public void swap(){
		tempQueue = new LinkedBlockingQueue<Run>();
		
		int count = 1;
		//loop through queue and add to the tempQueue
		while( !finishQueue.isEmpty() ) {
			Run current = finishQueue.poll();
			if( count == 2 ){
				secondRunner = current;	//save second runner for swapping.
			} else {
				tempQueue.add(current);
			}
			++count;
		}	
		
		finishQueue.add(secondRunner);
		while(!tempQueue.isEmpty()){
			Run current = tempQueue.poll();
			finishQueue.add(current);
		}
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
		return eventType;
	}

	/**
	 * Clears bib number if found in startingQueue.
	 */
	
	public void clr(int bib) {
		Run current;
		tempQueue = new LinkedBlockingQueue<Run>();
		
		//check if in the start queue - if so, call the shared method.
		for(Run r : ChronoTimer.getCurrent().getStartQueue()){
			if(r.getBibNum()==bib){		//it is in startQueue - need to remove it
				while( !startQueue.isEmpty() ) {
					current = startQueue.poll();
					if( current.getBibNum() != bib ) {
						tempQueue.add(current);
					}
				}
				startQueue = tempQueue;
				return;
			}			
		}
	
		//check if it is in the finishQueue
		
		for(Run r : ChronoTimer.getCurrent().getFinishQueue()){
			if(r.getBibNum()==bib){		//it is in startQueue - need to remove it
				Printer.print("This runner is already in the race and can't be removed.");
				return;
			}			
		}
				
		//check if it is in the competedRuns queue
		for(Run r : ChronoTimer.getCurrent().getCompletedRuns()){
			if(r.getBibNum()==bib){		//it is in startQueue - need to remove it
				Printer.print("This runner has already finished this race and can't be removed.");
				return;
			}			
		}
		//else it is not an existing bib number
		Printer.print("Bib number not found.");
	}
}
