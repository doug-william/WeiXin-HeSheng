package tx.helper.service;

import java.util.Date;

public abstract class WorkFlow {
	
	public enum WFStatus {OPEN,DONE,CANCEL}
	
	public static int DEFAULT_EXPIRE = 60; // default timeout 60s.
	
	String wfName;
	
	String openId;
	
	WFStatus status = WFStatus.OPEN;
	
	int expire = DEFAULT_EXPIRE;
	
	Date activeTime = new Date();
	
	public WFStatus getStatus(){
		return this.status;
	}
	
	public void resetActiveTime(){
		this.activeTime = new Date();
	}
	
	public boolean checkTimeout(){
		// util.trace(this, "NowTime="+System.currentTimeMillis()+"; activeTime="+this.activeTime.getTime()+"; expire="+this.expire);
		if((System.currentTimeMillis()-this.activeTime.getTime())/1000 >= this.expire){
			return true;
		}
		return false;
	}
	
	public abstract String getPrompt();
	
	public void cancel(){
		this.status = WFStatus.CANCEL;
	}
	
	public abstract WorkFlow end(String res);
	
}


