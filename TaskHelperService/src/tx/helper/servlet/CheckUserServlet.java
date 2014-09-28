package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import tx.commons.util.Util;
import tx.helper.dao.ITaskDAO;
import tx.helper.dao.TaskDAO;
import tx.helper.service.HelperService;


public class CheckUserServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();

	Logger logger = Logger. getLogger("asdf");
	
    public CheckUserServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String access_token = request.getParameter("access_token");
		String openId = request.getParameter("openid");
		String certId = request.getParameter("cardno");
		//username参数可能是中文，Safari和Chrome使用ISO_8859-1编码；现在不知道IE采用什么编码。
		String userName = request.getParameter("username");
		//userName = new String(userName.getBytes("ISO-8859-1"), "UTF-8");
		userName = URLDecoder.decode(userName, "utf-8");
		String userPwd = request.getParameter("userpwd");
		
		util.trace(this, "CheckUser tokenId="+access_token+";openId="+openId+";certId="+certId+";userName="+userName+";userPwd="+userPwd+";crypted pwd="+DigestUtils.md5Hex(userPwd));
		logger.log(Level.INFO, "CheckUser tokenId="+access_token+";openId="+openId+";certId="+certId+";userName="+userName+";userPwd="+userPwd+";crypted pwd="+DigestUtils.md5Hex(userPwd));
		String resMsg = "";
		if ( openId == null || certId == null || userName == null || userPwd == null )
			resMsg = "{\"openid\":\""+openId+"\",\"result\":\"missing some parameter\"}";
		else
		{
			/*if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){*/
				boolean result = dao.checkUser(openId, certId, userName, DigestUtils.md5Hex(userPwd));
				if(result){
					resMsg = "{\"openid\":\""+openId+"\",\"result\":\"success\"}";
				}else{
					resMsg = "{\"openid\":\""+openId+"\",\"result\":\"invalid data\"}";
				}
			/*}else{
				// Bad access_token;
				if ( service.getActiveToken() == null )
					util.trace(this, "there is no token");
				else
					util.trace(this, "token not match:" + access_token + "-" + service.getActiveToken());
				resMsg = "{\"openid\":\""+openId+"\",\"result\":\"invalid access_token\"}";
			}*/
		}
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(resMsg);
		out.flush();
		out.close();
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
