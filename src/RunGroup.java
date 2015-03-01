
public interface RunGroup {

	public void trigger( int channel, long timestamp );
	
	public void cancel();
	
	public void dnf();
}
