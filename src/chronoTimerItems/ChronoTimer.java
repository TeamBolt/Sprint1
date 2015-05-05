package chronoTimerItems;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import commands.Command;
import commands.Command_CLR;
import commands.Command_Cancel;
import commands.Command_Conn;
import commands.Command_DNF;
import commands.Command_Disc;
import commands.Command_Endrun;
import commands.Command_Event;
import commands.Command_Export;
import commands.Command_Fin;
import commands.Command_NULL;
import commands.Command_Newrun;
import commands.Command_Num;
import commands.Command_Off;
import commands.Command_On;
import commands.Command_Print;
import commands.Command_Reset;
import commands.Command_Start;
import commands.Command_Swap;
import commands.Command_TIME;
import commands.Command_Toggle;
import commands.Command_Trig;
import commands.Command_RCL;
import runGroups.Run;
import runGroups.RunGroup;


/**
 * Main class for ChronoTimer project. Reads in commands from either a test 
 * file or from stdin (java console), and creates and executes those commands.
 * To read in a test file set a program parameter to "test.txt".
 * 
 * The ON command needs to be run before any other commands (excepting exit) can run.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Chris Harmon
 */
public class ChronoTimer {
	
	protected static Window window;
	protected static boolean isOn = false;
	protected static String eventType;
	protected static RunGroup current;
	protected static ArrayList<RunGroup> archive = new ArrayList<RunGroup>();
	protected static ArrayList<Channel> channels = new ArrayList<Channel>();
	protected static ArrayList<String> eventLog = new ArrayList<String>();

	/**
	 * @param args will have the name of the test file if one was provided.
	 * 
	 * This will read from the test file, or if none was provided will loop
	 * and wait for input from stdIn (java console) and pass commands on
	 * to readCommand() for validation and execution.
	 */
	public static void main(String[] args) {
		if ( args.length > 0 ) {
			java.net.URL url = ChronoTimer.class.getClassLoader().getResource(args[0]);
			ChronoTimer.readTestFile(url.getPath());
		} else {
			// Start the GUI.
			window = new Window();
		}
	}
	
	/**
	 * Reads in commands from a file and gives them to readCommand.
	 * @param filename to read from.
	 */
	public static void readTestFile(String filename) {
		try( BufferedReader br = new BufferedReader(new FileReader(filename)) ) {
			for ( String line; ( line = br.readLine() ) != null; ) {
				
				// First get the timestamp out and set the time
				String[] args = line.split("	");
				
				// We need to convert this string time into timestamp
				long timestamp = SystemTimer.convertStringToLong(args[0]);
				
				//Then send the command to readCommand()
				ChronoTimer.readCommand(timestamp, args[1]);
			}
		} catch (FileNotFoundException e) {
			//e.printStackTrace();
			Printer.print("Error opening file.");
		} catch (IOException e) {
			//e.printStackTrace();
			Printer.print("Error reading from file.");
		}
	}

	/**
	 * Creates and executes the given comment at the given timestamp.
	 * Only the EXIT and ON commands are recognized when the system is off.
	 * If an invalid command or paramter was inputed a message will be
	 * printed and the command will NOT be added to the eventLog.
	 * @param timestamp
	 * @param command
	 */
	public static void readCommand(long timestamp, String command) {
		String[] args = command.split(" ");
		String name = args[0];
		
		// We can 'execute' the exit command right away.
		if ( name.equalsIgnoreCase("EXIT") ) System.exit(0);
		
		// If the command isn't "ON" and the ChronoTimer is off, no command to read.
		if ( !name.equalsIgnoreCase("ON") && !ChronoTimer.isOn ) return;
		
		Command cmdObj = new Command_NULL();
		String event = SystemTimer.convertLongToString(timestamp) + "	" + command;
		
		// Gets ints out of the parameters if they are there to get, will be -1 if
		// no parameter or parameter was not parse-able into an int.
		int paramOne = -1;
		int paramTwo = -1;
		
		if ( args.length > 1 && args[1] != null ) {
			try {
				paramOne = Integer.parseInt(args[1]); 
			} catch (NumberFormatException e) {
				// Do nothing, we want this to fail silently.
			}
			
		}
		
		if ( args.length > 2 && args[2] != null ) {
			try {
				paramTwo = Integer.parseInt(args[2]); 
			} catch (NumberFormatException e) {
				// Do nothing, we want this to fail silently.
			}
		}
		
		// Create the appropriate command object if the correct parameters exist.
		switch (name.toUpperCase()){
			case "TIME": 	if ( args.length > 1 ) cmdObj = new Command_TIME(args[1]);
							break;
			case "ON":		cmdObj = new Command_On();
							break;
			case "OFF":		cmdObj = new Command_Off();
							break;
			case "CONN":	if ( paramTwo > 0 && paramTwo < 9 ) cmdObj = new Command_Conn(args[1], paramTwo);
							break;
			case "TOGGLE":	if ( paramOne > 0 && paramOne < 9 ) cmdObj = new Command_Toggle(paramOne);
							break;
			case "TRIG":	if ( paramOne > 0 && paramOne < 9 ) cmdObj = new Command_Trig(timestamp, paramOne);
							break;
			case "NUM":		if ( paramOne > 0 ) cmdObj = new Command_Num(paramOne);
							break;
			case "START":	cmdObj = new Command_Start(timestamp);
							break;
			case "FIN":		cmdObj = new Command_Fin(timestamp);
							break;
			case "DNF":		cmdObj = new Command_DNF();
							break;
			case "PRINT":	if ( paramOne <= 0 ) cmdObj = new Command_Print();
							if ( paramOne > 0) cmdObj = new Command_Print(paramOne);
							break;
			case "DISC":	if ( paramOne > 0 && paramOne < 9 ) cmdObj = new Command_Disc(paramOne);
							break;
			case "CANCEL":	cmdObj = new Command_Cancel();
							break;
			case "EVENT":	if ( args.length > 1 ) cmdObj = new Command_Event(args[1]);
							break;
			case "NEWRUN":	cmdObj = new Command_Newrun();
							break;
			case "ENDRUN":	cmdObj = new Command_Endrun();
							break;
			case "EXPORT":	if ( paramOne <= 0 ) cmdObj = new Command_Export();
							if ( paramOne > 0) cmdObj = new Command_Export(paramOne);
							break;
			case "CLR":		if ( paramOne > 0) cmdObj = new Command_CLR(paramOne);
							break;
			case "SWAP":	cmdObj = new Command_Swap();
							break;
			case "RCL":		cmdObj = new Command_RCL();
							break;
			case "RESET":	cmdObj = new Command_Reset();
							break;
			default:		cmdObj = new Command_NULL();;
							break;
		}
		
		
		// If for some reason we couldn't make a valid command, let them know.
		if ( cmdObj instanceof Command_NULL ) {
			Printer.print("Invalid Command Entered.");
			return;
		}
		
		// Execute and add to the eventLog.
		cmdObj.execute();
		if ( isOn ) ChronoTimer.eventLog.add(event);
	}
	
	/**
	 * Formats the current run, or the most recently finished run into json.
	 * Then calls updateURL.
	 */
	public static void sendJson(){
		if ( !isOn ) return;

		RunGroup group = null;

		if ( current != null && !current.isEmpty() ) {
			group = current;
		} else if ( !archive.isEmpty() && !archive.get(archive.size()-1).isEmpty()) {
			group = archive.get(archive.size()-1);
		} else {
			// There is no rungroup with data.
			return;
		}

		String  item = "{\"event\":\"";

		String runs = "\"run\":\"";
		String bib = "\"bib\":\"";
		String start = "\"start\":\"";
		String finish = "\"finish\":\"";
		String elapsed = "\"elapsed\":\"";

		String toJson = "[";

		// Export completed runs.
		if ( !group.getCompletedRuns().isEmpty() ) {
			Iterator<Run> iterator = group.getCompletedRuns().iterator();
			while ( iterator.hasNext() ) {
				Run run = iterator.next();

				toJson += item + group.getEventType() + "\",";
				toJson += runs + run.getRunNum() + "\",";
				toJson += bib + run.getBibNum() + "\",";
				
				// Handle the special case of a person who is DNF but never started, 
				// only happens when a RunGroup.end is called.
				if ( run.getStartTime() == 0 ) {
					toJson += start + "DNF\",";
				} else {
					toJson += start + SystemTimer.convertLongToString(run.getStartTime()) + "\",";
				}

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
				toJson += elapsed + run.getElapsed() + "\"},";
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
		updateURL(toJson);
	}

	/**
	 * Sends the json to the server.
	 */
	private static void updateURL( String json ) {
		try {
			URL site = new URL("http://teambolttimer.appspot.com/server");
			HttpURLConnection conn = (HttpURLConnection) site.openConnection();

			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setRequestMethod("POST");

			String content = "data=" + json;
			DataOutputStream out = new DataOutputStream(conn.getOutputStream());
			out.writeBytes(content);
			out.flush();
			out.close();

			new InputStreamReader(conn.getInputStream());
		} catch ( Exception e ) {
			Printer.print("Error updating server");
		}
	}
	
	public static boolean isOn() {
		return isOn;
	}
	
	public static void setOn( boolean o ) {
		isOn = o;
	}

	public static Window getWindow() {
		return window;
	}

	public static String getEventType() {
		return eventType;
	}

	public static void setEventType(String eventType) {
		ChronoTimer.eventType = new String(eventType);
	}

	public static RunGroup getCurrent() {
		return current;
	}

	public static void setCurrent(RunGroup current) {
		ChronoTimer.current = current;
	}

	public static ArrayList<RunGroup> getArchive() {
		return archive;
	}

	public static ArrayList<Channel> getChannels() {
		return channels;
	}

	public static ArrayList<String> getEventLog() {
		return eventLog;
	}
}
