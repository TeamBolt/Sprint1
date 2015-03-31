
public class Command_Event implements Command {
	private String event;
	
	public Command_Event(String e){
		event = e.toUpperCase();
	}
	
	@Override
	public void execute() {
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
		
		if ( ChronoTimer.current == null || ChronoTimer.current.isEmpty() ) {
			if ( ChronoTimer.eventType.equals("IND") ) {
				ChronoTimer.current = new RunGroupInd();
			} else if ( ChronoTimer.eventType.equals("PARIND") ) {
				Printer.print("Event type Parallel Individual not yet supported");
			} else if ( ChronoTimer.eventType.equals("GRP") ) {
				ChronoTimer.current = new RunGroupGrp();
			} else {
				Printer.print("Event type Parallel Group not yet supported");
			}
		} else {
			Printer.print("End the current run with ENDRUN to create an event of the new type.");
		}
		
	}

}
