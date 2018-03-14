package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.StringUtil;
import com.dongyang.util.StringTool;
import java.util.Date;

import jdo.sys.PatTool;

import com.dongyang.jdo.TJDODBTool;
/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 内嵌式医保程序</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class INSORDERIBSDATEControl extends TControl{
     private TParm result;
     private static final String actionName = "action.ins.InsAction";
     public void onInit() {
          super.onInit();
          this.result = (TParm)this.getParameter();
          this.initPage();
          //设置返回值
//          this.setReturnValue(result);
     }
     /**
      * 初始化UI
      */
     public void initPage(){
         this.setValue("START_DATE",this.result.getTimestamp("ADM_DATE"));
     }
     /**
      * 确认
      */
     public void onOK(){
         //开始时间
         Timestamp startDate = (Timestamp)this.getValue("START_DATE");
         //结束时间
         Timestamp endDate = (Timestamp)this.getValue("END_DATE");
         Timestamp endDateNew = new Timestamp(StringUtil.getInstance().getNextDayMiss(endDate.toString(),"1"));
         //看诊号
         String caseNo = this.result.getValue("CASE_NO");
         TParm action = new TParm();
         action.setData("START_DATE",startDate);
         action.setData("END_DATE",endDateNew);
         action.setData("CASE_NO",caseNo);
         action.setData("CTZ_CODE",this.result.getValue("CTZ_CODE"));
         action.setData("INS_COMPANY",this.result.getValue("INS_COMPANY"));
         action.setData("OPT_USER",this.result.getValue("OPT_USER"));
         action.setData("OPT_TERM",this.result.getValue("OPT_TERM"));
         action.setData("BKC023",createJYNo(caseNo));
         TParm actionParm = new TParm();
         if(this.excuteSave(action)){
             messageBox_("汇入成功！");
             actionParm.setData("START","Y");

         }else{
             messageBox_("汇入失败！");
             actionParm.setData("START","N");
         }
         this.setReturnValue(actionParm);
     }
     /**
      * 返回数据库操作工具
      * @return TJDODBTool
      */
     public TJDODBTool getDBTool() {
         return TJDODBTool.getInstance();
     }
     /**
     * 计算得到交易号
     * @return String
     */
    public String createJYNo(String caseNo){
        TParm parm = new TParm(getDBTool().select("SELECT MAX(BKC023) AS BKC023 FROM INS_ORDER WHERE CASE_NO='"+caseNo+"'"));
       // System.out.println("parm"+parm);
       // System.out.println("行数:"+parm.getCount("BKC023")+"字符数:"+parm.getValue("BKC023",0).length());
        if(parm.getValue("BKC023",0).length()!=0){
            String bkc023 = parm.getValue("BKC023",0);
            return bkc023;
        }
       // System.out.println("病案号:"+this.result.getValue("MR_NO"));
        String mrNo = this.result.getValue("MR_NO").substring(4,PatTool.getInstance().getMrNoLength()); //====chenxi
       // System.out.println("病案号:"+mrNo);
        String year = String.valueOf(StringTool.getYear(new Date()));
       // System.out.println("年:"+year);
        String month = String.valueOf(StringTool.getMonth(new Date()));
        if(month.length()==1){
            month="0"+month;
        }
       // System.out.println("月:"+month);
        String day = String.valueOf(StringTool.getDay(new Date()));
        if(day.length()==1){
            day="0"+day;
        }
       // System.out.println("日:"+day);
       // System.out.println("交易号:"+mrNo+year+month+day+"01");
        return mrNo+year+month+day+"01";
    }

     /**
      * 取消
      */
     public void onNo(){
         this.closeWindow();
     }
     /**
      * 执行保存
      * @param parm TParm
      * @return boolean
      */
     public boolean excuteSave(TParm parm){
         TParm actionParm = TIOM_AppServer.executeAction(actionName,"excuteSave",parm);
         if(actionParm.getErrCode()<0){
             return false;
         }
         return true;
     }
}
