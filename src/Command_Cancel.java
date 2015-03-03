
public class Command_Cancel implements Command {
	private long timestamp;
	
	public Command_Cancel(long t) {
		timestamp = t;
	}
	
	@Override
	public void execute() {
		if ( ChronoTimer.current != null ) {
			ChronoTimer.current.cancel();
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}

}
