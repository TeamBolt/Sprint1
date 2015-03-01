
public class Channel {
	Sensor sensor;
	Boolean enabled;
	
	public Channel(){
		enabled = false;
	}
	
	public void toggle(){
		enabled = !enabled;
	}
	
	public void connectSensor(Sensor s){
		sensor = s;
	}
	
	/**
	 * Get a signal from a sensor and trigger an event on the timing System.
	 */
	public void trigger(){
		if(!enabled) return;
		// Use some method by the connected sensor
	}
}
