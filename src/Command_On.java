
public class Command_On implements Command {
	private long timestamp;
	Channel channel1;  //BK - Create new channels
	Channel channel2;
	
	public Command_On(long t) {
		timestamp = t;
		channel1 = new Channel();
		channel2 = new Channel();
	}

	@Override
	public void execute() {
		// Starts the SystemTimer
		
		//Check first if it is already on.  If it is, then we don't want to do anything extra.
		
		if(!ChronoTimer.isOn){
			SystemTimer.start();
			ChronoTimer.isOn = true;
					
			//BK - Add the Channels to the Array List
			ChronoTimer.channels.add(channel1);
			ChronoTimer.channels.add(channel2);
		}
	}
	
	
	
	
	
	
}
