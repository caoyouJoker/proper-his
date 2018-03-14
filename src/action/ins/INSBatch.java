package action.ins;

import com.dongyang.patch.Patch;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;

import java.text.DecimalFormat;
import java.util.Date;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;

import jdo.ins.InsManager;
import jdo.inw.InwStationMaintainTool;
import java.util.Vector;
import jdo.sys.SystemTool;

/**
 * <p>Title: סԺ����ʵʱ�ϴ���ϸ</p>
 *
 * <p>Description:  סԺ����ʵʱ�ϴ���ϸ</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * @author yufh 2013.06.03
 * @version 1.0
 */
public class INSBatch extends Patch {
    public INSBatch() {
    }

    /**
     * �����߳�
     * @return boolean
     */
    public boolean run() {
        TConnection connection = TDBPoolManager.getInstance().getConnection();
        Timestamp sysDate = SystemTool.getInstance().getDate();
//        System.out.println("sysDate=========="+sysDate);
        Timestamp bilDate = StringTool.rollDate(sysDate, -1);
	    String sql =
	    	" SELECT A.CASE_NO,A.ADM_SEQ,A.NHIHOSP_NO,A.IN_DATE," +
	        " A.DOWN_DATE,A.PERSONAL_NO,A.HIS_CTZ_CODE,D.INS_DEPT_CODE"+
	        " FROM INS_ADM_CONFIRM A,ADM_INP C,SYS_DEPT D"+
	        " WHERE A.CASE_NO = C.CASE_NO"+
	        " AND D.DEPT_CODE = C.IN_DEPT_CODE"+
            " AND A.DOWN_DATE IS NOT NULL"+
	        " AND A.IN_STATUS IN('0','7')"+
	        " GROUP BY A.CASE_NO,A.ADM_SEQ,A.NHIHOSP_NO,A.IN_DATE,A.DOWN_DATE," +
	        " A.PERSONAL_NO,A.HIS_CTZ_CODE,D.INS_DEPT_CODE";
        //��ѯҽ������
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("parm===query"+parm);	
        parm.setData("DATE", sysDate);
        parm.setData("BILDATE", bilDate);
        // �Ƿ����ҽ������
        if (parm.getCount() <= 0) {
            connection.close();
            return false;
        }
        for (int i = 0; i < parm.getCount("CASE_NO"); i++) {
//        	System.out.println("case_no======"+i);	
            if (insertSingleData(parm, i, connection)) {
                connection.commit();
            }
            connection.commit();
            continue;
        }
        connection.commit();
        connection.close();
        return true;
    }

    /**
     * ѭ��
     * @param parm TParm
     * @param row int
     * @param connection TConnection
     * @return boolean
     */
    public boolean insertSingleData(TParm parm, int row, TConnection connection) {
        TParm result = new TParm();
        TParm batchParm = new TParm();
        TParm selMaxSeqParm = new TParm();
        TParm selMaxSeq = new TParm();
        TParm batchLogParm = new TParm();
        String caseNo = parm.getValue("CASE_NO", row);
        String hisctzCode = parm.getValue("HIS_CTZ_CODE", row);
        Timestamp inDate = StringTool.getTimestamp(StringTool.getString(
        		parm.getTimestamp("IN_DATE", row), "yyyy-MM-dd"),"yyyy-MM-dd");
        Timestamp downDate =  StringTool.getTimestamp(StringTool.getString(
        		parm.getTimestamp("DOWN_DATE", row), "yyyy-MM-dd"),"yyyy-MM-dd");
        Timestamp bilDate = parm.getTimestamp("BILDATE");
        Timestamp sysDate = StringTool.getTimestamp(StringTool.getString(
        		parm.getTimestamp("DATE"), "yyyy-MM-dd"),"yyyy-MM-dd");
        String beginDate ="";
        String endDate ="";
        beginDate = StringTool.getString(bilDate,"yyyyMMdd") + "000000";
        endDate = StringTool.getString(bilDate,"yyyyMMdd") + "235959";
//    	System.out.println("hisctzCode=========="+hisctzCode);
//    	System.out.println("inDate=========="+inDate);
//    	System.out.println("downDate=========="+downDate);
//    	System.out.println("bilDate=========="+bilDate);
    	//�ʸ�ȷ���鿪����ļ����ϴ���ʽ
    	int Date1 =0;
    	String flg ="";//�ж��ӳٲ���ԭ���Ƿ��ϴ���ֵ
    	Date1 = StringTool.getDateDiffer(sysDate, downDate);
    	//ϵͳʱ���뿪��ʱ�����1��
    	if(Date1==1){    		
        //�ж��ʸ�ȷ�����Ƿ񳬹�3��
    	  int Date2	= 0;
    	  Date2 = StringTool.getDateDiffer(downDate,inDate)+1;
    	  if(Date2>=2){
    		  Date2 = 2;
    		  flg ="1";  
    	  }
    	  else
    		  flg ="2";   
    		for(int i=0;i<Date2;i++){
    			beginDate =	 StringTool.getString(
    					     StringTool.rollDate(downDate, -i),"yyyyMMdd") + "000000";
    			endDate =  StringTool.getString(
					       StringTool.rollDate(downDate, -i),"yyyyMMdd") + "235959";
    			//��ѯҽ������סԺ��ϸ
    			TParm ParmMX = MXSQL(caseNo,beginDate,endDate);
    	    	 if (ParmMX.getCount() <= 0) {
    	    		 return false; 
    	    	 }
    	    	 //��ϲ�ѯ
     			TParm ParmZD = ZDSQL(caseNo);
    	    	 if (ParmZD.getCount() <= 0) {
    	    		 return false; 
    	    	 }
    	    	//ҽ������ʵʱ�ϴ�
    	    	 result=onUpload(ParmMX,hisctzCode,parm,row,ParmZD,flg,beginDate);    	         
    		}   		       	    
    	}else{
	    		flg ="2";
	    		//��ѯҽ������סԺ��ϸ
	    		TParm ParmMX = MXSQL(caseNo,beginDate,endDate);
	       	     if (ParmMX.getCount() <= 0) {
	       		     return false; 
	       	 }
	       	    //��ϲ�ѯ
	            TParm ParmZD = ZDSQL(caseNo);
	       	     if (ParmZD.getCount() <= 0) {
	       		     return false; 
	       	 }
       	      //ҽ������ʵʱ�ϴ�
	       	  result=onUpload(ParmMX,hisctzCode,parm,row,ParmZD,flg,beginDate);       	 
   } 
//    	System.out.println("result=========="+result);
    	if (result.getErrCode() < 0) {
            //д��������־��(ʧ��)
        	String postDate = StringTool.getString(parm.getTimestamp("DATE"),
    	    "yyyyMMdd") + "235959";
            //��ѯ���������־�����
    	    selMaxSeq.setData("POST_DATE",postDate);
    	    selMaxSeq.setData("SYSTEM_CODE","INS");
            selMaxSeqParm = SystemTool.getInstance().selMaxBatchSeq(selMaxSeq);
            if (selMaxSeqParm.getErrCode() < 0) {
                return false;
            }
            int maxSeq = selMaxSeqParm.getInt("SEQ", 0);
            maxSeq = maxSeq + 1;
            batchLogParm.setData("POST_DATE", postDate);
            batchLogParm.setData("SYSTEM_CODE", "INS");
            batchLogParm.setData("SEQ", maxSeq);
            batchLogParm.setData("CASE_NO", caseNo);
            batchLogParm.setData("MR_NO", "");
            batchLogParm.setData("IPD_NO", "");
            batchLogParm.setData("DEPT_CODE", "");
            batchLogParm.setData("STATION_CODE", "");
            batchLogParm.setData("OPT_USER", "INS_BATCH");
            batchLogParm.setData("OPT_TERM", "127.0.0.1");
        	batchLogParm.setData("STATUS", "0");
            batchParm = SystemTool.getInstance().insertBatchLog(batchLogParm,
                    connection);
            if (batchParm.getErrCode() < 0) {
                return false;
            }
            connection.commit();
            return false;
        }
        return true;
    }
    /**
     * ҽ������ʵʱ�ϴ�
     */
    public TParm onUpload(TParm ParmMX,String hisctzCode,
    		TParm parm,int row,TParm ParmZD,String flg,String beginDate) {
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
//             System.out.println("result" + result);    	
    //סԺÿ������ϴ�
         actionParmZD.addData("ADM_SEQ", parm.getData("ADM_SEQ",row));//��ҽ˳���
         actionParmZD.addData("HOSP_NHI_NO", parm.getData("NHIHOSP_NO",row));//ҽԺ����
         String date  =beginDate.substring(0, 4)+"-"+beginDate.substring(4, 6)
         +"-"+beginDate.substring(6, 8);
         actionParmZD.addData("BILL_DATE", date);//���÷���ʱ��
         actionParmZD.addData("OWN_NO", parm.getData("PERSONAL_NO",row));//���˱��
         actionParmZD.addData("DEPT_CODE", parm.getData("INS_DEPT_CODE",row));//סԺ����
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
   	     " AND B.IN_STATUS IN ('0','7')"+
   	     " AND A.DOSAGE_QTY <>0"+
   	     " AND A.BILL_DATE  BETWEEN  TO_DATE('"+beginDate+"','YYYYMMDDHH24MISS')"+  
   	     " AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+ 
   	     " AND '"+beginDate+"' >= C.START_DATE"+ 
   	     " AND '"+endDate+"' < =C.END_DATE"+
   	     " AND A.BILL_DATE BETWEEN D.KSSJ AND D.JSSJ"+
   	     " ORDER BY A.CASE_NO";
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
}
