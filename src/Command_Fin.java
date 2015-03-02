
public class Command_Fin implements Command {
	private long timestamp;
	
	public Command_Fin(long t) {
		timestamp = t;
	}
	@Override
	public void execute() {
		ChronoTimer.current.trigger(2, timestamp);
	}

}
