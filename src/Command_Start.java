
public class Command_Start implements Command {
	private long timestamp;
	
	public Command_Start(long t) {
		timestamp = t;
	}
	@Override
	public void execute() {
		ChronoTimer.current.trigger(1, timestamp);
	}

}
