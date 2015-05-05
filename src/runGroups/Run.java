package runGroups;

import chronoTimerItems.SystemTimer;


/**
 * Run class represents on Run for a competitor (with bib number) in a RunGroup.
 * 
 * The Run can be in 4 possible states: 
 * 		"waiting" 
 * 		"inProgress"
 * 		"finished"
 * 		"dnf"
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @authors Chris Harmon
 */
public class Run {
	private long startTime;
	private long finishTime;
	private int bibNum;
	private int runNum;
	private String state;
	
	public void setStartTime( long t ) {
		startTime = t;
	}
	
	public void setFinishTime( long t ) {
		finishTime = t;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getFinishTime() {
		return finishTime;
	}
	
	public int getBibNum() {
		return bibNum;
	}
	
	public int getRunNum() {
		return runNum;
	}
	
	public String getState() {
		return state;
	}
	
	public void setState( String s ) {
		state = s;
	}
	
	/**
	 * Creates a run for the:
	 * @param int run 	run number, associated with a RunGroup.
	 * @param int bib	bib number, associated with a Competitor.
	 */
	public Run( int run, int bib ) {
		bibNum = bib;
		runNum = run;
		state = "waiting";
	}
	
	/**
	 * @return elapsed time formatted for display, or nothing.
	 */
	public String getElapsed() {
		long fin;
		if ( state.equals("finished") ) {
			fin = finishTime;
		} else {
			fin = SystemTimer.getTime();
		}
		Double elap =  (double) (fin - startTime) / 1000;
		String out = String.format("%.2f", elap);
		return out;
	}
	
	/**
	 * Prints the Run to the Printer.
	 */
	public String print() {
		String output = runNum + "        " + bibNum + "      ";
		if ( state == "finished" ) output += getElapsed();
		if ( state == "dnf" ) output += "DNF";
		if ( state == "waiting" ) output += "WAITING";
		if ( state == "inProgress" ) output += "RUNNING (" + getElapsed() + ")";
		
		return output;
	}
}