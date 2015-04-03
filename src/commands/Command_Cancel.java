package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Cancel implements Command {
	private long timestamp;
	
	public Command_Cancel(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempt to cancel the current run, or prints error if no current rungroup.
	 */
	@Override
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().cancel();
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}

}
