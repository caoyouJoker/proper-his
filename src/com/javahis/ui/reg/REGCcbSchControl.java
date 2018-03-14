package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.reg.REGCcbSchtool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ҽ��ͨ��Ŀ���Ű�
 * </p>
 * 
 * <p>
 * Description:ҽ��ͨ��Ŀ���Ű�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:BuleCore
 * </p>
 * 
 * @author zhangp 20120815
 * @version 1.0
 */
public class REGCcbSchControl extends TControl{
	String startdate="";
	 /**
	  * ��ʼ��
	 */
	public void onInit() {
	 super.onInit();
	 Timestamp sysDate = SystemTool.getInstance().getDate();
	 String date = StringTool.getString(sysDate, "yyyyMMdd");//ϵͳ����
	 callFunction("UI|tButton_1|setEnabled", false);//14�հ�ť�û�
	 //��ȡ�ҺŲ�������CCB_DATE 
	 String sql =" SELECT CCB_DATE " +
	 		     " FROM REG_SYSPARM";
     TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//     System.out.println("result=============="+result);
     String ccbdate  = result.getValue("CCB_DATE",0);//��������
     if(Double.parseDouble(date)>=Double.parseDouble(ccbdate)){
    	 callFunction("UI|tButton_1|setEnabled",true);//14�հ�ť	����ʹ�� 
    	//ת�����ͳ�YYYY-MM-DD 00:00:00
         startdate = ccbdate.substring(0,4)+"-"+ccbdate.substring(4,6)+"-"+
                            ccbdate.substring(6,8)+" 00:00:00";       
     }    
    }
	/**
	 * ���չҺ�
	 */
	public void q1(){
		String today = SystemTool.getInstance().getDate().toString();
		String newdate = today.substring(0, 4) + today.substring(5, 7)
				+ today.substring(8, 10);
		//��ѯ�Ƿ���ͬһʱ�Ρ����ҡ�ҽʦ������
		TParm parmRoom = REGCcbSchtool.getInstance().Room(newdate,newdate);
		if(parmRoom.getData("ADM_DATE")!=null){
			this.messageBox("����ͬһʱ�Ρ����ҡ�ҽʦ�����ݣ�����ϵ��Ϣ��");
			return ;
		}
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"upload", new TParm());
		Map<String, Object> map = (HashMap) parm.getData();
		Map<String, Object> result = (HashMap) map.get("Result");
//		System.out.println("result=============="+result);
		if(result.get("EXECUTE_FLAG").equals("0"))
		this.messageBox("���ͳɹ�");	
		else
		this.messageBox(result.get("EXECUTE_MESSAGE").toString());		
	}
	
	/**
	 * ԤԼ�Һ�
	 */
	public void q2(){
		Timestamp nextdate = StringTool.getTimestamp(startdate, "yyyy-MM-dd HH:mm:ss");
		String date14 = StringTool.rollDate(nextdate, 1).toString();
		String startdate14 = date14.substring(0, 4) + date14.substring(5, 7)
				+ date14.substring(8, 10);
		String day14 = StringTool.rollDate(nextdate, 14).toString();
		String enddate14 = day14.substring(0, 4) + day14.substring(5, 7)
				+ day14.substring(8, 10);
		//��ѯ�Ƿ���ͬһʱ�Ρ����ҡ�ҽʦ������
		TParm parmRoom = REGCcbSchtool.getInstance().Room(startdate14,enddate14);
		if(parmRoom.getData("ADM_DATE")!=null){
			this.messageBox("����ͬһʱ�Ρ����ҡ�ҽʦ�����ݣ�����ϵ��Ϣ��");
			return ;
		}
		TParm parm1 = new TParm();
		parm1.setData("STRAT_DATE",startdate);
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"upload2", parm1);
		Map<String, Object> map = (HashMap) parm.getData();
		Map<String, Object> result = (HashMap) map.get("Result");
		if(result.get("EXECUTE_FLAG").equals("0"))
		this.messageBox("���ͳɹ�");	
		else
		this.messageBox(result.get("EXECUTE_MESSAGE").toString());	
	}
	
	/**
	 * ������Ϣ
	 */
	public void dept(){
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"uploadDept", new TParm());
		Map<String, Object> map = (HashMap) parm.getData();
		Map<String, Object> result = (HashMap) map.get("Result");
		if(result.get("EXECUTE_FLAG").equals("0"))
		this.messageBox("���ͳɹ�");	
		else
		this.messageBox(result.get("EXECUTE_MESSAGE").toString());	
	}
	
	/**
	 * ҽʦ��Ϣ
	 */
	public void dr(){ 
		TParm parm = TIOM_AppServer.executeAction("action.reg.REGCcbSchAction",
				"uploadDr", new TParm());
		Map<String, Object> map = (HashMap) parm.getData();
		Map<String, Object> result = (HashMap) map.get("Result");
		if(result.get("EXECUTE_FLAG").equals("0"))
		this.messageBox("���ͳɹ�");	
		else
		this.messageBox(result.get("EXECUTE_MESSAGE").toString());	
	}
}
