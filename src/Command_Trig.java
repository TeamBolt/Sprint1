
public class Command_Trig {
	
	private long timestamp;
	private int channelNum;
	
	public Command_Trig(long t, int c)
	{
		timestamp = t;
		channelNum = c;
	}
	
	public void execute() {
		if ( ChronoTimer.current != null ) {
			ChronoTimer.current.trigger( channelNum, timestamp );
		} else {
			Printer.print("No Current Run, please enter the NEWRUN command");
		}
	}

}
