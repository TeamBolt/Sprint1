package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Cancel implements Command {
	
	public Command_Cancel() {}
	
	/**
	 * Attempt to cancel the current run, or prints error if no current rungroup.
	 */
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().cancel();
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
}
