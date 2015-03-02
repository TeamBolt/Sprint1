
public class Channel {
	Sensor sensor;
	Boolean enabled;
	Boolean connected;  //BK - Added Connected flag to see if that channel has been used or not.
	
	public Channel(){
		enabled = false;
		connected = false;  //BK - Set connected field to false initially
		sensor = new Sensor();
	}
	
	public void toggle(){
		enabled = !enabled;
	}
	
	public void connectSensor(Sensor s){
		sensor = s;
		connected=true;		//BK - Set this channel to true
	}
	
	/**
	 * Get a signal from a sensor and trigger an event on the timing System.
	 */
	public void trigger(){
		if(!enabled) return;
		// Use some method by the connected sensor
	}
}
