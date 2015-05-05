package commands;
import chronoTimerItems.SystemTimer;


public class Command_TIME implements Command{
	private String time;
	
	public Command_TIME(String t) {
		time = t;
	}
	
	/**
	 * Sets the current time in the system timer (sets offset).
	 */
	public void execute() {
		// Set the system timer to 'time'.
		SystemTimer.setTime(time);
	}
}
