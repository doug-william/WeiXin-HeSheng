package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tx.commons.util.Util;
import tx.helper.service.HelperService;


public class QueryLoginServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
       
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
    public QueryLoginServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String openid = request.getParameter("openId");
		String startDate = request.getParameter("startDate");
		String endDate = request.getParameter("endDate");
		
		util.trace(this, ">>>>> QueryLogin startDate="+startDate+";endDate="+endDate+";openid="+openid);
		
		try{
			
			if(startDate != null && endDate != null){
				Date _startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDate);
				Date _endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDate);
				
				if(_endDate.compareTo(_startDate) > 30*24*60*1000){
					Calendar endCal = Calendar.getInstance();
					endCal.setTime(_startDate);
					endCal.add(Calendar.DAY_OF_YEAR, 30);
					_endDate = endCal.getTime();
				}
				
				
				endDate = new SimpleDateFormat("yyyy-MM-dd").format(_endDate);
			}
			
			String res = service.invokeCRMHttpGet(10000,5000,"queryLog", "openid="+openid,"startdate="+startDate,"enddate="+endDate);
			
			util.trace(this, "<<<<< QueryLog res="+res);
			
		   	if(res == null || res.length() == 0){
		   		res = "请求超时，请稍候再试.";
		   	}
		   	
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
