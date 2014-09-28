package tx.helper.service;

import tx.helper.module.TCall;

public class WXUser {
	
	private String openId;
	private String userName;
	private Location location;
	private String duty; //职位
	private WorkFlow workFlow;
	
	private String activeTaskId; // 用户当前正在处理的工单编号；
	
	private TCall activeCall;
	
	public TCall getActiveCall() {
		return activeCall;
	}

	public void setActiveCall(TCall activeCall) {
		this.activeCall = activeCall;
	}

	public WorkFlow getWorkFlow() {
		return workFlow;
	}

	public void setWorkFlow(WorkFlow workFlow) {
		this.workFlow = workFlow;
	}
	
	public void cancelWorkFlow(){
		if(this.workFlow != null){
			this.workFlow.cancel();
			this.workFlow = null;
		}
	}

	public WXUser(String openId){
		this.openId = openId;
	}
	
	public String getOpenId(){
		return this.openId;
	}
	
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public Location getLocation() {
		return location;
	}
	public void setLocation(Location location) {
		this.location = location;
	}

	public String getActiveTaskId() {
		return activeTaskId;
	}

	public void setActiveTaskId(String activeTaskId) {
		this.activeTaskId = activeTaskId;
	}

	public String getDuty() {
		return duty;
	}

	public void setDuty(String duty) {
		this.duty = duty;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj instanceof WXUser){
			WXUser user = (WXUser)obj;
			if(this.openId.equals(user.getOpenId())) return true;
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return this.openId+"<-->"+this.userName+":["+this.location+"]";
	}

}

