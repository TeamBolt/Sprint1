
public class Command_Fin implements Command {
	private long timestamp;
	
	public Command_Fin(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempts to trigger channel 2, prints error if no current rungroup.
	 */
	@Override
	public void execute() {
		if ( ChronoTimer.current != null ) {
			ChronoTimer.current.trigger( 2, timestamp );
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
}
