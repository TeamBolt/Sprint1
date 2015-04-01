import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;

/**
 * Sensor object, has a type, and is usually connected to a channel.
 * 
 * Team Bolt ( Chris Harmon, Kevari Francis, Blake Watzke, Ben Kingsbury )
 * 
 */
public class Sensor extends JFrame {
	String type;
	int channelNum;
	
	public Sensor(String t, int c ){
		type = t;
		channelNum = c;
		
		setTitle(type + " on Channel #" + channelNum);
		setSize(200,100);
		setResizable(false);
		JButton button = new JButton("Trigger Sensor");
		button.addActionListener(new buttonListener());
		add(button);
		setVisible(true);
	}
	
	private class buttonListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ChronoTimer.readCommand(SystemTimer.getTime(), "trig " + channelNum );
		}
		
	}
}
