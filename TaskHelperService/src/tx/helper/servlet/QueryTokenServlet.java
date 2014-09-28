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
import tx.helper.service.HelperService;

public class QueryTokenServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
	Logger logger = Logger. getLogger("asdf");
       
    public QueryTokenServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		System.out.println("receive getToken request");
		String appId = request.getParameter("appid");
		String appPwd = request.getParameter("apppwd");
		String res = service.getToken(appId, appPwd);
		util.trace(this, "QueryToken appId="+appId+";appPwd="+appPwd+"; res="+res+";  from ip:"+request.getRemoteAddr()+"; port:"+request.getRemotePort());
		logger.log(Level.INFO, "QueryToken appId="+appId+";appPwd="+appPwd+"; res=" + res+";  from ip:"+request.getRemoteAddr()+"; port:"+request.getRemotePort());
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		out.write(res);
		out.flush();
		out.close();
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
