package tx.helper.service;


public class LogoutCancelWorkFlow extends WorkFlow {
	
	public LogoutCancelWorkFlow(String openId){
		this.openId = openId;
		this.expire = WorkFlow.DEFAULT_EXPIRE;
		this.wfName = "LogoutCancelWorkFlow";
	}

	@Override
	public String getPrompt() {
		this.status = WFStatus.DONE;
		return "下班打卡已经取消，你可以进行其他的操作。";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
