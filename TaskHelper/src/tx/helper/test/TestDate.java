package tx.helper.test;


import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import tx.helper.util.WXUtil;

public class TestDate {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		// TestDate tester = new TestDate();
		// tester.launch();
		
		/*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd%20HH:mm:ss");
		System.out.println(sdf.format(new Date()));
		
		String datetime = "2014-03-16 17:10:31";
			String url = URLEncoder.encode(datetime);
			System.out.println(url);*/
		
		String url = "LV%20%26%20PC";
		System.out.println(URLDecoder.decode(url, "utf-8"));
	}
	
	void launch(){
		
		for(int i = 0;i<100;i++){
			
			ClientThread tThread = new ClientThread();
			tThread.setName("Client1");
			
			tThread.start();
			
		}
		
	}
	
	class ClientThread extends Thread {

		@Override
		public void run() {
			
			while(true){
				long startTime = System.currentTimeMillis();
				String res = WXUtil.GetHTTPRequest(5000,2000,"http://172.16.0.101:9090/queryDate");
				long delay = System.currentTimeMillis()-startTime;
				System.out.println("["+delay+"] --------------------> "+res);
				
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
		}
	}

}
