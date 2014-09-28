package tx.helper.test;

import java.io.FileReader;
import java.io.IOException;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import sun.misc.BASE64Decoder;
import tx.helper.util.WXUtil;

public class TestInvokeJS {

	public static void main(String[] args) throws IOException {
		
		BASE64Decoder decoder = new BASE64Decoder(); 
		
		byte[] b = decoder.decodeBuffer("MzEuMjE4NzEyNzQ5ODc2"); 
		System.out.println("--------->Y="+new String(b)); 
		
		byte[] b2 = decoder.decodeBuffer("MTIxLjQwMzcxMDU2MTcx"); 
		System.out.println("--------->X="+new String(b2)); 
		
		/*
		try {
			tester.invokeJS();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		*/
	}
	
	void invokeJS() throws Throwable {
		
		ScriptEngine engine = new ScriptEngineManager().getEngineByName("javascript");  
        
        String fileUrl = WXUtil.class.getResource("/")+"map.js";
		fileUrl = fileUrl.substring(6);
        System.out.println("Load js file --> "+fileUrl);
        engine.eval(new FileReader(fileUrl));
        if(engine instanceof Invocable){
        	Invocable invoke = (Invocable)engine;
        	String result = (String)invoke.invokeFunction("loadAddr",31.214775,121.392487);
        	System.out.println("Ret --> "+result);
        }
		
	}

}
