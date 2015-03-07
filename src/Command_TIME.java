
public class Command_TIME implements Command{

	private String time;
	private long timeStamp;
	
	public Command_TIME(long ts, String t) {
		time = t;
		timeStamp = ts;
	}
	
	/**
	 * Sets the current time in the system timer (sets offset).
	 */
	@Override
	public void execute() {
		// Set the system timer to 'time'
		SystemTimer.setTime(time);
	}

}
