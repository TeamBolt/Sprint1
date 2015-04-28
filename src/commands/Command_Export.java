package commands;

import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import chronoTimerItems.ChronoTimer;
import chronoTimerItems.Printer;
import chronoTimerItems.SystemTimer;
import runGroups.Run;
import runGroups.RunGroup;

public class Command_Export implements Command {

	int runNum;
	String inJson;

	public Command_Export(int num){
		runNum = num;
	}

	@Override
	public void execute() {
		this.exportToXML();
	}


	public void exportToXML(){
		// Takes care of the case that there is no run that corresponds to the given run number
		if( (ChronoTimer.getCurrent() == null || ChronoTimer.getCurrent().getRun() != runNum) &&
				( runNum > ChronoTimer.getArchive().size() || runNum <= 0)) {
			Printer.print("No Run #" + runNum + " found.");
			return;
		}

		RunGroup group = null;

		// checks if the run to be exported is the current run or an archived run
		if(ChronoTimer.getCurrent() != null && ChronoTimer.getCurrent().getRun() == runNum){
			group =  ChronoTimer.getCurrent();
		} else if (runNum <=  ChronoTimer.getArchive().size() && runNum > 0 ) {
			group = ChronoTimer.getArchive().get(runNum-1);
		}

		String fileName = "RUN #" + runNum;
		File outFile = new File(fileName);

		String  item = "<item event=" + group.getEventType() + ">\n";
		String runs = "\t<run>";
		String bib = "\t<bib>";
		String start = "\t<start>";
		String finish = "\t<finish>";
		String elapsed = "\t<elapsed>";


		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(outFile));

			String toXml = "";

			// Export completed runs.
			if ( !group.getCompletedRuns().isEmpty() ) {
				Iterator<Run> iterator = group.getCompletedRuns().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toXml += item;
					toXml += runs + run.getRunNum() + "</run>\n";
					toXml += bib + run.getBibNum() + "</bib>\n";
					toXml += start + SystemTimer.convertLongToString(run.getStartTime()) + "</start>\n";

					if(run.getState().equals("dnf")){
						toXml += finish + "DNF</finish>\n";
						toXml += elapsed + "DNF</elapsed>\n";
					} else {
						toXml += finish + SystemTimer.convertLongToString(run.getFinishTime()) + "</finish>\n";
						toXml += elapsed + run.getElapsed() +"</elapsed>\n";
					}

					toXml += "</item event=" + group.getEventType() + ">\n\n";
				}

			}

			// Export inProgress runs.
			if ( !group.getFinishQueue().isEmpty() ) {
				Iterator<Run> iterator = group.getFinishQueue().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toXml += item;
					toXml += runs + run.getRunNum() + "</run>\n";
					toXml += bib + run.getBibNum() + "</bib>\n";
					toXml += start + SystemTimer.convertLongToString(run.getStartTime()) + "</start>\n";
					toXml += finish + "RUNNING</finish>\n";
					toXml += elapsed + "RUNNING (" + run.getElapsed() + ")</elapsed>\n";
					toXml += "</item event=" + group.getEventType() + ">\n\n";
				}
			}

			// Export waiting runs.
			if ( !group.getStartQueue().isEmpty() ) {
				Iterator<Run> iterator = group.getStartQueue().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toXml += item;
					toXml += runs + run.getRunNum() + "</run>\n";
					toXml += bib + run.getBibNum() + "</bib>\n";
					toXml += start + "WAITING</start>\n";
					toXml += finish + "WAITING</finish>\n";
					toXml += elapsed + "WAITING</elapsed>\n";
					toXml += "</item event=" + group.getEventType() + ">\n\n";
				}
			}	

			fw.write(toXml);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}


}
