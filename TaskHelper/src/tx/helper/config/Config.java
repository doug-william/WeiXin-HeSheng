package tx.helper.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import tx.commons.util.Util;
import tx.util.script.SQLScript;

public class Config {

	// CRM info configuration
	public static String crmUrl;
	public static String clientId;
	public static String clientPwd;
	
	// WX App info configuration
	public static String appUrl;
	
	// WX info configuration
	public static String wxToken;
	public static String wxAppId;
	public static String wxSecret;
	
	// Baidu info configuration
	public static String bdAppId;
	public static String bdSecret;
	public static String bdBucket;

	static {
		
		InputStream in  = null;
		
		try {
			Properties p = new Properties();
			String fileUrl = Config.class.getResource("/") + "config.properties";
			Util.getInstance().trace(Config.class, "Load filePath --> " + fileUrl);
			fileUrl = fileUrl.substring(5);
			Util.getInstance().trace(Config.class, "Get config filePath --> " + fileUrl);

			in = new FileInputStream(new File(fileUrl));
			p.load(in);

			crmUrl = p.getProperty("CRMUrl");
			clientId = p.getProperty("ClientId");
			clientPwd = p.getProperty("ClientPwd");
			
			wxToken = p.getProperty("WXToken");
			wxAppId = p.getProperty("WXAppId");
			wxSecret = p.getProperty("WXSecret");
			
			bdAppId = p.getProperty("BDAppId");
			bdSecret = p.getProperty("BDSecret");
			bdBucket = p.getProperty("BDBucket");
			
			appUrl = p.getProperty("AppURL");
			
			Util.getInstance().trace(Config.class, "----> Load Config CRMInfo crmUrl="+crmUrl+";clientId="+clientId+";clientPwd="+clientPwd);
			Util.getInstance().trace(Config.class, "----> Load Config WXInfo wxToken="+wxToken+";wxAppId="+wxAppId+";wxSecret="+wxSecret);
			Util.getInstance().trace(Config.class, "----> Load Config BaiduInfo bdAppId="+bdAppId+";bdSecret="+bdSecret+";bdBucket="+bdBucket);
			Util.getInstance().trace(Config.class, "----> Load Config AppURL appUrl="+appUrl);	
			
		} catch (Throwable e) {
			Util.getInstance().error(Config.class, e);
		}
		
		finally{
			if (in != null){
				try {
					in.close();
				} catch (IOException e) {
					Util.getInstance().error(Config.class, e);
				}
			}
		}
	}
	
	public static String loadSQLScript(String sqlName){
		
		String sqlUrl = Config.class.getResource("/")+"sql/"+sqlName;
		sqlUrl = sqlUrl.substring(6);
		return SQLScript.loadScript(sqlUrl);
		
	}

}
