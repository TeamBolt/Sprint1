
public class Command_Conn implements Command {
	private long timestamp;
	private String sensor;
	private int channelNum;
	
	public Command_Conn(long t, String snsr, int chnl) {
		sensor = snsr;		//BK - Set new Sensor type to this string.
		channelNum = chnl;
		timestamp = t;
	}
	
	/**
	 * Attempts to connect a sensor to a channel, prints error if unable.
	 */
	@Override
	public void execute() {
		// Get channel from ArrayList.
		Channel channel = ChronoTimer.channels.get(channelNum-1);
		
		// Check if it is connected.  
		if(channel.sensor != null){
			Printer.print("Channel is already connected");
		} else {
			// If it is not connected, set the sensor to the specified type.
			Sensor s = new Sensor(sensor, channelNum);
			channel.sensor = s;
		}
	}
}
