package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tx.commons.util.Util;
import tx.helper.service.HelperService;

public class QueryBrandsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
       
    public QueryBrandsServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		request.setCharacterEncoding("UTF-8");
		
		String openid = request.getParameter("openId");
		String customername = request.getParameter("customerName");
		// util.trace(this	,"-----> openid="+openid+"; custName="+customername);
		customername = URLEncoder.encode(customername, "UTF-8");
		util.trace(this, ">>>>> QueryBrands openid="+openid+";customer="+customername);
		
		try{
			
			String res = service.invokeCRMHttpGet(10000,5000,"queryBrands","openid="+openid,"customername="+customername);
			util.trace(this, "<<<<< QueryBrands res="+res);
		  
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
