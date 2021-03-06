package runGroups;

import java.util.concurrent.LinkedBlockingQueue;


/**
 * Interface for RunGroup, we have the following implementations:
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
	
	public LinkedBlockingQueue<Run> getStartQueue();
	
	public LinkedBlockingQueue<Run> getFinishQueue();
	
	public LinkedBlockingQueue<Run> getCompletedRuns();
	
	public boolean isEmpty();
	
	public String getEventType();
		
	public void swap();

	public void clr(int bib);
}