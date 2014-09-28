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

/**
 * Servlet implementation class OffSite
 */
public class OffSite extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();

	Logger logger = Logger. getLogger("asdf");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public OffSite() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openId = request.getParameter("openid");
		String callIdentityId = request.getParameter("callidentityid");
		String location = request.getParameter("location");
		String offsiteTime = request.getParameter("offsitetime");
		
		util.trace(this, "OffSite tokenId="+access_token+";openid="+openId+";callid="+callIdentityId+";location="+location+";offsitetime="+offsiteTime);
		logger.log(Level.INFO, "OffSite tokenId="+access_token+";openid="+openId+";callid="+callIdentityId+";location="+location+";offsitetime="+offsiteTime);
		String resMsg = "";
		if ( openId == null || callIdentityId == null || location == null || offsiteTime == null )
			resMsg = "{\"openid\":\""+openId+"\",\"result\":\"missing some parameter\"}";
		else
		{
			/*if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){*/
				String strRet = dao.offSite(openId, callIdentityId, location, offsiteTime);
				resMsg = "{\"openid\":\""+openId+"\",\"result\":\"" + strRet + "\"}";
				util.trace(this, resMsg);
				logger.log(Level.INFO, resMsg);
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}

}
