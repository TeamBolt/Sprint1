import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SystemTimer {
	
	private static Calendar cal = Calendar.getInstance();
	private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
	private static long offset = 0;

	public static void start(){
		
	}
	public static void stop(){

	}

	public static void setTime(String time) {
		setTime( convertStringToLong(time) );
	}
	
	public static long convertStringToLong(String time) {
		// Add the decimal point if it's not there so the parser is happy.
		if ( false == time.contains(".") ) {
			time = time + ".0";
		}
		
		Date date;
		long timestamp = 0;
		try {
			date = dateFormat.parse(time);
			cal.setTime(date);
			timestamp = cal.getTimeInMillis();
		} catch (ParseException e) {
			System.out.println("Invalid time entered.");
		}
		
		return timestamp;
	}
	
	public static String convertLongToString(long time) {
		return dateFormat.format(time);
		
	}

	public static void setTime(long time){
		offset = time - System.currentTimeMillis();
	}

	public static long getTime() {
		return offset + System.currentTimeMillis();
	}

	public static String getTime(boolean time){
		//String t = dateFormat.format(currentTime);

		return convertLongToString(getTime());
	}

}
