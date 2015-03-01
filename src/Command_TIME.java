
public class Command_TIME implements Command{

	private String time;
	private long timeStamp;
	
	public Command_TIME(long ts, String t) {
		time = t;
		timeStamp = ts;
	}
	
	@Override
	public void execute() {
		// Set the system timer to 'time'
		SystemTimer.setTime(time);
	}

}
