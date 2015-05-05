package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Endrun implements Command {

	
	public Command_Endrun() {}
	
	/**
	 * Attempts to end the current run, prints error if there is no current run to end.
	 */
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().end();
			ChronoTimer.getArchive().add(ChronoTimer.getCurrent());
			ChronoTimer.setCurrent(null);
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
}