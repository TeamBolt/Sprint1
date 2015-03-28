
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
		
	}

}
