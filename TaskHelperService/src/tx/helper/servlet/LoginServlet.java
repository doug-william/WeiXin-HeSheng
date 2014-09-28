package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tx.commons.util.Util;
import tx.helper.dao.ITaskDAO;
import tx.helper.dao.TaskDAO;
import tx.helper.service.HelperService;

public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();
	
	Logger logger = Logger. getLogger("asdf");

    public LoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openId = request.getParameter("openid");
		String logintime = request.getParameter("logintime");
		String location = request.getParameter("location");
		String loginmode = request.getParameter("loginmode");
		
		util.trace(this, "LoginLogout tokenId="+access_token+";openId="+openId+";logintime="+logintime+";location="+location+";loginmode="+loginmode);
		logger.log(Level.INFO,  "LoginLogout tokenId="+access_token+";openId="+openId+";logintime="+logintime+";location="+location+";loginmode="+loginmode);
		String resMsg = "";
		if ( openId == null || logintime == null || location == null || loginmode == null )
			resMsg = "{\"openid\":\""+openId+"\",\"result\":\"missing some parameter\"}";
		else
		{
			if ( loginmode.equalsIgnoreCase("login") || loginmode.equalsIgnoreCase("logout") )
			{
				//if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){
					String strResult = dao.userLog(openId, loginmode, logintime, location);
					resMsg = "{\"openid\":\""+openId+"\",\"result\":\"" + strResult + "\"}";
				/*}else{
					// Bad access_token;
					if ( service.getActiveToken() == null )
						util.trace(this, "there is no token");
					else
						util.trace(this, "token not match:" + access_token + "-" + service.getActiveToken());
					resMsg = "{\"openid\":\""+openId+"\",\"result\":\"invalid access_token\"}";
				}*/			
			}
			else
				resMsg = "{\"openid\":\""+openId+"\",\"result\":\"invalid login mode\"}";
		}
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(resMsg);
		out.flush();
		out.close();	
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}

}
