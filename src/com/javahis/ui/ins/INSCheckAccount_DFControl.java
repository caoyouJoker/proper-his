package com.javahis.ui.ins;

import jdo.ins.InsManager;
import jdo.sys.Operator;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
/**
 * <p>Title: ҽ���渶����</p>
 * <p>Description: ҽ���渶����</p>
 * @version 1.0
 */
public class INSCheckAccount_DFControl extends TControl{
	TParm regionParm;//ҽ���������
	TTable localTable;//��������
	TTable centerTable;//��������
	/**
     * ��ʼ������
     */
    public void onInit() {
    	setValue("UPLOAD_DATE", SystemTool.getInstance().getDate());
    	regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// ���ҽ���������
    	localTable = (TTable) this.getComponent("TABLE1");//��������
    	centerTable = (TTable) this.getComponent("TABLE2");//��������
    }
    /**
     * ��ѯ
     */
    public void onQuery(){
    	//���ݼ��
    	if(checkdata())
		    return;  
    	 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd")+"000000"; //��ʼ����
    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd")+"235959"; //��������
    	String sql =
    		" SELECT CONFIRM_NO,PAT_NAME,ID_NO,TOTAL_AMT,UPLOAD_DATE," +
    		" CASE STATUS_FLG  WHEN '1' THEN '������' WHEN '2' THEN '���ϴ�' " +
    		" WHEN '3' THEN '�ѳ���' WHEN '4' THEN '�Ѷ���' ELSE '' END AS STATUS_FLG "+
    		" FROM INS_ADVANCE_PAYMENT " +
    		" WHERE UPLOAD_DATE BETWEEN TO_DATE('"+startdate+"','YYYYMMDDhh24miss') " +
    		" AND TO_DATE('"+enddate+"','YYYYMMDDhh24miss') " +
    		" AND STATUS_FLG IN('2','4')";
//    	System.out.println("sql=======" + sql);
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getErrCode()<0){
    		messageBox(result.getErrText());
    		return;
    	}
    	if(result.getCount()<0){
    		messageBox("��������");
    	}
    	localTable.setParmValue(result);
    }
    /**
	 * ���ݼ��
	 */
	private boolean checkdata(){
	   	if(this.getValue("UPLOAD_DATE").equals("")){
    		this.messageBox("�������ڲ���Ϊ��");
    		return true;
    	}
	    return false; 
	} 
    
    
    /**
     * ������
     */
    public void onCheckAll(){
    	//���ݼ��
    	if(checkdata())
		    return;
    	TParm localParm = localTable.getParmValue();
//    	System.out.println("localParm=======" + localParm);
    	if(localParm==null){
    		 messageBox("���Ȳ�ѯ����");
    		 return;
    	} 
    	int count = localParm.getCount();
//    	System.out.println("count=======" + count);
    	double totalAmt = 0.00;
    	int allTime = count;
    	for (int i = 0; i < count; i++) {
			totalAmt += localParm.getDouble("TOTAL_AMT",i);
    	}
//    	System.out.println("totalAmt=======" +  StringTool.round(totalAmt, 2));		
//		System.out.println("allTime=======" + allTime);
    	 String uploadDate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd"); //��������  
    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//��ȡHOSP_NHI_NO
    	TParm parm = new TParm();
    	parm.addData("HOSP_NHI_NO", hospital);//ҽԺ����
		parm.addData("DATE", uploadDate);//��������
		parm.addData("TOTAL_AMT", StringTool.round(totalAmt, 2));//�������
		parm.addData("ALL_TIME", allTime);//���˴�
		parm.addData("PARM_COUNT", 4);//�������
		parm.setData("PIPELINE", "DataDown_czys");
		parm.setData("PLOT_TYPE", "S");	
		TParm result = InsManager.getInstance().safe(parm);
//		System.out.println("result=======" + result);	
		 if (result.getErrCode() < 0) {	        	
	     	    this.messageBox(result.getErrText());
				return;
		 }else{
//		  System.out.println("TOTAL_AMT=======" + result.getDouble("TOTAL_AMT"));
//		  System.out.println("ALL_TIME=======" + result.getInt("ALL_TIME"));	 
		 if(StringTool.round(totalAmt, 2)==result.getDouble("TOTAL_AMT")&&
			allTime==result.getInt("ALL_TIME")){
			 String startdate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	         "UPLOAD_DATE")), "yyyyMMdd")+"000000"; //��ʼ����
	    	 String enddate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	         "UPLOAD_DATE")), "yyyyMMdd")+"235959"; //��������
			//����INS_ADVANCE_PAYMENT��״̬ 4 �Ѷ���
			 String sql1 = " UPDATE INS_ADVANCE_PAYMENT " +
             " SET STATUS_FLG = '4'" +          
             " WHERE UPLOAD_DATE BETWEEN TO_DATE('"+startdate+"','YYYYMMDDhh24miss') " +
     		 " AND TO_DATE('"+enddate+"','YYYYMMDDhh24miss')";
		 result = new TParm(TJDODBTool.getInstance().update(sql1));	
		 if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return;
         } 		 
		   messageBox("���˳ɹ�");
		   onQuery();
		 }		 
		 else if(StringTool.round(totalAmt, 2)!=result.getDouble("TOTAL_AMT")||
					allTime!=result.getInt("ALL_TIME"))
		   messageBox("���������˴�������,�ɶ���ϸ��"); 
			 
		 }
		
		
		
    }
    /**
     * ����ϸ��
     */
    public void onCheckDetailAccnt(){
    	//���ݼ��
    	if(checkdata())
		    return;
    	TParm localParm = localTable.getParmValue();
//    	System.out.println("localParm=======" + localParm);
    	if(localParm==null){
    		 messageBox("���Ȳ�ѯ����");
    		 return;
    	} 
    	TParm parm = new TParm();   
    	 String uploadDate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
         "UPLOAD_DATE")), "yyyyMMdd"); //��������  
    	String hospital =  regionParm.getData("NHI_NO", 0).toString();//��ȡHOSP_NHI_NO
		parm.addData("HOSP_NHI_NO", hospital);//ҽԺ����
		parm.addData("DATE", uploadDate);//��������
		parm.addData("PARM_COUNT", 2);
	  	parm.setData("PIPELINE", "DataDown_czyd");
		parm.setData("PLOT_TYPE", "N");
		TParm result = InsManager.getInstance().safe(parm);	
//		System.out.println("result=======" + result);
		if(result.getErrCode()<0){
			messageBox(result.getErrText());
			return;
		}
		centerTable.setParmValue(result);
//		countDetail();
    }
    /**
     * ���
     */
    public void onclear(){
    	this.setValue("UPLOAD_DATE", SystemTool.getInstance().getDate());
    	localTable.removeRowAll();
    	centerTable.removeRowAll();
    }  
    
    
    /**
     * ������ϸ�˲��
     */
    public void countDetail(){
    	TTable table1 = (TTable)this.getComponent("TABLE1");//TABLE1
    	TTable table2 = (TTable)this.getComponent("TABLE2");//TABLE2
    	if(table1.getParmValue()==null||table2.getParmValue()==null){
    		messageBox("�������ݲ���Ϊ��");
    		return;
    	}
//    	ADM_SEQ;PAT_NAME;TOT_AMT;NHI_AMT;OWN_AMT;ADD_AMT;UPLOAD_DATE
//    	CONFIRM_NO;NAME;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;ADDPAY_AMT
    	TParm tableParm1 = table1.getParmValue();
    	TParm tableParm2 = table2.getParmValue();
    	TParm parm = new TParm();
    	for (int i = 0; i < table1.getRowCount(); i++) {
    	      String admSeqLocal = tableParm1.getData("ADM_SEQ", i).toString();
    	      boolean canfind = false;
    	      for(int j = 0;j < table2.getRowCount();j++){
    	        String admSeqCenter = tableParm2.getData("CONFIRM_NO", j).toString();
    	        if(!admSeqLocal.equals(admSeqCenter))
    	          continue;
    	        canfind = true;
    	        //���ؽ��
    	        double totAmtLocal = tableParm1.getDouble("TOT_AMT", i);//�������
    	        double nhiAmtLocal = tableParm1.getDouble("NHI_AMT", i);//�걨���
    	        double ownAmtLocal = tableParm1.getDouble("OWN_AMT", i);//ȫ�Էѽ��
    	        double addAmtLocal = tableParm1.getDouble("ADD_AMT", i);//�������
    	        //���Ķ˽��
    	        double totAmtCenter = tableParm2.getDouble("TOTAL_AMT", j);//�������
    	        double nhiAmtCenter = tableParm2.getDouble("TOTAL_NHI_AMT", j);//�걨���
    	        double ownAmtCenter = tableParm2.getDouble("OWN_AMT", j);//ȫ�Էѽ��
    	        double addAmtCenter = tableParm2.getDouble("ADDPAY_AMT", j);//�������
    	        if(totAmtLocal != totAmtCenter ||
    	                nhiAmtLocal != nhiAmtCenter ||
    	                ownAmtLocal != ownAmtCenter ||
    	                addAmtLocal != addAmtCenter ){
    	               parm.addData("STATUS_ONE", "Y");
    	               parm.addData("STATUS_TWO", "N");
    	               parm.addData("STATUS_THREE", "N");
    	               parm.addData("ADM_SEQ",tableParm1.getData("ADM_SEQ", i));
    	               parm.addData("NAME",tableParm1.getData("PAT_NAME", i));
    	               parm.addData("TOT_AMT_LOCAL",tableParm1.getData("TOT_AMT", i));
    	               parm.addData("TOT_AMT_CENTER",tableParm2.getData("TOTAL_AMT", j));
    	               parm.addData("NHI_AMT_LOCAL",tableParm1.getData("NHI_AMT", i));
    	               parm.addData("NHI_AMT_CENTER",tableParm2.getData("TOTAL_NHI_AMT", j));
    	               parm.addData("OWN_AMT_LOCAL",tableParm1.getData("OWN_AMT", i));
    	               parm.addData("OWN_AMT_CENTER",tableParm2.getData("OWN_AMT", j));
    	               parm.addData("ADD_AMT_LOCAL",tableParm1.getData("ADD_AMT", i));
    	               parm.addData("ADD_AMT_CENTER",tableParm2.getData("ADDPAY_AMT", j));
    	             }
    	      }
    	      if(!canfind){
    	          parm.addData("STATUS_ONE", "N");
    	          parm.addData("STATUS_TWO", "Y");
    	          parm.addData("STATUS_THREE", "N");
	              parm.addData("ADM_SEQ",tableParm1.getData("ADM_SEQ", i));
	              parm.addData("NAME",tableParm1.getData("PAT_NAME", i));
    	          parm.addData("TOT_AMT_LOCAL",tableParm1.getData("TOT_AMT", i));
    	          parm.addData("TOT_AMT_CENTER",0);
    	          parm.addData("NHI_AMT_LOCAL",tableParm1.getData("NHI_AMT", i));
    	          parm.addData("NHI_AMT_CENTER",0);
    	          parm.addData("OWN_AMT_LOCAL",tableParm1.getData("OWN_AMT", i));
    	          parm.addData("OWN_AMT_CENTER",0);
    	          parm.addData("ADD_AMT_LOCAL",tableParm1.getData("ADD_AMT", i));
    	          parm.addData("ADD_AMT_CENTER",0);
    	        }
		}
//    	ADM_SEQ;PAT_NAME;TOT_AMT;NHI_AMT;OWN_AMT;ADD_AMT;UPLOAD_DATE
//    	CONFIRM_NO;NAME;TOTAL_AMT;TOTAL_NHI_AMT;OWN_AMT;ADDPAY_AMT
    	for(int i = 0;i < table2.getRowCount();i++){
    	      String confirmNoCenter = tableParm2.getData("CONFIRM_NO", i).toString();
    	      boolean canfind = false;
    	      for (int j = 0; j < table1.getRowCount(); j++) {
    	        String confirmNoLocal = tableParm1.getData("ADM_SEQ", i).toString();
    	        if (!confirmNoLocal.equals(confirmNoCenter))
    	          continue;
    	        canfind = true;
    	      }
    	      if(!canfind){
    	        parm.addData("STATUS_ONE", "N");
    	        parm.addData("STATUS_TWO", "N");
    	        parm.addData("STATUS_THREE", "Y");
    	        parm.addData("ADM_SEQ",tableParm2.getData("CONFIRM_NO", i));
    	        parm.addData("NAME",tableParm2.getData("NAME", i));
    	        parm.addData("TOT_AMT_LOCAL",0);
    	        parm.addData("TOT_AMT_CENTER",tableParm2.getData("TOTAL_AMT", i));
    	        parm.addData("NHI_AMT_LOCAL",0);
    	        parm.addData("NHI_AMT_CENTER",tableParm2.getData("TOTAL_NHI_AMT", i));
    	        parm.addData("OWN_AMT_LOCAL",0);
    	        parm.addData("OWN_AMT_CENTER",tableParm2.getData("OWN_AMT", i));
    	        parm.addData("ADD_AMT_LOCAL",0);
    	        parm.addData("ADD_AMT_CENTER",tableParm2.getData("ADDPAY_AMT", i));
    	      }
    	    }
    	    if(parm.getCount("ADM_SEQ") <= 0){
    	    	messageBox("����ϸ�ʳɹ�");
    	    	return;
    	    }
    	    TParm reParm = (TParm)this.openDialog(
    	            "%ROOT%\\config\\ins\\INSCheckAccount_DFDetail.x", parm);
    }
}
