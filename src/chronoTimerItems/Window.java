package chronoTimerItems;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;


@SuppressWarnings("serial")
public class Window extends JFrame {
	
	private TextArea display;
	private TextArea printer;
	private TextField textField;
	private JButton onButton;
	private JButton printOnButton;
	private Timer timer;
	private ArrayList<JButton> channelButtons = new ArrayList<JButton>();
	private ArrayList<JCheckBox> channelChecks = new ArrayList<JCheckBox>();

	public Window() throws HeadlessException {
		timer = new Timer(1,
				new ActionListener()
				{
				public void actionPerformed(ActionEvent e) 
					{
						if ( ChronoTimer.isOn == true ) {
							if ( ChronoTimer.current != null ) {
								display.setText(ChronoTimer.current.doPrint());
							}
						}
					}					
				});
		
		setTitle("ChronoTimer");
		setLayout(new GridLayout(1,3));
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setSize(1250,500);
		setResizable(false);
		createContents();
		timer.start();
		setVisible(true);
	}
	
	public void createContents() {
		JPanel left = new JPanel(new BorderLayout());
		JPanel right = new JPanel(new FlowLayout());
		JPanel center = new JPanel(new FlowLayout());

		
		
		// Left

		JPanel title = new JPanel(new FlowLayout());
		JPanel channels = new JPanel( new GridLayout(8,2));
		JButton chanButt1	= new JButton("Trig 1");
		JButton chanButt2	= new JButton("Trig 2");
		JButton chanButt3	= new JButton("Trig 3");
		JButton chanButt4	= new JButton("Trig 4");
		JCheckBox chanCheck1	= new JCheckBox("Enabled 1");
		JCheckBox chanCheck2	= new JCheckBox("Enabled 2");
		JCheckBox chanCheck3	= new JCheckBox("Enabled 3");
		JCheckBox chanCheck4	= new JCheckBox("Enabled 4");
		JButton chanButt5	= new JButton("Trig 5");
		JButton chanButt6	= new JButton("Trig 6");
		JButton chanButt7	= new JButton("Trig 7");
		JButton chanButt8	= new JButton("Trig 8");
		JCheckBox chanCheck5	= new JCheckBox("Enabled 5");
		JCheckBox chanCheck6	= new JCheckBox("Enabled 6");
		JCheckBox chanCheck7	= new JCheckBox("Enabled 7");
		JCheckBox chanCheck8	= new JCheckBox("Enabled 8");
		
		channelButtons.add(chanButt1);
		channelButtons.add(chanButt2);
		channelButtons.add(chanButt3);
		channelButtons.add(chanButt4);
		channelButtons.add(chanButt5);
		channelButtons.add(chanButt6);
		channelButtons.add(chanButt7);
		channelButtons.add(chanButt8);
		
		channelChecks.add(chanCheck1);
		channelChecks.add(chanCheck2);
		channelChecks.add(chanCheck3);
		channelChecks.add(chanCheck4);
		channelChecks.add(chanCheck5);
		channelChecks.add(chanCheck6);
		channelChecks.add(chanCheck7);
		channelChecks.add(chanCheck8);
		
		for ( JButton b : channelButtons ) {
			b.addActionListener(new ChannelButtonListener());
		}
		for ( JCheckBox c : channelChecks ) {
			c.addActionListener(new ChannelCheckListener());
		}

		channels.add(chanButt1);
		channels.add(chanButt2);
		channels.add(chanCheck1);
		channels.add(chanCheck2);
		channels.add(chanButt3);
		channels.add(chanButt4);
		channels.add(chanCheck3);
		channels.add(chanCheck4);
		channels.add(chanButt5);
		channels.add(chanButt6);
		channels.add(chanCheck5);
		channels.add(chanCheck6);
		channels.add(chanButt7);
		channels.add(chanButt8);
		channels.add(chanCheck7);
		channels.add(chanCheck8);
		title.add(new JLabel("Channels"));
		left.add(channels, BorderLayout.CENTER);
	
		// Right
		printer = new TextArea("",25,38);
		printer.setEditable(false);
		printer.setBackground(Color.LIGHT_GRAY);
		printer.setForeground(Color.GREEN);
		printOnButton = new JButton("Turn Printer On");
		printOnButton.addActionListener(new PrintOnButtonListener());
		right.add(printOnButton);
		right.add(printer);
		
		
		// Center
		display = new TextArea("",23,38);
		display.setEditable(false);
		display.setBackground(Color.LIGHT_GRAY);
		display.setForeground(Color.GREEN);
		textField = new TextField("",30);
		textField.addActionListener(new TextListener());
		JPanel bc1 = new JPanel(new FlowLayout());
		JPanel bc2 = new JPanel(new FlowLayout());
		bc1.add(display);
		bc2.add(textField);
		onButton = new JButton("Turn On");
		onButton.addActionListener(new OnButtonListener());
		center.add(onButton);
		center.add(bc1);
		center.add(bc2);

		
		add(left);
		add(center);
		add(right);
		
		refresh();

	}
	
	private class TextListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			String text = e.getActionCommand();
			if ( ChronoTimer.isOn == false && !text.equalsIgnoreCase("ON")) {
				textField.setText("Duno Why This Works.");
				textField.setText("");
				return;
			}
			long ts = SystemTimer.getTime();
			
			ChronoTimer.readCommand(ts, text);
			
			refresh();
		}
	}
	
	private class OnButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			if ( ChronoTimer.isOn == true ) {
				ChronoTimer.readCommand(SystemTimer.getTime(), "OFF");
			} else {
				ChronoTimer.readCommand(SystemTimer.getTime(), "ON");
			}
			
			refresh();
		}
	}
	
	private class ChannelButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			String name = e.getActionCommand();
			name = name.substring(name.length()-1);
			
			System.out.println(name);

			if ( ChronoTimer.isOn == true ) {
				ChronoTimer.readCommand(SystemTimer.getTime(), "TRIG " + name );
			}
			
			refresh();
		}
	}
	
	private class ChannelCheckListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			String name = e.getActionCommand();
			name = name.substring(name.length()-1);
			
			if ( ChronoTimer.isOn == true ) {
				ChronoTimer.readCommand(SystemTimer.getTime(), "TOGGLE " + name );
			}
			
			refresh();
		}
	}
	
	private class PrintOnButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			if ( Printer.isOn == true ) {
				Printer.isOn = false;
				printOnButton.setText("Turn Printer On");
			} else {
				Printer.isOn = true;
				printOnButton.setText("Turn Printer Off");
			}
			
			refresh();
		}
	}
	
	private void refresh() {
		if ( ChronoTimer.isOn == true ) {
			display.setBackground(Color.BLACK);
			onButton.setText("Turn Off");
		} else {
			display.setBackground(Color.LIGHT_GRAY);
			onButton.setText("Turn On");
		}
		
		if ( Printer.isOn == true ) {
			printer.setBackground(Color.BLACK);
		} else {
			printer.setBackground(Color.LIGHT_GRAY);
		}
		
		textField.setText("Duno Why This Works.");
		textField.setText("");
		
		for ( Channel c : ChronoTimer.channels ) {
			JCheckBox check = channelChecks.get(c.channelNum - 1);
			if ( c.enabled == true ) {
				check.setSelected(true);
			} else {
				check.setSelected(false);
			}
		}
		
		display.setText("Duno Why This Works.");
		display.setText("");
		
		//revalidate();
		//repaint();
	}
	
	public void updatePrinter(String text) {
		printer.append(text + "\n");
	}
}


