package tx.helper.test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import tx.helper.module.CRMEmployeeInfo;

import com.google.gson.Gson;

public class TestJson {

	/**
	 * @param args
	 * @throws UnsupportedEncodingException 
	 */
	public static void main(String[] args) throws UnsupportedEncodingException {

		// String result = "{\"openid\":\"dad091e112v\",\"result\":\"success\",\"employeeInfo\":{\"employeename\":\"tongxiao\",\"employeeposition\":\"EM\"}}";
		
		String result = "{\"openid\":\"ovuASuHe6YKAmqTlbYVq_nzZ6vRI\",\"result\":\"0\",\"employeeinfo\":{\"employeename\":\"jack\",\"employeeposition\":\"%2C%E7%8E%B0%E5%9C%BA%E5%B7%A5%E7%A8%8B%E5%B8%88%2C\"}}";
		
		System.out.println(result);
		Gson gson = new Gson();
		CRMEmployeeInfo crm = gson.fromJson(result, CRMEmployeeInfo.class);
		System.out.println(crm.getOpenid()+";"+crm.getEmployeeinfo().getEmployeename()+";"+crm.getEmployeeinfo().getEmployeeposition());
		System.out.println("------> "+URLDecoder.decode(crm.getEmployeeinfo().getEmployeeposition(), "utf-8"));
	}

}
