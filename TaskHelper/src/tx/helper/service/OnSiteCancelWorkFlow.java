package tx.helper.service;

public class OnSiteCancelWorkFlow extends WorkFlow {

	public OnSiteCancelWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "OnSiteCancelWorkFlow";
		this.status = WFStatus.DONE;
	}
	
	public OnSiteCancelWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		return "到场签到已经取消，您可以通过<a href=\"http://18621295953.duapp.com/myOrders.html?openid="+openId+"\">切换工单</a>来选择其他的工单。";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
