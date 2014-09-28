package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tx.commons.util.Util;
import tx.helper.service.HelperService;

public class QueryStaticsServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
	
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
       
    public QueryStaticsServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
			
		request.setCharacterEncoding("utf-8");
		
		String openid = request.getParameter("openid");
		String len = request.getParameter("len");
		
		util.trace(this, ">>>>> QueryStatics len="+len+";openid="+openid);
		
		try{
			Calendar nowDate = Calendar.getInstance();
			nowDate.add(Calendar.DAY_OF_YEAR, 0 - Integer.parseInt(len) + 1);
			String startDate = new SimpleDateFormat("yyyy-MM-dd").format(nowDate.getTime());
			nowDate.add(Calendar.DAY_OF_YEAR, Integer.parseInt(len));
			String endDate = new SimpleDateFormat("yyyy-MM-dd").format(nowDate.getTime());
			
			String res = service.invokeCRMHttpGet(10000,5000,"queryStats", "openid="+openid,"startdate="+startDate,"enddate="+endDate);
			
			util.trace(this, "<<<<< QueryStatics res="+res);
			
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
