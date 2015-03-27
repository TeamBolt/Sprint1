import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;



/**
 * The class represents and individual run group. It knows how to add, start, 
 * finish, cancel, and dnf runs, as well as how to print itself.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Chris Harmon
 */
public class RunGroupInd implements RunGroup{

	public int runNum;
	public int startChannel;
	public int finishChannel;
	public LinkedBlockingQueue<Run> startQueue;
	public LinkedBlockingQueue<Run> finishQueue;
	public LinkedBlockingQueue<Run> completedRuns;
	
	/**
	 * This sets up the RunGroup to the default values, and gives it it's run number.
	 */
	public RunGroupInd() {
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
			current.startTime = timestamp;
			current.state = "inProgress";
			finishQueue.add(current);
			Printer.print("Bib #" + current.bibNum + " Start:  " + SystemTimer.convertLongToString(timestamp));
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
		
		current.state = "waiting";
		
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
		
		Printer.print("Bib #" + current.bibNum + " Canceled");
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
		String out = "RUN      BIB      TIME\n";
		
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

}
