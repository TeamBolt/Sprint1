
public class Command_Num implements Command {
	private int bib;
	private long timestamp;
	
	Command_Num(long t, int b){
		bib = b;
		timestamp = t;
	}

	@Override
	public void execute() {
		if ( ChronoTimer.current != null ) {
			ChronoTimer.current.add(bib);
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}

}
