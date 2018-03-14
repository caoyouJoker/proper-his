package action.ccb;

import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.serialization.ObjectSerializationCodecFactory;
import org.apache.mina.transport.socket.nio.NioSocketConnector;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
 
/**
 * <p>
 * Title: socket通信Client端
 * </p>
 * 
 * <p>
 * Description: socket通信Client端
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fuwj 2012-08-28
 * @version 1.0
 */
public class CBCClientAction extends TAction {

	public static Map map = null;
	
	/**
	 * 发送排班信息
	 * @param parm
	 * @return
	 */
	public TParm SendFileBtoB(TParm parm) {

		String his_code = parm.getValue("HIS_CODE");
		String ftp_date = parm.getValue("FTP_DATE");
		String seq_no = parm.getValue("SEQ_NO");
		String file_type = parm.getValue("FILE_TYPE");
		String file_name = parm.getValue("FILE_NAME");

		Random rdm = new Random();
		String guid = "";
		for (int i = 0; i < 40; i++) {
			guid += rdm.nextInt(10);
		}

		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		Map<String, Object> returnData = new HashMap<String, Object>();
		Map<String, Object> operteData = new LinkedHashMap<String, Object>();
		operteData.put("METHOD", "SendFileHtoB");
		operteData.put("OPT_ID", "");
		operteData.put("OPT_NAME", "");
		operteData.put("OPT_IP", "");
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		operteData.put("OPT_DATE", sdf.format(date));
		operteData.put("GUID", guid);
		operteData.put("TOKEN", "");
		Map<String, Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("EXECUTE_FLAG", "0");
		resultData.put("EXECUTE_MESSAGE", "");
		resultData.put("ACCOUNT", "");
		returnMap.put("ReturnData", returnData);
		returnMap.put("OperateInfo", operteData);
		returnMap.put("Result", resultData);

		Map<String, Object> commitData = new LinkedHashMap<String, Object>();
		commitData.put("HIS_CODE", his_code);
		commitData.put("FTP_DATE", ftp_date);
		commitData.put("SEQ_NO", seq_no);
		commitData.put("FILE_TYPE", file_type);
		commitData.put("FILE_NAME", file_name);
		returnMap.put("CommitData", commitData);
		Map rMap = invokeSocket(returnMap);
		return new TParm(rMap);
	}
	
	/**
	 * 发送退费请求
	 * @param parm
	 * @return
	 */
	public TParm DoReturn(TParm parm) {
System.out.println("接收参数：============================"+parm);
		Map<String, Map<String, Object>> returnMap = new LinkedHashMap<String, Map<String, Object>>();
		Map<String, Object> returnData = new LinkedHashMap<String, Object>();
				
		Random rdm = new Random();
		String guid = "";
		for (int i = 0; i < 40; i++) {
			guid += rdm.nextInt(10);
		} 
		Map<String, Object> operteData = new LinkedHashMap<String, Object>();
		operteData.put("METHOD", "DoReturn"); 
		operteData.put("OPT_ID", parm.getData("OPT_ID"));
		operteData.put("OPT_NAME", parm.getData("OPT_NAME"));
		operteData.put("OPT_IP", parm.getData("OPT_IP"));
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		operteData.put("OPT_DATE", sdf.format(date));
		operteData.put("GUID", guid); 
		operteData.put("TOKEN", "");
		Map<String, Object> resultData = new LinkedHashMap<String, Object>();
		resultData.put("EXECUTE_FLAG", "0");
		resultData.put("EXECUTE_MESSAGE", "");
		resultData.put("ACCOUNT", "");
		returnMap.put("ReturnData", returnData); 
		returnMap.put("OperateInfo", operteData);
		returnMap.put("Result", resultData);
		Map<String, Object> commitData = new LinkedHashMap<String, Object>();
		commitData.put("HIS_CODE", parm.getData("HIS_CODE"));
		commitData.put("CARD_NO", parm.getData("CARD_NO"));
		commitData.put("PERSON_NO", parm.getData("PERSON_NO"));
		commitData.put("O_TRAN_NO", parm.getData("O_TRAN_NO"));
		commitData.put("O_STREAM_NO", parm.getData("O_STREAM_NO"));
		commitData.put("O_PAY_SEQ", parm.getData("O_PAY_SEQ"));
		commitData.put("TRAN_NO", parm.getData("TRAN_NO"));
		commitData.put("AMOUNT", parm.getData("AMOUNT"));
		commitData.put("DATE", parm.getData("DATE"));
		commitData.put("RETURN_TYPE", parm.getData("RETURN_TYPE"));
		returnMap.put("CommitData", commitData);
		Map rMap = invokeSocket(returnMap);
		return new TParm(rMap);
	}
	/**
	 * 建立Scoket通信
	 * @param pmap
	 * @return
	 */
	public Map invokeSocket(Map pmap) {
		IoConnector connector = new NioSocketConnector();
		connector.setConnectTimeoutMillis(30000);
		connector.getFilterChain().addLast("codec",
				new ProtocolCodecFilter(new ObjectSerializationCodecFactory()));

		connector.setHandler(new ClientHandler(pmap));
		connector.getSessionConfig().setUseReadOperation(true);
		ConnectFuture future = connector.connect(new InetSocketAddress(
				"172.20.107.166", 2220));		
		IoSession session;
		future.awaitUninterruptibly();
		session = future.getSession();
		Map map1 = (Map) session.read().awaitUninterruptibly().getMessage();
		return map;
	}

}
