import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;


public class Window extends JFrame {
	
	private TextArea textArea;
	private TextField textField;
	private JButton onButton;

	public Window() throws HeadlessException {
		setTitle("ChronoTimer");
		setSize(620,300);
		setLayout(new BorderLayout());
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		createContents();
		setVisible(true);
	}
	
	public void createContents() {
		JPanel controlPanel = new JPanel();
		JPanel console = new JPanel();
		JPanel textEntry = new JPanel();
		
		controlPanel.setLayout(new GridLayout(1,3));
		JPanel c1 = new JPanel(new FlowLayout());
		JPanel c2 = new JPanel(new FlowLayout());
		JPanel c3 = new JPanel(new FlowLayout());
		onButton = new JButton("Turn On");
		onButton.addActionListener(new OnButtonListener());
		c1.add(onButton);
		c2.add(new JLabel(""));
		c3.add(new JLabel(""));
		controlPanel.add(c1);
		controlPanel.add(c2);
		controlPanel.add(c3);
		
		textArea = new TextArea();
		textArea.setEnabled(false);
		console.add(textArea);
		textField = new TextField("",40);
		textField.addActionListener(new TextListener());
		JPanel t = new JPanel(new FlowLayout());
		t.add(textField);
		textEntry.add(t);
		
		
		add(controlPanel, BorderLayout.NORTH);
		add(console, BorderLayout.CENTER);
		add(textEntry, BorderLayout.SOUTH);
	}
	
	private class TextListener implements ActionListener
	{
		public void actionPerformed(ActionEvent e)
		{	long ts = SystemTimer.getTime();
			String text = e.getActionCommand();
			
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
	
	private void refresh() {
		if ( ChronoTimer.isOn == true ) {
			onButton.setText("Turn Off");
		} else {
			onButton.setText("Turn On");
		}
		
		textField.setText("Duno Why This Works.");
		textField.setText("");
		
		//revalidate();
		//repaint();
	}
	
	public void updateConsole(String text) {
		textArea.append(text + "\n");
	}
}


