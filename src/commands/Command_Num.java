package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Num implements Command {
	private int bib;
	private long timestamp;
	
	public Command_Num(long t, int b){
		bib = b;
		timestamp = t;
	}
	
	/**
	 * Attempts to add a run with bibnum 'bib' to the current rungroup.
	 * Prints error if no current rungroup.
	 */
	@Override
	public void execute() {
		if ( ChronoTimer.current != null ) {
			ChronoTimer.current.add(bib);
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}

}
