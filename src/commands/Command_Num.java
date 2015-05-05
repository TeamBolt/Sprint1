package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Num implements Command {
	private int bib;
	
	public Command_Num(int b){
		bib = b;
	}
	
	/**
	 * Attempts to add a run with bibnum 'bib' to the current rungroup.
	 * Prints error if no current rungroup.
	 */
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().add(bib);
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
}
