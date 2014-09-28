package tx.helper.test;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import com.google.gson.Gson;

import tx.helper.util.WXUtil;

public class TestPCS {

	private String access_tonken = null;
	
	public static void main(String[] args) {
		
		
		TestPCS pcs = new TestPCS();
		// pcs.launch();
		trustAllHosts();
		pcs.queryAccessToken("WXApp", "123456");
		
		// pcs.launch();

	}
	
	 public static void trustAllHosts() {
	        // Create a trust manager that does not validate certificate chains
	        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
	                    public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                            return new java.security.cert.X509Certificate[] {};
	                    }

						@Override
						public void checkClientTrusted(java.security.cert.X509Certificate[] chain,String authType)
								throws java.security.cert.CertificateException {
							
						}

						@Override
						public void checkServerTrusted(java.security.cert.X509Certificate[] chain,String authType)
								throws java.security.cert.CertificateException {
						}
	                }
	        };
	     
	        try {
	            SSLContext sc = SSLContext.getInstance("TLS");
	            sc.init(null, trustAllCerts, new java.security.SecureRandom());
	            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }
	
	
	void launch(){
		
		String url = "https://pcs.baidu.com/rest/2.0/pcs/quota?method=info&access_token="+this.access_tonken;
		String res = WXUtil.GetHTTPRequest(5000,2000,url);
		System.out.println(">>>>>> Info "+res);
		
	}
	
	void queryAccessToken(String client_id,String client_secret){
		// String url = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials&client_id="+client_id+"&client_secret="+client_secret;
		String url = "https://172.168.2.104:8443/TaskHelperService/queryToken?appId="+client_id+"&appPwd="+client_secret;
		String res = WXUtil.GetHTTPRequest(5000,2000,url);
		
		System.out.println("----> "+res);
		
		Gson gson = new Gson();
		BDToken bdToken = gson.fromJson(res, BDToken.class);
		System.out.println(">>>>> access_tonken="+bdToken.getAccess_token());
		this.access_tonken = bdToken.getAccess_token();
	}

}

class BDToken {
	private String access_token;
	private int expires_in;
	
	public String getAccess_token() {
		return access_token;
	}
	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}
	public int getExpires_in() {
		return expires_in;
	}
	public void setExpires_in(int expires_in) {
		this.expires_in = expires_in;
	}
	
}
