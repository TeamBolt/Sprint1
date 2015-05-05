package commands;

import chronoTimerItems.ChronoTimer;

public class Command_Off implements Command {
	
	public Command_Off() {}
	
	/**
	 * Shuts the system off and clears out the data.
	 */
	public void execute() {
		ChronoTimer.setOn(false);
		
		// Clear out the archive, channels, and eventLog.
		ChronoTimer.getArchive().clear();
		ChronoTimer.getChannels().clear();
		ChronoTimer.getEventLog().clear();
		ChronoTimer.setCurrent(null);
	}
}