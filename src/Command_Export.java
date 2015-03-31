import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;

import org.omg.CORBA.Current;

public class Command_Export implements Command {

	int runNum;

	public Command_Export(int num){
		runNum = num;
	}

	@Override
	public void execute() {
		this.exportToXML();
	}


	public void exportToXML(){
		// Takes care of the case that there is no run that corresponds to the given run number
		if( (ChronoTimer.current == null || ChronoTimer.current.getRun() != runNum) &&
				( runNum > ChronoTimer.archive.size() || runNum <= 0)) {
			Printer.print("No Run #" + runNum + " found.");
			return;
		}

		String fileName = "RUN #" + runNum;
		File outFile = new File(fileName);

		String  item = "<item event=" + ChronoTimer.eventType + ">\n";
		String runs = "\t<run>";
		String bib = "\t<bib>";
		String start = "\t<start>";
		String finish = "\t<finish>";
		String elapsed = "\t<elapsed>";
		RunGroup group = null;

		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(outFile));

			// checks if the run to be exported is the current run or an archived run
			if(ChronoTimer.current != null && ChronoTimer.current.getRun() == runNum){
				group =  ChronoTimer.current;
			} else if (runNum <=  ChronoTimer.archive.size() && runNum > 0 ) {
				group = ChronoTimer.archive.get(runNum-1);
			}

			String toXml = "";

			// Export completed runs.
			if ( !group.getCompletedRuns().isEmpty() ) {
				Iterator<Run> iterator = group.getCompletedRuns().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toXml += item;
					toXml += runs + run.runNum + "</run>\n";
					toXml += bib + run.bibNum + "</bib>\n";
					toXml += start + SystemTimer.convertLongToString(run.startTime) + "</start>\n";

					if(run.state.equals("dnf")){
						toXml += finish + "DNF</finish>\n";
						toXml += elapsed + "DNF</elapsed>\n";
					} else {
						toXml += finish + SystemTimer.convertLongToString(run.finishTime) + "</finish>\n";
						toXml += elapsed + run.getElapsed() +"</elapsed>\n";
					}

					toXml += "</item event=" + ChronoTimer.eventType + ">\n\n";
				}

			}

			// Export inProgress runs.
			if ( !group.getFinishQueue().isEmpty() ) {
				Iterator<Run> iterator = group.getFinishQueue().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toXml += item;
					toXml += runs + run.runNum + "</run>\n";
					toXml += bib + run.bibNum + "</bib>\n";
					toXml += start + SystemTimer.convertLongToString(run.startTime) + "</start>\n";
					toXml += finish + "RUNNING</finish>\n";
					toXml += elapsed + "RUNNING (" + run.getElapsed() + ")</elapsed>\n";
					toXml += "</item event=" + ChronoTimer.eventType + ">\n\n";
				}
			}

			// Export waiting runs.
			if ( !group.getStartQueue().isEmpty() ) {
				Iterator<Run> iterator = group.getStartQueue().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toXml += item;
					toXml += runs + run.runNum + "</run>\n";
					toXml += bib + run.bibNum + "</bib>\n";
					toXml += start + "WAITING</start>\n";
					toXml += finish + "WAITING</finish>\n";
					toXml += elapsed + "WAITING</elapsed>\n";
					toXml += "</item event=" + ChronoTimer.eventType + ">\n\n";
				}
			}	

			fw.write(toXml);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
