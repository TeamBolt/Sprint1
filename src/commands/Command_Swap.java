package commands;

import java.util.concurrent.LinkedBlockingQueue;

import runGroups.Run;
import runGroups.RunGroup;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;

public class Command_Swap implements Command{
	
	private long timestamp;

	public Command_Swap(long t){
		timestamp = t;
	}

	@Override
	public void execute() {		
		if ( ChronoTimer.getCurrent() == null ) {
			Printer.print("No Current Run, please enter the NEWRUN command");
			return;
		} 
		//test if there are enough racers - if not exit command
		if(ChronoTimer.getCurrent().getFinishQueue().isEmpty()){
			Printer.print("No Run in progress.  A run must be started first.");
		} else if(ChronoTimer.getCurrent().getFinishQueue().size()<2){
			Printer.print("Not enough runners to swap.  Please add another runner");
		} 
		//tests passed - can now swap
		else {
			ChronoTimer.getCurrent().swap();
		}		
		
	}

}
