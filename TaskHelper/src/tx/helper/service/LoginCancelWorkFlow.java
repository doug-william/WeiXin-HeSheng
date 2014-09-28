package tx.helper.service;

public class LoginCancelWorkFlow extends WorkFlow {

	public LoginCancelWorkFlow(String openId){
		this.openId = openId;
		this.expire = WorkFlow.DEFAULT_EXPIRE;
		this.wfName = "LoginCancelWorkFlow";
	}
	
	@Override
	public String getPrompt() {
		this.status = WFStatus.DONE;
		return "上班打卡已经取消，你可以进行其他的操作。";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
