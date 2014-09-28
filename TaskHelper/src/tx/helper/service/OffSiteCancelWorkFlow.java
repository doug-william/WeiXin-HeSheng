package tx.helper.service;

public class OffSiteCancelWorkFlow extends WorkFlow {

	public OffSiteCancelWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "OffCancelWorkFlow";
	}
	
	public OffSiteCancelWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		this.status = WFStatus.DONE;
		return "离场签到已取消。";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
