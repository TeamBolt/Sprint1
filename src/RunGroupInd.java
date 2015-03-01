import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;




public class RunGroupInd implements RunGroup{

	public int runNum;
	public int startChannel;
	public int stopChannel;
	public LinkedBlockingQueue<Run> startQueue;
	public LinkedBlockingQueue<Run> finishQueue;
	public HashSet<Run> completedRuns;
	
	@Override
	public void trigger(int channel, long timestamp) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cancel() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dnf() {
		// TODO Auto-generated method stub
		
	}

}
