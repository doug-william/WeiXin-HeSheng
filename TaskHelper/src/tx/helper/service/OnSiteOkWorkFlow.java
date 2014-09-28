package tx.helper.service;

public class OnSiteOkWorkFlow extends WorkFlow {

	public OnSiteOkWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "OnSiteOkWorkFlow";
		this.status = WFStatus.DONE;
	}
	
	public OnSiteOkWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		return "到场签到已完成，记得离场时签出哦。";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
