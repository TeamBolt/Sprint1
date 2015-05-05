package commands;
import chronoTimerItems.Channel;
import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;


public class Command_Disc implements Command {
	private int channelNum;
	
	public Command_Disc(int chnl) {
		channelNum = chnl;
	}	
	
	/**
	 * Disconnects a sensor from a channel (does nothing if no sensor was connected).
	 */
	public void execute() {
		// Get channel from ArrayList.
		Channel channel = ChronoTimer.getChannels().get(channelNum-1);
		if ( channel.getSensor() == null ) {
			Printer.print("No sensor connected to channel " + channelNum + ".");
			return;
		}
		channel.getSensor().dispose();
		channel.setSensor(null);
	}
}
