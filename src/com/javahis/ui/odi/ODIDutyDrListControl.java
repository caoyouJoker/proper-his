package com.javahis.ui.odi;

import com.dongyang.control.*;
import jdo.ins.RuleTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.manager.TCM_Transform;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.system.textFormat.TextFormatDept;
import com.javahis.system.textFormat.TextFormatSYSDeptForReg;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.system.textFormat.TextFormatStation;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class ODIDutyDrListControl extends TControl {
    private static String TABLE = "TABLE";
    private String action = "INSERT";
    //=======pangben modify 20110511
    private String drCode="";
    /**
     * 初始化参数
     */
    public void onInitParameter(){
//        this.setPopedem("deptEnabled",false);
//        this.setPopedem("stationEnabled",true);

    }
     public void onInit() {
         super.onInit();
         callFunction("UI|" + TABLE + "|addEventListener",TABLE + "->" + TTableEvent.CLICKED,this,"onTableClicked");
         onInitPopeDem();
         onClear();
         //luhai 2012-04-04 默认页面加载时执行查询操作 begin
         this.onQuery();
         //luhai 2012-04-04 默认页面加载时执行查询操作 end
     }
     /**
     * 初始化权限
     */
    public void onInitPopeDem() {
        //权限可否选择科室
        if (!this.getPopedem("deptEnabled")) {
            this.callFunction("UI|DEPT_CODE|setEnabled", false);
            this.callFunction("UI|DEPT_CODEOUT|setEnabled", false);
        }
        //权限可否选择病区
        if (!this.getPopedem("stationEnabled")) {
            this.callFunction("UI|STATION_CODE|setEnabled", false);
            this.callFunction("UI|STATION_CODEOUT|setEnabled", false);
        }
    }
     public void onTableClicked(int row)
     {
         if(row < 0)
             return;
         action = "EDIT";
         //=========pangben modify 20110511 start
         TParm parm = ((TTable)this.getComponent(TABLE)).getParmValue();
         this.setValueForParm("STATION_CODE;DEPT_CODE;DR_CODE", parm, row);
         this.setValue("SEQ",parm.getValue("SEQ",row));
         callFunction("UI|delete|setEnabled",true);
         drCode=parm.getValue("DR_CODE",row);
         //=========pangben modify 20110511 stop
     }
     /**
      * 保存
      */
     public void onSave(){
         out("保存begin");
    	 TextFormatDept dept =(TextFormatDept)this.getComponent("DEPT_CODE");
    	 TextFormatSYSStation station =(TextFormatSYSStation)this.getComponent("STATION_CODE");
    	 TextFormatSYSOperator drCode =(TextFormatSYSOperator)this.getComponent("DR_CODE");
    	 if("".equals(dept.getValue()+"")){
    		 this.messageBox("请选择科室！");
    		 return;
    	 }
    	 if("".equals(station.getValue()+"")){
    		 this.messageBox("请选择病区！");
    		 return;
    	 }
    	 if("".equals(drCode.getValue()+"")){
    		 this.messageBox("请选择医生！");
    		 return;
    	 }
         if("EDIT".equals(action))
         {
             if(!emptyTextCheck("STATION_CODE,DR_CODE,DEPT_CODE"))
                 return;
             //callFunction("UI|" + TABLE + "|setModuleParmUpdate",parm);
//             if(!(Boolean)callFunction("UI|" + TABLE + "|onUpdate")){
//                 messageBox("E0001");
//                 return;
//             }
             //==========pangben modify 20110511 start
             String updateSQL="UPDATE ODI_DUTYDRLIST SET DEPT_CODE='"+this.getValueString("DEPT_CODE")+"',"+
                              "STATION_CODE='"+this.getValueString("STATION_CODE")+"',DR_CODE='"+this.getValueString("DR_CODE")+"',"
                              +"OPT_USER='"+Operator.getID()+"',OPT_DATE=SYSDATE,OPT_TERM='"+Operator.getIP()+"',"

                              +"SEQ='"+this.getValueString("SEQ")+"' WHERE DEPT_CODE='"+this.getValueString("DEPT_CODE")+"' AND "+
                              "STATION_CODE='"+this.getValueString("STATION_CODE")+"' AND DR_CODE='"+drCode+"'";
             TParm updateParm = new TParm(this.getDBTool().update(updateSQL));
            if(updateParm.getErrCode()!=0){
                messageBox("修改失败");
                return;
            }
             messageBox("修改成功");
         }else
         {
             if(!emptyTextCheck("STATION_CODE,DR_CODE,DEPT_CODE"))
                 return;
             String sql = "SELECT NVL(MAX(SEQ),0)+1 AS SEQ FROM ODI_DUTYDRLIST WHERE STATION_CODE='"+this.getValueString("STATION_CODE")+"' AND DEPT_CODE='"+this.getValueString("DEPT_CODE")+"'";
             TParm insertParm = new TParm(this.getDBTool().select(sql));
             //luhai modify 2012-4-4 begin
             //将seq 存为最大号
//             insertParm.getData("SEQ",0);
             String seq = insertParm.getData("SEQ",0)+"";
             this.setValue("SEQ", seq);
             //luhai modify 2012-4-4 end

             //callFunction("UI|" + TABLE + "|setModuleParmInsert",parm);
//             if(!(Boolean)callFunction("UI|" + TABLE + "|onInsert")){
//                 messageBox("E0002");
//                 return;
//             }
             String insertSQL="INSERT INTO ODI_DUTYDRLIST(DEPT_CODE,STATION_CODE,"+
                              "DR_CODE,SEQ,OPT_USER,OPT_DATE,OPT_TERM)VALUES('"+this.getValueString("DEPT_CODE")+"','"
                              +this.getValueString("STATION_CODE")+"','"+this.getValueString("DR_CODE")+"','"+
                              this.getValueString("SEQ")+"','"+Operator.getID()+"',SYSDATE,'"+Operator.getIP()+"')";
             TParm insertParms = new TParm(this.getDBTool().update(insertSQL));
             if(insertParms.getErrCode()!=0){
                messageBox("该医生已经为所选病区的值班医生，不可重复设置！");
                return;
            }
             messageBox("P0002");
             action = "EDIT";
             callFunction("UI|delete|setEnabled",true);
         }
          //==========pangben modify 20110511 start
         out("保存end");
         this.onQuery();
     }
     /**
     * 返回数据库操作工具
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

     /**
      * 删除
      */
     public void onDelete(){
         out("删除begin");
         if(messageBox("提示信息 Tips","是否删除? \n Whether to delete",this.YES_NO_OPTION) != 0)
             return;
         int row = (Integer)callFunction("UI|" + TABLE + "|getSelectedRow");
         if(row < 0)
             return;
//         if(!(Boolean)callFunction("UI|" + TABLE + "|onDelete"))
//         {
//             messageBox("E0003");
//             return;
//         }
         String deleteSQL = "DELETE ODI_DUTYDRLIST WHERE STATION_CODE='" +
                            this.getValueString("STATION_CODE") + "' AND DR_CODE='" +
                            this.getValueString("DR_CODE") + "' AND DEPT_CODE='" +
                            this.getValueString("DEPT_CODE") + "'";
         TParm deleteParms = new TParm(this.getDBTool().update(deleteSQL));
         if (deleteParms.getErrCode() != 0) {
             messageBox("E0003");
             return;
         }

         messageBox("P0003");
         int rows = (Integer) callFunction("UI|" + TABLE + "|getSelectedRow");
         if (rows < 0) {
             this.onClear();
         }
         out("删除end");
         this.onQuery();
     }
     /**
      * 清空
      */
     public void onClear(){
         out("清空begin");
         clearValue("DR_CODE;SEQ");
         this.setValue("DEPT_CODE",Operator.getDept());
         this.setValue("STATION_CODE",Operator.getStation());
         callFunction("UI|" + TABLE + "|clearSelection");
         callFunction("UI|save|setEnabled", true);
         callFunction("UI|delete|setEnabled", false);
         //病区
         TextFormatSYSStation h = (TextFormatSYSStation)this.getComponent(
                 "STATION_CODE");
         h.setDeptCode(this.getValueString("DEPT_CODE"));
         h.onQuery();
         //经治医师
         TextFormatSYSOperator k = (TextFormatSYSOperator)this.getComponent(
                 "DR_CODE");
         k.setDept(this.getValueString("DEPT_CODE"));
         k.onQuery();
         action = "INSERT";
         out("清空end");
   }
     /**
      * 查询初始化自动执行
      */
     public void onQuery()
     {
         String region="";
          //===========pangben modify 20110511 start 添加查询区域参数
          if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
              region=" AND B.REGION_CODE='"+Operator.getRegion()+"'";
          String SQL="SELECT A.DEPT_CODE,A.STATION_CODE,A.DR_CODE,A.SEQ,A.OPT_USER,A.OPT_DATE,"+
                     "A.OPT_TERM FROM ODI_DUTYDRLIST A,SYS_DEPT B WHERE A.DEPT_CODE=B.DEPT_CODE(+)"+region+" ";
          SQL+=" AND  A.DEPT_CODE='"+this.getValueString("DEPT_CODE")+"'";
          SQL+="ORDER BY A.STATION_CODE";

          TParm parm = new TParm(getDBTool().select(SQL));
         // callFunction("UI|" + TABLE + "|onQuery",parm);
          callFunction("UI|" + TABLE + "|setParmValue",parm);
         if(null==parm||parm.getCount()<1){
             this.messageBox("没有找到要查询的数据");
             return;
         }
          //===========pangben modify 20110511 stop
          int row = (Integer) callFunction("UI|" + TABLE + "|getRowCount");
          if (row < 0)
              this.onClear();

     }
     /**
      * 科室变动
      */
     public void onChangeDept(){
    	 TextFormatDept dept =(TextFormatDept)this.getComponent("DEPT_CODE");
    	 TextFormatSYSStation station =(TextFormatSYSStation)this.getComponent("STATION_CODE");
    	 TextFormatSYSOperator drCode =(TextFormatSYSOperator)this.getComponent("DR_CODE");
    	 station.setValue("");
    	 drCode.setValue("");
    	 station.setDeptCode(dept.getValue()+"");
    	 station.onQuery();
    	 drCode.setDept(dept.getValue()+"");
    	 drCode.onQuery();
     }
     public static void main(String[] args) {

     }
}
