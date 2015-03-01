
public class Command_On implements Command {
	private long timestamp;
	
	public Command_On(long t) {
		timestamp = t;
	}

	@Override
	public void execute() {
		// Starts the SystemTimer
		SystemTimer.start();
		ChronoTimer.isOn = true;
	}
	
	
	
	
	
	
}
