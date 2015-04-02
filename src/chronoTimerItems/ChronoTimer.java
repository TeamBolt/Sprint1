package chronoTimerItems;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import commands.Command;
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
import commands.Command_TIME;
import commands.Command_Toggle;
import commands.Command_Trig;


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
@SuppressWarnings("unused")
public class ChronoTimer {
	
	public static Window window;
	public static boolean isOn = false;
	public static String eventType;
	public static RunGroup current;
	public static ArrayList<RunGroup> archive = new ArrayList<RunGroup>();
	public static ArrayList<Channel> channels = new ArrayList<Channel>();
	public static ArrayList<String> eventLog = new ArrayList<String>();

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
			window = new Window();
			// Begin looping to read commands from console.
//			try {
//				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//				String input;
//				 
//				while( ( input = br.readLine() ) != null ) {
//					long timestamp = SystemTimer.getTime(); //save the timestamp first.
//					ChronoTimer.readCommand( timestamp, input );
//				}
//			} catch (IOException e) {
//				//e.printStackTrace();
//				Printer.print("Error reading from console.");
//			}
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
		// no parameter or paramter was not parse-able into an int.
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
			case "TIME": 	if ( args.length > 1 ) cmdObj = new Command_TIME(timestamp, args[1]);
							break;
			case "ON":		cmdObj = new Command_On(timestamp);
							break;
			case "OFF":		cmdObj = new Command_Off(timestamp);
							break;
			case "CONN":	if ( paramTwo > 0 && paramTwo < 9 ) cmdObj = new Command_Conn(timestamp, args[1], paramTwo);
							break;
			case "TOGGLE":	if ( paramOne > 0 && paramOne < 9 ) cmdObj = new Command_Toggle(timestamp, paramOne);
							break;
			case "TRIG":	if ( paramOne > 0 && paramOne < 9 ) cmdObj = new Command_Trig(timestamp, paramOne);
							break;
			case "NUM":		if ( paramOne > 0 ) cmdObj = new Command_Num(timestamp, paramOne);
							break;
			case "START":	cmdObj = new Command_Start(timestamp);
							break;
			case "FIN":		cmdObj = new Command_Fin(timestamp);
							break;
			case "DNF":		cmdObj = new Command_DNF(timestamp);
							break;
			case "PRINT":	if ( paramOne <= 0 ) cmdObj = new Command_Print(timestamp);
							if ( paramOne > 0) cmdObj = new Command_Print(timestamp, paramOne);
							break;
			case "DISC":	if ( paramOne > 0) cmdObj = new Command_Disc(timestamp, paramOne);
							break;
			case "CANCEL":	cmdObj = new Command_Cancel(timestamp);
							break;
			// The rest of these we don't necessarily need yet.
			case "EVENT":	if ( args.length > 1 ) cmdObj = new Command_Event(args[1]);
							break;
			case "NEWRUN":	cmdObj = new Command_Newrun(timestamp);
							break;
			case "ENDRUN":	cmdObj = new Command_Endrun(timestamp);
							break;
			case "EXPORT":	cmdObj = new Command_Export(paramOne);
							break;
			case "CLR":		cmdObj = new Command_NULL();
							break;
			case "SWAP":	cmdObj = new Command_NULL();
							break;
			case "RCL":		cmdObj = new Command_NULL();
							break;
			case "RESET":	cmdObj = new Command_Reset(timestamp);
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
}
