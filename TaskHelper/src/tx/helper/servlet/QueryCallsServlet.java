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

public class QueryCallsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
       
    public QueryCallsServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		request.setCharacterEncoding("utf-8");
		
		String openid = request.getParameter("openId");
		String state = request.getParameter("state");
		String customerName = request.getParameter("customerName")!= null?URLEncoder.encode(request.getParameter("customerName"),"utf-8"):"";
		String brandName = request.getParameter("brandName")!= null?URLEncoder.encode(request.getParameter("brandName"),"utf-8"):"";
		String group = request.getParameter("group");
		
		util.trace(this, ">>>>> QueryCall state="+state+";group="+group+";customerName="+customerName+";brandName="+brandName+";openid="+openid);
		
		try{
			
			String res = service.invokeCRMHttpGet(10000,5000,"queryCalls", "openid="+openid,"state="+state,"group="+group,"customername="+customerName,"brandname="+brandName);
			
			util.trace(this, "<<<<< QueryCall res="+res);
			
		   	if(res == null || res.length() == 0){
		   		res = "请求超时，请稍候再试.";
		   	}
		   	
		   	res = URLDecoder.decode(res, "utf-8");
		   	
		   	response.setCharacterEncoding("utf-8"); 
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
