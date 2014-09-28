package tx.helper.service;

import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import com.google.gson.Gson;

import tx.commons.util.Util;
import tx.helper.config.Config;
import tx.helper.module.CRMEmployeeInfo;
import tx.helper.module.TCall;
import tx.helper.module.TCallResult;
import tx.helper.service.WorkFlow.WFStatus;
import tx.helper.util.WXUtil;

class UploadPicTask implements Runnable {

	private Util util = Util.getInstance();
	
	private String token;
	private String mediaId;
	private String destName;
	private String openid;
	private String orderId;
	private HelperService service;
	
	public UploadPicTask(String openid,String token,String mediaId,String destName,String orderId,HelperService service){
		this.token = token;
		this.mediaId = mediaId;
		this.destName = destName;
		this.openid = openid;
		this.orderId = orderId;
		this.service = service;
	}
	
	@Override
	public void run() {
		
			try{
				
				util.trace(this, "UploadPic begin token="+this.token+";meidaId="+mediaId+";destName="+destName);
				String fileName = WXUtil.upLoadImage(this.openid,this.token, this.mediaId, Config.bdBucket, destName);
				
				if(fileName != null && fileName.length()>0){
					
					util.trace(this, ">>>>> UploadPic picUrl="+fileName+";callId="+orderId+";openid="+openid);
				
					String[] fileNameRes = fileName.split(";");
				
					String res = service.invokeCRMHttpGet(10000,5000,"uploadPic", "openid="+openid,"callidentityid="+this.orderId,"picuri="+fileNameRes[0],"size="+fileNameRes[1],"contenttype=image/jpeg");
				
					util.trace(this, "<<<<< UploadPic res="+res);
				
					if(res == null || res.length() == 0){
						util.error(this, "UploadPic error.");
					}
				}
				
			}catch(Throwable e){
				util.error(this, e);
				service.postMessageToUser(this.openid, "上传照片失败,请与管理员联系。");
			}	
	}
}


public class HelperService {

	private Util util = Util.getInstance();

	private static HelperService helper;

	private int invokeId = 1;
	
	private HelperService() {
		// 定时刷新用户的会话状态；
		new Timer("WXUserWFTimer",false).schedule(new TimerTask(){

			@Override
			public void run() {
				try{
					synchronized(userMaps){
						Set<String> userIds = userMaps.keySet();
						for(String userId : userIds){
							WXUser wxUser = userMaps.get(userId);
							if(wxUser != null && wxUser.getWorkFlow() != null){
								if(wxUser.getWorkFlow().getStatus() == WFStatus.OPEN){
									if(wxUser.getWorkFlow().checkTimeout()){
										wxUser.getWorkFlow().cancel();
									}
								}else{
									wxUser.getWorkFlow().cancel();
								}
							}
						}
					}
					
				}catch(Throwable e){
					Util.getInstance().error(this, e);
				}
			}
		}, 0, 2000);
	}

	private String ServiceToken = null;
	
	// private TaskDAO dao = TaskDAO.getInstance();
	
	private Map<String,WXUser> userMaps = new HashMap<String,WXUser>();
	
	private String CRMToken = null;
	
	public String getCRMToken(){
		return this.CRMToken;
	}

	public static HelperService getInstance() {
		if (helper == null) helper = new HelperService();
		return helper;
	}
	
	public void addWXUser(WXUser user){
		this.userMaps.put(user.getOpenId(), user);
	}
	
	public WXUser getWXUser(String openId){
		return this.userMaps.get(openId);
	}
	
	public void updateWXUser(WXUser user){
		this.userMaps.put(user.getOpenId(), user);
	}

	public String getServiceToken() {
		return ServiceToken;
	}

	public void updateServiceToken() {

		try {
			this.getToken(Config.wxAppId, Config.wxSecret);
		} catch (Throwable e) {
			util.error(this, "Update token failure for " + e.getMessage());
			util.error(this, e);
		}
	}
	
	public void updateCrmToken() {
		
		try{
			
			util.info(this, "Update CRMToken appId=" + Config.clientId + ";appSecret=" + Config.clientPwd);
			String resMsg = WXUtil.QueryToken(Config.crmUrl+"/queryToken?appid="+ Config.clientId + "&apppwd=" + Config.clientPwd);
			if (resMsg != null && resMsg.length() > 0) {
				if (resMsg.contains("access_token")) {
					Gson gson = new Gson();
					WXToken token = gson.fromJson(resMsg, WXToken.class);
					util.info(this, "!!!!! Update CRMToken from " + this.CRMToken + " -----> " + token.getAccess_token());
					this.CRMToken = token.getAccess_token();
				}else{
					util.warn(this, "Update CRMToken failure for "+resMsg);
				}
			}else{
				util.warn(this, "Update CRMToken failue for queryToken res is null.");
			}
			
		}catch(Throwable e){
			util.error(this, "Update CRMToken failure for "+e.getMessage());
			util.error(this, e);
		}
		
	}

	public void createMenu() {

		String menuConfig = WXUtil.loadMenuConfig("menu.js");
		util.info(this, "Begin to createMenu token=" + this.ServiceToken);
		if (this.ServiceToken == null || this.ServiceToken.length() < 1) this.updateServiceToken();
		String res = WXUtil.SendPostMsg("https://api.weixin.qq.com/cgi-bin/menu/create?access_token=" + this.ServiceToken, menuConfig);
		if (res != null && res.length() > 0) {
			Gson gson = new Gson();
			WXError error = gson.fromJson(res, WXError.class);
			if ("0".equals(error.getErrcode())) {
				util.info(this, "Create menu successfully.");
			} else {
				util.error(this, "Create menu failure for " + error.getErrmsg());
			}
		}
	}

	private boolean getToken(String app, String secret) {
		util.info(this, "Update token app=" + app + ";secret=" + secret);
		String resMsg = WXUtil.QueryToken("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid="+ app + "&secret=" + secret);
		if (resMsg != null && resMsg.length() > 0) {
			if (resMsg.contains("access_token")) {
				Gson gson = new Gson();
				WXToken token = gson.fromJson(resMsg, WXToken.class);
				util.info(this, "!!!!! Update token from " + this.ServiceToken + " -----> " + token.getAccess_token()+" : hashcode="+this.hashCode());
				this.ServiceToken = token.getAccess_token();
				return true;
			}
		}
		return false;
	}
	
	public String invokeCRMHttpGet(int stimeout,int ctimeout,String method,String... params){
		
		StringBuffer url = new StringBuffer(Config.crmUrl+"/"+method+"?access_token="+this.CRMToken);
		if(params != null && params.length>0){
			for(String param : params){
				url.append("&"+param);
			}
		}
		final int _invokeId = invokeId;
		util.trace(this,"Invoke <"+_invokeId+"> CRMHttp url="+url);
		String res = WXUtil.GetHTTPRequest(stimeout,ctimeout,url.toString());
		util.trace(this,"Invoke <"+_invokeId+"> CRMhttp res="+res);
		invokeId ++;
		return res;
	}
	
	public TCall updateTCall(String openid){
		
		String res = helper.invokeCRMHttpGet(5000,2000,"queryActiveCall","openid="+openid);
		if(res != null && res.length()>0){
			Gson gson = new Gson();
			TCallResult result = gson.fromJson(res, TCallResult.class);
			if("success".equals(result.getResult()) || "0".equals(result.getResult())){
				WXUser user = this.getWXUser(openid);
				if(user == null) user = new WXUser(openid);
				TCall call = new TCall(openid,result.getCall().getCallidentityid(),result.getCall().getState());
				try{
					call.setCalltitle(result.getCall().getCalltitle());
					call.setCname(URLDecoder.decode(result.getCall().getCname(),"utf-8"));
					call.setPname(URLDecoder.decode(result.getCall().getPname(),"utf-8"));
					call.setDesc(URLDecoder.decode(result.getCall().getDesc(),"utf-8"));
				}catch(Throwable e){
					util.error(this, e.getMessage()); 
				}
				user.setActiveCall(call);
				this.updateWXUser(user);
				return user.getActiveCall();
			}else if("not binding".equals(result.getResult())){
			   	return null;
	   		}
		}
		return null;
		
	}
	
	public WXUser updateWXUser(String openid){
		
		String res = helper.invokeCRMHttpGet(5000,2000,"queryEmployeeInfo","openid="+openid);
		if(res != null && res.length()>0){
			Gson gson = new Gson();
	   		CRMEmployeeInfo result = gson.fromJson(res, CRMEmployeeInfo.class);
	   		if("success".equals(result.getResult()) || "0".equals(result.getResult())){
	   			WXUser user = this.getWXUser(openid);
	   			if(user == null) user= new WXUser(openid);
	   			try{
	   				user.setDuty(URLDecoder.decode(result.getEmployeeinfo().getEmployeeposition(), "utf-8"));
		   			user.setUserName(URLDecoder.decode(result.getEmployeeinfo().getEmployeename(), "utf-8"));
	   			}catch(Throwable e){
	   				util.error(this, e.getMessage()); 
	   			}
	   			return user;
	   		}else if("not binding".equals(result.getResult())){
			   	return null;
	   		}
		}
		return null;
	}
	
	public boolean uploadPic(String mediaId,String openid){
		
		WXUser user = this.getWXUser(openid);
		if(user.getActiveCall() != null){
			Date nowTime = new Date();
			String dateTimeTag = new SimpleDateFormat("yyyyMMddHHmmss").format(nowTime);
			String year = new SimpleDateFormat("yyyy").format(nowTime);
			String date = new SimpleDateFormat("MMdd").format(nowTime);
			String destName = "/"+year+"/"+date+"/"+openid+"/"+dateTimeTag+"_"+user.getActiveCall().getCallidentityid()+".jpg";
			
			try{
				
				util.trace(this, "UploadPic begin token="+this.ServiceToken+";meidaId="+mediaId+";destName="+destName);
				String fileName = WXUtil.upLoadImage(openid,this.ServiceToken, mediaId, Config.bdBucket, destName);
				
				if(fileName != null && fileName.length()>10){
					
					if("invalid token".equals(fileName)){
						this.updateServiceToken();
						return false;
					}
					
					util.trace(this, ">>>>> UploadPic picUrl="+fileName+";callId="+user.getActiveCall().getCallidentityid()+";openid="+openid);
					String[] fileNameRes = fileName.split(";");
					if(fileNameRes[0] != null && !"null".equals(fileNameRes[0])){
						String res = this.invokeCRMHttpGet(5000,5000,"uploadPic", "openid="+openid,"callidentityid="+user.getActiveCall().getCallidentityid(),"picuri="+fileNameRes[0],"size="+fileNameRes[1],"contenttype=image/jpeg");
						util.trace(this, "<<<<< UploadPic res="+res);
						Gson gson = new Gson();
						TCallResult result = gson.fromJson(res, TCallResult.class);
						if("success".equals(result.getResult()) || "0".equals(result.getResult())){
							return true;
						}else{
							return false;
						}
					}else{
						util.error(this, "UploadPic error.");
						return false;
					}
				}
				
			}catch(Throwable e){
				util.error(this, e);
				return false;
			}	
			// new Thread(new UploadPicTask(openid,this.ServiceToken,mediaId,destName,user.getActiveCall().getCallidentityid(),this)).start();
		}else{
			util.error(this, "Upload pic failure for no active order since openid="+openid);
		}
		
		return false;
		
	}
	
	public void postMessage() {
		
		try{
			
			String res = this.invokeCRMHttpGet(5000,2000,"queryNewCalls");
			util.trace(this, "<<<<< QueryNewCalls res="+res);
			
		   	if(res == null || res.length() == 0){
		   		res = "请求超时，请稍候再试.";
		   		util.error(this, "QueryNewCalls failure for timeout.");
		   		return;
		   	}
		   	
		   	Gson gson = new Gson();
		   	OrdersResult result = gson.fromJson(res, OrdersResult.class);
		   	if(result != null && "success".equals(result.getResult())){
		   		List<TOrder> orderList = result.getCalls();
		   		if(orderList != null && orderList.size()>0){
		   			for(TOrder order : orderList){
		   				if(order != null && order.getOpenid() != null){
		   					StringBuffer content = new StringBuffer("【新报修单】\n");
		   					content.append("工单编号："+order.getCalltitle()+"\n");
		   					content.append("客户名称："+URLDecoder.decode(order.getCustomername(), "utf-8")+"\n");
		   					content.append("品牌名称："+URLDecoder.decode(order.getBrandname(),"utf-8")+"\n");
		   					content.append("问题描述："+URLDecoder.decode(order.getDesc(), "utf-8")+"\n");
		   					content.append("客户地址："+URLDecoder.decode(order.getAddress(),"utf-8"));
		   					String postMsg = WXUtil.createPostMsg(order.getOpenid(), content.toString());
							WXUtil.SendPostMsg("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + this.ServiceToken, postMsg);
		   				}
		   				
		   			}
		   		}
		   	}else{
		   		util.warn(this, "QueryNewCalls failure for "+result.getResult());
		   	}
			
		}catch(Throwable e){
			util.error(this, e);
		}
		
		// this.postMessageToUser("请大家及时关注新产生的工单，点击<a href='http://18621295953.duapp.com/myOrders.html'>查询工单</a>了解最新的工单信息。");
	}
	
	public void postMessageToUser(String openid,String content) {
		
		if(this.ServiceToken != null && this.ServiceToken.length()>0){
			String postMsg = WXUtil.createPostMsg(openid, content);
			WXUtil.SendPostMsg("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + this.ServiceToken, postMsg);
		}
	}
	
	
	class OrdersResult {
	
		String result;
		
		List<TOrder> calls = new ArrayList<TOrder>();

		public String getResult() {
			return result;
		}

		public void setResult(String result) {
			this.result = result;
		}

		public List<TOrder> getCalls() {
			return calls;
		}

		public void setCalls(List<TOrder> calls) {
			this.calls = calls;
		}
		
	}
	
	class TOrder {
		
		String callidentityid;
		String calltitle;
		String customername;
		String brandname;
		String desc;
		String address;
		String engineer;
		String openid;
		
		public String getCallidentityid() {
			return callidentityid;
		}
		public void setCallidentityid(String callidentityid) {
			this.callidentityid = callidentityid;
		}
		public String getCalltitle() {
			return calltitle;
		}
		public void setCalltitle(String calltitle) {
			this.calltitle = calltitle;
		}		
		public String getCustomername() {
			return customername;
		}
		public void setCustomername(String customername) {
			this.customername = customername;
		}
		public String getBrandname() {
			return brandname;
		}
		public void setBrandname(String brandname) {
			this.brandname = brandname;
		}
		public String getDesc() {
			return desc;
		}
		public void setDesc(String desc) {
			this.desc = desc;
		}
		public String getAddress() {
			return address;
		}
		public void setAddress(String address) {
			this.address = address;
		}
		public String getEngineer() {
			return engineer;
		}
		public void setEngineer(String engineer) {
			this.engineer = engineer;
		}
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		
	}

	class WXToken {
		String access_token;
		int expires_in;

		public String getAccess_token() {
			return access_token;
		}

		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}

		public int getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(int expires_in) {
			this.expires_in = expires_in;
		}
	}

	class WXError {
		String errcode;
		String errmsg;

		public String getErrcode() {
			return errcode;
		}

		public void setErrcode(String errcode) {
			this.errcode = errcode;
		}

		public String getErrmsg() {
			return errmsg;
		}

		public void setErrmsg(String errmsg) {
			this.errmsg = errmsg;
		}
	}

}
