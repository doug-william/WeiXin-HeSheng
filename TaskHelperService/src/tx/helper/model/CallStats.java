package tx.helper.model;

public class CallStats {
	private int wait;
	private int processing;
	private int finished;
	
	public void setWait(int Wait) {
		wait = Wait;
	}
	
	public int getWait() {
		return wait;
	}

	public void setProcessing(int Processing) {
		processing = Processing;
	}
	
	public int getProcessing() {
		return processing;
	}
	
	public void setFinished(int Finished) {
		finished = Finished;
	}
	
	public int getFinished() {
		return finished;
	}
}
