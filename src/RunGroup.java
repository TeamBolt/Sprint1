/**
 * Interface for RunGroup, we will later have the following implementations:
 * 		RunGroupInd
 * 		RunGroupParInd
 * 		RunGroupGrp
 * 		RunGroupParGrp
 * 		
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @authors Chris Harmon and Ben Kingsbury
 */
public interface RunGroup {

	public void trigger( int channel, long timestamp );
	
	public void cancel();
	
	public void dnf();
	
	public void print();
	
	public String doPrint();
	
	public void add(int bib);
	
	public void end();
	
	public int getRun();
	
}
