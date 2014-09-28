package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
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
import tx.helper.model.EmployeeInfo;
import tx.helper.model.UserLog;
import tx.helper.service.HelperService;

/**
 * Servlet implementation class QueryEmployeeInfo
 */
public class QueryEmployeeInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();
	
	Logger logger = Logger. getLogger("asdf");		
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryEmployeeInfo() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openid = request.getParameter("openid");
		
		util.trace(this, "QueryEmployeeInfo tokenId="+access_token+";openid="+openid);
		logger.log(Level.INFO, "QueryEmployeeInfo tokenId="+access_token+";openid="+openid);
		String resMsg = "";
		////if ( access_token == null || openid == null )
		if ( openid == null )
			resMsg = "{\"openid\":\""+openid+"\",\"result\":\"missing some parameter\"}";
		else
		{
			//if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){
				EmployeeInfo employeeInfo = new EmployeeInfo();
				String strRet = dao.queryEmployeeInfo(openid, employeeInfo);
				resMsg = "{\"openid\":\""+openid+"\",\"result\":\"" + strRet + "\",\"employeeinfo\":";
				//Gson gson = new GsonBuilder().disableHtmlEscaping().create();
				Gson gson = new Gson();logger.log(Level.INFO, "QueryEmployeeInfo 3");
				String json = null;
				json = gson.toJson(employeeInfo);logger.log(Level.INFO, "QueryEmployeeInfo 4");
				resMsg += json;
				resMsg += "}";
				//resMsg = URLEncoder.encode(resMsg, "utf-8");
				//resMsg = URLEncoder.encode(resMsg, "utf-8");
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
