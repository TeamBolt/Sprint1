package chronoTimerItems;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class SystemTimer {
	
	private static Calendar cal = Calendar.getInstance();
	private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
	private static long offset = 0;

	/**
	 * Converts string time into timestamp and sets offset.
	 * @param String time
	 */
	public static void setTime(String time) {
		setTime( convertStringToLong(time) );
	}
	
	/**
	 * Converts String time into timstamp and returns it.
	 * @param String time
	 * @return long timestamp
	 */
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
	
	/**
	 * Converts timestamp into String time.
	 * @param long timestamp
	 * @return String time
	 */
	public static String convertLongToString(long timestamp) {
		return dateFormat.format(timestamp);
		
	}
	
	/**
	 * Sets offset.
	 * @param long timestamp
	 */
	public static void setTime(long timestamp){
		offset = timestamp - System.currentTimeMillis();
	}

	/**
	 * Get the timestamp.
	 * @return long timestamp
	 */
	public static long getTime() {
		return offset + System.currentTimeMillis();
	}

	/**
	 * Get the string time
	 * @param boolean getString (to override)
	 * @return String time
	 */
	public static String getTime(boolean getString){
		return convertLongToString(getTime());
	}

}
