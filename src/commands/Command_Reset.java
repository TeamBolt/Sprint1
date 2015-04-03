package commands;

import java.util.ArrayList;

import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;

import runGroups.RunGroupInd;


public class Command_Reset implements Command {
	
	private long timestamp;
	
	public Command_Reset(long t)
	{
		timestamp = t;
	}

	@Override
	public void execute() {
	
	
		
			//ChronoTimer.readCommand(timestamp, "OFF");
			//ChronoTimer.readCommand(timestamp, "ON");
			//use implementation of on/off command
			
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
			
			//System.out.println("got to bottom of command reset exectue");
		
		
	}
	
	

}
