package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_DNF implements Command {
	
	public Command_DNF() {}
	
	/**
	 * Attempts to DNF the current run, prints error if no current rungroup.
	 */
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().dnf();
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
}
