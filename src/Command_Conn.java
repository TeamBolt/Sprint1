
public class Command_Conn implements Command {
	private long timestamp;
	private String sensor;
	private int channelNum;
	
	public Command_Conn(long t, String snsr, int chnl) {
		sensor = snsr;		//BK - Set new Sensor type to this string.
		channelNum = chnl;
		timestamp = t;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		
		//BK - Get channel from ArrayList
		Channel c = ChronoTimer.channels.get(channelNum-1);
		
		//BK - Check if it is connected.  
		if(c.connected){
			System.out.println("Channel is already connected");
		}
		
		//BK - If it is not connected, set the sensor to the specified type.
		else {
			ChronoTimer.channels.get(channelNum-1).sensor.type=sensor;
		}
		
		
	}

}
