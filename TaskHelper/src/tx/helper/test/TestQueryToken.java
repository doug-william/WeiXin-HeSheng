package tx.helper.test;

import tx.helper.util.WXUtil;

import com.google.gson.Gson;

public class TestQueryToken {

	String _token = "";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		TestQueryToken tester = new TestQueryToken();
		if(tester.getToken()){
			tester.createMenu();
		}
	}
	
	boolean getToken() {
		String resMsg = WXUtil.QueryToken("https://api.weixin.qq.com/cgi-bin/token?grant_type=client_credential&appid=wxd19e33e3aca7b8f3&secret=70b6d64906ebc5164e6557aba355eba5");
		if(resMsg != null && resMsg.length()>0){
			if(resMsg.contains("access_token")){
				Gson gson = new Gson();
				WXToken token = gson.fromJson(resMsg, WXToken.class);
				this._token = token.getAccess_token();
				System.out.println("------> Token: "+token.getAccess_token());
				return true;
			}
		}
		return false;
	}
	
	void createMenu() {
		
		String menuConfig = WXUtil.loadMenuConfig("menu.js");
		String res = WXUtil.SendPostMsg("https://api.weixin.qq.com/cgi-bin/menu/create?access_token="+this._token, menuConfig);
		if(res != null) System.out.println("------------> CreateMenu res : "+res);
	}
	
	class WXToken {
		String access_token;
		int expires_in;
		
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

}
