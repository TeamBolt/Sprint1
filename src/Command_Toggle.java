
public class Command_Toggle implements Command {
	private long timestamp;
	private int channel;
	
	public Command_Toggle(long t, int chnl) {
		channel = chnl;
		timestamp = t;
	}


	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

}
