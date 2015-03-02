
public class Command_Num implements Command {
	private int bib;
	private long timestamp;
	
	Command_Num(long t, int b){
		bib = b;
		timestamp = t;
	}

	@Override
	public void execute() {
		ChronoTimer.current.add(bib);
	}

}
