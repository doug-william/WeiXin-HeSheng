package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tx.commons.util.Util;
import tx.helper.dao.ITaskDAO;
import tx.helper.dao.TaskDAO;
import tx.helper.model.UserLog;
import tx.helper.service.HelperService;

/**
 * Servlet implementation class QueryLog
 */
public class QueryLog extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();
	
	Logger logger = Logger. getLogger("asdf");	
       
    public QueryLog() {
        super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openid = request.getParameter("openid");
		String startdate = request.getParameter("startdate");
		String enddate = request.getParameter("enddate");
		
		util.trace(this, "QueryLog tokenId="+access_token+";openid="+openid+";startdate="+startdate+";enddate="+enddate);
		logger.log(Level.INFO, "QueryLog tokenId="+access_token+";openid="+openid+";startdate="+startdate+";enddate="+enddate);
		String resMsg = "";
		////if ( access_token == null || openid == null || startdate == null || enddate == null )
		if ( openid == null || startdate == null || enddate == null )
			resMsg = "{\"openid\":\""+openid+"\",\"result\":\"missing some parameter\"}";
		else
		{
			//if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){
				Collection<UserLog> logList = new ArrayList<UserLog>();
				String strRet = dao.queryUserLog(openid, startdate, enddate, logList);
				resMsg = "{\"openid\":\""+openid+"\",\"result\":\"" + strRet + "\",\"logs\":";
				Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				String json = null;
				json = gson.toJson(logList);
				if ( json == null )
					resMsg += "[]";
				else
					resMsg += json;
				resMsg += "}";
				util.trace(this, resMsg);
				logger.log(Level.INFO, resMsg);				
			/*}else{
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
