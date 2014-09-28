package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tx.commons.util.Util;
import tx.helper.dao.ITaskDAO;
import tx.helper.dao.TaskDAO;
import tx.helper.model.BrandName;
import tx.helper.model.CallInfo;
import tx.helper.service.HelperService;

/**
 * Servlet implementation class QueryActiveCall
 */
public class QueryActiveCall extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();
	
	Logger logger = Logger. getLogger("asdf");	
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryActiveCall() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openid = request.getParameter("openid");
		
		util.trace(this, "QueryActiveCall tokenId="+access_token+";openid="+openid);
		logger.log(Level.INFO, "QueryActiveCall tokenId="+access_token+";openid="+openid);
		String resMsg = "";
		if ( openid == null )
			resMsg = "{\"openid\":\""+openid+"\",\"result\":\"missing some parameter\"}";
		else
		{
			//if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){
				CallInfo callInfo = new CallInfo();
				String strRet = dao.queryActiveCall(openid, callInfo);
				resMsg = "{\"openid\":\""+openid+"\",\"result\":\"" + strRet + "\",\"call\":{";
				if ( callInfo.getCallidentityid().equalsIgnoreCase("0") )
					resMsg += "}}";
				else
					resMsg += "\"callidentityid\":\"" + callInfo.getCallidentityid() + "\",\"calltitle\":\"" + callInfo.getCallTitle() + "\",\"cname\":\"" + callInfo.getCustomername() + "\",\"pname\":\"" + callInfo.getBrandname() + "\",\"state\":\"" + callInfo.getState() + "\",\"desc\":\"" + callInfo.getDesc() + "\"}}";
				util.trace(this, resMsg);
				logger.log(Level.INFO, resMsg);				
/*			}else{
				// Bad access_token;
				if ( service.getActiveToken() == null )
				{
					util.trace(this, "there is no token");
					logger.log(Level.INFO, "there is no token");
				}
				else
				{
					util.trace(this, "token not match:" + access_token + "-" + service.getActiveToken());
					logger.log(Level.INFO, "token not match:" + access_token + "-" + service.getActiveToken());
				}
				resMsg = "{\"openid\":\""+openid+"\",\"result\":\"invalid access_token\"}";
			}	*/		
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
