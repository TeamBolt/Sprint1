
public class Command_Conn implements Command {
	private String sensor;
	private int channelNum;
	
	public Command_Conn(String snsr, int chnl) {
		sensor = snsr;
		channelNum = chnl;
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
	}

}
