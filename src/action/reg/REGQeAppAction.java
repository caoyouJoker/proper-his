package action.reg;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import jdo.reg.client.ServiceForTXYYSoapClient;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;


public class REGQeAppAction extends TAction{
	
	public TParm reconciliationQe(TParm parm){
		System.out.println("进来REGQeAppAction");
		String sDate = parm.getValue("S_DATE");
		String eDate = parm.getValue("E_DATE");

		System.out.println("GetQyiPayData===================");
//		String json = this.getWS("GetQyiPayData", nameParm, valueParm); 
		
		String json = ServiceForTXYYSoapClient.getInstance().getQyiPayData(sDate, eDate);
		System.out.println("Action:=="+json);
		
//		String json="{'errorcode':'00000000','message':'成功','data':[{'OrderNo':'1606152101200014','BussType':'1','BussSN':'160615001382','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T11:53:34.897','PayTime':'2016-06-15T11:53:40.98','BussTime':'2016-06-15T11:53:33.883'},{'OrderNo':'1606152101200015','BussType':'1','BussSN':'','PayMoney':0.0000,'Source':'3','PayType':'4','OrderTime':'2016-06-15T14:56:20.373','PayTime':'2016-06-15T14:56:22.12','BussTime':'2016-06-15T14:56:08.33'},{'OrderNo':'1606152101200007','BussType':'1','BussSN':'160615001154','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T10:44:45.897','PayTime':'2016-06-15T10:44:49.39','BussTime':'2016-06-15T10:44:44.353'},{'OrderNo':'1606152101200001','BussType':'1','BussSN':'2016061521001004290207675173','PayMoney':10.0000,'Source':'3','PayType':'3','OrderTime':'2016-06-15T10:27:49.367','PayTime':'2016-06-15T10:28:24','BussTime':'2016-06-15T10:27:43.627'},{'OrderNo':'1606152101200009','BussType':'1','BussSN':'160615001245','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T11:03:40.267','PayTime':'2016-06-15T11:03:44.183','BussTime':'2016-06-15T11:03:39.457'},{'OrderNo':'1606152101200004','BussType':'1','BussSN':'160615001107','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T10:38:18.78','PayTime':'2016-06-15T10:38:24.24','BussTime':'2016-06-15T10:38:10.733'},{'OrderNo':'1606161104101210','BussType':'1','BussSN':null,'PayMoney':0.0000,'Source':'1','PayType':'4','OrderTime':'2016-06-16T14:20:18.12','PayTime':'2016-06-16T14:20:18.557','BussTime':'2016-06-16T14:19:50.327'},{'OrderNo':'1606152101200012','BussType':'1','BussSN':'160615001297','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T11:13:27.967','PayTime':'2016-06-15T11:13:30.103','BussTime':'2016-06-15T11:13:26.953'},{'OrderNo':'1606152101200013','BussType':'1','BussSN':'160615001349','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T11:29:55.09','PayTime':'2016-06-15T11:29:57.743','BussTime':'2016-06-15T11:29:54.187'},{'OrderNo':'1606162101200001','BussType':'1','BussSN':'160616000717','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-16T09:40:59.823','PayTime':'2016-06-16T09:41:03.193','BussTime':'2016-06-16T09:40:58.547'},{'OrderNo':'1606152101200002','BussType':'1','BussSN':'160615001061','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T10:31:34.82','PayTime':'2016-06-15T10:31:38.923','BussTime':'2016-06-15T10:31:33.557'},{'OrderNo':'1606152101200016','BussType':'1','BussSN':'','PayMoney':0.0000,'Source':'3','PayType':'4','OrderTime':'2016-06-15T14:59:21.083','PayTime':'2016-06-15T14:59:22.19','BussTime':'2016-06-15T14:59:10.023'},{'OrderNo':'1606152101200006','BussType':'1','BussSN':'160615001130','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T10:40:21.71','PayTime':'2016-06-15T10:40:55.373','BussTime':'2016-06-15T10:40:20.633'},{'OrderNo':'1606151104101061','BussType':'1','BussSN':null,'PayMoney':0.0000,'Source':'1','PayType':'4','OrderTime':'2016-06-15T10:53:32.187','PayTime':'2016-06-15T10:53:32.687','BussTime':'2016-06-15T10:52:50.917'},{'OrderNo':'1606161104101212','BussType':'1','BussSN':'160616001556','PayMoney':10.0000,'Source':'1','PayType':'0','OrderTime':'2016-06-16T14:24:13.297','PayTime':'2016-06-16T14:24:20.577','BussTime':'2016-06-16T14:24:03.78'},{'OrderNo':'1606152101200003','BussType':'1','BussSN':'160615001100','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T10:37:35.897','PayTime':'2016-06-15T10:37:40.797','BussTime':'2016-06-15T10:37:34.337'},{'OrderNo':'1606152101200010','BussType':'1','BussSN':'160615001256','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T11:05:48.5','PayTime':'2016-06-15T11:05:52.743','BussTime':'2016-06-15T11:05:46.8'},{'OrderNo':'1606152101200008','BussType':'1','BussSN':'160615001231','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T11:00:36.157','PayTime':'2016-06-15T11:00:41.74','BussTime':'2016-06-15T11:00:35.377'},{'OrderNo':'1606152101200011','BussType':'1','BussSN':'160615001282','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T11:11:07.443','PayTime':'2016-06-15T11:11:11.013','BussTime':'2016-06-15T11:11:05.307'},{'OrderNo':'1606152101200005','BussType':'1','BussSN':'160615001112','PayMoney':10.0000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T10:39:12.273','PayTime':'2016-06-15T10:39:15.05','BussTime':'2016-06-15T10:39:11.043'},{'OrderNo':'1606152101200019','BussType':'1','BussSN':'','PayMoney':0.0000,'Source':'3','PayType':'4','OrderTime':'2016-06-15T15:17:35.863','PayTime':'2016-06-15T15:19:14.83','BussTime':'2016-06-15T15:11:51.977'},{'OrderNo':'1606162101100004','BussType':'2','BussSN':'','PayMoney':0.0000,'Source':'3','PayType':'4','OrderTime':'2016-06-16T10:46:45.9','PayTime':'2016-06-16T10:46:47.707','BussTime':'2016-06-16T10:46:54.243'},{'OrderNo':'1606151104200070','BussType':'2','BussSN':null,'PayMoney':0.0000,'Source':'1','PayType':'4','OrderTime':'2016-06-15T13:56:42.227','PayTime':'2016-06-15T13:56:42.943','BussTime':'2016-06-15T13:56:48.887'},{'OrderNo':'1606152101100014','BussType':'2','BussSN':'160615001738','PayMoney':15.5000,'Source':'3','PayType':'0','OrderTime':'2016-06-15T14:34:38.317','PayTime':'2016-06-15T14:34:41.967','BussTime':'2016-06-15T14:34:42.497'},{'OrderNo':'1606152101100015','BussType':'2','BussSN':'2016061521001004040233728470','PayMoney':3.3000,'Source':'3','PayType':'3','OrderTime':'2016-06-15T14:35:32.557','PayTime':'2016-06-15T14:35:51','BussTime':'2016-06-15T14:35:54.163'},{'OrderNo':'1606152101100013','BussType':'2','BussSN':'','PayMoney':0.0000,'Source':'3','PayType':'4','OrderTime':'2016-06-15T14:33:22.657','PayTime':'2016-06-15T14:33:24.59','BussTime':'2016-06-15T14:33:30.44'}]}";
		
		JSONObject obj = JSONObject.fromObject(json);
		String date = obj.get("data").toString();		
		System.out.println(date);
		TParm parmR = new TParm();
		JSONArray list = JSONArray.fromObject(date);
		for (int i = 0; i < list.size(); i++) {
			 JSONObject info=list.getJSONObject(i);
			 
			 parmR.addData("OrderNo", info.getString("OrderNo"));
			 parmR.addData("BussType", getBussType(info.getString("BussType")));
			 parmR.addData("BussSN", info.getString("BussSN"));
			 parmR.addData("PayMoney", info.getString("PayMoney"));
			 parmR.addData("Source", getSource(info.getString("Source")));
			 parmR.addData("PayType", getPayType(info.getString("PayType")));
			 if(info.get("OrderTime") != null && info.getString("OrderTime").length() > 0){
				 parmR.addData("OrderTime", info.getString("OrderTime").substring(0,19).replace("T", " ")); 
			 }else{
				 parmR.addData("OrderTime", "");
			 }
			 
			 if(info.get("PayTime") != null && info.getString("PayTime").length() > 0){
				 parmR.addData("PayTime", info.getString("PayTime").substring(0,19).replace("T", " ")); 
			 }else{
				 parmR.addData("PayTime", "");
			 }
			 
			 if(info.get("BussTime") != null && info.getString("BussTime").length() > 0){
				 parmR.addData("BussTime", info.getString("BussTime").substring(0,19).replace("T", " ")); 
			 }else{
				 parmR.addData("BussTime", "");
			 }

		}
		parmR.setCount(parmR.getCount("OrderNo"));
		System.out.println(parmR);
		
		
		return parmR;
	
		//return null;
	}
	
	public TParm unRegQe(TParm parm){
		TParm result = new TParm(); 
		try {
	    	
			String ip=ServiceForTXYYSoapClient.getInstance().getWebServicesIp();
			
	    	HttpClient httpClient = new DefaultHttpClient();
	    	
	    	String url="http://"+ip+"/Service/QyiToHIS_Service.ashx?action=BACKAPPOINT" +	    			
	    			"&HISPatientID=" +parm.getValue("MR_NO")+
	    			"&AppointCode=" +parm.getValue("CASE_NO")+
	    			"&OrderNo=" +
	    			"&BusinessFrom=" +parm.getValue("OPT_USER")+
	    			"&CancelType=1";
	    	
	    	System.out.println("退预约网址=="+url);
	    	
	    	HttpGet httpGet = new HttpGet(url);
	    	
	    	HttpResponse httpResponse =	httpClient.execute(httpGet);
	    	
//	    	System.out.println(httpResponse);
	    	
	    	HttpEntity httpEntity = httpResponse.getEntity();
	    	
//	    	System.out.println(httpEntity);
	    	
	    	InputStream inputStream = httpEntity.getContent();
	    	
//	    	System.out.println(inputStream);
	    	
	    	BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));
	    	
//	    	System.out.println(bufferedReader);
	    	
	    	
	    	String json =  bufferedReader.readLine();

	    	System.out.println("退预约返回json==="+json);
	    	
	    	JSONObject obj = JSONObject.fromObject(json);
	
	    	String message =obj.get("message").toString();
	    	String errorcode =obj.get("errorcode").toString();

	    	result.setData("message", message);
	    	result.setData("errorcode", errorcode);
	    	return result;

	    	
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
			
			
	}
	

	public String getBussType(String code){
		String desc = code;
		if("1".equals(code)){
			desc="挂号";
		}else if("2".equals(code)){
			desc="缴费";
		}else if("3".equals(code)){
			desc="预约";
		}else if("4".equals(code)){
			desc="退号";
		}else if("5".equals(code)){
			desc="退费";
		}else if("6".equals(code)){
			desc="退预约";
		}else if("7".equals(code)){
			desc="取号（报到）";
		}
		return desc;
		
	}
	
	public String getSource(String code){
		String desc = code;
		if("1".equals(code)){
			desc="手机APP";
		}else if("2".equals(code)){
			desc="网站";
		}else if("3".equals(code)){
			desc="自助机";
		}else if("4".equals(code)){
			desc="窗口";
		}else if("5".equals(code)){
			desc="电话";
		}
		return desc;
		
	}
	
	public String getPayType(String code){
		String desc = code;
		if("1".equals(code)){
			desc="银联线下";
		}else if("2".equals(code)){
			desc="银联线上";
		}else if("3".equals(code)){
			desc="支付宝";
		}else if("4".equals(code)){
			desc="无需支付";
		}else if("0".equals(code)){
			desc="院内卡";
		}
		return desc;
		
	}
	
	
	

}
