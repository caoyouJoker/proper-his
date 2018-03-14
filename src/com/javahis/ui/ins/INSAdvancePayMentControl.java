package com.javahis.ui.ins;


import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;


import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSCJAdvanceTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.sys.CTZTool;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
/**
 * <p>
 * Title:סԺ�渶�����ϴ�
 * Description:סԺ�渶�����ϴ�
 * Copyright: Copyright (c) 2017
 * @version 1.0
 */
public class INSAdvancePayMentControl extends TControl{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	SimpleDateFormat df = new SimpleDateFormat("yyyyMM");
	TTable tableInfo;// �渶�����б�
	TTable oldTable;// ��ϸ����ǰ����
	TTable newTable;// ��ϸ���ܺ�����
	TParm regionParm;// ҽ���������
	TTabbedPane tabbedPane;// ҳǩ
	int selectNewRow; // ��ϸ���ܺ����ݻ�õ�ǰѡ����
	// ����
	private Compare compare = new Compare();
	private boolean ascending = false;
	private int sortColumn = -1;
	// ��ϸ����ǰ����
	private String[] pagetwo = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	//  ��ϸ���ܺ�����
	private String[] pagethree = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"PRICE", "ADDPAY_FLG", "NHI_ORDER_CODE",
			"HYGIENE_TRADE_CODE", "NHI_ORD_CLASS_CODE","CHARGE_DATE" };
	
	/**
     * ��ʼ������
     */
    public void onInit() {
		tableInfo = (TTable) this.getComponent("TABLEINFO");//�渶�����б�
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE");// ҳǩ
		oldTable = (TTable) this.getComponent("OLD_TABLE");// ��ϸ����ǰ����
		newTable = (TTable) this.getComponent("NEW_TABLE");// ��ϸ���ܺ�����
		setValue("YEAR_MON", SystemTool.getInstance().getDate());
		setValue("START_DATE", SystemTool.getInstance().getDate());
 	    setValue("END_DATE", SystemTool.getInstance().getDate());	 
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		newTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onExaCreateEditComponent");
		 //���� �д���
        this.addEventListener("NEW_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                              "onTableChangeValue");
     // �������
		addListener(newTable);
    }
	/**
	 * У��Ϊ�շ���
	 * 
	 * @param name
	 * @param message
	 */
	private void onCheck(String name, String message) {
		this.messageBox(message);
		this.grabFocus(name);
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "��Ժ��ʼ���ڲ�����Ϊ��");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "��Ժ�������ڲ�����Ϊ��");
			return;
		}
		TParm parm = new TParm();
		parm.setData("START_DATE", sdf.format(this.getValue("START_DATE")));// ��Ժ��ʼʱ��
		parm.setData("END_DATE", sdf.format(this.getValue("END_DATE"))); // ��Ժ����ʱ��
		String sql1 ="";
	    String sql2 ="";
	    if(this.getValue("MR_NO").toString().length()>0)
			sql1 = " AND A.MR_NO = '"+ getValue("MR_NO") + "'";
		if(!this.getValue("STATUS_FLG").equals(""))	
			sql2 = " AND A.STATUS_FLG = '"+ getValue("STATUS_FLG") + "'";	
		String SQL = " SELECT A.YEAR_MON,A.CONFIRM_NO, A.MR_NO, A.CASE_NO, A.ID_NO, A.PAT_NAME, " +
				     " CASE A.SEX_CODE  WHEN '1' THEN '��' WHEN '2' THEN 'Ů' " +
				     " ELSE '' END AS SEX_DESC,A.COMPANY_NAME,(SELECT S.CTZ_DESC FROM SYS_CTZ S " +
				     " WHERE S.NHI_NO = A.CTZ_CODE) AS CTZ_DESC,A.IN_DATE, A.DS_DATE," +
				     " A.TOTAL_AMT, A.INV_NO,CASE A.STATUS_FLG  WHEN '1' THEN '������' "+
				     " WHEN '2' THEN '���ϴ�' WHEN '3' THEN '�ѳ���' WHEN '4' THEN '�Ѷ���' " +
				     " ELSE '' END AS STATUS_FLG,A.UPLOAD_DATE,A.SPECIAL_SITUATION " +
				     " FROM INS_ADVANCE_PAYMENT A "+ 
				     " WHERE A.IN_DATE BETWEEN TO_DATE " +
				     " ('"+ parm.getValue("START_DATE")+"000000"+"','YYYYMMDDhh24miss')"+  
				     " AND TO_DATE ('"+ parm.getValue("END_DATE")+"235959"+"', 'YYYYMMDDhh24miss')" +
				     sql1+
				     sql2;	
//		System.out.println("SQL=====:"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));	
//		System.out.println("result=====:"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}
	/**
	 * ��ѯ����(�����Ų�ѯ)
	 */
	public void onQueryNO() {
		String mrno = PatTool.getInstance().checkMrno(
			TypeTool.getString(getValue("MR_NO")));
		setValue("MR_NO",mrno);
		onQuery();		
	}
		
	
	
	/**
	 * ��Ϣ����
	 */
	public void onDownload(){
		if (null == this.getValue("YEAR_MON")
				|| this.getValue("YEAR_MON").toString().length() <= 0) {
			onCheck("YEAR_MON", "��Ժ�ںŲ�����Ϊ��");
			return;
		}
		
	   TParm downParm = new TParm();
	   TParm result = new TParm();
	   String caseNo = "";
	   String yearmon = df.format(this.getValue("START_DATE"));
//	   System.out.println("yearmon=====:"+yearmon);
	   if(this.getValue("CASE_NO").toString().length()>0)
		 caseNo = getValueString("CASE_NO");
	   downParm.addData("HOSP_NHI_NO",regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
	   downParm.addData("CASE_NO", caseNo);//סԺ��
	   downParm.addData("BEGIN_DATE", yearmon);//��Ժ�ں�YYYYMM
	   downParm.addData("PARM_COUNT", 3);//�������       	   	   
	   downParm.setData("PIPELINE", "DataDown_czyd");
	   downParm.setData("PLOT_TYPE", "M");	  	    
       System.out.println("downParm:"+downParm);
       result = InsManager.getInstance().safe(downParm,"");
//       System.out.println("result:============"+result);
       if (result.getErrCode() < 0) {	        	
     	    this.messageBox(result.getErrText());
			return;
       }else{
    	//�ڲ���INS_ADVANCE_PAYMENT��֮ǰ��ѯ�Ƿ������������ݣ�������ɾ��
    	 String sql1 = "";
    	 if(this.getValue("CASE_NO").toString().length()>0)
    		 sql1 = " AND CASE_NO = '"+ caseNo + "'"; 
    	 
    	String sql = " SELECT * FROM INS_ADVANCE_PAYMENT"+
    	             " WHERE YEAR_MON = '"+ yearmon + "'"+
    	             sql1;
    	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
//    	 System.out.println("data:============count"+data.getCount());
   		if(data.getErrCode()<0){
   		    this.messageBox(data.getErrText());
   			return;
   		}
   		//�ɷ��ظ�����
   		if(data.getCount()<=0){
   			result.setData("YEAR_MON", yearmon);//��Ժ�ں�
   			result.setData("CASE_NO_FLG", caseNo);//�ж��Ƿ���һ������
   			result.setData("OPT_USER", Operator.getID());
   			result.setData("OPT_TERM", Operator.getIP());
	        this.insertAdvancePayment(result);
	        messageBox("���سɹ�");
   		}else{
   		   //ɾ��������
//   			String sqldel = " DELETE FROM INS_ADVANCE_PAYMENT"+
//            " WHERE YEAR_MON = '"+ yearmon + "'"+
//            sql1;
//            TParm datadel = new TParm(TJDODBTool.getInstance().update(sqldel));
//            if(datadel.getErrCode()<0){
//       		    this.messageBox(datadel.getErrText());
//       			return;
//       		}
// 			result.setData("YEAR_MON", yearmon);//��Ժ�ں�
//   			result.setData("CASE_NO_FLG", caseNo);//�ж��Ƿ���һ������
//   			result.setData("OPT_USER", Operator.getID());
//   			result.setData("OPT_TERM", Operator.getIP());
//	        this.insertAdvancePayment(result); 
//	        messageBox("���سɹ�");   			
   		    messageBox("������,����������");  				
   		    return;
   		}	      
     }
	}
	/**
	 * ����INS_ADVANCE_PAYMENT
	 * @param parm
	 */
	public void insertAdvancePayment(TParm parm){
//		 System.out.println("parm=====:"+parm);
		 TParm result = new TParm();
		for (int i = 0; i < parm.getCount("CONFIRM_NO"); i++){
			String sqldel = " SELECT A.MR_NO FROM ADM_INP A " +
					" WHERE A.CASE_NO = '" + parm.getValue("CASE_NO",i) + "'";
            TParm datadel = new TParm(TJDODBTool.getInstance().select(sqldel));
            String mrNo = datadel.getValue("MR_NO", 0);
//            System.out.println("mrNo=====:"+mrNo);
    		String indate = parm.getValue("IN_HOSP_DATE",i); //��Ժʱ��
    		String dsdate = parm.getValue("OUT_HOSP_DATE",i); //��Ժʱ��
//    		System.out.println("indate=====:"+indate);
//			System.out.println("dsdate=====:"+dsdate);
    		String	sql = " INSERT INTO INS_ADVANCE_PAYMENT(YEAR_MON,CONFIRM_NO,CASE_NO,MR_NO," +
            " ID_NO,PAT_NAME,SEX_CODE ,COMPANY_NAME,CTZ_CODE,IN_DATE,DS_DATE," +
            " TOTAL_AMT,STATUS_FLG,OPT_USER,OPT_DATE,OPT_TERM)" +
            " VALUES('"+ parm.getValue("YEAR_MON")+ "', " + 
            "'" + parm.getValue("CONFIRM_NO",i) + "', " +
            "'" + parm.getValue("CASE_NO",i) + "', " +
            "'" + mrNo + "', " +
            "'" + parm.getValue("SID",i) + "', " +
            "'" + parm.getValue("NAME",i) + "', " +
            "'" + parm.getValue("SEX_CODE",i) + "', " +
            "'" + parm.getValue("WORK_DEPARTMENT",i) + "', " +
            "'" + parm.getValue("CTZ_CODE",i) + "', " +
            " TO_DATE('" + indate + "','YYYYMMDDHH24MISS'), " +
            " TO_DATE('" + dsdate + "','YYYYMMDDHH24MISS'), " +
            " "+ parm.getDouble("TOTAL_AMT",i)+ "," +
            " '1',"+
            "'" + parm.getValue("OPT_USER")+ "',"+
            "SYSDATE,"+
            "'" + parm.getValue("OPT_TERM")+ "'"+
            ")";
//	      System.out.println("sql============"+sql);
	       result = new TParm(TJDODBTool.getInstance().update(sql));
			if(result.getErrCode()<0){
				messageBox(result.getErrText());
				return;
			}
		}
		onReQuery(parm);
	}
	/**
	 * ��ѯ����
	 */
	public void onReQuery(TParm parm) {	
		String sql1 = "";
		 String yearmon = parm.getValue("YEAR_MON") ;
		 String caseNo = parm.getValue("CASE_NO_FLG");
		if(caseNo.length()>0)
   		 sql1 = " AND CASE_NO = '"+ caseNo + "'";   	 
   	    String sql = " SELECT A.YEAR_MON,A.CONFIRM_NO, A.MR_NO, A.CASE_NO, A.ID_NO, A.PAT_NAME, " +
                 " CASE A.SEX_CODE  WHEN '1' THEN '��' WHEN '2' THEN 'Ů' " +
                 " ELSE '' END AS SEX_DESC,A.COMPANY_NAME,(SELECT S.CTZ_DESC FROM SYS_CTZ S " +
                 " WHERE S.NHI_NO = A.CTZ_CODE) AS CTZ_DESC,A.IN_DATE, A.DS_DATE," +
                 " A.TOTAL_AMT, A.INV_NO,CASE A.STATUS_FLG  WHEN '1' THEN '������' "+
                 " WHEN '2' THEN '���ϴ�' WHEN '3' THEN '�ѳ���' WHEN '4' THEN '�Ѷ���' " +
                 " ELSE '' END AS STATUS_FLG,A.UPLOAD_DATE,A.SPECIAL_SITUATION " +
                 " FROM INS_ADVANCE_PAYMENT A "+
   	             " WHERE YEAR_MON = '"+ yearmon + "'"+
   	             sql1;
     	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
  		if(data.getErrCode()<0){
  		    this.messageBox(data.getErrText());
  			return;
  		}
  		tableInfo.setParmValue(data);
	}

	/**
	 * ��ϸ����
	 */
	public void onSumdetail(){
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}
		parm.setData("REGION_CODE", Operator.getRegion()); // ҽԺ����
		parm.setData("NHIHOSP_NO",regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String endDate = sdf.format(SystemTool.getInstance().getDate());
//		System.out.println("endDate============"+endDate);
		parm.setData("END_DATE", endDate); // ����ʱ��
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onExeAdvance", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ��ʧ��:"+result.getErrText());
			return;
		} 
	
		this.messageBox("���ܳɹ�");
	}
	/**
	 * У���Ƿ��л�ý���
	 * 
	 * @return TParm
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("YEAR_MON"));//�ں�
		parm.setData("CASE_NO", parm.getValue("CASE_NO")); // סԺ��
		parm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); //�渶סԺ˳���
		parm.setData("START_DATE", 
				parm.getValue("IN_DATE").replace("-", "").substring(0,8));//��ʼʱ��
		parm.setData("MR_NO", parm.getValue("MR_NO"));
//		System.out.println("parm============"+parm);
		return parm;
	}
	
	/**
	 * �����ϴ�
	 */
	public void onUpload(){	
		//�Ƿ��ô�Ʊ�ݺ�(����)
//		if (null == this.getValue("INV_NO")
//				|| this.getValue("INV_NO").toString().length() <= 0) {
//			onCheck("INV_NO", "Ʊ�ݺŲ�����Ϊ��");
//			return;
//		}
		if(!this.getRadioButton("NEW_RDO_1").isSelected()){
			this.messageBox("����ȫ�����ϴ�");
			return;
		}
			
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
		//ִ�з��ó���
		this.DataDown_czys_U(parm);
		//ִ�з����ϴ�
		if(this.DataUpload_H(parm).getErrCode() < 0)
	       return;	   
		else{
	     //�ϴ������ر����˵��	
	    this.DataDown_czys_T(parm);
	    this.messageBox("�ϴ��ɹ�");
		}      
	}
	 /**
     * ִ�з����ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_H(TParm parm) {
		TParm tableParm = null;
		TParm newParm = new TParm(); // �ϴ�����
		TParm result = new TParm();
		TParm parmValue = newTable.getParmValue(); // �����ϸ���ܺ�����
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			String nhiOrderCode = tableParm.getValue("NHI_ORDER_CODE");
			//ȥ���ϼ���
			if (nhiOrderCode.equals("")) {// ҽ������
				continue;
			}
			newParm.addData("CONFIRM_NO", tableParm.getValue("CONFIRM_NO"));//�渶סԺ˳���
			newParm.addData("HOSP_NHI_NO",regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
			String chargedate  = tableParm.getValue("CHARGE_DATE").replace("/", "-")+" 00:00:00";
//	 		System.out.println("chargedate============"+chargedate);	 		
			newParm.addData("CHARGE_DATE", chargedate); // ��ϸ¼��ʱ��
			newParm.addData("SEQ_NO", tableParm.getValue("SEQ_NO"));//���
			newParm.addData("NHI_CODE", tableParm.getValue("NHI_ORDER_CODE"));//��Ŀҽ������
			newParm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC"));//ҽ������
			newParm.addData("PRICE", tableParm.getDouble("PRICE"));//����
			newParm.addData("QTY", tableParm.getInt("QTY"));//����
			newParm.addData("TOTAL_AMT", tableParm.getDouble("TOTAL_AMT"));//�ܽ��          
            newParm.addData("ADD_FLG", 
            		tableParm.getValue("ADDPAY_FLG").equals("Y")? "1" : "0");//�ۼ�������־
            newParm.addData("PZWH", tableParm.getValue("HYGIENE_TRADE_CODE"));//��׼�ĺ�	
            newParm.addData("PRINT_NO", this.getValue("INV_NO"));//ҽ��ר��Ʊ�ݺ�
            newParm.addData("PARM_COUNT", 12);//�������   
		}
            newParm.setData("PIPELINE", "DataUpload");
            newParm.setData("PLOT_TYPE", "H");	  	    
//            System.out.println("newParm:====="+newParm);
            result = InsManager.getInstance().safe(newParm);
//            System.out.println("result:====="+result);
         if (result.getErrCode() < 0) {	        	
      	    //ִ�з��ó��� 
      	    this.DataDown_czys_U(parm);  
      	    this.messageBox("�ϴ�ʧ��");
 			return result; 
            }else{
		   //����INS_ADVANCE_PAYMENT��״̬ 2 ���ϴ� ��Ʊ�ݺź��ϴ�ʱ��
		 String sql1 = " UPDATE INS_ADVANCE_PAYMENT " +
             " SET STATUS_FLG = '2'," +
             " INV_NO = '" + this.getValue("INV_NO") + "'," +
             " UPLOAD_DATE = SYSDATE" +
             " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
		 result = new TParm(TJDODBTool.getInstance().update(sql1));	
		 if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return result;
         }
		 //����INS_IBS_UPLOAD_ADVANCE��Ʊ�ݺ�
		 String sql2 = " UPDATE INS_IBS_UPLOAD_ADVANCE " +
         " SET INV_NO = '" + this.getValue("INV_NO") + "'" +
         " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
	     result = new TParm(TJDODBTool.getInstance().update(sql2)); 
	       if (result.getErrCode() < 0) {
               this.messageBox(result.getErrText());
               return result;
       }		 
    }
         return result;
 }
    /**
     * ����渶��������ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_T(TParm parm) {  	
    	TParm result = new TParm();
    	//�ϴ������ر����˵��
		String specialSitu = this.getValueString("SPECIAL_SITUATION");
	    if(specialSitu.length()>0){
	    parm.setData("SPECIAL_SITUATION",specialSitu);    
        TParm specialParm = new TParm();
        specialParm.addData("CONFIRM_NO",parm.getValue("CONFIRM_NO"));//�渶סԺ˳���
        specialParm.addData("HOSP_NHI_NO", 
        		regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
        specialParm.addData("SPECIAL_SITUATION", 
        		parm.getValue("SPECIAL_SITUATION"));//�������˵�� 
        specialParm.addData("PARM_COUNT", 3);//�������   
        specialParm.setData("PIPELINE", "DataDown_czys");
        specialParm.setData("PLOT_TYPE", "T");       
        result = InsManager.getInstance().safe(specialParm);
//        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        //����INS_ADVANCE_PAYMENT�������
        String sql2 =
            " UPDATE INS_ADVANCE_PAYMENT " +
            " SET SPECIAL_SITUATION = '" + parm.getData("SPECIAL_SITUATION") + "' " +
            " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
//        System.out.println("sql2=======" + sql2);
        result = new TParm(TJDODBTool.getInstance().update(sql2));	        
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }      
    }
	    return result;
  }
	
	/**
	 * ���ó���
	 */
	public void onCancel(){
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
		if(this.DataDown_czys_U(parm).getErrCode() < 0){
		   messageBox("����ʧ��");
		   return;
		}
		else{
	    //����INS_ADVANCE_PAYMENT״̬ 3�ѳ���
	     String sql = " UPDATE INS_ADVANCE_PAYMENT " +
		            " SET STATUS_FLG = '3' " +
		            " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "' ";
//		 System.out.println("sql=======" + sql);
		 TParm  result = new TParm(TJDODBTool.getInstance().update(sql));
		 if (result.getErrCode() < 0) {
	            this.messageBox(result.getErrText());
	            return;
	        }      
		 messageBox("�����ɹ�");
		}
	}
	 /**
     * �渶����
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_U(TParm parm) {
        TParm result = new TParm();
        TParm cancelParm = new TParm(); // ��������
        cancelParm.addData("CONFIRM_NO", parm.getValue("CONFIRM_NO"));//�渶סԺ˳���
        cancelParm.addData("HOSP_NHI_NO", 
        		regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
        cancelParm.addData("PARM_COUNT", 2);//�������   
        cancelParm.setData("PIPELINE", "DataDown_czys");
        cancelParm.setData("PLOT_TYPE", "U");      
        result = InsManager.getInstance().safe(cancelParm);
//        System.out.println("result�渶����" + result);
//        if (result.getErrCode() < 0) {
//            this.messageBox(result.getErrText());
//            return result;
//        }       
        return result;
    }
	/**
	 * ���
	 */
	public void onClear(){
		this.setValue("YEAR_MON", SystemTool.getInstance().getDate());
		this.setValue("CASE_NO", "");	
		this.setValue("INV_NO", "");
		this.setValue("MR_NO", "");
		this.setValue("STATUS_FLG", "");
		this.setValue("SPECIAL_SITUATION", "");
		tableInfo.removeRowAll();
		oldTable.acceptText();
		oldTable.setDSValue();
		oldTable.removeRowAll();
		newTable.acceptText();
		newTable.setDSValue();
		newTable.removeRowAll();
		tabbedPane.setSelectedIndex(0); // ��һ��ҳǩ
		clearValue("SUM_AMT;NEW_SUM_AMT");
	}
	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {
		switch (tabbedPane.getSelectedIndex()) {
		// 1 :��ϸ����ǰҳǩ 2����ϸ���ܺ�ҳǩ
		case 1:
			onSplitOld();
			break;
		case 2:
			onSplitNew();
			break;
		}
	}
	/**
	 * ��ϸ����ǰ����
	 */
	public void onSplitOld() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�
		//05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("OLD_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"OLD_RDO_" + i).getName());
					break;
				}
			}
		}
		TParm result = INSIbsOrderTool.getInstance().queryOldSplit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			oldTable.acceptText();
			oldTable.setDSValue();
			oldTable.removeRowAll();
			return;
			}		
		double qty = 0.00; // ����
		double totalAmt = 0.00; // �������

		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pagetwo.length; i++) {
			if (i == 0) {
				result.addData(pagetwo[i], "�ϼ�:");
				continue;
			}
			result.addData(pagetwo[i], "");
		}
		result.addData("QTY", qty);
		result.addData("TOTAL_AMT", totalAmt);
		result.setCount(result.getCount() + 1);
		oldTable.setParmValue(result);
		this.setValue("SUM_AMT", totalAmt); // ����ܽ��
	}
	/**
	 * ��ϸ���ܺ�����
	 */
	public void onSplitNew() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�
		//05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("NEW_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"NEW_RDO_" + i).getName());
					break;
				}
				else {
					parm.setData("NHI_ORD_CLASS_CODE","");	
				}
			}
		}
		String sql1 = "";
		if(parm.getValue("NHI_ORD_CLASS_CODE").length()>0)
		sql1 = " AND A.NHI_ORD_CLASS_CODE ='" + parm.getData("NHI_ORD_CLASS_CODE") + "'";
		//�����ϸ���ܺ�����
		 String sql = " SELECT A.SEQ_NO,A.ORDER_CODE,A.ORDER_DESC," +
		 " A.PRICE,A.QTY,A.TOTAL_AMT,A.ADDPAY_FLG," +
		 " A.NHI_ORDER_CODE,A.HYGIENE_TRADE_CODE,A.NHI_ORD_CLASS_CODE," +
		 " TO_CHAR(A.CHARGE_DATE,'YYYY/MM/DD') AS CHARGE_DATE,A.CONFIRM_NO,'N' AS FLG" +
		 " FROM INS_IBS_UPLOAD_ADVANCE A " +
		 " WHERE A.CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "'" +
		 " AND A.TOTAL_AMT <> 0" +
		 sql1 +
		 " ORDER BY A.SEQ_NO";
		 TParm upLoadParmOne = new TParm(TJDODBTool.getInstance().select(sql));
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		
		if (upLoadParmOne.getCount() == 0) {
			newTable.acceptText();
			newTable.setDSValue();
			newTable.removeRowAll();
			return;
			}
		double qty = 0.00; // ����
		double totalAmt = 0.00; // �������
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			qty += upLoadParmOne.getDouble("QTY", i);
			totalAmt += upLoadParmOne.getDouble("TOTAL_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pagethree.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pagethree[i], "�ϼ�:");
				continue;
			}
			upLoadParmOne.addData(pagethree[i], "");
		}
		upLoadParmOne.addData("QTY", qty);
		upLoadParmOne.addData("TOTAL_AMT", totalAmt);
		upLoadParmOne.addData("CONFIRM_NO", "");// �渶סԺ˳���
		upLoadParmOne.addData("FLG", ""); // ��������
		upLoadParmOne.addData("HYGIENE_TRADE_CODE", ""); //����׼��
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.addData("ADDPAY_FLG", "");//�ۼ�������־
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// ��Ӻϼ�
		newTable.setParmValue(upLoadParmOne);
		this.setValue("NEW_SUM_AMT", totalAmt); // �ܽ����ʾ
		callFunction("UI|upload|setEnabled", true);
	}
	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}
	/**
	 * ��ϸ���ܺ����ݱ������
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion()); // �������
		// ִ�����INS_IBS_UPLOAD_ADVANCE�����
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "updateUpLoadAdvance", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onSplitNew();
		}
	}
	/**
     * �����д���
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) { // �����ϼ�����
    	newTable.acceptText();
         TTableNode node = (TTableNode) obj;
         if (node == null) {
             return;
         }
         int row = node.getRow();        
         int column = node.getColumn();
 		// ���㵱ǰ�ܽ��
      	double qty = 0.0;
      	 if (column == 4) {
      		qty = Double.parseDouble(String.valueOf(node.getValue()));
          } else {
         	 qty = Double.parseDouble(String.valueOf(newTable.
                      getItemData(row, "QTY")));
          }
        double price = newTable.getParmValue().getDouble("PRICE",row);
        TParm parm = getTotalAmt(qty,price);
		newTable.setItem(row, "TOTAL_AMT",parm.getValue("FEES"));
//		System.out.println("newTable=====:"+newTable.getParmValue());
    }
    /**
     * �����ܽ��
     */
    public TParm getTotalAmt(double total, double ownPrice) {
        TParm parm = new TParm();
        double fees =  Math.abs(StringTool.round(total * ownPrice,2));
//    	System.out.println("fees=====:"+fees);
        parm.setData("FEES", fees);
        return parm;
    }
	/**
	 * ��ϸ���ܺ������½�����
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOTAL_AMT"};
		TParm parm = newTable.getParmValue();
//		System.out.println("parm111=======" + parm);
		TParm result = new TParm();
		// ���һ��������
		for (int i = 0; i < pagethree.length; i++) {
			result.setData(pagethree[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}
		result.setData("FLG", "Y"); // ��������
		if (parm.getCount() > 0) {
			// ��úϼ�����
			result.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO",0)); // ����˳��� ����		
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1); // �Ƴ��ϼ�
			int seqNo = -1; // ������˳�����
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1); // ˳���
			parm.setRowData(parm.getCount(), result, -1); // ����½�������
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1); // ���ϼ����·���
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("û�����ݲ������½�����");
			return;
		}
		newTable.setParmValue(parm);
	}
	/**
	 * ���SYS_FEE��������(�����鴰��)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) {
		selectNewRow = row;
		// �����ǰ�к�
		column = newTable.getColumnModel().getColumnIndex(column);
		String columnName = newTable.getParmMap(column);
		// ҽ�� �� ��������
		if ("ORDER_CODE".equalsIgnoreCase(columnName)
				|| "QTY".equalsIgnoreCase(columnName)) {
		} else {
			return;
		}
		if ("ORDER_CODE".equalsIgnoreCase(columnName)) {
			TTextField textfield = (TTextField) com;
			TParm parm = new TParm();
			parm.setData("RX_TYPE", ""); // ������ CAT1_TYPE = LIS/RIS
			textfield.onInit();
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popExaReturn");
		}
	}
	/**
	 * ���¸�ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		newTable.acceptText();
		TParm newParm = newTable.getParmValue();
		newParm
				.setData("ORDER_CODE", selectNewRow, parm
						.getValue("ORDER_CODE")); // ҽ����
		newParm
				.setData("ORDER_DESC", selectNewRow, parm
						.getValue("ORDER_DESC")); // ҽ������
		newParm.setData("PRICE", selectNewRow, parm.getDouble("OWN_PRICE")); // ����
		newParm.setData("NHI_ORDER_CODE", selectNewRow, parm
				.getValue("NHI_CODE_I")); // ҽ�����ô���
		newParm.setData("HYGIENE_TRADE_CODE", selectNewRow, parm
				.getValue("HYGIENE_TRADE_CODE")); //��׼�ĺ�
		//�ۼ�������־
		 String SQL =" SELECT LJZFBZ FROM INS_RULE"+
         " WHERE SFXMBM = '"+ parm.getValue("NHI_CODE_I") + "'";
         TParm LJZF = new TParm(TJDODBTool.getInstance().select(SQL));
        if (LJZF.getCount()>0) 
        	newParm.setData("ADDPAY_FLG",selectNewRow, 
        			LJZF.getValue("LJZFBZ",0).equals("1")? "Y" : "N");
        else           	
	        newParm.setData("ADDPAY_FLG",selectNewRow, "N");
		newTable.setParmValue(newParm);
	}
	
	/**
	 * ��ϸ���ܺ�����ɾ������
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("������ɾ���ϼ�����");
			return;
		}
        String sql = " DELETE FROM INS_IBS_UPLOAD_ADVANCE " +
        		     " WHERE CONFIRM_NO= '" + parm.getData("CONFIRM_NO",row) + "' " +
        	         " AND SEQ_NO='" + parm.getData("SEQ_NO",row) + "'";
        TParm  result = new TParm(TJDODBTool.getInstance().update(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		this.messageBox("P0005"); // ִ�гɹ�
		onSplitNew();
	}
	/**
	 * �����������������
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = newTable.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = newTable.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}
	/**
	 * vectoryת��param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		newTable.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}
	/**
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ת��parm�е���
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

}
