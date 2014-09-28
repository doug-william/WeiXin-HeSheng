package tx.helper.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import tx.commons.util.Util;
import tx.helper.config.Config;
import tx.helper.module.CRMResult;
import tx.helper.service.HelperService;
import tx.helper.service.Location;
import tx.helper.service.LoginWorkFlow;
import tx.helper.service.LogoutWorkFlow;
import tx.helper.service.MyInfoWorkFlow;
import tx.helper.service.MyStaticsWorkFlow;
import tx.helper.service.OffSiteWorkFlow;
import tx.helper.service.OnSiteWorkFlow;
import tx.helper.service.UploadPicOkWorkFlow;
import tx.helper.service.UploadPicWorkFlow;
import tx.helper.service.WXUser;
import tx.helper.service.WorkFlow;
import tx.helper.service.WorkFlow.WFStatus;
import tx.helper.util.WXUtil;

public class TaskHelperServlet extends HttpServlet {

	private static final long serialVersionUID = -1388821377824765986L;
	
	private Util util = Util.getInstance();
	
	private HelperService helper = HelperService.getInstance();
	
	private static final String NOTBINDING = "您尚未绑定帐号，请先<a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Config.wxAppId+"&redirect_uri="+Config.appUrl+"/wxAuth2&response_type=code&scope=snsapi_base&state=login#wechat_redirect\">绑定帐号</a>";

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String signature = request.getParameter("signature");
		String timestamp = request.getParameter("timestamp");
		String nonce = request.getParameter("nonce");
		String echostr = request.getParameter("echostr");
		
		util.info(this, "TaskHelper init get signature="+signature+";timestamp="+timestamp+";nonce="+nonce+";echostr="+echostr);
		// Check signature.
		boolean valid = WXUtil.CheckSignature(timestamp, Config.wxToken, nonce, signature);
		
		PrintWriter out = response.getWriter();
		if(valid){
			// 验证合法返回字符?
			out.write(echostr);
		}else{
			out.write("");
		}
		
		out.flush();
		out.close();
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		BufferedReader br = request.getReader();
		Map<String,String> msgMap = readRequest(br);
		
		String msgType = msgMap.get("MsgType");
		String resMsg = "";
		if(msgType != null && msgType.length()>1){
			
			if("event".equalsIgnoreCase(msgType)){
				if("subscribe".equals(msgMap.get("Event"))){
					// Subscribe event.
					resMsg = "<a href=\""+Config.appUrl+"/bindingNote.html\">请先点击此链接阅读关注服务号注意事项！</a>\n\n您好！我是您的工作微助手，为了保证您高效的工作，请先进行身份验证，即可享受我为您带来的微服务：\n\n <a href=\"https://open.weixin.qq.com/connect/oauth2/authorize?appid="+Config.wxAppId+"&redirect_uri="+Config.appUrl+"/wxAuth2&response_type=code&scope=snsapi_base&state=login#wechat_redirect\">点击立即验证身份</a> \n\n 如果您没有完成身份验证我将无法给你提供任何服务，抱歉！";
				}else if("unsubscribe".equals(msgMap.get("Event"))){
					// unSubscribe event.
					// TODO
				}else if("LOCATION".equals(msgMap.get("Event"))){
					util.trace(this, "$$$$$$ update location latitude="+msgMap.get("Latitude")+",Longitude="+msgMap.get("Longitude"));
					WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
					if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
						resMsg = NOTBINDING;
					}else {
						user.setLocation(new Location(msgMap.get("Latitude"),msgMap.get("Longitude"),msgMap.get("Precision")));
						helper.updateWXUser(user);
					}
					// resMsg = "您的位置: 纬度="+msgMap.get("Latitude")+"; 经度="+msgMap.get("Longitude")+"; 精度="+msgMap.get("Precision");
				}else if("CLICK".equals(msgMap.get("Event"))){
					// Menu Click.
					if("Login".equals(msgMap.get("EventKey"))){
						WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
						if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
							resMsg = NOTBINDING;
						}else {
							if(user.getWorkFlow() != null){
								user.getWorkFlow().cancel();
							}
							
							LoginWorkFlow loginWF = new LoginWorkFlow(user.getOpenId());
							resMsg = loginWF.getPrompt();
							user.setWorkFlow(loginWF);
						}
						
					}else if("Logout".equals(msgMap.get("EventKey"))){
						WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
						if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
							resMsg = NOTBINDING;
						}else{
							if(user.getWorkFlow() != null){
								user.getWorkFlow().cancel();
							}
							LogoutWorkFlow logoutWF = new LogoutWorkFlow(user.getOpenId());
							resMsg = logoutWF.getPrompt();
							user.setWorkFlow(logoutWF);
						}
						
					}else if("MyInfo".equals(msgMap.get("EventKey"))){
						WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
						if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
							resMsg = NOTBINDING;
						}else{
							if(user.getWorkFlow() != null){
								user.getWorkFlow().cancel();
							}
							MyInfoWorkFlow myInfoWF = new MyInfoWorkFlow(user.getOpenId());
							resMsg = myInfoWF.getPrompt();
							user.setWorkFlow(myInfoWF);
						}
					}else if("OffSite".equals(msgMap.get("EventKey"))){
						WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
						if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
							resMsg = NOTBINDING;
						}else{
							if(user.getWorkFlow() != null){
								user.getWorkFlow().cancel();
							}
							OffSiteWorkFlow offSiteWF = new OffSiteWorkFlow(user.getOpenId());
							resMsg = offSiteWF.getPrompt();
							user.setWorkFlow(null);
						}
					}else if("OnSite".equals(msgMap.get("EventKey"))){
						WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
						if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
							resMsg = NOTBINDING;
						}else{
							if(user.getWorkFlow() != null){
								user.getWorkFlow().cancel();
							}
							OnSiteWorkFlow onSiteWF = new OnSiteWorkFlow(user.getOpenId());
							resMsg = onSiteWF.getPrompt();
							util.trace(this, "OnSite resMsg --> "+resMsg);
							user.setWorkFlow(onSiteWF);
						}
						
					}else if("UploadPic".equals(msgMap.get("EventKey"))){
						WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
						if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
							resMsg = NOTBINDING;
						}else{
							if(user.getWorkFlow() != null){
								user.getWorkFlow().cancel();
							}
							UploadPicWorkFlow upPicWF = new UploadPicWorkFlow(user.getOpenId());
							resMsg = upPicWF.getPrompt();
							util.trace(this, "UploadPic resMsg --> "+resMsg);
							user.setWorkFlow(upPicWF);
						}
					}else if("MyStatics".equals(msgMap.get("EventKey"))){
						WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
						if(user == null && !checkUserInfo(msgMap.get("FromUserName"))){
							resMsg = NOTBINDING;
						}else{
							if(user.getWorkFlow() != null){
								user.getWorkFlow().cancel();
							}
							MyStaticsWorkFlow statWF = new MyStaticsWorkFlow(user.getOpenId());
							resMsg = statWF.getPrompt();
							util.trace(this, "MyStatics resMsg --> "+resMsg);
							user.setWorkFlow(statWF);
						}
					}
					else
					{
						resMsg = "无效的请求。";
					}
				}
				else{
					// Unsupported at the moment.
					resMsg = "无效的请求。";
					util.warn(this, "Unsupported msgType at the moment.");
				}
			}else if("text".equalsIgnoreCase(msgType)){
				// resMsg = this.handleTextRequest(msgMap);
				String reqMsg = msgMap.get("Content");
				WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
				if(user != null){
					if(user.getWorkFlow() != null && user.getWorkFlow().getStatus() == WFStatus.OPEN){
						WorkFlow workFlow = user.getWorkFlow().end(reqMsg);
						if(workFlow != null){
							resMsg = workFlow.getPrompt();
							user.setWorkFlow(workFlow);
						}
					}else{
						resMsg = "无效的请求。";
					}
				}else{
					resMsg = "无效的请求。";
				}
			
			}else if("voice".equalsIgnoreCase(msgType)){
				WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
				if(user != null){
					user.cancelWorkFlow();
				}
				resMsg = "无效的请求。";
			}else if("image".equalsIgnoreCase(msgType)){
				WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
				if(user != null && user.getWorkFlow() != null){
					WorkFlow workFlow = user.getWorkFlow();
					if(workFlow instanceof UploadPicWorkFlow){
						// Upload pic.
						WorkFlow nextWF = workFlow.end(msgMap.get("MediaId"));
						if(nextWF != null){
							resMsg = nextWF.getPrompt();
							user.setWorkFlow(nextWF);
						}
					}else if(workFlow instanceof UploadPicOkWorkFlow){
						WorkFlow nextWF = workFlow.end(msgMap.get("MediaId"));
						if(nextWF != null){
							resMsg = nextWF.getPrompt();
							user.setWorkFlow(nextWF);
						}
					}else{
						resMsg = "请再次点击菜单中的【上传图片】以确保是在处理正确的工单。";
					}
				}else{
					resMsg = "无效的请求。";
				}
			}
			else{
				// Not supported at the moment.
				WXUser user = helper.getWXUser(msgMap.get("FromUserName"));
				if(user != null){
					user.cancelWorkFlow();
				}
				resMsg = "无效的请求。";
			}
			
		}
		
		/*if(resMsg == "" || resMsg.trim().length()<1){
			resMsg = "无效的请求。";
		}*/
		
		String responseMsg = WXUtil.handleRequest(msgMap, resMsg);
		
		util.info(this, ">>>>> "+responseMsg);
		response.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
		out.write(responseMsg);
		out.flush();
		br.close();
		out.close();
		
	}
	
	private boolean checkUserInfo(String openid){
		
		String res = helper.invokeCRMHttpGet(4000,4000,"queryEmployeeInfo","openid="+openid);
		if(res != null && res.length()>0){
			Gson gson = new Gson();
	   		CRMResult result = gson.fromJson(res, CRMResult.class);
	   		if("0".equals(result.getResult())){
	   			WXUser user = new WXUser(openid);
	   			helper.addWXUser(user);
	   			return true;
	   		}else if("not binding".equals(result.getResult())){
			   	return false;
	   		}
		}
		return false;
	}
	
	private Map<String,String> readRequest(BufferedReader br){
		
		try{
			
			char[] buf = new char[1024];
			int v = 0;
			StringBuffer sb = new StringBuffer();
			while((v = br.read(buf)) != -1){
				sb.append(buf,0,v);
			}
			
			String content = sb.toString();
			util.info(this, "Post value : "+content);
			return WXUtil.XMLUnmarshal(content);
			
		}catch(Throwable e){
			util.error(this, e);
		}
		return null;
	}
	

}
