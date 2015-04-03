package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Start implements Command {
	private long timestamp;
	
	public Command_Start(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempts to trigger channel 1, or prints error if no current rungroup.
	 */
	@Override
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().trigger( 1, timestamp );
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}

}
