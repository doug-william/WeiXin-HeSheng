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
 * Servlet implementation class UploadPic
 */
public class UploadPic extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	private ITaskDAO dao = TaskDAO.getInstance();

	Logger logger = Logger. getLogger("asdf");
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UploadPic() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String access_token = request.getParameter("access_token");
		String openId = request.getParameter("openid");
		String picuri = request.getParameter("picuri");		
		String callIdentityId = request.getParameter("callidentityid");
		String size = request.getParameter("size");
		int nsize;
		try{nsize = Integer.parseInt(size);}catch(Exception e){nsize = 0;}
		String contenttype = request.getParameter("contenttype");
		
		util.trace(this, "UploadPic tokenId="+access_token+";openid="+openId+";callid="+callIdentityId+";picuri="+picuri+";contenttype="+contenttype);
		logger.log(Level.INFO, "UploadPic tokenId="+access_token+";openid="+openId+";callid="+callIdentityId+";picuri="+picuri+";contenttype="+contenttype);
		String resMsg = "";
		if ( openId == null || picuri == null || callIdentityId == null || size == null || contenttype == null )
			resMsg = "{\"openid\":\""+openId+"\",\"result\":\"missing some parameter\"}";
		else
		{		
			/*if(service.getActiveToken() != null && service.getActiveToken().equals(access_token)){*/
				String strRet = dao.uploadPic(openId, callIdentityId, picuri, nsize, contenttype);
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
