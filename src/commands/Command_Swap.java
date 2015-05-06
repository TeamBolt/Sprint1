package commands;

import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Swap implements Command{

	public Command_Swap(){}

	/**
	 * Swaps the next two racers in line to finish if possible, or print an error.
	 */
	public void execute() {		
		if ( ChronoTimer.getCurrent() == null ) {
			Printer.print("No Current Run, please enter the NEWRUN command");
			return;
		} 
		
		// Don't event check queues if it's pargrp or parind.
		if ( ChronoTimer.getEventType().equals("PARGRP") || ChronoTimer.getEventType().equals("PARIND") ) {
			if ( ChronoTimer.getCurrent() != null ) ChronoTimer.getCurrent().swap();
			return;
		}
		
		// Test if there are enough racers, if not exit command.
		if(ChronoTimer.getCurrent().getFinishQueue().isEmpty()){
			Printer.print("No Run in progress.  A run must be started first.");
		} else if(ChronoTimer.getCurrent().getFinishQueue().size()<2){
			Printer.print("Not enough runners to swap.  Please add another runner");
		} else { //Tests passed, can now swap.
			ChronoTimer.getCurrent().swap();
		}		
	}
}
