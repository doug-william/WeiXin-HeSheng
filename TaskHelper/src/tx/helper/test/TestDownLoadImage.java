package tx.helper.test;

import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import app.bcs.BCSHelper;

import tx.helper.config.Config;
import tx.helper.util.WXUtil;

public class TestDownLoadImage {

	private BCSHelper bcs = BCSHelper.getInstance();
	
	public static void main(String[] args) {
		TestDownLoadImage tester = new TestDownLoadImage();
		tester.downLoadImage("JAvJj9ht3LTGqlsuQCdbc3BBVPeXgvYWUjPedSJbOe0Dcw1P7AR36q6340-uVOsq","4d2xLEq7sE9zHI7BZGTtwKBWXEWkacRFyn-pf6t_XKeYEfw9WU4pp5g91AYq7mQ8", "C:/Dev/test4.jpg");
	}
	
	void downLoadImage(String token,String mediaId,String destName){
		
		// bcs.init("GgPgNo1a2SOiGbMgGzrVLPyA", "1QZi8Ntfqa3TaaEzGktOIAZWOTbKHhZq");
		bcs.init(Config.bdAppId, Config.bdSecret);
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		
		FileOutputStream outputStream = null;
		BufferedInputStream bis = null;
		
		try {
			long startTime = System.currentTimeMillis();
			HttpGet get = new HttpGet("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+token+"&media_id="+mediaId);
			CloseableHttpResponse response = httpClient.execute(get);
			if (response != null) {
				tx.commons.util.Util.getInstance().trace(WXUtil.class, "Response code : "+response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if(entity != null){
						long len = response.getEntity().getContentLength();
						String type = response.getEntity().getContentType().getValue();
						System.out.println("Res len="+len+"; type="+type);
						if(len > 1024){
							bis = new BufferedInputStream(entity.getContent(),2*8*1024);
							/*outputStream = new FileOutputStream(destName);
							bis = new BufferedInputStream(entity.getContent());
							int intByte = 0;
							while((intByte = bis.read()) != -1){
								outputStream.write(intByte);
							}
							System.out.println("Write Image "+destName+" ok.");*/
							String res = bcs.SaveAsImage("task12", "/20140226103122.png", bis,len,type);
							System.out.println("------> "+res);
						}else{
							System.err.println("------> failure for : image not exist access_token or mediaId not right.");
						}
						
						if(bis != null)	bis.close();
					}
				}
			}
			System.out.println("SaveAs elapsed: "+(System.currentTimeMillis()-startTime));
		} catch (Throwable e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		} finally {
			
			if(bis != null)
				try {
					bis.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
			if(outputStream != null)
				try {
					outputStream.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}

}
