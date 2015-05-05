package commands;
import chronoTimerItems.ChronoTimer;


public class Command_Toggle implements Command {
	private int channel;
	
	public Command_Toggle(int chnl) {
		channel = chnl;
	}

	/**
	 * Toggles the specified channel (shouldn't be able to fail).
	 */
	public void execute() {
		ChronoTimer.getChannels().get(channel-1).toggle();
	}
}
