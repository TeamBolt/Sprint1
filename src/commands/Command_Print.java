package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Print implements Command {
	private int run;
	
	public Command_Print() {
		run = 0;
	}
	
	public Command_Print(int r) {
		run = r;
	}
	
	/**
	 * Prints the current rungroup (or prints error if none exists) if no run specified,
	 * otherwise prints the specified run (or error if the run doesn't exist).
	 */
	public void execute() {

		if ( run == 0 || ( ChronoTimer.getCurrent() != null && run == ChronoTimer.getCurrent().getRun() ) ) {
			if ( ChronoTimer.getCurrent() != null ) {
				ChronoTimer.getCurrent().print();
			} else if ( !ChronoTimer.getArchive().isEmpty() ){
				ChronoTimer.getArchive().get(ChronoTimer.getArchive().size()-1).print();
			} else {
				Printer.print("No Run to print.");
			}
		} else {
			if ( ChronoTimer.getArchive().size() >= run ) {
				ChronoTimer.getArchive().get( run-1 ).print();
			} else {
				Printer.print("No Run #" + run + " found.");
			}
		}
	}
}
