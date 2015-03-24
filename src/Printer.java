import java.util.ArrayList;

/**
 * Printer class represents both the printer and the console.
 * Prints to the console always, and to the printer when it isOn.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 * @author Chris Harmon
 */
public class Printer {

	public static boolean isOn = false;
	public static ArrayList<String> log = new ArrayList<String>();
	
	/**
	 * Print output to the console (and later the printer when enabled)
	 * @param output
	 */
	public static void print(String output) {
		log.add(output);
		System.out.println(output);
		ChronoTimer.window.updateConsole(output);
		
		if ( isOn == true ) {
			// Also print to the printer, which we don't need to do yet.
		}
	}
}
