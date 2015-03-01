
public class Command_Print implements Command {
	private long timestamp;
	private int run = 0;
	
	public Command_Print(long t) {
		timestamp = t;
	}
	
	public Command_Print(long t, int r) {
		timestamp = t;
		run = r;
	}
	
	@Override
	public void execute() {
		// if run == 0 or run == current.runNum print the current RunGroup.
		// Otherwise print the run at archive index run - 1.
	}

}
