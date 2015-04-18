package commands;

import java.util.Iterator;

import runGroups.Run;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;



public class Command_CLR implements Command{
	private long timestamp;
	private int bib;
	
	
	public Command_CLR(long t, int b){
		timestamp = t;
		bib = b;
		
	}


	@SuppressWarnings("unchecked")
	@Override
	public void execute() {
		if ( ChronoTimer.getCurrent() != null ) {
			ChronoTimer.getCurrent().clr(bib);
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}
	
		
		
	
		
		
		
}
	

