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
		//check if in the start queue - if so, call the shared method.
		for(Run r : ChronoTimer.getCurrent().getStartQueue()){
			if(r.getBibNum()==bib){		//it is in startQueue - need to remove it
				ChronoTimer.getCurrent().clr(bib);
				return;
			}			
		}
				
		//check if it is in the finishQueue
		
		for(Run r : ChronoTimer.getCurrent().getFinishQueue()){
			if(r.getBibNum()==bib){		//it is in startQueue - need to remove it
				Printer.print("This runner is already in the race and can't be removed.");
				return;
			}			
		}
				
		//check if it is in the competedRuns queue
		for(Run r : ChronoTimer.getCurrent().getCompletedRuns()){
			if(r.getBibNum()==bib){		//it is in startQueue - need to remove it
				Printer.print("This runner has already finished this race and can't be removed.");
				return;
			}			
		}
		//else it is not an existing bib number
		Printer.print("Bib number not found.");
	}
		
		
	
		
		
		
}
	

