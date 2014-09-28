package tx.helper.module;

public class TCall {
	
	private String userId;  // 分配的用户Id
	
	private String callidentityid;
	
	private String calltitle;
	
	private String cname;
	
	private String pname;
	
	private String state;
	
	private String desc;
	
	public TCall(String userId,String callId,String state){
		this.userId = userId;
		this.callidentityid = callId;
		this.state = state;
	}
	
	public String getCalltitle() {
		return calltitle;
	}



	public void setCalltitle(String calltitle) {
		this.calltitle = calltitle;
	}



	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCallidentityid() {
		return callidentityid;
	}

	public void setCallidentityid(String callidentityid) {
		this.callidentityid = callidentityid;
	}

	public String getCname() {
		return cname;
	}

	public void setCname(String cname) {
		this.cname = cname;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
	
}
