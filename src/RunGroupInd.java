import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;




public class RunGroupInd implements RunGroup{

	public int runNum;
	public int startChannel;
	public int finishChannel;
	public LinkedBlockingQueue<Run> startQueue;
	public LinkedBlockingQueue<Run> finishQueue;
	public HashSet<Run> completedRuns;
	
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
		completedRuns = new HashSet<Run>();
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
		Channel channel = ChronoTimer.channels.get(c);
		if ( channel.enabled == false ) return;
		
		if ( c == startChannel && !startQueue.isEmpty() ) {
			// Start channel was triggered, the run is off!
			Run current = startQueue.poll();
			current.startTime = timestamp;
			current.state = "inProgress";
			finishQueue.add(current);
		} else if ( c == finishChannel && !finishQueue.isEmpty()) {
			// Finish channel triggered, the run is completed.
			Run current = finishQueue.poll();
			current.finishTime = timestamp;
			current.state = "finished";
			completedRuns.add(current);
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
		
		Run current = finishQueue.poll();
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
	}

}
