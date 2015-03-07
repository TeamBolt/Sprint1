
public class Command_On implements Command {
	private long timestamp;
	
	
	public Command_On(long t) {
		timestamp = t;
	}

	/**
	 * Turns the system on, sets up default values, instantiates channels, but only if
	 * the system was not already on.
	 */
	@Override
	public void execute() {
		
		// Check first if it is already on.  If it is, then we don't want to do anything extra.
		if ( ChronoTimer.isOn ) return;
		
		// Starts the system.
		ChronoTimer.isOn = true;
				
		// Add all 8 Channels to the Array List.
		// To increase to 12, change the 9 to a 13.
		for ( int i = 1; i < 9; ++i ) {
			ChronoTimer.channels.add(new Channel(i));
		}
		
		// Create the default RunGroup
		ChronoTimer.eventType = "IND";
		ChronoTimer.current = new RunGroupInd();
	}
	
	
	
	
	
	
}
