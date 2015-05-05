package commands;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
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
	
	public Command_Export(){
		runNum = 0;
	}

	public void execute() {
		this.exportToXML();
	}

	/**
	 * Formats the data as xml and prints it to an appropriately named file.
	 */
	public void exportToXML(){
		RunGroup group = null;

		if ( runNum == 0 || ( ChronoTimer.getCurrent() != null && runNum == ChronoTimer.getCurrent().getRun() ) ) {
			if ( ChronoTimer.getCurrent() != null ) {
				group = ChronoTimer.getCurrent();
			} else if ( !ChronoTimer.getArchive().isEmpty() ){
				group = ChronoTimer.getArchive().get(ChronoTimer.getArchive().size()-1);
			} else {
				Printer.print("No Run to Export.");
				return;
			}
		} else {
			if ( ChronoTimer.getArchive().size() >= runNum ) {
				group = ChronoTimer.getArchive().get( runNum-1 );
			} else {
				Printer.print("No Run #" + runNum + " found.");
				return;
			}
		}

		String fileName = "RUN #" + group.getRun();
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
					
					// Handle the special case of a person who is DNF but never started, 
					// only happens when a RunGroup.end is called.
					if ( run.getStartTime() == 0 ) {
						toXml += start + "DNF</finish>\n";
					} else {
						toXml += start + SystemTimer.convertLongToString(run.getStartTime()) + "</start>\n";
					}

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
			Printer.print("Export complete.");
		} catch (IOException e) {
			Printer.print("Export error.");
		}

	}


}
