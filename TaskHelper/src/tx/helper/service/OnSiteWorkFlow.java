package tx.helper.service;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

import tx.commons.util.Util;
import tx.helper.config.Config;
import tx.helper.module.CRMResult;
import tx.helper.module.TCall;
import tx.helper.util.WXUtil;

public class OnSiteWorkFlow extends WorkFlow {

	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	public OnSiteWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "OnSiteWorkFlow";
	}
	
	public OnSiteWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		WXUser user = service.getWXUser(this.openId);
		if(user != null){
			TCall activeCall = service.updateTCall(this.openId);
			if(activeCall != null){
				String stateDesc="";
				if(user.getActiveCall().getState().equalsIgnoreCase("wait")){
					stateDesc="待上门";
					if(user.getLocation() == null)
						return "【到场签到确认】\n 位置未知！\n 您正在处理工单信息：\n 工单编号："+user.getActiveCall().getCalltitle()+"\n 状态："+stateDesc+"\n 客户名称："+user.getActiveCall().getCname()+"\n 问题描述："+user.getActiveCall().getDesc()+" \n 确认到场签到请回复\"1\",取消签到请回复\"0\"";
					else
						return "【到场签到确认】\n 您正在处理工单信息：\n 工单编号："+user.getActiveCall().getCalltitle()+"\n 状态："+stateDesc+"\n 客户名称："+user.getActiveCall().getCname()+"\n 问题描述："+user.getActiveCall().getDesc()+" \n 确认到场签到请回复\"1\",取消签到请回复\"0\"";
				}
				else if(user.getActiveCall().getState().equalsIgnoreCase("work")){
					stateDesc="处理中";
					this.status = WFStatus.DONE;
				}
				else if(user.getActiveCall().getState().equalsIgnoreCase("offsite")){
					stateDesc="已离场";
					this.status = WFStatus.DONE;
				}
				else if(user.getActiveCall().getState().equalsIgnoreCase("finished")){
					stateDesc="已完成";
					this.status = WFStatus.DONE;
				}
				else{
					stateDesc="无效";
					this.status = WFStatus.DONE;
				}
				return "【到场签到】\n 无法执行此项操作\n 工单处于【" + stateDesc+ "】状态。\n\n 工单编号："+user.getActiveCall().getCalltitle()+"\n 客户名称："+user.getActiveCall().getCname()+"\n 问题描述："+user.getActiveCall().getDesc();
			}else {
				this.status = WFStatus.DONE;
				return "您还没有选择工单，请先<a href=\"http://mvsengineertaskhelper.duapp.com/myOrders.html?openid="+openId+"\">选择工单</a>.";
			}
		}else{
			this.status = WFStatus.DONE;
			return "您尚未绑定帐号，请先<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Config.wxAppId+"&redirect_uri="+Config.appUrl+"/wxAuth2&response_type=code&scope=snsapi_base&state=login#wechat_redirect\">绑定帐号</a>";
		}
	}

	@Override
	public WorkFlow end(String res) {
		if("1".equals(res)){
			String loginTime = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date());
			util.trace(this, "User["+openId+"] OnSite ok @"+loginTime);
			WXUser user = service.getWXUser(this.openId);
			String location = "";
			if(user != null && user.getLocation() != null){
				location = String.valueOf(user.getLocation().getLongitude())+","+String.valueOf(user.getLocation().getLatitude());
				// location = WXUtil.geocoderBMap(user.getLocation().getLatitude(),user.getLocation().getLongitude());
				location = WXUtil.convertBDAxis(user.getLocation().getLatitude(), user.getLocation().getLongitude());
				user.setLocation(null);
			}
			//if(location.split(",").length>1) location = location.split(",")[1]+","+location.split(",")[0];
		   	String response = service.invokeCRMHttpGet(4000,4000,"onSite", "openid="+openId,"onsitetime="+loginTime,"location="+WXUtil.geocoderBMap(location),"callidentityid="+user.getActiveCall().getCallidentityid());
		   
		   	if(response != null && response.length()>0){
		   		Gson gson = new Gson();
		   		CRMResult result = gson.fromJson(response, CRMResult.class);
		   		if("success".equals(result.getResult())){
		   			return new OnSiteOkWorkFlow(this.openId);
		   		}else{
		   			return new SysFailedWorkFlow("onsite", result.getResult());
		   		}
		   	}else{
		   		util.warn(this, "OnSite failure for response is null.");
		   		return new SysFailedWorkFlow("onsite", "请求超时，请稍候再试.");
		   	}
		}else{
			util.trace(this, "OnSite cancel.");
			return new OnSiteCancelWorkFlow(this.openId);
		}
	}

}
