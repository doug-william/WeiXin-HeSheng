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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import tx.commons.util.Util;
import tx.helper.dao.ITaskDAO;
import tx.helper.dao.TaskDAO;
import tx.helper.model.BrandName;
import tx.helper.model.CustomerInfo;
import tx.helper.service.HelperService;

/**
 * Servlet implementation class QueryBrands
 */
public class QueryBrands extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();
	
	Logger logger = Logger. getLogger("asdf");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public QueryBrands() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openid = request.getParameter("openid");
		//username参数可能是中文，Safari和Chrome使用ISO_8859-1编码；现在不知道IE采用什么编码。
		String customername = request.getParameter("customername");
		customername = URLDecoder.decode(customername, "utf-8");
		
		util.trace(this, "QueryBrands tokenId="+access_token+";openid="+openid+";customername="+customername);
		logger.log(Level.INFO, "QueryBrands tokenId="+access_token+";openid="+openid+";customername="+customername);
		String resMsg = "";
		if ( openid == null || customername == null )
			resMsg = "{\"openid\":\""+openid+"\",\"result\":\"missing some parameter\"}";
		else
		{
			//if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){
				Collection<BrandName> brandNameList = new ArrayList<BrandName>();
				String strRet = dao.queryBrands(openid, customername, brandNameList);
				resMsg = "{\"openid\":\""+openid+"\",\"result\":\"" + strRet + "\",\"customername\":\"" + customername + "\",\"brandnames\":[";
				if ( brandNameList.isEmpty() == false )
				{
					for ( BrandName brandName : brandNameList )
						resMsg += "\"" + brandName.getName() + "\",";
					resMsg = resMsg.substring(0, resMsg.length() - 1);
				}
				resMsg += "]}";
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
