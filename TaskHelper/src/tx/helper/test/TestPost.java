package tx.helper.test;

import tx.helper.util.WXUtil;

public class TestPost {

	public static void main(String []args){
		TestPost tester = new TestPost();
		tester.launch();
	}
	
	void launch(){
		
		String url = "http://172.16.2.31:7080/ici/iciDispatch";
		
		String res = WXUtil.SendPostMsg(url, "");
		
		System.out.println("Res ---> "+res);
		
		
	}
	
}
