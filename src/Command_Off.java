import java.util.ArrayList;


public class Command_Off implements Command {
private long timestamp;
	
	public Command_Off(long t) {
		timestamp = t;
	}
	@Override
	public void execute() {
		ChronoTimer.isOn = false;
		SystemTimer.stop();
		
		// Clear out the archive, channels, and eventLog.
		ChronoTimer.archive = new ArrayList<RunGroup>();
		ChronoTimer.channels = new ArrayList<Channel>();
		ChronoTimer.eventLog = new ArrayList<String>();
	}
}
