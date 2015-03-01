import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;




public class ChronoTimer {
	
	public static boolean isOn;
	public static RunGroup current;
	public static ArrayList<RunGroup> archive;
	public static ArrayList<Channel> channels;
	public static HashSet<String> eventLog = new HashSet<String>();

	/**
	 * @param args
	 * 
	 * To run the class with the test file, use "run configurations" and set a 
	 * program arguments test.txt.
	 */
	public static void main(String[] args) {
		if ( args.length > 0 ) {
			java.net.URL url = Keypad.class.getClassLoader().getResource(args[0]);
			ChronoTimer.readTestFile(url.getPath());
		} else {
			
			boolean exit = false;
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String input;
				 
				while( (input = br.readLine() ) != null ) {
					if ( input.equals("EXIT") ) System.exit(0);
					
					// We need to get the current timestamp from the system timer.
					long timestamp = SystemTimer.getTime();
					ChronoTimer.readCommand(timestamp, input);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		

	}
	
	public static void readTestFile(String filename) {
		// Maybe we should pause the system timer for the duration of this function
		// so that command timestamps are accurate.
		
		
		try( BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null; ) {
				
				// First get the timestamp out and set the time
				String[] args = line.split("	");
				
				// We need to convert this string time into timestamp
				long timestamp = SystemTimer.convertStringToLong(args[0]);
				
				//Then send the command to readCommand()
				ChronoTimer.readCommand(timestamp, args[1]);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void readCommand(long timestamp, String command) {
		String[] args = command.split(" ");
		String name = args[0];
		Command cmdObj;
		String event = SystemTimer.convertLongToString(timestamp) + "	" + command;
		
		int paramOne = 0;
		int paramTwo = 0;
		if ( args.length > 1 && args[1] != null ) {
			paramOne = Integer.parseInt(args[1]);
		}
		if ( args.length > 2 && args[2] != null ) {
			paramTwo = Integer.parseInt(args[1]);
		}
		
		// Create the appropriate command object.
		switch (name) {
			case "TIME": 	cmdObj = new Command_TIME(timestamp, args[1]);
							break;
			case "ON":		cmdObj = new Command_On();
							break;
			case "OFF":		cmdObj = new Command_NULL();
							break;
			case "EXIT":	cmdObj = new Command_NULL();
							break;
			case "CONN":	cmdObj = new Command_NULL();
							break;
			case "TOGGLE":	cmdObj = new Command_NULL();
							break;
			case "NUM":		cmdObj = new Command_NULL();
							break;
			case "START":	cmdObj = new Command_NULL();
							break;
			case "FIN":		cmdObj = new Command_NULL();
							break;
			case "DNF":		cmdObj = new Command_NULL();
							break;
			case "PRINT":	cmdObj = new Command_NULL();
							break;
			case "DISC":	cmdObj = new Command_NULL();
							break;
			case "CANCEL":	cmdObj = new Command_NULL();
							break;
			// Ther rest of these we don't neccessarily need yet.
			case "EVENT":	cmdObj = new Command_NULL();
							break;
			case "NEWRUN":	cmdObj = new Command_NULL();
							break;
			case "ENDRUN":	cmdObj = new Command_NULL();
							break;
			case "EXPORT":	cmdObj = new Command_NULL();
							break;
			case "CLR":		cmdObj = new Command_NULL();
							break;
			case "SWAP":	cmdObj = new Command_NULL();
							break;
			case "RCL":		cmdObj = new Command_NULL();
							break;
			case "RESET":	cmdObj = new Command_NULL();
							break;
			default:		cmdObj = new Command_NULL();
							System.out.println("Invalid Command Entered");
							break;
				
		}
		
		cmdObj.execute();
		ChronoTimer.eventLog.add(event);
		System.out.println(event);
		
	}
}
