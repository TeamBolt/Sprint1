package commands;

import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_CLR implements Command{
	private int bib;
	
	public Command_CLR(int b){
		bib = b;
	}

	/**
	 * Attempts to clear the given bib number from the current run.
	 */
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().clr(bib);
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
	
		
		
	
		
		
		
}
	

