package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Fin implements Command {
	private long timestamp;
	
	public Command_Fin(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempts to trigger channel 2, prints error if no current rungroup.
	 */
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().trigger( 2, timestamp );
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
}
