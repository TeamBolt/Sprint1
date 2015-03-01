
public class Command_Conn implements Command {
	private long timestamp;
	private String sensor;
	private int channelNum;
	
	public Command_Conn(long t, String snsr, int chnl) {
		sensor = snsr;
		channelNum = chnl;
		timestamp = t;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

}
