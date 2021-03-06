package commands;

import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;
import runGroups.RunGroupInd;


public class Command_Reset implements Command {
	
	public Command_Reset() {}

	/**
	 * Turns the system off, and back on again. reseting archive, channels, eventlog, eventType, and current.
	 */
	public void execute() {
			// Clear out the archive, channels, and eventLog.
			ChronoTimer.getArchive().clear();
			ChronoTimer.getChannels().clear();
			ChronoTimer.getEventLog().clear();
		
			
			// Add all 8 Channels to the Array List.
			// To increase to 12, change the 9 to a 13.
			for ( int i = 1; i < 9; ++i ) {
				ChronoTimer.getChannels().add(new Channel(i));
			}
			
			// Create the default RunGroup
			ChronoTimer.setEventType("IND");
			ChronoTimer.setCurrent(new RunGroupInd());
			ChronoTimer.setOn(true); //Since commands don't work unless the system is already on, this should do nothing.
	}
}
