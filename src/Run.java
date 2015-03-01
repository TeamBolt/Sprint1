
public class Run {
	long startTime;
	long stopTime;
	int bibNum;
	int runNum;
	String state;
	
	public Run( int run, int bib ) {
		bibNum = bib;
		runNum = run;
	}
	
	public long getElapsed() {
		if ( state.equals("finished") ) return stopTime - startTime;
		return 0;
	}
	

}
