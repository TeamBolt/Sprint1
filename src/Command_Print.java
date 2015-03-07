
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
	
	/**
	 * Prints the current rungroup (or prints error if none exists) if no run specified,
	 * otherwise prints the specified run (or error if the run doesn't exist).
	 */
	@Override
	public void execute() {
		if ( run == 0 ) {
			if ( ChronoTimer.current != null ) {
				ChronoTimer.current.print();
			} else {
				Printer.print("No Current Run, please enter the NEWRUN command");
			}
		} else {
			if ( ChronoTimer.archive.size() >= run ) {
				ChronoTimer.archive.get( run-1 ).print();
			} else {
				Printer.print("No Run #" + run + " found.");
			}
		}
	}
}
