package tx.helper.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

import tx.commons.util.Util;
import tx.helper.config.Config;
import tx.helper.service.HelperService;
import tx.helper.service.WXUser;
import tx.helper.util.WXUtil;


public class WXAuth2Servlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
       
    public WXAuth2Servlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String code = request.getParameter("code");
		String state = request.getParameter("state");
		util.trace(this, "----> code="+code+";state="+state);
		
		String openId = "";
		
		String res = WXUtil.GetHTTPRequest(4000,4000,"https://api.weixin.qq.com/sns/oauth2/access_token?appid="+Config.wxAppId+"&secret="+Config.wxSecret+"&code="+code+"&grant_type=authorization_code");
		if(res != null && res.length()>0){
			Gson gson = new Gson();
			WXToken wxToken = gson.fromJson(res, WXToken.class);
			openId = wxToken.getOpenid();
			util.trace(this, "queryOpenId ---> openId="+openId);
		}
		
		if("login".equals(state)){
			response.sendRedirect("login.html?openid="+openId);
		}else if("myOrder".equals(state)){
			WXUser user = service.getWXUser(openId);
			if(user != null && user.getLocation() != null){
				response.sendRedirect("myOrders.html?openid="+openId+"&oriLoc="+user.getLocation().toString());
			}else{
				response.sendRedirect("myOrders.html?openid="+openId+"&oriLoc=");
			}
		}else if("mySign".equals(state)){
			response.sendRedirect("mySignIn.html?openid="+openId);
		}
	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}
	
	class WXToken {
		
		private String access_token;
		private int expires_time;
		private String refresh_token;
		private String openid;
		private String scope;
		
		public String getAccess_token() {
			return access_token;
		}
		public void setAccess_token(String access_token) {
			this.access_token = access_token;
		}
		public int getExpires_time() {
			return expires_time;
		}
		public void setExpires_time(int expires_time) {
			this.expires_time = expires_time;
		}
		public String getRefresh_token() {
			return refresh_token;
		}
		public void setRefresh_token(String refresh_token) {
			this.refresh_token = refresh_token;
		}
		public String getOpenid() {
			return openid;
		}
		public void setOpenid(String openid) {
			this.openid = openid;
		}
		public String getScope() {
			return scope;
		}
		public void setScope(String scope) {
			this.scope = scope;
		}
	}

}
