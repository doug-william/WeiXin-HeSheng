package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.digest.DigestUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tx.commons.util.Util;
import tx.helper.dao.ITaskDAO;
import tx.helper.dao.TaskDAO;
import tx.helper.model.CallInfo;
import tx.helper.model.NewCallInfo;
import tx.helper.service.HelperService;

/**
 * Servlet implementation class QueryNewCalls
 */
public class QueryNewCalls extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();

	Logger logger = Logger. getLogger("asdf");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryNewCalls() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {		
		util.trace(this, "QueryNewCalls");
		logger.log(Level.INFO, "QueryNewCalls");
		String resMsg = "";
		/*if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){*/
			Collection<NewCallInfo> newCallList = new ArrayList<NewCallInfo>();
			String strRet = dao.queryNewCalls(newCallList);
			resMsg = "{\"result\":\"" + strRet + "\",\"calls\":";
			Gson gson = new GsonBuilder().disableHtmlEscaping().create(); 
			String json = null;
			json = gson.toJson(newCallList);
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
				util.trace(this, "there is no token");
			else
				util.trace(this, "token not match:" + access_token + "-" + service.getActiveToken());
			resMsg = "{\"result\":\"invalid access_token\"}";
		}*/
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
