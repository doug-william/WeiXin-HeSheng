package tx.helper.service;

import tx.helper.config.Config;

public class MyInfoWorkFlow extends WorkFlow {

	private HelperService service = HelperService.getInstance();
	
	public MyInfoWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "MyInfoWorkFlow";
	}
	
	public MyInfoWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		this.status = WFStatus.DONE;
		WXUser user = service.updateWXUser(this.openId);
		if(user != null){
			return "【当前登录情况】\n 姓名："+user.getUserName()+"\n 职位："+user.getDuty();
		}
		return "您尚未绑定帐号，请先<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Config.wxAppId+"&redirect_uri="+Config.appUrl+"/wxAuth2&response_type=code&scope=snsapi_base&state=login#wechat_redirect\">绑定帐号</a>";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}
