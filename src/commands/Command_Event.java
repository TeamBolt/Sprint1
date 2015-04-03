package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;
import runGroups.RunGroupGrp;
import runGroups.RunGroupInd;
import runGroups.RunGroupParInd;


public class Command_Event implements Command {
	private String event;
	
	public Command_Event(String e){
		event = e.toUpperCase();
	}
	
	@Override
	public void execute() {
		if ( ChronoTimer.getCurrent() != null && !ChronoTimer.getCurrent().isEmpty() ) {
			Printer.print("End the current run with ENDRUN to create an event of the new type.");
			return;
		}
		
		switch(event){
		case "IND":
		case "PARIND":
		case "GRP":
		case "PARGRP":
			ChronoTimer.setEventType(event);
			break;
		default:
			Printer.print("INVALID EVENT TYPE");
			break;
		}
		
		if ( ChronoTimer.getEventType().equals("IND") ) {
			ChronoTimer.setCurrent( new RunGroupInd() );
		} else if ( ChronoTimer.getEventType().equals("PARIND") ) {
			ChronoTimer.setCurrent( new RunGroupParInd() );
		} else if ( ChronoTimer.getEventType().equals("GRP") ) {
			ChronoTimer.setCurrent( new RunGroupGrp() );
		} else {
			Printer.print("Event type Parallel Group not yet supported");
		}
	}

}
