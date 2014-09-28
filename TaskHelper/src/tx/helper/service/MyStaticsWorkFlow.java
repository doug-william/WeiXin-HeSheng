package tx.helper.service;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.gson.Gson;

import tx.commons.util.Util;
import tx.helper.config.Config;

public class MyStaticsWorkFlow extends WorkFlow {

	private HelperService service = HelperService.getInstance();
	
	private Util util = Util.getInstance();
	
	public MyStaticsWorkFlow(String openId,int expire){
		this.openId = openId;
		this.expire = expire;
		this.wfName = "MyStaticsWorkFlow";
	}
	
	public MyStaticsWorkFlow(String openId){
		this(openId,WorkFlow.DEFAULT_EXPIRE);
	}
	
	@Override
	public String getPrompt() {
		
		this.status = WFStatus.DONE;
		
		WXUser user = service.getWXUser(this.openId);
		if(user != null){
			
			try{
				
				String startDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
				Calendar nowDate = Calendar.getInstance();
				nowDate.add(Calendar.DAY_OF_YEAR, 1);
				String endDate = new SimpleDateFormat("yyyy-MM-dd").format(nowDate.getTime());
				
				String res = service.invokeCRMHttpGet(4000,4000,"queryStats", "openid="+this.openId,"startdate="+startDate,"enddate="+endDate);
				
				util.trace(this, "<<<<< QueryStatics res="+res);
				
			   	if(res == null || res.length() == 0){
			   		return "请求超时，请稍候再试.";
			   	}
			   	
			   	Gson gson = new Gson();
			   	StaticsResult stat = gson.fromJson(res, StaticsResult.class);
		   		if("success".equals(stat.getResult())){
		   			return "【我的报修统计】\n 等待上门："+stat.getWait()+" \n 处理中："+stat.getProcessing()+" \n 完结工单："+stat.getFinished()+" \n <a href=\""+Config.appUrl+"/myStatics.html?openid="+openId+"\">查看更多报修统计信息</a>";
		   		}else{
		   			return new SysFailedWorkFlow("mystats", stat.getResult()).getPrompt();
		   		}
			}catch(Throwable e){
				util.error(this, e);
			}
			
			return "请求超时，请稍候再试.";
			
		}
		
		return "您尚未绑定帐号，请先<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Config.wxAppId+"&redirect_uri="+Config.appUrl+"/wxAuth2&response_type=code&scope=snsapi_base&state=login#wechat_redirect\">绑定帐号</a>";
	}

	@Override
	public WorkFlow end(String res) {
		return null;
	}

}

class StaticsResult {
	
	private String openid;
	private String result;
	private String wait;
	private String processing;
	private String finished;
	
	public String getOpenid() {
		return openid;
	}
	public void setOpenid(String openid) {
		this.openid = openid;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	public String getWait() {
		return wait;
	}
	public void setWait(String wait) {
		this.wait = wait;
	}
	public String getProcessing() {
		return processing;
	}
	public void setProcessing(String processing) {
		this.processing = processing;
	}
	public String getFinished() {
		return finished;
	}
	public void setFinished(String finished) {
		this.finished = finished;
	}
	
}
