/**
 * The Channel class represents a channel in the timer. It can be enabled or not
 * and can have a sensor attached. Channels default to disable.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Kevari Francis
 */
public class Channel {
	public Sensor sensor;
	public Boolean enabled;
	public int channelNum;
	
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
	
	/**
	 * Get a signal from a sensor and trigger an event on the timing System.
	 */
	public void trigger(){
		if(!enabled) return;
		// Maybe call ChronoTimer.readCommand with a TRIG ?
	}
}
