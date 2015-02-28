import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import javax.swing.Timer;

public class SystemTimer {
	private long currentTime;
	private Timer timer;
	//private boolean isOn = true; //???not sure if we need this???
	private Calendar cal = Calendar.getInstance();
	private DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss.S");

	public SystemTimer() {		
		currentTime = cal.getTimeInMillis();

		timer = new Timer(0, new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				++currentTime;							
			}
		});		
	}

	public void start(){
		timer.start();
	//	isOn = true;
	}
	public void stop(){
		timer.stop();
	//	isOn = false;
	}

	public void setTime(String time) {
		Date date;
		try {
			date = dateFormat.parse(time);
			System.out.println("new date: " + dateFormat.format(date));
			cal.setTime(date);
			currentTime = cal.getTimeInMillis();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public void setTime(long time){
		currentTime = time;
	}

	public long getTime() {
		return currentTime;
	}

	public String getTime(boolean time){
		//String t = dateFormat.format(currentTime);

		return  dateFormat.format(currentTime);//t;//t.substring(0,t.length()-1);
	}

}
