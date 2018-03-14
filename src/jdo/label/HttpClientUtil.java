package jdo.label;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import jdo.sys.Operator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 辅助类
 * </p>
 * 
 * <p>
 * Description: 新电子标签接口
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:BlueCore
 * </p>
 * 
 * @author Yuanxm 2012.08.30
 * @version 1.0
 */
public class HttpClientUtil  extends TJDOTool  {

	// private static HttpClient httpClient = new DefaultHttpClient();

	/**
	 * Get请求
	 * 
	 * @param url
	 *            请求URL
	 * @return
	 */
	public static String get(String url) {

		long startTime = System.currentTimeMillis();
		String body = null;
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				600);// 数据传输时间60s

		try {
			// Get请求
			HttpGet httpget = new HttpGet(url);

			if (Constant.parameters != null && Constant.parameters.size() > 0) {
				httpget.addHeader(Constant.parameters.get(0).getName(),
						Constant.parameters.get(0).getValue());
				httpget.addHeader(Constant.parameters.get(1).getName(),
						Constant.parameters.get(1).getValue());
				httpget.addHeader(Constant.parameters.get(2).getName(),
						Constant.parameters.get(2).getValue());
			}
			// 发�?请求
			HttpResponse httpresponse = httpClient.execute(httpget);
			// 获取返回数据
			if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = httpresponse.getEntity();
				body = EntityUtils.toString(entity);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		long endTime = System.currentTimeMillis();
		System.out.println("GET电子标签请求时间------------------"
				+ (endTime - startTime));
		return body;

	}

	/**
	 * Post请求
	 * 
	 * @param url
	 *            请求URL
	 * @param params
	 *            post请求json参数
	 * @return
	 */
	public static String post(String url, String json) {

		String body = null;
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				600);// 数据传输时间60s
		/**
		long startTime = System.currentTimeMillis();
		System.out.println("JSON------:"+json+"   URL---:"+url);
		*/

		String uuid = System.currentTimeMillis()+"";
		
		String sql = "INSERT INTO  IND_LABEL_LOG (ID,MSG,OPT_USER,OPT_DATE,OPT_TERM ) VALUES( '"+uuid+"','"+json+"','"+Operator.getID()+"',SYSDATE,'"+Operator.getIP()+"' ) ";
		if(sql != null && sql.length() > 4000) {
			sql = sql.substring(0,3999);
		}
		try{
			TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
		}catch (Exception e) {
			// TODO: handle exception
			System.out.println("保存日志数据出错");
			e.printStackTrace() ;
		}
		
		
		try {
			// Post请求
			HttpPost httppost = new HttpPost(url);

			StringEntity stringEntity = new StringEntity(json, "UTF-8");
			stringEntity.setContentType("application/json");
			stringEntity.setContentEncoding("utf-8");

			// 设置参数
			httppost.setEntity(stringEntity);
			// 发送请求
			HttpResponse httpresponse = httpClient.execute(httppost);

			if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取返回数据
				HttpEntity entity = httpresponse.getEntity();
				InputStream inputStream = entity.getContent();
				char[] buffer = new char[(int) entity.getContentLength()];

				InputStreamReader reader = new InputStreamReader(inputStream);
				reader.read(buffer);
				inputStream.close();
				body = new String(buffer);
				reader.close();
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
		
		/**
		long endTime = System.currentTimeMillis();
		System.out.println("POST电子标签请求时间------------------"
				+ (endTime - startTime));
		 */
		if (body != null && !body.equals("")) {
			body = body.replaceAll("[\\[\\]]", "");
		}
		return body;

	}
	
	/**
	 * Post请求
	 * 
	 * @param url
	 *            请求URL
	 * @param params
	 *            post请求json参数
	 * @return
	 */
	public static String post(String url, String json,List<Map<String, Object>> list ) {
		 
		String body = null;
		HttpClient httpClient = new DefaultHttpClient();
		httpClient.getParams().setParameter(
				CoreConnectionPNames.CONNECTION_TIMEOUT, 2000);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT,
				600);// 数据传输时间60s
		 
		
		 

		String status = "";
		try {
			// Post请求
			HttpPost httppost = new HttpPost(url);

			StringEntity stringEntity = new StringEntity(json, "UTF-8");
			stringEntity.setContentType("application/json");
			stringEntity.setContentEncoding("utf-8");

			// 设置参数
			httppost.setEntity(stringEntity);
			// 发送请求
			HttpResponse httpresponse = httpClient.execute(httppost);

			if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取返回数据
				HttpEntity entity = httpresponse.getEntity();
				InputStream inputStream = entity.getContent();
				char[] buffer = new char[(int) entity.getContentLength()];

				InputStreamReader reader = new InputStreamReader(inputStream);
				reader.read(buffer);
				inputStream.close();
				body = new String(buffer);
				reader.close();
			}
			 
		} catch (UnsupportedEncodingException e) {
			//不被支持的编码
			status = "unsupportedEncodingException";
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			//客户端提交给服务器的请求,不符合HTTP协议
			status = "clientProtocolException";
			e.printStackTrace();
		} catch (ParseException e) {
			//
			status = "parseException";
			e.printStackTrace();
		} catch (IOException e) {
			//流关闭异常
			status = "IOException";
			e.printStackTrace();
		} finally {
			if (httpClient != null && httpClient.getConnectionManager() != null) {
				httpClient.getConnectionManager().shutdown();
			}
		}
	 
		
 
		if (body != null && !body.equals("")) {
			body = body.replaceAll("[\\[\\]]", "");
			status = body ;
		}
		
		 
		String msg = "";
		String  tagNo = "";
		 
		for(int i = 0 ; i < list.size() ; i++){
			String uuid = System.currentTimeMillis()+"";
			Map<String, Object> map = (Map<String, Object>)list.get(i);
			String name = (String)map.get("ProductName");
			String spec = (String)map.get("Spec");
			tagNo = (String)map.get("TagNo") ;
			msg   =  name+"-"+spec ;
			String sql = "INSERT INTO  IND_ESL_LOG (LOG_ID,ESL_ID,IN_PARA,RETN_PARA,OPT_USER,OPT_DATE,OPT_TERM ) VALUES( '"+uuid+"','"+tagNo+"','"+msg+"','"+status+"','"+Operator.getID()+"',SYSDATE,'"+Operator.getIP()+"' ) ";
			try{
				TParm parm = new TParm(TJDODBTool.getInstance().update(sql));
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("保存日志数据出错");
				e.printStackTrace() ;
			}
	 
		}
		//String sql = "INSERT INTO  IND_LABEL_LOG (ID,MSG,OPT_USER,OPT_DATE,OPT_TERM ) VALUES( '"+uuid+"','"+msg+"','"+Operator.getID()+"',SYSDATE,'"+Operator.getIP()+"' ) ";
		
		

		
		return body;

	}


}
