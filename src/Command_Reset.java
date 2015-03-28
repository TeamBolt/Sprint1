import java.util.ArrayList;


public class Command_Reset implements Command {
	
	private long timestamp;
	
	public Command_Reset(long t)
	{
		timestamp = t;
	}

	@Override
	public void execute() {
		
		if(!ChronoTimer.isOn) //make sure Chronotimer is not off
		{
			return;
		}
		else
		{
			//ChronoTimer.readCommand(timestamp, "OFF");
			//ChronoTimer.readCommand(timestamp, "ON");
			//use implementation of on/off command
			
			ChronoTimer.isOn = false;
			
			// Clear out the archive, channels, and eventLog.
			ChronoTimer.archive = new ArrayList<RunGroup>();
			ChronoTimer.channels = new ArrayList<Channel>();
			ChronoTimer.eventLog = new ArrayList<String>();
			
			ChronoTimer.isOn = true;
			
			// Add all 8 Channels to the Array List.
			// To increase to 12, change the 9 to a 13.
			for ( int i = 1; i < 9; ++i ) {
				ChronoTimer.channels.add(new Channel(i));
			}
			
			// Create the default RunGroup
			ChronoTimer.eventType = "IND";
			ChronoTimer.current = new RunGroupInd();
		}
		
	}
	
	

}
