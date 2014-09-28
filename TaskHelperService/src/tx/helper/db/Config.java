package tx.helper.db;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import tx.commons.util.Util;
import tx.helper.util.EncryptionDecryption;
import tx.util.script.SQLScript;

public class Config {

	public static String dbDriver;
	public static String dbURL;
	public static String dbUser;
	public static String dbPwd;
	public static String connPoolSize;
	public static boolean dbTestOnBorrow;
	public static String dbValidationQuery;
	
	public static String clientId;
	public static String clientSecret;

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

			EncryptionDecryption des = new EncryptionDecryption("hesheng");
			dbDriver = p.getProperty("DBDriver");
			dbURL = p.getProperty("DBURL");
			dbUser = p.getProperty("DBUser");
			dbPwd = des.decrypt(p.getProperty("DBPwd"));
			connPoolSize = p.getProperty("DBConnPoolSize");
			dbValidationQuery = p.getProperty("DBValidationQuery");
			dbTestOnBorrow=Boolean.parseBoolean(p.getProperty("DBTestOnBorrow"));
			clientId = p.getProperty("ClientID");
			clientSecret = p.getProperty("ClientSecret");
			
			Util.getInstance().trace(Config.class,"Load Config dbDriver=" + dbDriver + ";dbURL=" + dbURL+ ";dbUser=" + dbUser + ";dbPwd=" + dbPwd + ";poolSize=" + connPoolSize+";testBorrow="+dbTestOnBorrow+";validateQuery="+dbValidationQuery+";clientId="+clientId+";clientSecret="+clientSecret);

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
