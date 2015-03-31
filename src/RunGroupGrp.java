import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;



/**
 * The class represents and Group run group. It knows how to add, start, 
 * finish, cancel, and dnf runs, as well as how to print itself.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Chris Harmon
 */
public class RunGroupGrp implements RunGroup{

	public int runNum;
	public int startChannel;
	public int finishChannel;
	public LinkedBlockingQueue<Run> startQueue;
	public LinkedBlockingQueue<Run> finishQueue;
	public LinkedBlockingQueue<Run> completedRuns;
	
	/**
	 * This sets up the RunGroup to the default values, and gives it it's run number.
	 */
	public RunGroupGrp() {
		// This is 1 if there are no RunGroups in the archive, and increments thereafter.
		runNum = ChronoTimer.archive.size() + 1;
		
		// Defaults.
		startChannel = 1;
		finishChannel = 2;
		
		// Instantiations.
		startQueue = new LinkedBlockingQueue<Run>();
		finishQueue = new LinkedBlockingQueue<Run>();
		completedRuns = new LinkedBlockingQueue<Run>();
	}
	
	/**
	 * Starts all runs if start channel is triggered and there are races waiting to start.
	 * Finishes one run at a time as the finish channel is triggeres (and it is enabled, and there are races waiting to finish)
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
			// Start channel was triggered, all runs are off!
			for ( Run current : startQueue ) {
				current.startTime = timestamp;
				current.state = "inProgress";
				finishQueue.add(current);
				Printer.print("Bib #" + current.bibNum + " Start:  " + SystemTimer.convertLongToString(timestamp));
			}
			startQueue.clear(); //All races have started.
		} else if ( c == finishChannel && !finishQueue.isEmpty()) {
			// Finish channel triggered, the run is completed.
			Run current = finishQueue.poll();
			current.finishTime = timestamp;
			current.state = "finished";
			completedRuns.add(current);
			Printer.print("Bib #" + current.bibNum + " Finish: " + SystemTimer.convertLongToString(timestamp));
		}
	}

	/**
	 * Cancels all running races and replaces them in the start queue.
	 */
	@Override
	public void cancel() {
		// Cancel only makes sense if there is a run waiting to finish.
		if ( finishQueue.isEmpty() ) return;
		LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
		for ( Run current : finishQueue ) {
			current.state = "waiting";
			tempQueue.add(current);
			Printer.print("Bib #" + current.bibNum + " Canceled");
		}
		finishQueue.clear();
		
		if ( !startQueue.isEmpty() ) {
			// If there were people waiting to start, we need to budge in line.
			tempQueue.addAll(startQueue);
		}
		
		startQueue = tempQueue; //Replaces startQueue with newly constructed version.
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
		current.state = "dnf";
		completedRuns.add(current);
		Printer.print("Bib #" + current.bibNum + " Did Not Finish");
	}

	public void print() {
		Printer.print(doPrint());
	}
	
	public String doPrint() {
		String out = "RUN      BIB      TIME	    Group\n";
		
		// Print completed runs.
		if ( !completedRuns.isEmpty() ) {
			Iterator<Run> iterator = completedRuns.iterator();
			while ( iterator.hasNext() ) {
				Run run = iterator.next();
				out += run.print() + "\n";
			}
		}
		
		// Print inProgress runs.
		if ( !finishQueue.isEmpty() ) {
			Iterator<Run> iterator = finishQueue.iterator();
			while ( iterator.hasNext() ) {
				Run run = iterator.next();
				out += run.print() + "\n";
			}
		}
		
		// Print waiting runs.
		if ( !startQueue.isEmpty() ) {
			Iterator<Run> iterator = startQueue.iterator();
			while ( iterator.hasNext() ) {
				Run run = iterator.next();
				out += run.print() + "\n";
			}
		}	
		
		return out;
	}
	
	/**
	 * Adds a new run with the given bib number to the startQueue.
	 * 
	 * @param int bib	bib number for this run.
	 */
	public void add(int bib) {
		Run run = new Run(runNum, bib);
		run.state = "waiting";
		startQueue.add(run);
	}
	
	/**
	 * End all current runs with state dnf.
	 */
	public void end() {
		while ( !startQueue.isEmpty() ) {
			Run r = startQueue.poll();
			r.state = "dnf";
			Printer.print("Bib #" + r.bibNum + " Did Not Finish");
			completedRuns.add(r);
		}
		
		while ( !finishQueue.isEmpty() ) {
			Run r = finishQueue.poll();
			r.state = "dnf";
			Printer.print("Bib #" + r.bibNum + " Did Not Finish");
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

}
