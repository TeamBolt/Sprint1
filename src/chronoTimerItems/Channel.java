package chronoTimerItems;

/**
 * The Channel class represents a channel in the timer. It can be enabled or not
 * and can have a sensor attached. Channels default to disable.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Kevari Francis
 */
public class Channel {
	private Sensor sensor;
	private Boolean enabled;
	private int channelNum;
	
	/**
	 * Constructor sets up the default value (disabled).
	 */
	public Channel(int num){
		enabled = false;
		channelNum = num;
	}
	
	/**
	 * Toggle the channel between disabled and enabled.
	 */
	public void toggle(){
		enabled = !enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public int getChannelNum() {
		return channelNum;
	}
	
	public Sensor getSensor() {
		return sensor;
	}
	
	public void setSensor( Sensor s ) {
		sensor = s;
	}
}
