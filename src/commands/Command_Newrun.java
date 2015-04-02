package commands;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;
import runGroups.RunGroupGrp;
import runGroups.RunGroupInd;
import runGroups.RunGroupParInd;


public class Command_Newrun implements Command {
private long timestamp;
	
	public Command_Newrun(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempts to create a new run, prints error if there is already a current run.
	 * Also prints error if the event type is one we haven't implemented yet.
	 */
	@Override
	public void execute() {
		if ( ChronoTimer.current == null ) {
			if ( ChronoTimer.eventType.equals("IND") ) {
				ChronoTimer.current = new RunGroupInd();
			} else if ( ChronoTimer.eventType.equals("PARIND") ) {
				ChronoTimer.current = new RunGroupParInd();
			} else if ( ChronoTimer.eventType.equals("GRP") ) {
				ChronoTimer.current = new RunGroupGrp();
			} else {
				Printer.print("Event type Parallel Group not yet supported");
			}
		} else {
			Printer.print("There is a current run, must call ENDRUN before NEWRUN.");
		}
	}
}
