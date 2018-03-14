package action.med;

import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import jdo.sys.SystemTool;

import org.apache.axis.client.Call;
import org.apache.axis.client.Service;
import org.apache.axis.encoding.XMLType;

import com.dongyang.action.TAction;
import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

public class MedLisOrRisAction extends TAction{
	
	
	public TParm onSave(TParm parm)  {
//		System.out.println("lis������"+parm);
		String cat1Type = parm.getValue("CAT1_TYPE"); 		 
		String applicationNo = parm.getValue("APPLICATION_NO");
		
		
		String patient_name="";  //���� 
		String patient_bingannum = ""; //������
		String patient_cardnum=""; //���֤��
		String msg_type=""; //��Ϣ����
		String msg_content=""; //��Ϣ����
		String check_time=""; //����/�������
		String msg_time=StringTool.getString(SystemTool.getInstance().getDate(), "yyyy-MM-dd HH:mm:ss"); //��Ϣʱ��
		
		String sql = "";
		if("LIS".equals(cat1Type)){
			sql = "SELECT DISTINCT B.OPT_DATE ,A.MR_NO,A.PAT_NAME,C.IDNO," +
					" A.ORDER_DESC  FROM MED_APPLY A,MED_LIS_RPT B ,SYS_PATINFO C " +
					" WHERE A.APPLICATION_NO = B.APPLICATION_NO" +
					" AND A.ORDER_NO = B.ORDER_NO" +
					" AND A.MR_NO = C.MR_NO" +
					" AND A.APPLICATION_NO='"+applicationNo+"'";
			msg_type="����֪ͨ";
		}
		if("RIS".equals(cat1Type)){
			sql = "SELECT DISTINCT B.OPT_DATE ,A.MR_NO,A.PAT_NAME,C.IDNO," +
					" A.ORDER_DESC  FROM MED_APPLY A,MED_RPTDTL B ,SYS_PATINFO C" +
					" WHERE A.APPLICATION_NO = B.APPLICATION_NO" +
					" AND A.MR_NO = C.MR_NO" +
					" AND A.APPLICATION_NO='"+applicationNo+"'";
			
			msg_type="���֪ͨ";
		}
		
		TParm parmL = new TParm(TJDODBTool.getInstance().select(sql));
		if(parmL.getCount() > 0){
			patient_name = parmL.getValue("PAT_NAME", 0);
			patient_bingannum = parmL.getValue("MR_NO", 0);
			patient_cardnum = parmL.getValue("IDNO", 0);
			msg_content = parmL.getValue("ORDER_DESC", 0);
			check_time =StringTool.getString(parmL.getTimestamp("OPT_DATE", 0) , "yyyy-MM-dd HH:mm:ss");
				
		}else{
			return parmL;
		}
	
//		System.out.println("������������");

		String re ="";
		
		try {
			TConfig config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
			String ip = config.getString("", "WEB_SERVICES_IP_APPNV");
//			String endpointURL = "http://192.168.8.238:8080/web/services/dictionaryService/getCisDate";
//			String endpointURL = "http://"+ip+"/HospitalAppNV/cxf/CisServer?wsdl";
			String endpointURL = "http://"+ip+"/HospitalAppNV/cxf/CisServer";
			 Service service = new Service();  
             Call call = (Call) service.createCall();
			call.setTimeout(60000);
			call.setTargetEndpointAddress(endpointURL);
			call.setOperationName("cisData");//WSDL���������Ľӿ�����  		
			//  ���� ������ ���֤�� ��Ϣ���� ��Ϣ���� ��Ϣʱ��  XSD_DATE
			call.addParameter("arg0", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);//�ӿڵĲ���  
			call.addParameter("arg1", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);//�ӿڵĲ���  
			call.addParameter("arg2", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);//�ӿڵĲ���  
			call.addParameter("arg3", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);//�ӿڵĲ���  
			call.addParameter("arg4", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);//�ӿڵĲ���  
			call.addParameter("arg5", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);//�ӿڵĲ���  
			call.addParameter("arg6", org.apache.axis.encoding.XMLType.XSD_STRING,javax.xml.rpc.ParameterMode.IN);//�ӿڵĲ���  			
			call.setReturnType(org.apache.axis.encoding.XMLType.XSD_STRING);//���÷�������  			
//			System.out.println("call: " + call);			
			re = (String)call.invoke(new Object[]{patient_name,patient_bingannum,patient_cardnum,msg_type,msg_content,check_time,msg_time});  
			System.out.println("���ؽ��������"+re);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		
//		System.out.println("��ȥ��������");
		TParm result = new TParm();	
		result.setData("result", re);
		return result;
		
	}

}
