package com.javahis.ui.reg;

import com.dongyang.control.*;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;

import jdo.sys.SystemTool;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.*;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.OdiUtil;
import jdo.sys.Operator;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>Title: 病患统计 </p>
 *
 * <p>Description: 病患统计 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class StatistiscControl extends TControl {
    /**
     * 初始化方法
     */
    public void onInit(){
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate,"yyyyMMdd");
        this.setValue("START_DATE",StringTool.getTimestamp(dateStr+" 00:00:00","yyyyMMdd hh:mm:ss"));//modify by wanglong 20120913
        this.setValue("END_DATE",sysDate);//modify by wanglong 20120913 将“日期”和“时间”合并为“日期时间”一个输入框

        setValue("REGION_CODE", Operator.getRegion()); //pangben modify 20110410

        //========pangben modify 20110421 start 权限添加
        TComboBox cboRegion = (TComboBox)this.getComponent("REGION_CODE");
        cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(this.
                getValueString("REGION_CODE")));
        //===========pangben modify 20110421 stop
    }
    /**
     * 执行
     */
    public void onQuery() throws ParseException {
        TParm tableParm = new TParm();
        //REG_DATE;DEPT_CODE;DR_CODE;CLINICTYPE_CODE;MR_NO;PAT_NAME;SEX_CODE;BIRTH_DATE;NATION_CODE;
		String sql = 
			"SELECT B.PAT_NAME,A.REG_DATE,A.CASE_NO,A.MR_NO,A.ADM_TYPE," +
				   "B.NATION_CODE,B.BIRTH_DATE,B.SEX_CODE,A.DEPT_CODE,A.DR_CODE," +
				   "A.CLINICTYPE_CODE,A.SEEN_DR_TIME," +
				   "CASE WHEN A.VISIT_CODE='0' THEN '初诊' ELSE '复诊' END VISIT_CODE," +
				   "C.REGION_CHN_ABN " +
			  "FROM REG_PATADM A,SYS_PATINFO B,SYS_REGION C,BIL_REG_RECP D " +
			 "WHERE A.MR_NO=B.MR_NO " +
			   "AND A.CASE_NO = D.CASE_NO " +
			   "AND REGCAN_DATE IS NULL " +
//			   (this.getValueString("ADM_TYPE").equals("O")?"AND ADM_DATE ":"AND REG_DATE ") +//modify by wanglong 20121011
			   " AND D.BILL_DATE "+ //modify by huangtt 20141211 
			   " BETWEEN TO_DATE('" + this.getText("START_DATE") + "','YYYY/MM/DD HH24:MI:SS') " +//modify by wanglong 20121011
			        "AND TO_DATE('" + this.getText("END_DATE") + "','YYYY/MM/DD HH24:MI:SS') " +
			   "AND A.REGION_CODE=C.REGION_CODE";//modify by wanglong 20120913 
		
		if (this.getValueString("REGION_CODE").length() != 0)
           sql += " AND A.REGION_CODE= '" + this.getValue("REGION_CODE") + "' ";//pangben modify 20110410 start
        if(this.getValueString("ADM_TYPE").length()!=0){
            sql+=" AND A.ADM_TYPE='"+this.getValueString("ADM_TYPE")+"'";
        }
        if(this.getValueString("DEPT_CODE").length()!=0){
            sql+=" AND A.DEPT_CODE='"+this.getValueString("DEPT_CODE")+"'";
        }
        if(this.getValueString("OPERATOR").length()!=0){
            sql+=" AND A.DR_CODE='"+this.getValueString("OPERATOR")+"'";
        }
        sql+=" ORDER BY C.REGION_CHN_ABN";//========pangben modify 20110410,fuxin modify 20120306
//        System.out.println("查询病患信息"+sql);
        TParm parm = new TParm(getDBTool().select(sql));
        int rowConut = parm.getCount();
        tableParm.setCount(rowConut);
        //PAT_NAME;REG_DATE;ONE_SAVE;PHA_DATE;OPB_DATE
        for(int i=0;i<rowConut;i++){
            TParm temp = parm.getRow(i);
            tableParm.addData("MR_NO",temp.getValue("MR_NO"));
            tableParm.addData("PAT_NAME",temp.getValue("PAT_NAME"));
            tableParm.addData("REG_DATE",temp.getTimestamp("REG_DATE"));
            tableParm.addData("CASE_NO",temp.getValue("CASE_NO"));
            tableParm.addData("DEPT_CODE",temp.getValue("DEPT_CODE"));
            tableParm.addData("DR_CODE",temp.getValue("DR_CODE"));
            tableParm.addData("CLINICTYPE_CODE",temp.getValue("CLINICTYPE_CODE"));
            tableParm.addData("SEX_CODE",temp.getValue("SEX_CODE"));
            tableParm.addData("NATION_CODE",temp.getValue("NATION_CODE"));
            tableParm.addData("VISIT_CODE",temp.getValue("VISIT_CODE"));
            tableParm.addData("SEEN_DR_TIME",temp.getTimestamp("SEEN_DR_TIME"));
            tableParm.addData("REGION_CHN_ABN",temp.getValue("REGION_CHN_ABN")); //pangben modify 20110410 ,fuxin  modify  20120306
            tableParm.addData("BIRTH_DATE",OdiUtil.getInstance().showAge(temp.getTimestamp("BIRTH_DATE"),temp.getTimestamp("REG_DATE")));
            String sqlOrd = "SELECT ORDER_DATE FROM OPD_ORDER WHERE CASE_NO='"+temp.getValue("CASE_NO")+"' ORDER BY ORDER_DATE";
           // System.out.println("医生第一次保存:"+sqlOrd);
            TParm ordP = new TParm(getDBTool().select(sqlOrd));
            if(ordP.getCount()>0){
                tableParm.addData("ONE_SAVE",ordP.getTimestamp("ORDER_DATE",0));
            }else{
                tableParm.addData("ONE_SAVE","");
            }
            String phaSql = "SELECT ORDER_DATE " +
            		          "FROM OPD_ORDER " +
            		         "WHERE CASE_NO='"+temp.getValue("CASE_NO")+"' " +
            		           "AND CAT1_TYPE='PHA' " +
            		      "ORDER BY ORDER_DATE";
          //  System.out.println("第一次保存开药品的时间："+phaSql);
            TParm phaParm = new TParm(getDBTool().select(phaSql));
            if(phaParm.getCount()>0){
               tableParm.addData("PHA_DATE",phaParm.getTimestamp("ORDER_DATE",0));
           }else{
               tableParm.addData("PHA_DATE","");
           }
           String opbSql = "SELECT RECEIPT_NO " +
           					 "FROM OPD_ORDER " +
           					"WHERE CASE_NO='"+temp.getValue("CASE_NO")+"' " +
           					"ORDER BY RECEIPT_NO";
           //System.out.println("查询最后的打票号码:"+opbSql);
           TParm opbParm = new TParm(getDBTool().select(opbSql));
           if(opbParm.getCount()>0){
               String receiptNo = opbParm.getValue("RECEIPT_NO",0);
               String recepSql = "SELECT PRINT_DATE " +
               					   "FROM BIL_INVRCP " +
               					  "WHERE RECP_TYPE = 'OPB' " +
               						"AND CANCEL_FLG='0' " +
               						"AND RECEIPT_NO='"+receiptNo+"'  " +
               					  "ORDER BY PRINT_DATE";
              // System.out.println("最后结算时间");
               TParm recpParm = new TParm(getDBTool().select(recepSql));
               if(recpParm.getCount()>0){
                   tableParm.addData("OPB_DATE",recpParm.getTimestamp("PRINT_DATE",0));
               }else{
                   tableParm.addData("OPB_DATE","");
               }
           }else{
               tableParm.addData("OPB_DATE","");
           }
        }
        int tableCount = tableParm.getCount();
        //PAT_NAME;REG_DATE;ONE_SAVE;WAIT_DATE;PHA_DATE;OPB_DATE;WAITOPB_DATE   
        for(int i=0;i<tableCount;i++){
            TParm temp = tableParm.getRow(i);
            String st = "";
            String en = "";
            String opben = "";
            String seenTime="";
            if(temp.getValue("ONE_SAVE")!=null||temp.getValue("ONE_SAVE").length()!=0){
                st = StringTool.getString(StringTool.getTimestamp(temp.getValue("ONE_SAVE").split("\\.")[0],"yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmss");
            }
            if(temp.getValue("REG_DATE")!=null||temp.getValue("REG_DATE").length()!=0){
                en = StringTool.getString(StringTool.getTimestamp(temp.getValue("REG_DATE").split("\\.")[0],"yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmss");
            }
            if(temp.getValue("OPB_DATE")!=null||temp.getValue("OPB_DATE").length()!=0){
                opben = StringTool.getString(StringTool.getTimestamp(temp.getValue("OPB_DATE").split("\\.")[0],"yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmss");
            }
            if(temp.getValue("SEEN_DR_TIME")!=null||temp.getValue("SEEN_DR_TIME").length()!=0){
                seenTime = StringTool.getString(StringTool.getTimestamp(temp.getValue("SEEN_DR_TIME").split("\\.")[0],"yyyy-MM-dd HH:mm:ss"),"yyyyMMddHHmmss");
            }
            tableParm.addData("WAIT_DATE",getDifDate(st,en));
            tableParm.addData("WAITOPB_DATE",getDifDate(opben,seenTime));
        }
		if (tableCount <= 0) {//modify by wanglong 20121011
			messageBox("E0008");
			this.callFunction("UI|TABLE|setParmValue", new TParm());
			this.clearValue("PAT_NUM");
			return;
		}
        ((TTextField) getComponent("PAT_NUM")).setValue(tableCount+"");//add by wanglong 20120913 增加病患数量信息
     // System.out.println("tableParm==="+tableParm);
        this.getTTable("TABLE").setParmValue(tableParm);
    }
    public static void main(String[] args) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        Date now = null;
        try {
            now = df.parse("20091209000808");
        }
        catch (ParseException ex) {
        }
        Date date = null;
        try {
            date = df.parse("20091209000718");
        }
        catch (ParseException ex1) {
        }
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ( (l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
      //  System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");

    }
    /**
     * 返回时间差
     * @param st String
     * @param en String
     * @return String
     */
    public String getDifDate(String st,String en) throws ParseException {
        if(st.length()==0||en.length()==0)
            return "";
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date now = df.parse(st);
        java.util.Date date = df.parse(en);
        long l = now.getTime() - date.getTime();
        long day = l / (24 * 60 * 60 * 1000);
        long hour = (l / (60 * 60 * 1000) - day * 24);
        long min = ( (l / (60 * 1000)) - day * 24 * 60 - hour * 60);
        long s = (l / 1000 - day * 24 * 60 * 60 - hour * 60 * 60 - min * 60);
        //System.out.println("" + day + "天" + hour + "小时" + min + "分" + s + "秒");
        return day + "天" + hour + "小时" + min + "分" + s + "秒";
    }

    /**
     * 导出
     */
    public void onExecl(){
        ExportExcelUtil.getInstance().exportExcel(this.getTTable("TABLE"),"统计病人看诊情况");
    }
    /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool(){
        return TJDODBTool.getInstance();
    }
    /**
     * 得到TABLE
     * @param tag String
     * @return TTable
     */
    public TTable getTTable(String tag){
        return (TTable)this.getComponent(tag);
    }
    /**
     * 清空
     */
    public void onClear(){
        this.clearValue("ADM_TYPE;DEPT_CODE;OPERATOR");
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String dateStr = StringTool.getString(sysDate,"yyyyMMdd");
        this.setValue("ST",StringTool.getTimestamp(dateStr,"yyyyMMdd"));
        this.setValue("EN",sysDate);
        this.getTTable("TABLE").removeRowAll();
    }

}
