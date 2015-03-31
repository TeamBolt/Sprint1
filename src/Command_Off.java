import java.util.ArrayList;


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
		ChronoTimer.isOn = false;
		
		// Clear out the archive, channels, and eventLog.
		ChronoTimer.archive = new ArrayList<RunGroup>();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.eventLog = new ArrayList<String>();
		ChronoTimer.current = null;
	}
}


//// Should turn everything off and set all fields to 0 or null.  Should clear out current as well.