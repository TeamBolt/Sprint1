
public class Command_Event implements Command {
	private String event;
	
	public Command_Event(String e){
		event = e.toUpperCase();
	}
	
	@Override
	public void execute() {
		if ( ChronoTimer.current != null && !ChronoTimer.current.isEmpty() ) {
			Printer.print("End the current run with ENDRUN to create an event of the new type.");
			return;
		}
		
		switch(event){
		case "IND":
		case "PARIND":
		case "GRP":
		case "PARGRP":
			ChronoTimer.eventType  = event;
			break;
		default:
			Printer.print("INVALID EVENT TYPE");
			break;
		}
		
		if ( ChronoTimer.eventType.equals("IND") ) {
			ChronoTimer.current = new RunGroupInd();
		} else if ( ChronoTimer.eventType.equals("PARIND") ) {
			ChronoTimer.current = new RunGroupParInd();
		} else if ( ChronoTimer.eventType.equals("GRP") ) {
			ChronoTimer.current = new RunGroupGrp();
		} else {
			Printer.print("Event type Parallel Group not yet supported");
		}
	}

}
