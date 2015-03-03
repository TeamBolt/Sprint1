
public class Command_Print implements Command {
	private long timestamp;
	private int run = 0;
	
	public Command_Print(long t) {
		timestamp = t;
	}
	
	public Command_Print(long t, int r) {
		timestamp = t;
		run = r;
	}
	
	@Override
	public void execute() {
		if ( run == 0 ) {
			if ( ChronoTimer.current != null ) {
				ChronoTimer.current.print();
			} else {
				Printer.print("No Current Run, please enter the NEWRUN command");
			}
		} else {
			ChronoTimer.archive.get( run-1 ).print();
		}
	}

}
