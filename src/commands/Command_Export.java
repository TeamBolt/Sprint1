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
	//	this.exportToXML();
		this.exportToJson();
		this.updateURL();
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

	public void exportToJson(){
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

		String  item = "{\"event\":\"";

		String runs = "\"run\":\"";
		String bib = "\"bib\":\"";
		String start = "\"start\":\"";
		String finish = "\"finish\":\"";
		String elapsed = "\"elapsed\":\"";


		try {
			BufferedWriter fw = new BufferedWriter(new FileWriter(outFile));

			String toJson = "[";

			// Export completed runs.
			if ( !group.getCompletedRuns().isEmpty() ) {
				Iterator<Run> iterator = group.getCompletedRuns().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toJson += item + group.getEventType() + "\",";
					toJson += runs + run.getRunNum() + "\",";
					toJson += bib + run.getBibNum() + "\",";
					toJson += start + SystemTimer.convertLongToString(run.getStartTime()) + "\",";

					if(run.getState().equals("dnf")){
						toJson += finish + "DNF\",";
						toJson += elapsed + "DNF\"},";
					} else {
						toJson += finish + SystemTimer.convertLongToString(run.getFinishTime()) + "\",";
						toJson += elapsed + run.getElapsed() +"\"},";
					}

				}
			}

			// Export inProgress runs.
			if ( !group.getFinishQueue().isEmpty() ) {
				Iterator<Run> iterator = group.getFinishQueue().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toJson += item + group.getEventType() + "\",";
					toJson += runs + run.getRunNum() + "\",";
					toJson += bib + run.getBibNum() + "\",";
					toJson += start + SystemTimer.convertLongToString(run.getStartTime()) + "\",";
					toJson += finish + "RUNNING\",";
					toJson += elapsed + "RUNNING(" + run.getElapsed() + ")\"},";


				}

			}

			// Export waiting runs.
			if ( !group.getStartQueue().isEmpty() ) {
				Iterator<Run> iterator = group.getStartQueue().iterator();
				while ( iterator.hasNext() ) {
					Run run = iterator.next();

					toJson += item + group.getEventType() + "\",";
					toJson += runs + run.getRunNum() + "\",";
					toJson += bib + run.getBibNum() + "\",";
					toJson += start + "WAITING\",";
					toJson += finish +  "WAITING\",";
					toJson += elapsed +  "WAITING\"},";
				}

			}	

			toJson = toJson.substring(0, (toJson.length()-1));
			toJson += "]";
			inJson = toJson;
			fw.write(toJson);
			fw.close();
		} catch(Exception e){//catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void updateURL() {
		try {
			//			URL site = new URL("http://harmonlab7.appspot.com/helloworld");
			URL site = new URL("http://teambolt361.appspot.com/server");
			HttpURLConnection conn = (HttpURLConnection) site.openConnection();
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");

			String content = "data=" + inJson;
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(content);
			out.flush();
			out.close();

			new InputStreamReader(conn.getInputStream());
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
