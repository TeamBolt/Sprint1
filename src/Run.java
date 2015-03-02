
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
	 * @return elapsed time, or 0 if the run isn't finished.
	 */
	public String getElapsed() {
		if ( state.equals("finished") ) {
			Double elap =  (double) (finishTime - startTime) / 1000;
			String out = String.format("%.2f", elap);
			return out;
		}
		return "";
	}
	
	public void print() {
		String output = runNum + "        " + bibNum + "      ";
		if ( state == "finished" ) output += getElapsed();
		if ( state == "dnf" ) output += "DNF";
		if ( state == "waiting" ) output += "WAITING";
		if ( state == "inProgress" ) output += "RUNNING";
		
		Printer.print(output);
	}
	

}
