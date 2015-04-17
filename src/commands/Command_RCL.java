package commands;
import java.util.ListIterator;

import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_RCL implements Command {
private long timestamp;
	
	public Command_RCL(long t) {
		timestamp = t;
	}
	
	/**
	 * Attempts to end the current run, prints error if there is no current run to end.
	 */
	@Override
	public void execute() {
		
		// Generate an iterator. Start just after the last element.
		ListIterator<String> li = ChronoTimer.getEventLog().listIterator(ChronoTimer.getEventLog().size());

		// Iterate in reverse.
		while(li.hasPrevious()) {
			String com = li.previous();
			com = com.toLowerCase();
			if ( com.contains("start") || com.contains("fin") || com.contains("trig") ) {
				Printer.print(com);
				break;
			}
		}
	}
}