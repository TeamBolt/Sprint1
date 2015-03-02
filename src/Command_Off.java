
public class Command_Off implements Command {
private long timestamp;
	
	public Command_Off(long t) {
		timestamp = t;
	}
	@Override
	public void execute() {
		ChronoTimer.isOn = false;
		SystemTimer.stop();
	}

}
