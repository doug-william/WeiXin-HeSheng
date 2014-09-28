package tx.helper.service;


public class LogoutOkWorkFlow extends WorkFlow {

	public LogoutOkWorkFlow(String openId){
		this.openId = openId;
		this.expire = WorkFlow.DEFAULT_EXPIRE;
		this.wfName = "LogoutOkWorkFlow";
	}
	
	@Override
	public String getPrompt() {
		this.status = WFStatus.DONE;
		return "下班打卡完成，谢谢！";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
