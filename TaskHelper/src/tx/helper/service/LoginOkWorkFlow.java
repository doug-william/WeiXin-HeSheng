package tx.helper.service;

public class LoginOkWorkFlow extends WorkFlow {

	public LoginOkWorkFlow(String openId){
		this.openId = openId;
		this.expire = WorkFlow.DEFAULT_EXPIRE;
		this.wfName = "LoginOkWorkFlow";
	}
	
	@Override
	public String getPrompt() {
		this.status = WFStatus.DONE;
		return "上班打卡完成，谢谢！";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}
	
	

}
