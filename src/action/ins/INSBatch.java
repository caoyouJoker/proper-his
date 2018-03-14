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
 * <p>Title: 住院病人实时上传明细</p>
 *
 * <p>Description:  住院病人实时上传明细</p>
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
     * 批次线程
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
        //查询医保病人
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
//		System.out.println("parm===query"+parm);	
        parm.setData("DATE", sysDate);
        parm.setData("BILDATE", bilDate);
        // 是否存在医保病人
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
     * 循环
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
    	//资格确认书开立后的几种上传方式
    	int Date1 =0;
    	String flg ="";//判断延迟补传原因是否上传空值
    	Date1 = StringTool.getDateDiffer(sysDate, downDate);
    	//系统时间与开立时间相差1天
    	if(Date1==1){    		
        //判断资格确认书是否超过3天
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
    			//查询医保病人住院明细
    			TParm ParmMX = MXSQL(caseNo,beginDate,endDate);
    	    	 if (ParmMX.getCount() <= 0) {
    	    		 return false; 
    	    	 }
    	    	 //诊断查询
     			TParm ParmZD = ZDSQL(caseNo);
    	    	 if (ParmZD.getCount() <= 0) {
    	    		 return false; 
    	    	 }
    	    	//医保病人实时上传
    	    	 result=onUpload(ParmMX,hisctzCode,parm,row,ParmZD,flg,beginDate);    	         
    		}   		       	    
    	}else{
	    		flg ="2";
	    		//查询医保病人住院明细
	    		TParm ParmMX = MXSQL(caseNo,beginDate,endDate);
	       	     if (ParmMX.getCount() <= 0) {
	       		     return false; 
	       	 }
	       	    //诊断查询
	            TParm ParmZD = ZDSQL(caseNo);
	       	     if (ParmZD.getCount() <= 0) {
	       		     return false; 
	       	 }
       	      //医保病人实时上传
	       	  result=onUpload(ParmMX,hisctzCode,parm,row,ParmZD,flg,beginDate);       	 
   } 
//    	System.out.println("result=========="+result);
    	if (result.getErrCode() < 0) {
            //写入批次日志档(失败)
        	String postDate = StringTool.getString(parm.getTimestamp("DATE"),
    	    "yyyyMMdd") + "235959";
            //查询最大批次日志档序号
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
     * 医保病人实时上传
     */
    public TParm onUpload(TParm ParmMX,String hisctzCode,
    		TParm parm,int row,TParm ParmZD,String flg,String beginDate) {
    	TParm result = new TParm();
     DecimalFormat df = new DecimalFormat("##########0.00");
   	 TParm actionParmMX = new TParm();
	 TParm actionParmZD = new TParm();
	       //医保病人实时上传明细   	    	 
    	int count = ParmMX.getCount("ADM_SEQ");
    	double allamt =0.00;
         for(int j=0;j<count;j++){ 
         allamt+=ParmMX.getDouble("TOT_AMT",j);//发生金额合计
    	 actionParmMX.addData("ADM_SEQ", ParmMX.getData("ADM_SEQ",j));//就医顺序号
    	 actionParmMX.addData("SEQ_NO", j+1);//序号
    	 actionParmMX.addData("HOSP_NHI_NO", ParmMX.getData("NHIHOSP_NO",j));//医院编码	 
    	 String billdate = StringTool.getString(ParmMX.getTimestamp("BILL_DATE",j), "yyyy-MM-dd HH:mm:ss");
    	 actionParmMX.addData("BILL_DATE", billdate);//费用发生时间
    	 actionParmMX.addData("NHI_CODE", ParmMX.getData("SFXMBM",j));//三目收费项目编码
    	 actionParmMX.addData("NHI_DESC", ParmMX.getData("ORDER_DESC",j));//医院服务项目名称
    	 actionParmMX.addData("DOSE_CODE", ParmMX.getData("JX",j));//剂型
    	 actionParmMX.addData("SPECIFICATION", ParmMX.getData("GG",j));//规格
    	 actionParmMX.addData("PRICE", ParmMX.getData("OWN_PRICE",j));//单价
    	 actionParmMX.addData("QTY", ParmMX.getData("DOSAGE_QTY",j));//数量
    	 actionParmMX.addData("TOT_AMT", ParmMX.getData("TOT_AMT",j));//发生金额	    	 
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
    //住院每日诊断上传
         actionParmZD.addData("ADM_SEQ", parm.getData("ADM_SEQ",row));//就医顺序号
         actionParmZD.addData("HOSP_NHI_NO", parm.getData("NHIHOSP_NO",row));//医院编码
         String date  =beginDate.substring(0, 4)+"-"+beginDate.substring(4, 6)
         +"-"+beginDate.substring(6, 8);
         actionParmZD.addData("BILL_DATE", date);//费用发生时间
         actionParmZD.addData("OWN_NO", parm.getData("PERSONAL_NO",row));//个人编号
         actionParmZD.addData("DEPT_CODE", parm.getData("INS_DEPT_CODE",row));//住院科室
         actionParmZD.addData("DR_NHI_CODE", ParmZD.getData("DR_QUALIFY_CODE",0));//医师编码
        //诊断
 		String mainDiag = "";
 		//诊断编码
 		String otherdiagecode = "";
 		int count1 = ParmZD.getCount("ICD_CHN_DESC");
 		 for(int m=0;m<count1;m++){
 			mainDiag +=ParmZD.getData("ICD_CHN_DESC",m)+",";
 			otherdiagecode +=ParmZD.getData("ICD_CODE",m)+"@";
 		 } 		
         actionParmZD.addData("DIAGE_CODE", mainDiag.length()>0? 
        		 mainDiag.substring(0, mainDiag.length() - 1):"");//病情诊断
         actionParmZD.addData("SPE_REMARK", ParmZD.getData("DESCRIPTION",0));//特殊情况
         actionParmZD.addData("TOT_AMT", df.format(allamt));//发生金额合计  
         if(flg.equals("1"))
         actionParmZD.addData("DELAY", "资格确认书延迟开立");//延迟补传原因
         else if (flg.equals("2"))
         actionParmZD.addData("DELAY", "");//延迟补传原因
         actionParmZD.addData("OTHER_DIAGE_CODE", otherdiagecode.length()>0? 
         		otherdiagecode.substring(0, otherdiagecode.length() - 1):"");//诊断编码
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
     *明细SQL 
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
     *诊断SQL 
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
