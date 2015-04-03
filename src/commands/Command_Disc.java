package commands;
import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;


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
		Channel channel = ChronoTimer.getChannels().get(channelNum-1);
		channel.getSensor().dispose();
		channel.setSensor(null);
	}

}
