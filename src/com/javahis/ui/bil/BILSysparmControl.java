package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>Title: 账务参数档</p>
 *
 * <p>Description: 账务参数档</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-8-11
 * @version 1.0
 */
public class BILSysparmControl extends TControl{
    /**
     * 表
     */
    TTable table;
      /**
       * 初始化
       */
      public void onInit() {
          super.onInit();
          //得到界面上上的表
          table = (TTable)this.getComponent("TABLE");
      }
      /**
       * 表点击事件
       */
      public void onTableClicked(){
          int row=table.getSelectedRow();
          TParm parm=table.getDataStore().getBuffer(table.getDataStore().PRIMARY).getRow(row);
          //数据上翻
          setTextValue(parm);
          //们记住别不可编辑
          setTextEnabled(false);
      }
      /**
       * 数据上翻
       * @param parm TParm
       */
      public void setTextValue(TParm parm){
          this.setValueForParm("ADM_TYPE;MONTH_CYCLE;DAY_CYCLE",parm);
      }
      /**
       * 界面上们记住别的输入
       * @param boo boolean
       */
      public void setTextEnabled(boolean boo){
          this.callFunction("UI|ADM_TYPE|setEnabled",boo);
      }
      /**
       * 清空
       */
      public void onClear(){
          setTextEnabled(true);
          this.clearValue("ADM_TYPE;MONTH_CYCLE;DAY_CYCLE");
          table.retrieve();
          table.setDSValue();
      }
      /**
       * 保存
       * @return boolean
       */
      public boolean onSave(){
          int row=table.getSelectedRow();
          boolean saveFlg=false;
          //如果有选中则为更新
          if(row>=0){
              //处理更新数据
              if(!dealUpdate(row,saveFlg))
                  return false;
          }
          else{
              //处理新增数据
              if(!dealNewData())
                  return false;
          }
          //保存数据方法
          return saveData();
      }
      /**
       * 调用保存方法
       * @return boolean
       */
      public boolean saveData(){
          if (!table.update()) {
              messageBox("保存失败!");
              return false;
          }
          messageBox("保存成功!");
          table.resetModify();
          onClear();
          return true;
      }
      /**
       * 处理新增
       * @return boolean
       */
      public boolean dealNewData(){
          //得到门急住别
          String admType=getValueString("ADM_TYPE");
          //检核门急住别
          if(!checkAdmType(admType))
              return false;
          //结账日期
         String monthCycle=getValueString("MONTH_CYCLE");
         //检核结账日期
         if(!checkMonthCycle(monthCycle))
             return false;
         //得到结账时间
         String dayCycle=getValueString("DAY_CYCLE");
         //检核结账时间
         if(!checkDayCycle(dayCycle))
             return false;
         //处理新增数据
         setNewData();
          return true;
      }
      /**
       * 处理新增数据
       */
      public void setNewData(){
          int row=table.addRow();
          table.setItem(row,"ADM_TYPE",getValueString("ADM_TYPE"));
          table.setItem(row,"MONTH_CYCLE",getValueString("MONTH_CYCLE"));
          table.setItem(row,"DAY_CYCLE",getValueString("DAY_CYCLE"));
          //添加固定参数
          setTableData(row);
      }
      /**
       * 检核门急住别
       * @param admType String
       * @return boolean
       */
      public boolean checkAdmType(String admType){
          if(admType==null||admType.length()==0){
             messageBox("门急住别不能为空!");
             return false;
         }
         //检核主键重复
         if(table.getDataStore().exist("ADM_TYPE ='"+admType+"'")){
             messageBox("数据已存在,不可添加!");
             return false;
         }
          return true;
      }
      /**
       * 处理更新数据
       * @param row int
       * @param saveFlg boolean
       * @return boolean
       */
      public boolean dealUpdate(int row,boolean saveFlg){
          //结账日期
          String monthCycle=getValueString("MONTH_CYCLE");
          //检核结账日期
          if(!checkMonthCycle(monthCycle))
              return false;
          String month=table.getItemString(row,"MONTH_CYCLE");
          //如果有修改则更新
          if(!monthCycle.equals(month)){
              table.setItem(row, "MONTH_CYCLE", monthCycle);
              //记录是否有修改
              saveFlg=true;
          }
          //得到结账时间
          String dayCycle=getValueString("DAY_CYCLE");
          //检核结账时间
          if(!checkDayCycle(dayCycle))
              return false;
          String day=table.getItemString(row,"DAY_CYCLE");
          if(!day.equals(dayCycle)){
              table.setItem(row, "DAY_CYCLE", dayCycle);
              //记录是否有修改
              saveFlg=true;
          }
          //如果有修改则修改固定数据
          if(saveFlg)
              setTableData(row);
          return true;
      }
      /**
       * 修固定数据
       * @param row int
       */
      public void setTableData(int row){
          table.setItem(row,"OPT_USER",Operator.getID());
          table.setItem(row,"OPT_DATE",SystemTool.getInstance().getDate());
          table.setItem(row,"OPT_TERM",Operator.getIP());
      }
      /**
       * 检核结账时间
       * @param dayCycle String
       * @return boolean
       */
      public boolean checkDayCycle(String dayCycle){
          if(dayCycle==null||dayCycle.length()==0){
              messageBox("结账时间不能为空");
              return false;
          }
          int length=dayCycle.length();
          if(length!=6){
              messageBox("结账时间格式为6位数字");
              return false;
          }
          for(int i=0;i<length;i++){
              String s=dayCycle.substring(i,i+1);
              if(!onClickCheck(s)){
                  messageBox("结账时间必须是数字!");
                  return false;
              }
          }
          //如果没有字母
          int monthDay=Integer.parseInt(dayCycle);
          if(monthDay<0||monthDay>235959){
              messageBox("结账时间必须是00000到235959之间的数字");
              return false;
          }
          return true;
      }
      /**
       * 检核结账日期的有效性
       * @param monthCycle String
       * @return boolean
       */
      public boolean checkMonthCycle(String monthCycle){
          if(monthCycle==null||monthCycle.length()==0){
              messageBox("结账日期不能为空");
              return false;
          }
          int length=monthCycle.length();
          if(length!=2){
              messageBox("结账日期必须是2位数字");
              return false;
          }

          for(int i=0;i<length;i++){
              String s=monthCycle.substring(i,i+1);
              if(!onClickCheck(s)){
                  messageBox("结账日期必须是数字!");
                  return false;
              }
          }
          //如果没有字母
          int monthDay=Integer.parseInt(monthCycle);
          if(monthDay<0||monthDay>31){
              messageBox("结账日期必须是0到31之间的数字");
              return false;
          }
          return true;
      }

      /**
       * 检查输入的是否是数字
       * @param checkText String
       * @return boolean
       * @author pangjx 2007-08-10
       */
      public boolean onClickCheck(String checkText) {
          //正则表达式表示数字在0－9之间
          String regex = "[0-9]";
          boolean flag = false;
          if (checkText.matches(regex)) {
              flag = true;
          }
          else {
              flag = false;
          }
          return flag;
      }
















}
