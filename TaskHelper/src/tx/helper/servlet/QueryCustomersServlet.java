package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tx.commons.util.Util;
import tx.helper.service.HelperService;


public class QueryCustomersServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
    public QueryCustomersServlet() {
        super();
    }


	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String openid = request.getParameter("openId");
		util.trace(this, ">>>>> QueryCustomers openid="+openid);
		
		try{
			
			String res = service.invokeCRMHttpGet(10000,5000,"queryCustomers","openid="+openid);
			util.trace(this, "<<<<< QueryCustomers res="+res);
		  
			if(res == null || res.length() == 0){
		   		res = "请求超时，请稍候再试.";
		   	}
		   	response.setCharacterEncoding("utf-8"); 
		   	res = URLDecoder.decode(res, "utf-8");
		   	PrintWriter out = response.getWriter();
		   	
	        out.print(res);
	        out.flush();
	        out.close();
		   	
		}catch(Throwable e){
			util.error(this, e);
		}
		
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		this.doGet(request, response);
	}

}
