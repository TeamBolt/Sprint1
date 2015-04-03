package commands;

import chronoTimerItems.ChronoTimer;

public class Command_Off implements Command {
private long timestamp;
	
	public Command_Off(long t) {
		timestamp = t;
	}
	
	/**
	 * Shuts the system off and clears out the data.
	 */
	@Override
	public void execute() {
		ChronoTimer.setOn(false);
		
		// Clear out the archive, channels, and eventLog.
		ChronoTimer.getArchive().clear();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getEventLog().clear();
		ChronoTimer.setCurrent(null);
	}
}


//// Should turn everything off and set all fields to 0 or null.  Should clear out current as well.