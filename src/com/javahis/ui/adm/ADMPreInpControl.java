package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

import jdo.adm.ADMXMLTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTextFormat;

/**
 * Ԥ�Ǽ�
 * @author Administrator
 *
 */
public class ADMPreInpControl extends TControl{
	TParm parm;
	String preType="";
	String bedNo="";
	String caseNo="";
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		Object obj=this.getParameter();
		if(obj instanceof TParm)
			parm=(TParm) obj;
		initPage();
	}
	/**
	 * 
	 */
	public void initPage(){
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.setValue("IPD_NO", parm.getValue("IPD_NO"));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));
		this.setValue("SEX_CODE", parm.getValue("SEX_CODE"));
		this.setValue("AGE", parm.getValue("AGE"));
		this.setValue("PRETREAT_OUT_DEPT",parm.getValue("PRETREAT_OUT_DEPT"));
		this.setValue("PRETREAT_OUT_STATION",parm.getValue("PRETREAT_OUT_STATION"));
		this.setValue("NURSING_CLASS_CODE",parm.getValue("NURSING_CLASS_CODE"));
		caseNo = parm.getValue("CASE_NO");
		//�ж�����������	
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(SystemTool.getInstance().getDate().getTime());
		calendar.get(Calendar.AM_PM);
		switch (calendar.get(Calendar.AM_PM)) {
		case Calendar.AM:
			this.setValue("PRETREAT_DATE",SystemTool.getInstance().getDate().toString().substring(0,10).replaceAll("-", "/")+" 12:00:00");
			break;
			
		case Calendar.PM:
			this.setValue("PRETREAT_DATE",SystemTool.getInstance().getDate().toString().substring(0,10).replaceAll("-", "/")+" 23:59:59");
			break;

		}
		//=================
		bedNo=(String) parm.getValue("BED_NO");
	}
	/**
	 * ����
	 */
	public void onSave(){
		if(!checkData())
			return ;
		
		if(this.getRadioButton("STATION_FLG").isSelected()){
			preType="2";
		}else if(this.getRadioButton("OPERATION_FLG").isSelected()){
			preType="3";
		}else{
			preType="4";
		}
		String preNo=SystemTool.getInstance().getNo("ALL", "ADM", "PRETREAT_NO",
        "PRETREAT_NO"); //����ȡ��ԭ��
		String date=SystemTool.getInstance().getDate().toString().replaceAll("-", "/").substring(0,19);
		String inStation="";
		String inDept="";
		if(!this.getRadioButton("OUT_FLG").isSelected()){
			inStation = this.getValue("PRETREAT_IN_STATION").toString();
			inDept = this.getValue("PRETREAT_IN_DEPT").toString();
		}
		String sql="INSERT INTO " +
				" ADM_PRETREAT(PRETREAT_NO,MR_NO,IPD_NO,PRETREAT_IN_STATION," +
				" PRETREAT_IN_DEPT,PRETREAT_OUT_STATION," +
				" PRETREAT_OUT_DEPT,NURSING_CLASS_CODE,PRETREAT_TYPE,PATIENT_CONDITION," +
				" PRETREAT_DATE,OPT_TREAM,OPT_USER,OPT_DATE,EXEC_FLG) VALUES ('"+preNo+"','"+this.getValue("MR_NO")+"','"+this.getValue("IPD_NO")+"'," +
				" '"+inStation+"','"+inDept+"'," +
				" '"+this.getValue("PRETREAT_OUT_STATION")+"','"+this.getValue("PRETREAT_OUT_DEPT")+"'," +
				" '"+this.getValue("NURSING_CLASS_CODE")+"','"+preType+"','"+parm.getValue("PATIENT_CONDITION")+"'," +
				" TO_DATE('"+this.getValue("PRETREAT_DATE").toString().substring(0,19).replaceAll("-", "/")+"','yyyy/MM/dd HH24:mi:ss')," +
				"'"+Operator.getIP()+"','"+Operator.getID()+"',TO_DATE('"+date+"','yyyy/MM/dd HH24:mi:ss'),'N')";
		TParm result=new TParm(TJDODBTool.getInstance().update(sql));
		if(result.getErrCode()<0){
			this.messageBox("����ʧ��");
			return;
		}
		this.messageBox("����ɹ�");
		sql="UPDATE SYS_BED SET PRE_FLG='Y' , PRETREAT_OUT_NO='"+preNo+"' WHERE BED_NO='"+bedNo+"'";
		result=new TParm(TJDODBTool.getInstance().update(sql));
		//add by huangtt 20170502 start ���Ͳ���������Ϣ		
		TParm xmlParm = ADMXMLTool.getInstance().creatPatXMLFile(caseNo);
        if (xmlParm.getErrCode() < 0) {
            this.messageBox("�������ӿڷ���ʧ�� " + xmlParm.getErrText());
        }
        //add by huangtt 20170502 end ���Ͳ���������Ϣ
		this.closeWindow();
	}
	
	/**
	 * У������
	 */
	public boolean checkData(){
		if(!this.getRadioButton("OUT_FLG").isSelected()){
			if(this.getValue("PRETREAT_IN_DEPT")==null){
				this.messageBox("ת����Ҳ���Ϊ��");
				return false;
			}
			if(this.getValue("PRETREAT_IN_STATION")==null){
				this.messageBox("ת�벡������Ϊ��");
				return false;
			}
		}
		if(this.getValue("PRETREAT_DATE")==null){
			this.messageBox("ת��ʱ�䲻��Ϊ��");
			return false;
		}
		return true;
	}
	
	/**
	 * 
	 * @return
	 */
	public TRadioButton getRadioButton(String tagName){
		return (TRadioButton) this.getComponent(tagName);
	}
	
	/**
	 * 
	 * @return
	 */
	public TTextFormat getTextFormat(String tagName){
		return (TTextFormat) this.getComponent(tagName);
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.clearValue("PRETREAT_DATE;PRETREAT_IN_STATION;PRETREAT_IN_DEPT;PRETREAT_OUT_DEPT;PRETREAT_OUT_STATION;NURSING_CLASS_CODE");
		caseNo="";
	}
}
