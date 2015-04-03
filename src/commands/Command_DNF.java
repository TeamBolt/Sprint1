package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_DNF implements Command {
	private long timestamp;
	
	public Command_DNF(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempts to DNF the current run, prints error if no current rungroup.
	 */
	@Override
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().dnf();
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}

}