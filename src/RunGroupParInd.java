import java.util.concurrent.LinkedBlockingQueue;

/**
 * The class represents and parallel individual run group. It knows how to add, start, 
 * finish, cancel, and dnf runs, as well as how to print itself.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Chris Harmon
 */
public class RunGroupParInd extends RunGroupShared implements RunGroup{

	public int startChannelOne;
	public int finishChannelOne;
	public int startChannelTwo;
	public int finishChannelTwo;
	public boolean racerOneIsRunning;
	public boolean racerTwoIsRunning;
	public boolean racersSwitched;
	public boolean raceInProgress;

	
	/**
	 * This sets up the RunGroup to the default values, and gives it it's run number.
	 */
	public RunGroupParInd() {
		super();
		
		// Defaults.
		startChannelOne = 1;
		finishChannelOne = 2;
		startChannelTwo = 3;
		finishChannelTwo = 4;
		racerOneIsRunning = false;
		racerTwoIsRunning = false;
		racersSwitched = false;
		raceInProgress = false;

		eventType = "PARIND";
	}
	
	/**
	 * Starts and Finishes runs if the channel supplied is one we are interested in
	 * and there is at least one run in the appropriate start or finish queue.
	 * 
	 * @param int channel		The channel which was triggered.
	 * @param long timestamp	The time at which the trigger occured.
	 */
	public void trigger(int c, long timestamp) {
		// If the channel is disabled, do nothing.
		Channel channel = ChronoTimer.channels.get( c - 1 );
		if ( channel.enabled == false ) return;
		
		// If a race is in progress only accept finish triggers.
		if ( raceInProgress == true && ( c == startChannelOne || c == startChannelTwo ) ) {
			Printer.print("Cannot start competitor, race in progress");
			return;
		}
		
		// Only accept a start trigger if the associated lane is NOT occupied.
		if ( c == startChannelOne && !startQueue.isEmpty() && racerOneIsRunning == false ) {
			// Start channel one was triggered, the run is off!
			Run current = startQueue.poll();
			current.startTime = timestamp;
			current.state = "inProgress";
			finishQueue.add(current);
			
			// Set flags and print.
			racerOneIsRunning = true;
			if ( racerTwoIsRunning == true ) { raceInProgress = true; }
			Printer.print("Bib #" + current.bibNum + " Start:  " + SystemTimer.convertLongToString(timestamp));
		// Only accept a finish trigger if the associated lane IS occupied.
		} else if ( c == finishChannelOne && !finishQueue.isEmpty() && racerOneIsRunning == true ) {
			// Finish channel one triggered, the run is completed.
			Run current;
			if ( racersSwitched == false || racerTwoIsRunning == false ) {
				// If the other race is not waiting to finish, or we are first in line just finish the next in line.
				current = finishQueue.poll();
			} else {
				// If the other racer is ahead this racer, get the second in line and finish it.
				Run second = finishQueue.poll();
				current = finishQueue.poll();
				finishQueue.add(second);
			}
			
			current.finishTime = timestamp;
			current.state = "finished";
			completedRuns.add(current);
			
			// Set flags and print.
			racerOneIsRunning = false;
			racersSwitched = false;
			if ( racerTwoIsRunning == false ) { raceInProgress = false; }
			Printer.print("Bib #" + current.bibNum + " Finish: " + SystemTimer.convertLongToString(timestamp));
		// Only accept start trigger on channel 2 if racer 2 isn't already running AND either racer 1 is running, or is also waiting to start.
		} else if ( c == startChannelTwo && ( racerOneIsRunning == true || startQueue.size() >= 2 ) && racerTwoIsRunning == false ) {
			// Start channel 2 triggered, the run is off!
			Run current;
			if ( racerOneIsRunning == true ) {
				// If the other racer is away, just start the next in line.
				current = startQueue.poll();
			} else { 
				// Otherwise we have to get the second racer in line, and start it.
				LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
				Run first = startQueue.poll();
				current = startQueue.poll();
				tempQueue.add(first);
				tempQueue.addAll(startQueue);
				startQueue = tempQueue;
				
				// And set the flag, so we know which order to pull things from the finishQueue.
				racersSwitched = true;
			}
			
			current.startTime = timestamp;
			current.state = "inProgress";
			finishQueue.add(current);
			
			// Set flags and print.
			racerTwoIsRunning = true;
			if ( racerOneIsRunning == true ) { raceInProgress = true; }
			Printer.print("Bib #" + current.bibNum + " Start:  " + SystemTimer.convertLongToString(timestamp));
		} else if ( c == finishChannelTwo && !finishQueue.isEmpty() && racerTwoIsRunning == true ) {
			// Finish channel two triggered, the run is completed.
			Run current;
			if ( racersSwitched == true || racerOneIsRunning == false ) {
				// If the other race is not waiting to finish, or we are first in line just finish the next in line.
				current = finishQueue.poll();
			} else {
				// If the other racer is ahead this racer, get the second in line and finish it.
				Run second = finishQueue.poll();
				current = finishQueue.poll();
				finishQueue.add(second);
			}
			current.finishTime = timestamp;
			current.state = "finished";
			completedRuns.add(current);
			
			// Set flags and print.
			racerTwoIsRunning = false;
			racersSwitched = false;
			if ( racerOneIsRunning == false ) { raceInProgress = false; }
			Printer.print("Bib #" + current.bibNum + " Finish: " + SystemTimer.convertLongToString(timestamp));
		}
	}

	/**
	 * Cancels all racers (1-2) waiting to finish.
	 */
	public void cancel() {
		// Cancel only makes sense if there is a run waiting to finish.
		if ( finishQueue.isEmpty() ) return;
		
		// Get the runs out in order (if there is more than one)
		Run first = null;
		Run second = null;
		if ( racersSwitched == false ) {
			first = finishQueue.poll();
			if ( !finishQueue.isEmpty() ) second = finishQueue.poll();
		} else {
			second = finishQueue.poll();
			if ( !finishQueue.isEmpty() ) first = finishQueue.poll();
		}
		
		// Set state(s).
		first.state = "waiting";
		if ( second != null ) second.state = "waiting";
		
		if ( !startQueue.isEmpty() ) {
			// If there were people waiting to start, we need to budge in line.
			LinkedBlockingQueue<Run> tempQueue = new LinkedBlockingQueue<Run>();
			
			if ( first != null ) tempQueue.add(first);
			if ( second != null ) tempQueue.add(second);
			tempQueue.addAll(startQueue);
			startQueue = tempQueue;
		} else {
			// Otherwise we just move the run back into the startQueue.
			if ( first != null ) startQueue.add(first);
			if ( second != null ) startQueue.add(second);
		}
		
		// Reset Flags and print.
		racerOneIsRunning = false;
		racerTwoIsRunning = false;
		racersSwitched = false;
		raceInProgress = false;
		if ( first != null ) Printer.print("Bib #" + first.bibNum + " Canceled");
		if ( second != null ) Printer.print("Bib #" + second.bibNum + " Canceled");
	}

	/**
	 * DNFs the run that would be first to finish, gives it state "dnf" but no
	 * finishTime and moves it into completedRuns.
	 */
	public void dnf() {
		// DNF only makes sense if there is a run waiting to finish.
		if ( finishQueue.isEmpty() ) return;
		
		if ( racerTwoIsRunning == false || ( finishQueue.size() == 2 && racersSwitched == false ) ) {
			// DNF racer 1.
			racerOneIsRunning = false;
		} else if ( racerOneIsRunning == false || ( finishQueue.size() == 2 && racersSwitched == true ) ) {
			// DNF racer 2.
			racerTwoIsRunning = false;
		}
		
		Run current = finishQueue.poll();
		current.state = "dnf";
		completedRuns.add(current);
		
		// Reset Flags and print.
		racersSwitched = false;
		if ( finishQueue.isEmpty()) { raceInProgress = false; }
		Printer.print("Bib #" + current.bibNum + " Did Not Finish");
	}
}
