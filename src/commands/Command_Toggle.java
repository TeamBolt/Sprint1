package commands;
import chronoTimerItems.ChronoTimer;


public class Command_Toggle implements Command {
	private long timestamp;
	private int channel;
	
	public Command_Toggle(long t, int chnl) {
		channel = chnl;
		timestamp = t;
	}

	/**
	 * Toggles the specified channel (shouldn't be able to fail).
	 */
	@Override
	public void execute() {
		ChronoTimer.getChannels().get(channel-1).toggle();
	}

}
