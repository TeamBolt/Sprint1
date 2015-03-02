
public class Command_Trig {
	
	private long timestamp;
	private int channelNum;
	
	public Command_Trig(long t, int c)
	{
		timestamp = t;
		channelNum = c;
	}
	
	public void execute() {
		ChronoTimer.current.trigger(channelNum, timestamp);
	}

}
