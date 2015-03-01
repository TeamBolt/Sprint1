
public class Command_Disc implements Command {
	private long timestamp;
	private int channelNum;
	
	public Command_Disc(long t, int chnl) {
		channelNum = chnl;
		timestamp = t;
	}	
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub

	}

}
