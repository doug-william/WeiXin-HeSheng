package tx.helper.test;

import tx.helper.util.WXUtil;

public class TestLoadMenu {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		String msg = WXUtil.loadMenuConfig("menu.js");
		System.out.println("---> \r\n"+msg);
	}

}
