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
	long startTime;
	long finishTime;
	int bibNum;
	int runNum;
	String state;
	
	/**
	 * Creates a run for the:
	 * @param int run 	run number, associated with a RunGroup.
	 * @param int bib	bib number, associated with a Competitor.
	 */
	public Run( int run, int bib ) {
		bibNum = bib;
		runNum = run;
	}
	
	/**
	 * @return elapsed time formatted for display, or nothing.
	 */
	public String getElapsed() {
		if ( state.equals("finished") ) {
			Double elap =  (double) (finishTime - startTime) / 1000;
			String out = String.format("%.2f", elap);
			return out;
		}
		return "";
	}
	
	/**
	 * Prints the Run to the Printer.
	 */
	public void print() {
		String output = runNum + "        " + bibNum + "      ";
		if ( state == "finished" ) output += getElapsed();
		if ( state == "dnf" ) output += "DNF";
		if ( state == "waiting" ) output += "WAITING";
		if ( state == "inProgress" ) output += "RUNNING";
		
		Printer.print(output);
	}
	

}
