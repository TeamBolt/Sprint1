
public class Printer {

	public static boolean isOn = false;
	
	public static void print(String output) {
		System.out.println(output);
		
		if ( isOn == true ) {
			// Also print to the printer, which we don't need to do yet.
		}
	}
}