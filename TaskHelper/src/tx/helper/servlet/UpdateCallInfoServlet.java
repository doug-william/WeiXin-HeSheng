package tx.helper.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import tx.commons.util.Util;
import tx.helper.module.CRMResult;
import tx.helper.service.HelperService;
import tx.helper.service.UploadPicWorkFlow;
import tx.helper.service.WXUser;
import tx.helper.util.WXUtil;


public class UpdateCallInfoServlet extends HttpServlet {
	
	private static final long serialVersionUID = 1L;
    
	private Util util = Util.getInstance();
	
	private HelperService service = HelperService.getInstance();
	
    public UpdateCallInfoServlet() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String offsiteTime = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss").format(new Date());
		request.setCharacterEncoding("utf-8");
		
		String openid = request.getParameter("openId");
		String scoretech  = request.getParameter("scoretech");
		String scoreman  = request.getParameter("scoreman");
		String callId = request.getParameter("callidentityid");
		String desc = request.getParameter("desc");
		String location = request.getParameter("loc");
	
		util.trace(this, ">>>>> UpdateCallInfo scoretech="+scoretech+";scoreman="+scoreman+";desc="+desc+";callId="+callId+";openid="+openid+";location="+location+";offsitetime="+offsiteTime);
		
		try{
			
			String res = service.invokeCRMHttpGet(10000,50000,"updateCall", "openid="+openid,"task="+desc,"assistnumtech="+scoretech,"assistnumnontech="+scoreman,"callidentityid="+callId,"location="+location,"offsitetime="+offsiteTime);
			
			util.trace(this, "<<<<< UpdateCallInfo res="+res);
			
		   	if(res == null || res.length() == 0){
		   		res = "请求超时，请稍候再试.";
		   	}
		   	else if(res != null && res.length()>0){
		   		Gson gson = new Gson();
		   		CRMResult result = gson.fromJson(res, CRMResult.class);
		   		if("success".equals(result.getResult()) || "0".equals(result.getResult())){
   					StringBuffer content = new StringBuffer("【工单处理成功】\n您已填写完现场工作描述及评分，如需上传图片，请至菜单栏选择图片上传，如无需上传照片则无需做任何操作。");
   					String postMsg = WXUtil.createPostMsg(openid, content.toString());
					WXUtil.SendPostMsg("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + service.getServiceToken(), postMsg);
		   		}
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
