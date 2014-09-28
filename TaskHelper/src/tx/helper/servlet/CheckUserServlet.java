package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import tx.commons.util.Util;
import tx.helper.module.CRMResult;
import tx.helper.service.HelperService;
import tx.helper.service.WXUser;
import tx.helper.util.WXUtil;

public class CheckUserServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
    public CheckUserServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		this.doPost(request, response);
		
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
	   try{
		   
		   	request.setCharacterEncoding("UTF-8");
		   
		   	String certId = request.getParameter("certId");
		   	String userId = request.getParameter("userId");
		   	String userPwd = request.getParameter("userPwd");
		   	String openId = request.getParameter("openId");
		   	
		   	String _userId = URLEncoder.encode(userId, "UTF-8");
		   	util.trace(this, "userId="+userId+"; _userId="+_userId);
		   	String res = service.invokeCRMHttpGet(10000,5000,"checkUser", "openid="+openId,"cardno="+certId,"username="+_userId,"userpwd="+userPwd);
		   	JsonObject myObj = new JsonObject();
		   	if(res != null && res.length()>0){
		   		Gson gson = new Gson();
		   		CRMResult result = gson.fromJson(res, CRMResult.class);
		   		if("success".equals(result.getResult()) || "0".equals(result.getResult())){
		   			WXUser user = new WXUser(openId);
		   			user.setUserName(userId);
		   			service.addWXUser(user);
				   	myObj.addProperty("result","success");

   					StringBuffer content = new StringBuffer("【绑定成功】\n");
   					WXUser user1 = service.updateWXUser(openId);
   					if(user1 != null){
   						content.append("\n 姓名："+user1.getUserName()+"\n 职位："+user1.getDuty());
   					}
   					String postMsg = WXUtil.createPostMsg(openId, content.toString());
					WXUtil.SendPostMsg("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + service.getServiceToken(), postMsg);				   	
		   		}else if("invalid data".equals(result.getResult())){
				   	myObj.addProperty("result","身份证号码，帐号或密码不匹配");
		   		}else {
		   			myObj.addProperty("result",result.getResult());
		   		}
		   	}else{
			   	myObj.addProperty("result","请求超时，请稍候再试.");
		   	}
			
		   	response.setCharacterEncoding("UTF-8");
		   	PrintWriter out = response.getWriter();
		   	
	        out.println(myObj.toString());
	        out.flush();
	        out.close();
	        
		}catch(Throwable e){
			util.error(this, e);
		}
		
	}

}
