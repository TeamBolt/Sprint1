import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Timer;

public class SystemTimer {
	
	private static Timer timer;
	//private boolean isOn = true; //???not sure if we need this???
	private static Calendar cal = Calendar.getInstance();
	private static DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");
	private static long currentTime = cal.getTimeInMillis();

	public static void start(){
		timer = new Timer(1, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				++currentTime;	
			}
		});		
		timer.start();
	//	isOn = true;
	}
	
	public static void stop(){
		timer.stop();
	//	isOn = false;
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
		currentTime = time;
	}

	public static long getTime() {
		return currentTime;
	}

	public static String getTime(boolean time){
		//String t = dateFormat.format(currentTime);

		return convertLongToString(currentTime);//t;//t.substring(0,t.length()-1);
	}

}
