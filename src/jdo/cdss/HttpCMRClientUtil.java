package jdo.cdss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;


public class HttpCMRClientUtil {
	
	 public String crmClient(String url) throws URISyntaxException, ClientProtocolException, IOException{  
	        //String httpOrgCreateTest = url + "lifebank/membermgmt.do?method=CmrRegister&mobile=13502003530&password=123456";  
//	    	String httpOrgCreateTest = url;
	        String content = null;
	        
	        HttpClient client = new DefaultHttpClient();
	        
	        HttpGet request = new HttpGet();  
	        URL urll = new URL(url);
	        System.out.println(urll.getProtocol());
	        System.out.println(urll.getHost());
	        System.out.println(urll.getPath());
	        System.out.println(urll.getQuery());
	        URI uri = new URI(urll.getProtocol(), urll.getHost() + ":" + urll.getPort(), urll.getPath(), urll.getQuery(), null);
	        System.out.println(uri);
//	        request.setURI(new URI(httpOrgCreateTest));  
	        request.setURI(uri);  
	        HttpResponse response = client.execute(request);
	        BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuffer sb = new StringBuffer("");  
	        String line = "";  
	        String NL = System.getProperty("line.separator");  
	        while ((line = in.readLine()) != null) {  
	            sb.append(line + NL);  
	        }  
	        in.close();  
	        content = sb.toString(); 
	        return content;
	       
	    }  
	 
	 public String post(String url,String inStr) throws Exception{
		 String content = null;
		 HttpClient client = new DefaultHttpClient();
//		 HttpPost post = new HttpPost("http://192.168.8.144:8080/drools/rest/cdss.do?method=fireRules2");
		 HttpPost post = new HttpPost(url);
		 post.addHeader("Content-Type", "application/json;charset=gbk"); 
		 StringEntity entity = new StringEntity(inStr,"gbk");
//		 System.out.println("inStr=="+inStr);
//		 StringEntity entity = new StringEntity("{\"admType\":\"I\",\"advicePojos\":[],\"advise\":[],\"age\":69,\"ageDay\":21,\"ageMonth\":10,\"allergyFlg\":\"\",\"allergyPojos\":[],\"breakPoints\":[],\"calWeight\":0,\"diags\":[\"23\",\"01\",\"10\",\"18\"],\"erdPojos\":[],\"exaPojos\":[{\"id\":\"3qeC9EX6\",\"testUnit\":\"\",\"testValue\":1,\"testValueText\":\"\",\"testitemCode\":\"294102\"},{\"id\":\"z17sljYk\",\"testUnit\":\"10^9/L\",\"testValue\":138,\"testValueText\":\"\",\"testitemCode\":\"190015\"},{\"id\":\"pG4vdQAM\",\"testUnit\":\"umol/L\",\"testValue\":73,\"testValueText\":\"\",\"testitemCode\":\"202\"},{\"id\":\"p3UxIflX\",\"testUnit\":\"umol/L\",\"testValue\":426,\"testValueText\":\"\",\"testitemCode\":\"205\"},{\"id\":\"jOpcZb0Q\",\"testUnit\":\"g/L\",\"testValue\":66,\"testValueText\":\"\",\"testitemCode\":\"206\"},{\"id\":\"fDJr8cdV\",\"testUnit\":\"g/L\",\"testValue\":44,\"testValueText\":\"\",\"testitemCode\":\"207\"},{\"id\":\"SZnPH5kT\",\"testUnit\":\"umol/L\",\"testValue\":13.9,\"testValueText\":\"\",\"testitemCode\":\"210\"},{\"id\":\"0748Tsle\",\"testUnit\":\"umol/L\",\"testValue\":4.8,\"testValueText\":\"\",\"testitemCode\":\"212\"},{\"id\":\"9TP92Ing\",\"testUnit\":\"mmol/L\",\"testValue\":6.9,\"testValueText\":\"\",\"testitemCode\":\"214\"},{\"id\":\"zMIkRW2C\",\"testUnit\":\"mmol/L\",\"testValue\":4,\"testValueText\":\"\",\"testitemCode\":\"216\"},{\"id\":\"FyKYvc6M\",\"testUnit\":\"U/L\",\"testValue\":31,\"testValueText\":\"\",\"testitemCode\":\"273\"},{\"id\":\"Gyx0jNv5\",\"testUnit\":\"U/L\",\"testValue\":17,\"testValueText\":\"\",\"testitemCode\":\"276\"},{\"id\":\"ADremNOf\",\"testUnit\":\"U/L\",\"testValue\":28,\"testValueText\":\"\",\"testitemCode\":\"279\"},{\"id\":\"QUDsi18N\",\"testUnit\":\"umol/L\",\"testValue\":73,\"testValueText\":\"\",\"testitemCode\":\"202\"},{\"id\":\"2rhN0TkT\",\"testUnit\":\"umol/L\",\"testValue\":426,\"testValueText\":\"\",\"testitemCode\":\"205\"},{\"id\":\"enSXDuH7\",\"testUnit\":\"g/L\",\"testValue\":66,\"testValueText\":\"\",\"testitemCode\":\"206\"},{\"id\":\"ovDxBEoh\",\"testUnit\":\"g/L\",\"testValue\":44,\"testValueText\":\"\",\"testitemCode\":\"207\"},{\"id\":\"CRlBLm9s\",\"testUnit\":\"umol/L\",\"testValue\":13.9,\"testValueText\":\"\",\"testitemCode\":\"210\"},{\"id\":\"fjsYjTCv\",\"testUnit\":\"umol/L\",\"testValue\":4.8,\"testValueText\":\"\",\"testitemCode\":\"212\"},{\"id\":\"RzdbSo4v\",\"testUnit\":\"mmol/L\",\"testValue\":6.9,\"testValueText\":\"\",\"testitemCode\":\"214\"},{\"id\":\"a2a4SL29\",\"testUnit\":\"mmol/L\",\"testValue\":4,\"testValueText\":\"\",\"testitemCode\":\"216\"},{\"id\":\"VJkpmte3\",\"testUnit\":\"U/L\",\"testValue\":31,\"testValueText\":\"\",\"testitemCode\":\"273\"},{\"id\":\"Gc8biygH\",\"testUnit\":\"U/L\",\"testValue\":17,\"testValueText\":\"\",\"testitemCode\":\"276\"},{\"id\":\"94Bron9S\",\"testUnit\":\"U/L\",\"testValue\":28,\"testValueText\":\"\",\"testitemCode\":\"279\"},{\"id\":\"iyPvuMJJ\",\"testUnit\":\"umol/L\",\"testValue\":73,\"testValueText\":\"\",\"testitemCode\":\"202\"},{\"id\":\"te3FOkP3\",\"testUnit\":\"umol/L\",\"testValue\":426,\"testValueText\":\"\",\"testitemCode\":\"205\"},{\"id\":\"GqdBxPHt\",\"testUnit\":\"g/L\",\"testValue\":66,\"testValueText\":\"\",\"testitemCode\":\"206\"},{\"id\":\"Au171Ou3\",\"testUnit\":\"g/L\",\"testValue\":44,\"testValueText\":\"\",\"testitemCode\":\"207\"},{\"id\":\"CIfudx3j\",\"testUnit\":\"umol/L\",\"testValue\":13.9,\"testValueText\":\"\",\"testitemCode\":\"210\"},{\"id\":\"nccEoOE0\",\"testUnit\":\"umol/L\",\"testValue\":4.8,\"testValueText\":\"\",\"testitemCode\":\"212\"},{\"id\":\"W1gvZLBi\",\"testUnit\":\"mmol/L\",\"testValue\":6.9,\"testValueText\":\"\",\"testitemCode\":\"214\"},{\"id\":\"EQ8Djruu\",\"testUnit\":\"mmol/L\",\"testValue\":4,\"testValueText\":\"\",\"testitemCode\":\"216\"},{\"id\":\"f2j8o1Yo\",\"testUnit\":\"U/L\",\"testValue\":31,\"testValueText\":\"\",\"testitemCode\":\"273\"},{\"id\":\"pb0Rx244\",\"testUnit\":\"U/L\",\"testValue\":17,\"testValueText\":\"\",\"testitemCode\":\"276\"},{\"id\":\"4pijPt12\",\"testUnit\":\"U/L\",\"testValue\":28,\"testValueText\":\"\",\"testitemCode\":\"279\"},{\"id\":\"nqrTGWtn\",\"testUnit\":\"umol/L\",\"testValue\":73,\"testValueText\":\"\",\"testitemCode\":\"202\"},{\"id\":\"C7w5iyjP\",\"testUnit\":\"umol/L\",\"testValue\":426,\"testValueText\":\"\",\"testitemCode\":\"205\"},{\"id\":\"z6sYHT2z\",\"testUnit\":\"g/L\",\"testValue\":66,\"testValueText\":\"\",\"testitemCode\":\"206\"},{\"id\":\"K71mS94a\",\"testUnit\":\"g/L\",\"testValue\":44,\"testValueText\":\"\",\"testitemCode\":\"207\"},{\"id\":\"xHWrUusd\",\"testUnit\":\"umol/L\",\"testValue\":13.9,\"testValueText\":\"\",\"testitemCode\":\"210\"},{\"id\":\"Jow2qYED\",\"testUnit\":\"umol/L\",\"testValue\":4.8,\"testValueText\":\"\",\"testitemCode\":\"212\"},{\"id\":\"EyQWp8oD\",\"testUnit\":\"mmol/L\",\"testValue\":6.9,\"testValueText\":\"\",\"testitemCode\":\"214\"},{\"id\":\"HJuHJXVm\",\"testUnit\":\"mmol/L\",\"testValue\":4,\"testValueText\":\"\",\"testitemCode\":\"216\"},{\"id\":\"tvoG5PkK\",\"testUnit\":\"U/L\",\"testValue\":31,\"testValueText\":\"\",\"testitemCode\":\"273\"},{\"id\":\"DI30hMfn\",\"testUnit\":\"U/L\",\"testValue\":17,\"testValueText\":\"\",\"testitemCode\":\"276\"},{\"id\":\"d7Ie1APm\",\"testUnit\":\"U/L\",\"testValue\":28,\"testValueText\":\"\",\"testitemCode\":\"279\"},{\"id\":\"kSK8ClJE\",\"testUnit\":\"umol/L\",\"testValue\":73,\"testValueText\":\"\",\"testitemCode\":\"202\"},{\"id\":\"5wsZVPBk\",\"testUnit\":\"umol/L\",\"testValue\":426,\"testValueText\":\"\",\"testitemCode\":\"205\"},{\"id\":\"Mq2BWJd3\",\"testUnit\":\"g/L\",\"testValue\":66,\"testValueText\":\"\",\"testitemCode\":\"206\"},{\"id\":\"ZQoZ4BJG\",\"testUnit\":\"g/L\",\"testValue\":44,\"testValueText\":\"\",\"testitemCode\":\"207\"},{\"id\":\"FXmIZNIl\",\"testUnit\":\"umol/L\",\"testValue\":13.9,\"testValueText\":\"\",\"testitemCode\":\"210\"},{\"id\":\"RKJvrLMi\",\"testUnit\":\"umol/L\",\"testValue\":4.8,\"testValueText\":\"\",\"testitemCode\":\"212\"},{\"id\":\"Ugmn2ptM\",\"testUnit\":\"mmol/L\",\"testValue\":6.9,\"testValueText\":\"\",\"testitemCode\":\"214\"},{\"id\":\"GORZTJMp\",\"testUnit\":\"mmol/L\",\"testValue\":4,\"testValueText\":\"\",\"testitemCode\":\"216\"},{\"id\":\"daZawfCh\",\"testUnit\":\"U/L\",\"testValue\":31,\"testValueText\":\"\",\"testitemCode\":\"273\"},{\"id\":\"xUHG7UgF\",\"testUnit\":\"U/L\",\"testValue\":17,\"testValueText\":\"\",\"testitemCode\":\"276\"},{\"id\":\"GblZiXVt\",\"testUnit\":\"U/L\",\"testValue\":28,\"testValueText\":\"\",\"testitemCode\":\"279\"}],\"gestationalWeeks\":0,\"lastExaPojos\":[],\"lastOrderPojos\":[{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"vnmZ3l48\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":1,\"orderCode\":\"Y0201032\",\"orderDate\":\"\",\"orderDateLong\":1480608000000,\"orderDesc\":\"\",\"orderTimeLong\":1480654187000,\"rxNo\":\"1612021389\",\"seqNo\":\"1\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"50\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"Ib9Pz1bL\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":1,\"orderCode\":\"Y1001006\",\"orderDate\":\"\",\"orderDateLong\":1480608000000,\"orderDesc\":\"\",\"orderTimeLong\":1480654189000,\"rxNo\":\"1612021389\",\"seqNo\":\"21\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"50\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"6HXiKOp6\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":1,\"orderCode\":\"Y1002004\",\"orderDate\":\"\",\"orderDateLong\":1480608000000,\"orderDesc\":\"\",\"orderTimeLong\":1480654190000,\"rxNo\":\"1612021389\",\"seqNo\":\"36\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"50\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"O0rjhBT5\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":75,\"orderCode\":\"2H022006\",\"orderDate\":\"\",\"orderDateLong\":1480608000000,\"orderDesc\":\"\",\"orderTimeLong\":1480654237000,\"rxNo\":\"1612021389\",\"seqNo\":\"65\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"23\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"LNZ0i5h4\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":20,\"orderCode\":\"2U010007\",\"orderDate\":\"\",\"orderDateLong\":1481472000000,\"orderDesc\":\"\",\"orderTimeLong\":1481506270000,\"rxNo\":\"1612120001\",\"seqNo\":\"2\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"23\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"F4OthqmQ\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":100,\"orderCode\":\"2N031012\",\"orderDate\":\"\",\"orderDateLong\":1478016000000,\"orderDesc\":\"\",\"orderTimeLong\":1478016000000,\"rxNo\":\"1612021434\",\"seqNo\":\"1\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":0,\"unit\":\"23\"},{\"freqCycle\":1,\"freqTimes\":3,\"id\":\"rg1UcCew\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":0.5,\"orderCode\":\"2G042012\",\"orderDate\":\"\",\"orderDateLong\":1478016000000,\"orderDesc\":\"\",\"orderTimeLong\":1478016000000,\"rxNo\":\"1612021434\",\"seqNo\":\"3\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":0,\"unit\":\"141\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"HQCqStpb\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":100,\"orderCode\":\"2N031012\",\"orderDate\":\"\",\"orderDateLong\":1480608000000,\"orderDesc\":\"\",\"orderTimeLong\":1480654094000,\"rxNo\":\"1612021383\",\"seqNo\":\"4\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"23\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"PukefSh0\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":75,\"orderCode\":\"2H022006\",\"orderDate\":\"\",\"orderDateLong\":1480608000000,\"orderDesc\":\"\",\"orderTimeLong\":1480654106000,\"rxNo\":\"1612021383\",\"seqNo\":\"5\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"23\"},{\"freqCycle\":1,\"freqTimes\":1,\"id\":\"qK9Yf24P\",\"liquidMainFlg\":\"\",\"liquidNo\":\"\",\"medQty\":2.5,\"orderCode\":\"2C052007\",\"orderDate\":\"\",\"orderDateLong\":1480608000000,\"orderDesc\":\"\",\"orderTimeLong\":1480654118000,\"rxNo\":\"1612021383\",\"seqNo\":\"7\",\"sysDate\":\"\",\"sysDateLong\":1481644800000,\"sysTimeLong\":1481699392000,\"tags\":[],\"takeDays\":1,\"unit\":\"23\"}],\"lmpFlg\":\"\",\"mainDiag\":\"\",\"mrNo\":\"\",\"newBornFlg\":\"N\",\"operationDiags\":[],\"orderPojos\":[],\"sex\":\"男\",\"weight\":77}","gbk");//解决中文乱码问题    
	     entity.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
         
	     post.setEntity(entity);
         
         HttpResponse response = client.execute(post);
         
         BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
	        StringBuffer sb = new StringBuffer("");  
	        String line = "";  
	        String NL = System.getProperty("line.separator");  
	        while ((line = in.readLine()) != null) {  
	            sb.append(line + NL);  
	        }  
	        in.close();  
	        content = sb.toString(); 
//	        System.out.println(sb.toString());
	        return content;
	        
		 
	 }

}
