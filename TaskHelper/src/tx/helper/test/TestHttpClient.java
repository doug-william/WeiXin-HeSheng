package tx.helper.test;

import tx.helper.util.WXUtil;

public class TestHttpClient {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		for(int i = 0;i<100;i++){
			Thread clientThread = new Thread(new WSClient());
			clientThread.start();
		}
		
	}
	
	

}

class WSClient implements Runnable {

	@Override
	public void run() {

		WXUtil.GetHTTPRequest(5000,5000,"http://172.16.0.192:8090/webPhone.html");
		
		try {
			Thread.sleep(100000000);
		} catch (InterruptedException e) {
		}
		
	}
	
}