package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.ins.INSNoticeTool;
import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.reg.RegMethodTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SysFee;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.javahis.util.DateUtil;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: סԺʵʱ�ϴ�
 * </p>
 */
public class INSBatchControl extends TControl {
	TParm data;
	static SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
    //ҽ��ҽԺ����
    private String nhi_hosp_code;
    //ҽԺ����
    private String nhi_hosp_desc;

	public void onInit() {
		super.onInit();
		((TTable) getComponent("Table")).addEventListener("Table->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		this.setValue("UPLOAD_DATE", DateUtil.getNowTime("yyyy/MM/dd"));
		TParm hospParm = INSUpLoadTool.getInstance().getNhiHospCode(Operator.
                getRegion());
        this.nhi_hosp_code = hospParm.getValue("NHI_NO", 0);
        this.nhi_hosp_desc = hospParm.getValue("REGION_CHN_DESC", 0);
		onClear();

	}

	/**
	 * ���Ӷ�Table�ļ��� delete
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked() {
		int row = (Integer) callFunction("UI|Table|getClickedRow");
		if (row < 0)
			return;
		TTable table3 = (TTable) callFunction("UI|Table|getThis");
		data = table3.getParmValue();
	}
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryTParm = new TParm();
		TParm result = new TParm();
		 if ("".equals(this.getValue("MR_NO"))) {
	            messageBox("�����Ų���Ϊ��");
	            return;
	        }
		 
		 // modify by huangtt 20160930 EMPI���߲�����ʾ start
		 String mrNo = PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
	        Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));
	        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				mrNo = pat.getMrNo();
			}
	     // modify by huangtt 20160930 EMPI���߲�����ʾ end
		 
		 setValue("MR_NO", mrNo);
		queryTParm.setData("MR_NO", this.getValue("MR_NO"));		
		String sql =
		  " SELECT C.MR_NO,A.PAT_NAME,A.CASE_NO,A.ADM_SEQ,A.NHIHOSP_NO,A.IN_DATE,"+
	      " A.DOWN_DATE,A.PERSONAL_NO,A.HIS_CTZ_CODE,D.INS_DEPT_CODE,A.DS_DATE"+
	      " FROM INS_ADM_CONFIRM A,ADM_INP C,SYS_DEPT D"+
	      " WHERE A.CASE_NO = C.CASE_NO"+
	      " AND D.DEPT_CODE = C.IN_DEPT_CODE"+
	      " AND C.MR_NO = '"+ queryTParm.getData("MR_NO")+ "'"+
	      " GROUP BY C.MR_NO,A.PAT_NAME, A.CASE_NO,A.ADM_SEQ,A.NHIHOSP_NO,A.IN_DATE,A.DOWN_DATE,"+
	      " A.PERSONAL_NO,A.HIS_CTZ_CODE,D.INS_DEPT_CODE,A.DS_DATE";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		// �жϴ���ֵ
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			return;
		}
		((TTable) getComponent("Table")).setParmValue(result);
	}

	/**
	 * �ϴ�
	 */
	public void onUpload() {
		String bilDate = StringTool.getString(TCM_Transform.getTimestamp(getValue(
	     "UPLOAD_DATE")), "yyyyMMdd"); //�õ������ʱ��
		String flg ="1";//�ж��ӳٲ���ԭ���Ƿ��ϴ���ֵ
        String caseNo = data.getValue("CASE_NO",0);
        String hisctzCode = data.getValue("HIS_CTZ_CODE",0);
        String beginDate ="";
        String endDate ="";
        beginDate = bilDate + "000000";
        endDate = bilDate + "235959";
      //��ѯҽ������סԺ��ϸ
		TParm ParmMX = MXSQL(caseNo,beginDate,endDate);
//		System.out.println("ParmMX" + ParmMX);  
    	 if (ParmMX.getCount() <= 0) {
    		 messageBox("û�в���סԺ��ϸ");
    		 return ; 
    	 }
    	 //��ϲ�ѯ
			TParm ParmZD = ZDSQL(caseNo);
    	 if (ParmZD.getCount() <= 0) {
    		 messageBox("û�в������");
    		 return ; 
    	 }
    	//ҽ������ʵʱ�ϴ�
    	 TParm result = onUpload(ParmMX,hisctzCode,data,ParmZD,flg,beginDate); 
    	 if(result.getErrCode()<0){
    	 this.messageBox(result.getErrText());	 
    	 this.messageBox("�ϴ�ʧ��");
    		 return ; 	 
    	 }
	     this.messageBox("�ϴ��ɹ�");
	}
    /**
     * ҽ������ʵʱ�ϴ�
     */
    public TParm onUpload(TParm ParmMX,String hisctzCode,
    		TParm parm,TParm ParmZD,String flg,String beginDate) {
    	TParm result = new TParm();
     DecimalFormat df = new DecimalFormat("##########0.00");
   	 TParm actionParmMX = new TParm();
	 TParm actionParmZD = new TParm();
	       //ҽ������ʵʱ�ϴ���ϸ   	    	 
  	int count = ParmMX.getCount("ADM_SEQ");
   	double allamt =0.00;
        for(int j=0;j<count;j++){ 
        allamt+=ParmMX.getDouble("TOT_AMT",j);//�������ϼ�
    	 actionParmMX.addData("ADM_SEQ", ParmMX.getData("ADM_SEQ",j));//��ҽ˳���
    	 actionParmMX.addData("SEQ_NO", j+1);//���
    	 actionParmMX.addData("HOSP_NHI_NO", ParmMX.getData("NHIHOSP_NO",j));//ҽԺ����	 
    	 String billdate = StringTool.getString(ParmMX.getTimestamp("BILL_DATE",j), "yyyy-MM-dd HH:mm:ss");
    	 actionParmMX.addData("BILL_DATE", billdate);//���÷���ʱ��
    	 actionParmMX.addData("NHI_CODE", ParmMX.getData("SFXMBM",j));//��Ŀ�շ���Ŀ����
    	 actionParmMX.addData("NHI_DESC", ParmMX.getData("ORDER_DESC",j));//ҽԺ������Ŀ����
    	 actionParmMX.addData("DOSE_CODE", ParmMX.getData("JX",j));//����
    	 actionParmMX.addData("SPECIFICATION", ParmMX.getData("GG",j));//���
    	 actionParmMX.addData("PRICE", ParmMX.getData("OWN_PRICE",j));//����
    	 actionParmMX.addData("QTY", ParmMX.getData("DOSAGE_QTY",j));//����
    	 actionParmMX.addData("TOT_AMT", ParmMX.getData("TOT_AMT",j));//�������	    	 
   }
         if(hisctzCode.equals("11")||
         	    hisctzCode.equals("12")||
         	    hisctzCode.equals("13")){
         		 actionParmMX.addData("PARM_COUNT", 11);
         	     actionParmMX.setData("PIPELINE", "DataDown_zjks");
         	     actionParmMX.setData("PLOT_TYPE", "E");
         	    
         	 }else if(hisctzCode.equals("21")||
         	    	  hisctzCode.equals("22")||
         	    	  hisctzCode.equals("23")){
         		 actionParmMX.addData("PARM_COUNT", 11);
         	     actionParmMX.setData("PIPELINE", "DataDown_cjks");
         	     actionParmMX.setData("PLOT_TYPE", "E");   	    	 
         	 }
         	 result = InsManager.getInstance().safe(actionParmMX);
         	if (result.getErrCode() < 0) {
                return result;
            }
//             System.out.println("result" + result);    	
    //סԺÿ������ϴ�
         actionParmZD.addData("ADM_SEQ", parm.getData("ADM_SEQ",0));//��ҽ˳���
         actionParmZD.addData("HOSP_NHI_NO", parm.getData("NHIHOSP_NO",0));//ҽԺ����
         String date  =beginDate.substring(0, 4)+"-"+beginDate.substring(4, 6)
         +"-"+beginDate.substring(6, 8);
         actionParmZD.addData("BILL_DATE", date);//���÷���ʱ��
         actionParmZD.addData("OWN_NO", parm.getData("PERSONAL_NO",0));//���˱��
         actionParmZD.addData("DEPT_CODE", parm.getData("INS_DEPT_CODE",0));//סԺ����
         actionParmZD.addData("DR_NHI_CODE", ParmZD.getData("DR_QUALIFY_CODE",0));//ҽʦ����
         //���
  		String mainDiag = "";
  		//��ϱ���
  		String otherdiagecode = "";
  		int count1 = ParmZD.getCount("ICD_CHN_DESC");
  		 for(int m=0;m<count1;m++){
  			mainDiag +=ParmZD.getData("ICD_CHN_DESC",m)+",";
  			otherdiagecode +=ParmZD.getData("ICD_CODE",m)+"@";
  		 } 		
          actionParmZD.addData("DIAGE_CODE", mainDiag.length()>0? 
         		 mainDiag.substring(0, mainDiag.length() - 1):"");//�������
         actionParmZD.addData("SPE_REMARK", ParmZD.getData("DESCRIPTION",0));//�������
         actionParmZD.addData("TOT_AMT", df.format(allamt));//�������ϼ�  
         if(flg.equals("1"))
         actionParmZD.addData("DELAY", "�ʸ�ȷ�����ӳٿ���");//�ӳٲ���ԭ��
         else if (flg.equals("2"))
         actionParmZD.addData("DELAY", "");//�ӳٲ���ԭ��
         actionParmZD.addData("OTHER_DIAGE_CODE", otherdiagecode.length()>0? 
          		otherdiagecode.substring(0, otherdiagecode.length() - 1):"");//��ϱ���
         if(hisctzCode.equals("11")||
    	    hisctzCode.equals("12")||
    	    hisctzCode.equals("13")){
        	 actionParmZD.addData("PARM_COUNT", 11);
        	 actionParmZD.setData("PIPELINE", "DataDown_zjks");
        	 actionParmZD.setData("PLOT_TYPE", "F");
    	 }else if(hisctzCode.equals("21")||
    	    	  hisctzCode.equals("22")||
    	    	  hisctzCode.equals("23")){
    		 actionParmZD.addData("PARM_COUNT", 11);
    		 actionParmZD.setData("PIPELINE", "DataDown_cjks");
    		 actionParmZD.setData("PLOT_TYPE", "F");   	    	 
    	    	 }  
    	 result = InsManager.getInstance().safe(actionParmZD);  
//    	  System.out.println("result" + result);
    	 if (result.getErrCode() < 0) {
             return result;
         }
    	return result;
    }
    /**
     *��ϸSQL 
     */
    public TParm MXSQL(String caseNo,String beginDate,String endDate){
    	String sqlMX =
   		 " SELECT A.CASE_NO,B.ADM_SEQ,B.NHIHOSP_NO,A.BILL_DATE,D.SFXMBM,C.ORDER_DESC,"+
            " D.JX,D.GG,A.OWN_PRICE,A.DOSAGE_QTY,A.TOT_AMT"+
            " FROM IBS_ORDD A,INS_ADM_CONFIRM B,SYS_FEE_HISTORY C,INS_RULE D"+
   	     " WHERE A.CASE_NO = B.CASE_NO"+
   	     " AND A.CASE_NO = '"+caseNo+"'"+
   	     " AND A.ORDER_CODE =C.ORDER_CODE"+
   	     " AND C.NHI_CODE_I =D.SFXMBM"+
   	     " AND A.DOSAGE_QTY <>0"+
   	     " AND A.BILL_DATE  BETWEEN  TO_DATE('"+beginDate+"','YYYYMMDDHH24MISS')"+  
   	     " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
   	     " AND '"+beginDate+"' >= C.START_DATE"+ 
   	     " AND '"+endDate+"' < =C.END_DATE"+
   	     " AND A.BILL_DATE BETWEEN D.KSSJ AND D.JSSJ"+
   	     " ORDER BY A.CASE_NO";
      System.out.println("MXSQL" + sqlMX);   	
   	 TParm ParmMX = new TParm(TJDODBTool.getInstance().select(sqlMX));	
    	return ParmMX;    	
    }
    /**
     *���SQL 
     */
    public TParm ZDSQL(String caseNo){
    	String sqlZD =
   		 " SELECT A.ICD_CODE,B.ICD_CHN_DESC,C.DR_QUALIFY_CODE,A.DESCRIPTION"+
   		 " FROM ADM_INPDIAG A,SYS_DIAGNOSIS B, SYS_OPERATOR C"+
   		 " WHERE A.CASE_NO = '"+caseNo+"'"+
   		 " AND A.IO_TYPE = 'M'"+
   		 " AND A.ICD_CODE = B.ICD_CODE"+
   		 " AND A.OPT_USER = C.USER_ID";
   	 TParm ParmZD = new TParm(TJDODBTool.getInstance().select(sqlZD)); 
   	    return ParmZD;    	
    }
	/**
	 * ���
	 */
	public void onClear() {
		((TTable) getComponent("Table")).removeRowAll();
		this.setValue("MR_NO", "");
		this.setValue("UPLOAD_DATE", DateUtil.getNowTime("yyyy/MM/dd"));

	}	    	 
}
