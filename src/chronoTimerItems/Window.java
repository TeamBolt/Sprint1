package chronoTimerItems;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.*;


/**
 * This is the UI for the ChronoTimer.
 * 
 * @author Chris Harmon
 */
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
	private int count = 0;

	/**
	 * Constructor for the window. Instantiates the timer which updates the display
	 * as well as the server, then calls createContents to get the window built.
	 * @throws HeadlessException
	 */
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
							if ( count >= 1000 ) {
								ChronoTimer.sendJson();
								count = 0;
							}
							++count;
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
	
	/**
	 * Creates the window and adds action listeners.
	 */
	public void createContents() {
		JPanel left = new JPanel(new BorderLayout());
		JPanel right = new JPanel(new BorderLayout());
		JPanel center = new JPanel(new BorderLayout());

		// Left Panel.
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
		left.add(title, BorderLayout.NORTH);
		left.add(channels, BorderLayout.CENTER);
	
		// Right Panel.
		printer = new TextArea("",26,38);
		printer.setEditable(false);
		printer.setBackground(Color.LIGHT_GRAY);
		printer.setForeground(Color.GREEN);
		printOnButton = new JButton("Turn Printer On");
		printOnButton.addActionListener(new PrintOnButtonListener());
		JPanel r1 = new JPanel(new FlowLayout());
		JPanel r2 = new JPanel(new FlowLayout());
		r1.add(printOnButton);
		r2.add(printer);
		right.add(r1, BorderLayout.NORTH);
		right.add(r2, BorderLayout.CENTER);
		
		
		// Center Panel.
		display = new TextArea("",24,38);
		display.setEditable(false);
		display.setBackground(Color.LIGHT_GRAY);
		display.setForeground(Color.GREEN);
		textField = new TextField("",30);
		textField.addActionListener(new TextListener());
		JPanel bc1 = new JPanel(new FlowLayout());
		JPanel bc2 = new JPanel(new FlowLayout());
		JPanel bc3 = new JPanel(new FlowLayout());
		onButton = new JButton("Turn On");
		onButton.addActionListener(new OnButtonListener());
		bc1.add(onButton);
		bc2.add(display);
		bc3.add(textField);
		center.add(bc1, BorderLayout.NORTH);
		center.add(bc2, BorderLayout.CENTER);
		center.add(bc3, BorderLayout.SOUTH);

		// Now add all the panels to the window.
		add(left);
		add(center);
		add(right);
		refresh();
	}
	
	/**
	 * Action listener for the text input.
	 */
	private class TextListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			String text = e.getActionCommand();
			if ( ChronoTimer.isOn == false && !text.equalsIgnoreCase("ON") && !text.equalsIgnoreCase("EXIT")) {
				textField.setText("Duno Why This Works.");
				textField.setText("");
				return;
			}
			long ts = SystemTimer.getTime();
			
			ChronoTimer.readCommand(ts, text);
			
			refresh();
		}
	}
	
	/**
	 * Action listener for the ON button.
	 */
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
	
	/**
	 * Action listener for the channel buttons.
	 */
	private class ChannelButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	
			String name = e.getActionCommand();
			name = name.substring(name.length()-1);
			
			if ( ChronoTimer.isOn == true ) {
				ChronoTimer.readCommand(SystemTimer.getTime(), "TRIG " + name );
			}
			
			refresh();
		}
	}
	
	/**
	 * Action listener for the channel check boxes.
	 */
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
	
	/**
	 * Action listener for the printer ON button.
	 */
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
	
	/**
	 * Private method used to update the window after any changes.
	 */
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
			JCheckBox check = channelChecks.get(c.getChannelNum() - 1);
			if ( c.isEnabled() == true ) {
				check.setSelected(true);
			} else {
				check.setSelected(false);
			}
		}
		
		display.setText("Duno Why This Works.");
		display.setText("");
	}
	
	/**
	 * Prints the given text to the printer text area.
	 */
	public void updatePrinter(String text) {
		printer.append(text + "\n");
	}
}