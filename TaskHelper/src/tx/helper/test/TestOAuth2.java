package tx.helper.test;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import tx.helper.util.WXUtil;

public class TestOAuth2 {


	public static void main(String[] args) throws UnsupportedEncodingException {
		
		TestOAuth2 tester = new TestOAuth2();
		
		// System.out.println("---> "+URLEncoder.encode("http://18621295953.duapp.com/taskHelper", "utf-8"));
		
		tester.authorize("wxd19e33e3aca7b8f3", "http://18621295953.duapp.com/taskHelper");
		
	}
	
	void authorize(String appId,String reUri) {
		
		try{
			
			String _reUri = URLEncoder.encode(reUri, "utf-8");
			
			String auth_url = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="+_reUri+"&response_type=code&scope=snsapi_base&state=18621295953#wechat_redirect";
			String res = WXUtil.GetHTTPRequest(5000,2000,auth_url);
			System.out.println("Res ---> "+res);
			
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

}
