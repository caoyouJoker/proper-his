package com.javahis.ui.ins;



import java.text.SimpleDateFormat;
import java.util.Date;
import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TypeTool;
/**
 * <p>
 * Title:��������벿ƽ̨��������
 * Description:��������벿ƽ̨��������
 * Copyright: Copyright (c) 2017
 * @version 1.0
 */
public class INSPlatformAccountControl extends TControl{
	SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	TTable accountTable;//������Ϣ
	TTable downloadTable;//������Ϣ
	TParm regionParm;// ҽ���������
	TTabbedPane tabbedPane;// ҳǩ
	/**
     * ��ʼ������
     */
    public void onInit() {
    	accountTable = (TTable) this.getComponent("ACCOUNT_TABLE");//������Ϣ
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE");// ҳǩ
		downloadTable = (TTable) this.getComponent("DOWNLOAD_TABLE");//������Ϣ
		setValue("START_DATE", SystemTool.getInstance().getDate());
 	    setValue("END_DATE", SystemTool.getInstance().getDate());	 
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
		//�����¼�
		callFunction("UI|ACCOUNT_TABLE|addEventListener", "ACCOUNT_TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
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
		if(tabbedPane.getSelectedIndex()==0){
		if (null == this.getValue("MR_NO")
				|| this.getValue("MR_NO").toString().length() <= 0) {
			onCheck("MR_NO", "�����Ų�����Ϊ��");
			return;
		}
		String mrNo = getValueString("MR_NO");
		String SQL = " SELECT A.MR_NO,A.PAT_NAME,CASE B.SEX_CODE  WHEN '1' THEN '��'"+
		             " WHEN '2' THEN 'Ů'ELSE '' END AS SEX_DESC,A.IDNO,A.ADM_SEQ,A.UPLOAD_DATE,"+
		             " (A.PHA_AMT+A.EXM_AMT+A.TREAT_AMT+A.OP_AMT+A.BED_AMT+A.MATERIAL_AMT+"+
		             "  A.OTHER_AMT+A.BLOODALL_AMT+A.BLOOD_AMT ) AS TOTAL_AMT,"+
		             " (A.NHI_PAY+A.ARMYAI_AMT+A.ACCOUNT_PAY_AMT) AS INS_AMT,"+
		             " CASE A.CHECK_FLG  WHEN '0' THEN 'δ�������'"+
		             " WHEN '2' THEN '���벿ƽ̨�����ϴ��ɹ�'"+
		             " WHEN '3' THEN '��ƽ̨���˳ɹ�' END AS CHECK_FLG,A.CASE_NO"+
		             " FROM INS_IBS A, INS_ADM_CONFIRM B"+
		             " WHERE A.CASE_NO = B.CASE_NO"+
		             " AND A.MR_NO = '"+ mrNo + "'"+
		             " AND A.INS_CROWD_TYPE = '3'"+
		             " AND B.IN_STATUS = '2'";	
//		System.out.println("SQL=====:"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));	
//		System.out.println("result=====:"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");// ִ��ʧ��
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			accountTable.removeRowAll();
			return;
		}
		accountTable.setParmValue(result);
		}
		else{			
		// ��ѯ��������	
			if (null == this.getValue("START_DATE")
					|| this.getValue("START_DATE").toString().length() <= 0) {
				onCheck("START_DATE", "���㿪ʼʱ�䲻����Ϊ��");
				return;
			}
			if (null == this.getValue("END_DATE")
					|| this.getValue("END_DATE").toString().length() <= 0) {
				onCheck("END_DATE", "�������ʱ�䲻����Ϊ��");
				return;
			}			
		TParm parm = new TParm();		
		parm.setData("ADM_SEQ_FLG",this.getValue("ADM_SEQ"));
		parm.setData("START_DATE",sdf.format(this.getValue("START_DATE")));
		parm.setData("END_DATE",sdf.format(this.getValue("END_DATE")));
		onReQuery(parm);		
		}
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
	 * �������
	 * @param order
	 */
	public void onTableClicked(int row){
		TTable table1 = (TTable) this.getComponent("ACCOUNT_TABLE");
		TParm Parm = table1.getParmValue();
		//��ֵ����
		setValue("ADM_SEQ", Parm.getValue("ADM_SEQ", row));
		setValue("START_DATE",Parm.getTimestamp("UPLOAD_DATE", row));
		setValue("END_DATE",Parm.getTimestamp("UPLOAD_DATE", row));
		//�����������
		String startdate = sdf.format(Parm.getValue("UPLOAD_DATE", row));
		String enddate = sdf.format(Parm.getValue("UPLOAD_DATE", row));
//		System.out.println("startdate=====:"+startdate);		
	   	String sql = " SELECT MR_NO,PAT_NAME,SEX_DESC,IDNO,ADM_SEQ,"+
	   	    " UPLOAD_DATE,TOTAL_AMT,INS_AMT,"+
	   	    " CASE CHECK_FLG  WHEN '0' THEN 'δ�������'"+
            " WHEN '2' THEN '���벿ƽ̨�����ϴ��ɹ�'"+
            " WHEN '3' THEN '��ƽ̨���˳ɹ�' END AS CHECK_FLG"+
	   	    " FROM INS_YD_DOWNLOAD"+
	        " WHERE UPLOAD_DATE BETWEEN TO_DATE('"+ startdate+"000000"+"','YYYYMMDDHH24MISS')"+  
	        " AND TO_DATE('"+ enddate+"235959"+"','YYYYMMDDHH24MISS')"+
	        " AND ADM_SEQ = '"+ Parm.getValue("ADM_SEQ", row) + "'"; 
	     	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
	  		if(data.getErrCode()<0){
	  		    this.messageBox(data.getErrText());
	  			return;
	  		}
	  		downloadTable.setParmValue(data);

	}	
	/**
	 * ����
	 */
	public void onAccount(){
		TParm parm = getTableSeleted();	
		if (null == parm) {
			return;
		}	
//		   System.out.println("parm============"+parm);
		   TParm accountParm = new TParm();
		   TParm result = new TParm();		  
		   accountParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ"));//��ҽ��ˮ��
		   accountParm.addData("HOSP_NHI_NO",regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
		   accountParm.addData("TOTAL_AMT",parm.getDouble("TOTAL_AMT"));//�����ܶ�
		   accountParm.addData("INS_AMT",parm.getDouble("INS_AMT"));//ҽ�ƻ���֧���ܶ�
		   accountParm.addData("USER_NAME",Operator.getID());//ҽԺ����Ա
		   accountParm.addData("PARM_COUNT", 5);//�������       	   	   
		   accountParm.setData("PIPELINE", "DataDown_yjks");
		   accountParm.setData("PLOT_TYPE", "A2");	  	    
//	       System.out.println("accountParm:"+accountParm);
	       result = InsManager.getInstance().safe(accountParm);
	       if (result.getErrCode() < 0) {	        	
	     	    this.messageBox(result.getErrText());
				return;
	       }else{
	    	  //���¶���״̬��ʱ��(INS_IBS) 
	  		 String sql1 = " UPDATE INS_IBS " +
	               " SET CHECK_FLG = '2'," +
	               " CHECK_DATE = SYSDATE" +
	               " WHERE CASE_NO = '" + parm.getData("CASE_NO") + "'" +
	               " AND ADM_SEQ= '" + parm.getData("ADM_SEQ") + "'";
	  		 result = new TParm(TJDODBTool.getInstance().update(sql1));	
	  		 if (result.getErrCode() < 0) {
	               this.messageBox(result.getErrText());
	               return;
	           }	    	   
	    	   messageBox("���˳ɹ�"); 
	       }
	       onQuery();
	}
	
	/**
	 * ����
	 */
	public void onDownload(){
	if (!getAccount()) {
		return;
	    }
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "���㿪ʼʱ�䲻����Ϊ��");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "�������ʱ�䲻����Ϊ��");
			return;
		}		
	   TParm downParm = new TParm();
	   TParm result = new TParm();
	   String admSeq = "";
	   String startdate = sdf.format(this.getValue("START_DATE"));
	   String enddate = sdf.format(this.getValue("END_DATE"));
	   if(this.getValue("ADM_SEQ").toString().length()>0)
		   admSeq = getValueString("ADM_SEQ");
	   downParm.addData("HOSP_NHI_NO",regionParm.getData("NHI_NO", 0).toString());//ҽԺ����
	   downParm.addData("ADM_SEQ", admSeq);//��ҽ��ˮ��
	   downParm.addData("START_DATE", startdate);//���㿪ʼʱ��
	   downParm.addData("END_DATE", enddate);//�������ʱ��
	   downParm.addData("PARM_COUNT", 4);//�������       	   	   
	   downParm.setData("PIPELINE", "DataDown_yjkd");
	   downParm.setData("PLOT_TYPE", "A2");
//       System.out.println("downParm:"+downParm);
       result = InsManager.getInstance().safe(downParm,"");
//       System.out.println("result:============"+result);
       if (result.getErrCode() < 0) {	        	
     	    this.messageBox(result.getErrText());
			return;
       }else{
    	//�ڲ���INS_YD_DOWNLOAD��֮ǰ��ѯ�Ƿ�������������
    	 String sql1 = "";
    	 if(this.getValue("ADM_SEQ").toString().length()>0)
    		 sql1 = " AND ADM_SEQ = '"+ admSeq + "'"; 
    	 
    	String sql = " SELECT * FROM INS_YD_DOWNLOAD"+
    	             " WHERE UPLOAD_DATE BETWEEN TO_DATE('"+ startdate+"000000"+"','YYYYMMDDHH24MISS')"+  
        	         " AND TO_DATE('"+ enddate+"235959"+"','YYYYMMDDHH24MISS')"+
    	             sql1;
    	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
//    	 System.out.println("data:============count"+data.getCount());
   		if(data.getErrCode()<0){
   		    this.messageBox(data.getErrText());
   			return;
   		}
   		//�ɷ��ظ�����
   		if(data.getCount()<=0){
   			result.setData("ADM_SEQ_FLG", admSeq);//�ж��Ƿ���һ������
   			result.setData("OPT_USER", Operator.getID());
   			result.setData("OPT_TERM", Operator.getIP());
   			result.setData("START_DATE", startdate);
   			result.setData("END_DATE", enddate);
	        this.insertyddownload(result);
	        messageBox("���سɹ�");
   		}else{
   		   //ɾ��������
   			String sqldel = " DELETE FROM INS_YD_DOWNLOAD"+
             " WHERE UPLOAD_DATE BETWEEN TO_DATE('"+ startdate+"000000"+"','YYYYMMDDHH24MISS')"+  
	           " AND TO_DATE('"+ enddate+"235959"+"','YYYYMMDDHH24MISS')"+
             sql1;
            TParm datadel = new TParm(TJDODBTool.getInstance().update(sqldel));
            if(datadel.getErrCode()<0){
       		    this.messageBox(datadel.getErrText());
       			return;
       		}
   			result.setData("ADM_SEQ_FLG", admSeq);//�ж��Ƿ���һ������
   			result.setData("OPT_USER", Operator.getID());
   			result.setData("OPT_TERM", Operator.getIP());
   			result.setData("START_DATE", startdate);
   			result.setData("END_DATE", enddate);
	        this.insertyddownload(result); 
	        messageBox("���سɹ�");   							
   		}	      
     }
	}
	/**
	 * ����INS_YD_DOWNLOAD
	 * @param parm
	 */
	public void insertyddownload(TParm parm){
//		 System.out.println("parm=====:"+parm);
		 TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ADM_SEQ"); i++){
			String sqldel = " SELECT A.MR_NO,A.CASE_NO,A.PAT_NAME," +
			" CASE B.SEX_CODE  WHEN '1' THEN '��'" +
			" WHEN '2' THEN 'Ů'ELSE '' END AS SEX_DESC," +
			" A.IDNO,A.ADM_SEQ,TO_CHAR(A.UPLOAD_DATE,'YYYYMMDD') AS UPLOAD_DATE" +
			" FROM INS_IBS A, INS_ADM_CONFIRM B" +
			" WHERE A.CASE_NO = B.CASE_NO" +
			" AND A.ADM_SEQ = '" + parm.getValue("ADM_SEQ",i) + "'" +
			" AND A.INS_CROWD_TYPE = '3'" +
			" AND B.IN_STATUS = '2'";
            TParm datadel = new TParm(TJDODBTool.getInstance().select(sqldel));
    		String	sql = " INSERT INTO INS_YD_DOWNLOAD(MR_NO,CASE_NO," +
            " PAT_NAME,SEX_DESC,IDNO,ADM_SEQ,UPLOAD_DATE,TOTAL_AMT," +
            " INS_AMT,CHECK_FLG,OPT_USER,OPT_DATE,OPT_TERM)" +
            " VALUES('"+ datadel.getValue("MR_NO", 0)+ "', " + 
            "'" + datadel.getValue("CASE_NO", 0) + "', " +
            "'" + datadel.getValue("PAT_NAME", 0) + "', " +
            "'" + datadel.getValue("SEX_DESC", 0) + "', " +
            "'" + datadel.getValue("IDNO", 0) + "', " +
            "'" + datadel.getValue("ADM_SEQ", 0) + "', " +
            " TO_DATE('" + datadel.getValue("UPLOAD_DATE",0) + "','YYYYMMDDHH24MISS'), " +
            " " + parm.getDouble("TOTAL_AMT",i)+ "," +
            " " + parm.getDouble("INS_AMT",i)+ "," +
            "'" + parm.getValue("CHECK_FLG",i) + "',"+
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
			//����INS_IBS�Ķ���״̬
	  		 String sql1 = " UPDATE INS_IBS " +
             " SET CHECK_FLG = '3'," +
             " CHECK_DATE = SYSDATE" +
             " WHERE CASE_NO = '" + datadel.getValue("CASE_NO", 0) + "'" +
             " AND ADM_SEQ= '" + datadel.getValue("ADM_SEQ", 0) + "'";
		 result = new TParm(TJDODBTool.getInstance().update(sql1));	
		 if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return;
         }
		}
		onReQuery(parm);
		
		
	}
	/**
	 * �ж��Ƿ��������
	 * @return
	 */
	private boolean getAccount(){
		Date date =new Date();
		String time=date.getHours()+""+date.getMinutes();
		//StringTool.getDate(, "HHmm");
		if (time.compareTo("1330")<0) {
			this.messageBox("������1��30���Ժ�������ز���");
			return false;
		}
		return true;
	}
	/**
	 * ��ѯ����
	 */
	public void onReQuery(TParm parm) {	
		String sql1 = "";
		 String admSeq = parm.getValue("ADM_SEQ_FLG");
		 String startdate = parm.getValue("START_DATE");
		 String enddate = parm.getValue("END_DATE");
		if(admSeq.length()>0)
   		 sql1 = " AND ADM_SEQ = '"+ admSeq + "'";   	 
   	    String sql = " SELECT MR_NO,PAT_NAME,SEX_DESC,IDNO,ADM_SEQ,"+
   	    " UPLOAD_DATE,TOTAL_AMT,INS_AMT," +
   	    " CASE CHECK_FLG  WHEN '0' THEN 'δ�������'"+
        " WHEN '2' THEN '���벿ƽ̨�����ϴ��ɹ�'"+
        " WHEN '3' THEN '��ƽ̨���˳ɹ�' END AS CHECK_FLG"+
   	    " FROM INS_YD_DOWNLOAD"+
        " WHERE UPLOAD_DATE BETWEEN TO_DATE('"+ startdate+"000000"+"','YYYYMMDDHH24MISS')"+  
        " AND TO_DATE('"+ enddate+"235959"+"','YYYYMMDDHH24MISS')"+
        sql1;
     	TParm data = new TParm(TJDODBTool.getInstance().select(sql));
  		if(data.getErrCode()<0){
  		    this.messageBox(data.getErrText());
  			return;
  		}
  		downloadTable.setParmValue(data);
	}

	/**
	 * У���Ƿ��л�ý���
	 * 
	 * @return TParm
	 */
	private TParm getTableSeleted() {
		int row = accountTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = accountTable.getParmValue().getRow(row);	
		return parm;
	}
	/**
	 * ���
	 */
	public void onClear(){
		this.setValue("MR_NO", "");
		this.setValue("ADM_SEQ", "");
		accountTable.removeRowAll();
		downloadTable.removeRowAll();
		tabbedPane.setSelectedIndex(0); // ��һ��ҳǩ
	}


}
