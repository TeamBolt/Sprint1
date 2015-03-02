
public interface RunGroup {

	public void trigger( int channel, long timestamp );
	
	public void cancel();
	
	public void dnf();
	
	public void print();
	
	public void add(int bib);
}
