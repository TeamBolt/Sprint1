import java.util.ArrayList;


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
			
			
			//ChronoTimer.isOn = false;
			
			// Clear out the archive, channels, and eventLog.
			ChronoTimer.archive.clear();
			ChronoTimer.channels.clear();
			//ChronoTimer.eventLog = new ArrayList<String>();
		
			
			// Add all 8 Channels to the Array List.
			// To increase to 12, change the 9 to a 13.
			for ( int i = 1; i < 9; ++i ) {
				ChronoTimer.channels.add(new Channel(i));
			}
			
			// Create the default RunGroup
			ChronoTimer.eventType = "IND";
			ChronoTimer.current = new RunGroupInd();
			ChronoTimer.isOn=true;
			
			System.out.println("got to bottom of command reset exectue");
		
		
	}
	
	

}
