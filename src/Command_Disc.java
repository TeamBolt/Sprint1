
public class Command_Disc implements Command {
	private long timestamp;
	private int channelNum;
	
	public Command_Disc(long t, int chnl) {
		channelNum = chnl;
		timestamp = t;
	}	
	
	/**
	 * Disconnects a sensor from a channel (does nothing if no sensor was connected).
	 */
	@Override
	public void execute() {
		// Get channel from ArrayList.
		Channel channel = ChronoTimer.channels.get(channelNum-1);
		channel.sensor = null;
	}

}
