import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.print.DocFlavor.URL;


public class Keypad {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		java.net.URL url = Keypad.class.getClassLoader().getResource("test.txt");
		
		Keypad.readTestFile(url.getPath());

	}
	
	public static void readTestFile(String filename) {
		// Maybe we should pause the system timer for the duration of this function
		// so that command timestamps are accurate.
		
		
		try( BufferedReader br = new BufferedReader(new FileReader(filename))) {
			for (String line; (line = br.readLine()) != null; ) {
				
				// First get the timestamp out and set the time
				String[] args = line.split("	");
				Command setTime = new Command_TIME(args[0]);
				setTime.execute();
				
				//Then send the command to readCommand()
				Keypad.readCommand(args[1]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void readCommand(String command) {
		String[] args = command.split(" ");
		String name = args[0];
		System.out.println(name);
		Command cmdObj;
		
		// Create the appropriate command object.
		switch (name) {
			case "TIME": 	cmdObj = new Command_TIME(args[1]);
							break;
			case "ON":		cmdObj = new Command_NULL();
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
		
	}

}
