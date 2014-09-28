package tx.helper.util;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HelperUtil {

	public static Map<String,Float> handleBookMsg(String bookMsg) {
		
		Pattern p = Pattern.compile("(([\u4e00-\u9fa5|a-zA-Z]+)([0-9.]+))");
		Matcher matcher = p.matcher(bookMsg);
		Map<String,Float> msgMap =  new HashMap<String,Float>();
		while (matcher.find()){
			msgMap.put(matcher.group(2), Float.valueOf(matcher.group(3)));
		}	
		return msgMap;
	}
	
	public static String geneUUID(){
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}
	
}
