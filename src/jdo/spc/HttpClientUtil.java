package jdo.spc;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

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
import org.apache.http.util.EntityUtils;


/**
 * <p>
 * Title: 辅助类
 * </p>
 *
 * <p>
 * Description: 电子标签接口
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
public class HttpClientUtil {

	// private static HttpClient httpClient = new DefaultHttpClient();


	/**
	 * Get请求
	 * 
	 * @param url     请求URL
	 * @return
	 */
	public static String get(String url) {
		String body = null;
		HttpClient httpClient = new DefaultHttpClient();
		//return "{\"Status\":\"10000\",\"ReturnObject\":\"\",\"Message\":\"发送成功\"}";
		
		try {
			// Get请求
			HttpGet httpget = new HttpGet(url);

			if(Constant.parameters != null && Constant.parameters.size() >  0 ){
				httpget.addHeader( Constant.parameters.get(0).getName(),  Constant.parameters.get(0).getValue());
				httpget.addHeader(Constant.parameters.get(1).getName(),Constant.parameters.get(1).getValue());
				httpget.addHeader(Constant.parameters.get(2).getName(),  Constant.parameters.get(2).getValue());
			}
			// 发送请求
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
		return body;
		
	}

	/**
	 * Post请求
	 * 
	 * @param url         请求URL
	 * @param params      post请求json参数
	 * @return
	 */
	public static String post(String url, String params) {
		String body = null;
		HttpClient httpClient = new DefaultHttpClient();
		
		//return "{\"ObjectId\":\"业务ID\",\"LabelNo\":\"标签ID\",\"Label\":0,\"Harbor\":0,\"Station\":-1,\"Power\":100,\"Quantity\":100,\"Data\":\"原始协议数据\",\"Succeed\":true,\"Message\":\"其他信息\",\"ReceiveDate\":\"2012-5-2 12:50:22\",\"IsReceiveData\":true}";

		
		try {
			// Post请求
			HttpPost httppost = new HttpPost(url);
			if(Constant.parameters != null && Constant.parameters.size() >  0 ){
				httppost.addHeader( Constant.parameters.get(0).getName(),  Constant.parameters.get(0).getValue());
				httppost.addHeader(Constant.parameters.get(1).getName(),Constant.parameters.get(1).getValue());
				httppost.addHeader(Constant.parameters.get(2).getName(),  Constant.parameters.get(2).getValue());
			}
//			System.out.println("send json==:"+params);
			StringEntity stringEntity = new StringEntity(params, "utf-8");
			stringEntity.setContentType("application/json");
			stringEntity.setContentEncoding("utf-8");
			// 设置参数
			httppost.setEntity(stringEntity);
			// 发送请求
			HttpResponse httpresponse = httpClient.execute(httppost);

			if (httpresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// 获取返回数据
				HttpEntity entity = httpresponse.getEntity();
				InputStream  inputStream = entity.getContent(); 
            	char[] buffer = new char[(int)entity.getContentLength()];
            	 
                InputStreamReader reader = new InputStreamReader(inputStream);
                 reader.read(buffer);
                 inputStream.close();
                 //JSONObject.
                //JSONObject entitys = new JSONObject();
                // return  entitys;
     
                 body =new String(buffer);
				//body = EntityUtils.toString(entity);
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
			
		return body;
		
	}
	
	


}
