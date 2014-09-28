package tx.helper.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

import tx.commons.util.Util;
import tx.helper.config.Config;
import tx.helper.module.CRMResult;
import tx.helper.util.WXUtil;

public class LoginWorkFlow extends WorkFlow {
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private String location = "";
	
	public LoginWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "LoginWorkFlow";
	}
	
	public LoginWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt(){
		WXUser user = service.getWXUser(this.openId);
		if(user != null){
			if(user.getLocation() == null){
				this.status = WFStatus.DONE;
				//return "上班打卡，您目前的位置未知，请打开GPS或者到室外信号好的地方再试。或者请关闭当前对话框口然后再重试；如还不行请确认已经允许微信能够访问你的位置信息，具体设置可以参考手册或询问管理员。";
				return "上班打卡，您目前的位置未知。\n\n<a href=\""+Config.appUrl+"/loginlogoutNote.html\">立即点击查阅解决方法。</a>";
			}
			location = WXUtil.convertBDAxis(user.getLocation().getLatitude(), user.getLocation().getLongitude());
			return "上班打卡，您目前的位置: "+WXUtil.geocoderBMap(location)+", 确认请回复\"1\",取消请回复\"0\" ";
		}else{
			this.status = WFStatus.CANCEL;
			return "您尚未绑定帐号，请先<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Config.wxAppId+"&redirect_uri="+Config.appUrl+"/wxAuth2&response_type=code&scope=snsapi_base&state=login#wechat_redirect\">绑定帐号</a>";
			//return "上班打卡，您目前的位置暂时无法获取，请关闭当前对话框口然后再重试；如还不行请确认已经允许微信能够访问你位置信息，具体设置可以参考手册或询问管理员。";
		}
	}
	
	@Override
	public WorkFlow end(String res){
		if("1".equals(res)){
			String loginTime = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date());
			util.trace(this, "User["+openId+"] Login ok @"+loginTime);
			WXUser user = service.getWXUser(this.openId);
			if(user != null && user.getLocation() != null){
				// location = String.valueOf(user.getLocation().getLongitude())+","+String.valueOf(user.getLocation().getLatitude());
				// location = WXUtil.geocoderBMap(user.getLocation().getLatitude(),user.getLocation().getLongitude());
				user.setLocation(null);
			}
			if(location.split(",").length>1) location = location.split(",")[1]+","+location.split(",")[0];
		   	String response = service.invokeCRMHttpGet(3000,2000,"login", "openid="+openId,"logintime="+loginTime,"location="+location,"loginmode=login");
		   
		   	if(response != null && response.length()>0){
		   		Gson gson = new Gson();
		   		CRMResult result = gson.fromJson(response, CRMResult.class);
		   		if("0".equals(result.getResult())){
		   			return new LoginOkWorkFlow(this.openId);
		   		}else{
		   			return new SysFailedWorkFlow("login", result.getResult());
		   		}
		   	}else{
		   		util.warn(this, "UserLogin failure for response is null.");
		   		return new SysFailedWorkFlow("login", "请求超时，请稍候再试.");
		   	}
		}else{
			util.trace(this, "User["+this.openId+"] login cancel.");
			return new LoginCancelWorkFlow(this.openId);
		}
	}

}
