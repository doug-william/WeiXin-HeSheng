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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tx.commons.util.Util;
import tx.helper.dao.ITaskDAO;
import tx.helper.dao.TaskDAO;
import tx.helper.model.CallStats;
import tx.helper.model.UserLog;
import tx.helper.service.HelperService;

/**
 * Servlet implementation class QueryStats
 */
public class QueryStats extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();
	
	Logger logger = Logger. getLogger("asdf");		
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryStats() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openid = request.getParameter("openid");
		String startdate = request.getParameter("startdate");
		String enddate = request.getParameter("enddate");
		
		util.trace(this, "QueryStats tokenId="+access_token+";openid="+openid+";startdate="+startdate+";enddate="+enddate);
		logger.log(Level.INFO, "QueryStats tokenId="+access_token+";openid="+openid+";startdate="+startdate+";enddate="+enddate);
		String resMsg = "";
		if ( openid == null || startdate == null || enddate == null )
			resMsg = "{\"openid\":\""+openid+"\",\"result\":\"missing some parameter\"}";
		else
		{
			//if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){
				CallStats callStats = new CallStats();
				String strRet = dao.queryStats(openid, startdate, enddate, callStats);
				resMsg = "{\"openid\":\""+openid+"\",\"result\":\"" + strRet + "\",\"wait\":\"" + callStats.getWait() + "\",\"processing\":\"" + callStats.getProcessing() + "\",\"finished\":\"" + callStats.getFinished() + "\"}";
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
