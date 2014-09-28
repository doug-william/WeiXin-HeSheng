package tx.helper.service;

import java.util.Timer;
import java.util.TimerTask;

import tx.commons.util.Util;
import tx.helper.db.Config;
import tx.helper.util.HelperUtil;

import java.util.logging.Logger;
import java.util.logging.Level;

public class HelperService {

	private Util util = Util.getInstance();
	
	private static final int EXPIRE_TIME = 7200;

	private static HelperService helper;
	
	Logger logger = Logger. getLogger("asdf");

	private HelperService() {
		
/*		new Timer("TokenTimer",false).schedule(new TimerTask(){

			@Override
			public void run() {
				try{
					
					if(activeToken != null){
						int duration = (int)((System.currentTimeMillis()-activeToken.getCreateTime())/1000);
						if(duration >= activeToken.getExpire()){
							util.warn(this, "ActiveToken invalid tokenId="+activeToken.getTokenId());
							activeToken = null;
						}
					}
					
				}catch(Throwable e){
					Util.getInstance().error(this, e);
				}
			}
		}, 0, 1000*120);
		*/
	}

	private Token activeToken = null;
	
	public static HelperService getInstance() {
		if (helper == null) helper = new HelperService();
		return helper;
	}

	public String getActiveToken() {
		if(this.activeToken != null)
		{
			logger.log(Level.WARNING, "getActiveToken---token:" + this.activeToken.getTokenId());
			return this.activeToken.getTokenId();
		}
		logger.log(Level.WARNING, "getActiveToken---token is null");
		return null;
	}
	
	// 每次客户端请求时生成一个新的TokenId,并且该Token有生命周期，默认是是7200秒。
	public String getToken(String app, String secret) {
		if(app != null && secret != null){
			if(app.equals(Config.clientId) && secret.equals(Config.clientSecret)){
				String tokenId = HelperUtil.geneUUID();
				activeToken = new Token(tokenId,EXPIRE_TIME);
				logger.log(Level.WARNING, "Update---tokenId="+tokenId);
				util.warn(this, "Update tokenId="+tokenId);
				return "{\"access_token\":\""+tokenId+"\",\"expire\":"+EXPIRE_TIME+"}";
			}else{
				util.warn(this, "GetToken failure for bad clientId or clientSecret.");
				return "{\"result\":\"failure\",\"reason\":\"bad clientId or clientSecret\"}";
			}
		}else{
			util.warn(this, "GetToken failure for clientId or clientSecret is null.");
			return "{\"result\":\"failure\",\"reason\":\"clientId or clientSecret is null\"}";
		}
	}

}

class Token {
	
	String tokenId;
	int expire;
	long createTime;
	
	Token(String tokenId,int expire){
		this.tokenId = tokenId;
		this.expire = expire;
		this.createTime = System.currentTimeMillis();
	}
	
	String getTokenId() {
		return tokenId;
	}
	void setTokenId(String tokenId) {
		this.tokenId = tokenId;
	}
	public int getExpire() {
		return expire;
	}
	void setExpire(int expire) {
		this.expire = expire;
	}
	long getCreateTime() {
		return createTime;
	}
	
}
