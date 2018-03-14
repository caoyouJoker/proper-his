package com.javahis.ui.bil;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Date;

import jdo.ekt.EKTIO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 补退款查询</p>
 *
 * <p>Description: 补退款查询</p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author luhai 20120320
 * @version 1.0
 */
public class BILPayTypeDetailControl extends TControl{
    /**
      * 初始化方法
      */
     public void onInit() {
         initPage();
         //权限添加
         TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
         cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                 getValueString("REGION_CODE")));
     }

     private TTable table;
     DecimalFormat df1 = new DecimalFormat("########0.00");
     public BILPayTypeDetailControl() {
     }
     public static final String cash="现金";
     public static final String bank="银行卡";
     public static final String check="支票";
     public static final String debit="记账";
     
     /**
      * 查询方法
      */
     public void onQuery() {
         TParm parm = new TParm();
         String date_s = getValueString("DATE_S");
         String date_e = getValueString("DATE_E");
         if (null == date_s || date_s.length() <= 0 || null == date_e ||
             date_e.length() <= 0) {
             this.messageBox("请输入需要查询的时间范围");
             return;
         }
         date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "").
                  replace("-", "").replace(" ", "");
         date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "").
                  replace("-", "").replace(" ", "");
         if (null != this.getValueString("REGION_CODE") && this.getValueString("REGION_CODE").length() > 0)
             parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
         parm.setData("DATE_S", date_s);
         parm.setData("DATE_E", date_e);
//         TParm result = CLPOverPersonManagerTool.getInstance().selectData("selectOverflowCase",parm);
         StringBuffer wherebf =new StringBuffer();
         if(!"".equals(date_s)){
        	 wherebf.append(" AND A.CHARGE_DATE>=TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') ");
         }
         if(!"".equals(date_e)){
        	 wherebf.append(" AND A.CHARGE_DATE<=TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') ");
         }
         String mrNo=this.getValueString("MR_NO");
         if(!"".equals(mrNo)){
        	 wherebf.append(" AND A.MR_NO ='"+mrNo+"'");
         }
         String optUser=this.getValueString("OPT_USER");
         if(!"".equals(optUser)){
        	 wherebf.append(" AND A.OPT_USER ='"+optUser+"'");
         }
         StringBuffer sqlbf = new StringBuffer();
         //===zhangp 20120412 start
         sqlbf.append(" SELECT * FROM ( ");
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE WHEN" +
         		" A.PAY_CASH< 0" +
         		" THEN '结算退款'" +
         		" ELSE '结算补款'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME," +
         		" A.PAY_CASH AS AMT, C.STATION_DESC AS SATION_DESC, '现金' AS PAY_TYPE," +
         		" E.USER_NAME AS OPT_NAME,A.CHARGE_DATE" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_CASH <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
         sqlbf.append(" UNION ALL ");
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE WHEN" +
         		" A.PAY_BANK_CARD< 0" +
         		" THEN '结算退款'" +
         		" ELSE '结算补款'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME," +
         		" A.PAY_BANK_CARD AS AMT, C.STATION_DESC AS SATION_DESC, '银行卡' AS PAY_TYPE," +
         		" E.USER_NAME AS OPT_NAME,A.CHARGE_DATE" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_BANK_CARD <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
         sqlbf.append(" UNION ALL ");
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE" +
         		" WHEN PAY_CHECK< 0" +
         		" THEN '结算退款'" +
         		" ELSE '结算补款'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME, PAY_CHECK AS AMT, C.STATION_DESC AS SATION_DESC," +
         		" '支票' AS PAY_TYPE, E.USER_NAME AS OPT_NAME,A.CHARGE_DATE" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_CHECK <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
         sqlbf.append(" UNION ALL ");
         sqlbf.append("(SELECT A.RECEIPT_NO," +
         		" CASE" +
         		" WHEN PAY_DEBIT< 0" +
         		" THEN '结算退款'" +
         		" ELSE '结算补款'" +
         		" END AS TYPE," +
         		" A.MR_NO, D.PAT_NAME, PAY_DEBIT AS AMT, C.STATION_DESC AS SATION_DESC," +
         		" '记账' AS PAY_TYPE, E.USER_NAME AS OPT_NAME,A.CHARGE_DATE" +
         		" FROM BIL_IBS_RECPM A," +
         		" ADM_INP B," +
         		" SYS_STATION C," +
         		" SYS_PATINFO D," +
         		" SYS_OPERATOR E" +
         		" WHERE A.CASE_NO = B.CASE_NO(+)" +
         		" AND B.STATION_CODE = C.STATION_CODE(+)" +
         		" AND A.MR_NO = D.MR_NO(+)" +
         		" AND A.OPT_USER = E.USER_ID(+)" +
         		" AND (CASE" +
         		" WHEN A.PAY_BILPAY IS NULL" +
         		" THEN 0" +
         		" ELSE A.PAY_BILPAY" +
         		" END - A.OWN_AMT) <> 0" +
         		" AND PAY_DEBIT <> 0" +
         		//===zhangp 20120507 start
         		//===zhangp 20120614 start
//         		" AND A.AR_AMT>=0" +
//         		" AND REFUND_DATE IS NULL");
         		" AND A.AR_AMT>=0");
         //===zhangp 20120614 end
         		//===zhangp 20120507 end
         sqlbf.append(wherebf.toString());
         sqlbf.append(")");
         sqlbf.append(" ) ORDER BY CHARGE_DATE ");
       //===zhangp 20120412 end
//         sqlbf.append("SELECT A.CASE_NO,CASE WHEN A.ADM_TYPE='E' THEN '急诊' WHEN A.ADM_TYPE='O' THEN '门诊' WHEN A.ADM_TYPE='I' THEN '住院' WHEN A.ADM_TYPE='H' THEN '健检'  end AS ADM_TYPE_DESC ,");
//         sqlbf.append(" B.PAT_NAME ,A.ADM_DATE,A.OPT_USER,C.USER_NAME AS OPT_USER_DESC,A.GREEN_BALANCE,A.GREEN_PATH_TOTAL,A.MR_NO ");
//         sqlbf.append("  FROM REG_PATADM A,SYS_PATINFO B,SYS_OPERATOR C  WHERE A.MR_NO=B.MR_NO(+) AND A.OPT_USER=C.USER_ID(+)  ");
//         sqlbf.append(" AND (A.GREEN_BALANCE>0  or GREEN_PATH_TOTAL>0) ");

//         System.out.println("查询sql："+sqlbf.toString());
//         System.out.println("======sqlbf===asdasd=="+sqlbf); 
         TParm result = new TParm(TJDODBTool.getInstance().select(sqlbf.toString()));
         if(result.getErrCode()<0){
        	 this.messageBox("查询失败");
        	 return;
         }
         if(result.getCount()<=0){
             this.messageBox("查无数据");
         }
         table.setParmValue(result);
     }

     /**
      * 初始画面数据
      */
     private void initPage() {
         Timestamp date = StringTool.getTimestamp(new Date());
         table = (TTable) getComponent("TABLE");
         this.setValue("REGION_CODE", Operator.getRegion());
         // 初始化查询区间
         this.setValue("DATE_E",
                       date.toString().substring(0, 10).replace('-', '/') +
                       " 23:59:59");
         this.setValue("DATE_S",
                      date.toString().substring(0, 10).
                       replace('-', '/') + " 00:00:00");
         //绑定控件事件
         callFunction("UI|MR_NO|addEventListener",
                      "MR_NO->" + TKeyListener.KEY_RELEASED, this,
                      "onKeyReleased");
         this.setValue("OPT_USER", Operator.getID());
         
     }
     public void onKeyReleased(KeyEvent e) {
         if (e.getKeyCode() != 10) {
             return;
         }
         
         TTextField mrNO=(TTextField)this.getComponent("MR_NO");
         String srcMrNo = PatTool.getInstance().checkMrno(mrNO.getValue());
         mrNO.setValue(srcMrNo);
       //modify by huangtt 20160928 EMPI患者查重提示  start 
         Pat pat = Pat.onQueryByMrNo(srcMrNo);       
 		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
 	            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
 	           mrNO.setValue(pat.getMrNo());
 	     }
        //modify by huangtt 20160928 EMPI患者查重提示  end
         
         
         
         mrNO.setFocusable(true);
         this.onQuery();
     }
 	/**
 	 * 医疗卡读卡
 	 */
 	public void onEKT() {
 		TParm parm = EKTIO.getInstance().TXreadEKT();
         //System.out.println("parm==="+parm);
     	if (null == parm || parm.getValue("MR_NO").length() <= 0) {
             this.messageBox("此医疗卡无效");
             return;
         } 
     	//zhangp 20120130
     	if(parm.getErrCode()<0){
     		messageBox(parm.getErrText());
     	}
 		setValue("MR_NO", parm.getValue("MR_NO"));
 		
 	   //modify by huangtt 20160928 EMPI患者查重提示  start 
         String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));    
         Pat pat = Pat.onQueryByMrNo(srcMrNo);       
 		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
 	            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
 	          setValue("MR_NO", pat.getMrNo());
 	     }
        //modify by huangtt 20160928 EMPI患者查重提示  end
 		
 		this.onQuery();
 		//修改读医疗卡功能  end luhai 
 	}
     /**
      * 打印方法
      */
     public void onPrint() {
         //补款笔数
         int fillCnt=0;
         //补款金额
         double fillAmt=0;
         //补款现金
         double fillCash=0;
         //补款支票
         double fillCheck=0;
         //退款笔数
         int returnCnt=0;
         //退款金额
         double returnAmt=0;
         //退款现金
         double returnCash=0;
         //退款支票
         double returnCheck=0;
         //总计笔数
         int totCnt=0;
         //总计金额
         double totAmt=0;
         //总计现金
         double totCash=0;
         //总计支票
         double totCheck=0;
         //报表数据
         TParm result = new TParm();
         TParm parm = table.getParmValue();
         if (null == parm || parm.getCount() <= 0) {
             this.messageBox("没有需要打印的数据");
             return;
         }
         TParm parmValue=new TParm();
         for(int i=0;i<parm.getCount();i++){
             parmValue.addData("RECEIPT_NO",parm.getValue("RECEIPT_NO",i));
             parmValue.addData("TYPE",parm.getValue("TYPE",i));
             parmValue.addData("MR_NO",parm.getValue("MR_NO",i));
             parmValue.addData("PAT_NAME",parm.getValue("PAT_NAME",i));
             parmValue.addData("AMT",parm.getValue("AMT",i));
             parmValue.addData("SATION_DESC",parm.getValue("SATION_DESC",i));
             parmValue.addData("PAY_TYPE",parm.getValue("PAY_TYPE",i));
             parmValue.addData("OPT_NAME",parm.getValue("OPT_NAME",i));
//             parmValue.addData("STATION_DESC",parm.getValue("STATION_DESC",i));
             if("结算退款".equals(parm.getValue("TYPE",i))){
            	 returnCnt++;
            	 returnAmt+=parm.getDouble("AMT",i);
            	 if(cash.equals(parm.getValue("PAY_TYPE",i))){
            		 returnCash+=parm.getDouble("AMT",i);            		 
            	 }
            	 //===zhangp 20120601 start
//            	 if(check.equals(parm.getValue("PAY_TYPE",i))){
            	 if(check.equals(parm.getValue("PAY_TYPE",i)) || bank.equals(parm.getValue("PAY_TYPE",i))){
            		 //===zhangp 20120601 end
            		 returnCheck+=parm.getDouble("AMT",i);            		 
            	 }
             }
             if("结算补款".equals(parm.getValue("TYPE",i))){
            	 fillCnt++;
            	 fillAmt+=parm.getDouble("AMT",i);
            	 if(cash.equals(parm.getValue("PAY_TYPE",i))){
            		 fillCash+=parm.getDouble("AMT",i);            		 
            	 }
            	 //===zhangp 20120601 start
//            	 if(check.equals(parm.getValue("PAY_TYPE",i))){
            	 if(check.equals(parm.getValue("PAY_TYPE",i)) || bank.equals(parm.getValue("PAY_TYPE",i))){
            		 //===zhangp 20120601 end
            		 fillCheck+=parm.getDouble("AMT",i);            		 
            	 }
             }
             totCnt++;
             totAmt+=parm.getDouble("AMT",i);
        	 if(cash.equals(parm.getValue("PAY_TYPE",i))){
        		 totCash+=parm.getDouble("AMT",i);            		 
        	 }
        	 //===zhangp 20120601 start
//        	 if(check.equals(parm.getValue("PAY_TYPE",i))){
        	 if(check.equals(parm.getValue("PAY_TYPE",i)) || bank.equals(parm.getValue("PAY_TYPE",i))){
        		 //===zhangp 20120601 end
        		 totCheck+=parm.getDouble("AMT",i);            		 
        	 }
         }
         parmValue.setCount(parm.getCount());
         parmValue.addData("SYSTEM", "COLUMNS", "RECEIPT_NO");
         parmValue.addData("SYSTEM", "COLUMNS", "TYPE");
         parmValue.addData("SYSTEM", "COLUMNS", "MR_NO");
         parmValue.addData("SYSTEM", "COLUMNS", "PAT_NAME");
         parmValue.addData("SYSTEM", "COLUMNS", "SATION_DESC");
         parmValue.addData("SYSTEM", "COLUMNS", "AMT");
         parmValue.addData("SYSTEM", "COLUMNS", "PAY_TYPE");
         parmValue.addData("SYSTEM", "COLUMNS", "OPT_NAME");
//         parmValue.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
         result.setData("S_DATE", "TEXT",
                        getValueString("DATE_S").substring(0,
                 getValueString("DATE_S").lastIndexOf(".")));
         result.setData("E_DATE", "TEXT",
                        getValueString("DATE_E").substring(0,
                 getValueString("DATE_S").lastIndexOf(".")));
         result.setData("OPT_USER", Operator.getName());
         result.setData("T1", parmValue.getData());
         result.setData("TITLE", "TEXT",
                        (null != Operator.getHospitalCHNFullName() ?
                         Operator.getHospitalCHNFullName() : "所有院区") +
                        "结算补款退款明细表（汇总）");
         //打印时间
         result.setData("PRINT_DATE","TEXT",StringTool.getString(SystemTool.getInstance().getDate(),"yyyy年MM月dd日"));
         //加入合计相关信息
         result.setData("fillCnt","TEXT", fillCnt);
         result.setData("fillAmt","TEXT", df1.format(fillAmt));
         result.setData("fillCash","TEXT", df1.format(fillCash));
         result.setData("fillCheck","TEXT", df1.format(fillCheck));
         result.setData("returnCnt","TEXT", returnCnt);
         result.setData("returnAmt","TEXT", df1.format(returnAmt));
         result.setData("returnCash","TEXT", df1.format(returnCash));
         result.setData("returnCheck","TEXT", df1.format(returnCheck));
         result.setData("totCnt","TEXT", totCnt);
         result.setData("totAmt","TEXT", df1.format(totAmt));
         result.setData("totCash","TEXT",df1.format( totCash));
         result.setData("totCheck","TEXT", df1.format(totCheck));
         //卢海加入制表人
         //表尾
         result.setData("CREATEUSER", "TEXT", Operator.getName());
//         this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPayTypeDetail.jhw.jhw",
//                              result);
         this.openPrintWindow(IReportTool.getInstance().getReportPath("BILPayTypeDetail.jhw"),
                              IReportTool.getInstance().getReportParm("BILPayTypeDetail.class", result));//报表合并modify by wanglong 20130730

     }

     /**
      * 清空
      */
     public void onClear() {
    	 this.setValue("MR_NO", "");
    	 this.setValue("OPT_USER", "");
         initPage();
         table.removeRowAll();
     }

     /**
      * 汇出Excel
      */
     public void onExport() {
         //得到UI对应控件对象的方法
         TParm parm = table.getParmValue();
         if (null == parm || parm.getCount() <= 0) {
             this.messageBox("没有需要导出的数据");
             return;
         }
         ExportExcelUtil.getInstance().exportExcel(table, "补退款查询表");
     }

}
