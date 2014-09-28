package tx.helper.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import app.bcs.BCSHelper;

import com.google.gson.Gson;

import sun.misc.BASE64Decoder;
import tx.commons.util.Util;
import tx.helper.config.Config;

public class WXUtil {

	private static BCSHelper bcs = BCSHelper.getInstance();
	
	public static String ComputeSHA1(String str) {

		try {
			MessageDigest md = MessageDigest.getInstance("SHA-1");
			md.update(str.getBytes("UTF-8"));

			byte[] ret = md.digest();
			StringBuffer sb = new StringBuffer();

			for (byte b : ret) {
				int n = b & 0xff;
				if (n < 0x0f) {
					sb.append(0);
				}
				sb.append(Integer.toHexString(n));
			}
			return sb.toString();
		} catch (Throwable e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		}
		return null;
	}

	public static boolean CheckSignature(String timestamp, String token,
			String nonce, String signature) {

		List<String> lists = new ArrayList<String>(3) {
			private static final long serialVersionUID = 1L;

			@Override
			public String toString() {
				return this.get(0) + this.get(1) + this.get(2);
			}
		};

		lists.add(timestamp);
		lists.add(token);
		lists.add(nonce);

		Collections.sort(lists);

		String sign = ComputeSHA1(lists.toString());
		if (sign != null && sign.equalsIgnoreCase(signature)) {
			return true;
		} else {
			return false;
		}
	}

	public static String handleRequest(Map<String, String> request,String resContent) {
		if (request != null && request.size() > 0) {
			return XMLMarshal(request.get("FromUserName"),request.get("ToUserName"), "text", resContent);
		} else {
			return "";
		}
	}

	public static String createPostMsg(String toUserName, String content) {

		try {

			StringBuffer msg = new StringBuffer("{");
			msg.append("\"touser\":\"" + toUserName+ "\",\"msgtype\":\"text\",\"text\":{");
			msg.append("\"content\":\""+ new String(content.getBytes("UTF-8"), "UTF-8") + "\"}");
			msg.append("}");

			return msg.toString();

		} catch (Throwable e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		}
		return null;

	}
	
	public static String GetHTTPRequest(int stimeout,int ctimeout,String url){
		
		CloseableHttpClient httpClient = HttpClients.createDefault();
		String resMsg = "";
		try {

			HttpGet get = new HttpGet(url);
			RequestConfig config = RequestConfig.custom().setSocketTimeout(stimeout).setConnectTimeout(ctimeout).build();
			get.setConfig(config);
			CloseableHttpResponse response = httpClient.execute(get);
			if (response != null) {
				tx.commons.util.Util.getInstance().trace(WXUtil.class, "Response code : "+response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if(entity != null){
						resMsg = EntityUtils.toString(entity);
						tx.commons.util.Util.getInstance().trace(WXUtil.class, "Http get response : "+resMsg);
					}
				}
			}

		} catch (Throwable e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, "HttpRequest failure for "+e.getMessage());
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		} finally {
			try {
				if(httpClient != null)httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resMsg;
		
	}

	public static String QueryToken(String url) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		String resMsg = "";
		try {

			HttpGet get = new HttpGet(url);
			RequestConfig config = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(2000).build();
			get.setConfig(config);
			CloseableHttpResponse response = httpClient.execute(get);
			if (response != null) {
				Util.getInstance().warn(WXUtil.class, "Response code : "+response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if(entity != null){
						resMsg = EntityUtils.toString(entity);
						Util.getInstance().warn(WXUtil.class, "Http get response : "+resMsg);
					}
				}
			}

		} catch (Throwable e) {
			Util.getInstance().error(WXUtil.class, e);
		} finally {
			try {
				if(httpClient != null)httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return resMsg;

	}
	
	
	public static String upLoadImage(String openid,String token,String mediaId,String bucket,String destName) {
		
		    long startTime = System.currentTimeMillis();
		
			bcs.init(Config.bdAppId, Config.bdSecret);
				
			CloseableHttpClient httpClient = HttpClients.createDefault();
				
			FileOutputStream outputStream = null;
			BufferedInputStream bis = null;
			
			String res = "";
				
				try {
					
					HttpGet get = new HttpGet("http://file.api.weixin.qq.com/cgi-bin/media/get?access_token="+token+"&media_id="+mediaId);
					CloseableHttpResponse response = httpClient.execute(get);
					if (response != null) {
						Util.getInstance().warn(WXUtil.class, "Response code : "+response.getStatusLine().getStatusCode()+";"+response.getStatusLine().toString());
						if (response.getStatusLine().getStatusCode() == 200) {
							HttpEntity entity = response.getEntity();
							if(entity != null){
								long len = response.getEntity().getContentLength();
								String type = response.getEntity().getContentType().getValue();
								// System.out.println("Res len="+len+"; type="+type);
								Util.getInstance().warn(WXUtil.class, "Download image len="+len+";type="+type);
								if(len > 1024){
									bis = new BufferedInputStream(entity.getContent(),2*8*1024);
									res = bcs.SaveAsImage(bucket,destName, bis,len,type);
									// System.out.println("------> "+res);
									res = res + ";"+len;
									Util.getInstance().warn(WXUtil.class, "Upload pic over,fileName="+res);
								}else{
									
									String resMsg = EntityUtils.toString(entity);
									Util.getInstance().warn(WXUtil.class, "Download pic failure for : "+resMsg);
									//System.err.println("------> failure for : image not exist access_token or mediaId not right.");
									res = "invalid token";
								}
								
								if(bis != null)	bis.close();
							}
						}
					}
					
				} catch (Throwable e) {
					tx.commons.util.Util.getInstance().error(WXUtil.class, e);
					// String postMsg = WXUtil.createPostMsg(openid, "上传照片失败,请与管理员联系。");
					// SendPostMsg("https://api.weixin.qq.com/cgi-bin/message/custom/send?access_token=" + token, postMsg);
					
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
					
					Util.getInstance().warn(WXUtil.class,"Upload pic over res=["+res+"], elapsed:"+(System.currentTimeMillis()-startTime));
					
				}
				
				return res;
		
	}
	

	public static String SendPostMsg(String url,String content) {

		CloseableHttpClient httpClient = HttpClients.createDefault();
		String resMsg = "";
		try {

			HttpPost post = new HttpPost(url);
			RequestConfig config = RequestConfig.custom().setSocketTimeout(5000).setConnectTimeout(2000).build();
			post.setConfig(config);
			tx.commons.util.Util.getInstance().info(WXUtil.class, "Post url="+url);
			StringEntity myEntity = new StringEntity(content, ContentType.create("text/json", "UTF-8"));
			post.setEntity(myEntity);

			CloseableHttpResponse response = httpClient.execute(post);
			if (response != null) {
				tx.commons.util.Util.getInstance().info(WXUtil.class,"<<<<< Send Post msg to " + url + "; response code ="+ response.getStatusLine().getStatusCode());
				if (response.getStatusLine().getStatusCode() == 200) {
					HttpEntity entity = response.getEntity();
					if(entity != null){
						resMsg = EntityUtils.toString(entity);
						tx.commons.util.Util.getInstance().trace(WXUtil.class, "Post url"+url+" response : "+resMsg);
					}
				} 
			}

		} catch (Throwable e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		} finally {
			if (httpClient != null) {
				try {
					httpClient.close();
				} catch (IOException ee) {
					tx.commons.util.Util.getInstance().error(WXUtil.class, ee);
				}
			}
		}

		return resMsg;

	}

	public static String XMLMarshal(String toUser, String fromUser,
			String msgType, String content) {

		try {

			StringBuffer res = new StringBuffer("<xml>");
			res.append("<ToUserName><![CDATA["+toUser+"]]></ToUserName>");
			res.append("<FromUserName><![CDATA["+fromUser+"]]></FromUserName>");
			res.append("<CreateTime>" + System.currentTimeMillis()+ "</CreateTime>");
			res.append("<MsgType><![CDATA["+msgType+"]]></MsgType>");
			res.append("<Content><![CDATA["+new String(content.getBytes("UTF-8"), "UTF-8")+"]]></Content>");
			res.append("</xml>");
			return res.toString();
		} catch (Throwable e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		}

		return "";
	}
	
	public static String convertBDAxis(String longitude,String dimension) {
		
		try{
			
			String res = GetHTTPRequest(3000,2000,"http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x="+dimension+"&y="+longitude);
			if(res.length()>0){
				Gson gson = new Gson();
				BMapCoord coord = gson.fromJson(res, BMapCoord.class);
				if(coord.getError() == 0){
					BASE64Decoder decoder = new BASE64Decoder();
					longitude = new String(decoder.decodeBuffer(coord.y));
					dimension = new String(decoder.decodeBuffer(coord.x));
					return longitude+","+dimension;
				}
			}
			
		}catch(Throwable e){
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		}
		
		return null;
		
	}
	
	public static String geocoderBMap(String bdAxis) {
		
		// 1.将GPS数据转换为Baidu坐标;
		/*String addr = "";
		try{
			
			String res = GetHTTPRequest("http://api.map.baidu.com/ag/coord/convert?from=0&to=4&x="+dimension+"&y="+longitude);
			if(res.length()>0){
				Gson gson = new Gson();
				BMapCoord coord = gson.fromJson(res, BMapCoord.class);
				if(coord.getError() == 0){
					BASE64Decoder decoder = new BASE64Decoder();
					longitude = new String(decoder.decodeBuffer(coord.y));
					dimension = new String(decoder.decodeBuffer(coord.x));
				}
			}
			
		}catch(Throwable e){
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		}*/
		
		if(bdAxis != null){
			
			// String[] bdAxises = bdAxis.split(",");
			
			// 2.根据Baidu坐标查询地址信息;
			
			// http://api.map.baidu.com/geocoder/v2/?ak=E4805d16520de693a3fe707cdc962045&callback=renderReverse&location=39.983424,116.322987&output=json&pois=1
			Util.getInstance().trace(WXUtil.class, "Convert GSP coordite into longitude="+bdAxis);
			String result = GetHTTPRequest(3000,2000,"http://api.map.baidu.com/geocoder/v2/?ak=4f02e07c81213f3b77ade28a063b1d89&location="+bdAxis+"&output=json&pois=0");
			if(result.length()>0){
				Gson gson = new Gson();
				BMapLocation mapLocation = gson.fromJson(result, BMapLocation.class);
				if(mapLocation.getStatus() == 0){
					return mapLocation.getResult().getFormatted_address();
				}
			}
			
		}
		
		return "";
	}

	public static String loadMenuConfig(String menufile) {

		String fileUrl = WXUtil.class.getResource("/") + menufile;
		fileUrl = fileUrl.substring(5);
		Util.getInstance().trace(WXUtil.class,"Load menu filePath --> " + fileUrl);

		FileInputStream fin = null;
		String msg = "";

		try {
			fin = new FileInputStream(new File(fileUrl));
			byte[] msgBuf = new byte[4096];
			while (fin.read(msgBuf) != -1) {

			}
			msg = new String(msgBuf, "UTF-8");

		} catch (Exception e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		}

		finally {
			if (fin != null)
				try {
					fin.close();
				} catch (IOException e) {
					tx.commons.util.Util.getInstance().error(WXUtil.class, e);
				}
		}

		return msg;
	}

	public static Map<String, String> XMLUnmarshal(String xmlText) {

		try {

			Document doc = DocumentHelper.parseText(xmlText);

			if (doc != null) {

				Map<String, String> msgMap = new HashMap<String, String>();

				Element root = doc.getRootElement();
				msgMap.put("ToUserName", root.elementText("ToUserName"));
				msgMap.put("FromUserName", root.elementText("FromUserName"));
				msgMap.put("CreateTime", root.elementText("CreateTime"));
				String msgType = root.elementText("MsgType");
				msgMap.put("MsgType", msgType);
				if ("text".equals(msgType)) {
					msgMap.put("Content", root.elementText("Content"));
				} else if ("image".equals(msgType)) {
					msgMap.put("PicUrl", root.elementText("PicUrl"));
					msgMap.put("MediaId", root.elementText("MediaId"));
				} else if ("voice".equals(msgType)) {
					msgMap.put("MediaId", root.elementText("MediaId"));
					msgMap.put("Format", root.elementText("Format"));
				} else if ("event".equals(msgType)) {
					msgMap.put("Event", root.elementText("Event"));
					if("LOCATION".equals(root.elementText("Event"))){
						msgMap.put("Latitude", root.elementText("Latitude"));
						msgMap.put("Longitude", root.elementText("Longitude"));
						msgMap.put("Precision", root.elementText("Precision"));
					}else if("CLICK".equals(root.elementText("Event"))){
						msgMap.put("EventKey", root.elementText("EventKey"));
					}
				}
				msgMap.put("MsgId", root.elementText("MsgId"));

				return msgMap;

			}

		} catch (Throwable e) {
			tx.commons.util.Util.getInstance().error(WXUtil.class, e);
		}
		return null;
	}
	
	// Baidu 坐标.
	class BMapCoord {
		int error;
		String x;
		String y;
		public int getError() {
			return error;
		}
		public void setError(int error) {
			this.error = error;
		}
		public String getX() {
			return x;
		}
		public void setX(String x) {
			this.x = x;
		}
		public String getY() {
			return y;
		}
		public void setY(String y) {
			this.y = y;
		}
		
		@Override
		public String toString(){
			return this.x+","+this.y;
		}
		
	}
	
	class BMapLocation {
		int status;
		LocationRes result;
		public int getStatus() {
			return status;
		}
		public void setStatus(int status) {
			this.status = status;
		}
		public LocationRes getResult() {
			return result;
		}
		public void setResult(LocationRes result) {
			this.result = result;
		}
	}
	
	class LocationRes {
		String formatted_address;
		Location location;
		public String getFormatted_address() {
			return formatted_address;
		}
		public void setFormatted_address(String formatted_address) {
			this.formatted_address = formatted_address;
		}
		public Location getLocation() {
			return location;
		}
		public void setLocation(Location location) {
			this.location = location;
		}
	}
	
	class Location {
		double lng;
		double lat;
		public double getLng() {
			return lng;
		}
		public void setLng(double lng) {
			this.lng = lng;
		}
		public double getLat() {
			return lat;
		}
		public void setLat(double lat) {
			this.lat = lat;
		}
		
	}

}
