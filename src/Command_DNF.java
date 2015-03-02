
public class Command_DNF implements Command {
	private long timestamp;
	
	public Command_DNF(long t) {
		timestamp = t;
	}
	@Override
	public void execute() {
		ChronoTimer.current.dnf();
	}

}
