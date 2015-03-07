
public class Command_Endrun implements Command {
private long timestamp;
	
	public Command_Endrun(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempts to end the current run, prints error if there is no current run to end.
	 */
	@Override
	public void execute() {
		if ( ChronoTimer.current != null ) {
			ChronoTimer.current.end();
			ChronoTimer.archive.add(ChronoTimer.current);
			ChronoTimer.current = null;
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
}
