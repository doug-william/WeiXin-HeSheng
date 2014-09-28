package tx.helper.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

import tx.helper.config.Config;

public class TestUrl {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		String userId= "%E5%91%B3%E5%8D%83%E6%8B%89%E9%9D%A2";
		
		String _userId = URLDecoder.decode(userId, "utf-8");
		
		System.out.println("_userId="+_userId);
		
		StringBuffer url = new StringBuffer(Config.crmUrl+"/checkUser?access_token=sdkf81431d2s5498zde");
		String[] params = new String[]{"userId=韩昌明","userPwd=123456"};
		if(params != null && params.length>0){
			for(String param : params){
				url.append("&"+param);
			}
		}
		final int _invokeId = 0;
		String _url = "";
		try {
			_url = URLEncoder.encode(url.toString(), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println("Invoke <"+_invokeId+"> CRMHttp url="+_url);
		//String res = WXUtil.GetHTTPRequest(_url);
		// util.trace(this,"Invoke <"+_invokeId+"> CRMhttp res="+res);
		// return res;
		
	}

}
