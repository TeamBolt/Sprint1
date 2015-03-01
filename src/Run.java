
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
	public long getElapsed() {
		if ( state.equals("finished") ) return finishTime - startTime;
		return 0;
	}
	
	public void print() {
		System.out.print( runNum + "      " + bibNum + "      " );
		if ( state == "finished" ) System.out.print( getElapsed() );
		if ( state == "dns" ) System.out.print("DNF");
		if ( state == "waiting" ) System.out.print("WAITING");
		if ( state == "inProgress" ) System.out.print("RUNNING");
	}
	

}
